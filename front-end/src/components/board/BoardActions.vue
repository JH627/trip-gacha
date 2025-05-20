<script setup lang="ts">
import { Button, Space } from 'ant-design-vue'
import { BoardDetail } from '@/types/board'

const props = defineProps<{
  board: BoardDetail
}>()

const emit = defineEmits(['like', 'dislike', 'report', 'edit', 'delete'])

const handleLike = () => emit('like')
const handleDislike = () => emit('dislike')
const handleReport = () => emit('report')
const handleEdit = () => emit('edit')
const handleDelete = () => emit('delete')

console.log(props.board)
</script>

<template>
  <Space class="board-actions">
    <Button :type="board.isLiked ? 'primary' : 'default'" @click="handleLike">
      좋아요 ({{ board.likeCount }})
    </Button>
    <Button :type="board.isDisliked ? 'danger' : 'default'" @click="handleDislike"> 싫어요 </Button>
    <template v-if="board.mine">
      <Button @click="handleEdit">수정</Button>
      <Button danger @click="handleDelete">삭제</Button>
    </template>
    <template v-else>
      <Button @click="handleReport">신고</Button>
    </template>
  </Space>
</template>

<style scoped>
.board-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  margin-bottom: 40px;
  gap: 12px;
}

:deep(.ant-btn) {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
  flex: 1;
  min-width: 120px;
  max-width: 200px;
}

@media screen and (max-width: 768px) {
  .board-actions {
    flex-direction: column;
    align-items: stretch;
  }

  :deep(.ant-btn) {
    max-width: none;
  }
}
</style>
