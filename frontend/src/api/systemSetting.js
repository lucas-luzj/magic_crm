import request from '@/utils/request'

/**
 * 系统设置API
 */
export const systemSettingApi = {
  /**
   * 获取所有设置
   * @returns {Promise} API响应
   */
  getAllSettings() {
    return request({
      url: '/system-settings',
      method: 'get'
    })
  },

  /**
   * 分页获取设置
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getSettingsPage(params = {}) {
    return request({
      url: '/system-settings/page',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 20,
        group: params.group,
        keyword: params.keyword,
        ...params
      }
    })
  },

  /**
   * 根据分组获取设置
   * @param {string} group - 设置分组
   * @returns {Promise} API响应
   */
  getSettingsByGroup(group) {
    return request({
      url: `/system-settings/group/${group}`,
      method: 'get'
    })
  },

  /**
   * 获取所有设置分组
   * @returns {Promise} API响应
   */
  getAllSettingGroups() {
    return request({
      url: '/system-settings/groups',
      method: 'get'
    })
  },

  /**
   * 根据键获取设置值
   * @param {string} key - 设置键
   * @returns {Promise} API响应
   */
  getSettingValue(key) {
    return request({
      url: `/system-settings/value/${key}`,
      method: 'get'
    })
  },

  /**
   * 根据键获取设置值，带默认值
   * @param {string} key - 设置键
   * @param {string} defaultValue - 默认值
   * @returns {Promise} API响应
   */
  getSettingValueWithDefault(key, defaultValue) {
    return request({
      url: `/system-settings/value/${key}/${defaultValue}`,
      method: 'get'
    })
  },

  /**
   * 设置值
   * @param {string} key - 设置键
   * @param {string} value - 设置值
   * @returns {Promise} API响应
   */
  setSettingValue(key, value) {
    return request({
      url: `/system-settings/value/${key}`,
      method: 'put',
      data: { value }
    })
  },

  /**
   * 批量设置值
   * @param {Object} settings - 设置键值对
   * @returns {Promise} API响应
   */
  setSettingValues(settings) {
    return request({
      url: '/system-settings/batch',
      method: 'put',
      data: settings
    })
  },

  /**
   * 创建设置项
   * @param {Object} setting - 设置数据
   * @returns {Promise} API响应
   */
  createSetting(setting) {
    return request({
      url: '/system-settings',
      method: 'post',
      data: setting
    })
  },

  /**
   * 更新设置项
   * @param {number} id - 设置ID
   * @param {Object} setting - 设置数据
   * @returns {Promise} API响应
   */
  updateSetting(id, setting) {
    return request({
      url: `/system-settings/${id}`,
      method: 'put',
      data: setting
    })
  },

  /**
   * 删除设置项
   * @param {number} id - 设置ID
   * @returns {Promise} API响应
   */
  deleteSetting(id) {
    return request({
      url: `/system-settings/${id}`,
      method: 'delete'
    })
  },

  /**
   * 重置为默认值
   * @param {string} key - 设置键
   * @returns {Promise} API响应
   */
  resetToDefault(key) {
    return request({
      url: `/system-settings/reset/${key}`,
      method: 'post'
    })
  },

  /**
   * 获取邮件配置
   * @returns {Promise} API响应
   */
  getEmailConfig() {
    return request({
      url: '/system-settings/config/email',
      method: 'get'
    })
  },

  /**
   * 获取短信配置
   * @returns {Promise} API响应
   */
  getSmsConfig() {
    return request({
      url: '/system-settings/config/sms',
      method: 'get'
    })
  },

  /**
   * 获取微信配置
   * @returns {Promise} API响应
   */
  getWechatConfig() {
    return request({
      url: '/system-settings/config/wechat',
      method: 'get'
    })
  },

  /**
   * 获取钉钉配置
   * @returns {Promise} API响应
   */
  getDingtalkConfig() {
    return request({
      url: '/system-settings/config/dingtalk',
      method: 'get'
    })
  },

  /**
   * 获取推送配置
   * @returns {Promise} API响应
   */
  getPushConfig() {
    return request({
      url: '/system-settings/config/push',
      method: 'get'
    })
  },

  /**
   * 测试邮件配置
   * @param {Object} request - 测试请求数据
   * @returns {Promise} API响应
   */
  testEmailConfig(request) {
    return request({
      url: '/system-settings/test/email',
      method: 'post',
      data: request
    })
  },

  /**
   * 测试短信配置
   * @param {Object} request - 测试请求数据
   * @returns {Promise} API响应
   */
  testSmsConfig(request) {
    return request({
      url: '/system-settings/test/sms',
      method: 'post',
      data: request
    })
  },

  /**
   * 测试微信配置
   * @param {Object} request - 测试请求数据
   * @returns {Promise} API响应
   */
  testWechatConfig(request) {
    return request({
      url: '/system-settings/test/wechat',
      method: 'post',
      data: request
    })
  },

  /**
   * 测试钉钉配置
   * @param {Object} request - 测试请求数据
   * @returns {Promise} API响应
   */
  testDingtalkConfig(request) {
    return request({
      url: '/system-settings/test/dingtalk',
      method: 'post',
      data: request
    })
  },

  /**
   * 测试推送配置
   * @param {Object} request - 测试请求数据
   * @returns {Promise} API响应
   */
  testPushConfig(request) {
    return request({
      url: '/system-settings/test/push',
      method: 'post',
      data: request
    })
  }
}

export default systemSettingApi
