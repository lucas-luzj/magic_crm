<template>
  <div class="design-canvas" @click="handleClick">
    <div class="canvas-container">
      <div 
        class="canvas-viewport" 
        :class="viewportClasses"
      >
        <el-form 
          :label-position="formConfig.labelPosition || 'left'" 
          :label-width="formLabelWidth" 
          :size="formConfig.size || 'default'"
        >
          <BaseContainer 
            :model-value="formComponents" 
            @update:model-value="updateComponents"
          />
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, provide, ref, watch, reactive } from 'vue'
import { useFormDesignerStore } from '../stores/formDesigner'
import { ExpressionEvaluator } from '../core/ExpressionEvaluator.js'
import BaseContainer from '../core/BaseContainer.vue'

// Props & Emits
const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:modelValue'])

// Store
const store = useFormDesignerStore()

// Computed Properties
const formConfig = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

provide('formConfig', formConfig)
const currentMode = computed(() => store.designerConfig.mode)
const currentDevice = computed(() => store.designerConfig.device)

const previewModel = ref({});
provide('previewModel', previewModel);

// 当进入预览模式时，构建初始数据模型
watch(currentMode, (newMode) => {
  if (newMode === 'preview') {
    const model = {};
    const traverse = (components) => {
      if (!components) return;
      components.forEach(comp => {
        if (comp.field) {
          model[comp.field] = comp.value !== undefined ? comp.value : comp.defaultValue;
        }
        if (comp.children) traverse(comp.children);
        if (comp.tabs) comp.tabs.forEach(tab => traverse(tab.children));
      });
    };
    traverse(formConfig.value.components);
    previewModel.value = reactive(model);
  }
}, { immediate: true });


// 创建一个包含所有求值结果的最终状态
const evaluatedState = computed(() => {
  if (currentMode.value !== 'preview') {
    return null;
  }
  
  const model = previewModel.value;
  const computedValues = ExpressionEvaluator.evaluateComputedVariables(
    model,
    formConfig.value.computedVariables
  );
  
  const finalModel = ExpressionEvaluator.evaluateAssignments(
    model,
    computedValues,
    formConfig.value.expressions
  );

  return {
    model: finalModel,
    computedValues,
  };
});
provide('evaluatedState', evaluatedState);

const formComponents = computed(() => formConfig.value.components || [])

const viewportClasses = computed(() => [
  `device-${currentDevice.value}`,
  { 'is-preview': currentMode.value === 'preview' }
])

const showFormHeader = computed(() => 
  currentMode.value === 'preview' && (formConfig.value.title || formConfig.value.description)
)

const formLabelWidth = computed(() => {
  const width = formConfig.value.labelWidth
  if (width === undefined || width === null) {
    return '100px'
  }
  return typeof width === 'number' ? `${width}px` : width
})

const handleClick = () => {
  if (currentMode.value === 'edit') {
    store.setSelectedComponent(null)
  }
} 

// Event Handlers
const updateComponents = (components) => {
  formConfig.value = {
    ...formConfig.value,
    components
  }
}
</script>

<style lang="scss" scoped>
.design-canvas {
  height: 100%;
  display: flex;
  flex-direction: column;

  .canvas-container {
    flex: 1;
    overflow: auto;
    padding: 24px;
    display: flex;
    justify-content: center;
  }

  .canvas-viewport {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.12);
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    min-height: 600px;
    padding: 24px;

    &.device-desktop {
      width: 100%;
    }

    &.device-tablet {
      width: 768px;
    }

    &.device-mobile {
      width: 375px;
    }

    &.is-preview {
      padding: 32px;
    }
  }

  .form-header {
    margin-bottom: 24px;
    text-align: center;

    .form-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 16px 0;
    }

    .form-description {
      font-size: 14px;
      color: #909399;
      margin: 0;
    }
  }
}
</style>