import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSpotStore = defineStore('spot', () => {
  const selectedSpotIds = ref<Set<number>>(new Set())

  const addSpotIds = (addedSpotIds: number[]) => {
    console.log(addedSpotIds)
    addedSpotIds.forEach((id) => selectedSpotIds.value.add(id))
  }

  const removeSpotIds = (removedSpotId: number[]) => {
    removedSpotId.forEach((id) => selectedSpotIds.value.delete(id))
  }

  const getSpotIdsArray = () => {
    return Array.from(selectedSpotIds.value)
  }

  return {
    selectedSpotIds,
    addSpotIds,
    removeSpotIds,
    getSpotIdsArray,
  }
})
