<template>
  <div class="lumina-profile-wrapper">
    <!-- Floating Streak Pill (Top Right) -->
    <div class="floating-streak">
      <span class="streak-text">{{ streakCount }} DAY STREAK</span>
      <span class="streak-emoji">🔥</span>
    </div>

    <!-- Background Effects (Standardized) -->
    <div class="bg-effects">
      <div class="light-1"></div>
      <div class="light-2"></div>
    </div>

    <!-- The Light Beam Cross Effect (Pure CSS) -->
    <div class="divine-cross">
      <div class="vertical-beam"></div>
      <div class="horizontal-beam"></div>
    </div>

    <div class="content-container">
      <!-- 1. Profile Header -->
      <header class="profile-header">
        <!-- ERROR NOTIFICATION -->
        <transition name="fade">
          <div v-if="errorMessage" class="error-notification-profile">
            <span>⚠️ {{ errorMessage }}</span>
            <button class="close-error" @click="errorMessage = ''">x</button>
          </div>
        </transition>

        <div class="avatar-container">
          <div
            class="avatar-box"
            :style="{
              backgroundImage: userProfileImage
                ? `url(${userProfileImage})`
                : `url(${defaultAvatar})`,
              backgroundSize: 'cover',
            }"
          ></div>

          <!-- Edit Profile Trigger -->
          <button class="edit-profile-trigger" @click="openEditProfile">
            <i class="fas fa-pencil-alt"></i>
          </button>
        </div>
        <h1 class="profile-name">{{ user.name }}</h1>

        <!-- Words of Wisdom -->
        <div class="words-of-wisdom">
          <p class="quote-text shimmer-text">"{{ dailyQuote.text }}"</p>
          <span class="quote-author">— {{ dailyQuote.author }}</span>
        </div>
      </header>

      <!-- 2. Main Stats Grid -->
      <section class="stats-grid">
        <div class="stat-card">
          <label>REQUESTS</label>
          <div class="value">{{ myRequests.length }}</div>
          <span class="subtext">Shared</span>
        </div>
        <div class="stat-card">
          <label>BADGES</label>
          <div class="value">{{ badges.length }}</div>
          <span class="subtext">Earned</span>
        </div>
        <div class="stat-card">
          <label>MY PRAYERS</label>
          <div class="value">{{ totalPrayers }}</div>
          <span class="subtext">Total Prayers</span>
        </div>
      </section>

      <!-- Inbox Panel (Unified) -->
      <!-- Inbox Panel (Unified) -->
      <section class="notifications-section">
        <div class="inbox-panel">
          <!-- Header Inside Panel -->
          <div class="inbox-header">
            <h2 class="inbox-title">Whispers</h2>

            <!-- Tabs -->
            <div class="inbox-tabs">
              <button
                class="tab-btn"
                :class="{ active: activeTab === 'unread' }"
                @click="activeTab = 'unread'"
              >
                Unread
                <span v-if="unreadCount > 0" class="tab-badge">{{ unreadCount }}</span>
              </button>
              <button
                class="tab-btn"
                :class="{ active: activeTab === 'all' }"
                @click="activeTab = 'all'"
              >
                All
              </button>
            </div>

            <div class="inbox-actions">
              <button
                v-if="unreadCount > 0 && activeTab === 'unread'"
                class="mark-all-btn"
                @click="markAllRead"
              >
                Mark all read
              </button>
            </div>
          </div>

          <!-- List Items -->
          <div class="inbox-list" v-if="filteredNotifications.length > 0">
            <div
              v-for="n in filteredNotifications"
              :key="n.id"
              class="inbox-item"
              :class="{ 'is-unread': !n.read }"
            >
              <!-- Avatar -->
              <div class="item-icon-wrapper avatar-wrapper">
                <img
                  v-if="n.senderProfileImage"
                  :src="n.senderProfileImage"
                  alt="User"
                  class="sender-avatar"
                  @error="$event.target.src = defaultAvatar"
                />
                <span v-else class="item-icon">{{ n.senderProfileImage }}</span>
              </div>

              <div class="item-content">
                <p class="item-msg">{{ n.message }}</p>
                <span class="item-time">{{ formatTimeAgo(n.createdAt) }}</span>
              </div>

              <button
                v-if="!n.read"
                class="item-action-btn"
                @click="markRead(n.id)"
                title="Mark Read"
              >
                <i class="fas fa-check"></i>
              </button>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="inbox-empty-state">
            <div class="empty-icon">📭</div>
            <p v-if="activeTab === 'unread'">All caught up! No new whispers.</p>
            <p v-else>No notifications yet.</p>
          </div>
        </div>
      </section>

      <!-- 3. Badges Section -->
      <section class="milestones-section" v-if="badges.length > 0">
        <h2 class="section-heading">MILESTONES (BADGES)</h2>
        <div class="milestones-grid">
          <div v-for="b in badges" :key="b.id" class="milestone-card">
            <div class="m-icon">
              <img v-if="b.iconUrl" :src="b.iconUrl" :alt="b.name" class="badge-img" />
              <span v-else>✨</span>
            </div>
            <h3 class="m-title">{{ b.name }}</h3>
            <p class="m-desc">{{ b.description }}</p>
          </div>
        </div>
      </section>

      <!-- Badges Empty State -->
      <section class="milestones-section" v-else-if="badges.length === 0">
        <h2 class="section-heading">MILESTONES (BADGES)</h2>
        <div class="empty-badges-state">
          <span class="empty-badge-icon">🛡️</span>
          <p>
            Your spiritual journey marks will appear here. <br />
            Continue to pray and encourage others!
          </p>
        </div>
      </section>

      <!-- 4. Your Voice Section (My Requests) -->
      <section class="voice-section">
        <div class="voice-header">
          <h2 class="section-heading">STREAM OF GRACE (HISTORY)</h2>
          <button class="history-btn" @click="toggleHistory">
            {{ showHistory ? 'HIDE HISTORY' : 'SHOW HISTORY' }}
          </button>
        </div>

        <div v-if="!showHistory || myRequests.length === 0" class="voice-card">
          <div class="mailbox-icon">🕊️</div>
          <p class="voice-prompt">"Cast all your anxiety on him because he cares for you."</p>
          <button class="light-prayer-btn" @click="$router.push('/pray')">LIGHT A PRAYER</button>
        </div>

        <div v-if="showHistory && myRequests.length > 0" class="history-timeline">
          <div
            v-for="(req, index) in myRequests"
            :key="req.prayerRequest.id"
            class="history-card-modern"
            :class="{ answered: req.prayerRequest.answered }"
            :style="{ animationDelay: `${index * 100}ms` }"
          >
            <div class="card-glow"></div>
            <div class="card-content">
              <div class="card-top">
                <span class="category-pill">{{ req.prayerRequest.category }}</span>
                <span class="date-text">{{ formatDate(req.prayerRequest.createdAt) }}</span>
                <div class="card-actions-top">
                  <!-- Unified Action Button -->
                  <button
                    class="icon-btn more-btn"
                    @click.stop="openRequestActions(req.prayerRequest)"
                  >
                    <i class="fas fa-ellipsis-h"></i>
                  </button>
                </div>
              </div>

              <h3 class="prayer-text">"{{ req.prayerRequest.content }}"</h3>

              <div class="card-footer">
                <div class="prayer-count">🔥 {{ req.prayerCount || 0 }} Prayers</div>

                <div v-if="req.prayerRequest.answered" class="answered-badge">
                  <span>ANSWERED</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Answer Modal -->
      <!-- Modern Answer Modal -->
      <div v-if="showAnswerModal" class="modal-overlay" @click.self="showAnswerModal = false">
        <div class="modal-glass">
          <div class="modal-header">
            <span class="modal-icon">✨</span>
            <h3>Praise Report</h3>
          </div>

          <p class="modal-subtitle">
            "I will give thanks to you, Lord, with all my heart; I will tell of all your wonderful
            deeds."
          </p>

          <textarea
            v-model="answerContent"
            class="modal-input"
            placeholder="How has God moved in this situation?"
          ></textarea>

          <div class="modal-actions">
            <button class="cancel-btn" @click="showAnswerModal = false">Cancel</button>
            <button class="confirm-btn" @click="submitAnswer">Declare It</button>
          </div>
        </div>
      </div>

      <!-- Unified Action Sheet -->
      <ActionSheet
        :isOpen="showActionSheet"
        title="Prayer Options"
        :actions="sheetActions"
        @close="showActionSheet = false"
      />

      <!-- Edit Profile Bottom Sheet -->
      <ProfileEditBottomSheet
        :isOpen="showEditProfileModal"
        :currentUser="user"
        @close="showEditProfileModal = false"
        @updated="fetchData"
      />

      <!-- Edit Prayer Body Sheet -->
      <RequestBottomSheet
        :isOpen="showRequestSheet"
        :requestToEdit="requestToEdit"
        @close="showRequestSheet = false"
        @submitted="handleRequestSubmitted"
      />

      <!-- New Logout Button -->
      <div class="logout-card-wrapper">
        <button @click="logout" class="logout-pill-btn">LOGOUT</button>
      </div>
    </div>
  </div>
