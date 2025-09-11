import { is } from 'bpmn-js/lib/util/ModelUtil'

/**
 * 测试版表单属性提供器
 * 简化版本，用于验证基本功能
 */
export default function TestFormProvider(propertiesPanel, translate) {
  
  this.getGroups = function(element) {
    return function(groups) {
      // 只为用户任务添加表单配置
      if (is(element, 'bpmn:UserTask')) {
        groups.push({
          id: 'test-form-properties',
          label: '表单配置 (测试)',
          entries: [
            {
              id: 'test-form-key',
              label: '表单标识',
              html: '<input type="text" name="formKey" placeholder="请输入表单标识" />',
              get: function(element) {
                return {
                  formKey: element.businessObject.formKey || ''
                }
              },
              set: function(element, values) {
                const modeling = window.bpmnModeler.get('modeling')
                return modeling.updateProperties(element, {
                  formKey: values.formKey || undefined
                })
              }
            },
            {
              id: 'test-form-config',
              label: '表单配置',
              html: `
                <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                  <p style="margin: 0 0 10px 0; font-size: 12px; color: #666;">
                    表单配置功能已集成到BPMN属性面板中！
                  </p>
                  <button type="button" onclick="alert('表单配置功能正常工作！')" 
                          style="padding: 6px 12px; background: #409eff; color: white; border: none; border-radius: 4px; cursor: pointer;">
                    测试按钮
                  </button>
                </div>
              `,
              get: function() {
                return {}
              },
              set: function() {
                return
              }
            }
          ]
        })
      }
      return groups
    }
  }

  propertiesPanel.registerProvider(this)
}

TestFormProvider.$inject = ['propertiesPanel', 'translate']