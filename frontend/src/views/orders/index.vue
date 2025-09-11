<template>
  <div class="orders-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>订单管理</h2>
        <p>管理您的订单信息和状态</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增订单
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in statsData" :key="stat.title">
        <div class="stat-card" :style="{ borderLeft: `4px solid ${stat.color}` }">
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
          <div class="stat-icon" :style="{ color: stat.color }">
            <el-icon>
              <component :is="stat.icon" />
            </el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
            clearable
          />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input
            v-model="searchForm.customer"
            placeholder="请输入客户名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待确认" value="pending" />
            <el-option label="进行中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
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

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="140" fixed="left">
          <template #default="{ row }">
            <el-link type="primary" @click="handleView(row)">{{ row.orderNo }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="customer" label="客户名称" min-width="120" />
        <el-table-column prop="products" label="产品" min-width="200">
          <template #default="{ row }">
            <div class="products-list">
              <el-tag
                v-for="product in row.products.slice(0, 2)"
                :key="product"
                size="small"
                class="product-tag"
              >
                {{ product }}
              </el-tag>
              <el-tag v-if="row.products.length > 2" size="small" type="info">
                +{{ row.products.length - 2 }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="updateTime" label="更新时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleAction(command, row)">
              <el-button type="primary" link>
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="confirm" v-if="row.status === 'pending'">
                    确认订单
                  </el-dropdown-item>
                  <el-dropdown-item command="complete" v-if="row.status === 'processing'">
                    完成订单
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" v-if="['pending', 'processing'].includes(row.status)">
                    取消订单
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    删除订单
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  Plus, Search, Refresh, View, Edit, ArrowDown, CircleCheck, CircleClose} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 统计数据
const statsData = ref([
  {
    title: '总订单数',
    value: '1,234',
    icon: 'ShoppingCart',
    color: '#409EFF'
  },
  {
    title: '待处理',
    value: '56',
    icon: 'Clock',
    color: '#E6A23C'
  },
  {
    title: '已完成',
    value: '987',
    icon: 'CircleCheck',
    color: '#67C23A'
  },
  {
    title: '已取消',
    value: '23',
    icon: 'CircleClose',
    color: '#F56C6C'
  }
])

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  customer: '',
  status: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    orderNo: 'ORD20240101001',
    customer: '张三',
    products: ['产品A', '产品B', '产品C'],
    amount: 12800,
    status: 'completed',
    createTime: '2024-01-15 10:30:00',
    updateTime: '2024-01-16 14:20:00'
  },
  {
    id: 2,
    orderNo: 'ORD20240101002',
    customer: '李四',
    products: ['产品D'],
    amount: 8500,
    status: 'processing',
    createTime: '2024-01-16 14:20:00',
    updateTime: '2024-01-16 14:20:00'
  },
  {
    id: 3,
    orderNo: 'ORD20240101003',
    customer: '王五',
    products: ['产品E', '产品F'],
    amount: 15600,
    status: 'pending',
    createTime: '2024-01-17 09:15:00',
    updateTime: '2024-01-17 09:15:00'
  }
])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 100
})

// 选中的行
const selectedRows = ref([])

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    pending: '待确认',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || '未知'
}

// 搜索
const handleSearch = () => {
  console.log('搜索条件:', searchForm)
  ElMessage.success('搜索功能开发中...')
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (key === 'dateRange') {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  ElMessage.info('新增订单功能开发中...')
}

// 查看
const handleView = (row) => {
  ElMessage.info(`查看订单: ${row.orderNo}`)
}

// 编辑
const handleEdit = (row) => {
  ElMessage.info(`编辑订单: ${row.orderNo}`)
}

// 操作处理
const handleAction = (command, row) => {
  switch (command) {
    case 'confirm':
      ElMessage.success(`订单 ${row.orderNo} 已确认`)
      break
    case 'complete':
      ElMessage.success(`订单 ${row.orderNo} 已完成`)
      break
    case 'cancel':
      ElMessageBox.confirm(
        `确定要取消订单 "${row.orderNo}" 吗？`,
        '确认取消',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success('订单已取消')
      })
      break
    case 'delete':
      ElMessageBox.confirm(
        `确定要删除订单 "${row.orderNo}" 吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success('删除成功')
      })
      break
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
}
</script>

<style lang="scss" scoped>
.orders-page {
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

.stats-cards {
  margin-bottom: 20px;

  .stat-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }

    .stat-content {
      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 8px;
      }

      .stat-title {
        font-size: 14px;
        color: #909399;
      }
    }

    .stat-icon {
      font-size: 32px;
      opacity: 0.8;
    }
  }
}

.search-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .search-form {
    margin-bottom: 0;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
}

.table-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .products-list {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;

    .product-tag {
      margin: 0;
    }
  }

  .amount {
    font-weight: 600;
    color: #409EFF;
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .stats-cards {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .search-form {
    :deep(.el-form-item) {
      width: 100%;
      margin-right: 0;
    }
  }
}
</style>