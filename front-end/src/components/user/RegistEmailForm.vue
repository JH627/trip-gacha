<script setup lang="ts">
import { ref } from 'vue'
import { defaultApi } from '@/api/axios'

const emit = defineEmits<{
  (e: 'next', email: string): void
}>()

const email = ref('')
const emailCheck = ref('')
const showEmailCheck = ref(false)
const isEmailVerified = ref(false)

// 이메일 인증 번호 전송
const handleEmailVerify = async () => {
  await defaultApi
    .post('/email/verification', {
      email: email.value,
    })
    .then(() => {
      showEmailCheck.value = true
    })
    .catch((error) => console.log(error))
}

// 인증번호 검증
const handleEmailCheck = async () => {
  await defaultApi
    .post('/email/verification-confirm', {
      email: email.value,
      code: emailCheck.value,
    })
    .then(() => {
      isEmailVerified.value = true
    })
    .catch((error) => {
      console.error(error)
    })
}

// 다음 단계로
// 이메일 전달
const handleNext = () => {
  if (isEmailVerified.value) {
    emit('next', email.value)
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
        <button
          type="button"
          class="verify-btn"
          @click="handleEmailCheck"
          :disabled="isEmailVerified"
        >
          확인
        </button>
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
  gap: 8px;
  width: 100%;
}

input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  min-width: 0;
}

input:focus {
  outline: none;
  border-color: #4a90e2;
}

.verify-btn {
  padding: 8px 16px;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  min-width: 80px;
}

.verify-btn:hover {
  background-color: #357abd;
}

.verify-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
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

@media screen and (max-width: 375px) {
  .input-group {
    gap: 4px;
  }

  input {
    padding: 8px;
    font-size: 0.9rem;
  }

  .verify-btn {
    padding: 8px;
    min-width: 60px;
    font-size: 0.9rem;
  }
}
</style>
