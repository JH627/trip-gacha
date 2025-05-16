<script setup lang="ts">
interface Destination {
  id: number
  name: string
  description: string
  img: string
}

defineProps<{
  destination: Destination
}>()
</script>

<template>
  <div class="spot-card">
    <div class="spot-image">
      <img :src="destination.img" :alt="destination.name" />
    </div>
    <div class="spot-info">
      <h3>{{ destination.name }}</h3>
      <p>{{ destination.description }}</p>
    </div>
  </div>
</template>

<style scoped>
.spot-card {
  flex: 0 0 300px;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.spot-card:hover {
  transform: translateY(-4px);
  background: #9ea3c2;
}

.spot-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.spot-card:hover .spot-image img {
  transform: scale(1.05);
}

.spot-info {
  padding: 1rem;
}

.spot-info h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.spot-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
