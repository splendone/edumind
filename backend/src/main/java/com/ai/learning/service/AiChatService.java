package com.ai.learning.service;

import com.ai.learning.dto.AiChatRequest;
import com.ai.learning.vo.AiChatResponse;

/**
 * AI智能答疑服务接口
 */
public interface AiChatService {
    
    /**
     * 基于课程内容的智能问答（RAG检索增强）
     */
    AiChatResponse chat(AiChatRequest request);
    
    /**
     * 获取对话历史
     */
    Object getChatHistory(Long userId, String sessionId);
    
    /**
     * 评价AI回答
     */
    void rateAnswer(Long recordId, Integer rating);
}
