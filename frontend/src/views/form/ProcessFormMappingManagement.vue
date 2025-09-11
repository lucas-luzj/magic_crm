<template>
  <el-card>
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>流程表单映射管理</h2>
        <p class="page-description">管理流程节点与表单的关联关系，支持配置默认表单</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建映射
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="流程定义">
          <el-select
            v-model="searchForm.processDefinitionKey"
            placeholder="请选择流程定义"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="process in processDefinitions"
              :key="process.processKey"
              :label="process.processName"
              :value="process.processKey"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务定义">
          <el-input
            v-model="searchForm.taskDefinitionKey"
            placeholder="请输入任务定义Key"
            clearable
            style="width: 200px"
          />
        </el-form-item>
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
        <el-form-item label="映射类型">
          <el-select
            v-model="searchForm.mappingType"
            placeholder="请选择映射类型"
            clearable
            style="width: 150px"
          >
            <el-option label="开始节点" value="START" />
            <el-option label="任务节点" value="TASK" />
            <el-option label="结束节点" value="END" />
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

    <!-- 映射列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="processDefinitionKey" label="流程定义" min-width="150" />
        
        <el-table-column prop="taskDefinitionKey" label="任务定义" min-width="150" />
        
        <el-table-column prop="formTemplateName" label="表单模板" min-width="200">
          <template #default="{ row }">
            <div class="template-info">
              <div class="template-name">{{ row.formTemplateName }}</div>
              <div class="template-key">{{ row.formTemplateKey }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="mappingType" label="映射类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMappingTypeTag(row.mappingType)" size="small">
              {{ getMappingTypeText(row.mappingType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="formVersion" label="表单版本" width="100" />
        
        <el-table-column prop="isDefault" label="默认表单" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="formPosition" label="表单位置" width="120">
          <template #default="{ row }">
            {{ getFormPositionText(row.formPosition) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createdBy" label="创建者" width="120" />
        
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              v-if="!row.isDefault" 
              size="small" 
              type="success" 
              @click="handleSetDefault(row)"
            >
              <el-icon><Check /></el-icon>
              设为默认
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑映射对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑映射' : '新建映射'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="mappingForm" :rules="mappingRules" ref="mappingFormRef" label-width="120px">
        <el-form-item label="流程定义" prop="processDefinitionKey">
          <el-select v-model="mappingForm.processDefinitionKey" placeholder="请选择流程定义" style="width: 100%">
            <el-option
              v-for="process in processDefinitions"
              :key="process.processKey"
              :label="process.processName"
              :value="process.processKey"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="任务定义" prop="taskDefinitionKey">
          <el-input v-model="mappingForm.taskDefinitionKey" placeholder="请输入任务定义Key" />
        </el-form-item>
        
        <el-form-item label="表单模板" prop="formTemplateId">
          <el-select v-model="mappingForm.formTemplateId" placeholder="请选择表单模板" style="width: 100%">
            <el-option
              v-for="template in formTemplates"
              :key="template.id"
              :label="template.formName"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="映射类型" prop="mappingType">
          <el-select v-model="mappingForm.mappingType" placeholder="请选择映射类型" style="width: 100%">
            <el-option label="开始节点" value="START" />
            <el-option label="任务节点" value="TASK" />
            <el-option label="结束节点" value="END" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="表单版本" prop="formVersion">
          <el-input-number v-model="mappingForm.formVersion" :min="1" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="是否默认" prop="isDefault">
          <el-switch v-model="mappingForm.isDefault" />
        </el-form-item>
        
        <el-form-item label="表单位置" prop="formPosition">
          <el-select v-model="mappingForm.formPosition" placeholder="请选择表单位置" style="width: 100%">
            <el-option label="任务前" value="BEFORE" />
            <el-option label="任务后" value="AFTER" />
            <el-option label="替换任务" value="REPLACE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSave" :loading="saving">
          确认
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Check, Delete } from '@element-plus/icons-vue'
import { processFormMappingApi } from '@/api/processFormMapping'
import { processDefinitionApi } from '@/api/workflow'
import { formTemplateApi } from '@/api/formTemplate'

export default {
  name: 'ProcessFormMappingManagement',
  components: {
    Plus, Search, Refresh, Edit, Check, Delete
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const saving = ref(false)
    const tableData = ref([])
    const processDefinitions = ref([])
    const formTemplates = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const mappingFormRef = ref()

    // 搜索表单
    const searchForm = reactive({
      processDefinitionKey: '',
      taskDefinitionKey: '',
      formTemplateId: '',
      mappingType: ''
    })

    // 映射表单
    const mappingForm = reactive({
      processDefinitionKey: '',
      taskDefinitionKey: '',
      formTemplateId: '',
      mappingType: 'TASK',
      formVersion: 1,
      isDefault: false,
      formPosition: 'BEFORE'
    })

    // 表单验证规则
    const mappingRules = {
      processDefinitionKey: [
        { required: true, message: '请选择流程定义', trigger: 'change' }
      ],
      formTemplateId: [
        { required: true, message: '请选择表单模板', trigger: 'change' }
      ],
      mappingType: [
        { required: true, message: '请选择映射类型', trigger: 'change' }
      ]
    }

    // 方法
    const loadProcessDefinitions = async () => {
      try {
        const response = await processDefinitionApi.getAllProcessDefinitions()
        processDefinitions.value = response || []
      } catch (error) {
        console.error('加载流程定义失败:', error)
      }
    }

    const loadFormTemplates = async () => {
      try {
        const response = await formTemplateApi.getFormTemplates({ page: 0, size: 1000 })
        formTemplates.value = response.content || []
      } catch (error) {
        console.error('加载表单模板失败:', error)
      }
    }

    const loadMappings = async () => {
      loading.value = true
      try {
        // 这里需要实现根据搜索条件加载映射的API
        // 暂时使用模拟数据
        tableData.value = []
      } catch (error) {
        console.error('加载映射失败:', error)
        // request工具已经处理了错误提示
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      loadMappings()
    }

    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      loadMappings()
    }

    const handleCreate = () => {
      isEdit.value = false
      Object.keys(mappingForm).forEach(key => {
        if (key === 'mappingType') {
          mappingForm[key] = 'TASK'
        } else if (key === 'formVersion') {
          mappingForm[key] = 1
        } else if (key === 'isDefault') {
          mappingForm[key] = false
        } else if (key === 'formPosition') {
          mappingForm[key] = 'BEFORE'
        } else {
          mappingForm[key] = ''
        }
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.keys(mappingForm).forEach(key => {
        mappingForm[key] = row[key]
      })
      dialogVisible.value = true
    }

    const handleSetDefault = async (row) => {
      try {
        await ElMessageBox.confirm('确定要设置此映射为默认表单吗？', '确认设置', {
          type: 'warning'
        })
        
        // 这里需要实现设置默认映射的API
        ElMessage.success('设置成功')
        loadMappings()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('设置默认映射失败:', error)
          // request工具已经处理了错误提示
        }
      }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除此映射吗？', '确认删除', {
          type: 'warning'
        })
        
        await processFormMappingApi.deleteProcessFormMapping(row.id)
        ElMessage.success('删除成功')
        loadMappings()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除映射失败:', error)
          // request工具已经处理了错误提示
        }
      }
    }

    const confirmSave = async () => {
      if (!mappingFormRef.value) return

      try {
        await mappingFormRef.value.validate()
        
        saving.value = true

        if (isEdit.value) {
          await processFormMappingApi.updateProcessFormMapping(mappingForm.id, mappingForm)
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadMappings()
        } else {
          await processFormMappingApi.createProcessFormMapping(mappingForm)
          ElMessage.success('创建成功')
          dialogVisible.value = false
          loadMappings()
        }
      } catch (error) {
        console.error('保存映射失败:', error)
        // request工具已经处理了错误提示
      } finally {
        saving.value = false
      }
    }

    const getMappingTypeTag = (type) => {
      const typeMap = {
        START: 'success',
        TASK: 'primary',
        END: 'warning'
      }
      return typeMap[type] || 'info'
    }

    const getMappingTypeText = (type) => {
      const typeMap = {
        START: '开始节点',
        TASK: '任务节点',
        END: '结束节点'
      }
      return typeMap[type] || type
    }

    const getFormPositionText = (position) => {
      const positionMap = {
        BEFORE: '任务前',
        AFTER: '任务后',
        REPLACE: '替换任务'
      }
      return positionMap[position] || position
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }

    // 生命周期
    onMounted(() => {
      loadProcessDefinitions()
      loadFormTemplates()
      loadMappings()
    })

    return {
      loading,
      saving,
      tableData,
      processDefinitions,
      formTemplates,
      dialogVisible,
      isEdit,
      mappingFormRef,
      searchForm,
      mappingForm,
      mappingRules,
      handleSearch,
      handleReset,
      handleCreate,
      handleEdit,
      handleSetDefault,
      handleDelete,
      confirmSave,
      getMappingTypeTag,
      getMappingTypeText,
      getFormPositionText,
      formatDateTime
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
</style>
