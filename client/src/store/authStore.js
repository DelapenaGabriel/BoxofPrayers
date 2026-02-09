import { defineStore } from 'pinia'
import api from '@/services/api'
import AuthService from '@/services/AuthService'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || {},
    feedRefreshTrigger: 0,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    setAuthToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    setUser(user) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.token = ''
      this.user = {}
      delete api.defaults.headers.common['Authorization']
    },
    triggerFeedRefresh() {
      this.feedRefreshTrigger++
    },
    async login(loginData) {
      try {
        const response = await AuthService.login(loginData)
        if (response.data && response.data.token) {
          this.setAuthToken(response.data.token)
          this.setUser(response.data.user)
          return response.data
        }
      } catch (error) {
        throw error
      }
    },
    async register(registerData) {
      try {
        const response = await AuthService.register(registerData)
        return response.data
      } catch (error) {
        throw error
      }
    },
    async fetchUser() {
      try {
        const response = await AuthService.getProfile()
        if (response.data) {
          this.setUser(response.data)
        }
        return response.data
      } catch (error) {
        console.error('Failed to fetch user profile', error)
        // If 401, maybe logout?
        // if (error.response && error.response.status === 401) this.logout()
        throw error
      }
    },
  },
})
