<template>
  <div class="login-container">
    <div class="login-wrapper">
      <LoginForm />
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from '@/components/user/LoginForm.vue'
</script>

<style scoped>
.login-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 400px;
}

@media screen and (max-width: 768px) {
  .login-container {
    padding: 10px;
  }
}

@media screen and (max-width: 375px) {
  .login-container {
    padding: 5px;
  }
}
</style>
