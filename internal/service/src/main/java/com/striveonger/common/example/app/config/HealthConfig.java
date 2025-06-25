package com.striveonger.common.example.app.config;

import com.striveonger.common.core.thread.ThreadKit;
import com.striveonger.common.example.app.service.MemoryStorageService;
import com.striveonger.common.third.actuator.constant.ServiceStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Lee
 * @since 2024-11-25 16:11
 */
@Configuration
public class HealthConfig {
    private final Logger log = LoggerFactory.getLogger(HealthConfig.class);

    @Resource
    private MemoryStorageService service;

    @Bean("active")
    HealthIndicator active() {
        return () -> {
            if (ServiceStatus.Operator.isDown()) {
                log.info("liveness health down");
                return Health.down().build();
            } else {
                log.info("liveness health up");
                return Health.up().build();
            }
        };
    }

    @Bean("ready")
    HealthIndicator ready() {
        return () -> {
            if (ServiceStatus.UNKNOWN.equals(ServiceStatus.Operator.status())) {
                log.info("readiness health up");
                // 加载热点数据
                service.save("a", Map.of("value", "a", "description", "a"));
                service.save("b", Map.of("value", "b", "description", "b"));
                service.save("c", Map.of("value", "c", "description", "c"));
                ThreadKit.sleep(10, TimeUnit.SECONDS);
                ServiceStatus.Operator.up();
                return Health.up().build();
            }
            if (ServiceStatus.Operator.isDown()) {
                return Health.down().build();
            } else {
                return Health.up().build();
            }
        };
    }

}
