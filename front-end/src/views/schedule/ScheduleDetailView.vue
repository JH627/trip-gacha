<script setup lang="ts">
import { authApi } from '@/api/axios'
import type { ScheduleDetail } from '@/types/trip'
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Tabs } from 'ant-design-vue'

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

const getDateForDay = (day: number) => {
  if (!schedule.value?.startDate) return ''
  const startDate = new Date(schedule.value.startDate)
  const date = new Date(startDate)
  date.setDate(startDate.getDate() + day - 1)
  return date.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' })
}

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
      <div class="title-container">
        <h1 class="title">{{ schedule.title }}</h1>
        <span class="period">{{ schedule.startDate }} ~ {{ schedule.endDate }}</span>
      </div>

      <!-- 날짜 선택 -->
      <Tabs v-model:activeKey="selectedDay" class="day-tabs">
        <Tabs.TabPane v-for="day in availableDays" :key="day" :tab="`${day}일차`" />
      </Tabs>

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

.title-container {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 32px;
}

.title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1a1a1a;
  margin: 0;
}

.period {
  font-size: 1rem;
  color: #666;
}

.schedule-detail {
  background-color: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.day-tabs {
  margin-bottom: 24px;
}

:deep(.ant-tabs-tab) {
  padding: 12px 24px;
  font-size: 1.1rem;
  white-space: pre-line;
  text-align: center;
  line-height: 1.4;
}

:deep(.ant-tabs-tab-active) {
  color: #1890ff;
}

.schedule-route {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 24px;
}

.schedule-route-item {
  width: 48%;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.schedule-route-item-title {
  margin-bottom: 20px;
}

.schedule-route-item-title h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.spots-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.spot-item {
  display: flex;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;
}

.spot-item:hover {
  transform: translateY(-2px);
}

.spot-image {
  width: 140px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.spot-info {
  flex: 1;
}

.spot-info h4 {
  margin: 0 0 12px 0;
  font-size: 1.2rem;
  color: #1a1a1a;
  font-weight: 600;
}

.spot-info p {
  margin: 6px 0;
  color: #666;
  font-size: 0.95rem;
  line-height: 1.5;
}

.spot-address {
  color: #888;
  font-size: 0.9rem;
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

.loading {
  text-align: center;
  padding: 48px;
  font-size: 1.2rem;
  color: #666;
}
</style>
