<script setup lang="ts">
import { useRouter } from 'vue-router'
import { authApi } from '@/api/axios'
import { onMounted, ref } from 'vue'

const router = useRouter()
const wishList = ref<any[]>([])

onMounted(async () => {
  await authApi
    .get('/trip/spot?category=MARKED')
    .then((res) => {
      wishList.value = res.data.result.spots.slice(0, 6)
    })
    .catch((err) => {
      console.log(err)
    })
})
</script>

<template>
  <div class="card-header">
    <span>찜한 관광지</span>
    <a @click="router.push('/spot?type=MARKED')">모두 보기</a>
  </div>
  <div class="wish-list">
    <div class="wish-box" v-for="item in wishList" :key="item.id">
      <div class="wish-box-img">
        <img :src="item.img" />
      </div>
      <p>{{ item.name }}</p>
    </div>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 4px;
}

.card-header span {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
}

.card-header a {
  cursor: pointer;
  color: #228be6;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 6px;
}

.card-header a:hover {
  color: #74c0fc;
  background: rgba(34, 139, 230, 0.1);
}

.wish-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 4px;
}

.wish-box {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  cursor: pointer;
}

.wish-box:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.wish-box-img {
  width: 100%;
  height: 140px;
  overflow: hidden;
}

.wish-box-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.wish-box:hover .wish-box-img img {
  transform: scale(1.05);
}

.wish-box p {
  margin: 0;
  padding: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  color: #1a1a1a;
  text-align: left;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
