package com.ai.learning.service;

import com.ai.learning.dto.LearningProgressDTO;
import com.ai.learning.vo.LearningReportVO;

/**
 * 学习跟踪服务接口
 */
public interface LearningService {
    
    /**
     * 更新学习进度
     */
    void updateLearningProgress(LearningProgressDTO dto);
    
    /**
     * 获取用户的学习进度
     */
    Object getUserLearningProgress(Long userId, Long courseId);
    
    /**
     * 获取用户的学习统计
     */
    Object getUserLearningStats(Long userId);
    
    /**
     * 生成学习报告
     */
    LearningReportVO generateLearningReport(Long userId, String period);
    
    /**
     * 获取知识点掌握度分析
     */
    Object getKnowledgeMasteryAnalysis(Long userId, Long courseId);
}
