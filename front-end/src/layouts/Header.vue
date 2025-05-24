<script setup lang="ts">
import { useRouter } from 'vue-router'
import logoImage from '@/assets/logo.jpg'
import { UserOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ref, onMounted, watch } from 'vue'
import type { Profile } from '@/types/auth'

const router = useRouter()
const authStore = useAuthStore()
const isLoggedIn = ref(false)
const userInfo = ref<Profile | null>(authStore.profile)

// 모바일 메뉴 표시 여부
const drawerVisible = ref(false)
// 모바일 여부 확인
const isMobile = ref(window.innerWidth <= 768)

// 모바일 메뉴 표시
const showDrawer = () => {
  if (isMobile.value) drawerVisible.value = true
}

// 모바일 메뉴 닫기
const closeDrawer = () => {
  drawerVisible.value = false
}

// 메뉴 클릭 시 이동
const handleMenuClick = (path: string) => {
  router.push(path)
  closeDrawer()
}

// 로그아웃 시 실행
const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
  if (isMobile.value) {
    closeDrawer()
  }
}

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  isLoggedIn.value = await authStore.checkLogin()
  window.addEventListener('resize', () => {
    isMobile.value = window.innerWidth <= 768
  })
})

// accessToken, profile 변경 감지
watch(
  () => authStore.accessToken,
  (newToken) => {
    isLoggedIn.value = !!newToken
  },
  { immediate: true },
)
watch(
  () => authStore.profile,
  (newProfile) => {
    userInfo.value = newProfile
  },
  { immediate: true },
)
</script>

<template>
  <div class="header-content">
    <div class="header-main-content">
      <!-- 로고, 링크들 -->
      <div class="logo-container">
        <img :src="logoImage" alt="로고" class="logo" @click="router.push('/')" />
        <router-link to="/trip/lobby" class="board-link">여행 시작</router-link>
        <router-link to="/spot" class="board-link">관광지 둘러보기</router-link>
        <router-link to="/board" class="board-link">게시판</router-link>
        <router-link to="/game" class="board-link">게임</router-link>
      </div>
      <div>
        <!-- 로그인 상태 확인: 로그인 시 -->
        <template v-if="isLoggedIn">
          <span>{{ userInfo?.nickname }}님 안녕하세요 &nbsp;</span>
          <!-- 데스크톱: hover dropdown -->
          <a-dropdown v-if="!isMobile" trigger="hover" placement="bottomRight">
            <!-- 프로필 이미지 -->
            <a-avatar :src="userInfo?.profileImg" class="profile-avatar">
              <template #icon v-if="!userInfo?.profileImg">
                <UserOutlined />
              </template>
            </a-avatar>
            <!-- 드롭다운 메뉴 -->
            <template #overlay>
              <a-menu>
                <a-menu-item @click="router.push('/mypage')">마이페이지</a-menu-item>
                <a-menu-item @click="handleLogout" class="logout-menu">로그아웃</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
          <!-- 모바일: 클릭 시 Drawer 표시 -->
          <a-avatar v-else :src="userInfo?.profileImg" class="profile-avatar" @click="showDrawer">
            <!-- 프로필 이미지 -->
            <template #icon v-if="!userInfo?.profileImg">
              <UserOutlined />
            </template>
          </a-avatar>
        </template>
        <!-- 로그인 상태 확인: 비 로그인 시 -->
        <template v-else>
          <a-button @click="router.push('/login')" class="login-btn">로그인</a-button>
        </template>
      </div>
    </div>
  </div>

  <!-- 모바일 메뉴 Drawer -->
  <a-drawer placement="right" :visible="drawerVisible" @close="closeDrawer" class="mobile-drawer">
    <!-- 모바일 메뉴 컨테이너 -->
    <div class="drawer-content">
      <div class="drawer-menu-item" @click="handleMenuClick('/trip/lobby')">여행 시작</div>
      <div class="drawer-menu-item" @click="handleMenuClick('/spot')">관광지 둘러보기</div>
      <div class="drawer-menu-item" @click="handleMenuClick('/board')">게시판</div>
      <div class="drawer-menu-item" @click="handleMenuClick('/mypage')">마이페이지</div>
      <div class="drawer-menu-item logout" @click="handleLogout">로그아웃</div>
    </div>
  </a-drawer>
</template>

<style scoped>
/* 헤더 컨테이너 */
.header-content {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 40px;
  width: 100%;
}

/* 헤더 메인 컨테이너 */
.header-main-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1500px;
  padding: 0 10px;
}

/* 로고 컨테이너 */
.logo-container {
  display: flex;
  align-items: center;
  gap: 40px;
}

/* 로고 이미지 */
.logo {
  height: 70px;
  cursor: pointer;
  object-fit: contain;
}

/* 게시판 링크 */
.board-link {
  color: black;
  text-decoration: none;
  font-size: 16px;
}
.board-link:hover {
  color: #666;
}

/* 로그인 버튼 */
.login-btn {
  color: white;
  background-color: black;
}

/* 프로필 이미지 */
.profile-avatar {
  background-color: #87d068 !important;
  cursor: pointer;
}

/* 모바일 메뉴 Drawer */
.mobile-drawer {
  display: none;
}

/* 모바일 메뉴 아이템 */
.drawer-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  height: 100%;
}
.drawer-menu-item {
  font-size: 18px;
  cursor: pointer;
  padding: 10px 0;
}
.drawer-menu-item:hover {
  color: #666;
}

/* 로그아웃 메뉴 */
.drawer-menu-item.logout,
.logout-menu {
  margin-top: auto;
  color: #ff4d4f !important;
}
.drawer-menu-item.logout:hover,
.logout-menu:hover {
  color: #ff7875 !important;
}

@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  .header-main-content {
    min-width: auto;
    padding: 0;
  }
  .logo {
    height: 50px;
  }
  .board-link {
    display: none;
  }
  .mobile-drawer {
    display: block;
  }
}

@media screen and (min-width: 769px) {
  .mobile-drawer {
    display: none;
  }
}
</style>
