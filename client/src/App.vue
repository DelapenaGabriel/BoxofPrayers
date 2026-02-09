<script>
import GlobalBackground from './components/HomeComponents/GlobalBackground.vue'
import BottomTabBar from './components/shared/BottomTabBar.vue'
import IntroOverlay from './components/IntroOverlay.vue'
import RequestBottomSheet from './components/shared/RequestBottomSheet.vue' // New Component
import { useRoute, useRouter } from 'vue-router'
import { computed, ref, watch } from 'vue'
import { useSanctuaryStore } from '@/store/sanctuaryStore'
import { useAuthStore } from '@/store/authStore'
import ToastNotification from './components/shared/ToastNotification.vue'

export default {
  name: 'App',
  components: {
    GlobalBackground,
    BottomTabBar,
    IntroOverlay,
    RequestBottomSheet,
    ToastNotification,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const isRequestSheetOpen = ref(false)

    // Sync Route with Modal
    watch(
      () => route.path,
      (newPath) => {
        if (newPath === '/request') {
          isRequestSheetOpen.value = true
        } else {
          // If we leave /request, close the sheet (optional, but good for back button)
          if (isRequestSheetOpen.value && newPath !== '/request') {
            isRequestSheetOpen.value = false
          }
        }
      },
    )

    // Also handle initial load
    if (route.path === '/request') {
      isRequestSheetOpen.value = true
    }

    // Hide nav on login, register, intro, OR IMMERSIVE MODE
    const sanctuaryStore = useSanctuaryStore()
    const showNav = computed(() => {
      if (sanctuaryStore.isImmersive) return false
      const hiddenRoutes = ['login', 'register', 'intro']
      return !hiddenRoutes.includes(route.name)
    })

    const handleFabClick = () => {
      isRequestSheetOpen.value = true
      // Optional: push route?
      // router.push('/request')
    }

    const closeRequestSheet = () => {
      isRequestSheetOpen.value = false
      // If we are currently on the request route, go back to home
      if (route.path === '/request') {
        router.push('/')
      }
    }

    return { handleFabClick, showNav, isRequestSheetOpen, closeRequestSheet }
  },
}
</script>

<template>
  <div class="app-container">
    <IntroOverlay />
    <GlobalBackground />

    <!-- SCROLLABLE CONTENT AREA -->
    <main class="main-content" :class="{ 'with-nav': showNav }">
      <RouterView v-slot="{ Component }">
        <Transition name="fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>

    <!-- FIXED NAVIGATION -->
    <BottomTabBar v-if="showNav" @fab-click="handleFabClick" />

    <!-- MODALS -->
    <RequestBottomSheet :isOpen="isRequestSheetOpen" @close="closeRequestSheet" />
    <ToastNotification />
  </div>
</template>

<style scoped>
.app-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden; /* Prevent body scroll */
}

.main-content {
  flex: 1;
  overflow-y: auto; /* Enable vertical scrolling */
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch; /* Smooth scrolling on iOS */
  position: relative;
  z-index: 1;
}

/* Add padding to the bottom of the content to account for the fixed nav bar */
.main-content.with-nav {
  padding-bottom: calc(var(--tab-bar-height) + var(--safe-area-bottom) + 20px);
}
</style>
