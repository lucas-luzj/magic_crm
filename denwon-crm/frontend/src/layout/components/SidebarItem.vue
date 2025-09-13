<template>
  <div v-if="!item.meta?.hidden">
    <!-- 有子菜单 -->
    <el-sub-menu v-if="hasChildren" :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
    
    <!-- 无子菜单 -->
    <el-menu-item v-else :index="resolvePath(item.path)">
      <el-icon v-if="item.meta?.icon || (item.children && item.children[0]?.meta?.icon)">
        <component :is="item.meta?.icon || item.children[0]?.meta?.icon" />
      </el-icon>
      <template #title>
        {{ item.meta?.title || (item.children && item.children[0]?.meta?.title) }}
      </template>
    </el-menu-item>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { isExternal } from '@/utils/validate'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

// 判断是否有可显示的子菜单
const hasChildren = computed(() => {
  const children = props.item.children || []
  const showingChildren = children.filter(item => !item.meta?.hidden)
  
  if (showingChildren.length === 1) {
    // 只有一个子菜单时，直接显示子菜单
    return false
  }
  
  return showingChildren.length > 0
})

// 解析路径
const resolvePath = (routePath) => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  
  // 处理路径
  const basePath = props.basePath.endsWith('/') ? props.basePath : props.basePath + '/'
  const path = routePath.startsWith('/') ? routePath.slice(1) : routePath
  
  return basePath + path
}
</script>