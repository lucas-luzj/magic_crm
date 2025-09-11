<template>
  <el-card>
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>字典管理</h2>
        <p class="page-description">管理系统中的字典项，支持分类管理和权限控制</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建字典项
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="字典类型">
          <el-select
            v-model="searchForm.dictType"
            placeholder="请选择字典类型"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="type in dictTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典名称">
          <el-input
            v-model="searchForm.dictName"
            placeholder="请输入字典名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="是否系统">
          <el-select
            v-model="searchForm.isSystem"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.isActive"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="true" />
            <el-option label="停用" :value="false" />
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

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      @sort-change="handleSortChange"
    >
      <el-table-column prop="dictKey" label="字典键" width="150" />
      <el-table-column prop="dictName" label="字典名称" min-width="200" />
      <el-table-column prop="dictValue" label="字典值" min-width="150" />
      <el-table-column prop="dictType" label="字典类型" width="120" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" sortable="custom" />
      <el-table-column label="是否系统" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isSystem ? 'danger' : 'success'" size="small">
            {{ row.isSystem ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'info'" size="small">
            {{ row.isActive ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdBy" label="创建者" width="120" />
      <el-table-column prop="createdTime" label="创建时间" width="180" sortable="custom">
        <template #default="{ row }">
          {{ formatDateTime(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleEdit(row)"
            :disabled="row.isSystem"
          >
            编辑
          </el-button>
          <el-button
            v-if="row.isActive"
            size="small"
            @click="handleDeactivate(row)"
            :disabled="row.isSystem"
          >
            停用
          </el-button>
          <el-button
            v-else
            size="small"
            @click="handleActivate(row)"
          >
            启用
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row)"
            :disabled="row.isSystem"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字典项' : '新建字典项'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="字典键" prop="dictKey">
          <el-input
            v-model="form.dictKey"
            placeholder="请输入字典键"
            :disabled="isEdit && form.isSystem"
          />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input
            v-model="form.dictName"
            placeholder="请输入字典名称"
          />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input
            v-model="form.dictValue"
            placeholder="请输入字典值"
          />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="form.dictType"
            placeholder="请输入字典类型"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="9999"
            placeholder="排序"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-switch
            v-model="form.isActive"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { dictionaryApi } from '@/api/dictionary'

export default {
  name: 'DictionaryManagement',
  setup() {
    // 响应式数据
    const loading = ref(false)
    const saving = ref(false)
    const tableData = ref([])
    const dictTypes = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref()

    // 搜索表单
    const searchForm = reactive({
      dictType: '',
      dictName: '',
      isSystem: null,
      isActive: null
    })

    // 分页数据
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 排序数据
    const sortData = reactive({
      sortBy: 'sortOrder',
      sortDir: 'asc'
    })

    // 表单数据
    const form = reactive({
      id: null,
      dictKey: '',
      dictName: '',
      dictValue: '',
      dictType: '',
      description: '',
      sortOrder: 0,
      isActive: true,
      isSystem: false
    })

    // 表单验证规则
    const formRules = {
      dictKey: [
        { required: true, message: '请输入字典键', trigger: 'blur' },
        { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
      ],
      dictName: [
        { required: true, message: '请输入字典名称', trigger: 'blur' },
        { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
      ],
      dictValue: [
        { required: true, message: '请输入字典值', trigger: 'blur' },
        { min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: 'blur' }
      ],
      dictType: [
        { required: true, message: '请输入字典类型', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ]
    }

    // 方法
    const loadDictionaries = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.currentPage - 1,
          size: pagination.pageSize,
          sortBy: sortData.sortBy,
          sortDir: sortData.sortDir,
          ...searchForm
        }
        
                 const response = await dictionaryApi.getDictionaries(params)
         tableData.value = response.records || []
         pagination.total = response.totalCount || 0
      } catch (error) {
        console.error('加载字典数据失败:', error)
        // request工具已经处理了错误提示
      } finally {
        loading.value = false
      }
    }

    const loadDictTypes = async () => {
      try {
        const response = await dictionaryApi.getDictTypes()
        dictTypes.value = response || []
      } catch (error) {
        console.error('加载字典类型失败:', error)
      }
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      loadDictionaries()
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        dictType: '',
        dictName: '',
        isSystem: null,
        isActive: null
      })
      pagination.currentPage = 1
      loadDictionaries()
    }

    const handleSortChange = ({ prop, order }) => {
      sortData.sortBy = prop
      sortData.sortDir = order === 'ascending' ? 'asc' : 'desc'
      loadDictionaries()
    }

    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      loadDictionaries()
    }

    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      loadDictionaries()
    }

    const handleCreate = () => {
      isEdit.value = false
      Object.assign(form, {
        id: null,
        dictKey: '',
        dictName: '',
        dictValue: '',
        dictType: '',
        description: '',
        sortOrder: 0,
        isActive: true,
        isSystem: false
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(form, { ...row })
      dialogVisible.value = true
    }

    const handleSave = async () => {
      try {
        await formRef.value.validate()
        
        saving.value = true

        if (isEdit.value) {
          await dictionaryApi.updateDictionary(form.id, form)
          ElMessage.success('更新字典项成功')
        } else {
          await dictionaryApi.createDictionary(form)
          ElMessage.success('创建字典项成功')
        }
        
        dialogVisible.value = false
        loadDictionaries()
      } catch (error) {
        console.error('保存字典项失败:', error)
        // request工具已经处理了错误提示
      } finally {
        saving.value = false
      }
    }

    const handleActivate = async (row) => {
      try {
        await dictionaryApi.activateDictionary(row.id)
        ElMessage.success('启用成功')
        loadDictionaries()
      } catch (error) {
        console.error('启用字典项失败:', error)
        // request工具已经处理了错误提示
      }
    }

    const handleDeactivate = async (row) => {
      try {
        await dictionaryApi.deactivateDictionary(row.id)
        ElMessage.success('停用成功')
        loadDictionaries()
      } catch (error) {
        console.error('停用字典项失败:', error)
        // request工具已经处理了错误提示
      }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除此字典项吗？', '确认删除', {
          type: 'warning'
        })
        
        await dictionaryApi.deleteDictionary(row.id)
        ElMessage.success('删除成功')
        loadDictionaries()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除字典项失败:', error)
          // request工具已经处理了错误提示
        }
      }
    }

    // 工具方法
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }

    // 生命周期
    onMounted(() => {
      loadDictionaries()
      loadDictTypes()
    })

    return {
      loading,
      saving,
      tableData,
      dictTypes,
      dialogVisible,
      isEdit,
      formRef,
      searchForm,
      pagination,
      sortData,
      form,
      formRules,
      handleSearch,
      handleReset,
      handleSortChange,
      handleSizeChange,
      handleCurrentChange,
      handleCreate,
      handleEdit,
      handleSave,
      handleActivate,
      handleDeactivate,
      handleDelete,
      formatDateTime
    }
  }
}
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;

  .header-left {
    h2 {
      margin: 0 0 8px 0;
      color: #303133;
    }

    .page-description {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
