<template>
  <div class="notifications-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>消息通知</h2>
        <p class="header-subtitle">管理您的所有通知消息</p>
      </div>
      <div class="header-right">
        <el-button @click="markAllAsRead" :disabled="unreadCount === 0">
          <el-icon><Check /></el-icon>
          全部已读
        </el-button>
        <el-button type="danger" @click="batchDelete" :disabled="selectedNotifications.length === 0">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="通知类型">
          <el-select v-model="filterForm.type" placeholder="选择类型" clearable>
            <el-option label="全部类型" value="" />
            <el-option label="任务通知" value="task" />
            <el-option label="流程通知" value="process" />
            <el-option label="系统通知" value="system" />
            <el-option label="警告通知" value="warning" />
            <el-option label="成功通知" value="success" />
            <el-option label="信息通知" value="info" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
            <el-option label="全部状态" value="" />
            <el-option label="未读" value="unread" />
            <el-option label="已读" value="read" />
          </el-select>
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

    <!-- 通知列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h4>通知列表</h4>
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
        :data="notifications"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-icon :size="20" :color="getNotificationColor(row.type)">
              <component :is="getNotificationIcon(row.type)" />
            </el-icon>
          </template>
        </el-table-column>
        
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="message" label="内容" min-width="300" show-overflow-tooltip />
        
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'success' : 'warning'" size="small">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handleNotificationClick(row)"
              v-if="!row.isRead"
            >
              标记已读
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteNotification(row.id)"
            >
              删除
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

    <!-- 通知设置对话框 -->
    <el-dialog
      v-model="settingsDialogVisible"
      title="通知设置"
      width="600px"
    >
      <NotificationSettings @settings-updated="handleSettingsUpdated" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Check, Delete, Search, Refresh, Clock, Document, 
  Warning, InfoFilled, User, Setting 
} from '@element-plus/icons-vue'
import NotificationSettings from '@/components/NotificationSettings.vue'
import { notificationApi } from '@/api/notification'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const notifications = ref([])
const selectedNotifications = ref([])
const unreadCount = ref(0)
const settingsDialogVisible = ref(false)

// 筛选表单
const filterForm = reactive({
  type: '',
  status: '',
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
  await loadNotifications()
  await loadUnreadCount()
})

/**
 * 加载通知列表
 */
const loadNotifications = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.page,
      size: pagination.size,
      type: filterForm.type,
      status: filterForm.status
    }
    
    // 处理时间范围
    if (filterForm.dateRange && filterForm.dateRange.length === 2) {
      params.startTime = filterForm.dateRange[0]
      params.endTime = filterForm.dateRange[1]
    }
    
    const response = await notificationApi.getNotifications(params)
    
    if (response.data && response.data.data) {
      notifications.value = response.data.data.records || response.data.data
      pagination.total = response.data.data.total || 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载未读数量
 */
const loadUnreadCount = async () => {
  try {
    const response = await notificationApi.getUnreadCount()
    if (response.data && response.data.data) {
      unreadCount.value = response.data.data.count || 0
    }
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  loadNotifications()
}

/**
 * 重置筛选条件
 */
const handleReset = () => {
  Object.assign(filterForm, {
    type: '',
    status: '',
    dateRange: []
  })
  pagination.page = 1
  loadNotifications()
}

/**
 * 刷新列表
 */
const refreshList = () => {
  loadNotifications()
  loadUnreadCount()
}

/**
 * 处理通知点击
 */
const handleNotificationClick = async (notification) => {
  try {
    if (!notification.isRead) {
      await notificationApi.markAsRead(notification.id)
      notification.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      ElMessage.success('已标记为已读')
    }
    
    // 根据通知类型跳转到相应页面
    if (notification.actionUrl) {
      router.push(notification.actionUrl)
    }
  } catch (error) {
    console.error('处理通知点击失败:', error)
    ElMessage.error('处理通知失败')
  }
}

/**
 * 删除通知
 */
const deleteNotification = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除此通知吗？', '确认删除', {
      type: 'warning'
    })
    
    await notificationApi.deleteNotification(id)
    notifications.value = notifications.value.filter(n => n.id !== id)
    ElMessage.success('通知已删除')
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除通知失败:', error)
    ElMessage.error('删除通知失败')
  }
}

/**
 * 批量删除
 */
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedNotifications.value.length} 条通知吗？`, '确认删除', {
      type: 'warning'
    })
    
    const ids = selectedNotifications.value.map(n => n.id)
    await notificationApi.batchDeleteNotifications(ids)
    
    notifications.value = notifications.value.filter(n => !ids.includes(n.id))
    selectedNotifications.value = []
    ElMessage.success('批量删除成功')
  } catch (error) {
    if (error === 'cancel') return
    console.error('批量删除失败:', error)
    ElMessage.error('批量删除失败')
  }
}

/**
 * 标记全部为已读
 */
const markAllAsRead = async () => {
  try {
    await notificationApi.markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
    ElMessage.success('已标记全部为已读')
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('标记全部已读失败')
  }
}

/**
 * 选择变化
 */
const handleSelectionChange = (selection) => {
  selectedNotifications.value = selection
}

/**
 * 页面大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadNotifications()
}

/**
 * 当前页变化
 */
const handleCurrentChange = (page) => {
  pagination.page = page
  loadNotifications()
}

/**
 * 设置更新回调
 */
const handleSettingsUpdated = () => {
  ElMessage.success('通知设置已更新')
  settingsDialogVisible.value = false
}

/**
 * 获取通知图标
 */
const getNotificationIcon = (type) => {
  const iconMap = {
    'task': Clock,
    'process': Document,
    'system': Setting,
    'warning': Warning,
    'success': Success,
    'info': InfoFilled,
    'user': User
  }
  return iconMap[type] || InfoFilled
}

/**
 * 获取通知颜色
 */
const getNotificationColor = (type) => {
  const colorMap = {
    'task': '#409EFF',
    'process': '#67C23A',
    'system': '#909399',
    'warning': '#E6A23C',
    'success': '#67C23A',
    'info': '#409EFF',
    'user': '#F56C6C'
  }
  return colorMap[type] || '#409EFF'
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.notifications-container {
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
  
  .header-right {
    display: flex;
    gap: 12px;
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
  .notifications-container {
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
