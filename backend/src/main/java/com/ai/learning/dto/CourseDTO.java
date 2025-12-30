package com.ai.learning.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 课程DTO
 */
@Data
public class CourseDTO {
    
    @NotBlank(message = "课程标题不能为空")
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String coverImage;
    
    private Long teacherId;
    
    private Long categoryId;
    
    private String level;
    
    private BigDecimal price;
    
    private Integer totalHours;
}
