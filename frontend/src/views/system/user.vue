<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="用户名/姓名/邮箱"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter="handleFilter"
      />
      <el-select
        v-model="listQuery.departmentId"
        placeholder="选择部门"
        clearable
        style="width: 200px;"
        class="filter-item"
      >
        <el-option
          v-for="dept in departmentOptions"
          :key="dept.id"
          :label="dept.name"
          :value="dept.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.roleId"
        placeholder="选择角色"
        clearable
        style="width: 150px;"
        class="filter-item"
      >
        <el-option
          v-for="role in roleOptions"
          :key="role.id"
          :label="role.roleName"
          :value="role.id"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="Search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="success" icon="Plus" @click="handleCreate">
        新增用户
      </el-button>
    </div>

    <el-table
      :data="list"
      v-loading="listLoading"
      element-loading-text="Loading"
      border
      highlight-current-row
    >
      <el-table-column label="头像" prop="avatar" width="80" align="center">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar">
            {{ row.fullName ? row.fullName.charAt(0) : 'U' }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="用户名" prop="username" width="120" />
      <el-table-column label="姓名" prop="fullName" width="120" />
      <el-table-column label="邮箱" prop="email" width="200" />
      <el-table-column label="手机号" prop="phoneNumber" width="130" />
      <el-table-column label="部门" prop="departmentName" width="150" />
      <el-table-column label="职位" prop="position" width="120" />
      <el-table-column label="角色" prop="roles" width="200">
        <template #default="{ row }">
          <el-tag
            v-for="role in row.roles"
            :key="role.id"
            :type="getRoleType(role.roleCode)"
            size="small"
            style="margin-right: 5px; margin-bottom: 2px;"
          >
            {{ role.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="isActive" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'danger'">
            {{ row.isActive ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后登录" prop="lastLoginAt" width="180">
        <template #default="{ row }">
          <span>{{ formatDate(row.lastLoginAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(row)">
            重置密码
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-show="total > 0"
      :current-page="listQuery.page"
      :page-size="listQuery.size"
      :page-sizes="[10, 20, 30, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; text-align: center;"
    />
    

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogFormVisible"
      width="800px"
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="temp.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="temp.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="fullName">
              <el-input v-model="temp.fullName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input v-model="temp.phoneNumber" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-tree-select
                v-model="temp.departmentId"
                :data="departmentTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                placeholder="请选择部门"
                clearable
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="temp.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="角色" prop="roleIds">
              <el-select
                v-model="temp.roleIds"
                placeholder="请选择角色"
                multiple
                style="width: 100%"
                collapse-tags
                collapse-tags-tooltip
              >
                <el-option
                  v-for="role in roleOptions"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="isActive">
              <el-radio-group v-model="temp.isActive">
                <el-radio :label="true">启用</el-radio>
                <el-radio :label="false">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="temp.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="未知" :value="0" />
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="temp.birthday"
                type="date"
                placeholder="请选择生日"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker
                v-model="temp.hireDate"
                type="date"
                placeholder="请选择入职日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="籍贯" prop="hometown">
              <el-input v-model="temp.hometown" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="temp.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="毕业院校" prop="graduateSchool">
          <el-input v-model="temp.graduateSchool" placeholder="请输入毕业院校" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="temp.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系人电话" prop="emergencyContactPhone">
          <el-input v-model="temp.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="temp.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item v-if="dialogStatus === 'create'" label="密码" prop="password">
          <el-input
            v-model="temp.password"
            type="password"
            placeholder="请输入密码"
            show-password
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

    <!-- 重置密码对话框 -->
    <el-dialog
      title="重置密码"
      v-model="resetPasswordDialogVisible"
      width="400px"
    >
      <el-form
        ref="resetPasswordForm"
        :rules="resetPasswordRules"
        :model="resetPasswordForm"
        label-position="left"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="resetPasswordForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetPasswordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="resetPassword">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, createUser, updateUser, deleteUser, resetPassword } from '@/api/user'
import { getDepartmentTree } from '@/api/department'
import { getAllRoles } from '@/api/role'
import { getUserRoles, updateUserRoles } from '@/api/userRole'
import { formatDate } from '@/utils/date'

export default {
  name: 'UserManagement',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (this.dialogStatus === 'create' && !value) {
        callback(new Error('密码不能为空'))
      } else if (value && value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.resetPasswordForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 20,
        keyword: '',
        departmentId: null,
        roleId: null
      },
      temp: {
        id: undefined,
        username: '',
        email: '',
        password: '',
        fullName: '',
        phoneNumber: '',
        departmentId: null,
        position: '',
        roleIds: [],
        isActive: true,
        gender: 0,
        birthday: null,
        hireDate: null,
        hometown: '',
        address: '',
        graduateSchool: '',
        emergencyContact: '',
        emergencyContactPhone: '',
        remarks: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      departmentOptions: [],
      departmentTreeOptions: [],
      roleOptions: [],
      resetPasswordDialogVisible: false,
      resetPasswordForm: {
        userId: null,
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '用户名不能为空', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '姓名不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        phoneNumber: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      resetPasswordRules: {
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDepartmentOptions()
    this.getRoleOptions()
  },
  methods: {
    formatDate,
    getList() {
      this.listLoading = true
      getUserList(this.listQuery).then(response => {
        this.list = response.records
        this.total = response.totalCount
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    getDepartmentOptions() {
      getDepartmentTree().then(response => {
        this.departmentOptions = this.flattenDepartments(response)
        this.departmentTreeOptions = response
      })
    },
    getRoleOptions() {
      getAllRoles().then(response => {
        this.roleOptions = response
      })
    },
    flattenDepartments(departments, result = []) {
      departments.forEach(dept => {
        result.push({
          id: dept.id,
          name: dept.name
        })
        if (dept.children && dept.children.length > 0) {
          this.flattenDepartments(dept.children, result)
        }
      })
      return result
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        username: '',
        email: '',
        password: '',
        fullName: '',
        phoneNumber: '',
        departmentId: null,
        position: '',
        roleIds: [],
        isActive: true,
        gender: 0,
        birthday: null,
        hireDate: null,
        hometown: '',
        address: '',
        graduateSchool: '',
        emergencyContact: '',
        emergencyContactPhone: '',
        remarks: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增用户'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogTitle = '编辑用户'
      this.dialogFormVisible = true
      
      // 获取用户的角色信息
      if (row.id) {
        getUserRoles(row.id).then(response => {
          this.temp.roleIds = response.map(role => role.id)
        })
      }
      
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const { roleIds, ...userData } = this.temp
          createUser(userData).then((response) => {
            // 创建用户成功后，更新用户角色关联
            if (roleIds && roleIds.length > 0) {
              return updateUserRoles(response.id, roleIds)
            }
          }).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const { roleIds, ...userData } = this.temp
          updateUser(userData.id, userData).then(() => {
            // 更新用户信息成功后，更新用户角色关联
            if (roleIds) {
              return updateUserRoles(userData.id, roleIds)
            }
          }).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
        })
      })
    },
    handleResetPassword(row) {
      this.resetPasswordForm = {
        userId: row.id,
        password: '',
        confirmPassword: ''
      }
      this.resetPasswordDialogVisible = true
      this.$nextTick(() => {
        this.$refs['resetPasswordForm'].clearValidate()
      })
    },
    resetPassword() {
      this.$refs['resetPasswordForm'].validate((valid) => {
        if (valid) {
          resetPassword(this.resetPasswordForm.userId, this.resetPasswordForm.password).then(() => {
            this.resetPasswordDialogVisible = false
            this.$notify({
              title: '成功',
              message: '密码重置成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    getRoleType(roleCode) {
      const roleMap = {
        'ROLE_ADMIN': 'danger',
        'ROLE_MANAGER': 'warning',
        'ROLE_USER': 'success'
      }
      return roleMap[roleCode] || 'info'
    },
    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.page = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
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