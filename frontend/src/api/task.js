import request from '@/utils/request'

/**
 * 任务相关API
 */
export const taskApi = {
  /**
   * 获取待办任务列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getPendingTasks(params = {}) {
    return request({
      url: '/tasks/pending',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        processDefinitionKey: params.processDefinitionKey,
        taskName: params.taskName,
        assignee: params.assignee,
        candidateUser: params.candidateUser,
        candidateGroup: params.candidateGroup,
        ...params
      }
    })
  },

  /**
   * 获取已办任务列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getCompletedTasks(params = {}) {
    return request({
      url: '/tasks/completed',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        processDefinitionKey: params.processDefinitionKey,
        taskName: params.taskName,
        assignee: params.assignee,
        startTime: params.startTime,
        endTime: params.endTime,
        ...params
      }
    })
  },

  /**
   * 获取我发起的流程实例列表
   * @param {Object} params - 查询参数
   * @returns {Promise} API响应
   */
  getInitiatedProcesses(params = {}) {
    return request({
      url: '/process-instances/initiated',
      method: 'get',
      params: {
        page: params.page || 1,
        size: params.size || 10,
        processDefinitionKey: params.processDefinitionKey,
        processInstanceName: params.processInstanceName,
        startTime: params.startTime,
        endTime: params.endTime,
        status: params.status,
        ...params
      }
    })
  },

  /**
   * 获取任务详情
   * @param {string} taskId - 任务ID
   * @returns {Promise} API响应
   */
  getTaskDetail(taskId) {
    return request({
      url: `/tasks/${taskId}`,
      method: 'get'
    })
  },

  /**
   * 完成任务
   * @param {string} taskId - 任务ID
   * @param {Object} data - 任务数据
   * @returns {Promise} API响应
   */
  completeTask(taskId, data = {}) {
    return request({
      url: `/tasks/${taskId}/complete`,
      method: 'post',
      data: {
        variables: data.variables || {},
        comment: data.comment,
        formData: data.formData,
        ...data
      }
    })
  },

  /**
   * 认领任务
   * @param {string} taskId - 任务ID
   * @returns {Promise} API响应
   */
  claimTask(taskId) {
    return request({
      url: `/tasks/${taskId}/claim`,
      method: 'post'
    })
  },

  /**
   * 取消认领任务
   * @param {string} taskId - 任务ID
   * @returns {Promise} API响应
   */
  unclaimTask(taskId) {
    return request({
      url: `/tasks/${taskId}/unclaim`,
      method: 'post'
    })
  },

  /**
   * 委派任务
   * @param {string} taskId - 任务ID
   * @param {string} assignee - 被委派人
   * @returns {Promise} API响应
   */
  delegateTask(taskId, assignee) {
    return request({
      url: `/tasks/${taskId}/delegate`,
      method: 'post',
      data: { assignee }
    })
  },

  /**
   * 转办任务
   * @param {string} taskId - 任务ID
   * @param {string} assignee - 转办人
   * @returns {Promise} API响应
   */
  transferTask(taskId, assignee) {
    return request({
      url: `/tasks/${taskId}/transfer`,
      method: 'post',
      data: { assignee }
    })
  },

  /**
   * 获取任务统计信息
   * @returns {Promise} API响应
   */
  getTaskStatistics() {
    return request({
      url: '/tasks/statistics',
      method: 'get'
    })
  },

  /**
   * 获取流程实例统计信息
   * @returns {Promise} API响应
   */
  getProcessStatistics() {
    return request({
      url: '/process-instances/statistics',
      method: 'get'
    })
  },

  /**
   * 获取任务历史
   * @param {string} taskId - 任务ID
   * @returns {Promise} API响应
   */
  getTaskHistory(taskId) {
    return request({
      url: `/tasks/${taskId}/history`,
      method: 'get'
    })
  },

  /**
   * 添加任务评论
   * @param {string} taskId - 任务ID
   * @param {string} comment - 评论内容
   * @returns {Promise} API响应
   */
  addTaskComment(taskId, comment) {
    return request({
      url: `/tasks/${taskId}/comments`,
      method: 'post',
      data: { comment }
    })
  },

  /**
   * 获取任务评论列表
   * @param {string} taskId - 任务ID
   * @returns {Promise} API响应
   */
  getTaskComments(taskId) {
    return request({
      url: `/tasks/${taskId}/comments`,
      method: 'get'
    })
  }
}

export default taskApi