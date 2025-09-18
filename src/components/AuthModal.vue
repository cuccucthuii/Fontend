<template>
  <transition name="modal-fade">
    <div v-if="show" class="modal-overlay" @click.self="closeModal">
      <div class="modal-container">
        <!-- Close button -->
        <button class="close-btn" @click="closeModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <!-- Modal content -->
        <div class="modal-content">
          <!-- Left decorative panel -->
          <div class="decorative-panel">
            <div class="decorative-icon">
              <div class="icon-ring outer"></div>
              <div class="icon-ring middle"></div>
              <div class="icon-ring inner"></div>
              <div class="icon-center">
                <div class="center-dot"></div>
                <div class="center-dots">
                  <div class="dot"></div>
                  <div class="dot"></div>
                  <div class="dot"></div>
                  <div class="dot"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- Right form panel -->
          <div class="form-panel">
            <!-- Header with mascot and tabs -->
            <div class="form-header">
              <div class="mascot-section">
                <div class="mascot">🎬</div>
                <span class="brand-name">DEV CINEMA</span>
              </div>
              
              <!-- Tab buttons -->
              <div class="tab-buttons">
                <button 
                  :class="['tab-btn', { active: activeTab === 'login' }]"
                  @click="activeTab = 'login'"
                >
                  Đăng nhập
                </button>
                <button 
                  :class="['tab-btn', { active: activeTab === 'register' }]"
                  @click="activeTab = 'register'"
                >
                  Đăng ký
                </button>
              </div>
            </div>

            <!-- Form title -->
            <h2 class="form-title">{{ activeTab === 'login' ? 'Đăng nhập' : 'Đăng ký' }}</h2>

            <!-- Login Form -->
            <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="auth-form">
              <div class="form-group">
                <label class="form-label">Email</label>
                <input 
                  v-model="loginForm.email"
                  type="email" 
                  class="form-input"
                  placeholder="Email"
                  required
                />
              </div>
              
              <div class="form-group">
                <label class="form-label">Mật khẩu</label>
                <div class="password-input-wrapper">
                  <input 
                    v-model="loginForm.password"
                    :type="showLoginPassword ? 'text' : 'password'"
                    class="form-input"
                    placeholder="Mật khẩu"
                    required
                  />
                  <button 
                    type="button"
                    class="password-toggle"
                    @click="showLoginPassword = !showLoginPassword"
                  >
                    <svg v-if="!showLoginPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                      <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                      <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
                    </svg>
                  </button>
                </div>
              </div>

              <button type="submit" class="submit-btn" :disabled="isLoading">
                <span v-if="isLoading">Đang xử lý...</span>
                <span v-else>Đăng nhập</span>
              </button>

              <div class="form-footer">
                <a href="#" class="forgot-password">Quên mật khẩu?</a>
              </div>
            </form>

            <!-- Register Form -->
            <form v-if="activeTab === 'register'" @submit.prevent="handleRegister" class="auth-form">
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Họ</label>
                  <input 
                    v-model="registerForm.lastName"
                    type="text" 
                    class="form-input"
                    placeholder="Họ"
                    required
                  />
                </div>
                <div class="form-group">
                  <label class="form-label">Tên</label>
                  <input 
                    v-model="registerForm.firstName"
                    type="text" 
                    class="form-input"
                    placeholder="Tên"
                    required
                  />
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Email</label>
                <input 
                  v-model="registerForm.email"
                  type="email" 
                  class="form-input"
                  placeholder="Email"
                  required
                />
              </div>

              <div class="form-group">
                <label class="form-label">Số điện thoại</label>
                <input 
                  v-model="registerForm.phone"
                  type="tel" 
                  class="form-input"
                  placeholder="Số điện thoại"
                  required
                />
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Mật khẩu</label>
                  <div class="password-input-wrapper">
                    <input 
                      v-model="registerForm.password"
                      :type="showRegisterPassword ? 'text' : 'password'"
                      class="form-input"
                      placeholder="Mật khẩu"
                      required
                    />
                    <button 
                      type="button"
                      class="password-toggle"
                      @click="showRegisterPassword = !showRegisterPassword"
                    >
                      <svg v-if="!showRegisterPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                      </svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                        <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">Xác nhận mật khẩu</label>
                  <div class="password-input-wrapper">
                    <input 
                      v-model="registerForm.confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      class="form-input"
                      placeholder="Xác nhận mật khẩu"
                      required
                    />
                    <button 
                      type="button"
                      class="password-toggle"
                      @click="showConfirmPassword = !showConfirmPassword"
                    >
                      <svg v-if="!showConfirmPassword" width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                      </svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2"/>
                        <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>

              <button type="submit" class="submit-btn" :disabled="isLoading">
                <span v-if="isLoading">Đang xử lý...</span>
                <span v-else>Đăng ký</span>
              </button>

              <div class="form-footer">
                <span class="switch-text">Bạn đã có tài khoản?</span>
                <button type="button" class="switch-link" @click="activeTab = 'login'">Đăng nhập</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser, registerUser } from '../services/userService'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  initialTab: {
    type: String,
    default: 'login'
  }
})