</template>

<script>
import BadgeService from '@/services/BadgeService'
import NotificationService from '@/services/NotificationService'
import PrayerRequestService from '@/services/PrayerRequestService'
import PrayerService from '@/services/PrayerService'
import CloudinaryService from '@/services/CloudinaryService'
import QuotesService from '@/services/QuotesService'
import api from '@/services/api'
import defaultAvatar from '@/assets/default.jpg'
import ProfileEditBottomSheet from '@/components/shared/ProfileEditBottomSheet.vue'
import RequestBottomSheet from '@/components/shared/RequestBottomSheet.vue'
import ActionSheet from '@/components/shared/ActionSheet.vue'

import { useAuthStore } from '@/store/authStore'
import { mapStores } from 'pinia'

export default {
  name: 'ProfileSection',
  components: { ProfileEditBottomSheet, RequestBottomSheet, ActionSheet },

  data() {
    return {
      badges: [],
      notifications: [],
      unreadCount: 0,
      activeTab: 'unread', // 'unread' | 'all'
      myRequests: [],
      showHistory: false,
      showAnswerModal: false,
      showActionSheet: false,
      showEditProfileModal: false,
      showRequestSheet: false,
      requestToEdit: null,
      editForm: {
        name: '',
        displayName: '',
      },
      dailyQuote: {
        text: '',
        author: '',
      },
      selectedRequest: null, // For ActionSheet & Answer Modal
      answerContent: '',
      streakCount: 0,
      totalPrayers: 0,
      errorMessage: '',
      isDeleting: false,
      isSavingProfile: false,
    }
  },
  computed: {
    ...mapStores(useAuthStore),
    filteredNotifications() {
      if (this.activeTab === 'unread') {
        return this.notifications.filter((n) => !n.read)
      }
      return this.notifications
    },
    userInitial() {
      return this.authStore.user?.username
        ? this.authStore.user.username.charAt(0).toUpperCase()
        : 'P'
    },
    defaultAvatar() {
      return defaultAvatar
    },
    user() {
      return this.authStore.user || {}
    },
    userProfileImage() {
      return this.authStore.user?.profileImage || ''
    },
    // Dynamic Actions for the Action Sheet
    sheetActions() {
      if (!this.selectedRequest) return []

      const actions = []
      const req = this.selectedRequest

      // 1. Edit Request Content
      actions.push({
        label: 'Edit Request',
        icon: '📝',
        handler: () => {
          this.requestToEdit = req
          this.showRequestSheet = true
        },
      })

      // 2. Answered Logic
      if (req.answered) {
        actions.push({
          label: 'Edit Praise Report',
          icon: '✨',
          handler: () => {
            this.answerContent = req.answerContent || ''
            this.showAnswerModal = true
          },
        })
      } else {
        actions.push({
          label: 'Mark Answered',
          icon: '🙌',
          handler: () => {
            this.answerContent = ''
            this.showAnswerModal = true
          },
        })
      }

      // 3. Delete Action
      actions.push({
        label: 'Delete Request',
        icon: '🗑️',
        variant: 'danger',
        loading: this.isDeleting,
        handler: async () => {
          if (confirm('Are you sure you want to delete this?')) {
            await this.performDelete(req.id)
          }
        },
      })

      return actions
    },
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      // Get Quote
      QuotesService.getDailyQuote()
        .then((res) => {
          const verse = res.data
          this.dailyQuote = {
            text: verse.text,
            author: `${verse.book} ${verse.chapter}:${verse.verse}`,
          }
        })
        .catch((err) => {
          console.error('Failed to load daily quote', err)
          this.dailyQuote = { text: 'Be still, and know that I am God.', author: 'Psalm 46:10' }
        })

      BadgeService.getMyBadges().then((res) => (this.badges = res.data))
      NotificationService.getMyNotifications().then((res) => {
        this.notifications = res.data
      })
      NotificationService.getUnreadCount().then((res) => (this.unreadCount = res.data))

      PrayerRequestService.listPrayerRequests().then((res) => {
        const currentUserId = Number(this.authStore.user?.id)
        this.myRequests = res.data.filter((dto) => {
          const reqId = dto.prayerRequest.requesterId
          return reqId === currentUserId
        })
        this.showHistory = this.myRequests.length > 0
      })

      PrayerService.listPrayers().then((res) => {
        const prayers = res.data
        this.totalPrayers = prayers.length
        this.calculateStreak(prayers)
      })
    },

    formatDate(dateString) {
      if (!dateString) return 'Just now'
      return new Date(dateString).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
    },

    // Open Unified Action Sheet
    openRequestActions(req) {
      this.selectedRequest = req
      this.showActionSheet = true
    },

    async performDelete(id) {
      if (!id || this.isDeleting) return

      this.isDeleting = true // Start loading

      try {
        await PrayerRequestService.deletePrayerRequest(id)

        this.myRequests = this.myRequests.filter((r) => r.prayerRequest.id !== id)
        if (this.myRequests.length === 0) this.showHistory = false

        this.showActionSheet = false
        this.selectedRequest = null // Reset
      } catch (err) {
        console.error('Failed to delete request', err)
        this.errorMessage = 'Could not delete request at this time.'
        this.showActionSheet = false
      } finally {
        this.isDeleting = false // End loading
      }
    },

    calculateStreak(prayers) {
      if (!prayers || prayers.length === 0) {
        this.streakCount = 0
        return
      }

      // Extract unique dates from prayers
      const uniqueDates = new Set()
      prayers.forEach((p) => {
        const d = new Date(p.prayedAt || p.createdAt)
        d.setHours(0, 0, 0, 0)
        uniqueDates.add(d.getTime())
      })

      const sortedTimestamps = Array.from(uniqueDates).sort((a, b) => b - a)

      if (sortedTimestamps.length === 0) {
        this.streakCount = 0
        return
      }

      // Get today and yesterday at midnight
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const todayTs = today.getTime()

      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      const yesterdayTs = yesterday.getTime()

      const latestTs = sortedTimestamps[0]

      // If the latest prayer is before yesterday, streak is broken (missed a day)
      if (latestTs < yesterdayTs) {
        this.streakCount = 0
        return
      }

      // Count consecutive days
      let streak = 0
      for (let i = 0; i < sortedTimestamps.length; i++) {
        const ts = sortedTimestamps[i]

        if (i === 0) {
          // First day in the streak
          streak = 1
          continue
        }

        // Check if current date is exactly 1 day before the previous date
        const prevDate = new Date(sortedTimestamps[i - 1])
        prevDate.setDate(prevDate.getDate() - 1)
        const expectedPrevTs = prevDate.getTime()

        if (ts === expectedPrevTs) {
          streak++
        } else {
          // Gap found, streak is broken
          break
        }
      }

      this.streakCount = streak
    },

    markRead(id) {
      NotificationService.markAsRead(id).then(() => {
        const n = this.notifications.find((x) => x.id === id)
        if (n) n.read = true
        if (this.unreadCount > 0) this.unreadCount--
      })
    },
    markAllRead() {
      this.notifications.forEach((n) => {
        if (!n.read) {
          this.markRead(n.id)
        }
      })
    },
    formatMessage(msg) {
      return msg
    },
    formatTimeAgo(dateString) {
      if (!dateString) return 'Just now'
      const date = new Date(dateString)
      const now = new Date()
      const seconds = Math.floor((now - date) / 1000)

      if (seconds < 60) return 'Just now'
      const minutes = Math.floor(seconds / 60)
      if (minutes < 60) return `${minutes}m ago`
      const hours = Math.floor(minutes / 60)
      if (hours < 24) return `${hours}h ago`
      const days = Math.floor(hours / 24)
      if (days < 7) return `${days}d ago`
      return date.toLocaleDateString()
    },
    toggleHistory() {
      this.showHistory = !this.showHistory
    },
    // markAnswered Removed -> Moved to ActionSheet Logic
    submitAnswer() {
      if (!this.selectedRequest) return

      // Merge existing data just in case
      const payload = {
        ...this.selectedRequest,
        answered: true,
        answerContent: this.answerContent,
      }

      api.put(`/api/prayer-requests/${payload.id}`, payload).then(() => {
        // Update Local State
        const index = this.myRequests.findIndex((r) => r.prayerRequest.id === payload.id)
        if (index !== -1) {
          this.myRequests[index].prayerRequest.answered = true
          this.myRequests[index].prayerRequest.answerContent = this.answerContent
        }

        this.selectedRequest = null
        this.showAnswerModal = false
        this.answerContent = ''
      })
    },
    logout() {
      this.authStore.logout()
      window.location.reload()
    },
    openEditProfile() {
      this.showEditProfileModal = true
    },
    async saveProfile() {
      if (this.isSavingProfile) return

      this.isSavingProfile = true
      try {
        const updatedUser = {
          ...this.authStore.user,
          name: this.editForm.name,
          displayName: this.editForm.displayName,
        }
        this.authStore.setUser(updatedUser)
        await api.put('/api/users/profile', this.editForm)
        this.showEditProfileModal = false
      } catch (e) {
        console.error('Failed to save profile', e)
        this.errorMessage = 'Could not save changes'
      } finally {
        this.isSavingProfile = false
      }
    },
    // openEditRequest -> Moved to ActionSheet logic but referenced by 'Edit Request' action
    // We already have the logic inside the action handler, so we just need ensuring RequestBottomSheet is handled
    handleRequestSubmitted() {
      this.showRequestSheet = false
      this.requestToEdit = null
      this.fetchData()
    },
  },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Inter:wght@300;400;600&display=swap');

