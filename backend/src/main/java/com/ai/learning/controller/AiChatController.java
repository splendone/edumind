package com.ai.learning.controller;

import com.ai.learning.dto.AiChatRequest;
import com.ai.learning.service.AiChatService;
import com.ai.learning.vo.AiChatResponse;
import com.ai.learning.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI智能答疑控制器
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiChatController {
    
    @Autowired
    private AiChatService aiChatService;
    
    /**
     * AI智能问答
     */
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@RequestBody @Validated AiChatRequest request) {
        log.info("AI chat request: {}", request);
        AiChatResponse response = aiChatService.chat(request);
        return Result.success(response);
    }
    
    /**
     * 获取对话历史
     */
    @GetMapping("/chat/history")
    public Result<Object> getChatHistory(
            @RequestParam Long userId,
            @RequestParam String sessionId) {
        Object history = aiChatService.getChatHistory(userId, sessionId);
        return Result.success(history);
    }
    
    /**
     * 评价AI回答
     */
    @PostMapping("/chat/rate")
    public Result<Void> rateAnswer(
            @RequestParam Long recordId,
            @RequestParam Integer rating) {
        aiChatService.rateAnswer(recordId, rating);
        return Result.success();
    }
}
