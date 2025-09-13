import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref(false)
  
  // 设备类型
  const device = ref('desktop')
  
  // 缓存的视图
  const cachedViews = ref([])
  
  // 访问过的视图
  const visitedViews = ref([])
  
  // 主题设置
  const theme = ref({
    primaryColor: '#409eff',
    size: 'default',
    showTagsView: true,
    showBreadcrumb: true,
    fixedHeader: true,
    sidebarLogo: true
  })
  
  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  // 设置设备类型
  function setDevice(type) {
    device.value = type
  }
  
  // 添加缓存视图
  function addCachedView(view) {
    if (cachedViews.value.includes(view.name)) return
    if (!view.meta?.noCache) {
      cachedViews.value.push(view.name)
    }
  }
  
  // 删除缓存视图
  function delCachedView(view) {
    const index = cachedViews.value.indexOf(view.name)
    if (index > -1) {
      cachedViews.value.splice(index, 1)
    }
  }
  
  // 添加访问视图
  function addVisitedView(view) {
    if (visitedViews.value.some(v => v.path === view.path)) return
    visitedViews.value.push({
      name: view.name,
      path: view.path,
      title: view.meta?.title || 'no-name',
      meta: view.meta
    })
  }
  
  // 删除访问视图
  function delVisitedView(view) {
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index > -1) {
      visitedViews.value.splice(index, 1)
    }
  }
  
  // 更新主题设置
  function updateTheme(settings) {
    Object.assign(theme.value, settings)
  }
  
  return {
    sidebarCollapsed,
    device,
    cachedViews,
    visitedViews,
    theme,
    toggleSidebar,
    setDevice,
    addCachedView,
    delCachedView,
    addVisitedView,
    delVisitedView,
    updateTheme
  }
})