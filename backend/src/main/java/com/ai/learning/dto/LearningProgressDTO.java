package com.ai.learning.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 学习进度DTO
 */
@Data
public class LearningProgressDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "视频ID不能为空")
    private Long videoId;
    
    private Integer learnTime;
    
    private BigDecimal progress;
    
    private Integer lastPosition;
}
