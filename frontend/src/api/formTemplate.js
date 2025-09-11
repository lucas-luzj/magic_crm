import request from '../utils/request'

// 表单模板API接口
export const formTemplateApi = {
  // 获取表单模板列表（分页）
  getFormTemplates(params = {}) {
    return request({
      url: '/form-templates',
      method: 'get',
      params
    })
  },

  // 根据ID获取表单模板
  getFormTemplateById(id) {
    return request({
      url: `/form-templates/${id}`,
      method: 'get'
    })
  },

  // 根据表单键获取表单模板
  getFormTemplateByKey(formKey) {
    return request({
      url: `/form-templates/by-key/${formKey}`,
      method: 'get'
    })
  },

  // 创建表单模板
  createFormTemplate(data) {
    return request({
      url: '/form-templates',
      method: 'post',
      data
    })
  },

  // 更新表单模板
  updateFormTemplate(id, data) {
    return request({
      url: `/form-templates/${id}`,
      method: 'put',
      data
    })
  },

  // 删除表单模板
  deleteFormTemplate(id) {
    return request({
      url: `/form-templates/${id}`,
      method: 'delete'
    })
  },

  // 复制表单模板
  copyFormTemplate(id, newFormKey, newFormName) {
    return request({
      url: `/form-templates/${id}/copy`,
      method: 'post',
      params: {
        newFormKey,
        newFormName
      }
    })
  },

  // 激活表单模板
  activateFormTemplate(id) {
    return request({
      url: `/form-templates/${id}/activate`,
      method: 'put'
    })
  },

  // 停用表单模板
  deactivateFormTemplate(id) {
    return request({
      url: `/form-templates/${id}/deactivate`,
      method: 'put'
    })
  },

  // 获取所有分类
  getCategories() {
    return request({
      url: '/form-templates/categories',
      method: 'get'
    })
  },

  // 根据标签查找表单模板
  getFormTemplatesByTag(tag) {
    return request({
      url: `/form-templates/by-tag/${tag}`,
      method: 'get'
    })
  }
}