<script setup lang="ts">
import { Input, Button, Space } from 'ant-design-vue'
import { ref } from 'vue'
import { BoardDetail } from '@/types/board'

const props = defineProps<{
  board: BoardDetail
}>()

const emit = defineEmits(['update'])

const isEditing = ref(false)
const editedContent = ref('')

const handleEdit = () => {
  editedContent.value = props.board.content
  isEditing.value = true
}

const handleUpdate = () => {
  emit('update', editedContent.value)
  isEditing.value = false
}
</script>

<template>
  <div class="board-content">
    <div v-if="!isEditing" class="content-text">
      {{ board.content }}
    </div>
    <div v-else class="edit-area">
      <Input.TextArea v-model:value="editedContent" :rows="10" />
      <Space class="edit-buttons">
        <Button type="primary" @click="handleUpdate">수정완료</Button>
        <Button @click="isEditing = false">취소</Button>
      </Space>
    </div>
  </div>
</template>

<style scoped>
.board-content {
  margin-bottom: 32px;
  padding: 32px;
  background-color: #fafafa;
  border-radius: 12px;
  min-height: 200px;
  line-height: 1.8;
  font-size: 16px;
  color: #333;
}

.edit-area {
  margin-bottom: 24px;
}

.edit-buttons {
  margin-top: 20px;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.ant-input-textarea) {
  border-radius: 8px;
}
</style>