const emit = defineEmits(['close', 'login-success'])

const router = useRouter()
const activeTab = ref(props.initialTab)
const isLoading = ref(false)

// Login form
const loginForm = ref({
  email: '',
  password: ''
})

// Register form
const registerForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// Password visibility
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)

// Watch for prop changes
watch(() => props.initialTab, (newTab) => {
  activeTab.value = newTab
})

watch(() => props.show, (newShow) => {
  if (newShow) {
    activeTab.value = props.initialTab
  }
})

function closeModal() {
  emit('close')
}

async function handleLogin() {
  if (isLoading.value) return
  
  isLoading.value = true
  try {
    const response = await loginUser({
      email: loginForm.value.email,
      password: loginForm.value.password
    })
    
    if (response.data) {
      // Store user info
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify(response.data))
      
      emit('login-success', response.data)
      closeModal()
      
      // Reset form
      loginForm.value = { email: '', password: '' }
    }
  } catch (error) {
    console.error('Login error:', error)
    alert('Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.')
  } finally {
    isLoading.value = false
  }
}

async function handleRegister() {
  if (isLoading.value) return
  
  // Validate password match
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  
  isLoading.value = true
  try {
    const response = await registerUser({
      firstName: registerForm.value.firstName,
      lastName: registerForm.value.lastName,
      email: registerForm.value.email,
      phone: registerForm.value.phone,
      password: registerForm.value.password
    })
    
    if (response.data) {
      alert('Đăng ký thành công! Vui lòng đăng nhập.')
      activeTab.value = 'login'
      
      // Pre-fill login form
      loginForm.value.email = registerForm.value.email
      
      // Reset register form
      registerForm.value = {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: ''
      }
    }
  } catch (error) {
    console.error('Register error:', error)
    alert('Đăng ký thất bại. Vui lòng thử lại.')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.modal-container {
  position: relative;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  z-index: 10;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.modal-content {
  display: flex;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  min-height: 500px;
}

.decorative-panel {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
}

.decorative-icon {
  position: relative;
  width: 120px;
  height: 120px;
}

.icon-ring {
  position: absolute;
  border-radius: 50%;
  border: 4px solid;
}

.icon-ring.outer {
  width: 120px;
  height: 120px;
  border-color: #000;
  top: 0;
  left: 0;
}

.icon-ring.middle {
  width: 80px;
  height: 80px;
  border-color: #ffd700;
  top: 20px;
  left: 20px;
}

.icon-ring.inner {
  width: 50px;
  height: 50px;
  border-color: #00bfff;
  top: 35px;
  left: 35px;
}

.icon-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
}

.center-dot {
  width: 20px;
  height: 20px;
  background: #ffd700;
  border-radius: 50%;
  position: absolute;
  top: 0;
  left: 0;
}

.center-dots {
  position: absolute;
  top: -15px;
  left: -15px;
  width: 50px;
  height: 50px;
}

.center-dots .dot {
  position: absolute;
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
}

.center-dots .dot:nth-child(1) { top: 0; left: 50%; transform: translateX(-50%); }
.center-dots .dot:nth-child(2) { top: 50%; right: 0; transform: translateY(-50%); }
.center-dots .dot:nth-child(3) { bottom: 0; left: 50%; transform: translateX(-50%); }
.center-dots .dot:nth-child(4) { top: 50%; left: 0; transform: translateY(-50%); }

.form-panel {
  flex: 2;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: 30px;
}

.mascot-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.mascot {
  font-size: 32px;
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  letter-spacing: 1px;
}

.tab-buttons {
  display: flex;
  gap: 8px;
  background: #f1f3f4;
  border-radius: 12px;
  padding: 4px;
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin: 0 0 30px 0;
  text-align: center;
}

.auth-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.form-input {
  padding: 16px;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  font-size: 16px;
  background: #f8f9fa;
  transition: all 0.2s;
  outline: none;
}

.form-input:focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.password-input-wrapper {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: #333;
}

.submit-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 16px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.forgot-password {
  color: #ff6b6b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
}

.forgot-password:hover {
  text-decoration: underline;
}

.switch-text {
  color: #666;
  font-size: 14px;
  margin-right: 8px;
}

.switch-link {
  background: none;
  border: none;
  color: #ff6b6b;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
}

.switch-link:hover {
  color: #ee5a52;
}

/* Responsive */
@media (max-width: 768px) {
  .modal-content {
    flex-direction: column;
  }
  
  .decorative-panel {
    display: none;
  }
  
  .form-panel {
    padding: 30px 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
  
  .form-title {
    font-size: 28px;
  }
}

/* Animation */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container,
.modal-fade-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from .modal-container {
  transform: scale(0.9) translateY(20px);
}

.modal-fade-leave-to .modal-container {
  transform: scale(0.9) translateY(20px);
}
</style>
