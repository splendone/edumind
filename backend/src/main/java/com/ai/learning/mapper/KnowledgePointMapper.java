package com.ai.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识点Mapper
 */
@Mapper
public interface KnowledgePointMapper extends BaseMapper<Object> {
    // 可以添加自定义的向量检索方法
}
