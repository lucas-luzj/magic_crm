<template>
  <div class="reports-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>报表分析</h2>
        <p>数据驱动决策，洞察业务趋势</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 时间筛选 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="快速选择">
          <el-radio-group v-model="quickSelect" @change="handleQuickSelect">
            <el-radio-button label="today">今天</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="quarter">本季度</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 核心指标 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="metric in metricsData" :key="metric.title">
        <div class="metric-card" :style="{ background: metric.gradient }">
          <div class="metric-content">
            <div class="metric-info">
              <div class="metric-value">{{ metric.value }}</div>
              <div class="metric-title">{{ metric.title }}</div>
              <div class="metric-trend" :class="metric.trend > 0 ? 'up' : 'down'">
                <el-icon>
                  <ArrowUp v-if="metric.trend > 0" />
                  <ArrowDown v-else />
                </el-icon>
                {{ Math.abs(metric.trend) }}%
                <span class="trend-text">{{ metric.trend > 0 ? '较上期' : '较上期' }}</span>
              </div>
            </div>
            <div class="metric-icon">
              <el-icon>
                <component :is="metric.icon" />
              </el-icon>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 销售趋势 -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>销售趋势分析</span>
              <el-radio-group v-model="salesChartType" size="small">
                <el-radio-button label="line">折线图</el-radio-button>
                <el-radio-button label="bar">柱状图</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <my-chart :modelValue="salesTrendOption" style="height: 350px;" />
          </div>
        </el-card>
      </el-col>

      <!-- 客户分析 -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>客户分析</span>
          </template>
          <div class="chart-container">
            <my-chart :modelValue="customerAnalysisOption" style="height: 350px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 产品销售排行 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <span>产品销售排行</span>
          </template>
          <div class="chart-container">
            <my-chart :modelValue="productRankingOption" style="height: 400px;" />
          </div>
        </el-card>
      </el-col>

      <!-- 地区分布 -->
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <span>地区分布</span>
          </template>
          <div class="region-list">
            <div
              v-for="(region, index) in regionData"
              :key="region.name"
              class="region-item"
            >
              <div class="region-rank">{{ index + 1 }}</div>
              <div class="region-info">
                <div class="region-name">{{ region.name }}</div>
                <div class="region-value">¥{{ region.value.toLocaleString() }}</div>
              </div>
              <div class="region-progress">
                <el-progress
                  :percentage="region.percentage"
                  :stroke-width="6"
                  :show-text="false"
                  :color="getProgressColor(index)"
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>详细数据</span>
          <el-button type="primary" size="small" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="sales" label="销售额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.sales.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orders" label="订单数" width="100" />
        <el-table-column prop="customers" label="新增客户" width="100" />
        <el-table-column prop="conversion" label="转化率" width="100">
          <template #default="{ row }">
            <span>{{ row.conversion }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="avgOrder" label="客单价" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.avgOrder.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="growth" label="增长率" width="100">
          <template #default="{ row }">
            <span :class="row.growth > 0 ? 'growth-up' : 'growth-down'">
              {{ row.growth > 0 ? '+' : '' }}{{ row.growth }}%
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed,onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import {
  Download, Refresh, ArrowUp, ArrowDown,
  TrendCharts, User, ShoppingCart, Wallet
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 筛选表单
const filterForm = reactive({
  dateRange: []
})

const quickSelect = ref('month')
const salesChartType = ref('line')

// 核心指标数据
const metricsData = ref([
  {
    title: '总销售额',
    value: '¥2,847,392',
    trend: 12.5,
    icon: 'Wallet',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '订单总数',
    value: '8,234',
    trend: 8.2,
    icon: 'ShoppingCart',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '客户总数',
    value: '1,856',
    trend: -2.1,
    icon: 'User',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '平均客单价',
    value: '¥346',
    trend: 15.3,
    icon: 'TrendCharts',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 销售趋势图表配置
const salesTrendOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: {
    data: ['销售额', '订单数']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  },
  yAxis: [
    {
      type: 'value',
      name: '销售额(万)',
      position: 'left'
    },
    {
      type: 'value',
      name: '订单数',
      position: 'right'
    }
  ],
  series: [
    {
      name: '销售额',
      type: salesChartType.value,
      yAxisIndex: 0,
      data: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 290, 330],
      itemStyle: {
        color: '#409EFF'
      },
      smooth: true
    },
    {
      name: '订单数',
      type: salesChartType.value,
      yAxisIndex: 1,
      data: [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90, 149],
      itemStyle: {
        color: '#67C23A'
      },
      smooth: true
    }
  ]
}))

// 客户分析图表配置
const customerAnalysisOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '客户类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      data: [
        { value: 1048, name: '企业客户', itemStyle: { color: '#409EFF' } },
        { value: 735, name: '个人客户', itemStyle: { color: '#67C23A' } },
        { value: 580, name: '代理商', itemStyle: { color: '#E6A23C' } },
        { value: 484, name: '其他', itemStyle: { color: '#F56C6C' } }
      ]
    }
  ]
})

