<script setup lang="ts">
import { ref } from 'vue'
import { authApi } from '@/api/axios'
import type { ChatMessage, ChatResponse } from '@/types/chat'

// 채팅 메시지
const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const loading = ref(false)

// 메시지 전송
const sendMessage = async () => {
  // 메시지가 없으면 종료
  if (!inputMessage.value.trim()) {
    return
  }

  // 메시지 추가
  messages.value.push({
    text: inputMessage.value,
    isUser: true,
  })

  // 메시지 초기화
  const userMessage = inputMessage.value
  inputMessage.value = ''
  loading.value = true

  // 메시지 전송
  try {
    const response = await authApi.post<ChatResponse>('/chat', {
      content: userMessage,
    })

    messages.value.push({
      text: response.data.result,
      isUser: false,
    })
  } catch (error) {
    console.error(error)
    messages.value.push({
      text: '죄송합니다. 오류가 발생했습니다.',
      isUser: false,
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="chat-container">
    <div class="chat-messages">
      <!-- 처음 화면 -->
      <div v-if="messages.length === 0" class="placeholder-message">
        AI 채팅은 로그인 후에 이용가능합니다. <br />
        새로고침 시 채팅이 초기화됩니다.
      </div>
      <!-- 채팅 메시지 -->
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.isUser ? 'user-message' : 'ai-message']"
      >
        {{ message.text }}
      </div>
      <!-- AI 응답 메시지 -->
      <div v-if="loading" class="message ai-message">AI가 응답하고 있습니다...</div>
    </div>
    <!-- 채팅 입력 컨테이너 -->
    <div class="chat-input">
      <a-input
        v-model:value="inputMessage"
        placeholder="메시지를 입력하세요..."
        @pressEnter="sendMessage"
      >
        <!-- 전송 버튼 -->
        <template #suffix>
          <a-button type="primary" :loading="loading" @click="sendMessage"> 전송 </a-button>
        </template>
      </a-input>
    </div>
  </div>
</template>

<style scoped>
/* 채팅 컨테이너 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

/* 채팅 메시지 컨테이너 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 채팅 메시지 */
.message {
  max-width: 80%;
  padding: 0.8rem 1rem;
  border-radius: 12px;
  word-break: break-word;
}

/* 사용자 메시지 */
.user-message {
  align-self: flex-end;
  background-color: #1890ff;
  color: white;
}

/* AI 메시지 */
.ai-message {
  align-self: flex-start;
  background-color: white;
  border: 1px solid #d9d9d9;
  white-space: pre-line;
}

/* 채팅 입력 컨테이너 */
.chat-input {
  padding: 1rem;
  background-color: white;
  border-top: 1px solid #d9d9d9;
}

/* 채팅 입력 버튼 */
.chat-input :deep(.ant-input-affix-wrapper) {
  border-radius: 20px;
}

.chat-input :deep(.ant-btn) {
  border-radius: 20px;
}

/* 빈 메시지 */
.placeholder-message {
  text-align: center;
  color: #999;
  font-size: 0.9rem;
  margin: auto;
  padding: 2rem;
}
</style>
