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
  </div>
</template>

<script setup lang="ts">
import { PlanProgress, progressTextMap } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const planId = computed(() => route.params.planId as string)
const currentProgress = ref(PlanProgress.SELECT_ACCOMMODATION)
const endProgress = PlanProgress.COMPLETE
const socketStore = useSocketStore()
const authStore = useAuthStore()

const processMessage = (message: string) => {
  alert(message)
}

const processJoinMessage = (body: string) => {
  const response: PlanProgress = JSON.parse(body)

  console.log('현재 : ' + currentProgress.value)
  console.log('다음 : ' + response)

  currentProgress.value = response
}

onMounted(() => {
  try {
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
</script>

<style scoped>
.container {
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
</style>
