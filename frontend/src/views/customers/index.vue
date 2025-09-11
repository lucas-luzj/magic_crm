<template>
  <div class="customers-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>客户管理</h2>
        <p>管理您的客户信息和关系</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="客户名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入客户名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="searchForm.type" placeholder="请选择客户类型" clearable>
            <el-option label="企业客户" value="enterprise" />
            <el-option label="个人客户" value="individual" />
            <el-option label="代理商" value="agent" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="活跃" value="active" />
            <el-option label="潜在" value="potential" />
            <el-option label="流失" value="lost" />
          </el-select>
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
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="客户名称" min-width="120" />
        <el-table-column prop="type" label="客户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="company" label="公司" min-width="120" />
        <el-table-column prop="status" label="状态" width="80">
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
import { reactive, ref,onMounted } from 'vue'
import { Plus, Search, Refresh, User, View, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    name: '张三',
    type: 'enterprise',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    company: '阿里巴巴',
    status: 'active',
    createTime: '2024-01-15 10:30:00',
    avatar: ''
  },
  {
    id: 2,
    name: '李四',
    type: 'individual',
    phone: '13800138002',
    email: 'lisi@example.com',
    company: '腾讯',
    status: 'potential',
    createTime: '2024-01-16 14:20:00',
    avatar: ''
  },
  {
    id: 3,
    name: '王五',
    type: 'agent',
    phone: '13800138003',
    email: 'wangwu@example.com',
    company: '百度',
    status: 'active',
    createTime: '2024-01-17 09:15:00',
    avatar: ''
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

// 获取类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    enterprise: 'success',
    individual: 'primary',
    agent: 'warning'
  }
  return colorMap[type] || 'info'
}

// 获取类型文本
const getTypeText = (type) => {
  const textMap = {
    enterprise: '企业客户',
    individual: '个人客户',
    agent: '代理商'
  }
  return textMap[type] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    active: 'success',
    potential: 'warning',
    lost: 'danger'
  }
  return colorMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '活跃',
    potential: '潜在',
    lost: '流失'
  }
  return textMap[status] || '未知'
}

// 搜索
const handleSearch = () => {
  console.log('搜索条件:', searchForm)
  // 这里调用API进行搜索
  ElMessage.success('搜索功能开发中...')
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  ElMessage.info('新增客户功能开发中...')
}

// 查看
const handleView = (row) => {
  ElMessage.info(`查看客户: ${row.name}`)
}

// 编辑
const handleEdit = (row) => {
  ElMessage.info(`编辑客户: ${row.name}`)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除客户 "${row.name}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 重新加载数据
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 重新加载数据
}

// 初始化数据
onMounted(() => {
  // 加载数据
})
</script>

<style lang="scss" scoped>
.customers-page {
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

    .header-right {
      width: 100%;
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