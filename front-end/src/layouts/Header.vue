<script setup lang="ts">
import { useRouter } from 'vue-router'
import logoImage from '@/assets/logo.jpg'

const router = useRouter()
</script>

<template>
  <div class="header-content">
    <div class="header-main-content">
      <img :src="logoImage" alt="로고" class="logo" @click="router.push('/')" />
      <a-button @click="router.push('/login')" class="login-btn">로그인</a-button>
    </div>
  </div>
</template>

<style scoped>
.header-content {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
  width: 100%;
}

.header-main-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1500px;
  padding: 0 10px;
}

.logo {
  height: 70px;
  cursor: pointer;
  object-fit: contain;
}

.login-btn {
  color: white;
  background-color: black;
}

@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }

  .header-main-content {
    min-width: auto;
    padding: 0;
  }

  .logo {
    height: 50px;
  }
}
</style>
