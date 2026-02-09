<template>
  <div class="stack-container">
    <TransitionGroup name="card" tag="div" class="cards-wrapper">
      <div
        v-for="(card, index) in visibleCards"
        :key="card.id"
        class="card-slot"
        :style="getCardStyle(index)"
        @touchstart="onTouchStart($event, index)"
        @touchmove="onTouchMove($event, index)"
        @touchend="onTouchEnd($event, index)"
      >
        <!-- GLASS CARD CONTENT -->
        <div class="swipe-card glass-panel">
          <!-- Category Badge -->
          <div class="card-top">
            <span class="category-badge">{{ card.prayerRequest.category }}</span>
            <span class="time-badge">{{ formatDate(card.prayerRequest.createdAt) }}</span>
          </div>

          <!-- Main Content -->
          <div class="card-content">
            <h3 class="card-author">{{ card.prayerRequest.name || 'Anonymous' }}</h3>
            <p class="card-text">"{{ card.prayerRequest.content }}"</p>
          </div>

          <!-- Footer / Instructions -->
          <div class="card-bottom">
            <div class="swipe-hint left" :style="{ opacity: getHintOpacity('left', index) }">
              PASS
            </div>
            <div class="swipe-hint right" :style="{ opacity: getHintOpacity('right', index) }">
              PRAY
            </div>
          </div>
        </div>
      </div>
    </TransitionGroup>

    <!-- EMPTY STATE -->
    <div v-if="cards.length === 0" class="empty-stack">
      <div class="empty-icon">🕊️</div>
      <p>You have viewed all requests.</p>
      <button @click="$emit('refresh')" class="refresh-btn">Refresh</button>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'

export default {
  name: 'PrayerCardStack',
  props: {
    cards: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['swipe-right', 'swipe-left', 'refresh'],
  setup(props, { emit }) {
    // We only interact with the TOP card (index 0)
    const touchStartX = ref(0)
    const touchCurrentX = ref(0)
    const isDragging = ref(false)

    // Compute visible stack (reverse order for z-index stacking context usually,
    // but in absolute positioning, last is on top. We want index 0 on top.)
    // Actually, simple way: Render only first 3.
    // CSS z-index: index 0 -> z-index 10, index 1 -> z-index 9...

    // We operate on a copy or just use the props? props is reactive array.
    // We can just trust the parent handles removal.

    const visibleCards = computed(() => {
      return props.cards.slice(0, 3)
    })

    const onTouchStart = (e, index) => {
      if (index !== 0) return // Only top card
      touchStartX.value = e.touches[0].clientX
      touchCurrentX.value = touchStartX.value
      isDragging.value = true
    }

    const onTouchMove = (e, index) => {
      if (!isDragging.value || index !== 0) return
      touchCurrentX.value = e.touches[0].clientX
    }

    const onTouchEnd = (e, index) => {
      if (!isDragging.value || index !== 0) return
      isDragging.value = false

      const diff = touchCurrentX.value - touchStartX.value
      const threshold = 100 // px to swipe

      if (diff > threshold) {
        emit('swipe-right', props.cards[0])
      } else if (diff < -threshold) {
        emit('swipe-left', props.cards[0])
      }

      // Reset
      touchStartX.value = 0
      touchCurrentX.value = 0
    }

    const getCardStyle = (index) => {
      // Base Styles
      let style = {
        zIndex: 10 - index,
        transform: `scale(${1 - index * 0.05}) translateY(${index * 15}px)`,
        opacity: 1 - index * 0.2, // Fade out back cards
      }

      // Interaction for Top Card (index 0)
      if (index === 0 && isDragging.value) {
        const diff = touchCurrentX.value - touchStartX.value
        const rotate = diff * 0.05 // Slight rotation
        style.transform = `translateX(${diff}px) rotate(${rotate}deg)`
        style.transition = 'none' // Immediate follow
      } else {
        style.transition = 'transform 0.3s ease, opacity 0.3s ease'
      }

      return style
    }

    const getHintOpacity = (side, index) => {
      if (index !== 0 || !isDragging.value) return 0
      const diff = touchCurrentX.value - touchStartX.value

      if (side === 'right' && diff > 0) return Math.min(diff / 100, 1)
      if (side === 'left' && diff < 0) return Math.min(Math.abs(diff) / 100, 1)
      return 0
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString()
    }

    return {
      visibleCards,
      getCardStyle,
      onTouchStart,
      onTouchMove,
      onTouchEnd,
      getHintOpacity,
      formatDate,
    }
  },
}
</script>

<style scoped>
.stack-container {
  position: relative;
  width: 100%;
  height: 60vh; /* Adjust as needed */
  perspective: 1000px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cards-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.card-slot {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  will-change: transform;
}

.swipe-card {
  width: 90%;
  max-width: 360px;
  height: 480px; /* Fixed height card */
  /* Glassmorphism implicitly usually, but defined here explicit or via utility */
  background: rgba(20, 20, 20, 0.85);
  backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 215, 0, 0.15);
  border-radius: 24px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.card-top {
  display: flex;
  justify-content: space-between;
  opacity: 0.7;
  font-size: 0.8rem;
}

.category-badge {
  color: var(--gold-primary);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 700;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: center;
}

.card-author {
  font-family: var(--font-heading);
  color: var(--starlight);
  margin-bottom: 20px;
}

.card-text {
  font-size: 1.2rem;
  line-height: 1.6;
  font-style: italic;
  color: var(--starlight-dim);
}

.card-bottom {
  position: relative;
  height: 40px;
}

/* Hints */
.swipe-hint {
  position: absolute;
  top: 0;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 800;
  letter-spacing: 2px;
}

.swipe-hint.left {
  left: 0;
  color: #ff4d4d;
  border: 2px solid #ff4d4d;
  transform: rotate(-15deg);
}

.swipe-hint.right {
  right: 0;
  color: var(--success);
  border: 2px solid var(--success);
  transform: rotate(15deg);
}

/* Empty State */
.empty-stack {
  text-align: center;
  z-index: 0;
}
.empty-icon {
  font-size: 3rem;
  margin-bottom: 10px;
}
.refresh-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background: var(--gold-primary);
  color: var(--void-bg);
  border: none;
  border-radius: 20px;
  font-weight: 700;
}

/* Transition for removing cards */
.card-leave-active {
  transition: all 0.5s ease;
}
.card-leave-to {
  transform: translateX(100vw) rotate(30deg) !important;
  opacity: 0;
}
</style>
