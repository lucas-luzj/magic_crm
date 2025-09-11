<template>
  <div class="process-instances-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>流程实例管理</h2>
        <p class="header-subtitle">管理和监控所有流程实例的运行状态</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="goToStartProcess">
          <el-icon><Plus /></el-icon>
          发起流程
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="流程定义">
          <el-select v-model="filterForm.processDefinitionKey" placeholder="选择流程" clearable>
            <el-option label="全部流程" value="" />
            <el-option 
              v-for="process in processDefinitions" 
              :key="process.key" 
              :label="process.name" 
              :value="process.key" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
            <el-option label="全部状态" value="" />
            <el-option label="运行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已挂起" value="SUSPENDED" />
            <el-option label="已终止" value="TERMINATED" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="发起人">
          <el-input v-model="filterForm.startUser" placeholder="输入发起人" clearable />
        </el-form-item>
        
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 流程实例列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>流程实例列表</h4>
          <div class="header-actions">
            <el-button @click="refreshList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="processInstances"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="实例ID" width="120" show-overflow-tooltip />
        
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="businessKey" label="业务键" width="120" show-overflow-tooltip />
        
        <el-table-column prop="startUserName" label="发起人" width="100" />
        
        <el-table-column prop="startTime" label="发起时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="duration" label="运行时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.startTime, row.endTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewProcess(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="suspendProcess(row)"
              v-if="row.status === 'ACTIVE'"
            >
              <el-icon><VideoPause /></el-icon>
              挂起
            </el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="activateProcess(row)"
              v-if="row.status === 'SUSPENDED'"
            >
              <el-icon><VideoPlay /></el-icon>
              激活
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="terminateProcess(row)"
              v-if="row.status === 'ACTIVE'"
            >
              <el-icon><Close /></el-icon>
              终止
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 流程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`流程实例详情 - ${selectedProcess?.processDefinitionName}`"
      width="80%"
      top="5vh"
    >
      <ProcessInstanceDetail
        v-if="selectedProcess"
        :process-instance="selectedProcess"
        @process-updated="handleProcessUpdated"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Refresh, View, VideoPause, VideoPlay, Close 
} from '@element-plus/icons-vue'
import ProcessInstanceDetail from '@/components/workflow/ProcessInstanceDetail.vue'
import { processInstanceApi, processDefinitionApi } from '@/api/workflow'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const processInstances = ref([])
const processDefinitions = ref([])
const selectedProcesses = ref([])
const detailDialogVisible = ref(false)
const selectedProcess = ref(null)

// 筛选表单
const filterForm = reactive({
  processDefinitionKey: '',
  status: '',
  startUser: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 页面初始化
onMounted(async () => {
  await loadProcessDefinitions()
  await loadProcessInstances()
})

/**
 * 加载流程定义列表
 */
const loadProcessDefinitions = async () => {
  try {
    const response = await processDefinitionApi.getAllProcessDefinitions()
    if (response.data && response.data.data) {
      processDefinitions.value = response.data.data
    }
  } catch (error) {
    console.error('加载流程定义失败:', error)
  }
}

/**
 * 加载流程实例列表
 */
const loadProcessInstances = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.page,
      size: pagination.size,
      processDefinitionKey: filterForm.processDefinitionKey,
      status: filterForm.status,
      startUser: filterForm.startUser
    }
    
    // 处理时间范围
    if (filterForm.dateRange && filterForm.dateRange.length === 2) {
      params.startTime = filterForm.dateRange[0]
      params.endTime = filterForm.dateRange[1]
    }
    
    const response = await processInstanceApi.getProcessInstances(params)
    
    if (response.data && response.data.data) {
      processInstances.value = response.data.data.records || response.data.data
      pagination.total = response.data.data.total || 0
    }
  } catch (error) {
    console.error('加载流程实例失败:', error)
    ElMessage.error('加载流程实例失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  loadProcessInstances()
}

/**
 * 重置筛选条件
 */
const handleReset = () => {
  Object.assign(filterForm, {
    processDefinitionKey: '',
    status: '',
    startUser: '',
    dateRange: []
  })
  pagination.page = 1
  loadProcessInstances()
}

/**
 * 刷新列表
 */
const refreshList = () => {
  loadProcessInstances()
}

/**
 * 查看流程详情
 */
const viewProcess = (process) => {
  selectedProcess.value = process
  detailDialogVisible.value = true
}

/**
 * 挂起流程
 */
const suspendProcess = async (process) => {
  try {
    await ElMessageBox.confirm('确定要挂起此流程实例吗？', '确认操作', {
      type: 'warning'
    })
    
    await processInstanceApi.suspendProcessInstance(process.id)
    ElMessage.success('流程已挂起')
    refreshList()
  } catch (error) {
    if (error === 'cancel') return
    console.error('挂起流程失败:', error)
    ElMessage.error('挂起流程失败')
  }
}

/**
 * 激活流程
 */
const activateProcess = async (process) => {
  try {
    await ElMessageBox.confirm('确定要激活此流程实例吗？', '确认操作', {
      type: 'warning'
    })
    
    await processInstanceApi.activateProcessInstance(process.id)
    ElMessage.success('流程已激活')
    refreshList()
  } catch (error) {
    if (error === 'cancel') return
    console.error('激活流程失败:', error)
    ElMessage.error('激活流程失败')
  }
}

/**
 * 终止流程
 */
const terminateProcess = async (process) => {
  try {
    await ElMessageBox.confirm('确定要终止此流程实例吗？此操作不可撤销！', '确认操作', {
      type: 'error'
    })
    
    await processInstanceApi.terminateProcessInstance(process.id, '管理员手动终止')
    ElMessage.success('流程已终止')
    refreshList()
  } catch (error) {
    if (error === 'cancel') return
    console.error('终止流程失败:', error)
    ElMessage.error('终止流程失败')
  }
}

/**
 * 选择变化
 */
const handleSelectionChange = (selection) => {
  selectedProcesses.value = selection
}

/**
 * 页面大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadProcessInstances()
}

/**
 * 当前页变化
 */
const handleCurrentChange = (page) => {
  pagination.page = page
  loadProcessInstances()
}

/**
 * 流程更新回调
 */
const handleProcessUpdated = () => {
  refreshList()
}

/**
 * 前往发起流程页面
 */
const goToStartProcess = () => {
  router.push('/workflow/start-process')
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
</script>

<style lang="scss" scoped>
.process-instances-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
  
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

.filter-card {
  margin-bottom: 20px;
  
  .filter-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
    
    .header-actions {
      display: flex;
      gap: 12px;
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

// 响应式设计
@media (max-width: 768px) {
  .process-instances-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .filter-form {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}
</style>