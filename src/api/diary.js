import { request } from './request'

/**
 * 日记相关API接口
 */

// 创建日记
export const createDiary = (data) => {
  return request({
    url: '/diary/create',
    method: 'POST',
    data
  })
}

// 获取日记列表
export const getDiaryList = (params) => {
  return request({
    url: '/diary/list',
    method: 'GET',
    params
  })
}

// 获取日记详情
export const getDiaryDetail = (id) => {
  return request({
    url: `/diary/${id}`,
    method: 'GET'
  })
}

// 更新日记
export const updateDiary = (id, data) => {
  return request({
    url: `/diary/${id}`,
    method: 'PUT',
    data
  })
}

// 删除日记
export const deleteDiary = (id) => {
  return request({
    url: `/diary/${id}`,
    method: 'DELETE'
  })
}

// 转发日记到论坛
export const forwardDiaryToForum = (id, data) => {
  return request({
    url: `/diary/${id}/forward`,
    method: 'POST',
    data
  })
}

// 获取日记统计信息
export const getDiaryStats = () => {
  return request({
    url: '/diary/stats',
    method: 'GET'
  })
}

// 日记搜索
export const searchDiary = (params) => {
  return request({
    url: '/diary/search',
    method: 'GET',
    params
  })
} 