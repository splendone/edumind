package com.ai.learning.service;

import com.ai.learning.dto.CourseDTO;
import com.ai.learning.entity.Course;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 分页查询课程列表
     */
    Page<Course> getCourseList(Integer current, Integer size, String keyword, Long categoryId);
    
    /**
     * 创建课程
     */
    Long createCourse(CourseDTO courseDTO);
    
    /**
     * 更新课程
     */
    void updateCourse(Long id, CourseDTO courseDTO);
    
    /**
     * 获取课程详情
     */
    Course getCourseDetail(Long id);
    
    /**
     * 删除课程
     */
    void deleteCourse(Long id);
    
    /**
     * 推荐课程列表
     */
    Object getRecommendedCourses(Long userId, Integer limit);
}
