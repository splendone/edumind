<template>
  <div class="dashboard-page">
    <h1 class="page-title">学习中心</h1>

    <!-- 学习统计概览 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">学习课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completedCourses }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatTime(stats.totalLearnTime) }}</div>
              <div class="stat-label">学习时长</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.avgProgress }}%</div>
              <div class="stat-label">平均进度</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习报告 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>学习趋势</span>
              <el-radio-group v-model="period" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" style="height: 300px"></div>
        </el-card>

        <!-- 我的课程 -->
        <el-card class="courses-card">
          <template #header>
            <span>我的课程</span>
          </template>
          <el-table :data="myCourses" style="width: 100%">
            <el-table-column prop="title" label="课程名称" />
            <el-table-column label="进度" width="200">
              <template #default="{ row }">
                <el-progress :percentage="row.progress" />
              </template>
            </el-table-column>
            <el-table-column label="学习时长" width="120">
              <template #default="{ row }">
                {{ formatTime(row.learnTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="continueLearning(row.id)">
                  继续学习
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <!-- 知识点掌握度 -->
        <el-card class="knowledge-card">
          <template #header>
            <span>知识点掌握度</span>
          </template>
          <div class="knowledge-list">
            <div
              v-for="item in knowledgeMastery"
              :key="item.name"
              class="knowledge-item"
            >
              <div class="knowledge-name">{{ item.name }}</div>
              <el-progress
                :percentage="item.mastery"
                :color="getProgressColor(item.mastery)"
              />
            </div>
          </div>
        </el-card>

        <!-- 最近成就 -->
        <el-card class="achievement-card">
          <template #header>
            <span>最近成就</span>
          </template>
          <div class="achievement-list">
            <div v-for="achievement in achievements" :key="achievement.id" class="achievement-item">
              <el-icon :size="32" :color="achievement.color">
                <Medal />
              </el-icon>
              <div class="achievement-info">
                <div class="achievement-name">{{ achievement.name }}</div>
                <div class="achievement-time">{{ achievement.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStats, generateReport } from '@/api/learning'
import * as echarts from 'echarts'

const router = useRouter()

const stats = ref({
  totalCourses: 0,
  completedCourses: 0,
  totalLearnTime: 0,
  avgProgress: 0
})

const period = ref('week')
const chartRef = ref(null)
const myCourses = ref([])
const knowledgeMastery = ref([])
const achievements = ref([
  {
    id: 1,
    name: '连续学习7天',
    time: '2024-01-15',
    color: '#409eff'
  },
  {
    id: 2,
    name: '完成第一门课程',
    time: '2024-01-10',
    color: '#67c23a'
  }
])

onMounted(async () => {
  await loadStats()
  await loadReport()
  initChart()
})

const loadStats = async () => {
  try {
    const res = await getStats({ userId: 1 })
    stats.value = {
      totalCourses: res.data.courseCount || 0,
      completedCourses: res.data.completedCount || 0,
      totalLearnTime: res.data.totalLearnTime || 0,
      avgProgress: Math.round(res.data.avgProgress || 0)
    }
  } catch (error) {
    console.error('Failed to load stats', error)
  }
}

const loadReport = async () => {
  try {
    const res = await generateReport({ userId: 1, period: period.value })
    myCourses.value = res.data.recentCourses || []
    knowledgeMastery.value = res.data.knowledgeMastery || []
  } catch (error) {
    console.error('Failed to load report', error)
  }
}

const initChart = () => {
  if (!chartRef.value) return

  const chart = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '学习时长(分钟)'
    },
    series: [
      {
        data: [120, 90, 150, 80, 100, 110, 95],
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
}

const formatTime = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const getProgressColor = (progress) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  return '#f56c6c'
}

const continueLearning = (courseId) => {
  router.push(`/learning/${courseId}`)
}
</script>

<style scoped>
.dashboard-page {
  padding: 20px;
}

.page-title {
  margin: 0 0 24px;
  font-size: 28px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.chart-card,
.courses-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.knowledge-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.knowledge-name {
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.achievement-card {
  margin-top: 20px;
}

.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.achievement-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.achievement-time {
  font-size: 12px;
  color: #999;
}
</style>
