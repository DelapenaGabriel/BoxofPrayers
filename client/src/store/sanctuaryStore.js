import { defineStore } from 'pinia'

export const useSanctuaryStore = defineStore('sanctuary', {
  state: () => ({
    count: 0,
    maxStars: 150,
    isImmersive: false,
  }),

  getters: {
    // Returns the count, capped at maxStars for visual performance
    starCount: (state) => Math.min(state.count, state.maxStars),
  },

  actions: {
    // Called when initial data is fetched
    setStarCount(n) {
      this.count = n
    },

    // Called when user lights a candle
    addStar() {
      if (this.count < this.maxStars) {
        this.count++
      }
    },

    // Toggle Immersive Mode
    toggleImmersive() {
      this.isImmersive = !this.isImmersive
    },
  },
})
