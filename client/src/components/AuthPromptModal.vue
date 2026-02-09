<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isOpen" class="overlay-backdrop">
        <div class="auth-card">
          <button class="close-btn" @click="$emit('close')">x</button>

          <div class="icon-wrapper">
            <div class="glow-effect"></div>
            <div class="modal-icon">🕯️</div>
          </div>

          <h2 class="auth-title">Join the Circle of Prayer</h2>
          <p class="auth-desc">
            Sign in or create an account to lift prayers and share your own burdens with the
            community.
          </p>

          <div class="action-buttons">
            <router-link to="/login" class="btn primary">Sign In</router-link>
            <router-link to="/register" class="btn secondary">Create Account</router-link>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
export default {
  name: 'AuthPromptModal',
  props: {
    isOpen: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['close'],
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700&family=Lato:wght@300;400&display=swap');

.overlay-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 200002;
  background-color: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-card {
  width: 90%;
  max-width: 400px;
  background: linear-gradient(145deg, #1a1510, #050302);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: var(--radius-lg);
  padding: var(--space-2xl) var(--space-xl);
  text-align: center;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.8);
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  color: #8a7a5a;
  font-size: 1.2rem;
  cursor: pointer;
  z-index: 10;
}

.icon-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: var(--space-lg);
}

.modal-icon {
  font-size: 3rem;
  z-index: 2;
}

.glow-effect {
  position: absolute;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s infinite;
}

.auth-title {
  font-family: 'Cinzel', serif;
  color: #ffecd1;
  font-size: 1.5rem;
  margin-bottom: var(--space-sm);
}

.auth-desc {
  font-family: var(--font-body);
  color: #a89f91;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: var(--space-xl);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.btn {
  display: block;
  text-decoration: none;
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-xl);
  font-family: 'Cinzel', serif;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn.primary {
  background: linear-gradient(90deg, #b8860b, #d4af37);
  color: #000;
  font-weight: 700;
  box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(212, 175, 55, 0.4);
}

.btn.secondary {
  background: transparent;
  border: 1px solid rgba(168, 159, 145, 0.4);
  color: #a89f91;
}

.btn.secondary:hover {
  border-color: #d4af37;
  color: #fff;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
