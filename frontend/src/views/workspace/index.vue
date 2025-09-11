<template>
  <div class="workspace-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>个人工作台</h2>
        <p class="header-subtitle">管理您的待办任务、已办任务和发起的流程</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="goToStartProcess">
          <el-icon><Plus /></el-icon>
          发起流程
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <div class="stats-icon pending">
            <el-icon size="24"><Clock /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-number">{{ statistics.pendingTasks }}</div>
            <div class="stats-label">待办任务</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <div class="stats-icon completed">
            <el-icon size="24"><Check /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-number">{{ statistics.completedTasks }}</div>
            <div class="stats-label">已办任务</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <div class="stats-icon initiated">
            <el-icon size="24"><Document /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-number">{{ statistics.initiatedProcesses }}</div>
            <div class="stats-label">发起流程</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <div class="stats-icon overdue">
            <el-icon size="24"><Warning /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-number">{{ statistics.overdueTasks }}</div>
            <div class="stats-label">逾期任务</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：待办任务和已办任务 -->
      <div class="left-panel">
        <!-- 待办任务 -->
        <el-card class="task-card" shadow="never">
          <template #header>
            <div class="card-header">
              <h4>
                <el-icon><Clock /></el-icon>
                待办任务
              </h4>
              <el-link type="primary" @click="viewAllPendingTasks">查看全部</el-link>
            </div>
          </template>
          
          <div class="task-list" v-loading="pendingTasksLoading">
            <div 
              v-for="task in pendingTasks" 
              :key="task.id"
              class="task-item"
              @click="handleTask(task.id)"
            >
              <div class="task-info">
                <div class="task-title">{{ task.name }}</div>
                <div class="task-process">{{ task.processDefinitionName }}</div>
                <div class="task-meta">
                  <el-tag size="small" type="warning">{{ task.assignee }}</el-tag>
                  <span class="task-time">{{ formatDate(task.createTime) }}</span>
                </div>
              </div>
              <div class="task-actions">
                <el-button size="small" type="primary" plain>
                  <el-icon><Edit /></el-icon>
                  处理
                </el-button>
              </div>
            </div>
            
            <el-empty 
              v-if="!pendingTasksLoading && pendingTasks.length === 0"
              description="暂无待办任务"
              :image-size="80"
            />
          </div>
        </el-card>

        <!-- 已办任务 -->
        <el-card class="task-card" shadow="never">
          <template #header>
            <div class="card-header">
              <h4>
                <el-icon><Check /></el-icon>
                已办任务
              </h4>
              <el-link type="primary" @click="viewAllCompletedTasks">查看全部</el-link>
            </div>
          </template>
          
          <div class="task-list" v-loading="completedTasksLoading">
            <div 
              v-for="task in completedTasks" 
              :key="task.id"
              class="task-item completed"
              @click="viewTask(task.id)"
            >
              <div class="task-info">
                <div class="task-title">{{ task.name }}</div>
                <div class="task-process">{{ task.processDefinitionName }}</div>
                <div class="task-meta">
                  <el-tag size="small" type="success">已完成</el-tag>
                  <span class="task-time">{{ formatDate(task.endTime) }}</span>
                </div>
              </div>
              <div class="task-actions">
                <el-button size="small" type="info" plain>
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
              </div>
            </div>
            
            <el-empty 
              v-if="!completedTasksLoading && completedTasks.length === 0"
              description="暂无已办任务"
              :image-size="80"
            />
          </div>
        </el-card>
      </div>

      <!-- 右侧：发起的流程 -->
      <div class="right-panel">
        <el-card class="process-card" shadow="never">
          <template #header>
            <div class="card-header">
              <h4>
                <el-icon><Document /></el-icon>
                我发起的流程
              </h4>
              <el-link type="primary" @click="viewAllInitiatedProcesses">查看全部</el-link>
            </div>
          </template>
          
          <div class="process-list" v-loading="initiatedProcessesLoading">
            <div 
              v-for="process in initiatedProcesses" 
              :key="process.id"
              class="process-item"
              @click="viewProcess(process.id)"
            >
              <div class="process-info">
                <div class="process-title">{{ process.processDefinitionName }}</div>
                <div class="process-business-key" v-if="process.businessKey">
                  业务键: {{ process.businessKey }}
                </div>
                <div class="process-meta">
                  <el-tag 
                    size="small" 
                    :type="getProcessStatusType(process.status)"
                  >
                    {{ getProcessStatusText(process.status) }}
                  </el-tag>
                  <span class="process-time">{{ formatDate(process.startTime) }}</span>
                </div>
              </div>
              <div class="process-actions">
                <el-button size="small" type="info" plain>
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
              </div>
            </div>
            
            <el-empty 
              v-if="!initiatedProcessesLoading && initiatedProcesses.length === 0"
              description="暂无发起的流程"
              :image-size="80"
            />
          </div>
        </el-card>

        <!-- 快速操作 -->
        <el-card class="quick-actions-card" shadow="never">
          <template #header>
            <div class="card-header">
              <h4>
                <el-icon><Operation /></el-icon>
                快速操作
              </h4>
            </div>
          </template>
          
          <div class="quick-actions">
            <el-button 
              class="quick-action-btn"
              @click="goToStartProcess"
            >
              <el-icon><Plus /></el-icon>
              <span>发起流程</span>
            </el-button>
            
            <el-button 
              class="quick-action-btn"
              @click="goToProcessDefinitions"
            >
              <el-icon><Document /></el-icon>
              <span>流程定义</span>
            </el-button>
            
            <el-button 
              class="quick-action-btn"
              @click="goToFormTemplates"
            >
              <el-icon><Edit /></el-icon>
              <span>表单模板</span>
            </el-button>
            
            <el-button 
              class="quick-action-btn"
              @click="goToFormDesigner"
            >
              <el-icon><Setting /></el-icon>
              <span>表单设计</span>
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>

  <!-- 任务详情对话框 -->
  <TaskDetailDialog
    v-model="taskDetailVisible"
    :task-id="selectedTaskId"
    :mode="taskDetailMode"
    @task-updated="handleTaskUpdated"
  />
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus, Clock, Check, Document, Warning, Edit, View, 
  Operation, Setting 
} from '@element-plus/icons-vue'
import TaskDetailDialog from '@/components/workflow/TaskDetailDialog.vue'
import { taskApi } from '@/api/task'
import { processInstanceApi } from '@/api/workflow'

