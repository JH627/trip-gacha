<template>
  <div class="game-selection">
    <div class="game-grid">
      <div class="game-card" @click="$emit('selectGame', 'FastClick')">
        <div class="game-icon">
          <MousePointerClick :size="32" />
        </div>
        <span class="game-name">FastClick</span>
      </div>

      <div class="game-card" @click="$emit('selectGame', 'Roulette')">
        <div class="game-icon">
          <RotateCcw :size="32" />
        </div>
        <span class="game-name">룰렛</span>
      </div>

      <div class="game-card" @click="$emit('selectGame', 'CoinToss')">
        <div class="game-icon">
          <Coins :size="32" />
        </div>
        <span class="game-name">코인토스</span>
      </div>

      <div class="game-card" @click="$emit('selectGame', 'Crocodilia')">
        <div class="game-icon">
          <Zap :size="32" />
        </div>
        <span class="game-name">악어 입 벌리기</span>
      </div>

      <div class="game-card" @click="$emit('selectGame', 'OtherGame')">
        <div class="game-icon">
          <Gamepad2 :size="32" />
        </div>
        <span class="game-name">다른 게임</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'
import { MousePointerClick, RotateCcw, Coins, Zap, Gamepad2 } from 'lucide-vue-next'

defineEmits(['selectGame'])
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
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
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
