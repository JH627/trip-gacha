<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { KakaoMap, KakaoMapMarker, KakaoMapPolyline } from 'vue3-kakao-maps'

interface Coordinate {
  lat: number
  lng: number
}

interface Props {
  coordinates: Coordinate[]
}

const props = defineProps<Props>()

const defaultCoordinate = {
  lat: 33.450701,
  lng: 126.570667,
}

const map = ref<kakao.maps.Map>()

const mapOptions = {
  scrollwheel: true,
  disableDoubleClickZoom: false,
  disableDoubleTapZoom: false,
  disableTwoFingerTapZoom: false,
  draggable: true,
  keyboardShortcuts: true,
  zoomable: true,
  center: defaultCoordinate,
  level: 3
}

const onLoadKakaoMap = (mapRef: kakao.maps.Map) => {
  map.value = mapRef
  if (props.coordinates.length > 0) {
    setBounds()
  }
}

const setBounds = () => {
  if (!map.value || props.coordinates.length === 0) return
  
  const bounds = new kakao.maps.LatLngBounds()
  props.coordinates.forEach(coord => {
    if (coord.lat && coord.lng) {
      bounds.extend(new kakao.maps.LatLng(coord.lat, coord.lng))
    }
  })
  
  if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
    const singlePoint = bounds.getNorthEast()
    map.value.setCenter(singlePoint)
    map.value.setLevel(3)
  } else {
    map.value.setBounds(bounds)
  }
}

watch(() => props.coordinates, () => {
  nextTick(() => {
    if (map.value) {
      setBounds()
    }
  })
}, { deep: true })
</script>

<template>
  <div class="map-wrapper">
    <KakaoMap
      :lat="coordinates[0]?.lat || defaultCoordinate.lat"
      :lng="coordinates[0]?.lng || defaultCoordinate.lng"
      :draggable="true"
      :zoom="3"
      :options="mapOptions"
      class="kakao-map"
      @onLoadKakaoMap="onLoadKakaoMap"
      :style="{ width: '100%', height: '100%' }"
    >
      <KakaoMapMarker 
        v-for="(coord, index) in coordinates" 
        :key="index"
        :lat="coord.lat" 
        :lng="coord.lng"
      />
      <KakaoMapPolyline
        v-if="coordinates.length > 1"
        :latLngList="coordinates"
        :strokeWeight="5"
        strokeColor="#FF0000"
        :strokeOpacity="0.7"
      />
    </KakaoMap>
  </div>
</template>

<style scoped>
.map-wrapper {
  width: 100%;
  height: 500px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.kakao-map {
  width: 100%;
  height: 100%;
}
</style> 