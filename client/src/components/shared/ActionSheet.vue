<template>
  <BottomSheet :isOpen="isOpen" @close="$emit('close')">
    <div class="action-sheet-container">
      <!-- Title & Message -->
      <div v-if="title || message" class="sheet-header">
        <h3 v-if="title" class="sheet-title">{{ title }}</h3>
        <p v-if="message" class="sheet-message">{{ message }}</p>
      </div>

      <!-- Actions List -->
      <div class="actions-list">
        <button
          v-for="(action, index) in actions"
          :key="index"
          class="action-btn"
          :class="[
            action.variant ? `variant-${action.variant}` : '',
            { 'is-loading': action.loading },
          ]"
          @click="handleAction(action)"
          :disabled="action.loading"
        >
          <span v-if="action.icon" class="action-icon">{{ action.icon }}</span>
          <span class="action-label">{{ action.label }}</span>
          <span v-if="action.loading" class="spinner"></span>
        </button>
      </div>

      <!-- Cancel Button -->
      <button class="cancel-btn" @click="$emit('close')">Cancel</button>
    </div>
  </BottomSheet>
</template>

<script>
import BottomSheet from './BottomSheet.vue'

export default {
  name: 'ActionSheet',
  components: { BottomSheet },
  props: {
    isOpen: Boolean,
    title: String,
    message: String,
    actions: {
      type: Array, // [{ label, handler, variant: 'danger'|'primary'|'default', icon, loading }]
      default: () => [],
    },
  },
  emits: ['close'],
  setup(props, { emit }) {
    const handleAction = async (action) => {
      if (action.handler) {
        await action.handler()
      }
      emit('close')
    }

    return { handleAction }
  },
}
</script>

<style scoped>
.action-sheet-container {
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.sheet-header {
  text-align: center;
  margin-bottom: 10px;
  padding: 0 10px;
}

.sheet-title {
  color: #fff;
  font-family: var(--font-heading);
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.sheet-message {
  color: var(--starlight-muted);
  font-size: 0.9rem;
  line-height: 1.4;
}

.actions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 16px;
  border-radius: 16px;
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition:
    transform 0.2s,
    background 0.2s;
  position: relative;
  overflow: hidden;
}

.action-btn:active {
  transform: scale(0.98);
  background: rgba(255, 255, 255, 0.1);
}

.variant-danger {
  color: #ff6b6b;
  border-color: rgba(255, 107, 107, 0.3);
  background: rgba(255, 107, 107, 0.05);
}

.variant-primary {
  color: var(--gold-primary);
  border-color: rgba(229, 195, 122, 0.3);
  background: rgba(229, 195, 122, 0.05);
}

.cancel-btn {
  background: none;
  border: none;
  padding: 16px;
  color: var(--starlight-dim);
  font-size: 1rem;
  cursor: pointer;
  font-weight: 600;
  margin-top: 5px;
}
.cancel-btn:active {
  opacity: 0.7;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
