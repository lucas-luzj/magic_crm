<template>
  <div class="process-designer">
    <div class="designer-header">
      <div class="header-left">
        <el-button v-if="isEditMode" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button type="primary" @click="saveProcess" :loading="saving">
          <el-icon><DocumentAdd /></el-icon>
          {{ isEditMode ? '更新流程' : '保存流程' }}
        </el-button>
        <el-button @click="exportBpmn">
          <el-icon><Download /></el-icon>
          导出BPMN
        </el-button>
        <el-button @click="importBpmn">
          <el-icon><Upload /></el-icon>
          导入BPMN
        </el-button>
        <el-button @click="clearCanvas">
          <el-icon><Delete /></el-icon>
          清空画布
        </el-button>
        <el-button @click="openFormConfig" :disabled="!selectedElement || !isUserTask(selectedElement)">
          <el-icon><Setting /></el-icon>
          表单配置
        </el-button>
      </div>
      <div class="header-right">
        <el-input
          v-model="processName"
          placeholder="请输入流程名称"
          style="width: 200px; margin-right: 10px;"
        />
        <el-select v-model="processCategory" placeholder="选择分类" style="width: 120px;">
          <el-option label="审批流程" value="approval" />
          <el-option label="业务流程" value="business" />
          <el-option label="系统流程" value="system" />
        </el-select>
      </div>
    </div>

    <div class="designer-container">
      <div class="designer-canvas" ref="canvasRef"></div>
      <CustomPropertiesPanel 
        :selected-element="selectedElement"
        :bpmn-modeler="bpmnModeler"
        @element-updated="onElementUpdated"
      />
    </div>

    <!-- 导入文件对话框 -->
    <input
      ref="fileInputRef"
      type="file"
      accept=".bpmn,.xml"
      style="display: none"
      @change="handleFileImport"
    />

    <!-- 保存对话框 -->
    <el-dialog v-model="saveDialogVisible" :title="isEditMode ? '更新流程定义' : '保存流程定义'" width="500px">
      <el-form :model="saveForm" label-width="100px">
        <el-form-item label="流程名称" required>
          <el-input v-model="saveForm.processName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" required>
          <el-select v-model="saveForm.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="审批流程" value="approval" />
            <el-option label="业务流程" value="business" />
            <el-option label="系统流程" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="流程描述">
          <el-input
            v-model="saveForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入流程描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSave" :loading="saving">确定</el-button>
      </template>
    </el-dialog>

    <!-- 表单配置对话框 -->
    <FormConfigDialog
      v-model="formConfigVisible"
      :element="selectedElement"
      @save="saveFormConfig"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, toRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentAdd, Download, Upload, Delete, ArrowLeft, Setting } from '@element-plus/icons-vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda'
import { processDefinitionApi } from '@/api/workflow'
import FormConfigDialog from '@/components/workflow/FormConfigDialog.vue'
import CustomPropertiesPanel from '@/components/workflow/CustomPropertiesPanel.vue'
import translateModule from '@/components/workflow/BpmnTranslate.js'
import { paletteProviderModule } from '@/components/workflow/BpmnPaletteProvider.js'
import { contextPadProviderModule } from '@/components/workflow/BpmnContextPadProvider.js'

// 导入必要的BPMN样式
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'

const route = useRoute()
const router = useRouter()

// 响应式数据
const canvasRef = ref(null)
const fileInputRef = ref(null)
const processName = ref('')
const processCategory = ref('approval')
const saving = ref(false)
const saveDialogVisible = ref(false)

// 编辑模式相关
const isEditMode = ref(false)
const editProcessDefinitionId = ref('')

// 表单配置相关
const formConfigVisible = ref(false)
const selectedElement = ref(null)
const bpmnModeler = ref(null)

const saveForm = ref({
  processName: '',
  category: 'approval',
  description: ''
})

// 默认的BPMN XML模板
const defaultBpmnXml = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="4.12.0">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">
        <dc:Bounds x="179" y="79" width="36" height="36" />
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

// 初始化BPMN建模器
const initBpmnModeler = async () => {
  bpmnModeler.value = new BpmnModeler({
    container: canvasRef.value,
    additionalModules: [
      translateModule, // 添加中文翻译模块
      paletteProviderModule, // 添加自定义中文调色板
      contextPadProviderModule, // 添加自定义中文上下文菜单
    ],
    moddleExtensions: {
      camunda: camundaModdleDescriptor,
    }
  })

  // 将建模器实例设置为全局变量，供表单配置使用
  window.bpmnModeler = bpmnModeler.value

  // 添加元素选择监听
  const eventBus = bpmnModeler.value.get('eventBus')
  eventBus.on('selection.changed', (event) => {
    const newSelection = event.newSelection
    if (newSelection && newSelection.length > 0) {
      // 直接存储原始对象，不让Vue响应式系统包装
      selectedElement.value = newSelection[0]
    } else {
      selectedElement.value = null
    }
  })

  // 检查是否为编辑模式
  await checkEditMode()
}

