package com.striveonger.app.runner;

import com.striveonger.common.core.thread.ThreadKit;
import com.striveonger.app.service.KvStorageService;
import com.striveonger.common.third.actuator.constant.ServiceStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Mr.Lee
 * @since 2025-06-26 16:40
 */
@Component
public class LoadHotspotData implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(LoadHotspotData.class);

    @Resource
    private KvStorageService service;

    @Override
    public void run(String... args) throws Exception {
        if (ServiceStatus.Type.UNKNOWN == ServiceStatus.Operator.status()) {
            log.info("readiness health up");
            // 加载热点数据
            service.save("a", Map.of("value", "a", "description", "a"));
            service.save("b", Map.of("value", "b", "description", "b"));
            service.save("c", Map.of("value", "c", "description", "c"));
            // 模拟耗时操作
            ThreadKit.sleep(3000);
            // 设置服务状态为可以提供服务的状态(准备就绪, 可以接入流量, k8s 可以将 pod 从 pending 状态迁移到 running 状态)
            ServiceStatus.Operator.ready();
        }
    }
}
