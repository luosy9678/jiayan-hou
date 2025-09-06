import axios from 'axios'
import { showToast } from 'vant'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8081/api/v1', // 统一使用8081端口
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const { data } = response
    
    // 统一处理响应
    if (data.code === 200) {
      return data
    } else {
      // 业务错误处理
      showToast({
        message: data.message || '请求失败',
        type: 'fail'
      })
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理HTTP状态码错误
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          showToast({
            message: '登录已过期，请重新登录',
            type: 'fail'
          })
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          // window.location.href = '/login'
          break
        case 403:
          showToast({
            message: '没有权限访问',
            type: 'fail'
          })
          break
        case 404:
          showToast({
            message: '请求的资源不存在',
            type: 'fail'
          })
          break
        case 500:
          showToast({
            message: '服务器内部错误',
            type: 'fail'
          })
          break
        default:
          showToast({
            message: data?.message || '请求失败',
            type: 'fail'
          })
      }
    } else if (error.request) {
      showToast({
        message: '网络连接失败，请检查网络',
        type: 'fail'
      })
    } else {
      showToast({
        message: error.message || '请求失败',
        type: 'fail'
      })
    }
    
    return Promise.reject(error)
  }
)

// 通用请求方法
export const request = (config) => {
  return service(config)
}

// 导出axios实例
export default service 