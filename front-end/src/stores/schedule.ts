import type { ScheduleDetail } from '@/types/trip'
import { defineStore } from 'pinia'
import { ref, type Ref } from 'vue'

export const useScheduleStore = defineStore('schedule', () => {
  const schedule: Ref<ScheduleDetail | null> = ref(null)

  const saveSchedule = (finalSchedule: ScheduleDetail) => {
    schedule.value = finalSchedule
  }

  return { schedule, saveSchedule }
})
