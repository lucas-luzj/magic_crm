import request from '@/utils/request'

// 获取角色列表（分页）
export function getRoles(params) {
  return request({
    url: '/sys/roles',
    method: 'get',
    params
  })
}

// 获取所有角色（下拉选择）
export function getAllRoles() {
  return request({
    url: '/sys/roles/all',
    method: 'get'
  })
}

// 根据ID获取角色详情
export function getRoleById(id) {
  return request({
    url: `/sys/roles/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/sys/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/sys/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/sys/roles/${id}`,
    method: 'delete'
  })
}

// 获取角色关联的菜单权限ID列表
export function getRoleMenuIds(id) {
  return request({
    url: `/sys/roles/${id}/menu-ids`,
    method: 'get'
  })
}

// 更新角色菜单权限
export function updateRoleMenus(id, menuIds) {
  return request({
    url: `/sys/roles/${id}/menus`,
    method: 'put',
    data: menuIds
  })
}