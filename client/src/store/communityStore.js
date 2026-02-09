import { defineStore } from 'pinia'
import api from '@/services/api'
import CommunityService from '@/services/CommunityService'
import { useAuthStore } from '@/store/authStore'

export const useCommunityStore = defineStore('community', {
  state: () => ({
    posts: [],
    loading: false,
    error: null,
    hasMore: true,
    page: 0,
    limit: 10,
  }),
  actions: {
    async fetchFeed(reset = false) {
      if (reset) {
        this.page = 0
        this.posts = []
        this.hasMore = true
      }

      if (!this.hasMore && !reset) return

      this.loading = true
      this.error = null

      try {
        const offset = this.page * this.limit
        const response = await api.get('/api/community/posts', {
          params: { limit: this.limit, offset },
        })

        const newPosts = response.data
        if (newPosts.length < this.limit) {
          this.hasMore = false
        }

        if (reset) {
          this.posts = newPosts
        } else {
          // Dedup just in case
          const existingIds = new Set(this.posts.map((p) => p.id))
          const uniqueNewPosts = newPosts.filter((p) => !existingIds.has(p.id))
          this.posts = [...this.posts, ...uniqueNewPosts]
        }

        this.page++
        this.error = null // Clear error on successful fetch
      } catch (err) {
        console.error('Failed to fetch community feed', err)
        // Only set error if it's an actual failure, not an empty result
        if (err.response?.status === 401) {
          this.error = 'Please log in to view the community feed.'
        } else if (err.response?.status >= 500) {
          this.error = 'Server error. Please try again later.'
        } else if (!err.response) {
          this.error = 'Unable to connect. Please check your internet connection.'
        } else {
          this.error = 'Failed to load posts. Please try again.'
        }
      } finally {
        this.loading = false
      }
    },

    async createPost(postData) {
      try {
        // postData can be { content, media, originalPostId }
        // If it's a repost, backend expects originalPostId
        const response = await CommunityService.createPost(postData)
        this.posts.unshift(response.data)
        return response.data
      } catch (err) {
        console.error('Store: Failed to create post', err)
        throw err
      }
    },

    async deletePost(postId) {
      try {
        await api.delete(`/api/community/posts/${postId}`)
        this.posts = this.posts.filter((p) => p.id !== postId)
      } catch (err) {
        console.error('Failed to delete post', err)
        throw err
      }
    },

    async updatePost(postId, postData) {
      try {
        const response = await CommunityService.updatePost(postId, postData)
        const post = this.posts.find((p) => p.id === postId)
        if (post) {
          Object.assign(post, response.data)
        }
        return response.data
      } catch (err) {
        console.error('Failed to update post', err)
        throw err
      }
    },

    async addReaction(postId, type = 'AMEN') {
      const post = this.posts.find((p) => p.id === postId)
      if (post) {
        // Optimistic update
        if (!post.likedByCurrentUser || type !== 'AMEN') {
          // Simple logic for now
          // Assuming AMEN is the only count we track explicitly for like button
          // If we have multiple types, we need more complex logic.
          // For now, let's assume simple toggle-like behavior for AMEN
          if (type === 'AMEN' && !post.likedByCurrentUser) {
            post.amenCount++
            post.likedByCurrentUser = true
          }
        }
      }

      try {
        await api.post(`/api/community/posts/${postId}/react`, null, {
          params: { type },
        })
      } catch (err) {
        console.error('Failed to react', err)
        // Revert optimistic update
        if (post) {
          if (type === 'AMEN' && post.likedByCurrentUser) {
            post.amenCount--
            post.likedByCurrentUser = false
          }
        }
      }
    },

    async removeReaction(postId, type = 'AMEN') {
      const post = this.posts.find((p) => p.id === postId)
      if (post) {
        // Optimistic update
        if (type === 'AMEN' && post.likedByCurrentUser) {
          post.amenCount--
          post.likedByCurrentUser = false
        }
      }

      try {
        await api.delete(`/api/community/posts/${postId}/react`, {
          params: { type },
        })
      } catch (err) {
        console.error('Failed to remove reaction', err)
        // Revert
        if (post) {
          if (type === 'AMEN' && !post.likedByCurrentUser) {
            post.amenCount++
            post.likedByCurrentUser = true
          }
        }
      }
    },

    async fetchComments(postId) {
      try {
        const response = await api.get(`/api/community/posts/${postId}/comments`)
        return response.data
      } catch (err) {
        console.error('Failed to fetch comments', err)
        throw err
      }
    },

    async addComment(postId, content) {
      try {
        const response = await api.post(`/api/community/posts/${postId}/comments`, {
          content,
        })
        // Update local post comment count
        const post = this.posts.find((p) => p.id === postId)
        if (post) {
          post.commentCount++
        }
        return response.data
      } catch (err) {
        console.error('Failed to add comment', err)
        throw err
      }
    },

    async updateComment(postId, commentId, content) {
      try {
        const response = await api.put(`/api/community/comments/${commentId}`, {
          content,
        })
        // Update local state if we were tracking comments in store (we aren't really, but good to have)
        return response.data
      } catch (err) {
        console.error('Failed to update comment', err)
        throw err
      }
    },

    async deleteComment(postId, commentId) {
      try {
        await api.delete(`/api/community/comments/${commentId}`)
        // Update local post comment count
        const post = this.posts.find((p) => p.id === postId)
        if (post && post.commentCount > 0) {
          post.commentCount--
        }
      } catch (err) {
        console.error('Failed to delete comment', err)
        throw err
      }
    },
  },
})
