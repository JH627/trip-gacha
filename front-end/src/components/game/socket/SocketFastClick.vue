<template>
  <div class="game-container" :class="getBackgroundClass()" @click="handleClick">
    <!-- 대기 화면 (초대받은 사람용) -->
    <div v-if="currentProgress === 'waiting'" class="waiting-screen">
      <h1 class="title">반응 속도 게임</h1>
      <div v-if="!props.isOwner" class="waiting-message">
        <p class="description">방장이 게임을 시작하기를 기다리는 중...</p>
        <div class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
      <div v-else class="creator-controls">
        <p class="description">화면이 빨간색으로 바뀌면<br />가능한 빨리 클릭하세요!</p>
        <button @click="handleStartGame" class="start-button">게임 시작하기</button>
      </div>
    </div>
    <!-- 게임 진행 중 -->
    <div v-if="currentProgress === 'playing'">
      <!-- 카운트다운 -->
      <div v-if="gameState === 'countdown'" class="countdown-text">
        {{ countdown }}
      </div>

      <!-- 준비 -->
      <div v-if="gameState === 'waiting'" class="wait-text">준비하세요...</div>

      <!-- 클릭 시작 -->
      <div v-if="gameState === 'active'" class="click-text">지금 클릭하세요!</div>

      <!-- 결과 전송 완료 -->
      <div v-if="gameState === 'finished'" class="finished-text">
        결과 전송 완료!<br />
        다른 플레이어를 기다리는 중...
      </div>
    </div>

    <!-- 최종 결과 화면 (RESULT) -->
    <div v-if="currentProgress === 'result'" class="modal-overlay">
      <div class="modal-content">
        <h2 class="result-title">게임 결과</h2>

        <div class="reaction-time">
          <p>
            나의 반응 속도:
            <span class="bold" :class="getTimeClass()">{{ getMyTimeText() }}</span>
          </p>
        </div>

        <div class="ranking">
          <h3 class="ranking-title">순위</h3>
          <div class="ranking-list">
            <div v-for="(player, index) in gameResult" :key="player.userId" class="ranking-item">
              <div class="rank-info">
                <span class="rank">{{ index + 1 }}위</span>
                <img :src="player.img" :alt="player.nickname" class="player-avatar" />
                <span class="player-name">{{ player.nickname }}</span>
              </div>
              <span class="player-time" :class="getPlayerTimeClass(player.clickTime)">
                {{ getPlayerTimeText(player.clickTime) }}
              </span>
            </div>
          </div>
        </div>

        <div class="button-group">
          <button @click="emit('close')" class="back-button">종료하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { GameProgress } from '@/components/game/Game'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRoute } from 'vue-router'

interface GameUserInfo {
  userId: string
  nickname: string
  img: string
  clickTime: number
}

interface GameResponse {
  data: GameUserInfo[] | null
  progress: GameProgress
}

// Props에서 isGameCreator 제거
const props = defineProps<{
  gameType: string
  isOwner: boolean // 방장 여부
}>()

const emit = defineEmits(['close'])

// 상태 관리
const route = useRoute()
const planId = computed(() => route.params.planId as string)
const socketStore = useSocketStore()
const authStore = useAuthStore()

// 게임 상태를 단순화
const currentProgress = ref<'waiting' | 'intro' | 'playing' | 'result'>('waiting')
const introCountdown = ref(3)

// 게임 상태
const gameState = ref<'countdown' | 'waiting' | 'active' | 'finished'>('countdown')
const countdown = ref(3)
const startTime = ref(0)
const myClickTime = ref(999)
const gameResult = ref<GameUserInfo[]>([])
const gameId = ref('')
const gameStarted = ref(false) // 게임 시작 여부 추가

// 타이머
let introTimer: number | null = null
let countdownTimer: number | null = null
let colorChangeTimer: number | null = null

// 배경색 클래스 계산
const getBackgroundClass = () => {
  if (currentProgress.value === 'playing') {
    return gameState.value === 'active' ? 'bg-red' : 'bg-green'
  }
  return 'bg-green'
}

// 내 시간 텍스트
const getMyTimeText = () => {
  if (myClickTime.value === 999) return '시간 초과'
  if (myClickTime.value === -1) return '너무 빨리 클릭!'
  return `${myClickTime.value}ms`
}

// 내 시간 클래스
const getTimeClass = () => {
  if (myClickTime.value === 999 || myClickTime.value === -1) return 'time-penalty'
  if (myClickTime.value < 200) return 'time-excellent'
  if (myClickTime.value < 300) return 'time-good'
  return 'time-normal'
}

// 플레이어 시간 텍스트
const getPlayerTimeText = (time: number) => {
  if (time === 999) return '시간 초과'
  if (time === -1) return '너무 빨리 클릭!'
  return `${time}ms`
}

// 플레이어 시간 클래스
const getPlayerTimeClass = (time: number) => {
  if (time === 999 || time === -1) return 'time-penalty'
  if (time < 200) return 'time-excellent'
  if (time < 300) return 'time-good'
  return 'time-normal'
}

// 모든 타이머 정리
const clearAllTimers = () => {
  if (introTimer) {
    clearInterval(introTimer)
    introTimer = null
  }
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  if (colorChangeTimer) {
    clearTimeout(colorChangeTimer)
    colorChangeTimer = null
  }
}

