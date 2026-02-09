import api from '@/services/api'
export default {
  listPrayers() {
    return api.get('/api/prayers')
  },
  listPublicPrayers() {
    return api.get('/api/prayers/public')
  },
  getPrayer(prayerId) {
    return api.get(`/api/prayers/${prayerId}`)
  },
  createPrayer(prayerData) {
    return api.post('/api/prayers', prayerData)
  },
  deletePrayer(prayerId) {
    return api.delete(`/api/prayers/${prayerId}`)
  },
}
