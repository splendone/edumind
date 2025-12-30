package com.ai.learning.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 学习报告VO
 */
@Data
public class LearningReportVO {
    
    private Long userId;
    
    private String period;
    
    private String generateTime;
    
    private Integer totalCourses;
    
    private Integer completedCourses;
    
    private Long totalLearnTime;
    
    private Double avgProgress;
    
    private List<Object> recentCourses;
    
    private Map<String, Integer> learningTrend;
    
    private List<Map<String, Object>> knowledgeMastery;
}
