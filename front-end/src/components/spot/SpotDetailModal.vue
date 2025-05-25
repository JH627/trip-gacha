<script setup lang="ts">
import type { SpotInfo } from '@/types/trip'
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps'

const props = defineProps<{
  spot: SpotInfo | null
  show: boolean
}>()

const emit = defineEmits<{
  close: []
}>()

const closeModal = () => {
  emit('close')
}

const mapOptions = {
  center: { lat: 33.450701, lng: 126.570667 },
  level: 3
}
</script>

<template>
  <Teleport to="body">
    <div v-if="show && spot" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeModal">×</button>
        <div class="modal-header">
          <img :src="spot.img" class="modal-img" />
          <div class="modal-title">
            <h2>{{ spot.name }}</h2>
            <div class="modal-stars">★ {{ spot.stars?.toFixed(1) ?? '0.0' }}</div>
          </div>
        </div>
        <div class="modal-body">
          <div class="info-section">
            <h3>기본 정보</h3>
            <div class="info-item">
              <span class="label">주소</span>
              <span class="value">{{ spot.address }}</span>
            </div>
            <div class="info-item">
              <span class="label">영업시간</span>
              <span class="value">{{ spot.workTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">전화번호</span>
              <span class="value">{{ spot.phone }}</span>
            </div>
          </div>
          <div class="info-section">
            <h3>상세 설명</h3>
            <p class="description">{{ spot.content }}</p>
          </div>
          <div class="info-section">
            <h3>위치</h3>
            <div class="map-container">
              <KakaoMap
                v-if="show"
                :lat="spot.latitude"
                :lng="spot.longitude"
                :options="mapOptions"
                class="kakao-map"
              >
                <KakaoMapMarker
                  :lat="spot.latitude"
                  :lng="spot.longitude"
                />
              </KakaoMap>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  padding: 24px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 8px;
  line-height: 1;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f0f0f0;
}

.modal-header {
  margin-bottom: 24px;
}

.modal-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
  margin-bottom: 16px;
}

.modal-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #222;
  margin: 0;
}

.modal-stars {
  background: #fff0f6;
  color: #ffb400;
  font-weight: 700;
  font-size: 1.2rem;
  padding: 8px 16px;
  border-radius: 12px;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
}

.info-section h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  width: 100px;
  color: #666;
  font-weight: 500;
}

.value {
  flex: 1;
  color: #333;
}

.description {
  color: #444;
  line-height: 1.6;
  margin: 0;
  white-space: pre-line;
}

.map-container {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-top: 12px;
}

.kakao-map {
  width: 100%;
  height: 100%;
}

@media (max-width: 600px) {
  .modal-content {
    width: 95%;
    padding: 16px;
  }

  .modal-img {
    height: 200px;
  }

  .modal-title h2 {
    font-size: 1.4rem;
  }

  .info-item {
    flex-direction: column;
    gap: 4px;
  }

  .label {
    width: auto;
  }

  .map-container {
    height: 200px;
  }
}
</style>
