import SockJS from 'sockjs-client'
import { Client, type Message, over } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

export interface RoomInfo {
  roomId: string
  title: string
  startDate: string
  endDate: string
  destination: string
  owner: SocketRoomUser
  userList: SocketRoomUser[]
}

export interface SocketRoomUser {
  userId: string
  nickname: string
  img: string
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

export enum RoomEventType {
  CREATE = 'CREATE',
  CREATED = 'CREATED',
  INIT = 'INIT',
  LEAVE = 'LEAVE',
  JOIN = 'JOIN',
  BOOM = 'BOOM',
}

export interface CreateRoomRequest {
  title: string
  destination: string
  password: string
  startDate: string
  endDate: string
}

export interface JoinRoomRequest {
  password: string
}

export interface RoomHeader {
  roomId: string
  title: string
  destination: string
  userCount: number | null
  startDate: string
  endDate: string
}

export interface RoomResponse<T> {
  type: RoomEventType
  success: boolean
  data: T
}
