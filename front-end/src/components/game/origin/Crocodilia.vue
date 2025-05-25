<template>
  <div class="crocodile-game">
    <audio ref="bgm" loop preload="auto">
      <source src="@/assets/music/gameBGM.mp3" type="audio/mpeg" />
    </audio>
    <h1 class="game-title">악어 이빨 게임</h1>

    <div class="game-container">
      <!-- 점수 표시 -->
      <div class="score-display">점수: {{ score }}</div>

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
                :disabled="tooth.pressed || gameState === 'failed'"
              ></button>
              <div v-if="isPressing === index" class="finger"></div>
            </div>
          </div>

          <!-- 악어 혀 -->
          <div class="tongue"></div>
        </div>
      </div>

      <!-- 게임 결과 메시지 -->
      <div v-if="gameState === 'failed'" class="game-message game-over">
        <p class="message-title">앗! 물렸다! 게임 오버!</p>
        <p class="message-score">점수: {{ score }}</p>
      </div>

      <div v-if="gameState === 'won'" class="game-message game-win">
        <p class="message-title">축하합니다! 모든 이빨을 성공적으로 눌렀습니다!</p>
        <p class="message-score">점수: {{ score }}</p>
      </div>

      <!-- 다시하기 버튼 -->
      <div class="restart-container">
        <button v-if="gameState !== 'playing'" @click="resetGame" class="restart-button">
          다시 하기
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue'

type GameState = 'playing' | 'failed' | 'won'
type Tooth = {
  pressed: boolean
}

export default defineComponent({
  name: 'CrocodileDentist',
  setup() {
    const gameState = ref<GameState>('playing')
    const teeth = ref<Tooth[]>([])
    const trapToothIndex = ref<number>(-1)
    const score = ref<number>(0)
    const isMouthClosed = ref<boolean>(false)
    const isShaking = ref<boolean>(false)
    const isPressing = ref<number>(-1)
    const bgm = ref<HTMLAudioElement | null>(null)

    // 게임 초기화
    const initGame = () => {
      // 8개의 이빨 생성
      teeth.value = Array(8)
        .fill(null)
        .map(() => ({ pressed: false }))

      // 함정 이빨 랜덤 선택
      trapToothIndex.value = Math.floor(Math.random() * teeth.value.length)

      gameState.value = 'playing'
      score.value = 0
      isMouthClosed.value = false
      isShaking.value = false
      isPressing.value = -1

      // BGM 재생
      if (bgm.value) {
        bgm.value.volume = 0.1
        bgm.value.currentTime = 0
        bgm.value.play().catch((error) => {
          console.log('BGM 재생 실패:', error)
        })
      }
    }

    // 이빨 누르기
    const pressTooth = (index: number) => {
      if (gameState.value !== 'playing' || teeth.value[index].pressed) {
        return
      }

      // 손가락 표시
      isPressing.value = index

      // 이빨 누르기 성공
      teeth.value[index].pressed = true
      score.value++

      // 함정 이빨을 누른 경우
      if (index === trapToothIndex.value) {
        // 입을 더 크게 벌리기
        const mouth = document.querySelector('.crocodile-mouth') as HTMLElement
        if (mouth) {
          mouth.style.height = '18rem'
          mouth.style.transition = 'height 0.5s ease-out'
        }

        // 잠시 후 입 닫기
        setTimeout(() => {
          gameOver()
          // 입이 닫힌 후 흔들림 시작
          setTimeout(() => {
            isShaking.value = true
            setTimeout(() => {
              isShaking.value = false
            }, 1000)
          }, 200)
        }, 500)
        return
      }

      // 손가락 숨기기
      setTimeout(() => {
        isPressing.value = -1
      }, 300)
    }

    // 게임 오버
    const gameOver = () => {
      gameState.value = 'failed'
      isMouthClosed.value = true
    }

    // 게임 재시작
    const resetGame = () => {
      const mouth = document.querySelector('.crocodile-mouth') as HTMLElement
      if (mouth) {
        mouth.style.height = '14rem'
        mouth.style.transition = 'all 0.5s'
      }
      initGame()
    }

    // 컴포넌트 마운트 시 게임 초기화
    onMounted(() => {
      bgm.value = document.querySelector('audio')
      initGame()
    })

    return {
      gameState,
      teeth,
      trapToothIndex,
      score,
      isMouthClosed,
      isShaking,
      isPressing,
      pressTooth,
      resetGame,
    }
  },
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
  min-height: 100%;
  overflow-y: auto;
}

.game-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #166534;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.game-container {
  position: relative;
  width: 100%;
  max-width: 32rem;
}

.score-display {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #16a34a;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 1rem;
  font-weight: bold;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

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

.game-message {
  margin-top: 2rem;
  text-align: center;
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.game-over {
  background: linear-gradient(to bottom, #fee2e2, #fecaca);
  border: 3px solid #ef4444;
}

.game-win {
  background: linear-gradient(to bottom, #dcfce7, #bbf7d0);
  border: 3px solid #22c55e;
}

.message-title {
  font-size: 1.75rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.game-over .message-title {
  color: #dc2626;
}

.game-win .message-title {
  color: #16a34a;
}

.message-score {
  font-size: 1.25rem;
  margin-top: 0.75rem;
}

.restart-container {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.restart-button {
  padding: 1rem 2rem;
  background: linear-gradient(to bottom, #16a34a, #15803d);
  color: white;
  border-radius: 1rem;
  font-weight: bold;
  font-size: 1.25rem;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.restart-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
  background: linear-gradient(to bottom, #15803d, #14532d);
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

@media (max-width: 600px) {
  .lower-teeth {
    overflow-x: auto;
    white-space: nowrap;
    gap: 0.5rem;
    justify-content: flex-start;
  }
  .tooth-container {
    display: inline-block;
  }
  .game-title {
    font-size: 1.2rem;
    margin-bottom: 0.7rem;
    word-break: keep-all;
    padding-top: 0.5rem;
    line-height: 1.3;
  }
}
</style>
