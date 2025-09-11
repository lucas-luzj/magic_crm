<template>
  <div class="analytics-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>销售趋势</span>
          </template>
          <div ref="salesChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>用户分布</span>
          </template>
          <div ref="userChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <span>月度数据对比</span>
          </template>
          <div ref="monthlyChart" class="chart-large"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const salesChart = ref()
const userChart = ref()
const monthlyChart = ref()

const initSalesChart = () => {
  const chart = echarts.init(salesChart.value)
  const option = {
    title: {
      text: '销售趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [120, 200, 150, 80, 70, 110],
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }
  chart.setOption(option)
}

const initUserChart = () => {
  const chart = echarts.init(userChart.value)
  const option = {
    title: {
      text: '用户分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: '新用户' },
        { value: 735, name: '活跃用户' },
        { value: 580, name: '沉睡用户' },
        { value: 484, name: '流失用户' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  chart.setOption(option)
}

const initMonthlyChart = () => {
  const chart = echarts.init(monthlyChart.value)
  const option = {
    title: {
      text: '月度数据对比',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['销售额', '订单量', '用户数'],
      top: 30
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
        type: 'bar',
        data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '订单量',
        type: 'bar',
        data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '用户数',
        type: 'line',
        data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  nextTick(() => {
    initSalesChart()
    initUserChart()
    initMonthlyChart()
  })
})
</script>

<style scoped>
.analytics-container {
  padding: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.chart-large {
  height: 400px;
}
</style>