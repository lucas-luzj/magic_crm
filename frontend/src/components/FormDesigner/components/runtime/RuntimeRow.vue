<template>
    <el-row :gutter="10" class="runtime-row">
        <template v-for="child in children" :key="child.id || child.field">
            <el-col :span="child.span || 24" :offset="child.offset || 0" v-if="isVisible(child)">
                <BaseRender :formData="formData" :computedVars="computedVars"
                    :cfg="child" :disabled="isDisabled(child)" />
            </el-col>
        </template>
    </el-row>
</template>


<script setup>

import { computed, defineProps } from 'vue'
import BaseRender from '../../core/BaseRender.vue';



const props = defineProps({
    formData: { type: Object, required: true },
    computedVars: { type: Object, required: true },
    cfg: { type: Object, required: true },
    disabled: Boolean,
});

const children = computed(() => {
    if (Array.isArray(props.cfg)) return props.cfg;

    const its = props.cfg.children || props.cfg.components || props.cfg.fields || []
    console.log(its)
    return its;
})


const isVisible = (component) => {
    if (component.visible == undefined || component.visible == null || component.visible === true) return true;
    return props.computedVars[component.visible];
}

const isDisabled = (component) => {
    if (props.disabled) return true;

    if (component.disabled == undefined || component.disabled == null) return false;
    return props.computedVars[component.disabled];
}


</script>

<style scoped>
.runtime-row {
    margin: 8px;
}
</style>