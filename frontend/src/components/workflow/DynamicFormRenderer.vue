<template>
  <div class="dynamic-form-renderer">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="loading"
    >
      <div v-if="!formConfig || formConfig.length === 0" class="empty-form">
        <el-empty description="暂无表单配置" />
      </div>
      
      <template v-else>
        <!-- 表单标题 -->
        <div v-if="formTitle" class="form-title">
          <h3>{{ formTitle }}</h3>
        </div>
        
        <!-- 动态渲染表单字段 -->
        <template v-for="field in formConfig" :key="field.id">
          <el-form-item
            :label="field.label"
            :prop="field.id"
            :required="field.required"
            v-show="field.visible !== false"
          >
            <!-- 文本输入框 -->
            <el-input
              v-if="field.type === 'text'"
              v-model="formData[field.id]"
              :placeholder="field.placeholder"
              :readonly="field.readonly || readonly"
              clearable
            />
            
            <!-- 多行文本 -->
            <el-input
              v-else-if="field.type === 'textarea'"
              v-model="formData[field.id]"
              type="textarea"
              :rows="4"
              :placeholder="field.placeholder"
              :readonly="field.readonly || readonly"
            />
            
            <!-- 数字输入 -->
            <el-input-number
              v-else-if="field.type === 'number'"
              v-model="formData[field.id]"
              :placeholder="field.placeholder"
              :readonly="field.readonly || readonly"
              style="width: 100%"
            />
            
            <!-- 日期选择 -->
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="formData[field.id]"
              type="date"
              :placeholder="field.placeholder"
              :readonly="field.readonly || readonly"
              style="width: 100%"
            />
            
            <!-- 日期时间选择 -->
            <el-date-picker
              v-else-if="field.type === 'datetime'"
              v-model="formData[field.id]"
              type="datetime"
              :placeholder="field.placeholder"
              :readonly="field.readonly || readonly"
              style="width: 100%"
            />
            
            <!-- 下拉选择 -->
            <el-select
              v-else-if="field.type === 'select'"
              v-model="formData[field.id]"
              :placeholder="field.placeholder"
              :disabled="field.readonly || readonly"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="option in parseOptions(field.options)"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            
            <!-- 单选框 -->
            <el-radio-group
              v-else-if="field.type === 'radio'"
              v-model="formData[field.id]"
              :disabled="field.readonly || readonly"
            >
              <el-radio
                v-for="option in parseOptions(field.options)"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </el-radio>
            </el-radio-group>
            
            <!-- 复选框 -->
            <el-checkbox-group
              v-else-if="field.type === 'checkbox'"
              v-model="formData[field.id]"
              :disabled="field.readonly || readonly"
            >
              <el-checkbox
                v-for="option in parseOptions(field.options)"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </el-checkbox>
            </el-checkbox-group>
            
            <!-- 文件上传 -->
            <el-upload
              v-else-if="field.type === 'file'"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :disabled="field.readonly || readonly"
              :file-list="getFileList(field.id)"
              @success="handleFileSuccess"
              @remove="handleFileRemove"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">{{ field.placeholder }}</div>
              </template>
            </el-upload>
            
            <!-- 未知类型的字段 -->
            <el-input
              v-else
              v-model="formData[field.id]"
              :placeholder="`未支持的字段类型: ${field.type}`"
              readonly
            />
          </el-form-item>
        </template>
        
        <!-- 表单操作按钮 -->
        <div v-if="!readonly && showActions" class="form-actions">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ submitText }}
          </el-button>
        </div>
      </template>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  // 表单配置数据
  formConfig: {
    type: Array,
    default: () => []
  },
  // 表单标题
  formTitle: {
    type: String,
    default: ''
  },
  // 初始表单数据
  modelValue: {
    type: Object,
    default: () => ({})
  },
  // 是否只读模式
  readonly: {
    type: Boolean,
    default: false
  },
  // 是否显示操作按钮
  showActions: {
    type: Boolean,
    default: true
  },
  // 提交按钮文本
  submitText: {
    type: String,
    default: '提交'
  },
  // 文件上传地址
  uploadUrl: {
    type: String,
    default: '/api/upload'
  }
})

