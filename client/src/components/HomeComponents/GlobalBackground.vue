<script setup>
import { onMounted, onUnmounted, reactive, watch } from 'vue'
import { useSanctuaryStore } from '@/store/sanctuaryStore' // IMPORT STORE

// --- CONFIGURATION ---
const INTERACTION_RADIUS = 300
const FORCE_FACTOR = 10

// --- STATE ---
const store = useSanctuaryStore() // INITIALIZE STORE

const state = reactive({
  width: 0,
  height: 0,
  mouseX: -500,
  mouseY: -500,
  particles: [], // This will now represent the Stars
})

let animationFrameId = null

// --- HELPER: Create Single Particle ---
const createParticle = (isNew = false) => {
  return {
    // Random position
    x: Math.random() * state.width,
    y: Math.random() * state.height,
    originX: Math.random() * state.width,
    originY: Math.random() * state.height,

    // Size and Alpha
    baseSize: Math.random() * 3 + 3.5,
    baseAlpha: Math.random() * 0.3 + 0.1,

    // Movement
    vx: (Math.random() - 0.5) * 0.3,
    vy: (Math.random() - 0.5) * 0.3,
    dx: 0,
    dy: 0,

    // Animation state
    // If it's a new star added by user action, start at size 0 to "grow" in.
    // If it's initial load, start at 0 so they fade in together.
    currentSize: 0,
    currentAlpha: 0,
    sparklePhase: Math.random() * Math.PI * 2,
  }
}

// --- INITIALIZATION ---
const initParticles = () => {
  state.width = window.innerWidth
  state.height = window.innerHeight

  // Clear and rebuild based on CURRENT store count
  state.particles = []
  for (let i = 0; i < store.starCount; i++) {
    state.particles.push(createParticle())
  }
}

// --- WATCHER: Sync Store to Visuals ---
watch(
  () => store.starCount,
  (newCount, oldCount) => {
    const currentLength = state.particles.length
    const difference = newCount - currentLength

    if (difference > 0) {
      // Add new stars (Prayers received)
      for (let i = 0; i < difference; i++) {
        state.particles.push(createParticle(true))
      }
    } else if (difference < 0) {
      // Remove stars (Decay or cleanup)
      // Removing from the start (oldest particles)
      state.particles.splice(0, Math.abs(difference))
    }
  },
)

// --- ANIMATION LOOP ---
const animate = () => {
  const time = Date.now() * 0.003

  state.particles.forEach((p) => {
    // 1. Natural Drift
    p.originX += p.vx
    p.originY += p.vy

    // Wrap around screen logic
    if (p.originX < -50) p.originX = state.width + 50
    if (p.originX > state.width + 50) p.originX = -50
    if (p.originY < -50) p.originY = state.height + 50
    if (p.originY > state.height + 50) p.originY = -50

    // 2. Interaction
    const distX = p.originX - state.mouseX
    const distY = p.originY - state.mouseY
    const dist = Math.sqrt(distX * distX + distY * distY)

    // Default Idle State
    let targetSize = p.baseSize
    let targetAlpha = p.baseAlpha + Math.sin(time + p.sparklePhase) * 0.1

    if (dist < INTERACTION_RADIUS) {
      const intensity = 1 - dist / INTERACTION_RADIUS
      const angle = Math.atan2(distY, distX)
      const force = intensity * FORCE_FACTOR

      // PHYSICS: Attraction
      p.dx -= Math.cos(angle) * force
      p.dy -= Math.sin(angle) * force

      // VISUALS: React to mouse
      targetSize = p.baseSize * (1 + intensity * 1.5)
      targetAlpha = 0.6 + intensity * 0.4
    }

    // Friction
    p.dx *= 0.92
    p.dy *= 0.92

    // Smooth transition (This makes new stars "fade/grow" in)
    p.currentSize = p.currentSize * 0.9 + targetSize * 0.1
    p.currentAlpha = p.currentAlpha * 0.9 + targetAlpha * 0.1

    // Position
    p.x = p.originX + p.dx
    p.y = p.originY + p.dy
  })

  animationFrameId = requestAnimationFrame(animate)
}

// --- EVENTS ---
const handleMouseMove = (e) => {
  state.mouseX = e.clientX
  state.mouseY = e.clientY

  const wrapper = document.querySelector('.sanctuary-wrapper')
  if (wrapper) {
    wrapper.style.setProperty('--cursor-x', `${e.clientX}px`)
    wrapper.style.setProperty('--cursor-y', `${e.clientY}px`)
  }
}

const handleResize = () => {
  state.width = window.innerWidth
  state.height = window.innerHeight
}

