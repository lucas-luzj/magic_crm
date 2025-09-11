<template>
  <div class="form-fields-config">
    <div class="config-header">
      <span class="title">表单字段配置</span>
      <el-button type="primary" size="small" @click="addField">
        <el-icon><Plus /></el-icon>
        添加字段
      </el-button>
    </div>
    
    <div class="fields-list" v-if="fields.length > 0">
      <div 
        v-for="(field, index) in fields" 
        :key="field.id || index"
        class="field-item"
      >
        <div class="field-header">
          <span class="field-title">字段 {{ index + 1 }}</span>
          <el-button 
            type="danger" 
            size="small" 
            text 
            @click="removeField(index)"
          >
            删除
          </el-button>
        </div>
        
        <div class="field-config">
          <el-form :model="field" label-width="80px" size="small">
            <el-form-item label="字段ID">
              <el-input 
                v-model="field.id" 
                placeholder="fieldId"
                @input="updateFields"
              />
            </el-form-item>
            
            <el-form-item label="字段标签">
              <el-input 
                v-model="field.label" 
                placeholder="字段显示名称"
                @input="updateFields"
              />
            </el-form-item>
            
            <el-form-item label="字段类型">
              <el-select 
                v-model="field.type" 
                placeholder="选择类型"
                @change="updateFields"
              >
                <el-option label="文本输入" value="text" />
                <el-option label="多行文本" value="textarea" />
                <el-option label="数字" value="number" />
                <el-option label="日期" value="date" />
                <el-option label="日期时间" value="datetime" />
                <el-option label="下拉选择" value="select" />
                <el-option label="单选" value="radio" />
                <el-option label="多选" value="checkbox" />
                <el-option label="文件上传" value="file" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="是否必填">
              <el-switch 
                v-model="field.required" 
                @change="updateFields"
              />
            </el-form-item>
            
            <el-form-item label="占位符">
              <el-input 
                v-model="field.placeholder" 
                placeholder="请输入..."
                @input="updateFields"
              />
            </el-form-item>
            
            <el-form-item 
              label="选项配置" 
              v-if="['select', 'radio', 'checkbox'].includes(field.type)"
            >
              <el-input 
                v-model="field.options" 
                type="textarea" 
                :rows="3"
                placeholder="每行一个选项，格式：值|显示文本"
                @input="updateFields"
              />
            </el-form-item>
            
            <el-form-item label="默认值">
              <el-input 
                v-model="field.defaultValue" 
                placeholder="默认值"
                @input="updateFields"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <p>暂无字段配置，点击"添加字段"开始配置</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const fields = ref([...props.modelValue])

// 监听外部数据变化
watch(() => props.modelValue, (newValue) => {
  fields.value = [...newValue]
}, { deep: true })

// 添加字段
const addField = () => {
  const newField = {
    id: `field_${Date.now()}`,
    label: '',
    type: 'text',
    required: false,
    placeholder: '',
    options: '',
    defaultValue: '',
    readonly: false,
    visible: true
  }
  fields.value.push(newField)
  updateFields()
}

// 删除字段
const removeField = (index) => {
  fields.value.splice(index, 1)
  updateFields()
}

// 更新字段数据
const updateFields = () => {
  emit('update:modelValue', [...fields.value])
}
</script>

<style scoped>
.form-fields-config {
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.fields-list {
  max-height: 500px;
  overflow-y: auto;
}

.field-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 12px;
}

.field-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 6px 6px 0 0;
}

.field-title {
  font-weight: 500;
  color: #606266;
  font-size: 13px;
}

.field-config {
  padding: 16px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 滚动条样式 */
.fields-list::-webkit-scrollbar {
  width: 6px;
}

.fields-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.fields-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.fields-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>