<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import api from '@/services/api'
import defaultAvatar from '@/assets/default.jpg'

const timeFrame = ref('weekly') // 'weekly' | 'monthly' | 'all'
const users = ref([])
const isLoading = ref(true)

const fetchLeaderboard = async () => {
  isLoading.value = true
  try {
    const response = await api.get(`/api/users/leaderboard?timeFrame=${timeFrame.value}`)
    users.value = response.data
  } catch (error) {
    console.error('Failed to load leaderboard', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchLeaderboard()
})

watch(timeFrame, () => {
  fetchLeaderboard()
})

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}
</script>

<template>
  <div class="leaderboard-container">
    <div class="lb-header">
      <h3 class="lb-title">Guardians of Faith</h3>
      <p class="lb-subtitle">Honoring those who carry the light.</p>
    </div>

    <!-- TABS -->
    <div class="lb-tabs">
      <button
        class="lb-tab"
        :class="{ active: timeFrame === 'weekly' }"
        @click="timeFrame = 'weekly'"
      >
        Weekly
      </button>
      <button
        class="lb-tab"
        :class="{ active: timeFrame === 'monthly' }"
        @click="timeFrame = 'monthly'"
      >
        Monthly
      </button>
      <button class="lb-tab" :class="{ active: timeFrame === 'all' }" @click="timeFrame = 'all'">
        All Time
      </button>
    </div>

    <!-- LIST -->
    <div class="lb-list">
      <div v-if="isLoading" class="loading-state">Loading Guardians...</div>
      <div
        v-else
        v-for="(user, index) in users"
        :key="user.id"
        class="lb-item"
        :class="getRankClass(index)"
      >
        <div class="lb-rank">#{{ index + 1 }}</div>

        <div class="lb-info">
          <div class="lb-user">
            <div class="lb-avatar-wrapper">
              <img
                :src="user.avatar && user.avatar.startsWith('http') ? user.avatar : defaultAvatar"
                class="lb-avatar-img"
              />
            </div>
            <span class="lb-name">{{ user.name }}</span>
            <!-- Halo/Flame for top streak -->
            <span v-if="index === 0" class="halo" title="Top Guardian">😇</span>
          </div>
          <div class="lb-stats">
            <span class="count">{{ user.prayerCount }}</span>
            <span class="label">Lifted</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.leaderboard-container {
  width: 100%;
  max-width: 400px;
  background: rgba(18, 12, 8, 0.6);
  border: 1px solid rgba(255, 215, 0, 0.15);
  border-radius: 20px;
  padding: 25px;
  backdrop-filter: blur(10px);
  font-family: 'Cinzel', serif;
  color: #ffecd1;
}

.lb-header {
  text-align: center;
  margin-bottom: 20px;
}

.lb-title {
  margin: 0;
  color: #e5c37a;
  font-size: 1.2rem;
}

.lb-subtitle {
  font-family: var(--font-body);
  font-size: 0.8rem;
  color: #8a7a5a;
  margin-top: 5px;
  font-style: italic;
}

.lb-tabs {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.lb-tab {
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: #8a7a5a;
  padding: 5px 10px;
  cursor: pointer;
  font-family: 'Cinzel', serif;
  transition: 0.3s;
}

.lb-tab.active {
  color: #e5c37a;
  border-color: #e5c37a;
}

.lb-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.lb-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.03);
  transition: 0.2s;
}

.lb-item:hover {
  background: rgba(255, 255, 255, 0.06);
}

.lb-rank {
  font-weight: 700;
  color: #666;
  width: 30px;
  text-align: center;
}

.rank-gold .lb-rank {
  color: #ffd700;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}
.rank-silver .lb-rank {
  color: #c0c0c0;
}
.rank-bronze .lb-rank {
  color: #cd7f32;
}

.lb-info {
  flex-grow: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.lb-user {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-body);
  font-weight: 600;
  font-size: 0.95rem;
}

.streak-flame {
  font-size: 0.9rem;
  filter: drop-shadow(0 0 5px orange);
}

.halo {
  font-size: 0.9rem;
  filter: drop-shadow(0 0 5px gold);
}

.lb-stats {
  text-align: right;
}

.count {
  display: block;
  font-weight: 700;
  color: #e5c37a;
}

.label {
  display: block;
  font-size: 0.6rem;
  color: #8a7a5a;
  text-transform: uppercase;
}

.loading-state {
  text-align: center;
  color: #8a7a5a;
  font-style: italic;
  padding: 20px;
  font-family: var(--font-body);
}

/* Avatar Styles */
.lb-avatar-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.lb-avatar-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--gold);
}

.lb-avatar-emoji {
  font-size: 1.2rem;
}
</style>
