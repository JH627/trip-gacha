<script setup lang="ts">
import type { ScheduleInfo } from '@/types/trip'

defineProps<{
  schedule: ScheduleInfo
}>()

const emit = defineEmits<{
  (e: 'click', scheduleId: number): void
}>()

const handleClick = (scheduleId: number) => {
  emit('click', scheduleId)
}
</script>

<template>
  <div class="schedule-card" @click="handleClick(schedule.scheduleId)">
    <div class="schedule-card-img">
      <img :src="schedule.destinationImg" alt="여행 이미지" />
    </div>
    <div class="schedule-card-content">
      <p>{{ schedule.title }}</p>
      <p>{{ schedule.destination }}</p>
      <p>{{ schedule.startDate }} ~ {{ schedule.endDate }}</p>
    </div>
  </div>
</template>

<style scoped>
.schedule-card {
  display: flex;
  flex-direction: column;
  background-color: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.schedule-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.schedule-card-img {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.schedule-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.schedule-card:hover .schedule-card-img img {
  transform: scale(1.05);
}

.schedule-card-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.schedule-card-content p:first-child {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.schedule-card-content p:nth-child(2) {
  font-size: 1rem;
  color: #4a4a4a;
  font-weight: 500;
}

.schedule-card-content p:last-child {
  font-size: 0.9rem;
  color: #666;
  margin-top: 4px;
}
</style>
