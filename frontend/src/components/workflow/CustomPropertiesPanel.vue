<template>
  <div class="custom-properties-panel">
    <div class="panel-header">
      <h3>属性面板</h3>
    </div>
    
    <!-- 流程级别属性 (当未选择元素或选择了流程本身时显示) -->
    <div class="panel-content" v-if="!selectedElement || isProcessElement">
      <!-- 流程基本信息 -->
      <div class="property-group">
        <div class="group-header" @click="toggleSection('processInfo')">
          <span>流程信息</span>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.processInfo }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.processInfo">
          <div class="property-item">
            <label>流程ID</label>
            <el-input 
              v-model="processId" 
              @blur="updateProcessId"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>流程名称</label>
            <el-input 
              v-model="processName" 
              @blur="updateProcessName"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>流程描述</label>
            <el-input 
              v-model="processDescription"
              type="textarea"
              :rows="2"
              @blur="updateProcessDescription"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>是否可执行</label>
            <el-switch 
              v-model="isExecutable"
              @change="updateIsExecutable"
            />
          </div>
        </div>
      </div>

      <!-- 流程变量集中管理 -->
      <div class="property-group">
        <div class="group-header" @click="toggleSection('processVariables')">
          <span>流程变量管理</span>
          <div class="header-actions" @click.stop>
            <el-button size="small" type="primary" @click="addVariable">
              <el-icon><Plus /></el-icon>
              添加变量
            </el-button>
            <el-button size="small" @click="collectVariablesFromImport">
              <el-icon><DocumentAdd /></el-icon>
              收集变量
            </el-button>
          </div>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.processVariables }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.processVariables">
          <div v-if="processVariables.length === 0" class="empty-state">
            <el-icon><DocumentAdd /></el-icon>
            <p>暂无流程变量，点击上方按钮添加</p>
          </div>
          <div v-else class="variables-list">
            <div 
              v-for="(variable, index) in processVariables" 
              :key="index"
              class="variable-item"
            >
              <div class="variable-header" @click="toggleVariableExpansion(index)">
                <div class="variable-info">
                  <span class="variable-name">{{ variable.name }}</span>
                  <el-tag :type="getVariableTypeColor(variable.type)" size="small">
                    {{ getVariableTypeLabel(variable.type) }}
                  </el-tag>
                </div>
                <div class="variable-actions">
                  <el-button size="small" @click.stop="editVariable(index)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button size="small" type="danger" @click.stop="removeVariable(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="variable-details" v-if="variable.expanded">
                <div class="variable-form">
                  <el-input 
                    v-model="variable.name" 
                    placeholder="变量名 (驼峰命名)"
                    size="small"
                    @blur="updateVariables"
                  />
                  <el-select 
                    v-model="variable.type" 
                    placeholder="变量类型"
                    size="small"
                    @change="updateVariables"
                  >
                    <el-option label="字符串 (string)" value="string" />
                    <el-option label="布尔值 (boolean)" value="boolean" />
                    <el-option label="整数 (integer)" value="integer" />
                    <el-option label="浮点数 (double)" value="double" />
                    <el-option label="日期 (date)" value="date" />
                    <el-option label="JSON对象 (json)" value="json" />
                  </el-select>
                  <el-input 
                    v-model="variable.defaultValue" 
                    placeholder="默认值 (可选)"
                    size="small"
                    @blur="updateVariables"
                  />
                  <el-input 
                    v-model="variable.description"
                    type="textarea"
                    :rows="2"
                    placeholder="变量描述和用途说明"
                    size="small"
                    @blur="updateVariables"
                  />
                </div>
              </div>
              <div class="variable-usage" v-if="variable.usage && variable.usage.length > 0">
                <div class="usage-header">使用位置:</div>
                <div class="usage-list">
                  <el-tag 
                    v-for="usage in variable.usage" 
                    :key="usage.elementId"
                    size="small"
                    @click="highlightElement(usage.elementId)"
                    class="usage-tag"
                  >
                    {{ usage.elementName || usage.elementId }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 流程统计信息 -->
      <div class="property-group">
        <div class="group-header">
          <span>流程统计</span>
        </div>
        <div class="group-content">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">用户任务</div>
              <div class="stat-value">{{ processStats.userTasks }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">网关</div>
              <div class="stat-value">{{ processStats.gateways }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">事件</div>
              <div class="stat-value">{{ processStats.events }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">变量数</div>
              <div class="stat-value">{{ processVariables.length }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 元素级别属性 (当选择了具体元素时显示) -->
    <div class="panel-content" v-else-if="selectedElement">
      <!-- 基本信息 -->
      <div class="property-group">
        <div class="group-header" @click="toggleSection('elementInfo')">
          <span>基本信息</span>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.elementInfo }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.elementInfo">
          <div class="property-item">
            <label>元素ID</label>
            <el-input 
              v-model="elementId" 
              @blur="updateElementId"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>元素名称</label>
            <el-input 
              v-model="elementName" 
              @blur="updateElementName"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>元素类型</label>
            <el-input 
              :value="elementType" 
              readonly
              size="small"
            />
          </div>
        </div>
      </div>

      <!-- 条件表达式 (仅对连线显示) -->
      <div class="property-group" v-if="isSequenceFlow">
        <div class="group-header" @click="toggleSection('elementProperties')">
          <span>条件表达式</span>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.elementProperties }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.elementProperties">
          <div class="property-item">
            <label>条件</label>
            <el-input 
              v-model="conditionExpression"
              type="textarea"
              :rows="3"
              placeholder="例如: ${approved == true}"
              @blur="updateConditionExpression"
              size="small"
            />
          </div>
          <div class="expression-templates">
            <div class="template-header">常用表达式模板:</div>
            <div class="template-buttons">
              <el-button size="small" @click="insertTemplate('${approved == true}')">审批通过</el-button>
              <el-button size="small" @click="insertTemplate('${days <= 3}')">天数≤3</el-button>
              <el-button size="small" @click="insertTemplate('${amount > 1000}')">金额>1000</el-button>
            </div>
          </div>
          <div class="variable-references" v-if="processVariables.length > 0">
            <div class="ref-header">可用变量:</div>
            <div class="ref-list">
              <el-tag 
                v-for="variable in processVariables" 
                :key="variable.name"
                size="small"
                @click="insertVariableReference(variable.name)"
                class="variable-ref-tag"
              >
                ${{'{'}}{{ variable.name }}{{'}'}}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 任务分配 (仅对用户任务显示) -->
      <div class="property-group" v-if="isUserTask">
        <div class="group-header" @click="toggleSection('elementProperties')">
          <span>任务分配</span>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.elementProperties }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.elementProperties">
          <div class="property-item">
            <label>受理人</label>
            <el-input 
              v-model="assignee"
              placeholder="例如: ${managerUserId}"
              @blur="updateAssignee"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>候选人</label>
            <el-input 
              v-model="candidateUsers"
              placeholder="例如: user1,user2,user3"
              @blur="updateCandidateUsers"
              size="small"
            />
          </div>
          <div class="property-item">
            <label>候选组</label>
            <el-input 
              v-model="candidateGroups"
              placeholder="例如: managers,hr-team"
              @blur="updateCandidateGroups"
              size="small"
            />
          </div>
        </div>
      </div>

      <!-- 表单配置 (仅对用户任务显示) -->
      <div class="property-group" v-if="isUserTask">
        <div class="group-header">
          <span>表单配置</span>
        </div>
        <div class="group-content">
          <div class="property-item">
            <label>表单键</label>
            <el-input 
              v-model="formKey"
              placeholder="例如: leave_form"
              @blur="updateFormKey"
              size="small"
            />
            <!-- 表单绑定状态显示 -->
            <div v-if="formKey" class="form-binding-status">
              <el-tag type="success" size="small" style="margin-top: 8px;">
                <el-icon><Check /></el-icon>
                已绑定表单: {{ formKey }}
              </el-tag>
              <el-button 
                type="primary" 
                link 
                size="small" 
                @click="openFormConfig"
                style="margin-left: 8px;"
              >
                重新配置
              </el-button>
            </div>
            <div v-else class="form-binding-status">
              <el-tag type="info" size="small" style="margin-top: 8px;">
                <el-icon><Warning /></el-icon>
                未绑定表单
              </el-tag>
              <el-button 
                type="primary" 
                link 
                size="small" 
                @click="openFormConfig"
                style="margin-left: 8px;"
              >
                配置表单
              </el-button>
            </div>
          </div>
          <div class="property-item">
            <label>表单类型</label>
            <el-select 
              v-model="formType"
              placeholder="选择表单类型"
              @change="updateFormType"
              size="small"
              style="width: 100%"
            >
              <el-option label="内置表单" value="embedded" />
              <el-option label="外部表单" value="external" />
              <el-option label="生成表单" value="generated" />
              <el-option label="自定义表单" value="custom" />
            </el-select>
          </div>
        </div>
      </div>

      <!-- 变量使用情况 -->
      <div class="property-group" v-if="elementVariableUsage.length > 0">
        <div class="group-header" @click="toggleSection('variableUsage')">
          <span>使用的变量</span>
          <el-icon class="collapse-icon" :class="{ 'collapsed': collapsedSections.variableUsage }">
            <ArrowDown />
          </el-icon>
        </div>
        <div class="group-content" v-show="!collapsedSections.variableUsage">
          <div class="variable-usage-list">
            <div 
              v-for="varUsage in elementVariableUsage" 
              :key="varUsage.name"
              class="usage-item"
            >
              <div class="usage-info">
                <span class="usage-var-name">${{'{'}}{{ varUsage.name }}{{'}'}}}</span>
                <el-tag :type="getVariableTypeColor(varUsage.type)" size="small">
                  {{ getVariableTypeLabel(varUsage.type) }}
                </el-tag>
              </div>
              <div class="usage-context">{{ varUsage.context }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 变量编辑对话框 -->
    <el-dialog v-model="variableDialogVisible" title="编辑变量" width="500px">
      <el-form :model="editingVariable" label-width="100px">
        <el-form-item label="变量名" required>
          <el-input 
            v-model="editingVariable.name" 
            placeholder="支持中文、英文、数字、下划线"
            @input="validateVariableNameInput"
          />
          <div class="form-item-tip">
            <el-icon><InfoFilled /></el-icon>
            变量名支持中文、英文、数字、下划线，不能以数字开头，不能包含空格或特殊符号
          </div>
          <div v-if="variableNameError" class="form-item-error">
            {{ variableNameError }}
          </div>
        </el-form-item>
        <el-form-item label="变量类型" required>
          <el-select v-model="editingVariable.type" style="width: 100%;">
            <el-option label="字符串 (string)" value="string" />
            <el-option label="布尔值 (boolean)" value="boolean" />
            <el-option label="整数 (integer)" value="integer" />
            <el-option label="浮点数 (double)" value="double" />
            <el-option label="日期 (date)" value="date" />
            <el-option label="JSON对象 (json)" value="json" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认值">
          <el-input v-model="editingVariable.defaultValue" placeholder="可选" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="editingVariable.description"
            type="textarea"
            :rows="3"
            placeholder="描述变量的用途和使用场景"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="variableDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveVariable">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, toRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, DocumentAdd, InfoFilled, ArrowDown } from '@element-plus/icons-vue'
import {
  extractVariablesFromExpression,
  validateVariableName,
  updateVariableReferences,
  isVariableNameDuplicate
} from '@/utils/workflowVariableUtils'

const props = defineProps({
  selectedElement: {
    type: Object,
    default: null
  },
  bpmnModeler: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['element-updated'])

// 响应式数据
const processVariables = ref([])
const variableDialogVisible = ref(false)
const editingVariable = ref({})
const editingVariableIndex = ref(-1)
const variableNameError = ref('')

// 折叠展开状态管理
const collapsedSections = ref({
  processInfo: false,
  processVariables: false,
  elementInfo: false,
  elementProperties: false,
  variableUsage: false
})

// 流程级别属性
const processId = ref('')
const processName = ref('')
const processDescription = ref('')
const isExecutable = ref(true)

// 元素级别属性
const elementId = ref('')
const elementName = ref('')
const conditionExpression = ref('')
const assignee = ref('')
const candidateUsers = ref('')
const candidateGroups = ref('')
const formKey = ref('')
const formType = ref('embedded')

// 计算属性
const isProcessElement = computed(() => {
  return props.selectedElement && props.selectedElement.type === 'bpmn:Process'
})

const elementType = computed(() => {
  if (!props.selectedElement) return ''
  const type = props.selectedElement.businessObject?.$type || props.selectedElement.type
  return getElementTypeLabel(type)
})

const isSequenceFlow = computed(() => {
  return props.selectedElement && 
         (props.selectedElement.businessObject?.$type === 'bpmn:SequenceFlow' ||
          props.selectedElement.type === 'bpmn:SequenceFlow')
})

const isUserTask = computed(() => {
  return props.selectedElement && 
         (props.selectedElement.businessObject?.$type === 'bpmn:UserTask' ||
          props.selectedElement.type === 'bpmn:UserTask')
})

const processStats = computed(() => {
  if (!props.bpmnModeler) {
    return { userTasks: 0, gateways: 0, events: 0 }
  }

  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const elements = elementRegistry.getAll()
    
    let userTasks = 0
    let gateways = 0
    let events = 0
    
    elements.forEach(element => {
      const type = element.businessObject?.$type || element.type
      if (type?.includes('UserTask')) userTasks++
      else if (type?.includes('Gateway')) gateways++
      else if (type?.includes('Event')) events++
    })
    
    return { userTasks, gateways, events }
  } catch (error) {
    return { userTasks: 0, gateways: 0, events: 0 }
  }
})

const elementVariableUsage = computed(() => {
  if (!props.selectedElement) return []
  
  const usage = []
  const element = props.selectedElement
  
  // 检查条件表达式中的变量使用
  if (conditionExpression.value) {
    const variables = extractVariablesFromExpression(conditionExpression.value)
    variables.forEach(varName => {
      const variable = processVariables.value.find(v => v.name === varName)
      if (variable) {
        usage.push({
          name: varName,
          type: variable.type,
          context: '条件表达式'
        })
      }
    })
  }
  
  // 检查任务分配中的变量使用
  if (assignee.value) {
    const variables = extractVariablesFromExpression(assignee.value)
    variables.forEach(varName => {
      const variable = processVariables.value.find(v => v.name === varName)
      if (variable) {
        usage.push({
          name: varName,
          type: variable.type,
          context: '任务受理人'
        })
      }
    })
  }
  
  return usage
})

// 方法定义
const loadProcessProperties = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const processElement = elementRegistry.find(element => 
      element.businessObject?.$type === 'bpmn:Process'
    )
    
    if (processElement) {
      const businessObject = processElement.businessObject
      processId.value = businessObject.id || ''
      processName.value = businessObject.name || ''
      processDescription.value = businessObject.documentation?.[0]?.text || ''
      isExecutable.value = businessObject.isExecutable !== false
    }
  } catch (error) {
    console.warn('加载流程属性失败:', error)
  }
}

