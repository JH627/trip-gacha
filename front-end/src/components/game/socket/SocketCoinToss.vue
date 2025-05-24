<template>
  <div class="coin-toss-container">
    <audio ref="bgm" loop preload="auto">
      <source src="@/assets/music/gameBGM2.mp3" type="audio/mpeg" />
    </audio>
    <h1 class="title">코인토스 게임</h1>

    <!-- 선택 화면 -->
    <div v-if="gameState === 'selection'" class="selection-container">
      <h2 class="selection-title">동전의 어느 면이 나올지 선택하세요</h2>
      <div class="button-container">
        <button
          @click="selectSide('heads')"
          class="side-button"
          :class="{ selected: userChoice === 'heads' }"
        >
          <div class="coin-circle heads">
            <span class="coin-text">앞</span>
          </div>
          <span>앞면</span>
        </button>

        <button
          @click="selectSide('tails')"
          class="side-button"
          :class="{ selected: userChoice === 'tails' }"
        >
          <div class="coin-circle tails">
            <span class="coin-text">뒤</span>
          </div>
          <span>뒷면</span>
        </button>
      </div>

      <div class="start-button-container">
        <button @click="startCountdown" class="start-button" :disabled="!userChoice">
          시작하기
        </button>
      </div>
    </div>

    <!-- 카운트다운 화면 -->
    <div v-if="gameState === 'countdown'" class="countdown-container">
      <div class="countdown-number">
        {{ countdown }}
      </div>
    </div>

    <!-- 동전 던지기 화면 -->
    <div v-if="gameState === 'flipping'" class="flipping-container">
      <div class="coin-base"></div>

      <div ref="coinRef" class="coin" :class="{ 'coin-flip': isFlipping }">
        <div class="coin-side coin-front">
          <span class="coin-text">앞</span>
        </div>
        <div class="coin-side coin-back">
          <span class="coin-text">뒤</span>
        </div>
      </div>

      <div v-if="showFinger" class="finger" :style="fingerStyle">
        <div class="finger-shape"></div>
      </div>
    </div>

    <!-- 결과 화면 -->
    <div v-if="gameState === 'result'" class="result-container">
      <div class="result-coin" :class="result === 'heads' ? 'heads' : 'tails'">
        <span class="result-text">{{ result === 'heads' ? '앞' : '뒤' }}</span>
      </div>

      <div class="result-text-container">
        <p class="result-info">
          당신의 선택: <span class="bold">{{ userChoice === 'heads' ? '앞면' : '뒷면' }}</span>
        </p>
        <p class="result-info">
          결과: <span class="bold">{{ result === 'heads' ? '앞면' : '뒷면' }}</span>
        </p>
        <p class="result-message" :class="isMatch ? 'success' : 'fail'">
          {{ isMatch ? '일치! 축하합니다! 🎉' : '불일치! 다시 도전해보세요! 😢' }}
        </p>
      </div>

      <button @click="resetGame" class="restart-button">다시 하기</button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue'

type GameState = 'selection' | 'countdown' | 'flipping' | 'result'
type CoinSide = 'heads' | 'tails'

export default defineComponent({
  name: 'CoinToss',
  setup() {
    const gameState = ref<GameState>('selection')
    const userChoice = ref<CoinSide | null>(null)
    const result = ref<CoinSide | null>(null)
    const countdown = ref(3)
    const isFlipping = ref(false)
    const showFinger = ref(false)
    const fingerStyle = ref({
      bottom: '10px',
      left: '50%',
      transform: 'translateX(-50%)',
    })
    const coinRef = ref<HTMLElement | null>(null)
    const bgm = ref<HTMLAudioElement | null>(null)

    const isMatch = computed(() => {
      return userChoice.value === result.value
    })

    const selectSide = (side: CoinSide) => {
      userChoice.value = side
    }

    const startCountdown = () => {
      gameState.value = 'countdown'
      countdown.value = 3

      const timer = setInterval(() => {
        countdown.value--

        if (countdown.value <= 0) {
          clearInterval(timer)
          startFlipping()
        }
      }, 1000)
    }

    const startFlipping = () => {
      gameState.value = 'flipping'
      showFinger.value = true

      // 손가락 애니메이션
      setTimeout(() => {
        if (coinRef.value) {
          // 손가락이 위로 올라가는 애니메이션
          fingerStyle.value = {
            bottom: '30px',
            left: '50%',
            transform: 'translateX(-50%)',
          }

          // 잠시 후 동전 플립 시작
          setTimeout(() => {
            isFlipping.value = true
            showFinger.value = false

            // 랜덤 결과 결정
            result.value = Math.random() < 0.5 ? 'heads' : 'tails'

            // 동전 회전 애니메이션 후 결과 표시
            setTimeout(() => {
              gameState.value = 'result'
              isFlipping.value = false
            }, 3000)
          }, 500)
        }
      }, 500)
    }

    const resetGame = () => {
      gameState.value = 'selection'
      userChoice.value = null
      result.value = null
      isFlipping.value = false
      showFinger.value = false
    }

    // 컴포넌트 마운트 시 BGM 재생
    onMounted(() => {
      bgm.value = document.querySelector('audio')
      if (bgm.value) {
        bgm.value.volume = 0.5
        bgm.value.play().catch((error) => {
          console.log('BGM 재생 실패:', error)
        })
      }
    })

    return {
      gameState,
      userChoice,
      result,
      countdown,
      isFlipping,
      showFinger,
      fingerStyle,
      coinRef,
      isMatch,
      selectSide,
      startCountdown,
      resetGame,
    }
  },
})
</script>

