<template>
  <div class="auth-page">
    <!-- Back Button (Absolute to Page) -->
    <button class="back-btn" @click="$router.push('/')" aria-label="Back">
      <svg
        viewBox="0 0 24 24"
        width="24"
        height="24"
        stroke="currentColor"
        stroke-width="2"
        fill="none"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M19 12H5M12 19l-7-7 7-7" />
      </svg>
    </button>
    <div class="auth-container">
      <!-- HEADER -->
      <header class="auth-header">
        <h1 class="brand-title">Step Inside</h1>
        <p class="auth-subtitle">Welcome back to the Sanctuary</p>
      </header>

      <!-- Back Button (Removed from here) -->

      <!-- GLASS FORM CARD -->
      <GlassCard variant="default" class="auth-card">
        <form @submit.prevent="handleLogin" class="auth-form">
          <!-- ERROR NOTIFICATION -->
          <transition name="fade">
            <div v-if="errorMessage" class="error-notification">
              <span class="error-icon">⚠️</span>
              {{ errorMessage }}
            </div>
          </transition>

          <!-- EMAIL -->
          <div class="input-container">
            <label class="floating-label">Email Address</label>
            <input v-model="email" type="email" placeholder=" " required class="auth-input" />
          </div>

          <!-- PASSWORD -->
          <div class="input-container">
            <label class="floating-label">Password</label>
            <input v-model="password" type="password" placeholder=" " required class="auth-input" />
          </div>

          <!-- ACTIONS -->
          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="loading">
              <span v-if="!loading">Enter Sanctuary</span>
              <span v-else class="loader"></span>
            </button>
          </div>
        </form>
      </GlassCard>

      <!-- SWITCH TO REGISTER -->
      <div class="auth-switch">
        <p>New here?</p>
        <router-link to="/register" class="link-gold">Create Account</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'
import GlassCard from '@/components/shared/GlassCard.vue'

export default {
  name: 'LoginView',
  components: { GlassCard },
  setup() {
    const email = ref('')
    const password = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    const authStore = useAuthStore()
    const router = useRouter()

    const handleLogin = async () => {
      loading.value = true
      try {
        await authStore.login({ email: email.value, password: password.value })
        router.push('/')
      } catch (err) {
        console.error('Login failed', err)
        errorMessage.value = 'Invalid email or password. Please try again.'
        // Shake animation trigger could go here
      } finally {
        loading.value = false
      }
    }

    return { email, password, loading, errorMessage, handleLogin }
  },
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-md);
  position: relative;
  z-index: 10;
  padding-bottom: env(safe-area-inset-bottom, 20px);
}

.auth-container {
  width: 100%;
  max-width: 420px;
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  position: relative;
}

/* HEADER */
.auth-header {
  text-align: center;
}

.brand-title {
  font-family: var(--font-heading);
  font-size: 2.5rem; /* Mobile default */
  color: var(--gold-primary);
  margin-bottom: var(--space-xs);
  text-shadow: 0 0 30px rgba(229, 195, 122, 0.4);
  letter-spacing: -1px;
}

.auth-subtitle {
  font-size: 1rem;
  opacity: 0.8;
  font-weight: 300;
  color: var(--starlight);
}

/* BACK BUTTON */
.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--gold-primary);
  width: 44px; /* Larger tap target */
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 50;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}

.back-btn:hover {
  background: var(--gold-primary);
  color: var(--void-bg);
  transform: translateX(-3px);
}

/* FORM STYLES */
.auth-card {
  padding: var(--space-lg) var(--space-md) !important;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

/* ERROR MESSAGE */
.error-notification {
  background: rgba(255, 59, 48, 0.15);
  border: 1px solid rgba(255, 59, 48, 0.3);
  color: #ff6b6b;
  padding: var(--space-md);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  animation: shake 0.4s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

.error-icon {
  font-size: 1.1rem;
}

@keyframes shake {
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }
  20%,
  80% {
    transform: translate3d(2px, 0, 0);
  }
  30%,
  50%,
  70% {
    transform: translate3d(-4px, 0, 0);
  }
  40%,
  60% {
    transform: translate3d(4px, 0, 0);
  }
}

/* FLOATING INPUTS */
.input-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  position: relative;
}

.input-container label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--gold-primary);
  margin-left: var(--space-xs);
  opacity: 0.9;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.auth-input {
  width: 100%;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  font-size: 1rem;
  color: var(--starlight);
  font-family: var(--font-body);
  transition: all 0.3s ease;
}

.auth-input:focus {
  outline: none;
  border-color: var(--gold-primary);
  background: rgba(0, 0, 0, 0.4);
  box-shadow: 0 0 15px rgba(229, 195, 122, 0.1);
}

.auth-input::placeholder {
  color: transparent;
}

/* ACTIONS */
.form-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  margin-top: var(--space-sm);
}

.btn-primary {
  background: linear-gradient(135deg, var(--gold-primary) 0%, var(--gold-dim) 100%);
  color: var(--void-bg);
  border: none;
  padding: var(--space-md);
  border-radius: var(--radius-lg);
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 4px 15px rgba(229, 195, 122, 0.2);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 56px; /* Explicit touch target height */
}

.btn-primary:active {
  transform: scale(0.98);
}
.btn-primary:disabled {
  opacity: 0.7;
  cursor: wait;
}

/* FOOTER / SWITCH */
.auth-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-sm);
  margin-top: 10px;
  font-size: 1rem;
}

.link-gold {
  color: var(--gold-primary);
  text-decoration: none;
  font-weight: 700;
  position: relative;
}
.link-gold::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0%;
  height: 1px;
  background: var(--gold-primary);
  transition: width 0.3s ease;
}
.link-gold:hover::after {
  width: 100%;
}

/* LOADER */
.loader {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(0, 0, 0, 0.2);
  border-top: 2px solid #000;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Tablet & Desktop Overrides */
@media (min-width: 768px) {
  .brand-title {
    font-size: 3.5rem;
  }
  .auth-card {
    padding: var(--space-xl) var(--space-lg) !important;
  }
  .back-btn {
    top: 40px;
    left: 40px;
  }
}
</style>
