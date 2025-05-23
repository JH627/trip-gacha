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
        },
        {
          path: 'board/:id',
          name: 'boardDetail',
          component: BoardDetailView,
          props: true,
        },
        {
          path: 'board/write',
          name: 'boardWrite',
          component: BoardWriteView,
        },
        {
          path: 'mypage',
          name: 'mypage',
          component: MyPageView,
        },
        {
          path: 'spot',
          name: 'spot',
          component: SpotListView,
        },
        {
          path: 'schedule',
          name: 'schedule',
          component: ScheduleListView,
        },
        {
          path: 'schedule/:scheduleId',
          name: 'scheduleDetail',
          component: ScheduleDetailView,
          props: true,
        },
        {
          path: 'game',
          name: 'GameView',
          component: GameView,
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
        },
        {
          path: 'room/:roomId',
          name: 'room',
          component: () => import('../views/RoomView.vue'),
        },
      ],
    },
  ],
})

export default router
