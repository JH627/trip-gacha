<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { authApi } from '@/api/axios'

const activityStats = ref({
  scheduleCount: 0,
  spotCount: 0,
  postCount: 0,
  joinDays: 0,
})

onMounted(async () => {
  try {
    const response = await authApi.get('/user/activity-stats')
    activityStats.value = response.data.result
  } catch (error) {
    console.error('활동 정보를 불러오는데 실패했습니다:', error)
  }
})
</script>

<template>
  <div class="card-header">
    <span>나의 활동</span>
  </div>
  <div class="activity-list">
    <div class="activity-item">
      <div class="activity-icon">📅</div>
      <div class="activity-content">
        <h3>생성한 일정</h3>
        <p>{{ activityStats.scheduleCount }}</p>
      </div>
    </div>
    <div class="activity-item">
      <div class="activity-icon">⭐</div>
      <div class="activity-content">
        <h3>찜한 관광지</h3>
        <p>{{ activityStats.spotCount }}</p>
      </div>
    </div>
    <div class="activity-item">
      <div class="activity-icon">✍️</div>
      <div class="activity-content">
        <h3>작성한 게시글</h3>
        <p>{{ activityStats.postCount }}</p>
      </div>
    </div>
    <div class="activity-item">
      <div class="activity-icon">🎉</div>
      <div class="activity-content">
        <h3>함께한 일수</h3>
        <p>{{ activityStats.joinDays }}일</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 4px;
}

.card-header span {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
}

.activity-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 4px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.activity-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-icon {
  font-size: 2rem;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.activity-item:hover .activity-icon {
  transform: scale(1.1);
  background: #e9ecef;
}

.activity-content {
  flex: 1;
}

.activity-content h3 {
  font-size: 0.95rem;
  font-weight: 600;
  color: #4a4a4a;
  margin: 0 0 4px 0;
}

.activity-content p {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.2;
}

@media (max-width: 768px) {
  .activity-list {
    grid-template-columns: 1fr;
  }
}
</style>
