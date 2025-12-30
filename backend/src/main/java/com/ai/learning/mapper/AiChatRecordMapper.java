package com.ai.learning.mapper;

import com.ai.learning.entity.AiChatRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话记录Mapper
 */
@Mapper
public interface AiChatRecordMapper extends BaseMapper<AiChatRecord> {
}
