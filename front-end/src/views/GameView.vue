<template>
  <div>
    <button @click="open">모달 열기</button>
    <ReuseableModal
      v-if="isOpen"
      :visible="true"
      modalTitle="게임 선택"
      @close-modal="close"
      @close="close"
    >
      <component
        :is="currentGameComponent"
        @selectGame="handleSelectGame"
        @goBackToSelect="goBackToGameSelection"
      />
    </ReuseableModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ReuseableModal from '@/components/ReuseableModal.vue'
import GameSelection from '@/components/game/GameSelection.vue'
import FastClick from '@/components/game/FastClick.vue'
import Roulette from '@/components/game/Roulette.vue'
import CoinToss from '@/components/game/CoinToss.vue'
import Crocodilia from '@/components/game/Crocodilia.vue'
//import OtherGame from '@/components/game/OtherGame.vue' // 예시

const isOpen = ref(false)
const selectedGame = ref<string | null>(null)

const goBackToGameSelection = () => {
  selectedGame.value = null
}
// 게임 선택 UI 보여줄지, 아니면 선택한 게임 컴포넌트 보여줄지 결정
const currentGameComponent = computed(() => {
  if (!selectedGame.value) {
    return GameSelection
  }
  if (selectedGame.value === 'FastClick') {
    return FastClick
  }
  if (selectedGame.value === 'Roulette') {
    return Roulette
  }
  if (selectedGame.value === 'CoinToss') {
    return CoinToss
  }
  if (selectedGame.value === 'Crocodilia') {
    return Crocodilia
  }

  //   if (selectedGame.value === 'OtherGame') {
  //     return OtherGame
  //   }
  return GameSelection // 기본
})

const handleSelectGame = (gameName: string) => {
  selectedGame.value = gameName
}

const close = () => {
  isOpen.value = false
  selectedGame.value = null // 모달 닫히면 다시 초기화
}

const open = () => {
  isOpen.value = true // 모달 닫히면 다시 초기화
}
</script>
