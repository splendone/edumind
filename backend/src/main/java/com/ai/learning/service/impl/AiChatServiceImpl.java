package com.ai.learning.service.impl;

import com.ai.learning.dto.AiChatRequest;
import com.ai.learning.entity.AiChatRecord;
import com.ai.learning.mapper.AiChatRecordMapper;
import com.ai.learning.mapper.KnowledgePointMapper;
import com.ai.learning.service.AiChatService;
import com.ai.learning.utils.AiApiClient;
import com.ai.learning.vo.AiChatResponse;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI智能答疑服务实现类
 * 实现RAG（检索增强生成）技术
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {
    
    @Autowired
    private AiChatRecordMapper chatRecordMapper;
    
    @Autowired
    private KnowledgePointMapper knowledgePointMapper;
    
    @Autowired
    private AiApiClient aiApiClient;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${ai.rag.enabled}")
    private Boolean ragEnabled;
    
    @Value("${ai.rag.top-k}")
    private Integer topK;
    
    @Override
    public AiChatResponse chat(AiChatRequest request) {
        log.info("AI Chat Request: userId={}, courseId={}, question={}", 
                request.getUserId(), request.getCourseId(), request.getQuestion());
        
        try {
            // 1. 生成或获取会话ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            // 2. 获取对话上下文
            List<Map<String, String>> conversationHistory = getConversationHistory(
                request.getUserId(), sessionId
            );
            
            // 3. RAG检索：从课程知识点中检索相关内容
            String relevantContext = "";
            if (ragEnabled && request.getCourseId() != null) {
                relevantContext = retrieveRelevantKnowledge(
                    request.getCourseId(), 
                    request.getQuestion()
                );
            }
            
            // 4. 构建增强的提示词
            String enhancedPrompt = buildEnhancedPrompt(
                request.getQuestion(), 
                relevantContext, 
                conversationHistory
            );
            
            // 5. 调用AI API生成回答
            String aiAnswer = aiApiClient.generateResponse(enhancedPrompt, conversationHistory);
            
            // 6. 保存对话记录到数据库
            AiChatRecord record = new AiChatRecord();
            record.setUserId(request.getUserId());
            record.setCourseId(request.getCourseId());
            record.setSessionId(sessionId);
            record.setQuestion(request.getQuestion());
            record.setAnswer(aiAnswer);
            record.setContext(JSON.toJSONString(Map.of(
                "relevantContext", relevantContext,
                "conversationLength", conversationHistory.size()
            )));
            chatRecordMapper.insert(record);
            
            // 7. 缓存对话历史到Redis
            cacheConversation(request.getUserId(), sessionId, 
                request.getQuestion(), aiAnswer);
            
            // 8. 构建响应
            AiChatResponse response = new AiChatResponse();
            response.setSessionId(sessionId);
            response.setAnswer(aiAnswer);
            response.setRecordId(record.getId());
            response.setRelevantPoints(extractRelevantPoints(relevantContext));
            
            return response;
            
        } catch (Exception e) {
            log.error("AI chat error", e);
            throw new RuntimeException("AI答疑服务异常: " + e.getMessage());
        }
    }
    
    /**
     * RAG检索：从知识库检索相关内容
     */
    private String retrieveRelevantKnowledge(Long courseId, String question) {
        // 这里简化实现，实际应该使用向量数据库进行语义检索
        // 可以集成Milvus、Pinecone等向量数据库
        
        log.info("Retrieving relevant knowledge for courseId={}, question={}", 
                courseId, question);
        
        // 方案1：关键词匹配（简单实现）
        // 提取问题中的关键词
        List<String> keywords = extractKeywords(question);
        
        // 从知识点表中检索相关内容
        StringBuilder context = new StringBuilder();
        for (String keyword : keywords) {
            // 这里应该使用更复杂的检索逻辑
            // 实际项目中应该：
            // 1. 将问题转换为向量embeddings
            // 2. 在向量数据库中进行相似度搜索
            // 3. 返回top-k个最相关的知识点
            context.append("相关知识点: ").append(keyword).append("\n");
        }
        
        // 方案2：向量检索（生产环境推荐）
        // List<KnowledgePoint> points = vectorSearch(question, courseId, topK);
        
        return context.toString();
    }
    
    /**
     * 提取关键词（简化实现）
     */
    private List<String> extractKeywords(String text) {
        // 简单的关键词提取，实际应该使用NLP工具
        // 如jieba分词、HanLP等
        List<String> keywords = new ArrayList<>();
        String[] words = text.split("[，。！？\\s]+");
        for (String word : words) {
            if (word.length() >= 2) {
                keywords.add(word);
            }
        }
        return keywords;
    }
    
    /**
     * 构建增强的提示词
     */
    private String buildEnhancedPrompt(String question, String context, 
                                      List<Map<String, String>> history) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是一个专业的在线学习助教，负责回答学生的课程相关问题。\n\n");
        
        if (!context.isEmpty()) {
            prompt.append("【课程相关知识点】\n");
            prompt.append(context);
            prompt.append("\n");
        }
        
        prompt.append("【学生问题】\n");
        prompt.append(question);
        prompt.append("\n\n");
        
        prompt.append("请基于以上知识点回答学生的问题，要求：\n");
        prompt.append("1. 回答要准确、详细、易懂\n");
        prompt.append("2. 如果知识点中有相关内容，优先使用\n");
        prompt.append("3. 可以适当举例说明\n");
        prompt.append("4. 保持友好的教学语气\n");
        
        return prompt.toString();
    }
    
    /**
     * 获取对话历史
     */
    private List<Map<String, String>> getConversationHistory(Long userId, String sessionId) {
        String cacheKey = "chat:history:" + userId + ":" + sessionId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return (List<Map<String, String>>) cached;
        }
        
        // 从数据库加载最近的对话历史
        LambdaQueryWrapper<AiChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatRecord::getUserId, userId)
               .eq(AiChatRecord::getSessionId, sessionId)
               .orderByDesc(AiChatRecord::getCreatedTime)
               .last("LIMIT 10");
        
        List<AiChatRecord> records = chatRecordMapper.selectList(wrapper);
        List<Map<String, String>> history = new ArrayList<>();
        
        for (int i = records.size() - 1; i >= 0; i--) {
            AiChatRecord record = records.get(i);
            history.add(Map.of("role", "user", "content", record.getQuestion()));
            history.add(Map.of("role", "assistant", "content", record.getAnswer()));
        }
        
        return history;
    }
    
    /**
     * 缓存对话到Redis
     */
    private void cacheConversation(Long userId, String sessionId, 
                                   String question, String answer) {
        String cacheKey = "chat:history:" + userId + ":" + sessionId;
        List<Map<String, String>> history = getConversationHistory(userId, sessionId);
        
        history.add(Map.of("role", "user", "content", question));
        history.add(Map.of("role", "assistant", "content", answer));
        
        // 只保留最近20条对话
        if (history.size() > 20) {
            history = history.subList(history.size() - 20, history.size());
        }
        
        redisTemplate.opsForValue().set(cacheKey, history, 2, TimeUnit.HOURS);
    }
    
    /**
     * 提取相关知识点
     */
    private List<String> extractRelevantPoints(String context) {
        List<String> points = new ArrayList<>();
        String[] lines = context.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                points.add(line.trim());
            }
        }
        return points;
    }
    
    @Override
    public Object getChatHistory(Long userId, String sessionId) {
        return getConversationHistory(userId, sessionId);
    }
    
    @Override
    public void rateAnswer(Long recordId, Integer rating) {
        AiChatRecord record = new AiChatRecord();
        record.setId(recordId);
        record.setRating(rating);
        chatRecordMapper.updateById(record);
    }
}
