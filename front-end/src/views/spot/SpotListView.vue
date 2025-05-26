<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import SpotCard from '@/components/spot/SpotCard.vue'
import SpotDetailModal from '@/components/spot/SpotDetailModal.vue'
import type { SpotInfo, Destination, SpotCategory, SpotParams } from '@/types/trip'
import { SPOT_CATEGORIES } from '@/types/trip'
import { authApi } from '@/api/axios'

const route = useRoute()

// 카테고리 목록 (전체, 명소, 식당, 카페, 찜 목록)
const categories = SPOT_CATEGORIES

// 검색어
const search = ref('')
// 선택한 카테고리 (기본값: 명소)
const selectedCategory = ref<SpotCategory>((route.query.type as SpotCategory) || 'ATTRACTION')
// 선택한 목적지
const selectedDestination = ref<number | null>(null)
// 정렬 기준
const sort = ref<SpotParams['sort']>('STARS')
// 목적지 목록
const destinations = ref<Destination[]>([])

// 관광지 목록
const spots = ref<SpotInfo[]>([])
// 전체 관광지 개수
const totalSpots = ref(0)
// 모달 상태 관리
const selectedSpot = ref<SpotInfo | null>(null)
const showModal = ref(false)

// 페이지네이션 상태
const currentPage = ref(1)
const pageSize = 12

// 목적지 목록 조회
const fetchDestinations = async () => {
  try {
    const { data } = await authApi.get('/trip/destination')
    destinations.value = data.result.map((dest: any) => ({
      destinationId: dest.destinationId,
      name: dest.name,
      description: dest.description || '',
      img: dest.img,
    }))
  } catch (error) {
    console.error('목적지 목록 조회 실패:', error)
  }
}

// 관광지 목록 조회
const fetchSpots = async () => {
  try {
    const params: SpotParams = {
      category: selectedCategory.value,
      sort: sort.value,
      page: currentPage.value - 1,
    }

    // 목적지 선택 시 목적지 ID 추가
    if (selectedDestination.value !== null) {
      params.destinationId = selectedDestination.value
    }
    if (search.value) {
      params.keyword = search.value
    }

    const { data } = await authApi.get('/trip/spot', { params })
    spots.value = data.result.spots
    totalSpots.value = data.result.total
  } catch (error) {
    console.error('관광지 목록 조회 실패:', error)
  }
}

// 필터링된 관광지 목록
const filteredSpots = computed(() => spots.value)

// 찜하기 토글
const toggleWish = async (spotId: number) => {
  try {
    await authApi.post('/trip/bookmark', { spotId })
    // 전체 목록 새로고침 대신 해당 스팟의 상태만 업데이트
    const spotIndex = spots.value.findIndex((spot) => spot.spotId === spotId)
    if (spotIndex !== -1) {
      const spot = spots.value[spotIndex]
      spot.marked = !spot.marked
      spot.likes = spot.marked ? (spot.likes || 0) + 1 : (spot.likes || 1) - 1
    }
  } catch (error) {
    console.error('찜하기 실패:', error)
  }
}

// 상세 정보 모달
const handleShowDetail = (spot: SpotInfo) => {
  selectedSpot.value = spot
  showModal.value = true
}
const handleCloseModal = () => {
  showModal.value = false
  selectedSpot.value = null
}

// 초기 데이터 로드
onMounted(() => {
  fetchDestinations()
  fetchSpots()
})

// 페이지 변경 핸들러
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchSpots()
}

// 검색어나 필터 변경 시 목록 새로고침
watch([search, selectedCategory, selectedDestination, sort], () => {
  currentPage.value = 1 // 필터 변경 시 첫 페이지로 이동
  fetchSpots()
})
</script>

<template>
  <div class="spot-list-page">
    <!-- 최상단: 검색바 -->
    <div class="top-search-bar">
      <div class="search-container">
        <input type="text" placeholder="관광지 검색..." v-model="search" />
        <button class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
    </div>

    <!-- 상단: 카테고리/정렬/지역 -->
    <div class="top-controls">
      <div class="categories">
        <button
          v-for="cat in categories"
          :key="cat.value"
          :class="{ active: selectedCategory === cat.value }"
          @click="selectedCategory = cat.value"
        >
          {{ cat.label }}
        </button>
      </div>
      <div class="controls-right">
        <div class="destination-select">
          <select v-model="selectedDestination">
            <option :value="null">전체 지역</option>
            <option
              v-for="dest in destinations"
              :key="dest.destinationId"
              :value="dest.destinationId"
            >
              {{ dest.name }}
            </option>
          </select>
        </div>
        <div class="sort-select">
          <select v-model="sort">
            <option value="STARS">평점순</option>
            <option value="NAME">이름순</option>
            <option value="LIKE">좋아요순</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 메인: 관광지 리스트 -->
    <div class="spot-list">
      <SpotCard
        v-for="spot in filteredSpots"
        :key="spot.spotId"
        :spot="spot"
        @toggle-wish="toggleWish"
        @show-detail="handleShowDetail"
      />
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination-container">
      <a-pagination
        v-model:current="currentPage"
        :total="totalSpots"
        :page-size="pageSize"
        :show-size-changer="false"
        show-less-items
        @change="handlePageChange"
      />
    </div>

    <!-- 상세 정보 모달 -->
    <SpotDetailModal :spot="selectedSpot" :show="showModal" @close="handleCloseModal" />
  </div>
</template>

<style scoped>
.spot-list-page {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  margin-top: 24px;
  padding: 24px 16px;
  min-height: calc(100vh - 64px);
  background: #fff;
  border-radius: 12px;
}

.top-search-bar {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.search-container {
  position: relative;
  width: 100%;
  max-width: 600px;
}

.search-container input {
  width: 100%;
  padding: 16px 20px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.search-container input:focus {
  border-color: #1677ff;
  outline: none;
}

.search-btn {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 8px;
}

.search-btn:hover {
  color: #1677ff;
}

.top-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.categories {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.categories button {
  background: #f7f8fa;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.categories button:hover {
  background: #e6f4ff;
}

.categories button.active {
  background: #1677ff;
  color: #fff;
}

.controls-right {
  display: flex;
  gap: 16px;
}

.destination-select select,
.sort-select select {
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-size: 0.95rem;
  background: #fff;
  cursor: pointer;
  min-width: 140px;
}

.destination-select select:hover,
.sort-select select:hover {
  border-color: #1677ff;
}

.destination-select select:focus,
.sort-select select:focus {
  outline: none;
  border-color: #1677ff;
}

.spot-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 16px 0;
}

@media (max-width: 1200px) {
  .spot-list {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 900px) {
  .spot-list {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 600px) {
  .spot-list {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .top-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .controls-right {
    flex-direction: column;
  }

  .pagination-container {
    margin-top: 24px;
    padding: 12px 0;
  }
}
</style>
