<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  // Interactive mode (clickable to open) vs Target mode (just visual)
  interactive: {
    type: Boolean,
    default: true,
  },
  pulse: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['open'])
const router = useRouter()
const boxRef = ref(null)

const handleClick = () => {
  if (props.interactive) {
    emit('open')
    // Or navigate: router.push('/pray')
  }
}
</script>

<template>
  <div
    ref="boxRef"
    class="prayer-box-container"
    @click="handleClick"
    :class="{ interactive: interactive, pulsing: pulse }"
    v-motion
    :initial="{ y: 0, scale: 1 }"
    :hovered="{ y: -10, scale: 1.05 }"
    :enter="{ opacity: 1, scale: 1 }"
  >
    <!-- The Box Vessel -->
    <div class="vessel-glass">
      <div class="inner-light"></div>
      <div class="lid"></div>

      <!-- Particles inside (mock representation of prayers) -->
      <div class="particles">
        <div class="p p1"></div>
        <div class="p p2"></div>
        <div class="p p3"></div>
      </div>
    </div>

    <div class="label" v-if="interactive">
      <span class="icon">✨</span>
      <span class="text">Open the Vessel</span>
    </div>
  </div>
</template>

<style scoped>
.prayer-box-container {
  position: relative;
  width: 120px;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  perspective: 600px;
}

.interactive {
  cursor: pointer;
}

.vessel-glass {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.2),
    inset 0 0 20px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  position: relative;
  overflow: hidden;
  transform-style: preserve-3d;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
}

.interactive:hover .vessel-glass {
  box-shadow:
    0 20px 50px rgba(255, 215, 0, 0.15),
    inset 0 0 30px rgba(255, 215, 0, 0.1);
  border-color: rgba(255, 215, 0, 0.4);
}

.pulsing .vessel-glass {
  animation: pulse-receive 0.6s ease-out;
}

@keyframes pulse-receive {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 rgba(255, 215, 0, 0);
  }
  50% {
    transform: scale(1.1);
    box-shadow: 0 0 40px rgba(255, 215, 0, 0.4);
  }
  100% {
    transform: scale(1);
  }
}

/* Inner Light */
.inner-light {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.6) 0%, transparent 70%);
  filter: blur(10px);
  opacity: 0.6;
}

/* Lid (Stylized top border) */
.lid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 20px;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.1), transparent);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.particles .p {
  position: absolute;
  background: #fff;
  border-radius: 50%;
  opacity: 0.6;
  animation: float-p 4s infinite ease-in-out;
}

.p1 {
  width: 4px;
  height: 4px;
  top: 60%;
  left: 30%;
  animation-delay: 0s;
}
.p2 {
  width: 3px;
  height: 3px;
  top: 40%;
  left: 60%;
  animation-delay: 1.2s;
}
.p3 {
  width: 5px;
  height: 5px;
  top: 70%;
  left: 50%;
  animation-delay: 2.5s;
}

@keyframes float-p {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.label {
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.interactive:hover .label {
  opacity: 1;
}

.text {
  font-family: 'Cinzel', serif;
  font-size: 0.75rem;
  color: #e5c37a;
  letter-spacing: 1px;
  text-transform: uppercase;
}
</style>
