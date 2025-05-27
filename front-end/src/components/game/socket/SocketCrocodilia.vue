<template>
  <audio ref="bgm" loop preload="auto">
    <source src="@/assets/music/gameBGM.mp3" type="audio/mpeg" />
  </audio>
  <div class="crocodile-game">
    <h1 class="game-title">악어 이빨 게임</h1>

    <!-- 게임 대기 화면 -->
    <div v-if="gameProgress === 'waiting'" class="waiting-screen">
      <div v-if="!props.isOwner" class="waiting-message">
        <p class="description">방장이 게임을 시작하기를 기다리는 중...</p>
        <div class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
      <div v-else class="creator-controls">
        <p class="description">모든 플레이어가 준비되면 게임을 시작하세요!</p>
        <button @click="startGame" class="start-button">게임 시작하기</button>
      </div>
    </div>

    <!-- 게임 진행 화면 -->
    <div v-if="gameProgress === 'playing'" class="game-container">
      <!-- 플레이어 정보 및 턴 표시 -->
      <div class="players-info">
        <div class="players-list">
          <div
            v-for="(player, index) in players"
            :key="player.userId"
            class="player-item"
            :class="{
              current: currentTurnIndex === index,
              eliminated: player.eliminated,
            }"
          >
            <img :src="player.img" :alt="player.nickname" class="player-avatar-small" />
            <span class="player-name-small">{{ player.nickname }}</span>
            <span v-if="player.eliminated" class="eliminated-badge">탈락</span>
          </div>
        </div>
      </div>

      <!-- 악어 머리 -->
      <div class="crocodile-head" :class="{ shake: isShaking }">
        <!-- 악어 윗부분 -->
        <div class="crocodile-upper">
          <div class="nose-container">
            <div class="nose"></div>
          </div>
          <div class="eyes-container">
            <div class="eye"></div>
            <div class="eye"></div>
          </div>
        </div>

        <!-- 악어 입 -->
        <div class="crocodile-mouth" :class="{ 'mouth-closed': isMouthClosed }">
          <!-- 윗니 -->
          <div class="upper-teeth">
            <div v-for="i in 4" :key="`upper-${i}`" class="tooth upper-tooth"></div>
          </div>

          <!-- 아랫니 -->
          <div class="lower-teeth" :class="{ 'teeth-hidden': isMouthClosed }">
            <div v-for="(tooth, index) in teeth" :key="`lower-${index}`" class="tooth-container">
              <button
                @click="pressTooth(index)"
                class="tooth lower-tooth"
                :class="{
                  'tooth-pressed': tooth.pressed,
                  'tooth-danger': gameState === 'failed' && index === trapToothIndex,
                }"
                :disabled="!isMyTurn || tooth.pressed || gameState === 'failed'"
              ></button>
              <div v-if="isPressing === index" class="finger"></div>
            </div>
          </div>

          <!-- 악어 혀 -->
          <div class="tongue"></div>
        </div>
      </div>

      <!-- 턴 안내 메시지 -->
      <div class="turn-message">
        <p v-if="isMyTurn" class="my-turn">당신의 차례입니다! 이빨을 선택하세요.</p>
        <p v-else class="waiting-turn">{{ currentPlayer?.nickname }}님의 차례를 기다리는 중...</p>
      </div>
    </div>

    <!-- 게임 결과 화면 -->
    <div v-if="gameProgress === 'result'" class="result-screen">
      <div class="game-message">
        <h2 class="result-title">게임 결과</h2>

        <div v-if="gameResult" class="result-content">
          <div class="loser-info">
            <h3>패배자</h3>
            <div class="player-card loser">
              <img
                :src="gameResult.loser.img"
                :alt="gameResult.loser.nickname"
                class="player-avatar"
              />
              <span class="player-name">{{ gameResult.loser.nickname }}</span>
              <span class="loser-badge">악어에게 물렸습니다!</span>
            </div>
          </div>

          <div class="survivors-info">
            <h3>생존자</h3>
            <div class="survivors-list">
              <div
                v-for="survivor in gameResult.survivors"
                :key="survivor.userId"
                class="player-card survivor"
              >
                <img :src="survivor.img" :alt="survivor.nickname" class="player-avatar" />
                <span class="player-name">{{ survivor.nickname }}</span>
              </div>
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

