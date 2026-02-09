<template>
  <div class="page-container">
    <div class="bg-effects">
      <div class="light-orb orb-1"></div>
      <div class="light-orb orb-2"></div>
      <div class="light-orb orb-3"></div>
    </div>

    <!-- MAIN HEADERS -->
    <header class="page-header">
      <div class="header-content">
        <h1 class="main-title">Fellowship</h1>
        <p class="subtitle">Join the chorus of faith</p>
      </div>
    </header>

    <!-- FILTER BAR -->
    <div class="filter-sticky-wrapper">
      <div class="feed-controls glass-panel">
        <div class="controls-inner">
          <div class="sort-wrapper">
            <select v-model="sortBy" class="sort-select">
              <option value="newest">Newest</option>
              <option value="oldest">Oldest</option>
              <option value="needs_prayer">Needs Prayer</option>
            </select>
          </div>

          <div class="chips-scroll-container">
            <button
              class="filter-chip"
              :class="{ active: selectedCategory === '' }"
              @click="setCategory('')"
            >
              <span class="chip-text">All</span>
            </button>
            <button
              v-for="cat in categories"
              :key="cat"
              class="filter-chip"
              :class="{ active: selectedCategory === cat }"
              @click="setCategory(cat)"
            >
              <span class="chip-text">{{ cat }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- FEED SECTION -->
    <div class="feed-wrapper" :class="{ 'fade-in': !loading }">
      <!-- LOADING STATE -->
      <div v-if="loading" class="feed-loading-container">
        <div class="candle-loader"></div>
        <p class="loading-message">Gathering prayers...</p>
      </div>

      <!-- EMPTY STATE -->
      <div v-else-if="sortedPrayers.length === 0" class="empty-state glass-panel">
        <div class="empty-icon">🕊️</div>
        <h3>The Sanctuary is Silent</h3>
        <p>No prayers found for this category yet.</p>
      </div>

      <!-- PRAYER LIST -->
      <div v-else class="feed-list">
        <PrayerCard
          v-for="(prayer, index) in paginatedPrayers"
          :key="prayer.prayerRequest.id"
          :prayer="prayer"
          :isPrayFeed="true"
          @pray="handlePray"
          @request-auth="showAuthPrompt = true"
        />
      </div>

      <!-- PAGINATION CONTROLS -->
      <div class="pagination-controls" v-if="totalPages > 1 && !loading">
        <button
          class="page-btn arrow-btn"
          @click="prevPage"
          :disabled="currentPage === 1"
          aria-label="Previous Page"
        >
          <span class="icon">←</span>
        </button>

        <span class="page-info">
          <span class="current">{{ currentPage }}</span>
          <span class="divider">/</span>
          <span class="total">{{ totalPages }}</span>
        </span>

        <button
          class="page-btn arrow-btn"
          @click="nextPage"
          :disabled="currentPage === totalPages"
          aria-label="Next Page"
        >
          <span class="icon">→</span>
        </button>
      </div>
    </div>

    <!-- SUCCESS MODAL -->
    <Transition name="modal-fade">
      <div v-if="showSuccessModal" class="overlay-backdrop modal-mode" @click.self="closeModal">
        <div class="modal-card premium glass-panel" v-if="showSuccessModal">
          <div class="god-rays"></div>
          <div class="modal-icon-wrapper large">
            <div class="core-glow"></div>
            <div class="modal-icon">🕯️</div>
          </div>

          <h2 class="modal-title premium-title">Prayer Ascending</h2>
          <p class="modal-body premium-body">"Your voice has joined the chorus of the saints."</p>

          <button class="modal-btn premium-btn" @click="closeModal">Amen</button>
        </div>
      </div>
    </Transition>

    <!-- AUTH PROMPT MODAL -->
    <AuthPromptModal :isOpen="showAuthPrompt" @close="showAuthPrompt = false" />
  </div>
</template>

<script>
import PrayerRequestService from '@/services/PrayerRequestService'
import PrayerService from '@/services/PrayerService'
import { useSanctuaryStore } from '@/store/sanctuaryStore'
import { useAuthStore } from '@/store/authStore'
import { mapActions, mapStores } from 'pinia'
import AuthPromptModal from '@/components/AuthPromptModal.vue'
import PrayerCard from '@/components/PrayerCard.vue'

export default {
  name: 'FellowshipFeed',
  components: {
    AuthPromptModal,
    PrayerCard,
  },
  data() {
    return {
      loading: true,
      prayers: [],
      isProcessing: false,
      showSuccessModal: false,
      showAuthPrompt: false,
      activePrayer: null,
      myPrayedRequestIds: new Set(),

      // Filters
      selectedCategory: '',
      categories: ['Answered', 'General', 'Healing', 'Family', 'Peace', 'Gratitude', 'Other'],

      // Sorting & Pagination
      sortBy: 'newest',
      currentPage: 1,
      itemsPerPage: 6,
    }
  },
  computed: {
    ...mapStores(useAuthStore),
    filteredPrayers() {
      // Basic category filtering handles mostly by backend param, but redundant safety:
      let filtered = this.prayers
      return filtered
    },
    sortedPrayers() {
      // Sort logic
      const sorted = [...this.filteredPrayers]
      return sorted.sort((a, b) => {
        if (this.sortBy === 'needs_prayer') {
          const countA = a.prayerCount || 0
          const countB = b.prayerCount || 0
          if (countA !== countB) return countA - countB
          // Fallback to newest
          const dateA = new Date(a.prayerRequest.createdAt)
          const dateB = new Date(b.prayerRequest.createdAt)
          return dateB - dateA
        } else if (this.sortBy === 'oldest') {
          const dateA = new Date(a.prayerRequest.createdAt)
          const dateB = new Date(b.prayerRequest.createdAt)
          return dateA - dateB
        } else {
          // Default newest
          const dateA = new Date(a.prayerRequest.createdAt)
          const dateB = new Date(b.prayerRequest.createdAt)
          return dateB - dateA
        }
      })
    },
    totalPages() {
      if (!this.sortedPrayers || !this.sortedPrayers.length) return 0
      return Math.ceil(this.sortedPrayers.length / this.itemsPerPage)
    },
    paginatedPrayers() {
      if (!this.sortedPrayers) return []
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.sortedPrayers.slice(start, end)
    },
    feedRefreshTrigger() {
      return this.authStore.feedRefreshTrigger
    },
  },
  watch: {
    feedRefreshTrigger() {
      this.fetchPrayers()
    },
  },
  methods: {
    ...mapActions(useSanctuaryStore, ['addStar']),

    setCategory(cat) {
      if (this.selectedCategory === cat) return
      this.selectedCategory = cat
      this.currentPage = 1 // Reset page
      this.fetchPrayers()
    },

    fetchPrayers() {
      this.loading = true
      const params = {}

      if (this.selectedCategory === 'Answered') {
        params.isAnswered = true
      } else if (this.selectedCategory) {
        params.category = this.selectedCategory
      }

      PrayerRequestService.listPrayerRequests(params)
        .then((response) => {
          this.prayers = response.data || []
        })
        .catch((error) => {
          console.error('Error fetching prayers:', error)
          // Handle error visibly if needed
        })
        .finally(() => {
          // Add artificial delay for smoother UI transition if it's too fast
          setTimeout(() => {
            this.loading = false
            this.checkMyPrayers()
          }, 300)
        })
    },

    checkMyPrayers() {
      if (!this.authStore.token) return

      if (this.myPrayedRequestIds.size === 0) {
        PrayerService.listPrayers()
          .then((res) => {
            res.data.forEach((p) => {
              if (p.prayerRequestId) {
                this.myPrayedRequestIds.add(p.prayerRequestId)
              }
            })
            this.updatePrayedStatus()
          })
          .catch((err) => console.error('Error fetching prayer history:', err))
      } else {
        this.updatePrayedStatus()
      }
    },

    updatePrayedStatus() {
      this.prayers.forEach((p) => {
        if (this.myPrayedRequestIds.has(p.prayerRequest.id)) {
          p.hasPrayed = true
        }
      })
    },

    handlePray(prayer) {
      if (!this.authStore.token) {
        this.showAuthPrompt = true
        return
      }

      if (prayer.hasPrayed) return

      this.activePrayer = prayer
      // No full page loader, just optimistic update or card loader?
      // Let's use card-based feedback primarily, but modal is good for "Effect"

      const prayerPayload = {
        prayerRequestId: prayer.prayerRequest.id,
        userId: null,
      }

      PrayerService.createPrayer(prayerPayload)
        .then(() => {
          this.activePrayer.hasPrayed = true
          this.activePrayer.prayerCount++
          this.showSuccessModal = true
          this.addStar()
          this.myPrayedRequestIds.add(prayer.prayerRequest.id)
        })
        .catch((error) => {
          console.error('Error recording prayer:', error)
        })
    },

    closeModal() {
      this.showSuccessModal = false
      this.activePrayer = null
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.scrollToTop()
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.scrollToTop()
      }
    },
    scrollToTop() {
      const topEl = document.querySelector('.page-header')
      if (topEl) topEl.scrollIntoView({ behavior: 'smooth' })
    },
  },
  mounted() {
    this.fetchPrayers()
  },
}
</script>

