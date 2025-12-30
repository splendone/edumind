# API接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token（部分接口需要）
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```

## 响应格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1640000000000
}
```

## 1. AI智能答疑模块

### 1.1 AI聊天

**接口**: `POST /ai/chat`

**描述**: 基于课程内容的智能问答（RAG检索增强）

**请求参数**:
```json
{
  "userId": 1,
  "courseId": 1,
  "sessionId": "session_xxx",
  "question": "什么是Java多态？"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "sessionId": "session_xxx",
    "answer": "多态是面向对象编程的三大特性之一...",
    "recordId": 123,
    "relevantPoints": [
      "面向对象三大特性",
      "多态的实现方式"
    ]
  }
}
```

### 1.2 获取对话历史

**接口**: `GET /ai/chat/history`

**参数**:
- userId: 用户ID
- sessionId: 会话ID

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "role": "user",
      "content": "什么是Java多态？"
    },
    {
      "role": "assistant",
      "content": "多态是..."
    }
  ]
}
```

### 1.3 评价AI回答

**接口**: `POST /ai/chat/rate`

**参数**:
- recordId: 对话记录ID
- rating: 评分（1-5）

## 2. 学习跟踪模块

### 2.1 更新学习进度

**接口**: `POST /learning/progress`

**请求**:
```json
{
  "userId": 1,
  "courseId": 1,
  "videoId": 1,
  "learnTime": 300,
  "progress": 85.5,
  "lastPosition": 1200
}
```

### 2.2 获取学习进度

**接口**: `GET /learning/progress`

**参数**:
- userId: 用户ID
- courseId: 课程ID

**响应**:
```json
{
  "code": 200,
  "data": {
    "totalVideos": 20,
    "completedVideos": 15,
    "avgProgress": 75.5,
    "totalLearnTime": 3600,
    "records": []
  }
}
```

### 2.3 获取学习统计

**接口**: `GET /learning/stats`

**参数**:
- userId: 用户ID

**响应**:
```json
{
  "code": 200,
  "data": {
    "courseCount": 5,
    "completedCount": 2,
    "avgProgress": 65.8,
    "totalLearnTime": 7200
  }
}
```

### 2.4 生成学习报告

**接口**: `GET /learning/report`

**参数**:
- userId: 用户ID
- period: 统计周期（week/month）

**响应**:
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "period": "week",
    "generateTime": "2024-01-15 10:30:00",
    "totalCourses": 5,
    "completedCourses": 2,
    "totalLearnTime": 7200,
    "avgProgress": 65.8,
    "recentCourses": [],
    "learningTrend": {
      "本周": 120,
      "上周": 90
    },
    "knowledgeMastery": [
      {
        "name": "Java基础",
        "mastery": 85.5
      }
    ]
  }
}
```

### 2.5 知识点掌握度分析

**接口**: `GET /learning/knowledge-mastery`

**参数**:
- userId: 用户ID
- courseId: 课程ID

**响应**:
```json
{
  "code": 200,
  "data": {
    "learningProgress": {},
    "knowledgePoints": [
      {
        "name": "变量与数据类型",
        "masteryLevel": "精通",
        "score": 95,
        "practiceCount": 20
      }
    ],
    "overallMastery": 86.5
  }
}
```

## 3. 课程管理模块

### 3.1 课程列表

**接口**: `GET /course/list`

**参数**:
- current: 当前页
- size: 每页数量
- keyword: 搜索关键词（可选）
- categoryId: 分类ID（可选）

**响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Java程序设计基础",
        "subtitle": "从零开始学Java",
        "coverImage": "http://...",
        "teacherId": 2,
        "level": "BEGINNER",
        "price": 199.00,
        "studentCount": 1234,
        "rating": 4.8
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 3.2 课程详情

