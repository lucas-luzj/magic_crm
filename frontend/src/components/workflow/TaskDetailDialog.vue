<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="task-detail-container" v-loading="loading">
      <!-- 任务基本信息 -->
      <el-card class="task-info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <h4>
              <el-icon><Document /></el-icon>
              任务信息
            </h4>
          </div>
        </template>
        
        <div class="task-info-grid" v-if="taskDetail">
          <div class="info-item">
            <label>任务名称：</label>
            <span>{{ taskDetail.name }}</span>
          </div>
          <div class="info-item">
            <label>流程名称：</label>
            <span>{{ taskDetail.processDefinitionName }}</span>
          </div>
          <div class="info-item">
            <label>当前处理人：</label>
            <span>{{ taskDetail.assignee || '未分配' }}</span>
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDate(taskDetail.createTime) }}</span>
          </div>
          <div class="info-item">
            <label>到期时间：</label>
            <span>{{ taskDetail.dueDate ? formatDate(taskDetail.dueDate) : '无' }}</span>
          </div>
          <div class="info-item">
            <label>优先级：</label>
            <el-tag :type="getPriorityType(taskDetail.priority)">
              {{ getPriorityText(taskDetail.priority) }}
            </el-tag>
          </div>
          <div class="info-item full-width" v-if="taskDetail.description">
            <label>任务描述：</label>
            <p>{{ taskDetail.description }}</p>
          </div>
        </div>
      </el-card>

      <!-- 流程变量 -->
      <el-card class="variables-card" shadow="never" v-if="processVariables && Object.keys(processVariables).length > 0">
        <template #header>
          <div class="card-header">
            <h4>
              <el-icon><Operation /></el-icon>
              流程变量
            </h4>
          </div>
        </template>
        
        <div class="variables-grid">
          <div 
            v-for="(value, key) in processVariables" 
            :key="key"
            class="variable-item"
          >
            <label>{{ key }}：</label>
            <span>{{ formatVariableValue(value) }}</span>
          </div>
        </div>
      </el-card>

      <!-- 动态表单 -->
      <el-card class="form-card" shadow="never" v-if="formConfig">
        <template #header>
          <div class="card-header">
            <h4>
              <el-icon><Edit /></el-icon>
              {{ formConfig.formName || '任务表单' }}
            </h4>
          </div>
        </template>
        
        <FormRenderer
          ref="formRendererRef"
          :config="formConfig"
          :data="formData"
          @update:data="handleFormDataChange"
        />
      </el-card>

      <!-- 审批意见 -->
      <el-card class="comment-card" shadow="never">
        <template #header>
          <div class="card-header">
            <h4>
              <el-icon><ChatDotRound /></el-icon>
              审批意见
            </h4>
          </div>
        </template>
        
        <el-input
          v-model="comment"
          type="textarea"
          :rows="4"
          placeholder="请输入审批意见..."
          maxlength="500"
          show-word-limit
        />
      </el-card>

      <!-- 历史记录 -->
      <el-card class="history-card" shadow="never" v-if="taskHistory.length > 0">
        <template #header>
          <div class="card-header">
            <h4>
              <el-icon><Clock /></el-icon>
              处理历史
            </h4>
          </div>
        </template>
        
        <el-timeline>
          <el-timeline-item
            v-for="item in taskHistory"
            :key="item.id"
            :timestamp="formatDate(item.endTime)"
            placement="top"
          >
            <div class="history-item">
              <div class="history-header">
                <span class="task-name">{{ item.taskName }}</span>
                <el-tag size="small" :type="getTaskStatusType(item.deleteReason)">
                  {{ getTaskStatusText(item.deleteReason) }}
                </el-tag>
              </div>
              <div class="history-content">
                <p><strong>处理人：</strong>{{ item.assignee }}</p>
                <p v-if="item.comment"><strong>意见：</strong>{{ item.comment }}</p>
                <p><strong>处理时间：</strong>{{ formatDate(item.endTime) }}</p>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          v-if="!taskDetail?.assignee && mode === 'pending'"
          type="warning" 
          @click="handleClaim"
          :loading="actionLoading"
        >
          认领任务
        </el-button>
        <el-button 
          v-if="taskDetail?.assignee && mode === 'pending'"
          type="danger" 
          @click="handleUnclaim"
          :loading="actionLoading"
        >
          取消认领
        </el-button>
        <el-button 
          v-if="mode === 'pending'"
          type="primary" 
          @click="handleComplete"
          :loading="actionLoading"
        >
          完成任务
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Operation, Edit, Clock, ChatDotRound 
} from '@element-plus/icons-vue'
import FormRenderer from '@/components/FormDesigner/FormRenderer.vue'
import { taskApi } from '@/api/task'
import { FormConfigService } from '@/services/formConfigService'

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  taskId: {
    type: String,
    default: ''
  },
  mode: {
    type: String,
    default: 'pending', // pending, completed
    validator: (value) => ['pending', 'completed'].includes(value)
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'task-updated'])

