<script setup lang="ts">
import {
  RoomEventType,
  type JoinPlan,
  type RoomInfo,
  type RoomResponse,
  type SocketRoomUser,
  type SocketUserInfo,
} from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { onMounted, ref, watch, type Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Crown } from 'lucide-vue-next'

// roomId를 url에서 빼옴
const route = useRoute()
const router = useRouter()
const roomId = route.params.roomId as string
// roomId를 통해 해당 방 정보 (제목, 여행지, 시작일, 종료일) 불러옴
// roomId : [사람 리스트]를 불러옴 (이 리스트에는 구독한 사람만 들어감 ㅇㅇ)
const socketStore = useSocketStore()
const authStore = useAuthStore()

const title = ref('')
const ownerId = ref('')
// 사람 리스트를 출력함
const userList: Ref<SocketRoomUser[]> = ref([])

const removeUserById = (userId: string) => {
  userList.value = userList.value.filter((user) => user.userId !== userId)
}

const processRoomRequest = (body: string) => {
  const response: RoomResponse<RoomInfo | JoinPlan> = JSON.parse(body)
  let data

  switch (response.type) {
    case RoomEventType.PLAN:
      data = response.data as JoinPlan
      router.push(`/trip/plan/${data.planId}`)
      return
    case RoomEventType.INIT:
      // 들어왔을 때, 계획 짜는 중이면 해당 페이지로 바로 이동 (구독도 알아서)
      data = response.data as RoomInfo

      console.log('초기화')
      console.log(data)

      if (data.planning) {
        // 바로 계획 페이지로 이동하게 요청보내기 ( roomId == planId라서 roomId 보내도 댐 ㅇㅇ)
        router.push(`/trip/plan/${data.roomId}`)
        return
      }
    case RoomEventType.JOIN:
      data = response.data as RoomInfo
      ownerId.value = data.owner.userId
      userList.value = data.userList
      title.value = data.title
      return
    case RoomEventType.LEAVE:
      // 입력받은 유저 정보를 userList에서 삭제함
      data = response.data as RoomInfo
      const leaveUser = data.userList[0]
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

const leaveRoom = () => {
  socketStore.send(`/app/room/leave/${roomId}`, authStore.accessToken || '', null)
}

interface StartPlanRequest {
  roomId: string
}

const startPlan = () => {
  socketStore.send(`/app/plan/start`, authStore.accessToken || '', {
    roomId: roomId,
  } as StartPlanRequest)
}
</script>

<template>
  <div class="room-container">
    <div class="room-header">
      <button class="room-button leave-button" @click="leaveRoom">나가기</button>
      <div class="room-title">{{ title }}</div>
      <button class="room-button start-button" @click="startPlan">시작하기</button>
    </div>

    <div class="user-list">
      <div class="user-container" v-for="user in userList" :key="user.userId">
        <div class="user-profile">
          <div class="image-container" :class="{ 'is-owner': user.userId === ownerId }">
            <img :src="user.img" alt="프로필 이미지" class="user-image" />
            <Crown v-if="user.userId === ownerId" class="crown-icon" :size="20" />
          </div>
          <div class="user-info">
            <div class="user-nickname">{{ user.nickname }}</div>
            <div v-if="user.userId === ownerId" class="owner-badge">방장</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.room-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e0e0e0;
  flex-shrink: 0;
  margin-bottom: 12px;
}

.room-title {
  font-size: 1.5rem;
  font-weight: bold;
  text-align: center;
  flex-grow: 1;
  margin: 0 15px;
}

.room-button {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  min-width: 80px;
}

.leave-button {
  background-color: #f44336;
  color: white;
}

.start-button {
  background-color: #4caf50;
  color: white;
}

.user-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  flex: 1;
  overflow-y: auto;
  align-content: flex-start;
  padding: 0 20px 20px 20px;
  box-sizing: border-box;
}

.user-container {
  width: calc(50% - 10px);
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  height: fit-content;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 15px;
}

.image-container {
  position: relative;
  width: 60px;
  height: 60px;
}

.user-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e0e0;
}

.is-owner {
  position: relative;
}

.is-owner .user-image {
  border-color: #ffc107;
}

.crown-icon {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  color: #ffc107;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
}

.user-info {
  flex-grow: 1;
}

.user-nickname {
  font-weight: bold;
  font-size: 1.1rem;
  margin-bottom: 4px;
}

.owner-badge {
  display: inline-block;
  background-color: #ffc107;
  color: #333;
  font-size: 0.8rem;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .user-container {
    width: 100%;
  }

  .user-list {
    padding: 0 15px 15px 15px;
  }
}
</style>
