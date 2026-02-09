import axios from 'axios'
import { useAuthStore } from '@/store/authStore'

// Create an axios instance
const api = axios.create({
  // If in production, use the environment variable (VITE_REMOTE_API).
  // If in development, use an empty string to leverage the Vite proxy in vite.config.js.
  baseURL: import.meta.env.PROD ? import.meta.env.VITE_REMOTE_API : '',
})

// Add request interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token

    if (token && !config.skipAuth) {
      config.headers.Authorization = `Bearer ${token}`
    } else {
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

export default api
