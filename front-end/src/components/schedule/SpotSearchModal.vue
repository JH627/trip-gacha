<script setup lang="ts">
import { ref, watch } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import type { SpotInfo } from '@/types/trip'
import { authApi } from '@/api/axios'
import defaultImg from '@/assets/no_img.png'

const props = defineProps<{
  visible: boolean
  currentDay: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', visible: boolean): void
  (e: 'addSpot', spot: SpotInfo, day: number): void
}>()

const searchQuery = ref('')
const searchResults = ref<SpotInfo[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

const categoryMap: Record<string, string> = {
  ACCOMMODATION: '숙소',
  ATTRACTION: '명소',
  RESTAURANT: '식당',
  CAFE: '카페',
}

const handleSearch = async (page = 1) => {
  if (!searchQuery.value.trim()) return

  loading.value = true
  currentPage.value = page
  try {
    const response = await authApi.get(
      `/trip/spot?category=ALLSPOT&keyword=${encodeURIComponent(searchQuery.value)}&page=${page - 1}&size=${pageSize}`,
    )
    searchResults.value = response.data.result.spots
    totalPages.value = Math.ceil(response.data.result.total / pageSize)
  } catch (error) {
    console.error('검색 중 오류 발생:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  handleSearch(page)
}

const handleAddSpot = (spot: SpotInfo) => {
  emit('addSpot', spot, props.currentDay)
  emit('update:visible', false)
  searchQuery.value = ''
  searchResults.value = []
  currentPage.value = 1
  totalPages.value = 1
}

const handleClose = () => {
  emit('update:visible', false)
  searchQuery.value = ''
  searchResults.value = []
  currentPage.value = 1
  totalPages.value = 1
}

// 검색어가 변경될 때마다 페이지 초기화
watch(searchQuery, () => {
  currentPage.value = 1
})
</script>

<template>
  <div v-if="visible" class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>여행지 검색</h3>
        <button class="close-btn" @click="handleClose">&times;</button>
      </div>

      <div class="search-container">
        <div class="search-input-wrapper">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="여행지를 검색하세요"
            @keyup.enter="handleSearch(1)"
          />
          <button class="search-btn" @click="handleSearch(1)">
            <SearchOutlined />
          </button>
        </div>
      </div>

      <div class="search-results">
        <div v-if="loading" class="loading">검색 중...</div>
        <div v-else-if="searchResults.length === 0" class="no-results">검색 결과가 없습니다</div>
        <template v-else>
          <div class="result-list">
            <div
              v-for="spot in searchResults"
              :key="spot.spotId"
              class="result-item"
              @click="handleAddSpot(spot)"
            >
              <div class="spot-image">
                <img :src="spot.img || defaultImg" :alt="spot.name" />
              </div>
              <div class="spot-info">
                <h4>{{ spot.name }}</h4>
                <p class="spot-address">{{ spot.address }}</p>
                <p class="spot-category">
                  {{ categoryMap[spot.category] || spot.category }}
                </p>
              </div>
            </div>
          </div>
          <div class="pagination">
            <button
              class="page-btn"
              :disabled="currentPage === 1"
              @click="handlePageChange(currentPage - 1)"
            >
              이전
            </button>
            <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
            <button
              class="page-btn"
              :disabled="currentPage === totalPages"
              @click="handlePageChange(currentPage + 1)"
            >
              다음
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.search-container {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input-wrapper {
  display: flex;
  gap: 8px;
}

.search-input-wrapper input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 1rem;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-results {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
}

.loading,
.no-results {
  text-align: center;
  color: #999;
  padding: 32px 0;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  cursor: pointer;
}

.result-item:hover {
  background-color: #f5f5f5;
}

.spot-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-info {
  flex: 1;
  min-width: 0;
}

.spot-info h4 {
  margin: 0 0 4px;
  font-size: 1rem;
}

.spot-address {
  margin: 0 0 4px;
  font-size: 0.9rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.spot-category {
  margin: 0;
  font-size: 0.8rem;
  color: #1890ff;
  background-color: #e6f7ff;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: white;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.page-btn:disabled {
  background-color: #f5f5f5;
  color: #d9d9d9;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.9rem;
  color: #666;
}
</style>
