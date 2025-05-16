<script setup lang="ts"></script>

<template>
  <a-carousel autoplay>
    <div>
      <img
        src="https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrg"
        alt="여행지 1"
        class="carousel-image"
      />
    </div>
    <div>
      <img
        src="https://images.pexels.com/photos/189833/pexels-photo-189833.jpeg?auto=compress&cs=tinysrgb"
        alt="여행지 1"
        class="carousel-image"
      />
    </div>
    <div>
      <img
        src="https://images.pexels.com/photos/31992566/pexels-photo-31992566.jpeg?auto=compress&cs=tinysrgb"
        alt="여행지 1"
        class="carousel-image"
      />
    </div>
    <div>
      <img
        src="https://images.pexels.com/photos/691668/pexels-photo-691668.jpeg?auto=compress&cs=tinysrgb"
        alt="여행지 1"
        class="carousel-image"
      />
    </div>
  </a-carousel>
</template>

<style scoped>
/* 상단 오토 스크롤 이미지 슬라이드 스타일 */
:deep(.slick-slide) {
  text-align: center;
  height: 50vh;
  background: #f3f3f3;
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.ant-carousel) {
  overflow: hidden;
}

.carousel-image {
  max-height: 100%;
  max-width: 100%;
  object-fit: fill;
}
</style>
