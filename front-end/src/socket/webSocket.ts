import SockJS from 'sockjs-client'
import { Client, type Message, over } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

export interface RoomInfo {
  roomId: string
  title: string
  startDate: number
  endDate: number
  tripTarget: string
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
  JOIN = 'JOIN',
  INIT = 'INIT',
  LEAVE = 'LEAVE',
}

export interface LobbyResponse<T> {
  type: LobyEventType
  data: T
}
