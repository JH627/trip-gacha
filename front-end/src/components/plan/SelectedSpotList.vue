<template>
  <div class="selected-spots-container">
    <!-- 태그 선택 섹션 -->
    <div class="tag-section">
      <div class="tag-container">
        <button
          v-for="tag in categoryTags"
          :key="tag.value"
          class="tag-button"
          :class="{ active: selectedCategory === tag.value }"
          @click="selectCategory(tag.value)"
        >
          <component :is="tag.icon" :size="16" />
          {{ tag.label }}
        </button>
      </div>
    </div>

    <!-- 선택된 스팟 카드 리스트 -->
    <div class="spots-scroll-container">
      <div v-if="filteredSpots.length === 0" class="no-spots">
        <MapPin :size="48" class="no-spots-icon" />
        <p>선택된 {{ getCategoryLabel(selectedCategory) }}이(가) 없습니다</p>
      </div>

      <div v-else class="spots-list">
        <div
          v-for="spot in filteredSpots"
          :key="spot.spotId"
          class="spot-card"
          draggable="true"
          @dragstart="handleDragStart($event, spot)"
          @dragend="handleDragEnd($event)"
        >
          <div class="spot-image-container">
            <img :src="spot.img" :alt="spot.name" class="spot-image" />
          </div>
          <div class="spot-info">
            <h4 class="spot-name">{{ spot.name }}</h4>
            <button class="detail-button" @click="showDetail(spot)">자세히보기</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 여행 일정 섹션 -->
    <div class="travel-schedule-section">
      <h3 class="schedule-title">여행 일정</h3>
      <div class="days-container">
        <div
          v-for="(day, index) in travelDays"
          :key="day.date"
          class="day-folder"
          @dragover.prevent
          @drop="handleDrop($event, day.date)"
        >
          <div class="day-header">
            <Calendar :size="16" />
            <span class="day-number">{{ index + 1 }}일차</span>
            <span class="day-date">{{ formatDate(day.date) }}</span>
          </div>
          <div class="day-content">
            <div v-if="day.spots.length === 0" class="empty-day">
              <Plus :size="24" class="empty-icon" />
              <p>스팟을 드래그해서 추가하세요</p>
            </div>
            <div v-else class="day-spots">
              <div v-for="spot in day.spots" :key="spot.spotId" class="day-spot-card">
                <img :src="spot.img" :alt="spot.name" class="day-spot-image" />
                <div class="day-spot-info">
                  <h5 class="day-spot-name">{{ spot.name }}</h5>
                  <p class="day-spot-category">{{ getCategoryLabel(spot.category) }}</p>
                </div>
                <button
                  class="delete-button"
                  @click="updateSchedule('remove', day.date, spot.spotId)"
                  title="삭제"
                >
                  <Trash2 :size="16" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Home, MapPin, UtensilsCrossed, Coffee, Calendar, Plus, Trash2 } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRoute } from 'vue-router'

interface SpotInfo {
  spotId: number
  name: string
  address: string
  category: string
  content: string
  destination: string
  img: string
  latitude: number
  longitude: number
  likes: number
  marked: boolean
  phone: string
  stars: number
  workTime: string
}

interface TravelDay {
  date: string
  spots: SpotInfo[]
}

interface SpotOperation {
  type: 'add' | 'remove'
  date: string
  spotId: number
}

interface Props {
  planId: string
}

const props = defineProps<Props>()
const emit = defineEmits(['spot-detail'])

// 상태 관리
const selectedSpots = ref<SpotInfo[]>([])
const selectedCategory = ref<string>('ACCOMMODATION')
const draggedSpot = ref<SpotInfo | null>(null)
const travelDays = ref<TravelDay[]>([])

// 카테고리 태그 정의
const categoryTags = [
  { label: '숙소', value: 'ACCOMMODATION', icon: Home },
  { label: '관광지', value: 'ATTRACTION', icon: MapPin },
  { label: '음식점', value: 'RESTAURANT', icon: UtensilsCrossed },
  { label: '카페', value: 'CAFE', icon: Coffee },
]

