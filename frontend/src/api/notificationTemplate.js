import request from '@/utils/request'

/**
 * 通知模板API
 */
export const notificationTemplateApi = {
  /**
   * 获取模板列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getTemplates(params = {}) {
    return request({
      url: '/notification-templates',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        name: params.name,
        type: params.type,
        channel: params.channel,
        isEnabled: params.isEnabled,
        ...params
      }
    })
  },

  /**
   * 创建模板
   * @param {Object} data - 模板数据
   * @returns {Promise} API响应
   */
  createTemplate(data) {
    return request({
      url: '/notification-templates',
      method: 'post',
      data
    })
  },

  /**
   * 更新模板
   * @param {number} id - 模板ID
   * @param {Object} data - 模板数据
   * @returns {Promise} API响应
   */
  updateTemplate(id, data) {
    return request({
      url: `/notification-templates/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除模板
   * @param {number} id - 模板ID
   * @returns {Promise} API响应
   */
  deleteTemplate(id) {
    return request({
      url: `/notification-templates/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取模板详情
   * @param {number} id - 模板ID
   * @returns {Promise} API响应
   */
  getTemplateById(id) {
    return request({
      url: `/notification-templates/${id}`,
      method: 'get'
    })
  },

  /**
   * 根据代码获取模板
   * @param {string} code - 模板代码
   * @returns {Promise} API响应
   */
  getTemplateByCode(code) {
    return request({
      url: `/notification-templates/code/${code}`,
      method: 'get'
    })
  },

  /**
   * 根据类型获取模板列表
   * @param {string} type - 通知类型
   * @returns {Promise} API响应
   */
  getTemplatesByType(type) {
    return request({
      url: `/notification-templates/type/${type}`,
      method: 'get'
    })
  },

  /**
   * 根据渠道获取模板列表
   * @param {string} channel - 通知渠道
   * @returns {Promise} API响应
   */
  getTemplatesByChannel(channel) {
    return request({
      url: `/notification-templates/channel/${channel}`,
      method: 'get'
    })
  },

  /**
   * 根据类型和渠道获取模板列表
   * @param {string} type - 通知类型
   * @param {string} channel - 通知渠道
   * @returns {Promise} API响应
   */
  getTemplatesByTypeAndChannel(type, channel) {
    return request({
      url: `/notification-templates/type/${type}/channel/${channel}`,
      method: 'get'
    })
  },

  /**
   * 启用/禁用模板
   * @param {number} id - 模板ID
   * @returns {Promise} API响应
   */
  toggleTemplateStatus(id) {
    return request({
      url: `/notification-templates/${id}/toggle`,
      method: 'put'
    })
  },

  /**
   * 复制模板
   * @param {number} id - 模板ID
   * @param {Object} data - 复制数据
   * @returns {Promise} API响应
   */
  copyTemplate(id, data) {
    return request({
      url: `/notification-templates/${id}/copy`,
      method: 'post',
      data
    })
  },

  /**
   * 渲染模板
   * @param {string} code - 模板代码
   * @param {Object} variables - 变量数据
   * @returns {Promise} API响应
   */
  renderTemplate(code, variables) {
    return request({
      url: `/notification-templates/${code}/render`,
      method: 'post',
      data: variables
    })
  },

  /**
   * 获取模板统计信息
   * @returns {Promise} API响应
   */
  getTemplateStats() {
    return request({
      url: '/notification-templates/stats/type',
      method: 'get'
    })
  },

  /**
   * 获取渠道统计信息
   * @returns {Promise} API响应
   */
  getChannelStats() {
    return request({
      url: '/notification-templates/stats/channel',
      method: 'get'
    })
  },

  /**
   * 批量启用/禁用模板
   * @param {Object} data - 批量操作数据
   * @returns {Promise} API响应
   */
  batchToggleTemplateStatus(data) {
    return request({
      url: '/notification-templates/batch-toggle',
      method: 'put',
      data
    })
  },

  /**
   * 获取所有通知类型
   * @returns {Promise} API响应
   */
  getNotificationTypes() {
    return request({
      url: '/notification-templates/types',
      method: 'get'
    })
  },

  /**
   * 获取所有通知渠道
   * @returns {Promise} API响应
   */
  getNotificationChannels() {
    return request({
      url: '/notification-templates/channels',
      method: 'get'
    })
  }
}

export default notificationTemplateApi
