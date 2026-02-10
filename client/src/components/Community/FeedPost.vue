<template>
  <article class="feed-post">
    <div class="post-header">
      <div class="user-avatar-wrapper">
        <img :src="post.userProfileImage || defaultAvatar" alt="User" class="avatar" />
      </div>

      <div class="user-info">
        <div class="name-row">
          <div class="user-name">{{ post.userDisplayName }}</div>
        </div>
        <div class="meta-info">
          <span class="timestamp">{{ formatTimeAgo(post.createdAt) }}</span>
        </div>
      </div>

      <button class="more-btn" @click="showActionSheet = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="1"></circle>
          <circle cx="19" cy="12" r="1"></circle>
          <circle cx="5" cy="12" r="1"></circle>
        </svg>
      </button>
    </div>

    <div class="post-content">
      <div v-if="isEditing" class="edit-wrapper">
        <textarea v-model="editContent" class="inline-edit-area" ref="editInput"></textarea>
        <div class="edit-actions">
          <button class="edit-btn cancel" @click="cancelEdit">Cancel</button>
          <button class="edit-btn save" @click="saveEdit">Save</button>
        </div>
      </div>
      <p v-else-if="post.content" class="post-text">{{ post.content }}</p>

      <div v-if="post.originalPost" class="reposted-card">
        <div class="reposted-header">
          <span class="repost-icon">🔁</span>
          <span class="repost-author">
            <span class="repost-label">Reposted from </span>
            <strong>{{ post.originalPost?.userDisplayName }}</strong>
          </span>
        </div>
        <p class="repost-text">{{ post.originalPost?.content }}</p>
      </div>

      <div v-if="embeddedVideo" class="post-media video-embed">
        <iframe
          :src="embeddedVideo"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
      </div>
      <div v-else-if="post.imageUrl" class="post-media">
        <img :src="post.imageUrl" alt="Post content" loading="lazy" />
      </div>

      <div v-else-if="post.videoUrl" class="post-media">
        <video :src="post.videoUrl" controls class="post-video"></video>
      </div>
    </div>

    <div class="stats-row" v-if="post.amenCount > 0 || post.commentCount > 0">
      <div class="stat-item" v-if="post.amenCount > 0">
        <span class="stat-icon">🙏</span>
        <span class="stat-text">{{ post.amenCount }}</span>
      </div>
      <div class="stat-item clickable" v-if="post.commentCount > 0" @click="toggleComments">
        <span class="stat-text">{{ post.commentCount }} Comments</span>
      </div>
    </div>

    <div class="action-bar">
      <button class="action-btn" :class="{ active: post.likedByCurrentUser }" @click="toggleLike">
        <div class="icon-wrapper">
          <svg
            v-if="!post.likedByCurrentUser"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"
            ></path>
          </svg>
          <span v-else class="amen-active">🙏</span>
        </div>
        <span class="action-label">Amen</span>
      </button>

      <button class="action-btn" @click="toggleComments">
        <div class="icon-wrapper">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"
            ></path>
          </svg>
        </div>
        <span class="action-label">Comment</span>
      </button>

      <button class="action-btn" @click="repost">
        <div class="icon-wrapper">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="17 1 21 5 17 9"></polyline>
            <path d="M3 11V9a4 4 0 0 1 4-4h14"></path>
            <polyline points="7 23 3 19 7 15"></polyline>
            <path d="M21 13v2a4 4 0 0 1-4 4H3"></path>
          </svg>
        </div>
        <span class="action-label">Repost</span>
      </button>
    </div>

    <transition name="slide-down">
      <div v-if="showComments" class="comments-section">
        <CommentSection :postId="post.id" />
      </div>
    </transition>

    <ActionSheet
      :isOpen="showActionSheet"
      title="Post Options"
      :actions="postActions"
      @close="showActionSheet = false"
    />

    <ActionSheet
      :isOpen="showRepostSheet"
      title="Repost this content?"
      message="Sharing this will add it to your profile feed."
      :actions="repostActions"
      @close="showRepostSheet = false"
    />
  </article>
</template>

<script>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/store/authStore'
import { useCommunityStore } from '@/store/communityStore'
import { useToastStore } from '@/store/toastStore'
import CommentSection from './CommentSection.vue'
import ActionSheet from '@/components/shared/ActionSheet.vue'
import defaultProfileImage from '@/assets/default.jpg'
import { formatTimeAgo } from '@/utils/dateUtils'

