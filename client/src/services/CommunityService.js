import api from '@/services/api'

export default {
  getAllPosts() {
    return api.get('/api/community/posts', {
      params: { limit: 1000, offset: 0 }, // Admin view - get all posts
    })
  },

  getPostById(postId) {
    return api.get(`/api/community/posts/${postId}`)
  },

  createPost(postData) {
    return api.post('/api/community/posts', postData)
  },

  updatePost(postId, postData) {
    return api.put(`/api/community/posts/${postId}`, postData)
  },

  deletePost(postId) {
    return api.delete(`/api/community/posts/${postId}`)
  },

  getComments(postId) {
    return api.get(`/api/community/posts/${postId}/comments`)
  },

  createComment(postId, content) {
    return api.post(`/api/community/posts/${postId}/comments`, { content })
  },

  deleteComment(commentId) {
    return api.delete(`/api/community/comments/${commentId}`)
  },

  updateComment(commentId, content) {
    return api.put(`/api/community/comments/${commentId}`, { content })
  },
}
