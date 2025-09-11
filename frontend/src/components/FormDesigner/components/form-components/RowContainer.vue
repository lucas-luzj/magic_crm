<template>
  <div class="row-container">
    <div class="row-header" v-if="mode === 'edit'">
      <span class="row-title">{{ config.label || '行容器' }}</span>
    </div>
    
    <BaseContainer 
      :model-value="children" 
      @update:model-value="updateChildren"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
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

// 子组件列表
const children = computed(() => {
  return props.config.children || []
})

// 更新子组件
const updateChildren = (newChildren) => {
  emit('update', { ...props.config, children: newChildren })
}

</script>

<style scoped>
.row-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.row-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.row-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}
</style>