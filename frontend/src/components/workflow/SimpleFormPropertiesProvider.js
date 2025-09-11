import { is } from 'bpmn-js/lib/util/ModelUtil'

/**
 * 简化版表单属性提供器
 * 为用户任务节点添加表单配置功能
 */
export default function SimpleFormPropertiesProvider(propertiesPanel, translate, modeling, moddle) {
  
  this.getGroups = function(element) {
    return function(groups) {
      // 只为用户任务添加表单配置
      if (is(element, 'bpmn:UserTask')) {
        groups.push(createFormGroup(element, translate, modeling, moddle))
      }
      return groups
    }
  }

  propertiesPanel.registerProvider(this)
}

SimpleFormPropertiesProvider.$inject = ['propertiesPanel', 'translate', 'modeling', 'moddle']

/**
 * 创建表单配置组
 */
function createFormGroup(element, translate, modeling, moddle) {
  return {
    id: 'form-properties',
    label: '表单配置',
    entries: [
      {
        id: 'form-key',
        label: '表单标识',
        modelProperty: 'formKey',
        get: function(element) {
          return {
            formKey: element.businessObject.formKey || ''
          }
        },
        set: function(element, values) {
          return modeling.updateProperties(element, {
            formKey: values.formKey || undefined
          })
        },
        validate: function(element, values) {
          const formKey = values.formKey
          if (formKey && !/^[a-zA-Z][a-zA-Z0-9_-]*$/.test(formKey)) {
            return { formKey: '表单标识格式不正确' }
          }
        }
      },
      {
        id: 'form-fields-json',
        label: '表单字段配置',
        modelProperty: 'formFieldsJson',
        get: function(element) {
          const formFields = getFormFields(element)
          return {
            formFieldsJson: JSON.stringify(formFields, null, 2)
          }
        },
        set: function(element, values) {
          try {
            const formFields = JSON.parse(values.formFieldsJson || '[]')
            return setFormFields(element, formFields, modeling, moddle)
          } catch (error) {
            console.error('表单字段配置JSON格式错误:', error)
            return
          }
        },
        validate: function(element, values) {
          try {
            JSON.parse(values.formFieldsJson || '[]')
          } catch (error) {
            return { formFieldsJson: 'JSON格式不正确' }
          }
        }
      }
    ]
  }
}

/**
 * 获取表单字段配置
 */
function getFormFields(element) {
  const businessObject = element.businessObject
  const extensionElements = businessObject.extensionElements
  
  if (!extensionElements) {
    return []
  }

  const formData = extensionElements.values.find(
    ext => ext.$type === 'custom:FormData'
  )

  if (!formData || !formData.formFields) {
    return []
  }

  return formData.formFields.map(field => ({
    id: field.id,
    label: field.label,
    type: field.type,
    required: field.required,
    placeholder: field.placeholder,
    options: field.options,
    defaultValue: field.defaultValue,
    readonly: field.readonly,
    visible: field.visible !== false
  }))
}

/**
 * 设置表单字段配置
 */
function setFormFields(element, formFields, modeling, moddle) {
  const businessObject = element.businessObject
  
  // 创建或获取扩展元素
  let extensionElements = businessObject.extensionElements
  if (!extensionElements) {
    extensionElements = moddle.create('bpmn:ExtensionElements')
  }

  // 移除现有的表单数据
  extensionElements.values = (extensionElements.values || []).filter(
    ext => ext.$type !== 'custom:FormData'
  )

  // 添加新的表单数据
  if (formFields && formFields.length > 0) {
    const formData = moddle.create('custom:FormData', {
      formFields: formFields.map(field => 
        moddle.create('custom:FormField', {
          id: field.id,
          label: field.label,
          type: field.type,
          required: field.required,
          placeholder: field.placeholder,
          options: field.options,
          defaultValue: field.defaultValue,
          readonly: field.readonly,
          visible: field.visible !== false
        })
      )
    })
    
    extensionElements.values.push(formData)
  }

  return modeling.updateProperties(element, { extensionElements })
}