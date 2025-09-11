import request from '@/utils/request'

// 客户管理API
export const customerApi = {
  // 获取客户列表
  getCustomers(params) {
    return request({
      url: '/api/customers/search',
      method: 'post',
      data: params
    })
  },

  // 获取客户详情
  getCustomerById(id) {
    return request({
      url: `/api/customers/${id}`,
      method: 'get'
    })
  },

  // 创建客户
  createCustomer(data) {
    return request({
      url: '/api/customers',
      method: 'post',
      data
    })
  },

  // 更新客户
  updateCustomer(id, data) {
    return request({
      url: `/api/customers/${id}`,
      method: 'put',
      data
    })
  },

  // 删除客户
  deleteCustomer(id) {
    return request({
      url: `/api/customers/${id}`,
      method: 'delete'
    })
  },

  // 批量删除客户
  deleteCustomers(ids) {
    return request({
      url: '/api/customers/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取客户统计信息
  getCustomerStatistics() {
    return request({
      url: '/api/customers/statistics',
      method: 'get'
    })
  },

  // 获取公海池客户
  getPublicPoolCustomers(params) {
    return request({
      url: '/api/customers/public-pool',
      method: 'get',
      params
    })
  },

  // 认领客户
  claimCustomer(id) {
    return request({
      url: `/api/customers/${id}/claim`,
      method: 'post'
    })
  },

  // 释放客户到公海池
  releaseToPool(id, reason) {
    return request({
      url: `/api/customers/${id}/release-to-pool`,
      method: 'post',
      data: { reason }
    })
  },

  // 分配客户
  assignCustomer(id, ownerId) {
    return request({
      url: `/api/customers/${id}/assign`,
      method: 'post',
      data: { ownerId }
    })
  },

  // 分享客户
  shareCustomer(id, userIds) {
    return request({
      url: `/api/customers/${id}/share`,
      method: 'post',
      data: { userIds }
    })
  },

  // 合并客户
  mergeCustomers(sourceId, targetId) {
    return request({
      url: '/api/customers/merge',
      method: 'post',
      data: { sourceId, targetId }
    })
  },

  // 更新客户状态
  updateCustomerStatus(id, status) {
    return request({
      url: `/api/customers/${id}/status`,
      method: 'put',
      data: { status }
    })
  },

  // 导入客户
  importCustomers(data) {
    return request({
      url: '/api/customers/import',
      method: 'post',
      data
    })
  },

  // 导出客户
  exportCustomers(params) {
    return request({
      url: '/api/customers/export',
      method: 'post',
      data: params,
      responseType: 'blob'
    })
  },

  // 检查客户重复
  checkDuplicates(name, phone, email) {
    return request({
      url: '/api/customers/check-duplicates',
      method: 'post',
      data: { name, phone, email }
    })
  }
}

export default customerApi
