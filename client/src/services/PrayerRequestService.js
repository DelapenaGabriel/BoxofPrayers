import api from '@/services/api'

export default {
  listPrayerRequests(params) {
    return api.get('/api/prayer-requests', { params })
  },
  getPrayerRequest(requestId) {
    return api.get(`/api/prayer-requests/${requestId}`)
  },
  createPrayerRequest(requestData) {
    return api.post('/api/prayer-requests', requestData)
  },
  updatePrayerRequest(requestId, requestData) {
    return api.put(`/api/prayer-requests/${requestId}`, requestData)
  },
  deletePrayerRequest(requestId) {
    return api.delete(`/api/prayer-requests/${requestId}`)
  },
}
