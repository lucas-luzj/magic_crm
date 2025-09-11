<template>
  <div class="app-wrapper" :class="{ 'sidebar-collapsed': themeStore.sidebarCollapsed }">
    <!-- 侧边栏 -->
    <div class="sidebar-container">
      <Sidebar />
    </div>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="navbar-container">
        <Navbar />
      </div>

      <!-- 标签页导航 -->
      <div class="tags-view-container">
        <TagsView />
      </div>

      <!-- 页面内容 -->
      <div class="app-main">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <keep-alive :include="tabsStore.cachedViews">
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar.vue'
import TagsView from './components/TagsView/index.vue'
import { useThemeStore } from '@/stores/theme'
import { useTabsStore } from '@/stores/tabs'

const themeStore = useThemeStore()
const tabsStore = useTabsStore()
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;

  &.sidebar-collapsed {
    .sidebar-container {
      width: 64px;
    }

    .main-container {
      margin-left: 64px;
    }
  }
}

.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 210px;
  height: 100vh;
  overflow: hidden;
  background: var(--sidebar-bg);
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  transition: width 0.28s ease-out;
  z-index: 1001;
}

.main-container {
  position: relative;
  margin-left: 210px;
  flex: 1;
  height: 100vh;
  transition: margin-left 0.28s ease-out;
  display: flex;
  flex-direction: column;
}

.navbar-container {
  height: 60px;
  background: var(--bg-color);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 1000;
}

.tags-view-container {
  height: 40px;
  background: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
}

.app-main {
  flex: 1;
  overflow: auto;
  background: #f0f2f5;
  padding: 20px;
}

// 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 响应式设计
@media (max-width: 768px) {
  .app-wrapper {
    .sidebar-container {
      width: 210px;
      transform: translateX(-100%);
      
      &.mobile-open {
        transform: translateX(0);
      }
    }

    .main-container {
      margin-left: 0;
      width: 100%;
    }

    &.sidebar-collapsed {
      .sidebar-container {
        transform: translateX(-100%);
      }

      .main-container {
        margin-left: 0;
      }
    }
  }
}
</style>