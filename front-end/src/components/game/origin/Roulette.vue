<template>
  <div class="roulette-container">
    <audio ref="bgm" loop preload="auto">
      <source src="@/assets/music/gameBGM2.mp3" type="audio/mpeg" />
    </audio>
    <h1 class="title">룰렛 게임</h1>

    <!-- 설정 화면 -->
    <div v-if="gamePhase === 'setup'" class="setup-container">
      <div class="setup-box">
        <h2 class="setup-title">룰렛 항목 개수 설정</h2>
        <div class="input-group">
          <label class="input-label">항목 개수 (2-8)</label>
          <input type="number" v-model.number="itemCount" min="2" max="8" class="number-input" />
        </div>
        <button
          @click="proceedToInput"
          class="primary-button"
          :disabled="itemCount < 2 || itemCount > 8"
        >
          다음
        </button>
      </div>
    </div>

    <!-- 입력 화면 -->
    <div v-if="gamePhase === 'input'" class="input-container">
      <div class="input-box">
        <h2 class="input-title">룰렛 항목 입력</h2>
        <div v-for="(item, index) in items" :key="index" class="item-input-group">
          <label class="item-label">항목 {{ index + 1 }}</label>
          <input
            type="text"
            v-model="items[index]"
            class="text-input"
            placeholder="항목 내용을 입력하세요"
          />
        </div>
        <div class="button-group">
          <button @click="gamePhase = 'setup'" class="secondary-button">이전</button>
          <button @click="startRoulette" class="primary-button" :disabled="!allItemsFilled">
            룰렛 시작
          </button>
        </div>
      </div>
    </div>

    <!-- 룰렛 화면 -->
    <div v-if="gamePhase === 'spinning' || gamePhase === 'result'" class="roulette-game-container">
      <!-- 룰렛 -->
      <div class="roulette-wrapper">
        <!-- 화살표 마커 -->
        <div class="arrow-marker"></div>

        <!-- 룰렛 휠 -->
        <div
          ref="rouletteWheel"
          class="roulette-wheel"
          :style="{ transform: `rotate(${-rotationDegree}deg)` }"
          @transitionend="onRouletteStop"
        >
          <div
            v-for="(item, index) in items"
            :key="index"
            class="roulette-item"
            :style="{
              transform: `rotate(${(index * 360) / items.length + 90}deg)`,
              'background-color': getItemColor(index),
            }"
          >
            <span class="item-text">{{ item }}</span>
          </div>
        </div>

        <!-- 룰렛 중앙 -->
        <div class="roulette-center"></div>
      </div>

      <!-- 돌리기 버튼 -->
      <button
        v-if="gamePhase === 'spinning' && !isSpinning"
        @click="spinRoulette"
        class="spin-button"
      >
        돌리기
      </button>

      <!-- 결과 화면 -->
      <div v-if="gamePhase === 'result'" class="result-container">
        <!-- 폭죽 효과 -->
        <div class="fireworks-container">
          <div
            v-for="i in 20"
            :key="`firework-${i}`"
            class="firework"
            :style="getRandomFireworkStyle()"
          ></div>
        </div>

        <div class="result-box">
          <h2 class="result-title">결과</h2>
          <p class="result-text">{{ selectedItem }}</p>
        </div>

        <button @click="resetGame" class="reset-button">다시 하기</button>
      </div>
    </div>

    <button class="back-button" @click="$emit('goBackToSelect')">
      <i class="fas fa-arrow-left"></i> 게임 선택
    </button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue'

type GamePhase = 'setup' | 'input' | 'spinning' | 'result'

