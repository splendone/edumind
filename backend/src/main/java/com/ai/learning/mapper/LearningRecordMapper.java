package com.ai.learning.mapper;

import com.ai.learning.entity.LearningRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 学习记录Mapper
 */
@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {
    
    /**
     * 获取用户的总学习时长
     */
    @Select("SELECT SUM(learn_time) as totalTime FROM learning_record WHERE user_id = #{userId}")
    Long getTotalLearnTime(@Param("userId") Long userId);
    
    /**
     * 获取用户的课程学习统计
     */
    @Select("SELECT " +
            "COUNT(DISTINCT course_id) as courseCount, " +
            "SUM(is_completed) as completedCount, " +
            "AVG(progress) as avgProgress " +
            "FROM learning_record WHERE user_id = #{userId}")
    Map<String, Object> getUserLearningStats(@Param("userId") Long userId);
}
