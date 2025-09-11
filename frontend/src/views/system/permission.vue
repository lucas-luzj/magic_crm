<template>
  <div class="permission-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><plus /></el-icon>新增权限
          </el-button>
        </div>
      </template>

      <!-- 权限树 -->
      <div class="tree-container">
        <el-tree
          ref="permissionTreeRef"
          :data="menuTree"
          node-key="id"
          default-expand-all
          :props="treeProps"
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <span class="node-label">{{ node.label }}</span>
              <span class="node-info">
                <el-tag size="small" v-if="data.permissionCode">{{ data.permissionCode }}</el-tag>
                <el-tag size="small" :type="data.status === 1 ? 'success' : 'danger'">
                  {{ data.status === 1 ? '启用' : '禁用' }}
                </el-tag>
                <span class="node-actions">
                  <el-button size="mini" @click="handleEdit(data)">编辑</el-button>
                  <el-button size="mini" type="danger" @click="handleDelete(data)">删除</el-button>
                </span>
              </span>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>

    <!-- 权限编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="80px"
      >
        <el-form-item label="权限名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="menuForm.permissionCode" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="权限类型" prop="menuType">
          <el-select v-model="menuForm.menuType" placeholder="请选择权限类型">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTreeOptions"
            :props="treeSelectProps"
            placeholder="请选择父级权限"
            check-strictly
            clearable
            :disabled="menuForm.id === menuForm.parentId"
          />
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="menuForm.menuType === 'MENU'">
          <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="menuForm.menuType === 'MENU'">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="menuForm.menuType === 'MENU'">
          <el-input v-model="menuForm.icon" placeholder="请输入图标类名" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="menuForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
            <el-radio :value="true">启用</el-radio>
            <el-radio :value="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="menuForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入权限描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMenuTree, createMenu, updateMenu, deleteMenu, getMenuById } from '@/api/menu'

// 权限树数据
const menuTree = ref([])
const permissionTreeRef = ref()

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentMenuId = ref(null)

// 表单相关
const menuFormRef = ref()
const menuForm = reactive({
  menuName: '',
  permissionCode: '',
  menuType: 'MENU',
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  status: true,
  description: ''
})

// 表单验证规则
const menuRules = reactive({
  menuName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
})

// 树选择器配置
const treeSelectProps = {
  value: 'id',
  label: 'menuName',
  children: 'children'
}

// 树配置
const treeProps = {
  children: 'children',
  label: 'menuName'
}

// 树选择器选项（过滤掉按钮类型）
const menuTreeOptions = computed(() => {
  const filterButtons = (nodes) => {
    return nodes.filter(node => {
      if (node.children) {
        node.children = filterButtons(node.children)
      }
      return node.menuType === 'MENU' // 只保留菜单类型
    })
  }
  return filterButtons(JSON.parse(JSON.stringify(menuTree.value)))
})

// 加载权限树
const loadMenuTree = async () => {
  try {
    const data = await getMenuTree()
    menuTree.value = data
  } catch (error) {
    console.error('加载权限树失败:', error)
    ElMessage.error('加载权限树失败')
  }
}

// 新增权限
const handleCreate = () => {
  dialogTitle.value = '新增权限'
  Object.assign(menuForm, {
    menuName: '',
    permissionCode: '',
    menuType: 'MENU',
    parentId: 0,
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    status: true,
    description: ''
  })
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = async (data) => {
  try {
    const response = await getMenuById(data.id)
    Object.assign(menuForm, response.data)
    dialogTitle.value = '编辑权限'
    currentMenuId.value = data.id
    dialogVisible.value = true
  } catch (error) {
    console.error('加载权限详情失败:', error)
    ElMessage.error('加载权限详情失败')
  }
}

// 删除权限
const handleDelete = async (data) => {
  try {
    await ElMessageBox.confirm(`确定要删除权限"${data.menuName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteMenu(data.id)
    ElMessage.success('删除成功')
    loadMenuTree()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除权限失败:', error)
      ElMessage.error('删除权限失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!menuFormRef.value) return
  
  try {
    await menuFormRef.value.validate()
    
    if (dialogTitle.value === '新增权限') {
      await createMenu(menuForm)
      ElMessage.success('新增成功')
    } else {
      await updateMenu(currentMenuId.value, menuForm)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    loadMenuTree()
  } catch (error) {
    console.error('提交表单失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-container {
  max-height: 600px;
  overflow-y: auto;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-label {
  font-weight: 500;
}

.node-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-actions {
  margin-left: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-tree-node__content) {
  height: 36px;
}
</style>