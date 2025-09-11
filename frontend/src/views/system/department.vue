<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="部门名称/编码"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter="handleFilter"
      />
      <el-button class="filter-item" type="primary" icon="Search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="success" icon="Plus" @click="handleCreate">
        新增部门
      </el-button>
    </div>

    <el-table
      :data="list"
      v-loading="listLoading"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="false"
    >
      <el-table-column label="部门名称" prop="name" min-width="200">
        <template #default="{ row }">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="部门编码" prop="code" width="120" />
      <el-table-column label="负责人" prop="managerName" width="120" />
      <el-table-column label="用户数量" prop="userCount" width="100" />
      <el-table-column label="排序" prop="sortOrder" width="80" />
      <el-table-column label="状态" prop="isActive" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'danger'">
            {{ row.isActive ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createdAt" width="180">
        <template #default="{ row }">
          <span>{{ formatDate(row.createdAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button type="success" size="small" @click="handleCreateChild(row)">
            添加子部门
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 部门表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogFormVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="100px"
        style="width: 100%;"
      >
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="temp.code" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="父部门" prop="parentId">
          <el-tree-select
            v-model="temp.parentId"
            :data="departmentTreeOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择父部门"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="managerId">
          <el-select
            v-model="temp.managerId"
            placeholder="请选择负责人"
            clearable
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="user.fullName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="temp.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="temp.isActive">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="temp.description"
            type="textarea"
            :rows="3"
            placeholder="请输入部门描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getDepartmentTree, createDepartment, updateDepartment, deleteDepartment } from '@/api/department'
import { getUserList } from '@/api/user'
import { formatDate } from '@/utils/date'

export default {
  name: 'DepartmentManagement',
  data() {
    return {
      list: [],
      listLoading: true,
      listQuery: {
        keyword: ''
      },
      temp: {
        id: undefined,
        name: '',
        code: '',
        parentId: null,
        managerId: null,
        description: '',
        sortOrder: 0,
        isActive: true
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      departmentTreeOptions: [],
      userOptions: [],
      rules: {
        name: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' },
          { min: 2, max: 100, message: '部门名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        code: [
          { max: 50, message: '部门编码长度不能超过 50 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDepartmentTreeOptions()
    this.getUserOptions()
  },
  methods: {
    formatDate,
    getList() {
      this.listLoading = true
      getDepartmentTree().then(response => {
        this.list = response
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    getDepartmentTreeOptions() {
      getDepartmentTree().then(response => {
        this.departmentTreeOptions = response
      })
    },
    getUserOptions() {
      getUserList({ page: 1, size: 1000 }).then(response => {
        this.userOptions = response.records || []
      })
    },
    handleFilter() {
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        code: '',
        parentId: null,
        managerId: null,
        description: '',
        sortOrder: 0,
        isActive: true
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增部门'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleCreateChild(row) {
      this.resetTemp()
      this.temp.parentId = row.id
      this.dialogStatus = 'create'
      this.dialogTitle = `新增子部门 - ${row.name}`
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogTitle = '编辑部门'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createDepartment(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
            this.getDepartmentTreeOptions()
          })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateDepartment(tempData.id, tempData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
            this.getDepartmentTreeOptions()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('此操作将永久删除该部门, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDepartment(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
          this.getDepartmentTreeOptions()
        })
      })
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}

.filter-item {
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 10px;
  margin-right: 10px;
}
</style>
