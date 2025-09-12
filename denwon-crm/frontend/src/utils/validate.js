/**
 * 验证工具函数
 */

/**
 * 判断是否为外部链接
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 验证邮箱
 */
export function isEmail(email) {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(email)
}

/**
 * 验证手机号
 */
export function isMobile(mobile) {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(mobile)
}

/**
 * 验证电话号码
 */
export function isPhone(phone) {
  const reg = /^(\d{3,4}-)?\d{7,8}$/
  return reg.test(phone)
}

/**
 * 验证身份证号
 */
export function isIdCard(idCard) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(idCard)
}

/**
 * 验证URL
 */
export function isUrl(url) {
  const reg = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i
  return reg.test(url)
}

/**
 * 验证密码强度
 * 至少8位，包含大小写字母、数字和特殊字符
 */
export function isStrongPassword(password) {
  const reg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
  return reg.test(password)
}

/**
 * 验证用户名
 * 3-20位字母、数字、下划线
 */
export function isUsername(username) {
  const reg = /^[a-zA-Z0-9_]{3,20}$/
  return reg.test(username)
}

/**
 * 验证金额
 * 最多两位小数
 */
export function isMoney(money) {
  const reg = /^\d+(\.\d{1,2})?$/
  return reg.test(money)
}

/**
 * 验证整数
 */
export function isInteger(num) {
  const reg = /^-?\d+$/
  return reg.test(num)
}

/**
 * 验证正整数
 */
export function isPositiveInteger(num) {
  const reg = /^[1-9]\d*$/
  return reg.test(num)
}

/**
 * 验证百分比
 * 0-100，最多两位小数
 */
export function isPercentage(num) {
  const reg = /^(100|[1-9]?\d)(\.\d{1,2})?$/
  return reg.test(num)
}