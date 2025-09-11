<template>
  <div class="expression-editor">
    <el-input
      ref="inputRef"
      :model-value="modelValue"
      :placeholder="placeholder"
      @update:model-value="emit('update:modelValue', $event)"
    >
      <template #append>
        <el-popover
          placement="bottom-end"
          :width="300"
          trigger="click"
        >
          <template #reference>
            <el-button :icon="MagicStick" />
          </template>
          <div class="variable-selector">
            <div v-if="formFields.length > 0" class="variable-category">
              <div class="category-title">表单字段</div>
              <ul class="variable-list">
                <li
                  v-for="field in formFields"
                  :key="field.value"
                  @click="insertVariable(field.value)"
                >
                  {{ field.label }}
                </li>
              </ul>
            </div>
            <div v-if="computedVariables.length > 0" class="variable-category">
              <div class="category-title">计算变量</div>
              <ul class="variable-list">
                <li
                  v-for="variable in computedVariables"
                  :key="variable.value"
                  @click="insertVariable(variable.value)"
                >
                  {{ variable.label }}
                </li>
              </ul>
            </div>
            <el-empty v-if="formFields.length === 0 && computedVariables.length === 0" description="无可用变量" :image-size="60" />
          </div>
        </el-popover>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue';
import { MagicStick } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: '输入表达式',
  },
  formConfig: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const inputRef = ref(null);

// 从表单配置中提取所有可用字段
const formFields = computed(() => {
  const fields = [];
  const traverse = (components) => {
    if (!components) return;
    components.forEach(comp => {
      if (comp.field) {
        fields.push({
          label: `${comp.label || comp.name} (${comp.field})`,
          value: `model.${comp.field}`,
        });
      }
      if (comp.children) traverse(comp.children);
      if (comp.tabs) comp.tabs.forEach(tab => traverse(tab.children));
    });
  };
  traverse(props.formConfig.components);
  return fields;
});

// 从表单配置中提取计算变量
const computedVariables = computed(() => {
  return (props.formConfig.computedVariables || [])
    .filter(v => v.name)
    .map(v => ({
      label: v.name,
      value: v.name,
    }));
});

// 插入变量到输入框
const insertVariable = (text) => {
  const inputElement = inputRef.value.input;
  const start = inputElement.selectionStart;
  const end = inputElement.selectionEnd;
  const currentValue = props.modelValue || '';
  
  const newValue = currentValue.substring(0, start) + text + currentValue.substring(end);
  emit('update:modelValue', newValue);

  // 更新光标位置
  const newCursorPos = start + text.length;
  nextTick(() => {
    inputElement.focus();
    inputElement.setSelectionRange(newCursorPos, newCursorPos);
  });
};
</script>

<style lang="scss" scoped>
.variable-selector {
  max-height: 400px;
  overflow-y: auto;
}
.variable-category {
  margin-bottom: 16px;
  .category-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 8px;
  }
  .variable-list {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      padding: 6px 8px;
      cursor: pointer;
      border-radius: 4px;
      font-size: 13px;
      &:hover {
        background-color: #f5f7fa;
      }
    }
  }
}
</style>