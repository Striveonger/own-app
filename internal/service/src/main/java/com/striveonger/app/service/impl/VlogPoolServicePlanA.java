package com.striveonger.app.service.impl;

import com.striveonger.app.service.VlogPoolService;
import com.striveonger.common.core.Timepiece;
import com.striveonger.common.third.cache.reids.RedisKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mr.Lee
 * @since 2026-04-25 11:21
 */
@Service("vlogPoolServicePlanA")
public class VlogPoolServicePlanA extends VlogPoolService {
    private final Logger log = LoggerFactory.getLogger(VlogPoolServicePlanA.class);

    /**
     * 视频池用户已看
     * 视频按上传时间排序, 最新视频在顶部
     */
    private static final String VLOG_USER_VIEWED_PREFIX = "vlog:user:viewed:";

    /**
     * 从视频池中获取用户没看过的视频ID列表
     *
     * @param userId 用户ID
     * @param count  需要获取的视频数量
     * @return 视频ID列表
     */
    @Override
    public List<String> list(String userId, int count) {
        log.info("get vlog list for user: {}, count: {}", userId, count);

        Timepiece timepiece = Timepiece.of("vlog-list-for-user: " + userId);
        // 1. 从视频池中获取候选视频列表
        Set<String> viewedVlogs = getUserViewedVlogs(userId);
        timepiece.mark("get-user-viewed-vlogs: " + viewedVlogs.size());

        List<String> hotCandidates = getCandidatesFromPool(VLOG_POOL_HOT, count, viewedVlogs);
        timepiece.mark("get-hot-vlogs: " + hotCandidates.size());
        List<String> newCandidates = getCandidatesFromPool(VLOG_POOL_NEW, count, viewedVlogs);
        timepiece.mark("get-new-vlogs: " + newCandidates.size());
        List<String> timelineCandidates = getCandidatesFromPool(VLOG_POOL_TIMELINE, count * 3, viewedVlogs);
        timepiece.mark("get-timeline-vlogs: " + timelineCandidates.size());

        List<String> result = buildRecommendList(hotCandidates, newCandidates, timelineCandidates, count);
        timepiece.mark("build-recommend-list: " + result.size());

        recordUserViewed(userId, result);
        timepiece.mark("record-user-viewed-vlogs: " + userId);
        timepiece.show();
        return result;
    }

    /**
     * 获取用户已看过的视频集合
     *
     * @param userId 用户ID
     * @return 用户已看过的视频ID集合
     */
    private Set<String> getUserViewedVlogs(String userId) {
        String key = VLOG_USER_VIEWED_PREFIX + userId;
        return RedisKit.getClient().getSet(key);
    }

    /**
     * 从指定视频池中获取候选视频列表
     *
     * @param key   视频池Key
     * @param limit 获取数量上限
     * @return 候选视频ID列表
     */
    private List<String> getCandidatesFromPool(String key, int limit, Set<String> viewed) {
        List<String> ids = RedisKit.ZSet.range(key, 0, -1);
        List<String> result = new ArrayList<>();
        int count = 0;
        for (String id : ids) {
            if (viewed.contains(id)) continue;
            if (count++ >= limit) break;
            result.add(id);
        }
        return result;
    }

    /**
     * 构建最终推荐列表
     * 按比例从热门池、新手池、时间线池中选取视频
     * 热门池50%, 新手池30%, 时间线池20%
     *
     * @param hotList      热门池过滤后视频
     * @param newList      新手池过滤后视频
     * @param timelineList 时间线池过滤后视频
     * @param count        需要返回的视频数量
     * @return 最终推荐列表
     */
    private List<String> buildRecommendList(List<String> hotList, List<String> newList, List<String> timelineList, int count) {
        List<String> result = new ArrayList<>();
        addFromList(result, hotList, Math.min(hotList.size(), (int) Math.ceil(count * 0.5)));
        addFromList(result, newList, Math.min(newList.size(), (int) Math.ceil(count * 0.3)));
        addFromList(result, timelineList, Math.min(timelineList.size(), count - result.size()));
        return result;
    }

    private void addFromList(List<String> result, List<String> source, int count) {
        int added = 0;
        for (String vlogId : source) {
            if (added >= count) {
                break;
            }
            if (!result.contains(vlogId)) {
                result.add(vlogId);
                added++;
            }
        }
    }

    /**
     * 记录用户已看过的视频
     * 将视频ID添加到用户的去重池中
     * 如果视频是热点视频, 则标记为可复活状态
     *
     * @param userId  用户ID
     * @param vlogIds 视频ID列表
     */
    private void recordUserViewed(String userId, List<String> vlogIds) {
        if (vlogIds.isEmpty()) {
            return;
        }
        String key = VLOG_USER_VIEWED_PREFIX + userId;
        RedisKit.Set.add(key, vlogIds);
    }

}
