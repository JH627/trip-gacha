<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/axios'

const content = ref('')

// API 테스트를 위한 함수

const testLogoutRequest = async () => {
  try {
    const response = await authApi.post('/auth/logout')
    console.error('로그아웃 요청 성공:', response)
  } catch (error) {
    console.error('로그아웃 요청 실패:', error)
  }
}

const testGetRequest = async () => {
  try {
    const response = await authApi.get('/trip/recommend')
    content.value = response.data
    console.log('GET 요청 성공:', response.data)
  } catch (error) {
    console.error('GET 요청 실패:', error)
  }
}

const router = useRouter()
const goToLogin = () => {
  router.push('/login') // 로그인 페이지로
}
</script>

<template>
  <div class="welcome">
    <h1>API 테스트 페이지</h1>
    <div class="test-buttons">
      <button @click="goToLogin" class="test-button">로그인 페이지로</button>
      <button @click="testGetRequest" class="test-button">GET 테스트</button>
      <button @click="testLogoutRequest" class="test-button">로그아웃 테스트</button>
    </div>
  </div>
  <p>{{ content }}</p>
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