const scanProcessVariables = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const elements = elementRegistry.getAll()
    const foundVariables = new Set()
    
    elements.forEach(element => {
      const businessObject = element.businessObject
      if (!businessObject) return
      
      // 扫描条件表达式
      if (businessObject.conditionExpression?.body) {
        const variables = extractVariablesFromExpression(businessObject.conditionExpression.body)
        variables.forEach(varName => foundVariables.add(varName))
      }
      
      // 扫描任务分配
      if (businessObject.assignee) {
        const variables = extractVariablesFromExpression(businessObject.assignee)
        variables.forEach(varName => foundVariables.add(varName))
      }
    })
    
    // 更新变量使用情况
    processVariables.value.forEach(variable => {
      variable.usage = []
      if (foundVariables.has(variable.name)) {
        // 查找使用该变量的元素
        elements.forEach(element => {
          const businessObject = element.businessObject
          if (!businessObject) return
          
          let isUsed = false
          let context = ''
          
          if (businessObject.conditionExpression?.body?.includes(`\${${variable.name}}`)) {
            isUsed = true
            context = '条件表达式'
          } else if (businessObject.assignee?.includes(`\${${variable.name}}`)) {
            isUsed = true
            context = '任务分配'
          }
          
          if (isUsed) {
            variable.usage.push({
              elementId: element.id,
              elementName: businessObject.name || element.id,
              context
            })
          }
        })
      }
    })
  } catch (error) {
    console.warn('扫描流程变量失败:', error)
  }
}