const processGameOperation = (body: string) => {
  try {
    const response: GameResponse = JSON.parse(body)
    console.log('게임 응답:', response)

    switch (response.progress) {
      case GameProgress.WAIT:
        // 대기 중인 플레이어 수 업데이트
        if (response.data) {
          // 첫 번째로 접속한 사람이 방장
          //isFirstPlayer.value = response.data[0]?.userId === authStore.userId
        }
        break
      case GameProgress.PLAY_GAME:
        // 게임이 이미 시작되었다면 중복 시작 방지
        if (!gameStarted.value) {
          gameStarted.value = true
          currentProgress.value = 'playing'
          startLocalGame()
        }
        break
      case GameProgress.RESULT:
        // 모든 타이머 정리하고 결과 화면으로
        clearAllTimers()
        gameStarted.value = false
        currentProgress.value = 'result'
        if (response.data) {
          gameResult.value = response.data
        }
        break
    }
  } catch (error) {
    console.error('게임 메시지 파싱 오류:', error)
  }
}

// 게임 시작 (자동 호출)
const startGame = () => {
  socketStore.send(`/app/game/fast-click/start/${gameId.value}`, authStore.accessToken || '', null)
}

// 로컬 게임 시작
const startLocalGame = () => {
  // 이미 진행 중인 타이머들 정리
  clearAllTimers()

  gameState.value = 'countdown'
  countdown.value = 3

  // 카운트다운 시작
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  countdownTimer = window.setInterval(() => {
    countdown.value--

    if (countdown.value <= 0) {
      clearInterval(countdownTimer!)
      countdownTimer = null
      gameState.value = 'waiting'

      // 랜덤 딜레이 후 빨간색으로 변경 (1-4초)
      const randomDelay = Math.floor(Math.random() * 3000) + 1000

      colorChangeTimer = window.setTimeout(() => {
        if (gameStarted.value) {
          // 게임이 여전히 진행 중인지 확인
          gameState.value = 'active'
          startTime.value = Date.now()
        }
      }, randomDelay)
    }
  }, 1000)
}

// 클릭 처리
const handleClick = () => {
  if (currentProgress.value !== 'playing' || !gameStarted.value) return

  if (gameState.value === 'active') {
    // 정상 클릭
    const clickTime = Date.now()
    myClickTime.value = clickTime - startTime.value
    gameState.value = 'finished'
    sendResult()
  } else if (gameState.value === 'waiting') {
    // 너무 빨리 클릭
    if (colorChangeTimer) {
      clearTimeout(colorChangeTimer)
      colorChangeTimer = null
    }
    myClickTime.value = -1
    gameState.value = 'finished'
    sendResult()
  }
}

// 결과 전송
const sendResult = () => {
  socketStore.send(
    `/app/game/fast-click/update/${gameId.value}`,
    authStore.accessToken || '',
    myClickTime.value,
  )
}

const handleStartGame = () => {
  if (!props.isOwner) return

  currentProgress.value = 'playing'
  startGame()
}

// 컴포넌트 마운트 시 자동 시작
onMounted(() => {
  gameId.value = planId.value
  socketStore.subscribe(`/topic/game/fast-click/${gameId.value}`, processGameOperation)

  // 모든 사용자가 대기 상태로 시작
  currentProgress.value = 'waiting'
})

// 컴포넌트 언마운트 시 정리
onBeforeUnmount(() => {
  clearAllTimers()
  gameStarted.value = false
})
</script>

<style scoped>
.game-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

.bg-green {
  background-color: #22c55e;
}

.bg-red {
  background-color: #ef4444;
}

.intro-screen {
  color: white;
  text-align: center;
  max-width: 28rem;
}

.description {
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.auto-start-timer {
  font-size: 1.5rem;
  font-weight: bold;
  color: #fbbf24;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.countdown-text {
  color: white;
  font-size: 9rem;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.wait-text,
.click-text,
.finished-text {
  color: white;
  font-size: 2.25rem;
  font-weight: bold;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
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
  max-width: 32rem;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.result-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-align: center;
}

.reaction-time {
  text-align: center;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 0.5rem;
}

.reaction-time p {
  font-size: 1.125rem;
  margin: 0;
}

.bold {
  font-weight: bold;
}

.time-excellent {
  color: #22c55e;
}

.time-good {
  color: #3b82f6;
}

.time-normal {
  color: #6b7280;
}

.time-penalty {
  color: #ef4444;
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
  padding: 0.75rem 0;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ranking-item:last-child {
  border-bottom: none;
}

.rank-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.rank {
  font-weight: bold;
  min-width: 30px;
}

.player-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.player-name {
  font-weight: 500;
}

.player-time {
  font-weight: bold;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.back-button {
  flex: 1;
  padding: 0.75rem;
  background-color: #6b7280;
  color: white;
  border-radius: 0.5rem;
  font-weight: bold;
  transition: background-color 0.3s ease;
  border: none;
  cursor: pointer;
}

.back-button:hover {
  background-color: #4b5563;
}

/* 반응형 */
@media (max-width: 768px) {
  .title {
    font-size: 2rem;
  }

  .countdown-text {
    font-size: 6rem;
  }

  .wait-text,
  .click-text,
  .finished-text {
    font-size: 1.5rem;
  }

  .modal-content {
    padding: 1.5rem;
    width: 95%;
  }

  .rank-info {
    gap: 0.5rem;
  }

  .player-avatar {
    width: 24px;
    height: 24px;
  }
}

.waiting-screen {
  color: white;
  text-align: center;
  max-width: 28rem;
}

.waiting-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.creator-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.start-button {
  padding: 1rem 2rem;
  font-size: 1.2rem;
  font-weight: bold;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.start-button:hover {
  background-color: #2563eb;
}

.loading-dots {
  display: flex;
  gap: 0.5rem;
}

.loading-dots span {
  width: 12px;
  height: 12px;
  background-color: #fbbf24;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}
.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>
