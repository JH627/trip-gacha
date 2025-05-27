<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch } from 'vue'
import {
  LobyEventType,
  RoomEventType,
  type LobbyResponse,
  type RoomHeader,
  type RoomResponse,
  type SocketUserInfo,
} from '@/socket/webSocket'
import defaultProfile from '../assets/no_profile.png'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRouter } from 'vue-router'
import CreateRoomModal from '@/components/room/CreateRoomModal.vue'
import {
  UserIcon,
  PlusCircleIcon,
  SearchIcon,
  MapPinIcon,
  UsersIcon,
  CalendarIcon,
} from 'lucide-vue-next'

const socketStore = useSocketStore()
const router = useRouter()
const accessToken = ref('')
const authStore = useAuthStore()
const destinationStore = useDestinationStore()

const processLobbyData = (body: string) => {
  const response: LobbyResponse<SocketUserInfo | SocketUserInfo[]> = JSON.parse(body)

  switch (response.type) {
    case LobyEventType.LEAVE:
      if (!Array.isArray(response.data)) {
        console.log('삭제 :' + response.data)
        removeUser(response.data.socketId)
      }
      return
    case LobyEventType.INIT:
      if (!Array.isArray(response.data)) {
        return
      }

      const userInfoList = response.data

      if (userInfoList.length === 0) {
        return
      }

      userInfoList.sort((a, b) => a.loginTime - b.loginTime)
      userInfoList.forEach(addUser)
      return
    case LobyEventType.JOIN:
      if (Array.isArray(response.data)) {
        return
      }
      const userInfo = response.data
      addUser(userInfo)
  }
}

const processRoomData = (body: string) => {
  const response: RoomResponse<RoomHeader | RoomHeader[]> = JSON.parse(body)

  switch (response.type) {
    case RoomEventType.INIT:
      if (!Array.isArray(response.data)) {
        return
      }

      const roomHeaderList: RoomHeader[] = response.data

      if (roomHeaderList.length === 0) {
        return
      }

      roomHeaderList.sort((a, b) => a.roomId.localeCompare(b.roomId))
      roomHeaderList.forEach(addRoom)
      return
    case RoomEventType.CREATE:
      if (Array.isArray(response.data)) {
        return
      }

      console.log('방 생성 성공 :' + response.data.title)
      socketStore.send('/app/room/join/' + response.data.roomId, accessToken.value, {
        password: '',
      })

      return
    case RoomEventType.CREATED:
      if (Array.isArray(response.data)) {
        return
      }
      const roomHeader: RoomHeader = response.data as RoomHeader
      addRoom(roomHeader)
      return
    case RoomEventType.JOIN:
      if (Array.isArray(response.data)) {
        return
      }

      if (response.success) {
        console.log('방 이동 : ' + response.data.roomId)
        // 구독하고 이동하기
        router.push(`/trip/room/${response.data.roomId}`)
      }
      return
    default:
      return
  }
}

watch(
  () => socketStore.connected,
  async (connected) => {
    console.log('lobbyView')
    if (connected) {
      accessToken.value = authStore.accessToken || ''
      const profile = await authStore.getProfile()
      const newUser: SocketUserInfo = {
        socketId: '',
        nickname: profile?.nickname || '',
        img: profile?.profileImg || defaultProfile,
        loginTime: Date.now(),
      }
      socketStore.subscribe('/user/queue/lobby', processLobbyData)
      socketStore.subscribe('/topic/lobby', processLobbyData)
      socketStore.subscribe('/user/queue/room', processRoomData)
      socketStore.subscribe('/topic/room', processRoomData)
      socketStore.subscribeError()

      socketStore.send('/app/lobby/join', accessToken.value, newUser)
    }
  },
)

const roomMap = reactive(new Map<string, RoomHeader>())
const rooms = computed(() =>
  Array.from(roomMap.values()).sort((a, b) => b.createdAt.localeCompare(a.createdAt)),
)
const filteredRooms = computed(() => {
  return rooms.value.filter((room) => {
    const value = keyword.value.toLowerCase()
    if (searchOption.value === 'title') {
      return room.title.toLowerCase().includes(value)
    } else if (searchOption.value === 'destination') {
      const destName = destinationStore.getDestinationName(room.destination)
      return destName?.toLowerCase().includes(value)
    }
    return true
  })
})

const addRoom = (room: RoomHeader) => {
  roomMap.set(room.roomId, room)
}

const removeRoom = (roomId: string) => {
  roomMap.delete(roomId)
}

const userMap = reactive(new Map<string, SocketUserInfo>())
const users = computed(() =>
  Array.from(userMap.values()).sort((a, b) => {
    return new Date(a.loginTime).getTime() - new Date(b.loginTime).getTime()
  }),
)

const addUser = (user: SocketUserInfo) => {
  userMap.set(user.socketId, user)
}

const removeUser = (socketId: string) => {
  userMap.delete(socketId)
}

const keyword = ref('')
const searchOption = ref('title')

const onSearch = () => {
  console.log(`${keyword} 검색하기`)
}

import type { SelectProps } from 'ant-design-vue'
import RoomPasswordModal from '@/components/room/RoomPasswordModal.vue'
import { useDestinationStore } from '@/stores/destination'
const options1 = ref<SelectProps['options']>([
  {
    value: 'title',
    label: '제목',
  },
  {
    value: 'destination',
    label: '목적지',
  },
])
const focusOn = () => {
  console.log('focus')
}
const customHandleChange = (value: string) => {
  console.log(`selected ${value}`)
}

