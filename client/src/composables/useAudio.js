// src/composables/useAudio.js
import { ref } from 'vue'

// 1. Create the state OUTSIDE the function so it is global/singleton
// This ensures the music keeps playing when you change pages.
const isPlaying = ref(false)
const audio = new Audio('/audio/worship-instrumental.mp3') // Make sure mp3 is in public/audio/

// Configuration
audio.loop = true
audio.volume = 0.3 // Set volume (0.0 to 1.0)

export function useAudio() {
  const playAudio = () => {
    audio.play().catch((err) => {
      console.warn('Audio autoplay blocked (waiting for interaction):', err)
    })
    isPlaying.value = true
  }

  const pauseAudio = () => {
    audio.pause()
    isPlaying.value = false
  }

  const toggleAudio = () => {
    if (isPlaying.value) {
      pauseAudio()
    } else {
      playAudio()
    }
  }

  // Attempt to play immediately (might work if user has interacted with domain before)
  // We use a small timeout to ensure app is mounted
  if (!isPlaying.value) {
    /* We won't force it here globally to avoid errors, 
        we rely on IntroOverlay or Navbar to trigger it. 
        But we CAN return the methods. */
  }

  return {
    isPlaying,
    playAudio,
    pauseAudio,
    toggleAudio,
  }
}
