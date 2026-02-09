<script setup>
import { onMounted, reactive } from 'vue'
import PrayerService from '@/services/PrayerService'
import { useSanctuaryStore } from '@/store/sanctuaryStore'

const store = useSanctuaryStore()

const state = reactive({
  loading: true,
  intercessions: 0,
})

// --- AUDIO LOGIC ---
import { useAudio } from '@/composables/useAudio'
const { isPlaying, toggleAudio } = useAudio()

// --- API METHODS ---
const fetchSanctuaryData = () => {
  PrayerService.listPublicPrayers()
    .then((response) => {
      const allPrayers = response.data

      // Filter for prayers created TODAY
      const today = new Date().toDateString()
      const todayCount = allPrayers.filter((p) => {
        const pDate = new Date(p.prayedAt)
        return pDate.toDateString() === today
      }).length

      state.intercessions = todayCount

      // SYNC GLOBAL BACKGROUND
      store.setStarCount(todayCount)

      state.loading = false
    })
    .catch((error) => {
      console.error('Error fetching prayers:', error)
      // Fallback or error state
      state.loading = false
    })
}

// --- LIFECYCLE ---
onMounted(() => {
  fetchSanctuaryData()
})
</script>

<template>
  <div class="sanctuary-page">
    <div class="bg-effects">
      <div class="light-1"></div>
      <div class="light-2"></div>
    </div>

    <!-- MAIN CONTENT -->
    <main
      class="center-stage"
      :class="{ 'fade-in': !state.loading, 'immersive-hidden': store.isImmersive }"
    >
      <!-- Title Group -->
      <div class="title-group">
        <h1 class="main-title">The Sanctuary</h1>
    
      </div>

      <!-- Stats Container (Simplified) -->
      <div class="stats-container">
        <div class="stat-item">
          <span class="stat-number">{{ state.intercessions }}</span>
          <span class="stat-label">Prayers Today</span>
        </div>
      </div>
    </main>

    <!-- IMMERSIVE CONTROLS (ALWAYS VISIBLE-ISH) -->
    <div class="controls-container" :class="{ dimmed: store.isImmersive }">
      <!-- MUTE TOGGLE -->
      <button class="control-btn sound-btn" @click="toggleAudio" :class="{ active: isPlaying }">
        <svg v-if="isPlaying" viewBox="0 0 24 24" fill="currentColor" class="icon-svg">
          <path
            d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"
          />
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="currentColor" class="icon-svg">
          <path
            d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73 4.27 3zM12 4L9.91 6.09 12 8.18V4z"
          />
        </svg>
      </button>

      <!-- MEDITATE TOGGLE -->
      <button
        class="control-btn meditate-btn"
        @click="store.toggleImmersive()"
        :class="{ active: store.isImmersive }"
      >
        <span class="btn-text">{{ store.isImmersive ? 'Return' : 'Witness' }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
/* Import Fonts: Cinzel for the header/brand, Lato for UI text */
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;700&family=Lato:wght@300;400;700&display=swap');

/* --- LAYOUT & BASE --- */
.sanctuary-page {
  width: 100%;
  min-height: 100vh; /* Mobile scroll safety */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: var(--font-body);
  color: #fff;
  z-index: 10;
  padding: var(--space-3xl) var(--space-lg) 140px var(--space-lg);
  /* Increased bottom padding for mobile navbar safety + extra space */
}

/* --- BOTTOM CONTROLS --- */
.controls-container {
  position: absolute;
  bottom: 120px; /* Above TAB BAR */
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  z-index: 50;
  transition: all 0.5s ease;
}

/* Move down when immersive (since nav is gone) */
.controls-container.dimmed {
  bottom: 60px;
}

/* MINIMAL MODE (Immersive) Styles */
.controls-container.dimmed .control-btn {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.4);
  box-shadow: none;
  backdrop-filter: blur(2px);
}

.controls-container.dimmed .control-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
  opacity: 1;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.15);
  transform: translateY(-2px) scale(1.05);
}

