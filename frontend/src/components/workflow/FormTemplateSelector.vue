<template>
  <div class="form-template-selector">
    <el-form :model="selectorForm" label-width="100px">
      <el-form-item label="选择方式">
        <el-radio-group v-model="selectorForm.mode" @change="handleModeChange">
          <el-radio value="template">选择表单模板</el-radio>
          <el-radio value="manual">手动配置字段</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <template v-if="selectorForm.mode === 'template'">
        <el-form-item label="表单模板">
          <el-select
            v-model="selectorForm.templateId"
            placeholder="请选择表单模板"
            filterable
            clearable
            style="width: 100%"
            @change="handleTemplateChange"
          >
            <el-option
              v-for="template in formTemplates"
              :key="template.id"
              :label="`${template.formName} (${template.formKey})`"
              :value="template.id"
            >
              <div class="template-option">
                <div class="template-name">{{ template.formName }}</div>
                <div class="template-key">{{ template.formKey }}</div>
                <div class="template-desc" v-if="template.formDescription">{{ template.formDescription }}</div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="表单标识" v-if="selectedTemplate">
          <el-input
            v-model="selectorForm.formKey"
            placeholder="表单标识"
            readonly
          />
        </el-form-item>
        
        <el-form-item label="表单名称" v-if="selectedTemplate">
          <el-input
            v-model="selectorForm.formName"
            placeholder="表单名称"
          />
        </el-form-item>

        <!-- 表单预览 -->
        <div v-if="selectedTemplate && selectedTemplate.formConfig.components.length > 0" class="template-preview">
          <el-divider>表单预览</el-divider>
          <div class="preview-container">
            <FormRenderer
              :config="selectedTemplate.formConfig"
              :disabled="true"
              mode="preview"
            />
          </div>
        </div>
      </template>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { formTemplateApi } from '@/api/formTemplate'
import FormRenderer from '../FormDesigner/FormRenderer.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({
      mode: 'template',
      templateId: null,
      formKey: '',
      formName: '',
      fields: []
    })
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const formTemplates = ref([])
const loading = ref(false)

const selectorForm = reactive({
  mode: 'template',
  templateId: null,
  formKey: '',
  formName: '',
  fields: []
})

// 选中的模板
const selectedTemplate = computed(() => {
  if (!selectorForm.templateId) return null
  return formTemplates.value.find(t => t.id === selectorForm.templateId)
})

// 预览组件数据
const previewComponents = computed(() => {
  if (!selectedTemplate.value) return []
  
  try {
    const formData = selectedTemplate.value.formConfig || {}
    
    if (formData.components && Array.isArray(formData.components)) {
      return formData.components
    }
    
    return []
  } catch (error) {
    console.warn('解析表单数据失败:', error)
    return []
  }
})

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    Object.assign(selectorForm, newVal)
  }
}, { immediate: true, deep: true })

// 监听表单变化，向上传递
watch(selectorForm, (newVal) => {
  emit('update:modelValue', { ...newVal })
  emit('change', { ...newVal })
}, { deep: true })

// 加载表单模板列表
const loadFormTemplates = async () => {
  try {
    loading.value = true
    console.log('开始加载表单模板...')
    
    const data = await formTemplateApi.getFormTemplates({
      page: 0,
      size: 1000,
      status: 'ACTIVE'
    })
    
    console.log('表单模板API响应:', data)
    
    // 处理不同的响应格式
    if (data) {
      if (data.records && Array.isArray(data.records)) {
        // 分页响应格式
        formTemplates.value = data.records
        console.log('加载表单模板成功 (分页格式):', formTemplates.value.length, '个模板')
      } else if (data.data && Array.isArray(data.data)) {
        // 统一响应格式
        formTemplates.value = data.data
        console.log('加载表单模板成功 (统一格式):', formTemplates.value.length, '个模板')
      } else if (Array.isArray(data)) {
        // 直接数组格式
        formTemplates.value = data
        console.log('加载表单模板成功 (数组格式):', formTemplates.value.length, '个模板')
      } else {
        console.warn('未识别的响应格式:', data)
        formTemplates.value = []
      }
    } else {
      console.warn('响应数据为空:', data)
      formTemplates.value = []
    }
  } catch (error) {
    console.error('加载表单模板失败:', error)
    ElMessage.error('加载表单模板失败: ' + (error.message || '未知错误'))
    formTemplates.value = []
  } finally {
    loading.value = false
  }
}

