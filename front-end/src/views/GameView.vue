<template>
  <div>
    <button @click="show = true">게임 모달 열기</button>
    <GameModal :is-open="show" @close="show = false" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import GameModal from '@/components/game/GameModal.vue'

const show = ref(false)
</script>
