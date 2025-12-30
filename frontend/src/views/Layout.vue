<template>
  <el-container class="layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon><Reading /></el-icon>
          <span>AI学习平台</span>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          @select="handleMenuSelect"
          class="nav-menu"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/course/list">课程</el-menu-item>
          <el-menu-item index="/test/list">在线测试</el-menu-item>
          <el-menu-item index="/community">社区</el-menu-item>
          <el-menu-item index="/ai-chat">AI答疑</el-menu-item>
        </el-menu>
        <div class="user-section">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" src="https://via.placeholder.com/32" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToDashboard">学习中心</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>© 2024 AI学习平台 | 智能在线学习系统</p>
        <p>Powered by Spring Boot + Vue 3 + AI</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const username = ref('学生用户')

const activeMenu = computed(() => {
  return route.path
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const goToDashboard = () => {
  router.push('/user/dashboard')
}

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  gap: 8px;
}

.nav-menu {
  flex: 1;
  border: none;
  margin: 0 40px;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.footer {
  background: #f5f7fa;
  height: 60px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  color: #666;
  font-size: 12px;
  line-height: 1.8;
}
</style>
