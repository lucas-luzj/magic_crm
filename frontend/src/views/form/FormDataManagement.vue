<template>
  <el-card>
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>表单数据管理</h2>
        <p class="page-description">管理所有提交的表单数据，支持查看、审批和统计</p>
      </div>
      <div class="header-right">
        <el-button @click="handleRefresh" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="表单模板">
          <el-select
            v-model="searchForm.formTemplateId"
            placeholder="请选择表单模板"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="template in formTemplates"
              :key="template.id"
              :label="template.formName"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="流程实例">
          <el-input
            v-model="searchForm.processInstanceId"
            placeholder="请输入流程实例ID"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="任务ID">
          <el-input
            v-model="searchForm.taskId"
            placeholder="请输入任务ID"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="已审批" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="提交人">
          <el-input
            v-model="searchForm.submitUser"
            placeholder="请输入提交人"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表单数据列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="formTemplateName" label="表单模板" min-width="200">
          <template #default="{ row }">
            <div class="template-info">
              <div class="template-name">{{ row.formTemplateName }}</div>
              <div class="template-key">{{ row.formTemplateKey }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="processInstanceId" label="流程实例" width="150" show-overflow-tooltip />
        
        <el-table-column prop="taskId" label="任务ID" width="120" show-overflow-tooltip />
        
        <el-table-column prop="businessKey" label="业务键" width="120" show-overflow-tooltip />
        
        <el-table-column prop="submitUser" label="提交人" width="120" />
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="submitTime" label="提交时间" width="180" sortable="custom">
          <template #default="{ row }">
            {{ formatDateTime(row.submitTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'SUBMITTED'" 
              size="small" 
              type="success" 
              @click="handleApprove(row)"
            >
              <el-icon><Check /></el-icon>
              审批
            </el-button>
            <el-button 
              v-if="row.status === 'SUBMITTED'" 
              size="small" 
              type="danger" 
              @click="handleReject(row)"
            >
              <el-icon><Close /></el-icon>
              拒绝
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
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
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看表单数据对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="表单数据详情"
      width="60%"
      :close-on-click-modal="false"
    >
      <div v-if="selectedFormData" class="form-data-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="表单模板">{{ selectedFormData.formTemplateName }}</el-descriptions-item>
          <el-descriptions-item label="流程实例">{{ selectedFormData.processInstanceId }}</el-descriptions-item>
          <el-descriptions-item label="任务ID">{{ selectedFormData.taskId }}</el-descriptions-item>
          <el-descriptions-item label="业务键">{{ selectedFormData.businessKey }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ selectedFormData.submitUser }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(selectedFormData.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedFormData.status)">
              {{ getStatusText(selectedFormData.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="form-data-content">
          <h4>表单数据</h4>
          <el-card>
            <pre class="json-content">{{ formatJson(selectedFormData.formData) }}</pre>
          </el-card>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      title="审批表单数据"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="审批意见">
          <el-input 
            v-model="approveForm.comment" 
            type="textarea" 
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmApprove" :loading="approving">
          确认审批
        </el-button>
      </template>
    </el-dialog>

    <!-- 拒绝对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝表单数据"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因" required>
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea" 
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Check, Close, Delete } from '@element-plus/icons-vue'
import { formDataInstanceApi } from '@/api/formDataInstance'
import { formTemplateApi } from '@/api/formTemplate'

export default {
  name: 'FormDataManagement',
  components: {
    Search, Refresh, View, Check, Close, Delete
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const tableData = ref([])
    const formTemplates = ref([])
    const viewDialogVisible = ref(false)
    const approveDialogVisible = ref(false)
    const rejectDialogVisible = ref(false)
    const approving = ref(false)
    const rejecting = ref(false)
    const selectedFormData = ref(null)

    // 搜索表单
    const searchForm = reactive({
      formTemplateId: '',
      processInstanceId: '',
      taskId: '',
      status: '',
      submitUser: ''
    })

    // 分页数据
    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })

    // 审批表单
    const approveForm = reactive({
      comment: ''
    })

    // 拒绝表单
    const rejectForm = reactive({
      reason: ''
    })

    // 方法
    const loadFormTemplates = async () => {
      try {
        const response = await formTemplateApi.getFormTemplates({ page: 0, size: 1000 })
        formTemplates.value = response.content || []
      } catch (error) {
        console.error('加载表单模板失败:', error)
      }
    }

    const loadFormData = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.page - 1,
          size: pagination.size,
          formTemplateId: searchForm.formTemplateId || undefined,
          processInstanceId: searchForm.processInstanceId || undefined,
          taskId: searchForm.taskId || undefined,
          status: searchForm.status || undefined,
          submitUser: searchForm.submitUser || undefined
        }

        const response = await formDataInstanceApi.getFormDataInstances(params)
        tableData.value = response.content || []
        pagination.total = response.totalElements || 0
      } catch (error) {
        console.error('加载表单数据失败:', error)
        // request工具已经处理了错误提示
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      pagination.page = 1
      loadFormData()
    }

    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.page = 1
      loadFormData()
    }

    const handleRefresh = () => {
      loadFormData()
    }

    const handleSortChange = ({ prop, order }) => {
      // 处理排序
      console.log('排序:', prop, order)
      loadFormData()
    }

    const handleSizeChange = (size) => {
      pagination.size = size
      pagination.page = 1
      loadFormData()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      loadFormData()
    }

    const handleView = (row) => {
      selectedFormData.value = row
      viewDialogVisible.value = true
    }

    const handleApprove = (row) => {
      selectedFormData.value = row
      approveForm.comment = ''
      approveDialogVisible.value = true
    }

    const handleReject = (row) => {
      selectedFormData.value = row
      rejectForm.reason = ''
      rejectDialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这条表单数据吗？', '确认删除', {
          type: 'warning'
        })
        
        await formDataInstanceApi.deleteFormDataInstance(row.id)
        ElMessage.success('删除成功')
        loadFormData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          // request工具已经处理了错误提示
        }
      }
    }

    const confirmApprove = async () => {
      if (!approveForm.comment.trim()) {
        ElMessage.warning('请输入审批意见')
        return
      }

      approving.value = true
      try {
        await formDataInstanceApi.approveFormData(selectedFormData.value.id, 'current_user')
        ElMessage.success('审批成功')
        approveDialogVisible.value = false
        loadFormData()
      } catch (error) {
        console.error('审批失败:', error)
        // request工具已经处理了错误提示
      } finally {
        approving.value = false
      }
    }

    const confirmReject = async () => {
      if (!rejectForm.reason.trim()) {
        ElMessage.warning('请输入拒绝原因')
        return
      }

      rejecting.value = true
      try {
        await formDataInstanceApi.rejectFormData(selectedFormData.value.id, 'current_user')
        ElMessage.success('拒绝成功')
        rejectDialogVisible.value = false
        loadFormData()
      } catch (error) {
        console.error('拒绝失败:', error)
        // request工具已经处理了错误提示
      } finally {
        rejecting.value = false
      }
    }

    const getStatusType = (status) => {
      const statusMap = {
        DRAFT: 'info',
        SUBMITTED: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return statusMap[status] || 'info'
    }

    const getStatusText = (status) => {
      const statusMap = {
        DRAFT: '草稿',
        SUBMITTED: '已提交',
        APPROVED: '已审批',
        REJECTED: '已拒绝'
      }
      return statusMap[status] || status
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }

    const formatJson = (json) => {
      if (!json) return '{}'
      return JSON.stringify(json, null, 2)
    }

    // 生命周期
    onMounted(() => {
      loadFormTemplates()
      loadFormData()
    })

    return {
      loading,
      tableData,
      formTemplates,
      viewDialogVisible,
      approveDialogVisible,
      rejectDialogVisible,
      approving,
      rejecting,
      selectedFormData,
      searchForm,
      pagination,
      approveForm,
      rejectForm,
      handleSearch,
      handleReset,
      handleRefresh,
      handleSortChange,
      handleSizeChange,
      handleCurrentChange,
      handleView,
      handleApprove,
      handleReject,
      handleDelete,
      confirmApprove,
      confirmReject,
      getStatusType,
      getStatusText,
      formatDateTime,
      formatJson
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.template-info {
  display: flex;
  flex-direction: column;
}

.template-name {
  font-weight: 500;
  color: #303133;
}

.template-key {
  font-size: 12px;
  color: #909399;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.form-data-detail {
  max-height: 600px;
  overflow-y: auto;
}

.form-data-content {
  margin-top: 20px;
}

.form-data-content h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.json-content {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 300px;
  overflow-y: auto;
}
</style>
