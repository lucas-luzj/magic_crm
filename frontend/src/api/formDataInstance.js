import request from '../utils/request'

// 表单数据实例API接口
export const formDataInstanceApi = {
  // 获取表单数据实例列表（分页）
  getFormDataInstances(params = {}) {
    return request({
      url: '/form-data-instances',
      method: 'get',
      params
    })
  },

  // 根据ID获取表单数据实例
  getFormDataInstanceById(id) {
    return request({
      url: `/form-data-instances/${id}`,
      method: 'get'
    })
  },

  // 根据表单模板ID获取表单数据实例
  getFormDataInstancesByFormTemplate(formTemplateId) {
    return request({
      url: `/form-data-instances/by-form-template/${formTemplateId}`,
      method: 'get'
    })
  },

  // 根据流程实例ID获取表单数据实例
  getFormDataInstancesByProcessInstance(processInstanceId) {
    return request({
      url: `/form-data-instances/by-process-instance/${processInstanceId}`,
      method: 'get'
    })
  },

  // 根据任务ID获取表单数据实例
  getFormDataInstancesByTask(taskId) {
    return request({
      url: `/form-data-instances/by-task/${taskId}`,
      method: 'get'
    })
  },

  // 提交表单数据
  submitFormData(data) {
    return request({
      url: '/form-data-instances/submit',
      method: 'post',
      data
    })
  },

  // 保存草稿
  saveDraft(data) {
    return request({
      url: '/form-data-instances/draft',
      method: 'post',
      data
    })
  },

  // 审批表单数据
  approveFormData(id, approver) {
    return request({
      url: `/form-data-instances/${id}/approve`,
      method: 'put',
      params: { approver }
    })
  },

  // 拒绝表单数据
  rejectFormData(id, rejector) {
    return request({
      url: `/form-data-instances/${id}/reject`,
      method: 'put',
      params: { rejector }
    })
  },

  // 删除表单数据实例
  deleteFormDataInstance(id) {
    return request({
      url: `/form-data-instances/${id}`,
      method: 'delete'
    })
  },

  // 获取表单使用统计
  getFormUsageCount(formTemplateId) {
    return request({
      url: `/form-data-instances/statistics/form/${formTemplateId}`,
      method: 'get'
    })
  },

  // 获取流程使用统计
  getProcessUsageCount(processDefinitionKey) {
    return request({
      url: `/form-data-instances/statistics/process/${processDefinitionKey}`,
      method: 'get'
    })
  }
}
