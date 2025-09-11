<template>
  <BaseComponent :config="config" :model-value="modelValue" @update:model-value="handleUpdate">
    <DynamicField 
      :config="config" 
      :model-value="modelValue"
      :mode="mode"
      :component-name="componentName"
      @update:model-value="handleUpdate"
    />
  </BaseComponent>
</template>

<script setup>
import { computed } from 'vue';
import BaseComponent from './BaseComponent.vue';
import DynamicField from './DynamicField.vue';

const props = defineProps({
  config: { cmpType: Object, required: true },
  mode: { type: String, default: 'edit' },
  modelValue: { type: [String, Number, Boolean, Array, Object], default: undefined }
});

const componentName = computed(() => {
    return props.config.componentName || `el-${props.config.cmpType}`;
});

const emit = defineEmits(['update:modelValue']);

const handleUpdate = (value) => {
  emit('update:modelValue', value);
};
</script>