// 상태 관리
const route = useRoute()
const planId = computed(() => route.params.planId as string)
const socketStore = useSocketStore()
const authStore = useAuthStore()
const userId = ref('')

interface GamePlayer {
  userId: string
  nickname: string
  img: string
  eliminated: boolean
}

interface GameResponse {
  data: any
  progress: GameProgress
}

interface CrocodileGameData {
  players: GamePlayer[]
  currentTurnIndex: number
  teeth: { pressed: boolean }[]
  trapToothIndex: number
  gameState: 'playing' | 'failed'
}

interface GameResult {
  loser: GamePlayer
  survivors: GamePlayer[]
}

const props = defineProps<{
  gameType: string
  isOwner: boolean
}>()

const emit = defineEmits(['close'])

// 게임 상태
const gameProgress = ref<'waiting' | 'playing' | 'result'>('waiting')
const players = ref<GamePlayer[]>([])
const currentTurnIndex = ref(0)
const teeth = ref<{ pressed: boolean }[]>([])
const trapToothIndex = ref(-1)
const gameState = ref<'playing' | 'failed'>('playing')
const gameResult = ref<GameResult | null>(null)
const gameId = ref('')

// UI 상태
const isMouthClosed = ref(false)
const isShaking = ref(false)
const isPressing = ref(-1)
const bgm = ref<HTMLAudioElement | null>(null)

// 현재 플레이어 계산
const currentPlayer = computed(() => {
  if (players.value.length === 0) return null
  return players.value[currentTurnIndex.value]
})

// 내 턴인지 확인
const isMyTurn = computed(() => {
  console.log(currentPlayer.value?.userId + ' ' + userId.value)
  console.log(currentPlayer.value?.userId == userId.value)
  console.log(gameState.value === 'playing')

  return currentPlayer.value?.userId == userId.value && gameState.value === 'playing'
})

// 소켓 메시지 처리
const processGameOperation = (body: string) => {
  try {
    const response: GameResponse = JSON.parse(body)
    console.log('악어게임 응답:', response)

    switch (response.progress) {
      case GameProgress.WAIT:
        gameProgress.value = 'waiting'
        if (response.data?.players) {
          players.value = response.data.players
        }
        break

      case GameProgress.PLAY_GAME:
        gameProgress.value = 'playing'
        if (response.data) {
          const gameData: CrocodileGameData = response.data
          players.value = gameData.players
          currentTurnIndex.value = gameData.currentTurnIndex
          teeth.value = gameData.teeth
          trapToothIndex.value = gameData.trapToothIndex
          gameState.value = gameData.gameState
        }
        break

      case GameProgress.RESULT:
        gameProgress.value = 'result'
        if (response.data) {
          gameResult.value = response.data as GameResult
        }
        break
    }
  } catch (error) {
    console.error('악어게임 메시지 파싱 오류:', error)
  }
}

// 게임 시작
const startGame = () => {
  if (!props.isOwner) return

  socketStore.send(`/app/game/crocodile/start/${gameId.value}`, authStore.accessToken || '', null)
}

