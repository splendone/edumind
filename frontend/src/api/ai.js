import request from '@/utils/request'

/**
 * AI智能答疑API
 */

// AI聊天
export const aiChat = (data) => {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}

// 获取对话历史
export const getChatHistory = (params) => {
  return request({
    url: '/ai/chat/history',
    method: 'get',
    params
  })
}

// 评价AI回答
export const rateAnswer = (data) => {
  return request({
    url: '/ai/chat/rate',
    method: 'post',
    data
  })
}
