<template>
  <div class="process-start-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/workflow/process-list' }">流程管理</el-breadcrumb-item>
        <el-breadcrumb-item>启动流程</el-breadcrumb-item>
      </el-breadcrumb>
      
      <div class="header-actions">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>

    <!-- 流程信息卡片 -->
    <el-card class="process-info-card" shadow="never">
      <div class="process-info">
        <div class="process-icon">
          <el-icon size="32"><Document /></el-icon>
        </div>
        <div class="process-details">
          <h3>{{ processDefinition.name }}</h3>
          <p class="process-description">{{ processDefinition.description || '暂无描述' }}</p>
          <div class="process-meta">
            <el-tag size="small" type="info">版本 v{{ processDefinition.version }}</el-tag>
            <el-tag size="small" type="success">{{ processDefinition.category || '默认分类' }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 启动表单 -->
    <el-card class="form-card" shadow="never" v-loading="formLoading">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><Edit /></el-icon>
            {{ formConfig.formName || '启动表单' }}
          </h4>
        </div>
      </template>

      <div class="form-content">
        <!-- 如果有表单配置，显示动态表单 -->
        <FormRenderer
          v-if="formConfig.components && formConfig.components.length > 0"
          ref="formRendererRef"
          :config="formConfig"
          v-model="formData"
          @validate-success="onValidateSuccess"
          @validate-error="onValidateError"
        />
        
        <!-- 如果没有表单配置，显示提示信息 -->
        <el-empty
          v-else
          description="该流程未配置启动表单"
          :image-size="120"
        >
          <template #image>
            <el-icon size="120" color="#e6e6e6"><Document /></el-icon>
          </template>
        </el-empty>
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button @click="resetForm" :disabled="submitting">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button 
          type="primary" 
          @click="startProcess" 
          :loading="submitting"
          :disabled="!canSubmit"
        >
          <el-icon><Check /></el-icon>
          启动流程
        </el-button>
      </div>
    </el-card>

    <!-- 启动成功对话框 -->
    <el-dialog
      v-model="successDialogVisible"
      title="流程启动成功"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="success-content">
        <div class="success-icon">
          <el-icon size="48" color="#67c23a"><CircleCheck /></el-icon>
        </div>
        <div class="success-info">
          <h4>流程已成功启动</h4>
          <p>流程实例ID: {{ processInstanceId }}</p>
          <p>您可以在个人工作台查看流程进度</p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="goToWorkspace">前往工作台</el-button>
          <el-button type="primary" @click="startAnother">再次启动</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Document, Edit, Refresh, Check, CircleCheck 
} from '@element-plus/icons-vue'
import FormRenderer from '@/components/FormDesigner/FormRenderer.vue'
import { FormConfigService } from '@/services/formConfigService'
import { processDefinitionApi, processInstanceApi } from '@/api/workflow'
import { useUserStore } from '@/stores/user'

// 路由和状态
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const processDefinitionId = route.params.id

// 响应式数据
const processDefinition = ref({})
const formConfig = ref({
  components: [],
  labelPosition: 'left',
  labelWidth: 100,
  size: 'default'
})
const formData = reactive({})
const formLoading = ref(false)
const submitting = ref(false)
const successDialogVisible = ref(false)
const processInstanceId = ref('')

// 表单引用
const formRendererRef = ref()

// 计算属性
const canSubmit = computed(() => {
  return formConfig.value.components && formConfig.value.components.length > 0
})

// 页面初始化
onMounted(async () => {
  await loadProcessDefinition()
  await loadStartForm()
})

/**
 * 加载流程定义信息
 */
const loadProcessDefinition = async () => {
  try {
    const data = await processDefinitionApi.getProcessDefinition(processDefinitionId)
    if (data && data.records) {
      processDefinition.value = data.records
    }
  } catch (error) {
    console.error('加载流程定义失败:', error)
    ElMessage.error('加载流程定义失败')
  }
}

/**
 * 加载启动表单配置
 */
const loadStartForm = async () => {
  try {
    formLoading.value = true
    console.log('加载启动表单配置:', processDefinitionId)
    
    const config = await FormConfigService.getStartFormConfig(processDefinitionId)
    console.log('获取到的表单配置:', config)
    
    formConfig.value = config
    
    // 初始化表单数据
    if (config.components && Array.isArray(config.components)) {
      config.components.forEach(component => {
        if (component.field && component.defaultValue !== undefined) {
          formData[component.field] = component.defaultValue
        }
      })
    }
    
  } catch (error) {
    console.error('加载启动表单失败:', error)
    ElMessage.warning('该流程未配置启动表单，将直接启动')
  } finally {
    formLoading.value = false
  }
}

/**
 * 启动流程
 */
