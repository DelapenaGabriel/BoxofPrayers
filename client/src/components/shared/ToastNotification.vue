<template>
  <div class="toast-container">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast-message"
        :class="toast.type"
        @click="removeToast(toast.id)"
      >
        <span class="icon">{{ getIcon(toast.type) }}</span>
        <span class="text">{{ toast.message }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script>
import { useToastStore } from '@/store/toastStore'
import { computed } from 'vue'

export default {
  name: 'ToastNotification',
  setup() {
    const store = useToastStore()
    const toasts = computed(() => store.toasts)

    const removeToast = (id) => {
      store.removeToast(id)
    }

    const getIcon = (type) => {
      switch (type) {
        case 'success':
          return '✨'
        case 'error':
          return '⚠️'
        default:
          return 'ℹ️'
      }
    }

    return { toasts, removeToast, getIcon }
  },
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  bottom: 100px; /* Above nav bar */
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 9999;
  pointer-events: none;
  width: 90%;
  max-width: 400px;
}

.toast-message {
  pointer-events: auto;
  background: rgba(20, 20, 20, 0.95);
  border: 1px solid rgba(255, 215, 0, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #fff;
  padding: 12px 16px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  justify-content: center;
}

.toast-message.success {
  border-color: rgba(75, 181, 67, 0.5);
  box-shadow: 0 4px 20px rgba(75, 181, 67, 0.15);
}

.toast-message.error {
  border-color: rgba(255, 99, 71, 0.5);
  box-shadow: 0 4px 20px rgba(255, 99, 71, 0.15);
}

.icon {
  font-size: 1.2rem;
}

/* Transitions */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.9);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.9);
}
</style>
