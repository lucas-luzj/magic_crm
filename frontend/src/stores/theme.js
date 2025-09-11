import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    isDark: false,
    primaryColor: '#409EFF',
    sidebarCollapsed: false,
    language: 'zh-cn'
  }),

  actions: {
    toggleTheme() {
      this.isDark = !this.isDark
      this.applyTheme()
    },

    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    setPrimaryColor(color) {
      this.primaryColor = color
      this.applyTheme()
    },

    applyTheme() {
      const html = document.documentElement
      if (this.isDark) {
        html.classList.add('dark')
      } else {
        html.classList.remove('dark')
      }
      
      // 设置主题色
      html.style.setProperty('--el-color-primary', this.primaryColor)
      
      // 保存到本地存储
      localStorage.setItem('theme-settings', JSON.stringify({
        isDark: this.isDark,
        primaryColor: this.primaryColor,
        sidebarCollapsed: this.sidebarCollapsed
      }))
    },

    initTheme() {
      const saved = localStorage.getItem('theme-settings')
      if (saved) {
        const settings = JSON.parse(saved)
        this.isDark = settings.isDark || false
        this.primaryColor = settings.primaryColor || '#409EFF'
        this.sidebarCollapsed = settings.sidebarCollapsed || false
      }
      this.applyTheme()
    }
  }
})