onMounted(() => {
  // Set canvas size and create initial particles
  handleResize()
  initParticles()

  // Start loop
  animate()

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  cancelAnimationFrame(animationFrameId)
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <div class="sanctuary-wrapper">
    <!-- Background Vignette -->
    <div class="bg-vignette"></div>

    <!-- The Cross -->
    <div class="cross-container">
      <div class="cross-glow-back"></div>
      <div class="cross-beam vertical"></div>
      <div class="cross-beam horizontal"></div>
      <div class="cross-center-flare"></div>
    </div>

    <!-- Particles (Now mapped to Store Count) -->
    <div class="particles-layer">
      <div
        v-for="(p, i) in state.particles"
        :key="i"
        class="particle"
        :style="{
          transform: `translate(${p.x}px, ${p.y}px)`,
          width: `${p.currentSize}px`,
          height: `${p.currentSize}px`,
          opacity: p.currentAlpha,
          /* Glow relative to size - creates a sharp starry look */
          boxShadow: `0 0 ${p.currentSize * 4}px ${p.currentSize}px rgba(255, 230, 180, ${p.currentAlpha})`,
        }"
      ></div>
    </div>

    <!-- Cursor Light Source -->
    <div class="cursor-aura"></div>

    <!-- App Content -->
    <div class="content-slot">
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
/* --- MAIN CONTAINER --- */
.sanctuary-wrapper {
  --cursor-x: -100px;
  --cursor-y: -100px;

  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  min-height: 100vh;
  background-color: #050302;
  overflow: hidden;
  z-index: -1;
}

/* --- 1. BACKGROUND --- */
.bg-vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, #1f1206 0%, #000000 95%);
  z-index: 0;
  pointer-events: none;
}

/* --- 2. THE GLOWING CROSS --- */
.cross-container {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cross-glow-back {
  position: absolute;
  top: 30%;
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(255, 140, 0, 0.12), transparent 70%);
  filter: blur(50px);
  opacity: 0.8;
  animation: pulseBack 8s ease-in-out infinite;
}

.cross-beam {
  position: absolute;
  background: linear-gradient(to bottom, transparent, #ffcc66, transparent);
  border-radius: 50%;
  box-shadow: 0 0 40px 8px rgba(255, 180, 50, 0.4);
  animation: breathe 6s ease-in-out infinite;
}

.vertical {
  width: 3px;
  height: 350px;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    #ffd700 40%,
    #ffd700 60%,
    transparent 100%
  );
}

.horizontal {
  width: 220px;
  height: 3px;
  transform: translateY(-60px);
  background: linear-gradient(to right, transparent 0%, #ffd700 40%, #ffd700 60%, transparent 100%);
}

.cross-center-flare {
  position: absolute;
  width: 90px;
  height: 90px;
  transform: translateY(-60px);
  background: radial-gradient(
    circle,
    rgba(255, 255, 230, 1) 0%,
    rgba(255, 180, 20, 0.6) 40%,
    transparent 70%
  );
  filter: blur(8px);
  mix-blend-mode: screen;
  animation: flicker 4s ease-in-out infinite;
}

@keyframes breathe {
  0%,
  100% {
    opacity: 0.7;
    filter: drop-shadow(0 0 10px rgba(255, 160, 0, 0.4));
  }
  50% {
    opacity: 1;
    filter: drop-shadow(0 0 25px rgba(255, 200, 50, 0.7));
  }
}

@keyframes pulseBack {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.15);
    opacity: 0.7;
  }
}

@keyframes flicker {
  0%,
  100% {
    opacity: 0.8;
    transform: translateY(-60px) scale(1);
  }
  25% {
    opacity: 1;
    transform: translateY(-60px) scale(1.1);
  }
  50% {
    opacity: 0.7;
    transform: translateY(-60px) scale(0.95);
  }
  75% {
    opacity: 0.9;
    transform: translateY(-60px) scale(1.05);
  }
}

/* --- 3. PARTICLES --- */
.particles-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}

.particle {
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 50%;
  background: #dfc851;
  will-change: transform, opacity, width, height, box-shadow;
}

/* --- 5. CONTENT --- */
.content-slot {
  position: relative;
  z-index: 20;
  width: 100%;
  height: 100%;
}

/* --- DESKTOP ENHANCEMENTS --- */
/* --- DESKTOP ENHANCEMENTS --- */
@media (min-width: 1024px) {
  .vertical {
    height: 550px; /* Bigger */
    width: 4px;
  }

  .horizontal {
    width: 380px; /* Bigger */
    height: 4px;
  }

  .cross-center-flare {
    width: 160px;
    height: 160px;
  }

  .cross-glow-back {
    width: 500px;
    height: 500px;
  }
}
</style>
