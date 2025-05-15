<template>
  <div class="regist-form">
    <h1 class="form-title">회원가입</h1>
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

    <div class="form-group">
      <label for="passwordConfirm">비밀번호 확인</label>
      <input
        type="password"
        id="passwordConfirm"
        v-model="passwordConfirm"
        placeholder="비밀번호를 다시 입력하세요"
        required
      />
    </div>

    <div class="form-group">
      <label for="nickname">닉네임</label>
      <input
        type="text"
        id="nickname"
        v-model="nickname"
        placeholder="닉네임을 입력하세요"
        required
      />
    </div>

    <div class="button-group">
      <button type="button" class="next-btn" @click="handleNext" :disabled="!isFormValid">
        다음
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const emit = defineEmits<{
  (e: 'next'): void
}>()

const password = ref('')
const passwordConfirm = ref('')
const nickname = ref('')

const isFormValid = computed(() => {
  return (
    password.value &&
    passwordConfirm.value &&
    nickname.value &&
    password.value === passwordConfirm.value
  )
})

const handleNext = () => {
  if (isFormValid.value) {
    emit('next')
  }
}
</script>

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

input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: #4a90e2;
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
