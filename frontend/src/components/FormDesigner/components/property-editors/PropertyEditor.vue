<template>
  <div class="property-editor">
    <!-- 文本输入 -->
    <el-input 
      v-if="property.type === 'input'" 
      :model-value="value"
      :placeholder="property.placeholder" 
      @input="handleUpdate"
    />

    <!-- 数字输入 -->
    <el-input-number 
      v-else-if="property.type === 'number'" 
      :model-value="value" 
      :min="property.min"
      :max="property.max" 
      @change="handleUpdate"
    />

    <!-- 开关 -->
    <el-switch 
      v-else-if="property.type === 'switch'" 
      :model-value="value"
      @change="handleUpdate"
    />

    <!-- 选择器 -->
    <el-select 
      v-else-if="property.type === 'select'" 
      :model-value="value"
      @change="handleUpdate"
    >
      <el-option 
        v-for="option in property.options" 
        :key="option.value" 
        :label="option.label"
        :value="option.value" 
      />
    </el-select>

    <!-- 多行文本 -->
    <el-input 
      v-else-if="property.type === 'textarea'" 
      :model-value="value" 
      type="textarea"
      :rows="property.rows || 3" 
      @input="handleUpdate"
    />

    <!-- 选项列表编辑器 -->
    <OptionsEditor
      v-else-if="property.type === 'options'"
      :model-value="value"
      @update:model-value="handleUpdate"
    />

    <!-- 标签页配置编辑器 -->
    <TabsEditor
      v-else-if="property.type === 'tabs'"
      :model-value="value"
      @update:model-value="handleUpdate"
    />
  </div>
</template>

<script setup>
import OptionsEditor from './OptionsEditor.vue'
import TabsEditor from './TabsEditor.vue'

// Props & Emits
const props = defineProps({
  property: {
    type: Object,
    required: true
  },
  value: {
    type: [String, Number, Boolean, Array, Object],
    default: null
  }
})

const emit = defineEmits(['update'])

// Event Handlers
const handleUpdate = (newValue) => {
  emit('update', newValue)
}
</script>

<style lang="scss" scoped>
.property-editor {
  width: 100%;
}
</style>