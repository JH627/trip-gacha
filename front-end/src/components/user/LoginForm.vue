<template>
  <div class="login-box">
    <h1 class="login-title">로그인</h1>
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label for="email">이메일</label>
        <input type="email" id="email" v-model="email" placeholder="이메일을 입력하세요" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호</label>
        <input
          type="password"
          id="password"
          v-model="password"
          placeholder="비밀번호를 입력하세요"
          required
        />
      </div>
      <button type="submit" class="login-button">로그인</button>
    </form>
    <div class="signup-link">
      <router-link to="/regist">회원가입</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')

const handleLogin = async () => {
  try {
    await authStore.login({
      email: email.value,
      password: password.value,
    })
    router.push('/')
  } catch (error) {
    console.error('로그인 실패:', error)
    alert('로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.')
  }
}
</script>

<style scoped>
.login-box {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  width: 100%;
}

.login-title {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-size: 0.9rem;
  color: #666;
}

input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #4a90e2;
}

.login-button {
  background-color: #4a90e2;
  color: white;
  padding: 1rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #357abd;
}

.signup-link {
  text-align: center;
  margin-top: 1.5rem;
}

.signup-link a {
  color: #4a90e2;
  text-decoration: none;
  font-size: 0.9rem;
}

.signup-link a:hover {
  text-decoration: underline;
}

/* Mobile styles */
@media (max-width: 768px) {
  .login-box {
    padding: 1.5rem;
  }

  .login-title {
    font-size: 1.5rem;
  }

  input {
    padding: 0.7rem;
  }
}
</style>
