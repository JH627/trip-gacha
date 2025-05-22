<script setup lang="ts">
import { authApi } from '@/api/axios'
import type { ScheduleInfo } from '@/types/trip'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ScheduleCard from '@/components/schedule/ScheduleCard.vue'

const router = useRouter()
const scheduleList = ref<ScheduleInfo[]>([])

const goToScheduleDetail = (scheduleId: number) => {
  router.push(`/schedule/${scheduleId}`)
}

onMounted(async () => {
  try {
    const schedules = await authApi.get('/trip/schedule')
    scheduleList.value = schedules.data.result
  } catch (error) {
    console.log(error)
  }
})
</script>

<template>
  <div class="main-container">
    <h2 class="title">내 여행일정</h2>
    <div class="schedule-list-container">
      <ScheduleCard
        v-for="schedule in scheduleList"
        :key="schedule.scheduleId"
        :schedule="schedule"
        @click="goToScheduleDetail"
      />
    </div>
  </div>
</template>

<style scoped>
.main-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 0px;
  min-height: calc(100vh - 64px);
}

.title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 24px;
  padding-left: 8px;
}

.schedule-list-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
  width: 100%;
  padding: 16px;
}
</style>