const addVariable = () => {
  editingVariable.value = {
    name: '',
    type: 'string',
    defaultValue: '',
    description: '',
    expanded: true
  }
  editingVariableIndex.value = -1
  variableDialogVisible.value = true
}

const editVariable = (index) => {
  editingVariable.value = { ...processVariables.value[index] }
  editingVariableIndex.value = index
  variableDialogVisible.value = true
}

// 验证变量名称是否合法
// 实时验证变量名输入
const validateVariableNameInput = () => {
  variableNameError.value = !editingVariable.value.name ? '请输入变量名' : ''
}


// 转义正则表达式特殊字符
const escapeRegExp = (string) => {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

const saveVariable = () => {
  // 验证变量名
  const nameError = validateVariableName(editingVariable.value.name)
  if (nameError) {
    ElMessage.error(nameError)
    return
  }
  
  // 检查变量名是否重复
  const existingIndex = processVariables.value.findIndex(v => v.name === editingVariable.value.name)
  if (existingIndex !== -1 && existingIndex !== editingVariableIndex.value) {
    ElMessage.error('变量名已存在')
    return
  }
  
  // 如果是编辑变量且变量名发生了变化，需要同步更新流程中的引用
  let oldVariableName = null
  if (editingVariableIndex.value !== -1) {
    oldVariableName = processVariables.value[editingVariableIndex.value].name
  }
  
  if (editingVariableIndex.value === -1) {
    // 新增变量
    processVariables.value.push({ ...editingVariable.value })
  } else {
    // 编辑变量
    processVariables.value[editingVariableIndex.value] = { ...editingVariable.value }
    
    // 如果变量名发生了变化，同步更新流程中的引用
    if (oldVariableName && oldVariableName !== editingVariable.value.name) {
      updateVariableReferences(oldVariableName, editingVariable.value.name)
    }
  }
  
  variableDialogVisible.value = false
  updateVariables()
  ElMessage.success('变量保存成功')
}

const removeVariable = (index) => {
  processVariables.value.splice(index, 1)
  updateVariables()
  ElMessage.success('变量删除成功')
}

const toggleVariableExpansion = (index) => {
  processVariables.value[index].expanded = !processVariables.value[index].expanded
}

const updateVariables = () => {
  // 这里可以将变量信息保存到BPMN模型中
  nextTick(() => {
    scanProcessVariables()
  })
}

const insertTemplate = (template) => {
  conditionExpression.value = template
  updateConditionExpression()
}

const insertVariableReference = (variableName) => {
  const reference = `\${${variableName}}`
  if (conditionExpression.value) {
    conditionExpression.value += ` && ${reference}`
  } else {
    conditionExpression.value = reference
  }
  updateConditionExpression()
}

const highlightElement = (elementId) => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const selection = props.bpmnModeler.get('selection')
    const element = elementRegistry.get(elementId)
    
    if (element) {
      selection.select(element)
    }
  } catch (error) {
    console.warn('高亮元素失败:', error)
  }
}

