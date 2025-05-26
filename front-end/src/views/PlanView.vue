<template>
  <div class="container">
    <div class="progress-header">
      <button class="nav-button prev-button" @click="goPrev">뒤로</button>
      <div class="progress-text">{{ progressTextMap[currentProgress] }}</div>
      <button v-if="currentProgress !== endProgress" class="nav-button next-button" @click="goNext">
        앞으로
      </button>
      <button v-else class="complete-button" @click="saveSchedule">완료</button>
    </div>

    <!-- Body 섹션 - currentProgress에 따라 다른 컴포넌트 렌더링 -->
    <div class="body-container">
      <!-- 검색창과 정렬 버튼 -->
      <div
        v-if="
          currentProgress === PlanProgress.SELECT_TOURIST_SPOTS ||
          currentProgress === PlanProgress.SELECT_ACCOMMODATION
        "
        class="search-section"
      >
        <div class="search-bar">
          <input
            type="text"
            v-model="keyword"
            placeholder="검색어를 입력하세요"
            class="search-input"
          />
          <button class="search-button" @click="handleSearch">검색</button>
        </div>
        <button class="sort-button" @click="toggleSort">
          <SortAsc v-if="sortOrder === 'asc'" :size="16" />
          <SortDesc v-else :size="16" />
          정렬
        </button>
      </div>

      <!-- 태그 선택 섹션 (관광지 선택 단계에서만 표시) -->
      <div v-if="currentProgress === PlanProgress.SELECT_TOURIST_SPOTS" class="tag-section">
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

      <!-- 진행 상태에 따른 컴포넌트 렌더링 -->
      <AccommodationList
        v-if="currentProgress === PlanProgress.SELECT_ACCOMMODATION"
        :destination-id="destinationId"
        :plan-id="planId"
        :keyword="keyword"
        :sort-order="sortOrder"
        @item-selected="handleItemSelected"
      />

      <TouristSpotList
        v-else-if="currentProgress === PlanProgress.SELECT_TOURIST_SPOTS"
        :destination-id="destinationId"
        :plan-id="planId"
        :keyword="keyword"
        :sort-order="sortOrder"
        :category="selectedCategory"
        @item-selected="handleItemSelected"
      />

      <SelectedSpotList
        v-else-if="currentProgress === PlanProgress.FINALIZE_DESTINATIONS"
        :plan-id="planId"
        @spot-detail="handleSpotDetail"
      />

      <SocketScheduleDetailView v-else-if="currentProgress === PlanProgress.REVIEW_AND_EDIT" />

      <!-- 기타 진행 상태들... -->
      <div v-else class="placeholder">
        <p>{{ progressTextMap[currentProgress] }} 단계입니다.</p>
      </div>
    </div>

    <div class="game-button" @click="openModal">
      <img class="game-icon" :src="WhiteGloves" />
    </div>
    <GameModal
      :is-open="isOpen"
      :is-socket="true"
      :invited-game-type="invitedGameType"
      @close="closeModal"
    />
  </div>
</template>

