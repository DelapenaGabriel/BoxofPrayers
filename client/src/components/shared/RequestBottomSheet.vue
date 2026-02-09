<template>
  <BottomSheet :isOpen="isOpen" :title="formTitle" @close="$emit('close')">
    <div class="request-form-container">
      <!-- SUCCESS STATE -->
      <div v-if="success" class="success-message">
        <div class="icon">🕊️</div>
        <h3>Prayer Received</h3>
        <p>Your request has been placed in the sanctuary.</p>
        <button @click="resetForm" class="btn-primary small">Return to Sanctuary</button>
      </div>

      <!-- FORM -->
      <form v-else @submit.prevent="submitRequest" class="glass-form">
        <!-- PRAYER CONTENT -->
        <div class="form-group">
          <label>Your Prayer</label>
          <textarea
            v-model="prayerContent"
            placeholder="Share your burden or gratitude..."
            rows="5"
            required
          ></textarea>
          <div class="char-count">{{ prayerContent.length }} characters</div>
        </div>

        <!-- CATEGORY -->
        <div class="form-group">
          <label>Category</label>
          <div class="chip-grid">
            <button
              v-for="cat in categories"
              :key="cat"
              type="button"
              class="chip"
              :class="{ active: selectedCategory === cat }"
              @click="selectedCategory = cat"
            >
              {{ cat }}
            </button>
          </div>
        </div>

        <!-- OPTIONS -->
        <div class="form-options">
          <label class="toggle-label">
            <input type="checkbox" v-model="isAnonymous" />
            <span class="custom-checkbox"></span>
            <span>Keep Anonymous</span>
          </label>
        </div>

        <!-- SUBMIT -->
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="loading || !isValid">
            {{ loading ? 'Sending...' : 'Submit Request' }}
          </button>
        </div>
      </form>
    </div>
  </BottomSheet>

  <!-- Guest Auth Prompt -->
  <AuthPromptModal :isOpen="showAuthPrompt" @close="showAuthPrompt = false" />
</template>

<script>
import { ref, computed, watch } from 'vue'
import BottomSheet from '@/components/shared/BottomSheet.vue'
import AuthPromptModal from '@/components/AuthPromptModal.vue'
import PrayerRequestService from '@/services/PrayerRequestService'
import { useAuthStore } from '@/store/authStore'

export default {
  name: 'RequestBottomSheet',
  components: { BottomSheet, AuthPromptModal },
  props: {
    isOpen: Boolean,
    title: String,
    requestToEdit: {
      type: Object,
      default: null,
    },
  },
  emits: ['close', 'submitted'],
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const prayerContent = ref('')
    const selectedCategory = ref('General')
    const isAnonymous = ref(false)
    const loading = ref(false)
    const success = ref(false)
    const showAuthPrompt = ref(false)

    const categories = ['General', 'Healing', 'Peace', 'Gratitude', 'Family', 'Other']

    // Watch for edit mode
    watch(
      () => props.isOpen,
      (newVal) => {
        if (newVal) {
          if (props.requestToEdit) {
            // Fill form for editing
            prayerContent.value = props.requestToEdit.content || ''
            selectedCategory.value = props.requestToEdit.category || 'General'
            isAnonymous.value = props.requestToEdit.isAnonymous || false
          } else {
            // Reset for new request
            prayerContent.value = ''
            selectedCategory.value = 'General'
            isAnonymous.value = false
            success.value = false
          }
        }
      },
    )

    const formTitle = computed(() => {
      if (props.title) return props.title
      return props.requestToEdit ? 'Edit Prayer Request' : 'New Prayer Request'
    })

    const isValid = computed(() => {
      return prayerContent.value.length > 5 && selectedCategory.value
    })

    const submitRequest = async () => {
      if (!authStore.token) {
        showAuthPrompt.value = true
        return
      }

      if (!isValid.value) return
      loading.value = true

      const user = authStore.user || {}
      const payload = {
        content: prayerContent.value,
        category: selectedCategory.value,
        isAnonymous: isAnonymous.value,
        name: isAnonymous.value ? 'Anonymous' : user.name || 'Anonymous',
      }

      try {
        if (props.requestToEdit) {
          await PrayerRequestService.updatePrayerRequest(props.requestToEdit.id, payload)
        } else {
          await PrayerRequestService.createPrayerRequest(payload)
        }

        authStore.triggerFeedRefresh()
        success.value = true
        emit('submitted')
      } catch (error) {
        console.error('Failed to submit prayer', error)
        alert('Briefly unable to send. Please try again.')
      } finally {
        loading.value = false
      }
    }

    const resetForm = () => {
      prayerContent.value = ''
      selectedCategory.value = 'General'
      success.value = false
      emit('close')
    }

    return {
      prayerContent,
      selectedCategory,
      isAnonymous,
      loading,
      success,
      categories,
      isValid,
      submitRequest,
      resetForm,
      showAuthPrompt,
      formTitle,
    }
  },
}
</script>

<style scoped>
.request-form-container {
  padding: 0 var(--space-lg) var(--space-2xl) var(--space-lg);
  color: #fff;
}

.form-group {
  margin-bottom: var(--space-lg);
}

label {
  display: block;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--gold-primary);
  opacity: 0.8;
  margin-bottom: var(--space-sm);
  font-weight: 600;
}

textarea {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-md);
  color: #fff;
  font-family: var(--font-body);
  font-size: 1rem;
  resize: none;
  transition: all 0.3s ease;
}

textarea:focus {
  outline: none;
  border-color: var(--gold-primary);
  background: rgba(255, 255, 255, 0.08);
}

.char-count {
  text-align: right;
  font-size: 0.7rem;
  color: var(--starlight-dim);
  margin-top: 5px;
}

/* Chip Grid */
.chip-grid {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-sm);
}

.chip {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: var(--space-sm) var(--space-md);
  border-radius: 20px;
  color: var(--starlight-dim);
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.chip.active {
  background: var(--gold-primary);
  color: var(--void-bg);
  border-color: var(--gold-primary);
  font-weight: 700;
}

/* Options */
.form-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-xl);
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
  font-size: 0.9rem;
  color: #ddd;
  text-transform: none;
  letter-spacing: 0;
}

.toggle-label input {
  display: none;
}

.custom-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid var(--gold-dim);
  border-radius: 6px;
  position: relative;
  transition: all 0.2s;
}

.toggle-label input:checked + .custom-checkbox {
  background: var(--gold-primary);
  border-color: var(--gold-primary);
}

.toggle-label input:checked + .custom-checkbox::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: var(--void-bg);
  font-size: 14px;
  font-weight: bold;
}

/* Actions */
.btn-primary {
  width: 100%;
  background: linear-gradient(135deg, var(--gold-primary) 0%, var(--gold-dim) 100%);
  color: var(--void-bg);
  border: none;
  padding: var(--space-md);
  border-radius: var(--radius-lg);
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.2s;
  height: 56px; /* Touch target */
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.success-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 0;
}
.success-message .icon {
  font-size: 3rem;
  margin-bottom: 20px;
}
.success-message h3 {
  color: var(--gold-primary);
  font-family: var(--font-heading);
  margin-bottom: 10px;
}
.success-message p {
  color: var(--starlight-dim);
  margin-bottom: 20px;
}
.btn-primary.small {
  width: auto;
  min-width: 200px;
  padding: 14px 32px;
  margin-top: 10px;
  height: auto;
}
</style>
