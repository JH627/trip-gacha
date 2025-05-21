<script setup lang="ts"></script>

<template>
  <div class="card-header">
    <span>최근 활동</span>
  </div>
  <ul class="activity-list">
    <li>제주도 여행지 추가 <span>2일 전</span></li>
    <li>부산 3박 4일 일정 생성 <span>1주일 전</span></li>
    <li>여행 후기 게시글 작성 <span>2주일 전</span></li>
  </ul>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: bold;
}
.activity-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.activity-list li {
  background: #f7f8fa;
  border-radius: 6px;
  padding: 10px 14px;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  font-size: 1rem;
}
</style>
