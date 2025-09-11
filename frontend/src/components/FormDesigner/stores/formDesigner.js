import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

export const useFormDesignerStore = defineStore('formDesigner', () => {
  // 当前拖拽的组件类型
  const draggedComponent = ref(null)
  // 当前选中组件c
  const selectedComponent = ref(null)
  
  // 设计器配置
  const designerConfig = reactive({
    // 当前预览模式：edit | preview
    mode: 'edit',
    // 当前设备类型：desktop | tablet | mobile
    device: 'desktop',
  })
  
  // 历史记录
  const history = reactive({
    records: [],
    currentIndex: -1,
    maxSize: 50
  })
  
  // 设置拖拽组件
  const setDraggedComponent = (componentType) => {
    draggedComponent.value = componentType
  }
  
  // 清除拖拽组件
  const clearDraggedComponent = () => {
    draggedComponent.value = null
  }
  
  // 切换模式
  const setMode = (mode) => {
    designerConfig.mode = mode
  }
  
  // 切换设备
  const setDevice = (device) => {
    designerConfig.device = device
  }
  
  // 设置选中组件
  const setSelectedComponent = (component) => {
    selectedComponent.value = component
  }
  
  // 添加历史记录
  const addHistory = (formConfig) => {
    // 移除当前索引之后的记录
    if (history.currentIndex < history.records.length - 1) {
      history.records.splice(history.currentIndex + 1)
    }
    
    // 添加新记录
    history.records.push(JSON.parse(JSON.stringify(formConfig)))
    
    // 限制历史记录数量
    if (history.records.length > history.maxSize) {
      history.records.shift()
    } else {
      history.currentIndex++
    }
  }
  
  // 撤销
  const undo = () => {
    if (history.currentIndex > 0) {
      history.currentIndex--
      return JSON.parse(JSON.stringify(history.records[history.currentIndex]))
    }
    return null
  }
  
  // 重做
  const redo = () => {
    if (history.currentIndex < history.records.length - 1) {
      history.currentIndex++
      return JSON.parse(JSON.stringify(history.records[history.currentIndex]))
    }
    return null
  }
  
  // 检查是否可以撤销
  const canUndo = () => {
    return history.currentIndex > 0
  }
  
  // 检查是否可以重做
  const canRedo = () => {
    return history.currentIndex < history.records.length - 1
  }
  
  return {
    // 状态
    draggedComponent,
    designerConfig,
    history,
    selectedComponent,
    
    // 方法
    setSelectedComponent,
    setDraggedComponent,
    clearDraggedComponent,
    setMode,
    setDevice,
    addHistory,
    undo,
    redo,
    canUndo,
    canRedo
  }
})