// 更新方法
const updateProcessId = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const modeling = props.bpmnModeler.get('modeling')
    const processElement = elementRegistry.find(element => 
      element.businessObject?.$type === 'bpmn:Process'
    )
    
    if (processElement) {
      modeling.updateProperties(processElement, { id: processId.value })
      emit('element-updated', processElement)
    }
  } catch (error) {
    console.warn('更新流程ID失败:', error)
  }
}

const updateProcessName = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const modeling = props.bpmnModeler.get('modeling')
    const processElement = elementRegistry.find(element => 
      element.businessObject?.$type === 'bpmn:Process'
    )
    
    if (processElement) {
      modeling.updateProperties(processElement, { name: processName.value })
      emit('element-updated', processElement)
    }
  } catch (error) {
    console.warn('更新流程名称失败:', error)
  }
}

const updateProcessDescription = () => {
  // 实现流程描述更新逻辑
}

const updateIsExecutable = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const modeling = props.bpmnModeler.get('modeling')
    const processElement = elementRegistry.find(element => 
      element.businessObject?.$type === 'bpmn:Process'
    )
    
    if (processElement) {
      modeling.updateProperties(processElement, { isExecutable: isExecutable.value })
      emit('element-updated', processElement)
    }
  } catch (error) {
    console.warn('更新可执行状态失败:', error)
  }
}

