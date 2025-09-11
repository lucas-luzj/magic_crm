<template>
  <div class="task-handle-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/workspace' }">个人工作台</el-breadcrumb-item>
        <el-breadcrumb-item>任务处理</el-breadcrumb-item>
      </el-breadcrumb>
      
      <div class="header-actions">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>

    <!-- 任务信息卡片 -->
    <el-card class="task-info-card" shadow="never">
      <div class="task-info">
        <div class="task-icon">
          <el-icon size="32"><Document /></el-icon>
        </div>
        <div class="task-details">
          <h3>{{ taskInfo.name }}</h3>
          <p class="task-description">{{ taskInfo.description || '暂无描述' }}</p>
          <div class="task-meta">
            <el-tag size="small" type="primary">{{ taskInfo.processDefinitionName }}</el-tag>
            <el-tag size="small" type="info">创建时间: {{ formatDate(taskInfo.createTime) }}</el-tag>
            <el-tag size="small" type="warning" v-if="taskInfo.dueDate">
              截止时间: {{ formatDate(taskInfo.dueDate) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 流程信息 -->
    <el-card class="process-info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><InfoFilled /></el-icon>
            流程信息
          </h4>
        </div>
      </template>
      
      <div class="process-info">
        <div class="info-item">
          <label>流程名称:</label>
          <span>{{ processInfo.processDefinitionName }}</span>
        </div>
        <div class="info-item">
          <label>流程实例ID:</label>
          <span>{{ processInfo.processInstanceId }}</span>
        </div>
        <div class="info-item">
          <label>发起人:</label>
          <span>{{ processInfo.startUserName || '系统' }}</span>
        </div>
        <div class="info-item">
          <label>发起时间:</label>
          <span>{{ formatDate(processInfo.startTime) }}</span>
        </div>
        <div class="info-item" v-if="processInfo.businessKey">
          <label>业务键:</label>
          <span>{{ processInfo.businessKey }}</span>
        </div>
      </div>
    </el-card>

    <!-- 任务表单 -->
    <el-card class="form-card" shadow="never" v-loading="formLoading">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><Edit /></el-icon>
            {{ formConfig.formName || '任务表单' }}
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
          :disabled="readonly"
          @validate-success="onValidateSuccess"
          @validate-error="onValidateError"
        />
        
        <!-- 如果没有表单配置，显示提示信息 -->
        <el-empty
          v-else
          description="该任务未配置表单"
          :image-size="120"
        >
          <template #image>
            <el-icon size="120" color="#e6e6e6"><Document /></el-icon>
          </template>
        </el-empty>
      </div>

      <!-- 审批意见 -->
      <div class="approval-section" v-if="!readonly">
        <h5>审批意见</h5>
        <el-input
          v-model="approvalComment"
          type="textarea"
          :rows="4"
          placeholder="请输入审批意见（可选）"
          maxlength="500"
          show-word-limit
        />
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions" v-if="!readonly">
        <el-button @click="resetForm" :disabled="submitting">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button 
          @click="handleTask('reject')" 
          :loading="submitting"
          type="danger"
          v-if="showRejectButton"
        >
          <el-icon><Close /></el-icon>
          驳回
        </el-button>
        <el-button 
          type="primary" 
          @click="handleTask('approve')" 
          :loading="submitting"
        >
          <el-icon><Check /></el-icon>
          通过
        </el-button>
      </div>
      
      <!-- 只读模式提示 -->
      <div class="readonly-tip" v-else>
        <el-alert
          title="该任务为只读模式"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </el-card>

    <!-- 流程历史 -->
    <el-card class="history-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><Clock /></el-icon>
            流程历史
          </h4>
        </div>
      </template>
      
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in processHistory"
          :key="index"
          :timestamp="formatDate(item.endTime || item.startTime)"
          :type="getHistoryItemType(item)"
        >
          <div class="history-item">
            <div class="history-header">
              <span class="activity-name">{{ item.activityName }}</span>
              <el-tag size="small" :type="getHistoryTagType(item)">
                {{ getHistoryStatus(item) }}
              </el-tag>
            </div>
            <div class="history-details">
              <p v-if="item.assignee">处理人: {{ item.assigneeName || item.assignee }}</p>
              <p v-if="item.comment">意见: {{ item.comment }}</p>
              <p v-if="item.duration">耗时: {{ formatDuration(item.duration) }}</p>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 处理成功对话框 -->
    <el-dialog
      v-model="successDialogVisible"
      title="任务处理成功"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="success-content">
        <div class="success-icon">
          <el-icon size="48" color="#67c23a"><CircleCheck /></el-icon>
        </div>
        <div class="success-info">
          <h4>任务已成功处理</h4>
          <p>处理结果: {{ lastHandleResult }}</p>
          <p>您可以在个人工作台查看其他待办任务</p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="goToWorkspace">返回工作台</el-button>
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
  ArrowLeft, Document, Edit, Refresh, Check, Close, 
  InfoFilled, Clock, CircleCheck 
} from '@element-plus/icons-vue'
import FormRenderer from '@/components/FormDesigner/FormRenderer.vue'
import { FormConfigService } from '@/services/formConfigService'
import { taskApi, processInstanceApi } from '@/api/workflow'

// 路由和状态
const route = useRoute()
const router = useRouter()
const taskId = route.params.id

// 响应式数据
const taskInfo = ref({})
const processInfo = ref({})
const formConfig = ref({
  components: [],
  labelPosition: 'left',
  labelWidth: 100,
  size: 'default'
})
const formData = reactive({})
const approvalComment = ref('')
const processHistory = ref([])
const formLoading = ref(false)
const submitting = ref(false)
const successDialogVisible = ref(false)
const lastHandleResult = ref('')
const readonly = ref(false)

// 表单引用
const formRendererRef = ref()

// 计算属性
const showRejectButton = computed(() => {
  // 根据任务类型决定是否显示驳回按钮
  return taskInfo.value.taskDefinitionKey !== 'start'
})

// 页面初始化
onMounted(async () => {
  await loadTaskInfo()
  await loadProcessInfo()
  await loadTaskForm()
  await loadProcessHistory()
})

/**
 * 加载任务信息
 */
const loadTaskInfo = async () => {
  try {
    const response = await taskApi.getTask(taskId)
    if (response.data && response.data.data) {
      taskInfo.value = response.data.data
      
      // 检查任务是否已完成或被其他人处理
      if (taskInfo.value.endTime || taskInfo.value.assignee !== getCurrentUserId()) {
        readonly.value = true
      }
    }
  } catch (error) {
    console.error('加载任务信息失败:', error)
    ElMessage.error('加载任务信息失败')
  }
}

/**
 * 加载流程实例信息
 */
const loadProcessInfo = async () => {
  try {
    if (taskInfo.value.processInstanceId) {
      const response = await processInstanceApi.getProcessInstance(taskInfo.value.processInstanceId)
      if (response.data && response.data.data) {
        processInfo.value = response.data.data
      }
    }
  } catch (error) {
    console.error('加载流程信息失败:', error)
  }
}

/**
 * 加载任务表单配置
 */
const loadTaskForm = async () => {
  try {
    formLoading.value = true
    console.log('加载任务表单配置:', taskId)
    
    // 这里需要根据任务获取对应的表单配置
    // 暂时使用FormConfigService的getTaskFormConfig方法
    const config = await FormConfigService.getTaskFormConfig(taskId)
    console.log('获取到的任务表单配置:', config)
    
    formConfig.value = config
    
    // 如果任务有已填写的数据，需要加载
    if (taskInfo.value.variables) {
      Object.assign(formData, taskInfo.value.variables)
    }
    
  } catch (error) {
    console.error('加载任务表单失败:', error)
    ElMessage.warning('该任务未配置表单')
  } finally {
    formLoading.value = false
  }
}

/**
 * 加载流程历史
 */
const loadProcessHistory = async () => {
  try {
    if (taskInfo.value.processInstanceId) {
      const response = await processInstanceApi.getProcessHistory(taskInfo.value.processInstanceId)
      if (response.data && response.data.data) {
        processHistory.value = response.data.data
      }
    }
  } catch (error) {
    console.error('加载流程历史失败:', error)
  }
}

/**
 * 处理任务
 */
const handleTask = async (action) => {
  try {
    submitting.value = true
    
    // 如果有表单，先验证表单
    if (formRendererRef.value && formConfig.value.components.length > 0) {
      await formRendererRef.value.validate()
    }
    
    console.log('处理任务:', { action, formData, comment: approvalComment.value })
    
    // 准备处理数据
    const handleData = {
      taskId: taskId,
      action: action,
      variables: { ...formData },
      comment: approvalComment.value
    }
    
    // 调用任务处理API
    const response = await taskApi.handleTask(handleData)
    
    if (response.data && response.data.success) {
      lastHandleResult.value = action === 'approve' ? '通过' : '驳回'
      successDialogVisible.value = true
      ElMessage.success('任务处理成功')
    } else {
      throw new Error('处理任务返回数据异常')
    }
    
  } catch (error) {
    console.error('处理任务失败:', error)
    ElMessage.error(error.message || '处理任务失败')
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
    
    // 清空审批意见
    approvalComment.value = ''
    
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
 * 获取当前用户ID
 */
const getCurrentUserId = () => {
  // 这里需要从用户状态或token中获取当前用户ID
  return 'current_user_id'
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

/**
 * 格式化持续时间
 */
const formatDuration = (duration) => {
  if (!duration) return '-'
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}小时${minutes}分钟`
}

/**
 * 获取历史项目类型
 */
const getHistoryItemType = (item) => {
  if (item.endTime) return 'success'
  if (item.startTime) return 'primary'
  return 'info'
}

/**
 * 获取历史标签类型
 */
const getHistoryTagType = (item) => {
  if (item.endTime) return 'success'
  if (item.startTime) return 'warning'
  return 'info'
}

/**
 * 获取历史状态
 */
const getHistoryStatus = (item) => {
  if (item.endTime) return '已完成'
  if (item.startTime) return '进行中'
  return '待处理'
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
</script>

<style lang="scss" scoped>
.task-handle-container {
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

.task-info-card {
  margin-bottom: 20px;
  
  .task-info {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    
    .task-icon {
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
    
    .task-details {
      flex: 1;
      
      h3 {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
      
      .task-description {
        margin: 0 0 12px 0;
        color: var(--el-text-color-regular);
        line-height: 1.5;
      }
      
      .task-meta {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }
  }
}

.process-info-card {
  margin-bottom: 20px;
  
  .process-info {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
    
    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      
      label {
        font-weight: 600;
        color: var(--el-text-color-regular);
        min-width: 80px;
      }
      
      span {
        color: var(--el-text-color-primary);
      }
    }
  }
}

.form-card {
  margin-bottom: 20px;
  
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
  
  .approval-section {
    margin-bottom: 24px;
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
    
    h5 {
      margin: 0 0 12px 0;
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
  
  .readonly-tip {
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.history-card {
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
  
  .history-item {
    .history-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .activity-name {
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }
    
    .history-details {
      p {
        margin: 4px 0;
        color: var(--el-text-color-regular);
        font-size: 13px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
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
  .task-handle-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .task-info {
    flex-direction: column;
    text-align: center;
    
    .task-icon {
      align-self: center;
    }
  }
  
  .process-info {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>