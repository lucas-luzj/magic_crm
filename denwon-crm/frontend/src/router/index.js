import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

NProgress.configure({ showSpinner: false })

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      // 客户管理
      {
        path: 'customer',
        name: 'Customer',
        redirect: '/customer/list',
        meta: { title: '客户管理', icon: 'User' },
        children: [
          {
            path: 'list',
            name: 'CustomerList',
            component: () => import('@/views/customer/list.vue'),
            meta: { title: '客户列表' }
          },
          {
            path: 'pool',
            name: 'CustomerPool',
            component: () => import('@/views/customer/pool.vue'),
            meta: { title: '公海池' }
          },
          {
            path: 'detail/:id',
            name: 'CustomerDetail',
            component: () => import('@/views/customer/detail.vue'),
            meta: { title: '客户详情', hidden: true }
          }
        ]
      },
      // 线索管理
      {
        path: 'lead',
        name: 'Lead',
        redirect: '/lead/list',
        meta: { title: '线索管理', icon: 'Opportunity' },
        children: [
          {
            path: 'list',
            name: 'LeadList',
            component: () => import('@/views/lead/list.vue'),
            meta: { title: '线索列表' }
          },
          {
            path: 'detail/:id',
            name: 'LeadDetail',
            component: () => import('@/views/lead/detail.vue'),
            meta: { title: '线索详情', hidden: true }
          }
        ]
      },
      // 商机管理
      {
        path: 'opportunity',
        name: 'Opportunity',
        redirect: '/opportunity/list',
        meta: { title: '商机管理', icon: 'TrophyBase' },
        children: [
          {
            path: 'list',
            name: 'OpportunityList',
            component: () => import('@/views/opportunity/list.vue'),
            meta: { title: '商机列表' }
          },
          {
            path: 'board',
            name: 'OpportunityBoard',
            component: () => import('@/views/opportunity/board.vue'),
            meta: { title: '商机看板' }
          },
          {
            path: 'quote',
            name: 'QuoteList',
            component: () => import('@/views/opportunity/quote.vue'),
            meta: { title: '报价管理' }
          },
          {
            path: 'detail/:id',
            name: 'OpportunityDetail',
            component: () => import('@/views/opportunity/detail.vue'),
            meta: { title: '商机详情', hidden: true }
          }
        ]
      },
      // 合同管理
      {
        path: 'contract',
        name: 'Contract',
        redirect: '/contract/list',
        meta: { title: '合同管理', icon: 'Document' },
        children: [
          {
            path: 'list',
            name: 'ContractList',
            component: () => import('@/views/contract/list.vue'),
            meta: { title: '合同列表' }
          },
          {
            path: 'order',
            name: 'OrderList',
            component: () => import('@/views/contract/order.vue'),
            meta: { title: '订单管理' }
          },
          {
            path: 'delivery',
            name: 'DeliveryList',
            component: () => import('@/views/contract/delivery.vue'),
            meta: { title: '发货管理' }
          },
          {
            path: 'detail/:id',
            name: 'ContractDetail',
            component: () => import('@/views/contract/detail.vue'),
            meta: { title: '合同详情', hidden: true }
          }
        ]
      },
      // 项目管理
      {
        path: 'project',
        name: 'Project',
        redirect: '/project/list',
        meta: { title: '项目管理', icon: 'FolderOpened' },
        children: [
          {
            path: 'list',
            name: 'ProjectList',
            component: () => import('@/views/project/list.vue'),
            meta: { title: '项目列表' }
          },
          {
            path: 'task',
            name: 'TaskList',
            component: () => import('@/views/project/task.vue'),
            meta: { title: '任务管理' }
          },
          {
            path: 'detail/:id',
            name: 'ProjectDetail',
            component: () => import('@/views/project/detail.vue'),
            meta: { title: '项目详情', hidden: true }
          }
        ]
      },
      // 财务管理
      {
        path: 'finance',
        name: 'Finance',
        redirect: '/finance/receivable',
        meta: { title: '财务管理', icon: 'Wallet' },
        children: [
          {
            path: 'receivable',
            name: 'ReceivableList',
            component: () => import('@/views/finance/receivable.vue'),
            meta: { title: '应收管理' }
          },
          {
            path: 'receipt',
            name: 'ReceiptList',
            component: () => import('@/views/finance/receipt.vue'),
            meta: { title: '回款管理' }
          },
          {
            path: 'invoice',
            name: 'InvoiceList',
            component: () => import('@/views/finance/invoice.vue'),
            meta: { title: '发票管理' }
          },
          {
            path: 'commission',
            name: 'CommissionList',
            component: () => import('@/views/finance/commission.vue'),
            meta: { title: '提成管理' }
          }
        ]
      },
      // 产品管理
      {
        path: 'product',
        name: 'Product',
        redirect: '/product/list',
        meta: { title: '产品管理', icon: 'Box' },
        children: [
          {
            path: 'list',
            name: 'ProductList',
            component: () => import('@/views/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: 'category',
            name: 'ProductCategory',
            component: () => import('@/views/product/category.vue'),
            meta: { title: '产品分类' }
          },
          {
            path: 'price',
            name: 'PricePolicy',
            component: () => import('@/views/product/price.vue'),
            meta: { title: '价格策略' }
          }
        ]
      },
      // 采购管理
      {
        path: 'purchase',
        name: 'Purchase',
        redirect: '/purchase/list',
        meta: { title: '采购管理', icon: 'ShoppingCart' },
        children: [
          {
            path: 'list',
            name: 'PurchaseList',
            component: () => import('@/views/purchase/list.vue'),
            meta: { title: '采购合同' }
          },
          {
            path: 'supplier',
            name: 'SupplierList',
            component: () => import('@/views/purchase/supplier.vue'),
            meta: { title: '供应商管理' }
          },
          {
            path: 'inbound',
            name: 'InboundList',
            component: () => import('@/views/purchase/inbound.vue'),
            meta: { title: '入库管理' }
          }
        ]
      },
      // 售后服务
      {
        path: 'service',
        name: 'Service',
        redirect: '/service/asset',
        meta: { title: '售后服务', icon: 'Service' },
        children: [
          {
            path: 'asset',
            name: 'AssetList',
            component: () => import('@/views/service/asset.vue'),
            meta: { title: '设备管理' }
          },
          {
            path: 'order',
            name: 'ServiceOrderList',
            component: () => import('@/views/service/order.vue'),
            meta: { title: '服务工单' }
          },
          {
            path: 'warranty',
            name: 'WarrantyList',
            component: () => import('@/views/service/warranty.vue'),
            meta: { title: '保修合同' }
          },
          {
            path: 'knowledge',
            name: 'KnowledgeBase',
            component: () => import('@/views/service/knowledge.vue'),
            meta: { title: '知识库' }
          }
        ]
      },
      // 办公协同
      {
        path: 'office',
        name: 'Office',
        redirect: '/office/task',
        meta: { title: '办公协同', icon: 'OfficeBuilding' },
        children: [
          {
            path: 'task',
            name: 'MyTask',
            component: () => import('@/views/office/task.vue'),
            meta: { title: '我的任务' }
          },
          {
            path: 'calendar',
            name: 'Calendar',
            component: () => import('@/views/office/calendar.vue'),
            meta: { title: '日程管理' }
          },
          {
            path: 'notification',
            name: 'Notification',
            component: () => import('@/views/office/notification.vue'),
            meta: { title: '通知中心' }
          },
          {
            path: 'approval',
            name: 'Approval',
            component: () => import('@/views/office/approval.vue'),
            meta: { title: '审批中心' }
          }
        ]
      },
      // 报表中心
      {
        path: 'report',
        name: 'Report',
        redirect: '/report/sales',
        meta: { title: '报表中心', icon: 'DataAnalysis' },
        children: [
          {
            path: 'sales',
            name: 'SalesReport',
            component: () => import('@/views/report/sales.vue'),
            meta: { title: '销售报表' }
          },
          {
            path: 'finance',
            name: 'FinanceReport',
            component: () => import('@/views/report/finance.vue'),
            meta: { title: '财务报表' }
          },
          {
            path: 'service',
            name: 'ServiceReport',
            component: () => import('@/views/report/service.vue'),
            meta: { title: '服务报表' }
          },
          {
            path: 'custom',
            name: 'CustomReport',
            component: () => import('@/views/report/custom.vue'),
            meta: { title: '自定义报表' }
          }
        ]
      },
      // 系统管理
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting', permission: 'system.view' },
        children: [
          {
            path: 'user',
            name: 'UserManagement',
            component: () => import('@/views/system/user.vue'),
            meta: { title: '用户管理', permission: 'user.view' }
          },
          {
            path: 'role',
            name: 'RoleManagement',
            component: () => import('@/views/system/role.vue'),
            meta: { title: '角色管理', permission: 'role.view' }
          },
          {
            path: 'org',
            name: 'OrgManagement',
            component: () => import('@/views/system/org.vue'),
            meta: { title: '组织管理', permission: 'org.view' }
          },
          {
            path: 'dict',
            name: 'DictManagement',
            component: () => import('@/views/system/dict.vue'),
            meta: { title: '数据字典', permission: 'system.manage' }
          },
          {
            path: 'workflow',
            name: 'WorkflowManagement',
            component: () => import('@/views/system/workflow.vue'),
            meta: { title: '工作流管理', permission: 'system.manage' }
          },
          {
            path: 'log',
            name: 'LogManagement',
            component: () => import('@/views/system/log.vue'),
            meta: { title: '系统日志', permission: 'system.manage' }
          }
        ]
      },
      // 个人中心
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', hidden: true }
      }
    ]
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限', public: true }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', public: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  // 设置页面标题
  document.title = `${to.meta.title || '页面'} - Denwon CRM`
  
  const authStore = useAuthStore()
  const token = authStore.token
  
  // 公开页面直接访问
  if (to.meta.public) {
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
    return
  }
  
  // 需要登录的页面
  if (!token) {
    ElMessage.warning('请先登录')
    next(`/login?redirect=${to.path}`)
    return
  }
  
  // 检查权限
  if (to.meta.permission) {
    const hasPermission = authStore.hasPermission(to.meta.permission)
    if (!hasPermission) {
      ElMessage.error('您没有访问该页面的权限')
      next('/403')
      return
    }
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router