<script setup lang="ts">
import type { Destination } from '@/types/trip'

const props = defineProps<{
  destination: Destination | null
  open: boolean
}>()

const emit = defineEmits(['close', 'start'])
</script>

<template>
  <!-- 여행지 상세 모달 -->
  <a-modal
    :open="open"
    title="여행지 상세"
    :footer="null"
    @cancel="() => emit('close')"
    width="600px"
  >
    <div v-if="destination" class="modal-content">
      <img :src="destination.img" class="destination-image" />
      <h2>{{ destination.name }}</h2>
      <!-- 여행지 설명 추가 필요 -->
      <!-- <p>{{ destination.description }}</p> -->
      <a-button
        type="primary"
        size="large"
        class="start-button"
        @click="emit('start', destination)"
      >
        여행 시작하기
      </a-button>
    </div>
  </a-modal>
</template>

<style scoped>
/* 여행지 상세 모달 컨테이너 */
.modal-content {
  display: flex;
  flex-direction: column;
}

/* 여행지 이미지 */
.destination-image {
  width: 100%;
  border-radius: 12px;
  margin-bottom: 1rem;
}

/* 여행 시작 버튼 */
.start-button {
  margin-top: 2rem;
  width: 100%;
}
</style> 