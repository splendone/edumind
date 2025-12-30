import request from '@/utils/request'

/**
 * 学习跟踪API
 */

// 更新学习进度
export const updateProgress = (data) => {
  return request({
    url: '/learning/progress',
    method: 'post',
    data
  })
}

// 获取学习进度
export const getProgress = (params) => {
  return request({
    url: '/learning/progress',
    method: 'get',
    params
  })
}

// 获取学习统计
export const getStats = (params) => {
  return request({
    url: '/learning/stats',
    method: 'get',
    params
  })
}

// 生成学习报告
export const generateReport = (params) => {
  return request({
    url: '/learning/report',
    method: 'get',
    params
  })
}

// 获取知识点掌握度
export const getKnowledgeMastery = (params) => {
  return request({
    url: '/learning/knowledge-mastery',
    method: 'get',
    params
  })
}
