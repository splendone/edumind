package com.ai.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI对话记录实体类
 */
@Data
@TableName("ai_chat_record")
public class AiChatRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long courseId;
    
    private String sessionId;
    
    private String question;
    
    private String answer;
    
    private String context;
    
    private Integer rating;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
