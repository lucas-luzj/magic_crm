<template>
  <div class="tasks">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <div class="header-actions">
            <el-button @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待办任务" name="pending">
          <!-- 搜索区域 -->
          <div class="search-container">
            <el-form :model="searchForm" inline>
              <el-form-item label="任务名称">
                <el-input
                  v-model="searchForm.taskName"
                  placeholder="请输入任务名称"
                  clearable
                  style="width: 200px;"
                />
              </el-form-item>
              <el-form-item label="流程名称">
                <el-input
                  v-model="searchForm.processName"
                  placeholder="请输入流程名称"
                  clearable
                  style="width: 200px;"
                />
              </el-form-item>
              <el-form-item label="处理人">
                <el-input
                  v-model="searchForm.assignee"
                  placeholder="请输入处理人"
                  clearable
                  style="width: 150px;"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="resetSearch">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 待办任务表格 -->
          <el-table
            v-loading="loading"
            :data="pendingTasks"
            stripe
            style="width: 100%"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="processName" label="流程名称" min-width="150" />
            <el-table-column prop="assignee" label="处理人" width="100" />
            <el-table-column prop="businessKey" label="业务键" width="120" />
            <el-table-column prop="priority" label="优先级" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)">
                  {{ getPriorityLabel(row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="到期时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="400" align="center" fixed="right">
              <template #default="{ row }">
                <el-button-group>
                  <el-button size="small" type="primary" @click="completeTask(row)">
                    <el-icon><Check /></el-icon>
                    完成
                  </el-button>
                  <el-button size="small" type="success" @click="showApprovalDialog(row)">
                    <el-icon><CircleCheck /></el-icon>
                    审批
                  </el-button>
                </el-button-group>
                
                <el-dropdown @command="(command) => handleDropdownCommand(command, row)" style="margin-left: 8px;">
                  <el-button size="small">
                    更多操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="assign">
                        <el-icon><User /></el-icon>分配
                      </el-dropdown-item>
                      <el-dropdown-item command="delegate">
                        <el-icon><Share /></el-icon>委派
                      </el-dropdown-item>
                      <el-dropdown-item command="transfer">
                        <el-icon><Switch /></el-icon>转办
                      </el-dropdown-item>
                      <el-dropdown-item command="rollback" divided>
                        <el-icon><Back /></el-icon>回退
                      </el-dropdown-item>
                      <el-dropdown-item command="history">
                        <el-icon><Clock /></el-icon>历史记录
                      </el-dropdown-item>
                      <el-dropdown-item command="comments">
                        <el-icon><ChatDotRound /></el-icon>查看评论
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="已完成任务" name="completed">
          <!-- 已完成任务表格 -->
          <el-table
            v-loading="loading"
            :data="completedTasks"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="processName" label="流程名称" min-width="150" />
            <el-table-column prop="assignee" label="处理人" width="100" />
            <el-table-column prop="businessKey" label="业务键" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="endTime" label="完成时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="durationInMillis" label="耗时" width="100" align="center">
              <template #default="{ row }">
                {{ formatDuration(row.durationInMillis) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 完成任务对话框 -->
    <el-dialog v-model="completeDialogVisible" title="完成任务" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input :value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input
            v-model="completeForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入处理意见"
          />
        </el-form-item>
        <el-form-item label="流程变量">
          <el-input
            v-model="completeForm.variables"
            type="textarea"
            :rows="4"
            placeholder="请输入JSON格式的流程变量（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCompleteTask" :loading="completing">完成</el-button>
      </template>
    </el-dialog>

    <!-- 分配任务对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配任务" width="400px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input :value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="分配给" required>
          <el-input v-model="assignForm.assignee" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignTask" :loading="assigning">分配</el-button>
      </template>
    </el-dialog>

    <!-- 委派任务对话框 -->
    <el-dialog v-model="delegateDialogVisible" title="委派任务" width="400px">
      <el-form :model="delegateForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input :value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="委派给" required>
          <el-input v-model="delegateForm.delegatee" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelegateTask" :loading="delegating">委派</el-button>
      </template>
    </el-dialog>

    <!-- 转办任务对话框 -->
    <el-dialog v-model="transferDialogVisible" title="转办任务" width="400px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input :value="currentTask?.taskName" disabled />
        </el-form-item>
        <el-form-item label="转办给" required>
          <el-input v-model="transferForm.newAssignee" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransferTask" :loading="transferring">转办</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh, Search, RefreshLeft, Check, User, Share, Switch
} from '@element-plus/icons-vue'
import { taskApi } from '@/api/workflow'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const activeTab = ref('pending')
const pendingTasks = ref([])
const completedTasks = ref([])
const selectedRows = ref([])
const currentTask = ref(null)

// 对话框状态
const completeDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const delegateDialogVisible = ref(false)
const transferDialogVisible = ref(false)

// 操作状态
const completing = ref(false)
const assigning = ref(false)
const delegating = ref(false)
const transferring = ref(false)

const searchForm = reactive({
  taskName: '',
  processName: '',
  assignee: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const completeForm = reactive({
  comment: '',
  variables: ''
})

const assignForm = reactive({
  assignee: ''
})

const delegateForm = reactive({
  delegatee: ''
})

const transferForm = reactive({
  newAssignee: ''
})

// 获取待办任务
const getPendingTasks = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...searchForm
    }
    
    const data = await taskApi.getPendingTasks(params)
    pendingTasks.value = data.content || []
    pagination.total = data.totalElements || 0
  } catch (error) {
    console.error('获取待办任务失败:', error)
    ElMessage.error('获取待办任务失败')
  } finally {
    loading.value = false
  }
}

// 获取已完成任务
const getCompletedTasks = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...searchForm
    }
    
    const data = await taskApi.getCompletedTasks(params)
    completedTasks.value = data.content || []
    pagination.total = data.totalElements || 0
  } catch (error) {
    console.error('获取已完成任务失败:', error)
    ElMessage.error('获取已完成任务失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  pagination.page = 1
  if (tab === 'pending') {
    getPendingTasks()
  } else {
    getCompletedTasks()
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  if (activeTab.value === 'pending') {
    getPendingTasks()
  } else {
    getCompletedTasks()
  }
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    taskName: '',
    processName: '',
    assignee: ''
  })
  pagination.page = 1
  if (activeTab.value === 'pending') {
    getPendingTasks()
  } else {
    getCompletedTasks()
  }
}

// 刷新数据
const refreshData = () => {
  if (activeTab.value === 'pending') {
    getPendingTasks()
  } else {
    getCompletedTasks()
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  refreshData()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  refreshData()
}

// 获取优先级类型
const getPriorityType = (priority) => {
  if (priority >= 80) return 'danger'
  if (priority >= 50) return 'warning'
  return 'success'
}

// 获取优先级标签
const getPriorityLabel = (priority) => {
  if (priority >= 80) return '高'
  if (priority >= 50) return '中'
  return '低'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 格式化持续时间
const formatDuration = (milliseconds) => {
  if (!milliseconds) return '-'
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days}天${hours % 24}小时`
  if (hours > 0) return `${hours}小时${minutes % 60}分钟`
  if (minutes > 0) return `${minutes}分钟${seconds % 60}秒`
  return `${seconds}秒`
}

// 完成任务
const completeTask = (row) => {
  currentTask.value = row
  completeForm.comment = ''
  completeForm.variables = ''
  completeDialogVisible.value = true
}

// 确认完成任务
const confirmCompleteTask = async () => {
  try {
    completing.value = true
    let variables = {}
    
    if (completeForm.variables) {
      try {
        variables = JSON.parse(completeForm.variables)
      } catch (error) {
        ElMessage.error('流程变量格式错误，请输入有效的JSON')
        return
      }
    }

    // 添加处理意见到变量中
    if (completeForm.comment) {
      variables.comment = completeForm.comment
    }

    await taskApi.completeTask(
      currentTask.value.taskId,
      variables,
      userStore.userInfo.id
    )

    ElMessage.success('任务完成成功')
    completeDialogVisible.value = false
    getPendingTasks()
  } catch (error) {
    console.error('完成任务失败:', error)
    ElMessage.error('完成任务失败: ' + (error.message || '未知错误'))
  } finally {
    completing.value = false
  }
}

// 分配任务
const assignTask = (row) => {
  currentTask.value = row
  assignForm.assignee = ''
  assignDialogVisible.value = true
}

// 确认分配任务
const confirmAssignTask = async () => {
  if (!assignForm.assignee) {
    ElMessage.error('请输入分配人')
    return
  }

  try {
    assigning.value = true
    await taskApi.assignTask(
      currentTask.value.taskId,
      assignForm.assignee,
      userStore.userInfo.id
    )

    ElMessage.success('任务分配成功')
    assignDialogVisible.value = false
    getPendingTasks()
  } catch (error) {
    console.error('分配任务失败:', error)
    ElMessage.error('分配任务失败: ' + (error.message || '未知错误'))
  } finally {
    assigning.value = false
  }
}

// 委派任务
const delegateTask = (row) => {
  currentTask.value = row
  delegateForm.delegatee = ''
  delegateDialogVisible.value = true
}

// 确认委派任务
const confirmDelegateTask = async () => {
  if (!delegateForm.delegatee) {
    ElMessage.error('请输入委派人')
    return
  }

  try {
    delegating.value = true
    await taskApi.delegateTask(
      currentTask.value.taskId,
      delegateForm.delegatee,
      userStore.userInfo.id
    )

    ElMessage.success('任务委派成功')
    delegateDialogVisible.value = false
    getPendingTasks()
  } catch (error) {
    console.error('委派任务失败:', error)
    ElMessage.error('委派任务失败: ' + (error.message || '未知错误'))
  } finally {
    delegating.value = false
  }
}

// 转办任务
const transferTask = (row) => {
  currentTask.value = row
  transferForm.newAssignee = ''
  transferDialogVisible.value = true
}

// 确认转办任务
const confirmTransferTask = async () => {
  if (!transferForm.newAssignee) {
    ElMessage.error('请输入转办人')
    return
  }

  try {
    transferring.value = true
    await taskApi.transferTask(
      currentTask.value.taskId,
      transferForm.newAssignee,
      userStore.userInfo.id
    )

    ElMessage.success('任务转办成功')
    transferDialogVisible.value = false
    getPendingTasks()
  } catch (error) {
    console.error('转办任务失败:', error)
    ElMessage.error('转办任务失败: ' + (error.message || '未知错误'))
  } finally {
    transferring.value = false
  }
}

// 组件挂载
onMounted(() => {
  getPendingTasks()
})
</script>

<style scoped>
.tasks {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-container {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>