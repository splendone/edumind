<template>
  <div class="ai-chat-page">
    <el-card class="chat-container">
      <template #header>
        <div class="chat-header">
          <h2>
            <el-icon><ChatDotRound /></el-icon>
            AI智能答疑
          </h2>
          <p class="description">基于课程内容的智能问答，帮助你快速解决学习疑问</p>
        </div>
      </template>

      <!-- 课程选择 -->
      <div class="course-select">
        <el-select
          v-model="selectedCourse"
          placeholder="选择相关课程（可选）"
          clearable
          style="width: 300px"
        >
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.title"
            :value="course.id"
          />
        </el-select>
      </div>

      <!-- 聊天消息区 -->
      <div class="messages-container" ref="messagesRef">
        <div v-if="messages.length === 0" class="empty-state">
          <el-icon :size="64" color="#909399"><ChatLineRound /></el-icon>
          <p>开始提问吧！AI助教随时为你解答</p>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="message-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="36">
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-avatar v-else :size="36" style="background: #409eff">
              <el-icon><Cpu /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            
            <!-- AI回答的评分 -->
            <div v-if="msg.role === 'assistant' && msg.recordId" class="message-actions">
              <el-rate
                v-model="msg.rating"
                @change="(val) => handleRate(msg.recordId, val)"
                :max="5"
                size="small"
              />
              <span class="rating-text">评价这个回答</span>
            </div>

            <!-- 相关知识点 -->
            <div v-if="msg.relevantPoints && msg.relevantPoints.length > 0" class="relevant-points">
              <p class="points-title">相关知识点：</p>
              <el-tag
                v-for="(point, idx) in msg.relevantPoints"
                :key="idx"
                size="small"
                type="info"
                style="margin-right: 8px; margin-bottom: 8px"
              >
                {{ point }}
              </el-tag>
            </div>
          </div>
        </div>

        <div v-if="loading" class="message assistant loading">
          <div class="message-avatar">
            <el-avatar :size="36" style="background: #409eff">
              <el-icon><Cpu /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入你的问题..."
          @keydown.enter.ctrl="handleSend"
        />
        <div class="input-actions">
          <span class="tip">Ctrl + Enter 发送</span>
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!inputMessage.trim()"
            @click="handleSend"
          >
            <el-icon><Promotion /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChat, rateAnswer } from '@/api/ai'
import { ElMessage } from 'element-plus'

const selectedCourse = ref(null)
const courses = ref([
  { id: 1, title: 'Java程序设计基础' },
  { id: 2, title: 'Python数据分析' },
  { id: 3, title: 'Web前端开发' }
])

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)
const sessionId = ref(generateSessionId())

// 生成会话ID
function generateSessionId() {
  return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 发送消息
const handleSend = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage
  })

  inputMessage.value = ''
  loading.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    // 调用AI接口
    const res = await aiChat({
      userId: 1, // TODO: 从用户状态获取
      courseId: selectedCourse.value,
      sessionId: sessionId.value,
      question: userMessage
    })

    // 添加AI回复
    messages.value.push({
      role: 'assistant',
      content: res.data.answer,
      recordId: res.data.recordId,
      relevantPoints: res.data.relevantPoints,
      rating: 0
    })

    await nextTick()
    scrollToBottom()
  } catch (error) {
    ElMessage.error('AI服务异常，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 评价回答
const handleRate = async (recordId, rating) => {
  try {
    await rateAnswer({ recordId, rating })
    ElMessage.success('感谢你的评价！')
  } catch (error) {
    ElMessage.error('评价失败')
  }
}

// 格式化消息（支持Markdown）
const formatMessage = (text) => {
  // 简单的文本格式化
  return text
    .replace(/\n/g, '<br>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-chat-page {
  max-width: 1000px;
  margin: 0 auto;
}

.chat-container {
  min-height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;
}

.chat-header {
  text-align: center;
}

.chat-header h2 {
  margin: 0;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.description {
  margin: 8px 0 0;
  color: #666;
  font-size: 14px;
}

.course-select {
  margin-bottom: 20px;
}

.messages-container {
  flex: 1;
  min-height: 500px;
  max-height: 600px;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #909399;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message.user .message-text {
  background: #409eff;
  color: white;
}

.message.assistant .message-text {
  background: white;
}

.message-actions {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-text {
  font-size: 12px;
  color: #909399;
}

.relevant-points {
  margin-top: 12px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.points-title {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #409eff;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    opacity: 0.3;
    transform: translateY(0);
  }
  30% {
    opacity: 1;
    transform: translateY(-10px);
  }
}

.input-area {
  position: relative;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.tip {
  font-size: 12px;
  color: #909399;
}
</style>
