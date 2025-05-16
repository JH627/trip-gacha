<script setup lang="ts">
import Header from './Header.vue'
import Footer from './Footer.vue'
</script>

<template>
  <div class="default-layout">
    <div class="main-header">
      <Header />
    </div>
    <main class="main-content">
      <router-view />
    </main>
    <div class="main-footer">
      <Footer />
    </div>
  </div>
</template>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f0f0;
}

.main-header {
  height: 70px;
  width: 100%;
  background-color: white;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.main-content {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 1500px;
  margin: 90px auto 0;
  padding: 0 20px;
  margin-bottom: 10px;
}

.main-footer {
  position: relative;
  min-height: 70px;
  background-color: white;
  margin-top: auto;
}

@media screen and (max-width: 768px) {
  .main-content {
    margin-top: 80px;
    padding: 0 10px;
  }
}
</style>
