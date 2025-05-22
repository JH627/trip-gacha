<script setup lang="ts">
import {
  RoomEventType,
  type RoomInfo,
  type RoomResponse,
  type SocketRoomUser,
  type SocketUserInfo,
} from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { onMounted, ref, watch, type Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// roomId를 url에서 빼옴
const route = useRoute()
const router = useRouter()
const roomId = route.params.roomId
// roomId를 통해 해당 방 정보 (제목, 여행지, 시작일, 종료일) 불러옴
// roomId : [사람 리스트
// ]를 불러옴 (이 리스트에는 구독한 사람만 들어감 ㅇㅇ)
const socketStore = useSocketStore()
const authStore = useAuthStore()

const processRoomRequest = (body: string) => {
  const response: RoomResponse<RoomInfo> = JSON.parse(body)

  switch (response.type) {
    case RoomEventType.INIT:
    case RoomEventType.JOIN:
      ownerId.value = response.data.owner.userId
      userList.value = response.data.userList
      title.value = response.data.title
      return
    case RoomEventType.LEAVE:
      // 입력받은 유저 정보를 userList에서 삭제함
      const leaveUser = response.data.userList[0]
      removeUserById(leaveUser.userId)
      return
    case RoomEventType.BOOM:
      // lobby로 보냄 (이미 서버에서 방 정보를 삭제해서 나가게만 하면 된다)
      window.location.href = '/trip/lobby'
      return
    default:
      return
  }
}

onMounted(() => {
  try {
    socketStore.subscribe(`/user/queue/room/${roomId}`, processRoomRequest)
    socketStore.subscribe(`/topic/room/${roomId}`, processRoomRequest)

    // 방 정보 불러오는 로직
    socketStore.send(`/app/room/info/${roomId}`, authStore.accessToken || '', null)
  } catch (_error) {
    window.location.href = '/trip/lobby'
  }
})

const title = ref('')
const ownerId = ref('')
// 사람 리스트를 출력함
const userList: Ref<SocketRoomUser[]> = ref([])
const removeUserById = (userId: string) => {
  userList.value = userList.value.filter((user) => user.userId !== userId)
}
// 채팅은 아직 미구현으로 하쟈..
const leaveRoom = () => {
  socketStore.send(`/app/room/leave/${roomId}`, authStore.accessToken || '', null)
}
</script>

<template>
  <div>
    <div>
      <button @click="leaveRoom">나가기</button>
      <div>{{ title }}</div>
      <button>시작하기</button>
    </div>
    <div>
      <div class="userContainer" v-for="user in userList" :key="user.userId">
        <img :src="user.img" />
        <div>{{ user.nickname }}</div>
        <div>{{ user.userId === ownerId ? '방장임' : '' }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.userContainer {
  border: 1px solid black;
}
</style>
