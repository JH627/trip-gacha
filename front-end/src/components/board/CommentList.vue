<script setup lang="ts">
import { List, Typography, Button } from 'ant-design-vue'
import type { Comment } from '@/types/board'

const { Text } = Typography

const props = defineProps<{
  comments: Comment[]
}>()

const emit = defineEmits(['delete'])

const handleDelete = (commentId: number) => {
  emit('delete', commentId)
}
</script>

<template>
  <List class="comments-list" :data-source="comments" :row-key="(record) => record.commentId">
    <template #renderItem="{ item }">
      <List.Item>
        <div class="comment">
          <div class="comment-header">
            <Text strong>{{ item.authorName }}</Text>
            <Text type="secondary">
              {{
                new Date(item.createdAt).toLocaleString('ko-KR', {
                  year: 'numeric',
                  month: '2-digit',
                  day: '2-digit',
                  hour: '2-digit',
                  minute: '2-digit',
                })
              }}
            </Text>
          </div>
          <div class="comment-content">
            <Text v-if="item.isDeleted" type="secondary">삭제된 댓글입니다</Text>
            <template v-else>{{ item.content }}</template>
          </div>
          <div class="comment-actions">
            <Button v-if="item.isMine && !item.isDeleted" type="text" danger @click="handleDelete(item.commentId)">
              삭제
            </Button>
          </div>
        </div>
      </List.Item>
    </template>
  </List>
</template>

<style scoped>
.comment {
  width: 100%;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.comment:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  align-items: center;
}

.comment-content {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #333;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

:deep(.ant-list-item) {
  padding: 0 !important;
  margin-bottom: 16px !important;
}

:deep(.ant-typography) {
  margin-bottom: 0 !important;
}

:deep(.ant-btn) {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
}
</style>
