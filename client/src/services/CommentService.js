import api from '@/services/api'

export default {
  getComments(prayerRequestId) {
    return api.get(`/api/prayer-requests/${prayerRequestId}/comments`)
  },

  createComment(comment) {
    return api.post('/api/comments', comment)
  },

  getAllComments() {
    return api.get('/api/comments')
  },

  deleteComment(commentId) {
    return api.delete(`/api/comments/${commentId}`)
  },

  updateComment(commentId, content) {
    // Backend now supports PUT /api/comments/{id}
    return api.put(`/api/comments/${commentId}`, { content })
  },
}
