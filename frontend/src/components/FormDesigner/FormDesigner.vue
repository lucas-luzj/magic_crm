<template>
  <div class="form-designer">
    <!-- 主体内容区域 -->
    <div class="form-designer__body">
      <!-- 左侧组件面板 -->
      <div class="form-designer__sidebar-left" :class="{ 'is-collapsed': leftPanelCollapsed }">
        <ComponentLibrary 
          :collapsed="leftPanelCollapsed" 
          @update:collapsed="leftPanelCollapsed = $event"
          @drag-start="handleDragStart" 
        />
      </div>

      <!-- 中间设计画布 -->
      <div class="form-designer__main">
        <div class="main-toolbar">
          <div class="main-toolbar__left">
            <el-space>
              <el-radio-group v-model="currentMode" size="small">
                <el-radio-button label="edit">
                  <el-icon><Monitor /></el-icon>
                  <span>编辑</span>
                </el-radio-button>
                <el-radio-button label="preview">
                  <el-icon><View /></el-icon>
                  <span>预览</span>
                </el-radio-button>
              </el-radio-group>

              <el-radio-group v-model="currentDevice" size="small">
                <el-radio-button label="desktop">
                  <el-icon><Monitor /></el-icon>
                </el-radio-button>
                <el-radio-button label="tablet">
                  <el-icon><Iphone /></el-icon>
                </el-radio-button>
                <el-radio-button label="mobile">
                  <el-icon><Cellphone /></el-icon>
                </el-radio-button>
              </el-radio-group>
            </el-space>
          </div>
          <div class="main-toolbar__right">
            <el-space>
                <el-button @click="handleClear" type="danger" size="small" text>
                    <el-icon><Delete /></el-icon>
                    <span>清空</span>
                </el-button>
                <el-divider direction="vertical" />
                <el-button @click="handleImport" size="small" text>
                  <el-icon><Upload /></el-icon>
                  <span>导入</span>
                </el-button>
                <el-button @click="handleExport" size="small" text>
                  <el-icon><Download /></el-icon>
                  <span>导出</span>
                </el-button>
                <el-button @click="handleSave" size="small" type="primary">
                  <el-icon><Document /></el-icon>
                  <span>保存</span>
                </el-button>
            </el-space>
          </div>
        </div>
        <DesignCanvas v-model="formConfig" />
      </div>

      <!-- 右侧属性面板 -->
      <div class="form-designer__sidebar-right" :class="{ 'is-collapsed': rightPanelCollapsed }">
        <PropertyPanel 
          :collapsed="rightPanelCollapsed" 
          @update:collapsed="rightPanelCollapsed = !rightPanelCollapsed"
          :form-config="formConfig"
          @update-component="handleUpdateComponent" 
          @update-form="handleUpdateForm" 
        />
      </div>
    </div>

    <!-- 导入文件对话框 -->
    <input ref="fileInput" type="file" accept=".json" style="display: none" @change="handleFileImport" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Monitor, Iphone, Cellphone, Delete, Document, Download, Upload, View } from '@element-plus/icons-vue'
import ComponentLibrary from './components/ComponentLibrary.vue'
import DesignCanvas from './components/DesignCanvas.vue'
import PropertyPanel from './components/PropertyPanel.vue'
import { useFormDesignerStore } from './stores/formDesigner'
// 导入组件注册
import './components/form-components/index.js'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({
      title: '新建表单',
      name: 'new-form',
      description: '',
      components: []
    })
  }
})

const emit = defineEmits(['update:modelValue', 'save', 'export'])

const store = useFormDesignerStore()
const fileInput = ref(null)

// 面板折叠状态
const leftPanelCollapsed = ref(false)
const rightPanelCollapsed = ref(false)

// 表单配置
const formConfig = ref({ ...props.modelValue })

// 选中的组件
const selectedComponent = computed(() => store.selectedComponent)

