package com.striveonger.app.web.controller;

import com.striveonger.common.core.result.Result;
import com.striveonger.app.service.KvStorageService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Mr.Lee
 * @since 2025-04-26 10:18
 */
@RestController
public class MemoryStorageController {
    private final Logger log = LoggerFactory.getLogger(MemoryStorageController.class);

    @Resource
    private KvStorageService service;

    @PostMapping("/api/v1/storage/{key}")
    public Result save(@PathVariable String key, @RequestBody Map<String, String> data) {
        return service.save(key, data);
    }

    @DeleteMapping("/api/v1/storage/{key}")
    public Result delete(@PathVariable String key) {
        return service.delete(key);
    }

    @PutMapping("/api/v1/storage/{key}")
    public Result update(@PathVariable String key, @RequestBody Map<String, String> data) {
        return service.update(key, data);
    }

    @GetMapping("/api/v1/storage/all")
    public Result list() {
        return service.list();
    }

    @GetMapping("/api/v1/storage/{key}")
    public Result get(@PathVariable String key) {
        return service.get(key);
    }
}
