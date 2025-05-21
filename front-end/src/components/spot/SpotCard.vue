<script setup lang="ts">
const props = defineProps({
  spot: Object
})
const emit = defineEmits(['toggle-wish'])
</script>

<template>
  <div class="spot-card">
    <img class="spot-img" :src="spot.img" />
    <div class="spot-info">
      <div class="spot-title">{{ spot.name }}</div>
      <div class="spot-desc">{{ spot.desc }}</div>
      <div class="spot-meta">
        <span>{{ spot.location }}</span>
        <span>{{ spot.time }}</span>
        <span>{{ spot.price }}</span>
      </div>
      <div class="spot-actions">
        <button
          :class="spot.wish ? 'wish-btn wished' : 'wish-btn'"
          @click="$emit('toggle-wish', spot.id)"
        >
          {{ spot.wish ? '찜 취소' : '찜하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spot-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}



</style>
