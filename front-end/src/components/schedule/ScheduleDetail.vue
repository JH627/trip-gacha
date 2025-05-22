<script setup lang="ts">
import { Tabs, Button } from 'ant-design-vue'
</script>

<template>
  <div class="tab-container">
    <Tabs v-model:activeKey="selectedCategory" @change="handleCategoryChange" class="category-tabs">
      <template #rightExtra>
        <Button type="primary" @click="emit('write-click')">글쓰기</Button>
      </template>
      <Tabs.TabPane key="POPULAR" tab="인기" />
      <Tabs.TabPane key="FREE" tab="자유" />
      <Tabs.TabPane key="IDEA" tab="건의" />
    </Tabs>
  </div>
</template>

<style scoped></style>
