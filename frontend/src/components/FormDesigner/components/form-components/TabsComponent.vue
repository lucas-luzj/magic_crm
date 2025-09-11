<template>
  <div class="tabs-component">
    <div class="tabs-header">
      <div v-for="(tab, index) in tabs" :key="index" :class="['tab-item', { active: activeTab === index }]"
        @click="activeTab = index">
        {{ tab.label || `标签 ${index + 1}` }}
      </div>
    </div>
    <div class="tabs-content">
      <div v-for="(tab, index) in tabs" :key="index" :class="['tab-panel', { active: activeTab === index }]">
        <BaseContainer 
          :model-value="tab.children" 
          @update:model-value="updateTabChildren(index, $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import BaseContainer from '../../core/BaseContainer.vue'

const props = defineProps({
  config: {
    cmpType: Object,
    default: () => ({})
  },
  mode: {
    type: String,
    default: 'edit'
  }
})

const emit = defineEmits(['update'])

const activeTab = ref(0)

const tabs = computed({
  get: () => {
    const configTabs = props.config.tabs || [
      { label: '标签1', children: [] },
      { label: '标签2', children: [] },
      { label: '标签3', children: [] }
    ]
    // 确保每个标签页都有children数组
    return configTabs.map(tab => ({
      ...tab,
      children: tab.children || []
    }))
  },
  set: (value) => {
    emit('update', { ...props.config, tabs: value })
  }
})

// 更新指定标签页的子组件
const updateTabChildren = (tabIndex, children) => {
  const newTabs = [...tabs.value]
  newTabs[tabIndex] = {
    ...newTabs[tabIndex],
    children: children
  }
  tabs.value = newTabs
}

</script>

<style scoped>
.tabs-component {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.tabs-header {
  display: flex;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.tab-item {
  padding: 12px 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item:hover {
  background-color: #e4e7ed;
}

.tab-item.active {
  border-bottom-color: #409eff;
  color: #409eff;
  font-weight: 500;
}

.tabs-content {
  padding: 16px;
  min-height: 100px;
}

.tab-panel {
  display: none;
}

.tab-panel.active {
  display: block;
}
</style>