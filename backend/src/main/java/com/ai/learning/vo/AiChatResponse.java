package com.ai.learning.vo;

import lombok.Data;
import java.util.List;

/**
 * AI聊天响应VO
 */
@Data
public class AiChatResponse {
    
    private String sessionId;
    
    private String answer;
    
    private Long recordId;
    
    private List<String> relevantPoints;
    
    private Long timestamp;
    
    public AiChatResponse() {
        this.timestamp = System.currentTimeMillis();
    }
}
