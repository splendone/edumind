package com.ai.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI增强型在线学习平台 - 主启动类
 */
@SpringBootApplication
@MapperScan("com.ai.learning.mapper")
@EnableAsync
@EnableScheduling
public class AiLearningPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningPlatformApplication.class, args);
        System.out.println("========================================");
        System.out.println("AI Learning Platform Started Successfully!");
        System.out.println("API Documentation: http://localhost:8080/api/doc");
        System.out.println("========================================");
    }
}
