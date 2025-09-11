import { is } from 'bpmn-js/lib/util/ModelUtil'

/**
 * 表单属性提供器
 * 为用户任务节点提供表单配置功能
 */
export default function FormPropertiesProvider(propertiesPanel, injector) {
  this._propertiesPanel = propertiesPanel
  this._injector = injector
  
  propertiesPanel.registerProvider(this)
}

FormPropertiesProvider.$inject = ['propertiesPanel', 'injector']

/**
 * 获取属性组
 */
FormPropertiesProvider.prototype.getGroups = function(element) {
  return (groups) => {
    // 只为用户任务添加表单配置
    if (is(element, 'bpmn:UserTask')) {
      groups.push(createFormGroup(element, this._injector))
    }
    return groups
  }
}

/**
 * 创建表单配置组
 */
function createFormGroup(element, injector) {
  const modeling = injector.get('modeling')
  const moddle = injector.get('moddle')
  
  return {
    id: 'form-fields',
    label: '表单配置',
    entries: [
      {
        id: 'form-fields-list',
        label: '表单字段',
        html: createFormFieldsHTML(element, modeling, moddle),
        get: function(element) {
          return getFormFields(element)
        },
        set: function(element, values) {
          setFormFields(element, values, modeling, moddle)
        }
      }
    ]
  }
}

/**
 * 创建表单字段HTML
 */
function createFormFieldsHTML(element, modeling, moddle) {
  const formFields = getFormFields(element)
  
  return `
    <div class="form-fields-container">
      <div class="form-fields-header">
        <button type="button" class="add-field-btn" onclick="addFormField()">添加字段</button>
      </div>
      <div class="form-fields-list" id="form-fields-list">
        ${renderFormFields(formFields)}
      </div>
    </div>
    
    <style>
      .form-fields-container {
        padding: 10px 0;
      }
      
      .form-fields-header {
        margin-bottom: 10px;
      }
      
      .add-field-btn {
        background: #409EFF;
        color: white;
        border: none;
        padding: 6px 12px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
      }
      
      .add-field-btn:hover {
        background: #337ecc;
      }
      
      .form-field-item {
        border: 1px solid #ddd;
        border-radius: 4px;
        padding: 10px;
        margin-bottom: 10px;
        background: #f9f9f9;
      }
      
      .form-field-row {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
      }
      
      .form-field-row:last-child {
        margin-bottom: 0;
      }
      
      .form-field-label {
        width: 60px;
        font-size: 12px;
        color: #666;
      }
      
      .form-field-input {
        flex: 1;
        padding: 4px 8px;
        border: 1px solid #ddd;
        border-radius: 3px;
        font-size: 12px;
      }
      
      .form-field-select {
        flex: 1;
        padding: 4px 8px;
        border: 1px solid #ddd;
        border-radius: 3px;
        font-size: 12px;
      }
      
      .form-field-checkbox {
        margin-left: 8px;
      }
      
      .remove-field-btn {
        background: #f56c6c;
        color: white;
        border: none;
        padding: 4px 8px;
        border-radius: 3px;
        cursor: pointer;
        font-size: 11px;
        margin-left: 8px;
      }
      
      .remove-field-btn:hover {
        background: #e85656;
      }
    </style>
    
    <script>
      // 全局函数，用于添加字段
      window.addFormField = function() {
        const container = document.getElementById('form-fields-list');
        const fieldHtml = createFieldHTML({
          id: 'field_' + Date.now(),
          label: '新字段',
          type: 'text',
          required: false,
          placeholder: ''
        });
        container.insertAdjacentHTML('beforeend', fieldHtml);
      }
      
      // 创建单个字段HTML
      window.createFieldHTML = function(field) {
        return \`
          <div class="form-field-item" data-field-id="\${field.id}">
            <div class="form-field-row">
              <span class="form-field-label">字段ID:</span>
              <input type="text" class="form-field-input" value="\${field.id}" onchange="updateField(this, 'id')">
              <button type="button" class="remove-field-btn" onclick="removeField(this)">删除</button>
            </div>
            <div class="form-field-row">
              <span class="form-field-label">标签:</span>
              <input type="text" class="form-field-input" value="\${field.label}" onchange="updateField(this, 'label')">
            </div>
            <div class="form-field-row">
              <span class="form-field-label">类型:</span>
              <select class="form-field-select" onchange="updateField(this, 'type')">
                <option value="text" \${field.type === 'text' ? 'selected' : ''}>文本</option>
                <option value="textarea" \${field.type === 'textarea' ? 'selected' : ''}>多行文本</option>
                <option value="number" \${field.type === 'number' ? 'selected' : ''}>数字</option>
                <option value="date" \${field.type === 'date' ? 'selected' : ''}>日期</option>
                <option value="select" \${field.type === 'select' ? 'selected' : ''}>下拉框</option>
                <option value="checkbox" \${field.type === 'checkbox' ? 'selected' : ''}>复选框</option>
                <option value="file" \${field.type === 'file' ? 'selected' : ''}>文件</option>
              </select>
            </div>
            <div class="form-field-row">
              <span class="form-field-label">占位符:</span>
              <input type="text" class="form-field-input" value="\${field.placeholder || ''}" onchange="updateField(this, 'placeholder')">
            </div>
            <div class="form-field-row">
              <span class="form-field-label">必填:</span>
              <input type="checkbox" class="form-field-checkbox" \${field.required ? 'checked' : ''} onchange="updateField(this, 'required')">
            </div>
          </div>
        \`;
      }
      
      // 删除字段
      window.removeField = function(btn) {
        const fieldItem = btn.closest('.form-field-item');
        fieldItem.remove();
      }
      
      // 更新字段
      window.updateField = function(input, property) {
        console.log('更新字段属性:', property, input.value);
        // 这里可以添加实际的更新逻辑
      }
    </script>
  `
}

/**
 * 渲染表单字段列表
 */
function renderFormFields(fields) {
  if (!fields || fields.length === 0) {
    return '<div style="color: #999; font-size: 12px; text-align: center; padding: 20px;">暂无表单字段，点击"添加字段"开始配置</div>'
  }
  
  return fields.map(field => {
    return window.createFieldHTML ? window.createFieldHTML(field) : `
      <div class="form-field-item">
        <div>字段ID: ${field.id}</div>
        <div>标签: ${field.label}</div>
        <div>类型: ${field.type}</div>
      </div>
    `
  }).join('')
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
  
  const formData = extensionElements.values.find(e => e.$type === 'custom:FormData')
  if (!formData) {
    return []
  }
  
  return formData.fields || []
}

/**
 * 设置表单字段配置
 */
function setFormFields(element, fields, modeling, moddle) {
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