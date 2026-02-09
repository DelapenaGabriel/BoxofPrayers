<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'
import PrayerRequestService from '@/services/PrayerRequestService'
import PrayerService from '@/services/PrayerService'
import CommentService from '@/services/CommentService'
import CommunityService from '@/services/CommunityService'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('posts')
const isLoading = ref(true)
const error = ref(null)
const searchQuery = ref('')

// Data
const posts = ref([])
const requests = ref([])
const prayers = ref([])
const comments = ref([])

// Modals
const showPostModal = ref(false)
const showRequestModal = ref(false)
const showConfirmModal = ref(false)
const editingPost = ref(null)
const editingRequest = ref(null)
const confirmAction = ref(null)
const confirmMessage = ref('')
const confirmTitle = ref('')

onMounted(async () => {
  if (
    !authStore.user?.authorities?.some((a) => a.name === 'ROLE_ADMIN') &&
    authStore.user?.role !== 'ROLE_ADMIN'
  ) {
    if (authStore.user?.role !== 'ROLE_ADMIN') {
      // router.push('/')
    }
  }

  await fetchData()
})

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const [postsRes, reqRes, prayRes, commRes] = await Promise.all([
      CommunityService.getAllPosts(),
      PrayerRequestService.listPrayerRequests(),
      PrayerService.listPrayers(),
      CommentService.getAllComments(),
    ])

    posts.value = postsRes.data
    requests.value = reqRes.data
    prayers.value = prayRes.data
    comments.value = commRes.data
  } catch (e) {
    console.error('Failed to fetch admin data', e)
    error.value = 'Failed to load admin data. Is the server running?'
  } finally {
    isLoading.value = false
  }
}

const stats = computed(() => ({
  totalPosts: posts.value.length,
  totalRequests: requests.value.length,
  totalPrayers: prayers.value.length,
  totalComments: comments.value.length,
}))

const filteredPosts = computed(() => {
  if (!searchQuery.value) return posts.value
  const query = searchQuery.value.toLowerCase()
  return posts.value.filter(
    (p) =>
      p.content?.toLowerCase().includes(query) || p.userDisplayName?.toLowerCase().includes(query),
  )
})

const filteredRequests = computed(() => {
  if (!searchQuery.value) return requests.value
  const query = searchQuery.value.toLowerCase()
  return requests.value.filter(
    (r) =>
      r.prayerRequest.name?.toLowerCase().includes(query) ||
      r.prayerRequest.content?.toLowerCase().includes(query),
  )
})

const filteredPrayers = computed(() => {
  if (!searchQuery.value) return prayers.value
  return prayers.value
})

const filteredComments = computed(() => {
  if (!searchQuery.value) return comments.value
  const query = searchQuery.value.toLowerCase()
  return comments.value.filter(
    (c) => c.content?.toLowerCase().includes(query) || c.userName?.toLowerCase().includes(query),
  )
})

// Confirmation modal helper
const showConfirm = (title, message, action) => {
  confirmTitle.value = title
  confirmMessage.value = message
  confirmAction.value = action
  showConfirmModal.value = true
}

const handleConfirm = async () => {
  if (confirmAction.value) {
    await confirmAction.value()
  }
  showConfirmModal.value = false
  confirmAction.value = null
}

// Post handlers
const openCreatePost = () => {
  editingPost.value = { content: '', imageUrl: '', videoUrl: '', linkUrl: '' }
  showPostModal.value = true
}

const openEditPost = (post) => {
  editingPost.value = { ...post }
  showPostModal.value = true
}

const savePost = async () => {
  try {
    if (editingPost.value.id) {
      const response = await CommunityService.updatePost(editingPost.value.id, editingPost.value)
      const index = posts.value.findIndex((p) => p.id === editingPost.value.id)
      if (index !== -1) {
        posts.value[index] = response.data
      }
    } else {
      const response = await CommunityService.createPost(editingPost.value)
      posts.value.unshift(response.data)
    }
    showPostModal.value = false
    editingPost.value = null
  } catch (e) {
    alert('Failed to save post')
  }
}

