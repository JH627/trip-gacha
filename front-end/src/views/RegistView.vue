<template>
  <div class="regist-container">
    <div class="regist-body">
      <RegistStatusBar :current-step="currentStep" />
      <div class="regist-content">
        <RegistEmailForm v-if="currentStep === 'email'" @next="handleEmailStep" />
        <RegistInfoForm v-else-if="currentStep === 'info'" @next="handleInfoStep" />
        <RegistResultForm v-else-if="currentStep === 'result'" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import RegistStatusBar from '@/components/user/RegistStatusBar.vue'
import RegistEmailForm from '@/components/user/RegistEmailForm.vue'
import RegistInfoForm from '@/components/user/RegistInfoForm.vue'
import RegistResultForm from '@/components/user/RegistResultForm.vue'
import { defaultApi } from '@/api/axios'

const currentStep = ref<'email' | 'info' | 'result'>('email')

// 회원가입 입력 폼
const registrationData = ref({
  email: '',
  password: '',
  nickname: '',
  profileImg: null as File | null,
})

// 이메일 인증 성공 시 전역 변수에 이메일 저장 후 다음 스텝
const handleEmailStep = (email: string) => {
  registrationData.value.email = email
  currentStep.value = 'info'
}

// 개인정보 모두 입력 시 서버에 회원가입 요청 전송
const handleInfoStep = async (data: {
  password: string
  nickname: string
  profileImg: File | null
}) => {
  registrationData.value = {
    ...registrationData.value,
    ...data,
  }

  try {
    const formData = new FormData()
    formData.append('email', registrationData.value.email)
    formData.append('password', registrationData.value.password)
    formData.append('nickname', registrationData.value.nickname)
    // 이미지가 없다면 null 전송
    if (registrationData.value.profileImg) {
      formData.append('profileImg', registrationData.value.profileImg)
    }

    await defaultApi.post('/user/regist', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    // 결과 화면으로 이동
    currentStep.value = 'result'
  } catch (error) {
    console.error('Registration failed:', error)
  }
}
</script>

<style scoped>
.regist-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.regist-body {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.regist-content {
  width: 100%;
}

@media screen and (max-width: 768px) {
  .regist-container {
    padding: 10px;
  }

  .regist-body {
    padding: 20px;
    gap: 20px;
  }
}

@media screen and (max-width: 375px) {
  .regist-container {
    padding: 5px;
  }

  .regist-body {
    padding: 15px;
    gap: 15px;
  }
}
</style>
