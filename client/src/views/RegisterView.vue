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
      <header class="auth-header" v-if="!registrationSuccess">
        <h1 class="brand-title">Join The Light</h1>
        <p class="auth-subtitle">Begin your journey in the Sanctuary</p>
      </header>
      <!-- HEADER SUCCESS -->
      <header class="auth-header" v-else>
        <h1 class="brand-title">Welcome</h1>
        <p class="auth-subtitle">Your journey begins now</p>
      </header>

      <!-- Back Button (Removed from here) -->

      <!-- GLASS FORM CARD -->
      <GlassCard variant="default" class="auth-card">
        <!-- SUCCESS STATE -->
        <div v-if="registrationSuccess" class="success-view">
          <div class="success-icon">✨</div>
          <h2>Welcome, {{ name }}</h2>
          <p>Your account has been created.</p>
          <router-link to="/login" class="btn-primary link-btn"> Proceed to Login </router-link>
        </div>

        <!-- FORM STATE -->
        <form v-else @submit.prevent="handleRegister" class="auth-form">
          <!-- NAME -->
          <div class="input-container">
            <label class="floating-label">Full Name</label>
            <input v-model="name" type="text" placeholder=" " required class="auth-input" />
          </div>

          <!-- SPIRITUAL NAME -->
          <div class="input-container">
            <label class="floating-label">Display Name</label>
            <input
              v-model="spiritualName"
              type="text"
              placeholder=" "
              required
              class="auth-input"
            />
          </div>

          <!-- EMAIL -->
          <div class="input-container">
            <label class="floating-label">Email Address</label>
            <input v-model="email" type="email" placeholder=" " required class="auth-input" />
          </div>

          <!-- PASSWORD GRID -->
          <div class="password-grid">
            <div class="input-container">
              <label class="floating-label">Password</label>
              <input
                v-model="password"
                type="password"
                placeholder=" "
                required
                class="auth-input"
              />
            </div>
            <div class="input-container">
              <label class="floating-label">Confirm</label>
              <input
                v-model="confirmPassword"
                type="password"
                placeholder=" "
                required
                class="auth-input"
              />
            </div>
          </div>

          <transition name="fade">
            <div v-if="passwordMismatch" class="error-notification">
              <span class="error-icon">⚠️</span>
              Passwords do not match
            </div>
            <div v-else-if="errorMessage" class="error-notification">
              <span class="error-icon">⚠️</span>
              {{ errorMessage }}
            </div>
          </transition>

          <!-- ACTIONS -->
          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="isLoading || passwordMismatch">
              <span v-if="!isLoading">Create Account</span>
              <span v-else class="loader"></span>
            </button>
          </div>
        </form>
      </GlassCard>

      <!-- SWITCH TO LOGIN -->
      <div class="auth-switch" v-if="!registrationSuccess">
        <p>Already a member?</p>
        <router-link to="/login" class="link-gold">Sign In</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import AuthService from '@/services/AuthService'
import GlassCard from '@/components/shared/GlassCard.vue'

export default {
  name: 'RegisterView',
  components: { GlassCard },
  setup() {
    const name = ref('')
    const spiritualName = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const isLoading = ref(false)
    const registrationSuccess = ref(false)
    const errorMessage = ref('')

    const passwordMismatch = computed(() => {
      return confirmPassword.value.length > 0 && password.value !== confirmPassword.value
    })

    const handleRegister = async () => {
      if (passwordMismatch.value) return
      isLoading.value = true
      errorMessage.value = ''

      const userPayload = {
        name: name.value,
        displayName: spiritualName.value,
        email: email.value,
        password: password.value,
        confirmPassword: confirmPassword.value,
        role: 'ROLE_USER',
      }

      try {
        const response = await AuthService.register(userPayload)

        registrationSuccess.value = true
      } catch (error) {
        if (error.response?.status === 409) {
          errorMessage.value = 'An account with this email already exists.'
        } else {
          errorMessage.value = error.response?.data?.message || 'Registration failed.'
        }
      } finally {
        isLoading.value = false
      }
    }

    return {
      name,
      spiritualName,
      email,
      password,
      confirmPassword,
      isLoading,
      registrationSuccess,
      errorMessage,
      passwordMismatch,
      handleRegister,
    }
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
}

.auth-container {
  width: 100%;
  max-width: 460px;
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
  font-size: 2.2rem; /* Mobile default */
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

/* CARD */
.auth-card {
  padding: var(--space-xl) var(--space-lg) !important; /* Increased padding */
  width: 100%;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg); /* Increased gap between form chunks */
}

/* INPUTS */
.input-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  position: relative;
}

.password-grid {
  display: grid;
  grid-template-columns: 1fr; /* Mobile: Stacked */
  gap: var(--space-lg); /* Increased gap */
}

.input-container label {
  font-size: 0.8rem;
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
  height: 50px; /* Explicit comfortable height */
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
  margin-top: var(--space-md); /* More separation */
}

.btn-primary {
  width: 100%;
  background: linear-gradient(135deg, var(--gold-primary) 0%, var(--gold-dim) 100%);
  color: var(--void-bg);
  border: none;
  padding: 0 var(--space-lg);
  border-radius: var(--radius-lg);
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 4px 15px rgba(229, 195, 122, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  height: 56px; /* Explicit height */
  text-transform: uppercase;
  letter-spacing: 1px;
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: wait;
}

/* ERROR MESSAGE */
.error-notification {
  background: linear-gradient(135deg, rgba(255, 69, 58, 0.15) 0%, rgba(255, 69, 58, 0.05) 100%);
  border: 1px solid rgba(255, 69, 58, 0.2);
  border-left: 4px solid #ff453a;
  color: #ff453a;
  padding: var(--space-md);
  border-radius: var(--radius-md);
  font-size: 0.95rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(255, 69, 58, 0.1);
  animation: slideIn 0.4s cubic-bezier(0.25, 0.8, 0.25, 1) both;
}

.error-icon {
  font-size: 1.2rem;
  filter: drop-shadow(0 0 8px rgba(255, 69, 58, 0.4));
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* SWITCH */
.auth-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-sm);
  margin-top: var(--space-xl); /* More breathing room */
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

/* SUCCESS VIEW */
.success-view {
  text-align: center;
  padding: var(--space-xl) var(--space-md);
}
.success-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}
@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.link-btn {
  text-decoration: none;
  display: flex;
  justify-content: center;
  margin-top: 20px;
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
    font-size: 2.8rem;
  }
  .auth-card {
    padding: var(--space-2xl) var(--space-xl) !important; /* Even more spacing on desktop */
  }
  .back-btn {
    top: 40px;
    left: 40px;
  }
  .password-grid {
    grid-template-columns: 1fr 1fr; /* Switch to 2 columns on larger screens */
  }
}
</style>
