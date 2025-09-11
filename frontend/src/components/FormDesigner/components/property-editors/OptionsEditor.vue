<template>
  <div class="options-editor">
    <div v-for="(option, index) in options" :key="index" class="option-item">
      <el-input 
        v-model="option.label" 
        placeholder="选项文本" 
        @input="handleChange" 
      />
      <el-input 
        v-model="option.value" 
        placeholder="选项值" 
        @input="handleChange" 
      />
      <el-button 
        @click="removeOption(index)" 
        type="danger" 
        size="small" 
        text
      >
        <el-icon><Delete /></el-icon>
      </el-button>
    </div>
    
    <el-button 
      @click="addOption" 
      type="primary" 
      size="small" 
      text
    >
      <el-icon><Plus /></el-icon>
      添加选项
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
const options = ref([])

// Watch for prop changes
watch(() => props.modelValue, (newValue) => {
  options.value = newValue ? [...newValue] : []
}, { immediate: true, deep: true })

// Event Handlers
const handleChange = () => {
  emit('update:modelValue', [...options.value])
}

const addOption = () => {
  options.value.push({
    label: `选项${options.value.length + 1}`,
    value: `option${options.value.length + 1}`
  })
  handleChange()
}

const removeOption = (index) => {
  options.value.splice(index, 1)
  handleChange()
}
</script>

<style lang="scss" scoped>
.options-editor {
  .option-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .el-input {
      flex: 1;
    }
  }
}
</style>