const updateElementId = () => {
  if (!props.selectedElement || !props.bpmnModeler) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    modeling.updateProperties(props.selectedElement, { id: elementId.value })
    emit('element-updated', props.selectedElement)
  } catch (error) {
    console.warn('更新元素ID失败:', error)
  }
}

const updateElementName = () => {
  if (!props.selectedElement || !props.bpmnModeler) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    modeling.updateProperties(props.selectedElement, { name: elementName.value })
    emit('element-updated', props.selectedElement)
  } catch (error) {
    console.warn('更新元素名称失败:', error)
  }
}

const updateConditionExpression = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isSequenceFlow.value) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    const bpmnFactory = props.bpmnModeler.get('bpmnFactory')
    
    let conditionExpressionObj = null
    if (conditionExpression.value) {
      conditionExpressionObj = bpmnFactory.create('bpmn:FormalExpression', {
        body: conditionExpression.value
      })
    }
    
    modeling.updateProperties(props.selectedElement, {
      conditionExpression: conditionExpressionObj
    })
    
    emit('element-updated', props.selectedElement)
    scanProcessVariables()
  } catch (error) {
    console.warn('更新条件表达式失败:', error)
  }
}

const updateAssignee = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isUserTask.value) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    
    // 使用toRaw来避免Vue 3响应式代理问题
    const element = toRaw(props.selectedElement)
    const assigneeValue = toRaw(assignee.value)
    
    // 创建一个普通对象来避免代理问题
    const properties = {
      'flowable:assignee': assigneeValue || undefined,
      assignee: assigneeValue || undefined
    }
    
    // 清理undefined值
    Object.keys(properties).forEach(key => {
      if (properties[key] === undefined) {
        delete properties[key]
      }
    })
    
    console.log('更新受理人:', { element: element.id, properties })
    
    modeling.updateProperties(element, properties)
    emit('element-updated', props.selectedElement)
    scanProcessVariables()
  } catch (error) {
    console.error('更新受理人失败:', error)
    ElMessage.error('更新受理人失败: ' + error.message)
  }
}

