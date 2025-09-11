/**
 * 日期时间工具函数
 */

/**
 * 格式化日期时间
 * @param {Date|string|number} date - 日期
 * @param {string} format - 格式字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期（只显示日期部分）
 * @param {Date|string|number} date - 日期
 * @returns {string} 格式化后的日期字符串
 */
export function formatDateOnly(date) {
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间（只显示时间部分）
 * @param {Date|string|number} date - 日期
 * @returns {string} 格式化后的时间字符串
 */
export function formatTimeOnly(date) {
  return formatDate(date, 'HH:mm:ss')
}

/**
 * 相对时间格式化
 * @param {Date|string|number} date - 日期
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(date) {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

/**
 * 获取日期范围
 * @param {string} type - 类型：'today', 'yesterday', 'week', 'month', 'year'
 * @returns {Object} 包含开始和结束日期的对象
 */
export function getDateRange(type) {
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  
  switch (type) {
    case 'today':
      return {
        start: new Date(today),
        end: new Date(today.getTime() + 24 * 60 * 60 * 1000 - 1)
      }
    case 'yesterday':
      const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
      return {
        start: yesterday,
        end: new Date(yesterday.getTime() + 24 * 60 * 60 * 1000 - 1)
      }
    case 'week':
      const startOfWeek = new Date(today)
      startOfWeek.setDate(today.getDate() - today.getDay())
      const endOfWeek = new Date(startOfWeek)
      endOfWeek.setDate(startOfWeek.getDate() + 6)
      endOfWeek.setHours(23, 59, 59, 999)
      return {
        start: startOfWeek,
        end: endOfWeek
      }
    case 'month':
      const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      endOfMonth.setHours(23, 59, 59, 999)
      return {
        start: startOfMonth,
        end: endOfMonth
      }
    case 'year':
      const startOfYear = new Date(now.getFullYear(), 0, 1)
      const endOfYear = new Date(now.getFullYear(), 11, 31)
      endOfYear.setHours(23, 59, 59, 999)
      return {
        start: startOfYear,
        end: endOfYear
      }
    default:
      return {
        start: today,
        end: today
      }
  }
}

/**
 * 判断是否为今天
 * @param {Date|string|number} date - 日期
 * @returns {boolean} 是否为今天
 */
export function isToday(date) {
  if (!date) return false
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return false
  
  const today = new Date()
  return d.getFullYear() === today.getFullYear() &&
         d.getMonth() === today.getMonth() &&
         d.getDate() === today.getDate()
}

/**
 * 判断是否为昨天
 * @param {Date|string|number} date - 日期
 * @returns {boolean} 是否为昨天
 */
export function isYesterday(date) {
  if (!date) return false
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return false
  
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  
  return d.getFullYear() === yesterday.getFullYear() &&
         d.getMonth() === yesterday.getMonth() &&
         d.getDate() === yesterday.getDate()
}

/**
 * 获取两个日期之间的天数差
 * @param {Date|string|number} date1 - 日期1
 * @param {Date|string|number} date2 - 日期2
 * @returns {number} 天数差
 */
export function getDaysDiff(date1, date2) {
  const d1 = new Date(date1)
  const d2 = new Date(date2)
  
  if (isNaN(d1.getTime()) || isNaN(d2.getTime())) return 0
  
  const diff = Math.abs(d1.getTime() - d2.getTime())
  return Math.floor(diff / (24 * 60 * 60 * 1000))
}

/**
 * 添加天数
 * @param {Date|string|number} date - 日期
 * @param {number} days - 要添加的天数
 * @returns {Date} 新日期
 */
export function addDays(date, days) {
  const d = new Date(date)
  d.setDate(d.getDate() + days)
  return d
}

/**
 * 添加月份
 * @param {Date|string|number} date - 日期
 * @param {number} months - 要添加的月数
 * @returns {Date} 新日期
 */
export function addMonths(date, months) {
  const d = new Date(date)
  d.setMonth(d.getMonth() + months)
  return d
}

/**
 * 添加年份
 * @param {Date|string|number} date - 日期
 * @param {number} years - 要添加的年数
 * @returns {Date} 新日期
 */
export function addYears(date, years) {
  const d = new Date(date)
  d.setFullYear(d.getFullYear() + years)
  return d
}

/**
 * 获取月份的第一天
 * @param {Date|string|number} date - 日期
 * @returns {Date} 月份第一天
 */
export function getFirstDayOfMonth(date) {
  const d = new Date(date)
  return new Date(d.getFullYear(), d.getMonth(), 1)
}

/**
 * 获取月份的最后一天
 * @param {Date|string|number} date - 日期
 * @returns {Date} 月份最后一天
 */
export function getLastDayOfMonth(date) {
  const d = new Date(date)
  return new Date(d.getFullYear(), d.getMonth() + 1, 0)
}

/**
 * 获取年份的第一天
 * @param {Date|string|number} date - 日期
 * @returns {Date} 年份第一天
 */
export function getFirstDayOfYear(date) {
  const d = new Date(date)
  return new Date(d.getFullYear(), 0, 1)
}

/**
 * 获取年份的最后一天
 * @param {Date|string|number} date - 日期
 * @returns {Date} 年份最后一天
 */
export function getLastDayOfYear(date) {
  const d = new Date(date)
  return new Date(d.getFullYear(), 11, 31)
}
