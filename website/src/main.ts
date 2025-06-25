// js
import { createApp } from 'vue';
import { createPinia } from 'pinia'
import "bootstrap";
import 'animate.css';

import App from '@/App.vue';
// import router from '@/router';
// css
import "@/style.scss";

// app
const pinia = createPinia();
const app = createApp(App).use(pinia);
app.mount('#app');
