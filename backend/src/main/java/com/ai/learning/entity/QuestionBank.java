package com.ai.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题库实体类
 */
@Data
@TableName("question_bank")
public class QuestionBank {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long courseId;
    
    private Long chapterId;
    
    private QuestionType questionType;
    
    private String questionContent;
    
    private String options;
    
    private String correctAnswer;
    
    private String analysis;
    
    private Difficulty difficulty;
    
    private String knowledgePoints;
    
    private Integer usageCount;
    
    private BigDecimal correctRate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 题目类型枚举
     */
    public enum QuestionType {
        SINGLE,    // 单选
        MULTIPLE,  // 多选
        JUDGE,     // 判断
        FILL,      // 填空
        ESSAY      // 问答
    }
    
    /**
     * 难度枚举
     */
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
