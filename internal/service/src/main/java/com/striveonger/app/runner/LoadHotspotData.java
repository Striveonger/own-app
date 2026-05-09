package com.striveonger.app.runner;


import java.util.Map;

import com.striveonger.app.service.VlogPoolService;
import com.striveonger.common.core.Timepiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.striveonger.app.service.KvStorageService;
import com.striveonger.common.third.actuator.constant.ServiceStatus;

import jakarta.annotation.Resource;

/**
 * 加载热点数据
 * @author Mr.Lee
 * @since 2025-06-26 16:40
 */
@Component
public class LoadHotspotData implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(LoadHotspotData.class);

    @Resource
    private KvStorageService kvStorageService;

    @Resource
    private VlogPoolService vlogPoolServicePlanA;

    @Override
    public void run(String... args) throws Exception {
        Timepiece timepiece = Timepiece.of("Load Host Data...");
        if (ServiceStatus.Type.UNKNOWN == ServiceStatus.Operator.status()) {
            log.info("readiness health up");
            // 加载热点数据
            kvStorageService.save("a", Map.of("value", "a", "description", "a"));
            kvStorageService.save("b", Map.of("value", "b", "description", "b"));
            kvStorageService.save("c", Map.of("value", "c", "description", "c"));

            // 加载视频池数据
            vlogPoolServicePlanA.loadVlogPoolData();

            // 设置服务状态为可以提供服务的状态(准备就绪, 可以接入流量, k8s 可以将 pod 从 pending 状态迁移到 running 状态)
            ServiceStatus.Operator.ready();
        }
        timepiece.show();
    }
}
