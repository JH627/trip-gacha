<script setup lang="ts">
import type { Destination } from '@/types/trip'
import { defineProps, defineEmits } from 'vue'

const props = defineProps<{
  categories: { value: string; label: string }[]
  selectedCategory: string
  destinations: Destination[]
  selectedDestination: string
  sort: string
}>()
const emit = defineEmits(['update:selectedCategory', 'update:selectedDestination', 'update:sort'])
</script>
<template>
  <div class="top-controls">
    <div class="categories">
      <button
        v-for="cat in categories"
        :key="cat.value"
        :class="{ active: props.selectedCategory === cat.value }"
        @click="$emit('update:selectedCategory', cat.value)"
      >
        {{ cat.label }}
      </button>
    </div>
    <div class="controls-right">
      <div class="destination-select">
        <select
          :value="props.selectedDestination"
          @change="
            (e) => $emit('update:selectedDestination', (e.target as HTMLSelectElement)?.value)
          "
        >
          <option value="">전체 지역</option>
          <option v-for="dest in props.destinations" :key="dest.id" :value="dest.id">
            {{ dest.name }}
          </option>
        </select>
      </div>
      <div class="sort-select">
        <select
          :value="props.sort"
          @change="(e) => $emit('update:sort', (e.target as HTMLSelectElement)?.value)"
        >
          <option value="LIKE">인기순</option>
          <option value="STARS">평점순</option>
          <option value="NAME">이름순</option>
        </select>
      </div>
    </div>
    <slot />
  </div>
</template>
<style scoped>
.top-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}
.categories {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.categories button {
  background: #f7f8fa;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
}
.categories button:hover {
  background: #e6f4ff;
}
.categories button.active {
  background: #1677ff;
  color: #fff;
}
.controls-right {
  display: flex;
  gap: 16px;
}
.destination-select select,
.sort-select select {
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-size: 0.95rem;
  background: #fff;
  cursor: pointer;
  min-width: 140px;
}
.destination-select select:hover,
.sort-select select:hover {
  border-color: #1677ff;
}
.destination-select select:focus,
.sort-select select:focus {
  outline: none;
  border-color: #1677ff;
}
@media (max-width: 768px) {
  .top-controls {
    flex-direction: column;
    align-items: stretch;
  }
  .controls-right {
    flex-direction: column;
  }
}
</style>
