<template>
  <div class="advanced-task-operations">
    <!-- 审批对话框 -->
    <el-dialog
      v-model="approvalDialogVisible"
      title="任务审批"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approvalForm.approved">
            <el-radio :label="true">同意</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApproval" :loading="approving">
          确认审批
        </el-button>
      </template>
    </el-dialog>

    <!-- 回退对话框 -->
    <el-dialog
      v-model="rollbackDialogVisible"
      title="任务回退"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="rollbackForm" label-width="100px">
        <el-form-item label="回退节点" required>
          <el-select
            v-model="rollbackForm.targetActivityId"
            placeholder="请选择回退节点"
            style="width: 100%"
          >
            <el-option
              v-for="node in rollbackNodes"
              :key="node.activityId"
              :label="node.activityName"
              :value="node.activityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="回退原因">
          <el-input
            v-model="rollbackForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入回退原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rollbackDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRollback" :loading="rollingBack">
          确认回退
        </el-button>
      </template>
    </el-dialog>

    <!-- 任务历史对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="任务历史记录"
      width="800px"
    >
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in taskHistory"
          :key="index"
          :timestamp="formatDate(item.startTime)"
          placement="top"
        >
          <el-card>
            <h4>{{ item.taskName }}</h4>
            <p><strong>处理人:</strong> {{ item.assignee || '未分配' }}</p>
            <p v-if="item.endTime">
              <strong>完成时间:</strong> {{ formatDate(item.endTime) }}
            </p>
            <p v-if="item.durationInMillis">
              <strong>耗时:</strong> {{ formatDuration(item.durationInMillis) }}
            </p>
            <div v-if="item.variables && Object.keys(item.variables).length > 0">
              <strong>变量:</strong>
              <el-tag
                v-for="(value, key) in item.variables"
                :key="key"
                style="margin: 2px"
                size="small"
              >
                {{ key }}: {{ value }}
              </el-tag>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <!-- 任务评论对话框 -->
    <el-dialog
      v-model="commentsDialogVisible"
      title="任务评论"
      width="600px"
    >
      <div class="comments-section">
        <!-- 评论列表 -->
        <div v-if="taskComments.length > 0" class="comments-list">
          <div
            v-for="comment in taskComments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-header">
              <span class="comment-user">{{ comment.userId }}</span>
              <span class="comment-time">{{ formatDate(comment.time) }}</span>
            </div>
            <div class="comment-content">{{ comment.message }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无评论" />

        <!-- 添加评论 -->
        <div class="add-comment">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="请输入评论内容"
          />
          <div style="margin-top: 10px; text-align: right;">
            <el-button @click="commentsDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="addComment" :loading="addingComment">
              添加评论
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量操作"
      width="500px"
    >
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="操作类型">
          <el-select v-model="batchForm.operation" placeholder="请选择操作类型">
            <el-option label="批量完成" value="complete" />
            <el-option label="批量分配" value="assign" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="batchForm.operation === 'assign'" label="分配给">
          <el-input v-model="batchForm.assignee" placeholder="请输入用户名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchOperation" :loading="batchProcessing">
          确认操作
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { workflowApi } from '@/api/workflow'

// Props
const props = defineProps({
  selectedTasks: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['refresh'])

// 响应式数据
const approvalDialogVisible = ref(false)
const rollbackDialogVisible = ref(false)
const historyDialogVisible = ref(false)
const commentsDialogVisible = ref(false)
const batchDialogVisible = ref(false)

const approving = ref(false)
const rollingBack = ref(false)
const addingComment = ref(false)
const batchProcessing = ref(false)

const currentTask = ref(null)
const taskHistory = ref([])
const taskComments = ref([])
const rollbackNodes = ref([])
const newComment = ref('')

// 表单数据
const approvalForm = reactive({
  approved: true,
  comment: ''
})

const rollbackForm = reactive({
  targetActivityId: '',
  reason: ''
})

const batchForm = reactive({
  operation: '',
  assignee: ''
})

// 显示审批对话框
const showApprovalDialog = async (task) => {
  currentTask.value = task
  approvalForm.approved = true
  approvalForm.comment = ''
  approvalDialogVisible.value = true
}

// 确认审批
const confirmApproval = async () => {
  try {
    approving.value = true
    await workflowApi.approveTask(currentTask.value.taskId, {
      approved: approvalForm.approved,
      comment: approvalForm.comment
    })
    
    ElMessage.success('审批成功')
    approvalDialogVisible.value = false
    emit('refresh')
  } catch (error) {
    console.error('审批失败:', error)
    ElMessage.error('审批失败: ' + (error.message || '未知错误'))
  } finally {
    approving.value = false
  }
}

// 显示回退对话框
const showRollbackDialog = async (task) => {
  currentTask.value = task
  rollbackForm.targetActivityId = ''
  rollbackForm.reason = ''
  
  try {
    // 获取可回退的节点
    const response = await workflowApi.getRollbackNodes(task.taskId)
    rollbackNodes.value = response.data || []
    rollbackDialogVisible.value = true
  } catch (error) {
    console.error('获取回退节点失败:', error)
    ElMessage.error('获取回退节点失败')
  }
}

// 确认回退
const confirmRollback = async () => {
  if (!rollbackForm.targetActivityId) {
    ElMessage.error('请选择回退节点')
    return
  }

  try {
    rollingBack.value = true
    await workflowApi.rollbackTask(currentTask.value.taskId, {
      targetActivityId: rollbackForm.targetActivityId,
      reason: rollbackForm.reason
    })
    
    ElMessage.success('回退成功')
    rollbackDialogVisible.value = false
    emit('refresh')
  } catch (error) {
    console.error('回退失败:', error)
    ElMessage.error('回退失败: ' + (error.message || '未知错误'))
  } finally {
    rollingBack.value = false
  }
}

// 显示任务历史
const showTaskHistory = async (task) => {
  currentTask.value = task
  
  try {
    const response = await workflowApi.getTaskHistory(task.taskId)
    taskHistory.value = response.data || []
    historyDialogVisible.value = true
  } catch (error) {
    console.error('获取任务历史失败:', error)
    ElMessage.error('获取任务历史失败')
  }
}

// 显示任务评论
const showTaskComments = async (task) => {
  currentTask.value = task
  newComment.value = ''
  
  try {
    const response = await workflowApi.getTaskComments(task.taskId)
    taskComments.value = response.data || []
    commentsDialogVisible.value = true
  } catch (error) {
    console.error('获取任务评论失败:', error)
    ElMessage.error('获取任务评论失败')
  }
}

// 添加评论
const addComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.error('请输入评论内容')
    return
  }

  try {
    addingComment.value = true
    await workflowApi.addTaskComment(currentTask.value.taskId, {
      comment: newComment.value
    })
    
    ElMessage.success('评论添加成功')
    newComment.value = ''
    // 重新获取评论列表
    showTaskComments(currentTask.value)
  } catch (error) {
    console.error('添加评论失败:', error)
    ElMessage.error('添加评论失败: ' + (error.message || '未知错误'))
  } finally {
    addingComment.value = false
  }
}