// 이빨 누르기
const pressTooth = (toothIndex: number) => {
  if (!isMyTurn.value || teeth.value[toothIndex].pressed) return

  // 손가락 표시
  isPressing.value = toothIndex

  // 함정 이빨인 경우 애니메이션
  if (toothIndex === trapToothIndex.value) {
    setTimeout(() => {
      gameState.value = 'failed'
      isMouthClosed.value = true

      setTimeout(() => {
        isShaking.value = true
        setTimeout(() => {
          isShaking.value = false
          // ✅ 애니메이션 끝나고 서버에 전송
          socketStore.send(
            `/app/game/crocodile/press/${gameId.value}`,
            authStore.accessToken || '',
            {
              toothIndex,
            },
          )
        }, 1000)
      }, 200)
    }, 500)
  } else {
    // 일반 이빨이면 즉시 전송
    socketStore.send(`/app/game/crocodile/press/${gameId.value}`, authStore.accessToken || '', {
      toothIndex,
    })
  }

  // 손가락 숨기기
  setTimeout(() => {
    isPressing.value = -1
  }, 300)
}

const processGetMyId = (body: string) => {
  console.log('내 아이디지롱:' + JSON.parse(body))
  userId.value = JSON.parse(body)
}

// 컴포넌트 마운트
onMounted(() => {
  bgm.value = document.querySelector('audio')
  gameId.value = planId.value
  socketStore.subscribe(`/topic/game/crocodile/${gameId.value}`, processGameOperation)
  socketStore.subscribe(`/user/queue/game/userId`, processGetMyId)

  // 자기 아이디 불러오기 요청
  socketStore.send(`/app/game/get-user-id`, authStore.accessToken || '', null)
  // 게임 참가 요청
  socketStore.send(`/app/game/crocodile/join/${gameId.value}`, authStore.accessToken || '', null)

  if (bgm.value) {
    bgm.value.volume = 0.1
    bgm.value.currentTime = 0
    bgm.value.play().catch((error) => {
      console.log('BGM 재생 실패:', error)
    })
  }
})
</script>