// 响应式数据
const loading = ref(false)
const actionLoading = ref(false)
const taskDetail = ref(null)
const processVariables = ref({})
const formConfig = ref(null)
const formData = ref({})
const comment = ref('')
const taskHistory = ref([])
const formRendererRef = ref(null)

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const dialogTitle = computed(() => {
  if (!taskDetail.value) return '任务详情'
  return `${taskDetail.value.name} - ${props.mode === 'pending' ? '待办任务' : '已办任务'}`
})

// 监听器
watch(() => props.taskId, (newTaskId) => {
  if (newTaskId && visible.value) {
    loadTaskDetail()
  }
}, { immediate: true })

watch(visible, (newVisible) => {
  if (newVisible && props.taskId) {
    loadTaskDetail()
  } else if (!newVisible) {
    resetData()
  }
})

/**
 * 加载任务详情
 */
const loadTaskDetail = async () => {
  if (!props.taskId) return
  
  try {
    loading.value = true
    
    // 加载任务详情
    const response = await taskApi.getTaskDetail(props.taskId)
    if (response.data && response.data.data) {
      taskDetail.value = response.data.data
      processVariables.value = response.data.data.processVariables || {}
      
      // 加载表单配置
      await loadFormConfig()
      
      // 加载任务历史
      await loadTaskHistory()
    }
  } catch (error) {
    console.error('加载任务详情失败:', error)
    ElMessage.error('加载任务详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载表单配置
 */
const loadFormConfig = async () => {
  if (!taskDetail.value) return
  
  try {
    const config = await FormConfigService.getFormConfig(
      taskDetail.value.processDefinitionId,
      taskDetail.value.formKey || 'default'
    )
    
    if (config) {
      formConfig.value = config
      // 初始化表单数据
      formData.value = { ...processVariables.value }
    }
  } catch (error) {
    console.error('加载表单配置失败:', error)
    // 表单配置加载失败不影响任务详情显示
  }
}

/**
 * 加载任务历史
 */
const loadTaskHistory = async () => {
  if (!props.taskId) return
  
  try {
    const response = await taskApi.getTaskHistory(props.taskId)
    if (response.data && response.data.data) {
      taskHistory.value = response.data.data
    }
  } catch (error) {
    console.error('加载任务历史失败:', error)
    // 历史记录加载失败不影响任务详情显示
  }
}

/**
 * 认领任务
 */
const handleClaim = async () => {
  try {
    actionLoading.value = true
    await taskApi.claimTask(props.taskId)
    ElMessage.success('任务认领成功')
    emit('task-updated')
    await loadTaskDetail() // 重新加载任务详情
  } catch (error) {
    console.error('认领任务失败:', error)
    ElMessage.error('认领任务失败')
  } finally {
    actionLoading.value = false
  }
}

/**
 * 取消认领任务
 */
const handleUnclaim = async () => {
  try {
    await ElMessageBox.confirm('确定要取消认领此任务吗？', '确认操作', {
      type: 'warning'
    })
    
    actionLoading.value = true
    await taskApi.unclaimTask(props.taskId)
    ElMessage.success('取消认领成功')
    emit('task-updated')
    await loadTaskDetail() // 重新加载任务详情
  } catch (error) {
    if (error === 'cancel') return
    console.error('取消认领失败:', error)
    ElMessage.error('取消认领失败')
  } finally {
    actionLoading.value = false
  }
}

/**
 * 完成任务
 */
const handleComplete = async () => {
  try {
    // 验证表单
    if (formRendererRef.value) {
      const isValid = await formRendererRef.value.validate()
      if (!isValid) {
        ElMessage.error('请完善表单信息')
        return
      }
    }
    
    await ElMessageBox.confirm('确定要完成此任务吗？', '确认操作', {
      type: 'warning'
    })
    
    actionLoading.value = true
    
    const taskData = {
      variables: formData.value,
      comment: comment.value,
      formData: formData.value
    }
    
    await taskApi.completeTask(props.taskId, taskData)
    ElMessage.success('任务完成成功')
    emit('task-updated')
    handleClose()
  } catch (error) {
    if (error === 'cancel') return
    console.error('完成任务失败:', error)
    ElMessage.error('完成任务失败')
  } finally {
    actionLoading.value = false
  }
}

/**
 * 处理表单数据变化
 */
const handleFormDataChange = (data) => {
  formData.value = { ...data }
}

/**
 * 关闭对话框
 */
const handleClose = () => {
  visible.value = false
}

/**
 * 重置数据
 */
const resetData = () => {
  taskDetail.value = null
  processVariables.value = {}
  formConfig.value = null
  formData.value = {}
  comment.value = ''
  taskHistory.value = []
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

/**
 * 格式化变量值
 */
const formatVariableValue = (value) => {
  if (value === null || value === undefined) return '空'
  if (typeof value === 'object') return JSON.stringify(value)
  return String(value)
}

/**
 * 获取优先级类型
 */
const getPriorityType = (priority) => {
  if (priority >= 80) return 'danger'
  if (priority >= 50) return 'warning'
  return 'info'
}

/**
 * 获取优先级文本
 */
const getPriorityText = (priority) => {
  if (priority >= 80) return '高'
  if (priority >= 50) return '中'
  return '低'
}

/**
 * 获取任务状态类型
 */
const getTaskStatusType = (deleteReason) => {
  if (deleteReason === 'completed') return 'success'
  if (deleteReason === 'cancelled') return 'danger'
  return 'info'
}

/**
 * 获取任务状态文本
 */
const getTaskStatusText = (deleteReason) => {
  if (deleteReason === 'completed') return '已完成'
  if (deleteReason === 'cancelled') return '已取消'
  return '处理中'
}
</script>

<style scoped>
.task-detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  
  h4 {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
  }
}

.task-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  
  .info-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    
    &.full-width {
      grid-column: 1 / -1;
      flex-direction: column;
      
      p {
        margin: 4px 0 0 0;
        color: var(--el-text-color-regular);
        line-height: 1.5;
      }
    }
    
    label {
      font-weight: 500;
      color: var(--el-text-color-primary);
      white-space: nowrap;
      min-width: 80px;
    }
    
    span {
      color: var(--el-text-color-regular);
      word-break: break-all;
    }
  }
}

.variables-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  
  .variable-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    background: var(--el-fill-color-lighter);
    border-radius: 4px;
    
    label {
      font-weight: 500;
      color: var(--el-text-color-primary);
      white-space: nowrap;
      min-width: 80px;
    }
    
    span {
      color: var(--el-text-color-regular);
      word-break: break-all;
    }
  }
}

.history-item {
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .task-name {
      font-weight: 500;
      color: var(--el-text-color-primary);
    }
  }
  
  .history-content {
    p {
      margin: 4px 0;
      font-size: 14px;
      color: var(--el-text-color-regular);
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .task-info-grid,
  .variables-grid {
    grid-template-columns: 1fr;
  }
}
</style>