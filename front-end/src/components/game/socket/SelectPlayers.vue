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
6. players-select-complete 이벤트 일으키기
-->
<template>
  <div class="game-selection">
    <div class="game-grid">
      <div
        v-for="player in playerList"
        :key="player.socketId"
        class="game-card"
        @click="$emit('selectGame')"
      >
        <img class="profile-img" :src="player.img" />
        <span class="game-name">{{ player.nickname }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { defineProps, onMounted, ref, type Ref } from 'vue'
import type { SocketUserInfo } from '@/socket/webSocket'

const props = defineProps<{
  planId: string
  gameType: string
}>()

const authStore = useAuthStore()
const socketStore = useSocketStore()

const playerList: Ref<SocketUserInfo[]> = ref([])

const processMessage = (body: string) => {
  const response: SocketUserInfo[] = JSON.parse(body)
  playerList.value = response
}

onMounted(() => {
  socketStore.subscribe(`/user/queue/user-list`, processMessage)

  socketStore.send(`/plan/user-list`, authStore.accessToken || ``, props.planId)
})
</script>

<style scoped>
.game-selection {
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
  color: #333;
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

.game-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border-color: #4f46e5;
}

.game-card:active {
  transform: translateY(-2px);
}

.player-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background-color: #f3f4f6;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.game-card:hover .game-icon {
  background-color: #4f46e5;
  color: white;
}

.game-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .game-card {
    width: 120px;
    height: 100px;
  }

  .game-icon {
    width: 40px;
    height: 40px;
  }

  .game-icon svg {
    width: 24px;
    height: 24px;
  }

  .game-name {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .game-grid {
    gap: 12px;
  }

  .game-card {
    width: 100px;
    height: 90px;
  }

  .game-icon {
    width: 35px;
    height: 35px;
  }

  .game-icon svg {
    width: 20px;
    height: 20px;
  }
}
</style>