const updateCandidateUsers = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isUserTask.value) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    const element = toRaw(props.selectedElement)
    const candidateUsersValue = toRaw(candidateUsers.value)
    
    const properties = {
      'flowable:candidateUsers': candidateUsersValue || undefined,
      candidateUsers: candidateUsersValue || undefined
    }
    
    Object.keys(properties).forEach(key => {
      if (properties[key] === undefined) {
        delete properties[key]
      }
    })
    
    console.log('更新候选人:', { element: element.id, properties })
    
    modeling.updateProperties(element, properties)
    emit('element-updated', props.selectedElement)
  } catch (error) {
    console.error('更新候选人失败:', error)
    ElMessage.error('更新候选人失败: ' + error.message)
  }
}

const updateCandidateGroups = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isUserTask.value) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    const element = toRaw(props.selectedElement)
    const candidateGroupsValue = toRaw(candidateGroups.value)
    
    const properties = {
      'flowable:candidateGroups': candidateGroupsValue || undefined,
      candidateGroups: candidateGroupsValue || undefined
    }
    
    Object.keys(properties).forEach(key => {
      if (properties[key] === undefined) {
        delete properties[key]
      }
    })
    
    console.log('更新候选组:', { element: element.id, properties })
    
    modeling.updateProperties(element, properties)
    emit('element-updated', props.selectedElement)
  } catch (error) {
    console.error('更新候选组失败:', error)
    ElMessage.error('更新候选组失败: ' + error.message)
  }
}

const updateFormKey = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isUserTask.value) return
  
  const modeling = props.bpmnModeler.get('modeling')
  modeling.updateProperties(props.selectedElement, {
    formKey: formKey.value
  })
  
  emit('element-updated', props.selectedElement)
}

