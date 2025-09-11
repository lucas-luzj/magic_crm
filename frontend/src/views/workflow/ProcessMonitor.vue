<template>
  <div class="process-monitor-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>流程监控</h2>
        <p class="header-subtitle">实时监控流程运行状态和性能指标</p>
      </div>
      <div class="header-right">
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="primary" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon active">
                <el-icon size="24"><VideoPlay /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.activeProcesses }}</div>
                <div class="stats-label">运行中流程</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon completed">
                <el-icon size="24"><Check /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.completedProcesses }}</div>
                <div class="stats-label">已完成流程</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
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
        </el-col>
        
        <el-col :span="6">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon error">
                <el-icon size="24"><Warning /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ statistics.errorProcesses }}</div>
                <div class="stats-label">异常流程</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 流程趋势图 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="never">
            <template #header>
              <div class="card-header">
                <h4>流程趋势</h4>
                <el-radio-group v-model="trendPeriod" size="small">
                  <el-radio-button label="7d">7天</el-radio-button>
                  <el-radio-button label="30d">30天</el-radio-button>
                  <el-radio-button label="90d">90天</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-container">
              <div ref="trendChartRef" class="chart"></div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 流程类型分布 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="never">
            <template #header>
              <div class="card-header">
                <h4>流程类型分布</h4>
              </div>
            </template>
            <div class="chart-container">
              <div ref="pieChartRef" class="chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 性能指标 -->
    <div class="performance-section">
      <el-row :gutter="20">
        <!-- 平均处理时间 -->
        <el-col :span="8">
          <el-card class="metric-card" shadow="never">
            <template #header>
              <div class="card-header">
                <h4>平均处理时间</h4>
              </div>
            </template>
            <div class="metric-content">
              <div class="metric-value">{{ performanceMetrics.avgProcessTime }}</div>
              <div class="metric-trend" :class="performanceMetrics.processTimeTrend">
                <el-icon>
                  <component :is="getTrendIcon(performanceMetrics.processTimeTrend)" />
                </el-icon>
                {{ performanceMetrics.processTimeChange }}
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 任务完成率 -->
        <el-col :span="8">
          <el-card class="metric-card" shadow="never">
            <template #header>
              <div class="card-header">
                <h4>任务完成率</h4>
              </div>
            </template>
            <div class="metric-content">
              <div class="metric-value">{{ performanceMetrics.taskCompletionRate }}%</div>
              <div class="metric-trend" :class="performanceMetrics.completionRateTrend">
                <el-icon>
                  <component :is="getTrendIcon(performanceMetrics.completionRateTrend)" />
                </el-icon>
                {{ performanceMetrics.completionRateChange }}
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 系统负载 -->
        <el-col :span="8">
          <el-card class="metric-card" shadow="never">
            <template #header>
              <div class="card-header">
                <h4>系统负载</h4>
              </div>
            </template>
            <div class="metric-content">
              <div class="metric-value">{{ performanceMetrics.systemLoad }}%</div>
              <div class="metric-trend" :class="performanceMetrics.systemLoadTrend">
                <el-icon>
                  <component :is="getTrendIcon(performanceMetrics.systemLoadTrend)" />
                </el-icon>
                {{ performanceMetrics.systemLoadChange }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 实时监控 -->
    <el-card class="realtime-monitor" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>实时监控</h4>
          <div class="header-actions">
            <el-switch v-model="realtimeEnabled" @change="toggleRealtime" />
            <span class="switch-label">实时更新</span>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="realtimeData"
        stripe
        height="400"
      >
        <el-table-column prop="processName" label="流程名称" min-width="150" />
        <el-table-column prop="instanceId" label="实例ID" width="120" />
        <el-table-column prop="currentTask" label="当前任务" min-width="150" />
        <el-table-column prop="assignee" label="处理人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="运行时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :stroke-width="6" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="viewProcess(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 告警信息 -->
    <el-card class="alerts-card" shadow="never" v-if="alerts.length > 0">
      <template #header>
        <div class="card-header">
          <h4>系统告警</h4>
          <el-button size="small" @click="clearAlerts">清除告警</el-button>
        </div>
      </template>
      
      <el-alert
        v-for="alert in alerts"
        :key="alert.id"
        :title="alert.title"
        :type="alert.type"
        :description="alert.description"
        :closable="false"
        show-icon
        class="alert-item"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Refresh, Download, VideoPlay, Check, Clock, Warning,
  ArrowUp, ArrowDown, Minus
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const realtimeEnabled = ref(false)
const trendPeriod = ref('7d')

// 统计数据
const statistics = reactive({
  activeProcesses: 0,
  completedProcesses: 0,
  pendingTasks: 0,
  errorProcesses: 0
})

// 性能指标
const performanceMetrics = reactive({
  avgProcessTime: '2.5小时',
  processTimeTrend: 'down',
  processTimeChange: '12%',
  taskCompletionRate: 95,
  completionRateTrend: 'up',
  completionRateChange: '3%',
  systemLoad: 68,
  systemLoadTrend: 'up',
  systemLoadChange: '5%'
})

// 实时数据
const realtimeData = ref([])
const alerts = ref([])

// 图表引用
const trendChartRef = ref()
const pieChartRef = ref()

// 定时器
let realtimeTimer = null

// 页面初始化
onMounted(async () => {
  await loadStatistics()
  await loadPerformanceMetrics()
  await loadRealtimeData()
  await loadAlerts()
  
  nextTick(() => {
    initCharts()
  })
})

onUnmounted(() => {
  if (realtimeTimer) {
    clearInterval(realtimeTimer)
  }
})

/**
 * 加载统计数据
 */
const loadStatistics = async () => {
  try {
    // 模拟API调用
    Object.assign(statistics, {
      activeProcesses: 15,
      completedProcesses: 128,
      pendingTasks: 8,
      errorProcesses: 2
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

/**
 * 加载性能指标
 */
const loadPerformanceMetrics = async () => {
  try {
    // 模拟API调用
    // 这里可以调用实际的性能监控API
  } catch (error) {
    console.error('加载性能指标失败:', error)
  }
}

/**
 * 加载实时数据
 */
const loadRealtimeData = async () => {
  try {
    loading.value = true
    
    // 模拟实时数据
    realtimeData.value = [
      {
        processName: '员工请假申请',
        instanceId: 'PI001',
        currentTask: '部门经理审批',
        assignee: '张经理',
        status: 'ACTIVE',
        startTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
        progress: 60
      },
      {
        processName: '采购申请流程',
        instanceId: 'PI002',
        currentTask: '财务审批',
        assignee: '李会计',
        status: 'ACTIVE',
        startTime: new Date(Date.now() - 4 * 60 * 60 * 1000),
        progress: 80
      },
      {
        processName: '项目立项申请',
        instanceId: 'PI003',
        currentTask: '技术评审',
        assignee: '王工程师',
        status: 'ACTIVE',
        startTime: new Date(Date.now() - 1 * 60 * 60 * 1000),
        progress: 30
      }
    ]
  } catch (error) {
    console.error('加载实时数据失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载告警信息
 */
const loadAlerts = async () => {
  try {
    // 模拟告警数据
    alerts.value = [
      {
        id: 1,
        title: '流程执行超时',
        type: 'warning',
        description: '员工请假申请流程PI001已运行超过24小时，请检查处理进度'
      },
      {
        id: 2,
        title: '系统负载过高',
        type: 'error',
        description: '当前系统负载达到85%，可能影响流程执行性能'
      }
    ]
  } catch (error) {
    console.error('加载告警信息失败:', error)
  }
}

/**
 * 初始化图表
 */
const initCharts = () => {
  initTrendChart()
  initPieChart()
}

/**
 * 初始化趋势图
 */
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  const chart = echarts.init(trendChartRef.value)
  
  const option = {
    title: {
      text: '流程执行趋势',
      left: 'center',
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['启动', '完成', '异常'],
      bottom: 0
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '启动',
        type: 'line',
        data: [12, 15, 18, 14, 16, 8, 6],
        smooth: true
      },
      {
        name: '完成',
        type: 'line',
        data: [10, 12, 15, 13, 14, 7, 5],
        smooth: true
      },
      {
        name: '异常',
        type: 'line',
        data: [1, 2, 1, 1, 2, 1, 1],
        smooth: true
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

/**
 * 初始化饼图
 */
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  const chart = echarts.init(pieChartRef.value)
  
  const option = {
    title: {
      text: '流程类型分布',
      left: 'center',
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['请假申请', '采购申请', '项目立项', '其他']
    },
    series: [
      {
        name: '流程类型',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '请假申请' },
          { value: 25, name: '采购申请' },
          { value: 20, name: '项目立项' },
          { value: 20, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

/**
 * 切换实时更新
 */
const toggleRealtime = (enabled) => {
  if (enabled) {
    realtimeTimer = setInterval(() => {
      loadRealtimeData()
    }, 5000) // 5秒更新一次
  } else {
    if (realtimeTimer) {
      clearInterval(realtimeTimer)
      realtimeTimer = null
    }
  }
}

/**
 * 刷新数据
 */
const refreshData = async () => {
  await Promise.all([
    loadStatistics(),
    loadPerformanceMetrics(),
    loadRealtimeData(),
    loadAlerts()
  ])
  ElMessage.success('数据已刷新')
}

/**
 * 导出报告
 */
const exportReport = () => {
  ElMessage.info('导出功能开发中...')
}

/**
 * 查看流程详情
 */
const viewProcess = (process) => {
  router.push(`/workflow/process-instances?id=${process.instanceId}`)
}

/**
 * 清除告警
 */
const clearAlerts = () => {
  alerts.value = []
  ElMessage.success('告警已清除')
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
 * 获取趋势图标
 */
const getTrendIcon = (trend) => {
  const iconMap = {
    'up': ArrowUp,
    'down': ArrowDown,
    'stable': Minus
  }
  return iconMap[trend] || Minus
}

/**
 * 格式化持续时间
 */
const formatDuration = (startTime) => {
  if (!startTime) return '-'
  
  const now = new Date()
  const start = new Date(startTime)
  const diff = now - start
  
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 0) return `${hours}小时${minutes}分钟`
  return `${minutes}分钟`
}
</script>

<style lang="scss" scoped>
.process-monitor-container {
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
  
  .header-right {
    display: flex;
    gap: 12px;
  }
}

.stats-overview {
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
        
        &.active {
          background: var(--el-color-primary-light-9);
          color: var(--el-color-primary);
        }
        
        &.completed {
          background: var(--el-color-success-light-9);
          color: var(--el-color-success);
        }
        
        &.pending {
          background: var(--el-color-warning-light-9);
          color: var(--el-color-warning);
        }
        
        &.error {
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

.charts-section {
  margin-bottom: 24px;
  
  .chart-card {
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
    }
    
    .chart-container {
      height: 300px;
      
      .chart {
        width: 100%;
        height: 100%;
      }
    }
  }
}

.performance-section {
  margin-bottom: 24px;
  
  .metric-card {
    .card-header {
      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }
    
    .metric-content {
      text-align: center;
      
      .metric-value {
        font-size: 28px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 8px;
      }
      
      .metric-trend {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        font-size: 14px;
        
        &.up {
          color: var(--el-color-success);
        }
        
        &.down {
          color: var(--el-color-danger);
        }
        
        &.stable {
          color: var(--el-text-color-regular);
        }
      }
    }
  }
}

.realtime-monitor {
  margin-bottom: 24px;
  
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
      align-items: center;
      gap: 8px;
      
      .switch-label {
        font-size: 14px;
        color: var(--el-text-color-regular);
      }
    }
  }
}

.alerts-card {
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
  }
  
  .alert-item {
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .charts-section {
    .el-col {
      margin-bottom: 20px;
    }
  }
}

@media (max-width: 768px) {
  .process-monitor-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-overview {
    .el-col {
      margin-bottom: 16px;
    }
  }
  
  .performance-section {
    .el-col {
      margin-bottom: 16px;
    }
  }
}
</style>
