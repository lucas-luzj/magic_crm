<template>
  <div class="tabs-editor">
    <div v-for="(tab, index) in tabs" :key="index" class="tab-item">
      <div class="tab-header">
        <el-input 
          v-model="tab.label" 
          placeholder="标签名称" 
          @input="handleChange" 
          size="small" 
        />
        <el-button 
          @click="removeTab(index)" 
          type="danger" 
          size="small" 
          text
          :disabled="tabs.length <= 1"
        >
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
      <div class="tab-content">
        <el-input 
          v-model="tab.description" 
          placeholder="标签描述（可选）" 
          @input="handleChange"
          size="small" 
        />
      </div>
    </div>
    
    <el-button 
      @click="addTab" 
      type="primary" 
      size="small" 
      text
    >
      <el-icon><Plus /></el-icon>
      添加标签页
    </el-button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

// Props & Emits
const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

// Reactive Data
const tabs = ref([])

// Watch for prop changes
watch(() => props.modelValue, (newValue) => {
  tabs.value = newValue ? [...newValue] : []
}, { immediate: true, deep: true })

// Event Handlers
const handleChange = () => {
  emit('update:modelValue', [...tabs.value])
}

const addTab = () => {
  tabs.value.push({
    label: `标签${tabs.value.length + 1}`,
    description: '',
    children: []
  })
  handleChange()
}

const removeTab = (index) => {
  if (tabs.value.length > 1) {
    tabs.value.splice(index, 1)
    handleChange()
  }
}
</script>

<style lang="scss" scoped>
.tabs-editor {
  .tab-item {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 12px;
    margin-bottom: 12px;
    background-color: #fafafa;

    .tab-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .el-input {
        flex: 1;
      }
    }

    .tab-content {
      .el-input {
        width: 100%;
      }
    }
  }

  .tab-item:last-of-type {
    margin-bottom: 0;
  }
}
</style>