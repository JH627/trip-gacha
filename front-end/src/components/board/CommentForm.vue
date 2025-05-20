<script setup lang="ts">
import { Input, Button } from 'ant-design-vue'
import { ref } from 'vue'

const emit = defineEmits(['submit'])

const newComment = ref('')

const handleSubmit = () => {
  if (!newComment.value.trim()) return
  emit('submit', newComment.value)
  newComment.value = ''
}
</script>

<template>
  <div class="comment-form">
    <Input.TextArea v-model:value="newComment" placeholder="댓글을 입력하세요" :rows="3" />
    <Button type="primary" @click="handleSubmit" style="margin-top: 8px"> 댓글 작성 </Button>
  </div>
</template>

<style scoped>
.comment-form {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-bottom: 32px;
  background-color: #fafafa;
  padding: 24px;
  border-radius: 12px;
}

:deep(.ant-input-textarea) {
  border-radius: 8px;
}

:deep(.ant-btn) {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
}
</style>
