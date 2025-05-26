<template>
  <div>
    <a-modal
      :visible="visible"
      :title="title"
      centered
      :footer="null"
      :maskClosable="false"
      @cancel="$emit('close')"
      :width="modalWidth"
      :bodyStyle="{
        height: modalHeight,
        padding: '0',
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
      }"
    >
      <div class="modal-header" v-if="$slots.header || modalTitle">
        <slot name="header">
          <h2 class="modal-title">{{ modalTitle }}</h2>
        </slot>
      </div>

      <div class="modal-content">
        <slot></slot>
      </div>

      <div class="modal-footer" v-if="$slots.footer">
        <slot name="footer"></slot>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  modalTitle: {
    type: String,
    default: '',
  },
  width: {
    type: [Number, String],
    default: '500px',
  },
})

const emit = defineEmits(['close'])

const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1024)
const windowHeight = ref(typeof window !== 'undefined' ? window.innerHeight : 768)

onMounted(() => {
  const handleResize = () => {
    windowWidth.value = window.innerWidth
    windowHeight.value = window.innerHeight
  }
  window.addEventListener('resize', handleResize)
  handleResize()

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
  })
})

// 모달 너비 계산 (최대 너비 691.2px, 768이하일 땐 90%)
const modalWidth = computed(() => {
  if (windowWidth.value <= 768) {
    return '90%'
  }
  return '691.2px'
})

// 모달 높이 계산 (최대 700px, 화면 높이 - 100px 이하)
const modalHeight = computed(() => {
  const maxHeight = 700
  const availableHeight = windowHeight.value - 100
  return `${Math.min(availableHeight, maxHeight)}px`
})
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

.modal-content {
  flex: 1;
  overflow: scroll; /* 전체 콘텐츠가 모달 안에서 잘리지 않도록 */
  display: flex;
  flex-direction: column;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 1rem;
}
</style>
