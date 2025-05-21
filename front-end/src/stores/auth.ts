import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginRequest, Profile } from '@/types/auth'
import { authApi, defaultApi } from '@/api/axios'

export const useAuthStore = defineStore(
  'auth',
  () => {
    const accessToken = ref<string | null>(null)

    const profile = ref<Profile | null>(null)

    const login = async (loginRequest: LoginRequest): Promise<void> => {
      const response = await defaultApi.post('/auth/login', loginRequest)

      const authorizationHeader = response.headers.authorization

      // access token이 있는 경우
      if (authorizationHeader) {
        accessToken.value = response.headers.authorization
        authApi.defaults.headers.common['Authorization'] = accessToken.value

        await getProfile()
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
        profile.value = null
        delete authApi.defaults.headers.common['Authorization']
      }
    }

    const checkLogin = async () => {
      if (accessToken.value) {
        return true
      }

      await refreshAccessToken()

      return false
    }

    const refreshAccessToken = async () => {
      try {
        const response = await authApi.post('/auth/refresh-token')
        const newToken = response.headers.authorization
        if (newToken) {
          accessToken.value = newToken
          authApi.defaults.headers.common['Authorization'] = newToken
          return true
        }
      } catch (error) {
        console.error(error)
        accessToken.value = null
      }
    }

    const setAccessToken = (newAccessToken: string) => {
      accessToken.value = newAccessToken
    }

    const setProfile = (userProfile: Profile) => {
      profile.value = userProfile
    }

    const getProfile = async (): Promise<Profile | null> => {
      if (profile.value) {
        return profile.value
      }

      await authApi.get('/user').then((response) => {
        console.log(response.data)
        profile.value = response.data.result
      })

      return profile.value
    }

    return {
      accessToken,
      profile,
      checkLogin,
      login,
      logout,
      setAccessToken,
      refreshAccessToken,
      setProfile,
      getProfile,
    }
  },
  {
    persist: true,
  },
)