.lumina-profile-wrapper {
  --gold: #e5c37a;
  --gold-dim: #7d5d3b;
  --glass: rgba(20, 15, 10, 0.7);
  --border: rgba(229, 195, 122, 0.2);
  color: #fff;
  font-family: var(--font-body);
  padding: 40px;
}

.bg-effects {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  pointer-events: none;
  z-index: 0;
}

.light-1,
.light-2 {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.light-1 {
  background: #ffd700;
  top: -100px;
  left: -100px;
  animation: float 10s infinite alternate;
}

.light-2 {
  background: #ff8c00;
  bottom: -100px;
  right: -100px;
  animation: float 12s infinite alternate-reverse;
}

@keyframes float {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 30px);
  }
}

@media (max-width: 600px) {
  .light-1,
  .light-2 {
    width: 300px;
    height: 300px;
    opacity: 0.12;
    filter: blur(60px);
  }
  .light-1 {
    top: -50px;
    left: -50px;
  }
  .light-2 {
    bottom: -50px;
    right: -50px;
  }
}

.content-container {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

/* Floating Streak Pill */
.floating-streak {
  position: absolute;
  top: 30px;
  right: 40px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border);
  padding: 8px 16px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.streak-text {
  font-size: 11px;
  letter-spacing: 2px;
  color: var(--gold);
  font-weight: 600;
}

/* Header */
.profile-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.error-notification-profile {
  background: rgba(60, 20, 20, 0.9);
  border: 1px solid rgba(255, 100, 100, 0.4);
  color: #ffaaaa;
  padding: 8px 16px;
  border-radius: 8px;
  margin: 0 auto 20px auto;
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  backdrop-filter: blur(4px);
  max-width: 90%;
}

.close-error {
  background: none;
  border: none;
  color: #ffaaaa;
  cursor: pointer;
  font-weight: bold;
  font-size: 1rem;
}

.avatar-box {
  width: 100px;
  height: 100px;
  background: var(--glass);
  border: 1px solid var(--border);
  border-radius: 28px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-heading);
  font-size: 42px;
  color: var(--gold);
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.5);
  position: relative;
  overflow: hidden;
}

