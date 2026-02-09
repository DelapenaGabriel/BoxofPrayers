import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/authStore'

import HomeView from '../views/HomeView.vue'
import RequestView from '@/views/RequestView.vue'
import PrayerView from '@/views/PrayerView.vue'
import ProfileView from '@/views/ProfileView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import CommunityView from '@/views/CommunityView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import AdminView from '@/views/AdminView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/request',
    name: 'request',
    component: RequestView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/pray',
    name: 'pray',
    component: PrayerView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: '/community',
    name: 'community',
    component: CommunityView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      requiresAuth: false,
    },
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      requiresAuth: false,
    },
  },
]

// Create the router
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
})

router.beforeEach((to) => {
  // Get the Pinia Auth Store
  const authStore = useAuthStore()

  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some((x) => x.meta.requiresAuth)

  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && !authStore.token) {
    return { name: 'login' }
  }
  // Otherwise, do nothing and they'll go to their next destination
})

export default router
