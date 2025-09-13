<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-content">
        <h2 class="welcome-title">{{ greeting }}，{{ userName }}！</h2>
        <p class="welcome-desc">今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      <div class="welcome-stats">
        <div class="stat-item">
          <div class="stat-value">{{ todayStats.customers }}</div>
          <div class="stat-label">今日新增客户</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ todayStats.deals }}</div>
          <div class="stat-label">今日成交</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">¥{{ todayStats.revenue }}</div>
          <div class="stat-label">今日回款</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ todayStats.tasks }}</div>
          <div class="stat-label">待办任务</div>
        </div>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon :size="24"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalCustomers }}</div>
            <div class="stat-title">客户总数</div>
            <div class="stat-trend">
              <span class="trend-text" :class="{ up: stats.customerGrowth > 0 }">
                {{ stats.customerGrowth > 0 ? '+' : '' }}{{ stats.customerGrowth }}%
              </span>
              <span class="trend-label">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon :size="24"><TrophyBase /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalOpportunities }}</div>
            <div class="stat-title">商机总数</div>
            <div class="stat-trend">
              <span class="trend-text" :class="{ up: stats.opportunityGrowth > 0 }">
                {{ stats.opportunityGrowth > 0 ? '+' : '' }}{{ stats.opportunityGrowth }}%
              </span>
              <span class="trend-label">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">¥{{ formatMoney(stats.totalContracts) }}</div>
            <div class="stat-title">合同金额</div>
            <div class="stat-trend">
              <span class="trend-text" :class="{ up: stats.contractGrowth > 0 }">
                {{ stats.contractGrowth > 0 ? '+' : '' }}{{ stats.contractGrowth }}%
              </span>
              <span class="trend-label">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <el-icon :size="24"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">¥{{ formatMoney(stats.totalReceipts) }}</div>
            <div class="stat-title">回款金额</div>
            <div class="stat-trend">
              <span class="trend-text" :class="{ up: stats.receiptGrowth > 0 }">
                {{ stats.receiptGrowth > 0 ? '+' : '' }}{{ stats.receiptGrowth }}%
              </span>
              <span class="trend-label">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-area">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="quarter">本季</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <span>销售漏斗</span>
          </template>
          <div ref="funnelChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 列表区域 -->
    <el-row :gutter="20" class="list-area">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待办任务</span>
              <el-link type="primary" :underline="false" @click="goToTasks">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-link>
            </div>
          </template>
          <el-table :data="todoList" style="width: 100%">
            <el-table-column prop="title" label="任务" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="80">
              <template #default="{ row }">
                <el-tag size="small" :type="getTaskTypeTag(row.type)">
                  {{ row.typeText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="截止时间" width="120" />
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleTask(row)">
                  处理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新动态</span>
              <el-link type="primary" :underline="false" @click="goToActivities">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-link>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in activityList"
              :key="activity.id"
              :timestamp="activity.time"
              placement="top"
            >
              <div class="activity-content">
                <span class="activity-user">{{ activity.user }}</span>
                <span class="activity-action">{{ activity.action }}</span>
                <span class="activity-target">{{ activity.target }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { 
  User, TrophyBase, Document, Wallet, ArrowRight 
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 用户信息
const userName = computed(() => authStore.userName)

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 当前日期
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

// 今日统计
const todayStats = ref({
  customers: 12,
  deals: 5,
  revenue: '128,500',
  tasks: 8
})

// 统计数据
const stats = ref({
  totalCustomers: 1258,
  customerGrowth: 12.5,
  totalOpportunities: 326,
  opportunityGrowth: 8.3,
  totalContracts: 5680000,
  contractGrowth: 15.6,
  totalReceipts: 4250000,
  receiptGrowth: -3.2
})

// 趋势周期
const trendPeriod = ref('month')

// 待办列表
const todoList = ref([
  { id: 1, title: '联系客户张总确认合同细节', type: 'customer', typeText: '客户', dueDate: '今天 14:00' },
  { id: 2, title: '准备明天的产品演示PPT', type: 'opportunity', typeText: '商机', dueDate: '今天 18:00' },
  { id: 3, title: '审批采购申请单', type: 'approval', typeText: '审批', dueDate: '明天 10:00' },
  { id: 4, title: '跟进项目实施进度', type: 'project', typeText: '项目', dueDate: '明天 15:00' },
  { id: 5, title: '处理服务工单#2024', type: 'service', typeText: '售后', dueDate: '后天 09:00' }
])

// 动态列表
const activityList = ref([
  { id: 1, user: '张三', action: '创建了商机', target: '某科技公司ERP项目', time: '10分钟前' },
  { id: 2, user: '李四', action: '签订了合同', target: 'CRM-2024-0156', time: '30分钟前' },
  { id: 3, user: '王五', action: '完成了拜访', target: '某制造企业', time: '1小时前' },
  { id: 4, user: '赵六', action: '更新了报价', target: 'QT-2024-0089', time: '2小时前' },
  { id: 5, user: '系统', action: '自动分配了线索', target: '15条新线索', time: '3小时前' }
])

// 图表引用
const trendChart = ref(null)
const funnelChart = ref(null)
let trendChartInstance = null
let funnelChartInstance = null

// 格式化金额
const formatMoney = (value) => {
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + '万'
  }
  return value.toLocaleString()
}

// 获取任务类型标签
const getTaskTypeTag = (type) => {
  const typeMap = {
    customer: '',
    opportunity: 'success',
    approval: 'warning',
    project: 'info',
    service: 'danger'
  }
  return typeMap[type] || ''
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChart.value) return
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['新增客户', '新增商机', '成交金额', '转化率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        min: 0,
        max: 250,
        interval: 50,
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '转化率',
        min: 0,
        max: 100,
        interval: 20,
        axisLabel: {
          formatter: '{value}%'
        }
      }
    ],
    series: [
      {
        name: '新增客户',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 260, 280]
      },
      {
        name: '新增商机',
        type: 'line',
        smooth: true,
        data: [80, 82, 91, 94, 120, 110, 125, 132, 141, 154, 160, 180]
      },
      {
        name: '成交金额',
        type: 'bar',
        data: [50, 52, 60, 64, 80, 70, 85, 92, 101, 114, 120, 130]
      },
      {
        name: '转化率',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [30, 32, 35, 38, 42, 45, 48, 52, 55, 58, 60, 65]
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

// 初始化漏斗图表
const initFunnelChart = () => {
  if (!funnelChart.value) return
  
  funnelChartInstance = echarts.init(funnelChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c}'
    },
    series: [
      {
        name: '销售漏斗',
        type: 'funnel',
        left: '10%',
        top: 20,
        bottom: 20,
        width: '80%',
        min: 0,
        max: 100,
        minSize: '0%',
        maxSize: '100%',
        sort: 'descending',
        gap: 2,
        label: {
          show: true,
          position: 'inside'
        },
        labelLine: {
          length: 10,
          lineStyle: {
            width: 1,
            type: 'solid'
          }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            fontSize: 20
          }
        },
        data: [
          { value: 100, name: '线索' },
          { value: 80, name: '商机' },
          { value: 60, name: '报价' },
          { value: 40, name: '谈判' },
          { value: 20, name: '成交' }
        ]
      }
    ]
  }
  
  funnelChartInstance.setOption(option)
}

