<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="layout-sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-logo">
        <el-icon :size="24" color="#fff">
          <Platform />
        </el-icon>
        <span v-if="!isCollapsed" class="logo-text">Denwon CRM</span>
      </div>
      
      <el-scrollbar class="sidebar-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
        >
          <sidebar-item
            v-for="route in menuRoutes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
          />
        </el-menu>
      </el-scrollbar>
    </aside>
    
    <!-- 主体区域 -->
    <div class="layout-main" :class="{ collapsed: isCollapsed }">
      <!-- 顶部导航栏 -->
      <header class="layout-header">
        <div class="header-left">
          <el-icon 
            class="collapse-btn"
            :size="20"
            @click="toggleSidebar"
          >
            <component :is="isCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
          
          <breadcrumb />
        </div>
        
        <div class="header-right">
          <!-- 搜索 -->
          <el-tooltip content="全局搜索" placement="bottom">
            <el-icon class="header-icon" :size="18">
              <Search />
            </el-icon>
          </el-tooltip>
          
          <!-- 消息通知 -->
          <el-popover
            placement="bottom"
            :width="350"
            trigger="click"
          >
            <template #reference>
              <el-badge :value="unreadCount" :hidden="!unreadCount" class="header-badge">
                <el-icon class="header-icon" :size="18">
                  <Bell />
                </el-icon>
              </el-badge>
            </template>
            <notification-panel />
          </el-popover>
          
          <!-- 全屏 -->
          <el-tooltip content="全屏" placement="bottom">
            <el-icon 
              class="header-icon" 
              :size="18"
              @click="toggleFullscreen"
            >
              <FullScreen />
            </el-icon>
          </el-tooltip>
          
          <!-- 用户菜单 -->
          <el-dropdown class="user-dropdown" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatar" class="user-avatar">
                {{ userName.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="showSettings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 标签页 -->
      <div class="layout-tags">
        <tags-view />
      </div>
      
      <!-- 内容区域 -->
      <main class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </main>
    </div>
    
    <!-- 设置抽屉 -->
    <settings-drawer v-model="showSettingsDrawer" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { ElMessageBox } from 'element-plus'
import SidebarItem from './components/SidebarItem.vue'
import Breadcrumb from './components/Breadcrumb.vue'
import TagsView from './components/TagsView.vue'
import NotificationPanel from './components/NotificationPanel.vue'
import SettingsDrawer from './components/SettingsDrawer.vue'
import { 
  Platform, Search, Bell, FullScreen, User, Setting, 
  SwitchButton, ArrowDown, Expand, Fold 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

// 状态
const isCollapsed = ref(false)
const showSettingsDrawer = ref(false)
const unreadCount = ref(5)

// 计算属性
const activeMenu = computed(() => route.path)
const userName = computed(() => authStore.userName)
const userAvatar = computed(() => authStore.avatar)
const cachedViews = computed(() => appStore.cachedViews)
const menuRoutes = computed(() => {
  const routes = router.options.routes.find(r => r.path === '/')
  return routes?.children?.filter(r => !r.meta?.hidden) || []
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
  appStore.toggleSidebar()
}

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 前往个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 显示设置
const showSettings = () => {
  showSettingsDrawer.value = true
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    authStore.handleLogout()
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  // 获取用户信息
  if (!authStore.userInfo) {
    authStore.fetchUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

// 侧边栏
.layout-sidebar {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  
  &.collapsed {
    width: 64px;
  }
  
  .sidebar-logo {
    display: flex;
    align-items: center;
    height: 60px;
    padding: 0 20px;
    background-color: #2b3544;
    color: #fff;
    
    .logo-text {
      margin-left: 12px;
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
    }
  }
  
  .sidebar-scrollbar {
    height: calc(100% - 60px);
    
    :deep(.el-menu) {
      border: none;
      
      .el-menu-item {
        &.is-active {
          background-color: rgba(64, 158, 255, 0.1);
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 3px;
            background-color: #409eff;
          }
        }
      }
    }
  }
}

// 主体区域
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 210px;
  transition: margin-left 0.3s;
  
  &.collapsed {
    margin-left: 64px;
  }
}

// 顶部导航栏
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    
    .collapse-btn {
      cursor: pointer;
      margin-right: 20px;
      transition: color 0.3s;
      
      &:hover {
        color: #409eff;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .header-icon {
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #409eff;
      }
    }
    
    .header-badge {
      line-height: 1;
    }
    
    .user-dropdown {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;
        
        .user-avatar {
          margin-right: 8px;
        }
        
        .user-name {
          margin-right: 4px;
          font-size: 14px;
        }
      }
    }
  }
}

// 标签页
.layout-tags {
  height: 40px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}

// 内容区域
.layout-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}

// 动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

// 响应式
@media (max-width: 768px) {
  .layout-sidebar {
    position: fixed;
    left: 0;
    z-index: 1000;
    transform: translateX(0);
    
    &.collapsed {
      transform: translateX(-100%);
    }
  }
  
  .layout-main {
    margin-left: 0 !important;
  }
}
</style>