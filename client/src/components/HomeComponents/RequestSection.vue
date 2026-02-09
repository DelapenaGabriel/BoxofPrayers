<script>
import PrayerRequestService from '@/services/PrayerRequestService'
import AuthPromptModal from '@/components/AuthPromptModal.vue'
import { ref } from 'vue'
import { useMotion } from '@vueuse/motion'

export default {
  components: {
    AuthPromptModal,
  },
  setup() {
    const cardRef = ref()
    const { apply } = useMotion(cardRef, {
      initial: { opacity: 1, y: 0, scale: 1 },
      enter: { opacity: 1, y: 0, scale: 1 },
    })

    return { cardRef, applyAnimation: apply }
  },
  // --- STATE ---
  data() {
    return {
      name: '',
      prayer: '',
      isAnonymous: false,
      isSubmitting: false,
      showThankYou: false,
      category: 'General',
      categories: [
        'General',
        'Healing',
        'Wisdom',
        'Family',
        'Friends',
        'Protection',
        'Salvation',
        'Praise',
        'Other',
      ],
      errorMessage: '',
      showAuthPrompt: false,
    }
  },

  // --- ACTIONS ---
  methods: {
    async submitPrayer() {
      // GUEST CHECK
      if (!this.$store.state.token) {
        this.showAuthPrompt = true
        return
      }

      if (!this.prayer.trim()) return

      this.isSubmitting = true

      const prayerPayload = {
        requesterId: this.$store.state.user?.id || null, // Use store user ID if available
        name: this.isAnonymous
          ? 'Anonymous'
          : this.name || this.$store.state.user?.name || 'Anonymous',
        content: this.prayer.trim(),
        category: this.category,
      }

      try {
        await PrayerRequestService.createPrayerRequest(prayerPayload)

        // 1. Trigger "Release" Animation (Physics based)
        // Shrink slightly, float up into the box (negative Y), fade out
        await this.applyAnimation({
          y: -250,
          scale: 0.1,
          opacity: 0,
          transition: {
            type: 'spring',
            stiffness: 100,
            damping: 20,
            mass: 0.5,
          },
        })

        // Wait a small moment for the "absorb" feel
        setTimeout(() => {
          this.isSubmitting = false
          this.showThankYou = true
          this.resetForm()

          // Reset card position silently for next time (though v-if handles recreation usually)
          // this.applyAnimation({ y: 0, scale: 1, opacity: 1, transition: { duration: 0 } })
        }, 200)
      } catch (error) {
        console.error('Error submitting prayer:', error)
        this.isSubmitting = false
        this.errorMessage = 'Prayer could not be released at this moment. Please try again.'
      }
    },

    resetForm() {
      this.prayer = ''
      // Keep name/category preference or reset? Let's keep for convenience.
    },

    resetView() {
      this.showThankYou = false
      // Animation handled automatically by v-motion enter prop
    },
  },
}
</script>

