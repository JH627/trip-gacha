<script setup lang="ts">
import type { Destination } from '@/types/trip'

defineProps<{
  destination: Destination
}>()
</script>

<template>
  <!-- 여행지 상세 블럭 -->
  <div class="spot-card" @click="$emit('click')">
    <!-- 여행지 이미지 -->
    <div class="spot-image">
      <img :src="destination.img" :alt="destination.name" />
    </div>
    <!-- 여행지 정보 -->
    <div class="spot-info">
      <h3>{{ destination.name }}</h3>
      <!-- 여행지 설명 추가 필요 -->
      <!-- <p>{{ destination.description }}</p> -->
    </div>
  </div>
</template>

<style scoped>
/* 여행지 상세 블럭 */
.spot-card {
  flex: 0 0 300px;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  cursor: pointer;
}
.spot-card:hover {
  transform: translateY(-4px);
  background: #9ea3c2;
}

/* 여행지 이미지 컨테이너 */
.spot-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

/* 여행지 이미지 */
.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

/* 여행지 카드 호버 */
.spot-card:hover .spot-image img {
  transform: scale(1.05);
}

/* 여행지 정보 */
.spot-info {
  padding: 1rem;
}

/* 여행지 제목 */
.spot-info h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

/* 후에 추가 필요 */
/* 여행지 설명 */
.spot-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
