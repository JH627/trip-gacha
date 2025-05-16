<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { defaultApi } from '@/api/axios'

import SpotList from '@/components/welcome/SpotList.vue'
import WelcomeImageList from '@/components/welcome/WelcomeImageList.vue'

interface Destination {
  id: number
  name: string
  description: string
  img: string
}

const destinations = ref<Destination[]>([])

onMounted(async () => {
  try {
    const response = await defaultApi.get('/trip/destination')
    destinations.value = response.data.result
    console.log(destinations.value)
  } catch (error) {
    console.log(error)
  }
})

const open = ref(false)
const showModal = () => {
  open.value = true
}
const handleOk = (e) => {
  console.log(e)
  open.value = false
}
</script>

<template>
  <div class="welcome-container">
    <div class="welcome-info-wrapper">
      <div class="welcome-info">
        <WelcomeImageList />
      </div>
    </div>
    <div class="recommend-spot-container">
      <h1>추천 여행지</h1>
      <SpotList :destinations="destinations" />
    </div>
    <div class="ai-chat-modal">
      <div>
        <a-button type="primary" @click="showModal">AI 추천</a-button>
        <a-modal v-model:open="open" title="Basic Modal" :footer="null" @ok="handleOk">
          <p>Some contents...</p>
          <p>Some contents...</p>
          <p>Some contents...</p>
        </a-modal>
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome-container {
  width: 100%;
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 1rem;
}

.welcome-info-wrapper {
  width: 100%;
  position: relative;
  padding-top: 45%;
  margin-bottom: 1rem;
  border-radius: 12px;
  overflow: hidden;
}

.welcome-info {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  max-height: 550px;
  height: 100%;
}

.recommend-spot-container {
  margin-top: 2rem;
}

.recommend-spot-container h1 {
  margin-bottom: 1rem;
  font-size: 1.8rem;
  font-weight: bold;
}

@media screen and (max-width: 768px) {
  .welcome-container {
    padding: 0 0.5rem;
  }

  .welcome-info-wrapper {
    padding-top: 60%; /* 5:3 비율 */
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
    padding-top: 75%; /* 4:3 비율 */
  }

  .recommend-spot-container h1 {
    font-size: 1.2rem;
    margin-bottom: 0.6rem;
  }
}
</style>
