<template>
  <div class="create-post-card">
    <div class="card-content">
      <div class="input-header">
        <div class="user-avatar-wrapper">
          <img :src="userAvatar" alt="User" class="user-avatar" />
        </div>
        <div class="input-wrapper">
          <textarea
            v-model="content"
            placeholder="Share your testimony or encouragement..."
            rows="1"
            ref="textarea"
            @input="adjustHeight"
            @focus="isFocused = true"
            @blur="isFocused = false"
            class="content-input"
          ></textarea>
        </div>
      </div>

      <!-- Media Preview -->
      <transition name="fade">
        <div class="media-preview" v-if="imageUrl || videoUrl || isUploading">
          <div v-if="isUploading" class="uploading-state">
            <div class="spinner-sm"></div>
            <span>Uploading media...</span>
          </div>
          <template v-else>
            <div class="media-container">
              <img v-if="imageUrl" :src="imageUrl" alt="Preview" />
              <video v-if="videoUrl" :src="videoUrl" controls class="video-preview"></video>
              <button class="remove-media" @click="removeMedia" title="Remove media">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
          </template>
        </div>
      </transition>

      <!-- Actions Footer -->
      <div class="actions-row" :class="{ 'is-active': isFocused || content || imageUrl }">
        <div class="media-actions">
          <button class="media-btn" @click="triggerImageUpload" title="Add Photo/Video">
            <svg viewBox="0 0 24 24" fill="none" class="media-icon">
              <rect
                x="3"
                y="3"
                width="18"
                height="18"
                rx="2"
                ry="2"
                stroke="currentColor"
                stroke-width="2"
              />
              <circle cx="8.5" cy="8.5" r="1.5" fill="currentColor" />
              <polyline points="21 15 16 10 5 21" stroke="currentColor" stroke-width="2" />
            </svg>
            <span class="btn-label">Media</span>
          </button>
        </div>

        <div class="right-actions">
          <transition name="fade">
            <span
              v-if="content.length > 0"
              class="char-count"
              :class="{ 'near-limit': content.length > 280 }"
            >
              {{ content.length }}
            </span>
          </transition>

          <button
            class="post-btn"
            :disabled="!isValid"
            :class="{ publishing: publishing }"
            @click="submitPost"
          >
            <span v-if="!publishing">Post</span>
            <div v-else class="spinner-xs"></div>
          </button>
        </div>
      </div>
    </div>

    <!-- Hidden Input -->
    <input
      type="file"
      ref="fileInput"
      accept="image/*,video/*"
      @change="handleFileChange"
      style="display: none"
    />
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/store/authStore'
import { useCommunityStore } from '@/store/communityStore'
import axios from 'axios'

export default {
  name: 'CreatePost',
  emits: ['post-created'],
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const communityStore = useCommunityStore()

    const content = ref('')
    const imageUrl = ref('')
    const videoUrl = ref('')
    const publishing = ref(false)
    const isUploading = ref(false)
    const isFocused = ref(false)
    const textarea = ref(null)
    const fileInput = ref(null)

    const userAvatar = computed(() => {
      return authStore.user?.profileImage || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guest'
    })

    const isValid = computed(() => {
      return (
        (content.value.trim().length > 0 ||
          imageUrl.value.length > 0 ||
          videoUrl.value.length > 0) &&
        !isUploading.value
      )
    })

    const adjustHeight = () => {
      if (textarea.value) {
        textarea.value.style.height = 'auto'
        const newHeight = Math.min(textarea.value.scrollHeight, 200) // Max height 200px
        textarea.value.style.height = newHeight + 'px'
      }
    }

    const triggerImageUpload = () => {
      fileInput.value.click()
    }

    const handleFileChange = async (e) => {
      const file = e.target.files[0]
      if (!file) return

      isUploading.value = true
      const formData = new FormData()
      formData.append('file', file)
      formData.append('upload_preset', 'boxofprayers-community')

      try {
        const res = await axios.post(
          'https://api.cloudinary.com/v1_1/dujc4iuu8/auto/upload',
          formData,
        )

        if (res.data.resource_type === 'video') {
          videoUrl.value = res.data.secure_url
          imageUrl.value = ''
        } else {
          imageUrl.value = res.data.secure_url
          videoUrl.value = ''
        }
      } catch (err) {
        console.error('Upload failed', err)
        alert('Failed to upload media. Please try again.')
      } finally {
        isUploading.value = false
        if (fileInput.value) fileInput.value.value = ''
      }
    }

    const removeMedia = () => {
      imageUrl.value = ''
      videoUrl.value = ''
    }

    const submitPost = () => {
      if (!isValid.value || publishing.value) return

      publishing.value = true

      const postData = {
        content: content.value || '',
      }

      if (imageUrl.value) postData.imageUrl = imageUrl.value
      if (videoUrl.value) postData.videoUrl = videoUrl.value

      communityStore
        .createPost(postData)
        .then(() => {
          content.value = ''
          imageUrl.value = ''
          videoUrl.value = ''
          if (textarea.value) textarea.value.style.height = 'auto'
          isFocused.value = false
          emit('post-created')
        })
        .catch((err) => {
          console.error('Failed to create post:', err)
          const errorMessage = err.response?.data?.message || 'Failed to post. Please try again.'
          alert(errorMessage)
        })
        .finally(() => {
          publishing.value = false
        })
    }

    return {
      content,
      imageUrl,
      videoUrl,
      publishing,
      isUploading,
      isValid,
      isFocused,
      userAvatar,
      textarea,
      fileInput,
      adjustHeight,
      triggerImageUpload,
      removeMedia, // Renamed from removeImage
      submitPost,
      handleFileChange,
    }
  },
}
</script>