.controls-container.dimmed .sound-btn.active {
  color: rgba(255, 215, 0, 0.5);
  background: transparent;
  box-shadow: none;
  border-color: rgba(255, 215, 0, 0.1);
}

.controls-container.dimmed .sound-btn.active:hover {
  color: #ffd700;
  border-color: #ffd700;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.3);
}

/* Shared Button Styles */
.control-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #e5c37a;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.control-btn:hover {
  background: rgba(255, 215, 0, 0.15);
  border-color: #ffd700;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5);
  color: #fff;
}

.control-btn:active {
  transform: scale(0.95);
}

/* Sound Button */
.sound-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  padding: 0;
}

.sound-btn.active {
  color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.2);
}

.icon-svg {
  width: 24px;
  height: 24px;
}

/* Meditate Button */
.meditate-btn {
  padding: 12px 30px;
  height: 50px;
}

.meditate-btn .btn-text {
  font-family: var(--font-heading);
  text-transform: uppercase;
  letter-spacing: 2px;
  font-size: 0.85rem;
  font-weight: 600;
}

.meditate-btn.active {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
  color: #fff;
}

/* --- BG EFFECTS --- */
.bg-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.light-1,
.light-2 {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.light-1 {
  background: #ffd700;
  top: -100px;
  left: -100px;
  animation: float 10s infinite alternate;
}

.light-2 {
  background: #ff8c00;
  bottom: -100px;
  right: -100px;
  animation: float 12s infinite alternate-reverse;
}

@keyframes float {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 30px);
  }
}

@media (max-width: 600px) {
  .light-1,
  .light-2 {
    width: 300px;
    height: 300px;
    opacity: 0.12;
    filter: blur(60px);
  }
  .light-1 {
    top: -50px;
    left: -50px;
  }
  .light-2 {
    bottom: -50px;
    right: -50px;
  }
}

/* --- CENTER STAGE --- */
.center-stage {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  opacity: 0;
  transition:
    opacity 1.2s ease,
    transform 1.2s ease,
    filter 1.2s ease;
}

.center-stage.fade-in {
  opacity: 1;
}

/* HIDDEN STATE */
.center-stage.immersive-hidden {
  opacity: 0 !important;
  transform: scale(0.92);
  filter: blur(10px);
  pointer-events: none;
}

/* --- TYPOGRAPHY --- */
.title-group {
  text-align: center;
  margin-bottom: var(--space-3xl);
}

.main-title {
  font-family: var(--font-heading);
  font-size: clamp(3rem, 10vw, 5.5rem);
  font-weight: 400;
  margin: 0;
  color: #ffffff;
  text-shadow:
    0 0 20px rgba(255, 255, 255, 0.6),
    0 0 40px rgba(255, 215, 0, 0.3);
  letter-spacing: -2px;
  line-height: 1.1;
}

.subtitle {
  font-family: var(--font-heading);
  font-style: italic;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
  color: #aaa;
  margin-top: var(--space-sm);
  letter-spacing: 1px;
}

/* --- STATS CONTAINER --- */
.stats-container {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: var(--space-md) var(--space-xl);
  border-radius: 100px;
  backdrop-filter: blur(8px);
  margin-bottom: var(--space-2xl);
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.3);
  width: auto;
  max-width: 90%;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 0.7rem;
  letter-spacing: 2px;
  color: #ccc;
  text-transform: uppercase;
  text-align: center;
}

.stat-number {
  font-family: var(--font-heading);
  font-size: 1.8rem;
  color: #fff;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
}

/* --- MOBILE OPTIMIZATIONS --- */
@media (max-width: 600px) {
  .stats-container {
    padding: var(--space-md) var(--space-lg);
  }

  .stat-label {
    letter-spacing: 1px;
    font-size: 0.65rem;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .subtitle {
    max-width: 80%;
    margin: 10px auto;
  }
}
</style>
