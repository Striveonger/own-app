package com.striveonger.app.web.controller;

import com.striveonger.common.core.Jackson;
import com.striveonger.common.core.KeyGen;
import com.striveonger.common.core.constant.ResultStatus;
import com.striveonger.common.core.exception.CustomException;
import com.striveonger.common.core.result.Result;
import com.striveonger.common.core.thread.ThreadKit;
import com.striveonger.common.core.thread.ThreadPool;
import com.striveonger.app.web.dto.AudioGenerateDTO;
import com.striveonger.common.web.holder.WebHolder;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Mr.Lee
 * @since 2025-04-26 10:18
 */
@Controller
public class AudioController {
    private final Logger log = LoggerFactory.getLogger(AudioController.class);

    @Resource
    private OpenAiAudioSpeechModel model;

    private final ThreadPool pool = ThreadKit.pool().build();

    private final Map<String, Future<byte[]>> map = new ConcurrentHashMap<>();

    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    @PostMapping("/api/v1/audio/submit")
    @ResponseBody
    public Result submit(@RequestBody AudioGenerateDTO dto) {
        log.info("submit audio: {}", Jackson.toJSONString(dto));
        String key = KeyGen.build(dto.getText(), dto.getVoice(), Objects.toString(dto.getSpeed()));
        if (!cache.containsKey(key)) {
            Future<byte[]> future = pool.submit(() -> {
                OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                        // 使用的tts模型
                        .model("FunAudioLLM/CosyVoice2-0.5B")
                        // 输出格式
                        .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                        // 声音选择
                        .voice("FunAudioLLM/CosyVoice2-0.5B:" + dto.getVoice())
                        // 语速
                        .speed(dto.getSpeed())
                        .build();
                try {
                    SpeechResponse response = model.call(new SpeechPrompt(dto.getText(), options));
                    return response.getResult().getOutput();
                } catch (Exception e) {
                    log.error("generate audio error", e);
                    return null;
                }
            });
            map.put(key, future);
        }
        return Result.success().data(Map.of("key", key));
    }

    @GetMapping("/api/v1/audio/play")
    public void play(String key) {
        byte[] bytes;
        if (cache.containsKey(key)) {
            bytes = cache.get(key);
            WebHolder.preview("play.mp3", bytes);
        } else if (map.containsKey(key)) {
            Future<byte[]> future = map.remove(key);
            try {
                bytes = future.get();
                cache.put(key, bytes);
                WebHolder.preview("play.mp3", bytes);
            } catch (Exception e) {
                log.error("play audio error", e);
            }
        }
        throw new CustomException(ResultStatus.NOT_FOUND);
    }

}
