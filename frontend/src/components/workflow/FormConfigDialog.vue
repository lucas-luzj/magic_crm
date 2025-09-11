<template>
  <el-dialog
    v-model="visible"
    title="表单配置"
    width="900px"
    :before-close="handleClose"
  >
    <div class="form-config-dialog">
      <div class="element-info">
        <el-alert
          :title="`正在配置: ${elementName}`"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      
      <el-divider />

      <!-- 表单选择器 -->
      <div class="form-selector-section">
        <FormTemplateSelector
          :model-value="selectorConfig"
          @update:modelValue="Object.assign(selectorConfig, $event)"
          @change="handleSelectorChange"
        />
      </div>

      <el-divider v-if="selectorConfig.mode === 'manual'" />
      <!-- 手动配置模式的字段配置 -->
      <div v-if="selectorConfig.mode === 'manual'" class="form-fields-config">
        <div class="fields-header">
          <h3>表单字段配置</h3>
          <el-button type="primary" @click="addField" size="small">
            <el-icon><Plus /></el-icon>
            添加字段
          </el-button>
        </div>
        
        <div class="fields-list">
          <div v-if="formConfig.fields.length === 0" class="empty-fields">
            <el-empty description="暂无表单字段，点击上方按钮添加字段" />
          </div>
          
          <div v-else class="fields-container">
            <div
              v-for="(field, index) in formConfig.fields"
              :key="field.id || index"
              class="field-item"
            >
              <div class="field-header">
                <span class="field-title">字段 {{ index + 1 }}: {{ field.label || field.id }}</span>
                <div class="field-actions">
                  <el-button @click="moveFieldUp(index)" :disabled="index === 0" size="small" text>
                    <el-icon><ArrowUp /></el-icon>
                  </el-button>
                  <el-button @click="moveFieldDown(index)" :disabled="index === formConfig.fields.length - 1" size="small" text>
                    <el-icon><ArrowDown /></el-icon>
                  </el-button>
                  <el-button @click="removeField(index)" type="danger" size="small" text>
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              
              <div class="field-config">
                <el-row :gutter="16">
                  <el-col :span="8">
                    <el-form-item label="字段ID" size="small">
                      <el-input v-model="field.id" placeholder="字段唯一标识" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="字段标签" size="small">
                      <el-input v-model="field.label" placeholder="显示名称" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="字段类型" size="small">
                      <el-select v-model="field.cmpType" placeholder="选择类型">
                        <el-option label="文本输入" value="text" />
                        <el-option label="多行文本" value="textarea" />
                        <el-option label="数字输入" value="number" />
                        <el-option label="日期选择" value="date" />
                        <el-option label="日期时间" value="datetime" />
                        <el-option label="下拉选择" value="select" />
                        <el-option label="单选框" value="radio" />
                        <el-option label="复选框" value="checkbox" />
                        <el-option label="文件上传" value="file" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="占位符" size="small">
                      <el-input v-model="field.placeholder" placeholder="输入提示文字" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="默认值" size="small">
                      <el-input v-model="field.defaultValue" placeholder="默认值" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="16" v-if="['select', 'radio', 'checkbox'].includes(field.type)">
                  <el-col :span="24">
                    <el-form-item label="选项配置" size="small">
                      <el-input
                        v-model="field.options"
                        type="textarea"
                        :rows="3"
                        placeholder="每行一个选项，格式：值|显示文本，如：&#10;annual|年假&#10;sick|病假&#10;personal|事假"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="16">
                  <el-col :span="8">
                    <el-form-item size="small">
                      <el-checkbox v-model="field.required">必填字段</el-checkbox>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item size="small">
                      <el-checkbox v-model="field.readonly">只读字段</el-checkbox>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item size="small">
                      <el-checkbox v-model="field.visible" :indeterminate="field.visible === undefined">显示字段</el-checkbox>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="loadSampleConfig">加载示例</el-button>
        <el-button @click="previewForm">预览表单</el-button>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存配置</el-button>
      </div>
    </template>
  </el-dialog>
  <!-- 表单预览对话框 -->
  <el-dialog v-model="previewVisible" title="表单预览" width="600px">
    <FormRenderer :config="selectorConfig" v-model="previewData" />
    <template #footer>
      <el-button @click="previewVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ArrowUp, ArrowDown, Delete } from '@element-plus/icons-vue'
import FormRenderer from '../FormDesigner/FormRenderer.vue'
import FormTemplateSelector from './FormTemplateSelector.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  element: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'save'])

