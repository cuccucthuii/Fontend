<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Đổi mật khẩu</h2>
        <button class="close-btn" @click="closeModal">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="password-form">
        <div class="form-group">
          <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
          <div class="input-wrapper">
            <input
              id="currentPassword"
              v-model="form.currentPassword"
              :type="showCurrentPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="Nhập mật khẩu hiện tại"
              required
            />
            <button
              type="button"
              class="password-toggle"
              @click="showCurrentPassword = !showCurrentPassword"
            >
              <svg v-if="showCurrentPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="newPassword" class="form-label">Mật khẩu mới</label>
          <div class="input-wrapper">
            <input
              id="newPassword"
              v-model="form.newPassword"
              :type="showNewPassword ? 'text' : 'password'"
              class="form-input"
              :class="{ 'error': form.newPassword && form.newPassword.length < 6 }"
              placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)"
              required
              minlength="6"
              @input="checkPasswordStrength"
            />
            <button
              type="button"
              class="password-toggle"
              @click="showNewPassword = !showNewPassword"
            >
              <svg v-if="showNewPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
            </button>
          </div>
          
          <!-- Password strength indicator -->
          <div v-if="form.newPassword" class="password-strength">
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :class="passwordStrength.level"
                :style="{ width: passwordStrength.percentage + '%' }"
              ></div>
            </div>
            <span class="strength-text" :class="passwordStrength.level">
              {{ passwordStrength.text }}
            </span>
          </div>
          
          <!-- Minimum length error -->
          <div v-if="form.newPassword && form.newPassword.length < 6" class="password-error">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" class="error-icon">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Mật khẩu phải có ít nhất 6 ký tự</span>
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
          <div class="input-wrapper">
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              class="form-input"
              :class="{ 'error': form.confirmPassword && !isPasswordMatch }"
              placeholder="Nhập lại mật khẩu mới"
              required
            />
            <button
              type="button"
              class="password-toggle"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <svg v-if="showConfirmPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
            </button>
          </div>
          
          <!-- Password match indicator -->
          <div v-if="form.confirmPassword" class="password-match">
            <svg v-if="isPasswordMatch" width="16" height="16" viewBox="0 0 24 24" fill="none" class="match-icon success">
              <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" class="match-icon error">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span :class="{ 'success': isPasswordMatch, 'error': !isPasswordMatch }">
              {{ isPasswordMatch ? 'Mật khẩu khớp' : 'Mật khẩu không khớp' }}
            </span>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeModal">
            Hủy
          </button>
          <button 
            type="submit" 
            class="btn-submit"
            :disabled="!isFormValid || isLoading"
            :class="{ 'loading': isLoading }"
          >
            <svg v-if="isLoading" class="loading-spinner" width="20" height="20" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" opacity="0.3"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            {{ isLoading ? 'Đang xử lý...' : 'Đổi mật khẩu' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { passwordService } from '../services/passwordService'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'success', 'error'])

// Form data
const form = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// UI state
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const isLoading = ref(false)

// Password strength
const passwordStrength = ref({
  level: 'weak',
  percentage: 0,
  text: ''
})

// Computed properties
const isPasswordMatch = computed(() => {
  return form.value.newPassword && form.value.confirmPassword && 
         form.value.newPassword === form.value.confirmPassword
})

const isFormValid = computed(() => {
  return form.value.currentPassword && 
         form.value.newPassword && 
         form.value.confirmPassword &&
         form.value.newPassword.length >= 6 &&
         isPasswordMatch.value &&
         passwordStrength.value.level !== 'weak'
})

// Methods
function closeModal() {
  resetForm()
  emit('close')
}

function resetForm() {
  form.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  showCurrentPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
  passwordStrength.value = {
    level: 'weak',
    percentage: 0,
    text: ''
  }
}

