import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import EmptyLayout from '@/layouts/EmptyLayout.vue'
import SocketLayout from '@/layouts/SocketLayout.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegistView from '@/views/RegistView.vue'
import BoardListView from '@/views/board/BoardListView.vue'
import BoardDetailView from '@/views/board/BoardDetailView.vue'
import BoardWriteView from '@/views/board/BoardWriteView.vue'
import MyPageView from '@/views/user/MyPageView.vue'
import SpotListView from '@/views/spot/SpotListView.vue'
import ScheduleListView from '@/views/schedule/ScheduleListView.vue'
import ScheduleDetailView from '@/views/schedule/ScheduleDetailView.vue'
import GameView from '@/views/GameView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'defaultLayout',
      component: DefaultLayout,
      children: [
        {
          path: '/',
          name: 'home',
          component: WelcomeView,
        },
        {
          path: 'login',
          name: 'login',
          component: LoginView,
        },
        {
          path: 'regist',
          name: 'regist',
          component: RegistView,
        },
        {
          path: 'board',
          name: 'board',
          component: BoardListView,
          meta: { requiresAuth: true }
        },
        {
          path: 'board/:id',
          name: 'boardDetail',
          component: BoardDetailView,
          props: true,
          meta: { requiresAuth: true }
        },
        {
          path: 'board/write',
          name: 'boardWrite',
          component: BoardWriteView,
          meta: { requiresAuth: true }
        },
        {
          path: 'mypage',
          name: 'mypage',
          component: MyPageView,
          meta: { requiresAuth: true }
        },
        {
          path: 'spot',
          name: 'spot',
          component: SpotListView,
          meta: { requiresAuth: true }
        },
        {
          path: 'schedule',
          name: 'schedule',
          component: ScheduleListView,
          meta: { requiresAuth: true }
        },
        {
          path: 'schedule/:scheduleId',
          name: 'scheduleDetail',
          component: ScheduleDetailView,
          props: true,
          meta: { requiresAuth: true }
        },
        {
          path: 'game',
          name: 'GameView',
          component: GameView,
          meta: { requiresAuth: true }
        },
      ],
    },
    {
      path: '/',
      name: 'emptyLayout',
      component: EmptyLayout,
      children: [],
    },
    {
      path: '/trip',
      name: 'socketLayout',
      component: SocketLayout,
      children: [
        {
          path: 'lobby',
          name: 'lobby',
          component: () => import('../views/LobbyView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'room/:roomId',
          name: 'room',
          component: () => import('../views/RoomView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'plan/:planId',
          name: 'plan',
          component: () => import('../views/PlanView.vue'),
          meta: { requiresAuth: true }
        },
      ],
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {
    const isLoggedIn = await authStore.checkLogin()
    
    if (!isLoggedIn) {
      // 로그인 페이지로 리다이렉트하면서 원래 가려던 페이지 정보를 저장
      next({
        name: 'login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  next()
})

export default router
