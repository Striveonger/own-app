import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import * as path from 'path'
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    css: {
        preprocessorOptions: {
            scss: {
                silenceDeprecations: ["legacy-js-api"],
                api: 'modern-compiler' // or "modern"
            }
        }
    },
    base: '/own/',
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
        }
    },
    server: {
        open: false,
        port: 9527,
        host: '127.0.0.1',
        proxy: {
            '/own/api': {
                target: 'http://127.0.0.1',
                changeOrigin: true,
                // secure: false,
                // rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
})