// 检查编辑模式并加载数据
const checkEditMode = async () => {
  const mode = route.query.mode
  if (mode === 'edit') {
    isEditMode.value = true
    editProcessDefinitionId.value = route.query.processDefinitionId
    processName.value = route.query.processName || ''
    processCategory.value = route.query.category || 'approval'
    
    // 从sessionStorage获取BPMN XML
    const bpmnXml = sessionStorage.getItem('editProcessBpmnXml')
    if (bpmnXml) {
      try {
        await bpmnModeler.value.importXML(bpmnXml)
        // 自动调整视图
        const canvas = bpmnModeler.value.get('canvas')
        canvas.zoom('fit-viewport')
        ElMessage.success('流程加载成功，可以开始编辑')
      } catch (error) {
        console.error('加载BPMN失败:', error)
        ElMessage.error('流程加载失败，将使用默认模板')
        await bpmnModeler.value.importXML(defaultBpmnXml)
      }
      // 清理sessionStorage
      sessionStorage.removeItem('editProcessBpmnXml')
    } else {
      ElMessage.warning('未找到流程数据，将使用默认模板')
      await bpmnModeler.value.importXML(defaultBpmnXml)
    }
  } else {
    // 新建模式，导入默认的BPMN图
    await bpmnModeler.value.importXML(defaultBpmnXml)
  }
}

// 返回按钮
const goBack = () => {
  router.push('/workflow/process-definitions')
}

// 保存流程
const saveProcess = () => {
  saveForm.value.processName = processName.value
  saveForm.value.category = processCategory.value
  saveDialogVisible.value = true
}

