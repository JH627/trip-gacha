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
        <div v-for="spot in filteredSpots" :key="spot.spotId" class="spot-card">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Home, MapPin, UtensilsCrossed, Coffee } from 'lucide-vue-next'

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

interface Props {
  planId: string
}

const props = defineProps<Props>()
const emit = defineEmits(['spot-detail'])

// 상태 관리
const selectedSpots = ref<SpotInfo[]>([])
const selectedCategory = ref<string>('ACCOMMODATION')

// 카테고리 태그 정의
const categoryTags = [
  { label: '숙소', value: 'ACCOMMODATION', icon: Home },
  { label: '관광지', value: 'ATTRACTION', icon: MapPin },
  { label: '음식점', value: 'RESTAURANT', icon: UtensilsCrossed },
  { label: '카페', value: 'CAFE', icon: Coffee },
]

// 테스트 데이터 (실제로는 서버에서 받아올 데이터)
const testData: SpotInfo[] = [
  {
    spotId: 1,
    name: '그랜드 호텔',
    address: '서울시 강남구',
    category: 'ACCOMMODATION',
    content: '럭셔리한 5성급 호텔입니다.',
    destination: '서울',
    img: '/placeholder.svg?height=150&width=200',
    latitude: 37.5665,
    longitude: 126.978,
    likes: 120,
    marked: true,
    phone: '02-1234-5678',
    stars: 5,
    workTime: '24시간 운영',
  },
  {
    spotId: 2,
    name: '경복궁',
    address: '서울시 종로구',
    category: 'ATTRACTION',
    content: '조선왕조의 대표적인 궁궐입니다.',
    destination: '서울',
    img: '/placeholder.svg?height=150&width=200',
    latitude: 37.5796,
    longitude: 126.977,
    likes: 89,
    marked: true,
    phone: '02-3700-3900',
    stars: 4,
    workTime: '09:00-18:00',
  },
  {
    spotId: 3,
    name: '맛있는 한식당',
    address: '서울시 중구',
    category: 'RESTAURANT',
    content: '전통 한식을 맛볼 수 있는 곳입니다.',
    destination: '서울',
    img: '/placeholder.svg?height=150&width=200',
    latitude: 37.5636,
    longitude: 126.997,
    likes: 67,
    marked: true,
    phone: '02-2345-6789',
    stars: 4,
    workTime: '11:00-22:00',
  },
  {
    spotId: 4,
    name: '아늑한 카페',
    address: '서울시 마포구',
    category: 'CAFE',
    content: '조용하고 아늑한 분위기의 카페입니다.',
    destination: '서울',
    img: '/placeholder.svg?height=150&width=200',
    latitude: 37.5502,
    longitude: 126.9214,
    likes: 45,
    marked: true,
    phone: '02-3456-7890',
    stars: 4,
    workTime: '08:00-22:00',
  },
  {
    spotId: 5,
    name: '비즈니스 호텔',
    address: '서울시 영등포구',
    category: 'ACCOMMODATION',
    content: '비즈니스 여행객을 위한 호텔입니다.',
    destination: '서울',
    img: '/placeholder.svg?height=150&width=200',
    latitude: 37.5264,
    longitude: 126.8962,
    likes: 78,
    marked: true,
    phone: '02-4567-8901',
    stars: 3,
    workTime: '24시간 운영',
  },
]

// 필터링된 스팟 계산
const filteredSpots = computed(() => {
  return selectedSpots.value.filter((spot) => spot.category === selectedCategory.value)
})

// 카테고리 라벨 가져오기
const getCategoryLabel = (category: string) => {
  const tag = categoryTags.find((tag) => tag.value === category)
  return tag ? tag.label : category
}

// 카테고리 선택
const selectCategory = (category: string) => {
  selectedCategory.value = category
}

// 상세보기
const showDetail = (spot: SpotInfo) => {
  console.log('상세보기:', spot)
  emit('spot-detail', spot)
}

// 컴포넌트 마운트 시 테스트 데이터 설정
onMounted(() => {
  // 실제로는 여기서 서버 데이터를 받아올 예정
  selectedSpots.value = testData
})
</script>

<style scoped>
.selected-spots-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  flex: 1;
  min-height: 200px;
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
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.spot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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

/* 스크롤바 스타일링 */
.spots-list::-webkit-scrollbar {
  height: 6px;
}

.spots-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.spots-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.spots-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 반응형 */
@media (max-width: 768px) {
  .tag-container {
    justify-content: center;
  }

  .spot-card {
    width: 160px;
  }

  .spots-list {
    gap: 10px;
  }
}
</style>