// 产品销售排行图表配置
const productRankingOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value'
  },
  yAxis: {
    type: 'category',
    data: ['产品E', '产品D', '产品C', '产品B', '产品A']
  },
  series: [
    {
      name: '销售额',
      type: 'bar',
      data: [120, 200, 150, 80, 70],
      itemStyle: {
        color: new Array(5).fill(0).map((_, index) => {
          const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
          return colors[index]
        })
      }
    }
  ]
})

// 地区数据
const regionData = ref([
  { name: '北京', value: 1234567, percentage: 100 },
  { name: '上海', value: 987654, percentage: 80 },
  { name: '广州', value: 765432, percentage: 62 },
  { name: '深圳', value: 654321, percentage: 53 },
  { name: '杭州', value: 543210, percentage: 44 },
  { name: '成都', value: 432109, percentage: 35 },
  { name: '武汉', value: 321098, percentage: 26 },
  { name: '西安', value: 210987, percentage: 17 }
])

// 表格数据
const tableData = ref([
  {
    date: '2024-01-01',
    sales: 125000,
    orders: 234,
    customers: 45,
    conversion: 12.5,
    avgOrder: 534,
    growth: 8.2
  },
  {
    date: '2024-01-02',
    sales: 132000,
    orders: 267,
    customers: 52,
    conversion: 13.2,
    avgOrder: 494,
    growth: 5.6
  },
  {
    date: '2024-01-03',
    sales: 98000,
    orders: 189,
    customers: 38,
    conversion: 11.8,
    avgOrder: 518,
    growth: -2.3
  }
])

// 获取进度条颜色
const getProgressColor = (index) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  return colors[index % colors.length]
}

// 处理日期变化
const handleDateChange = (dates) => {
  console.log('日期范围:', dates)
  // 重新加载数据
}

// 快速选择处理
const handleQuickSelect = (value) => {
  const today = new Date()
  let startDate, endDate

  switch (value) {
    case 'today':
      startDate = endDate = today.toISOString().split('T')[0]
      break
    case 'week':
      const weekStart = new Date(today.setDate(today.getDate() - today.getDay()))
      startDate = weekStart.toISOString().split('T')[0]
      endDate = new Date().toISOString().split('T')[0]
      break
    case 'month':
      startDate = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0]
      endDate = new Date().toISOString().split('T')[0]
      break
    // 其他情况...
  }

  filterForm.dateRange = [startDate, endDate]
}

// 导出报表
const handleExport = () => {
  ElMessage.success('报表导出功能开发中...')
}

// 刷新数据
const handleRefresh = () => {
  ElMessage.success('数据已刷新')
}

// 初始化
onMounted(() => {
  quickSelect.value = 'month'
  handleQuickSelect('month')
})
</script>

<style lang="scss" scoped>
.reports-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .header-left {
    h2 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 24px;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }
}

.filter-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .filter-form {
    margin-bottom: 0;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
}

.metrics-row {
  margin-bottom: 20px;

  .metric-card {
    border-radius: 12px;
    padding: 20px;
    color: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-5px);
    }

    .metric-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .metric-info {
        .metric-value {
          font-size: 28px;
          font-weight: 700;
          margin-bottom: 8px;
        }

        .metric-title {
          font-size: 14px;
          margin-bottom: 8px;
          opacity: 0.9;
        }

        .metric-trend {
          display: flex;
          align-items: center;
          font-size: 12px;
          font-weight: 600;

          .el-icon {
            margin-right: 4px;
          }

          .trend-text {
            margin-left: 4px;
            opacity: 0.8;
          }

          &.up {
            color: #67C23A;
          }

          &.down {
            color: #F56C6C;
          }
        }
      }

      .metric-icon {
        .el-icon {
          font-size: 48px;
          opacity: 0.8;
        }
      }
    }
  }
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .chart-container {
    width: 100%;
  }

  .region-list {
    .region-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .region-rank {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background: #409EFF;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        margin-right: 12px;
      }

      .region-info {
        flex: 1;
        margin-right: 12px;

        .region-name {
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }

        .region-value {
          font-size: 14px;
          color: #409EFF;
          font-weight: 600;
        }
      }

      .region-progress {
        width: 100px;
      }
    }
  }
}

.table-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .amount {
    font-weight: 600;
    color: #409EFF;
  }

  .growth-up {
    color: #67C23A;
    font-weight: 600;
  }

  .growth-down {
    color: #F56C6C;
    font-weight: 600;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .metrics-row {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .charts-row {
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>