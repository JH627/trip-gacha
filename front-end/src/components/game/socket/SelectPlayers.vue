<!--
1. props로 planId, 게임 타입을 받는다.
2. onMounted일 때, 'user/queue/game' 을 구독 -> plan에 있는 UserList를 불러오는 요청 -> send.'/plan/user-list'
3. 소켓 서버에서 실제 리스트(나를 제외) 반환 받음

리스트 타입은 
interface SocketRoomUser {
  userId: string
  nickname: string
  img: string
} 의 리스트임

4. 나를 제외한 실제 리스트를 카드 형태로 출력 -> 이 때, 클릭하면 선택 상태가 됨 (다중 선택 가능)
참고로 출력하는 컨테이너는 아래 templete를 따르면 됨

5. 밑의 초대 버튼 클릭 시, /game/invite 요청 + { planId, 선택한 유저 아이디 리스트, 게임 타입} -> 이걸 받는 건 Modal에서할거니 요청만 보내면 됨
응답 받은 사람들은 각자, 모달 isOpen 강제 개방 + selected뭐시기 강제 정함 + 게임 실행 초기화면 진입

6. players-select-complete 이벤트 일으키기
-->
<template>
  <div class="game-selection">
    <div class="game-grid">
      <div
        v-for="player in playerList"
        :key="player.userId"
        class="game-card"
        :class="{ selected: selectedUserIds.includes(player.userId) }"
        @click="togglePlayerSelection(player.userId)"
      >
        <img class="profile-img" :src="player.img" />
        <span class="game-name">{{ player.nickname }}</span>
      </div>
    </div>
    <div class="invite-button-wrapper">
      <button class="invite-button" @click="invitePlayers" :disabled="selectedUserIds.length === 0">
        초대하기 ({{ selectedUserIds.length }}명)
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { computed, defineProps, onMounted, ref, type Ref } from 'vue'
import type { SocketRoomUser, SocketUserInfo } from '@/socket/webSocket'
import { useRoute } from 'vue-router'

const route = useRoute()
const planId = computed(() => route.params.planId as string)

const props = defineProps<{
  gameType: string
}>()

const emit = defineEmits<{
  (e: 'players-select-complete'): void
}>()

const authStore = useAuthStore()
const socketStore = useSocketStore()

const playerList: Ref<SocketRoomUser[]> = ref([])
const selectedUserIds = ref<string[]>([])

const processMessage = (body: string) => {
  const response: SocketRoomUser[] = JSON.parse(body)
  playerList.value = response
}

const togglePlayerSelection = (userId: string) => {
  console.log('선태 유저 : ' + userId)

  const index = selectedUserIds.value.indexOf(userId)
  if (index === -1) {
    selectedUserIds.value.push(userId)
  } else {
    selectedUserIds.value.splice(index, 1)
  }

  console.log(selectedUserIds.value)
}

const invitePlayers = () => {
  console.log(selectedUserIds.value)

  socketStore.send(`/app/game/invite`, authStore.accessToken || '', {
    planId: planId.value,
    userIds: selectedUserIds.value,
    gameType: props.gameType,
  })

  emit('players-select-complete')
}

onMounted(() => {
  socketStore.subscribe(`/user/queue/user-list`, processMessage)

  console.log('props.planId:', planId.value)

  socketStore.send(`/app/plan/user-list`, authStore.accessToken || ``, planId.value)
})
</script>

<style scoped>
.game-selection {
  padding: 20px;
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
  justify-items: center;
}

.game-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 120px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  gap: 12px;
}

.game-card.selected {
  border-color: #4f46e5;
  background-color: #eef2ff;
}

.profile-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.game-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
}

.invite-button-wrapper {
  margin-top: 20px;
  text-align: center;
}

.invite-button {
  padding: 10px 20px;
  background-color: #4f46e5;
  color: white;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.invite-button:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}
</style>
