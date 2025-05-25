<script setup lang="ts">
import type { ScheduleDetailItem } from '@/types/trip'

interface Props {
  spots: ScheduleDetailItem[]
}

defineProps<Props>()

const DEFAULT_SPOT_IMAGE = new URL('@/assets/no_img.png', import.meta.url).href
</script>

<template>
  <div class="spots-container">
    <div v-for="item in spots" :key="item.spotInfo.spotId" class="spot-item">
      <img 
        :src="item.spotInfo.img || DEFAULT_SPOT_IMAGE" 
        :alt="item.spotInfo.name" 
        class="spot-image" 
      />
      <div class="spot-info">
        <h4>{{ item.spotInfo.name }}</h4>
        <p class="spot-address">{{ item.spotInfo.address }}</p>
        <p class="spot-time" v-if="item.spotInfo.workTime">운영시간: {{ item.spotInfo.workTime }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spots-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 500px;
  overflow-y: auto;
  padding: 4px;
  background-color: #fafafa;
  border-radius: 8px;
}

.spots-container::-webkit-scrollbar {
  width: 6px;
}

.spots-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.spots-container::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.spots-container::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.spot-item {
  display: flex;
  gap: 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  border: 1px solid #e8e8e8;
  padding: 12px;
  height: 90px;
}

.spot-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #1890ff;
  background-color: #f8f8f8;
}

.spot-image {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
  border: 1px solid #f0f0f0;
}

.spot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  overflow: hidden;
}

.spot-info h4 {
  margin: 0;
  font-size: 1rem;
  color: #1a1a1a;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.spot-info p {
  margin: 0;
  color: #666;
  font-size: 0.8rem;
  line-height: 1.3;
}

.spot-address {
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.spot-time {
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 768px) {
  .spots-container {
    max-height: none;
    padding: 0;
    background-color: transparent;
  }

  .spot-item {
    height: 80px;
    padding: 8px;
  }

  .spot-image {
    width: 60px;
    height: 60px;
  }

  .spot-info h4 {
    font-size: 0.95rem;
  }

  .spot-info p {
    font-size: 0.75rem;
  }
}
</style> 