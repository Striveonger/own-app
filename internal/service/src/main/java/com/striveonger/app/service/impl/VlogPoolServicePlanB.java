package com.striveonger.app.service.impl;

import com.striveonger.app.service.VlogPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Lee
 * @since 2026-04-25 11:21
 */
@Service
public class VlogPoolServicePlanB extends VlogPoolService {
    private final Logger log = LoggerFactory.getLogger(VlogPoolServicePlanB.class);


    /**
     * 视频池用户已看
     * 视频按上传时间排序, 最新视频在顶部
     */
    private static final String VLOG_USER_VIEWED_PREFIX = "vlog:user:viewed:";

    /**
     * 从视频池中获取用户没看过的视频ID列表
     * @param userId 用户ID
     * @param count 需要获取的视频数量
     * @return 视频ID列表
     */
    public List<String> list(String userId, int count) {
        log.info("get vlog list for user: {}, count: {}", userId, count);

        return List.of();
    }

}
