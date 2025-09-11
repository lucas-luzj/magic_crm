/**
 * 自定义属性提供器 - 增强流程变量和条件表达式的编辑功能
 */

import { is } from 'bpmn-js/lib/util/ModelUtil'

const LOW_PRIORITY = 500

export default function CustomPropertiesProvider(propertiesPanel, translate, injector) {
  this.getGroups = function(element) {
    return function(groups) {
      // 添加自定义分组
      if (is(element, 'bpmn:Process')) {
        groups.push(createProcessVariablesGroup(element, translate))
      }
      
      if (is(element, 'bpmn:SequenceFlow')) {
        groups.push(createConditionGroup(element, translate))
      }
      
      if (is(element, 'bpmn:UserTask')) {
        groups.push(createTaskVariablesGroup(element, translate))
        groups.push(createAssignmentGroup(element, translate))
      }
      
      if (is(element, 'bpmn:ServiceTask')) {
        groups.push(createServiceTaskGroup(element, translate))
      }
      
      if (is(element, 'bpmn:Gateway')) {
        groups.push(createGatewayGroup(element, translate))
      }
      
      return groups
    }
  }

  propertiesPanel.registerProvider(LOW_PRIORITY, this)
}

CustomPropertiesProvider.$inject = ['propertiesPanel', 'translate', 'injector']

// 创建流程变量分组
function createProcessVariablesGroup(element, translate) {
  return {
    id: 'process-variables',
    label: translate('流程变量'),
    entries: [
      {
        id: 'process-variables-list',
        label: translate('变量列表'),
        html: `
          <div class="process-variables-container">
            <div class="variables-header">
              <label>${translate('流程变量配置')}</label>
              <button type="button" class="add-variable-btn" onclick="addProcessVariable()">
                ${translate('添加变量')}
              </button>
            </div>
            <div class="variables-list" id="process-variables-list">
              <div class="variable-item">
                <input type="text" placeholder="${translate('变量名')}" class="variable-name" />
                <select class="variable-type">
                  <option value="string">${translate('字符串')}</option>
                  <option value="number">${translate('数字')}</option>
                  <option value="boolean">${translate('布尔值')}</option>
                  <option value="date">${translate('日期')}</option>
                </select>
                <input type="text" placeholder="${translate('默认值')}" class="variable-value" />
                <button type="button" class="remove-variable-btn" onclick="removeVariable(this)">×</button>
              </div>
            </div>
            <div class="variables-help">
              <small>${translate('提示：这些变量可在条件表达式中使用，如 ${days > 3}')}</small>
            </div>
          </div>
        `,
        get: function() {
          return {}
        },
        set: function() {}
      }
    ]
  }
}

// 创建条件表达式分组
function createConditionGroup(element, translate) {
  return {
    id: 'condition',
    label: translate('条件表达式'),
    entries: [
      {
        id: 'condition-type',
        label: translate('条件类型'),
        html: `
          <select class="condition-type-select">
            <option value="">${translate('无条件')}</option>
            <option value="expression">${translate('表达式')}</option>
            <option value="script">${translate('脚本')}</option>
          </select>
        `,
        get: function() {
          const conditionExpression = element.businessObject.conditionExpression
          return {
            conditionType: conditionExpression ? 'expression' : ''
          }
        },
        set: function(values) {
          // 设置条件类型的逻辑
        }
      },
      {
        id: 'condition-expression',
        label: translate('条件表达式'),
        html: `
          <div class="condition-container">
            <textarea class="condition-expression" placeholder="${translate('例如: ${days > 3} 或 ${approvedByManager == true}')}" rows="3"></textarea>
            <div class="condition-help">
              <small>
                ${translate('常用表达式示例：')}<br>
                • ${translate('数字比较')}: \${days > 3}<br>
                • ${translate('布尔判断')}: \${approved == true}<br>
                • ${translate('字符串比较')}: \${status == 'approved'}<br>
                • ${translate('组合条件')}: \${days > 3 && approved == true}
              </small>
            </div>
          </div>
        `,
        get: function() {
          const conditionExpression = element.businessObject.conditionExpression
          return {
            conditionExpression: conditionExpression ? conditionExpression.body : ''
          }
        },
        set: function(values) {
          const modeling = window.bpmnModeler?.get('modeling')
          if (modeling && values.conditionExpression) {
            const conditionExpression = window.bpmnModeler.get('moddle').create('bpmn:FormalExpression', {
              body: values.conditionExpression
            })
            modeling.updateProperties(element, {
              conditionExpression: conditionExpression
            })
          }
        }
      }
    ]
  }
}

// 创建任务变量分组
function createTaskVariablesGroup(element, translate) {
  return {
    id: 'task-variables',
    label: translate('任务变量'),
    entries: [
      {
        id: 'input-variables',
        label: translate('输入变量'),
        html: `
          <div class="task-variables-container">
            <div class="variables-section">
              <label>${translate('输入变量 (从流程传入任务)')}</label>
              <div class="input-variables-list">
                <div class="variable-mapping">
                  <input type="text" placeholder="${translate('源变量名')}" class="source-var" />
                  <span>→</span>
                  <input type="text" placeholder="${translate('目标变量名')}" class="target-var" />
                  <button type="button" class="add-mapping-btn">+</button>
                </div>
              </div>
            </div>
            <div class="variables-section">
              <label>${translate('输出变量 (从任务传回流程)')}</label>
              <div class="output-variables-list">
                <div class="variable-mapping">
                  <input type="text" placeholder="${translate('任务变量名')}" class="task-var" />
                  <span>→</span>
                  <input type="text" placeholder="${translate('流程变量名')}" class="process-var" />
                  <button type="button" class="add-mapping-btn">+</button>
                </div>
              </div>
            </div>
          </div>
        `,
        get: function() {
          return {}
        },
        set: function() {}
      }
    ]
  }
}

