<template>
  <el-form-item
    v-if="isVisible"
    class="compact-form-item"
    :label="config.label"
    :required="config.required"
    :prop="config.field"
    :label-width="labelWidth"
  >
    <slot>
      <component
        :is="componentType"
        v-if="!isContainer"
        v-model="componentValue"
        :placeholder="config.placeholder"
        :disabled="isDisabled"
        v-bind="componentProps"
      />
    </slot>
  </el-form-item>
</template>

<script setup>
import { computed, inject } from 'vue';
import { useFormDesignerStore } from '../stores/formDesigner';
import { ExpressionEvaluator } from './ExpressionEvaluator';

const props = defineProps({
  config: {
    cmpType: Object,
    required: true,
  },
  modelValue: {
    type: [String, Number, Boolean, Array, Object],
    default: undefined,
  },
});

const emit = defineEmits(['update:modelValue']);

const formConfig = inject('formConfig');
const previewModel = inject('previewModel');
const evaluatedState = inject('evaluatedState');
const store = useFormDesignerStore();
const currentMode = computed(() => store.designerConfig.mode);

const componentValue = computed({
  get: () => {
    if (currentMode.value === 'preview' && props.config.field && evaluatedState?.value) {
      // 从包含赋值结果的最终模型中读取值
      return evaluatedState.value.model[props.config.field];
    }
    // 在编辑模式下，返回设计时的值
    return props.modelValue;
  },
  set: (val) => {
    if (currentMode.value === 'preview' && props.config.field) {
      // 将用户输入写回原始数据模型，触发重新求值
      previewModel.value[props.config.field] = val;
    } else {
      // 在编辑模式下，更新组件配置
      emit('update:modelValue', val);
    }
  },
});

const isContainer = computed(() => {
  const containerTypes = ['RowContainer', 'TabsComponent'];
  return containerTypes.includes(props.config.cmpType);
});

const componentType = computed(() => {
  // 根据 config.cmpType 动态解析组件
  // 这是一个简化的例子，您可能需要一个更复杂的解析逻辑
  return `el-${props.config.cmpType.toLowerCase().replace('component', '')}`;
});

const componentProps = computed(() => {
  // 过滤掉 BaseComponent 已经处理的 props
  const { label, required, field, ...rest } = props.config;
  return rest;
});

const labelWidth = computed(() => {
    if (props.config.labelWidth) {
        return `${props.config.labelWidth}px`;
    }
    if (formConfig?.value?.labelWidth) {
        return `${formConfig.value.labelWidth}px`;
    }
    return undefined;
});

const isVisible = computed(() => {
  if (currentMode.value === 'preview' && evaluatedState?.value) {
    const visible = ExpressionEvaluator.evaluateProperty(
      props.config.visible,
      evaluatedState.value.model,
      evaluatedState.value.computedValues
    );
    // 如果表达式结果为 undefined 或空字符串，则默认为 true
    return visible === undefined || visible === '' ? true : !!visible;
  }
  return true; // 在编辑模式下总是可见
});

const isDisabled = computed(() => {
  if (currentMode.value === 'preview' && evaluatedState?.value) {
    const disabled = ExpressionEvaluator.evaluateProperty(
      props.config.disabled,
      evaluatedState.value.model,
      evaluatedState.value.computedValues
    );
    return !!disabled;
  }
  // 在编辑模式下，我们仍然尊重组件本身的 disabled 开关
  return props.config.disabled === true;
});

</script>

<style scoped>
.compact-form-item {
  margin-bottom: 0;
}
</style>