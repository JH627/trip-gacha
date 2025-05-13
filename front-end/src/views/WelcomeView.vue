<script setup lang="ts">
import { onMounted } from 'vue'
import { defaultApi } from '@/api/axios'

// API 테스트를 위한 함수
const testPostRequest = async () => {
  try {
    const testData = {
      email: 'string@test.com',
      password: 'string',
    }
    const response = await defaultApi.post('/user/login', testData)
    console.log('POST 요청 성공:', response.data)
  } catch (error) {
    console.error('POST 요청 실패:', error)
  }
}

// 컴포넌트 마운트 시 테스트 실행
onMounted(() => {
  testGetRequest()
  testPostRequest()
})
</script>

<template>
  <div class="welcome">
    <h1>API 테스트 페이지</h1>
    <div class="test-buttons">
      <button @click="testGetRequest" class="test-button">GET 요청 테스트</button>
      <button @click="testPostRequest" class="test-button">POST 요청 테스트</button>
    </div>
  </div>
</template>

<style scoped>
.welcome {
  padding: 2rem;
  text-align: center;
}

.test-buttons {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.test-button {
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.test-button:hover {
  background-color: #357abd;
}
</style>
