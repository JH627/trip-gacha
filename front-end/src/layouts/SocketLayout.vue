<script setup lang="ts">
import { onBeforeUnmount, onBeforeMount, ref } from 'vue'
import { useSocketStore } from '@/stores/socket'
import { useAuthStore } from '@/stores/auth'
//import Gloves from '@/assets/gloves.png' -> 버튼 색 밝아지면 검은 아이콘
import WhiteGloves from '@/assets/gloves-white.png'
import GameModal from '@/components/game/GameModal.vue'

const socketStore = useSocketStore()
const authStore = useAuthStore()

onBeforeMount(async () => {
  socketStore.initializeClient()
  await socketStore.connect(authStore.accessToken || '', false)
})

onBeforeUnmount(() => {
  socketStore.disconnect()
})

const isOpen = ref(false)

const openModal = () => {
  console.log('open!')
  isOpen.value = true
}

const closeModal = () => {
  isOpen.value = false
}
</script>

<template>
  <div class="socket-layout">
    <router-view />
    <div class="game-button" @click="openModal">
      <img class="game-icon" :src="WhiteGloves" />
    </div>
    <GameModal :is-open="isOpen" :is-socket="false" @close="closeModal" />
  </div>
</template>

<style scoped>
.socket-layout {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  background-color: #f5f5f5;
  padding: 1rem;
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
