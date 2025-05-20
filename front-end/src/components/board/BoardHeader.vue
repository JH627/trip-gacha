<script setup lang="ts">
import { Typography, Space } from 'ant-design-vue'
import { BoardDetail } from '@/types/board'

const { Title, Text } = Typography

defineProps<{
  board: BoardDetail
}>()
</script>

<template>
  <div class="board-header">
    <Title :level="2">{{ board.title }}</Title>
    <Space class="board-info">
      <Text>작성자: {{ board.authorName }}</Text>
      <Text>작성일: {{ new Date(board.createdAt).toLocaleDateString() }}</Text>
      <Text>조회수: {{ board.viewCount }}</Text>
    </Space>
  </div>
</template>

<style scoped>
.board-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
}

.board-info {
  margin-top: 12px;
  color: #666;
}
</style>
