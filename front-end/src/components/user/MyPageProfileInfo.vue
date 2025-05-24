<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { computed, ref } from 'vue'
import { UserOutlined, EditOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import { authApi } from '@/api/axios'
import { message } from 'ant-design-vue'

const authStore = useAuthStore()
const userInfo = computed(() => authStore.profile)
const isEditing = ref(false)
const editedNickname = ref('')
const editedImage = ref<File | null>(null)
const previewImage = ref('')

const startEditing = () => {
  editedNickname.value = userInfo.value?.nickname || ''
  previewImage.value = userInfo.value?.profileImg || ''
  isEditing.value = true
}

const cancelEditing = () => {
  isEditing.value = false
  editedImage.value = null
  previewImage.value = userInfo.value?.profileImg || ''
}

// 프로필 이미지 수정
const handleImageChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    editedImage.value = target.files[0]
    previewImage.value = URL.createObjectURL(target.files[0])
  }
}

const saveProfile = async () => {
  try {
    const formData = new FormData()
    formData.append('nickname', editedNickname.value)
    if (editedImage.value) {
      formData.append('profileImg', editedImage.value)
    }

    await authApi.put('/user', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    await authStore.getProfile(true)
    message.success('프로필이 수정되었습니다')
    isEditing.value = false
  } catch (error) {
    console.error('프로필 수정 실패:', error)
    message.error('프로필 수정에 실패했습니다')
  }
}
</script>

<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>프로필 정보</h2>
      <!-- 확인 모드 -->
      <div class="edit-buttons">
        <!-- 수정 버튼 -->
        <template v-if="!isEditing">
          <a-button type="primary" @click="startEditing">
            <template #icon><EditOutlined /></template>
            수정
          </a-button>
        </template>
        <!-- 수정 모드 -->
        <template v-else>
          <!-- 저장 버튼 -->
          <a-button type="primary" @click="saveProfile">
            <template #icon><CheckOutlined /></template>
            저장
          </a-button>
          <!-- 취소 버튼 -->
          <a-button @click="cancelEditing">
            <template #icon><CloseOutlined /></template>
            취소
          </a-button>
        </template>
      </div>
    </div>

    <div class="profile-content">
      <div class="avatar-container">
        <!-- 프로필 이미지 -->
        <a-avatar :size="120" :src="previewImage || userInfo?.profileImg" class="profile-avatar">
          <template #icon v-if="!previewImage && !userInfo?.profileImg">
            <UserOutlined />
          </template>
        </a-avatar>
        <!-- 프로필 이미지 수정 -->
        <input
          v-if="isEditing"
          type="file"
          accept="image/*"
          class="image-input"
          @change="handleImageChange"
        />
      </div>

      <div class="profile-info">
        <div class="info-item">
          <b>닉네임</b>
          <template v-if="!isEditing">
            {{ userInfo?.nickname }}
          </template>
          <template v-else>
            <a-input v-model:value="editedNickname" placeholder="닉네임을 입력하세요" />
          </template>
        </div>
        <div class="info-item">
          <b>이메일</b>
          <span>{{ userInfo?.email }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  width: 100%;
  padding: 0 4px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 4px;
}

.profile-header h2 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: row;
  height: 100%;
  align-items: center;
  justify-content: center;
  gap: 70px;
}

.avatar-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.profile-avatar {
  background-color: #87d068 !important;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.profile-avatar:hover {
  transform: scale(1.05);
}

.image-input {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0;
  width: 120px;
  height: 120px;
  cursor: pointer;
  border-radius: 50%;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  max-width: 300px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 1rem;
  padding: 20px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.info-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-item b {
  min-width: 70px;
  color: #4a4a4a;
  font-weight: 600;
}

.info-item span {
  color: #1a1a1a;
}

:deep(.ant-input) {
  border-radius: 6px;
}

.edit-buttons {
  display: flex;
  gap: 8px;
}

:deep(.ant-btn) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 32px;
  padding: 0 16px;
}

:deep(.ant-btn-primary) {
  background-color: #228be6;
}

:deep(.ant-btn-primary:hover) {
  background-color: #74c0fc;
}
</style>
