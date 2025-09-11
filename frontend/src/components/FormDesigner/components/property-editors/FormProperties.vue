<template>
  <div class="form-properties">
    <el-collapse v-model="activeNames">
      <el-collapse-item title="基础信息" name="basic">
        <div class="property-section">
          <el-form :model="formProps" label-width="80px" size="small">
            <el-form-item label="表单标题">
              <el-input v-model="formProps.title" @input="handleChange" />
            </el-form-item>

            <el-form-item label="表单名称">
              <el-input v-model="formProps.name" @input="handleChange" />
            </el-form-item>

            <el-form-item label="表单描述">
              <el-input 
                v-model="formProps.description" 
                type="textarea" 
                :rows="3" 
                @input="handleChange" 
              />
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <el-collapse-item title="布局设置" name="layout">
        <div class="property-section">
          <el-form :model="formProps" label-width="80px" size="small">
            <el-form-item label="标签位置">
              <el-select v-model="formProps.labelPosition" @change="handleChange">
                <el-option label="顶部" value="top" />
                <el-option label="左侧" value="left" />
                <el-option label="右侧" value="right" />
              </el-select>
            </el-form-item>

            <el-form-item label="标签宽度">
              <el-input-number 
                v-model="formProps.labelWidth" 
                :min="0" 
                :max="300" 
                @change="handleChange" 
              />
            </el-form-item>

            <el-form-item label="组件尺寸">
              <el-select v-model="formProps.size" @change="handleChange">
                <el-option label="大" value="large" />
                <el-option label="默认" value="default" />
                <el-option label="小" value="small" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-item>

      <el-collapse-item title="计算变量" name="computedVars">
        <div class="property-section">
          <div v-for="(variable, index) in formProps.computedVariables" :key="index" class="expression-item">
            <el-form :model="variable" label-width="80px" size="small" class="expression-form">
              <el-form-item label="变量名">
                <el-input v-model="variable.name" placeholder="e.g., allowEdit" @input="handleChange" />
              </el-form-item>
              <el-form-item label="表达式">
                <ExpressionEditor 
                  v-model="variable.expression"
                  :form-config="formConfig"
                  placeholder="e.g., model.status === 'draft'"
                  @update:model-value="handleChange"
                />
              </el-form-item>
            </el-form>
            <el-button 
              type="danger" 
              icon="el-icon-delete" 
              circle 
              size="small" 
              class="delete-btn"
              @click="removeComputedVariable(index)"
            />
          </div>
          <el-button type="primary" plain size="small" @click="addComputedVariable">
            <el-icon><plus /></el-icon>
            <span>添加计算变量</span>
          </el-button>
        </div>
      </el-collapse-item>

      <el-collapse-item title="动作/赋值" name="assignments">
        <div class="property-section">
          <div v-for="(expr, index) in formProps.expressions" :key="index" class="expression-item">
            <el-form :model="expr" label-width="80px" size="small" class="expression-form">
              <el-form-item label="目标属性">
                <el-input v-model="expr.target" placeholder="组件ID.属性 (e.g., component1.value)" @input="handleChange" />
              </el-form-item>
              <el-form-item label="赋值表达式">
                <ExpressionEditor 
                  v-model="expr.value"
                  :form-config="formConfig"
                  placeholder="e.g., model.firstName + ' ' + model.lastName"
                  @update:model-value="handleChange"
                />
              </el-form-item>
            </el-form>
            <el-button 
              type="danger" 
              icon="el-icon-delete" 
              circle 
              size="small" 
              class="delete-btn"
              @click="removeExpression(index)"
            />
          </div>
          <el-button type="primary" plain size="small" @click="addExpression">
            <el-icon><plus /></el-icon>
            <span>添加动作</span>
          </el-button>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import ExpressionEditor from '../common/ExpressionEditor.vue'

// Collapse panel state
const activeNames = ref(['basic', 'layout', 'computedVars', 'assignments'])

// Props & Emits
const props = defineProps({
  formConfig: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update'])

// Reactive Data
const formProps = reactive({
  title: '',
  name: '',
  description: '',
  labelPosition: 'left',
  labelWidth: 100,
  size: 'default',
  expressions: [],
  computedVariables: []
})

// Watch for form config changes
watch(() => props.formConfig, (newConfig) => {
  Object.assign(formProps, {
    title: newConfig.title || '',
    name: newConfig.name || '',
    description: newConfig.description || '',
    labelPosition: newConfig.labelPosition || 'left',
    labelWidth: newConfig.labelWidth || 100,
    size: newConfig.size || 'default',
    expressions: newConfig.expressions || [],
    computedVariables: newConfig.computedVariables || []
  })
}, { immediate: true, deep: true })

// Event Handlers
const handleChange = () => {
  emit('update', { ...formProps })
}

const addExpression = () => {
  if (!formProps.expressions) {
    formProps.expressions = []
  }
  formProps.expressions.push({ target: '', value: '' })
  handleChange()
}

const removeExpression = (index) => {
  formProps.expressions.splice(index, 1)
  handleChange()
}

const addComputedVariable = () => {
  if (!formProps.computedVariables) {
    formProps.computedVariables = []
  }
  formProps.computedVariables.push({ name: '', expression: '' })
  handleChange()
}

const removeComputedVariable = (index) => {
  formProps.computedVariables.splice(index, 1)
  handleChange()
}
</script>

<style lang="scss" scoped>

</style>