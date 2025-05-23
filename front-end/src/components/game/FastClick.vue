<template>
  <div
    class="game-container"
    :class="
      gameState === 'initial' || gameState === 'countdown' || gameState === 'waiting'
        ? 'bg-green'
        : 'bg-red'
    "
    @click="handleClick"
  >
    <!-- 안내 -->
    <div v-if="gameState === 'initial'" class="initial-screen">
      <h1 class="title">반응 속도 게임</h1>
      <p>화면이 빨간색으로 바뀌면 가능한 빨리 클릭하세요!</p>
      <button @click="startGame" class="start-button">시작하기</button>
    </div>

    <!-- 카운트 다운 -->
    <div v-if="gameState === 'countdown'" class="countdown-text">
      {{ countdown }}
    </div>

    <!-- 준비 -->
    <div v-if="gameState === 'waiting'" class="wait-text">준비하세요...</div>

    <!-- 클릭 시작 -->
    <div v-if="gameState === 'active'" class="click-text">지금 클릭하세요!</div>

    <!-- 결과 -->
    <div v-if="gameState === 'finished'" class="modal-overlay">
      <div class="modal-content">
        <h2 class="result-title">결과</h2>

        <div class="reaction-time">
          <p>
            <span class="bold">{{ nickname }}</span
            >님의 반응 속도:
            <span class="bold">{{ userReactionTime }}ms</span>
          </p>
        </div>

        <div class="ranking">
          <h3 class="ranking-title">순위</h3>
          <div class="ranking-list">
            <div
              v-for="(player, index) in sortedPlayers"
              :key="player.name"
              class="ranking-item"
              :class="player.name === nickname ? 'highlight' : ''"
            >
              <span>{{ index + 1 }}. {{ player.name }}</span>
              <span>{{ player.time }}ms</span>
            </div>
          </div>
        </div>

        <div class="button-group">
          <button @click="resetGame" class="restart-button">다시 시작하기</button>
          <button @click="emit('goBackToSelect')" class="back-button">
            게임 선택으로 돌아가기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from 'vue'
import ReuseableModal from '@/components/ReuseableModal.vue'

// Types
interface Player {
  name: string
  time: number
}

// Game state
const gameState = ref<'initial' | 'countdown' | 'waiting' | 'active' | 'finished'>('initial')
const countdown = ref(3)
const startTime = ref(0)
const userReactionTime = ref(0)
const nickname = ref('플레이어')
const mockPlayers = ref<Player[]>([
  { name: '김철수', time: 245 },
  { name: '이영희', time: 312 },
  { name: '박지민', time: 189 },
  { name: '최수진', time: 278 },
  { name: '정민준', time: 356 },
])
const players = ref<Player[]>([])

// Computed properties
const sortedPlayers = computed(() => {
  return [...players.value].sort((a, b) => a.time - b.time)
})

// Timers
let countdownTimer: number | null = null
let colorChangeTimer: number | null = null

// Start the game
const startGame = () => {
  // Generate a random nickname if not set
  if (nickname.value === '플레이어') {
    nickname.value = `플레이어${Math.floor(Math.random() * 1000)}`
  }

  gameState.value = 'countdown'
  countdown.value = 3

  // Start countdown
  countdownTimer = window.setInterval(() => {
    countdown.value--

    if (countdown.value <= 0) {
      clearInterval(countdownTimer!)
      gameState.value = 'waiting'

      // Random delay before turning red (0-3000ms)
      const randomDelay = Math.floor(Math.random() * 3000)

      colorChangeTimer = window.setTimeout(() => {
        gameState.value = 'active'
        startTime.value = Date.now()
      }, randomDelay)
    }
  }, 1000)
}

// Handle click
const handleClick = () => {
  // Only register clicks when the game is active
  if (gameState.value === 'active') {
    const clickTime = Date.now()
    userReactionTime.value = clickTime - startTime.value

    // Create players list with mock players and user
    players.value = [...mockPlayers.value, { name: nickname.value, time: userReactionTime.value }]

    gameState.value = 'finished'
  } else if (gameState.value === 'waiting') {
    // If clicked too early
    clearTimeout(colorChangeTimer!)
    userReactionTime.value = -1 // Indicate early click

    // Create players list with mock players and user (with penalty)
    players.value = [
      ...mockPlayers.value,
      { name: nickname.value, time: 9999 }, // Penalty for clicking too early
    ]

    gameState.value = 'finished'
  }
}

// Reset the game
const resetGame = () => {
  gameState.value = 'initial'
  userReactionTime.value = 0
  startTime.value = 0

  // Clear any existing timers
  if (countdownTimer) clearInterval(countdownTimer)
  if (colorChangeTimer) clearTimeout(colorChangeTimer)
}

// Clean up timers when component is unmounted
onBeforeUnmount(() => {
  if (countdownTimer) clearInterval(countdownTimer)
  if (colorChangeTimer) clearTimeout(colorChangeTimer)
})

const emit = defineEmits(['close-modal', 'goBackToSelect'])
const isOpen = ref<boolean>(true)
const open = () => {
  isOpen.value = true
}
const close = () => {
  isOpen.value = false
}
</script>

<style scoped>
.game-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}
.bg-green {
  background-color: #22c55e;
}
.bg-red {
  background-color: #ef4444;
}
.countdown-text {
  color: white;
  font-size: 9rem;
  font-weight: bold;
}
.initial-screen {
  color: white;
  text-align: center;
  max-width: 28rem;
}
.title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}
.start-button {
  margin-top: 1.5rem;
  padding: 0.75rem 1.5rem;
  background-color: white;
  color: #16a34a;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 1.25rem;
  transition: background-color 0.3s ease;
}
.start-button:hover {
  background-color: #f3f4f6;
}
.wait-text,
.click-text {
  color: white;
  font-size: 2.25rem;
  font-weight: bold;
}
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}
.modal-content {
  background-color: white;
  border-radius: 0.5rem;
  padding: 2rem;
  max-width: 28rem;
  width: 100%;
}
.result-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}
.reaction-time p {
  font-size: 1.125rem;
}
.bold {
  font-weight: bold;
}
.ranking {
  margin-bottom: 1.5rem;
}
.ranking-title {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}
.ranking-list {
  background-color: #f3f4f6;
  border-radius: 0.5rem;
  padding: 1rem;
}
.ranking-item {
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
}
.ranking-item:last-child {
  border-bottom: none;
}
.highlight {
  font-weight: bold;
  background-color: #fef9c3;
  padding: 0 0.5rem;
  margin: 0 -0.5rem;
  border-radius: 0.25rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.restart-button {
  flex: 1;
  padding: 0.75rem;
  background-color: #22c55e;
  color: white;
  border-radius: 0.5rem;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.restart-button:hover {
  background-color: #16a34a;
}

.back-button {
  flex: 1;
  padding: 0.75rem;
  background-color: #e5e7eb;
  color: #111827;
  border-radius: 0.5rem;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #d1d5db;
}
</style>
