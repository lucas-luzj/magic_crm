import { is } from 'bpmn-js/lib/util/ModelUtil'

/**
 * 简化版表单属性提供器
 * 符合BPMN.js属性面板规范
 */
export default function SimpleFormProvider(propertiesPanel, translate, modeling, moddle) {
  
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

SimpleFormProvider.$inject = ['propertiesPanel', 'translate', 'modeling', 'moddle']

/**
 * 创建表单配置组
 */
function createFormGroup(element, translate, modeling, moddle) {
  return {
    id: 'form-configuration',
    label: '表单配置',
    entries: [
      // 表单标识
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
            return { formKey: '表单标识只能包含字母、数字、下划线和连字符，且必须以字母开头' }
          }
        }
      },
      
      // 表单字段配置按钮
      {
        id: 'form-fields-config',
        label: '表单字段',
        html: `
          <div class="form-fields-section">
            <div class="form-fields-info">
              <span id="fields-count">字段数量: 0</span>
            </div>
            <div class="form-fields-actions">
              <button type="button" class="form-config-btn" onclick="openFormFieldsDialog()">
                配置表单字段
              </button>
            </div>
          </div>
          
          <style>
            .form-fields-section {
              padding: 8px 0;
            }
            
            .form-fields-info {
              margin-bottom: 8px;
              font-size: 12px;
              color: #666;
            }
            
            .form-config-btn {
              background: #409EFF;
              color: white;
              border: none;
              padding: 6px 12px;
              border-radius: 4px;
              cursor: pointer;
              font-size: 12px;
              width: 100%;
            }
            
            .form-config-btn:hover {
              background: #337ecc;
            }
          </style>
          
          <script>
            window.openFormFieldsDialog = function() {
              // 获取当前选中的元素
              const selection = window.bpmnModeler.get('selection').get()
              const element = selection[0]
              
              if (!element) {
                alert('请先选择一个用户任务节点')
                return
              }
              
              // 获取现有的表单字段
              const fields = getElementFormFields(element)
              
              // 创建简单的配置对话框
              const fieldsJson = JSON.stringify(fields, null, 2)
              const newFieldsJson = prompt(
                '请编辑表单字段配置（JSON格式）:\\n\\n示例格式：\\n[\\n  {\\n    "id": "leaveType",\\n    "label": "请假类型",\\n    "type": "select",\\n    "required": true,\\n    "options": "年假|病假|事假"\\n  }\\n]',
                fieldsJson
              )
              
              if (newFieldsJson !== null) {
                try {
                  const newFields = JSON.parse(newFieldsJson)
                  setElementFormFields(element, newFields)
                  updateFieldsCount(newFields.length)
                  alert('表单字段配置已保存！')
                } catch (e) {
                  alert('JSON格式错误：' + e.message)
                }
              }
            }
            
            // 获取元素的表单字段
            window.getElementFormFields = function(element) {
              const businessObject = element.businessObject
              const extensionElements = businessObject.extensionElements
              
              if (!extensionElements) {
                return []
              }
              
              const formData = extensionElements.values.find(e => e.$type === 'custom:FormData')
              return formData ? (formData.fields || []) : []
            }
            
            // 设置元素的表单字段
            window.setElementFormFields = function(element, fields) {
              const modeling = window.bpmnModeler.get('modeling')
              const moddle = window.bpmnModeler.get('moddle')
              const businessObject = element.businessObject
              
              // 创建或获取扩展元素
              let extensionElements = businessObject.extensionElements
              if (!extensionElements) {
                extensionElements = moddle.create('bpmn:ExtensionElements')
              }
              
              // 查找或创建表单数据元素
              let formData = extensionElements.values.find(e => e.$type === 'custom:FormData')
              if (!formData) {
                formData = moddle.create('custom:FormData')
                extensionElements.values.push(formData)
              }
              
              // 设置字段数据
              formData.fields = fields
              
              // 更新元素
              modeling.updateProperties(element, {
                extensionElements: extensionElements
              })
            }
            
            // 更新字段数量显示
            window.updateFieldsCount = function(count) {
              const countElement = document.getElementById('fields-count')
              if (countElement) {
                countElement.textContent = '字段数量: ' + count
              }
            }
            
            // 初始化时更新字段数量
            setTimeout(() => {
              const selection = window.bpmnModeler.get('selection').get()
              if (selection.length > 0) {
                const fields = getElementFormFields(selection[0])
                updateFieldsCount(fields.length)
              }
            }, 100)
          </script>
        `,
        get: function() {
          return {}
        },
        set: function() {
          return
        }
      },
      
      // 表单字段预览
      {
        id: 'form-fields-preview',
        label: '字段预览',
        html: `
          <div class="form-fields-preview">
            <div id="fields-preview" style="font-size: 11px; color: #666; max-height: 150px; overflow-y: auto;">
              暂无字段配置
            </div>
          </div>
          
          <script>
            // 更新字段预览
            window.updateFieldsPreview = function(fields) {
              const previewElement = document.getElementById('fields-preview')
              if (!previewElement) return
              
              if (!fields || fields.length === 0) {
                previewElement.innerHTML = '暂无字段配置'
                return
              }
              
              const preview = fields.map(field => 
                \`<div style="margin-bottom: 4px; padding: 4px; background: #f5f5f5; border-radius: 2px;">
                  <strong>\${field.label || field.id}</strong> (\${field.type || 'text'})
                  \${field.required ? ' <span style="color: red;">*</span>' : ''}
                </div>\`
              ).join('')
              
              previewElement.innerHTML = preview
            }
            
            // 监听选择变化，更新预览
            setTimeout(() => {
              if (window.bpmnModeler) {
                const eventBus = window.bpmnModeler.get('eventBus')
                eventBus.on('selection.changed', (event) => {
                  const element = event.newSelection[0]
                  if (element && window.getElementFormFields) {
                    const fields = window.getElementFormFields(element)
                    window.updateFieldsPreview(fields)
                    window.updateFieldsCount(fields.length)
                  }
                })
              }
            }, 200)
          </script>
        `,
        get: function() {
          return {}
        },
        set: function() {
          return
        }
      }
    ]
  }
}