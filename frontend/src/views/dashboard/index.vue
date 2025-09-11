<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in overviewCards" :key="card.title">
        <div class="overview-card" :style="{ background: card.gradient }">
          <div class="card-content">
            <div class="card-info">
              <h3>{{ card.value }}</h3>
              <p>{{ card.title }}</p>
              <span class="card-trend" :class="card.trend > 0 ? 'up' : 'down'">
                <el-icon>
                  <TrendCharts v-if="card.trend > 0" />
                  <Bottom v-else />
                </el-icon>
                {{ Math.abs(card.trend) }}%
              </span>
            </div>
            <div class="card-icon">
              <el-icon>
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="salesPeriod" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <my-chart :modelValue="salesChartOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <span>客户分布</span>
          </template>
          <div class="chart-container">
            <my-chart :modelValue="customerChartOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-row :gutter="20" class="table-section">
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <span>最新订单</span>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="customer" label="客户" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">
                <span class="amount">¥{{ row.amount.toLocaleString() }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <span>热门产品</span>
          </template>
          <el-table :data="hotProducts" style="width: 100%">
            <el-table-column prop="name" label="产品名称" />
            <el-table-column prop="sales" label="销量" />
            <el-table-column prop="revenue" label="收入">
              <template #default="{ row }">
                <span class="amount">¥{{ row.revenue.toLocaleString() }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="growth" label="增长率">
              <template #default="{ row }">
                <span :class="row.growth > 0 ? 'growth-up' : 'growth-down'">
                  {{ row.growth > 0 ? '+' : '' }}{{ row.growth }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref,computed } from 'vue'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
// import VChart from 'vue-echarts'

import { TrendCharts, Bottom, User, ShoppingCart, Box, Wallet } from '@element-plus/icons-vue'

// 概览卡片数据
const overviewCards = ref([
  {
    title: '总客户数',
    value: '2,847',
    trend: 12.5,
    icon: 'User',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '本月订单',
    value: '1,234',
    trend: 8.2,
    icon: 'ShoppingCart',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '产品总数',
    value: '856',
    trend: -2.1,
    icon: 'Box',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '本月收入',
    value: '¥128.5万',
    trend: 15.3,
    icon: 'Wallet',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 销售周期
const salesPeriod = ref('month')

// 销售趋势图表配置
const salesChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
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
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '销售额',
      type: 'line',
      smooth: true,
      data: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 290, 330],
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ]
        }
      }
    }
  ]
}))

// 客户分布图表配置
const customerChartOption = ref({
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
      name: '客户分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
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

// 最新订单数据
const recentOrders = ref([
  { orderNo: 'ORD001', customer: '张三', amount: 12800, status: '已完成' },
  { orderNo: 'ORD002', customer: '李四', amount: 8500, status: '进行中' },
  { orderNo: 'ORD003', customer: '王五', amount: 15600, status: '已完成' },
  { orderNo: 'ORD004', customer: '赵六', amount: 9200, status: '待确认' },
  { orderNo: 'ORD005', customer: '钱七', amount: 11300, status: '已完成' }
])

// 热门产品数据
const hotProducts = ref([
  { name: '产品A', sales: 1234, revenue: 156800, growth: 12.5 },
  { name: '产品B', sales: 987, revenue: 98700, growth: -3.2 },
  { name: '产品C', sales: 756, revenue: 113400, growth: 8.7 },
  { name: '产品D', sales: 654, revenue: 78480, growth: 15.3 },
  { name: '产品E', sales: 543, revenue: 65160, growth: -1.8 }
])

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '已完成': 'success',
    '进行中': 'warning',
    '待确认': 'info',
    '已取消': 'danger'
  }
  return statusMap[status] || 'info'
}
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;

  .overview-card {
    border-radius: 12px;
    padding: 20px;
    color: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    }

    .card-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-info {
        h3 {
          font-size: 28px;
          font-weight: 700;
          margin: 0 0 8px 0;
        }

        p {
          font-size: 14px;
          margin: 0 0 8px 0;
          opacity: 0.9;
        }

        .card-trend {
          display: flex;
          align-items: center;
          font-size: 12px;
          font-weight: 600;

          .el-icon {
            margin-right: 4px;
          }

          &.up {
            color: #67C23A;
          }

          &.down {
            color: #F56C6C;
          }
        }
      }

      .card-icon {
        .el-icon {
          font-size: 48px;
          opacity: 0.8;
        }
      }
    }
  }
}

.charts-section,
.table-section {
  margin-bottom: 20px;
}

.chart-card,
.table-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;

  :deep(.el-card__header) {
    border-bottom: 1px solid #f0f0f0;
    padding: 18px 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      color: #303133;
    }
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.chart-container {
  width: 100%;
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

// 响应式设计
@media (max-width: 768px) {
  .overview-cards {
    .overview-card {
      margin-bottom: 15px;
      
      .card-content {
        .card-info h3 {
          font-size: 24px;
        }
        
        .card-icon .el-icon {
          font-size: 36px;
        }
      }
    }
  }

  .charts-section,
  .table-section {
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>