<script setup lang="ts">
import type { ScheduleDetail, SpotInfo } from '@/types/trip'
import { onMounted, ref, computed, watch, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { Tabs, Modal, message } from 'ant-design-vue'
import ScheduleMap from '@/components/schedule/ScheduleMap.vue'
import ScheduleSpotList from '@/components/schedule/ScheduleSpotList.vue'
import SpotSearchModal from '@/components/schedule/SpotSearchModal.vue'
import { EditOutlined, CheckOutlined, CloseOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { useSocketStore } from '@/stores/socket'
import { useAuthStore } from '@/stores/auth'
import { useScheduleStore } from '@/stores/schedule'

// 컴포넌트 상태 관리
const route = useRoute()
const planId = route.params.planId as string
const schedule = ref<ScheduleDetail | null>(null)
const selectedDay = ref(1)
const isModalVisible = ref(false)
const isEditMode = ref(false)
const isSaving = ref(false)
const editedTitle = ref('')
const editedStartDate = ref('')
const editedEndDate = ref('')
const isSpotSearchModalVisible = ref(false)

// 스토어
const socketStore = useSocketStore()
const authStore = useAuthStore()

// 여행 일정 날짜 계산
const availableDays = computed(() => {
  if (!schedule.value) return []
  // 시작일과 종료일 사이의 모든 날짜를 생성
  const startDate = new Date(schedule.value.startDate)
  const endDate = new Date(schedule.value.endDate)
  const days = []

  // 날짜 차이 계산
  const diffTime = endDate.getTime() - startDate.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1

  // 1부터 diffDays까지의 배열 생성
  for (let i = 1; i <= diffDays; i++) {
    days.push(i)
  }

  return days
})

// 선택된 날짜의 장소 목록
const selectedDaySpots = computed(() => {
  if (!schedule.value) return []
  return schedule.value.scheduleDetailItems
    .filter((item) => item.day === selectedDay.value)
    .sort((a, b) => a.order - b.order)
})

// 선택된 날짜의 좌표 목록
const selectedDayCoordinates = computed(() => {
  if (!schedule.value) return []
  return selectedDaySpots.value.map((spot) => ({
    lat: spot.spotInfo.latitude,
    lng: spot.spotInfo.longitude,
  }))
})

const scheduleStore = useScheduleStore()

// 소켓 메시지 처리
const processFinalMessage = (body: string) => {
  try {
    const response: ScheduleDetail = JSON.parse(body)
    schedule.value = response
    scheduleStore.saveSchedule(response)

    editedTitle.value = response.title

    if (schedule.value && schedule.value.scheduleDetailItems.length > 0) {
      selectedDay.value = schedule.value.scheduleDetailItems[0].day
    }

    // 편집 모드일 때 편집 필드 업데이트
    if (isEditMode.value && schedule.value) {
      editedTitle.value = schedule.value.title
      editedStartDate.value = schedule.value.startDate
      editedEndDate.value = schedule.value.endDate
    }

    console.log('일정 데이터 업데이트:', response)
  } catch (error) {
    console.error('일정 데이터 파싱 오류:', error)
    message.error('일정 데이터를 불러오는데 실패했습니다')
  }
}

// 일정 공유 상태 변경 (HTTP API 유지 - 소켓에 없는 기능)
const toggleShare = async () => {
  if (!schedule.value) return

  try {
    // 임시로 HTTP API 사용 (소켓 API가 없는 경우)
    // await authApi.post(`/trip/schedule/share/${scheduleId}`)

    // 로컬 상태 업데이트 (실제로는 서버 응답 후 업데이트)
    schedule.value.shared = !schedule.value.shared
    message.success('일정 공유 상태가 변경되었습니다')
  } catch (error) {
    console.error('일정 공유 상태 변경에 실패했습니다:', error)
    message.error('일정 공유 상태 변경에 실패했습니다')
  }
}

// 일정 데이터 가져오기 (소켓 사용)
const fetchScheduleData = async () => {
  if (socketStore.connected) {
    socketStore.send(`/app/plan/final/get/${planId}`, authStore.accessToken || '', null)
  }
}

// 수정 모드 토글
const toggleEditMode = () => {
  if (!isEditMode.value) {
    isEditMode.value = true
    // 편집 모드 진입 시 현재 값으로 초기화
    if (schedule.value) {
      editedTitle.value = schedule.value.title
      editedStartDate.value = schedule.value.startDate
      editedEndDate.value = schedule.value.endDate
    }
  } else {
    isEditMode.value = false
    // 수정 모드 종료 시 데이터 새로고침
    fetchScheduleData()
  }
}

// 모달 관련 메서드
const showModal = () => {
  isModalVisible.value = true
}

const closeModal = () => {
  isModalVisible.value = false
}

const handleMoveSpot = (spotId: number, targetDay: number) => {
  if (!schedule.value) return

  // 로컬 상태 업데이트
  const spotToMove = schedule.value.scheduleDetailItems.find(
    (item) => item.spotInfo.spotId === spotId,
  )

  if (spotToMove) {
    spotToMove.day = targetDay
    // 순서 재정렬
    const daySpots = schedule.value.scheduleDetailItems.filter((item) => item.day === targetDay)
    daySpots.forEach((spot, index) => {
      spot.order = index + 1
    })
  }
}

// 최소 일수 계산 (현재 관광지를 넣은 상태에서의 최소 기준)
const minDays = computed(() => {
  if (!schedule.value) return 1
  const days = new Set(schedule.value.scheduleDetailItems.map((item) => item.day))
  return Math.max(...Array.from(days))
})

// 시작일과 종료일 변경 시 최소 일수 체크
const handleDateChange = () => {
  const start = new Date(editedStartDate.value)
  const end = new Date(editedEndDate.value)
  const diffDays = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1

  if (diffDays < minDays.value) {
    message.error(`일정은 최소 ${minDays.value}일 이상이어야 합니다`)
    // 원래 날짜로 되돌리기
    editedStartDate.value = schedule.value?.startDate || ''
    editedEndDate.value = schedule.value?.endDate || ''
    return
  }

  // 로컬 상태 업데이트
  if (schedule.value) {
    schedule.value = {
      ...schedule.value,
      startDate: editedStartDate.value,
      endDate: editedEndDate.value,
    }
  }
}

// selectedDay가 availableDays 범위를 벗어나지 않도록 감시
watch(
  () => availableDays.value,
  (newDays) => {
    if (newDays.length > 0) {
      const maxDay = Math.max(...newDays)
      if (selectedDay.value > maxDay) {
        selectedDay.value = maxDay
      }
    }
  },
  { immediate: true },
)

// 제목 변경 실시간 체크
watch(
  () => editedTitle.value,
  (newTitle) => {
    if (schedule.value) {
      schedule.value.title = newTitle
    }
  },
)

// 일정 저장 (소켓 사용)
const saveSchedule = async () => {
  if (!schedule.value) return

  isSaving.value = true
  try {
    // 일정 기본 정보와 순서 업데이트
    const scheduleItems = schedule.value.scheduleDetailItems.map((spot) => ({
      spotId: spot.spotInfo.spotId,
      day: spot.day,
      sequence: spot.order,
    }))

    // 요청 보내기 전 날짜별로 sequence는 1부터 시작하도록 변경
    const dayGroups = scheduleItems.reduce(
      (acc, spot) => {
        if (!acc[spot.day]) {
          acc[spot.day] = []
        }
        acc[spot.day].push(spot)
        return acc
      },
      {} as Record<number, typeof scheduleItems>,
    )

    // 각 날짜별로 sequence 재정렬
    Object.values(dayGroups).forEach((spots) => {
      spots.forEach((spot, index) => {
        spot.sequence = index + 1
      })
    })

    // 소켓으로 업데이트 요청
    const updateData: ScheduleDetail = {
      ...schedule.value,
      title: editedTitle.value,
      startDate: editedStartDate.value,
      endDate: editedEndDate.value,
      scheduleDetailItems: schedule.value.scheduleDetailItems.map((item, index) => ({
        ...item,
        order:
          scheduleItems.find((si) => si.spotId === item.spotInfo.spotId)?.sequence || index + 1,
      })),
    }

    socketStore.send(`/app/plan/final/update/${planId}`, authStore.accessToken || '', updateData)

    message.success('일정이 저장되었습니다')
    isEditMode.value = false
  } catch (error) {
    console.error('일정 저장에 실패했습니다:', error)
    message.error('일정 저장에 실패했습니다')
  } finally {
    isSaving.value = false
  }
}

// 여행지 제거 처리
const handleRemoveSpot = (spotId: number) => {
  if (!schedule.value) return

  // 로컬 상태에서 해당 여행지 제거
  schedule.value.scheduleDetailItems = schedule.value.scheduleDetailItems.filter(
    (item) => item.spotInfo.spotId !== spotId,
  )

  // 순서 재정렬
  const dayGroups = schedule.value.scheduleDetailItems.reduce(
    (acc, spot) => {
      if (!acc[spot.day]) {
        acc[spot.day] = []
      }
      acc[spot.day].push(spot)
      return acc
    },
    {} as Record<number, typeof schedule.value.scheduleDetailItems>,
  )

  Object.entries(dayGroups).forEach(([day, spots]) => {
    spots.forEach((spot, index) => {
      spot.order = index + 1
    })
  })
}

// 여행지 추가 처리
const handleAddSpot = (spot: SpotInfo, day: number) => {
  if (!schedule.value) return

  // 해당 날짜의 마지막 순서 찾기
  const daySpots = schedule.value.scheduleDetailItems.filter((item) => item.day === day)
  const lastOrder = daySpots.length > 0 ? Math.max(...daySpots.map((item) => item.order)) : 0

  // 새로운 여행지 추가
  schedule.value.scheduleDetailItems.push({
    day,
    spotInfo: spot,
    order: lastOrder + 1,
  })
}

// 컴포넌트 마운트 시 소켓 연결 및 데이터 로드
onMounted(() => {
  if (socketStore.connected) {
    socketStore.subscribe(`/topic/plan/final/${planId}`, processFinalMessage)
  }
  // 소켓 구독

  // 초기 데이터 요청
  fetchScheduleData()
})

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  // 필요시 소켓 구독 해제 로직 추가
})
</script>

