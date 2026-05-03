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
 * @since 2026-05-02
 */
@Service
public class VlogPoolServicePlanB extends VlogPoolService {
    private final Logger log = LoggerFactory.getLogger(VlogPoolServicePlanB.class);

    /**
     * 用户已看过的视频前缀
     */
    private static final String VLOG_USER_VIEWED_PREFIX = "vlog:user:viewed:";

    /**
     * 用户偏移量前缀
     */
    private static final String VLOG_USER_OFFSET_PREFIX = "vlog:user:offset:";

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
        Set<String> viewed = getUserViewedVlogs(userId);
        log.info("offset: {}, viewed size: {}", offset, viewed.size());
        timepiece.mark("get-offset: {}, viewed: {}", offset, viewed.size());

        // 2. 获取热门池和新池的前100条视频
        Set<String> hoted = new HashSet<>(RedisKit.ZSet.range(VLOG_POOL_HOT, 0, CANDIDATE_POOL_SIZE));
        Set<String> newed = new HashSet<>(RedisKit.ZSet.range(VLOG_POOL_NEW, 0, CANDIDATE_POOL_SIZE));
        // 去重：热门池和新池中都存在的视频
        hoted.removeAll(viewed);
        newed.removeAll(viewed);
        timepiece.mark("remove-viewed: {}", viewed.size());
        // 计算热门池和新池的视频数量
        int hotCount = Math.min(hoted.size(), (int) Math.ceil(count * HOT_WEIGHT));
        int newCount = Math.min(newed.size(), (int) Math.ceil(count * NEW_WEIGHT));
        // stream 转换为 List 并截取(可能会带来性能损耗, 后面可以改为朴实的for循环)
        result.addAll(hoted.stream().toList().subList(0, hotCount));
        result.addAll(newed.stream().toList().subList(0, newCount));
        timepiece.mark("get-hot&new-candidates: {}", result.size());

        // 3. 从偏移位置开始遍历时间线，跳过已看过的和已返回的视频，返回目标数量
        int fetched = 0, diff = count - result.size();
        // 升序遍历时间线
        List<String> timeline = RedisKit.ZSet.range(false, VLOG_POOL_TIMELINE, (int) offset, -1);
        for (int i = (int) offset; i < timeline.size() && fetched < diff; i++) {
            fetched++;
            String vlogId = timeline.get(i);
            // 跳过已看过的或已返回的（去重）
            if (viewed.contains(vlogId) || result.contains(vlogId)) {
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
     * 获取用户已看过的视频集合
     *
     * @param userId 用户ID
     * @return 已看过的视频ID集合
     */
    private Set<String> getUserViewedVlogs(String userId) {
        String key = VLOG_USER_VIEWED_PREFIX + userId;
        return RedisKit.getClient().getSet(key);
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
        RedisKit.Set.add(key, vlogIds);
    }
}
