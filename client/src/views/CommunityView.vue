<template>
  <div class="community-view">
    <!-- Header -->
    <header class="feed-header">
      <div class="header-content">
        <h1 class="page-title">Community</h1>
        <p class="page-subtitle">Encourage one another daily</p>
      </div>
    </header>

    <!-- Feed Area -->
    <div class="feed-container">
      <CreatePost @post-created="handlePostCreated" />

      <div v-if="loading && posts.length === 0" class="loading-state">
        <div class="spinner"></div>
        <p>Gathering posts...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="fetchFeed(true)" class="retry-btn">Try Again</button>
      </div>

      <div v-else class="posts-list">
        <TransitionGroup name="list" tag="div">
          <div v-for="post in posts" :key="post.id" class="post-wrapper">
            <FeedPost :post="post" />
          </div>
        </TransitionGroup>

        <div v-if="hasMore" class="load-more" ref="loadMoreTrigger">
          <div v-if="loading" class="spinner-sm"></div>
        </div>

        <div v-if="!hasMore && posts.length > 0" class="end-of-feed">
          <div class="separator-line"></div>
          <p class="verse-text">“Therefore encourage one another and build each other up.”</p>
          <small class="verse-ref">1 Thessalonians 5:11</small>
        </div>

        <div v-if="posts.length === 0 && !loading && !error" class="empty-feed">
          <div class="empty-icon">🌱</div>
          <h3>Start the Conversation</h3>
          <p>Be the first to share a word of encouragement today.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, computed, ref } from 'vue'
import { useCommunityStore } from '@/store/communityStore'
import CreatePost from '@/components/Community/CreatePost.vue'
import FeedPost from '@/components/Community/FeedPost.vue'

export default {
  name: 'CommunityView',
  components: {
    CreatePost,
    FeedPost,
  },
  setup() {
    const store = useCommunityStore()
    const loadMoreTrigger = ref(null)

    onMounted(() => {
      store.fetchFeed(true)

      // Intersection Observer for Infinite Scroll
      const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && !store.loading && store.hasMore) {
            store.fetchFeed()
          }
        },
        { rootMargin: '200px' }, // Load earlier for smoother scroll
      )

      if (loadMoreTrigger.value) {
        observer.observe(loadMoreTrigger.value)
      }
    })

    const handlePostCreated = () => {
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    return {
      posts: computed(() => store.posts),
      loading: computed(() => store.loading),
      error: computed(() => store.error),
      hasMore: computed(() => store.hasMore),
      fetchFeed: store.fetchFeed,
      handlePostCreated,
      loadMoreTrigger,
    }
  },
}
</script>

<style scoped>
.community-view {
  min-height: 100vh;
  padding-bottom: 80px; /* Space for bottom nav */
}

.feed-header {
  padding: var(--space-xl) var(--space-lg) var(--space-lg);
  text-align: center;
  position: relative; /* Changed from sticky */
  z-index: 10;
  /* background: linear-gradient(...) removed or kept as background only */
  background: transparent;
  margin-bottom: var(--space-sm);
}

.header-content {
  max-width: 600px;
  margin: 0 auto;
}

.page-title {
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

.page-subtitle {
  font-family: var(--font-body);
  color: #a89f91;
  font-size: clamp(0.9rem, 3vw, 1.2rem);
  font-style: italic;
  margin-bottom: var(--space-2xl);
  text-align: center;
}

.feed-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 var(--space-md);
}

/* Loading & States */
.loading-state,
.error-state,
.empty-feed {
  text-align: center;
  padding: var(--space-3xl) var(--space-lg);
  color: var(--starlight-dim);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.spinner {
  border: 3px solid rgba(229, 195, 122, 0.1);
  border-top: 3px solid var(--gold-primary);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 0.8s linear infinite;
  margin-bottom: var(--space-lg);
}

.spinner-sm {
  border: 2px solid rgba(229, 195, 122, 0.1);
  border-top: 2px solid var(--gold-primary);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}

.retry-btn {
  background: rgba(229, 195, 122, 0.1);
  border: 1px solid var(--gold-primary);
  color: var(--gold-primary);
  padding: 12px 24px;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
  margin-top: var(--space-md);
  transition: all 0.2s;
}

.retry-btn:hover {
  background: rgba(229, 195, 122, 0.2);
  transform: translateY(-2px);
}

.load-more {
  padding: var(--space-xl);
  text-align: center;
  min-height: 60px;
}

/* End of Feed */
.end-of-feed {
  text-align: center;
  padding: var(--space-2xl) var(--space-lg);
  color: var(--starlight-dim);
  opacity: 0.8;
}

.separator-line {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--gold-dim), transparent);
  margin: 0 auto var(--space-lg);
}

.verse-text {
  font-family: var(--font-heading);
  font-size: 1.1rem;
  font-style: italic;
  color: var(--starlight);
  margin-bottom: var(--space-xs);
  line-height: 1.6;
}

.verse-ref {
  display: block;
  font-size: 0.8rem;
  color: var(--gold-primary);
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* Empty State */
.empty-icon {
  font-size: 3rem;
  margin-bottom: var(--space-md);
  opacity: 0.8;
  animation: float 3s ease-in-out infinite;
}

.empty-feed h3 {
  color: var(--starlight);
  margin-bottom: var(--space-xs);
  font-family: var(--font-heading);
}

/* Animations */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* List Transitions */
.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.list-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.list-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.list-leave-active {
  position: absolute;
  width: 100%;
}

/* Mobile Optimizations */
@media (max-width: 480px) {
  .feed-header {
    padding: var(--space-lg) var(--space-md) var(--space-md);
  }

  .page-title {
    font-size: 1.8rem;
  }

  .feed-container {
    padding: 0 var(--space-md);
  }
}
</style>
