<template>
  <div class="register-bg">
    <div class="register-glass">
      <div class="register-svg">
        <div class="register-illustration">
          <svg width="300" height="300" viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="150" cy="150" r="120" fill="rgba(253, 160, 133, 0.1)" stroke="rgba(253, 160, 133, 0.3)"
              stroke-width="2" />
            <path d="M150 80 L180 120 L150 160 L120 120 Z" fill="rgba(253, 160, 133, 0.6)" />
            <circle cx="150" cy="200" r="30" fill="rgba(246, 211, 101, 0.6)" />
            <text x="150" y="250" text-anchor="middle" fill="#666" font-size="16" font-family="Inter, sans-serif">Cinema
              System</text>
          </svg>
        </div>
      </div>
      <div class="register-form">
        <div class="logo">
          <img src="/vite.svg" alt="Logo" />
        </div>

        <!-- Debug info -->
        <div style="background: #f0f0f0; padding: 10px; margin-bottom: 10px; font-size: 12px;">
          Debug: showOTP = {{ showOTP }}, isSubmitting = {{ isSubmitting }}
        </div>

        <!-- Bước 1: Form đăng ký -->
        <div v-if="!showOTP">
          <h2>Đăng ký tài khoản</h2>
          <form @submit.prevent="handleRegister">
            <input v-model="fullName" placeholder="Họ và tên" required />
            <input v-model="phone" placeholder="Số điện thoại" required />
            <input v-model="email" type="email" placeholder="Email" required />
            <select v-model="role" required>
              <option value="">Chọn vai trò</option>
              <option value="manager">Quản lý</option>
              <option value="customer">Khách hàng</option>
              <option value="staff">Nhân viên bán vé</option>
            </select>
            <div class="input-group">
              <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Mật khẩu" required />
              <span class="toggle-pw" @click="showPassword = !showPassword">
                <svg v-if="showPassword" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5"
                  viewBox="0 0 24 24">
                  <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5"
                  viewBox="0 0 24 24">
                  <path
                    d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33" />
                  <path d="M1 1l22 22" />
                </svg>
              </span>
            </div>
            <input :type="showPassword ? 'text' : 'password'" v-model="confirmPassword" placeholder="Xác nhận mật khẩu"
              required />
            <button type="submit" :disabled="isSubmitting">
              <span v-if="isSubmitting">Đang xử lý...</span>
              <span v-else>Đăng ký</span>
            </button>
            <button type="button" @click="testShowOTP" style="margin-top: 10px; background: #ff6b6b;">
              Test Hiện OTP
            </button>
            <button type="button" @click="testSendOTP" style="margin-top: 10px; background: #4ecdc4;">
              Test Gửi OTP
            </button>
          </form>
        </div>

        <!-- Bước 2: Xác thực OTP -->
        <div v-else>
          <h2>Xác thực OTP</h2>
          <div class="otp-info">
            <p>Mã OTP đã được gửi đến email:</p>
            <p class="email-number">{{ email }}</p>
          </div>
          <form @submit.prevent="verifyOTP">
            <div class="otp-input-group">
              <input v-for="(digit, index) in 6" :key="index" v-model="otpDigits[index]" type="text" maxlength="1"
                class="otp-input" :data-index="index" @input="handleOTPInput($event, index)"
                @keydown="handleOTPKeydown($event, index)" ref="otpInputs" />
            </div>
            <div class="otp-actions">
              <button type="submit" :disabled="isVerifying || !isOTPComplete">
                <span v-if="isVerifying">Đang xác thực...</span>
                <span v-else>Xác thực OTP</span>
              </button>
              <button type="button" @click="resendOTP" :disabled="resendCountdown > 0" class="resend-btn">
                <span v-if="resendCountdown > 0">Gửi lại ({{ resendCountdown }}s)</span>
                <span v-else>Gửi lại OTP</span>
              </button>
            </div>
          </form>
          <button @click="backToRegister" class="back-btn">← Quay lại đăng ký</button>
        </div>

        <div class="register-links" v-if="!showOTP">
          <router-link to="/login">Đã có tài khoản? Đăng nhập</router-link>
        </div>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { registerUser } from '../services/userService'
