import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    
    // 添加token
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    
    // 处理POST请求的参数
    if (config.method === 'post' && config.data) {
      // 如果是FormData，不处理
      if (config.data instanceof FormData) {
        config.headers['Content-Type'] = 'multipart/form-data'
      }
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  
  failedQueue = []
}

service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的是文件流
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 正常响应
    if (res.success || res.code === 200) {
      return res
    }
    
    // 业务错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  async error => {
    const { response, config } = error
    const authStore = useAuthStore()
    
    if (response) {
      switch (response.status) {
        case 401:
          // Token过期，尝试刷新
          if (!isRefreshing) {
            isRefreshing = true
            
            try {
              const success = await authStore.handleRefreshToken()
              if (success) {
                isRefreshing = false
                processQueue(null, authStore.token)
                // 重试原请求
                config.headers['Authorization'] = `Bearer ${authStore.token}`
                return service(config)
              } else {
                throw new Error('Token刷新失败')
              }
            } catch (refreshError) {
              isRefreshing = false
              processQueue(refreshError, null)
              
              ElMessageBox.alert('登录状态已过期，请重新登录', '提示', {
                confirmButtonText: '重新登录',
                type: 'warning',
                callback: () => {
                  authStore.handleLogout()
                }
              })
              
              return Promise.reject(refreshError)
            }
          } else {
            // 正在刷新token，将请求加入队列
            return new Promise((resolve, reject) => {
              failedQueue.push({ resolve, reject })
            }).then(token => {
              config.headers['Authorization'] = `Bearer ${token}`
              return service(config)
            })
          }
          break
          
        case 403:
          ElMessage.error('没有权限访问该资源')
          router.push('/403')
          break
          
        case 404:
          ElMessage.error('请求的资源不存在')
          break
          
        case 500:
          ElMessage.error('服务器内部错误')
          break
          
        default:
          ElMessage.error(response.data?.message || `请求失败: ${response.status}`)
      }
    } else {
      // 网络错误
      ElMessage.error('网络连接失败，请检查网络设置')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export default {
  // GET请求
  get(url, params = {}) {
    return service({
      method: 'get',
      url,
      params
    })
  },
  
  // POST请求
  post(url, data = {}) {
    return service({
      method: 'post',
      url,
      data
    })
  },
  
  // PUT请求
  put(url, data = {}) {
    return service({
      method: 'put',
      url,
      data
    })
  },
  
  // DELETE请求
  delete(url, params = {}) {
    return service({
      method: 'delete',
      url,
      params
    })
  },
  
  // 上传文件
  upload(url, file, data = {}) {
    const formData = new FormData()
    formData.append('file', file)
    
    // 添加其他数据
    Object.keys(data).forEach(key => {
      formData.append(key, data[key])
    })
    
    return service({
      method: 'post',
      url,
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 下载文件
  download(url, params = {}, filename) {
    return service({
      method: 'get',
      url,
      params,
      responseType: 'blob'
    }).then(response => {
      const blob = new Blob([response.data])
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = filename || 'download'
      link.click()
      window.URL.revokeObjectURL(link.href)
    })
  }
}

export { service }