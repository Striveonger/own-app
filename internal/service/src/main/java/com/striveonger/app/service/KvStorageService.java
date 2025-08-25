package com.striveonger.app.service;

import com.striveonger.common.core.constant.ResultStatus;
import com.striveonger.common.core.result.Result;
import com.striveonger.app.storage.LRUStorage;
import com.striveonger.app.web.dto.StorageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Lee
 * @since 2025-04-30 16:37
 */
@Service
public class KvStorageService {
    private final Logger log = LoggerFactory.getLogger(KvStorageService.class);

    private final LRUStorage<StorageDTO> storage;

    public KvStorageService(@Value("${own.app.storage.memory.max-rows:1000}") Integer maxRows) {
        this.storage = new LRUStorage<>(maxRows);
    }

    public Result save(String key, Map<String, String> data) {
        log.info("save key={}, data={}", key, data);
        if ("all".equals(key)) {
            return Result.fail().message("key cannot be 'all'").show();
        }
        String value = data.get("value");
        String description = data.get("description");
        if (storage.containsKey(key)) {
            return Result.fail().message("key already exists");
        }
        StorageDTO item = new StorageDTO();
        item.setKey(key);
        item.setValue(value);
        item.setDescription(description);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        storage.put(key, item);
        return Result.success();
    }

    public Result delete(String key) {
        log.info("delete key={}", key);
        StorageDTO item = storage.remove(key);
        if (Objects.nonNull(item)) {
            return Result.success();
        } else {
            return Result.status(ResultStatus.NOT_FOUND);
        }
    }

    public Result update(String key, Map<String, String> data) {
        log.info("update key={}, data={}", key, data);
        String value = data.get("value");
        String description = data.get("description");
        if (storage.containsKey(key)) {
            StorageDTO item = storage.get(key);
            item.setValue(value);
            item.setDescription(description);
            item.setUpdateTime(LocalDateTime.now());
            storage.put(key, item);
            return Result.success();
        }
        return Result.status(ResultStatus.NOT_FOUND);
    }

    public Result list() {
        log.info("list all");
        List<StorageDTO> list = storage.values().stream().sorted(Comparator.comparing(StorageDTO::getUpdateTime)).toList();
        return Result.success().data(list);
    }

    public Result get(String key) {
        log.info("get key={}", key);
        if (Objects.isNull(key)) {
            return Result.fail().message("key is null");
        }
        StorageDTO item = storage.get(key);
        if (Objects.nonNull(item)) {
            return Result.success().data(item);
        } else {
            return Result.status(ResultStatus.NOT_FOUND);
        }
    }

}
