<script setup lang="ts">
import { onBeforeUnmount, onBeforeMount, ref } from 'vue'
import { useSocketStore } from '@/stores/socket'
import { useAuthStore } from '@/stores/auth'
//import Gloves from '@/assets/gloves.png' -> 버튼 색 밝아지면 검은 아이콘

const socketStore = useSocketStore()
const authStore = useAuthStore()

onBeforeMount(async () => {
  socketStore.initializeClient()
  await socketStore.connect(authStore.accessToken || '', false)
})

onBeforeUnmount(() => {
  socketStore.disconnect()
})
</script>

<template>
  <div class="socket-layout">
    <router-view />
  </div>
</template>

<style scoped>
.socket-layout {
  min-height: 100vh;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  background-color: #f5f5f5;
  padding: 1rem;
}
</style>
