<template>
  <ReuseableModal
    v-if="isOpen"
    :visible="true"
    :modalTitle="selectedGame === '' || selectedGame === null ? '게임 선택' : selectedGame"
    @close="handleClose"
    @close-modal="handleClose"
  >
    <component
      v-if="isSocket === false"
      :is="currentGameComponent"
      @selectGame="selectGame"
      @goBackToSelect="goBack"
    />
  </ReuseableModal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ReuseableModal from '@/components/ReuseableModal.vue'
import GameSelection from '@/components/game/GameSelection.vue'
import FastClick from '@/components/game/FastClick.vue'
import Roulette from '@/components/game/Roulette.vue'
import CoinToss from '@/components/game/CoinToss.vue'
import Crocodilia from '@/components/game/Crocodilia.vue'

const props = defineProps<{
  isOpen: boolean
  isSocket: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const selectedGame = ref<string | null>(null)

const currentGameComponent = computed(() => {
  if (!selectedGame.value) return GameSelection
  switch (selectedGame.value) {
    case 'FastClick':
      return FastClick
    case 'Roulette':
      return Roulette
    case 'CoinToss':
      return CoinToss
    case 'Crocodilia':
      return Crocodilia
    default:
      return GameSelection
  }
})

const currentSocketGameComponent = computed(() => {
  if (!selectedGame.value) return GameSelection
  switch (selectedGame.value) {
    case 'FastClick':
      return FastClick
    case 'Roulette':
      return Roulette
    case 'CoinToss':
      return CoinToss
    case 'Crocodilia':
      return Crocodilia
    default:
      return GameSelection
  }
})

const selectGame = (gameName: string) => {
  selectedGame.value = gameName
}

const goBack = () => {
  selectedGame.value = null
}

const handleClose = () => {
  emit('close')
  selectedGame.value = null // 초기화
}
</script>
