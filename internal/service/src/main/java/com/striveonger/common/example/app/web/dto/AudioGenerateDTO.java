package com.striveonger.common.example.app.web.dto;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 音频生成入参
 * @author Mr.Lee
 * @since 2025-06-30 17:01
 */
public class AudioGenerateDTO {

    private String text;

    private String voice;

    private Float speed;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVoice() {
        return StrUtil.isBlank(voice) ? "diana" : voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public float getSpeed() {
        return speed == null ? 1.0f : speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
