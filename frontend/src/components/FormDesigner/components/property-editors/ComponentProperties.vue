<template>
  <div class="component-properties">
    <el-collapse v-model="activeNames">
      <!-- 基础属性 -->
      <el-collapse-item title="基础属性" name="basic">
        <div class="property-section">
          <el-form :model="componentProps" label-width="80px" size="small">
            <el-form-item label="组件名称">
              <el-input v-model="componentProps.name" @input="handleChange" />
            </el-form-item>

            <el-form-item label="字段名">
              <el-input v-model="componentProps.field" @input="handleChange" />
            </el-form-item>

            <el-form-item label="标签文本">
              <el-input v-model="componentProps.label" @input="handleChange" />
            </el-form-item>

            <el-form-item label="占位符">
              <el-input v-model="componentProps.placeholder" @input="handleChange" />
            </el-form-item>

            <el-form-item label="是否必填">
              <el-switch v-model="componentProps.required" @change="handleChange" />
            </el-form-item>

            <el-form-item label="是否禁用">
              <el-switch v-model="componentProps.disabled" @change="handleChange" />
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <!-- 布局属性 -->
      <el-collapse-item title="布局属性" name="layout">
        <div class="property-section">
          <el-form :model="componentProps" label-width="80px" size="small">
            <el-form-item label="栅格占位">
              <el-slider 
                v-model="componentProps.span" 
                :min="1" 
                :max="24"
                :marks="{ 6: '6', 8: '8', 12: '12', 16: '16', 24: '24' }" 
                @change="handleChange" 
              />
            </el-form-item>

            <el-form-item label="栅格偏移">
              <el-slider 
                v-model="componentProps.offset" 
                :min="0" 
                :max="24"
                :marks="{ 0: '0', 6: '6', 12: '12', 18: '18', 24: '24' }" 
                @change="handleChange" 
              />
            </el-form-item>

            <el-form-item label="标签宽度">
              <el-input-number 
                v-model="componentProps.labelWidth" 
                :min="0" 
                :max="200"
                @change="handleChange" 
              />
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <!-- 专用属性 -->
      <el-collapse-item title="专用属性" name="special" v-if="dynamicProperties.length">
        <div class="property-section">
          <el-form :model="componentProps" label-width="80px" size="small">
            <el-form-item v-for="prop in dynamicProperties" :key="prop.key" :label="prop.label">
              <PropertyEditor 
                :property="prop"
                :value="componentProps[prop.key]"
                @update="handlePropertyUpdate(prop.key, $event)"
              />
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <!-- 显隐/禁用规则 -->
      <el-collapse-item title="显隐/禁用规则" name="rules">
        <div class="property-section">
          <el-form :model="componentProps" label-width="80px" size="small">
            <el-form-item label="是否可见">
              <el-select 
                v-model="componentProps.visible" 
                placeholder="选择计算变量或输入表达式"
                clearable 
                filterable 
                allow-create
                @change="handleChange"
              >
                <el-option 
                  v-for="variable in computedVariables"
                  :key="variable.name"
                  :label="`变量: ${variable.name}`"
                  :value="variable.name"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="是否禁用">
              <el-select 
                v-model="componentProps.disabled" 
                placeholder="选择计算变量或输入表达式"
                clearable 
                filterable 
                allow-create
                @change="handleChange"
              >
                <el-option 
                  v-for="variable in computedVariables"
                  :key="variable.name"
                  :label="`变量: ${variable.name}`"
                  :value="variable.name"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <!-- 验证规则 -->
      <el-collapse-item title="验证规则" name="validation" v-if="showValidation">
        <div class="property-section">
          <el-form :model="componentProps" label-width="80px" size="small">
            <el-form-item label="最小长度">
              <el-input-number v-model="componentProps.minLength" :min="0" @change="handleChange" />
            </el-form-item>

            <el-form-item label="最大长度">
              <el-input-number v-model="componentProps.maxLength" :min="0" @change="handleChange" />
            </el-form-item>

            <el-form-item label="正则表达式">
              <el-input v-model="componentProps.pattern" placeholder="如：^[0-9]+$" @input="handleChange" />
            </el-form-item>

            <el-form-item label="错误提示">
              <el-input v-model="componentProps.errorMessage" @input="handleChange" />
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ComponentManager } from '../../core/ComponentManager'
import PropertyEditor from './PropertyEditor.vue'

// Props & Emits
const props = defineProps({
  component: {
    cmpType: Object,
    required: true
  },
  formConfig: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update'])

// Reactive Data
const activeNames = ref(['basic', 'layout', 'rules'])
const componentProps = reactive({})

// Computed Properties
const componentConfig = computed(() => 
  ComponentManager.getComponentConfig(props.component.cmpType)
)

const dynamicProperties = computed(() => 
  componentConfig.value?.properties || []
)

const showValidation = computed(() => 
  componentConfig.value?.category === 'basic'
)

const computedVariables = computed(() => 
  props.formConfig.computedVariables || []
)

// Watch for component changes
watch(() => props.component, (newComponent) => {
  if (newComponent) {
    // 清空现有属性
    Object.keys(componentProps).forEach(key => {
      delete componentProps[key]
    })
    // 复制新组件属性
    Object.assign(componentProps, newComponent)
  }
}, { immediate: true, deep: true })

// Event Handlers
const handleChange = () => {
  emit('update', { ...componentProps })
}

const handlePropertyUpdate = (key, value) => {
  componentProps[key] = value
  handleChange()
}
</script>

<style lang="scss" scoped>
.component-properties {
  .property-section {
    margin-bottom: 16px;

    :deep(.el-form-item) {
      margin-bottom: 16px;

      .el-form-item__label {
        font-size: 12px;
        color: #606266;
      }
    }
  }
}
</style>