<script setup lang="ts">
import { ref } from 'vue'
import { Input, Select, Button } from 'ant-design-vue'

const searchType = ref('title')
const searchKeyword = ref('')

const emit = defineEmits(['search'])

// 검색 처리
const handleSearch = () => {
  emit('search', searchType.value, searchKeyword.value.trim())
}
</script>

<template>
  <div class="search-container">
    <!-- 검색 타입 선택 -->
    <Select v-model:value="searchType" class="search-type">
      <Select.Option value="title">제목</Select.Option>
      <Select.Option value="content">내용</Select.Option>
      <Select.Option value="authorName">작성자</Select.Option>
    </Select>
    <!-- 검색어 입력 -->
    <Input
      v-model:value="searchKeyword"
      placeholder="검색어를 입력하세요"
      class="search-input"
      @pressEnter="handleSearch"
    />
    <Button type="primary" @click="handleSearch">검색</Button>
  </div>
</template>

<style scoped>
/* 검색 컨테이너 */
.search-container {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

/* 검색 타입 선택 */
.search-type {
  width: 120px;
}

/* 검색어 입력 */
.search-input {
  width: 300px;
}

@media screen and (max-width: 768px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-type,
  .search-input {
    width: 100%;
  }

  .search-container :deep(.ant-btn) {
    width: 100%;
  }
}
</style>
