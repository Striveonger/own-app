package com.striveonger.common.example.app;

import com.striveonger.common.core.MarkGenerate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Mr.Lee
 * @since 2024-08-27 23:12
 */
@SpringBootApplication
public class AppLaunch {
    public static void main(String[] args) {
        SpringApplication.run(AppLaunch.class, args);
        MarkGenerate.build();
    }
}