const emit = defineEmits(['update:modelValue', 'submit', 'reset'])

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)

// 表单数据
const formData = reactive({})

// 上传请求头
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

// 表单验证规则
const formRules = computed(() => {
  const rules = {}
  
  props.formConfig.forEach(field => {
    if (field.required) {
      rules[field.id] = [
        {
          required: true,
          message: `请输入${field.label}`,
          trigger: ['blur', 'change']
        }
      ]
      
      // 根据字段类型添加特定验证规则
      if (field.type === 'email') {
        rules[field.id].push({
          type: 'email',
          message: '请输入正确的邮箱地址',
          trigger: ['blur', 'change']
        })
      } else if (field.type === 'number') {
        rules[field.id].push({
          type: 'number',
          message: '请输入数字',
          trigger: ['blur', 'change']
        })
      }
    }
  })
  
  return rules
})

// 监听props变化，初始化表单数据
watch(() => props.modelValue, (newVal) => {
  Object.assign(formData, newVal)
}, { immediate: true, deep: true })

// 监听表单数据变化，向上传递
watch(formData, (newVal) => {
  emit('update:modelValue', { ...newVal })
}, { deep: true })

// 监听表单配置变化，初始化默认值
watch(() => props.formConfig, (newConfig) => {
  initFormData(newConfig)
}, { immediate: true })

// 初始化表单数据
const initFormData = (config) => {
  config.forEach(field => {
    if (formData[field.id] === undefined) {
      // 设置默认值
      let defaultValue = field.defaultValue || ''
      
      // 根据字段类型设置合适的默认值
      if (field.type === 'checkbox') {
        defaultValue = []
      } else if (field.type === 'number') {
        defaultValue = field.defaultValue ? Number(field.defaultValue) : null
      } else if (field.type === 'file') {
        defaultValue = []
      }
      
      formData[field.id] = defaultValue
    }
  })
}

// 解析选项字符串
const parseOptions = (optionsStr) => {
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

// 获取文件列表
const getFileList = (fieldId) => {
  const files = formData[fieldId] || []
  return Array.isArray(files) ? files : []
}

// 文件上传成功
const handleFileSuccess = (response, file, fileList) => {
  const fieldId = file.raw?.fieldId
  if (fieldId) {
    formData[fieldId] = fileList.map(f => ({
      name: f.name,
      url: f.response?.data?.url || f.url
    }))
  }
}

// 文件移除
const handleFileRemove = (file, fileList) => {
  const fieldId = file.raw?.fieldId
  if (fieldId) {
    formData[fieldId] = fileList.map(f => ({
      name: f.name,
      url: f.response?.data?.url || f.url
    }))
  }
}

// 表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 触发提交事件
    emit('submit', { ...formData })
    
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('请检查表单输入')
  } finally {
    submitting.value = false
  }
}

// 表单重置
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  
  // 重置为默认值
  initFormData(props.formConfig)
  
  emit('reset')
  ElMessage.success('表单已重置')
}

// 手动验证表单
const validate = async () => {
  if (!formRef.value) return false
  
  try {
    await formRef.value.validate()
    return true
  } catch (error) {
    return false
  }
}

// 清除验证
const clearValidate = () => {
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 暴露方法给父组件
defineExpose({
  validate,
  clearValidate,
  formData
})

onMounted(() => {
  initFormData(props.formConfig)
})
</script>

<style scoped>
.dynamic-form-renderer {
  width: 100%;
}

.empty-form {
  text-align: center;
  padding: 40px 0;
}

.form-title {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.form-title h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
}

.form-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
  text-align: right;
}

.form-actions .el-button {
  margin-left: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-upload__tip) {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
</style>