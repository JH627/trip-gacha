<template>
  <div>{{ planTitle }}</div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const planTitle = computed(() => route.params.planId as string)
</script>

<style scoped></style>
