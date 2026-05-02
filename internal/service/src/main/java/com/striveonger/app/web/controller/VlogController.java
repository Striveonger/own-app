package com.striveonger.app.web.controller;

import com.striveonger.app.service.VlogPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.striveonger.common.core.result.Result;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;

/**
 * @author Mr.Lee
 * @since 2026-04-26 13:33
 */
@RestController
public class VlogController {
    private final Logger log = LoggerFactory.getLogger(VlogController.class);

    @Resource
    private VlogPoolService vlogPoolServicePlanA;

    @GetMapping("/api/v1/vlog/list")
    public Result list(String userId) {
        log.info("get vlog list");
        if (StrUtil.isBlank(userId)) {
            userId = "1";
        }
        var list = vlogPoolServicePlanA.list(userId);
        return Result.success().data(list);
    }
}