const isOpen = ref<boolean>(false)
const open = () => {
  isOpen.value = true
}
const close = () => {
  destinationStore.selectedDestinationID = 0
  isOpen.value = false
}

onMounted(() => {
  if (destinationStore.selectedDestinationID !== 0) {
    open()
  }
})

const clickRoomId = ref('')
const isPasswordModalOpen = ref<boolean>(false)
const passwordModalOpen = (roomId: string) => {
  clickRoomId.value = roomId
  isPasswordModalOpen.value = true
}
const passwordModalClose = () => {
  isPasswordModalOpen.value = false
}
</script>

<template>
  <div class="container">
    <CreateRoomModal v-if="isOpen" @close-modal="close" />
    <RoomPasswordModal
      v-if="isPasswordModalOpen"
      :room-id="clickRoomId"
      @close-modal="passwordModalClose"
    />
    <div class="lobby-header">
      <div class="logo">
        <h1>여행 로비</h1>
      </div>
      <button class="create-room-btn" @click="open">
        <PlusCircleIcon :size="18" />
        <span>방 생성</span>
      </button>
    </div>
    <div class="lobby-body">
      <div class="lobby-list">
        <div class="search-bar">
          <a-select
            ref="select"
            v-model:value="searchOption"
            class="search-select"
            :options="options1"
            @focus="focusOn"
            @change="customHandleChange"
          ></a-select>
          <a-input-search
            v-model:value="keyword"
            placeholder="검색어를 입력하세요"
            class="search-input"
            @search="onSearch"
          >
            <template #enterButton>
              <button class="search-button">
                <SearchIcon :size="16" />
              </button>
            </template>
          </a-input-search>
        </div>
        <div class="search-room-list">
          <div v-if="rooms.length === 0" class="no-results">
            <div class="no-results-content">
              <SearchIcon :size="48" class="no-results-icon" />
              <p>검색 결과가 없습니다</p>
            </div>
          </div>
          <div v-else class="room-list">
            <div
              class="room-item"
              v-for="room in filteredRooms"
              :key="room.roomId"
              @click="
                () => {
                  passwordModalOpen(room.roomId)
                }
              "
            >
              <!-- 목적지 -->
              <div class="room-destination">
                <MapPinIcon :size="16" class="room-icon" />
                <span>{{ destinationStore.getDestinationName(room.destination) }}</span>
              </div>

              <!-- 제목 및 날짜 -->
              <div class="room-info">
                <h3 class="room-title">{{ room.title }}</h3>
                <div class="room-dates">
                  <CalendarIcon :size="14" class="room-icon" />
                  <span>{{ room.startDate }} ~ {{ room.endDate }}</span>
                </div>
              </div>

              <!-- 인�� 수 -->
              <div class="room-users">
                <UsersIcon :size="16" class="room-icon" />
                <span>{{ room.userCount }} / 8</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="lobby-sidebar">
        <div class="sidebar-header">
          <UserIcon :size="18" />
          <h3>접속 중인 유저</h3>
        </div>
        <div v-if="users.length === 0" class="no-users">
          <p>접속 중인 유저가 없습니다</p>
        </div>
        <div v-else class="user-list">
          <div class="user-info" v-for="user in users" :key="user.socketId">
            <img :src="user.img" alt="프로필 이미지" class="user-avatar" />
            <div class="user-name">{{ user.nickname }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
  gap: 20px;
}

.lobby-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px 15px 10px;
  border-bottom: 1px solid #e1e5eb;
}

.logo h1 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.create-room-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.create-room-btn:hover {
  background-color: #4338ca;
}

.lobby-body {
  display: flex;
  gap: 20px;
  height: calc(100% - 80px);
}

.lobby-list {
  display: flex;
  flex-direction: column;
  flex: 3;
  gap: 15px;
  height: 100%;
}

.search-bar {
  display: flex;
  gap: 10px;
  width: 100%;
}

.search-select {
  width: 120px !important;
  border-radius: 8px;
}

.search-input {
  flex: 1;
  border-radius: 8px;
}

.search-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1677ff;
  border: none;
  color: white;
  height: 100%;
  width: 100%;
  cursor: pointer;
}

.search-room-list {
  flex: 1;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.no-results {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.no-results-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #9ca3af;
}

.no-results-icon {
  margin-bottom: 16px;
  color: #d1d5db;
}

.room-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px;
  gap: 12px;
  overflow-y: auto;
}

.room-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 10px;
  border-left: 4px solid #4f46e5;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  cursor: pointer;
}

.room-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.room-destination {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #4f46e5;
}

.room-info {
  flex: 4;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.room-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.room-dates {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #6b7280;
}

.room-users {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-weight: 500;
  color: #4b5563;
}

.room-icon {
  flex-shrink: 0;
}

.lobby-sidebar {
  display: flex;
  flex-direction: column;
  flex: 1;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.no-users {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #9ca3af;
}

.user-list {
  padding: 15px;
  overflow-y: auto;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f9fafb;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.user-name {
  font-weight: 500;
  color: #374151;
}

/* 스크롤바 스타일링 */
.room-list::-webkit-scrollbar,
.user-list::-webkit-scrollbar {
  width: 6px;
}

.room-list::-webkit-scrollbar-track,
.user-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.room-list::-webkit-scrollbar-thumb,
.user-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.room-list::-webkit-scrollbar-thumb:hover,
.user-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
