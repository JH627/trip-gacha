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

export function connect(onMessage: (msg: string) => void, onConnected?: () => void) {
  const socket = new SockJS('http://localhost:8081/ws')
  stompClient = over(socket)

  const userAuth = useAuthStore()
  console.log(userAuth.accessToken)

  stompClient.connect(
    { Authorization: userAuth.accessToken },
    () => {
      console.log('✅ 연결됨')

      // 이걸로 사용자를 맵핑함
      stompClient?.subscribe('/user/queue/loby', (message: Message) => {
        onMessage(message.body)
      })

      // 이걸로 특정 토픽을 구독함
      stompClient?.subscribe('/topic/loby', (message: Message) => {
        onMessage(message.body)
      })

      stompClient?.subscribe('/user/queue/errors', (message: Message) => {
        const errorMessage = message.body
        alert('WebSocket 인증 실패: ' + errorMessage)
      })

      // 위 두개 연결이 끝나면 실행함
      onConnected?.()
    },
    (error) => {
      console.error('웹소켓 연결 실패:', error)
    },
  )
}

export function enterLoby(socketUserInfo: SocketUserInfo) {
  stompClient?.send('/app/loby/enter', {}, JSON.stringify(socketUserInfo))
}

export function disconnect() {
  stompClient?.disconnect(() => {
    console.log('❌ 연결 해제됨')
  })
}
