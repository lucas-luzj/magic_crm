<template>
  <div class="navbar">
    <div class="navbar-left">
      <!-- 折叠按钮 -->
      <div class="hamburger-container" @click="toggleSidebar">
        <el-icon class="hamburger" :class="{ 'is-active': !themeStore.sidebarCollapsed }">
          <Fold v-if="!themeStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 面包屑导航 -->
      <el-breadcrumb class="breadcrumb" separator="/">
        <el-breadcrumb-item
          v-for="item in breadcrumbList"
          :key="item.path"
          :to="item.path === route.path ? '' : item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="navbar-right">
      <!-- 全局搜索 -->
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索菜单..."
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          @focus="showSearchResults = true"
          @blur="hideSearchResults"
        />
        
        <!-- 搜索结果 -->
        <div v-if="showSearchResults && searchResults.length" class="search-results">
          <div
            v-for="result in searchResults"
            :key="result.path"
            class="search-result-item"
            @click="handleSearchResultClick(result)"
          >
            <el-icon v-if="result.icon">
              <component :is="result.icon" />
            </el-icon>
            <span>{{ result.title }}</span>
          </div>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="toolbar">
        <!-- 全屏切换 -->
        <el-tooltip content="全屏" placement="bottom">
          <div class="toolbar-item" @click="toggleFullscreen">
            <el-icon>
              <FullScreen />
            </el-icon>
          </div>
        </el-tooltip>

        <!-- 主题切换 -->
        <el-tooltip :content="themeStore.isDark ? '切换到亮色主题' : '切换到暗色主题'" placement="bottom">
          <div class="toolbar-item" @click="toggleTheme">
            <el-icon>
              <Sunny v-if="themeStore.isDark" />
              <Moon v-else />
            </el-icon>
          </div>
        </el-tooltip>

        <!-- 消息通知 -->
        <NotificationCenter />

        <!-- 用户菜单 -->
        <el-dropdown class="user-dropdown" @command="handleUserCommand">
          <div class="user-info">
            <el-avatar :size="32" :src="userStore.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="username">{{ userStore.nickname || userStore.username }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item command="changePassword">
                <el-icon><Lock /></el-icon>
                修改密码
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="changePasswordVisible"
      title="修改密码"
      width="400px"
      :before-close="handleClosePasswordDialog"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="80px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="changePasswordVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Fold, Expand, Search, FullScreen, Sunny, Moon, Bell,
  User, ArrowDown, Setting, Lock, SwitchButton
} from '@element-plus/icons-vue'
import NotificationCenter from '@/components/NotificationCenter.vue'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'
import { useTabsStore } from '@/stores/tabs'
import { changePassword } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const userStore = useUserStore()
const tabsStore = useTabsStore()

// 搜索相关
const searchKeyword = ref('')
const showSearchResults = ref(false)
const searchResults = ref([])

// 修改密码相关
const changePasswordVisible = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 面包屑导航
const breadcrumbList = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta.title
  }))
})

// 切换侧边栏
const toggleSidebar = () => {
  themeStore.toggleSidebar()
}

// 切换主题
const toggleTheme = () => {
  themeStore.toggleTheme()
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 搜索处理
const handleSearch = (value) => {
  if (!value.trim()) {
    searchResults.value = []
    return
  }

  // 模拟搜索结果
  const allRoutes = router.getRoutes()
  searchResults.value = allRoutes
    .filter(route => {
      return route.meta?.title && 
             route.meta.title.toLowerCase().includes(value.toLowerCase())
    })
    .map(route => ({
      path: route.path,
      title: route.meta.title,
      icon: route.meta.icon
    }))
    .slice(0, 5)
}

const hideSearchResults = () => {
  setTimeout(() => {
    showSearchResults.value = false
  }, 200)
}

const handleSearchResultClick = (result) => {
  router.push(result.path)
  searchKeyword.value = ''
  showSearchResults.value = false
  
  // 添加到标签页
  const routeInfo = {
    name: result.name,
    path: result.path,
    meta: { title: result.title, icon: result.icon }
  }
  tabsStore.addView(routeInfo)
}

// 用户菜单处理
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'changePassword':
      changePasswordVisible.value = true
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await changePassword(passwordForm)
        ElMessage.success('密码修改成功，请重新登录')
        changePasswordVisible.value = false
        handleLogout()
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const handleClosePasswordDialog = () => {
  passwordFormRef.value?.resetFields()
  changePasswordVisible.value = false
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.logout()
    location.reload()
    ElMessage.success('退出登录成功')
  })
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
}

.navbar-left {
  display: flex;
  align-items: center;

  .hamburger-container {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      background-color: rgba(0, 0, 0, 0.05);
    }

    .hamburger {
      font-size: 18px;
      color: var(--text-color);
      transition: transform 0.3s ease;

      &.is-active {
        transform: rotate(180deg);
      }
    }
  }

  .breadcrumb {
    margin-left: 20px;
    
    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: var(--text-color);
        font-weight: 400;

        &:hover {
          color: #409EFF;
        }
      }

      &:last-child .el-breadcrumb__inner {
        color: #909399;
        cursor: default;

        &:hover {
          color: #909399;
        }
      }
    }
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;

  .search-container {
    position: relative;
    width: 240px;

    .search-results {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      z-index: 1000;
      max-height: 200px;
      overflow-y: auto;

      .search-result-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        cursor: pointer;
        transition: background-color 0.3s ease;

        &:hover {
          background-color: #f5f7fa;
        }

        .el-icon {
          margin-right: 8px;
          color: #909399;
        }

        span {
          font-size: 14px;
          color: var(--text-color);
        }
      }
    }
  }

  .toolbar {
    display: flex;
    align-items: center;
    gap: 12px;

    .toolbar-item {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      cursor: pointer;
      border-radius: 6px;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
      }

      .el-icon {
        font-size: 16px;
        color: var(--text-color);
      }
    }
  }

  .user-dropdown {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
      }

      .username {
        margin: 0 8px;
        font-size: 14px;
        color: var(--text-color);
        font-weight: 500;
      }

      .dropdown-icon {
        font-size: 12px;
        color: #909399;
        transition: transform 0.3s ease;
      }

      &:hover .dropdown-icon {
        transform: rotate(180deg);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .navbar {
    padding: 0 15px;

    .navbar-left .breadcrumb {
      display: none;
    }

    .navbar-right {
      gap: 10px;

      .search-container {
        width: 180px;
      }

      .toolbar {
        gap: 8px;
      }
    }
  }
}
</style>