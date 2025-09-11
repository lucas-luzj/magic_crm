<template>
  <div 
    class="base-container" 
    :class="{ 'is-dragging': isDragging }"
    @click.stop="handleCanvasClick"
  >
    <!-- 可拖拽的组件列表 -->
    <draggable
      v-model="items"
      :group="{ name: 'components', pull: true, put: true }"
      :animation="200"
      :ghost-class="'drag-ghost'"
      :chosen-class="'drag-chosen'"
      :drag-class="'drag-moving'"
      :swap-threshold="0.5"
      :empty-insert-threshold="20"
      item-key="id"
      tag="div"
      class="draggable-container"
      @start="handleDragStart"
      @end="handleDragEnd"
      @add="handleDragAdd"
      @change="handleDragChange"
    >
      <template #item="{ element: component, index }">
        <div 
          class="draggable-item"
          :class="{ 'is-selected': isSelected(component) }"
          @click.stop="handleComponentClick(component)"
        >
          <el-row :gutter="16">
            <el-col :span="component.span || 24" :offset="component.offset || 0">
              <div class="component-item" :class="{ 'draggable': currentMode === 'edit' }">
                <!-- 拖拽手柄 -->
                <div 
                  v-if="currentMode === 'edit'" 
                  class="drag-handle"
                  :title="'拖拽 ' + (component.label || component.name || '组件')"
                  @mousedown="handleDragHandleMouseDown"
                >
                  <el-icon><Rank /></el-icon>
                </div>

                <div class="component-wrapper">
                  <!-- 组件操作按钮 -->
                  <div 
                    v-if="showActions(component)" 
                    class="component-actions"
                  >
                    <el-button 
                      @click.stop="handleCopyComponent(component)" 
                      size="small" 
                      type="primary" 
                      text
                      title="复制组件"
                    >
                      <el-icon><CopyDocument /></el-icon>
                    </el-button>
                    <el-button 
                      @click.stop="handleDeleteComponent(component)" 
                      size="small" 
                      type="danger" 
                      text
                      title="删除组件"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>

                  <!-- 渲染组件 -->
                  <component 
                    :is="getComponentRenderer(component.cmpType)" 
                    :config="component" 
                    :mode="currentMode"
                    @update="handleComponentUpdate(component, $event)"
                  />
                </div>

                <!-- 拖拽插入指示器 -->
                <div class="drop-indicator drop-indicator-top"></div>
                <div class="drop-indicator drop-indicator-bottom"></div>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>

      <!-- 空状态插槽 -->
      <template #footer>
        <div v-if="isEmpty" class="empty-state">
          <el-icon class="empty-icon">
            <Plus />
          </el-icon>
          <p>拖拽组件到此处</p>
        </div>
      </template>
    </draggable>


  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Plus, CopyDocument, Delete, Rank } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { useFormDesignerStore } from '../stores/formDesigner'
import { ComponentManager } from './ComponentManager'
import { generateId } from '../utils/common'

// Props & Emits
const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

// Store
const store = useFormDesignerStore()

// Reactive Data
const isDragging = ref(false)

// Computed Properties
const items = computed({
  get: () => props.modelValue || [],
  set: (value) => {
    emit('update:modelValue', value)
  }
})

const currentMode = computed(() => store.designerConfig.mode)
const selectedComponent = computed(() => store.selectedComponent)
const isEmpty = computed(() => !items.value.length && currentMode.value === 'edit')

// Helper Functions
const isSelected = (component) => selectedComponent.value?.id === component.id
const showActions = (component) => currentMode.value === 'edit' && isSelected(component)

const getComponentRenderer = (componentType) => {
  return ComponentManager.getRenderer(componentType)
}

// Event Handlers
const handleCanvasClick = () => {
  store.setSelectedComponent(null)
}

const handleComponentClick = (component) => {
  store.setSelectedComponent(component)
}

const handleComponentUpdate = (component, updates) => {
  Object.assign(component, updates)
}

// Draggable Event Handlers
const handleDragStart = (event) => {
  isDragging.value = true
}

const handleDragEnd = (event) => {
  isDragging.value = false
  // 如果有移动，选中被移动的组件
  if (event.newIndex !== undefined && items.value[event.newIndex]) {
    store.setSelectedComponent(items.value[event.newIndex])
  }
}

const handleDragAdd = (event) => {
  const addedComponent = items.value[event.newIndex]
  if (addedComponent) {
    store.setSelectedComponent(addedComponent)
    ElMessage.success('组件添加成功')
  }
}