.edit-icon {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 30px;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-box:hover .edit-icon {
  opacity: 1;
}

.profile-name {
  font-family: var(--font-heading);
  font-size: 38px;
  font-weight: 400;
  color: var(--gold);
  margin: 0;
  letter-spacing: 2px;
}

.profile-tagline {
  font-family: var(--font-heading);
  font-style: italic;
  color: #aaa;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
  margin-top: 10px;
  letter-spacing: 1px;
}

.logout-card-wrapper {
  text-align: center;
  margin-top: 60px;
  margin-bottom: 40px;
}

.logout-pill-btn {
  background: rgba(255, 50, 50, 0.1);
  border: 1px solid rgba(255, 50, 50, 0.3);
  color: #ff6b6b;
  padding: 10px 30px;
  font-size: 11px;
  letter-spacing: 2px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s;
  font-family: var(--font-body);
}

.logout-pill-btn:hover {
  background: rgba(255, 50, 50, 0.2);
  box-shadow: 0 0 15px rgba(255, 50, 50, 0.2);
  transform: translateY(-1px);
}

/* Stats */
.stats-grid {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 60px;
}

.stat-card {
  background: var(--glass);
  border: 1px solid var(--border);
  padding: 20px;
  border-radius: 20px;
  text-align: center;
  backdrop-filter: blur(5px);
  flex: 1;
}

.stat-card label {
  font-size: 10px;
  letter-spacing: 3px;
  color: var(--gold-dim);
  display: block;
  margin-bottom: 5px;
}

.stat-card .value {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
}

.stat-card .subtext {
  font-size: 10px;
  opacity: 0.5;
  text-transform: uppercase;
}

/* Milestones */
.section-heading {
  font-family: var(--font-heading);
  font-size: 14px;
  letter-spacing: 5px;
  text-align: center;
  color: var(--gold);
  margin-bottom: 30px;
}

.milestones-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.milestone-card {
  background: var(--glass);
  border: 1px solid var(--border);
  border-radius: 20px;
  padding: 25px 15px;
  text-align: center;
  transition: transform 0.3s ease;
}

.m-icon {
  font-size: 2rem;
  margin-bottom: 15px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.badge-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
  filter: drop-shadow(0 0 8px rgba(255, 215, 0, 0.4));
}

.m-title {
  font-size: 10px;
  letter-spacing: 2px;
  color: var(--gold);
  margin-bottom: 8px;
}
.m-desc {
  font-size: 11px;
  color: #ccc;
  line-height: 1.4;
  height: 32px;
  margin-bottom: 15px;
}

/* Voice Section & Modern History Timeline */
.voice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 60px;
  margin-bottom: 30px; /* Consolidate spacing here */
}

