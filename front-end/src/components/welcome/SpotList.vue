<script setup lang="ts">
import SpotListDetail from './SpotListDetail.vue'
import { ref } from 'vue'

interface Destination {
  id: number
  name: string
  description: string
  img: string
}

defineProps<{
  destinations: Destination[]
}>()

const spotListRef = ref<HTMLElement | null>(null)

const scroll = (direction: 'left' | 'right') => {
  if (!spotListRef.value) return
  
  const scrollAmount = spotListRef.value.clientWidth
  const newScrollPosition = spotListRef.value.scrollLeft + (direction === 'left' ? -scrollAmount : scrollAmount)
  
  spotListRef.value.scrollTo({
    left: newScrollPosition,
    behavior: 'smooth'
  })
}
</script>

<template>
  <div class="spot-list-container">
    <button class="nav-button left" @click="scroll('left')">&lt;</button>
    <div class="spot-list" ref="spotListRef">
      <SpotListDetail
        v-for="destination in destinations"
        :key="destination.id"
        :destination="destination"
      />
    </div>
    <button class="nav-button right" @click="scroll('right')">&gt;</button>
  </div>
</template>

<style scoped>
.spot-list-container {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.spot-list {
  display: flex;
  gap: 1rem;
  padding: 1rem 0;
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

.spot-list::-webkit-scrollbar {
  display: none;
}

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

.nav-button.left {
  left: 0.5rem;
}

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

  .spot-list:active {
    cursor: grabbing;
  }
}
</style>
