<script setup lang="ts">
import SpotListDetail from './SpotListDetail.vue'
import { ref } from 'vue'
import type { Destination } from '@/types/trip'

defineProps<{
  destinations: Destination[]
}>()

const spotListRef = ref<HTMLElement | null>(null)

// 스크롤 이동
const scroll = (direction: 'left' | 'right') => {
  if (!spotListRef.value) {
    return
  }

  const scrollAmount = spotListRef.value.clientWidth
  const newScrollPosition =
    spotListRef.value.scrollLeft + (direction === 'left' ? -scrollAmount : scrollAmount)

  spotListRef.value.scrollTo({
    left: newScrollPosition,
    behavior: 'smooth',
  })
}
</script>

<template>
  <div class="spot-list-container">
    <!-- 스크롤 이동 버튼 - 왼쪽 -->
    <button class="nav-button left" @click="scroll('left')">&lt;</button>
    <!-- 여행지 목록 -->
    <div class="spot-list" ref="spotListRef">
      <!-- 여행지 상세 블럭 -->
      <SpotListDetail
        v-for="destination in destinations"
        :key="destination.id"
        :destination="destination"
        @click="() => $emit('select', destination)"
      />
    </div>
    <!-- 스크롤 이동 버튼 - 오른쪽 -->
    <button class="nav-button right" @click="scroll('right')">&gt;</button>
  </div>
</template>

<style scoped>
/* 여행지 목록 컨테이너 */
.spot-list-container {
  width: 100%;
  overflow: hidden;
  position: relative;
}

/* 여행지 목록 */
.spot-list {
  display: flex;
  gap: 1rem;
  padding: 1rem 0;
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 여행지 목록 스크롤바 */
.spot-list::-webkit-scrollbar {
  display: none;
}

/* 스크롤 이동 버튼 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  display: none;
  z-index: 2;
  transition: background-color 0.3s;
}
.nav-button:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

/* 스크롤 이동 버튼 - 왼쪽 */
.nav-button.left {
  left: 0.5rem;
}

/* 스크롤 이동 버튼 - 오른쪽 */
.nav-button.right {
  right: 0.5rem;
}

@media (hover: hover) {
  .nav-button {
    display: block;
  }

  .spot-list {
    cursor: default;
  }
}

@media screen and (max-width: 768px) {
  .nav-button {
    display: none;
  }

  .spot-list {
    cursor: grab;
  }

  /* 모바일 터치 이벤트 */
  .spot-list:active {
    cursor: grabbing;
  }
}
</style>
