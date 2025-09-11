<template>
    <div class="tabs-component">
        <div class="tabs-header">
            <div v-for="(tab, index) in tabs" :key="index" :class="['tab-item', { active: activeTab === index }]"
                @click="activeTab = index">
                {{ tab.label || `标签 ${index + 1}` }}
            </div>
        </div>
        <div class="tabs-content">
            <div v-for="(tab, index) in tabs" :key="index" :class="['tab-panel', { active: activeTab === index }]">
                <RuntimeRow :form-data="formData" :computedVars="computedVars" 
                    :cfg="tab.children" :disabled="disabled" />
            </div>
        </div>
    </div>
</template>


<script setup>




import { computed, defineProps, ref } from 'vue'
import RuntimeRow from './RuntimeRow.vue';


const props = defineProps({
    formData: { type: Object, required: true },
    computedVars: { type: Object, required: true },
    cfg: { type: Object, required: true },
    disabled: Boolean,
});


const activeTab = ref(0)

const tabs = computed(() => {
    return props.cfg?.tabs || []
})

</script>


<style scoped>
.tabs-component {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}

.tabs-header {
    display: flex;
    background-color: #f5f7fa;
    border-bottom: 1px solid #dcdfe6;
}

.tab-item {
    padding: 12px 20px;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all 0.3s;
}

.tab-item:hover {
    background-color: #e4e7ed;
}

.tab-item.active {
    border-bottom-color: #409eff;
    color: #409eff;
    font-weight: 500;
}

.tabs-content {
    padding: 16px;
    min-height: 100px;
}

.tab-panel {
    display: none;
}

.tab-panel.active {
    display: block;
}
</style>