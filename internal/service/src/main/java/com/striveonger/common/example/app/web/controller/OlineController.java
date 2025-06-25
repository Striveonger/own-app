package com.striveonger.common.example.app.web.controller;

import com.striveonger.common.core.result.Result;
import com.striveonger.common.third.actuator.constant.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Mr.Lee
 * @since 2025-04-26 10:18
 */
@RestController
public class OlineController {
    private final Logger log = LoggerFactory.getLogger(OlineController.class);

    @GetMapping("/api/v1/app/up")
    public Result up() {
        ServiceStatus.Operator.up();
        return Result.success();
    }

    @GetMapping("/api/v1/app/down")
    public Result down() {
        ServiceStatus.Operator.down();
        return Result.success();
    }

    @GetMapping("/api/v1/app/status")
    public Result status() {
        return Result.success().data(Map.of("status", ServiceStatus.Operator.status()));
    }

}
