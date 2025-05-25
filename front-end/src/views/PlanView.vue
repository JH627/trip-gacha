<template>
  <div class="container">
    <div class="progress-header">
      <button class="nav-button prev-button" @click="goPrev">뒤로</button>
      <div class="progress-text">{{ progressTextMap[currentProgress] }}</div>
      <button v-if="currentProgress !== endProgress" class="nav-button next-button" @click="goNext">
        앞으로
      </button>
      <button v-else class="nav-button next-button" @click="goNext">완료</button>
    </div>

    <!-- Body 섹션 추가 -->
    <div class="body-container">
      <!-- 검색창과 정렬 버튼 -->
      <div class="search-section">
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

      <!-- 데이터 리스트 컨테이너 -->
      <div class="list-container">
        <div v-for="item in spotData" :key="item.spotId" class="list-item">
          <!-- 사진 div (30%) -->
          <div class="image-section">
            <img :src="item.img" :alt="item.name" class="spot-image" />
            <button class="detail-button" @click="showDetail(item)">자세히보기</button>
          </div>

          <!-- 설명 div (70%) -->
          <div class="description-section">
            <!-- 숙소이름과 등록버튼 -->
            <div class="name-register-row">
              <h3 class="spot-name">{{ item.name }}</h3>
              <button
                class="register-button"
                :class="{ registered: item.marked }"
                @click="toggleRegister(item)"
              >
                {{ item.marked ? '등록됨' : '등록' }}
              </button>
            </div>

            <!-- 설명 -->
            <div class="content-section">
              <p class="spot-content">{{ truncateContent(item.content) }}</p>
              <div class="spot-info">
                <span class="address">{{ item.address }}</span>
                <span class="work-time" v-html="item.workTime"></span>
                <span class="phone">{{ item.phone }}</span>
              </div>
            </div>

            <!-- 별점과 좋아요 -->
            <div class="stats-row">
              <div class="stars">
                <Star v-for="n in 5" :key="n" :class="{ filled: n <= item.stars }" :size="16" />
                <span class="star-count">{{ item.stars }}</span>
              </div>
              <div class="likes">
                <Heart :class="{ liked: item.likes > 0 }" :size="16" />
                <span class="like-count">{{ item.likes }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 데이터가 없을 때 -->
        <div v-if="spotData.length === 0" class="no-data">
          <SearchX :size="48" class="no-data-icon" />
          <p>검색 결과가 없습니다</p>
        </div>
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
import { useRoute } from 'vue-router'
import WhiteGloves from '@/assets/gloves-white.png'
import GameModal from '@/components/game/GameModal.vue'
import { Game } from '@/components/game/Game'
import { authApi } from '@/api/axios'
import type { SpotParams } from '@/types/trip'
import { SortAsc, SortDesc, Star, Heart, SearchX } from 'lucide-vue-next'

// 기존 코드들...
const route = useRoute()
const planId = computed(() => route.params.planId as string)
const currentProgress = ref(PlanProgress.SELECT_ACCOMMODATION)
const endProgress = PlanProgress.COMPLETE
const socketStore = useSocketStore()
const authStore = useAuthStore()

const invitedGameType: Ref<Game> = ref(Game.DEFAULT)

// 새로 추가된 상태들
const keyword = ref('')
const sortOrder = ref<'asc' | 'desc'>('desc')
const spotData = ref<any[]>([])

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

const destinationId = ref(0)

const processDestinationId = (body: string) => {
  destinationId.value = JSON.parse(body)
}

onMounted(() => {
  try {
    socketStore.subscribe(`/user/queue/game`, processGameMessage)
    socketStore.subscribe(`/user/queue/plan`, processMessage)
    socketStore.subscribe(`/topic/plan/${planId.value}`, processJoinMessage)
    socketStore.subscribe(`/user/queue/destination`, processDestinationId)

    socketStore.send(`/app/plan/join/${planId.value}`, authStore.accessToken || '', null)
    socketStore.send(`/app/plan/destination`, authStore.accessToken || ``, planId.value)
  } catch (error) {
    window.location.href = '/trip/lobby'
  }
})

watch(destinationId, async (newDestinationId) => {
  if (newDestinationId !== 0 && currentProgress.value === PlanProgress.SELECT_ACCOMMODATION) {
    const params: SpotParams = {
      category: 'ALLSPOT',
      sort: 'STARS',
      page: 0,
      destinationId: newDestinationId,
      keyword: '',
    }

    const { data } = await authApi.get('/trip/spot', { params })
    spotData.value = data.result.spots || []
    console.log('조회 결과 : ', data)
  }

  if (newDestinationId !== 0 && currentProgress.value === PlanProgress.SELECT_TOURIST_SPOTS) {
    // 관광지 호출 요청
  }
})

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

// 새로 추가된 메서드들
const handleSearch = () => {
  console.log('검색:', keyword.value)
  // 검색 로직 구현
}

const toggleSort = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  // 정렬 로직 구현
}

const showDetail = (item: any) => {
  console.log('상세보기:', item)
  // 상세보기 로직 구현
}

const toggleRegister = (item: any) => {
  item.marked = !item.marked
  console.log('등록 토글:', item.name, item.marked)
  // 등록/해제 로직 구현
}

const truncateContent = (content: string) => {
  return content.length > 100 ? content.substring(0, 100) + '...' : content
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

.list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  padding-right: 5px;
}

.list-item {
  display: flex;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.list-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-section {
  width: 30%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  gap: 10px;
}

.spot-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-button {
  padding: 8px 12px;
  background-color: #6b7280;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.detail-button:hover {
  background-color: #4b5563;
}

.description-section {
  width: 70%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  gap: 12px;
}

.name-register-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.spot-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.register-button {
  padding: 6px 12px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.register-button:hover {
  background-color: #059669;
}

.register-button.registered {
  background-color: #6b7280;
}

.register-button.registered:hover {
  background-color: #4b5563;
}

.content-section {
  flex: 1;
}

.spot-content {
  margin: 0 0 10px 0;
  color: #374151;
  line-height: 1.5;
  font-size: 14px;
}

.spot-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stars,
.likes {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stars svg {
  color: #fbbf24;
}

.stars svg.filled {
  fill: #fbbf24;
}

.likes svg {
  color: #ef4444;
}

.likes svg.liked {
  fill: #ef4444;
}

.star-count,
.like-count {
  font-size: 12px;
  font-weight: 500;
  color: #374151;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #9ca3af;
}

.no-data-icon {
  margin-bottom: 16px;
  color: #d1d5db;
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

/* 스크롤바 스타일링 */
.list-container::-webkit-scrollbar {
  width: 6px;
}

.list-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.list-container::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.list-container::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 반응형 */
@media (max-width: 768px) {
  .list-item {
    flex-direction: column;
  }

  .image-section,
  .description-section {
    width: 100%;
  }

  .search-section {
    flex-direction: column;
    align-items: stretch;
  }

  .sort-button {
    align-self: flex-end;
    width: fit-content;
  }
}
</style>
