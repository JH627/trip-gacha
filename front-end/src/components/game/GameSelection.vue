<template>
  <div class="game-selection">
    <div class="game-grid">
      <div
        v-for="game in games"
        :key="game.value"
        class="game-card"
        @click="$emit('selectGame', game.value.toString())"
      >
        <div class="game-icon">
          <component :is="game.icon" :size="32" />
        </div>
        <span class="game-name">{{ game.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'
import { MousePointerClick, RotateCcw, Coins, Zap, Gamepad2 } from 'lucide-vue-next'
import { Game } from './Game'

defineEmits(['selectGame'])

// 게임 추가하면 여기에 넣기
const games = [
  {
    value: Game.FAST_CLICK,
    label: 'FastClick',
    icon: MousePointerClick,
  },
  {
    value: Game.ROULETTE,
    label: '룰렛',
    icon: RotateCcw,
  },
  {
    value: Game.COIN_TOSS,
    label: '코인토스',
    icon: Coins,
  },
  {
    value: Game.CROCODILIA,
    label: '악어 입 벌리기',
    icon: Zap,
  },
  {
    value: 'OTHER_GAME',
    label: '다른 게임',
    icon: Gamepad2,
  },
]
</script>

<style scoped>
.game-selection {
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  justify-items: center;
}

.game-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 120px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  gap: 12px;
}

.game-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border-color: #4f46e5;
}

.game-card:active {
  transform: translateY(-2px);
}

.game-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background-color: #f3f4f6;
  border-radius: 50%;
  color: #4f46e5;
  transition: all 0.3s ease;
}

.game-card:hover .game-icon {
  background-color: #4f46e5;
  color: white;
}

.game-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .game-card {
    width: 120px;
    height: 100px;
  }

  .game-icon {
    width: 40px;
    height: 40px;
  }

  .game-icon svg {
    width: 24px;
    height: 24px;
  }

  .game-name {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .game-grid {
    gap: 12px;
  }

  .game-card {
    width: 100px;
    height: 90px;
  }

  .game-icon {
    width: 35px;
    height: 35px;
  }

  .game-icon svg {
    width: 20px;
    height: 20px;
  }
}
</style>
