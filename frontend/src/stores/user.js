import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { getToken, setToken as setStorageToken, removeToken } from '@/utils/auth'
import { login as loginApi } from '@/api/auth'
import { getUserPermissions, getUserMenus } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = reactive({
    id: '',
    name: '',
    email: '',
    phone: '',
    avatar: '',
    role: '',
    status: 'active',
    createTime: '',
    lastLoginTime: ''
  })

  // 用户权限列表
  const userPermissions = ref([])

  // 用户菜单列表
  const userMenus = ref([])

  // 登录状态
  const isLoggedIn = ref(false)
  const token = ref(getToken() || '')

  // 设置用户信息
  const setUserInfo = (info) => {
    Object.assign(userInfo, info)
    isLoggedIn.value = true
  }

  // 设置用户权限
  const setUserPermissions = (permissions) => {
    userPermissions.value = permissions
  }

  // 设置用户菜单
  const setUserMenus = (menus) => {
    userMenus.value = menus
  }

  // 更新用户信息
  const updateUserInfo = (info) => {
    Object.assign(userInfo, info)
  }

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    setStorageToken(newToken)
    isLoggedIn.value = true
  }

  // 清除用户信息
  const clearUserInfo = () => {
    Object.keys(userInfo).forEach(key => {
      userInfo[key] = ''
    })
    userInfo.status = 'active'
    isLoggedIn.value = false
    token.value = ''
    userPermissions.value = []
    userMenus.value = []
    removeToken()
  }

  // 用户登录
  const login = async (loginForm) => {
    // 使用JWT登录
    const { token, user } = await loginApi({
      username: loginForm.username,
      password: loginForm.password
    })

    // 登录成功后设置token
    setToken(token)
    // 设置用户信息
    setUserInfo(user)

    // 获取用户权限和菜单
    await loadUserPermissions()
    await loadUserMenus()

    return { token, user }
  }

  // 加载用户权限
  const loadUserPermissions = async () => {
    try {
      const response = await getUserPermissions()
      setUserPermissions(response.data)
    } catch (error) {
      console.error('加载用户权限失败:', error)
    }
  }

  // 加载用户菜单
  const loadUserMenus = async () => {
    try {
      const response = await getUserMenus()
      setUserMenus(response.data)
    } catch (error) {
      console.error('加载用户菜单失败:', error)
    }
  }

  // 登出
  const logout = () => {
    clearUserInfo()
  }

  return {
    userInfo,
    isLoggedIn,
    token,
    setUserInfo,
    updateUserInfo,
    setToken,
    clearUserInfo,
    login,
    logout,
    userPermissions,
    userMenus,
    setUserPermissions,
    setUserMenus,
    loadUserPermissions,
    loadUserMenus
  }
})