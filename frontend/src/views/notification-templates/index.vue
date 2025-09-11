<template>
  <div class="notification-templates">
    <div class="page-header">
      <h2>通知模板管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建模板
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="模板名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入模板名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="type in notificationTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="通知渠道">
          <el-select
            v-model="searchForm.channel"
            placeholder="请选择渠道"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="channel in notificationChannels"
              :key="channel"
              :label="channel"
              :value="channel"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.isEnabled"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
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
    </div>

    <!-- 模板列表 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="templates"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="模板名称" min-width="150" />
        <el-table-column prop="code" label="模板代码" min-width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="channel" label="渠道" width="100">
          <template #default="{ row }">
            <el-tag :type="getChannelTagType(row.channel)">{{ row.channel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled ? 'success' : 'danger'">
              {{ row.isEnabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isSystem" label="系统模板" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isSystem" type="info">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="340" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="success" size="small" @click="handleCopy(row)">
              复制
            </el-button>
            <el-button
              v-if="!row.isSystem"
              type="warning"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.isEnabled ? '禁用' : '启用' }}
            </el-button>
            <el-button
              v-if="!row.isSystem"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
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
    </div>

    <!-- 批量操作 -->
    <div v-if="selectedTemplates.length > 0" class="batch-actions">
      <el-button type="success" @click="handleBatchEnable">
        批量启用
      </el-button>
      <el-button type="warning" @click="handleBatchDisable">
        批量禁用
      </el-button>
    </div>

    <!-- 模板详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="templateForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模板名称" required>
              <el-input v-model="templateForm.name" :disabled="isViewMode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板代码" required>
              <el-input v-model="templateForm.code" :disabled="isViewMode || isEditMode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            :rows="2"
            :disabled="isViewMode"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="通知类型" required>
              <el-select v-model="templateForm.type" :disabled="isViewMode" style="width: 100%">
                <el-option
                  v-for="type in notificationTypes"
                  :key="type"
                  :label="type"
                  :value="type"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知渠道" required>
              <el-select v-model="templateForm.channel" :disabled="isViewMode" style="width: 100%">
                <el-option
                  v-for="channel in notificationChannels"
                  :key="channel"
                  :label="channel"
                  :value="channel"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标题模板" required>
          <el-input
            v-model="templateForm.titleTemplate"
            :disabled="isViewMode"
            placeholder="支持变量替换，如：{{taskName}}"
          />
        </el-form-item>
        <el-form-item label="内容模板" required>
          <el-input
            v-model="templateForm.contentTemplate"
            type="textarea"
            :rows="6"
            :disabled="isViewMode"
            placeholder="支持变量替换，如：{{taskName}}、{{userName}}等"
          />
        </el-form-item>
        <el-form-item label="模板变量">
          <el-input
            v-model="templateForm.variables"
            type="textarea"
            :rows="3"
            :disabled="isViewMode"
            placeholder="JSON格式，定义模板中可用的变量"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否启用">
              <el-switch
                v-model="templateForm.isEnabled"
                :disabled="isViewMode"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系统模板">
              <el-switch
                v-model="templateForm.isSystem"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">取消</el-button>
          <el-button v-if="!isViewMode" type="primary" @click="handleSave">
            {{ isEditMode ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 复制模板对话框 -->
    <el-dialog
      v-model="copyDialogVisible"
      title="复制模板"
      width="400px"
    >
      <el-form :model="copyForm" label-width="80px">
        <el-form-item label="新代码" required>
          <el-input v-model="copyForm.code" placeholder="请输入新的模板代码" />
        </el-form-item>
        <el-form-item label="新名称" required>
          <el-input v-model="copyForm.name" placeholder="请输入新的模板名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="copyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCopyConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { notificationTemplateApi } from '@/api/notificationTemplate'

// 响应式数据
const loading = ref(false)
const templates = ref([])
const selectedTemplates = ref([])
const detailDialogVisible = ref(false)
const copyDialogVisible = ref(false)
const dialogMode = ref('') // 'view', 'create', 'edit'
const currentTemplate = ref(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  channel: '',
  isEnabled: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 模板表单
const templateForm = reactive({
  name: '',
  code: '',
  description: '',
  type: '',
  channel: '',
  titleTemplate: '',
  contentTemplate: '',
  variables: '',
  isEnabled: true,
  isSystem: false
})

// 复制表单
const copyForm = reactive({
  code: '',
  name: ''
})

// 通知类型和渠道
const notificationTypes = ref([])
const notificationChannels = ref([])

// 计算属性
const dialogTitle = computed(() => {
  switch (dialogMode.value) {
    case 'view': return '查看模板'
    case 'create': return '新建模板'
    case 'edit': return '编辑模板'
    default: return '模板详情'
  }
})

const isViewMode = computed(() => dialogMode.value === 'view')
const isEditMode = computed(() => dialogMode.value === 'edit')

// 方法
const loadTemplates = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await notificationTemplateApi.getTemplates(params)
    if (response.success) {
      templates.value = response.data.content
      pagination.total = response.data.totalElements
    }
  } catch (error) {
    ElMessage.error('加载模板列表失败')
  } finally {
    loading.value = false
  }
}

const loadNotificationTypes = async () => {
  try {
    const response = await notificationTemplateApi.getNotificationTypes()
    if (response.success) {
      notificationTypes.value = response.data
    }
  } catch (error) {
    console.error('加载通知类型失败:', error)
  }
}

const loadNotificationChannels = async () => {
  try {
    const response = await notificationTemplateApi.getNotificationChannels()
    if (response.success) {
      notificationChannels.value = response.data
    }
  } catch (error) {
    console.error('加载通知渠道失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadTemplates()
}

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    type: '',
    channel: '',
    isEnabled: null
  })
  handleSearch()
}

const handleRefresh = () => {
  loadTemplates()
}

const handleCreate = () => {
  dialogMode.value = 'create'
  Object.assign(templateForm, {
    name: '',
    code: '',
    description: '',
    type: '',
    channel: '',
    titleTemplate: '',
    contentTemplate: '',
    variables: '',
    isEnabled: true,
    isSystem: false
  })
  detailDialogVisible.value = true
}

const handleView = (row) => {
  dialogMode.value = 'view'
  Object.assign(templateForm, row)
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  currentTemplate.value = row
  Object.assign(templateForm, row)
  detailDialogVisible.value = true
}

const handleCopy = (row) => {
  currentTemplate.value = row
  copyForm.code = row.code + '_copy'
  copyForm.name = row.name + ' (复制)'
  copyDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '确认删除', {
      type: 'warning'
    })
    
    const response = await notificationTemplateApi.deleteTemplate(row.id)
    if (response.success) {
      ElMessage.success('删除成功')
      loadTemplates()
    } else {
      ElMessage.error(response.error || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleToggleStatus = async (row) => {
  try {
    const response = await notificationTemplateApi.toggleTemplateStatus(row.id)
    if (response.success) {
      ElMessage.success('状态更新成功')
      loadTemplates()
    } else {
      ElMessage.error(response.error || '状态更新失败')
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleSave = async () => {
  try {
    let response
    if (dialogMode.value === 'create') {
      response = await notificationTemplateApi.createTemplate(templateForm)
    } else if (dialogMode.value === 'edit') {
      response = await notificationTemplateApi.updateTemplate(currentTemplate.value.id, templateForm)
    }
    
    if (response.success) {
      ElMessage.success(dialogMode.value === 'create' ? '创建成功' : '更新成功')
      detailDialogVisible.value = false
      loadTemplates()
    } else {
      ElMessage.error(response.error || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCopyConfirm = async () => {
  try {
    const response = await notificationTemplateApi.copyTemplate(currentTemplate.value.id, copyForm)
    if (response.success) {
      ElMessage.success('复制成功')
      copyDialogVisible.value = false
      loadTemplates()
    } else {
      ElMessage.error(response.error || '复制失败')
    }
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const handleSelectionChange = (selection) => {
  selectedTemplates.value = selection
}

const handleBatchEnable = async () => {
  try {
    const ids = selectedTemplates.value.map(item => item.id)
    const response = await notificationTemplateApi.batchToggleTemplateStatus({
      ids,
      isEnabled: true
    })
    if (response.success) {
      ElMessage.success('批量启用成功')
      loadTemplates()
    } else {
      ElMessage.error(response.error || '批量启用失败')
    }
  } catch (error) {
    ElMessage.error('批量启用失败')
  }
}

const handleBatchDisable = async () => {
  try {
    const ids = selectedTemplates.value.map(item => item.id)
    const response = await notificationTemplateApi.batchToggleTemplateStatus({
      ids,
      isEnabled: false
    })
    if (response.success) {
      ElMessage.success('批量禁用成功')
      loadTemplates()
    } else {
      ElMessage.error(response.error || '批量禁用失败')
    }
  } catch (error) {
    ElMessage.error('批量禁用失败')
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadTemplates()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadTemplates()
}

const getTypeTagType = (type) => {
  const typeMap = {
    'SYSTEM': 'info',
    'WORKFLOW': 'primary',
    'TASK': 'success',
    'APPROVAL': 'warning',
    'CUSTOMER': 'danger',
    'SALES': 'success',
    'MARKETING': 'primary'
  }
  return typeMap[type] || 'info'
}

const getChannelTagType = (channel) => {
  const channelMap = {
    'IN_SITE': 'primary',
    'EMAIL': 'success',
    'SMS': 'warning',
    'WECHAT': 'success',
    'DINGTALK': 'info',
    'PUSH': 'danger'
  }
  return channelMap[channel] || 'info'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 生命周期
onMounted(() => {
  loadTemplates()
  loadNotificationTypes()
  loadNotificationChannels()
})
</script>

<style scoped>
.notification-templates {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.table-container {
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.pagination-container {
  padding: 20px;
  text-align: right;
}

.batch-actions {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  padding: 10px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
}

.dialog-footer {
  text-align: right;
}
</style>
