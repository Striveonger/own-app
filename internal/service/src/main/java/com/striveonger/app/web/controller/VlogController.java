package com.striveonger.app.web.controller;

import cn.hutool.core.util.StrUtil;
import com.striveonger.app.service.VlogPoolService;
import com.striveonger.common.core.result.Result;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Lee
 * @since 2026-04-26 13:33
 */
@RestController
public class VlogController {
    private final Logger log = LoggerFactory.getLogger(VlogController.class);

    @Resource
    private VlogPoolService vlogPoolServicePlanA;

    @Resource
    private VlogPoolService vlogPoolServicePlanB;

    @GetMapping("/api/v1/vlog/list")
    public Result list(String userId, @RequestParam(defaultValue = "A") String plan) {
        log.info("get vlog list, plan: {}", plan);
        if (StrUtil.isBlank(userId)) {
            userId = "1";
        }
        VlogPoolService service = "B".equalsIgnoreCase(plan) ? vlogPoolServicePlanB : vlogPoolServicePlanA;
        var list = service.list(userId);
        return Result.success().data(list);
    }
}
