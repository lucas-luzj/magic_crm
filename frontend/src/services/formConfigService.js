import { processDefinitionApi } from '@/api/workflow'
import { formTemplateApi } from '@/api/formTemplate'
import { getLeaveFormTemplate } from '@/data/leaveFormTemplate'
import { useUserStore } from '@/stores/user'

/**
 * 表单配置服务
 * 用于加载和处理流程中的表单配置
 */
export class FormConfigService {
  
  /**
   * 根据流程定义ID和表单键获取表单配置
   * @param {string} processDefinitionId - 流程定义ID
   * @param {string} formKey - 表单键
   * @returns {Promise<Object>} 表单配置对象
   */
  static async getFormConfig(processDefinitionId, formKey) {
    try {
      console.log('获取表单配置:', { processDefinitionId, formKey })
      
      // 1. 从流程定义中获取表单配置
      const response = await processDefinitionApi.getFormConfig(processDefinitionId)
      
      if (!response.data || !response.data.data) {
        throw new Error('未找到表单配置')
      }
      
      const configData = JSON.parse(response.data.data)
      console.log('流程表单配置数据:', configData)
      
      // 2. 查找匹配的表单配置
      let matchedConfig = null
      
      if (configData.configs && Array.isArray(configData.configs)) {
        // 多个表单配置的情况
        matchedConfig = configData.configs.find(config => config.formKey === formKey)
      } else if (configData.formKey === formKey) {
        // 单个表单配置的情况
        matchedConfig = configData
      }
      
      if (!matchedConfig) {
        throw new Error(`未找到表单键为 ${formKey} 的表单配置`)
      }
      
      console.log('匹配的表单配置:', matchedConfig)
      
      // 3. 如果是模板模式，需要从模板中获取完整配置
      if (matchedConfig.mode === 'template' && matchedConfig.templateId) {
        return await this.getFormConfigFromTemplate(matchedConfig.templateId, matchedConfig)
      }
      
      // 4. 手动模式，转换为FormRenderer需要的格式
      return this.convertToRendererConfig(matchedConfig)
      
    } catch (error) {
      console.error('获取表单配置失败:', error)
      throw error
    }
  }
  
  /**
   * 从表单模板获取配置
   * @param {string} templateId - 模板ID
   * @param {Object} baseConfig - 基础配置
   * @returns {Promise<Object>} 表单配置对象
   */
  static async getFormConfigFromTemplate(templateId, baseConfig) {
    try {
      console.log('从模板获取表单配置:', templateId)
      
      // 获取表单模板详情
      const templateResponse = await formTemplateApi.getFormTemplate(templateId)
      
      if (!templateResponse.data || !templateResponse.data.data) {
        throw new Error('未找到表单模板')
      }
      
      const template = templateResponse.data.data
      console.log('表单模板数据:', template)
      
      // 解析模板配置
      let templateConfig = {}
      if (typeof template.config === 'string') {
        templateConfig = JSON.parse(template.config)
      } else {
        templateConfig = template.config || {}
      }
      
      // 返回完整的表单配置
      return {
        formKey: baseConfig.formKey,
        formName: baseConfig.formName || template.name,
        templateId: templateId,
        mode: 'template',
        ...templateConfig
      }
      
    } catch (error) {
      console.error('从模板获取表单配置失败:', error)
      throw error
    }
  }
  
  /**
   * 将手动配置转换为FormRenderer需要的格式
   * @param {Object} config - 原始配置
   * @returns {Object} FormRenderer配置格式
   */
  static convertToRendererConfig(config) {
    // 如果已经是FormRenderer格式，直接返回
    if (config.components && Array.isArray(config.components)) {
      return config
    }
    
    // 转换旧格式的字段配置为新格式
    const components = []
    
    if (config.fields && Array.isArray(config.fields)) {
      config.fields.forEach(field => {
        const component = {
          type: this.mapFieldTypeToComponentType(field.type),
          field: field.name,
          label: field.label,
          required: field.required || false,
          span: field.span || 24,
          placeholder: field.placeholder || '',
          ...field.props
        }
        
        // 处理选项类型字段
        if (field.options && Array.isArray(field.options)) {
          component.options = field.options
        }
        
        // 处理默认值
        if (field.defaultValue !== undefined) {
          component.defaultValue = field.defaultValue
        }
        
        components.push(component)
      })
    }
    
    return {
      formKey: config.formKey,
      formName: config.formName,
      components: components,
      labelPosition: config.labelPosition || 'left',
      labelWidth: config.labelWidth || 100,
      size: config.size || 'default'
    }
  }
  
  /**
   * 映射字段类型到组件类型
   * @param {string} fieldType - 字段类型
   * @returns {string} 组件类型
   */
  static mapFieldTypeToComponentType(fieldType) {
    const typeMap = {
      'text': 'input',
      'textarea': 'textarea',
      'number': 'input-number',
      'select': 'select',
      'radio': 'radio-group',
      'checkbox': 'checkbox-group',
      'switch': 'switch',
      'date': 'date-picker',
      'datetime': 'date-picker',
      'time': 'time-picker',
      'file': 'upload'
    }
    
    return typeMap[fieldType] || 'input'
  }
  
  /**
   * 获取流程启动表单配置
   * @param {string} processDefinitionId - 流程定义ID
   * @returns {Promise<Object>} 启动表单配置
   */
  static async getStartFormConfig(processDefinitionId) {
    try {
      // 首先尝试从流程定义中获取表单配置
      const formConfig = await this.getFormConfig(processDefinitionId, 'start')
      return formConfig
    } catch (error) {
      console.error('获取启动表单配置失败:', error)
      
      // 如果没有配置启动表单，尝试根据流程类型提供默认表单
      try {
        // 获取流程定义信息来判断流程类型
        const processResponse = await processDefinitionApi.getProcessDefinition(processDefinitionId)
        if (processResponse.data && processResponse.data.data) {
          const processDefinition = processResponse.data.data
          
          // 如果是请假流程，使用请假表单模板
          if (processDefinition.key === 'leaveProcess' || 
              processDefinition.name.includes('请假') ||
              processDefinition.name.includes('leave')) {
            
            console.log('检测到请假流程，使用请假表单模板')
            
            // 获取当前用户信息
            let currentUser = {}
            try {
              const userStore = useUserStore()
              currentUser = userStore.userInfo || {}
            } catch (userError) {
              console.warn('获取用户信息失败:', userError)
            }
            
            // 返回请假表单模板
            return getLeaveFormTemplate(currentUser)
          }
        }
      } catch (processError) {
        console.error('获取流程定义信息失败:', processError)
      }
      
      // 如果都失败了，返回空配置
      return {
        formKey: 'start',
        formName: '启动表单',
        components: [],
        labelPosition: 'left',
        labelWidth: 100,
        size: 'default'
      }
    }
  }
  
  /**
   * 获取任务表单配置
   * @param {string} taskId - 任务ID
   * @returns {Promise<Object>} 任务表单配置
   */
  static async getTaskFormConfig(taskId) {
    try {
      // 这里需要根据任务ID获取对应的表单配置
      // 暂时返回空配置，后续实现
      return {
        formKey: `task_${taskId}`,
        formName: '任务表单',
        components: [],
        labelPosition: 'left',
        labelWidth: 100,
        size: 'default'
      }
    } catch (error) {
      console.error('获取任务表单配置失败:', error)
      throw error
    }
  }
}

export default FormConfigService