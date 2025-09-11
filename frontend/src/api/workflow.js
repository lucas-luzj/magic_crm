import request from '@/utils/request'

// 流程定义相关API
export const processDefinitionApi = {
  // 部署流程定义（通过文件上传）
  deployProcess(data) {
    const formData = new FormData()
    formData.append('processName', data.processName)
    formData.append('category', data.category)
    formData.append('bpmnFile', data.bpmnFile)
    
    return request({
      url: '/workflow/process-definitions/deploy',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 部署流程定义（通过XML内容）
  deployProcessFromXml(data) {
    return request({
      url: '/workflow/process-definitions/deploy-xml',
      method: 'post',
      data: data
    })
  },

  // 分页查询流程定义
  getProcessDefinitions(params) {
    return request({
      url: '/workflow/process-definitions',
      method: 'get',
      params
    })
  },

  // 获取所有流程定义
  getAllProcessDefinitions() {
    return request({
      url: '/workflow/process-definitions/all',
      method: 'get'
    })
  },

  // 根据ID获取流程定义详情
  getProcessDefinitionById(id) {
    return request({
      url: `/workflow/process-definitions/${id}`,
      method: 'get'
    })
  },

  // 根据流程定义ID获取流程定义详情
  getProcessDefinitionByProcessDefId(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/by-process-def-id/${processDefinitionId}`,
      method: 'get'
    })
  },

  // 激活流程定义
  activateProcessDefinition(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/activate`,
      method: 'post'
    })
  },

  // 挂起流程定义
  suspendProcessDefinition(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/suspend`,
      method: 'post'
    })
  },

  // 删除流程定义
  deleteProcessDefinition(processDefinitionId, cascade = false) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}`,
      method: 'delete',
      params: { cascade }
    })
  },

  // 获取流程定义的BPMN XML
  getProcessDefinitionBpmnXml(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/xml`,
      method: 'get'
    })
  },

  // 获取流程定义的流程图
  getProcessDefinitionDiagram(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/diagram`,
      method: 'get',
      responseType: 'blob'
    })
  },

  // 根据流程Key获取最新版本的流程定义
  getLatestProcessDefinitionByKey(processKey) {
    return request({
      url: `/workflow/process-definitions/latest/${processKey}`,
      method: 'get'
    })
  },

  // 根据分类获取流程定义
  getProcessDefinitionsByCategory(category) {
    return request({
      url: `/workflow/process-definitions/by-category/${category}`,
      method: 'get'
    })
  },

  // 保存流程定义的表单配置
  saveFormConfig(processDefinitionId, formConfig) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/form-config`,
      method: 'post',
      data: formConfig,
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 获取流程定义的表单配置
  getFormConfig(processDefinitionId) {
    return request({
      url: `/workflow/process-definitions/${processDefinitionId}/form-config`,
      method: 'get'
    })
  },

  // 根据流程定义ID更新表单配置
  updateFormConfig(id, formConfig) {
    return request({
      url: `/workflow/process-definitions/${id}/form-config`,
      method: 'put',
      data: formConfig,
      headers: {
        'Content-Type': 'application/json'
      }
    })
  }
}

// 流程实例相关API
export const processInstanceApi = {
  // 根据流程定义Key启动流程实例
  startProcessInstance(data) {
    return request({
      url: '/process-instances/start',
      method: 'post',
      params: {
        processDefinitionKey: data.processDefinitionKey,
        businessKey: data.businessKey
      },
      data: data.variables
    })
  },

  // 根据流程定义ID启动流程实例
  startProcessInstanceById(data) {
    return request({
      url: '/process-instances/start-by-id',
      method: 'post',
      params: {
        processDefinitionId: data.processDefinitionId,
        businessKey: data.businessKey
      },
      data: data.variables
    })
  },

  // 分页查询流程实例
  getProcessInstances(params) {
    return request({
      url: '/process-instances',
      method: 'get',
      params
    })
  },

  // 根据ID获取流程实例详情
  getProcessInstanceById(id) {
    return request({
      url: `/process-instances/${id}`,
      method: 'get'
    })
  },

  // 暂停流程实例
  suspendProcessInstance(processInstanceId) {
    return request({
      url: `/process-instances/${processInstanceId}/suspend`,
      method: 'put'
    })
  },

  // 激活流程实例
  activateProcessInstance(processInstanceId) {
    return request({
      url: `/process-instances/${processInstanceId}/activate`,
      method: 'put'
    })
  },

  // 终止流程实例
  terminateProcessInstance(processInstanceId, reason = '手动终止') {
    return request({
      url: `/process-instances/${processInstanceId}`,
      method: 'delete',
      params: { reason }
    })
  },

  // 获取流程实例的当前任务
  getCurrentTasks(processInstanceId) {
    return request({
      url: `/process-instances/${processInstanceId}/current-tasks`,
      method: 'get'
    })
  },

  // 获取流程实例的历史任务
  getHistoryTasks(processInstanceId) {
    return request({
      url: `/process-instances/${processInstanceId}/history-tasks`,
      method: 'get'
    })
  },

  // 设置流程变量
  setProcessVariable(processInstanceId, variableName, value) {
    return request({
      url: `/process-instances/${processInstanceId}/variables/${variableName}`,
      method: 'put',
      data: value
    })
  },

  // 获取流程变量
  getProcessVariables(processInstanceId) {
    return request({
      url: `/process-instances/${processInstanceId}/variables`,
      method: 'get'
    })
  },

  // 获取当前用户的流程实例
  getMyProcessInstances() {
    return request({
      url: '/process-instances/my-instances',
      method: 'get'
    })
  },

  // 获取发起的流程实例
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

  // 获取流程实例历史
  getProcessHistory(processInstanceId) {
    return request({
      url: `/api/process-instances/${processInstanceId}/history`,
      method: 'get'
    })
  },

  // 启动流程实例（通用方法）
  startProcess(data) {
    return request({
      url: '/process-instances/start',
      method: 'post',
      data
    })
  },

  // 获取流程实例详情
  getProcessInstance(id) {
    return request({
      url: `/api/process-instances/${id}`,
      method: 'get'
    })
  }
}

// 任务相关API
export const taskApi = {
  // 获取待办任务列表
  getPendingTasks(params) {
    return request({
      url: '/workflow/tasks/pending',
      method: 'get',
      params
    })
  },

  // 获取已完成任务列表
  getCompletedTasks(params) {
    return request({
      url: '/workflow/tasks/completed',
      method: 'get',
      params
    })
  },

  // 完成任务
  completeTask(taskId, variables, userId) {
    return request({
      url: `/workflow/tasks/${taskId}/complete`,
      method: 'post',
      params: { userId },
      data: variables
    })
  },

  // 分配任务
  assignTask(taskId, assignee, operatorId) {
    return request({
      url: `/workflow/tasks/${taskId}/assign`,
      method: 'post',
      params: { assignee, operatorId }
    })
  },

  // 委派任务
  delegateTask(taskId, delegatee, operatorId) {
    return request({
      url: `/workflow/tasks/${taskId}/delegate`,
      method: 'post',
      params: { delegatee, operatorId }
    })
  },

  // 转办任务
  transferTask(taskId, newAssignee, operatorId) {
    return request({
      url: `/workflow/tasks/${taskId}/transfer`,
      method: 'post',
      params: { newAssignee, operatorId }
    })
  },

  // ==================== 高级任务操作 API ====================

  // 审批任务
  approveTask(taskId, data) {
    return request({
      url: `/workflow/tasks/${taskId}/approve`,
      method: 'post',
      data
    })
  },

  // 回退任务
  rollbackTask(taskId, data) {
    return request({
      url: `/workflow/tasks/${taskId}/rollback`,
      method: 'post',
      data
    })
  },

  // 批量完成任务
  batchCompleteTasks(data) {
    return request({
      url: '/workflow/tasks/batch/complete',
      method: 'post',
      data
    })
  },

  // 批量分配任务
  batchAssignTasks(data) {
    return request({
      url: '/workflow/tasks/batch/assign',
      method: 'post',
      data
    })
  },

  // 获取任务历史记录
  getTaskHistory(taskId) {
    return request({
      url: `/workflow/tasks/${taskId}/history`,
      method: 'get'
    })
  },

  // 添加任务评论
  addTaskComment(taskId, data) {
    return request({
      url: `/workflow/tasks/${taskId}/comments`,
      method: 'post',
      data
    })
  },

  // 获取任务评论
  getTaskComments(taskId) {
    return request({
      url: `/workflow/tasks/${taskId}/comments`,
      method: 'get'
    })
  },

  // 获取可回退的节点
  getRollbackNodes(taskId) {
    return request({
      url: `/workflow/tasks/${taskId}/rollback-nodes`,
      method: 'get'
    })
  },

  // 获取任务详细统计
  getDetailedTaskStatistics() {
    return request({
      url: '/workflow/tasks/statistics/detailed',
      method: 'get'
    })
  },

  // 获取任务详情
  getTask(taskId) {
    return request({
      url: `/api/tasks/${taskId}`,
      method: 'get'
    })
  },

  // 处理任务（通用方法）
  handleTask(data) {
    return request({
      url: `/api/tasks/${data.taskId}/handle`,
      method: 'post',
      data: {
        action: data.action,
        variables: data.variables,
        comment: data.comment
      }
    })
  }
}