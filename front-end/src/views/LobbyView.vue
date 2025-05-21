<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch } from 'vue'
import {
  LobyEventType,
  RoomEventType,
  type LobbyResponse,
  type RoomHeader,
  type RoomInfo,
  type RoomResponse,
  type SocketUserInfo,
} from '@/socket/webSocket'
import defaultProfile from '../assets/no_profile.png'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRouter } from 'vue-router'
import CreateRoomModal from '@/components/room/CreateRoomModal.vue'

const socketStore = useSocketStore()

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

      const roomHeaderList = response.data

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
      const roomHeader = response.data
      addRoom(roomHeader)
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
      const authStore = useAuthStore()
      const profile = await authStore.getProfile()
      const newUser: SocketUserInfo = {
        socketId: '',
        nickname: profile?.nickname || '',
        img: profile?.profileImg || defaultProfile,
        loginTime: Date.now(),
      }
      socketStore.subscribe('/user/queue/lobby', processLobbyData)
      socketStore.subscribe('/topic/lobby', processLobbyData)
      socketStore.subscribe('/topic/room', processRoomData)
      socketStore.subscribeError()

      socketStore.send('/app/lobby/join', authStore.accessToken || '', newUser)
    }
  },
)

const roomMap = reactive(new Map<string, RoomHeader>())
const rooms = computed(() =>
  Array.from(roomMap.values()).sort((a, b) => a.roomId.localeCompare(b.roomId)),
)

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
const searchOption = ref('')

const onSearch = () => {
  console.log(`${keyword} 검색하기`)
}

import type { SelectProps } from 'ant-design-vue'
const options1 = ref<SelectProps['options']>([
  {
    value: 'title',
    label: '제목',
  },
  {
    value: 'tripTarger',
    label: '목적지',
  },
])
const focusOn = () => {
  console.log('focus')
}
const customHandleChange = (value: string) => {
  console.log(`selected ${value}`)
}

const router = useRouter()

const goNext = () => {
  router.push('/trip/room')
}

const isOpen = ref<boolean>(false)

const open = () => {
  isOpen.value = true
}

const close = () => {
  isOpen.value = false
}
</script>

<template>
  <div id="container">
    <CreateRoomModal v-if="isOpen" @close-modal="close" />
    <div id="lobyHeader">
      <div>로고</div>
      <button @click="open">방 생성</button>
    </div>
    <div id="lobyBody">
      <div id="lobyList">
        <div id="searchBar">
          <a-select
            ref="select"
            v-model:value="searchOption"
            style="width: 120px"
            :options="options1"
            @focus="focusOn"
            @change="customHandleChange"
          ></a-select>
          <a-input-search
            v-model:value="keyword"
            placeholder="input search text"
            style="width: 100%"
            @search="onSearch"
          />
        </div>
        <div id="searchRoomList">
          <div v-if="rooms.length === 0">검색 결과 없음</div>
          <div v-else class="room-list">
            <div class="room-item" v-for="room in rooms" :key="room.roomId">
              <!-- 목적지 -->
              <div class="room-destination">
                <a-typography-text strong>{{ room.destination }}</a-typography-text>
              </div>

              <!-- 제목 및 날짜 -->
              <div class="room-info">
                <a-typography-title :level="5" class="room-title">{{
                  room.title
                }}</a-typography-title>
                <a-typography-text type="secondary" class="room-dates">
                  {{ room.startDate }} ~ {{ room.endDate }}
                </a-typography-text>
              </div>

              <!-- 인원 수 -->
              <div class="room-users">
                <a-typography-text>{{ room.userCount }} / 8</a-typography-text>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div id="lobySideBar">
        <div>접속 중인 유저</div>
        <div v-if="users.length === 0">검색 결과 없음</div>
        <div v-else>
          <div id="userInfo" v-for="user in users" :key="user.socketId">
            <img :src="user.img" />
            <div>{{ user.nickname }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
img {
  width: 20px;
  height: 20px;
  border-radius: 100%;
}

#userInfo {
  display: flex;
}

#searchRoomList {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 80vh;
  background-color: darkgray;
  border-radius: 10px;
}

#lobySideBar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: flex-start;
  align-items: center;
  flex: 1;
  height: 80vh;
  background-color: darkgray;
  border-radius: 10px;
}

#lobyList {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 2;
  height: 80vh;
}

#lobyBody {
  display: flex;
  justify-content: center;
  width: 100%;
  height: 100%;
  gap: 2%;
}

#searchBar {
  max-width: 600px;
  min-width: 400px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

#lobyHeader {
  display: flex;
  justify-content: space-between;
  padding-left: 10px;
  padding-right: 10px;
  width: 100%;
}

#container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  gap: 20px;
}

.room-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  gap: 20px;
}

.room-item {
  display: flex;
  width: 100%;
  flex-direction: row;
  align-items: center;
  padding: 5px 0;
  background-color: lightcyan;
  border: 1px solid black;
  border-radius: 5px;
}

.room-destination {
  flex: 1;
  display: flex;
  justify-content: center;
  padding-left: 12px;
}

.room-info {
  flex: 4;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.room-title {
  margin: 0;
  font-weight: 600;
}

.room-dates {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.room-users {
  flex: 1;
  display: flex;
  justify-content: center;
  padding-right: 12px;
}
</style>
