<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="validationRules"
    :label-position="config.labelPosition || 'left'"
    :label-width="labelWidth"
    :size="config.size || 'default'"
    class="form-renderer"
  >
    <RuntimeRow :form-data="formData" 
      :computedVars="computedVariables" 
      :cfg="config"
      :disabled="disabled"
      />
    
  </el-form>
</template>

<script setup>
import { ref, reactive, computed, watch, toRaw, nextTick } from 'vue'
import { ComponentManager } from './core/ComponentManager'
import RuntimeRow from './components/runtime/RuntimeRow.vue'
// 导入组件注册
import './components/form-components/index.js'

// --- Props & Emits ---
const props = defineProps({
  config: {
    type: Object,
    required: true,
    default: () => ({
      fields:[],
      components: [],
      computedVariables: [],
      expressions: []
    })
  },
  modelValue: {
    type: Object,
    default: () => ({})
  },
  disabled: Boolean
})
const emit = defineEmits(['update:modelValue', 'validate-success', 'validate-error'])

// --- State ---
const formRef = ref()
const formData = reactive({})
const isUpdatingInternally = ref(false) // The single flag to prevent reactive loops

// --- Core Reactive Functions ---

// Calculated variables (e.g., isAdult)
const computedVariables = reactive({})
const createComputedVariables = () => {
  Object.keys(computedVariables).forEach(key => delete computedVariables[key])
  ;(props.config.computedVariables || []).forEach(variable => {
    if (variable.name && variable.expression) {
      computedVariables[variable.name] = computed(() => {
        try {
          const func = new Function('model', `return ${variable.expression}`)
          return func(formData)
        } catch (error) {
          console.error(`Error in computed variable '${variable.name}':`, error)
          return undefined
        }
      })
    }
  })
}

// Assignment expressions (e.g., displayName = name + ' (VIP)')
const runAssignments = () => {
  const computedValues = {}
  Object.keys(computedVariables).forEach(key => {
    computedValues[key] = computedVariables[key].value
  })

  ;(props.config.expressions || []).forEach(expr => {
    if (expr.target && expr.value) {
      try {
        const func = new Function('model', 'computed', `return ${expr.value}`)
        const result = func(formData, computedValues)
        if (result !== undefined && formData[expr.target] !== result) {
          formData[expr.target] = result
        }
      } catch (error) {
        console.error(`Error in assignment expression for '${expr.target}':`, error)
      }
    }
  })
}

// --- Initialization ---

const getDefaultValueByType = (type) => {
  const config = ComponentManager.getComponentConfig(type)
  if (config && config.defaultValue !== undefined) return config.defaultValue
  const typeDefaults = {
    'input': '', 'textarea': '', 'input-number': 0, 'number': 0,
    'select': null, 'radio-group': null, 'radio': null, 'checkbox-group': [],
    'checkbox': false, 'switch': false, 'date-picker': null, 'date': null,
    'time': null, 'datetime': null, 'tabs': null, 'row': null
  }
  return typeDefaults[type] !== undefined ? typeDefaults[type] : ''
}


const initializeForm = () => {
  isUpdatingInternally.value = true

  createComputedVariables()
  Object.keys(formData).forEach(key => delete formData[key])
  Object.assign(formData, props.modelValue)

  ;(props.config.components || []).forEach(component => {
    if (component.field && !(component.field in formData)) {
      formData[component.field] = component.defaultValue !== undefined
        ? component.defaultValue
        : getDefaultValueByType(component.type)
    }
  })

  runAssignments()

  nextTick(() => {
    isUpdatingInternally.value = false
  })
}

// --- Watchers for Orchestration ---

watch(() => props.config, initializeForm, { deep: true, immediate: true })

watch(() => props.modelValue, (newValue) => {
  if (isUpdatingInternally.value) return
  if (JSON.stringify(newValue) === JSON.stringify(toRaw(formData))) return

  isUpdatingInternally.value = true
  Object.keys(formData).forEach(key => delete formData[key])
  Object.assign(formData, newValue)
  runAssignments()
  nextTick(() => {
    isUpdatingInternally.value = false
  })
}, { deep: true })

watch(formData, () => {
  if (isUpdatingInternally.value) return

  isUpdatingInternally.value = true
  runAssignments()
  emit('update:modelValue', toRaw(formData))
  nextTick(() => {
    isUpdatingInternally.value = false
  })
}, { deep: true })


// --- UI Computed Properties ---

const labelWidth = computed(() => {
  const width = props.config.labelWidth || 100
  return typeof width === 'number' ? `${width}px` : width
})

const children = computed(() => {
  return (props.config.components || [])
})


const validationRules = computed(() => {
  const rules = {}
  children.value.forEach(component => {
    if (component.field) {
      const fieldRules = []
      if (component.required) {
        fieldRules.push({ required: true, message: `${component.label}不能为空`, trigger: ['blur', 'change'] })
      }
      if (component.rules && Array.isArray(component.rules)) {
        fieldRules.push(...component.rules)
      }
      if (fieldRules.length > 0) {
        rules[component.field] = fieldRules
      }
    }
  })
  return rules
})

const getComponentType = (component) => {
  const config = ComponentManager.getComponentConfig(component.type)
  if (config && config.componentName) return config.componentName
  const typeMap = {
    'input': 'el-input', 'textarea': 'el-input', 'number': 'el-input-number',
    'select': 'el-select', 'radio': 'el-radio-group', 'checkbox': 'el-checkbox',
    'switch': 'el-switch', 'date': 'el-date-picker', 'time': 'el-time-picker',
    'datetime': 'el-date-picker'
  }
  return typeMap[component.type] || 'el-input'
}

const getComponentProps = (component) => {
  const componentProps = { ...component }
  const excludeProps = ['type', 'field', 'label', 'required', 'span', 'offset', 'visible', 'disabled', 'defaultValue', 'rules']
  excludeProps.forEach(prop => delete componentProps[prop])
  if (component.type === 'textarea') {
    componentProps.type = 'textarea'
    componentProps.rows = component.rows || 3
  }
  if (component.type === 'datetime') {
    componentProps.type = 'datetime'
  }
  return componentProps
}


// --- Exposed Methods ---

const validate = async () => {
  if (!formRef.value) throw new Error('Form reference does not exist')
  try {
    await formRef.value.validate()
    const data = toRaw(formData)
    emit('validate-success', data)
    return true
  } catch (error) {
    emit('validate-error', error)
    throw error
  }
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
  initializeForm()
}

const clearValidate = (props) => {
  if (formRef.value) formRef.value.clearValidate(props)
}

const getFormData = () => {
  return toRaw(formData)
}

const setFormData = (data) => {
  isUpdatingInternally.value = true
  Object.keys(formData).forEach(key => delete formData[key])
  Object.assign(formData, data)
  runAssignments()
  nextTick(() => {
    isUpdatingInternally.value = false
  })
}

const save = async () => {
  await validate()
  const data = getFormData()
  emit('update:modelValue', data)
  return data
}



defineExpose({
  computedVariables,
  validate,
  resetForm,
  clearValidate,
  getFormData,
  setFormData,
  save,
})
</script>

<style lang="scss" scoped>
.form-renderer {
  .el-form-item {
    margin-bottom: 18px;
  }
}
</style>