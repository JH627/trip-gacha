<template>
  <div>
    <a-modal
      :visible="true"
      title=""
      centered
      :footer="null"
      :maskClosable="false"
      @cancel="$emit('close-modal')"
    >
      <div class="modal-header">
        <h2 class="modal-title">방 만들기</h2>
      </div>

      <a-form layout="vertical" @submit.prevent>
        <a-form-item label="제목">
          <a-input v-model:value="form.title" placeholder="방 제목을 입력하세요" />
        </a-form-item>
        <a-form-item label="비밀번호">
          <a-input v-model:value="form.password" type="text" placeholder="비밀번호 (선택)" />
        </a-form-item>
        <a-form-item label="목적지">
          <a-input v-model:value="form.destination" placeholder="여행 목적지를 입력하세요" />
        </a-form-item>
        <a-form-item label="여행 기간">
          <a-range-picker v-model:value="form.dateRange" style="width: 100%" :format="dateFormat" />
        </a-form-item>
        <div class="modal-footer">
          <a-button type="default" @click="$emit('close-modal')">취소</a-button>
          <a-button type="primary" @click="handleSubmit">생성</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue'
import type { Dayjs } from 'dayjs'
import type { CreateRoomRequest } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRouter } from 'vue-router'

const emit = defineEmits(['close-modal'])

const socketStore = useSocketStore()
const authStore = useAuthStore()

const form = ref<{
  title: string
  password: string
  destination: string
  dateRange: [Dayjs, Dayjs] | []
}>({
  title: '',
  password: '',
  destination: '',
  dateRange: [],
})

const dateFormat = 'YYYY-MM-DD'

const handleSubmit = () => {
  if (form.value.dateRange.length === 0) {
    return
  }

  const createRoomRequest: CreateRoomRequest = {
    title: form.value.title,
    password: form.value.password,
    destination: form.value.destination,
    startDate: form.value.dateRange[0]?.format(dateFormat),
    endDate: form.value.dateRange[1]?.format(dateFormat),
  }

  socketStore.send('/app/room/create', authStore.accessToken || '', createRoomRequest)
  emit('close-modal')
}
</script>

<style scoped>
.modal-header {
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
}

.modal-title {
  font-weight: bold;
  font-size: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 1rem;
}
</style>
