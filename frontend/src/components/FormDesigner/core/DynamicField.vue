<template>
  <component
    :is="componentName"
    v-if="mode === 'preview'"
    v-model="componentValue"
    :placeholder="config.placeholder"
    :disabled="config.disabled"
    v-bind="componentProps"
  />
  <component
    :is="componentName"
    v-else
    :model-value="designModeValue"
    :placeholder="config.placeholder"
    :disabled="true"
    v-bind="componentProps"
    class="design-mode"
  />
</template>

<script setup>
import { ref, watch, computed } from 'vue';

const props = defineProps({
  config: { type: Object, required: true },
  mode: { type: String, default: 'edit' },
  modelValue: { type: [String, Number, Boolean, Array, Object], default: undefined },
  componentName: { type: String, required: true }
});

const emit = defineEmits(['update:modelValue']);

const componentValue = ref(props.modelValue);

watch(() => props.modelValue, (newVal) => {
  componentValue.value = newVal;
});

watch(componentValue, (newVal) => {
    emit('update:modelValue', newVal);
});

const designModeValue = computed(() => {
  if (props.componentName === 'el-input-number') {
    return undefined;
  }
  return props.config.placeholder;
});

const componentProps = computed(() => {
  const { label, required, field, labelWidth, placeholder, disabled, ...rest } = props.config;
  return rest;
});
</script>

<style lang="scss" scoped>
.design-mode {
  width: 100%;
  pointer-events: none;
}
.el-select.design-mode, .el-date-picker.design-mode {
    width: 100%;
}
</style>