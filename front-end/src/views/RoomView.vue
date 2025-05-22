<script setup>
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
// roomId를 url에서 빼옴
const route = useRoute()
const roomId = route.params.roomId
// roomId를 통해 해당 방 정보 (제목, 여행지, 시작일, 종료일) 불러옴
// roomId : [사람 리스트
// ]를 불러옴 (이 리스트에는 구독한 사람만 들어감 ㅇㅇ)
const socketStore = useSocketStore()
const authStore = useAuthStore()

onMounted(() => {
  socketStore.subscribe(`/user/queue/room/${roomId}`, () => {})
  socketStore.subscribe(`/room/${roomId}`, () => {})

  // 방 입장 로직
  socketStore.send(`/app/room/${roomId}`, authStore.accessToken || '', null)
})

// 사람 리스트를 출력함
const userList = []
// 채팅은 아직 미구현으로 하쟈..
</script>

<template>
  <div>
    <div>
      <button>나가기</button>
      <div>제목</div>
      <button>시작하기</button>
    </div>
    <div>
      <div v-for="user in userList">
        <div>사진</div>
        <div>사용자 이름</div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
