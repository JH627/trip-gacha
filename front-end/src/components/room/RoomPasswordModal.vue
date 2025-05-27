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
        <h2 class="modal-title">방 입장</h2>
      </div>

      <a-form layout="vertical" @submit.prevent>
        <a-form-item label="비밀번호">
          <a-input v-model:value="form.password" type="text" placeholder="비밀번호를 입력하세요" />
        </a-form-item>
        <div class="modal-footer">
          <a-button type="default" @click="$emit('close-modal')">취소</a-button>
          <a-button type="primary" @click="handleSubmit">입장</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue'
import type { Dayjs } from 'dayjs'
import type { CreateRoomRequest, JoinRoomRequest } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const emit = defineEmits(['close-modal'])
const props = defineProps({
  roomId: String,
})

const socketStore = useSocketStore()
const authStore = useAuthStore()

const form = ref<{
  password: string
}>({
  password: '',
})

const handleSubmit = () => {
  if (form.value.password.length === 0) {
    message.error('비밀번호를 입력해주세요!')
    return
  }

  const joinRoomRequest: JoinRoomRequest = {
    password: form.value.password,
  }

  socketStore.send('/app/room/join/' + props.roomId, authStore.accessToken || '', joinRoomRequest)
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