const visible = ref(false)
const previewVisible = ref(false)
const previewData = ref({})

const elementName = ref('')
const formConfig = reactive({
  formKey: '',
  formName: '',
  fields: []
})

// 表单选择器配置
const selectorConfig = reactive({
  mode: 'template', // 'template' 或 'manual'
  templateId: null,
  formKey: '',
  formName: '',
  fields: []
})

// 监听显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.element) {
    loadElementConfig()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 处理表单选择器变化
const handleSelectorChange = (config) => {
  // 同步更新formConfig
  formConfig.formKey = config.formKey
  formConfig.formName = config.formName
  formConfig.fields = config.fields || []
}

// 加载元素配置
const loadElementConfig = async () => {
  if (!props.element) return
  
  elementName.value = props.element.businessObject?.name || props.element.id
  
  // 从元素中加载现有配置
  const businessObject = props.element.businessObject
  const existingFormKey = businessObject.formKey || ''
  const existingFormName = businessObject.name || ''
  
  // 重置配置
  formConfig.formKey = existingFormKey
  formConfig.formName = existingFormName
  formConfig.fields = []
  
  // 重置选择器配置
  selectorConfig.mode = 'template'
  selectorConfig.templateId = null
  selectorConfig.formKey = existingFormKey
  selectorConfig.formName = existingFormName
  selectorConfig.fields = []
  
  // 如果有formKey，尝试从不同来源加载表单配置
  if (existingFormKey) {
    try {
      let configLoaded = false
      
      // 首先尝试从sessionStorage加载（新建模式的暂存配置）
      const tempFormConfigs = JSON.parse(sessionStorage.getItem('tempFormConfigs') || '{}')
      if (tempFormConfigs[existingFormKey]) {
        const tempConfig = tempFormConfigs[existingFormKey]
        
        // 判断配置来源：如果有templateId说明是从模板选择的
        if (tempConfig.templateId) {
          selectorConfig.mode = 'template'
          selectorConfig.templateId = tempConfig.templateId
        } else {
          selectorConfig.mode = 'manual'
        }
        
        selectorConfig.formName = tempConfig.formName || existingFormName
        selectorConfig.fields = JSON.parse(JSON.stringify(tempConfig.fields || []))
        
        // 同步到formConfig
        formConfig.formName = selectorConfig.formName
        formConfig.fields = selectorConfig.fields
        
        ElMessage.success('已加载暂存的表单配置')
        configLoaded = true
      }
      
      // 如果是编辑模式且未从暂存加载，从数据库加载
      if (!configLoaded) {
        const editProcessDefinitionId = sessionStorage.getItem('editProcessDefinitionId')
        if (editProcessDefinitionId) {
          const { processDefinitionApi } = await import('@/api/workflow')
          const response = await processDefinitionApi.getFormConfig(editProcessDefinitionId)
          if (response.data && response.data.data) {
            const dbConfig = JSON.parse(response.data.data)
            
            // 支持多个表单配置的情况
            if (dbConfig.configs && Array.isArray(dbConfig.configs)) {
              const matchedConfig = dbConfig.configs.find(config => config.formKey === existingFormKey)
              if (matchedConfig) {
                loadConfigFromData(matchedConfig)
                ElMessage.success('已加载数据库中的表单配置')
                configLoaded = true
              }
            } else if (dbConfig.formKey === existingFormKey) {
              // 兼容旧版本单个配置格式
              loadConfigFromData(dbConfig)
              ElMessage.success('已加载数据库中的表单配置')
              configLoaded = true
            }
          }
        }
      }
      
      // 如果都没有加载到配置，尝试根据formKey匹配现有模板
      if (!configLoaded && existingFormKey) {
        await tryMatchExistingTemplate(existingFormKey)
      }
      
    } catch (error) {
      console.warn('加载表单配置失败:', error)
      ElMessage.warning('加载表单配置失败，将使用默认配置')
    }
  }
}

// 从配置数据加载到选择器
const loadConfigFromData = (configData) => {
  // 判断是否来自模板（通过templateId或其他标识）
  if (configData.templateId) {
    selectorConfig.mode = 'template'
    selectorConfig.templateId = configData.templateId
  } else {
    selectorConfig.mode = 'manual'
  }
  
  selectorConfig.formName = configData.formName || formConfig.formName
  selectorConfig.fields = JSON.parse(JSON.stringify(configData.fields || []))
  
  // 同步到formConfig
  formConfig.formName = selectorConfig.formName
  formConfig.fields = selectorConfig.fields
}

