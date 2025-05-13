import './assets/main.css'
import 'ant-design-vue/dist/reset.css'

import Antd from 'ant-design-vue'
import { createApp } from 'vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(Antd)
app.use(router)
app.mount('#app')