<style scoped>
/* COPIED FROM OTHER SECTIONS FOR CONSISTENCY & UPDATED */
.page-container {
  min-height: 100vh;
  padding-top: 100px; /* Navbar space */
  padding-bottom: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow-x: hidden;
}

/* --- BACKGROUND FX --- */
.bg-effects {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.light-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: floatOrb 20s infinite alternate ease-in-out;
}

.orb-1 {
  width: 50vw;
  height: 50vw;
  background: radial-gradient(circle, #ffd70033 0%, transparent 70%);
  top: -10%;
  left: -10%;
}
.orb-2 {
  width: 40vw;
  height: 40vw;
  background: radial-gradient(circle, #ff8c0033 0%, transparent 70%);
  bottom: 10%;
  right: -5%;
  animation-delay: -5s;
}
.orb-3 {
  width: 30vw;
  height: 30vw;
  background: radial-gradient(circle, #4169e122 0%, transparent 70%);
  top: 40%;
  left: 30%;
  animation-delay: -10s;
}

@keyframes floatOrb {
  0% {
    transform: translate(0, 0) scale(1);
  }
  100% {
    transform: translate(30px, 50px) scale(1.1);
  }
}

/* --- HEADER --- */
.page-header {
  text-align: center;
  margin-bottom: 0;
  z-index: 2;
  padding: 0 var(--space-md);
}

.main-title {
  font-family: var(--font-heading);
  font-size: 3.5rem;
  font-weight: 400;
  margin: 0;
  color: #ffffff;
  text-shadow:
    0 0 20px rgba(255, 255, 255, 0.6),
    0 0 40px rgba(255, 215, 0, 0.3);
  letter-spacing: -2px;
}

.subtitle {
  font-family: var(--font-body);
  color: #a89f91;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
  font-style: italic;
  margin-bottom: var(--space-2xl);
  text-align: center;
}

/* --- FILTER BAR --- */
.filter-sticky-wrapper {
  position: sticky;
  top: 85px;
  z-index: 50;
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0 var(--space-md);
  margin-bottom: var(--space-2xl);
}

.feed-controls {
  max-width: 800px;
  width: 100%;
  padding: var(--space-sm) var(--space-md);
  border-radius: 100px;
  background: rgba(18, 12, 8, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 215, 0, 0.15);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
}

.controls-inner {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  width: 100%;
}

.sort-wrapper {
  flex-shrink: 0;
  position: relative;
}

.sort-select {
  appearance: none;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.046);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--gold-primary);
  padding: var(--space-sm) var(--space-xl) var(--space-sm) var(--space-md);
  border-radius: 40px;
  font-family: var(--font-body);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  outline: none;
  transition: all 0.3s;
  background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23e5c37a%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E');
  background-repeat: no-repeat;
  background-position: right 12px top 50%;
  background-size: 10px auto;
}

.sort-select:hover {
  background-color: rgba(255, 255, 255, 0.08);
  border-color: rgba(212, 175, 55, 0.3);
}

.chips-scroll-container {
  display: flex;
  gap: var(--space-sm);
  overflow-x: auto;
  padding: 4px 2px;
  justify-content: flex-start;
  flex: 1;

  scrollbar-width: none;
  -ms-overflow-style: none;
}

.chips-scroll-container::-webkit-scrollbar {
  display: none;
}

@media (min-width: 1024px) {
  .chips-scroll-container {
    scrollbar-width: thin;
    scrollbar-color: var(--gold-primary) rgba(255, 255, 255, 0.05);
    padding-bottom: 12px;
  }
  .chips-scroll-container::-webkit-scrollbar {
    display: block;
    height: 6px;
  }
  .chips-scroll-container::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.05);
    border-radius: 4px;
  }
  .chips-scroll-container::-webkit-scrollbar-thumb {
    background: rgba(212, 175, 55, 0.4);
    border-radius: 4px;
  }
  .chips-scroll-container::-webkit-scrollbar-thumb:hover {
    background: var(--gold-primary);
  }
}

