<template>
  <div>
    <button @click="open">게임 모달 열기</button>
    <GameModal
      :is-open="show"
      @close="close"
      :is-socket="false"
      :invited-game-type="invitedGameType"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import GameModal from '@/components/game/GameModal.vue'
import { Game } from '@/components/game/Game'

const show = ref(false)
const invitedGameType = ref(Game.DEFAULT)

const open = () => {
  show.value = true
}
const close = () => {
  show.value = false
}

console.log('왜안댐?')
</script>
