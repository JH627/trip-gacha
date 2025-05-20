<script setup lang="ts">
import { ref } from 'vue'
import { Form, Input, Select, Button, message } from 'ant-design-vue'
import { authApi } from '@/api/axios'

const emit = defineEmits(['submit-success'])

const formRef = ref()
const formState = ref({
  title: '',
  content: '',
  category: 'FREE',
})

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    const formData = new FormData()
    formData.append('title', formState.value.title)
    formData.append('content', formState.value.content)
    formData.append('category', formState.value.category)

    await authApi.post('/board', formData)
    message.success('게시글이 작성되었습니다.')
    emit('submit-success')
  } catch (error) {
    console.error('Failed to create board:', error)
    message.error('게시글 작성에 실패했습니다.')
  }
}
</script>

<template>
  <div class="write-container">
    <Form ref="formRef" :model="formState" layout="vertical">
      <Form.Item
        name="category"
        label="카테고리"
        :rules="[{ required: true, message: '카테고리를 선택해주세요' }]"
      >
        <Select v-model:value="formState.category">
          <Select.Option value="FREE">자유</Select.Option>
          <Select.Option value="IDEA">건의</Select.Option>
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
        />
      </Form.Item>

      <Form.Item>
        <Button type="primary" @click="handleSubmit"> 작성하기 </Button>
      </Form.Item>
    </Form>
  </div>
</template>

<style scoped>
.write-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
