<template>
  <a-result status="success" title="회원가입 성공!" sub-title="로그인 하고 서비스를 즐겨보세요!">
    <template #extra>
      <a-button @click="goToWelcome">메인 페이지로</a-button>
      <a-button type="primary" @click="goToLogin">로그인</a-button>
    </template>
  </a-result>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToWelcome = () => {
  router.push('/')
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped></style>
