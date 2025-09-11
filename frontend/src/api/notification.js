import request from '@/utils/request'

/**
 * 消息通知API
 */
export const notificationApi = {
  /**
   * 获取通知列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getNotifications(params = {}) {
    return request({
      url: '/notifications',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        type: params.type,
        status: params.status,
        startTime: params.startTime,
        endTime: params.endTime,
        ...params
      }
    })
  },

  /**
   * 获取未读通知数量
   * @returns {Promise} API响应
   */
  getUnreadCount() {
    return request({
      url: '/notifications/unread-count',
      method: 'get'
    })
  },

  /**
   * 标记通知为已读
   * @param {string|number} id - 通知ID
   * @returns {Promise} API响应
   */
  markAsRead(id) {
    return request({
      url: `/notifications/${id}/read`,
      method: 'put'
    })
  },

  /**
   * 批量标记通知为已读
   * @param {Array} ids - 通知ID数组
   * @returns {Promise} API响应
   */
  batchMarkAsRead(ids) {
    return request({
      url: '/notifications/batch-read',
      method: 'put',
      data: { ids }
    })
  },

  /**
   * 标记所有通知为已读
   * @returns {Promise} API响应
   */
  markAllAsRead() {
    return request({
      url: '/notifications/mark-all-read',
      method: 'put'
    })
  },

  /**
   * 删除通知
   * @param {string|number} id - 通知ID
   * @returns {Promise} API响应
   */
  deleteNotification(id) {
    return request({
      url: `/notifications/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除通知
   * @param {Array} ids - 通知ID数组
   * @returns {Promise} API响应
   */
  batchDeleteNotifications(ids) {
    return request({
      url: '/notifications/batch-delete',
      method: 'delete',
      data: { ids }
    })
  },

  /**
   * 获取通知设置
   * @returns {Promise} API响应
   */
  getNotificationSettings() {
    return request({
      url: '/notifications/settings',
      method: 'get'
    })
  },

  /**
   * 更新通知设置
   * @param {Object} settings - 通知设置
   * @returns {Promise} API响应
   */
  updateNotificationSettings(settings) {
    return request({
      url: '/notifications/settings',
      method: 'put',
      data: settings
    })
  },

  /**
   * 发送测试通知
   * @param {Object} data - 测试通知数据
   * @returns {Promise} API响应
   */
  sendTestNotification(data) {
    return request({
      url: '/notifications/test',
      method: 'post',
      data
    })
  }
}

export default notificationApi
