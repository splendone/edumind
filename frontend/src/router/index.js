import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: '/course/list',
        name: 'CourseList',
        component: () => import('@/views/course/CourseList.vue')
      },
      {
        path: '/course/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/CourseDetail.vue')
      },
      {
        path: '/learning/:courseId',
        name: 'Learning',
        component: () => import('@/views/learning/LearningPage.vue')
      },
      {
        path: '/test/list',
        name: 'TestList',
        component: () => import('@/views/test/TestList.vue')
      },
      {
        path: '/test/:id',
        name: 'TestPage',
        component: () => import('@/views/test/TestPage.vue')
      },
      {
        path: '/community',
        name: 'Community',
        component: () => import('@/views/community/Community.vue')
      },
      {
        path: '/user/dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/user/Dashboard.vue')
      },
      {
        path: '/ai-chat',
        name: 'AiChat',
        component: () => import('@/views/AiChat.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