export default defineComponent({
  name: 'RouletteGame',
  setup() {
    // 게임 상태
    const gamePhase = ref<GamePhase>('setup')
    const itemCount = ref<number>(4)
    const items = ref<string[]>([])
    const rotationDegree = ref<number>(0)
    const isSpinning = ref<boolean>(false)
    const selectedItem = ref<string>('')
    const selectedIndex = ref<number>(-1)
    const rouletteWheel = ref<HTMLElement | null>(null)
    const bgm = ref<HTMLAudioElement | null>(null)

    // 모든 항목이 입력되었는지 확인
    const allItemsFilled = computed(() => {
      return items.value.every((item) => item.trim() !== '')
    })

    // 입력 화면으로 진행
    const proceedToInput = () => {
      if (itemCount.value < 2 || itemCount.value > 8) return

      // 항목 배열 초기화
      items.value = Array(itemCount.value).fill('')
      gamePhase.value = 'input'
    }

    // 룰렛 시작
    const startRoulette = () => {
      if (!allItemsFilled.value) return

      gamePhase.value = 'spinning'
      rotationDegree.value = 0
    }

    // 컴포넌트 마운트 시 오디오 요소 참조 설정 및 재생
    onMounted(() => {
      bgm.value = document.querySelector('audio')
      if (bgm.value) {
        bgm.value.volume = 0.1
        bgm.value.currentTime = 0
        bgm.value.play().catch((error) => {
          console.log('BGM 재생 실패:', error)
        })
      }
    })

    // 룰렛 돌리기
    const spinRoulette = () => {
      if (isSpinning.value) return

      isSpinning.value = true

      // 랜덤하게 결과 선택
      selectedIndex.value = Math.floor(Math.random() * items.value.length)
      selectedItem.value = items.value[selectedIndex.value]

      // 회전 각도 계산
      const baseRotation = 1800 // 5바퀴
      // 아이템 한칸 각도
      const itemAngle = 360 / items.value.length
      console.log(itemAngle)
      //
      const targetAngle = itemAngle * selectedIndex.value

      // 해당 번호까지 돌아가야하는 각도

      rotationDegree.value = baseRotation + targetAngle + 10
      if (items.value.length >= 5) {
        rotationDegree.value += itemAngle
      }
    }

    // 룰렛이 멈췄을 때 호출되는 함수
    const onRouletteStop = (event: TransitionEvent) => {
      // transform 속성의 transition이 끝났을 때만 처리
      if (event.propertyName === 'transform' && isSpinning.value) {
        // 약간의 지연을 주어 애니메이션이 완전히 끝난 후 결과 표시
        setTimeout(() => {
          gamePhase.value = 'result'
          isSpinning.value = false
        }, 0)
      }
    }

    // 게임 재설정
    const resetGame = () => {
      gamePhase.value = 'setup'
      itemCount.value = 4
      items.value = []
      rotationDegree.value = 0
      isSpinning.value = false
      selectedItem.value = ''
      selectedIndex.value = -1
    }

    // 항목 색상 가져오기
    const getItemColor = (index: number) => {
      const colors = [
        'bg-red-400',
        'bg-blue-400',
        'bg-green-400',
        'bg-yellow-400',
        'bg-pink-400',
        'bg-purple-400',
        'bg-orange-400',
        'bg-teal-400',
        'bg-cyan-400',
        'bg-lime-400',
        'bg-amber-400',
        'bg-emerald-400',
      ]
      return colors[index % colors.length]
    }

    // 랜덤 폭죽 스타일 생성
    const getRandomFireworkStyle = () => {
      const size = Math.floor(Math.random() * 10) + 5 // 5-15px
      const duration = Math.random() * 1 + 0.5 // 0.5-1.5s
      const x = Math.random() * 100 // 0-100%
      const y = Math.random() * 100 // 0-100%
      const delay = Math.random() * 0.5 // 0-0.5s
      const color = `hsl(${Math.random() * 360}, 100%, 50%)`

      return {
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor: color,
        left: `${x}%`,
        top: `${y}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`,
      }
    }

    return {
      gamePhase,
      itemCount,
      items,
      rotationDegree,
      isSpinning,
      selectedItem,
      rouletteWheel,
      allItemsFilled,
      proceedToInput,
      startRoulette,
      spinRoulette,
      resetGame,
      getItemColor,
      getRandomFireworkStyle,
      onRouletteStop,
    }
  },
})
</script>

<style scoped>
.roulette-container {
  width: 100%;
  max-width: 100%;
  min-height: 100%;
  height: auto;
  max-height: 100vh;
  overflow-y: auto;
  padding: 1rem 0.5rem;
  box-sizing: border-box;
  background: linear-gradient(to bottom, #1a1a2e, #16213e);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
}

.title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.setup-container,
.input-container,
.roulette-game-container {
  width: 100%;
  max-width: 28rem;
  margin: 0 auto;
  padding-top: 1rem;
  box-sizing: border-box;
}

.setup-box,
.input-box {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.setup-title,
.input-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #fff;
}

.input-group {
  margin-bottom: 1rem;
}

.input-label,
.item-label {
  display: block;
  font-size: 1rem;
  font-weight: 500;
  color: #fff;
  margin-bottom: 0.5rem;
}

.number-input,
.text-input {
  width: 100%;
  padding: 0.75rem 1rem;
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 0.5rem;
  color: #fff;
  font-size: 1rem;
  box-sizing: border-box;
}

.number-input:focus,
.text-input:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.3);
}

