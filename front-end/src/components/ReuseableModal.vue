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
      :wrapClassName="'custom-modal-wrapper'"
      :bodyStyle="{
        padding: '1rem',
        height: 'calc(100vh - 100px)',
        overflow: 'hidden',
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
import { computed } from 'vue'

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

// 화면 크기에 따라 모달 너비 조정
const modalWidth = computed(() => {
  // 모바일 화면에서는 90%로 설정
  if (typeof window !== 'undefined' && window.innerWidth < 768) {
    return '90%'
  }
  return props.width
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
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  width: 100%;
  max-height: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 1rem;
}

::v-deep(.custom-modal-wrapper .ant-modal-content) {
  padding-top: 20px;
  padding-bottom: 20px;
}
</style>
