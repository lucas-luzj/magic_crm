<template>
  <div class="process-definitions">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>流程定义管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="goToDesigner">
              <el-icon><Plus /></el-icon>
              新建流程
            </el-button>
            <el-button @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-container">
        <el-form :model="searchForm" inline>
          <el-form-item label="流程名称">
            <el-input
              v-model="searchForm.processName"
              placeholder="请输入流程名称"
              clearable
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="流程分类">
            <el-select v-model="searchForm.category" placeholder="请选择分类" clearable style="width: 150px;">
              <el-option label="审批流程" value="approval" />
              <el-option label="业务流程" value="business" />
              <el-option label="系统流程" value="system" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
              <el-option label="激活" value="ACTIVE" />
              <el-option label="挂起" value="SUSPENDED" />
            </el-select>
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

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="processName" label="流程名称" min-width="150" />
        <el-table-column prop="processKey" label="流程Key" min-width="120" />
        <el-table-column prop="version" label="版本" width="80" align="center" />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">
              {{ getCategoryLabel(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUSPENDED' ? 'danger' : 'success'">
              {{ row.status === 'SUSPENDED' ? '挂起' : '激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDate(row.deploymentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="340" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDiagram(row)">
              查看图
            </el-button>
            <el-button size="small" type="primary" @click="editProcess(row)">
              编辑
            </el-button>
            <el-button size="small" @click="startProcess(row)">
              启动
            </el-button>
            <el-button
              size="small"
              :type="row.status === 'SUSPENDED' ? 'success' : 'warning'"
              @click="toggleSuspension(row)"
            >
              {{ row.status === 'SUSPENDED' ? '激活' : '挂起' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteProcess(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 流程图查看对话框 -->
    <el-dialog v-model="diagramVisible" :title="`流程图 - ${currentProcess?.processName}`" width="80%">
      <div class="diagram-container">
        <img v-if="diagramUrl" :src="diagramUrl" alt="流程图" style="max-width: 100%; height: auto;" />
        <div v-else class="no-diagram">暂无流程图</div>
      </div>
    </el-dialog>

    <!-- 启动流程对话框 -->
    <el-dialog v-model="startProcessVisible" title="启动流程实例" width="500px">
      <el-form :model="startForm" label-width="100px">
        <el-form-item label="业务键">
          <el-input v-model="startForm.businessKey" placeholder="请输入业务键（可选）" />
        </el-form-item>
        <el-form-item label="流程变量">
          <el-input
            v-model="startForm.variables"
            type="textarea"
            :rows="4"
            placeholder="请输入JSON格式的流程变量（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startProcessVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStartProcess" :loading="starting">启动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Refresh, Search, RefreshLeft, View, VideoPlay, Switch, Delete, Edit
} from '@element-plus/icons-vue'
import { processDefinitionApi, processInstanceApi } from '@/api/workflow'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const starting = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const diagramVisible = ref(false)
const startProcessVisible = ref(false)
const diagramUrl = ref('')
const currentProcess = ref(null)

const searchForm = reactive({
  processName: '',
  category: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const startForm = reactive({
  businessKey: '',
  variables: ''
})

// 获取流程定义列表
const getProcessDefinitions = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size,
      ...searchForm
    }
    
    const pageData = await processDefinitionApi.getProcessDefinitions(params)
    tableData.value = pageData.records || []
    pagination.total = pageData.totalCount || 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getProcessDefinitions()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    processName: '',
    category: '',
    status: null
  })
  pagination.page = 1
  getProcessDefinitions()
}

// 刷新数据
const refreshData = () => {
  getProcessDefinitions()
}

// 跳转到设计器
const goToDesigner = () => {
  router.push('/workflow/process-designer')
}

// 编辑流程
const editProcess = async (row) => {
  try {
    // 获取流程定义的BPMN XML
    const bpmnXml = await processDefinitionApi.getProcessDefinitionBpmnXml(row.processDefinitionId)
    
    // 跳转到设计器，传递编辑参数
    router.push({
      path: '/workflow/process-designer',
      query: {
        mode: 'edit',
        processDefinitionId: row.processDefinitionId,
        processName: row.processName,
        processKey: row.processKey,
        category: row.category
      }
    })
    
    // 将BPMN XML存储到sessionStorage，供设计器使用
    sessionStorage.setItem('editProcessBpmnXml', bpmnXml)
  } catch (error) {
    console.error('获取流程定义XML失败:', error)
    ElMessage.error('获取流程定义失败，无法编辑')
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
  getProcessDefinitions()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  getProcessDefinitions()
}

// 获取分类类型
const getCategoryType = (category) => {
  const typeMap = {
    approval: 'primary',
    business: 'success',
    system: 'warning'
  }
  return typeMap[category] || 'info'
}

// 获取分类标签
const getCategoryLabel = (category) => {
  const labelMap = {
    approval: '审批流程',
    business: '业务流程',
    system: '系统流程'
  }
  return labelMap[category] || category
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 查看流程图
const viewDiagram = async (row) => {
  try {
    currentProcess.value = row
    const data = await processDefinitionApi.getProcessDefinitionDiagram(row.processDefinitionId)
    diagramUrl.value = URL.createObjectURL(data)
    diagramVisible.value = true
  } catch (error) {
    console.error('获取流程图失败:', error)
    ElMessage.error('获取流程图失败')
  }
}

// 启动流程
const startProcess = (row) => {
  currentProcess.value = row
  startForm.businessKey = ''
  startForm.variables = ''
  startProcessVisible.value = true
}

// 确认启动流程
const confirmStartProcess = async () => {
  try {
    starting.value = true
    let variables = {}
    
    if (startForm.variables) {
      try {
        variables = JSON.parse(startForm.variables)
      } catch (error) {
        ElMessage.error('流程变量格式错误，请输入有效的JSON')
        return
      }
    }

    // 为特定流程添加默认变量
    if (currentProcess.value.processKey === 'leaveProcess') {
      // 员工请假流程需要的默认变量
      variables = {
        employee: '当前用户', // 默认员工名称
        days: 1, // 默认请假天数
        reason: '个人事务', // 默认请假原因
        ...variables // 用户自定义变量会覆盖默认值
      }
    }

    await processInstanceApi.startProcessInstance({
      processDefinitionKey: currentProcess.value.processKey,
      businessKey: startForm.businessKey || undefined,
      variables
    })

    ElMessage.success('流程启动成功')
    startProcessVisible.value = false
  } catch (error) {
    console.error('启动流程失败:', error)
    ElMessage.error('启动流程失败: ' + (error.message || '未知错误'))
  } finally {
    starting.value = false
  }
}

// 切换挂起状态
const toggleSuspension = async (row) => {
  try {
    const action = row.status === 'SUSPENDED' ? '激活' : '挂起'
    await ElMessageBox.confirm(`确定要${action}此流程定义吗？`, `确认${action}`, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    if (row.status === 'SUSPENDED') {
      await processDefinitionApi.activateProcessDefinition(row.processDefinitionId)
    } else {
      await processDefinitionApi.suspendProcessDefinition(row.processDefinitionId)
    }

    ElMessage.success(`${action}成功`)
    getProcessDefinitions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除流程定义
const deleteProcess = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此流程定义吗？此操作不可恢复。', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await processDefinitionApi.deleteProcessDefinition(row.processDefinitionId, true)
    ElMessage.success('删除成功')
    getProcessDefinitions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 组件挂载
onMounted(() => {
  getProcessDefinitions()
})
</script>

<style scoped>
.process-definitions {
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

.diagram-container {
  text-align: center;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-diagram {
  color: #909399;
  font-size: 16px;
}
</style>