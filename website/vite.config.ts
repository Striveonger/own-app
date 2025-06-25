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
        host: '0.0.0.0',
        proxy: {

        }
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
        }
    }
})
