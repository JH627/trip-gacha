<template></template>

<script lang="ts" setup>
import { GameProgress } from '@/components/game/Game.ts'
import type { SocketRoomUser } from '@/socket/webSocket'
import { useAuthStore } from '@/stores/auth'
import { useSocketStore } from '@/stores/socket'
import { onMounted, ref, type Ref } from 'vue'

// 소켓 게임의 기본 틀

const props = defineProps<{
  initProgress: GameProgress
  gameId: string
}>()

const gameResult: Ref<{ userId: string; nickname: string; img: string; clickTime: number }[]> = ref(
  [],
)

const myClickTime = ref(999)

const socketStore = useSocketStore()
const authStore = useAuthStore()

const currentProgress = ref(props.initProgress)

const processGameOperation = (body: string) => {
  const res = JSON.parse(body)

  switch (res.progress) {
    case GameProgress.PLAY_GAME:
      currentProgress.value = res.progress
      return
    case GameProgress.RESULT:
      res.gameResult.value.push(res)
      currentProgress.value = res.progress
  }
}

const startGame = () => {
  socketStore.send(
    `/topic/game/fast-click/start/` + props.gameId,
    authStore.accessToken || '',
    null,
  )
}

const sendResult = () => {
  socketStore.send(
    `/topic/game/fast-click/update/` + props.gameId,
    authStore.accessToken || '',
    myClickTime,
  )
}

onMounted(() => {
  socketStore.subscribe(`/topic/game/fast-click/` + props.gameId, processGameOperation)
})
</script>

<style></style>