// 필터링된 스팟 계산
const filteredSpots = computed(() => {
  return selectedSpots.value.filter((spot) => spot.category === selectedCategory.value)
})

// 날짜 포맷팅
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 카테고리 라벨 가져오기
const getCategoryLabel = (category: string) => {
  const tag = categoryTags.find((tag) => tag.value === category)
  return tag ? tag.label : category
}

// 카테고리 선택
const selectCategory = (category: string) => {
  selectedCategory.value = category
}

// 드래그 시작
const handleDragStart = (event: DragEvent, spot: SpotInfo) => {
  draggedSpot.value = spot
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'

    // 전체 카드를 드래그 이미지로 설정
    const dragElement = event.target as HTMLElement
    const cardElement = dragElement.closest('.spot-card') as HTMLElement

    if (cardElement) {
      cardElement.classList.add('dragging')
      event.dataTransfer.setDragImage(
        cardElement,
        cardElement.offsetWidth / 2,
        cardElement.offsetHeight / 2,
      )

      setTimeout(() => {
        cardElement.classList.remove('dragging')
      }, 0)
    }
  }
}

// 드롭 처리 - 스팟 추가
const handleDrop = (event: DragEvent, date: string) => {
  event.preventDefault()

  if (draggedSpot.value) {
    updateSchedule('add', date, draggedSpot.value.spotId)
    draggedSpot.value = null
  }
}

const updateSchedule = (type: string, date: string, spotId: number) => {
  const payload = {
    type: type,
    date: date,
    spotId: spotId,
  }

  socketStore.send(
    `/app/plan/schedule/update/${planId.value}`,
    authStore.accessToken || '',
    payload,
  )
}

// 드래그 끝
const handleDragEnd = (event: DragEvent) => {
  const cardElement = event.target as HTMLElement
  const spotCard = cardElement.closest('.spot-card') as HTMLElement
  if (spotCard) {
    spotCard.classList.remove('dragging')
  }
}

// 상세보기
const showDetail = (spot: SpotInfo) => {
  console.log('상세보기:', spot)
  emit('spot-detail', spot)
}

const socketStore = useSocketStore()
const authStore = useAuthStore()
const route = useRoute()
const planId = computed(() => route.params.planId as string)

// 선택된 스팟 리스트 처리
const processSelectedSpotList = (body: string) => {
  const response: SpotInfo[] = JSON.parse(body)
  selectedSpots.value = response
}

// 여행 일정 데이터 처리
const processTravelSchedule = (body: string) => {
  const response: { [date: string]: SpotInfo[] } = JSON.parse(body)

  // Map 데이터를 TravelDay 배열로 변환
  const days: TravelDay[] = []
  const sortedDates = Object.keys(response).sort()

  sortedDates.forEach((date) => {
    days.push({
      date: date,
      spots: response[date] || [],
    })
  })

  travelDays.value = days
}

// 스팟 추가/삭제 응답 처리
const processSpotOperation = (body: string) => {
  const response = JSON.parse(body)
  const { type, date, spotId } = response

  const dayIndex = travelDays.value.findIndex((day) => day.date === date)
  if (dayIndex === -1) return

  if (type === 'add') {
    // selectedSpots에서 해당 spotId를 찾아서 추가
    const spot = selectedSpots.value.find((s) => s.spotId === spotId)
    if (spot) {
      travelDays.value[dayIndex].spots.push(spot)
    }
  } else if (type === 'remove') {
    // 해당 날짜에서 spotId 제거
    const spotIndex = travelDays.value[dayIndex].spots.findIndex((s) => s.spotId === spotId)
    if (spotIndex !== -1) {
      travelDays.value[dayIndex].spots.splice(spotIndex, 1)
    }
  }
}

const processDecideCallback = () => {
  // 추후 구현 예정
}

