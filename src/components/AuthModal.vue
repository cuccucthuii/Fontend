<template>
  <div v-if="isVisible" class="auth-modal-overlay" @click="closeModal">
    <div class="auth-modal" @click.stop>
      <div class="auth-modal-header">
        <h2>{{ isLogin ? 'Đăng nhập' : 'Đăng ký' }}</h2>
        <button class="close-btn" @click="closeModal">&times;</button>
      </div>
      
      <div class="auth-modal-body">
        <!-- Login Form -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label for="login-email">Email</label>
            <input
              id="login-email"
              v-model="loginForm.email"
              type="email"
              required
              placeholder="Nhập email của bạn"
            />
          </div>
          
          <div class="form-group">
            <label for="login-password">Mật khẩu</label>
            <input
              id="login-password"
              v-model="loginForm.password"
              type="password"
              required
              placeholder="Nhập mật khẩu"
            />
          </div>
          
          <button type="submit" class="auth-btn" :disabled="isLoading">
            {{ isLoading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
          </button>
        </form>
        
        <!-- Register Form -->
        <form v-else @submit.prevent="handleRegister" class="auth-form">
          <div class="form-group">
            <label for="register-name">Họ và tên</label>
            <input
              id="register-name"
              v-model="registerForm.fullName"
              type="text"
              required
              placeholder="Nhập họ và tên"
            />
          </div>
          
          <div class="form-group">
            <label for="register-email">Email</label>
            <input
              id="register-email"
              v-model="registerForm.email"
              type="email"
              required
              placeholder="Nhập email của bạn"
            />
          </div>
          
          <div class="form-group">
            <label for="register-phone">Số điện thoại</label>
            <input
              id="register-phone"
              v-model="registerForm.phone"
              type="tel"
              required
              placeholder="Nhập số điện thoại"
            />
          </div>
          
          <div class="form-group">
            <label for="register-password">Mật khẩu</label>
            <input
              id="register-password"
              v-model="registerForm.password"
              type="password"
              required
              placeholder="Nhập mật khẩu"
            />
          </div>
          
          <div class="form-group">
            <label for="register-confirm-password">Xác nhận mật khẩu</label>
            <input
              id="register-confirm-password"
              v-model="registerForm.confirmPassword"
              type="password"
              required
              placeholder="Nhập lại mật khẩu"
            />
          </div>
          
          <button type="submit" class="auth-btn" :disabled="isLoading">
            {{ isLoading ? 'Đang đăng ký...' : 'Đăng ký' }}
          </button>
        </form>
        
        <!-- Social Login -->
        <div class="social-login">
          <div class="divider">
            <span>Hoặc</span>
          </div>
          
          <button class="social-btn google-btn" @click="handleGoogleLogin">
            <svg width="20" height="20" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            Đăng nhập với Google
          </button>
        </div>
        
        <!-- Toggle between login and register -->
        <div class="auth-toggle">
          <p v-if="isLogin">
            Chưa có tài khoản? 
            <button type="button" @click="toggleMode" class="toggle-btn">
              Đăng ký ngay
            </button>
          </p>
          <p v-else>
            Đã có tài khoản? 
            <button type="button" @click="toggleMode" class="toggle-btn">
              Đăng nhập
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  defaultMode: {
    type: String,
    default: 'login' // 'login' or 'register'
  }
})

// Emits
const emit = defineEmits(['close', 'login-success', 'register-success'])

// Reactive data
const isVisible = ref(props.visible)
const isLogin = ref(props.defaultMode === 'login')
const isLoading = ref(false)

// Form data
const loginForm = reactive({
  email: '',
  password: ''
})

const registerForm = reactive({
  fullName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// Methods
const closeModal = () => {
  isVisible.value = false
  emit('close')
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  // Reset forms
  Object.keys(loginForm).forEach(key => loginForm[key] = '')
  Object.keys(registerForm).forEach(key => registerForm[key] = '')
}

const handleLogin = async () => {
  isLoading.value = true
  
  try {
    // TODO: Implement login API call
    console.log('Login data:', loginForm)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Success
    emit('login-success', loginForm)
    closeModal()
    
  } catch (error) {
    console.error('Login error:', error)
    // TODO: Show error message
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  if (registerForm.password !== registerForm.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  
  isLoading.value = true
  
  try {
    // TODO: Implement register API call
    console.log('Register data:', registerForm)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Success
    emit('register-success', registerForm)
    closeModal()
    
  } catch (error) {
    console.error('Register error:', error)
    // TODO: Show error message
  } finally {
    isLoading.value = false
  }
}

const handleGoogleLogin = async () => {
  try {
    // TODO: Implement Google OAuth
    console.log('Google login')
    
    // Simulate Google login
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Success
    emit('login-success', { provider: 'google' })
    closeModal()
    
  } catch (error) {
    console.error('Google login error:', error)
  }
}

// Watch for prop changes
watch(() => props.visible, (newVal) => {
  isVisible.value = newVal
})

watch(() => props.defaultMode, (newVal) => {
  isLogin.value = newVal === 'login'
})
</script>

<style scoped>
.auth-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.auth-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.auth-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.auth-modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1f2937;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.auth-modal-body {
  padding: 24px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.form-group input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.auth-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 8px;
}

.auth-btn:hover:not(:disabled) {
  background: #2563eb;
}

.auth-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.social-login {
  margin-top: 24px;
}

.divider {
  text-align: center;
  margin: 20px 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e5e7eb;
}

.divider span {
  background: white;
  padding: 0 16px;
  color: #6b7280;
  font-size: 14px;
}

.social-btn {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.social-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.google-btn {
  border-color: #dadce0;
}

.google-btn:hover {
  background: #f8f9fa;
  border-color: #dadce0;
}

.auth-toggle {
  margin-top: 24px;
  text-align: center;
}

.auth-toggle p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.toggle-btn {
  background: none;
  border: none;
  color: #3b82f6;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  font-size: 14px;
}

.toggle-btn:hover {
  color: #2563eb;
}

/* Responsive */
@media (max-width: 480px) {
  .auth-modal {
    width: 95%;
    margin: 20px;
  }
  
  .auth-modal-header,
  .auth-modal-body {
    padding: 16px;
  }
}
</style>
