<script setup lang="ts">
import { useRouter } from 'vue-router'
import BoardDetail from '@/components/board/BoardDetail.vue'

const router = useRouter()
defineProps<{
  id: string
}>()
</script>

<template>
  <div class="board-container">
    <div class="board-detail">
      <BoardDetail :board-id="Number(id)" @back="() => router.push('/board')" />
    </div>
  </div>
</template>

<style scoped>
.board-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.board-detail {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}
</style>
