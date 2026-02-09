import api from '@/services/api'

export default {
  getMyBadges() {
    return api.get('/api/badges/my')
  },
  getUserBadges(userId) {
    return api.get(`/api/badges/${userId}`)
  },
}