// 创建任务分配分组
function createAssignmentGroup(element, translate) {
  return {
    id: 'assignment',
    label: translate('任务分配'),
    entries: [
      {
        id: 'assignee-config',
        label: translate('受理人配置'),
        html: `
          <div class="assignment-container">
            <div class="assignment-type">
              <label>${translate('分配类型')}</label>
              <select class="assignment-type-select">
                <option value="assignee">${translate('指定受理人')}</option>
                <option value="candidateUsers">${translate('候选用户')}</option>
                <option value="candidateGroups">${translate('候选组')}</option>
                <option value="expression">${translate('表达式')}</option>
              </select>
            </div>
            <div class="assignment-value">
              <label>${translate('分配值')}</label>
              <input type="text" class="assignment-input" placeholder="${translate('例如: ${managerUserId} 或 user1,user2')}" />
              <div class="assignment-help">
                <small>
                  ${translate('表达式示例：')}<br>
                  • ${translate('变量引用')}: \${managerUserId}<br>
                  • ${translate('多个用户')}: user1,user2,user3<br>
                  • ${translate('用户组')}: managers,hr
                </small>
              </div>
            </div>
          </div>
        `,
        get: function() {
          const bo = element.businessObject
          return {
            assignee: bo.assignee || '',
            candidateUsers: bo.candidateUsers || '',
            candidateGroups: bo.candidateGroups || ''
          }
        },
        set: function(values) {
          const modeling = window.bpmnModeler?.get('modeling')
          if (modeling) {
            modeling.updateProperties(element, {
              assignee: values.assignee,
              candidateUsers: values.candidateUsers,
              candidateGroups: values.candidateGroups
            })
          }
        }
      }
    ]
  }
}

// 创建服务任务分组
function createServiceTaskGroup(element, translate) {
  return {
    id: 'service-task',
    label: translate('服务任务配置'),
    entries: [
      {
        id: 'implementation',
        label: translate('实现方式'),
        html: `
          <div class="service-task-container">
            <div class="implementation-type">
              <label>${translate('实现类型')}</label>
              <select class="implementation-type-select">
                <option value="class">${translate('Java类')}</option>
                <option value="expression">${translate('表达式')}</option>
                <option value="delegateExpression">${translate('委托表达式')}</option>
              </select>
            </div>
            <div class="implementation-value">
              <label>${translate('实现值')}</label>
              <input type="text" class="implementation-input" placeholder="${translate('例如: com.example.MyDelegate')}" />
            </div>
          </div>
        `,
        get: function() {
          const bo = element.businessObject
          return {
            'class': bo['class'] || '',
            expression: bo.expression || '',
            delegateExpression: bo.delegateExpression || ''
          }
        },
        set: function(values) {
          const modeling = window.bpmnModeler?.get('modeling')
          if (modeling) {
            modeling.updateProperties(element, values)
          }
        }
      }
    ]
  }
}

// 创建网关分组
function createGatewayGroup(element, translate) {
  return {
    id: 'gateway',
    label: translate('网关配置'),
    entries: [
      {
        id: 'gateway-info',
        label: translate('网关说明'),
        html: `
          <div class="gateway-container">
            <div class="gateway-help">
              <h4>${translate('网关类型说明：')}</h4>
              <ul>
                <li><strong>${translate('排他网关')}</strong>: ${translate('只能选择一个分支执行')}</li>
                <li><strong>${translate('并行网关')}</strong>: ${translate('所有分支并行执行')}</li>
                <li><strong>${translate('包容网关')}</strong>: ${translate('可以选择多个分支执行')}</li>
              </ul>
              <p><strong>${translate('注意：')}</strong>${translate('请在输出连线上设置条件表达式来控制分支选择')}</p>
            </div>
          </div>
        `,
        get: function() {
          return {}
        },
        set: function() {}
      }
    ]
  }
}

// 全局函数，供HTML中调用
window.addProcessVariable = function() {
  const container = document.getElementById('process-variables-list')
  const newItem = document.createElement('div')
  newItem.className = 'variable-item'
  newItem.innerHTML = `
    <input type="text" placeholder="变量名" class="variable-name" />
    <select class="variable-type">
      <option value="string">字符串</option>
      <option value="number">数字</option>
      <option value="boolean">布尔值</option>
      <option value="date">日期</option>
    </select>
    <input type="text" placeholder="默认值" class="variable-value" />
    <button type="button" class="remove-variable-btn" onclick="removeVariable(this)">×</button>
  `
  container.appendChild(newItem)
}

window.removeVariable = function(button) {
  button.parentElement.remove()
}

export const customPropertiesProviderModule = {
  __init__: ['customPropertiesProvider'],
  customPropertiesProvider: ['type', CustomPropertiesProvider]
}