/* Override general section heading margin for this specific header */
.voice-header .section-heading {
  margin-bottom: 0;
  text-align: left; /* Ensure it aligns left with the button on the right */
}

.history-btn {
  background: none;
  border: none;
  color: var(--gold-dim);
  font-size: 10px;
  letter-spacing: 2px;
  cursor: pointer;
}

.voice-card {
  /* Glassmorphism Premium Look */
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.03) 0%, rgba(255, 255, 255, 0.01) 100%);
  border: 1px solid rgba(229, 195, 122, 0.1);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);

  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  margin-top: 0; /* Handled by header margin */

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px; /* Senior Dev Spacing */

  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.voice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px 0 rgba(0, 0, 0, 0.3);
  border-color: rgba(229, 195, 122, 0.2);
}

.mailbox-icon {
  font-size: 48px;
  margin-bottom: 0; /* Reset */
  opacity: 0.8;
  filter: drop-shadow(0 0 10px rgba(229, 195, 122, 0.3));
}

.voice-prompt {
  font-family: var(--font-heading); /* More premium font */
  font-size: 18px;
  line-height: 1.6;
  color: var(--gold);
  margin-bottom: 0; /* Reset */
  max-width: 600px;
  opacity: 0.9;
}

.light-prayer-btn {
  background: linear-gradient(to bottom, #423219, #20180b);
  border: 1px solid var(--gold);
  color: var(--gold);
  padding: 14px 40px;
  border-radius: 50px;
  letter-spacing: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: 0.3s;
}

.light-prayer-btn:hover {
  box-shadow: 0 0 20px rgba(229, 195, 122, 0.3);
  transform: translateY(-2px);
}

/* Modern Timeline Styles */
.history-timeline {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin-top: 30px;
}

.history-card-modern {
  position: relative;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  padding: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) backwards;
}

