<template>
  <div class="component-library" :class="{ 'collapsed': collapsed }">
    <div class="library-header">
        <h3 v-show="!collapsed">组件库</h3>
        <el-button 
          :icon="collapsed ? Expand : Fold" 
          size="small" 
          text 
          @click="emit('update:collapsed', !collapsed)" 
        />
    </div>
    
    <div v-show="!collapsed" class="library-body">
      <div class="library-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索组件..."
          size="small"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="library-content">
        <div 
          v-for="category in filteredCategories" 
          :key="category.name"
          class="component-category"
        >
          <div 
            class="category-header"
            @click="toggleCategory(category.name)"
          >
            <el-icon class="category-icon">
              <ArrowRight v-if="!category.expanded" />
              <ArrowDown v-else />
            </el-icon>
            <span class="category-title">{{ category.title }}</span>
            <span class="category-count">({{ category.components.length }})</span>
          </div>
          
          <div 
            v-show="category.expanded"
            class="category-components"
          >
            <draggable
              :list="category.components"
              :group="{ name: 'components', pull: 'clone', put: false }"
              :sort="false"
              :clone="cloneComponent"
              item-key="type"
              @start="handleDragStart"
              @end="handleDragEnd"
            >
              <template #item="{ element: component }">
                <div class="component-item">
                  <div class="component-icon">
                    <el-icon>
                      <component :is="component.icon" />
                    </el-icon>
                  </div>
                  <div class="component-info">
                    <div class="component-name">{{ component.name }}</div>
                    <div class="component-desc">{{ component.description }}</div>
                  </div>
                </div>
              </template>
            </draggable>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ArrowRight, ArrowDown, Fold, Expand } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { ComponentManager } from '../core/ComponentManager'
import { generateId } from '../utils/common'

defineProps({
  collapsed: Boolean
})

const emit = defineEmits(['update:collapsed', 'drag-start'])

// Reactive Data
const searchKeyword = ref('')
const categories = ref([])

// 初始化组件库
onMounted(() => {
  categories.value = ComponentManager.getCategories()
  // 默认展开第一个分类
  if (categories.value.length > 0) {
    categories.value[0].expanded = true
  }
})

// 过滤后的分类
const filteredCategories = computed(() => {
  if (!searchKeyword.value) {
    return categories.value
  }
  
  return categories.value.map(category => ({
    ...category,
    components: category.components.filter(component =>
      component.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      component.description.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  })).filter(category => category.components.length > 0)
})

// 切换分类展开状态
const toggleCategory = (categoryName) => {
  const category = categories.value.find(cat => cat.name === categoryName)
  if (category) {
    category.expanded = !category.expanded
  }
}

// 克隆组件（用于拖拽）
const cloneComponent = (component) => {
  const componentConfig = ComponentManager.createComponent(component.cmpType)
  if (componentConfig) {
    componentConfig.id = generateId()
    return componentConfig
  }
  return null
}

// 处理拖拽开始
const handleDragStart = (event) => {
  emit('drag-start', event)
}

// 处理拖拽结束
const handleDragEnd = (event) => {
  if (event.to !== event.from) {
    // ElMessage.success('组件添加成功')
  }
}

</script>

<style lang="scss" scoped>
.component-library {
  height: 100%;
  display: flex;
  flex-direction: column;
  
  &.collapsed {
    width: 40px !important;
    
    .library-header {
      padding: 0 8px;
      justify-content: center;
      
      h3 {
        display: none;
      }
    }
  }
  .library-header {
    padding: 0 8px 0 16px;
    height: 48px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
    
    .header-left h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }

  .library-body {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  
  .library-search {
    padding: 16px 16px 0;
  }
  
  .library-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
  }
  
  .component-category {
    margin-bottom: 8px;
    
    .category-header {
      display: flex;
      align-items: center;
      padding: 8px;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.2s ease;
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      .category-icon {
        margin-right: 4px;
        color: #909399;
        transition: all 0.2s ease;
      }
      
      .category-title {
        flex: 1;
        font-weight: 500;
        color: #303133;
      }
      
      .category-count {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .category-components {
      padding-left: 16px;
    }
  }
  
  .component-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin-bottom: 8px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: grab;
    transition: all 0.2s ease;
    background: white;
    
    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
      transform: translateY(-1px);
    }
    
    &:active {
      cursor: grabbing;
    }
    
    .component-icon {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      border-radius: 4px;
      margin-right: 12px;
      color: #409eff;
    }
    
    .component-info {
      flex: 1;
      
      .component-name {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 2px;
      }
      
      .component-desc {
        font-size: 12px;
        color: #909399;
        line-height: 1.2;
      }
    }
  }
  
}
</style>