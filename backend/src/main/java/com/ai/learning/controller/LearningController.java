package com.ai.learning.controller;

import com.ai.learning.dto.LearningProgressDTO;
import com.ai.learning.service.LearningService;
import com.ai.learning.vo.LearningReportVO;
import com.ai.learning.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 学习跟踪控制器
 */
@Slf4j
@RestController
@RequestMapping("/learning")
public class LearningController {
    
    @Autowired
    private LearningService learningService;
    
    /**
     * 更新学习进度
     */
    @PostMapping("/progress")
    public Result<Void> updateProgress(@RequestBody @Validated LearningProgressDTO dto) {
        learningService.updateLearningProgress(dto);
        return Result.success("学习进度已更新");
    }
    
    /**
     * 获取用户学习进度
     */
    @GetMapping("/progress")
    public Result<Object> getProgress(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        Object progress = learningService.getUserLearningProgress(userId, courseId);
        return Result.success(progress);
    }
    
    /**
     * 获取用户学习统计
     */
    @GetMapping("/stats")
    public Result<Object> getStats(@RequestParam Long userId) {
        Object stats = learningService.getUserLearningStats(userId);
        return Result.success(stats);
    }
    
    /**
     * 生成学习报告
     */
    @GetMapping("/report")
    public Result<LearningReportVO> generateReport(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "week") String period) {
        LearningReportVO report = learningService.generateLearningReport(userId, period);
        return Result.success(report);
    }
    
    /**
     * 获取知识点掌握度分析
     */
    @GetMapping("/knowledge-mastery")
    public Result<Object> getKnowledgeMastery(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        Object analysis = learningService.getKnowledgeMasteryAnalysis(userId, courseId);
        return Result.success(analysis);
    }
}
