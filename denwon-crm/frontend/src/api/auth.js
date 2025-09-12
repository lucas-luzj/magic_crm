import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 用户登出
 */
export function logout() {
  return request.post('/auth/logout')
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request.get('/auth/current')
}

/**
 * 刷新Token
 */
export function refreshToken(token) {
  return request.post('/auth/refresh', { refreshToken: token })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request.post('/auth/change-password', data)
}

/**
 * 重置密码
 */
export function resetPassword(data) {
  return request.post('/auth/reset-password', data)
}

/**
 * 发送验证码
 */
export function sendCaptcha(email) {
  return request.post('/auth/send-captcha', { email })
}