.filter-chip {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--starlight-muted);
  padding: var(--space-sm) var(--space-md);
  border-radius: 40px;
  font-family: var(--font-body);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.filter-chip:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--starlight);
  border-color: rgba(255, 255, 255, 0.3);
}

.filter-chip.active {
  background: linear-gradient(135deg, var(--gold-primary) 0%, #d4af37 100%);
  color: #120c08;
  border-color: transparent;
  box-shadow: 0 0 15px rgba(212, 175, 55, 0.3);
  font-weight: 700;
}

/* --- FEED LAYOUT --- */
.feed-wrapper {
  width: 100%;
  max-width: 800px;
  padding: 0 var(--space-md);
  z-index: 2;
  position: relative;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

/* --- DROPDOWN OPTIONS --- */
.sort-select option {
  background-color: #120c08;
  color: var(--starlight);
  padding: 10px;
}

/* --- EMPTY STATE --- */
.empty-state {
  text-align: center;
  padding: var(--space-3xl) var(--space-md);
  border-radius: var(--radius-lg);
  background: rgba(0, 0, 0, 0.4);
}
.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.8;
}

/* --- PAGINATION --- */
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-lg);
  margin-top: var(--space-3xl);
}

.page-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--starlight);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}
.page-btn:hover:not(:disabled) {
  background: var(--gold-primary);
  color: #000;
  border-color: transparent;
  transform: translateY(-2px);
}
.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  font-family: var(--font-heading);
  font-size: 1.1rem;
  color: var(--starlight-muted);
  display: flex;
  gap: var(--space-sm);
}
.page-info .current {
  color: var(--gold-primary);
  font-weight: 700;
}

