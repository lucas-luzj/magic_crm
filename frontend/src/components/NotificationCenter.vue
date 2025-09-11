<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-end"
    :width="400"
    trigger="click"
    popper-class="notification-popover"
  >
    <template #reference>
      <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0">
        <el-button :icon="Bell" circle />
      </el-badge>
    </template>

    <div class="notification-center">
      <!-- 头部 -->
      <div class="notification-header">
        <h4>消息通知</h4>
        <div class="header-actions">
          <el-button 
            size="small" 
            type="text" 
            @click="markAllAsRead"
            :disabled="unreadCount === 0"
          >
            全部已读
          </el-button>
          <el-button 
            size="small" 
            type="text" 
            @click="goToNotificationPage"
          >
            查看全部
          </el-button>
        </div>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list" v-loading="loading">
        <div 
          v-for="notification in notifications" 
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead }"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon">
            <el-icon :size="20" :color="getNotificationColor(notification.type)">
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.message }}</div>
            <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
          </div>
          <div class="notification-actions">
            <el-button 
              size="small" 
              type="text" 
              @click.stop="deleteNotification(notification.id)"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <el-empty 
          v-if="!loading && notifications.length === 0"
          description="暂无通知"
          :image-size="80"
        />

        <div class="notification-footer" v-if="notifications.length > 0">
          <el-button size="small" type="primary" @click="goToNotificationPage">
            查看全部通知
          </el-button>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Bell, Close, Document, Warning, InfoFilled, 
  User, Setting, Clock, Check 
} from '@element-plus/icons-vue'
import { notificationApi } from '@/api/notification'

// 路由
const router = useRouter()

// 响应式数据
const visible = ref(false)
const loading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)

// 定时器
let refreshTimer = null

// 页面初始化
onMounted(() => {
  loadNotifications()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})

/**
 * 加载通知列表
 */
const loadNotifications = async () => {
  try {
    loading.value = true
    
    // 并行加载通知列表和未读数量
    const [notificationsResponse, unreadResponse] = await Promise.all([
      notificationApi.getNotifications({ size: 10 }),
      notificationApi.getUnreadCount()
    ])
    
    if (notificationsResponse.data && notificationsResponse.data.data) {
      notifications.value = notificationsResponse.data.data.records || notificationsResponse.data.data
    }
    
    if (unreadResponse.data && unreadResponse.data.data) {
      unreadCount.value = unreadResponse.data.data.count || 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 开始自动刷新
 */
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    if (!visible.value) {
      loadNotifications()
    }
  }, 30000) // 30秒刷新一次
}

/**
 * 停止自动刷新
 */
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

/**
 * 处理通知点击
 */
const handleNotificationClick = async (notification) => {
  try {
    // 标记为已读
    if (!notification.isRead) {
      await notificationApi.markAsRead(notification.id)
      notification.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
    
    // 根据通知类型跳转到相应页面
    if (notification.actionUrl) {
      router.push(notification.actionUrl)
      visible.value = false
    }
  } catch (error) {
    console.error('处理通知点击失败:', error)
  }
}

/**
 * 删除通知
 */
const deleteNotification = async (id) => {
  try {
    await notificationApi.deleteNotification(id)
    notifications.value = notifications.value.filter(n => n.id !== id)
    ElMessage.success('通知已删除')
  } catch (error) {
    console.error('删除通知失败:', error)
    ElMessage.error('删除通知失败')
  }
}

/**
 * 标记所有为已读
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
 * 前往通知页面
 */
const goToNotificationPage = () => {
  router.push('/notifications')
  visible.value = false
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
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  
  const now = new Date()
  const targetTime = new Date(time)
  const diff = now - targetTime
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return targetTime.toLocaleDateString('zh-CN')
}
</script>

<style lang="scss" scoped>
.notification-center {
  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    margin-bottom: 12px;
    
    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .notification-list {
    max-height: 400px;
    overflow-y: auto;
    
    .notification-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.2s;
      
      &:hover {
        background-color: var(--el-fill-color-lighter);
      }
      
      &.unread {
        background-color: var(--el-color-primary-light-9);
        border-left: 3px solid var(--el-color-primary);
      }
      
      .notification-icon {
        flex-shrink: 0;
        margin-top: 2px;
      }
      
      .notification-content {
        flex: 1;
        min-width: 0;
        
        .notification-title {
          font-size: 14px;
          font-weight: 500;
          color: var(--el-text-color-primary);
          margin-bottom: 4px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .notification-message {
          font-size: 12px;
          color: var(--el-text-color-regular);
          line-height: 1.4;
          margin-bottom: 4px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .notification-time {
          font-size: 11px;
          color: var(--el-text-color-secondary);
        }
      }
      
      .notification-actions {
        flex-shrink: 0;
        opacity: 0;
        transition: opacity 0.2s;
      }
      
      &:hover .notification-actions {
        opacity: 1;
      }
    }
    
    .notification-footer {
      padding: 12px;
      text-align: center;
      border-top: 1px solid var(--el-border-color-lighter);
      margin-top: 12px;
    }
  }
}

// 滚动条样式
.notification-list::-webkit-scrollbar {
  width: 4px;
}

.notification-list::-webkit-scrollbar-track {
  background: var(--el-fill-color-lighter);
  border-radius: 2px;
}

.notification-list::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 2px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: var(--el-border-color-dark);
}
</style>

<style>
.notification-popover {
  padding: 0 !important;
}
</style>