// 尝试匹配现有模板
const tryMatchExistingTemplate = async (formKey) => {
  try {
    const { formTemplateApi } = await import('@/api/formTemplate')
    const response = await formTemplateApi.getFormTemplates({
      page: 0,
      size: 1000,
      status: 'ACTIVE'
    })
    
    if (response.data && response.data.content) {
      const matchedTemplate = response.data.content.find(t => t.formKey === formKey)
      if (matchedTemplate) {
        selectorConfig.mode = 'template'
        selectorConfig.templateId = matchedTemplate.id
        selectorConfig.formKey = matchedTemplate.formKey
        selectorConfig.formName = matchedTemplate.name
        
        // 解析模板数据
        try {
          const formData = JSON.parse(matchedTemplate.formData || '{}')
          if (formData.components && Array.isArray(formData.components)) {
            selectorConfig.fields = convertComponentsToFields(formData.components)
          } else if (formData.fields && Array.isArray(formData.fields)) {
            selectorConfig.fields = JSON.parse(JSON.stringify(formData.fields))
          }
          
          // 同步到formConfig
          formConfig.formName = selectorConfig.formName
          formConfig.fields = selectorConfig.fields
          
          ElMessage.success(`已匹配到表单模板: ${matchedTemplate.name}`)
        } catch (error) {
          console.warn('解析模板数据失败:', error)
        }
      } else {
        // 没有匹配的模板，使用手动模式
        selectorConfig.mode = 'manual'
      }
    }
  } catch (error) {
    console.warn('匹配模板失败:', error)
    selectorConfig.mode = 'manual'
  }
}

// 转换组件为字段（复用FormTemplateSelector中的方法）
const convertComponentsToFields = (components) => {
  const fields = []
  
  const processComponent = (component) => {
    if (component.type === 'container') {
      if (component.children && Array.isArray(component.children)) {
        component.children.forEach(child => processComponent(child))
      }
    } else {
      const field = {
        id: component.id || component.name || `field_${Date.now()}`,
        label: component.label || component.placeholder || '未命名字段',
        type: mapComponentTypeToFieldType(component.type),
        placeholder: component.placeholder || '',
        defaultValue: component.defaultValue || '',
        required: component.required || false,
        readonly: component.readonly || false,
        visible: component.visible !== false,
        options: ''
      }
      if (component.options && Array.isArray(component.options)) {
        field.options = component.options.map(opt => `${opt.value}|${opt.label}`).join('')
      }
      
      fields.push(field)
    }
  }
  
  components.forEach(component => processComponent(component))
  return fields
}

// 组件类型映射
const mapComponentTypeToFieldType = (componentType) => {
  const typeMap = {
    'input': 'text',
    'textarea': 'textarea',
    'number': 'number',
    'select': 'select',
    'radio': 'radio',
    'checkbox': 'checkbox',
    'date': 'date',
    'datetime': 'datetime',
    'file': 'file'
  }
  return typeMap[componentType] || 'text'
}

// 添加字段
const addField = () => {
  const newField = {
    id: `field_${Date.now()}`,
    label: '新字段',
    type: 'text',
    placeholder: '',
    defaultValue: '',
    options: '',
    required: false,
    readonly: false,
    visible: true
  }
  formConfig.fields.push(newField)
}

// 删除字段
const removeField = (index) => {
  formConfig.fields.splice(index, 1)
}

// 上移字段
const moveFieldUp = (index) => {
  if (index > 0) {
    const temp = formConfig.fields[index]
    formConfig.fields[index] = formConfig.fields[index - 1]
    formConfig.fields[index - 1] = temp
  }
}

// 下移字段
const moveFieldDown = (index) => {
  if (index < formConfig.fields.length - 1) {
    const temp = formConfig.fields[index]
    formConfig.fields[index] = formConfig.fields[index + 1]
    formConfig.fields[index + 1] = temp
  }
}

