import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    // AutoImport({
    //   resolvers: [ElementPlusResolver()],
    //   imports: ['vue', 'vue-router', 'pinia'],
    //   dts: true
    // }),
    // Components({
    //   resolvers: [ElementPlusResolver()],
    //   dts: true
    // })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      'vue': 'vue/dist/vue.esm-bundler.js',
      'lucas-my-form': "E:\\MyProject\\my-form"
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace(/^\/api/, '/api')
          console.log("Proxy rewrite: " + path + " -> " + newPath)
          return newPath
        }
      },
      '/dm':{
        target: 'http://localhost:8080',
        changeOrigin: true,
         rewrite: (path) => {
          const newPath = path.replace(/^\/dm/, '/dm')
          console.log("Proxy rewrite: " + path + " -> " + newPath)
          return newPath
        }
      },
      "/uploads": { 
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace(/^\/uploads/, '/uploads')
          console.log("Proxy rewrite: " + path + " -> " + newPath)
          return newPath
        }
      }
    }
  }
})