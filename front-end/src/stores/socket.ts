import { ref } from 'vue'
import { defineStore } from 'pinia'
import { Frame, over, type Client, type Message } from 'stompjs'
import SockJS from 'sockjs-client'
import { useAuthStore } from './auth'

export const useSocketStore = defineStore('socket', () => {
  const stompClient = ref<Client | null>(null)
  const connected = ref(false)
  let isErrorHandled = false

  const initializeClient = () => {
    const socket = new SockJS('http://localhost:8081/ws')
    stompClient.value = over(socket)
  }

  const connect = async (token: string, hasRetried = false): Promise<void> => {
    isErrorHandled = false

    return new Promise((resolve, reject) => {
      stompClient.value?.connect(
        { Authorization: token },
        () => {
          connected.value = true
          console.log('연결 성공')
          isErrorHandled = false
          resolve()
        },
        async (err) => {
          if (isErrorHandled) return
          isErrorHandled = true

          connected.value = false
          console.log('연결 실패', err)

          if (!(err instanceof Frame)) {
            reject(err)
            return
          }

          const errorMessage = (err as { headers?: { message?: string } })?.headers?.message

          if (errorMessage?.includes('JWT expired') && !hasRetried) {
            try {
              console.log('토큰 재발급 시도')
              const authStore = useAuthStore()
              await authStore.refreshAccessToken()
              const newToken = authStore.accessToken || ''
              console.log('새 토큰 :' + newToken)
              initializeClient() // stompClient 새로 생성
              await connect(authStore.accessToken || '', true) // 재귀 호출로 재연결 시도
              resolve()
            } catch (refreshErr) {
              console.error('토큰 재발급 실패', refreshErr)
              reject(refreshErr)
            }
          } else {
            reject(err)
          }
        },
      )
    })
  }

  const disconnect = () => {
    stompClient.value?.disconnect(() => {
      connected.value = false
    })
  }

  const send = (destination: string, accessToken: string, body: any) => {
    stompClient.value?.send(destination, { Authorization: accessToken }, JSON.stringify(body))
  }

  const subscribe = (destination: string, callback: (msg: string) => void) => {
    stompClient.value?.subscribe(destination, (message) => {
      callback(message.body)
    })
  }

  const subscribeError = () => {
    stompClient.value?.subscribe('/user/queue/errors', (message) => {
      alert('소켓 에러: ' + message.body)
    })
  }

  return {
    stompClient,
    connected,
    initializeClient,
    connect,
    disconnect,
    send,
    subscribe,
    subscribeError,
  }
})
