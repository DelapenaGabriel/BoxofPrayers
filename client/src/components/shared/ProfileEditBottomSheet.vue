<template>
  <BottomSheet :isOpen="isOpen" title="Edit Profile" @close="$emit('close')">
    <div class="profile-edit-container">
      <form @submit.prevent="saveProfile" class="glass-form">
        <!-- AVATAR UPLOAD -->
        <div class="avatar-edit" @click="triggerFileInput">
          <div class="avatar-circle">
            <img
              v-if="previewImage || currentUser.profileImage"
              :src="previewImage || currentUser.profileImage"
              class="avatar-img"
            />
            <span v-else>{{ initials }}</span>
          </div>
          <span class="change-photo-text">Change Photo</span>
          <input
            type="file"
            ref="fileInput"
            accept="image/*"
            class="hidden-input"
            @change="handleFileChange"
          />
        </div>

        <!-- FULL NAME -->
        <div class="form-group">
          <label>Full Name</label>
          <input v-model="form.name" type="text" placeholder="Your Name" required />
        </div>

        <!-- SPIRITUAL NAME -->
        <div class="form-group">
          <label>Spiritual Name (Display)</label>
          <input
            v-model="form.displayName"
            type="text"
            placeholder="e.g. Brother Thomas"
            required
          />
        </div>

        <!-- ACTIONS -->
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? 'Saving...' : 'Save Changes' }}
          </button>
          <button type="button" class="btn-ghost" @click="$emit('close')">Cancel</button>
        </div>
      </form>
    </div>
  </BottomSheet>
</template>

<script>
import { ref, computed, watch } from 'vue'
import BottomSheet from '@/components/shared/BottomSheet.vue'
import { useAuthStore } from '@/store/authStore'
import api from '@/services/api'
import CloudinaryService from '@/services/CloudinaryService'

export default {
  name: 'ProfileEditBottomSheet',
  components: { BottomSheet },
  props: {
    isOpen: Boolean,
    currentUser: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ['close', 'updated'],
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const loading = ref(false)
    const fileInput = ref(null)
    const previewImage = ref(null)
    const selectedFile = ref(null)

    const form = ref({
      name: '',
      displayName: '',
    })

    // Sync prop to form when specific prop changes or opens
    watch(
      () => props.isOpen,
      (newVal) => {
        if (newVal && props.currentUser) {
          form.value = {
            name: props.currentUser.name || '',
            displayName: props.currentUser.displayName || '',
          }
          previewImage.value = null
          selectedFile.value = null
        }
      },
    )

    const initials = computed(() => {
      const n = form.value.name || form.value.displayName || '?'
      return n.charAt(0).toUpperCase()
    })

    const triggerFileInput = () => {
      fileInput.value.click()
    }

    const handleFileChange = (event) => {
      const file = event.target.files[0]
      if (!file) return

      selectedFile.value = file

      // Create preview
      const reader = new FileReader()
      reader.onload = (e) => {
        previewImage.value = e.target.result
      }
      reader.readAsDataURL(file)
    }

    const saveProfile = async () => {
      loading.value = true
      try {
        // 1. Upload Image if selected
        let imageUrl = props.currentUser.profileImage

        if (selectedFile.value) {
          // Upload to Cloudinary
          imageUrl = await CloudinaryService.uploadImage(selectedFile.value)
        }

        // 2. Update Profile Data
        const profileData = {
          ...form.value,
          profileImage: imageUrl,
        }

        const response = await api.put('/api/users/profile', profileData)

        // Update local store state
        authStore.setUser({ ...authStore.user, ...profileData })

        emit('updated')
        emit('close')
      } catch (error) {
        console.error('Failed to update profile', error)
        alert('Could not update profile.')
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      initials,
      saveProfile,
      fileInput,
      triggerFileInput,
      handleFileChange,
      previewImage,
    }
  },
}
</script>

<style scoped>
.profile-edit-container {
  padding: 0 20px 40px 20px;
  color: #fff;
}

.avatar-edit {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  cursor: pointer;
}

.avatar-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--gold-primary), var(--gold-dim));
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  font-weight: 700;
  color: var(--void-bg);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  margin-bottom: 10px;
}

.change-photo-text {
  font-size: 0.8rem;
  color: var(--gold-primary);
  text-decoration: underline;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--gold-primary);
  opacity: 0.8;
  margin-bottom: 8px;
  font-weight: 600;
}

input,
textarea {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 14px;
  color: #fff;
  font-family: var(--font-body);
  font-size: 1rem;
  transition: all 0.3s ease;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: var(--gold-primary);
  background: rgba(255, 255, 255, 0.08);
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 30px;
}

.btn-primary {
  width: 100%;
  background: linear-gradient(135deg, var(--gold-primary) 0%, var(--gold-dim) 100%);
  color: var(--void-bg);
  border: none;
  padding: 16px;
  border-radius: 16px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
}

.btn-primary:disabled {
  opacity: 0.6;
}

.btn-ghost {
  background: none;
  border: none;
  color: var(--starlight-dim);
  cursor: pointer;
}

.hidden-input {
  display: none;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}
</style>
