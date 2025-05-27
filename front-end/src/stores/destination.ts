import { authApi } from '@/api/axios'
import type { Destination } from '@/types/trip'
import { defineStore } from 'pinia'
import { ref, type Ref } from 'vue'

export const useDestinationStore = defineStore(
  'destination',
  () => {
    const destinations: Ref<Destination[]> = ref([])
    const destinationOptions = ref<{ label: string; value: number; disabled: boolean }[]>([])
    const selectedDestinationID = ref(0)

    const init = async () => {
      const result = await authApi.get('/trip/destination')
      destinations.value = result.data.result

      const realOptions = destinations.value.map((d: any) => ({
        label: d.name,
        value: d.destinationId,
        disabled: false,
      }))

      destinationOptions.value = [
        { label: '여행지를 골라주세요', value: 0, disabled: true },
        ...realOptions,
      ]
    }

    const selectDestination = (id: number) => {
      selectedDestinationID.value = id
    }

    const getDestinationName = (id: string) => {
      return destinations.value.find(
        (destination) => destination.destinationId === Number.parseInt(id),
      )?.name
    }

    return {
      destinations,
      destinationOptions,
      selectedDestinationID,
      init,
      selectDestination,
      getDestinationName,
    }
  },
  {
    persist: true,
  },
)
