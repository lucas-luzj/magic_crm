<template>
  <div class="form-designer-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-title">
          <el-button 
            type="text" 
            @click="router.push({ name: 'FormTemplateManagement' })"
            class="back-button"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
          <h2>{{ isEditMode ? '编辑表单' : '创建表单' }}</h2>
        </div>
        <p class="page-description">
          {{ isEditMode ? '编辑现有表单模板，修改字段配置和属性' : '可视化设计表单，支持拖拽组件、属性配置和实时预览' }}
        </p>
      </div>
      <div class="header-right">
        <el-button @click="handleSave" type="success" :loading="saving">
          <el-icon><Check /></el-icon>
          {{ isEditMode ? '更新表单' : '保存表单' }}
        </el-button>
      </div>
    </div>

    <!-- 表单基本信息 -->
    <el-card class="form-info-card" shadow="never" v-loading="loading">
      <template #header>
        <span>表单基本信息</span>
      </template>
      <el-form :model="formInfo" label-width="100px" size="small">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="表单键" required>
              <el-input 
                v-model="formInfo.formKey" 
                placeholder="表单唯一标识" 
                :readonly="isEditMode"
                :disabled="isEditMode"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="表单名称" required>
              <el-input v-model="formInfo.formName" placeholder="表单显示名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分类">
              <el-select v-model="formInfo.category" placeholder="选择分类" clearable>
                <el-option
                  v-for="category in categories"
                  :key="category"
                  :label="category"
                  :value="category"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="表单描述">
              <el-input 
                v-model="formInfo.formDescription" 
                type="textarea" 
                :rows="2"
                placeholder="表单描述信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 新的表单设计器 -->
    <div class="designer-container">
      <FormDesigner
        ref="formDesignerRef"
        v-model="formConfig"
        @save="handleFormSave"
        @export="handleFormExport"
      />
    </div>

    <!-- 保存表单对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      :title="isEditMode ? '更新表单模板' : '保存表单模板'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="saveForm" :rules="saveRules" ref="saveFormRef" label-width="100px">
        <el-form-item label="表单键" prop="formKey">
          <el-input v-model="saveForm.formKey" placeholder="请输入表单键" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="saveForm.formName" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="表单描述">
          <el-input 
            v-model="saveForm.formDescription" 
            type="textarea" 
            :rows="3"
            placeholder="请输入表单描述"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="saveForm.category" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="saveForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSave" :loading="saving">
          {{ isEditMode ? '确认更新' : '确认保存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, ArrowLeft } from '@element-plus/icons-vue'
import FormDesigner from '@/components/FormDesigner/FormDesigner.vue'
import { formTemplateApi } from '@/api/formTemplate'

const route = useRoute()
const router = useRouter()

// 响应式数据
const saving = ref(false)
const saveDialogVisible = ref(false)
const categories = ref([])
const saveFormRef = ref()
const formDesignerRef = ref()
const loading = ref(false)

// 编辑模式相关
const isEditMode = ref(false)
const currentFormId = ref(null)

// 表单基本信息
const formInfo = reactive({
  formKey: '',
  formName: '',
  formDescription: '',
  category: ''
})

// 表单配置 - 适配新FormDesigner的数据结构
const formConfig = ref({
  title: '新建表单',
  name: 'new-form',
  description: '',
  components: [],
  labelPosition: 'left',
  labelWidth: 100,
  size: 'default'
})

// 保存表单数据
const saveForm = reactive({
  formKey: '',
  formName: '',
  formDescription: '',
  category: '',
  tags: ''
})

// 保存表单验证规则
const saveRules = {
  formKey: [
    { required: true, message: '请输入表单键', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '表单键只能包含字母、数字和下划线，且以字母开头', trigger: 'blur' }
  ],
  formName: [
    { required: true, message: '请输入表单名称', trigger: 'blur' },
    { min: 2, max: 100, message: '表单名称长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}

// 监听表单基本信息变化，同步到formConfig
watch(() => formInfo.formName, (newName) => {
  if (newName) {
    formConfig.value.title = newName
    formConfig.value.name = formInfo.formKey || 'new-form'
  }
}, { immediate: true })

watch(() => formInfo.formDescription, (newDesc) => {
  formConfig.value.description = newDesc || ''
}, { immediate: true })

// 方法
const loadCategories = async () => {
  try {
    const response = await formTemplateApi.getCategories()
    categories.value = response || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadFormTemplate = async (id) => {
  loading.value = true
  try {
    const response = await formTemplateApi.getFormTemplateById(id)
    
    // 填充表单基本信息
    formInfo.formKey = response.formKey
    formInfo.formName = response.formName
    formInfo.formDescription = response.formDescription || ''
    formInfo.category = response.category || ''
    
    // 适配表单配置数据结构
    if (response.formConfig) {
      // 如果是新格式的formConfig，直接使用
      if (response.formConfig.components) {
        formConfig.value = {
          title: response.formName,
          name: response.formKey,
          description: response.formDescription || '',
          components: response.formConfig.components || [],
          labelPosition: response.formConfig.labelPosition || 'left',
          labelWidth: response.formConfig.labelWidth || 100,
          size: response.formConfig.size || 'default'
        }
      } else if (response.formConfig.fields) {
        // 如果是旧格式的fields，需要转换为新格式
        formConfig.value = {
          title: response.formName,
          name: response.formKey,
          description: response.formDescription || '',
          components: convertFieldsToComponents(response.formConfig.fields),
          labelPosition: 'left',
          labelWidth: 100,
          size: 'default'
        }
      }
    }
    
    ElMessage.success('表单模板加载成功')
  } catch (error) {
    console.error('加载表单模板失败:', error)
    ElMessage.error('加载表单模板失败')
    router.push({ name: 'FormTemplateManagement' })
  } finally {
    loading.value = false
  }
}

// 转换旧格式fields为新格式components
const convertFieldsToComponents = (fields) => {
  if (!Array.isArray(fields)) return []
  
  return fields.map(field => ({
    id: field.id || `field_${Date.now()}_${Math.random()}`,
    type: field.type || 'input',
    name: field.name || field.label || '字段',
    label: field.label || field.name || '字段',
    field: field.field || field.name || `field_${Date.now()}`,
    span: field.span || 24,
    required: field.required || false,
    placeholder: field.placeholder || '',
    ...field
  }))
}

const handleFormSave = (config) => {
  // 新FormDesigner组件的保存事件
  formConfig.value = config
  handleSave()
}

const handleFormExport = (config) => {
  // 新FormDesigner组件的导出事件
  console.log('表单导出:', config)
}

const handleSave = () => {
  if (!formConfig.value.components || formConfig.value.components.length === 0) {
    ElMessage.warning('请先添加表单组件')
    return
  }

  // 检查表单基本信息
  if (!formInfo.formKey || !formInfo.formName) {
    ElMessage.warning('请先填写表单基本信息')
    return
  }

  // 填充保存表单数据
  saveForm.formKey = formInfo.formKey
  saveForm.formName = formInfo.formName
  saveForm.formDescription = formInfo.formDescription
  saveForm.category = formInfo.category

  saveDialogVisible.value = true
}

const confirmSave = async () => {
  if (!saveFormRef.value) return

  try {
    await saveFormRef.value.validate()
    
    saving.value = true

    const formTemplate = {
      formKey: saveForm.formKey,
      formName: saveForm.formName,
      formDescription: saveForm.formDescription,
      category: saveForm.category,
      tags: saveForm.tags,
      formConfig: formConfig.value, // 使用新的formConfig格式
      status: 'DRAFT',
      createdBy: 'current_user' // 这里应该从用户状态获取
    }

    if (isEditMode.value && currentFormId.value) {
      // 编辑模式：更新现有表单
      formTemplate.id = currentFormId.value
      await formTemplateApi.updateFormTemplate(currentFormId.value, formTemplate)
      ElMessage.success('表单更新成功')
    } else {
      // 创建模式：创建新表单
      await formTemplateApi.createFormTemplate(formTemplate)
      ElMessage.success('表单创建成功')
    }
    
    saveDialogVisible.value = false
    
    // 跳转回表单模板管理页面
    router.push({ name: 'FormTemplateManagement' })
  } catch (error) {
    console.error('保存表单失败:', error)
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  formInfo.formKey = ''
  formInfo.formName = ''
  formInfo.formDescription = ''
  formInfo.category = ''
  
  formConfig.value = {
    title: '新建表单',
    name: 'new-form',
    description: '',
    components: [],
    labelPosition: 'left',
    labelWidth: 100,
    size: 'default'
  }
}

// 生命周期
onMounted(async () => {
  await loadCategories()
  
  // 检查是否为编辑模式
  const mode = route.query.mode
  const id = route.query.id
  
  if (mode === 'edit' && id) {
    isEditMode.value = true
    currentFormId.value = parseInt(id)
    
    // 如果有传递的基本信息，先填充
    if (route.query.formKey) {
      formInfo.formKey = route.query.formKey
    }
    if (route.query.formName) {
      formInfo.formName = route.query.formName
    }
    
    // 加载完整的表单模板数据
    await loadFormTemplate(currentFormId.value)
  } else {
    // 创建模式，重置表单
    isEditMode.value = false
    currentFormId.value = null
    resetForm()
  }
})
</script>

<style scoped>
.form-designer-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.back-button {
  padding: 8px 12px;
  color: #606266;
  font-size: 14px;
  border: none;
  background: none;
}

.back-button:hover {
  color: #409EFF;
  background-color: #f0f9ff;
}

.header-left h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.form-info-card {
  margin: 16px 20px;
  flex-shrink: 0;
}

.form-info-card :deep(.el-card__body) {
  padding: 16px;
}

.designer-container {
  flex: 1;
  margin: 0 20px 20px;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
}

.designer-container :deep(.form-designer) {
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.designer-container :deep(.form-designer__body) {
  height: calc(100% - 48px);
}

.designer-container :deep(.form-designer__main) {
  background: #f5f7fa;
}

.designer-container :deep(.canvas-viewport) {
  min-height: 500px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .form-designer-page {
    padding: 0;
  }
  
  .page-header {
    padding: 12px 16px;
  }
  
  .form-info-card {
    margin: 12px 16px;
  }
  
  .designer-container {
    margin: 0 16px 16px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .header-right {
    justify-content: flex-end;
  }
  
  .form-info-card :deep(.el-form) {
    .el-row {
      .el-col {
        margin-bottom: 12px;
      }
    }
  }
}
</style>
