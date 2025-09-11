<template>
  <el-card>
    
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>表单模板管理</h2>
        <p class="page-description">管理系统中的所有表单模板，支持创建、编辑、复制和删除操作</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建表单
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="表单名称">
          <el-input
            v-model="searchForm.formName"
            placeholder="请输入表单名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="激活" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
            <el-option label="草稿" value="DRAFT" />
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
    </el-card>

    <!-- 表单模板列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="formName" label="表单名称" min-width="200" sortable="custom">
          <template #default="{ row }">
            <div class="form-name-cell">
              <div class="form-title">{{ row.formName }}</div>
              <div class="form-key">{{ row.formKey }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="formDescription" label="描述" min-width="250" show-overflow-tooltip />
        
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.category" size="small">{{ row.category }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
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
        
        <el-table-column prop="formVersion" label="版本" width="80" />
        
        <el-table-column prop="createdBy" label="创建者" width="120" />
        
        <el-table-column prop="createdTime" label="创建时间" width="180" sortable="custom">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              @click="handlePreview(row)"
            >
              预览
            </el-button>
            <el-dropdown @command="(command) => handleDropdownCommand(command, row)">
              <el-button size="small">
                更多
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="copy">复制</el-dropdown-item>
                  <el-dropdown-item 
                    :command="row.status === 'ACTIVE' ? 'deactivate' : 'activate'"
                  >
                    {{ row.status === 'ACTIVE' ? '停用' : '激活' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 表单预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="表单预览"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="previewFormConfig" class="form-preview">
        <div class="form-header" v-if="previewFormConfig.title || previewFormConfig.description">
          <h3 v-if="previewFormConfig.title" class="form-title">{{ previewFormConfig.title }}</h3>
          <p v-if="previewFormConfig.description" class="form-description">{{ previewFormConfig.description }}</p>
        </div>
        <FormRenderer
          :config="previewFormConfig"
          v-model="previewFormData"
        />
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 复制表单对话框 -->
    <el-dialog
      v-model="copyDialogVisible"
      title="复制表单模板"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="copyForm" :rules="copyRules" ref="copyFormRef" label-width="100px">
        <el-form-item label="表单键" prop="formKey">
          <el-input
            v-model="copyForm.formKey"
            placeholder="请输入新的表单键"
          />
        </el-form-item>
        <el-form-item label="表单名称" prop="formName">
          <el-input
            v-model="copyForm.formName"
            placeholder="请输入新的表单名称"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="copyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCopyConfirm" :loading="copyLoading">
          确认复制
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, ArrowDown } from '@element-plus/icons-vue'
import { formTemplateApi } from '@/api/formTemplate'
import FormRenderer from '../../components/FormDesigner/FormRenderer.vue'

// 转换旧格式fields为新格式components
const convertFieldsToComponents = (fields) => {
  if (!Array.isArray(fields)) return []
  
  return fields.map(field => ({
    id: field.id || `field_${Date.now()}_${Math.random()}`,
    type: field.type || 'input',
    name: field.name || field.label || '字段',
    label: field.label || field.name || '字段',
    field: field.field || field.name || `field_${Date.now()}`,
    span: field.span || 24,
    required: field.required || false,
    placeholder: field.placeholder || '',
    ...field
  }))
}

export default {
  name: 'FormTemplateManagement',
  components: {
    FormRenderer
  },
  setup() {
    const router = useRouter()
    
    // 响应式数据
    const loading = ref(false)
    const tableData = ref([])
    const categories = ref([])
    const previewDialogVisible = ref(false)
    const copyDialogVisible = ref(false)
    const copyLoading = ref(false)
    const previewFormConfig = ref(null)
    const previewFormData = ref({})
    const copyFormRef = ref()

    // 搜索表单
    const searchForm = reactive({
      formName: '',
      category: '',
      status: ''
    })

    // 分页数据
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 排序数据
    const sortData = reactive({
      sortBy: 'id',
      sortDir: 'desc'
    })

    // 复制表单数据
    const copyForm = reactive({
      formKey: '',
      formName: ''
    })

    const copyRules = {
      formKey: [
        { required: true, message: '请输入表单键', trigger: 'blur' },
        { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '表单键只能包含字母、数字和下划线，且以字母开头', trigger: 'blur' }
      ],
      formName: [
        { required: true, message: '请输入表单名称', trigger: 'blur' }
      ]
    }

    let currentCopyRow = null

    // 生命周期
    onMounted(() => {
      loadFormTemplates()
      loadCategories()
    })

    // 方法
    const loadFormTemplates = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.currentPage - 1,
          size: pagination.pageSize,
          sortBy: sortData.sortBy,
          sortDir: sortData.sortDir,
          ...searchForm
        }
        
        const response = await formTemplateApi.getFormTemplates(params)
        tableData.value = response.records || []
        pagination.total = response.totalCount || 0
      } catch (error) {
        console.error('加载表单模板失败:', error)
        // request工具已经处理了错误提示
      } finally {
        loading.value = false
      }
    }

    const loadCategories = async () => {
      try {
        const response = await formTemplateApi.getCategories()
        categories.value = response || []
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      loadFormTemplates()
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        formName: '',
        category: '',
        status: ''
      })
      pagination.currentPage = 1
      loadFormTemplates()
    }

    const handleSortChange = ({ prop, order }) => {
      if (prop) {
        sortData.sortBy = prop
        sortData.sortDir = order === 'ascending' ? 'asc' : 'desc'
        loadFormTemplates()
      }
    }

    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      loadFormTemplates()
    }

    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      loadFormTemplates()
    }

    const handleCreate = () => {
      // 跳转到表单设计器页面创建新表单
      router.push({
        name: 'FormDesigner',
        query: { mode: 'create' }
      })
    }

    const handleEdit = (row) => {
      // 跳转到表单设计器页面编辑表单
      router.push({
        name: 'FormDesigner',
        query: { 
          mode: 'edit',
          id: row.id,
          formKey: row.formKey,
          formName: row.formName
        }
      })
    }

    const handlePreview = async (row) => {
      try {
        loading.value = true
        // 获取完整的表单模板数据（包含formConfig）
        const formTemplate = await formTemplateApi.getFormTemplateById(row.id)
        
        // 适配新FormDesigner的数据结构
        if (formTemplate.formConfig) {
          if (formTemplate.formConfig.components) {
            // 新格式：直接使用
            previewFormConfig.value = formTemplate.formConfig
          } else if (formTemplate.formConfig.fields) {
            // 旧格式：转换为新格式
            previewFormConfig.value = {
              title: formTemplate.formName,
              name: formTemplate.formKey,
              description: formTemplate.formDescription || '',
              components: convertFieldsToComponents(formTemplate.formConfig.fields),
              labelPosition: 'left',
              labelWidth: 100,
              size: 'default'
            }
          } else {
            // 空配置
            previewFormConfig.value = {
              title: formTemplate.formName,
              name: formTemplate.formKey,
              description: formTemplate.formDescription || '',
              components: [],
              labelPosition: 'left',
              labelWidth: 100,
              size: 'default'
            }
          }
        }
        
        previewFormData.value = {}
        previewDialogVisible.value = true
      } catch (error) {
        console.error('预览表单失败:', error)
        // request工具已经处理了错误提示，这里不需要再显示
      } finally {
        loading.value = false
      }
    }



    const handleDropdownCommand = (command, row) => {
      switch (command) {
        case 'copy':
          handleCopy(row)
          break
        case 'activate':
          handleActivate(row)
          break
        case 'deactivate':
          handleDeactivate(row)
          break
        case 'delete':
          handleDelete(row)
          break
      }
    }

    const handleCopy = (row) => {
      currentCopyRow = row
      copyForm.formKey = row.formKey + '_copy'
      copyForm.formName = row.formName + ' (副本)'
      copyDialogVisible.value = true
    }

    const handleCopyConfirm = async () => {
      if (!copyFormRef.value) return
      
      try {
        const valid = await copyFormRef.value.validate()
        if (!valid) return
      } catch {
        return
      }
      
      copyLoading.value = true
      try {
        await formTemplateApi.copyFormTemplate(
          currentCopyRow.id,
          copyForm.formKey,
          copyForm.formName
        )
        
        ElMessage.success('表单模板复制成功')
        copyDialogVisible.value = false
        loadFormTemplates()
      } catch (error) {
        console.error('复制表单模板失败:', error)
        // request工具已经处理了错误提示
      } finally {
        copyLoading.value = false
      }
    }

    const handleActivate = async (row) => {
      try {
        await formTemplateApi.activateFormTemplate(row.id)
        ElMessage.success('表单模板已激活')
        loadFormTemplates()
      } catch (error) {
        console.error('激活表单模板失败:', error)
        // request工具已经处理了错误提示
      }
    }

    const handleDeactivate = async (row) => {
      try {
        await formTemplateApi.deactivateFormTemplate(row.id)
        ElMessage.success('表单模板已停用')
        loadFormTemplates()
      } catch (error) {
        console.error('停用表单模板失败:', error)
        // request工具已经处理了错误提示
      }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除表单模板 "${row.formName}" 吗？此操作不可恢复。`,
          '确认删除',
          {
            type: 'warning',
            confirmButtonText: '确定删除',
            cancelButtonText: '取消'
          }
        )
        
        await formTemplateApi.deleteFormTemplate(row.id)
        ElMessage.success('表单模板删除成功')
        loadFormTemplates()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除表单模板失败:', error)
          // request工具已经处理了错误提示
        }
      }
    }

    // 工具方法
    const getStatusType = (status) => {
      const typeMap = {
        'ACTIVE': 'success',
        'INACTIVE': 'info',
        'DRAFT': 'warning'
      }
      return typeMap[status] || 'info'
    }

    const getStatusText = (status) => {
      const textMap = {
        'ACTIVE': '激活',
        'INACTIVE': '停用',
        'DRAFT': '草稿'
      }
      return textMap[status] || status
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }

    return {
      // 图标
      Plus,
      Search,
      Refresh,
      ArrowDown,
      // 数据
      loading,
      tableData,
      categories,
      previewDialogVisible,
      copyDialogVisible,
      copyLoading,
      previewFormConfig,
      previewFormData,
      copyFormRef,
      searchForm,
      pagination,
      sortData,
      copyForm,
      copyRules,
      // 方法
      loadFormTemplates,
      loadCategories,
      handleSearch,
      handleReset,
      handleSortChange,
      handleSizeChange,
      handleCurrentChange,
      handleCreate,
      handleEdit,
      handlePreview,

      handleDropdownCommand,
      handleCopy,
      handleCopyConfirm,
      handleActivate,
      handleDeactivate,
      handleDelete,
      getStatusType,
      getStatusText,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.form-template-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.form-name-cell {
  display: flex;
  flex-direction: column;
}

.form-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.form-key {
  font-size: 12px;
  color: #909399;
  font-family: 'Courier New', monospace;
}

.text-muted {
  color: #C0C4CC;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.form-preview {
  max-height: 600px;
  overflow-y: auto;
  padding: 0;
}

.form-header {
  padding: 20px 20px 16px;
  border-bottom: 1px solid #EBEEF5;
  background-color: #FAFAFA;
}

.form-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.form-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.form-preview :deep(.form-renderer) {
  padding: 20px;
  background-color: white;
}

.form-preview :deep(.el-form-item) {
  margin-bottom: 18px;
}

.form-preview :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

.form-preview :deep(.el-input__inner),
.form-preview :deep(.el-textarea__inner) {
  background-color: #F5F7FA;
  border-color: #E4E7ED;
}

.form-preview :deep(.el-input.is-disabled .el-input__inner),
.form-preview :deep(.el-textarea.is-disabled .el-textarea__inner) {
  background-color: #F5F7FA;
  color: #606266;
}
</style>