<template>
  <div class="start-process">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>发起流程</span>
          <el-button type="primary" @click="refreshProcessList">
            <el-icon>
              <Refresh />
            </el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 流程分类筛选 -->
      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-select v-model="selectedCategory" placeholder="选择流程分类" @change="filterProcesses" clearable>
              <el-option label="全部分类" value="" />
              <el-option label="审批流程" value="approval" />
              <el-option label="业务流程" value="business" />
              <el-option label="系统流程" value="system" />
            </el-select>
          </el-col>
          <el-col :span="12">
            <el-input v-model="searchKeyword" placeholder="搜索流程名称或描述" @input="filterProcesses" clearable>
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </el-col>
        </el-row>
      </div>

      <!-- 流程列表 -->
      <div class="process-list" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="8" v-for="process in filteredProcesses" :key="process.id">
            <el-card class="process-card" shadow="hover" @click="selectProcess(process)">
              <div class="process-info">
                <div class="process-header">
                  <h3>{{ process.processName }}</h3>
                  <el-tag :type="getCategoryType(process.category)" size="small">
                    {{ getCategoryLabel(process.category) }}
                  </el-tag>
                </div>
                <p class="process-description">{{ process.description || '暂无描述' }}</p>
                <div class="process-meta">
                  <span class="version">版本: v{{ process.version }}</span>
                  <span class="deploy-time">部署时间: {{ formatDate(process.deploymentTime) }}</span>
                </div>
              </div>
              <div class="process-actions">
                <el-button type="primary" size="small">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  发起流程
                </el-button>
                <el-button size="small" @click.stop="viewProcessDiagram(process)">
                  <el-icon>
                    <View />
                  </el-icon>
                  查看流程图
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="filteredProcesses.length === 0 && !loading" description="暂无可用流程" />
      </div>
    </el-card>

    <!-- 流程启动对话框 -->
    <el-dialog v-model="startDialogVisible" :title="`发起流程: ${selectedProcess?.processName}`" width="800px"
      :close-on-click-modal="false">
      <div v-if="selectedProcess">
        <!-- 流程基本信息 -->
        <el-descriptions :column="2" border class="process-details">
          <el-descriptions-item label="流程名称">
            {{ selectedProcess.processName }}
          </el-descriptions-item>
          <el-descriptions-item label="流程分类">
            <el-tag :type="getCategoryType(selectedProcess.category)" size="small">
              {{ getCategoryLabel(selectedProcess.category) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="流程版本">
            v{{ selectedProcess.version }}
          </el-descriptions-item>
          <el-descriptions-item label="流程描述" :span="2">
            {{ selectedProcess.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 启动表单 -->
        <div class="start-form-section">
          <el-divider content-position="left">
            <el-icon>
              <Edit />
            </el-icon>
            填写启动表单
          </el-divider>

          <div v-if="startFormConfig && startFormConfig.fields.length > 0">
            <FormRenderer ref="startFormRef" :config="startFormConfig" :model-value="startFormData"
              @update:model-value="handleStartFormChange" @validate="handleStartFormValidate" />
          </div>
          <el-alert v-else title="此流程无需填写启动表单" type="info" :closable="false" show-icon />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="startDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="startProcessInstance" :loading="starting" :disabled="!canStartProcess">
            <el-icon>
              <VideoPlay />
            </el-icon>
            启动流程
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 流程图查看对话框 -->
    <el-dialog v-model="diagramDialogVisible" :title="`流程图: ${viewingProcess?.processName}`" width="80%" top="5vh">
      <div class="diagram-container">
        <img v-if="diagramUrl" :src="diagramUrl" alt="流程图" style="max-width: 100%; height: auto;" />
        <div v-else class="no-diagram">暂无流程图</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import FormRenderer from '../../components/FormDesigner/FormRenderer.vue'
import { processDefinitionApi,processInstanceApi } from '@/api/workflow'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh, Search, Plus, View, Edit, VideoPlay
} from '@element-plus/icons-vue'

export default {
  name: 'StartProcess',
  components: {
    FormRenderer,
    Refresh,
    Search,
    Plus,
    View,
    Edit,
    VideoPlay
  },
  data() {
    return {
      loading: false,
      starting: false,
      selectedCategory: '',
      searchKeyword: '',

      // 流程列表
      processList: [],
      filteredProcesses: [],

      // 流程启动
      startDialogVisible: false,
      selectedProcess: null,
      startFormConfig: null,
      startFormData: {},

      // 流程图查看
      diagramDialogVisible: false,
      viewingProcess: null,
      diagramUrl: null
    }
  },

  computed: {
    canStartProcess() {
      if (!this.startFormConfig || this.startFormConfig.fields.length === 0) {
        return true // 无表单可直接启动
      }
      // 检查必填字段是否已填写
      return this.startFormConfig.fields.every(field => {
        if (!field.required) return true
        const value = this.startFormData[field.id]
        return value !== undefined && value !== null && value !== ''
      })
    }
  },

  mounted() {
    this.loadProcessList()
  },

  methods: {
    // 加载流程列表
    async loadProcessList() {
      this.loading = true
      try {
        const data = await processDefinitionApi.getProcessDefinitions()
        this.processList = data.records || []
        this.filterProcesses()
        ElMessage.success('流程列表加载成功')
      } catch (error) {
        console.error('加载流程列表失败:', error)
        ElMessage.error('加载流程列表失败')
        // 使用模拟数据
        this.processList = this.getMockProcessList()
        this.filterProcesses()
      } finally {
        this.loading = false
      }
    },

   

    // 刷新流程列表
    refreshProcessList() {
      this.loadProcessList()
    },

    // 筛选流程
    filterProcesses() {
      let filtered = this.processList

      // 按分类筛选
      if (this.selectedCategory) {
        filtered = filtered.filter(process => process.category === this.selectedCategory)
      }

      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(process =>
          process.name.toLowerCase().includes(keyword) ||
          (process.description && process.description.toLowerCase().includes(keyword))
        )
      }

      this.filteredProcesses = filtered
    },

    // 选择流程
    async selectProcess(process) {
      this.selectedProcess = process
      this.startFormData = {}

      this.startFormConfig = process.formConfig ? JSON.parse(process.formConfig) : null
      console.log(this.startFormConfig)
      

      this.startDialogVisible = true
    },


    // 处理启动表单数据变化
    handleStartFormChange(data) {
      this.startFormData = data
    },

    // 处理启动表单验证
    handleStartFormValidate(isValid, data) {
      // 可以在这里处理表单验证结果
    },

    // 启动流程实例
    async startProcessInstance() {
      // 如果有表单，先验证表单
      if (this.startFormConfig && this.startFormConfig.fields.length > 0) {
        if (this.$refs.startFormRef) {
          const isValid = await this.$refs.startFormRef.validate()
          if (!isValid) {
            ElMessage.error('请完善表单信息')
            return
          }
        }
      }

      this.starting = true
      try {
        const result = await processInstanceApi.startProcessInstance({
          processDefinitionKey: this.selectedProcess.processKey,
          businessKey: `${this.selectedProcess.processKey}_${Date.now()}`,
          variables: this.startFormData
        })

        ElMessage.success(`流程启动成功！流程实例ID: ${result.processInstanceId}`)
        this.startDialogVisible = false

        // 询问是否跳转到任务处理页面
        try {
          await ElMessageBox.confirm('流程已成功启动，是否跳转到任务处理页面？', '提示', {
            type: 'success',
            confirmButtonText: '跳转',
            cancelButtonText: '留在当前页面'
          })
          this.$router.push('/workflow/tasks')
        } catch {
          // 用户选择留在当前页面
        }

      } catch (error) {
        console.error('启动流程失败:', error)
        ElMessage.error('启动流程失败: ' + (error.message || '未知错误'))
      } finally {
        this.starting = false
      }
    },

    // 查看流程图
    async viewProcessDiagram(process) {
      this.viewingProcess = process
      try {
        const data = await processDefinitionApi.getProcessDefinitionDiagram(process.processDefinitionId)
        this.diagramUrl = URL.createObjectURL(data)
        this.diagramDialogVisible = true
      } catch (error) {
        console.error('获取流程图失败:', error)
        ElMessage.error('获取流程图失败')
      }
  },

    // 获取分类类型
    getCategoryType(category) {
      const typeMap = {
        'approval': 'warning',
        'business': 'success',
        'system': 'info'
      }
      return typeMap[category] || 'default'
    },

    // 获取分类标签
    getCategoryLabel(category) {
      const labelMap = {
        'approval': '审批流程',
        'business': '业务流程',
        'system': '系统流程'
      }
      return labelMap[category] || category
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
  },

  beforeUnmount() {
    if (this.bpmnViewer) {
      this.bpmnViewer.destroy()
    }
  }
}
</script>

<style scoped>
.start-process {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  margin-bottom: 20px;
}

.process-list {
  min-height: 400px;
}

.process-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  height: 200px;
}

.process-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.process-info {
  height: 140px;
  display: flex;
  flex-direction: column;
}

.process-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.process-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 10px;
}

.process-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 10px 0;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.process-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-top: auto;
}

.process-actions {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.process-details {
  margin-bottom: 20px;
}

.start-form-section {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}

.diagram-container {
  height: 70vh;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.diagram-canvas {
  width: 100%;
  height: 100%;
}

:deep(.bjs-powered-by) {
  display: none;
}
</style>