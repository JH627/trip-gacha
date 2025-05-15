<script setup lang="ts">
import { ref } from 'vue'
import { defaultApi } from '@/api/axios'

const emit = defineEmits<{
  (e: 'next'): void
}>()

const email = ref('')
const emailCheck = ref('')
const showEmailCheck = ref(false)
const isEmailVerified = ref(false)

const handleEmailVerify = async () => {
  try {
    await defaultApi.post('/email/verification', {
      email: email.value,
    })
    showEmailCheck.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleEmailCheck = async () => {
  try {
    await defaultApi.post('/email/verification-confirm', {
      email: email.value,
      code: emailCheck.value,
    })
    isEmailVerified.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleNext = () => {
  if (isEmailVerified.value) {
    emit('next')
  }
}
</script>

<template>
  <div class="regist-form">
    <h1 class="form-title">회원가입</h1>
    <div class="form-group">
      <label for="email">이메일</label>
      <div class="input-group">
        <input type="email" id="email" v-model="email" placeholder="이메일을 입력하세요" required />
        <button
          type="button"
          class="verify-btn"
          @click="handleEmailVerify"
          :disabled="showEmailCheck"
        >
          전송
        </button>
      </div>
    </div>

    <div class="form-group" v-if="showEmailCheck">
      <label for="emailCheck">이메일 확인</label>
      <div class="input-group">
        <input
          type="email"
          id="emailCheck"
          v-model="emailCheck"
          placeholder="인증번호를 입력하세요"
          required
        />
        <button type="button" class="verify-btn" @click="handleEmailCheck">확인</button>
      </div>
    </div>

    <div class="button-group">
      <button type="button" class="next-btn" @click="handleNext" :disabled="!isEmailVerified">
        다음
      </button>
    </div>
  </div>
</template>

<style scoped>
.regist-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-title {
  text-align: center;
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-size: 0.9rem;
  color: #666;
  font-weight: bold;
}

.input-group {
  display: flex;
  gap: 0.5rem;
}

input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: #4a90e2;
}

.verify-btn {
  padding: 0.8rem 1.5rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.verify-btn:hover {
  background-color: #357abd;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.next-btn {
  padding: 0.8rem 2rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.next-btn:hover:not(:disabled) {
  background-color: #357abd;
}

.next-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
