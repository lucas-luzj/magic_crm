<template>
  <div class="role-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><plus /></el-icon>新增角色
          </el-button>
        </div>
      </template>

      <!-- 搜索条件 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 角色列表 -->
      <el-table
        :data="roleList"
        v-loading="loading"
        style="width: 100%"
        :border="true"
      >
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleKey" label="角色标识" min-width="120" />
        <el-table-column prop="description" label="角色描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === true ? 'success' : 'danger'">
              {{ scope.row.status === true ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handlePermission(scope.row)"
            >
              权限
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :value="true">启用</el-radio>
            <el-radio :value="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限分配"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="menuTree"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="treeProps"
        :check-strictly="false"
        :default-checked-keys="checkedKeys"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPermissions">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getRoles, createRole, updateRole, deleteRole, getRoleMenuIds, updateRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

// 角色列表数据
const roleList = ref([])
const loading = ref(false)
const total = ref(0)

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: ''
})

// 对话框相关
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const dialogTitle = ref('')
const currentRoleId = ref(null)

// 表单相关
const roleFormRef = ref()
const roleForm = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  status: true
})

// 表单验证规则
const roleRules = reactive({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色标识', trigger: 'blur' }
  ]
})

// 权限树相关
const permissionTreeRef = ref()
const menuTree = ref([])
const treeProps = {
  children: 'children',
  label: 'menuName'
}
// 当前用户已选中的权限
const checkedKeys = ref([])

// 加载角色列表
const loadRoleList = async () => {
  try {
    loading.value = true
    const response = await getRoles(queryParams)
    roleList.value = response.records
    total.value = response.totalCount
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const data = await getMenuTree()
    menuTree.value = data
  } catch (error) {
    console.error('加载菜单树失败:', error)
    ElMessage.error('加载菜单树失败')
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadRoleList()
}

// 重置搜索
const resetSearch = () => {
  queryParams.keyword = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (size) => {
  queryParams.size = size
  loadRoleList()
}

// 当前页变化
const handleCurrentChange = (page) => {
  queryParams.page = page
  loadRoleList()
}

// 新增角色
const handleCreate = () => {
  dialogTitle.value = '新增角色'
  Object.assign(roleForm, {
    roleName: '',
    roleCode: '',
    description: '',
    status: true
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(roleForm, row)
  dialogVisible.value = true
}

// 权限分配
const handlePermission = async (row) => {
  currentRoleId.value = row.id
  try {
    // 加载菜单树
    await loadMenuTree()
    
    debugger
    // 获取角色已有的权限
    checkedKeys.value = await getRoleMenuIds(row.id)
    
    permissionDialogVisible.value = true
  } catch (error) {
    console.error('加载权限信息失败:', error)
    ElMessage.error('加载权限信息失败')
  }
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error('删除角色失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!roleFormRef.value) return
  
  try {
    await roleFormRef.value.validate()
    
    if (dialogTitle.value === '新增角色') {
      await createRole(roleForm)
      ElMessage.success('新增成功')
    } else {
      await updateRole(roleForm.id, roleForm)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    loadRoleList()
  } catch (error) {
    console.error('提交表单失败:', error)
  }
}

// 提交权限分配
const submitPermissions = async () => {
  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    await updateRoleMenus(currentRoleId.value, checkedKeys)
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('分配权限失败:', error)
    ElMessage.error('分配权限失败')
  }
}

// 格式化时间
const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleString('zh-CN')
}

// 初始化
onMounted(() => {
  loadRoleList()
})
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>