const updateFormType = () => {
  if (!props.selectedElement || !props.bpmnModeler || !isUserTask.value) return
  
  try {
    const modeling = props.bpmnModeler.get('modeling')
    modeling.updateProperties(props.selectedElement, { formKey: formKey.value })
    emit('element-updated', props.selectedElement)
  } catch (error) {
    console.warn('更新表单键失败:', error)
  }
}

// 工具方法
const getElementTypeLabel = (type) => {
  const typeMap = {
    'bpmn:StartEvent': '开始事件',
    'bpmn:EndEvent': '结束事件',
    'bpmn:UserTask': '用户任务',
    'bpmn:ServiceTask': '服务任务',
    'bpmn:ExclusiveGateway': '排他网关',
    'bpmn:ParallelGateway': '并行网关',
    'bpmn:SequenceFlow': '连线',
    'bpmn:Process': '流程'
  }
  return typeMap[type] || type
}

const getVariableTypeLabel = (type) => {
  const typeMap = {
    'string': '字符串',
    'boolean': '布尔值',
    'integer': '整数',
    'double': '浮点数',
    'date': '日期',
    'json': 'JSON'
  }
  return typeMap[type] || type
}

const getVariableTypeColor = (type) => {
  const colorMap = {
    'string': '',
    'boolean': 'success',
    'integer': 'warning',
    'double': 'warning',
    'date': 'info',
    'json': 'danger'
  }
  return colorMap[type] || ''
}

// 折叠展开控制方法
const toggleSection = (sectionName) => {
  collapsedSections.value[sectionName] = !collapsedSections.value[sectionName]
}

// 导入流程时收集变量的方法
const collectVariablesFromImport = () => {
  if (!props.bpmnModeler) return
  
  try {
    const elementRegistry = props.bpmnModeler.get('elementRegistry')
    const elements = elementRegistry.getAll()
    const foundVariables = new Set()
    
    elements.forEach(element => {
      const businessObject = element.businessObject
      if (!businessObject) return
      
      // 从条件表达式中提取变量
      if (businessObject.conditionExpression?.body) {
        const variables = extractVariablesFromExpression(businessObject.conditionExpression.body)
        variables.forEach(varName => foundVariables.add(varName))
      }
      
      // 从任务分配中提取变量
      if (businessObject.assignee) {
        const variables = extractVariablesFromExpression(businessObject.assignee)
        variables.forEach(varName => foundVariables.add(varName))
      }
      
      // 从候选用户中提取变量
      if (businessObject.candidateUsers) {
        const variables = extractVariablesFromExpression(businessObject.candidateUsers)
        variables.forEach(varName => foundVariables.add(varName))
      }
      
      // 从候选组中提取变量
      if (businessObject.candidateGroups) {
        const variables = extractVariablesFromExpression(businessObject.candidateGroups)
        variables.forEach(varName => foundVariables.add(varName))
      }
    })
    
    // 将发现的变量添加到流程变量列表中（如果不存在）
    foundVariables.forEach(varName => {
      const exists = processVariables.value.some(v => v.name === varName)
      if (!exists) {
        processVariables.value.push({
          name: varName,
          type: 'string', // 默认类型
          defaultValue: '',
          description: `从流程导入时自动发现的变量`,
          expanded: false,
          usage: []
        })
      }
    })
    
    if (foundVariables.size > 0) {
      ElMessage.success(`已自动收集到 ${foundVariables.size} 个流程变量`)
      scanProcessVariables() // 重新扫描变量使用情况
    }
  } catch (error) {
    console.warn('收集流程变量失败:', error)
  }
}

// 保存流程变量到数据库的方法
const saveProcessVariablesToDB = async () => {
  if (!processVariables.value.length) return
  
  try {
    const variablesData = processVariables.value.map(variable => ({
      processId: processId.value,
      name: variable.name,
      type: variable.type,
      defaultValue: variable.defaultValue,
      description: variable.description
    }))
    
    // 这里需要调用后端API保存变量信息
    // await api.saveProcessVariables(processId.value, variablesData)
    console.log('保存流程变量到数据库:', variablesData)
    
    return variablesData
  } catch (error) {
    console.error('保存流程变量失败:', error)
    throw error
  }
}

