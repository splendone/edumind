<template>
  <div class="home-page">
    <!-- Banner -->
    <div class="banner">
      <div class="banner-content">
        <h1>AI增强型在线学习平台</h1>
        <p class="subtitle">智能推荐 · 个性化学习 · AI答疑 · 学习跟踪</p>
        <el-button type="primary" size="large" @click="exploreCourses">
          开始学习
        </el-button>
      </div>
    </div>

    <!-- 特色功能 -->
    <div class="features-section">
      <h2 class="section-title">平台特色</h2>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="feature in features" :key="feature.title">
          <el-card class="feature-card" shadow="hover">
            <div class="feature-icon">
              <el-icon :size="48" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 推荐课程 -->
    <div class="courses-section">
      <div class="section-header">
        <h2 class="section-title">推荐课程</h2>
        <el-button text @click="viewAllCourses">查看全部</el-button>
      </div>
      <el-row :gutter="20">
        <el-col
          :xs="24" :sm="12" :md="8" :lg="6"
          v-for="course in recommendedCourses"
          :key="course.id"
        >
          <el-card class="course-card" shadow="hover" @click="viewCourse(course.id)">
            <div class="course-cover">
              <el-image :src="course.cover" fit="cover" />
              <div class="course-level">{{ course.level }}</div>
            </div>
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-teacher">{{ course.teacher }}</p>
              <div class="course-meta">
                <span><el-icon><User /></el-icon> {{ course.students }}</span>
                <span><el-icon><Star /></el-icon> {{ course.rating }}</span>
              </div>
              <div class="course-footer">
                <span class="price">¥{{ course.price }}</span>
                <el-button size="small" type="primary">立即学习</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 学习数据统计 -->
    <div class="stats-section">
      <h2 class="section-title">平台数据</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.label">
          <div class="stat-card">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, TrendCharts, Document, Medal } from '@element-plus/icons-vue'

const router = useRouter()

const features = ref([
  {
    icon: 'ChatDotRound',
    color: '#409eff',
    title: 'AI智能答疑',
    description: '基于RAG技术的智能问答，7×24小时为你解答疑问'
  },
  {
    icon: 'TrendCharts',
    color: '#67c23a',
    title: '学习跟踪',
    description: '实时记录学习进度，可视化展示学习轨迹'
  },
  {
    icon: 'Document',
    color: '#e6a23c',
    title: '智能组卷',
    description: 'AI自动组卷，个性化测试，精准评估学习效果'
  },
  {
    icon: 'Medal',
    color: '#f56c6c',
    title: '个性化推荐',
    description: '基于学习行为的智能课程推荐系统'
  }
])

const recommendedCourses = ref([
  {
    id: 1,
    title: 'Java程序设计基础',
    cover: 'https://via.placeholder.com/300x200',
    teacher: '张教师',
    level: '初级',
    students: '1234',
    rating: '4.8',
    price: '199'
  },
  {
    id: 2,
    title: 'Python数据分析',
    cover: 'https://via.placeholder.com/300x200',
    teacher: '李教师',
    level: '中级',
    students: '856',
    rating: '4.9',
    price: '299'
  },
  {
    id: 3,
    title: 'Web前端开发',
    cover: 'https://via.placeholder.com/300x200',
    teacher: '王教师',
    level: '初级',
    students: '2134',
    rating: '4.7',
    price: '249'
  },
  {
    id: 4,
    title: '机器学习入门',
    cover: 'https://via.placeholder.com/300x200',
    teacher: '赵教师',
    level: '高级',
    students: '567',
    rating: '4.9',
    price: '399'
  }
])

const stats = ref([
  { value: '10,000+', label: '在线学员' },
  { value: '500+', label: '精品课程' },
  { value: '100+', label: '名师团队' },
  { value: '95%', label: '好评率' }
])

const exploreCourses = () => {
  router.push('/course/list')
}

const viewAllCourses = () => {
  router.push('/course/list')
}

const viewCourse = (id) => {
  router.push(`/course/${id}`)
}
</script>

<style scoped>
.home-page {
  margin: -20px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 100px 20px;
  text-align: center;
}

.banner-content h1 {
  font-size: 48px;
  margin: 0 0 20px;
  font-weight: bold;
}

.subtitle {
  font-size: 20px;
  margin: 0 0 40px;
  opacity: 0.9;
}

.features-section,
.courses-section,
.stats-section {
  padding: 60px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-title {
  font-size: 32px;
  text-align: center;
  margin: 0 0 40px;
  color: #333;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.feature-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  margin-bottom: 16px;
}

.feature-card h3 {
  margin: 0 0 12px;
  font-size: 20px;
  color: #333;
}

.feature-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.course-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  position: relative;
  height: 200px;
  margin: -20px -20px 16px;
}

.course-level {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.course-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  margin: 0 0 12px;
  font-size: 14px;
  color: #666;
}

.course-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.course-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.stats-section {
  background: #f5f7fa;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
