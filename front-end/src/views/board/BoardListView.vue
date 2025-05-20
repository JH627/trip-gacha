<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { SearchParams } from '@/types/board'

import BoardList from '@/components/board/BoardList.vue'
import BoardSearch from '@/components/board/BoardSearch.vue'

const router = useRouter()

// 검색 타입, 키워드
const searchParams = ref<SearchParams>({
  type: 'title',
  keyword: '',
})
</script>

<template>
  <div class="board-container">
    <div class="board-content">
      <!-- 게시글 리스트 -->
      <div class="board-list">
        <BoardList
          :search-type="searchParams.type"
          :search-keyword="searchParams.keyword"
          @board-click="(boardId) => router.push(`/board/${boardId}`)"
          @write-click="() => router.push('/board/write')"
        />
      </div>
      <!-- 검색 입력 창-->
      <div class="board-search">
        <BoardSearch @search="(type, keyword) => (searchParams = { type, keyword })" />
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 게시판 컨테이너 */
.board-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

/* 게시판 컨텐츠 */
.board-content {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

/* 검색 입력 창 */
.board-search {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

/* 게시글 리스트 */
.board-list {
  margin-top: 0;
}
</style>
