<template>
  <div class="card-header">
    <span>내 여행일정</span>
    <a @click="router.push('/trip')">모두 보기</a>
  </div>
  <div class="trips-list">
    <div class="trip-box">제주도</div>
    <div class="trip-box">부산</div>
    <div class="trip-box">강릉</div>
    <div class="trip-box">여수</div>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: bold;
}
.trips-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.trip-box {
  background: #f7f8fa;
  border-radius: 10px;
  padding: 18px 24px;
  min-width: 90px;
  text-align: center;
  font-weight: bold;
}

.card-header a {
  cursor: pointer;
  color: blue;
}

.card-header a:hover {
  color: #6da9ec;
}
</style>
