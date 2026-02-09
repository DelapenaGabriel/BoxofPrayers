<template>
  <div class="comment-section-wrapper">
    <div v-if="loading" class="loading-comments">
      <div class="spinner-xs"></div>
      <small>Loading thoughts...</small>
    </div>

    <transition-group name="list" tag="div" class="comments-list" v-else>
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-avatar-wrapper">
          <img :src="comment.userProfileImage || defaultAvatar" class="comment-avatar" />
        </div>

        <div class="comment-content-wrapper">
          <div class="comment-bubble" :class="{ editing: editingCommentId === comment.id }">
            <div class="comment-header">
              <span class="comment-author">{{ comment.userDisplayName }}</span>
              <span class="comment-time">{{ formatTimeAgo(comment.createdAt) }}</span>
            </div>

            <div v-if="editingCommentId === comment.id" class="edit-comment-form">
              <textarea
                v-model="editCommentContent"
                class="edit-input-sm"
                @keydown.enter.prevent="saveEdit(comment.id)"
                @keydown.esc="cancelEdit"
                ref="editTextarea"
              ></textarea>
              <div class="edit-sm-actions">
                <button class="txt-btn cancel" @click="cancelEdit">Cancel</button>
                <button class="txt-btn save" @click="saveEdit(comment.id)">Save</button>
              </div>
            </div>
            <p v-else class="comment-text">{{ comment.content }}</p>
          </div>

          <div class="comment-footer" v-if="!editingCommentId">
            <button v-if="isOwner(comment)" @click="startEdit(comment.id)" class="action-link">
              Edit
            </button>
            <button
              v-if="isOwner(comment)"
              @click="deleteComment(comment.id)"
              class="action-link delete"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </transition-group>

    <!-- Empty State -->
    <div v-if="!loading && comments.length === 0" class="empty-comments">
      <small>No comments yet. Be the first to encourage!</small>
    </div>

    <!-- Input -->
    <div class="add-comment-row">
      <img :src="currentUserAvatar" class="user-avatar-sm" />
      <div class="input-wrapper" :class="{ 'is-focused': isInputFocused }">
        <input
          type="text"
          v-model="newComment"
          placeholder="Write a comment..."
          @keyup.enter="submitComment"
          @focus="isInputFocused = true"
          @blur="isInputFocused = false"
          :disabled="submitting"
        />
        <button
          @click="submitComment"
          :disabled="!newComment.trim() || submitting"
          class="send-btn"
        >
          <div v-if="submitting" class="spinner-xs"></div>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </div>
    </div>

    <ActionSheet
      :isOpen="showActionSheet"
      title="Comment Options"
      :actions="commentActions"
      @close="showActionSheet = false"
    />
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useCommunityStore } from '@/store/communityStore'
import { useAuthStore } from '@/store/authStore'
import ActionSheet from '@/components/shared/ActionSheet.vue'
import defaultProfileImage from '@/assets/default.jpg'

