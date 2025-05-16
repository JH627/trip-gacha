<template>
  <div class="status-bar">
    <!-- 데스크탑용 -->
    <div class="desktop-only">
      <a-steps :current="currentStepIndex">
        <a-step title="이메일 인증" :icon="h(UserOutlined)" />
        <a-step title="개인정보" :icon="h(SolutionOutlined)" />
        <a-step title="완료" :icon="h(SmileOutlined)" />
      </a-steps>
    </div>

    <!-- 모바일용 -->
    <div class="mobile-only">
      <a-progress :percent="progressPercent" status="active" :show-info="false" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { h, computed } from 'vue'
import { UserOutlined, SolutionOutlined, SmileOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  currentStep: 'email' | 'info' | 'result'
}>()

const currentStepIndex = computed(() => {
  switch (props.currentStep) {
    case 'email':
      return 0
    case 'info':
      return 1
    case 'result':
      return 2
    default:
      return 0
  }
})

const progressPercent = computed(() => {
  switch (props.currentStep) {
    case 'email':
      return 33
    case 'info':
      return 66
    case 'result':
      return 100
    default:
      return 0
  }
})
</script>

<style scoped>
.desktop-only {
  display: block;
  max-width: 800px;
  margin: 0 auto;
}

.mobile-only {
  display: none;
}

@media screen and (max-width: 768px) {
  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: block;
  }
}
</style>