// 路由
const router = useRouter()

// 响应式数据
const statistics = reactive({
  pendingTasks: 0,
  completedTasks: 0,
  initiatedProcesses: 0,
  overdueTasks: 0
})

const pendingTasks = ref([])
const completedTasks = ref([])
const initiatedProcesses = ref([])

const pendingTasksLoading = ref(false)
const completedTasksLoading = ref(false)
const initiatedProcessesLoading = ref(false)

// 任务详情对话框相关
const taskDetailVisible = ref(false)
const selectedTaskId = ref('')
const taskDetailMode = ref('pending') // pending, completed

// 页面初始化
onMounted(async () => {
  await Promise.all([
    loadStatistics(),
    loadPendingTasks(),
    loadCompletedTasks(),
    loadInitiatedProcesses()
  ])
})

/**
 * 加载统计数据
 */
const loadStatistics = async () => {
  try {
    // 这里需要调用统计API
    // 暂时使用模拟数据
    statistics.pendingTasks = 5
    statistics.completedTasks = 12
    statistics.initiatedProcesses = 8
    statistics.overdueTasks = 2
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

/**
 * 加载待办任务
 */
const loadPendingTasks = async () => {
  try {
    pendingTasksLoading.value = true
    
    const response = await taskApi.getPendingTasks({
      page: 1,
      size: 5 // 只显示前5条
    })
    
    if (response.data && response.data.data) {
      pendingTasks.value = response.data.data.records || response.data.data
      statistics.pendingTasks = pendingTasks.value.length
    }
  } catch (error) {
    console.error('加载待办任务失败:', error)
    ElMessage.error('加载待办任务失败')
  } finally {
    pendingTasksLoading.value = false
  }
}

/**
 * 加载已办任务
 */
const loadCompletedTasks = async () => {
  try {
    completedTasksLoading.value = true
    
    const response = await taskApi.getCompletedTasks({
      page: 1,
      size: 5 // 只显示前5条
    })
    
    if (response.data && response.data.data) {
      completedTasks.value = response.data.data.records || response.data.data
      statistics.completedTasks = completedTasks.value.length
    }
  } catch (error) {
    console.error('加载已办任务失败:', error)
    ElMessage.error('加载已办任务失败')
  } finally {
    completedTasksLoading.value = false
  }
}

/**
 * 加载发起的流程
 */
const loadInitiatedProcesses = async () => {
  try {
    initiatedProcessesLoading.value = true
    
    const response = await processInstanceApi.getInitiatedProcesses({
      page: 1,
      size: 5 // 只显示前5条
    })
    
    if (response.data && response.data.data) {
      initiatedProcesses.value = response.data.data.records || response.data.data
      statistics.initiatedProcesses = initiatedProcesses.value.length
    }
  } catch (error) {
    console.error('加载发起的流程失败:', error)
    ElMessage.error('加载发起的流程失败')
  } finally {
    initiatedProcessesLoading.value = false
  }
}

/**
 * 处理任务
 */
const handleTask = (taskId) => {
  router.push(`/workflow/task-handle/${taskId}`)
}

/**
 * 查看任务
 */
const viewTask = (taskId) => {
  router.push(`/workflow/task-handle/${taskId}`)
}

/**
 * 查看流程
 */
const viewProcess = (processInstanceId) => {
  router.push(`/workflow/process-instances?id=${processInstanceId}`)
}

/**
 * 查看任务详情
 */
const viewTaskDetail = (task, mode = 'pending') => {
  selectedTaskId.value = task.id
  taskDetailMode.value = mode
  taskDetailVisible.value = true
}

/**
 * 处理任务更新
 */
const handleTaskUpdated = () => {
  // 重新加载相关数据
  loadPendingTasks()
  loadCompletedTasks()
  loadStatistics()
}

/**
 * 查看全部待办任务
 */
const viewAllPendingTasks = () => {
  router.push('/workflow/tasks?type=pending')
}

/**
 * 查看全部已办任务
 */
const viewAllCompletedTasks = () => {
  router.push('/workflow/tasks?type=completed')
}

/**
 * 查看全部发起的流程
 */
const viewAllInitiatedProcesses = () => {
  router.push('/workflow/process-instances?type=initiated')
}

/**
 * 前往发起流程页面
 */
const goToStartProcess = () => {
  router.push('/workflow/start-process')
}

/**
 * 前往流程定义页面
 */
const goToProcessDefinitions = () => {
  router.push('/workflow/process-definitions')
}

/**
 * 前往表单模板页面
 */
const goToFormTemplates = () => {
  router.push('/form/templates')
}

/**
 * 前往表单设计器页面
 */
const goToFormDesigner = () => {
  router.push('/form/designer')
}

/**
 * 获取流程状态类型
 */
const getProcessStatusType = (status) => {
  const statusMap = {
    'ACTIVE': 'primary',
    'COMPLETED': 'success',
    'SUSPENDED': 'warning',
    'TERMINATED': 'danger'
  }
  return statusMap[status] || 'info'
}

/**
 * 获取流程状态文本
 */
const getProcessStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '进行中',
    'COMPLETED': '已完成',
    'SUSPENDED': '已挂起',
    'TERMINATED': '已终止'
  }
  return statusMap[status] || '未知'
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return '-'
  const now = new Date()
  const targetDate = new Date(date)
  const diffTime = now - targetDate
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    return targetDate.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return targetDate.toLocaleDateString('zh-CN')
  }
}
</script>

