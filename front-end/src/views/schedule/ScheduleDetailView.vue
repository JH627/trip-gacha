<script setup lang="ts">
import { authApi } from '@/api/axios'
import type { ScheduleDetail } from '@/types/trip'
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Tabs, Modal } from 'ant-design-vue'
import ScheduleMap from '@/components/schedule/ScheduleMap.vue'
import ScheduleSpotList from '@/components/schedule/ScheduleSpotList.vue'

// 컴포넌트 상태 관리
const route = useRoute()
const scheduleId = route.params.scheduleId
const schedule = ref<ScheduleDetail | null>(null)
const selectedDay = ref(1)
const isModalVisible = ref(false)

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

const selectedDayCoordinates = computed(() => {
  if (!schedule.value) return []
  return selectedDaySpots.value.map(spot => ({
    lat: spot.spotInfo.latitude,
    lng: spot.spotInfo.longitude
  }))
})

// 모달 관련 메서드
const showModal = () => {
  isModalVisible.value = true
}

const closeModal = () => {
  isModalVisible.value = false
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
            <button class="mobile-map-button" @click="showModal">지도 보기</button>
          </div>
          <ScheduleSpotList :spots="selectedDaySpots" />
        </div>
        <!-- 지도 -->
        <div class="schedule-route-item map-container desktop-only">
          <div class="schedule-route-item-title">
            <h3>지도</h3>
          </div>
          <ScheduleMap :coordinates="selectedDayCoordinates" />
        </div>
      </div>
    </div>
    <div v-else class="loading">로딩 중...</div>

    <!-- 모바일용 지도 Modal -->
    <Modal
      v-model:visible="isModalVisible"
      title="지도"
      :footer="null"
      :width="'100%'"
      :style="{ top: 0, padding: 0, paddingTop: '20px' }"
      :bodyStyle="{ padding: 0, height: 'auto' }"
      class="map-modal"
      @cancel="closeModal"
    >
      <div class="modal-map-wrapper">
        <ScheduleMap :coordinates="selectedDayCoordinates" />
      </div>
    </Modal>
  </div>
</template>

<style scoped>
.main-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 16px;
  min-height: calc(100vh - 64px);
  background-color: #f5f5f5;
}

.title-container {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.title {
  font-size: 2rem;
  font-weight: 800;
  color: #1a1a1a;
  margin: 0;
}

.period {
  font-size: 0.9rem;
  color: #666;
}

.schedule-detail {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.day-tabs {
  margin-bottom: 16px;
  background-color: white;
  padding: 0 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

:deep(.ant-tabs-tab) {
  padding: 8px 16px;
  font-size: 1rem;
  text-align: center;
}

:deep(.ant-tabs-tab-active) {
  color: #1890ff;
}

.schedule-route {
  display: flex;
  gap: 20px;
  margin-top: 16px;
}

.schedule-route-item {
  width: 100%;
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8e8e8;
}

.schedule-route-item-title {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-route-item-title h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.map-container {
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.loading {
  text-align: center;
  padding: 32px;
  font-size: 1.1rem;
  color: #666;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.mobile-map-button {
  display: none;
  padding: 6px 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: background-color 0.3s;
}

.mobile-map-button:hover {
  background-color: #40a9ff;
}

@media (min-width: 1024px) {
  .schedule-route {
    flex-direction: row;
  }

  .schedule-route-item {
    width: calc(50% - 10px);
  }
}

@media (max-width: 1023px) {
  .desktop-only {
    display: none;
  }

  .mobile-map-button {
    display: block;
  }

  .main-container {
    padding: 16px 12px;
  }

  .schedule-detail {
    padding: 16px;
  }

  .title {
    font-size: 1.8rem;
  }
}

.map-modal :deep(.ant-modal) {
  max-width: 100%;
  margin: 0;
  padding: 0;
  top: 0;
  height: 100vh;
}

.map-modal :deep(.ant-modal-content) {
  height: 100vh;
  border-radius: 0;
  padding: 0;
}

.map-modal :deep(.ant-modal-header) {
  margin: 0;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.map-modal :deep(.ant-modal-body) {
  padding: 0;
  height: calc(100vh - 55px);
}

.modal-map-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
