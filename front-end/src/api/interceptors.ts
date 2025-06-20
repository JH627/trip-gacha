import { authApi } from './axios'
import { useAuthStore } from '@/stores/auth'
import type { AxiosInstance } from 'axios'

export const setupInterceptors = (): void => {
  setupRequestInterceptor(authApi)
  setupResponseInterceptor(authApi)
}

const setupRequestInterceptor = (api: AxiosInstance) => {
  api.interceptors.request.use(
    (config) => {
      const authStore = useAuthStore()

      // store에 accesToken이 있는 경우
      if (authStore.accessToken) {
        // 헤더에 토큰 설정
        config.headers['Authorization'] = authStore.accessToken
      }

      return config
    },
    (error) => Promise.reject(error),
  )
}

const setupResponseInterceptor = (api: AxiosInstance) => {
  api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config
      const authStore = useAuthStore()

      // 401 에러이고 재시도하지 않은 요청일 경우
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true
        const errorStatus = error.response.data?.code

        if (errorStatus === '401') {
          console.log('액세스 토큰 만료, 리프레시 토큰으로 갱신 시도')

          try {
            // 서버에 토큰 갱신 요청 (리프레시 토큰은 쿠키에서 자동으로 전송됨)
            const response = await authApi.post('/auth/refresh-token')

            // 응답 헤더에서 새 액세스 토큰 추출
            const newToken = response.headers['authorization']

            if (newToken) {
              console.log('갱신 성공')
              authStore.accessToken = newToken

              // 원본 요청의 헤더 갱신
              originalRequest.headers['Authorization'] = newToken

              // 원본 요청 재시도
              return authApi(originalRequest)
            }
          } catch (refreshError) {
            console.log('갱신 실패')
            await authStore.logout()
            window.location.href = '/login'
            return Promise.reject(refreshError)
          }
        }
      }

      return Promise.reject(error)
    },
  )
}