const deletePost = (id) => {
  showConfirm('Delete Post', 'Are you sure you want to delete this post?', async () => {
    try {
      await CommunityService.deletePost(id)
      posts.value = posts.value.filter((p) => p.id !== id)
    } catch (e) {
      alert('Failed to delete post')
    }
  })
}

// Request handlers
const openEditRequest = (item) => {
  editingRequest.value = { ...item.prayerRequest }
  showRequestModal.value = true
}

const saveRequest = async () => {
  try {
    await PrayerRequestService.updatePrayerRequest(editingRequest.value.id, editingRequest.value)
    const index = requests.value.findIndex((r) => r.prayerRequest.id === editingRequest.value.id)
    if (index !== -1) {
      requests.value[index].prayerRequest = { ...editingRequest.value }
    }
    showRequestModal.value = false
    editingRequest.value = null
  } catch (e) {
    alert('Failed to update request')
  }
}

const deleteRequest = (id) => {
  showConfirm(
    'Delete Prayer Request',
    'Are you sure you want to delete this prayer request? This will delete all associated prayers and comments.',
    async () => {
      try {
        await PrayerRequestService.deletePrayerRequest(id)
        requests.value = requests.value.filter((r) => r.prayerRequest.id !== id)
      } catch (e) {
        alert('Failed to delete request')
      }
    },
  )
}

const deletePrayer = (id) => {
  showConfirm('Delete Prayer', 'Are you sure you want to delete this prayer?', async () => {
    try {
      await PrayerService.deletePrayer(id)
      prayers.value = prayers.value.filter((p) => p.id !== id)
    } catch (e) {
      alert('Failed to delete prayer')
    }
  })
}

const deleteComment = (id) => {
  showConfirm('Delete Comment', 'Are you sure you want to delete this comment?', async () => {
    try {
      // Try to delete as prayer request comment first
      await CommentService.deleteComment(id)
      comments.value = comments.value.filter((c) => c.id !== id)
    } catch (e) {
      // If that fails, it might be a post comment - try the community endpoint
      try {
        await CommunityService.deleteComment(id)
        comments.value = comments.value.filter((c) => c.id !== id)
      } catch (e2) {
        alert('Failed to delete comment')
      }
    }
  })
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString() + ' ' + new Date(dateStr).toLocaleTimeString()
}
</script>

