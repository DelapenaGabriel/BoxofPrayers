<script setup>
import { ref, onMounted } from 'vue'
import { useAudio } from '@/composables/useAudio'
import TheBox from './Intro/TheBox.vue'

const showIntro = ref(false)
const isEntering = ref(false) // Triggers the box opening
const isFading = ref(false) // Triggers transparency fade
const { playAudio } = useAudio()

const enterSite = () => {
  // 1. Play Audio
  playAudio()

  // 2. Open the Box (TheBox.vue watches this prop)
  isEntering.value = true

  // 3. Wait for box open animation (~1.5s) then fade out overlay
  // We want the user to see the "light" burst before fading out
  setTimeout(() => {
    isFading.value = true

    // 4. Remove from DOM after fade completes
    setTimeout(() => {
      showIntro.value = false
      localStorage.setItem('hasSeenIntro', 'true')
    }, 1500)
  }, 1200)
}

onMounted(() => {
  const hasSeen = localStorage.getItem('hasSeenIntro')
  const urlParams = new URLSearchParams(window.location.search)
  const forceShow = urlParams.get('intro') === 'true'

  if (!hasSeen || forceShow) {
    showIntro.value = true
  }
})
</script>

<template>
  <transition name="overlay-fade">
    <div
      v-if="showIntro"
      class="intro-overlay"
      :class="{ 'is-fading': isFading, 'bright-exit': isEntering }"
    >
      <!-- VIDEO BACKGROUND -->
      <div class="video-background">
        <video autoplay muted loop playsinline>
          <source src="/intro/background.mp4" type="video/mp4" />
        </video>
        <div class="video-overlay"></div>
      </div>

      <!-- 3D SCENE (Absolute, behind content) -->
      <TheBox :is-open="isEntering" @box-click="enterSite" />

      <!-- TEXT CONTENT (Foreground) -->
      <!-- We hide the text when entering to focus on the box opening -->
      <transition name="content-fade">
        <div v-if="!isEntering" class="content-wrapper">
          <h1 class="title">
            <span class="word">Box</span>
            <span class="word of">of</span>
            <span class="word">Prayers</span>
          </h1>
          <p class="subtitle">A Sanctuary for Your Soul</p>

          <button class="enter-btn" @click="enterSite">
            <span class="btn-text">Enter Sanctuary</span>
            <div class="btn-glow"></div>
          </button>
        </div>
      </transition>
    </div>
  </transition>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Outfit:wght@300;600&display=swap');

.intro-overlay {
  position: fixed;
  inset: 0;
  z-index: 999999;
  background-color: #050505;
  background-image: radial-gradient(circle at center, #1a1520 0%, #000000 90%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: background-color 2s ease;
}

/* When exiting, the background turns white (representing the light consuming the screen) */
.intro-overlay.bright-exit {
  background-image: radial-gradient(circle at center, #fffcf0 10%, #ffd700 40%, #ffffff 100%);
  /* We might want to handle this via the fade-out instead,
     but let's try a bright flash */
}

.intro-overlay.is-fading {
  pointer-events: none;
  opacity: 0;
  transition: opacity 1.5s ease-in-out;
}

/* --- VIDEO BACKGROUND --- */
.video-background {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.video-background video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(26, 21, 32, 0.4) 0%, rgba(0, 0, 0, 0.8) 90%);
  z-index: 1; /* Above video, below 3D scene */
}

/* --- TYPOGRAPHY --- */
.content-wrapper {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-lg);
  z-index: 10;
  /* Position relative to viewport height to ensure it sits below the box */
  margin-top: 35vh;
  padding: 0 var(--space-md);
}

.title {
  font-family: 'Cinzel', serif;
  font-size: clamp(2rem, 5vw, 4rem); /* Responsive font size */
  font-weight: 400;
  text-transform: uppercase;
  color: #fffcf0;
  letter-spacing: 0.2em;
  display: flex;
  gap: 0.5em;
  align-items: center;
  margin: 0;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
  flex-wrap: wrap;
  justify-content: center;
}

.word {
  opacity: 0;
  animation: fade-in-up 1s ease forwards;
}

.word:nth-child(1) {
  animation-delay: 0.5s;
}
.word:nth-child(2) {
  animation-delay: 0.8s;
  font-size: 0.6em;
  color: rgba(255, 255, 255, 0.6);
}
.word:nth-child(3) {
  animation-delay: 1.1s;
}

.subtitle {
  font-family: 'Outfit', sans-serif;
  font-size: clamp(0.8rem, 2vw, 1.2rem);
  color: max(rgba(255, 255, 255, 0.5));
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 0.4em;
  text-transform: uppercase;
  margin: 0;
  opacity: 0;
  animation: fade-in 1s ease forwards 1.5s;
  text-align: center;
  line-height: 1.6;
}

/* --- BUTTON --- */
.enter-btn {
  margin-top: var(--space-xl);
  background: rgba(0, 0, 0, 0.3); /* Subtle background for better contrast */
  border: 1px solid rgba(255, 215, 0, 0.3);
  cursor: pointer;
  position: relative;
  padding: var(--space-md) var(--space-3xl);
  overflow: hidden;
  opacity: 0;
  animation: fade-in 1s ease forwards 2s;
  transition: all 0.3s ease;
  border-radius: var(--radius-sm);
  backdrop-filter: blur(5px);
}

.enter-btn:hover {
  transform: translateY(-2px);
  background: rgba(0, 0, 0, 0.5);
  border-color: rgba(255, 215, 0, 0.6);
}

.btn-text {
  font-family: 'Outfit', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  color: #ffd700;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  position: relative;
  z-index: 2;
  transition: color 0.3s ease;
}

.enter-btn:hover .btn-text {
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.btn-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 215, 0, 0.2), transparent);
  transform: translateX(-100%);
  transition: all 0.5s ease;
}

.enter-btn:hover .btn-glow {
  transform: translateX(100%);
  transition: transform 0.8s ease;
}

/* --- ANIMATIONS --- */
@keyframes fade-in-up {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.content-fade-enter-active,
.content-fade-leave-active {
  transition: opacity 0.8s ease;
}

.content-fade-enter-from,
.content-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* --- MEDIA QUERIES --- */
@media (max-width: 768px) {
  .content-wrapper {
    margin-top: 40vh; /* Push text lower on mobile to clear the box */
    gap: var(--space-md);
  }

  .title {
    flex-direction: row;
    gap: 0.3em;
  }

  .enter-btn {
    padding: var(--space-sm) var(--space-xl);
    width: 80%; /* Wider touch target */
    max-width: 300px;
  }
}
</style>
