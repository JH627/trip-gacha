import SockJS from 'sockjs-client'
import { Client, type Message, over } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

export interface RoomInfo {
  roomId: string
  title: string
  inPeople: number
  maxPeople: number
}

export interface SocketUserInfo {
  socketId: string
  nickname: string
  img: string
  loginTime: number
}

export enum LobyEventType {
  ENTER = 'ENTER',
  LEAVE = 'LEAVE',
}

export interface LobbyResponse {
  type: LobyEventType
  userInfos: SocketUserInfo[]
}

declare module 'stompjs' {
  export interface Client {
    ws: WebSocket
  }
}
let stompClient: Client | null = null

const initializeClient = () => {
  stompClient = over(new SockJS('http://localhost:8081/ws'))
}

export const connect = async (onMessage: (msg: string) => void, onConnected?: () => void) => {
  const userAuth = useAuthStore()

  initializeClient()

  const tryConnect = async (token: string, hasRetried = false) => {
    try {
      await new Promise<void>((resolve, reject) => {
        stompClient?.connect(
          { Authorization: token },
          () => {
            console.log('✅ 연결됨')

            stompClient?.subscribe('/user/queue/loby', (message: Message) => {
              onMessage(message.body)
            })

            stompClient?.subscribe('/topic/loby', (message: Message) => {
              onMessage(message.body)
            })

            stompClient?.subscribe('/user/queue/errors', (message: Message) => {
              alert('WebSocket 인증 실패: ' + message.body)
            })

            onConnected?.()
            resolve()
          },
          (error) => {
            reject(error)
          },
        )
      })
    } catch (error) {
      console.error('웹소켓 연결 실패:', error)

      if (!hasRetried) {
        try {
          console.log('🔄 accessToken 재발급 시도 중...')
          await userAuth.refreshAccessToken()
          const newToken = useAuthStore().accessToken

          //기존 연결 명시적으로 종료 후 재시도
          if (stompClient && stompClient.connected) {
            console.log('명시적인 연결 종료')
            disconnect()
          }

          initializeClient()
          await tryConnect(newToken || '', true)
        } catch (e) {
          console.error('🚫 accessToken 재발급 실패:', e)
          alert('세션이 만료되었습니다. 다시 로그인해주세요.')
        }
      } else {
        alert('웹소켓 연결 실패: 인증 불가')
      }
    }
  }

  await tryConnect(userAuth.accessToken || '')
}

export function enterLoby(socketUserInfo: SocketUserInfo) {
  console.log(stompClient?.connected)
  console.log(stompClient)
  stompClient?.send('/app/loby/enter', {}, JSON.stringify(socketUserInfo))
}

export function disconnect() {
  stompClient?.disconnect(() => {
    console.log('❌ 연결 해제됨')
  })
}
