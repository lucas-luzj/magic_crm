import { ref, type Ref } from 'vue'

/**
 * 创建一个带loading状态的异步函数执行器
 * @param asyncFn - 要执行的异步函数
 * @param options - 配置选项
 * @returns 包含loading状态和执行函数的对象
 */
export function useLoading<T extends (...args: any[]) => Promise<any>>(
  asyncFn: T,
  options: {
    preventConcurrent?: boolean
    onError?: (error: any) => void
  } = {}
) {
  const loading = ref(false)
  
  const execute = async (...args: Parameters<T>): Promise<ReturnType<T>> => {
    if (loading.value && options.preventConcurrent !== false) {
      return Promise.resolve() as ReturnType<T> // 防止并发执行
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
 * @param loadingRef - 现有的loading响应式引用
 * @returns 执行器函数
 */
export function withLoading(loadingRef: Ref<boolean>) {
  return <T extends (...args: any[]) => Promise<any>>(asyncFn: T) => {
    return async (...args: Parameters<T>): Promise<ReturnType<T>> => {
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
 * @param loadingRefs - loading引用对象
 * @returns 包装后的执行器对象
 */
export function useMultiLoading(loadingRefs: Record<string, Ref<boolean>>) {
  const executors: Record<string, ReturnType<typeof withLoading>> = {}
  
  Object.keys(loadingRefs).forEach(key => {
    executors[key] = withLoading(loadingRefs[key])
  })
  
  return executors
}