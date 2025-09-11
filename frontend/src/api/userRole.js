import request from '@/utils/request'

// 获取用户关联的角色列表
export function getUserRoles(userId) {
  return request({
    url: `/sys/user-roles/user/${userId}`,
    method: 'get'
  })
}

// 获取用户关联的角色ID列表
export function getUserRoleIds(userId) {
  return request({
    url: `/sys/user-roles/user/${userId}/role-ids`,
    method: 'get'
  })
}

// 更新用户角色关联
export function updateUserRoles(userId, roleIds) {
  return request({
    url: `/sys/user-roles/user/${userId}`,
    method: 'put',
    data: roleIds
  })
}

// 获取角色关联的用户数量
export function getRoleUserCount(roleId) {
  return request({
    url: `/sys/user-roles/role/${roleId}/user-count`,
    method: 'get'
  })
}

// 检查用户是否拥有指定角色
export function userHasRole(userId, roleId) {
  return request({
    url: `/sys/user-roles/user/${userId}/has-role/${roleId}`,
    method: 'get'
  })
}

// 检查用户是否拥有指定权限
export function userHasPermission(userId, permission) {
  return request({
    url: `/sys/user-roles/user/${userId}/has-permission/${permission}`,
    method: 'get'
  })
}

// 获取当前登录用户的菜单权限
export function getCurrentUserMenus() {
  return request({
    url: '/sys/user-roles/current-user/menus',
    method: 'get'
  })
}

// 获取当前登录用户的权限列表
export function getCurrentUserPermissions() {
  return request({
    url: '/sys/user-roles/current-user/permissions',
    method: 'get'
  })
}