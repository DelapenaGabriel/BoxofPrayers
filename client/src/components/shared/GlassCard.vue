<template>
  <div class="glass-card" :class="variantClass">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'GlassCard',
  props: {
    variant: {
      type: String,
      default: 'default', // 'default', 'dark', 'highlight'
      validator: (value) => ['default', 'dark', 'highlight'].includes(value),
    },
  },
  computed: {
    variantClass() {
      return `variant-${this.variant}`
    },
  },
}
</script>

<style scoped>
.glass-card {
  position: relative;
  background: rgba(20, 20, 20, 0.6);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05); /* Subtle top light */
  border-radius: 20px;
  padding: 20px;
  overflow: hidden;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

/* VARIANTS */
.glass-card.variant-dark {
  background: rgba(5, 5, 5, 0.85);
  border-color: rgba(255, 255, 255, 0.04);
}

.glass-card.variant-highlight {
  background: rgba(229, 195, 122, 0.1);
  border-color: rgba(229, 195, 122, 0.2);
  box-shadow: 0 8px 32px rgba(229, 195, 122, 0.1);
}
</style>
