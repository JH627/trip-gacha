<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Form, Input, Select, Button, message } from 'ant-design-vue'
import { authApi } from '@/api/axios'
import { BoardDetail } from '@/types/board'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  boardId: number
}>()

const emit = defineEmits(['back', 'update'])

const formRef = ref()
const loading = ref(false)
const formState = ref({
  title: '',
  content: '',
  category: 'free',
})

const fetchBoardDetail = async () => {
  try {
    const response = await authApi.get(`/board/${props.boardId}`)
    const board = response.data.result
    formState.value = {
      title: board.title,
      content: board.content,
      category: board.category,
    }
  } catch (error) {
    console.error('Failed to fetch board detail:', error)
    message.error('게시글을 불러오는데 실패했습니다.')
  }
}

const handleSubmit = async () => {
  try {
    loading.value = true
    await formRef.value.validate()

    const formData = new FormData()
    formData.append('boardId', props.boardId.toString())
    formData.append('title', formState.value.title)
    formData.append('content', formState.value.content)
    formData.append('category', formState.value.category)

    await authApi.patch('/board', formData)
    message.success('게시글이 수정되었습니다.')
    emit('update')
    emit('back')
  } catch (error) {
    console.error('Failed to update board:', error)
    message.error('게시글 수정에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBoardDetail()
})
</script>

<template>
  <div class="write-container">
    <div class="back-button-container">
      <Button @click="emit('back')" type="link" class="back-button">
        <template #icon><ArrowLeftOutlined /></template>
        목록으로 돌아가기
      </Button>
    </div>
    <h2 class="edit-title">게시글 수정</h2>
    <Form ref="formRef" :model="formState" layout="vertical">
      <Form.Item
        name="category"
        label="카테고리"
        :rules="[{ required: true, message: '카테고리를 선택해주세요' }]"
      >
        <Select v-model:value="formState.category">
          <Select.Option value="free">자유</Select.Option>
          <Select.Option value="idea">건의</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="title"
        label="제목"
        :rules="[{ required: true, message: '제목을 입력해주세요' }]"
      >
        <Input v-model:value="formState.title" placeholder="제목을 입력하세요" />
      </Form.Item>

      <Form.Item
        name="content"
        label="내용"
        :rules="[{ required: true, message: '내용을 입력해주세요' }]"
      >
        <Input.TextArea
          v-model:value="formState.content"
          placeholder="내용을 입력하세요"
          :rows="10"
          :maxlength="1000"
          show-count
        />
      </Form.Item>

      <Form.Item class="form-actions">
        <Button type="primary" @click="handleSubmit" :loading="loading">수정하기</Button>
        <Button @click="emit('back')">취소</Button>
      </Form.Item>
    </Form>
  </div>
</template>

<style scoped>
.write-container {
  width: 100%;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.back-button-container {
  margin-bottom: 16px;
}

.back-button {
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
  padding: 8px 16px;
}

.back-button:hover {
  color: #1890ff;
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 4px;
}

.edit-title {
  margin-bottom: 24px;
  font-size: 16px;
  font-weight: bold;
  color: #222;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
  color: #222;
}

:deep(.ant-input),
:deep(.ant-input-textarea),
:deep(.ant-select-selector) {
  border-radius: 8px;
  border-color: #d9d9d9;
}

:deep(.ant-input:hover),
:deep(.ant-input-textarea:hover),
:deep(.ant-select-selector:hover) {
  border-color: #40a9ff;
}

:deep(.ant-btn) {
  border-radius: 4px;
  height: 36px;
  padding: 0 16px;
  font-weight: 500;
}

:deep(.ant-form-item) {
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .write-container {
    padding: 16px;
  }
}
</style>
