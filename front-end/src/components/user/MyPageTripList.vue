<script setup lang="ts">
import { useRouter } from 'vue-router'
import { authApi } from '@/api/axios'
import type { ScheduleInfo } from '@/types/trip'
import { onMounted, ref } from 'vue'

const router = useRouter()
const scheduleList = ref<ScheduleInfo[]>([])

const goToScheduleDetail = (scheduleId: number) => {
  router.push(`/schedule/${scheduleId}`)
}

onMounted(async () => {
  try {
    const schedules = await authApi.get('/trip/schedule')
    scheduleList.value = schedules.data.result.slice(0, 4) // 최대 4개만 표시
  } catch (error) {
    console.error('일정 정보를 불러오는데 실패했습니다:', error)
  }
})
</script>

<template>
  <div class="card-header">
    <span>내 여행일정</span>
    <a @click="router.push('/schedule')">모두 보기</a>
  </div>
  <div class="trips-list">
    <div
      v-for="schedule in scheduleList"
      :key="schedule.scheduleId"
      class="trip-box"
      @click="goToScheduleDetail(schedule.scheduleId)"
    >
      <img :src="schedule.destinationImg" alt="여행 이미지" />
      <div class="trip-box-content">
        <p>{{ schedule.title }}</p>
        <p>{{ schedule.destination }}</p>
        <p>{{ schedule.startDate }} ~ {{ schedule.endDate }}</p>
      </div>
    </div>
    <div v-if="scheduleList.length === 0" class="trip-box empty">여행 일정이 없습니다</div>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 4px;
}

.card-header span {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
}

.card-header a {
  cursor: pointer;
  color: #228be6;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 6px;
}

.card-header a:hover {
  color: #74c0fc;
  background: rgba(34, 139, 230, 0.1);
}

.trips-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 4px;
}

.trip-box {
  background: white;
  border-radius: 12px;
  padding: 0;
  width: 300px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.trip-box:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.trip-box img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.trip-box:hover img {
  transform: scale(1.05);
}

.trip-box-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.trip-box-content p {
  margin: 0;
  line-height: 1.4;
}

.trip-box-content p:first-child {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.trip-box-content p:nth-child(2) {
  font-size: 1rem;
  color: #4a4a4a;
  font-weight: 500;
}

.trip-box-content p:nth-child(3) {
  font-size: 0.85rem;
  color: #666;
  font-weight: 400;
}

.trip-box.empty {
  background: #f8f9fa;
  padding: 24px;
  text-align: center;
  color: #868e96;
  font-weight: 500;
  cursor: default;
  width: 100%;
}

.trip-box.empty:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
</style>
