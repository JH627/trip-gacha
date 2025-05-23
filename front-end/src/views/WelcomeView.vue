<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { defaultApi } from '@/api/axios'
import { useRouter } from 'vue-router'
import { RocketOutlined } from '@ant-design/icons-vue'

import SpotList from '@/components/welcome/SpotList.vue'
import WelcomeImageList from '@/components/welcome/WelcomeImageList.vue'
import AiChatModal from '@/components/welcome/AiChatModal.vue'
import type { Destination } from '@/types/trip'
import { WechatOutlined } from '@ant-design/icons-vue'
import SpotDetailModal from '@/components/welcome/SpotDetailModal.vue'

// 추천 여행지
const destinations = ref<Destination[]>([])
const router = useRouter()
const selectedDestination = ref<Destination | null>(null)
const detailModalOpen = ref(false)

onMounted(async () => {
  try {
    const response = await defaultApi.get('/trip/destination')
    destinations.value = response.data.result
  } catch (error) {
    console.log(error)
  }
})

// AI 채팅 모달
const open = ref(false)
const showModal = () => {
  open.value = true
}
const handleOk = () => {
  open.value = false
}

// 여행지 상세 모달
function openDetailModal(destination: Destination) {
  selectedDestination.value = destination
  detailModalOpen.value = true
}

function closeDetailModal() {
  detailModalOpen.value = false
  selectedDestination.value = null
}

// 여행지 상세 모달 - 여행 시작 버튼
function handleStartTrip() {
  router.push('/trip/lobby')
}
</script>

<template>
  <div class="welcome-container">
    <div class="welcome-info-wrapper">
      <!-- 상단 이미지 리스트 -->
      <div class="welcome-info">
        <WelcomeImageList />
      </div>
    </div>
    <!-- 여행 시작 버튼 -->
    <div class="start-trip-btn-wrapper">
      <a-button
        type="primary"
        size="large"
        class="start-trip-btn"
        @click="router.push('/trip/lobby')"
      >
        <RocketOutlined style="margin-right: 8px; font-size: 1.3em" />
        여행 시작하기
      </a-button>
    </div>
    <!-- 추천 여행지 리스트 -->
    <div class="recommend-spot-container">
      <h1>추천 여행지</h1>
      <!-- 클릭 시 모달 오픈 -->
      <SpotList :destinations="destinations" @select="openDetailModal" />
    </div>
    <!-- 여행지 상세 모달 -->
    <SpotDetailModal
      :destination="selectedDestination"
      :open="detailModalOpen"
      @close="closeDetailModal"
      @start="handleStartTrip"
    />
    <div class="ai-chat-modal">
      <div>
        <a-float-button
          shape="circle"
          type="primary"
          @click="showModal"
          :style="{
            left: '20px',
            bottom: '20px',
          }"
        >
          <template #icon>
            <WechatOutlined />
          </template>
        </a-float-button>
        <a-modal
          v-model:open="open"
          title="AI 여행 추천"
          :footer="null"
          @ok="handleOk"
          width="600px"
        >
          <AiChatModal />
        </a-modal>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 환영 페이지 전체 컨테이너 */
.welcome-container {
  width: 100%;
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* 환영 페이지 이미지 컨테이너 */
.welcome-info-wrapper {
  width: 100%;
  position: relative;
  margin-bottom: 1rem;
  border-radius: 12px;
  overflow: hidden;
}

.welcome-info {
  position: relative;
  width: 100%;
  height: auto;
}

/* 여행 시작 버튼 컨테이너 */
.start-trip-btn-wrapper {
  display: flex;
  justify-content: center;
  margin: 2rem 0 1rem 0;
}

/* 여행 시작 버튼 */
.start-trip-btn {
  font-size: 1.2rem;
  padding: 0.9rem 5rem;
  font-weight: bold;
  border-radius: 2rem;
  background: linear-gradient(90deg, #36d1c4 0%, #5b86e5 100%);
  border: none;
  box-shadow:
    0 4px 16px rgba(91, 134, 229, 0.15),
    0 1.5px 6px rgba(54, 209, 196, 0.12);
  transition:
    transform 0.1s,
    box-shadow 0.1s;
  display: flex;
  align-items: center;
}
.start-trip-btn:hover,
.start-trip-btn:focus {
  background: linear-gradient(90deg, #5b86e5 0%, #36d1c4 100%);
  transform: translateY(-2px) scale(1.04);
  box-shadow:
    0 8px 24px rgba(91, 134, 229, 0.22),
    0 3px 12px rgba(54, 209, 196, 0.18);
}

/* 추천 여행지 컨테이너 */
.recommend-spot-container {
  margin-top: 2rem;
}

/* 추천 여행지 제목 */
.recommend-spot-container h1 {
  margin-bottom: 1rem;
  font-size: 1.8rem;
  font-weight: bold;
}

/* AI 채팅 버튼 */
.ai-chat-button {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

@media screen and (max-width: 768px) {
  .welcome-container {
    padding: 0 0.5rem;
  }

  .welcome-info-wrapper {
    border-radius: 8px;
  }

  .recommend-spot-container {
    margin-top: 1.5rem;
  }

  .recommend-spot-container h1 {
    font-size: 1.4rem;
    margin-bottom: 0.8rem;
  }
}

@media screen and (max-width: 480px) {
  .welcome-info-wrapper {
    border-radius: 6px;
  }

  .recommend-spot-container h1 {
    font-size: 1.2rem;
    margin-bottom: 0.6rem;
  }
}
</style>
