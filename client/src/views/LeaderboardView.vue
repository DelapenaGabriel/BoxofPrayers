<script setup>
import Leaderboard from '@/components/HomeComponents/Leaderboard.vue'
</script>

<template>
  <div class="leaderboard-page">
    <div class="bg-effects">
      <div class="light-1"></div>
      <div class="light-2"></div>
    </div>

    <div class="content">
      <h1 class="page-title">Guardians of Faith</h1>
      <p class="page-subtitle">Honoring those who carry the light of intercession</p>

      <div class="board-wrapper">
        <Leaderboard />
      </div>
    </div>
  </div>
</template>

<style scoped>
.leaderboard-page {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  padding-top: 100px; /* Space for Navbar */
  position: relative;
  overflow-x: hidden;
}

.bg-effects {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  pointer-events: none;
  z-index: 0;
}

.light-1,
.light-2 {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.light-1 {
  background: #ffd700;
  top: -100px;
  left: -100px;
  animation: float 10s infinite alternate;
}

.light-2 {
  background: #ff8c00;
  bottom: -100px;
  right: -100px;
  animation: float 12s infinite alternate-reverse;
}

.content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  padding: 20px;
}

.page-title {
  font-family: var(--font-heading);
  font-size: 3.5rem;
  font-weight: 400;
  margin: 0;
  /* Luminous White Style */
  color: #ffffff;
  text-shadow:
    0 0 20px rgba(255, 255, 255, 0.6),
    0 0 40px rgba(255, 215, 0, 0.3);
  letter-spacing: -2px;
}

.page-subtitle {
  font-family: var(--font-body);
  color: #a89f91;
  margin-bottom: 40px;
  text-align: center;
  font-style: italic;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
}

.board-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

@keyframes float {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 30px);
  }
}

@media (max-width: 600px) {
  .page-title {
    font-size: 2.2rem;
  }

  .light-1,
  .light-2 {
    width: 300px;
    height: 300px;
    opacity: 0.12;
    filter: blur(60px);
  }
  .light-1 {
    top: -50px;
    left: -50px;
  }
  .light-2 {
    bottom: -50px;
    right: -50px;
  }
}
</style>
