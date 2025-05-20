<script setup lang="ts">
import { onMounted, ref, reactive, computed } from 'vue'
import {
  connect,
  enterLoby,
  LobyEventType,
  type LobbyResponse,
  type RoomInfo,
  type SocketUserInfo,
} from '@/socket/webSocket'
import defaultProfile from '../assets/no_profile.png'

onMounted(async () => {
  const newUser: SocketUserInfo = {
    socketId: '',
    nickname: '깐총이',
    img: defaultProfile,
    loginTime: Date.now(),
  }

  await connect(
    (body) => {
      try {
        const data: LobbyResponse = JSON.parse(body)

        console.log(data.type)
        console.log(data.userInfos)

        if (data.type === LobyEventType.LEAVE) {
          console.log('삭제 :' + data.userInfos[0])
          removeUser(data.userInfos[0].socketId)
          return
        }

        const userInfos = data.userInfos

        if (!userInfos.length) {
          return
        }

        userInfos.sort((a, b) => a.loginTime - b.loginTime)
        userInfos.forEach(addUser)
      } catch (e) {
        console.error('파싱 실패:', e)
      }
    },
    () => {
      enterLoby(newUser)
    },
  )
})

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
    value: 'jack',
    label: 'Jack',
  },
  {
    value: 'lucy',
    label: 'Lucy',
  },
  {
    value: 'yiminghe',
    label: 'Yiminghe',
  },
])
const focusOn = () => {
  console.log('focus')
}
const customHandleChange = (value: string) => {
  console.log(`selected ${value}`)
}
</script>

<template>
  <div id="container">
    <div id="lobyHeader">
      <div>로고</div>
      <button>방 생성</button>
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