<template>
  <div class="main-container">
    <div v-if="schedule" class="schedule-detail">
      <div class="title-container">
        <div class="title-left">
          <input
            v-model="editedTitle"
            class="title-input"
            :disabled="!isEditMode"
            :class="{ 'edit-mode': isEditMode }"
            placeholder="일정 제목을 입력하세요"
          />
        </div>
        <div class="title-right">
          <template v-if="!isEditMode">
            <span class="period">{{ schedule.startDate }} ~ {{ schedule.endDate }}</span>
          </template>
          <template v-else>
            <div class="date-inputs">
              <input
                v-model="editedStartDate"
                type="date"
                class="date-input"
                @change="handleDateChange"
              />
              <span>~</span>
              <input
                v-model="editedEndDate"
                type="date"
                class="date-input"
                @change="handleDateChange"
              />
            </div>
          </template>
          <button
            v-if="!socketStore.connected && schedule.mine"
            class="share-button"
            :class="{ shared: schedule.shared }"
            @click="toggleShare"
          >
            {{ schedule.shared ? '공유 취소' : '공유' }}
          </button>
        </div>
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
            <div class="title-actions">
              <button
                v-if="isEditMode"
                class="add-spot-button"
                @click="isSpotSearchModalVisible = true"
              >
                <PlusOutlined />
                장소 추가
              </button>
              <button
                v-if="schedule?.mine"
                class="edit-button"
                :class="{ editing: isEditMode }"
                @click="toggleEditMode"
              >
                <EditOutlined v-if="!isEditMode" />
                <CloseOutlined v-else />
              </button>
              <button
                v-if="isEditMode"
                class="save-button"
                :disabled="isSaving"
                @click="saveSchedule"
              >
                <CheckOutlined />
              </button>
              <button v-if="!isEditMode" class="mobile-map-button" @click="showModal">
                지도 보기
              </button>
            </div>
          </div>
          <ScheduleSpotList
            :spots="selectedDaySpots"
            :is-edit-mode="isEditMode"
            :current-day="selectedDay"
            :available-days="availableDays"
            @move-spot="handleMoveSpot"
            @remove-spot="handleRemoveSpot"
          />
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

    <!-- 여행지 검색 모달 -->
    <SpotSearchModal
      v-model:visible="isSpotSearchModalVisible"
      :current-day="selectedDay"
      @add-spot="handleAddSpot"
    />
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
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.title-left {
  display: flex;
  align-items: center;
}