// 确认保存
const confirmSave = async () => {
  if (!saveForm.value.processName) {
    ElMessage.error('请输入流程名称')
    return
  }

  try {
    saving.value = true
    const { xml } = await bpmnModeler.value.saveXML({ format: true })
    
    const response = await processDefinitionApi.deployProcessFromXml({
      processName: saveForm.value.processName,
      category: saveForm.value.category,
      bpmnXml: xml,
      fileName: `${saveForm.value.processName}.bpmn`
    })

    // 保存成功后，处理暂存的表单配置
    const tempFormConfigs = JSON.parse(sessionStorage.getItem('tempFormConfigs') || '{}')
    if (Object.keys(tempFormConfigs).length > 0 && response.data && response.data.data) {
      try {
        // 合并所有表单配置
        const allFormConfigs = Object.values(tempFormConfigs)
        const mergedFormConfig = {
          configs: allFormConfigs
        }
        
        // 保存到数据库
        await processDefinitionApi.saveFormConfig(
          response.data.data.processDefinitionId, 
          JSON.stringify(mergedFormConfig)
        )
        
        // 清除暂存的表单配置
        sessionStorage.removeItem('tempFormConfigs')
        ElMessage.success('流程和表单配置保存成功')
      } catch (error) {
        console.warn('保存表单配置失败:', error)
        ElMessage.warning('流程保存成功，但表单配置保存失败')
      }
    } else {
      const message = isEditMode.value ? '流程更新成功' : '流程保存成功'
      ElMessage.success(message)
    }
    
    saveDialogVisible.value = false
    
    // 如果是编辑模式，询问是否返回流程定义管理页面
    if (isEditMode.value) {
      ElMessageBox.confirm('是否返回流程定义管理页面？', '操作成功', {
        confirmButtonText: '返回',
        cancelButtonText: '继续编辑',
        type: 'success'
      }).then(() => {
        router.push('/workflow/process-definitions')
      }).catch(() => {
        // 用户选择继续编辑，不做任何操作
      })
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 导出BPMN
const exportBpmn = async () => {
  try {
    const { xml } = await bpmnModeler.value.saveXML({ format: true })
    const blob = new Blob([xml], { type: 'application/xml' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${processName.value || 'process'}.bpmn`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导入BPMN
const importBpmn = () => {
  fileInputRef.value.click()
}

// 处理文件导入
const handleFileImport = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const xml = await readFileAsText(file)
    
    // 验证XML格式
    if (!xml.trim().startsWith('<?xml')) {
      throw new Error('不是有效的XML文件格式')
    }
    
    // 检查是否包含BPMN内容
    if (!xml.includes('definitions') && !xml.includes('bpmn:definitions')) {
      throw new Error('不是有效的BPMN文件')
    }
    
    // 清空当前画布并导入新的BPMN
    await bpmnModeler.value.importXML(xml)
    
    // 自动调整视图
    const canvas = bpmnModeler.value.get('canvas')
    canvas.zoom('fit-viewport')
    
    ElMessage.success('BPMN文件导入成功')
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败: ' + (error.message || '请检查文件格式'))
  }
  
  // 清空input值，允许重复选择同一文件
  event.target.value = ''
}

// 读取文件为文本
const readFileAsText = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => resolve(e.target.result)
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsText(file, 'UTF-8')
  })
}

// 清空画布
const clearCanvas = () => {
  ElMessageBox.confirm('确定要清空画布吗？此操作不可恢复。', '确认清空', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    bpmnModeler.value.importXML(defaultBpmnXml)
    ElMessage.success('画布已清空')
  }).catch(() => {
    // 用户取消
  })
}

// 打开表单配置
const openFormConfig = () => {
  if (!selectedElement.value || !isUserTask(selectedElement.value)) {
    ElMessage.warning('请先选择一个用户任务节点')
    return
  }
  formConfigVisible.value = true
}

// 判断是否为用户任务
const isUserTask = (element) => {
  return element && element.businessObject && element.businessObject.$type === 'bpmn:UserTask'
}

// 处理自定义属性面板的元素更新事件
const onElementUpdated = (element) => {
  // 当属性面板更新元素时，触发BPMN建模器的重新渲染
  if (bpmnModeler.value) {
    const eventBus = bpmnModeler.value.get('eventBus')
    eventBus.fire('element.changed', { element })
  }
}

// 保存表单配置
const saveFormConfig = async (config) => {
  if (!selectedElement.value) return
  
  try {
    const modeling = bpmnModeler.value.get('modeling')
    
    // 使用toRaw获取原始元素，完全避免Vue响应式包装
    const element = toRaw(selectedElement.value)
    
    // 只在BPMN中设置formKey，便于后续识别
    modeling.updateProperties(element, {
      formKey: config.formKey
    })
    
    // 构建完整的配置数据，包含模板信息
    const configData = {
      formKey: config.formKey,
      formName: config.formName,
      fields: config.fields,
      mode: config.mode || 'manual'
    }
    
    // 如果是模板模式，包含模板ID
    if (config.mode === 'template' && config.templateId) {
      configData.templateId = config.templateId
    }
    
    // 如果是编辑模式，保存表单配置到数据库
    if (isEditMode.value && editProcessDefinitionId.value) {
      const formConfigJson = JSON.stringify(configData)
      
      await processDefinitionApi.saveFormConfig(editProcessDefinitionId.value, formConfigJson)
      ElMessage.success('表单配置已保存到数据库')
    } else {
      // 新建模式，暂存到sessionStorage，等流程保存时一起处理
      const formConfigs = JSON.parse(sessionStorage.getItem('tempFormConfigs') || '{}')
      formConfigs[config.formKey] = configData
      sessionStorage.setItem('tempFormConfigs', JSON.stringify(formConfigs))
      ElMessage.success('表单配置已暂存，保存流程时将一起保存')
    }
  } catch (error) {
    console.error('保存表单配置失败:', error)
    ElMessage.error('保存表单配置失败: ' + error.message)
  }
}

// 组件挂载
onMounted(() => {
  initBpmnModeler()
})

// 组件卸载
onUnmounted(() => {
  if (bpmnModeler.value) {
    bpmnModeler.value.destroy()
  }
})
</script>

<style scoped>
.process-designer {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.designer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  gap: 10px;
}

.header-right {
  display: flex;
  align-items: center;
}

.designer-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.designer-canvas {
  flex: 1;
  background: white;
  position: relative;
}

/* BPMN.js 样式覆盖 */
:deep(.bjs-container) {
  height: 100% !important;
}

:deep(.djs-palette) {
  left: 20px !important;
  top: 20px !important;
  z-index: 100;
}

:deep(.djs-context-pad) {
  display: block !important;
}

/* 工具栏样式 */
:deep(.djs-palette .djs-palette-entries) {
  padding: 10px 5px;
}

:deep(.djs-palette .entry) {
  margin-bottom: 5px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

:deep(.djs-palette .entry:hover) {
  background-color: #f0f9ff;
}
</style>