function checkPasswordStrength() {
  const password = form.value.newPassword
  if (!password) {
    passwordStrength.value = { level: 'weak', percentage: 0, text: '' }
    return
  }

  let score = 0
  let feedback = []

  // Length check
  if (password.length >= 6) score += 25
  else feedback.push('Ít nhất 6 ký tự')

  // Lowercase check
  if (/[a-z]/.test(password)) score += 25
  else feedback.push('Có chữ thường')

  // Uppercase check
  if (/[A-Z]/.test(password)) score += 25
  else feedback.push('Có chữ hoa')

  // Number check
  if (/\d/.test(password)) score += 25
  else feedback.push('Có số')

  // Special character check
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 10
  else feedback.push('Có ký tự đặc biệt')

  // Determine strength level
  let level, text
  if (score < 50) {
    level = 'weak'
    text = 'Yếu'
  } else if (score < 75) {
    level = 'medium'
    text = 'Trung bình'
  } else if (score < 90) {
    level = 'strong'
    text = 'Mạnh'
  } else {
    level = 'very-strong'
    text = 'Rất mạnh'
  }

  passwordStrength.value = {
    level,
    percentage: Math.min(score, 100),
    text
  }
}

async function handleSubmit() {
  if (!isFormValid.value) return

  isLoading.value = true

  try {
    await passwordService.changePassword({
      currentPassword: form.value.currentPassword,
      newPassword: form.value.newPassword,
      confirmPassword: form.value.confirmPassword
    })
    
    emit('success', 'Mật khẩu đã được thay đổi thành công!')
    closeModal()
  } catch (error) {
    console.error('Error changing password:', error)
    
    // Handle specific error messages
    let errorMessage = 'Có lỗi xảy ra khi đổi mật khẩu'
    
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.response?.data?.error) {
      errorMessage = error.response.data.error
    } else if (error.response?.status === 400) {
      errorMessage = 'Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin.'
    } else if (error.response?.status === 401) {
      errorMessage = 'Mật khẩu hiện tại không đúng hoặc phiên đăng nhập đã hết hạn'
    } else if (error.response?.status === 403) {
      errorMessage = 'Bạn không có quyền thực hiện thao tác này'
    } else if (error.response?.status === 500) {
      errorMessage = 'Lỗi máy chủ. Vui lòng thử lại sau.'
    }
    
    // You can emit an error event or show a toast notification
    emit('error', errorMessage)
  } finally {
    isLoading.value = false
  }
}

// Watch for modal close
watch(() => props.show, (newValue) => {
  if (!newValue) {
    resetForm()
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  max-width: 480px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 24px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.modal-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  color: #666;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.password-form {
  padding: 0 24px 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.form-input:focus {
  outline: none;
  border-color: #48dbfb;
  background: white;
  box-shadow: 0 0 0 3px rgba(72, 219, 251, 0.1);
}

.form-input.error {
  border-color: #e74c3c;
  background: #fdf2f2;
}

.password-toggle {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: #48dbfb;
  background: rgba(72, 219, 251, 0.1);
}

.password-strength {
  margin-top: 8px;
}

.strength-bar {
  height: 4px;
  background: #e1e5e9;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-fill.weak {
  background: #e74c3c;
}

.strength-fill.medium {
  background: #f39c12;
}

.strength-fill.strong {
  background: #27ae60;
}

.strength-fill.very-strong {
  background: #2ecc71;
}

.strength-text {
  font-size: 12px;
  font-weight: 600;
}

.strength-text.weak {
  color: #e74c3c;
}

.strength-text.medium {
  color: #f39c12;
}

.strength-text.strong {
  color: #27ae60;
}

.strength-text.very-strong {
  color: #2ecc71;
}

.password-match {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
}

.match-icon {
  width: 16px;
  height: 16px;
}

.match-icon.success {
  color: #27ae60;
}

.match-icon.error {
  color: #e74c3c;
}

.password-match .success {
  color: #27ae60;
}

.password-match .error {
  color: #e74c3c;
}

.password-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #e74c3c;
}

.error-icon {
  width: 16px;
  height: 16px;
  color: #e74c3c;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.btn-cancel {
  flex: 1;
  padding: 12px 24px;
  border: 2px solid #e1e5e9;
  border-radius: 10px;
  background: white;
  color: #666;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  border-color: #48dbfb;
  color: #48dbfb;
  background: rgba(72, 219, 251, 0.05);
}

.btn-submit {
  flex: 2;
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.3);
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(72, 219, 251, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-submit.loading {
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 640px) {
  .modal-container {
    margin: 20px;
    max-width: none;
  }
  
  .modal-header {
    padding: 20px 20px 0;
  }
  
  .password-form {
    padding: 0 20px 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn-cancel,
  .btn-submit {
    flex: 1;
  }
}
</style>
