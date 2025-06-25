package com.striveonger.common.example.app.web.dto;

import java.time.LocalDateTime;

/**
 * @author Mr.Lee
 * @since 2025-04-26 10:39
 */
public class StorageDTO {

    private String key;

    private String value;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
