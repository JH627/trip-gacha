<template>
  <div class="regist-container">
    <RegistStatusBar :current-step="currentStep" />
    <div class="regist-body">
      <div class="regist-content">
        <RegistEmailForm v-if="currentStep === 'email'" @next="handleNext" />
        <RegistInfoForm v-else-if="currentStep === 'info'" @next="handleNext" />
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

const currentStep = ref<'email' | 'info' | 'result'>('email')

const handleNext = () => {
  switch (currentStep.value) {
    case 'email':
      currentStep.value = 'info'
      break
    case 'info':
      currentStep.value = 'result'
      break
  }
}
</script>

<style scoped>
.regist-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.regist-body {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 1rem;
}

.regist-content {
  width: 100%;
  max-width: 400px;
}
</style>