// 加载示例配置
const loadSampleConfig = () => {
  formConfig.formKey = 'leave_application'
  formConfig.formName = '请假申请表'
  formConfig.fields = [
    {
      id: 'leaveType',
      label: '请假类型',
      type: 'select',
      placeholder: '请选择请假类型',
      defaultValue: '',
      options: 'annual|年假\nsick|病假\npersonal|事假\ncompensatory|调休',
      required: true,
      readonly: false,
      visible: true
    },
    {
      id: 'startDate',
      label: '开始日期',
      type: 'date',
      placeholder: '请选择开始日期',
      defaultValue: '',
      options: '',
      required: true,
      readonly: false,
      visible: true
    },
    {
      id: 'endDate',
      label: '结束日期',
      type: 'date',
      placeholder: '请选择结束日期',
      defaultValue: '',
      options: '',
      required: true,
      readonly: false,
      visible: true
    },
    {
      id: 'leaveDays',
      label: '请假天数',
      type: 'number',
      placeholder: '请输入请假天数',
      defaultValue: '1',
      options: '',
      required: true,
      readonly: false,
      visible: true
    },
    {
      id: 'reason',
      label: '请假理由',
      type: 'textarea',
      placeholder: '请输入请假理由',
      defaultValue: '',
      options: '',
      required: true,
      readonly: false,
      visible: true
    },
    {
      id: 'attachment',
      label: '附件',
      type: 'file',
      placeholder: '上传相关证明文件',
      defaultValue: '',
      options: '',
      required: false,
      readonly: false,
      visible: true
    }
  ]
  ElMessage.success('已加载请假流程示例配置')
}

// 预览表单
const previewForm = () => {
  if (selectorConfig.fields.length === 0) {
    ElMessage.warning('请先配置表单字段')
    return
  }
  previewData.value = {}
  previewVisible.value = true
}

// 保存配置
const saveConfig = () => {
  // 基础验证
  if (!selectorConfig.formKey) {
    ElMessage.warning('请输入表单标识')
    return
  }
  
  if (!/^[a-zA-Z][a-zA-Z0-9_-]*$/.test(selectorConfig.formKey)) {
    ElMessage.warning('表单标识格式不正确，应以字母开头，只能包含字母、数字、下划线和连字符')
    return
  }
  
  if (!selectorConfig.formName) {
    ElMessage.warning('请输入表单名称')
    return
  }
  
  if (selectorConfig.mode === 'template' && !selectorConfig.templateId) {
    ElMessage.warning('请选择表单模板')
    return
  }
  
  if (selectorConfig.fields.length === 0) {
    ElMessage.warning('请至少配置一个表单字段')
    return
  }
  
  // 验证字段配置
  const fieldIds = new Set()
  for (let i = 0; i < selectorConfig.fields.length; i++) {
    const field = selectorConfig.fields[i]
    
    // 检查必填项
    if (!field.id || !field.label || !field.cmpType) {
      ElMessage.warning(`第${i + 1}个字段的ID、标签和类型不能为空`)
      return
    }
    
    // 检查字段ID格式
    if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(field.id)) {
      ElMessage.warning(`第${i + 1}个字段的ID格式不正确，应以字母开头，只能包含字母、数字和下划线`)
      return
    }
    
    // 检查字段ID重复
    if (fieldIds.has(field.id)) {
      ElMessage.warning(`第${i + 1}个字段的ID "${field.id}" 重复，请使用唯一的字段ID`)
      return
    }
    fieldIds.add(field.id)
    
    // 检查选择类型字段的选项配置
    if (['select', 'radio', 'checkbox'].includes(field.type) && !field.options) {
      ElMessage.warning(`第${i + 1}个字段是选择类型，请配置选项`)
      return
    }
    
    // 验证选项格式
    if (field.options && ['select', 'radio', 'checkbox'].includes(field.type)) {
      const options = field.options.split("|").filter(opt => opt.trim())
      if (options.length === 0) {
        ElMessage.warning(`第${i + 1}个字段的选项配置不能为空`)
        return
      }
    }
  }
  
  // 构建保存数据，包含模板信息
  const saveData = {
    formKey: selectorConfig.formKey,
    formName: selectorConfig.formName,
    fields: selectorConfig.fields,
    mode: selectorConfig.mode
  }
  
  // 如果是模板模式，包含模板ID
  if (selectorConfig.mode === 'template' && selectorConfig.templateId) {
    saveData.templateId = selectorConfig.templateId
  }
  
  emit('save', saveData)
  
  visible.value = false
  ElMessage.success('表单配置已保存')
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.form-config-dialog {
  max-height: 70vh;
  overflow-y: auto;
}

.element-info {
  margin-bottom: 16px;
}

.form-item-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.fields-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.fields-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.empty-fields {
  text-align: center;
  padding: 40px 0;
}

.field-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 16px;
  overflow: hidden;
}

.field-header {
  background: #f5f7fa;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.field-title {
  font-weight: 500;
  color: #303133;
}

.field-actions {
  display: flex;
  gap: 4px;
}

.field-config {
  padding: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>