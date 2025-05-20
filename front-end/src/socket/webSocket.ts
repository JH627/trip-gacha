import SockJS from 'sockjs-client'
import { Client, type Message, over } from 'stompjs'

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

  stompClient.connect({}, () => {
    console.log('✅ 연결됨')

    // 이걸로 사용자를 맵핑함
    stompClient?.subscribe('/user/queue/loby', (message: Message) => {
      onMessage(message.body)
    })

    // 이걸로 특정 토픽을 구독함
    stompClient?.subscribe('/topic/loby', (message: Message) => {
      onMessage(message.body)
    })

    // 위 두개 연결이 끝나면 실행함
    onConnected?.()
  })
}

export function enterLoby(socketUserInfo: SocketUserInfo) {
  stompClient?.send('/app/loby/enter', {}, JSON.stringify(socketUserInfo))
}

export function disconnect() {
  stompClient?.disconnect(() => {
    console.log('❌ 연결 해제됨')
  })
}
