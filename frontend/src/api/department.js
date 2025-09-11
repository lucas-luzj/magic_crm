import request from '@/utils/request'

// 获取部门树形结构
export function getDepartmentTree() {
  return request({
    url: '/departments/tree',
    method: 'get'
  })
}

// 获取部门列表（分页）
export function getDepartmentList(params) {
  return request({
    url: '/departments',
    method: 'get',
    params
  })
}

// 获取部门详情
export function getDepartmentById(id) {
  return request({
    url: `/departments/${id}`,
    method: 'get'
  })
}

// 创建部门
export function createDepartment(data) {
  return request({
    url: '/departments',
    method: 'post',
    data
  })
}

// 更新部门
export function updateDepartment(id, data) {
  return request({
    url: `/departments/${id}`,
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDepartment(id) {
  return request({
    url: `/departments/${id}`,
    method: 'delete'
  })
}

// 获取启用的部门列表
export function getActiveDepartments() {
  return request({
    url: '/departments/active',
    method: 'get'
  })
}

// 搜索部门
export function searchDepartments(name) {
  return request({
    url: '/departments/search',
    method: 'get',
    params: { name }
  })
}