const startProcess = async () => {
  try {
    submitting.value = true
    
    // 如果有表单，先验证表单
    if (formRendererRef.value && formConfig.value.components.length > 0) {
      await formRendererRef.value.validate()
    }
    
    console.log('启动流程，原始表单数据:', formData)
    
    // 获取当前用户信息
    const currentUser = userStore.userInfo
    console.log('当前用户信息:', currentUser)
    
    // 解析流程变量
    let processVariables = {}
    try {
        // 通用流程变量解析
        processVariables = await ProcessVariableService.resolveProcessVariables(formData, currentUser)
      
      console.log('解析后的流程变量:', processVariables)
      
      // 验证流程变量
      const validation = ProcessVariableService.validateProcessVariables(processVariables)
      if (!validation.isValid) {
        console.warn('流程变量验证警告:', validation.warnings)
        if (validation.errors.length > 0) {
          throw new Error(`流程变量验证失败: ${validation.errors.join(', ')}`)
        }
      }
      
    } catch (variableError) {
      console.error('流程变量解析失败:', variableError)
      ElMessage.warning('流程变量解析失败，使用基础变量启动')
      
      // 使用基础变量作为后备方案
      processVariables = {
        ...formData,
        employee: currentUser?.username || currentUser?.id || 'unknown',
        employeeName: currentUser?.name || currentUser?.username || '未知用户',
        manager: 'admin', // 默认管理员
        hr: 'hr', // 默认HR
        applicant: currentUser?.username || currentUser?.id || 'unknown',
        startTime: new Date().toISOString(),
        businessKey: `process_${currentUser?.id || 'unknown'}_${Date.now()}`
      }
    }
    
    // 调用启动流程API
    const startData = {
      processDefinitionId: processDefinitionId,
      variables: processVariables,
      businessKey: processVariables.businessKey || `process_${Date.now()}`
    }
    
    console.log('启动流程请求数据:', startData)
    
    const response = await processInstanceApi.startProcess(startData)
    
    if (response.data && response.data.data) {
      processInstanceId.value = response.data.data.id || response.data.data.processInstanceId
      successDialogVisible.value = true
      ElMessage.success('流程启动成功')
    } else {
      throw new Error('启动流程返回数据异常')
    }
    
  } catch (error) {
    console.error('启动流程失败:', error)
    
    // 提供更详细的错误信息
    let errorMessage = '启动流程失败'
    if (error.message) {
      if (error.message.includes('Unknown property')) {
        errorMessage = '流程变量配置错误，请检查流程定义中的变量设置'
      } else if (error.message.includes('assignee')) {
        errorMessage = '任务分配人配置错误，请检查用户和角色设置'
      } else {
        errorMessage = error.message
      }
    }
    
    ElMessage.error(errorMessage)
  } finally {
    submitting.value = false
  }
}

/**
 * 重置表单
 */
const resetForm = async () => {
  try {
    await ElMessageBox.confirm('确定要重置表单吗？', '确认重置', {
      type: 'warning'
    })
    
    if (formRendererRef.value) {
      formRendererRef.value.resetForm()
    }
    
    // 清空表单数据
    Object.keys(formData).forEach(key => {
      delete formData[key]
    })
    
    // 重新设置默认值
    if (formConfig.value.components && Array.isArray(formConfig.value.components)) {
      formConfig.value.components.forEach(component => {
        if (component.field && component.defaultValue !== undefined) {
          formData[component.field] = component.defaultValue
        }
      })
    }
    
    ElMessage.success('表单已重置')
  } catch (error) {
    // 用户取消重置
  }
}

/**
 * 表单验证成功回调
 */
const onValidateSuccess = (data) => {
  console.log('表单验证成功:', data)
}

/**
 * 表单验证失败回调
 */
const onValidateError = (error) => {
  console.error('表单验证失败:', error)
  ElMessage.error('请检查表单填写是否正确')
}

/**
 * 返回上一页
 */
const goBack = () => {
  router.back()
}

/**
 * 前往工作台
 */
const goToWorkspace = () => {
  router.push('/workspace')
  successDialogVisible.value = false
}

/**
 * 再次启动流程
 */
const startAnother = () => {
  successDialogVisible.value = false
  resetForm()
}
</script>

<style lang="scss" scoped>
.process-start-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.process-info-card {
  margin-bottom: 20px;
  
  .process-info {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    
    .process-icon {
      flex-shrink: 0;
      width: 48px;
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--el-color-primary-light-9);
      border-radius: 8px;
      color: var(--el-color-primary);
    }
    
    .process-details {
      flex: 1;
      
      h3 {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
      
      .process-description {
        margin: 0 0 12px 0;
        color: var(--el-text-color-regular);
        line-height: 1.5;
      }
      
      .process-meta {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.form-card {
  .card-header {
    h4 {
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
  
  .form-content {
    margin-bottom: 24px;
    min-height: 200px;
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.success-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  
  .success-icon {
    flex-shrink: 0;
  }
  
  .success-info {
    flex: 1;
    
    h4 {
      margin: 0 0 12px 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
    
    p {
      margin: 0 0 8px 0;
      color: var(--el-text-color-regular);
      line-height: 1.5;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .process-start-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .process-info {
    flex-direction: column;
    text-align: center;
    
    .process-icon {
      align-self: center;
    }
  }
  
  .form-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>