<template>
  <div class="sanctuary-wrapper">
    <!-- Ambient Light Effects -->
    <div class="bg-effects">
      <div class="light-1"></div>
      <div class="light-2"></div>
    </div>

    <div class="content-container">
      <!-- HEADER -->
      <transition name="header-fade">
        <div v-if="!showThankYou" class="header-group" :class="{ 'fade-out': isSubmitting }">
          <h1 class="page-title">CRY OUT TO HIM</h1>
          <p class="page-subtitle">
            Come to Me, all who are weary and burdened, and I will give you rest
          </p>
        </div>
      </transition>

      <!-- ERROR ALERT -->
      <transition name="fade">
        <div v-if="errorMessage" class="error-notification">
          <span>⚠️ {{ errorMessage }}</span>
        </div>
      </transition>

      <!-- FORM CARD -->
      <div
        v-if="!showThankYou"
        class="form-perspective-container"
        ref="cardRef"
        v-motion
        :initial="{ opacity: 0, y: 30, scale: 0.95 }"
        :enter="{ opacity: 1, y: 0, scale: 1, transition: { duration: 800, ease: 'easeOut' } }"
      >
        <div class="sanctuary-card">
          <!-- Card Header / Category -->
          <div class="card-top-row">
            <div class="input-group">
              <select v-model="category" class="minimal-select">
                <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
              </select>
            </div>

            <div class="anonymous-toggle" @click="isAnonymous = !isAnonymous">
              <div class="toggle-dot" :class="{ active: isAnonymous }"></div>
              <span>{{ isAnonymous ? 'Anonymous' : 'Show Name' }}</span>
            </div>
          </div>

          <!-- Main Input -->
          <div class="main-input-wrapper">
            <textarea
              v-model="prayer"
              class="journal-input"
              placeholder="Pour out your heart..."
              :disabled="isSubmitting"
            ></textarea>
          </div>

          <!-- Footer/Action -->
          <div class="card-bottom-row">
            <input
              v-if="!isAnonymous"
              type="text"
              v-model="name"
              class="minimal-name-input"
              placeholder="Your Name (Optional)"
            />
            <span v-else class="anonymous-label">Start as Anonymous</span>

            <button
              class="light-candle-btn"
              @click="submitPrayer"
              :disabled="isSubmitting || !prayer"
            >
              <span>{{ isSubmitting ? 'Releasing...' : 'Release Prayer' }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- SUCCESS STATE -->
      <transition name="slow-fade">
        <div v-if="showThankYou" class="thank-you-container">
          <div class="success-icon-wrapper">
            <div class="success-glow"></div>
            <span class="success-icon">🕊️</span>
          </div>
          <h2>Your Prayer Has Ascended</h2>
          <p>"The Lord is close to the brokenhearted and saves those who are crushed in spirit."</p>
          <button class="return-btn" @click="resetView">Send Another Request</button>
        </div>
      </transition>
    </div>

    <!-- AUTH PROMPT -->
    <AuthPromptModal :isOpen="showAuthPrompt" @close="showAuthPrompt = false" />
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Playfair+Display:ital,wght@0,400;0,600;1,400&family=Inter:wght@300;400&display=swap');

/* --- WRAPPER & AMBIANCE --- */
.sanctuary-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px 120px 40px;
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

.content-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 650px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* --- TEXT --- */
.header-group {
  text-align: center;
  margin-bottom: 40px;
  transition: opacity 0.3s;
}

.header-group.fade-out {
  opacity: 0;
}

.page-title {
  font-family: var(--font-heading);
  font-size: 3.5rem;
  font-weight: 400;
  margin: 0;
  /* Luminous White Style */
  color: #ffffff;
  text-shadow:
    0 0 20px rgba(255, 255, 255, 0.6),
    0 0 40px rgba(255, 215, 0, 0.3);
  letter-spacing: -2px;
}

.page-subtitle {
  font-family: var(--font-body);
  color: #a89f91;
  margin-bottom: 40px;
  text-align: center;
  font-style: italic;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
}

/* --- ERROR NOTIFICATION --- */
.error-notification {
  background: rgba(60, 20, 20, 0.8);
  border: 1px solid rgba(255, 100, 100, 0.3);
  color: #ffaaaa;
  padding: 10px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 10px;
  backdrop-filter: blur(4px);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* --- CARD --- */
.form-perspective-container {
  perspective: 1000px;
  width: 100%;
}

.sanctuary-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(229, 195, 122, 0.2);
  border-radius: 24px;
  padding: 40px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 0 30px rgba(229, 195, 122, 0.05);
  display: flex;
  flex-direction: column;
  gap: 20px;
  transition:
    transform 0.5s ease,
    box-shadow 0.5s ease;
}

.sanctuary-card:hover {
  box-shadow:
    0 30px 80px rgba(0, 0, 0, 0.6),
    inset 0 0 50px rgba(229, 195, 122, 0.08);
  border-color: rgba(229, 195, 122, 0.3);
}

/* --- ASCENSION ANIMATION --- */
.sanctuary-card.ascending {
  animation: ascendToLight 1.5s cubic-bezier(0.6, 0.04, 0.98, 0.335) forwards;
}

@keyframes ascendToLight {
  0% {
    transform: translateY(0) scale(1);
    opacity: 1;
    filter: blur(0px);
  }
  40% {
    transform: translateY(-80px) scale(0.95);
    opacity: 0.8;
    filter: blur(2px) brightness(1.2);
  }
  100% {
    transform: translateY(-600px) scale(0.5);
    opacity: 0;
    filter: blur(20px) brightness(3);
  }
}

/* --- FORM ELEMENTS --- */
.card-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.minimal-select {
  background: transparent;
  border: none;
  color: #bfa57d;
  font-family: var(--font-heading);
  font-size: 0.9rem;
  cursor: pointer;
  outline: none;
  border-bottom: 1px dashed rgba(191, 165, 125, 0.3);
  padding-bottom: 2px;
}

.minimal-select option {
  background: #1a150e;
  color: #bfa57d;
}

.anonymous-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.8rem;
  color: #8a7a6a;
  font-family: var(--font-body);
}

