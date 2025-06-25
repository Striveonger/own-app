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
    server: {
        open: false,
        port: 9527,
        host: '10.13.147.1',
        proxy: {
            '/api': {
                target: 'http://10.13.147.1:18081',
                changeOrigin: true,
                // secure: false,
                // rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
        }
    }
})
