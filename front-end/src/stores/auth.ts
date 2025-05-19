import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginRequest } from '@/types/auth'
import { authApi, defaultApi } from '@/api/axios'

export const useAuthStore = defineStore(
  'auth',
  () => {
    const accessToken = ref<string | null>(null)

    const login = async (loginRequest: LoginRequest): Promise<void> => {
      const response = await defaultApi.post('/auth/login', loginRequest)

      const authorizationHeader = response.headers.authorization

      // access token이 있는 경우
      if (authorizationHeader) {
        accessToken.value = response.headers.authorization
        authApi.defaults.headers.common['Authorization'] = accessToken.value
      }
    }

    const logout = async () => {
      try {
        await authApi.post('/auth/logout')
      } catch (error) {
        console.error('로그아웃 중 에러 발생:', error)
      } finally {
        // 로컬 상태 정리는 항상 수행
        accessToken.value = null
        delete authApi.defaults.headers.common['Authorization']
      }
    }

    return {
      accessToken,
      login,
      logout,
    }
  },
  {
    persist: true,
  },
)