// 处理任务
const handleTask = (task) => {
  console.log('处理任务:', task)
}

// 跳转到任务页面
const goToTasks = () => {
  router.push('/office/task')
}

// 跳转到动态页面
const goToActivities = () => {
  router.push('/office/notification')
}

// 处理窗口大小变化
const handleResize = () => {
  trendChartInstance?.resize()
  funnelChartInstance?.resize()
}

// 生命周期
onMounted(() => {
  nextTick(() => {
    initTrendChart()
    initFunnelChart()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  funnelChartInstance?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
}

// 欢迎卡片
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
  color: #fff;
  
  .welcome-content {
    margin-bottom: 30px;
    
    .welcome-title {
      margin: 0 0 10px;
      font-size: 28px;
      font-weight: 500;
    }
    
    .welcome-desc {
      margin: 0;
      font-size: 14px;
      opacity: 0.9;
    }
  }
  
  .welcome-stats {
    display: flex;
    gap: 40px;
    
    .stat-item {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 5px;
      }
      
      .stat-label {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
}

// 统计卡片
.stat-cards {
  margin-bottom: 20px;
  
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    height: 100%;
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      color: #fff;
    }
    
    .stat-info {
      flex: 1;
      
      .stat-number {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 5px;
      }
      
      .stat-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 10px;
      }
      
      .stat-trend {
        display: flex;
        align-items: center;
        gap: 5px;
        
        .trend-text {
          font-size: 14px;
          font-weight: 500;
          color: #f56c6c;
          
          &.up {
            color: #67c23a;
          }
        }
        
        .trend-label {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

// 图表区域
.chart-area {
  margin-bottom: 20px;
  
  .chart-card {
    height: 400px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart-container {
      width: 100%;
      height: 320px;
    }
  }
}

// 列表区域
.list-area {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .activity-content {
    .activity-user {
      font-weight: 500;
      color: #409eff;
      margin-right: 5px;
    }
    
    .activity-action {
      color: #606266;
      margin-right: 5px;
    }
    
    .activity-target {
      color: #303133;
      font-weight: 500;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .welcome-card {
    .welcome-stats {
      flex-wrap: wrap;
      gap: 20px;
      
      .stat-item {
        width: calc(50% - 10px);
      }
    }
  }
}
</style>