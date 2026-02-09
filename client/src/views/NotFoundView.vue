<template>
  <div class="not-found-container">
    <div class="bg-effects">
      <div class="light-1"></div>
      <div class="light-2"></div>
    </div>

    <div class="content">
     
      <h1 class="title">404</h1>
      <h2 class="subtitle">Path Not Found</h2>
      <p class="description">
        The path you seek is veiled in shadow. Return to the light to continue your journey.
      </p>

      <router-link to="/" class="return-btn"> Return to Sanctuary </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFoundView',
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;700&family=Cinzel:wght@400;700&display=swap');

.not-found-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background-color: #050302;
  overflow: hidden;
  color: #fff;
  font-family: var(--font-body);
}

/* Background Effects (Consistent with other pages) */
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
  filter: blur(100px);
  opacity: 0.1;
}

.light-1 {
  background: #ffd700;
  top: -150px;
  left: -150px;
  animation: float 10s infinite alternate;
}

.light-2 {
  background: #ff8c00;
  bottom: -150px;
  right: -150px;
  animation: float 12s infinite alternate-reverse;
}

@keyframes float {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(40px, 40px);
  }
}

/* Content */
.content {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 2rem;
  max-width: 500px;
}

.icon-wrapper {
  font-size: 5rem;
  margin-bottom: 1rem;
  filter: drop-shadow(0 0 20px rgba(255, 215, 0, 0.2));
  animation: pulse 4s infinite ease-in-out;
}

.title {
  font-family: var(--font-heading);
  font-size: 6rem;
  margin: 0;
  line-height: 1;
  background: linear-gradient(135deg, #fffcf5 0%, #ebb64e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  opacity: 0.9;
}

.subtitle {
  font-family: var(--font-heading);
  font-size: 1.5rem;
  color: #e5c37a;
  margin-bottom: 1.5rem;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.description {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin-bottom: 3rem;
  font-weight: 300;
}

.return-btn {
  display: inline-block;
  padding: 16px 40px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(229, 195, 122, 0.3);
  color: #e5c37a;
  text-decoration: none;
  border-radius: 50px;
  font-family: var(--font-heading);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.return-btn:hover {
  background: rgba(229, 195, 122, 0.1);
  border-color: #e5c37a;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

@media (max-width: 600px) {
  .title {
    font-size: 4rem;
  }
  .subtitle {
    font-size: 1.2rem;
  }
  .light-1,
  .light-2 {
    width: 300px;
    height: 300px;
  }
}
</style>
