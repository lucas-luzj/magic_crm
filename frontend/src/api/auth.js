import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/auth/user-info',
    method: 'get'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/change-password',
    method: 'post',
    data
  })
}

// 获取用户权限列表
export function getUserPermissions() {
  return request({
    url: '/auth/user-permissions',
    method: 'get'
  })
}

// 获取用户菜单列表
export function getUserMenus() {
  return request({
    url: '/auth/user-menus',
    method: 'get'
  })
}