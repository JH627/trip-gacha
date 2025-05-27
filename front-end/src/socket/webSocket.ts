import SockJS from 'sockjs-client'
import { Client, type Message, over } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

export enum PlanProgress {
  SELECT_ACCOMMODATION = 'SELECT_ACCOMMODATION',
  SELECT_TOURIST_SPOTS = 'SELECT_TOURIST_SPOTS',
  FINALIZE_DESTINATIONS = 'FINALIZE_DESTINATIONS',
  REVIEW_AND_EDIT = 'REVIEW_AND_EDIT',
}

export const progressTextMap: Record<PlanProgress, string> = {
  [PlanProgress.SELECT_ACCOMMODATION]: '숙소 선택 중',
  [PlanProgress.SELECT_TOURIST_SPOTS]: '관광지 선택 중',
  [PlanProgress.FINALIZE_DESTINATIONS]: '여행지 확정 중',
  [PlanProgress.REVIEW_AND_EDIT]: '검토 및 수정 중',
}

export interface JoinPlan {
  planId: string
  process: PlanProgress
}

export interface RoomInfo {
  roomId: string
  title: string
  startDate: string
  endDate: string
  destination: string
  owner: SocketRoomUser
  planning: boolean
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
  CREATE = 'CREATE', // 내가 방을 만듬
  CREATED = 'CREATED', // 다른 사람이 방을 만듬
  INIT = 'INIT', // 처음들어와서 초기값을 받음
  LEAVE = 'LEAVE', // 누군가 떠남
  JOIN = 'JOIN', // 누군가 합류함
  BOOM = 'BOOM', // 방이 터짐 or 방을 나감
  PLAN = 'PLAN', // 계획 세우기 시작
}

export interface CreateRoomRequest {
  title: string
  destination: number
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
  createdAt: string
}

export interface RoomResponse<T> {
  type: RoomEventType
  success: boolean
  data: T
}
