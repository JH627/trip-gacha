import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginRequest } from '@/types/auth'
import { authApi } from '@/api/axios'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)

  const login = async (loginRequest: LoginRequest): Promise<void> => {
    const response = await authApi.post('/user/login', loginRequest)

    const authorizationHeader = response.headers.authorization

    // access token이 있는 경우
    if (authorizationHeader) {
      accessToken.value = response.headers.authorization
      authApi.defaults.headers.common['Authorization'] = accessToken.value
    }
  }

  const logout = async (): Promise<void> => {
    await authApi.post('/user/logout')
    accessToken.value = null
    delete authApi.defaults.headers.common['Authorization']
  }

  return {
    accessToken,

    login,
    logout,
  }
})
