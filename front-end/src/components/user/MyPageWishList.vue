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
      wishList.value = res.data.result
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
  margin-bottom: 30px;
  font-weight: bold;
}

.card-header a {
  cursor: pointer;
  color: blue;
}

.card-header a:hover {
  color: #6da9ec;
}

.wish-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.wish-box {
  width: 190px;
  height: 150px;
  text-align: center;
  font-weight: bold;
}

.wish-box-img {
  background: #f7f8fa;
  border-radius: 10px;
  width: 190px;
  height: 100px;
  border-radius: 10px;
  overflow: hidden;
}

.wish-box-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}
</style>