<template>
  <div class="admin-view">
    <div class="header-glass">
      <h1>Admin Portal</h1>

      <!-- Stats Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalPosts }}</div>
          <div class="stat-label">Posts</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalRequests }}</div>
          <div class="stat-label">Requests</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalPrayers }}</div>
          <div class="stat-label">Prayers</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalComments }}</div>
          <div class="stat-label">Comments</div>
        </div>
      </div>

      <!-- Search Bar -->
      <div class="search-bar">
        <input v-model="searchQuery" type="text" placeholder="Search..." class="search-input" />
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">
          Posts
        </button>
        <button :class="{ active: activeTab === 'requests' }" @click="activeTab = 'requests'">
          Requests
        </button>
        <button :class="{ active: activeTab === 'prayers' }" @click="activeTab = 'prayers'">
          Prayers
        </button>
        <button :class="{ active: activeTab === 'comments' }" @click="activeTab = 'comments'">
          Comments
        </button>
      </div>
    </div>

    <div class="content-area">
      <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Loading Sanctuary Data...</p>
      </div>

      <div v-else-if="error" class="error-msg">
        {{ error }}
        <button @click="fetchData">Retry</button>
      </div>

      <div v-else class="list-container">
        <!-- POSTS TAB -->
        <div v-if="activeTab === 'posts'" class="tab-content">
          <button class="create-btn" @click="openCreatePost">+ Create Post</button>
          <transition-group name="list" tag="div" class="item-list">
            <div v-for="post in filteredPosts" :key="post.id" class="admin-card">
              <div class="card-header">
                <span class="id-badge">#{{ post.id }}</span>
                <span class="date">{{ formatDate(post.createdAt) }}</span>
              </div>
              <div class="card-body">
                <div class="user-info">
                  <img
                    :src="post.userProfileImage || '/avatars/default.png'"
                    alt="avatar"
                    class="avatar-mini"
                  />
                  <span>{{ post.userDisplayName || 'User ' + post.userId }}</span>
                </div>
                <p class="content-preview">{{ post.content }}</p>
                <div v-if="post.imageUrl" class="media-preview">
                  <img :src="post.imageUrl" alt="Post image" />
                </div>
                <div class="meta">
                  <span class="stat">{{ post.amenCount }} Amens</span>
                  <span class="stat">{{ post.commentCount }} Comments</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="edit-btn" @click="openEditPost(post)">Edit</button>
                <button class="delete-btn" @click="deletePost(post.id)">Delete</button>
              </div>
            </div>
            <div v-if="filteredPosts.length === 0" key="empty" class="empty-state">
              No Posts found.
            </div>
          </transition-group>
        </div>

        <!-- REQUESTS TAB -->
        <div v-if="activeTab === 'requests'" class="tab-content">
          <transition-group name="list" tag="div" class="item-list">
            <div v-for="item in filteredRequests" :key="item.prayerRequest.id" class="admin-card">
              <div class="card-header">
                <span class="id-badge">#{{ item.prayerRequest.id }}</span>
                <span class="date">{{ formatDate(item.prayerRequest.createdAt) }}</span>
              </div>
              <div class="card-body">
                <h3>{{ item.prayerRequest.name || 'Anonymous' }}</h3>
                <p class="content-preview">{{ item.prayerRequest.content }}</p>
                <div class="meta">
                  <span class="tag">{{ item.prayerRequest.category }}</span>
                  <span class="stat">{{ item.prayerCount }} Prayers</span>
                  <span v-if="item.prayerRequest.isAnswered" class="tag answered">Answered</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="edit-btn" @click="openEditRequest(item)">Edit</button>
                <button class="delete-btn" @click="deleteRequest(item.prayerRequest.id)">
                  Delete
                </button>
              </div>
            </div>
            <div v-if="filteredRequests.length === 0" key="empty" class="empty-state">
              No Prayer Requests found.
            </div>
          </transition-group>
        </div>

        <!-- PRAYERS TAB -->
        <div v-if="activeTab === 'prayers'" class="tab-content">
          <transition-group name="list" tag="div" class="item-list">
            <div v-for="prayer in filteredPrayers" :key="prayer.id" class="admin-card">
              <div class="card-header">
                <span class="id-badge">#{{ prayer.id }}</span>
                <span class="date">{{ formatDate(prayer.prayedAt) }}</span>
              </div>
              <div class="card-body">
                <p>User ID: {{ prayer.userId || 'Anonymous' }}</p>
                <p>Praying for Request #{{ prayer.prayerRequestId }}</p>
              </div>
              <div class="card-actions">
                <button class="delete-btn" @click="deletePrayer(prayer.id)">Delete</button>
              </div>
            </div>
            <div v-if="filteredPrayers.length === 0" key="empty" class="empty-state">
              No Prayers found.
            </div>
          </transition-group>
        </div>

        <!-- COMMENTS TAB -->
        <div v-if="activeTab === 'comments'" class="tab-content">
          <transition-group name="list" tag="div" class="item-list">
            <div v-for="comment in filteredComments" :key="comment.id" class="admin-card">
              <div class="card-header">
                <span class="id-badge">#{{ comment.id }}</span>
                <span class="date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <div class="card-body">
                <div class="user-info">
                  <img
                    :src="comment.userProfileImage || '/avatars/default.png'"
                    alt="avatar"
                    class="avatar-mini"
                  />
                  <span>{{ comment.userName || 'User ' + comment.userId }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <p class="sub-text">On Request #{{ comment.prayerRequestId }}</p>
              </div>
              <div class="card-actions">
                <button class="delete-btn" @click="deleteComment(comment.id)">Delete</button>
              </div>
            </div>
            <div v-if="filteredComments.length === 0" key="empty" class="empty-state">
              No Comments found.
            </div>
          </transition-group>
        </div>
      </div>
    </div>

    <!-- Post Modal -->
    <div v-if="showPostModal" class="modal-overlay" @click="showPostModal = false">
      <div class="modal-content" @click.stop>
        <h2>{{ editingPost?.id ? 'Edit Post' : 'Create Post' }}</h2>
        <textarea
          v-model="editingPost.content"
          placeholder="What's on your mind?"
          rows="6"
        ></textarea>
        <input v-model="editingPost.imageUrl" type="text" placeholder="Image URL (optional)" />
        <input v-model="editingPost.videoUrl" type="text" placeholder="Video URL (optional)" />
        <input v-model="editingPost.linkUrl" type="text" placeholder="Link URL (optional)" />
        <div class="modal-actions">
          <button class="cancel-btn" @click="showPostModal = false">Cancel</button>
          <button class="save-btn" @click="savePost">Save</button>
        </div>
      </div>
    </div>

    <!-- Request Modal -->
    <div v-if="showRequestModal" class="modal-overlay" @click="showRequestModal = false">
      <div class="modal-content" @click.stop>
        <h2>Edit Prayer Request</h2>
        <input v-model="editingRequest.name" type="text" placeholder="Name" />
        <textarea v-model="editingRequest.content" placeholder="Content" rows="6"></textarea>
        <select v-model="editingRequest.category">
          <option value="HEALTH">Health</option>
          <option value="FAMILY">Family</option>
          <option value="WORK">Work</option>
          <option value="SPIRITUAL">Spiritual</option>
          <option value="OTHER">Other</option>
        </select>
        <label class="checkbox-label">
          <input v-model="editingRequest.isAnswered" type="checkbox" />
          Answered
        </label>
        <label class="checkbox-label">
          <input v-model="editingRequest.isVisible" type="checkbox" />
          Visible
        </label>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showRequestModal = false">Cancel</button>
          <button class="save-btn" @click="saveRequest">Save</button>
        </div>
      </div>
    </div>

    <!-- Confirmation Modal -->
    <div
      v-if="showConfirmModal"
      class="modal-overlay confirm-overlay"
      @click="showConfirmModal = false"
    >
      <div class="modal-content confirm-modal" @click.stop>
        <div class="confirm-icon">⚠️</div>
        <h2>{{ confirmTitle }}</h2>
        <p class="confirm-message">{{ confirmMessage }}</p>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showConfirmModal = false">Cancel</button>
          <button class="danger-btn" @click="handleConfirm">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-view {
  min-height: 100vh;
  padding-top: 380px; /* Reduced from 420px - matches actual header height better */
  padding-bottom: 100px;
  color: var(--starlight);
  font-family: var(--font-body);
  background: radial-gradient(circle at top right, rgba(255, 215, 0, 0.05), transparent 60%);
}

.header-glass {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 50;
  background: rgba(5, 5, 8, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 215, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
}

h1 {
  font-family: var(--font-heading);
  color: var(--gold-primary);
  font-size: 1.5rem;
  margin: 0;
  letter-spacing: 3px;
  text-transform: uppercase;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  width: 100%;
  max-width: 800px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 215, 0, 0.1);
  border-radius: 15px;
  padding: 15px 10px;
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 215, 0, 0.3);
  transform: translateY(-2px);
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--gold-primary);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--starlight-dim);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.search-bar {
  width: 100%;
  max-width: 800px;
}