.toggle-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #333;
  transition: 0.3s;
}

.toggle-dot.active {
  background: #e5c37a;
  box-shadow: 0 0 10px #e5c37a;
}

.main-input-wrapper {
  flex-grow: 1;
}

.journal-input {
  width: 100%;
  height: 200px;
  background: transparent;
  border: none;
  resize: none;
  color: #e5e5e5;
  font-family: var(--font-body);
  font-size: 1.5rem;
  line-height: 1.6;
  outline: none;
}

.journal-input::placeholder {
  color: rgba(255, 255, 255, 0.15);
  font-style: italic;
}

.card-bottom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 25px;
}

.minimal-name-input {
  background: transparent;
  border: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #aaa;
  font-family: var(--font-body);
  font-size: 0.9rem;
  padding: 5px;
  width: 200px;
  outline: none;
  transition: 0.3s;
}

.minimal-name-input:focus {
  border-color: #e5c37a;
  color: #fff;
}

.anonymous-label {
  font-size: 0.9rem;
  color: #666;
  font-style: italic;
}

.light-candle-btn {
  background: linear-gradient(135deg, #e5c37a, #997d48);
  border: none;
  padding: 12px 30px;
  border-radius: 50px;
  color: #0e0b07;
  font-weight: 700;
  font-family: var(--font-heading);
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.light-candle-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 0 25px rgba(229, 195, 122, 0.4);
}

.light-candle-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  filter: grayscale(0.5);
}

/* --- SUCCESS STATE --- */
.thank-you-container {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.success-icon-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.success-icon {
  font-size: 4rem;
  z-index: 2;
}

.success-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(229, 195, 122, 0.4) 0%, transparent 70%);
  animation: breathe 4s infinite;
}

.thank-you-container h2 {
  font-family: var(--font-heading);
  color: #e5c37a;
  font-size: 2.2rem;
  margin: 0;
}

.thank-you-container p {
  font-family: var(--font-body);
  font-style: italic;
  color: #ccc;
  font-size: 1.1rem;
  max-width: 500px;
  line-height: 1.6;
}

.return-btn {
  margin-top: 30px;
  background: transparent;
  border: 1px solid rgba(229, 195, 122, 0.4);
  color: #e5c37a;
  padding: 12px 40px;
  border-radius: 50px;
  font-family: var(--font-heading);
  cursor: pointer;
  transition: 0.3s;
}

.return-btn:hover {
  background: rgba(229, 195, 122, 0.1);
  border-color: #e5c37a;
  color: #fff;
}

/* --- GLOBAL ANIMATIONS --- */
@keyframes breathe {
  0% {
    transform: scale(0.9);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.slow-fade-enter-active {
  transition: opacity 2s ease;
}
.slow-fade-enter-from,
.slow-fade-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .sanctuary-card {
    padding: 25px;
  }
  .card-top-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  .anonymous-toggle {
    margin-left: 0;
  }
  .card-bottom-row {
    flex-direction: column-reverse;
    gap: 20px;
    align-items: stretch;
  }
  .minimal-name-input {
    width: 100%;
  }
  .light-candle-btn {
    width: 100%;
    justify-content: center;
  }
  .page-title {
    font-size: 2.2rem;
  }
}
/* --- HEADER TRANSITIONS --- */
.header-fade-enter-active {
  transition:
    opacity 1s ease,
    transform 1s ease;
}

.header-fade-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.header-fade-enter-from,
.header-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
