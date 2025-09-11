import request from '@/utils/request'

/**
 * 通知设置API
 */
export const notificationSettingsApi = {
  /**
   * 获取用户通知设置
   * @param {number} userId - 用户ID
   * @returns {Promise} API响应
   */
  getUserSettings(userId) {
    return request({
      url: `/notification-settings/user/${userId}`,
      method: 'get'
    })
  },

  /**
   * 更新用户通知设置
   * @param {number} userId - 用户ID
   * @param {Object} settings - 设置数据
   * @returns {Promise} API响应
   */
  updateUserSettings(userId, settings) {
    return request({
      url: `/notification-settings/user/${userId}`,
      method: 'put',
      data: settings
    })
  },

  /**
   * 获取所有通知设置
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getSettings(params = {}) {
    return request({
      url: '/notification-settings',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        userId: params.userId,
        type: params.type,
        channel: params.channel,
        isEnabled: params.isEnabled,
        ...params
      }
    })
  },

  /**
   * 创建通知设置
   * @param {Object} data - 设置数据
   * @returns {Promise} API响应
   */
  createSettings(data) {
    return request({
      url: '/notification-settings',
      method: 'post',
      data
    })
  },

  /**
   * 更新通知设置
   * @param {number} id - 设置ID
   * @param {Object} data - 设置数据
   * @returns {Promise} API响应
   */
  updateSettings(id, data) {
    return request({
      url: `/notification-settings/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除通知设置
   * @param {number} id - 设置ID
   * @returns {Promise} API响应
   */
  deleteSettings(id) {
    return request({
      url: `/notification-settings/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取设置详情
   * @param {number} id - 设置ID
   * @returns {Promise} API响应
   */
  getSettingsById(id) {
    return request({
      url: `/notification-settings/${id}`,
      method: 'get'
    })
  },

  /**
   * 批量更新用户设置
   * @param {number} userId - 用户ID
   * @param {Array} settings - 设置数组
   * @returns {Promise} API响应
   */
  batchUpdateUserSettings(userId, settings) {
    return request({
      url: `/notification-settings/user/${userId}/batch`,
      method: 'put',
      data: { settings }
    })
  },

  /**
   * 重置用户设置为默认
   * @param {number} userId - 用户ID
   * @returns {Promise} API响应
   */
  resetToDefault(userId) {
    return request({
      url: `/notification-settings/user/${userId}/reset`,
      method: 'post'
    })
  },

  /**
   * 获取默认通知设置
   * @returns {Promise} API响应
   */
  getDefaultSettings() {
    return request({
      url: '/notification-settings/defaults',
      method: 'get'
    })
  },

  /**
   * 复制其他用户设置
   * @param {number} fromUserId - 源用户ID
   * @param {number} toUserId - 目标用户ID
   * @returns {Promise} API响应
   */
  copyUserSettings(fromUserId, toUserId) {
    return request({
      url: `/notification-settings/copy/${fromUserId}/${toUserId}`,
      method: 'post'
    })
  },

  /**
   * 获取用户设置统计
   * @param {number} userId - 用户ID
   * @returns {Promise} API响应
   */
  getUserSettingsStats(userId) {
    return request({
      url: `/notification-settings/user/${userId}/stats`,
      method: 'get'
    })
  },

  /**
   * 获取所有通知类型
   * @returns {Promise} API响应
   */
  getNotificationTypes() {
    return request({
      url: '/notification-settings/types',
      method: 'get'
    })
  },

  /**
   * 获取所有通知渠道
   * @returns {Promise} API响应
   */
  getNotificationChannels() {
    return request({
      url: '/notification-settings/channels',
      method: 'get'
    })
  },

  /**
   * 获取通知优先级
   * @returns {Promise} API响应
   */
  getNotificationPriorities() {
    return request({
      url: '/notification-settings/priorities',
      method: 'get'
    })
  }
}

export default notificationSettingsApi