.search-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 30px;
  padding: 12px 20px;
  color: var(--starlight);
  font-size: 0.95rem;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--gold-primary);
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.2);
}

.search-input::placeholder {
  color: var(--starlight-dim);
}

.tabs {
  display: flex;
  gap: 8px;
  background: rgba(255, 255, 255, 0.03);
  padding: 4px;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.tabs button {
  background: none;
  border: none;
  color: var(--starlight-dim);
  padding: 8px 18px;
  border-radius: 25px;
  font-family: var(--font-body);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tabs button.active {
  background: linear-gradient(135deg, var(--gold-primary) 0%, #b8860b 100%);
  color: var(--void-bg);
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);
}

.content-area {
  padding: 0 20px;
  max-width: 800px;
  margin: 0 auto;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.create-btn {
  background: linear-gradient(135deg, var(--gold-primary) 0%, #b8860b 100%);
  border: none;
  color: var(--void-bg);
  padding: 12px 24px;
  border-radius: 30px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-start;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
}

.loading,
.error-msg {
  text-align: center;
  padding: 60px 20px;
  color: var(--starlight-dim);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 215, 0, 0.1);
  border-top-color: var(--gold-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.admin-card {
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  transition: all 0.3s ease;
}

.admin-card:hover {
  background: rgba(255, 255, 255, 0.04);
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 215, 0, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  color: var(--starlight-dim);
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  padding-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.id-badge {
  font-family: 'Courier New', monospace;
  background: rgba(255, 255, 255, 0.05);
  padding: 3px 8px;
  border-radius: 6px;
  color: var(--gold-secondary);
}

.card-body h3 {
  margin: 0 0 10px 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--starlight);
  line-height: 1.3;
}

.content-preview {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 15px 0;
  line-height: 1.6;
  font-weight: 300;
}

.media-preview {
  margin: 10px 0;
  border-radius: 12px;
  overflow: hidden;
}

.media-preview img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 0.8rem;
}

.tag {
  color: var(--gold-primary);
  background: rgba(255, 215, 0, 0.08);
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid rgba(255, 215, 0, 0.1);
}

.tag.answered {
  color: #34c759;
  background: rgba(52, 199, 89, 0.1);
  border-color: rgba(52, 199, 89, 0.2);
}

.stat {
  color: var(--starlight-dim);
  padding: 4px 0;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.03);
}

.edit-btn {
  background: rgba(0, 122, 255, 0.1);
  border: 1px solid rgba(0, 122, 255, 0.2);
  color: #0a84ff;
  padding: 8px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: rgba(0, 122, 255, 0.2);
  border-color: rgba(0, 122, 255, 0.5);
  box-shadow: 0 0 15px rgba(0, 122, 255, 0.2);
  transform: translateY(-1px);
}

.delete-btn {
  background: rgba(255, 69, 58, 0.1);
  border: 1px solid rgba(255, 69, 58, 0.2);
  color: #ff453a;
  padding: 8px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: rgba(255, 69, 58, 0.2);
  border-color: rgba(255, 69, 58, 0.5);
  box-shadow: 0 0 15px rgba(255, 69, 58, 0.2);
  transform: translateY(-1px);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar-mini {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  object-fit: cover;
}

.comment-text {
  background: rgba(0, 0, 0, 0.3);
  padding: 12px 15px;
  border-radius: 12px;
  margin: 0 0 8px 0;
  border-left: 2px solid var(--gold-dim);
  font-style: italic;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.9);
}

.sub-text {
  font-size: 0.75rem;
  color: var(--starlight-dim);
  margin: 0;
  text-align: right;
  opacity: 0.7;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--starlight-dim);
  font-style: italic;
  border: 1px dashed rgba(255, 255, 255, 0.1);
  border-radius: 20px;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: rgba(20, 20, 25, 0.95);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 20px;
  padding: 30px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 10px 50px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-content h2 {
  color: var(--gold-primary);
  margin: 0 0 20px 0;
  font-size: 1.5rem;
}

.modal-content input,
.modal-content textarea,
.modal-content select {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 12px 15px;
  color: var(--starlight);
  font-size: 0.95rem;
  margin-bottom: 15px;
  font-family: var(--font-body);
  outline: none;
  transition: all 0.3s ease;
}

.modal-content input:focus,
.modal-content textarea:focus,
.modal-content select:focus {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--gold-primary);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.2);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--starlight);
  margin-bottom: 15px;
  cursor: pointer;
}

.checkbox-label input[type='checkbox'] {
  width: auto;
  margin: 0;
  cursor: pointer;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--starlight);
  padding: 10px 24px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.save-btn {
  background: linear-gradient(135deg, var(--gold-primary) 0%, #b8860b 100%);
  border: none;
  color: var(--void-bg);
  padding: 10px 24px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
}

/* Confirmation Modal */
.confirm-modal {
  max-width: 400px;
  text-align: center;
}

.confirm-icon {
  font-size: 3rem;
  margin-bottom: 15px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.confirm-message {
  color: var(--starlight-dim);
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 20px;
}

.danger-btn {
  background: linear-gradient(135deg, #ff453a 0%, #d32f2f 100%);
  border: none;
  color: white;
  padding: 10px 24px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 69, 58, 0.3);
}

.danger-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 69, 58, 0.4);
}

.list-enter-active,
.list-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-value {
    font-size: 1.5rem;
  }
}
</style>