// 处理模式切换
const handleModeChange = (mode) => {
  if (mode === 'manual') {
    // 切换到手动模式时清空模板相关数据
    selectorForm.templateId = null
    selectorForm.formKey = ''
    selectorForm.formName = ''
    selectorForm.fields = []
  }
}

// 处理模板选择
const handleTemplateChange = (templateId) => {
  if (!templateId) {
    selectorForm.formKey = ''
    selectorForm.formName = ''
    selectorForm.fields = []
    return
  }

  const template = formTemplates.value.find(t => t.id === templateId)
  if (template) {
    selectorForm.formKey = template.formKey
    selectorForm.formName = template.formName
    
    try {
      const formConfig = template.formConfig || {}
      if (formConfig.components && Array.isArray(formConfig.components)) {
        selectorForm.fields = convertComponentsToFields(formConfig.components)
      } else {
        selectorForm.fields = []
      }
    } catch (error) {
      console.warn('解析表单数据失败:', error)
      selectorForm.fields = []
    }
  }
}

// 转换新格式组件为旧格式字段（用于兼容现有流程配置）
const convertComponentsToFields = (components) => {
  const fields = []
  const processComponent = (component) => {
    if (component.type === 'container') {
      // 容器组件，递归处理子组件
      if (component.children && Array.isArray(component.children)) {
        component.children.forEach(child => processComponent(child))
      }
    } else {
      // 普通组件，转换为字段
      const field = {
        id: component.field,
        field: component.field,
        label: component.label || component.placeholder || '未命名字段',
        cmpType: component.cmpType,
        placeholder: component.placeholder || '',
        defaultValue: component.defaultValue || '',
        required: component.required || false,
        readonly: component.readonly || false,
        visible: component.visible !== false,
        options: component.options || [],
        span: component.span || 24,
        offset: component.offset || 0
      }
      
      fields.push(field)
    }
  }
  
  components.forEach(component => processComponent(component))
  return fields
}

// 转换旧格式字段为新格式组件（用于预览）
const convertFieldsToComponents = (fields) => {
  return fields.map(field => ({
    id: field.id,
    type: mapFieldTypeToComponentType(field.type),
    label: field.label,
    placeholder: field.placeholder,
    defaultValue: field.defaultValue,
    required: field.required,
    readonly: field.readonly,
    visible: field.visible,
    options: parseFieldOptions(field.options)
  }))
}

// 组件类型映射到字段类型
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

// 字段类型映射到组件类型
const mapFieldTypeToComponentType = (fieldType) => {
  const typeMap = {
    'text': 'input',
    'textarea': 'textarea',
    'number': 'number',
    'select': 'select',
    'radio': 'radio',
    'checkbox': 'checkbox',
    'date': 'date',
    'datetime': 'datetime',
    'file': 'file'
  }
  return typeMap[fieldType] || 'input'
}

// 解析字段选项
const parseFieldOptions = (optionsStr) => {
  if (!optionsStr) return []
  
  return optionsStr.split('\n')
    .filter(line => line.trim())
    .map(line => {
      const [value, label] = line.split('|')
      return {
        value: value?.trim() || '',
        label: label?.trim() || value?.trim() || ''
      }
    })
}

onMounted(() => {
  loadFormTemplates()
})

// 暴露方法给父组件
defineExpose({
  loadFormTemplates,
  selectedTemplate,
  previewComponents
})
</script>

<style scoped>
.form-template-selector {
  width: 100%;
}

.template-option {
  padding: 4px 0;
}

.template-name {
  font-weight: 500;
  color: #303133;
}

.template-key {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.template-desc {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}

.template-preview {
  margin-top: 16px;
}

.preview-container {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  background: #fafafa;
  max-height: 400px;
  overflow-y: auto;
}
</style>