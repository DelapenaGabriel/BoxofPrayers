import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import MyApp from './App.vue'
import router from './router'
import scrollFade from './directives/scrollFade.js'
import { useAuthStore } from './store/authStore'
import api from './services/api'

import '@fortawesome/fontawesome-free/css/all.css'

/* import fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faCartPlus,
  faTrashCan,
  faXmark,
  faMagnifyingGlass,
  faRotate,
  faTable,
  faGrip,
} from '@fortawesome/free-solid-svg-icons'

/* add icons to the library */
library.add(faCartPlus)
library.add(faTrashCan)
library.add(faXmark)
library.add(faMagnifyingGlass)
library.add(faRotate)
library.add(faTable)
library.add(faGrip)

const app = createApp(MyApp)
const pinia = createPinia()
app.use(pinia)
app.use(router)

import { MotionPlugin } from '@vueuse/motion'
app.use(MotionPlugin)

// Initialize Auth Store AFTER Pinia is set up
const authStore = useAuthStore()

// Set initial header if token exists
const currentToken = authStore.token
if (currentToken) {
  api.defaults.headers.common['Authorization'] = `Bearer ${currentToken}`
}

// Add a response interceptor to handle 401 errors globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token is expired or invalid
      // Only redirect if this wasn't an explicitly public request
      if (!error.config.skipAuth) {
        authStore.logout()
        router.push('/login')
      }
    }
    return Promise.reject(error)
  },
)

app.mount('#app')
app.directive('scroll-fade', scrollFade)