**接口**: `GET /course/{id}`

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "Java程序设计基础",
    "description": "课程详细介绍...",
    "chapters": [
      {
        "id": 1,
        "title": "第一章",
        "videos": []
      }
    ]
  }
}
```

### 3.3 创建课程

**接口**: `POST /course`

**请求** (需要教师/管理员权限):
```json
{
  "title": "新课程",
  "subtitle": "副标题",
  "description": "课程描述",
  "categoryId": 1,
  "level": "BEGINNER",
  "price": 199.00
}
```

### 3.4 更新课程

**接口**: `PUT /course/{id}`

**请求** (需要教师/管理员权限):
```json
{
  "title": "更新后的标题",
  "description": "更新后的描述"
}
```

### 3.5 删除课程

**接口**: `DELETE /course/{id}`

### 3.6 推荐课程

**接口**: `GET /course/recommended`

**参数**:
- userId: 用户ID
- limit: 返回数量

## 4. 在线测试模块

### 4.1 获取题库列表

**接口**: `GET /question/list`

**参数**:
- courseId: 课程ID
- type: 题目类型（可选）
- difficulty: 难度（可选）

### 4.2 创建题目

**接口**: `POST /question`

**请求**:
```json
{
  "courseId": 1,
  "questionType": "SINGLE",
  "questionContent": "以下哪个是Java关键字？",
  "options": "[\"class\", \"Class\", \"CLASS\", \"clazz\"]",
  "correctAnswer": "class",
  "analysis": "class是Java的关键字...",
  "difficulty": "EASY"
}
```

### 4.3 获取试卷

**接口**: `GET /exam/{id}`

### 4.4 提交答案

**接口**: `POST /exam/submit`

**请求**:
```json
{
  "recordId": 1,
  "answers": [
    {
      "questionId": 1,
      "userAnswer": "class"
    }
  ]
}
```

### 4.5 获取考试记录

**接口**: `GET /exam/record`

**参数**:
- userId: 用户ID
- paperId: 试卷ID（可选）

### 4.6 错题本

**接口**: `GET /exam/wrong-questions`

**参数**:
- userId: 用户ID
- courseId: 课程ID（可选）

## 5. 用户管理模块

### 5.1 用户注册

**接口**: `POST /user/register`

**请求**:
```json
{
  "username": "student1",
  "password": "password123",
  "email": "student@example.com",
  "nickname": "学生A"
}
```

### 5.2 用户登录

**接口**: `POST /user/login`

**请求**:
```json
{
  "username": "student1",
  "password": "password123"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "student1",
      "nickname": "学生A",
      "role": "STUDENT"
    }
  }
}
```

### 5.3 获取用户信息

**接口**: `GET /user/info`

### 5.4 更新用户信息

**接口**: `PUT /user/info`

**请求**:
```json
{
  "nickname": "新昵称",
  "avatar": "http://...",
  "email": "new@example.com"
}
```

## 6. 社区交流模块

### 6.1 发帖

**接口**: `POST /discussion`

**请求**:
```json
{
  "courseId": 1,
  "title": "讨论主题",
  "content": "讨论内容"
}
```

### 6.2 回复

**接口**: `POST /discussion/reply`

**请求**:
```json
{
  "discussionId": 1,
  "content": "回复内容",
  "parentId": 0
}
```

### 6.3 学习笔记

**接口**: `POST /note`

**请求**:
```json
{
  "courseId": 1,
  "videoId": 1,
  "title": "笔记标题",
  "content": "笔记内容",
  "videoTime": 120,
  "isPublic": 1
}
```

## 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 限流说明

为保证服务稳定，部分接口实施限流：
- AI聊天接口：每分钟最多10次
- 普通接口：每分钟最多60次

## 注意事项

1. 所有时间字段使用Unix时间戳（毫秒）
2. 文件上传大小限制为100MB
3. Token有效期为7天
4. 分页接口默认每页10条记录

## 示例代码

### JavaScript (Axios)

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
})

// AI聊天
const chatWithAI = async (question) => {
  const res = await api.post('/ai/chat', {
    userId: 1,
    courseId: 1,
    question: question
  })
  return res.data
}
```

### Java (OkHttp)

```java
OkHttpClient client = new OkHttpClient();

RequestBody body = RequestBody.create(
    MediaType.parse("application/json"),
    "{\"userId\":1,\"question\":\"什么是多态？\"}"
);

Request request = new Request.Builder()
    .url("http://localhost:8080/api/ai/chat")
    .header("Authorization", "Bearer " + token)
    .post(body)
    .build();

Response response = client.newCall(request).execute();
```

## 更新日志

- v1.0.0 (2024-01-15): 初始版本发布

---

如有问题，请查阅完整文档或提交Issue。
