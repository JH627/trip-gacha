import './assets/main.css'
import 'ant-design-vue/dist/reset.css'

import Antd from 'ant-design-vue'
import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import pinia from './stores'
import { useKakao } from 'vue3-kakao-maps/@utils'

const app = createApp(App)

useKakao(import.meta.env.VITE_KAKAO_MAP_API_KEY)
app.use(pinia)
app.use(Antd)
app.use(router)
app.mount('#app')
