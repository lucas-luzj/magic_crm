<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// 初始化主题
onMounted(() => {
  themeStore.initTheme()
})
</script>

<style>
#app {
  height: 100vh;
  overflow: hidden;
}
</style>