/* --- SUCCESS MODAL --- */
.overlay-backdrop.modal-mode {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-card {
  background: #1a1a1c;
  padding: var(--space-2xl);
  border-radius: 30px;
  text-align: center;
  border: 1px solid rgba(255, 215, 0, 0.3);
  max-width: 90vw;
  width: 400px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 50px rgba(212, 175, 55, 0.2);
}
.modal-icon {
  font-size: 4rem;
  position: relative;
  z-index: 2;
}
.god-rays {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: conic-gradient(
    from 0deg,
    transparent 0deg,
    rgba(255, 215, 0, 0.1) 20deg,
    transparent 40deg
  );
  animation: rotateRays 10s linear infinite;
}
@keyframes rotateRays {
  to {
    transform: rotate(360deg);
  }
}

.premium-title {
  font-family: var(--font-heading);
  color: var(--gold-primary);
  margin: var(--space-md) 0 var(--space-sm);
  font-size: 1.8rem;
}
.premium-body {
  color: var(--starlight-dim);
  line-height: 1.6;
  margin-bottom: var(--space-xl);
  font-style: italic;
}
.premium-btn {
  background: var(--gold-primary);
  color: #000;
  border: none;
  padding: 12px 40px;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 1px;
  transition: transform 0.2s;
  pointer-events: auto;
  position: relative;
  z-index: 1002;
}
.premium-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(212, 175, 55, 0.4);
}

/* Modal Transition */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .main-title {
    font-size: 2.2rem;
  }
}

.loading-message {
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--gold-primary);
  font-weight: 500;
  font-size: 1.2rem;
  margin-top: var(--space-md);
}
</style>
