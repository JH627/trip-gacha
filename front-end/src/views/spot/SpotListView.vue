<template>
  <div class="spot-list-page">
    <!-- 최상단: 검색바 -->
    <div class="top-search-bar">
      <input type="text" placeholder="관광지 검색..." v-model="search" />
    </div>

    <!-- 상단: 카테고리/정렬 -->
    <div class="top-controls">
      <div class="categories">
        <button
          v-for="cat in categories"
          :key="cat"
          :class="{ active: selectedCategory === cat }"
          @click="selectedCategory = cat"
        >
          {{ cat }}
        </button>
      </div>
      <div class="sort-select">
        <select v-model="sort">
          <option value="인기순">인기순</option>
          <option value="평점순">평점순</option>
          <option value="리뷰순">리뷰순</option>
        </select>
      </div>
    </div>

    <!-- 메인: 관광지 리스트 + 사이드 -->
    <div class="main-content">
      <!-- 관광지 카드 리스트 -->
      <div class="spot-list">
        <SpotCard
          v-for="spot in filteredSpots"
          :key="spot.id"
          :spot="spot"
          @toggle-wish="toggleWish"
        />
      </div>
      <!-- 우측 추천/지도 (예시) -->
      <div class="side-bar">
        <div class="side-title">추천 관광지</div>
        <div class="side-recommend">
          <div v-for="rec in recommends" :key="rec.id" class="side-rec-item">
            <img :src="rec.img" />
            <div>
              <div>{{ rec.name }}</div>
              <div class="side-rec-score">{{ rec.score }}</div>
            </div>
          </div>
        </div>
        <div class="side-title">관광지 지도</div>
        <div class="side-map">지도 영역</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import SpotCard from '@/components/spot/SpotCard.vue'

// 예시 데이터
const search = ref('')
const categories = ['전체', '명소', '박물관', '카페', '역사', '테마파크']
const selectedCategory = ref('전체')
const sort = ref('인기순')

const spots = ref([
  {
    id: 1,
    name: '성산일출봉',
    category: '명소',
    img: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb',
    desc: '유네스코 세계자연유산으로 등재된 제주도 대표적인 관광명소입니다.',
    location: '제주특별자치도 서귀포시 성산읍',
    time: '07:00~20:00',
    price: '성인 5,000원',
    wish: false,
    score: 4.6,
    review: 342,
    visit: 12458,
  },
  // ...더미 데이터 추가
])

const recommends = [
  { id: 1, name: '우도', img: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb', score: 4.7 },
  { id: 2, name: '협재 해수욕장', img: 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca', score: 4.6 },
  { id: 3, name: '오설록 티 뮤지엄', img: 'https://images.unsplash.com/photo-1465101178521-c1a9136a3b99', score: 4.5 },
]

const filteredSpots = computed(() => {
  let list = spots.value
  if (selectedCategory.value !== '전체') {
    list = list.filter(s => s.category === selectedCategory.value)
  }
  if (search.value) {
    list = list.filter(s => s.name.includes(search.value))
  }
  // 정렬
  if (sort.value === '인기순') list = [...list].sort((a, b) => b.visit - a.visit)
  if (sort.value === '평점순') list = [...list].sort((a, b) => b.score - a.score)
  if (sort.value === '리뷰순') list = [...list].sort((a, b) => b.review - a.review)
  return list
})

function toggleWish(spotId: number) {
  const spot = spots.value.find(s => s.id === spotId)
  if (spot) spot.wish = !spot.wish
}
</script>

<style scoped>
.spot-list-page {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 0;
}
.top-search-bar {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 18px;
}
.top-search-bar input {
  width: 400px;
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 1rem;
}
.top-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}
.categories {
  display: flex;
  gap: 10px;
}
.categories button {
  background: #f7f8fa;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 1rem;
  cursor: pointer;
}
.categories button.active {
  background: #1677ff;
  color: #fff;
  font-weight: bold;
}
.sort-select select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
}
.main-content {
  display: flex;
  gap: 32px;
}
.spot-list {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.spot-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  overflow: hidden;
}
.spot-img {
  width: 320px;
  height: 160px;
  object-fit: cover;
}
.spot-info {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.spot-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.spot-desc {
  color: #555;
  margin-bottom: 12px;
}
.spot-meta {
  font-size: 0.95rem;
  color: #888;
  margin-bottom: 12px;
  display: flex;
  gap: 18px;
}
.spot-actions {
  margin-top: 8px;
}
.wish-btn {
  background: #fff0f6;
  color: #d4386f;
  border: none;
  border-radius: 8px;
  padding: 8px 24px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}
.wish-btn.wished {
  background: #d4386f;
  color: #fff;
}
.side-bar {
  width: 220px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.side-title {
  font-weight: bold;
  margin-bottom: 8px;
}
.side-recommend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.side-rec-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f7f8fa;
  border-radius: 8px;
  padding: 8px;
}
.side-rec-item img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}
.side-rec-score {
  color: #888;
  font-size: 0.95rem;
}
.side-map {
  background: #f7f8fa;
  border-radius: 8px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aaa;
}
@media (max-width: 1000px) {
  .main-content {
    flex-direction: column;
  }
  .side-bar {
    width: 100%;
    flex-direction: row;
    gap: 16px;
  }
}
</style>
