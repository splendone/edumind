package com.ai.learning.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI API调用客户端
 * 支持OpenAI、通义千问、文心一言等
 */
@Slf4j
@Component
public class AiApiClient {
    
    @Value("${ai.api.base-url}")
    private String baseUrl;
    
    @Value("${ai.api.api-key}")
    private String apiKey;
    
    @Value("${ai.api.model}")
    private String model;
    
    @Value("${ai.api.max-tokens}")
    private Integer maxTokens;
    
    @Value("${ai.api.temperature}")
    private Double temperature;
    
    private final OkHttpClient httpClient;
    
    public AiApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * 生成AI回答
     */
    public String generateResponse(String prompt, List<Map<String, String>> conversationHistory) {
        try {
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统消息
            messages.add(Map.of(
                "role", "system",
                "content", "你是一个专业、友好的在线学习助教。"
            ));
            
            // 添加历史对话
            if (conversationHistory != null && !conversationHistory.isEmpty()) {
                messages.addAll(conversationHistory);
            }
            
            // 添加当前问题
            messages.add(Map.of(
                "role", "user",
                "content", prompt
            ));
            
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            
            // 发送HTTP请求
            Request request = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(
                        requestBody.toJSONString(),
                        MediaType.parse("application/json")
                    ))
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("AI API request failed: {}", response.code());
                    return "抱歉，AI服务暂时不可用，请稍后再试。";
                }
                
                String responseBody = response.body().string();
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                
                // 解析响应
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    return message.getString("content");
                }
                
                return "抱歉，未能生成回答。";
            }
            
        } catch (IOException e) {
            log.error("AI API call failed", e);
            return "抱歉，AI服务调用失败: " + e.getMessage();
        }
    }
    
    /**
     * 生成文本嵌入向量（用于RAG检索）
     */
    public float[] generateEmbedding(String text) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "text-embedding-ada-002");
            requestBody.put("input", text);
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/embeddings")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(
                        requestBody.toJSONString(),
                        MediaType.parse("application/json")
                    ))
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Embedding API request failed: {}", response.code());
                    return new float[0];
                }
                
                String responseBody = response.body().string();
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                
                JSONArray data = jsonResponse.getJSONArray("data");
                if (data != null && !data.isEmpty()) {
                    JSONObject firstData = data.getJSONObject(0);
                    JSONArray embedding = firstData.getJSONArray("embedding");
                    
                    float[] vector = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        vector[i] = embedding.getFloatValue(i);
                    }
                    return vector;
                }
                
                return new float[0];
            }
            
        } catch (IOException e) {
            log.error("Embedding API call failed", e);
            return new float[0];
        }
    }
}
