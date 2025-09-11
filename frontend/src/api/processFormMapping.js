import request from '../utils/request'

// 流程表单映射API接口
export const processFormMappingApi = {
  // 创建流程表单映射
  createProcessFormMapping(data) {
    return request({
      url: '/process-form-mappings',
      method: 'post',
      data
    })
  },

  // 更新流程表单映射
  updateProcessFormMapping(id, data) {
    return request({
      url: `/process-form-mappings/${id}`,
      method: 'put',
      data
    })
  },

  // 删除流程表单映射
  deleteProcessFormMapping(id) {
    return request({
      url: `/process-form-mappings/${id}`,
      method: 'delete'
    })
  },

  // 根据ID获取流程表单映射
  getProcessFormMappingById(id) {
    return request({
      url: `/process-form-mappings/${id}`,
      method: 'get'
    })
  },

  // 根据流程定义Key获取映射
  getMappingsByProcessDefinition(processDefinitionKey) {
    return request({
      url: `/process-form-mappings/by-process/${processDefinitionKey}`,
      method: 'get'
    })
  },

  // 根据任务定义Key获取映射
  getMappingsByTaskDefinition(taskDefinitionKey) {
    return request({
      url: `/process-form-mappings/by-task/${taskDefinitionKey}`,
      method: 'get'
    })
  },

  // 根据表单模板ID获取映射
  getMappingsByFormTemplate(formTemplateId) {
    return request({
      url: `/process-form-mappings/by-form-template/${formTemplateId}`,
      method: 'get'
    })
  },

  // 获取默认表单映射
  getDefaultFormMapping(processDefinitionKey, taskDefinitionKey) {
    return request({
      url: '/process-form-mappings/default',
      method: 'get',
      params: {
        processDefinitionKey,
        taskDefinitionKey
      }
    })
  },

  // 根据流程定义Key和任务定义Key获取映射
  getMappingsByProcessAndTask(processDefinitionKey, taskDefinitionKey) {
    return request({
      url: '/process-form-mappings/by-process-and-task',
      method: 'get',
      params: {
        processDefinitionKey,
        taskDefinitionKey
      }
    })
  },

  // 根据流程定义Key和映射类型获取映射
  getMappingsByProcessAndType(processDefinitionKey, mappingType) {
    return request({
      url: '/process-form-mappings/by-process-and-type',
      method: 'get',
      params: {
        processDefinitionKey,
        mappingType
      }
    })
  },

  // 批量创建流程表单映射
  createBatchMappings(mappings) {
    return request({
      url: '/process-form-mappings/batch',
      method: 'post',
      data: mappings
    })
  },

  // 根据流程定义Key删除所有映射
  deleteMappingsByProcessDefinition(processDefinitionKey) {
    return request({
      url: `/process-form-mappings/by-process/${processDefinitionKey}`,
      method: 'delete'
    })
  },

  // 根据表单模板ID删除所有映射
  deleteMappingsByFormTemplate(formTemplateId) {
    return request({
      url: `/process-form-mappings/by-form-template/${formTemplateId}`,
      method: 'delete'
    })
  }
}