export default {
  name: 'FeedPost',
  components: { CommentSection, ActionSheet },
  props: {
    post: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const authStore = useAuthStore()
    const communityStore = useCommunityStore()
    const toastStore = useToastStore()

    const showComments = ref(false)
    const showActionSheet = ref(false)
    const showRepostSheet = ref(false)
    const defaultAvatar = defaultProfileImage

    // Editing State
    const isEditing = ref(false)
    const editContent = ref('')

    const isOwner = computed(() => {
      return authStore.user && authStore.user.id === props.post.userId
    })

    // Use the shared utility
    const formatTimeAgoStr = (dateStr) => formatTimeAgo(dateStr)

    const embeddedVideo = computed(() => {
      if (!props.post.content) return null
      const ytRegex =
        /(?:youtube\.com\/(?:[^\/]+\/.+\/|(?:v|e(?:mbed)?)\/|.*[?&]v=)|youtu\.be\/)([^"&?\/\s]{11})/i
      const match = props.post.content.match(ytRegex)
      return match && match[1] ? `https://www.youtube.com/embed/${match[1]}` : null
    })

    // Actions
    const startEdit = () => {
      editContent.value = props.post.content || ''
      isEditing.value = true
      showActionSheet.value = false
    }

    const cancelEdit = () => {
      isEditing.value = false
      editContent.value = ''
    }

    const saveEdit = async () => {
      if (editContent.value.trim() === props.post.content) {
        cancelEdit()
        return
      }

      try {
        await communityStore.updatePost(props.post.id, { content: editContent.value })
        props.post.content = editContent.value
        toastStore.showToast('Post updated', 'success')
        isEditing.value = false
      } catch (err) {
        console.error(err)
        toastStore.showToast('Failed to update post', 'error')
      }
    }

    const postActions = computed(() => {
      const actions = []
      if (isOwner.value) {
        actions.push({
          label: 'Edit Post',
          icon: '✏️',
          variant: 'default',
          handler: startEdit,
        })

        actions.push({
          label: 'Delete Post',
          icon: '🗑️',
          variant: 'danger',
          handler: async () => {
            if (confirm('Are you sure you want to delete this post?')) {
              try {
                await communityStore.deletePost(props.post.id)
                toastStore.showToast('Post deleted', 'success')
              } catch (e) {
                toastStore.showToast('Failed to delete post', 'error')
              }
            }
          },
        })
      } else {
        actions.push({
          label: 'Report Post',
          icon: '🚩',
          handler: () => {
            toastStore.showToast('Reported. We will review this shortly.', 'info')
            showActionSheet.value = false
          },
        })
      }
      return actions
    })

    const repostActions = computed(() => [
      {
        label: 'Repost to Feed',
        icon: '🔁',
        variant: 'primary',
        handler: async () => {
          try {
            await communityStore.createPost({
              content: '',
              originalPostId: props.post.id,
            })
            toastStore.showToast('Reposted successfully!', 'success')
            showRepostSheet.value = false
          } catch (e) {
            console.error(e)
            toastStore.showToast('Failed to repost.', 'error')
          }
        },
      },
    ])

    const toggleLike = () => {
      if (props.post.likedByCurrentUser) {
        communityStore.removeReaction(props.post.id)
      } else {
        communityStore.addReaction(props.post.id)
      }
    }

    const toggleComments = () => {
      showComments.value = !showComments.value
    }

    const repost = () => {
      showRepostSheet.value = true
    }

    return {
      defaultAvatar,
      isOwner,
      formatTimeAgo: formatTimeAgoStr,
      showComments,
      showActionSheet,
      showRepostSheet,
      embeddedVideo,
      toggleLike,
      toggleComments,
      repost,
      postActions,
      repostActions,
      isEditing,
      editContent,
      cancelEdit,
      saveEdit,
    }
  },
}
</script>

<style scoped>
.feed-post {
  background: linear-gradient(135deg, rgba(20, 20, 24, 0.6) 0%, rgba(10, 10, 12, 0.7) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 16px;
  margin-bottom: var(--space-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.feed-post:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
  border-color: rgba(229, 195, 122, 0.15);
}

.post-header {
  display: flex;
  align-items: center;
  padding: var(--space-md);
  padding-bottom: var(--space-sm);
}

.user-avatar-wrapper {
  margin-right: 12px;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid rgba(229, 195, 122, 0.1);
  transition: border-color 0.2s;
}

.feed-post:hover .avatar {
  border-color: var(--gold-primary);
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.name-row {
  display: flex;
  align-items: center;
}

.user-name {
  font-weight: 600;
  color: var(--starlight);
  font-size: 1rem;
  font-family: var(--font-body);
}

.timestamp {
  font-size: 0.75rem;
  color: var(--starlight-muted);
  margin-top: 2px;
}

.more-btn {
  background: transparent;
  border: none;
  color: var(--starlight-dim);
  cursor: pointer;
  padding: 8px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.more-btn:hover {
  opacity: 1;
  color: var(--starlight);
}

.more-btn svg {
  width: 20px;
  height: 20px;
}

/* Post Content */
.post-content {
  padding: 4px var(--space-md) var(--space-md);
}

.post-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.98rem;
  line-height: 1.6;
  margin-bottom: var(--space-sm);
  white-space: pre-wrap;
  font-family: var(--font-body);
}

/* Media */
.post-media {
  margin-top: var(--space-sm);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  background: #000;
}

.post-media img,
.post-video {
  width: 100%;
  height: auto;
  display: block;
  max-height: 500px;
  object-fit: cover;
}

/* Stats */
.stats-row {
  padding: var(--space-xs) var(--space-md);
  display: flex;
  justify-content: space-between;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 0.8rem;
  color: var(--starlight-dim);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-item.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-item.clickable:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--starlight);
}

.stat-icon {
  font-size: 0.9rem;
}

/* Action Bar */
.action-bar {
  display: flex;
  justify-content: space-between;
  padding: 4px var(--space-sm);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.1);
}

.action-btn {
  flex: 1;
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--starlight-dim);
  cursor: pointer;
  padding: 12px 0;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
  border-radius: 8px;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--starlight);
}

.action-btn.active {
  color: var(--gold-primary);
}

.icon-wrapper svg {
  width: 18px;
  height: 18px;
}

.amen-active {
  animation: bounce 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* Comments */
.comments-section {
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.2);
  padding: var(--space-md);
}

/* Repost Card */
.reposted-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.03);
  padding: 12px;
  margin-top: 12px;
  border-radius: 12px;
  position: relative;
  transition: all 0.2s;
}

