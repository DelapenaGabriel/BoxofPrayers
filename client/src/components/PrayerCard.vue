<template>
  <div
    class="prayer-card"
    :class="{ 'is-answered': prayer.prayerRequest.answered }"
    v-motion-slide-visible-once-bottom
  >
    <!-- ANSWERED BADGE (Top Right) -->
    <div v-if="prayer.prayerRequest.answered" class="answered-badge">
      <svg
        class="badge-icon"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
      >
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
        <polyline points="22 4 12 14.01 9 11.01" />
      </svg>
      <span>Answered</span>
    </div>

    <!-- USER HEADER -->
    <div class="card-header">
      <div class="user-avatar">
        <img
          v-if="!isAnonymousRequest && prayer.userProfileImage"
          :src="prayer.userProfileImage"
          alt="User avatar"
          class="avatar-image"
        />
        <div v-else class="avatar-fallback">
          {{
            isAnonymousRequest ? '?' : (prayer.prayerRequest.name || 'A').charAt(0).toUpperCase()
          }}
        </div>
      </div>
      <div class="user-info">
        <div class="user-name">{{ prayer.prayerRequest.name || 'Anonymous' }}</div>
        <div class="meta-info">
          <span class="timestamp">{{ formatTimeAgo(prayer.prayerRequest.createdAt) }}</span>
        </div>
      </div>
      <div class="category-badge">
        <span>{{ prayer.prayerRequest.category || 'General' }}</span>
      </div>
    </div>

    <!-- PRAYER CONTENT -->
    <div class="prayer-content">
      <p class="prayer-text">{{ prayer.prayerRequest.content }}</p>

      <!-- Testimony Link (if answered) -->
      <button v-if="prayer.prayerRequest.answered" class="testimony-link" @click="openPraiseReport">
        <span>Read Testimony</span>
      </button>
    </div>

    <!-- PRAYER STATS & ACTION -->
    <div class="prayer-actions">
      <div class="prayer-stats">
        <svg
          class="stats-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
        <span class="stats-count">{{ prayer.prayerCount || 0 }}</span>
        <span class="stats-label">PRAYING</span>
      </div>

      <button
        class="pray-button"
        :class="{ 'is-prayed': prayer.hasPrayed }"
        @click="handlePrayClick"
        :disabled="prayer.hasPrayed"
      >
        <svg
          v-if="!prayer.hasPrayed"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
        <span>{{ prayer.hasPrayed ? 'Prayer Joined' : 'Join Prayer' }}</span>
      </button>
    </div>

    <!-- COMMENTS SECTION -->
    <div class="comments-section">
      <!-- Comments List (if any) -->
      <div v-if="comments.length > 0" class="comments-container">
        <button class="comments-header" @click="showComments = !showComments">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          </svg>
          <span>{{ comments.length }} {{ comments.length === 1 ? 'Comment' : 'Comments' }}</span>
          <svg
            class="chevron"
            :class="{ 'is-open': showComments }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </button>

        <!-- Comments List -->
        <transition name="slide-fade">
          <div v-if="showComments" class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-avatar">
                <img
                  :src="comment.userProfileImage || defaultProfileImage"
                  alt="User Avatar"
                  class="avatar-image"
                />
              </div>
              <div
                class="comment-content"
                :class="{ 'is-editing': editingCommentId === comment.id }"
              >
                <div class="comment-meta">
                  <span class="commenter-name">{{ comment.userName || 'Anonymous' }}</span>
                  <span class="comment-time">{{ formatTimeAgo(comment.createdAt) }}</span>
                  <button
                    v-if="
                      currentUser &&
                      (currentUser.id === comment.userId ||
                        currentUser.id === prayer.prayerRequest.requesterId)
                    "
                    class="comment-options"
                    @click="openCommentActionSheet(comment.id)"
                  >
                    <svg viewBox="0 0 24 24" fill="currentColor">
                      <circle cx="12" cy="5" r="2" />
                      <circle cx="12" cy="12" r="2" />
                      <circle cx="12" cy="19" r="2" />
                    </svg>
                  </button>
                </div>

                <!-- Edit Mode -->
                <div v-if="editingCommentId === comment.id" class="comment-edit">
                  <textarea
                    v-model="editCommentText"
                    class="edit-textarea"
                    @keydown.enter.prevent="saveEditComment(comment.id)"
                    @keydown.esc="cancelEditComment"
                    rows="2"
                  ></textarea>
                  <div class="edit-buttons">
                    <button class="btn-secondary" @click="cancelEditComment">Cancel</button>
                    <button class="btn-primary" @click="saveEditComment(comment.id)">Save</button>
                  </div>
                </div>

                <!-- Display Mode -->
                <p v-else class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
          </div>
        </transition>
      </div>

      <!-- Comment Input (Always Visible) -->
      <div class="comment-input-container">
        <div class="comment-input-avatar">
          <img
            :src="currentUser?.profileImage || defaultProfileImage"
            alt="Current User"
            class="avatar-image"
          />
        </div>
        <div class="comment-input-wrapper">
          <input
            type="text"
            v-model="commentText"
            placeholder="Share words of encouragement..."
            @keyup.enter="submitComment"
            class="comment-input"
          />
          <button v-if="commentText.trim()" class="send-button" @click="submitComment">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="22" y1="2" x2="11" y2="13" />
              <polygon points="22 2 15 22 11 13 2 9 22 2" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- TESTIMONY MODAL -->
    <Transition name="modal-fade">
      <div v-if="showPraiseModal" class="modal-backdrop" @click.self="showPraiseModal = false">
        <div class="testimony-modal">
          <button class="modal-close" @click="showPraiseModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>

          <div class="modal-header">
            <h3>Prayer Testimony</h3>
          </div>

          <div v-if="isEditingPraise" class="testimony-edit">
            <textarea
              v-model="praiseEditText"
              placeholder="Share how this prayer was answered..."
              rows="5"
            ></textarea>
            <div class="modal-buttons">
              <button class="btn-secondary" @click="isEditingPraise = false">Cancel</button>
              <button class="btn-primary" @click="savePraiseReport">Save Testimony</button>
            </div>
          </div>

          <div v-else class="testimony-content">
            <p v-if="prayer.prayerRequest.answerContent">
              {{ prayer.prayerRequest.answerContent }}
            </p>
            <p v-else class="empty-state">This testimony hasn't been written yet.</p>
            <button
              v-if="
                currentUser && currentUser.id === prayer.prayerRequest.requesterId && !isPrayFeed
              "
              class="btn-primary full-width"
              @click="startEditPraise"
            >
              {{ prayer.prayerRequest.answerContent ? 'Edit Testimony' : 'Write Testimony' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <ActionSheet
      :isOpen="showActionSheet"
      title="Comment Options"
      :actions="commentActions"
      @close="showActionSheet = false"
    />
  </div>
</template>

<script>
import CommentService from '../services/CommentService'
import PrayerRequestService from '@/services/PrayerRequestService'
import defaultProfileImage from '@/assets/default.jpg'
import { mapStores } from 'pinia'
import { useAuthStore } from '@/store/authStore'
import ActionSheet from '@/components/shared/ActionSheet.vue'

export default {
  name: 'PrayerCard',
  components: { ActionSheet },
  props: {
    prayer: {
      type: Object,
      required: true,
    },
    isPrayFeed: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['pray', 'request-auth'],
  data() {
    return {
      commentText: '',
      comments: [],
      showComments: false,
      showPraiseModal: false,
      isEditingPraise: false,
      praiseEditText: '',
      showActionSheet: false,
      commentToDeleteId: null,
      // Comment Editing
      editingCommentId: null,
      editCommentText: '',
      selectedCommentId: null, // For action sheet context
      now: new Date(),
      timer: null,
      defaultProfileImage,
    }
  },
  mounted() {
    this.fetchComments()
    this.timer = setInterval(() => {
      this.now = new Date()
    }, 60000)
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    formatTimeAgo(dateString) {
      if (!dateString) return ''
      // Ensure we treat the date as UTC if it doesn't specify timezone
      const date = new Date(dateString.endsWith('Z') ? dateString : `${dateString}Z`)
      const diffInSeconds = Math.floor((this.now - date) / 1000)

      if (diffInSeconds < 10) return 'Just now'
      if (diffInSeconds < 60) return `${diffInSeconds}s ago`
      if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
      if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
      if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`
      if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)}w ago`
      if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)}mo ago`
      return `${Math.floor(diffInSeconds / 31536000)}y ago`
    },
    handlePrayClick(event) {
      this.$emit('pray', this.prayer, event)
    },
    openPraiseReport() {
      this.showPraiseModal = true
      this.isEditingPraise = false
      this.praiseEditText = this.prayer.prayerRequest.answerContent || ''
    },
    startEditPraise() {
      if (this.isPrayFeed) return // Restriction logic
      this.isEditingPraise = true
      this.praiseEditText = this.prayer.prayerRequest.answerContent || ''
    },
    savePraiseReport() {
      if (!this.praiseEditText.trim()) return

      const updatedReq = {
        ...this.prayer.prayerRequest,
        answered: true,
        answerContent: this.praiseEditText,
      }

      PrayerRequestService.updatePrayerRequest(updatedReq.id, updatedReq)
        .then(() => {
          this.prayer.prayerRequest.answerContent = this.praiseEditText
          this.isEditingPraise = false
        })
        .catch((err) => {
          console.error('Failed to update praise report', err)
          alert('Failed to save changes.')
        })
    },
    fetchComments() {
      const requestId = this.prayer.prayerRequest?.id
      if (!requestId) return

      CommentService.getComments(requestId)
        .then((response) => {
          this.comments = response.data
        })
        .catch((err) => {
          console.error('Error fetching comments:', err)
        })
    },
    submitComment() {
      if (!this.authStore.token) {
        this.$emit('request-auth')
        return
      }

      if (!this.commentText.trim()) return

      const payload = {
        prayerRequestId: this.prayer.prayerRequest.id,
        content: this.commentText,
      }

      CommentService.createComment(payload)
        .then((response) => {
          this.comments.unshift(response.data)
          this.commentText = ''
          this.showComments = true
        })
        .catch((err) => {
          console.error('Error posting comment:', err)
          alert('Failed to post comment. Please try again.')
        })
    },
    // Inline Comment Editing
    startEditComment(commentId) {
      const comment = this.comments.find((c) => c.id === commentId)
      if (comment) {
        this.editingCommentId = commentId
        this.editCommentText = comment.content
      }
      this.showActionSheet = false
    },
    cancelEditComment() {
      this.editingCommentId = null
      this.editCommentText = ''
    },
    saveEditComment(commentId) {
      if (!this.editCommentText.trim()) return

      // Optimistic update
      const comment = this.comments.find((c) => c.id === commentId)
      const oldContent = comment.content
      if (comment) {
        comment.content = this.editCommentText
      }

      CommentService.updateComment(commentId, this.editCommentText)
        .then(() => {
          this.editingCommentId = null
        })
        .catch((err) => {
          console.error('Failed to update comment', err)
          if (comment) comment.content = oldContent
          alert('Failed to update comment')
        })
    },
    openCommentActionSheet(commentId) {
      this.selectedCommentId = commentId
      this.showActionSheet = true
    },
    // Deletion
    confirmDeleteComment() {
      if (!this.selectedCommentId) return

      CommentService.deleteComment(this.selectedCommentId)
        .then(() => {
          this.comments = this.comments.filter((c) => c.id !== this.selectedCommentId)
          this.showActionSheet = false
          this.selectedCommentId = null
        })
        .catch((err) => {
          console.error('Failed to delete comment', err)
          alert('Could not delete comment.')
        })
    },
  },
  computed: {
    ...mapStores(useAuthStore),
    currentUser() {
      return this.authStore.user
    },
    isAnonymousRequest() {
      return (
        this.prayer.prayerRequest.isAnonymous === true ||
        this.prayer.prayerRequest.name === 'Anonymous'
      )
    },
    isRequester() {
      return this.currentUser && this.currentUser.id === this.prayer.prayerRequest.requesterId
    },
    commentActions() {
      return [
        {
          label: 'Edit Comment',
          icon: '✏️',
          variant: 'default',
          handler: () => this.startEditComment(this.selectedCommentId),
        },
        {
          label: 'Delete Comment',
          icon: '🗑️',
          variant: 'danger',
          handler: this.confirmDeleteComment,
        },
      ]
    },
  },
}
</script>

<style scoped>
/* ================================
   PRAYER CARD - Modern Black & Gold
   ================================ */

/* Base Card - Dark Glassmorphism */
.prayer-card {
  position: relative;
  background: linear-gradient(135deg, rgba(20, 20, 18, 0.75) 0%, rgba(10, 10, 8, 0.85) 100%);
  border: 1.5px solid transparent;
  background-clip: padding-box;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 20px;
  position: relative;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05),
    inset 0 -1px 0 rgba(212, 175, 55, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.prayer-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 20px;
  padding: 1.5px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.3), rgba(212, 175, 55, 0.12));
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  mask-composite: exclude;
  pointer-events: none;
}

.prayer-card:hover {
  transform: translateY(-4px);
  background: linear-gradient(135deg, rgba(25, 25, 22, 0.8) 0%, rgba(15, 15, 12, 0.9) 100%);
  box-shadow:
    0 8px 30px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.08),
    inset 0 -1px 0 rgba(212, 175, 55, 0.15),
    0 0 0 1px rgba(212, 175, 55, 0.35);
}

.prayer-card:hover::before {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.45), rgba(212, 175, 55, 0.18));
}

.prayer-card.is-answered {
  background: linear-gradient(135deg, rgba(55, 50, 35, 0.9) 0%, rgba(35, 30, 20, 0.95) 100%);
}

.prayer-card.is-answered::before {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.55), rgba(212, 175, 55, 0.25));
  box-shadow: inset 0 0 60px rgba(212, 175, 55, 0.1);
}

/* Answered Badge */
.answered-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.15) 0%, rgba(212, 175, 55, 0.08) 100%);
  border: 1.5px solid rgba(212, 175, 55, 0.4);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.7rem;
  font-weight: 700;
  color: #d4af37;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  backdrop-filter: blur(10px);
  z-index: 10;
}

.badge-icon {
  width: 14px;
  height: 14px;
  stroke: currentColor;
}

/* Card Header */
.card-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
  position: relative;
}

.user-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-image,
.avatar-fallback {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #d4af37 0%, #b8942e 100%);
  font-size: 1.1rem;
  font-weight: 700;
  color: #0f0f12;
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.25);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
  display: inline-block;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

/* Category Badge on Right */
.category-badge {
  margin-left: auto;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.12) 0%, rgba(212, 175, 55, 0.06) 100%);
  border: 1px solid rgba(212, 175, 55, 0.25);
  padding: 6px 14px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  transition: all 0.25s ease;
}

.category-badge span {
  font-size: 0.7rem;
  font-weight: 700;
  color: #d4af37;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  white-space: nowrap;
}

.category-badge:hover {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.18) 0%, rgba(212, 175, 55, 0.1) 100%);
  border-color: rgba(212, 175, 55, 0.35);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(212, 175, 55, 0.15);
}

.separator {
  color: rgba(255, 255, 255, 0.25);
  font-size: 0.6rem;
}

.timestamp {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.45);
}

/* Prayer Content */
.prayer-content {
  margin-bottom: 20px;
}

.prayer-text {
  font-size: 0.95rem;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 16px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.testimony-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(212, 175, 55, 0.08);
  border: 1px solid rgba(212, 175, 55, 0.3);
  color: #d4af37;
  padding: 10px 24px;
  border-radius: 24px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  letter-spacing: 0.3px;
}

.testimony-link:hover {
  background: rgba(212, 175, 55, 0.15);
  border-color: rgba(212, 175, 55, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.2);
}

/* Prayer Actions */
.prayer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  gap: 16px;
}

@media (max-width: 480px) {
  .prayer-actions {
    flex-direction: column;
    align-items: stretch;
  }
}

.prayer-stats {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

.stats-icon {
  width: 24px;
  height: 24px;
  stroke: #d4af37;
  flex-shrink: 0;
}

.stats-count {
  font-size: 1.5rem;
  font-weight: 700;
  color: #d4af37;
}

.stats-label {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
}

.pray-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: linear-gradient(135deg, #d4af37 0%, #b8942e 100%);
  border: none;
  color: #0f0f12;
  padding: 14px 28px;
  border-radius: 28px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.25s ease;
  min-height: 52px;
  box-shadow: 0 4px 16px rgba(212, 175, 55, 0.25);
}

.pray-button svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.pray-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(212, 175, 55, 0.35);
}

.pray-button.is-prayed {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.45);
  cursor: default;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: none;
}

/* Comments Section */
.comments-section {
  margin-top: 24px;
}

.comments-container {
  margin-bottom: 20px;
}

.comments-header {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 12px 16px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.comments-header:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(212, 175, 55, 0.2);
  color: #d4af37;
}

.comments-header svg:first-of-type {
  width: 20px;
  height: 20px;
  stroke: currentColor;
  flex-shrink: 0;
}

.comments-header span {
  flex: 1;
}

.chevron {
  width: 18px;
  height: 18px;
  stroke: currentColor;
  transition: transform 0.3s ease;
  flex-shrink: 0;
}

.chevron.is-open {
  transform: rotate(180deg);
}

/* Slide Fade Transition */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Comments List */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 12px;
  padding: 12px 0;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-avatar .avatar-image,
.comment-avatar .avatar-fallback {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-avatar .avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #444 0%, #333 100%);
  font-size: 0.8rem;
  font-weight: 600;
  color: #d4af37;
}

.comment-content {
  flex: 1;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  padding: 12px 14px;
  border-radius: 14px;
  min-width: 0;
  transition: all 0.2s ease;
}

.comment-content.is-editing {
  background: rgba(212, 175, 55, 0.05);
  border-color: rgba(212, 175, 55, 0.2);
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.commenter-name {
  font-weight: 600;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.9);
}

.comment-time {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.4);
}

.comment-options {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.35);
  cursor: pointer;
  padding: 4px;
  margin-left: auto;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.comment-options:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #d4af37;
}

.comment-options svg {
  width: 100%;
  height: 100%;
}

.comment-text {
  margin: 0;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.5;
  word-wrap: break-word;
}

/* Comment Edit */
.comment-edit {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edit-textarea {
  width: 100%;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 10px;
  color: #fff;
  font-size: 0.9rem;
  resize: vertical;
  outline: none;
  font-family: inherit;
}

.edit-textarea:focus {
  border-color: rgba(212, 175, 55, 0.4);
  background: rgba(0, 0, 0, 0.4);
}

.edit-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* Comment Input */
.comment-input-container {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.comment-input-avatar {
  flex-shrink: 0;
  margin-top: 4px;
}

.comment-input-avatar .avatar-image,
.comment-input-avatar .avatar-fallback {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-input-avatar .avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #444 0%, #333 100%);
  font-size: 0.8rem;
  font-weight: 600;
  color: #d4af37;
}

.comment-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  padding: 6px 6px 6px 18px;
  transition: all 0.2s ease;
}

.comment-input-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(212, 175, 55, 0.3);
}

.comment-input {
  flex: 1;
  background: none;
  border: none;
  color: #fff;
  font-size: 0.9rem;
  padding: 8px 0;
  outline: none;
  font-family: inherit;
}

.comment-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.send-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #d4af37 0%, #b8942e 100%);
  border: none;
  color: #0f0f12;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.send-button svg {
  width: 18px;
  height: 18px;
  stroke: currentColor;
}

.send-button:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 14px rgba(212, 175, 55, 0.3);
}

/* Buttons */
.btn-primary {
  background: linear-gradient(135deg, #d4af37 0%, #b8942e 100%);
  border: none;
  color: #000;
  padding: 14px 28px;
  border-radius: 28px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(212, 175, 55, 0.25);
  letter-spacing: 0.3px;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(212, 175, 55, 0.4);
}

.btn-primary.full-width {
  width: 100%;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.7);
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

/* Modal */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.testimony-modal {
  background: linear-gradient(135deg, rgba(50, 50, 40, 0.95) 0%, rgba(30, 30, 20, 0.98) 100%);
  border: 2px solid transparent;
  background-clip: padding-box;
  border-radius: 28px;
  padding: 40px;
  max-width: 550px;
  width: 100%;
  position: relative;
  box-shadow:
    0 25px 70px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.08),
    inset 0 -1px 0 rgba(0, 0, 0, 0.2);
}

.testimony-modal::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 28px;
  padding: 2px;
  background: linear-gradient(
    135deg,
    rgba(212, 175, 55, 0.5) 0%,
    rgba(212, 175, 55, 0.2) 50%,
    rgba(212, 175, 55, 0.1) 100%
  );
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  mask-composite: exclude;
  pointer-events: none;
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(255, 255, 255, 0.05);
  border: none;
  color: rgba(255, 255, 255, 0.5);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.modal-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  text-align: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #d4af37;
  text-shadow: 0 2px 10px rgba(212, 175, 55, 0.2);
  letter-spacing: 0.5px;
}

.testimony-content {
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.9;
  font-size: 1rem;
}

.testimony-content p {
  margin: 0 0 24px 0;
  font-size: 1rem;
  text-align: center;
  padding: 0 8px;
}

.empty-state {
  color: rgba(255, 255, 255, 0.5);
  font-style: italic;
  text-align: center;
  padding: 20px 0;
}

.testimony-edit textarea {
  width: 100%;
  background: rgba(0, 0, 0, 0.4);
  border: 1.5px solid rgba(212, 175, 55, 0.2);
  border-radius: 16px;
  padding: 16px;
  color: #fff;
  font-size: 1rem;
  line-height: 1.7;
  resize: vertical;
  outline: none;
  font-family: inherit;
  margin-bottom: 20px;
  transition: all 0.2s ease;
}

.testimony-edit textarea:focus {
  border-color: rgba(212, 175, 55, 0.5);
  background: rgba(0, 0, 0, 0.5);
  box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.1);
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* Modal Transition */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* Mobile Optimization */
@media (max-width: 480px) {
  .prayer-card {
    padding: 20px;
    border-radius: 16px;
    margin-bottom: 16px;
  }

  .answered-badge {
    top: 12px;
    right: 12px;
    font-size: 0.65rem;
    padding: 5px 12px;
  }

  .category-badge {
    position: absolute;
    top: 0;
    right: 0;
    padding: 4px 10px;
    font-size: 0.65rem;
  }

  .category-badge span {
    font-size: 0.65rem;
  }

  .user-name {
    font-size: 0.95rem;
  }

  .prayer-text {
    font-size: 0.9rem;
  }

  .pray-button {
    padding: 12px 24px;
    font-size: 0.85rem;
    min-height: 48px;
  }

  .testimony-modal {
    padding: 24px;
    border-radius: 20px;
  }

  .modal-header h3 {
    font-size: 1.2rem;
  }
}
</style>