export default {
  name: 'CommentSection',
  components: { ActionSheet },
  props: {
    postId: { type: Number, required: true },
  },
  setup(props) {
    const store = useCommunityStore()
    const authStore = useAuthStore()
    const comments = ref([])
    const loading = ref(true)
    const newComment = ref('')
    const submitting = ref(false)
    const defaultAvatar = defaultProfileImage
    const isInputFocused = ref(false)
    const editTextarea = ref(null)

    const showActionSheet = ref(false)
    const selectedCommentId = ref(null)

    // Editing State
    const editingCommentId = ref(null)
    const editCommentContent = ref('')

    const currentUserAvatar = computed(() => {
      return authStore.user?.profileImage || defaultAvatar
    })

    const commentActions = computed(() => [
      {
        label: 'Edit Comment',
        icon: '✏️',
        variant: 'default',
        handler: () => startEdit(selectedCommentId.value),
      },
      {
        label: 'Delete Comment',
        icon: '🗑️',
        variant: 'danger',
        handler: () => deleteComment(selectedCommentId.value),
      },
    ])

    const now = ref(new Date())
    let timer = null

    onMounted(() => {
      loadComments()
      timer = setInterval(() => {
        now.value = new Date()
      }, 60000)
    })

    onUnmounted(() => {
      if (timer) clearInterval(timer)
    })

    const loadComments = () => {
      loading.value = true
      store
        .fetchComments(props.postId)
        .then((data) => {
          comments.value = data
        })
        .finally(() => {
          loading.value = false
        })
    }

    const submitComment = () => {
      if (!newComment.value.trim() || submitting.value) return
      submitting.value = true

      store
        .addComment(props.postId, newComment.value)
        .then((comment) => {
          comments.value.push(comment)
          newComment.value = ''
        })
        .finally(() => {
          submitting.value = false
        })
    }

    const openActionSheet = (id) => {
      selectedCommentId.value = id
      showActionSheet.value = true
    }

    const startEdit = (commentId) => {
      const comment = comments.value.find((c) => c.id === commentId)
      if (comment) {
        editingCommentId.value = commentId
        editCommentContent.value = comment.content
        nextTick(() => {
          if (editTextarea.value) editTextarea.value.focus()
        })
      }
      showActionSheet.value = false
    }

    const cancelEdit = () => {
      editingCommentId.value = null
      editCommentContent.value = ''
    }

    const saveEdit = (commentId) => {
      if (!editCommentContent.value.trim()) return

      // Optimistic update
      const comment = comments.value.find((c) => c.id === commentId)
      const oldContent = comment.content
      if (comment) {
        comment.content = editCommentContent.value
      }

      store
        .updateComment(props.postId, commentId, editCommentContent.value)
        .then(() => {
          // Success
          editingCommentId.value = null
        })
        .catch((err) => {
          console.error(err)
          // Revert
          if (comment) comment.content = oldContent
          alert('Failed to update comment')
        })
    }

    const deleteComment = (commentId) => {
      if (!confirm('Delete this comment?')) return

      // Optimistic delete
      comments.value = comments.value.filter((c) => c.id !== commentId)
      showActionSheet.value = false

      store.deleteComment(props.postId, commentId).catch((err) => {
        console.error(err)
        alert('Failed to delete comment')
        loadComments() // Revert by reloading
      })
    }

    const isOwner = (comment) => {
      return authStore.user && authStore.user.id === comment.userId
    }

    const formatTimeAgo = (dateStr) => {
      if (!dateStr) return ''
      // Ensure we treat the date as UTC if it doesn't specify timezone
      const date = new Date(dateStr.endsWith('Z') ? dateStr : `${dateStr}Z`)
      const diffInSeconds = Math.floor((now.value - date) / 1000)

      if (diffInSeconds < 10) return 'Just now'
      if (diffInSeconds < 60) return `${diffInSeconds}s ago`
      if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`
      if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`
      if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`
      if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)}w ago`
      if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)}mo ago`
      return `${Math.floor(diffInSeconds / 31536000)}y ago`
    }

    return {
      comments,
      loading,
      newComment,
      submitting,
      defaultAvatar,
      currentUserAvatar,
      submitComment,
      deleteComment,
      isOwner,
      formatTimeAgo,
      showActionSheet,
      openActionSheet,
      commentActions,
      isInputFocused,
      editTextarea,
      // Editing
      editingCommentId,
      editCommentContent,
      cancelEdit,
      saveEdit,
      startEdit,
    }
  },
}
</script>

<style scoped>
.comment-section-wrapper {
  margin-top: var(--space-xs);
}

.loading-comments,
.empty-comments {
  text-align: center;
  padding: var(--space-md);
  color: var(--starlight-dim);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.comment-item {
  display: flex;
  gap: 10px;
}

.comment-avatar-wrapper {
  flex-shrink: 0;
}

.comment-avatar,
.user-avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.comment-content-wrapper {
  flex: 1;
  max-width: 100%;
}

.comment-bubble {
  background: rgba(255, 255, 255, 0.05);
  padding: 10px 14px;
  border-radius: 0 16px 16px 16px;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-author {
  font-weight: 600;
  color: var(--starlight);
  font-size: 0.85rem;
}

.comment-time {
  font-size: 0.7rem;
  color: var(--starlight-muted);
}

.comment-text {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-footer {
  display: flex;
  gap: 12px;
  margin-top: 4px;
  margin-left: 14px;
}

.action-link {
  background: transparent;
  border: none;
  color: var(--starlight-dim);
  font-size: 0.75rem;
  cursor: pointer;
  padding: 0;
  font-weight: 500;
  transition: color 0.2s;
}

.action-link:hover {
  color: var(--starlight);
  text-decoration: underline;
}

.action-link.delete:hover {
  color: #ff6b6b;
}

/* Input Area */
.add-comment-row {
  display: flex;
  gap: 10px;
  align-items: center;
  position: relative;
  z-index: 2;
}

.input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  transition: all 0.2s ease;
}

.input-wrapper.is-focused {
  background: rgba(0, 0, 0, 0.5);
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.2);
}

.input-wrapper input {
  width: 100%;
  background: transparent;
  border: none;
  padding: 10px 42px 10px 14px;
  color: var(--starlight);
  font-size: 0.9rem;
  font-family: var(--font-body);
}

.input-wrapper input:focus {
  outline: none;
}

.send-btn {
  position: absolute;
  right: 4px;
  background: transparent;
  border: none;
  color: var(--gold-primary);
  padding: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: rgba(229, 195, 122, 0.1);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.send-btn svg {
  width: 18px;
  height: 18px;
}

/* Edit Form */
.edit-comment-form {
  width: 100%;
}

.edit-input-sm {
  width: 100%;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid var(--gold-dim);
  border-radius: 8px;
  color: #fff;
  font-family: var(--font-body);
  font-size: 0.9rem;
  resize: none;
  outline: none;
  min-height: 60px;
  padding: 8px;
  margin-top: 4px;
}

.edit-sm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.txt-btn {
  background: none;
  border: none;
  font-size: 0.8rem;
  cursor: pointer;
  padding: 4px 8px;
  font-weight: 600;
  border-radius: 4px;
}

.txt-btn.cancel {
  color: var(--starlight-dim);
}
.txt-btn.cancel:hover {
  background: rgba(255, 255, 255, 0.05);
}

.txt-btn.save {
  color: var(--gold-primary);
}
.txt-btn.save:hover {
  background: rgba(212, 175, 55, 0.1);
}

/* Spinner */
.spinner-xs {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(229, 195, 122, 0.3);
  border-top: 2px solid var(--gold-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* List Transitions */
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>
