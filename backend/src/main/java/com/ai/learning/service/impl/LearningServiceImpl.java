package com.ai.learning.service.impl;

import com.ai.learning.dto.LearningProgressDTO;
import com.ai.learning.entity.LearningRecord;
import com.ai.learning.mapper.LearningRecordMapper;
import com.ai.learning.service.LearningService;
import com.ai.learning.vo.LearningReportVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 学习跟踪服务实现
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {
    
    @Autowired
    private LearningRecordMapper learningRecordMapper;
    
    @Override
    @Transactional
    public void updateLearningProgress(LearningProgressDTO dto) {
        log.info("Update learning progress: userId={}, courseId={}, videoId={}", 
                dto.getUserId(), dto.getCourseId(), dto.getVideoId());
        
        // 查找或创建学习记录
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getUserId, dto.getUserId())
               .eq(LearningRecord::getCourseId, dto.getCourseId())
               .eq(LearningRecord::getVideoId, dto.getVideoId());
        
        LearningRecord record = learningRecordMapper.selectOne(wrapper);
        
        if (record == null) {
            // 创建新记录
            record = new LearningRecord();
            record.setUserId(dto.getUserId());
            record.setCourseId(dto.getCourseId());
            record.setVideoId(dto.getVideoId());
            record.setLearnTime(dto.getLearnTime());
            record.setProgress(dto.getProgress());
            record.setLastLearnPosition(dto.getLastPosition());
            record.setIsCompleted(dto.getProgress().compareTo(new BigDecimal("95")) >= 0 ? 1 : 0);
            learningRecordMapper.insert(record);
        } else {
            // 更新现有记录
            record.setLearnTime(record.getLearnTime() + dto.getLearnTime());
            record.setProgress(dto.getProgress());
            record.setLastLearnPosition(dto.getLastPosition());
            record.setIsCompleted(dto.getProgress().compareTo(new BigDecimal("95")) >= 0 ? 1 : 0);
            learningRecordMapper.updateById(record);
        }
    }
    
    @Override
    public Object getUserLearningProgress(Long userId, Long courseId) {
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getUserId, userId)
               .eq(LearningRecord::getCourseId, courseId)
               .orderByDesc(LearningRecord::getUpdatedTime);
        
        List<LearningRecord> records = learningRecordMapper.selectList(wrapper);
        
        // 计算总体进度
        int totalVideos = records.size();
        int completedVideos = (int) records.stream()
                .filter(r -> r.getIsCompleted() == 1)
                .count();
        
        BigDecimal avgProgress = records.stream()
                .map(LearningRecord::getProgress)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(Math.max(totalVideos, 1)), 2, BigDecimal.ROUND_HALF_UP);
        
        int totalLearnTime = records.stream()
                .mapToInt(LearningRecord::getLearnTime)
                .sum();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalVideos", totalVideos);
        result.put("completedVideos", completedVideos);
        result.put("avgProgress", avgProgress);
        result.put("totalLearnTime", totalLearnTime);
        result.put("records", records);
        
        return result;
    }
    
    @Override
    public Object getUserLearningStats(Long userId) {
        Map<String, Object> stats = learningRecordMapper.getUserLearningStats(userId);
        Long totalTime = learningRecordMapper.getTotalLearnTime(userId);
        
        stats.put("totalLearnTime", totalTime != null ? totalTime : 0);
        
        return stats;
    }
    
    @Override
    public LearningReportVO generateLearningReport(Long userId, String period) {
        log.info("Generating learning report for userId={}, period={}", userId, period);
        
        LearningReportVO report = new LearningReportVO();
        report.setUserId(userId);
        report.setPeriod(period);
        report.setGenerateTime(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));
        
        // 获取学习统计数据
        Map<String, Object> stats = (Map<String, Object>) getUserLearningStats(userId);
        report.setTotalCourses(((Number) stats.getOrDefault("courseCount", 0)).intValue());
        report.setCompletedCourses(((Number) stats.getOrDefault("completedCount", 0)).intValue());
        report.setTotalLearnTime(((Number) stats.getOrDefault("totalLearnTime", 0)).longValue());
        report.setAvgProgress(((Number) stats.getOrDefault("avgProgress", 0)).doubleValue());
        
        // 获取最近学习的课程
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getUserId, userId)
               .orderByDesc(LearningRecord::getUpdatedTime)
               .last("LIMIT 10");
        
        List<LearningRecord> recentRecords = learningRecordMapper.selectList(wrapper);
        report.setRecentCourses(recentRecords);
        
        // 学习趋势数据（简化实现）
        Map<String, Integer> trendData = new HashMap<>();
        trendData.put("本周", 120);
        trendData.put("上周", 90);
        report.setLearningTrend(trendData);
        
        // 知识点掌握情况
        List<Map<String, Object>> knowledgeMastery = new ArrayList<>();
        Map<String, Object> kp1 = new HashMap<>();
        kp1.put("name", "Java基础");
        kp1.put("mastery", 85.5);
        knowledgeMastery.add(kp1);
        
        Map<String, Object> kp2 = new HashMap<>();
        kp2.put("name", "数据结构");
        kp2.put("mastery", 72.3);
        knowledgeMastery.add(kp2);
        
        report.setKnowledgeMastery(knowledgeMastery);
        
        return report;
    }
    
    @Override
    public Object getKnowledgeMasteryAnalysis(Long userId, Long courseId) {
        // 基于学习记录和测试成绩分析知识点掌握度
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取学习进度
        Object progress = getUserLearningProgress(userId, courseId);
        analysis.put("learningProgress", progress);
        
        // 知识点掌握度评估（简化实现）
        List<Map<String, Object>> knowledgePoints = new ArrayList<>();
        
        Map<String, Object> kp1 = new HashMap<>();
        kp1.put("name", "变量与数据类型");
        kp1.put("masteryLevel", "精通");
        kp1.put("score", 95);
        kp1.put("practiceCount", 20);
        knowledgePoints.add(kp1);
        
        Map<String, Object> kp2 = new HashMap<>();
        kp2.put("name", "循环与条件");
        kp2.put("masteryLevel", "良好");
        kp2.put("score", 78);
        kp2.put("practiceCount", 15);
        knowledgePoints.add(kp2);
        
        analysis.put("knowledgePoints", knowledgePoints);
        analysis.put("overallMastery", 86.5);
        
        return analysis;
    }
}
