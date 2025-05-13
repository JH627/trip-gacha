import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import EmptyLayout from '@/layouts/EmptyLayout.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'defaultLayout',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: WelcomeView
        }
      ]
    },
    {
      path: '/',
      name: 'emptyLayout',
      component: EmptyLayout,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginView
        }
      ]
    }
  ]
})

export default router
