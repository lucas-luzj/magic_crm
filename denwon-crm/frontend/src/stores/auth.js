import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUser, refreshToken } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const refreshTokenValue = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(null)
  const permissions = ref([])
  const roles = ref([])
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.name || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  
  // 登录
  async function handleLogin(loginForm) {
    try {
      const res = await login(loginForm)
      if (res.success) {
        const { accessToken, refreshToken: refresh, userInfo: user } = res.data
        
        // 保存token
        token.value = accessToken
        refreshTokenValue.value = refresh
        localStorage.setItem('token', accessToken)
        localStorage.setItem('refreshToken', refresh)
        
        // 保存用户信息
        userInfo.value = user
        permissions.value = user.permissions || []
        roles.value = user.roles || []
        
        ElMessage.success('登录成功')
        
        // 跳转到首页或重定向页面
        const redirect = router.currentRoute.value.query.redirect || '/'
        router.push(redirect)
        
        return true
      } else {
        ElMessage.error(res.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败: ' + error.message)
      return false
    }
  }
  
  // 登出
  async function handleLogout() {
    try {
      await logout()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除本地数据
      token.value = ''
      refreshTokenValue.value = ''
      userInfo.value = null
      permissions.value = []
      roles.value = []
      
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      
      // 跳转到登录页
      router.push('/login')
      ElMessage.success('已退出登录')
    }
  }
  
  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    
    try {
      const res = await getCurrentUser()
      if (res.success) {
        userInfo.value = res.data
        permissions.value = res.data.permissions || []
        roles.value = res.data.roles || []
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  // 刷新token
  async function handleRefreshToken() {
    if (!refreshTokenValue.value) return false
    
    try {
      const res = await refreshToken(refreshTokenValue.value)
      if (res.success) {
        const { accessToken, refreshToken: refresh } = res.data
        
        token.value = accessToken
        refreshTokenValue.value = refresh
        localStorage.setItem('token', accessToken)
        localStorage.setItem('refreshToken', refresh)
        
        return true
      }
    } catch (error) {
      console.error('刷新token失败:', error)
    }
    
    return false
  }
  
  // 检查权限
  function hasPermission(permission) {
    if (!permission) return true
    return permissions.value.includes(permission)
  }
  
  // 检查角色
  function hasRole(role) {
    if (!role) return true
    return roles.value.includes(role)
  }
  
  // 检查多个权限（OR）
  function hasAnyPermission(...perms) {
    return perms.some(p => hasPermission(p))
  }
  
  // 检查多个权限（AND）
  function hasAllPermissions(...perms) {
    return perms.every(p => hasPermission(p))
  }
  
  return {
    // 状态
    token,
    userInfo,
    permissions,
    roles,
    
    // 计算属性
    isLoggedIn,
    userName,
    avatar,
    
    // 方法
    handleLogin,
    handleLogout,
    fetchUserInfo,
    handleRefreshToken,
    hasPermission,
    hasRole,
    hasAnyPermission,
    hasAllPermissions
  }
})