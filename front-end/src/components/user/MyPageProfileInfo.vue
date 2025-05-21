<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'

const authStore = useAuthStore()

const userInfo = computed(() => authStore.profile)
</script>

<template>
  <div class="profile-container">
    <div class="avatar-container">
      <a-avatar :size="120" :src="userInfo?.profileImg" class="profile-avatar">
        <template #icon v-if="!userInfo?.profileImg">
          <UserOutlined />
        </template>
      </a-avatar>
    </div>
    <div class="profile-info">
      <div class="info-item"><b>닉네임</b> {{ userInfo?.nickname }}</div>
      <div class="info-item"><b>이메일</b> {{ userInfo?.email }}</div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.profile-avatar {
  background-color: #87d068 !important;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  max-width: 300px;
}

.info-item {
  display: flex;
  gap: 12px;
  font-size: 16px;
}

.info-item b {
  min-width: 60px;
  color: #666;
}
</style>