<style scoped>
.crocodile-game {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(to bottom, #f0fdf4, #dcfce7);
  padding: 1rem;
  min-height: 100vh;
}

.game-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #166534;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

/* 대기 화면 스타일 */
.waiting-screen {
  color: #166534;
  text-align: center;
  max-width: 28rem;
}

.waiting-message,
.creator-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.description {
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.start-button {
  padding: 1rem 2rem;
  font-size: 1.2rem;
  font-weight: bold;
  background-color: #16a34a;
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.start-button:hover {
  background-color: #15803d;
}

.loading-dots {
  display: flex;
  gap: 0.5rem;
}

.loading-dots span {
  width: 12px;
  height: 12px;
  background-color: #16a34a;
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

/* 게임 화면 스타일 */
.game-container {
  position: relative;
  width: 100%;
  max-width: 32rem;
}

.players-info {
  margin-bottom: 2rem;
  background: white;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.current-turn {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e5e7eb;
}

.turn-label {
  font-weight: bold;
  color: #166534;
}

.current-player {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #dcfce7;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
}

.players-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.player-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border-radius: 0.5rem;
  background: #f3f4f6;
  transition: all 0.3s;
}

.player-item.current {
  background: #dcfce7;
  border: 2px solid #16a34a;
}

.player-item.eliminated {
  background: #fee2e2;
  opacity: 0.6;
}

.player-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.player-avatar-small {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.player-name {
  font-weight: 500;
  color: #166534;
}

.player-name-small {
  font-size: 0.875rem;
  font-weight: 500;
}

.eliminated-badge {
  font-size: 0.75rem;
  background: #ef4444;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
}

/* 악어 스타일 */
.crocodile-head {
  position: relative;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
}

.crocodile-upper {
  background: linear-gradient(to bottom, #15803d, #166534);
  border-top-left-radius: 2rem;
  border-top-right-radius: 2rem;
  padding-top: 2.5rem;
  padding-left: 2rem;
  padding-right: 2rem;
  border: 4px solid #14532d;
}

.nose-container {
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
}

.nose {
  width: 5rem;
  height: 2.5rem;
  background: linear-gradient(to bottom, #166534, #14532d);
  border-top-left-radius: 9999px;
  border-top-right-radius: 9999px;
  border: 3px solid #14532d;
}

.eyes-container {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  padding: 0 2rem;
}

.eye {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background-color: white;
  border: 3px solid #14532d;
  position: relative;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
}

.eye::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0.75rem;
  height: 0.75rem;
  background-color: #14532d;
  border-radius: 50%;
}

.crocodile-mouth {
  position: relative;
  background: linear-gradient(to bottom, #16a34a, #15803d);
  border-bottom-left-radius: 2rem;
  border-bottom-right-radius: 2rem;
  transition: all 0.5s;
  overflow: hidden;
  height: 14rem;
  border: 4px solid #14532d;
  border-top: none;
}

.mouth-closed {
  height: 1rem !important;
  transition: height 0.2s ease-in !important;
}

.upper-teeth {
  display: flex;
  justify-content: space-around;
  padding-top: 1rem;
}

.lower-teeth {
  display: flex;
  justify-content: space-around;
  position: absolute;
  bottom: 0;
  width: 100%;
  transition: all 0.5s;
  z-index: 2;
  padding-bottom: 1rem;
}

.teeth-hidden {
  opacity: 0;
}

.tooth-container {
  perspective: 1000px;
}

.tooth {
  width: 3rem;
  height: 3rem;
  background: linear-gradient(to bottom, #ffffff, #f3f4f6);
  border: 3px solid #d1d5db;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.tooth:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.upper-tooth {
  border-radius: 0 0 1rem 1rem;
  border-top: none;
}

.lower-tooth {
  border-radius: 1rem 1rem 0 0;
  border-bottom: none;
}

.tooth-pressed {
  transform: translateY(20px);
}

.tooth-danger {
  background: linear-gradient(to bottom, #ef4444, #dc2626);
}

.tongue {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 2.5rem;
  background: linear-gradient(to bottom, #f87171, #ef4444);
  border-top-left-radius: 9999px;
  border-top-right-radius: 9999px;
  z-index: 1;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.finger {
  position: absolute;
  bottom: -2rem;
  left: 50%;
  transform: translateX(-50%);
  width: 1.5rem;
  height: 3rem;
  background: linear-gradient(to bottom, #fecaca, #fca5a5);
  border-radius: 1rem;
  z-index: 3;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.finger::after {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 1.5rem;
  height: 1.5rem;
  background: #fecaca;
  border-radius: 50%;
}

/* 턴 메시지 */
.turn-message {
  margin-top: 2rem;
  text-align: center;
  padding: 1rem;
  border-radius: 0.5rem;
}

.my-turn {
  background: #dcfce7;
  color: #166534;
  font-weight: bold;
  font-size: 1.1rem;
  margin: 0;
  padding: 1rem;
  border-radius: 0.5rem;
}

.waiting-turn {
  background: #f3f4f6;
  color: #6b7280;
  margin: 0;
  padding: 1rem;
  border-radius: 0.5rem;
}

/* 결과 화면 */
.result-screen {
  width: 100%;
  max-width: 32rem;
}

.game-message {
  background: white;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.result-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #166534;
}

.result-content {
  margin-bottom: 2rem;
}

.loser-info,
.survivors-info {
  margin-bottom: 2rem;
}

.loser-info h3,
.survivors-info h3 {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #166534;
}

.player-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 0.5rem;
}

.player-card.loser {
  background: #fee2e2;
  border: 2px solid #ef4444;
}

.player-card.survivor {
  background: #dcfce7;
  border: 2px solid #16a34a;
}

.loser-badge {
  background: #ef4444;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
}

.survivors-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
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

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    transform: translateX(-10px);
  }
  20%,
  40%,
  60%,
  80% {
    transform: translateX(10px);
  }
}

.shake {
  animation: shake 0.5s;
}

/* 반응형 */
@media (max-width: 768px) {
  .game-title {
    font-size: 2rem;
  }

  .players-list {
    justify-content: center;
  }

  .current-turn {
    flex-direction: column;
    text-align: center;
  }
}
</style>
