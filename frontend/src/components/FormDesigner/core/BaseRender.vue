<template>
    <component v-if="isLayout" style="width: 100%;" 
            :is="getComponentType(cfg)" 
            v-model="formData[cfg.field]"
            :placeholder="cfg.placeholder" 
            :disabled="disabled" 
            :cfg="cfg"
            v-bind="getComponentProps(cfg)" />

    <el-form-item v-else  :label="cfg.label" :prop="cfg.field" :required="cfg.required">
        <component style="width: 100%;" 
            :is="getComponentType(cfg)" 
            v-model="formData[cfg.field]"
            :placeholder="cfg.placeholder" 
            :disabled="disabled" 
            v-bind="getComponentProps(cfg)" />
    </el-form-item>
</template>

<script setup>

import { ref, reactive, computed, watch, toRaw, nextTick } from 'vue'
import { ComponentManager } from './ComponentManager'
import { createModelToggleComposable } from 'element-plus'

const props = defineProps({
    formData: { type: Object, required: true },
    computedVars: { type: Object, required: true },
    cfg: { type: Object, required: true },
    disabled: Boolean,
})

const isLayout = computed(() => {
  const config = ComponentManager.getComponentConfig(props.cfg.cmpType)
  return config?.category=="layout";
})

const getComponentType = (component) => {
  const config = ComponentManager.getComponentConfig(component.cmpType)
  const typeMap = {
    'input': 'el-input', 'textarea': 'el-input', 'number': 'el-input-number',
    'select': 'el-select', 'radio': 'el-radio-group', 'checkbox': 'el-checkbox',
    'switch': 'el-switch', 'date': 'el-date-picker', 'time': 'el-time-picker',
    'datetime': 'el-date-picker'
  }

  const cmp = config?.runComponent || config?.componentName || typeMap[component.cmpType] || 'el-'+component.cmpType
  return cmp
}

const getComponentProps = (component) => {
  let componentProps = { ...toRaw( component) }
  const excludeProps = ['span','offset','visible', 'field', 'label', 'required', 'span', 
    'offset', 'visible', 'disabled', 'defaultValue', 'rules','components','children']
  excludeProps.forEach(prop => delete componentProps[prop])
  componentProps = {
    ...componentProps, 
    formData: props.formData, 
    computedVars: props.computedVars,
  }
  return componentProps
}


</script>