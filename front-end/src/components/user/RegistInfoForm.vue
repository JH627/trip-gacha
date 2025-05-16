<template>
  <div class="regist-form">
    <h1 class="form-title">회원가입</h1>

    <div class="form-group form-img-group">
      <label>프로필 이미지</label>
      <a-upload
        :file-list="fileList"
        name="avatar"
        list-type="picture-card"
        class="avatar-uploader"
        :before-upload="beforeUpload"
        :show-upload-list="false"
      >
        <img v-if="imageUrl" :src="imageUrl" alt="avatar" />
        <div v-else>
          <LoadingOutlined v-if="loading" />
          <PlusOutlined v-else />
          <div class="ant-upload-text">Upload</div>
        </div>
      </a-upload>
    </div>

    <div class="form-group">
      <label for="password">비밀번호</label>
      <input
        type="password"
        id="password"
        v-model="password"
        placeholder="비밀번호를 입력하세요"
        required
      />
    </div>

    <div class="form-group">
      <label for="passwordConfirm">비밀번호 확인</label>
      <input
        type="password"
        id="passwordConfirm"
        v-model="passwordConfirm"
        placeholder="비밀번호를 다시 입력하세요"
        required
      />
    </div>

    <div class="form-group">
      <label for="nickname">닉네임</label>
      <input
        type="text"
        id="nickname"
        v-model="nickname"
        placeholder="닉네임을 입력하세요"
        required
      />
    </div>

    <div class="button-group">
      <button type="button" class="next-btn" @click="onSubmit" :disabled="!isFormValid">
        다음
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons-vue'

const password = ref('')
const passwordConfirm = ref('')
const nickname = ref('')
const fileList = ref<any[]>([])
const loading = ref(false)
const imageUrl = ref('')

const emit = defineEmits<{
  (
    e: 'next',
    payload: {
      password: string
      nickname: string
      profileImg: File | null
    },
  ): void
}>()

const isFormValid = computed(() => {
  return (
    password.value &&
    passwordConfirm.value &&
    password.value === passwordConfirm.value &&
    nickname.value
  )
})

function getBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = (err) => reject(err)
  })
}

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('JPG/PNG 파일만 업로드 가능합니다!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isLt2M) {
    message.error('이미지는 10MB보다 작아야 합니다!')
    return false
  }

  loading.value = true
  getBase64(file)
    .then((dataUrl) => {
      fileList.value = [
        {
          uid: file.uid || Date.now().toString(),
          name: file.name,
          status: 'done',
          url: dataUrl,
          originFileObj: file,
        },
      ]
      imageUrl.value = dataUrl
    })
    .catch(() => {
      message.error('이미지 처리 중 오류가 발생했습니다.')
    })
    .finally(() => {
      loading.value = false
    })

  // 자동 업로드 방지
  return false
}

const onSubmit = () => {
  if (!isFormValid.value) return
  emit('next', {
    password: password.value,
    nickname: nickname.value,
    profileImg: fileList.value[0]?.originFileObj || null,
  })
}
</script>

<style scoped>
.form-img-group {
  align-items: center;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-img-group label {
  align-self: center;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
  width: 100%;
}

.avatar-uploader img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-uploader > .ant-upload {
  width: 128px;
  height: 128px;
  margin: 0;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}

.regist-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-title {
  text-align: center;
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-size: 0.9rem;
  color: #666;
  font-weight: bold;
}

input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: #4a90e2;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.next-btn {
  padding: 0.8rem 2rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.next-btn:hover:not(:disabled) {
  background-color: #357abd;
}

.next-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