.primary-button {
  width: 100%;
  padding: 0.75rem 1rem;
  background: linear-gradient(45deg, #7c3aed, #6d28d9);
  color: white;
  border-radius: 0.5rem;
  font-weight: 600;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.primary-button:hover {
  background: linear-gradient(45deg, #6d28d9, #5b21b6);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(124, 58, 237, 0.4);
}

.primary-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.secondary-button {
  flex: 1;
  padding: 0.5rem 1rem;
  background-color: #6b7280;
  color: white;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.secondary-button:hover {
  background-color: #4b5563;
}

.button-group {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.roulette-wrapper {
  position: relative;
  filter: drop-shadow(0 0 20px rgba(124, 58, 237, 0.3));
}

.arrow-marker {
  position: absolute;
  top: -2rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 0;
  height: 0;
  border-left: 20px solid transparent;
  border-right: 20px solid transparent;
  border-top: 30px solid #7c3aed;
  filter: drop-shadow(0 0 10px rgba(124, 58, 237, 0.5));
}

.roulette-wheel {
  position: relative;
  width: 100%;
  max-width: 320px;
  height: auto;
  aspect-ratio: 1/1;
  margin: 0 auto;
  border-radius: 50%;
  border: 12px solid #7c3aed;
  overflow: hidden;
  transition: transform 10s cubic-bezier(0.2, 0.8, 0.2, 1);
  background: rgba(255, 255, 255, 0.1);
  box-shadow:
    0 0 30px rgba(124, 58, 237, 0.3),
    inset 0 0 20px rgba(0, 0, 0, 0.5);
}

.roulette-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 50%;
  transform-origin: 100% 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 2rem;
  text-align: center;
  font-weight: bold;
  font-size: 1.25rem;
  clip-path: polygon(0 0, 100% 0, 100% 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.roulette-item:hover {
  filter: brightness(1.2);
}

.roulette-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-right: 2px solid rgba(255, 255, 255, 0.3);
  transform-origin: 100% 100%;
  transform: rotate(0deg);
}

.item-text {
  transform: rotate(-45deg);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
  padding: 0.75rem;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  font-size: 1.25rem;
  font-weight: 700;
}

.roulette-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  background: linear-gradient(45deg, #7c3aed, #6d28d9);
  z-index: 10;
  box-shadow:
    0 0 20px rgba(124, 58, 237, 0.5),
    inset 0 0 10px rgba(0, 0, 0, 0.5);
  border: 4px solid rgba(255, 255, 255, 0.2);
}

.spin-button,
.reset-button {
  padding: 1rem 2rem;
  background: linear-gradient(45deg, #7c3aed, #6d28d9);
  color: white;
  border-radius: 0.75rem;
  font-weight: bold;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 5px 15px rgba(124, 58, 237, 0.3);
  display: block;
  margin: 1.5rem auto 0 auto;
}

.spin-button:hover,
.reset-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(124, 58, 237, 0.4);
}

.result-container {
  margin-top: 2rem;
  text-align: center;
}

.result-box {
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 1rem;
  box-shadow: 0 0 30px rgba(124, 58, 237, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: bounce 1s infinite;
}

.result-title {
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
  margin-bottom: 1rem;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.result-text {
  font-size: 2rem;
  font-weight: 800;
  color: #7c3aed;
  margin-bottom: 0px;
  text-shadow: 0 0 15px rgba(124, 58, 237, 0.5);
}

.fireworks-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 20;
  overflow: hidden;
}

.firework {
  position: absolute;
  border-radius: 50%;
  animation: firework-animation 1s ease-out forwards;
  opacity: 0;
}

@keyframes firework-animation {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

@media (max-width: 600px) {
  .setup-container,
  .input-container,
  .roulette-game-container {
    max-width: 100%;
    padding-top: 0.5rem;
  }
  .roulette-wheel {
    max-width: 95vw;
  }
  .title {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: #333;
  font-weight: 500;
  backdrop-filter: blur(8px);
}

.back-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateX(-4px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.back-button i {
  font-size: 1.1rem;
  color: #7c3aed;
}
</style>
