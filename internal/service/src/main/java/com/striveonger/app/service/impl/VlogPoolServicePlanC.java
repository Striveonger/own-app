package com.striveonger.app.service.impl;

import com.striveonger.app.service.VlogPoolService;
import com.striveonger.common.core.Timepiece;
import com.striveonger.common.third.cache.reids.RedisKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Plan B 视频池服务
 * 使用偏移量机制替代Plan A的"已观看集合"排除机制
 * <p>
 * 核心逻辑：
 * 1. 每次从hot池和new池各取前100条作为候选列表
 * 2. 从用户偏移量位置开始遍历，跳过已看过的视频
 * 3. 推进偏移量（按实际返回数量）
 *
 * @author Mr.Lee
 * @since 2026-05-04
 */
@Service("vlogPoolServicePlanC")
public class VlogPoolServicePlanC extends VlogPoolService {
    private final Logger log = LoggerFactory.getLogger(VlogPoolServicePlanC.class);

    /**
     * 用户已看过的视频前缀
     */
    private static final String VLOG_USER_VIEWED_PREFIX = "vlog:user:viewed:";

    /**
     * 用户偏移量前缀
     */
    private static final String VLOG_USER_OFFSET_PREFIX = "vlog:user:offset:";

    /**
     * 视频ID映射表(VLOG_ID_STRING -> VLOG_ID_LONG)
     */
    private static final String VLOG_ID_MAPPING = "vlog:id:mapping";

    /**
     * 候选池大小（每个池取前100条）
     */
    private static final int CANDIDATE_POOL_SIZE = 100;

    /**
     * 热门池权重
     */
    private static final double HOT_WEIGHT = 0.5;

    /**
     * 新池权重
     */
    private static final double NEW_WEIGHT = 0.3;

    @Override
    public void loadVlogPoolData() {
        super.loadVlogPoolData();
        // 初始化视频ID映射表
        log.info("init vlog pool service plan c");
        List<Vlog> list = super.getVlogPoolData();
        for (Vlog vlog : list) {
            boolean hasKey = RedisKit.Hash.hasKey(VLOG_ID_MAPPING, vlog.getId());
            if (!hasKey) {
                long size = RedisKit.Hash.size(VLOG_ID_MAPPING);
                RedisKit.Hash.set(VLOG_ID_MAPPING, vlog.getId(), size + 1);
            }
        }
    }

    /**
     * 从视频池中获取用户没看过的视频ID列表
     *
     * @param userId 用户ID
     * @param count  需要获取的视频数量
     * @return 视频ID列表
     */
    @Override
    public List<String> list(String userId, int count) {
        log.info("get vlog list for user: {}, count: {} (Plan B)", userId, count);

        Set<String> result = new HashSet<>();

        Timepiece timepiece = Timepiece.of("vlog-list-for-user: " + userId);

        // 1. 获取用户偏移量和已看过的视频
        long offset = getOffset(userId);
        timepiece.mark("get-offset: {}", userId, offset);

        // 2. 获取热门池和新池的前100条视频
        Set<String> hoted = new HashSet<>(RedisKit.ZSet.range(VLOG_POOL_HOT, 0, CANDIDATE_POOL_SIZE));
        Set<String> newed = new HashSet<>(RedisKit.ZSet.range(VLOG_POOL_NEW, 0, CANDIDATE_POOL_SIZE));
        // 计算热门池和新池的视频数量
        int hotCount = Math.min(hoted.size(), (int) Math.ceil(count * HOT_WEIGHT));
        int newCount = Math.min(newed.size(), (int) Math.ceil(count * NEW_WEIGHT));
        buildResultCandidates(userId, hoted, hotCount, result);
        buildResultCandidates(userId, newed, newCount, result);
        timepiece.mark("get-hot&new-candidates: {}", result.size());

        // 3. 从偏移位置开始遍历时间线，跳过已看过的和已返回的视频，返回目标数量
        int fetched = 0, diff = count - result.size();
        // 升序遍历时间线
        List<String> timeline = RedisKit.ZSet.range(false, VLOG_POOL_TIMELINE, (int) offset, -1);
        for (int i = (int) offset; i < timeline.size() && fetched < diff; i++) {
            fetched++;
            String vlogId = timeline.get(i);
            // 跳过已看过的或已返回的（去重）
            if (isViewed(userId, vlogId) || result.contains(vlogId)) {
                continue;
            }
            result.add(vlogId);
        }
        timepiece.mark("traverse-timeline: {}", result.size());

        // 4. 推进偏移量
        advanceOffset(userId, fetched);
        timepiece.mark("fetch-from-candidates: {}", result.size());

        // 5. 记录用户已看过的视频
        recordUserViewed(userId, result);
        timepiece.mark("record-user-viewed: {}", result.size());

        timepiece.show();
        return new ArrayList<>(result);
    }

    private void buildResultCandidates(String userId, Set<String> hoted, int hotCount, Set<String> result) {
        for (String vlogId : hoted) {
            if (hotCount <= 0) {
                break;
            }
            if (!isViewed(userId, vlogId)) {
                result.add(vlogId);
                hotCount--;
            }
        }
    }

    /**
     * 获取用户偏移量
     *
     * @param userId 用户ID
     * @return 偏移量，不存在返回0
     */
    private long getOffset(String userId) {
        String key = VLOG_USER_OFFSET_PREFIX + userId;
        String value = RedisKit.Value.get(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    /**
     * 推进用户偏移量
     *
     * @param userId   用户ID
     * @param consumed 本次消费的数量
     */
    private void advanceOffset(String userId, int consumed) {
        if (consumed <= 0) {
            return;
        }
        String key = VLOG_USER_OFFSET_PREFIX + userId;
        long current = getOffset(userId);
        RedisKit.Value.set(key, String.valueOf(current + consumed));
    }

    /**
     * 记录用户已看过的视频
     *
     * @param userId  用户ID
     * @param vlogIds 视频ID列表
     */
    private void recordUserViewed(String userId, Set<String> vlogIds) {
        if (vlogIds.isEmpty()) {
            return;
        }
        String key = VLOG_USER_VIEWED_PREFIX + userId;
        for (String vlogId : vlogIds) {
            int idx = RedisKit.Hash.get(VLOG_ID_MAPPING, vlogId);
            RedisKit.Bitmap.set(key, idx, true);
        }
    }

    /**
     * 检查视频是否已被用户查看
     *
     * @param userId 用户ID
     * @param vlogId 视频ID
     * @return 是否已被用户查看
     */
    private boolean isViewed(String userId, String vlogId) {
        // 从视频ID映射表中获取视频的索引
        int idx = RedisKit.Hash.get(VLOG_ID_MAPPING, vlogId);
        // 检查用户是否已查看该视频
        return RedisKit.Bitmap.get(VLOG_USER_VIEWED_PREFIX + userId, idx);
    }
}