.reposted-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
}

.reposted-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 0.85rem;
  color: var(--starlight-dim);
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.repost-icon {
  font-size: 1rem;
}

.repost-author strong {
  color: var(--starlight);
}

.repost-text {
  font-size: 0.9rem;
  color: var(--starlight);
  line-height: 1.5;
  white-space: pre-wrap;
}

/* Inline Edit */
.edit-wrapper {
  margin-bottom: var(--space-md);
  animation: fadeIn 0.2s ease;
}

.inline-edit-area {
  width: 100%;
  min-height: 80px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--gold-primary);
  border-radius: 8px;
  color: #fff;
  padding: 10px;
  font-family: var(--font-body);
  font-size: 1rem;
  resize: vertical;
  outline: none;
  margin-bottom: 8px;
}

.edit-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.edit-btn {
  padding: 6px 16px;
  border-radius: 20px;
  border: none;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn.cancel {
  background: rgba(255, 255, 255, 0.1);
  color: #ccc;
}

.edit-btn.save {
  background: var(--gold-primary);
  color: #000;
}

/* Video Embed */
.video-embed {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
  background: #000;
}
.video-embed iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

@keyframes bounce {
  0% {
    transform: scale(0.8);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
  max-height: 1000px;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  max-height: 0;
}

/* Mobile Optimizations */
@media (max-width: 480px) {
  .feed-post {
    border-radius: 16px;
    margin-bottom: 16px;
    border-left: none;
    border-right: none;
  }

  .post-header {
    padding: 12px 14px;
  }

  .avatar {
    width: 38px;
    height: 38px;
  }

  .post-content {
    padding: 0 14px 12px;
  }

  .post-text {
    font-size: 0.95rem;
  }

  .action-btn {
    font-size: 0.8rem;
  }
}
</style>
