package com.ai.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String coverImage;
    
    private Long teacherId;
    
    private Long categoryId;
    
    private CourseLevel level;
    
    private BigDecimal price;
    
    private Integer totalHours;
    
    private Integer studentCount;
    
    private BigDecimal rating;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 课程难度枚举
     */
    public enum CourseLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