<style scoped>
.create-post-card {
  background: rgba(20, 20, 24, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: var(--space-lg);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.create-post-card:focus-within {
  background: rgba(25, 25, 30, 0.8);
  border-color: var(--gold-glow);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-content {
  padding: var(--space-md);
}

.input-header {
  display: flex;
  gap: var(--space-sm);
}

.user-avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid rgba(229, 195, 122, 0.3);
}

.input-wrapper {
  flex: 1;
  padding-top: 2px;
}

.content-input {
  width: 100%;
  background: transparent;
  border: none;
  color: var(--starlight);
  font-family: var(--font-body);
  font-size: 1rem;
  line-height: 1.5;
  resize: none;
  padding: 8px 0;
  min-height: 44px;
}

.content-input:focus {
  outline: none;
}

.content-input::placeholder {
  color: var(--starlight-dim);
  opacity: 0.6;
}

/* Media Preview */
.media-preview {
  margin-top: var(--space-sm);
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.uploading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  color: var(--starlight-dim);
  font-size: 0.9rem;
}

.media-container {
  position: relative;
}

.media-container img,
.media-container video {
  width: 100%;
  height: auto;
  max-height: 300px;
  object-fit: cover;
  display: block;
}

.remove-media {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  backdrop-filter: blur(4px);
}

.remove-media:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
}

/* Actions Footer */
.actions-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-sm);
  padding-top: var(--space-xs);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.media-actions {
  display: flex;
  gap: var(--space-sm);
}

.media-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: none;
  color: var(--gold-primary);
  padding: 8px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.media-btn:hover {
  background: rgba(229, 195, 122, 0.1);
}

.media-icon {
  width: 20px;
  height: 20px;
}

.btn-label {
  font-size: 0.85rem;
  font-weight: 500;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 0.8rem;
  color: var(--starlight-dim);
}

.char-count.near-limit {
  color: var(--error);
}

.post-btn {
  background: linear-gradient(135deg, var(--gold-primary) 0%, #b8942e 100%);
  color: #0f0f12;
  border: none;
  padding: 8px 24px;
  border-radius: 24px;
  font-weight: 700;
  cursor: pointer;
  font-size: 0.95rem;
  min-width: 80px;
  height: 36px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.2);
}

.post-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(212, 175, 55, 0.3);
}

.post-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.3);
  box-shadow: none;
}

/* Spinner */
.spinner-sm {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(229, 195, 122, 0.2);
  border-top: 2px solid var(--gold-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.spinner-xs {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 0, 0, 0.2);
  border-top: 2px solid #000;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Mobile Adjustments */
@media (max-width: 480px) {
  .create-post-card {
    border-radius: 16px;
    margin-bottom: 20px;
  }

  .user-avatar {
    width: 38px;
    height: 38px;
  }

  .btn-label {
    display: none; /* Icon only on small screens */
  }

  .media-btn {
    padding: 8px;
  }
}
</style>
