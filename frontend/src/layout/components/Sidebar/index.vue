<template>
  <div class="sidebar" :class="{ 'collapsed': isCollapsed }">
    <!-- Logo区域 -->
    <div class="sidebar-header">
      <router-link to="/" class="sidebar-logo">
        <div class="logo-container">
          <div class="logo-icon">
            <el-icon>
              <MagicStick />
            </el-icon>
          </div>
          <div v-show="!isCollapsed" class="logo-content">
            <h1 class="logo-title">Magic CRM</h1>
            <p class="logo-subtitle">企业管理系统</p>
          </div>
        </div>
      </router-link>
    </div>

    <!-- 菜单区域 -->
    <div class="sidebar-content">
      <el-scrollbar class="menu-scrollbar">
        <nav class="sidebar-nav">
          <MenuGroup
            v-for="group in menuGroups"
            :key="group.key"
            :group="group"
            :is-collapsed="isCollapsed"
            :accordion-mode="accordionMode"
            :expanded="expandedGroupKey === group.key"
            @toggle="handleMenuGroupToggle"
          />
        </nav>
      </el-scrollbar>
    </div>

    <!-- 底部用户信息区域 -->
    <div class="sidebar-footer" v-show="!isCollapsed">
      <div class="user-info">
        <div class="user-avatar">
          <el-avatar :size="32" src="/default-avatar.png">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
        <div class="user-details">
          <p class="user-name">管理员</p>
          <p class="user-role">系统管理员</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { MagicStick, User } from '@element-plus/icons-vue'
import MenuGroup from './MenuGroup.vue'
import { useThemeStore } from '@/stores/theme'
import { usePermissionStore } from '@/stores/permission'
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const permissionStore = usePermissionStore()

const isCollapsed = computed(() => themeStore.sidebarCollapsed)

// 手风琴模式控制参数
const accordionMode = ref(true)

// 使用动态菜单数据
// const menuGroups = computed(() => permissionStore.userMenus)

const menuGroups = ref([
  {
    key: 'dashboard',
    title: '仪表盘',
    icon: 'DataAnalysis',
    items: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        title: '仪表盘',
        icon: 'DataAnalysis'
      },
      {
        path: '/workspace',
        name: 'Workspace',
        title: '个人工作台',
        icon: 'Monitor'
      }
    ]
  },
  {
    key: 'business',
    title: '业务管理',
    icon: 'Briefcase',
    items: [
      {
        path: '/customers',
        name: 'Customers',
        title: '客户管理',
        icon: 'User'
      },
      {
        path: '/orders',
        name: 'Orders',
        title: '订单管理',
        icon: 'ShoppingCart'
      },
      {
        path: '/products',
        name: 'Products',
        title: '产品管理',
        icon: 'Box'
      },
      {
        path: '/reports',
        name: 'Reports',
        title: '报表分析',
        icon: 'TrendCharts'
      }
    ]
  },
  {
    key: 'workflow',
    title: '工作流管理',
    icon: 'Operation',
    items: [
      {
        path: '/workflow',
        name: 'Workflow',
        title: '工作流管理',
        icon: 'Operation'
      },
      {
        path: '/workflow/start-process',
        name: 'StartProcess',
        title: '发起流程',
        icon: 'Plus'
      }
    ]
  },
  {
    key: 'form',
    title: '表单管理',
    icon: 'Document',
    items: [
      {
        path: '/form/templates',
        name: 'FormTemplateManagement',
        title: '表单模板管理',
        icon: 'Document'
      },
      {
        path: '/form/designer',
        name: 'FormDesigner',
        title: '表单设计器',
        icon: 'Edit'
      },
      {
        path: '/form/data',
        name: 'FormDataManagement',
        title: '表单数据管理',
        icon: 'List'
      },
      {
        path: '/form/mapping',
        name: 'ProcessFormMappingManagement',
        title: '流程表单映射',
        icon: 'Connection'
      }
    ]
  },
  {
    key: 'system',
    title: '系统管理',
    icon: 'Setting',
    items: [
      {
        path: '/system/user',
        name: 'Users',
        title: '用户管理',
        icon: 'User'
      },
      {
        path: '/system/department',
        name: 'Depts',
        title: '部门管理',
        icon: 'OfficeBuilding'
      },
      {
        path: '/system/dictionary',
        name: 'DictionaryManagement',
        title: '字典管理',
        icon: 'List'
      },
      {
        path: '/notification-templates',
        name: 'NotificationTemplates',
        title: '通知模板',
        icon: 'Document'
      },
      {
        path: '/notification-settings',
        name: 'NotificationSettings',
        title: '通知设置',
        icon: 'Setting'
      },
      {
        path: '/system/settings',
        name: 'SystemSettings',
        title: '系统设置',
        icon: 'Setting'
      },
      {
        path: '/system/roles',
        name: 'RolesSettings',
        title: '角色管理',
        icon: 'UserFilled'
      },
      {
        path: '/system/permission',
        name: 'PermissionSettings',
        title: '权限管理',
        icon: 'Menu'
      }
    ]
  }
])
// 当前展开的菜单组
const expandedGroupKey = ref(null)