import { AUTH_ENDPOINTS } from '../constants/api'
import api from '../services/api'

const fullName = ref('')
const phone = ref('')
const email = ref('')
const role = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const showPassword = ref(false)
const showOTP = ref(false)
const isSubmitting = ref(false)
const isVerifying = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])
const resendCountdown = ref(0)
const router = useRouter()



// Handle OTP input
function handleOTPInput(event, index) {
  const value = event.target.value
  if (value && /^\d$/.test(value)) {
    otpDigits.value[index] = value
    console.log(`Nhập OTP digit ${index + 1}:`, value)
    if (index < 5) {
      nextTick(() => {
        const nextInput = document.querySelector(`input[data-index="${index + 1}"]`)
        if (nextInput) nextInput.focus()
      })
    }
  } else {
    otpDigits.value[index] = ''
  }
}

// Handle OTP keydown (backspace)
function handleOTPKeydown(event, index) {
  if (event.key === 'Backspace' && !otpDigits.value[index] && index > 0) {
    otpDigits.value[index - 1] = ''
    nextTick(() => {
      const prevInput = document.querySelector(`input[data-index="${index - 1}"]`)
      if (prevInput) prevInput.focus()
    })
  }
}

// Check if OTP is complete
const isOTPComplete = computed(() => {
  return otpDigits.value.every(digit => digit !== '')
})

// Get OTP string
function getOTPString() {
  const otpString = otpDigits.value.join('')
  console.log('OTP string:', otpString)
  return otpString
}

// Send OTP API
async function sendOTP() {
  try {
    console.log('=== DEBUG SEND OTP ===')
    console.log('URL:', AUTH_ENDPOINTS.SEND_OTP)
    console.log('Data gửi:', { phone: phone.value, email: email.value })

    const response = await api.post(AUTH_ENDPOINTS.SEND_OTP, {
      phone: phone.value,
      email: email.value
    })

    console.log('Response status:', response.status)
    console.log('Response data:', response.data)
    console.log('=== END DEBUG ===')
    return response.data
  } catch (error) {
    console.error('=== LỖI API SEND OTP ===')
    console.error('Error object:', error)
    console.error('Error response:', error.response)
    console.error('Error message:', error.message)
    console.error('=== END LỖI ===')
    throw error
  }
}

// Verify OTP API
async function verifyOTPAPI(otp) {
  try {
    console.log('Gọi API verify OTP:', AUTH_ENDPOINTS.VERIFY_OTP)
    const response = await api.post(AUTH_ENDPOINTS.VERIFY_OTP, {
      phone: phone.value,
      email: email.value,
      otp: otp
    })
    console.log('Response từ API verify OTP:', response.data)
    return response.data
  } catch (error) {
    console.error('Lỗi API verify OTP:', error)
    throw error
  }
}

// Handle register (step 1)
async function handleRegister() {
  errorMessage.value = ''
  successMessage.value = ''

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp.'
    return
  }

  isSubmitting.value = true
  try {
    console.log('Đang gửi OTP cho email:', email.value)
    console.log('showOTP trước khi gửi:', showOTP.value)

    // Gửi OTP trước
    await sendOTP()
    console.log('Gửi OTP thành công, chuyển sang màn hình OTP')

    // Nếu gửi OTP thành công, chuyển sang bước 2
    showOTP.value = true
    console.log('showOTP sau khi set true:', showOTP.value)
    startResendCountdown()

  } catch (error) {
    console.error('Lỗi khi gửi OTP:', error)
    console.log('showOTP khi có lỗi:', showOTP.value)
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = 'Không thể gửi OTP. Vui lòng thử lại.'
    }
  } finally {
    isSubmitting.value = false
  }
}

