import request from '@/utils/request'

// 获取菜单列表（分页）
export function getMenus(params) {
  return request({
    url: '/sys/menus',
    method: 'get',
    params
  })
}

// 获取菜单树形结构
export function getMenuTree() {
  return request({
    url: '/sys/menus/tree',
    method: 'get'
  })
}

// 获取根级菜单
export function getRootMenus() {
  return request({
    url: '/sys/menus/root',
    method: 'get'
  })
}

// 根据父菜单ID获取子菜单
export function getMenusByParentId(parentId) {
  return request({
    url: `/sys/menus/parent/${parentId}`,
    method: 'get'
  })
}

// 根据ID获取菜单详情
export function getMenuById(id) {
  return request({
    url: `/sys/menus/${id}`,
    method: 'get'
  })
}

// 创建菜单
export function createMenu(data) {
  return request({
    url: '/sys/menus',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(id, data) {
  return request({
    url: `/sys/menus/${id}`,
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/sys/menus/${id}`,
    method: 'delete'
  })
}

// 获取所有可见菜单（用于前端路由生成）
export function getVisibleMenus() {
  return request({
    url: '/sys/menus/visible',
    method: 'get'
  })
}

// 根据类型获取菜单
export function getMenusByType(type) {
  return request({
    url: `/sys/menus/type/${type}`,
    method: 'get'
  })
}