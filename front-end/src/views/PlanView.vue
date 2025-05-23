<template>
  <div>
    <div class="progress-header">
      <button @click="goPrev">뒤로</button> <button @click="goNext">앞으로</button>
    </div>
    <div>{{ currentProgress }}</div>
  </div>
</template>
<script setup lang="ts">
import { PlanProgress } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const planId = computed(() => route.params.planId as string)
const currentProgress = ref(PlanProgress.SELECT_ACCOMMODATION)

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

<style scoped></style>
