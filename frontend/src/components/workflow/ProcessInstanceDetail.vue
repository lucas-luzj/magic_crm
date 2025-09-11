<template>
  <div class="process-instance-detail" v-loading="loading">
    <!-- 流程基本信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><InfoFilled /></el-icon>
            流程信息
          </h4>
        </div>
      </template>
      
      <div class="info-grid">
        <div class="info-item">
          <label>流程名称：</label>
          <span>{{ processInstance.processDefinitionName }}</span>
        </div>
        <div class="info-item">
          <label>实例ID：</label>
          <span>{{ processInstance.id }}</span>
        </div>
        <div class="info-item">
          <label>业务键：</label>
          <span>{{ processInstance.businessKey || '-' }}</span>
        </div>
        <div class="info-item">
          <label>发起人：</label>
          <span>{{ processInstance.startUserName || '系统' }}</span>
        </div>
        <div class="info-item">
          <label>发起时间：</label>
          <span>{{ formatDate(processInstance.startTime) }}</span>
        </div>
        <div class="info-item">
          <label>结束时间：</label>
          <span>{{ formatDate(processInstance.endTime) || '-' }}</span>
        </div>
        <div class="info-item">
          <label>状态：</label>
          <el-tag :type="getStatusType(processInstance.status)">
            {{ getStatusText(processInstance.status) }}
          </el-tag>
        </div>
        <div class="info-item">
          <label>运行时长：</label>
          <span>{{ formatDuration(processInstance.startTime, processInstance.endTime) }}</span>
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

    <!-- 当前任务 -->
    <el-card class="tasks-card" shadow="never" v-if="currentTasks.length > 0">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><Clock /></el-icon>
            当前任务
          </h4>
        </div>
      </template>
      
      <el-table :data="currentTasks" stripe>
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column prop="assignee" label="处理人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="handleTask(row)">
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
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
              <p v-if="item.duration">耗时: {{ formatDuration(item.startTime, item.endTime) }}</p>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 流程图 -->
    <el-card class="diagram-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>
            <el-icon><View /></el-icon>
            流程图
          </h4>
        </div>
      </template>
      
      <div class="diagram-container">
        <img 
          v-if="diagramUrl" 
          :src="diagramUrl" 
          alt="流程图" 
          style="max-width: 100%; height: auto;" 
        />
        <el-empty v-else description="暂无流程图" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  InfoFilled, Operation, Clock, View 
} from '@element-plus/icons-vue'
import { processInstanceApi, processDefinitionApi } from '@/api/workflow'

// Props
const props = defineProps({
  processInstance: {
    type: Object,
    required: true
  }
})

// Emits
const emit = defineEmits(['process-updated'])

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const processVariables = ref({})
const currentTasks = ref([])
const processHistory = ref([])
const diagramUrl = ref('')

// 页面初始化
onMounted(async () => {
  await loadProcessDetails()
})

/**
 * 加载流程详情
 */
const loadProcessDetails = async () => {
  try {
    loading.value = true
    
    // 并行加载各种数据
    await Promise.all([
      loadProcessVariables(),
      loadCurrentTasks(),
      loadProcessHistory(),
      loadProcessDiagram()
    ])
  } catch (error) {
    console.error('加载流程详情失败:', error)
    ElMessage.error('加载流程详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载流程变量
 */
const loadProcessVariables = async () => {
  try {
    const response = await processInstanceApi.getProcessVariables(props.processInstance.id)
    if (response.data && response.data.data) {
      processVariables.value = response.data.data
    }
  } catch (error) {
    console.error('加载流程变量失败:', error)
  }
}

/**
 * 加载当前任务
 */
const loadCurrentTasks = async () => {
  try {
    const response = await processInstanceApi.getCurrentTasks(props.processInstance.id)
    if (response.data && response.data.data) {
      currentTasks.value = response.data.data
    }
  } catch (error) {
    console.error('加载当前任务失败:', error)
  }
}

/**
 * 加载流程历史
 */
const loadProcessHistory = async () => {
  try {
    const response = await processInstanceApi.getProcessHistory(props.processInstance.id)
    if (response.data && response.data.data) {
      processHistory.value = response.data.data
    }
  } catch (error) {
    console.error('加载流程历史失败:', error)
  }
}

/**
 * 加载流程图
 */
const loadProcessDiagram = async () => {
  try {
    const response = await processDefinitionApi.getProcessDefinitionDiagram(
      props.processInstance.processDefinitionId
    )
    diagramUrl.value = URL.createObjectURL(response)
  } catch (error) {
    console.error('加载流程图失败:', error)
  }
}

/**
 * 处理任务
 */
const handleTask = (task) => {
  router.push(`/workflow/task-handle/${task.id}`)
}

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const typeMap = {
    'ACTIVE': 'primary',
    'COMPLETED': 'success',
    'SUSPENDED': 'warning',
    'TERMINATED': 'danger'
  }
  return typeMap[status] || 'info'
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const textMap = {
    'ACTIVE': '运行中',
    'COMPLETED': '已完成',
    'SUSPENDED': '已挂起',
    'TERMINATED': '已终止'
  }
  return textMap[status] || '未知'
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
const formatDuration = (startTime, endTime) => {
  if (!startTime) return '-'
  
  const start = new Date(startTime)
  const end = endTime ? new Date(endTime) : new Date()
  const diff = end.getTime() - start.getTime()
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (days > 0) return `${days}天${hours}小时`
  if (hours > 0) return `${hours}小时${minutes}分钟`
  return `${minutes}分钟`
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
</script>

<style lang="scss" scoped>
.process-instance-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
    color: var(--el-text-color-primary);
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    
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
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
    
    .activity-name {
      font-weight: 500;
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

.diagram-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}

// 响应式设计
@media (max-width: 768px) {
  .info-grid,
  .variables-grid {
    grid-template-columns: 1fr;
  }
}
</style>
