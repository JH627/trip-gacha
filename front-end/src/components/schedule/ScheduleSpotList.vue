<script setup lang="ts">
import { ref, watch } from 'vue'
import { MenuOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import type { ScheduleDetailItem } from '@/types/trip'
import defaultImg from '@/assets/no_img.png'
import draggable from 'vuedraggable'

const categoryMap: Record<string, string> = {
  ACCOMMODATION: '숙소',
  ATTRACTION: '명소',
  RESTAURANT: '식당',
  CAFE: '카페',
}

const props = defineProps<{
  spots: ScheduleDetailItem[]
  isEditMode: boolean
  currentDay: number
  availableDays: number[]
}>()

const emit = defineEmits<{
  (e: 'update:spots', spots: ScheduleDetailItem[]): void
  (e: 'moveSpot', spotId: number, targetDay: number): void
  (e: 'removeSpot', spotId: number): void
}>()

const localSpots = ref<ScheduleDetailItem[]>([])

// 부모 컴포넌트에서 전달받은 spots 값을 초기화
watch(
  () => props.spots,
  (newSpots) => {
    localSpots.value = [...newSpots]
  },
  { immediate: true },
)

const handleDragEnd = () => {
  // 순서 업데이트
  localSpots.value = localSpots.value.map((spot, index) => ({
    ...spot,
    order: index + 1,
  }))
  emit('update:spots', localSpots.value)
}

const handleDayChange = (spotId: number, targetDay: number) => {
  emit('moveSpot', spotId, targetDay)
}

const handleRemoveSpot = (spotId: number) => {
  emit('removeSpot', spotId)
}
</script>

<template>
  <div class="spot-list">
    <draggable
      v-model="localSpots"
      :disabled="!isEditMode"
      item-key="spotInfo.spotId"
      handle=".drag-handle"
      @end="handleDragEnd"
    >
      <template #item="{ element: spot }">
        <div class="spot-card">
          <div class="spot-order">{{ spot.order }}</div>
          <div class="drag-handle" v-if="isEditMode">
            <MenuOutlined />
          </div>
          <div class="spot-image">
            <img :src="spot.spotInfo.img || defaultImg" :alt="spot.spotInfo.name" />
          </div>
          <div class="spot-info">
            <h3>{{ spot.spotInfo.name }}</h3>
            <p class="spot-address">{{ spot.spotInfo.address }}</p>
            <p class="spot-category">
              {{ categoryMap[spot.spotInfo.category] || spot.spotInfo.category }}
            </p>
          </div>
          <div v-if="isEditMode" class="spot-actions">
            <select
              :value="spot.day"
              @change="
                (e: Event) =>
                  handleDayChange(
                    spot.spotInfo.spotId,
                    Number((e.target as HTMLSelectElement).value),
                  )
              "
            >
              <option v-for="day in availableDays" :key="day" :value="day">{{ day }}일차</option>
            </select>
            <button class="remove-btn" @click="handleRemoveSpot(spot.spotInfo.spotId)">
              <DeleteOutlined />
            </button>
          </div>
        </div>
      </template>
    </draggable>
  </div>
</template>

<style scoped>
.spot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spot-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  border: 1px solid #f0f0f0;
}

.spot-card:hover {
  border-color: #e6f7ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.spot-order {
  width: 24px;
  height: 24px;
  background-color: #1890ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
  border: 1px solid #40a9ff;
}

.drag-handle {
  cursor: move;
  color: #999;
  padding: 4px;
  display: flex;
  align-items: center;
}

.spot-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid #f0f0f0;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-info {
  flex-grow: 1;
  min-width: 0;
}

.spot-info h3 {
  margin: 0 0 4px;
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
}

.spot-address {
  margin: 0 0 4px;
  font-size: 0.9rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.spot-category {
  margin: 0;
  font-size: 0.8rem;
  color: #1890ff;
  background-color: #e6f7ff;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.spot-actions {
  display: flex;
  gap: 8px;
}

.spot-actions select {
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #666;
  background-color: white;
}

.spot-actions select:hover {
  border-color: #40a9ff;
}

.remove-btn {
  padding: 4px 8px;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  background-color: white;
  color: #ff4d4f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  background-color: #fff1f0;
}
</style>
