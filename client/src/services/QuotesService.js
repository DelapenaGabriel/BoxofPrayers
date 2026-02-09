import api from '@/services/api'

export default {
  getDailyQuote() {
    return api.get('/api/bible-verses/daily')
  },

  getRandomQuote() {
    return api.get('/api/bible-verses/random')
  },
}
