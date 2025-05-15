<template>
  <div class="login-container">
    <LoginForm />
  </div>
</template>

<script setup lang="ts">
import LoginForm from '@/components/user/LoginForm.vue'
</script>

<style scoped>
.login-container {
  width: 100%;
  max-width: 400px;
}
</style>
