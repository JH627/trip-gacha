<script setup lang="ts">
import { authApi } from '@/api/axios'
import type { ScheduleDetail } from '@/types/trip'
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Tabs, Modal } from 'ant-design-vue'
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps'

const defaultCoordinate = {
  lat: 33.450701,
  lng: 126.570667,
}

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

const showModal = () => {
  isModalVisible.value = true
}

const closeModal = () => {
  isModalVisible.value = false
}

const mapOptions = {
  scrollwheel: true,
  disableDoubleClickZoom: false,
  disableDoubleTapZoom: false,
  disableTwoFingerTapZoom: false,
  draggable: true,
  keyboardShortcuts: true,
  zoomable: true,
  center: defaultCoordinate,
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
        <div class="schedule-route-item map-container desktop-only">
          <div class="schedule-route-item-title">
            <h3>지도</h3>
          </div>
          <div class="map-wrapper">
            <KakaoMap
              :lat="defaultCoordinate.lat"
              :lng="defaultCoordinate.lng"
              :draggable="true"
              :zoom="3"
              :options="mapOptions"
              class="kakao-map"
            >
              <KakaoMapMarker :lat="defaultCoordinate.lat" :lng="defaultCoordinate.lng" />
            </KakaoMap>
          </div>
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
      :style="{ top: 0, padding: 0 }"
      :bodyStyle="{ padding: 0, height: '100vh' }"
      class="map-modal"
      @cancel="closeModal"
    >
      <div class="modal-map-wrapper">
        <KakaoMap
          :lat="defaultCoordinate.lat"
          :lng="defaultCoordinate.lng"
          :draggable="true"
          :zoom="3"
          :options="mapOptions"
          class="modal-kakao-map"
        >
          <KakaoMapMarker :lat="defaultCoordinate.lat" :lng="defaultCoordinate.lng" />
        </KakaoMap>
      </div>
    </Modal>
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
  flex-wrap: wrap;
  flex-direction: row;
  gap: 32px;
  margin-top: 24px;
}

.schedule-route-item {
  width: 100%;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.schedule-route-item-title {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.schedule-route-item-title h3 {
  font-size: 1.4rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.spots-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
}

.spots-container::-webkit-scrollbar {
  width: 6px;
}

.spots-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.spots-container::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.spots-container::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.spot-item {
  display: flex;
  gap: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.spot-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #e6e6e6;
}

.spot-image {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.spot-info {
  margin-top: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.spot-info h4 {
  margin: 0;
  font-size: 1.3rem;
  color: #1a1a1a;
  font-weight: 600;
}

.spot-info p {
  margin: 0;
  color: #666;
  font-size: 1rem;
  line-height: 1.6;
}

.spot-address {
  color: #888;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.spot-time {
  color: #666;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.map-wrapper {
  width: 100%;
  height: 500px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.kakao-map {
  width: 100%;
  height: 100%;
}

.map-container {
  min-height: 600px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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

@media (min-width: 1024px) {
  .schedule-route {
    flex-direction: row;
  }

  .schedule-route-item {
    width: calc(50% - 16px);
  }
}

.mobile-map-button {
  display: none;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.mobile-map-button:hover {
  background-color: #40a9ff;
}

@media (max-width: 1023px) {
  .desktop-only {
    display: none;
  }

  .mobile-map-button {
    display: block;
  }

  .schedule-route-item-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
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

.modal-kakao-map {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}
</style>
