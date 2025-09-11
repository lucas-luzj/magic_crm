import { ref } from 'vue'

/**
 * 创建一个带loading状态的异步函数执行器
 * @param {Function} asyncFn - 要执行的异步函数
 * @param {Object} options - 配置选项
 * @param {boolean} options.preventConcurrent - 是否防止并发执行
 * @param {Function} options.onError - 错误处理函数
 * @returns {Object} 包含loading状态和执行函数的对象
 */
export function useLoading(asyncFn, options = {}) {
  const loading = ref(false)
  
  const execute = async (...args) => {
    if (loading.value && options.preventConcurrent !== false) {
      return Promise.resolve() // 防止并发执行
    }
    
    loading.value = true
    try {
      const result = await asyncFn(...args)
      return result
    } catch (error) {
      if (options.onError) {
        options.onError(error)
      } else {
        console.error('异步操作失败:', error)
      }
      throw error
    } finally {
      loading.value = false
    }
  }
  
  return {
    loading,
    execute
  }
}

/**
 * 为现有的loading ref创建执行器
 * @param {Ref} loadingRef - 现有的loading响应式引用
 * @returns {Function} 执行器函数
 */
export function withLoading(loadingRef) {
  return (asyncFn) => {
    return async (...args) => {
      loadingRef.value = true
      try {
        const result = await asyncFn(...args)
        return result
      } catch (error) {
        console.error('异步操作失败:', error)
        throw error
      } finally {
        loadingRef.value = false
      }
    }
  }
}

/**
 * 批量处理多个异步操作的loading状态
 * @param {Object} loadingRefs - loading引用对象
 * @returns {Object} 包装后的执行器对象
 */
export function useMultiLoading(loadingRefs) {
  const executors = {}
  
  Object.keys(loadingRefs).forEach(key => {
    executors[key] = withLoading(loadingRefs[key])
  })
  
  return executors
}