// 处理菜单组切换
const handleMenuGroupToggle = (groupKey) => {
  if (accordionMode.value) {
    expandedGroupKey.value = expandedGroupKey.value === groupKey ? null : groupKey
  }
}

// 监听菜单数据变化，确保组件更新
// watch(() => permissionStore.userMenus, (newMenus) => {
//   console.log('菜单数据已更新:', newMenus)
// }, { deep: true })

// // 组件挂载时获取用户菜单
// onMounted(() => {
//   if (permissionStore.userMenus.length === 0) {
//     permissionStore.fetchUserMenus()
//   }
// })
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  border-right: 1px solid rgba(148, 163, 184, 0.1);
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(147, 51, 234, 0.05) 100%);
    pointer-events: none;
  }

  &.collapsed {
    width: 80px;
  }

  .sidebar-header {
    padding: 20px;
    border-bottom: 1px solid rgba(148, 163, 184, 0.1);
    background: rgba(15, 23, 42, 0.8);
    backdrop-filter: blur(20px);
    position: relative;
    z-index: 1;

    .sidebar-logo {
      text-decoration: none;
      color: inherit;
      display: block;

      .logo-container {
        display: flex;
        align-items: center;
        gap: 12px;
        transition: all 0.3s ease;

        .logo-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          background: linear-gradient(135deg, #3b82f6, #8b5cf6);
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 8px 32px rgba(59, 130, 246, 0.3);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
            transform: translateX(-100%);
            transition: transform 0.6s ease;
          }

          &:hover::before {
            transform: translateX(100%);
          }

          .el-icon {
            font-size: 24px;
            color: white;
            z-index: 1;
          }
        }

        .logo-content {
          flex: 1;
          min-width: 0;

          .logo-title {
            font-size: 20px;
            font-weight: 700;
            color: #f8fafc;
            margin: 0 0 4px 0;
            line-height: 1.2;
            background: linear-gradient(135deg, #f8fafc, #cbd5e1);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }

          .logo-subtitle {
            font-size: 12px;
            color: #94a3b8;
            margin: 0;
            font-weight: 500;
            letter-spacing: 0.5px;
          }
        }
      }
    }
  }

  .sidebar-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .menu-scrollbar {
      flex: 1;
      height: 100%;

      :deep(.el-scrollbar__view) {
        height: 100%;
      }

      :deep(.el-scrollbar__bar) {
        right: 4px;
        width: 4px;
        border-radius: 2px;

        &.is-vertical {
          .el-scrollbar__thumb {
            background: rgba(148, 163, 184, 0.3);
            border-radius: 2px;

            &:hover {
              background: rgba(148, 163, 184, 0.5);
            }
          }
        }
      }
    }

    .sidebar-nav {
      padding: 16px 12px;
      height: 100%;

      :deep(.menu-group) {
        margin-bottom: 8px;
      }
    }
  }

  .sidebar-footer {
    padding: 16px;
    border-top: 1px solid rgba(148, 163, 184, 0.1);
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(20px);

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 12px;
      background: rgba(30, 41, 59, 0.5);
      border: 1px solid rgba(148, 163, 184, 0.1);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(30, 41, 59, 0.8);
        border-color: rgba(59, 130, 246, 0.3);
        transform: translateY(-2px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
      }

      .user-avatar {
        flex-shrink: 0;
      }

      .user-details {
        flex: 1;
        min-width: 0;

        .user-name {
          font-size: 14px;
          font-weight: 600;
          color: #f8fafc;
          margin: 0 0 2px 0;
          line-height: 1.2;
        }

        .user-role {
          font-size: 12px;
          color: #94a3b8;
          margin: 0;
          line-height: 1.2;
        }
      }
    }
  }

  // 折叠状态样式
  &.collapsed {
    .sidebar-header {
      padding: 20px 16px;

      .logo-container {
        justify-content: center;

        .logo-icon {
          width: 40px;
          height: 40px;
        }
      }
    }

    .sidebar-content {
      .sidebar-nav {
        padding: 16px 8px;
      }
    }

    .sidebar-footer {
      padding: 16px 8px;

      .user-info {
        justify-content: center;
        padding: 8px;

        .user-details {
          display: none;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;

    &:not(.collapsed) {
      transform: translateX(0);
    }
  }
}
</style>