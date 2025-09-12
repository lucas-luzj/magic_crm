/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#ecf5ff',
          100: '#d9ecff',
          200: '#b3d8ff',
          300: '#85c1ff',
          400: '#5aa3ff',
          500: '#409eff',
          600: '#3a8ee6',
          700: '#337ecc',
          800: '#2d6db3',
          900: '#265d99',
        },
        success: '#67c23a',
        warning: '#e6a23c',
        danger: '#f56c6c',
        info: '#909399'
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', 'Helvetica', 'Arial', 'sans-serif']
      }
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false // 禁用Tailwind的基础样式重置，避免与Element Plus冲突
  }
}