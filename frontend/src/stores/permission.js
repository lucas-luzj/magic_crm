import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserMenus } from '@/api/auth'

export const usePermissionStore = defineStore('permission', () => {
  const userMenus = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取用户菜单
  const fetchUserMenus = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await getUserMenus()
      if (response.code === 200) {
        userMenus.value = response.data || []
      } else {
        throw new Error(response.message || '获取菜单失败')
      }
    } catch (err) {
      error.value = err.message || '获取菜单失败'
      console.error('获取用户菜单失败:', err)
    } finally {
      loading.value = false
    }
  }

  // 检查用户是否有权限访问某个路由
  const hasPermission = (routeName) => {
    if (!userMenus.value.length) return false
    
    // 递归检查菜单权限
    const checkMenuPermission = (menus) => {
      for (const menu of menus) {
        if (menu.path === routeName || menu.name === routeName) {
          return true
        }
        if (menu.children && menu.children.length) {
          if (checkMenuPermission(menu.children)) {
            return true
          }
        }
      }
      return false
    }
    
    return checkMenuPermission(userMenus.value)
  }

  // 重置权限状态
  const reset = () => {
    userMenus.value = []
    loading.value = false
    error.value = null
  }

  return {
    userMenus,
    loading,
    error,
    fetchUserMenus,
    hasPermission,
    reset
  }
})