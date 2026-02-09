<script setup>
// Import the shared audio logic
import { useAudio } from '@/composables/useAudio' // Adjust path if needed

// Destructure the state and function
// isPlaying is true when music is ON
const { isPlaying, toggleAudio } = useAudio()

import { computed } from 'vue'
import { useAuthStore } from '@/store/authStore'

const authStore = useAuthStore()
const isAuthenticated = computed(() => !!authStore.token)
</script>

<template>
  <nav class="sanctuary-nav">
    <div class="nav-container">
      <!-- HOME LINK -->
      <RouterLink to="/" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
          </svg>
        </div>
        <span class="label">Home</span>
      </RouterLink>

      <!-- PRAY LINK -->
      <RouterLink to="/pray" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
            />
          </svg>
        </div>
        <span class="label">Pray</span>
      </RouterLink>

      <!-- REQUEST LINK -->
      <RouterLink to="/request" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"
            />
          </svg>
        </div>
        <span class="label">Request</span>
      </RouterLink>

      <!-- LEADERBOARD LINK -->
      <RouterLink to="/leaderboard" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"
            />
          </svg>
        </div>
        <span class="label">Guardians</span>
      </RouterLink>

      <!-- PROFILE LINK (Auth Only) -->
      <RouterLink v-if="isAuthenticated" to="/profile" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M12 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zm0 10c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
            />
          </svg>
        </div>
        <span class="label">Profile</span>
      </RouterLink>

      <!-- LOGIN LINK (Public Only) -->
      <RouterLink v-else to="/login" class="nav-item">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path d="M10 17l5-5-5-5v10z" />
            <path d="M0 24V0h24v24H0z" fill="none" />
            <path
              d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-4h2v2h-2zm0-2h2V8h-2z"
              opacity=".3"
            />
            <!-- Key Icon -->
            <path
              d="M12.65 10C11.83 7.67 9.61 6 7 6c-3.31 0-6 2.69-6 6s2.69 6 6 6c2.61 0 4.83-1.67 5.65-4H17v4h4v-4h2v-4H12.65zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"
            />
          </svg>
        </div>
        <span class="label">Sign In</span>
      </RouterLink>

      <!-- VERTICAL DIVIDER -->
      <div class="nav-divider"></div>

      <!-- AUDIO TOGGLE BUTTON -->
      <button
        class="nav-item audio-btn"
        :class="{ 'active-sound': isPlaying }"
        @click="toggleAudio"
        type="button"
        aria-label="Toggle Background Music"
      >
        <div class="icon-wrapper">
          <!-- Playing Icon (Speaker Waves) -->
          <svg v-if="isPlaying" viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"
            />
          </svg>

          <!-- Muted Icon (Speaker X) -->
          <svg v-else viewBox="0 0 24 24" fill="currentColor" class="nav-icon">
            <path
              d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73 4.27 3zM12 4L9.91 6.09 12 8.18V4z"
            />
          </svg>
        </div>
        <span class="label">{{ isPlaying ? 'Peace' : 'Listen' }}</span>
      </button>
    </div>
  </nav>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;800&display=swap');

/* 1. Positioning & Container */
.sanctuary-nav {
  position: fixed;
  bottom: calc(var(--space-lg) + env(safe-area-inset-bottom));
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  width: 100%;
  display: flex;
  justify-content: center;
  pointer-events: none;
}

/* --- BRANDING (Logo) --- */
.top-branding {
  position: absolute;
  left: var(--space-2xl);
  top: var(--space-lg);
  z-index: 20;
}

.brand-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.brand-link:hover {
  opacity: 0.9;
}

.brand-logo {
  height: 80px;
  width: auto;
  filter: drop-shadow(0 4px 12px rgba(255, 215, 0, 0.15));
}

.brand-name {
  font-family: var(--font-body);
  font-size: 1.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #fffcf5 0%, #ffecd1 50%, #ebb64e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  letter-spacing: -0.5px;
}

.nav-container {
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: auto;
  max-width: 95%;
  gap: clamp(10px, 4vw, 35px);
  padding: var(--space-md) clamp(15px, 4vw, 40px);
  background-color: rgba(10, 5, 2, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 50px;
  border: 1px solid rgba(255, 215, 0, 0.15);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.6);
  transition: all 0.3s ease;
}

/* 2. Navigation Items */
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #5a5045;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  min-width: 44px;
  -webkit-tap-highlight-color: transparent;
}

/* 3. Icons */
.icon-wrapper {
  margin-bottom: 6px;
  transition: transform 0.3s ease;
  display: flex;
  justify-content: center;
}

.nav-icon {
  width: 24px;
  height: 24px;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.5));
}

/* 4. Text Labels */
.label {
  font-family: var(--font-heading);
  font-size: 0.65rem;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  font-weight: 600;
  opacity: 0.7;
  transition: opacity 0.3s;
}

/* 5. HOVER & ACTIVE STATES */
@media (hover: hover) {
  .nav-item:hover .icon-wrapper {
    transform: translateY(-3px);
  }
  .nav-item:hover {
    color: #a89f91;
  }
}

.nav-item.router-link-active {
  color: #ffaa00;
}
.nav-item.router-link-active .nav-icon {
  filter: drop-shadow(0 0 8px rgba(255, 170, 0, 0.6));
}
.nav-item.router-link-active .label {
  opacity: 1;
  text-shadow: 0 0 10px rgba(255, 170, 0, 0.4);
}

/* 6. DIVIDER */
.nav-divider {
  width: 1px;
  height: 30px;
  background-color: rgba(255, 255, 255, 0.15);
  margin: 0 5px;
}

/* 7. AUDIO BUTTON SPECIFIC */
.audio-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-family: inherit;
}

/* Sound On State (Active) */
.audio-btn.active-sound {
  color: #ffecd1;
}

.audio-btn.active-sound .nav-icon {
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.4));
}

.audio-btn.active-sound .label {
  opacity: 1;
  color: #ffaa00;
}

@media (max-width: 600px) {
  .top-branding {
    top: 0px;
    left: 0px;
  }
}

/* 8. MOBILE OPTIMIZATIONS */
@media (max-width: 480px) {
  .sanctuary-nav {
    bottom: 0;
    padding-bottom: env(safe-area-inset-bottom);
    background-color: rgba(10, 5, 2, 0.95);
    backdrop-filter: blur(20px);
  }

  .nav-container {
    padding: var(--space-sm) 0;
    width: 100%;
    max-width: 100%;
    border-radius: 0;
    border: none;
    border-top: 1px solid rgba(255, 215, 0, 0.15);
    justify-content: space-evenly;
    background-color: transparent;
    box-shadow: none;
  }

  .brand-logo,
  .brand-name {
    display: none;
  }

  .nav-icon {
    width: 24px;
    height: 24px;
  }

  .label {
    font-size: 0.6rem;
    letter-spacing: 0.5px;
    margin-top: 4px;
  }

  .icon-wrapper {
    margin-bottom: 0;
  }

  .nav-item {
    min-width: 44px; /* Ensure touch target size */
    min-height: 44px;
    padding: 0 5px;
  }
}

@media (max-width: 360px) {
  .label {
    display: none; /* Hide text on very small screens */
  }
  .nav-container {
    padding: var(--space-sm);
    gap: var(--space-md);
  }
}
</style>
