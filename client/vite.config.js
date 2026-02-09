import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'masked-icon.svg'],
      manifest: {
        name: 'Box of Prayers',
        short_name: 'Box of Prayers',
        description: 'A spiritual sanctuary for prayer and connection.',
        theme_color: '#050403',
        background_color: '#050403',
        icons: [
          {
            src: '/logo/logo-background.png',
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: '/logo/logo-background.png',
            sizes: '512x512',
            type: 'image/png',
          },
          {
            src: '/logo/logo-background.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'any maskable',
          },
        ],
      },
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Local backend
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
