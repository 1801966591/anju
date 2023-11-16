import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 9001, // 设置端口号，根据你的需求更改
    open: true
  },
  define: {

  }
})

