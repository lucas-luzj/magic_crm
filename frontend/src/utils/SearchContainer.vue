<template>
  <div class="search-container">
    <div class="search-wrapper">
      <div ref="searchItemsRef" class="search-items" :class="{ 'expanded': showExpanded }">
        <slot />
      </div>
      
      <div class="search-actions">
        <el-button 
          type="text" 
          @click="toggleExpanded"
          v-if="needToggleButton"
          class="toggle-btn"
        >
          {{ showExpanded ? '收起' : '更多' }}
          <el-icon>
            <ArrowUp v-if="showExpanded" />
            <ArrowDown v-else />
          </el-icon>
        </el-button>
        <el-button type="primary" @click="handleSearch" class="search-btn" :loading="loading">
          <el-icon>
            <Search />
          </el-icon>
          搜索
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { Search, ArrowUp, ArrowDown } from '@element-plus/icons-vue'

const props = withDefaults(defineProps({
  loading: {
    type: Boolean,
    default: false
  }
}), {
  loading: false
})

const emit = defineEmits(['search'])

// 搜索展开状态
const showExpanded = ref(false)
// 是否需要显示切换按钮
const needToggleButton = ref(false)
// 搜索项容器引用
const searchItemsRef = ref()

// 切换展开状态
const toggleExpanded = () => {
  showExpanded.value = !showExpanded.value
}

// 处理搜索
const handleSearch = () => {
  emit('search')
}

// 检查是否需要显示切换按钮
const checkNeedToggle = () => {
  if (!searchItemsRef.value) return
  
  nextTick(() => {
    const container = searchItemsRef.value
    // 临时展开以获取真实高度
    const wasExpanded = showExpanded.value
    showExpanded.value = true
    
    nextTick(() => {
      const scrollHeight = container.scrollHeight
      const clientHeight = 40 // 单行高度
      
      // 如果内容高度超过单行高度，则需要显示切换按钮
      needToggleButton.value = scrollHeight > clientHeight + 10 // 10px 容差
      
      // 恢复原来的展开状态，如果不需要切换按钮则保持展开
      if (needToggleButton.value) {
        showExpanded.value = wasExpanded
      } else {
        showExpanded.value = true // 不需要切换时保持展开
      }
    })
  })
}

// 监听窗口大小变化
const handleResize = () => {
  checkNeedToggle()
}

onMounted(() => {
  // 延迟检查，确保DOM完全渲染和插槽内容加载完成
  setTimeout(() => {
    checkNeedToggle()
  }, 200)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 暴露方法给父组件
defineExpose({
  toggleExpanded,
  showExpanded,
  checkNeedToggle
})
</script>

<style scoped>
/* 搜索容器样式 */
.search-container {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.search-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

.search-items {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  overflow: hidden;
  transition: all 0.3s ease;
  max-height: 40px; /* 单行高度 */
}

.search-items.expanded {
  max-height: none; /* 展开时不限制高度 */
}

.search-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
  height: 40px; /* 与搜索项高度一致 */
}

.toggle-btn {
  color: #409eff;
}

.toggle-btn:hover {
  color: #66b1ff;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-items {
    max-height: 80px; /* 小屏幕时允许两行 */
  }
}

@media (max-width: 768px) {
  .search-items {
    max-height: none; /* 移动端直接展开 */
  }
  
  .search-wrapper {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-actions {
    justify-content: flex-end;
    width: 100%;
  }
}
</style>