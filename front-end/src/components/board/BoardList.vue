<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Pagination, Tabs, Button } from 'ant-design-vue'
import type { Key } from 'ant-design-vue/es/table/interface'
import { authApi } from '@/api/axios'
import type { BoardHeader, BoardListProps } from '@/types/board'

const props = defineProps<BoardListProps>()
const emit = defineEmits(['board-click', 'write-click'])

// 카테고리 맵
const categoryMap: { [key: string]: string } = {
  POPULAR: '인기',
  FREE: '자유',
  IDEA: '건의',
}

// 게시글 리스트
const boards = ref<BoardHeader[]>([])
const currentPage = ref(1) // 현재 페이지
const totalPages = ref(1) // 총 페이지 수
const pageSize = ref(5) // 페이지 당 게시글 수
const selectedCategory = ref<string>('POPULAR') // 선택된 카테고리

// 게시글 가져오기
const fetchBoards = () => {
  authApi
    .get('/board/search', {
      params: {
        page: currentPage.value,
        offset: pageSize.value,
        category: selectedCategory.value,
        searchType: props.searchType,
        title: props.searchKeyword || '',
      },
    })
    .then((response) => {
      console.log(response.data.result)
      boards.value = response.data.result.boards
      totalPages.value = Math.ceil(response.data.result.totalCount / pageSize.value)
    })
    .catch((error) => {
      console.error(error)
    })
}

// 페이지 번호 변경
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchBoards()
}

// 페이징 크기 변경
const handlePageSizeChange = (current: number, size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchBoards()
}

// 카테고리 변경
const handleCategoryChange = (key: Key) => {
  selectedCategory.value = key as string
  currentPage.value = 1
  fetchBoards()
}

// 게시글 클릭
const handleBoardClick = (boardId: number) => {
  emit('board-click', boardId)
}

// 검색 타입과 키워드가 변경될 때
// 페이지 번호를 1로 초기화하고 게시글을 다시 가져옴
watch(
  () => [props.searchType, props.searchKeyword],
  () => {
    currentPage.value = 1
    fetchBoards()
  },
)

onMounted(() => {
  fetchBoards()
})
</script>

<template>
  <div class="board-list-container">
    <!-- 게시글 카테고리 -->
    <div class="category-filter">
      <Tabs
        v-model:activeKey="selectedCategory"
        @change="handleCategoryChange"
        class="category-tabs"
      >
        <template #rightExtra>
          <Button type="primary" @click="emit('write-click')">글쓰기</Button>
        </template>
        <Tabs.TabPane key="POPULAR" tab="인기" />
        <Tabs.TabPane key="FREE" tab="자유" />
        <Tabs.TabPane key="IDEA" tab="건의" />
      </Tabs>
    </div>

    <!-- 게시글 리스트 -->
    <div class="board-list">
      <div
        v-for="board in boards"
        class="board-list-card"
        :key="board.boardId"
        @click="handleBoardClick(board.boardId)"
      >
        <!-- 상단 제목 -->
        <div class="card-title">{{ board.title }}</div>
        <!-- 하단 정보 -->
        <div class="card-meta">
          <!-- TODO: 왼쪽에 사용자 이미지?? -->
          <span class="card-category">{{ categoryMap[board.category.toUpperCase()] }}</span>
        </div>
        <div class="card-info">
          <span class="card-writer">{{ board.authorName }}</span>
          <span>{{ new Date(board.createdAt).toLocaleDateString() }}</span>
          <span>조회 {{ board.viewCount }}</span>
          <span>좋아요 {{ board.likeCount }}</span>
          <span>댓글 {{ board.commentCount }}</span>
        </div>
      </div>
    </div>

    <!-- 페이징 -->
    <div class="pagination-container">
      <Pagination
        v-model:current="currentPage"
        :total="totalPages * pageSize"
        :page-size="pageSize"
        :page-size-options="['5', '10']"
        show-size-changer
        @change="handlePageChange"
        @showSizeChange="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<style scoped>
/* 게시글 리스트 컨테이너 */
.board-list-container {
  width: 100%;
}

/* 게시글 리스트 */
.board-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 12px 0;
}

/* 게시글 리스트 카드 */
.board-list-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 16px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.board-list-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

/* 게시글 리스트 카드 제목 */
.card-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #222;
}

/* 게시글 리스트 카드 추가 정보 */
.card-meta {
  font-size: 13px;
  color: #888;
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}
.card-category {
  font-weight: 500;
  color: #1890ff;
}
.card-info {
  font-size: 12px;
  color: #aaa;
  display: flex;
  gap: 12px;
}

/* 게시글 카테고리 필터 */
.category-filter {
  margin-bottom: 16px;
  background-color: #fff;
  padding: 0 16px;
}

/* 게시글 카테고리 탭 */
.category-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}
.category-tabs :deep(.ant-tabs-tab) {
  padding: 12px 16px;
  font-size: 16px;
}
.category-tabs :deep(.ant-tabs-tab-active) {
  font-weight: bold;
}
.category-tabs :deep(.ant-tabs-extra-content) {
  display: flex;
  align-items: center;
}

/* 페이징 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
