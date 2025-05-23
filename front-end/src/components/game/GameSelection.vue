<template>
  <div class="game-selection">
    <h3>게임 선택</h3>
    <button @click="$emit('selectGame', 'FastClick')">FastClick</button>
    <button @click="$emit('selectGame', 'OtherGame')">다른 게임</button>
  </div>
</template>

<script setup lang="ts">
// 그냥 이벤트 emit만 하면 됨
import { defineEmits } from 'vue'

defineEmits(['selectGame'])
</script>

<style scoped>
.game-selection {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
