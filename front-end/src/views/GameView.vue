<template>
  <div class="game-view">
    <div class="game-container">
      <component
        :is="currentGameComponent"
        @selectGame="selectGame"
        @goBackToSelect="goBack"
        :gameType="selectedGame"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import GameSelection from '@/components/game/GameSelection.vue'
import FastClick from '@/components/game/origin/FastClick.vue'
import Roulette from '@/components/game/origin/Roulette.vue'
import CoinToss from '@/components/game/origin/CoinToss.vue'
import Crocodilia from '@/components/game/origin/Crocodilia.vue'
import { Game } from '@/components/game/Game'

const router = useRouter()
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

const selectGame = (gameName: string) => {
  selectedGame.value = gameName
}

const goBack = () => {
  selectedGame.value = null
}
</script>

<style scoped>
.game-view {
  position: relative;
  width: 100%;
  height: 80vh;
  min-height: 100%;
  background-color: #fff;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.game-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  justify-content: center;
}

/* 게임 컴포넌트가 컨테이너를 꽉 채우도록 함 */
.game-container > * {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  justify-content: center;
}
</style>
