<script setup lang="ts">
import MyPageProfileInfo from '@/components/user/MyPageProfileInfo.vue'
import MyPageActivityInfo from '@/components/user/MyPageActivityInfo.vue'
import MyPageTripList from '@/components/user/MyPageTripList.vue'
import MyPageWishList from '@/components/user/MyPageWishList.vue'
</script>

<template>
  <div class="mypage-container">
    <h1 class="mypage-title">내 프로필</h1>
    <div class="mypage-grid">
      <!-- 프로필 정보 -->
      <div class="profile-card">
        <MyPageProfileInfo />
      </div>
      <!-- 최근 활동 -->
      <div class="activity-card">
        <MyPageActivityInfo />
      </div>
      <!-- 내 여행일정 -->
      <div class="trips-card">
        <MyPageTripList />
      </div>
      <!-- 찜한 관광지 -->
      <div class="wish-card">
        <MyPageWishList />
      </div>
    </div>
  </div>
</template>

<style scoped>
.mypage-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 0;
}
.mypage-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 24px;
  padding-left: 8px;
}
.mypage-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-auto-rows: min-content;
  gap: 24px;
}
.profile-card,
.activity-card,
.stats-card,
.trips-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 32px 28px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.profile-card {
  align-items: center;
}
.withdraw-btn {
  background: #fff1f0;
  color: #ff4d4f;
  border: none;
  border-radius: 8px;
  padding: 8px 0;
  width: 100%;
  cursor: pointer;
  font-weight: bold;
}
.stats-card {
  justify-content: center;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}
.stat-box {
  background: #f7f8fa;
  border-radius: 10px;
  padding: 18px 0;
  text-align: center;
}
.stat-num {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.activity-card,
.trips-card {
  min-height: 180px;
}

.profile-card,
.activity-card {
  height: 100%;
}

.wish-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 32px 28px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  min-height: 180px;
}
@media (max-width: 900px) {
  .mypage-grid {
    grid-template-columns: 1fr;
  }
}
</style>
