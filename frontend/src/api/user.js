import request from '@/utils/request'

// 获取用户列表（分页）
export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, password) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'put',
    data: { password }
  })
}

// 修改密码
export function changePassword(id, oldPassword, newPassword) {
  return request({
    url: `/api/users/${id}/change-password`,
    method: 'put',
    data: { oldPassword, newPassword }
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// 更新当前用户信息
export function updateCurrentUser(data) {
  return request({
    url: '/auth/me',
    method: 'put',
    data
  })
}

// 检查用户名是否存在
export function checkUsername(username) {
  return request({
    url: '/users/check-username',
    method: 'get',
    params: { username }
  })
}

// 检查邮箱是否存在
export function checkEmail(email) {
  return request({
    url: '/users/check-email',
    method: 'get',
    params: { email }
  })
}