.history-card-modern:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: var(--gold-dim);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  border-radius: 16px;
  box-shadow: inset 0 0 20px rgba(229, 195, 122, 0);
  transition: 0.5s;
}

.history-card-modern.answered .card-glow {
  box-shadow: inset 0 0 30px rgba(229, 195, 122, 0.15);
  border: 1px solid var(--gold);
}

.card-content {
  position: relative;
  z-index: 2;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.category-pill {
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  background: rgba(229, 195, 122, 0.1);
  color: var(--gold);
  padding: 4px 10px;
  border-radius: 4px;
}

.date-text {
  font-size: 10px;
  color: #666;
  margin-left: auto;
  margin-right: 15px;
}

.card-actions-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.icon-btn {
  background: none;
  border: none;
  color: var(--starlight-dim);
  cursor: pointer;
  font-size: 0.9rem;
  padding: 6px;
  transition: color 0.3s;
}
.icon-btn:hover {
  color: var(--gold-primary);
}

.delete-btn {
  background: none;
  border: none;
  color: #ff6b6b;
  cursor: pointer;
  font-size: 0.9rem;
  opacity: 0.6;
  transition: opacity 0.3s;
  padding: 6px;
}

.delete-btn:hover {
  color: #ff6b6b;
}

.prayer-text {
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.5;
  color: #e0e0e0;
  font-weight: 300;
  font-style: italic;
  margin-bottom: 20px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 15px;
}

.prayer-count {
  font-size: 12px;
  color: #ccc;
  display: flex;
  align-items: center;
  gap: 6px;
}

.answered-badge {
  background: var(--gold);
  color: #000;
  font-size: 10px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 4px;
  letter-spacing: 1px;
}

.mark-answered-link {
  background: none;
  border: none;
  color: var(--gold-dim);
  font-size: 11px;
  text-decoration: underline;
  cursor: pointer;
  opacity: 0.7;
  transition: 0.2s;
}

.mark-answered-link:hover {
  opacity: 1;
  color: var(--gold);
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .floating-streak {
    top: 20px;
    right: 20px;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .milestones-grid {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
  .profile-name {
    font-size: 28px;
  }
  .horizontal-beam {
    width: 90%;
  }
}

@media (max-width: 480px) {
  .profile-header {
    margin-top: 40px;
  }
  .milestones-grid {
    grid-template-columns: 1fr;
  }
  .stat-card {
    padding: 15px 5px;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Modern Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
  padding: 0 40px;
}

.modal-glass {
  background: linear-gradient(145deg, rgba(30, 30, 30, 0.95), rgba(10, 10, 10, 0.98));
  border: 1px solid var(--gold);
  padding: 40px;
  border-radius: 24px;
  width: 90%;
  max-width: 500px;
  text-align: center;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.5),
    0 0 30px rgba(229, 195, 122, 0.1);
  animation: scaleUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.modal-header {
  margin-bottom: 20px;
}

.modal-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 10px;
  text-shadow: 0 0 20px rgba(229, 195, 122, 0.5);
}

.modal-glass h3 {
  font-family: var(--font-heading);
  font-size: 24px;
  color: var(--gold);
  margin: 0;
  letter-spacing: 2px;
}

.modal-subtitle {
  font-family: var(--font-body);
  font-size: 13px;
  color: #aaa;
  font-style: italic;
  margin-bottom: 30px;
  line-height: 1.5;
}

.modal-input {
  width: 100%;
  height: 40px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 15px;
  color: #fff;
  font-family: var(--font-body);
  font-size: 14px;
  resize: none;
  margin-bottom: 30px;
  transition: all 0.3s;
}

.modal-input:focus {
  outline: none;
  border-color: var(--gold);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 15px rgba(229, 195, 122, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.confirm-btn {
  background: linear-gradient(to right, #e5c37a, #d4af37);
  color: #000;
  border: none;
  padding: 12px 30px;
  border-radius: 50px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(229, 195, 122, 0.4);
}

.cancel-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #ccc;
  padding: 12px 30px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  border-color: #fff;
  color: #fff;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes scaleUp {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
/* Luminous Title Style */
.section-heading,
.profile-name {
  font-family: var(--font-heading);
  color: #ffffff !important;
  text-shadow:
    0 0 20px rgba(255, 255, 255, 0.5),
    0 0 40px rgba(255, 215, 0, 0.2);
}

.section-heading {
  font-size: 1.2rem;
  margin-bottom: 20px;
  position: relative;
  display: inline-block;
  letter-spacing: 2px;
}

@media (max-width: 768px) {
  .lumina-profile-wrapper {
    padding: 80px 20px 100px 20px;
  }

  .stats-grid {
    flex-direction: column;
    align-items: stretch;
  }

  .milestones-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .voice-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .history-btn {
    align-self: flex-start;
  }

  .card-top {
    flex-wrap: wrap;
    gap: 10px;
  }

  .date-text {
    margin-left: 0;
    order: 3;
    width: 100%;
  }
}
/* --- DELETE MODAL STYLES (PREMIUM) --- */
.delete-mode .modal-glass {
  background: rgba(30, 20, 20, 0.95);
  border: 1px solid rgba(255, 80, 80, 0.3);
  box-shadow: 0 0 50px rgba(255, 50, 50, 0.15);
}

.modal-header.warning {
  margin-bottom: 20px;
}

.warning-icon {
  font-size: 3rem;
  filter: drop-shadow(0 0 10px rgba(255, 100, 100, 0.5));
  margin-bottom: 15px;
  display: block;
}

.warning-title {
  color: #ff6b6b;
  font-family: var(--font-heading);
  font-size: 1.5rem;
  margin: 0;
  text-shadow: 0 0 10px rgba(255, 50, 50, 0.2);
}

.warning-text {
  color: #ffcccc;
  font-size: 1rem;
  line-height: 1.6;
}

.delete-confirm {
  background: linear-gradient(to right, #8b0000, #b22222);
  border: 1px solid #ff4444;
  color: #fff;
}

.delete-confirm:hover {
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.4);
  transform: translateY(-2px);
}

/* --- INBOX PANEL STYLES (High Contrast & Modern) --- */
.notifications-section {
  width: 100%; /* Use full available width of container */
  max-width: 900px; /* Match content container max-width */
  margin: 0 auto 60px auto;
}

.inbox-panel {
  background: #0f0b08; /* Very dark/black matte */
  border: 1px solid rgba(229, 195, 122, 0.2);
  border-radius: 24px; /* More rounded */
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6); /* Premium depth */
}

/* Header */
.inbox-header {
  background: rgba(30, 25, 20, 0.5);
  border-bottom: 1px solid rgba(229, 195, 122, 0.1);
  padding: 16px 24px;
  display: flex;
  align-items: center;
}

.inbox-title {
  font-family: var(--font-heading);
  font-size: 1.2rem;
  color: #fff; /* Crisp white title */
  margin: 0;
  letter-spacing: 0.5px;
  margin-right: 32px;
}

/* Tabs */
.inbox-tabs {
  display: flex;
  gap: 24px;
  position: relative;
}

.tab-btn {
  background: none;
  border: none;
  color: #666;
  font-size: 0.9rem;
  font-family: var(--font-body);
  font-weight: 500;
  padding: 8px 0;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  color: #aaa;
}

.tab-btn.active {
  color: var(--gold);
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -17px;
  left: 0;
  width: 100%;
  height: 3px;
  background: var(--gold);
  border-radius: 3px 3px 0 0;
  box-shadow: 0 -2px 10px rgba(229, 195, 122, 0.5);
}

/* --- WORDS OF WISDOM --- */
.words-of-wisdom {
  margin-top: 15px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
}

.quote-text {
  font-family:
    'Times New Roman', serif; /* Or a Google Font Serif like Playfair Display if available */
  font-style: italic;
  font-size: 1.1rem;
  color: #dedede;
  margin-bottom: 8px;
  line-height: 1.5;
}

.shimmer-text {
  background: linear-gradient(
    to right,
    #bfbfbf 20%,
    #ffffff 40%,
    #e5c37a 50%,
    #ffffff 60%,
    #bfbfbf 80%
  );
  background-size: 200% auto;
  color: transparent; /* Fallback */
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shine 5s linear infinite;
  display: inline-block;
}

@keyframes shine {
  to {
    background-position: 200% center;
  }
}

.quote-author {
  display: block;
  font-size: 0.85rem;
  color: var(--gold);
  font-family: var(--font-heading);
  letter-spacing: 1px;
  opacity: 0.8;
}

/* --- EDIT PROFILE & MODAL --- */
.avatar-container {
  position: relative;
  display: inline-block;
}

.edit-profile-trigger {
  position: absolute;
  bottom: 20px;
  right: -10px;
  background: #1a1510;
  border: 1px solid var(--gold);
  color: var(--gold);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  transition: all 0.2s;
  font-size: 0.8rem;
}

.edit-profile-trigger:hover {
  background: var(--gold);
  color: #000;
  transform: scale(1.1);
}

.edit-profile-glass {
  background: rgba(18, 14, 10, 0.85);
  border: 1px solid rgba(229, 195, 122, 0.2);
  backdrop-filter: blur(20px);
  padding: 40px;
  width: 100%;
  max-width: 450px;
  border-radius: 20px;
}

.modal-title {
  font-family: var(--font-heading);
  color: var(--gold);
  text-align: center;
  font-size: 1.5rem;
  margin-bottom: 30px;
}

.modal-avatar-upload {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin: 0 auto 30px auto;
  position: relative;
  cursor: pointer;
  border: 2px solid var(--gold);
  overflow: hidden;
}

.modal-avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.modal-avatar-upload:hover .upload-overlay {
  opacity: 1;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: var(--gold-dim);
  font-size: 0.8rem;
  letter-spacing: 1px;
  margin-bottom: 8px;
  text-transform: uppercase;
}

.spiritual-label {
  font-family: var(--font-heading); /* Or a lighter serif check */
  font-size: 0.75rem !important; /* Slightly smaller */
  font-weight: 300;
  opacity: 0.9;
  letter-spacing: 1.5px !important;
}

.spiritual-helper {
  font-size: 0.7rem;
  color: var(--gold-dim);
  opacity: 0.7;
  margin-top: 6px;
  font-style: italic;
  font-family: var(--font-heading);
}

.modal-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  padding: 8px 12px; /* Reduced padding from 12px to 8px top/bottom */
  border-radius: 8px;
  font-family: var(--font-body);
  transition: all 0.3s;
  font-size: 0.9rem; /* Ensure text isn't huge */
}

.modal-input:focus {
  outline: none;
  border-color: var(--gold);
  box-shadow: 0 0 15px rgba(229, 195, 122, 0.1);
}

.gold-btn {
  background: linear-gradient(135deg, #e5c37a 0%, #b88a44 100%);
  border: none;
  color: #120e0a;
  font-weight: bold;
  letter-spacing: 1px;
}
.gold-btn:hover {
  box-shadow: 0 0 20px rgba(229, 195, 122, 0.4);
  transform: translateY(-2px);
}

/* Hover Effects Refinement */
.stat-card {
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}
.stat-card:hover {
  transform: scale(1.02);
  box-shadow: 0 10px 30px rgba(229, 195, 122, 0.1);
  border-color: rgba(229, 195, 122, 0.4);
}

.tab-badge {
  background: rgba(229, 195, 122, 0.2);
  color: var(--gold);
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 6px;
  border: 1px solid rgba(229, 195, 122, 0.3);
}

.inbox-actions {
  margin-left: auto;
}

.mark-all-btn {
  background: none;
  border: none;
  color: #555;
  font-size: 0.75rem;
  cursor: pointer;
  transition: color 0.2s;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.mark-all-btn:hover {
  color: var(--gold);
}

/* List */
.inbox-list {
  display: flex;
  flex-direction: column;
  background: #0f0b08;
  max-height: 400px; /* Default Industry Standard Height */
  overflow-y: auto; /* Enable scrolling */

  /* Scrollbar Styling */
  scrollbar-width: thin;
  scrollbar-color: var(--gold-dim) rgba(255, 255, 255, 0.05);
}

.inbox-list::-webkit-scrollbar {
  width: 6px;
}

.inbox-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.02);
}

.inbox-list::-webkit-scrollbar-thumb {
  background-color: var(--gold-dim);
  border-radius: 10px;
}

.inbox-item {
  display: flex;
  align-items: center; /* Center vertically for neatness */
  gap: 20px;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  transition: all 0.2s;
  position: relative;
}

.inbox-item:last-child {
  border-bottom: none;
}

.inbox-item:hover {
  background: rgba(255, 255, 255, 0.02);
}

/* Unread Item Style */
.inbox-item.is-unread {
  background: rgba(229, 195, 122, 0.03);
}
.inbox-item.is-unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--gold);
  box-shadow: 2px 0 15px rgba(229, 195, 122, 0.3);
}

/* Icons / Avatar */
.item-icon-wrapper {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  background: #1a1510;
  border: 1px solid rgba(255, 255, 255, 0.08); /* Subtle border */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.sender-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-icon {
  font-size: 1.2rem;
}

/* Content */
.item-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-msg {
  font-size: 0.95rem; /* Readable size */
  color: #eee;
  margin: 0;
  line-height: 1.5;
  font-weight: 400;
}

/* Make names bold if we had them wrapped, but plain is clean too */

.item-time {
  font-size: 0.75rem;
  color: #666;
  font-weight: 500;
}

/* Actions */
.item-action-btn {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #666;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  transition: all 0.2s;
  flex-shrink: 0;
  opacity: 0; /* Hidden by default for cleaner look */
  transform: translateX(10px);
}

.inbox-item:hover .item-action-btn {
  opacity: 1;
  transform: translateX(0);
}

.item-action-btn:hover {
  border-color: var(--gold);
  color: var(--gold);
  background: rgba(229, 195, 122, 0.1);
  box-shadow: 0 0 10px rgba(229, 195, 122, 0.2);
}

/* Empty State */
.inbox-empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #555;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.empty-icon {
  font-size: 2.5rem;
  opacity: 0.3;
  filter: grayscale(1);
}

.empty-badges-state {
  text-align: center;
  padding: 30px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  border: 1px dashed rgba(229, 195, 122, 0.2);
  color: var(--gold-dim);
}
.empty-badge-icon {
  display: block;
  font-size: 2rem;
  margin-bottom: 10px;
  opacity: 0.5;
}
</style>