<script setup lang="ts">
import { PlanProgress, progressTextMap } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { computed, onMounted, ref, watch, type Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import WhiteGloves from '@/assets/gloves-white.png'
import GameModal from '@/components/game/GameModal.vue'
import { Game } from '@/components/game/Game'
import { SortAsc, SortDesc, MapPin, UtensilsCrossed, Coffee, Heart } from 'lucide-vue-next'
import SelectedSpotList from '@/components/plan/SelectedSpotList.vue'

// 컴포넌트 import
import AccommodationList from '@/components/plan/AccommodationList.vue'
import TouristSpotList from '@/components/plan/TouristSpotList.vue'
import { useSpotStore } from '@/stores/spot'
import SocketScheduleDetailView from '@/components/plan/SocketScheduleDetailView.vue'
import { authApi } from '@/api/axios'
import { useDestinationStore } from '@/stores/destination'
import { useScheduleStore } from '@/stores/schedule'
import type { ScheduleDetail } from '@/types/trip'

const route = useRoute()
const planId = computed(() => route.params.planId as string)
const currentProgress = ref(PlanProgress.SELECT_ACCOMMODATION)
const endProgress = PlanProgress.REVIEW_AND_EDIT
const socketStore = useSocketStore()
const authStore = useAuthStore()

const invitedGameType: Ref<Game> = ref(Game.DEFAULT)

// 부모에서 관리하는 상태들
const keyword = ref('')
const sortOrder = ref<'asc' | 'desc'>('desc')
const destinationId = ref(0)
const selectedCategory = ref<string>('ATTRACTION')

// 카테고리 태그 정의
const categoryTags = [
  { label: '관광지', value: 'ATTRACTION', icon: MapPin },
  { label: '음식점', value: 'RESTAURANT', icon: UtensilsCrossed },
  { label: '카페', value: 'CAFE', icon: Coffee },
  { label: '찜', value: 'MARKED', icon: Heart },
]

const processMessage = (message: string) => {
  alert(message)
}

const processJoinMessage = (body: string) => {
  const response: PlanProgress = JSON.parse(body)
  console.log('현재 : ' + currentProgress.value)
  console.log('다음 : ' + response)
  currentProgress.value = response
}

const processGameMessage = (body: string) => {
  const gameType = JSON.parse(body)
  console.log(('초대 당한 게임 : ' + gameType) as Game)
  invitedGameType.value = gameType as Game
  openModal()
}

const processDestinationId = (body: string) => {
  destinationId.value = JSON.parse(body)
}

const spotStore = useSpotStore()

const processSpotIdMessage = (body: string) => {
  const response: { type: string; spotIds: number[] } = JSON.parse(body)

  console.log(response.spotIds)

  switch (response.type) {
    case 'add':
      spotStore.addSpotIds(response.spotIds)
      break
    case 'remove':
      spotStore.removeSpotIds(response.spotIds)
      break
  }
}

const router = useRouter()

const processByeMessage = (body: string) => {
  alert('여행 계획 생성 완료!')

  router.push('/')
}

onMounted(() => {
  try {
    socketStore.subscribe(`/user/queue/game`, processGameMessage)
    socketStore.subscribe(`/user/queue/plan`, processMessage)
    socketStore.subscribe(`/topic/plan/${planId.value}`, processJoinMessage)
    socketStore.subscribe(`/topic/plan/spot/${planId.value}`, processSpotIdMessage)
    socketStore.subscribe(`/topic/plan/bye/${planId.value}`, processByeMessage)
    socketStore.subscribe(`/user/queue/destination`, processDestinationId)

    socketStore.send(`/app/plan/join/${planId.value}`, authStore.accessToken || '', null)
    socketStore.send(`/app/plan/destination`, authStore.accessToken || ``, planId.value)
  } catch (error) {
    window.location.href = '/trip/lobby'
  }
})
const scheduleStore = useScheduleStore()

const saveSchedule = async () => {
  const finalSchedule: ScheduleDetail | null = scheduleStore.schedule

  if (!finalSchedule) {
    console.log('왜 없지')
  }

  const scheduleItems = finalSchedule?.scheduleDetailItems.map((item) => {
    return {
      day: item.day,
      sequence: item.order,
      spotId: item.spotInfo.spotId,
    }
  })

  const scheduleRequest = {
    destinationId: destinationId.value,
    title: finalSchedule?.title,
    startDate: finalSchedule?.startDate,
    endDate: finalSchedule?.endDate,
    scheduleItems: scheduleItems,
  }

  await authApi.post('/trip/schedule', scheduleRequest)

  socketStore.send(`/app/plan/bye/${planId.value}`, authStore.accessToken || '', null)
}

const goNext = () => {
  socketStore.send(`/app/plan/move/${planId.value}`, authStore.accessToken || '', true)
}

const goPrev = () => {
  socketStore.send(`/app/plan/move/${planId.value}`, authStore.accessToken || '', false)
}

const isOpen = ref(false)

const openModal = () => {
  console.log('open!')
  isOpen.value = true
}

const closeModal = () => {
  isOpen.value = false
  invitedGameType.value = Game.DEFAULT
}

// 검색 및 정렬 핸들러
const handleSearch = () => {
  console.log('검색:', keyword.value)
  // 검색 이벤트는 자식 컴포넌트에서 watch로 감지
}

const toggleSort = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  // 정렬 변경 이벤트는 자식 컴포넌트에서 watch로 감지
}

// 카테고리 선택 핸들러
const selectCategory = (category: string) => {
  selectedCategory.value = category
  console.log('선택된 카테고리:', category)
}

// 자식 컴포넌트에서 아이템 선택 시 호출
const handleItemSelected = (item: any) => {
  let requestUrl = ''
  switch (item.type) {
    case 'register':
      requestUrl = 'add-cart/'
      break
    case 'remove':
      requestUrl = 'remove-cart/'
      break
  }

  socketStore.send('/app/plan/' + requestUrl + planId.value, authStore.accessToken || '', item.item)
}

const handleSpotDetail = (spot: any) => {
  console.log('선택된 스팟 상세보기:', spot)
  // 상세보기 모달이나 페이지로 이동하는 로직 추가
}
</script>

<style scoped>
.container {
  position: relative;
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
  flex-shrink: 0;
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

.complete-button {
  order: 3;
  padding: 8px 16px;
  border: 1px solid #d0d0d0;
  border-radius: 6px;
  background-color: green;
  color: white;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.body-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow: hidden;
}

.search-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.search-bar {
  display: flex;
  flex: 1;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.1);
}

.search-button {
  padding: 10px 20px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.search-button:hover {
  background-color: #4338ca;
}

.sort-button {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 10px 15px;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.sort-button:hover {
  background-color: #e9ecef;
}

/* 태그 섹션 스타일 */
.tag-section {
  margin-bottom: 20px;
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

.placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 16px;
}

.game-button {
  position: absolute;
  bottom: 50px;
  right: 50px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ff4757;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.game-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.6);
}

.game-button:active {
  transform: scale(0.95);
}

.game-icon {
  width: 36px;
  height: 36px;
  color: white;
  pointer-events: none;
}

/* 반응형 */
@media (max-width: 768px) {
  .search-section {
    flex-direction: column;
    align-items: stretch;
  }

  .sort-button {
    align-self: flex-end;
    width: fit-content;
  }

  .tag-container {
    justify-content: center;
  }
}
</style>
