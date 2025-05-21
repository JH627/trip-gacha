<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch } from 'vue'
import {
  LobyEventType,
  type LobbyResponse,
  type RoomInfo,
  type SocketUserInfo,
} from '@/socket/webSocket'
import defaultProfile from '../assets/no_profile.png'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRouter } from 'vue-router'

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

const processRoomData = (body: string) => {}

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

const rooms = ref<RoomInfo[]>([])
const userMap = reactive(new Map<string, SocketUserInfo>())
const users = computed(() =>
  Array.from(userMap.values()).sort((a, b) => {
    return new Date(a.loginTime).getTime() - new Date(b.loginTime).getTime()
  }),
)

const addUser = (user: SocketUserInfo) => {
  userMap.set(user.socketId, user)

  console.log('추가 성공 : ' + userMap)
}

const removeUser = (socketId: string) => {
  userMap.delete(socketId)

  console.log('삭제 성공 : ' + userMap)
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
</script>

<template>
  <div id="container">
    <div id="lobyHeader">
      <div>로고</div>
      <button @click="goNext">방 생성</button>
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
          <div v-else>
            <div v-for="room in rooms" :key="room.roomId">
              {{ room.title }} | {{ room.inPeople }} / {{ room.maxPeople }}
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
</style>
