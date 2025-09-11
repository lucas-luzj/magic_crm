<template>
  <div class="property-panel" :class="{ 'collapsed': collapsed }">
    <div class="panel-header">
      <h3>{{ panelTitle }}</h3>
      <el-button 
          :icon="collapsed ? Fold : Expand" 
          size="small" 
          text 
          @click="emit('update:collapsed', !collapsed)" 
        />
    </div>

    <div v-if="!collapsed" class="panel-content">
      <!-- 组件属性编辑 -->
      <ComponentProperties 
        v-if="selectedComponent" 
        :component="selectedComponent"
        :form-config="formConfig"
        @update="handleComponentUpdate"
      />

      <!-- 表单属性编辑 -->
      <FormProperties 
        v-else 
        :form-config="formConfig"
        @update="handleFormUpdate"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormDesignerStore } from '../stores/formDesigner'
import ComponentProperties from './property-editors/ComponentProperties.vue'
import FormProperties from './property-editors/FormProperties.vue'
import { Fold, Expand } from '@element-plus/icons-vue'
// Props & Emits
const props = defineProps({
  formConfig: {
    type: Object,
    required: true
  },
  collapsed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update-component', 'update-form', 'update:collapsed'])

// Store
const store = useFormDesignerStore()

// Computed Properties
const selectedComponent = computed(() => store.selectedComponent)

const panelTitle = computed(() => 
  selectedComponent.value ? '组件属性' : '表单属性'
)

// Event Handlers
const handleComponentUpdate = (updates) => {
  emit('update-component', updates)
}

const handleFormUpdate = (updates) => {
  emit('update-form', updates)
}
</script>

<style lang="scss" scoped>
.property-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;

  &.collapsed {
    width: 40px !important;
    
    .panel-header {
      padding: 8px;
      justify-content: center;
      
      h3 {
        display: none;
      }
    }
  }

  .panel-header {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    background-color: #fafafa;
    display: flex;
    align-items: center;
    justify-content: space-between;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }

  .panel-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
  }
}
</style>