const handleDragChange = (event) => {
  // Can be used for logging or other logic
}

// 拖拽手柄鼠标按下事件
const handleDragHandleMouseDown = (event) => {
  // This can be used for special drag handle logic in the future
}

const handleCopyComponent = (component) => {
  const newComponent = JSON.parse(JSON.stringify(component))
  newComponent.id = generateId()
  newComponent.name = `${component.name}_copy`

  const components = [...items.value]
  const index = components.findIndex(c => c.id === component.id)
  components.splice(index + 1, 0, newComponent)

  items.value = components
  store.setSelectedComponent(newComponent)
  ElMessage.success('组件复制成功')
}

const handleDeleteComponent = async (component) => {
  try {
    await ElMessageBox.confirm('确定要删除这个组件吗？', '提示', {
      type: 'warning'
    })

    items.value = items.value.filter(c => c.id !== component.id)
    store.setSelectedComponent(null)
    ElMessage.success('组件删除成功')
  } catch {
    // 用户取消删除
  }
}
</script>

<style lang="scss" scoped>
.base-container {
  padding: 16px;
  min-height: 100px;
  position: relative;

  .draggable-container {
    min-height: 60px;
    width: 100%;
  }

  &.is-dragging {
    .draggable-item:not(.drag-chosen) {
      opacity: 0.6;
    }
  }

  .draggable-item {
    margin-bottom: 8px;
    position: relative;
    transition: all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);

    &.is-selected {
      .component-item {
        outline: 2px solid #409eff;
        outline-offset: 2px;
      }
    }

    &:hover {
      .component-item {
        background-color: rgba(64, 158, 255, 0.05);
      }

      .drag-handle {
        opacity: 1;
      }
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  .component-item {
    position: relative;
    border-radius: 4px;
    transition: all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
    background: white;
    border: 1px solid transparent;

    &.draggable {
      cursor: grab;
      
      &:active {
        cursor: grabbing;
      }
    }

    &:hover {
      border-color: #e4e7ed;
    }
  }

  // 拖拽手柄
  .drag-handle {
    position: absolute;
    left: -12px;
    top: 50%;
    transform: translateY(-50%);
    z-index: 15;
    cursor: grab;
    opacity: 0;
    transition: all 0.2s ease;
    background: #409eff;
    color: white;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);

    &:hover {
      background: #337ecc;
      transform: translateY(-50%) scale(1.1);
    }

    &:active {
      cursor: grabbing;
    }
  }

  .component-wrapper {
    position: relative;
    padding: 2px;

    .component-actions {
      position: absolute;
      top: -8px;
      right: -8px;
      z-index: 10;
      display: flex;
      gap: 4px;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
      padding: 4px;
    }
  }

  // 拖拽插入指示器
  .drop-indicator {
    position: absolute;
    left: 0;
    right: 0;
    height: 2px;
    background: #409eff;
    opacity: 0;
    transition: opacity 0.2s ease;
    z-index: 5;

    &.drop-indicator-top {
      top: -1px;
    }

    &.drop-indicator-bottom {
      bottom: -1px;
    }

    &::before {
      content: '';
      position: absolute;
      left: -4px;
      top: -2px;
      width: 8px;
      height: 6px;
      background: #409eff;
      border-radius: 50%;
    }

    &::after {
      content: '';
      position: absolute;
      right: -4px;
      top: -2px;
      width: 8px;
      height: 6px;
      background: #409eff;
      border-radius: 50%;
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: #909399;
    border: 2px dashed #e4e7ed;
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #409eff;
      color: #409eff;
    }

    .empty-icon {
      font-size: 48px;
      margin-bottom: 16px;
      color: #e4e7ed;
      transition: color 0.3s ease;
    }

    p {
      margin: 0;
      font-size: 16px;
    }

    &:hover .empty-icon {
      color: #409eff;
    }
  }
}

// 全局拖拽样式
:global(.drag-ghost) {
  opacity: 0.5;
  background: #409eff;
  border: 2px dashed #409eff;
  border-radius: 4px;
}

:global(.drag-chosen) {
  opacity: 0.8;
  // transform: rotate(2deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:global(.drag-moving) {
  opacity: 0.8;
  // transform: rotate(2deg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

:global(.drag-fallback) {
  opacity: 0.8;
  background: white;
  border: 2px solid #409eff;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

// 拖拽时显示插入指示器
:global(.sortable-drag) {
  .drop-indicator {
    opacity: 1;
  }
}
</style>