<style scoped>
.coin-toss-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(to bottom, #fffbeb, #fef3c7);
  padding: 1rem;
}

.title {
  font-size: 1.875rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #92400e;
}

.selection-container {
  width: 100%;
  max-width: 28rem;
}

.selection-title {
  font-size: 1.25rem;
  text-align: center;
  margin-bottom: 1.5rem;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
}

.side-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  border-radius: 0.5rem;
  transition: all 0.3s;
  background-color: white;
}

.side-button:hover {
  background-color: #fef3c7;
}

.side-button.selected {
  background-color: #f59e0b;
  color: white;
}

.coin-circle {
  width: 6rem;
  height: 6rem;
  border-radius: 50%;
  border: 4px solid #92400e;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coin-circle.heads {
  background-color: #fbbf24;
}

.coin-circle.tails {
  background-color: #fcd34d;
}

.coin-text {
  font-size: 1.5rem;
}

.start-button-container {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.start-button {
  padding: 0.75rem 1.5rem;
  background-color: #d97706;
  color: white;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 1.125rem;
}

.start-button:hover {
  background-color: #b45309;
}

.start-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.countdown-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.countdown-number {
  font-size: 8rem;
  font-weight: bold;
  color: #d97706;
  animation: pulse 1s infinite;
}

.flipping-container {
  position: relative;
  width: 100%;
  height: 16rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coin-base {
  position: absolute;
  bottom: 0;
  width: 100%;
  display: flex;
  justify-content: center;
}

.coin-base::after {
  content: '';
  width: 4rem;
  height: 1.5rem;
  background-color: #92400e;
  border-radius: 9999px;
}

.coin {
  width: 6rem;
  height: 6rem;
  border-radius: 50%;
  background-color: #fbbf24;
  border: 4px solid #92400e;
  position: relative;
}

.finger {
  position: absolute;
  transition: all 0.5s ease;
}

.finger-shape {
  width: 1.5rem;
  height: 4rem;
  background-color: #fcd34d;
  border-top-left-radius: 9999px;
  border-top-right-radius: 9999px;
}

.result-container {
  width: 100%;
  max-width: 28rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
}

.result-coin {
  width: 8rem;
  height: 8rem;
  border-radius: 50%;
  border: 4px solid #92400e;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-coin.heads {
  background-color: #fbbf24;
}

.result-coin.tails {
  background-color: #fcd34d;
}

.result-text {
  font-size: 1.875rem;
}

.result-text-container {
  text-align: center;
  margin-bottom: 1.5rem;
}

.result-info {
  font-size: 1.25rem;
}

.bold {
  font-weight: bold;
}

.result-message {
  font-size: 1.5rem;
  margin-top: 1rem;
}

.result-message.success {
  color: #059669;
}

.result-message.fail {
  color: #dc2626;
}

.restart-button {
  padding: 0.75rem 1.5rem;
  background-color: #d97706;
  color: white;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 1.125rem;
}

.restart-button:hover {
  background-color: #b45309;
}

.backface-hidden {
  backface-visibility: hidden;
}

.coin-flip {
  animation: flip 3s forwards;
  transform-style: preserve-3d;
}

.coin-side {
  transform-style: preserve-3d;
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  backface-visibility: hidden;
}

.coin-front {
  background-color: #fbbf24;
  transform: rotateY(0deg);
}

.coin-back {
  background-color: #fcd34d;
  transform: rotateY(180deg);
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes flip {
  0% {
    transform: rotateY(0) translateY(0);
  }
  20% {
    transform: rotateY(180deg) translateY(-100px);
  }
  40% {
    transform: rotateY(360deg) translateY(-150px);
  }
  60% {
    transform: rotateY(540deg) translateY(-100px);
  }
  80% {
    transform: rotateY(720deg) translateY(-50px);
  }
  100% {
    transform: rotateY(var(--final-rotation)) translateY(0);
  }
}
</style>
