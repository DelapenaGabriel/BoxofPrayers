import api from '@/services/api'

export default {
  login(loginData) {
    return api.post('/api/login', loginData)
  },
  register(registerData) {
    return api.post('/api/register', registerData, { skipAuth: true })
  },
  getProfile() {
    return api.get('/api/users/profile')
  },
}
