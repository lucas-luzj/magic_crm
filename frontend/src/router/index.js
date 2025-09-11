import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { MyListPage } from 'lucas-my-form'
import { generateDynamicRoutes } from '@/utils/dynamicRoutes'
NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { 
          title: '仪表盘', 
          icon: 'DataAnalysis',
          keepAlive: true,
          affix: true
        }
      },
      {
        path: '/customers',
        name: 'Customers',
        component: () => import('@/views/customers/index.vue'),
        meta: { 
          title: '客户管理', 
          icon: 'User',
          keepAlive: true
        }
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { 
          title: '订单管理', 
          icon: 'ShoppingCart',
          keepAlive: true
        }
      },
      {
        path: '/products',
        name: 'Products',
        component: () => import('@/views/products/index.vue'),
        meta: { 
          title: '产品管理', 
          icon: 'Box',
          keepAlive: true
        }
      },
      {
        path: '/reports',
        name: 'Reports',
        component: () => import('@/views/reports/index.vue'),
        meta: { 
          title: '报表分析', 
          icon: 'TrendCharts',
          keepAlive: true
        }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { 
          title: '个人中心', 
          icon: 'User',
          keepAlive: false
        }
      },
      {
        path: '/users',
        component: MyListPage,
        props:{
          tbname:"User"
        },
        meta: { 
          title: '用户管理', 
          icon: 'User',
        }

      },
      {
        path: '/system/department',
        name: 'Department',
        component: () => import('@/views/system/department.vue'),
        meta: { 
          title: '部门管理', 
          icon: 'OfficeBuilding',
          keepAlive: true
        }
      },
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/user.vue'),
        meta: { 
          title: '用户管理', 
          icon: 'User',
          keepAlive: true
        }
      },
      // 工作流管理路由
      {
        path: '/workflow',
        name: 'Workflow',
        component: () => import('@/views/workflow/index.vue'),
        meta: { 
          title: '工作流管理', 
          icon: 'Operation',
          keepAlive: true
        }
      },
      {
        path: '/workflow/process-designer',
        name: 'ProcessDesigner',
        component: () => import('@/views/workflow/ProcessDesigner.vue'),
        meta: { 
          title: '流程设计器', 
          icon: 'Edit',
          keepAlive: false,
          hideInMenu: true
        }
      },
      {
        path: '/workflow/process-definitions',
        name: 'ProcessDefinitions',
        component: () => import('@/views/workflow/ProcessDefinitions.vue'),
        meta: { 
          title: '流程定义管理', 
          icon: 'Document',
          keepAlive: true,
          hideInMenu: true
        }
      },
      {
        path: '/workflow/process-instances',
        name: 'ProcessInstances',
        component: () => import('@/views/workflow/ProcessInstances.vue'),
        meta: { 
          title: '流程实例管理', 
          icon: 'Operation',
          keepAlive: true,
          hideInMenu: true
        }
      },
      {
        path: '/workflow/tasks',
        name: 'Tasks',
        component: () => import('@/views/workflow/Tasks.vue'),
        meta: { 
          title: '任务管理', 
          icon: 'List',
          keepAlive: true,
          hideInMenu: true
        }
      },
       {
        path: '/workflow/start-process',
        name: 'StartProcess',
        component: () => import('@/views/workflow/StartProcess.vue'),
        meta: { 
          title: '发起流程', 
          icon: 'Plus',
          keepAlive: false
        }
      },
      {
        path: '/workflow/process-start/:id',
        name: 'ProcessStart',
        component: () => import('@/views/workflow/ProcessStart.vue'),
        meta: { 
          title: '启动流程', 
          icon: 'Plus',
          keepAlive: false,
          hideInMenu: true
        }
      },
      {
        path: '/workflow/task-handle/:id',
        name: 'TaskHandle',
        component: () => import('@/views/workflow/TaskHandle.vue'),
        meta: { 
          title: '处理任务', 
          icon: 'Edit',
          keepAlive: false,
          hideInMenu: true
        }
      },
      {
        path: '/workflow/process-monitor',
        name: 'ProcessMonitor',
        component: () => import('@/views/workflow/ProcessMonitor.vue'),
        meta: { 
          title: '流程监控', 
          icon: 'Monitor',
          keepAlive: true
        }
      },
      {
        path: '/workspace',
        name: 'Workspace',
        component: () => import('@/views/workspace/index.vue'),
        meta: { 
          title: '个人工作台', 
          icon: 'Monitor',
          keepAlive: true
        }
      },
      // 表单管理路由
      {
        path: '/form/templates',
        name: 'FormTemplateManagement',
        component: () => import('@/views/form/FormTemplateManagement.vue'),
        meta: { 
          title: '表单模板管理', 
          icon: 'Document',
          keepAlive: true
        }
      },
      {
        path: '/form/designer',
        name: 'FormDesigner',
        component: () => import('@/views/form/FormDesigner.vue'),
        meta: { 
          title: '表单设计器', 
          icon: 'Edit',
          keepAlive: false
        }
      },
      {
        path: '/form/data',
        name: 'FormDataManagement',
        component: () => import('@/views/form/FormDataManagement.vue'),
        meta: { 
          title: '表单数据管理', 
          icon: 'List',
          keepAlive: true
        }
      },
      {
        path: '/form/mapping',
        name: 'ProcessFormMappingManagement',
        component: () => import('@/views/form/ProcessFormMappingManagement.vue'),
        meta: { 
          title: '流程表单映射', 
          icon: 'Connection',
          keepAlive: true
        }
      },
      // 系统管理路由
      {
        path: '/system/dictionary',
        name: 'DictionaryManagement',
        component: () => import('@/views/system/DictionaryManagement.vue'),
        meta: { 
          title: '字典管理', 
          icon: 'List',
          keepAlive: true
        }
      },
      {
        path: '/notifications',
        name: 'Notifications',
        component: () => import('@/views/notifications/index.vue'),
        meta: { 
          title: '消息通知', 
          icon: 'Bell',
          keepAlive: true
        }
      },
      {
        path: '/notification-templates',
        name: 'NotificationTemplates',
        component: () => import('@/views/notification-templates/index.vue'),
        meta: { 
          title: '通知模板', 
          icon: 'Document',
          keepAlive: true
        }
      },
      {
        path: '/notification-settings',
        name: 'NotificationSettings',
        component: () => import('@/views/notification-settings/index.vue'),
        meta: { 
          title: '通知设置', 
          icon: 'Setting',
          keepAlive: true
        }
      },
      {
        path: '/system/settings',
        name: 'SystemSettings',
        component: () => import('@/views/system/Settings.vue'),
        meta: { 
          title: '系统设置', 
          icon: 'Setting',
          keepAlive: true
        }
      },
     {
        path: '/system/roles',
        name: 'RolesSettings',
        component: () => import('@/views/system/role.vue'),
        meta: { 
          title: '角色管理', 
          icon: 'UserFilled',
          keepAlive: true
        }
      },
      {
        path: '/system/permission',
        name: 'PermissionSettings',
        component: () => import('@/views/system/permission.vue'),
        meta: { 
          title: '权限管理', 
          icon: 'Menu',
          keepAlive: true
        }
      },
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 是否已添加动态路由
let dynamicRoutesAdded = false

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const token = userStore.token
  
  // 检查是否需要认证
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    NProgress.done()
    return
  }
  
  // 如果已登录但访问登录页，重定向到首页
  if (to.path === '/login' && token) {
    next('/')
    NProgress.done()
    return
  }
  
  // 如果已登录但未添加动态路由，添加动态路由
  if (token && !dynamicRoutesAdded && userStore.userMenus.length > 0) {
    try {
      const dynamicRoutes = generateDynamicRoutes(userStore.userMenus, userStore.userPermissions)
      dynamicRoutes.forEach(route => {
        router.addRoute('Layout', route)
      })
      dynamicRoutesAdded = true
      
      // 如果当前路由不存在，重定向到首页
      if (to.matched.length === 0) {
        next('/')
        NProgress.done()
        return
      }
    } catch (error) {
      console.error('添加动态路由失败:', error)
    }
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router