import { useUserStore } from '@/stores/user'

// 将后端菜单转换为前端路由
export function convertMenuToRoute(menu) {
  const route = {
    path: menu.path,
    name: menu.name,
    component: () => import(`@/views/${menu.component}.vue`),
    meta: {
      title: menu.title,
      icon: menu.icon,
      keepAlive: menu.keepAlive || false,
      affix: menu.affix || false,
      hideInMenu: menu.hideInMenu || false,
      requiresAuth: menu.requiresAuth !== false,
      permissions: menu.permissions || []
    }
  }

  // 如果有子菜单，递归处理
  if (menu.children && menu.children.length > 0) {
    route.children = menu.children.map(child => convertMenuToRoute(child))
  }

  return route
}

// 过滤有权限的路由
export function filterRoutesWithPermission(routes, permissions) {
  return routes.filter(route => {
    // 检查路由是否需要权限
    if (!route.meta.requiresAuth) {
      return true
    }

    // 检查路由权限
    if (route.meta.permissions && route.meta.permissions.length > 0) {
      return route.meta.permissions.some(permission => 
        permissions.includes(permission)
      )
    }

    // 默认允许访问
    return true
  })
}

// 生成动态路由
export function generateDynamicRoutes(menus, permissions) {
  const dynamicRoutes = menus.map(menu => convertMenuToRoute(menu))
  return filterRoutesWithPermission(dynamicRoutes, permissions)
}

// 检查路由权限
export function hasRoutePermission(route, permissions) {
  if (!route.meta.requiresAuth) {
    return true
  }

  if (route.meta.permissions && route.meta.permissions.length > 0) {
    return route.meta.permissions.some(permission => 
      permissions.includes(permission)
    )
  }

  return true
}

// 检查按钮权限
export function hasButtonPermission(buttonCode, permissions) {
  return permissions.includes(buttonCode)
}