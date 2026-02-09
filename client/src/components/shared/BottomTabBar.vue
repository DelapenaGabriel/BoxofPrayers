<template>
  <div class="bottom-tab-bar">
    <!-- GLASS CONTAINER -->
    <div class="tab-glass floating-bar">
      <!-- HOME -->
      <router-link to="/" class="tab-item" active-class="active">
        <div class="icon-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="tab-icon">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
          </svg>
        </div>
        <span class="tab-label">Home</span>
      </router-link>

      <!-- PRAY -->
      <router-link to="/pray" class="tab-item" active-class="active">
        <div class="icon-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="tab-icon">
            <path
              d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
            />
          </svg>
        </div>
        <span class="tab-label">Pray</span>
      </router-link>

      <!-- FAB (CENTER) -->
      <div class="fab-wrapper">
        <button class="fab-btn" @click="$emit('fab-click')">
          <div class="fab-glow"></div>
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            class="fab-icon"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
        </button>
      </div>

      <!-- COMMUNITY -->
      <router-link to="/community" class="tab-item" active-class="active">
        <div class="icon-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="tab-icon">
            <path
              d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"
            />
          </svg>
        </div>
        <span class="tab-label">Community</span>
      </router-link>

      <!-- PROFILE (Dynamic Link) -->
      <router-link :to="profileLink" class="tab-item" active-class="active">
        <div class="icon-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="tab-icon">
            <path
              d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
            />
          </svg>
        </div>
        <span class="tab-label">{{ profileLabel }}</span>
      </router-link>

      <!-- ADMIN (Conditional) -->
      <router-link v-if="isAdmin" to="/admin" class="tab-item" active-class="active">
        <div class="icon-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="tab-icon">
            <path
              d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"
            />
          </svg>
        </div>
        <span class="tab-label">Admin</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useAuthStore } from '@/store/authStore'

export default {
  name: 'BottomTabBar',
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const isAuthenticated = computed(() => !!authStore.token)

    const profileLink = computed(() => (isAuthenticated.value ? '/profile' : '/login'))
    const profileLabel = computed(() => (isAuthenticated.value ? 'Profile' : 'Sign In'))

    const isAdmin = computed(() => {
      if (!authStore.user) return false
      // Check for role or authorities
      return (
        authStore.user.role === 'ROLE_ADMIN' ||
        (authStore.user.authorities &&
          authStore.user.authorities.some((a) => a.name === 'ROLE_ADMIN'))
      )
    })

    return {
      isAuthenticated,
      profileLink,
      profileLabel,
      isAdmin,
    }
  },
}
</script>

<style scoped>
.bottom-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 100000;
  height: auto;
  pointer-events: none; /* Let clicks pass through empty areas */
  padding: 0 var(--space-lg) calc(var(--safe-area-bottom) + var(--space-md)) var(--space-lg);
  display: flex;
  justify-content: center;
}

.tab-glass {
  pointer-events: auto;
  width: 100%;
  max-width: 550px;
  height: 65px;
  background: var(--glass-strong);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 40px; /* Pill shape */
  display: flex;
  align-items: center;
  justify-content: space-around;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  position: relative;
}

/* FAB */
.fab-wrapper {
  position: relative;
  top: -30px; /* Float above bar */
  width: 60px;
  height: 60px;
}

.fab-btn {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--gold-primary) 0%, #b8860b 100%);
  border: none;
  color: var(--void-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(229, 195, 122, 0.4);
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.fab-btn::after {
  content: '';
  position: absolute;
  inset: 2px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.4);
  opacity: 0.5;
}

.fab-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8) 0%, transparent 70%);
  opacity: 0;
  animation: pulse 2s infinite;
}

.fab-btn:active {
  transform: scale(0.9);
}

.fab-icon {
  width: 30px;
  height: 30px;
  stroke-width: 2.5px;
  z-index: 2;
}

/* TABS */
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--starlight-dim);
  text-decoration: none;
  height: 100%;
  position: relative;
  transition: all 0.3s ease;
}

.tab-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 2px;
  transition: transform 0.3s ease;
}

.tab-label {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.5px;
  opacity: 0.7;
  font-family: var(--font-body);
}

/* ACTIVE STATE */
.tab-item.active {
  color: var(--gold-primary);
}

.tab-item.active .tab-icon {
  transform: translateY(-2px);
  filter: drop-shadow(0 0 6px var(--gold-glow));
}

.tab-item.active .tab-label {
  opacity: 1;
  font-weight: 600;
}

.tab-item:active {
  transform: scale(0.95);
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.3;
  }
  100% {
    transform: scale(0.8);
    opacity: 0;
  }
}
</style>
