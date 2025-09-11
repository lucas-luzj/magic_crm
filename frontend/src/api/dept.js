import request from '@/utils/request'

/**
 * 部门管理API
 */
export const deptApi = {
  /**
   * 获取部门列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getDeptList(params = {}) {
    return request({
      url: '/depts',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        name: params.name,
        parentId: params.parentId,
        ...params
      }
    })
  },

  /**
   * 获取部门详情
   * @param {string|number} id - 部门ID
   * @returns {Promise} API响应
   */
  getDept(id) {
    return request({
      url: `/api/depts/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建部门
   * @param {Object} data - 部门数据
   * @returns {Promise} API响应
   */
  createDept(data) {
    return request({
      url: '/depts',
      method: 'post',
      data
    })
  },

  /**
   * 更新部门
   * @param {string|number} id - 部门ID
   * @param {Object} data - 部门数据
   * @returns {Promise} API响应
   */
  updateDept(id, data) {
    return request({
      url: `/api/depts/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除部门
   * @param {string|number} id - 部门ID
   * @returns {Promise} API响应
   */
  deleteDept(id) {
    return request({
      url: `/api/depts/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取部门树形结构
   * @returns {Promise} API响应
   */
  getDeptTree() {
    return request({
      url: '/depts/tree',
      method: 'get'
    })
  },

  /**
   * 获取部门下的用户列表
   * @param {string|number} deptId - 部门ID
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getDeptUsers(deptId, params = {}) {
    return request({
      url: `/api/depts/${deptId}/users`,
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        ...params
      }
    })
  },

  /**
   * 移动部门
   * @param {string|number} id - 部门ID
   * @param {string|number} parentId - 新父部门ID
   * @returns {Promise} API响应
   */
  moveDept(id, parentId) {
    return request({
      url: `/api/depts/${id}/move`,
      method: 'put',
      data: { parentId }
    })
  }
}

export default deptApi
