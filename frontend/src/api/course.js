import request from '@/utils/request'

/**
 * 课程管理API
 */

// 获取课程列表
export const getCourseList = (params) => {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

// 获取课程详情
export const getCourseDetail = (id) => {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

// 创建课程
export const createCourse = (data) => {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

// 更新课程
export const updateCourse = (id, data) => {
  return request({
    url: `/course/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export const deleteCourse = (id) => {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

// 获取推荐课程
export const getRecommendedCourses = (params) => {
  return request({
    url: '/course/recommended',
    method: 'get',
    params
  })
}
