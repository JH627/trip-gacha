<template>
  <ReuseableModal
    v-if="isOpen"
    :visible="true"
    :modalTitle="selectedGame === '' || selectedGame === null ? '게임 선택' : selectedGame"
    @close="handleClose"
    @close-modal="handleClose"
    @players-select-complete="playerSelectComplete"
  >
    <component
      :is="props.isSocket ? socketGameComponent : currentGameComponent"
      @selectGame="selectGame"
      @goBackToSelect="goBack"
      :planId="planId"
      :gameType="selectedGame"
    />
  </ReuseableModal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ReuseableModal from '@/components/ReuseableModal.vue'
import GameSelection from '@/components/game/GameSelection.vue'
import FastClick from '@/components/game/origin/FastClick.vue'
import Roulette from '@/components/game/origin/Roulette.vue'
import CoinToss from '@/components/game/origin/CoinToss.vue'
import Crocodilia from '@/components/game/origin/Crocodilia.vue'
import { Game } from './Game'
import SelectPlayers from './socket/SelectPlayers.vue'
import SocketFastClick from './socket/SocketFastClick.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const planId = route.params.planId

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
    case Game.FAST_CLICK:
      return FastClick
    case Game.ROULETTE:
      return Roulette
    case Game.COIN_TOSS:
      return CoinToss
    case Game.CROCODILIA:
      return Crocodilia
    default:
      return GameSelection
  }
})

const hasSelectedPlayers = ref(false)

const socketGameComponent = computed(() => {
  if (!selectedGame.value) return GameSelection

  if (!hasSelectedPlayers.value) return SelectPlayers

  switch (selectedGame.value) {
    case Game.FAST_CLICK:
      return SocketFastClick
    case Game.ROULETTE:
      return Roulette
    case Game.COIN_TOSS:
      return CoinToss
    case Game.CROCODILIA:
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

const playerSelectComplete = () => {
  hasSelectedPlayers.value = true
}
</script>