// Handle OTP verification (step 2)
async function verifyOTP() {
  if (!isOTPComplete.value) {
    errorMessage.value = 'Vui lòng nhập đầy đủ 6 số OTP.'
    return
  }

  isVerifying.value = true
  errorMessage.value = ''

  try {
    const otp = getOTPString()
    console.log('Đang xác thực OTP:', otp)

    // Xác thực OTP
    const otpResult = await verifyOTPAPI(otp)
    console.log('Kết quả xác thực OTP:', otpResult)

    if (otpResult.success) {
      console.log('OTP đúng, đang đăng ký user...')
      // Nếu OTP đúng, đăng ký user
      const registerResult = await registerUser({
        fullName: fullName.value,
        phone: phone.value,
        email: email.value,
        role: role.value,
        password: password.value
      })
      console.log('Kết quả đăng ký user:', registerResult)

      if (registerResult.success || registerResult.status === 'success') {
        successMessage.value = 'Đăng ký thành công! Vui lòng đăng nhập.'
        setTimeout(() => router.push('/login'), 1500)
      } else {
        errorMessage.value = registerResult.message || 'Đăng ký thất bại!'
      }
    } else {
      errorMessage.value = 'Mã OTP không đúng. Vui lòng thử lại.'
    }
  } catch (error) {
    console.error('Lỗi khi xác thực OTP:', error)
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = 'Xác thực OTP thất bại. Vui lòng thử lại.'
    }
  } finally {
    isVerifying.value = false
  }
}

// Resend OTP
async function resendOTP() {
  try {
    console.log('Đang gửi lại OTP...')
    await sendOTP()
    startResendCountdown()
    successMessage.value = 'Đã gửi lại mã OTP!'
  } catch (error) {
    console.error('Lỗi khi gửi lại OTP:', error)
    errorMessage.value = 'Không thể gửi lại OTP. Vui lòng thử lại.'
  }
}

// Start resend countdown
function startResendCountdown() {
  console.log('Bắt đầu đếm ngược gửi lại OTP')
  resendCountdown.value = 60
  const timer = setInterval(() => {
    resendCountdown.value--
    if (resendCountdown.value <= 0) {
      clearInterval(timer)
      console.log('Đếm ngược gửi lại OTP kết thúc')
    }
  }, 1000)
}

// Test function to show OTP screen
function testShowOTP() {
  console.log('Test: Force hiển thị màn hình OTP')
  showOTP.value = true
  startResendCountdown()
}

// Test function to send OTP
async function testSendOTP() {
  console.log('Test: Gửi OTP với email:', email.value)
  try {
    await sendOTP()
    console.log('Test: Gửi OTP thành công!')
    showOTP.value = true
    startResendCountdown()
  } catch (error) {
    console.error('Test: Gửi OTP thất bại!')
  }
}

// Back to register form
function backToRegister() {
  console.log('Quay lại form đăng ký')
  showOTP.value = false
  otpDigits.value = ['', '', '', '', '', '']
  errorMessage.value = ''
  successMessage.value = ''
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');

.register-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #f6d365 0%, #fda085 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', sans-serif;
}

.register-glass {
  display: flex;
  flex-direction: row;
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.18);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 40px;
  border: 1.5px solid rgba(255, 255, 255, 0.18);
  overflow: hidden;
  max-width: 900px;
  width: 100%;
}

.register-svg {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  min-width: 320px;
  padding: 32px 0 32px 32px;
}

.register-illustration {
  width: 100%;
  max-width: 340px;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-form {
  flex: 1;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  min-width: 320px;
}

.logo img {
  width: 56px;
  margin-bottom: 16px;
}

h2 {
  margin-bottom: 24px;
  font-weight: 700;
  color: #222;
  letter-spacing: 0.5px;
}

input,
select {
  width: 100%;
  margin-bottom: 16px;
  padding: 12px 14px;
  border: 1.5px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.8);
  transition: border 0.2s, box-shadow 0.2s;
  font-family: inherit;
  outline: none;
}