// 显示批量操作对话框
const showBatchDialog = () => {
  if (props.selectedTasks.length === 0) {
    ElMessage.error('请先选择要操作的任务')
    return
  }
  
  batchForm.operation = ''
  batchForm.assignee = ''
  batchDialogVisible.value = true
}

// 确认批量操作
const confirmBatchOperation = async () => {
  if (!batchForm.operation) {
    ElMessage.error('请选择操作类型')
    return
  }

  if (batchForm.operation === 'assign' && !batchForm.assignee) {
    ElMessage.error('请输入分配人')
    return
  }

  try {
    batchProcessing.value = true
    const taskIds = props.selectedTasks.map(task => task.taskId)

    if (batchForm.operation === 'complete') {
      await workflowApi.batchCompleteTasks({ taskIds })
      ElMessage.success('批量完成成功')
    } else if (batchForm.operation === 'assign') {
      await workflowApi.batchAssignTasks({
        taskIds,
        assignee: batchForm.assignee
      })
      ElMessage.success('批量分配成功')
    }

    batchDialogVisible.value = false
    emit('refresh')
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败: ' + (error.message || '未知错误'))
  } finally {
    batchProcessing.value = false
  }
}

// 工具函数
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const formatDuration = (milliseconds) => {
  if (!milliseconds) return '-'
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (days > 0) return `${days}天${hours % 24}小时`
  if (hours > 0) return `${hours}小时${minutes % 60}分钟`
  if (minutes > 0) return `${minutes}分钟${seconds % 60}秒`
  return `${seconds}秒`
}

// 暴露方法给父组件
defineExpose({
  showApprovalDialog,
  showRollbackDialog,
  showTaskHistory,
  showTaskComments,
  showBatchDialog
})
</script>

<style scoped>
.advanced-task-operations {
  /* 组件样式 */
}

.comments-section {
  max-height: 400px;
  overflow-y: auto;
}

.comments-list {
  margin-bottom: 20px;
}

.comment-item {
  border-bottom: 1px solid #ebeef5;
  padding: 10px 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 12px;
  color: #909399;
}

.comment-user {
  font-weight: bold;
}

.comment-content {
  color: #303133;
  line-height: 1.5;
}

.add-comment {
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}
</style>