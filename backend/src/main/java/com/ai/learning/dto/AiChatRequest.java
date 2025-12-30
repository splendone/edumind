package com.ai.learning.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    private Long courseId;
    
    private String sessionId;
    
    @NotBlank(message = "问题不能为空")
    private String question;
    
    private String context;
}
