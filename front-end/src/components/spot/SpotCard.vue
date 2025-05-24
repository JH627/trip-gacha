<script setup lang="ts">
import type { SpotInfo } from '@/types/trip'
import defaultImg from '@/assets/no_img.png'

const props = defineProps<{
  spot: SpotInfo
}>()

const emit = defineEmits<{
  'toggle-wish': [spotId: number]
  'show-detail': [spot: SpotInfo]
}>()
</script>

<template>
  <div class="spot-card">
    <div class="img-container">
      <img class="spot-img" :src="spot.img || defaultImg" />
      <div class="spot-stars">★ {{ spot.stars?.toFixed(1) ?? '0.0' }}</div>
    </div>
    <div class="spot-info">
      <div class="spot-title">{{ spot.name }}</div>
      <div class="spot-meta">
        <span>{{ spot.address }}</span>
        <span>{{ spot.workTime }}</span>
      </div>
      <div class="spot-desc">{{ spot.content }}</div>
      <div class="spot-actions">
        <button class="detail-btn" @click="$emit('show-detail', spot)">자세히 보기</button>
        <button
          :class="spot.marked ? 'wish-btn wished' : 'wish-btn'"
          @click="$emit('toggle-wish', spot.spotId)"
        >
          {{ spot.marked ? '찜 취소' : '찜하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Card Container */
.spot-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  height: 100%;
  transition: all 0.2s ease;
}

.spot-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.13);
  transform: translateY(-2px);
}

/* Image Section */
.img-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.spot-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-stars {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255, 255, 255, 0.92);
  color: #ffb400;
  font-weight: 700;
  font-size: 1.1rem;
  padding: 4px 12px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}

/* Content Section */
.spot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  gap: 12px;
}

.spot-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #222;
  line-height: 1.4;
  margin: 0;
}

.spot-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #666;
  font-size: 0.9rem;
}

.spot-desc {
  color: #555;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

/* Action Buttons */
.spot-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-top: auto;
}

.detail-btn,
.wish-btn {
  flex: 1;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  text-align: center;
}

.detail-btn {
  background: #f0f5ff;
  color: #1677ff;
}

.detail-btn:hover {
  background: #d6e4ff;
}

.wish-btn {
  background: #fff0f6;
  color: #d4386f;
}

.wish-btn:hover {
  background: #ffd6e7;
}

.wish-btn.wished {
  background: #d4386f;
  color: #fff;
}

.wish-btn.wished:hover {
  background: #c12e63;
}

/* Responsive Design */
@media (max-width: 900px) {
  .img-container {
    height: 180px;
  }

  .spot-info {
    padding: 12px;
    gap: 8px;
  }

  .spot-title {
    font-size: 1.1rem;
  }

  .spot-meta,
  .spot-desc {
    font-size: 0.85rem;
  }

  .detail-btn,
  .wish-btn {
    padding: 6px 12px;
    font-size: 0.85rem;
  }
}
</style>