input:focus,
select:focus {
  border: 1.5px solid #fda085;
  box-shadow: 0 2px 8px rgba(253, 160, 133, 0.10);
  background: #fff;
}

.input-group {
  position: relative;
  width: 100%;
  margin-bottom: 16px;
}

.toggle-pw {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #aaa;
  z-index: 2;
}

button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(90deg, #f6d365 0%, #fda085 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(253, 160, 133, 0.15);
  transition: background 0.2s, transform 0.1s;
  margin-top: 8px;
}

button:hover {
  background: linear-gradient(90deg, #fda085 0%, #f6d365 100%);
  transform: translateY(-2px) scale(1.03);
}

.register-links {
  width: 100%;
  text-align: right;
  margin-top: 8px;
}

.register-links a {
  color: #fda085;
  text-decoration: none;
  font-size: 14px;
  transition: text-decoration 0.2s;
}

.register-links a:hover {
  text-decoration: underline;
}

.error {
  color: #e74c3c;
  margin-top: 16px;
  font-size: 15px;
  text-align: center;
}

.success {
  color: #27ae60;
  margin-top: 16px;
  font-size: 15px;
  text-align: center;
}

/* New styles for OTP inputs */
.otp-input-group {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  gap: 8px;
}

.otp-input {
  width: 50px;
  height: 50px;
  text-align: center;
  font-size: 24px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  color: #333;
  font-weight: bold;
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
}

.otp-input:focus {
  border-color: #fda085;
  box-shadow: 0 0 8px rgba(253, 160, 133, 0.3);
}

.otp-input::-webkit-inner-spin-button,
.otp-input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.otp-input[type="text"] {
  -moz-appearance: textfield;
}

.otp-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  margin-top: 20px;
}

.otp-actions button {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.otp-actions button:first-child {
  background: linear-gradient(90deg, #f6d365 0%, #fda085 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(253, 160, 133, 0.15);
}

.otp-actions button:first-child:hover {
  background: linear-gradient(90deg, #fda085 0%, #f6d365 100%);
  transform: translateY(-2px);
}

.resend-btn {
  background: rgba(253, 160, 133, 0.1) !important;
  color: #fda085 !important;
  border: 2px solid #fda085 !important;
}

.resend-btn:hover {
  background: #fda085 !important;
  color: white !important;
}

.resend-btn:disabled {
  background: #e0e0e0 !important;
  color: #999 !important;
  border-color: #e0e0e0 !important;
  cursor: not-allowed;
}

.back-btn {
  background: rgba(255, 255, 255, 0.8);
  color: #666;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid #e0e0e0;
  margin-top: 16px;
  width: 100%;
}

.back-btn:hover {
  background: #f5f5f5;
  border-color: #d0d0d0;
  transform: translateY(-1px);
}

.otp-info {
  text-align: center;
  margin-bottom: 24px;
  padding: 16px;
  background: rgba(253, 160, 133, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(253, 160, 133, 0.2);
}

.otp-info p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

.email-number {
  font-weight: bold;
  color: #fda085;
  font-size: 16px !important;
  margin-top: 8px !important;
}

@media (max-width: 900px) {
  .register-glass {
    flex-direction: column;
    max-width: 98vw;
    border-radius: 32px;
  }

  .register-svg {
    padding: 32px 32px 0 32px;
    min-width: unset;
  }

  .register-form {
    padding: 40px 16px;
    min-width: unset;
  }
}

@media (max-width: 600px) {
  .register-bg {
    padding: 0;
  }

  .register-glass {
    max-width: 100vw;
    border-radius: 0;
    box-shadow: none;
  }

  .register-svg {
    display: none;
  }

  .register-form {
    padding: 32px 8px;
  }
}
</style>