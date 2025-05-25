<template>
  <div class="list-container">
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>데이터를 불러오는 중...</p>
    </div>

    <!-- 데이터 리스트 -->
    <div v-else>
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
              :class="{ registered: isRegistered(item.spotId) }"
              @click="toggleRegister(item)"
            >
              {{ isRegistered(item.spotId) ? '등록됨' : '등록' }}
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
      <div v-if="spotData.length === 0 && !isLoading" class="no-data">
        <SearchX :size="48" class="no-data-icon" />
        <p>검색 결과가 없습니다</p>
      </div>
    </div>
  </div>

  <!-- 페이지네이션 -->
  <div v-if="totalPages > 1" class="pagination-container">
    <div class="pagination-info"></div>
    <div class="pagination-controls">
      <button class="pagination-button" :disabled="currentPage === 0" @click="prevPage">
        이전
      </button>

      <div class="page-numbers">
        <button
          v-for="page in getVisiblePages()"
          :key="page"
          class="page-number"
          :class="{ active: currentPage === page - 1 }"
          @click="goToPage(page - 1)"
        >
          {{ page }}
        </button>
      </div>

      <button
        class="pagination-button"
        :disabled="currentPage === totalPages - 1"
        @click="nextPage"
      >
        다음
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted } from 'vue'
import { authApi } from '@/api/axios'
import type { SpotParams } from '@/types/trip'
import { Star, Heart, SearchX } from 'lucide-vue-next'
import { useSpotStore } from '@/stores/spot'

interface Props {
  destinationId: number
  planId: string
  keyword: string
  sortOrder: 'asc' | 'desc'
}

const props = defineProps<Props>()
const emit = defineEmits(['item-selected'])

const spotData = ref<any[]>([])
const spotStore = useSpotStore()

// 페이지네이션 상태
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const isLoading = ref(false)

const selectedSpotIds = computed(() => spotStore.getSpotIdsArray())

const isRegistered = (spotId: number) => {
  return selectedSpotIds.value.includes(spotId)
}

// 보이는 페이지 번호 계산
const getVisiblePages = () => {
  const maxVisible = 5
  const start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2) + 1)
  const end = Math.min(totalPages.value, start + maxVisible - 1)

  const pages = []
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
}

// API 호출 함수
const fetchSpotData = async () => {
  if (props.destinationId === 0) return

  isLoading.value = true
  try {
    const params: SpotParams = {
      category: 'ACCOMMODATION',
      sort: props.sortOrder === 'asc' ? 'NAME' : 'STARS',
      page: currentPage.value,
      destinationId: props.destinationId,
      keyword: props.keyword,
    }

    const { data } = await authApi.get('/trip/accommodation', { params })
    spotData.value = data.result.spots || []
    totalPages.value = data.result.total || 0
    totalElements.value = data.result.totalElements || 0
    console.log('숙소 조회 결과:', data)
  } catch (error) {
    console.error('숙소 조회 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 페이지네이션 핸들러
const goToPage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchSpotData()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchSpotData()
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchSpotData()
  }
}

const resetPagination = () => {
  currentPage.value = 0
}

// props 변경 감지
watch(
  [() => props.destinationId, () => props.keyword, () => props.sortOrder],
  () => {
    resetPagination()
    fetchSpotData()
  },
  { immediate: true },
)

const showDetail = (item: any) => {
  console.log('상세보기:', item)
  emit('item-selected', { type: 'detail', item })
}

const toggleRegister = (item: any) => {
  if (isRegistered(item.spotId)) {
    emit('item-selected', { type: 'remove', item })
  } else {
    emit('item-selected', { type: 'register', item })
  }
}

const truncateContent = (content: string) => {
  if (content === null) {
    return ''
  }
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}
</script>

<style scoped>
.list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  padding-right: 5px;
}

/* 로딩 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #6b7280;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f4f6;
  border-top: 3px solid #4f46e5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
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

/* 페이지네이션 스타일 */
.pagination-container {
  margin-top: 20px;
  padding: 15px 0;
  border-top: 1px solid #e5e7eb;
}

.pagination-info {
  text-align: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #6b7280;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.pagination-button {
  padding: 8px 16px;
  background-color: #f8f9fa;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s;
}

.pagination-button:hover:not(:disabled) {
  background-color: #e5e7eb;
  border-color: #9ca3af;
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s;
}

.page-number:hover {
  background-color: #e5e7eb;
  border-color: #9ca3af;
}

.page-number.active {
  background-color: #4f46e5;
  border-color: #4f46e5;
  color: white;
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

  .pagination-controls {
    flex-direction: column;
    gap: 15px;
  }

  .page-numbers {
    order: -1;
  }
}
</style>