<style lang="scss" scoped>
.workspace-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  
  .header-left {
    h2 {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
    
    .header-subtitle {
      margin: 0;
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
  
  .stats-card {
    cursor: pointer;
    transition: transform 0.2s;
    
    &:hover {
      transform: translateY(-2px);
    }
    
    .stats-content {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .stats-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &.pending {
          background: var(--el-color-warning-light-9);
          color: var(--el-color-warning);
        }
        
        &.completed {
          background: var(--el-color-success-light-9);
          color: var(--el-color-success);
        }
        
        &.initiated {
          background: var(--el-color-primary-light-9);
          color: var(--el-color-primary);
        }
        
        &.overdue {
          background: var(--el-color-danger-light-9);
          color: var(--el-color-danger);
        }
      }
      
      .stats-info {
        flex: 1;
        
        .stats-number {
          font-size: 24px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          line-height: 1;
          margin-bottom: 4px;
        }
        
        .stats-label {
          font-size: 14px;
          color: var(--el-text-color-regular);
        }
      }
    }
  }
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.task-card, .process-card, .quick-actions-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
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
}

.task-list, .process-list {
  .task-item, .process-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      border-color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
    }
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &.completed {
      opacity: 0.8;
    }
    
    .task-info, .process-info {
      flex: 1;
      
      .task-title, .process-title {
        font-size: 14px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 4px;
      }
      
      .task-process, .process-business-key {
        font-size: 12px;
        color: var(--el-text-color-regular);
        margin-bottom: 8px;
      }
      
      .task-meta, .process-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .task-time, .process-time {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }
    
    .task-actions, .process-actions {
      flex-shrink: 0;
    }
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  
  .quick-action-btn {
    height: 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    border: 1px dashed var(--el-border-color);
    background: transparent;
    
    &:hover {
      border-color: var(--el-color-primary);
      color: var(--el-color-primary);
    }
    
    span {
      font-size: 12px;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .workspace-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .task-item, .process-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    
    .task-actions, .process-actions {
      align-self: flex-end;
    }
  }
}
</style>