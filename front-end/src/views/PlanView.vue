<template>
  <div class="container">
    <div class="progress-header">
      <button class="nav-button prev-button" @click="goPrev">뒤로</button>
      <div class="progress-text">{{ progressTextMap[currentProgress] }}</div>
      <button v-if="currentProgress !== endProgress" class="nav-button next-button" @click="goNext">
        앞으로
      </button>
      <button v-else class="nav-button next-button" @click="goNext">완료</button>
    </div>
    <div class="game-button" @click="openModal">
      <img class="game-icon" :src="WhiteGloves" />
    </div>
    <GameModal
      :is-open="isOpen"
      :is-socket="true"
      :invited-game-type="invitedGameType"
      @close="closeModal"
    />
  </div>
</template>

<script setup lang="ts">
import { PlanProgress, progressTextMap } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { computed, onMounted, ref, type Ref } from 'vue'
import { useRoute } from 'vue-router'
import WhiteGloves from '@/assets/gloves-white.png'
import GameModal from '@/components/game/GameModal.vue'
import { Game } from '@/components/game/Game'

const route = useRoute()
const planId = computed(() => route.params.planId as string)
const currentProgress = ref(PlanProgress.SELECT_ACCOMMODATION)
const endProgress = PlanProgress.COMPLETE
const socketStore = useSocketStore()
const authStore = useAuthStore()

const invitedGameType: Ref<Game> = ref(Game.DEFAULT)

const processMessage = (message: string) => {
  alert(message)
}

const processJoinMessage = (body: string) => {
  const response: PlanProgress = JSON.parse(body)

  console.log('현재 : ' + currentProgress.value)
  console.log('다음 : ' + response)

  currentProgress.value = response
}

const processGameMessage = (body: string) => {
  const gameType = JSON.parse(body)
  // 모달에 선택한 게임 전달
  console.log(('초대 당한 게임 : ' + gameType) as Game)
  invitedGameType.value = gameType as Game
  // 모달 개방
  openModal()
}

onMounted(() => {
  try {
    socketStore.subscribe(`/user/queue/game`, processGameMessage)
    socketStore.subscribe(`/user/queue/plan`, processMessage)
    socketStore.subscribe(`/topic/plan/${planId.value}`, processJoinMessage)

    socketStore.send(`/app/plan/join/${planId.value}`, authStore.accessToken || '', null)
  } catch (error) {
    window.location.href = '/trip/lobby'
  }
})

const goNext = () => {
  socketStore.send(`/app/plan/move/${planId.value}`, authStore.accessToken || '', true)
}

const goPrev = () => {
  socketStore.send(`/app/plan/move/${planId.value}`, authStore.accessToken || '', false)
}

const isOpen = ref(false)

const openModal = () => {
  console.log('open!')
  isOpen.value = true
}

const closeModal = () => {
  isOpen.value = false
  invitedGameType.value = Game.DEFAULT
}
</script>

<style scoped>
.container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 15px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  box-sizing: border-box;
}

.nav-button {
  padding: 8px 16px;
  border: 1px solid #d0d0d0;
  border-radius: 6px;
  background-color: #f8f9fa;
  color: #333;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.nav-button:hover {
  background-color: #e9ecef;
  border-color: #adb5bd;
}

.nav-button:active {
  background-color: #dee2e6;
}

.prev-button {
  order: 1;
}

.progress-text {
  order: 2;
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.next-button {
  order: 3;
}

.game-button {
  position: absolute;
  bottom: 50px;
  right: 50px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ff4757;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.game-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.6);
}

.game-button:active {
  transform: scale(0.95);
}

.game-icon {
  width: 36px;
  height: 36px;
  color: white;
  pointer-events: none;
}
</style>
