<template>
  <Teleport to="body">
    <Transition name="sheet">
      <div v-if="isOpen" class="bottom-sheet-overlay" @click.self="close">
        <div
          ref="sheetContent"
          class="bottom-sheet-content"
          @touchstart="handleTouchStart"
          @touchmove="handleTouchMove"
          @touchend="handleTouchEnd"
        >
          <!-- DRAG HANDLE -->
          <div class="sheet-handle-area" @click="close">
            <div class="sheet-handle"></div>
          </div>

          <slot></slot>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
export default {
  name: 'BottomSheet',
  props: {
    isOpen: {
      type: Boolean,
      required: true,
    },
    title: {
      type: String,
      default: '',
    },
  },
  emits: ['close'],
  data() {
    return {
      startY: 0,
      currentY: 0,
      isDragging: false,
    }
  },
  methods: {
    close() {
      this.$emit('close')
    },
    handleTouchStart(e) {
      const touch = e.touches[0]
      this.startY = touch.clientY
      this.currentY = touch.clientY // Initialize currentY

      // Check if we are touching the content (not the handle)
      // and if that content is scrollable and not at the top.
      const content = e.target.closest('.bottom-sheet-content')
      const handle = e.target.closest('.sheet-handle-area')

      if (handle) {
        this.isDragging = true
      } else if (content) {
        // If content is scrolled down, don't drag sheet
        if (content.scrollTop > 0) {
          this.isDragging = false
        } else {
          // At top, allow drag if moving down?
          // We don't know the direction yet, so we assume yes until move checks delta.
          // However, to be safe, we only really want to drag via handle or top area.
          // Let's rely mainly on handle or if they are at the very top of content.
          this.isDragging = true
        }
      }
    },
    handleTouchMove(e) {
      if (!this.isDragging) return

      const touch = e.touches[0]
      this.currentY = touch.clientY
      const deltaY = this.currentY - this.startY

      // Only allow dragging DOWN
      if (deltaY > 0) {
        // Prevent default scrolling when dragging the sheet
        if (e.cancelable) e.preventDefault()

        // Also stop propagation to prevent parent scrolling
        e.stopPropagation()

        // Visual feedback: Transform the sheet (optional, but "Modern")
        const sheet = this.$refs.sheetContent
        if (sheet) {
          sheet.style.transform = `translateY(${deltaY}px)`
          sheet.style.transition = 'none'
        }
      } else {
        // Moving up? If we are at scrollTop 0 and moving up, that's just content scroll?
        // If we strictly started as "dragging", we might want to cancel it if we go UP?
        // But usually sheet doesn't go up past 0.
      }
    },
    handleTouchEnd(e) {
      if (!this.isDragging) return
      this.isDragging = false
      const deltaY = this.currentY - this.startY

      const sheet = this.$refs.sheetContent

      // Reset style
      if (sheet) {
        sheet.style.transform = ''
        sheet.style.transition = 'transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1)'
      }

      // Threshold to close (e.g. 100px)
      if (deltaY > 100 && this.currentY !== 0) {
        // Ensure we actually moved
        this.close()
      }

      this.startY = 0
      this.currentY = 0
    },
  },
}
</script>

<style scoped>
.bottom-sheet-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  z-index: 200001; /* Must be > BottomTabBar z-index (100,000) */
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bottom-sheet-content {
  width: 100%;
  max-width: 600px; /* Tablet constraint */
  max-height: 90vh;
  background: rgba(18, 14, 10, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.6);
  padding: 10px 20px 40px 20px;
  padding-bottom: calc(20px + env(safe-area-inset-bottom));
  overflow-y: auto;
  position: relative;
}

/* HANDLE */
.sheet-handle-area {
  width: 100%;
  height: 30px; /* Slightly bigger touch target */
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: grab;
  margin-bottom: 5px;
  touch-action: none; /* Critical for gesture handling */
}

.sheet-handle {
  width: 40px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

/* TRANSITIONS */
.sheet-enter-active,
.sheet-leave-active {
  transition: opacity 0.3s ease;
}

.sheet-enter-from,
.sheet-leave-to {
  opacity: 0;
}

.sheet-enter-active .bottom-sheet-content,
.sheet-leave-active .bottom-sheet-content {
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.sheet-enter-from .bottom-sheet-content,
.sheet-leave-to .bottom-sheet-content {
  transform: translateY(100%);
}
</style>