.title-right {
  display: flex;
  align-items: center;
  gap: 12px;
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

.share-button {
  padding: 6px 16px;
  border: 1px solid #1890ff;
  border-radius: 6px;
  background-color: white;
  color: #1890ff;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.share-button:hover {
  background-color: #e6f7ff;
  border-color: #40a9ff;
}

.share-button.shared {
  background-color: #1890ff;
  color: white;
}

.share-button.shared:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.title-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.edit-button {
  padding: 6px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.edit-button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.edit-button.editing {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.save-button {
  padding: 6px;
  border: 1px solid #52c41a;
  border-radius: 4px;
  background-color: white;
  color: #52c41a;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.save-button:hover {
  background-color: #52c41a;
  color: white;
}

.save-button:disabled {
  border-color: #d9d9d9;
  color: #d9d9d9;
  cursor: not-allowed;
}

.title-input {
  font-size: 2rem;
  font-weight: 800;
  color: #1a1a1a;
  border: 1px solid transparent;
  background: transparent;
  padding: 4px 8px;
  width: 100%;
  max-width: 400px;
  cursor: default;
  box-sizing: border-box;
  height: 44.8px;
  line-height: 1;
}

.title-input:disabled {
  cursor: default;
  -webkit-text-fill-color: #1a1a1a;
  opacity: 1;
}

.title-input.edit-mode {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: white;
  cursor: text;
}

.title-input.edit-mode:focus {
  border-color: #40a9ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.date-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 0.9rem;
  color: #666;
}

.add-spot-button {
  padding: 6px 12px;
  border: 1px solid #52c41a;
  border-radius: 4px;
  background-color: white;
  color: #52c41a;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s ease;
}

.add-spot-button:hover {
  background-color: #52c41a;
  color: white;
}
</style>
