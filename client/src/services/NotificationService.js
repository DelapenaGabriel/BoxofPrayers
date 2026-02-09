import api from '@/services/api'

export default {
  getMyNotifications() {
    return api.get('/api/notifications')
  },
  getUnreadCount() {
    return api.get('/api/notifications/unread-count')
  },
  markAsRead(id) {
    return api.put(`/api/notifications/${id}/read`)
  },
}