// 添加缺失的 loadElementProperties 函数
const loadElementProperties = () => {
  if (!props.selectedElement) return
  
  const element = props.selectedElement
  const businessObject = element.businessObject
  
  elementId.value = businessObject?.id || element.id || ''
  elementName.value = businessObject?.name || ''
  
  // 加载条件表达式
  if (isSequenceFlow.value) {
    conditionExpression.value = businessObject?.conditionExpression?.body || ''
  }
  
  // 加载任务分配信息
  if (isUserTask.value) {
    // 尝试多种方式获取assignee值
    const assigneeValue = businessObject?.assignee || 
                         businessObject?.$attrs?.['flowable:assignee'] ||
                         businessObject?.$attrs?.assignee ||
                         element.assignee || ''
    
    const candidateUsersValue = businessObject?.candidateUsers || 
                               businessObject?.$attrs?.['flowable:candidateUsers'] ||
                               businessObject?.$attrs?.candidateUsers || ''
    
    const candidateGroupsValue = businessObject?.candidateGroups || 
                                businessObject?.$attrs?.['flowable:candidateGroups'] ||
                                businessObject?.$attrs?.candidateGroups || ''
    
    const formKeyValue = businessObject?.formKey || 
                        businessObject?.$attrs?.['flowable:formKey'] ||
                        businessObject?.$attrs?.formKey || ''
    
    console.log('任务分配信息:', {
      assignee: assigneeValue,
      candidateUsers: candidateUsersValue,
      candidateGroups: candidateGroupsValue,
      formKey: formKeyValue
    })
    
    assignee.value = assigneeValue
    candidateUsers.value = candidateUsersValue
    candidateGroups.value = candidateGroupsValue
    formKey.value = formKeyValue
  }
}

// 监听选中元素变化
watch(() => props.selectedElement, (newElement) => {
  if (newElement) {
    loadElementProperties()
  } else {
    loadProcessProperties()
  }
}, { immediate: true })

// 监听BPMN建模器变化
watch(() => props.bpmnModeler, (newModeler) => {
  if (newModeler) {
    loadProcessProperties()
    scanProcessVariables()
  }
}, { immediate: true })
</script>

<style scoped>
.custom-properties-panel {
  width: 350px;
  height: 100%;
  background: white;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.property-group {
  border-bottom: 1px solid #f0f0f0;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-weight: 500;
  font-size: 14px;
  color: #606266;
}

.group-content {
  padding: 15px 20px;
}

.property-item {
  margin-bottom: 15px;
}

.property-item:last-child {
  margin-bottom: 0;
}

.property-item label {
  display: block;
  margin-bottom: 5px;
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state .el-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 变量管理样式 */
.variables-list {
  space-y: 10px;
}

.variable-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 10px;
  overflow: hidden;
}

.variable-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #fafafa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.variable-header:hover {
  background: #f0f9ff;
}

.variable-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.variable-name {
  font-weight: 500;
  color: #303133;
}

.variable-actions {
  display: flex;
  gap: 5px;
}

.variable-details {
  padding: 15px;
  border-top: 1px solid #e4e7ed;
  background: white;
}

.variable-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.variable-usage {
  padding: 10px 15px;
  background: #f9f9f9;
  border-top: 1px solid #e4e7ed;
}

.usage-header {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.usage-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.usage-tag {
  cursor: pointer;
}

.usage-tag:hover {
  opacity: 0.8;
}

/* 表达式模板样式 */
.expression-templates {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.template-header {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.template-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.variable-references {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.ref-header {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.ref-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.variable-ref-tag {
  cursor: pointer;
  font-family: 'Courier New', monospace;
}

.variable-ref-tag:hover {
  opacity: 0.8;
}

/* 统计信息样式 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

/* 变量使用情况样式 */
.variable-usage-list {
  space-y: 10px;
}

.usage-item {
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 8px;
}

.usage-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.usage-var-name {
  font-family: 'Courier New', monospace;
  font-weight: 500;
  color: #e6a23c;
}

.usage-context {
  font-size: 12px;
  color: #909399;
}

.validation-tip {
  font-size: 12px;
  color: #67c23a;
  margin-top: 4px;
}

.validation-error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
}
</style>