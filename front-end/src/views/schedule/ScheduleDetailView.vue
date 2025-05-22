<script setup lang="ts">
import { authApi } from '@/api/axios'
import type { ScheduleDetail } from '@/types/trip'
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const scheduleId = route.params.scheduleId
const schedule = ref<ScheduleDetail | null>(null)
const selectedDay = ref(1)

const availableDays = computed(() => {
  if (!schedule.value) return []
  const days = new Set(schedule.value.scheduleDetailItems.map((item) => item.day))
  return Array.from(days).sort((a, b) => a - b)
})

const selectedDaySpots = computed(() => {
  if (!schedule.value) return []
  return schedule.value.scheduleDetailItems
    .filter((item) => item.day === selectedDay.value)
    .sort((a, b) => a.order - b.order)
})

onMounted(async () => {
  try {
    const response = await authApi.get(`/trip/schedule/${scheduleId}`)
    schedule.value = response.data.result
    if (schedule.value && schedule.value.scheduleDetailItems.length > 0) {
      selectedDay.value = schedule.value.scheduleDetailItems[0].day
    }
  } catch (error) {
    console.error('일정 정보를 불러오는데 실패했습니다:', error)
  }
})
</script>

<template>
  <div class="main-container">
    <div v-if="schedule" class="schedule-detail">
      <h1 class="title">{{ schedule.title }}</h1>
      <div class="schedule-info">
        <div class="schedule-content">
          <div class="info-item">
            <h3>여행 기간</h3>
            <p>{{ schedule.startDate }} ~ {{ schedule.endDate }}</p>
          </div>

          <!-- 날짜 선택 -->
          <div class="day-selector">
            <button
              v-for="day in availableDays"
              :key="day"
              :class="['day-button', { active: selectedDay === day }]"
              @click="selectedDay = day"
            >
              {{}}
              {{ day }}일차
            </button>
          </div>

          <!-- 여행 일정과 지도 -->
          <div class="schedule-route">
            <!-- 여행 일정 -->
            <div class="schedule-route-item spots-list">
              <div class="schedule-route-item-title">
                <h3>방문 장소</h3>
              </div>
              <div class="spots-container">
                <div v-for="item in selectedDaySpots" :key="item.spotInfo.spotId" class="spot-item">
                  <img :src="item.spotInfo.img" :alt="item.spotInfo.name" class="spot-image" />
                  <div class="spot-info">
                    <h4>{{ item.spotInfo.name }}</h4>
                    <p>{{ item.spotInfo.content }}</p>
                    <p class="spot-address">{{ item.spotInfo.address }}</p>
                    <p class="spot-time">운영시간: {{ item.spotInfo.workTime }}</p>
                  </div>
                </div>
              </div>
            </div>
            <!-- 지도 -->
            <div class="schedule-route-item map-container">
              <div class="schedule-route-item-title">
                <h3>지도</h3>
              </div>
              <div class="map-placeholder">지도가 표시될 영역</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="loading">로딩 중...</div>
  </div>
</template>

<style scoped>
.main-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 24px;
  min-height: calc(100vh - 64px);
}

.title {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 32px;
  color: #1a1a1a;
}

.schedule-detail {
  background-color: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.schedule-info {
  display: flex;
  gap: 32px;
}

.schedule-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-item {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.info-item h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #4a4a4a;
  margin-bottom: 8px;
}

.info-item p {
  font-size: 1rem;
  color: #1a1a1a;
}

.loading {
  text-align: center;
  padding: 48px;
  font-size: 1.2rem;
  color: #666;
}

.day-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.day-button {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.day-button:hover {
  background: #f5f5f5;
}

.day-button.active {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;
}

.schedule-route {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}

.schedule-route-item {
  width: 48%;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.schedule-route-item-title {
  margin-bottom: 16px;
}

.spots-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.spot-item {
  display: flex;
  gap: 16px;
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.spot-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.spot-info {
  flex: 1;
}

.spot-info h4 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #1a1a1a;
}

.spot-info p {
  margin: 4px 0;
  color: #666;
  font-size: 0.9rem;
}

.spot-address {
  color: #888;
  font-size: 0.85rem;
}

.map-container {
  min-height: 500px;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  min-height: 400px;
  background: #e9ecef;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}
</style>