// 컴포넌트 마운트 시 설정
onMounted(() => {
  // 소켓 구독
  socketStore.subscribe(`/topic/plan/cart/${planId.value}`, processSelectedSpotList)
  socketStore.subscribe(`/topic/plan/decide/${planId.value}`, processDecideCallback)
  socketStore.subscribe(`/topic/plan/schedule/${planId.value}`, processTravelSchedule)
  socketStore.subscribe(`/topic/plan/spot-operation/${planId.value}`, processSpotOperation)

  // 초기 데이터 요청
  socketStore.send(`/app/plan/get-cart/${planId.value}`, authStore.accessToken || '', null)
  socketStore.send(`/app/plan/get-schedule/${planId.value}`, authStore.accessToken || '', null)
})
</script>

<style scoped>
.selected-spots-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100vh;
  overflow: hidden;
}

/* 태그 섹션 스타일 */
.tag-section {
  flex-shrink: 0;
}

.tag-container {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.2s ease;
}

.tag-button:hover {
  background-color: #e9ecef;
  border-color: #d1d5db;
}

.tag-button.active {
  background-color: #4f46e5;
  border-color: #4f46e5;
  color: white;
}

.tag-button.active:hover {
  background-color: #4338ca;
  border-color: #4338ca;
}

/* 스팟 리스트 스타일 */
.spots-scroll-container {
  flex-shrink: 0;
  min-height: 200px;
  max-height: 250px;
}

.no-spots {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #9ca3af;
}

.no-spots-icon {
  margin-bottom: 16px;
  color: #d1d5db;
}

.spots-list {
  display: flex;
  gap: 15px;
  overflow-x: auto;
  padding: 10px 0;
  scroll-behavior: smooth;
}

.spot-card {
  flex-shrink: 0;
  width: 180px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  cursor: grab;
  transition:
    transform 0.2s,
    box-shadow 0.2s,
    opacity 0.2s;
}

.spot-card:active {
  cursor: grabbing;
}

.spot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.spot-card.dragging {
  opacity: 0.5;
  transform: scale(0.95);
}

.spot-image-container {
  width: 100%;
  height: 120px;
  overflow: hidden;
}

.spot-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.spot-name {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-button {
  padding: 6px 12px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.detail-button:hover {
  background-color: #4338ca;
}

/* 여행 일정 섹션 */
.travel-schedule-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  min-height: 0;
}

.schedule-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  flex-shrink: 0;
}

.days-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  padding-right: 10px;
  max-height: calc(100vh - 400px);
}

.day-folder {
  flex-shrink: 0;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.day-folder:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.day-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.day-number {
  font-weight: 600;
  color: #4f46e5;
}

.day-date {
  font-size: 14px;
  color: #6b7280;
}

.day-content {
  padding: 20px;
  min-height: 100px;
}

.empty-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80px;
  color: #9ca3af;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
}

.empty-icon {
  margin-bottom: 8px;
  color: #d1d5db;
}

.day-spots {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.day-spot-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
  min-width: 200px;
  position: relative;
}

.day-spot-image {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
}

.day-spot-info {
  flex: 1;
}

.day-spot-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.day-spot-category {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

/* 삭제 버튼 스타일 */
.delete-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: none;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  color: #ef4444;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.delete-button:hover {
  background-color: #fef2f2;
  color: #dc2626;
  transform: scale(1.1);
}

.delete-button:active {
  transform: scale(0.95);
}

/* 스크롤바 스타일링 */
.spots-list::-webkit-scrollbar,
.days-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.spots-list::-webkit-scrollbar-track,
.days-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.spots-list::-webkit-scrollbar-thumb,
.days-container::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.spots-list::-webkit-scrollbar-thumb:hover,
.days-container::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 반응형 */
@media (max-width: 768px) {
  .selected-spots-container {
    height: 100vh;
  }

  .tag-container {
    justify-content: center;
  }

  .spot-card {
    width: 160px;
  }

  .spots-list {
    gap: 10px;
  }

  .day-header {
    padding: 12px 15px;
  }

  .day-content {
    padding: 15px;
  }

  .day-spot-card {
    min-width: 100%;
  }

  .days-container {
    max-height: calc(100vh - 350px);
  }
}
</style>
