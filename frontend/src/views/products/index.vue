<template>
  <div class="products-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>产品管理</h2>
        <p>管理您的产品信息和库存</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增产品
        </el-button>
      </div>
    </div>

    <!-- 产品统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="8" :lg="6">
        <div class="stat-item">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">856</div>
            <div class="stat-label">产品总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8" :lg="6">
        <div class="stat-item">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">23</div>
            <div class="stat-label">库存不足</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8" :lg="6">
        <div class="stat-item">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">156</div>
            <div class="stat-label">热门产品</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8" :lg="6">
        <div class="stat-item">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">¥2.8M</div>
            <div class="stat-label">总价值</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="产品名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入产品名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="产品分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option label="电子产品" value="electronics" />
            <el-option label="服装配饰" value="clothing" />
            <el-option label="家居用品" value="home" />
            <el-option label="运动户外" value="sports" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在售" value="active" />
            <el-option label="下架" value="inactive" />
            <el-option label="缺货" value="out_of_stock" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number
            v-model="searchForm.minPrice"
            placeholder="最低价"
            :min="0"
            controls-position="right"
            style="width: 120px;"
          />
          <span style="margin: 0 8px;">-</span>
          <el-input-number
            v-model="searchForm.maxPrice"
            placeholder="最高价"
            :min="0"
            controls-position="right"
            style="width: 120px;"
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

    <!-- 产品列表 -->
    <el-card class="products-card">
      <template #header>
        <div class="card-header">
          <span>产品列表</span>
          <div class="header-actions">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
              </el-radio-button>
              <el-radio-button label="list">
                <el-icon><List /></el-icon>
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="products-grid">
        <div
          v-for="product in tableData"
          :key="product.id"
          class="product-card"
        >
          <div class="product-image">
            <el-image
              :src="product.image"
              fit="cover"
              style="width: 100%; height: 180px;"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="product-actions">
              <el-button type="primary" size="small" circle @click="handleView(product)">
                <el-icon><View /></el-icon>
              </el-button>
              <el-button type="success" size="small" circle @click="handleEdit(product)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="danger" size="small" circle @click="handleDelete(product)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-category">{{ getCategoryText(product.category) }}</p>
            <div class="product-price">¥{{ product.price.toLocaleString() }}</div>
            <div class="product-stock">
              <span>库存: {{ product.stock }}</span>
              <el-tag :type="getStockStatus(product.stock).type" size="small">
                {{ getStockStatus(product.stock).text }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <el-table
        v-else
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }">
            <el-image
              :src="row.image"
              style="width: 50px; height: 50px; border-radius: 4px;"
              fit="cover"
            >
              <template #error>
                <div class="image-slot-small">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="产品名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryText(row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[12, 24, 48, 96]"
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
import { reactive, ref } from 'vue'
import {
  Plus, Search, Refresh, Box, Warning, Star, TrendCharts,
  Grid, List, Picture, View, Edit, Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  category: '',
  status: '',
  minPrice: null,
  maxPrice: null
})

// 视图模式
const viewMode = ref('grid')

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    name: 'iPhone 15 Pro',
    category: 'electronics',
    price: 7999,
    stock: 156,
    sales: 1234,
    status: 'active',
    createTime: '2024-01-15 10:30:00',
    image: ''
  },
  {
    id: 2,
    name: '运动T恤',
    category: 'clothing',
    price: 199,
    stock: 8,
    sales: 567,
    status: 'active',
    createTime: '2024-01-16 14:20:00',
    image: ''
  },
  {
    id: 3,
    name: '智能音箱',
    category: 'electronics',
    price: 299,
    stock: 0,
    sales: 89,
    status: 'out_of_stock',
    createTime: '2024-01-17 09:15:00',
    image: ''
  },
  {
    id: 4,
    name: '咖啡杯',
    category: 'home',
    price: 59,
    stock: 234,
    sales: 345,
    status: 'active',
    createTime: '2024-01-18 16:45:00',
    image: ''
  }
])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 100
})

// 获取分类文本
const getCategoryText = (category) => {
  const textMap = {
    electronics: '电子产品',
    clothing: '服装配饰',
    home: '家居用品',
    sports: '运动户外'
  }
  return textMap[category] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    active: 'success',
    inactive: 'info',
    out_of_stock: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '在售',
    inactive: '下架',
    out_of_stock: '缺货'
  }
  return textMap[status] || '未知'
}

// 获取库存状态
const getStockStatus = (stock) => {
  if (stock === 0) {
    return { type: 'danger', text: '缺货' }
  } else if (stock < 10) {
    return { type: 'warning', text: '库存不足' }
  } else {
    return { type: 'success', text: '充足' }
  }
}

// 搜索
const handleSearch = () => {
  console.log('搜索条件:', searchForm)
  ElMessage.success('搜索功能开发中...')
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (['minPrice', 'maxPrice'].includes(key)) {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  ElMessage.info('新增产品功能开发中...')
}

// 查看
const handleView = (row) => {
  ElMessage.info(`查看产品: ${row.name}`)
}

// 编辑
const handleEdit = (row) => {
  ElMessage.info(`编辑产品: ${row.name}`)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除产品 "${row.name}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
  })
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
.products-page {
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

.stats-row {
  margin-bottom: 20px;

  .stat-item {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;

      .el-icon {
        font-size: 24px;
        color: white;
      }
    }

    .stat-content {
      .stat-number {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.search-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.products-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;

  .product-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);

      .product-actions {
        opacity: 1;
      }
    }

    .product-image {
      position: relative;

      .image-slot {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 180px;
        background: #f5f7fa;
        color: #909399;
        font-size: 30px;
      }

      .product-actions {
        position: absolute;
        top: 10px;
        right: 10px;
        display: flex;
        gap: 8px;
        opacity: 0;
        transition: opacity 0.3s ease;
      }
    }

    .product-info {
      padding: 16px;

      .product-name {
        margin: 0 0 8px 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .product-category {
        margin: 0 0 12px 0;
        font-size: 14px;
        color: #909399;
      }

      .product-price {
        font-size: 18px;
        font-weight: 700;
        color: #409EFF;
        margin-bottom: 12px;
      }

      .product-stock {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.image-slot-small {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 50px;
  height: 50px;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
}

.price {
  font-weight: 600;
  color: #409EFF;
}

.low-stock {
  color: #F56C6C;
  font-weight: 600;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .stats-row {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
  }
}
</style>