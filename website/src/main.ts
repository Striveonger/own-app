// js
import { createApp } from 'vue';
import { createPinia } from 'pinia'
// import "bootstrap";
import ElementPlus from 'element-plus'


import App from '@/App.vue';
// import router from '@/router';
// css
import "@/style.scss";
import 'animate.css';
import 'element-plus/dist/index.css'


// app
const pinia = createPinia();
const app = createApp(App).use(pinia).use(ElementPlus);
app.mount('#app');