const currentMode = computed({
  get() {
    return store.designerConfig.mode
  },
  set(newVal) {
    store.setMode(newVal)
  }
})

const currentDevice = computed({
  get() {
    return store.designerConfig.device
  },
  set(newVal) {
    store.setDevice(newVal)
  }
})

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  Object.assign(formConfig.value, newVal)
}, { deep: true })

// 监听formConfig变化，同步到父组件
watch(formConfig, (newVal) => {
  emit('update:modelValue', { ...newVal })
}, { deep: true })



// 处理拖拽开始
const handleDragStart = (componentType) => {
  store.setDraggedComponent(componentType)
}

// 处理组件选择
const handleSelectComponent = (component) => {
  store.setSelectedComponent(component)
}

// 处理组件放置
const handleDropComponent = (componentData) => {
  if (!formConfig.value.components) {
    formConfig.value.components = []
  }
  formConfig.value.components.push(componentData)
  store.setSelectedComponent(componentData)
}

// 处理组件更新
const handleUpdateComponent = (updatedComponent) => {
  if (store.selectedComponent) {
    Object.assign(store.selectedComponent, updatedComponent)
  }
}

// 递归查找组件
const findComponentById = (components, id) => {
  for (const component of components) {
    if (component.id === id) {
      return component
    }
    // 检查容器组件的子组件
    if (component.children && component.children.length > 0) {
      const found = findComponentById(component.children, id)
      if (found) return found
    }
    // 检查标签页组件的子组件
    if (component.tabs && component.tabs.length > 0) {
      for (const tab of component.tabs) {
        if (tab.children && tab.children.length > 0) {
          const found = findComponentById(tab.children, id)
          if (found) return found
        }
      }
    }
  }
  return null
}

// 处理表单更新
const handleUpdateForm = (updatedForm) => {
  Object.assign(formConfig.value, updatedForm)
}

// 保存
const handleSave = () => {
  emit('save', { ...formConfig.value })
  ElMessage.success('保存成功')
}

// 导出
const handleExport = () => {
  try {
    const jsonString = JSON.stringify(formConfig.value, null, 2);
    const blob = new Blob([jsonString], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${formConfig.value.name || 'form-config'}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('Export failed:', error);
    ElMessage.error('导出失败');
  }
}

// 导入
const handleImport = () => {
  fileInput.value?.click()
}

// 处理文件导入
const handleFileImport = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const config = JSON.parse(e.target.result)
      Object.assign(formConfig.value, config)
      store.setSelectedComponent(null)
      ElMessage.success('导入成功')
    } catch (error) {
      ElMessage.error('文件格式错误')
    }
  }
  reader.readAsText(file)

  // 清空input值，允许重复导入同一文件
  event.target.value = ''
}

// 清空画布
const handleClear = async () => {
  if (!formConfig.value.components.length) return

  await ElMessageBox.confirm('确定要清空所有组件吗？', '提示', { type: 'warning' })

  formConfig.value.components = []
  store.setSelectedComponent(null)
}
</script>

<style lang="scss" scoped>
.form-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #F5F7FA;

  &__body {
    flex: 1;
    display: flex;
    overflow: hidden;
  }

  &__sidebar-left {
    width: 280px;
    background: white;
    border-right: 1px solid #DCDFE6;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    overflow: hidden;
    display: flex;
    flex-direction: column;

    &.is-collapsed {
      width: 49px; // 留出按钮位置
    }
  }

  &__main {
    flex: 1;
    background: #F5F7FA;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    .main-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 16px;
      height: 48px;
      background: white;
      border-bottom: 1px solid #e0e0e0;
      flex-shrink: 0;
    }
  }

  &__sidebar-right {
    width: 320px;
    background: white;
    border-left: 1px solid #DCDFE6;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    overflow: hidden;
    display: flex;
    flex-direction: column;

    &.is-collapsed {
      width: 49px; // 留出按钮位置
    }
  }
}
</style>