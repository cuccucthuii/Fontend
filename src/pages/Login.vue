<template>
  <div class="login-bg">
    <div class="login-bg-image"></div>
    <div class="login-bg-overlay"></div>
    <div class="login-marquee">
      <div class="marquee-content">
        Chào mừng đến với DEV CINEMA - Đặt vé siêu tốc, trải nghiệm điện ảnh đỉnh cao! &nbsp;|&nbsp; Ưu đãi hấp dẫn mỗi
        ngày! &nbsp;|&nbsp; Phim mới cập nhật liên tục!
      </div>
    </div>
    <div class="login-card">
      <div class="login-left">
        <div class="login-animated-svg">
          <!-- SVG animation: cuộn phim quay -->
          <svg width="180" height="180" viewBox="0 0 180 180" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="90" cy="90" r="80" stroke="#feca57" stroke-width="8" fill="#232526" />
            <circle cx="90" cy="90" r="60" stroke="#48dbfb" stroke-width="4" fill="#18191a" />
            <g>
              <circle cx="90" cy="90" r="40" fill="#fff" stroke="#bfa2db" stroke-width="2" />
              <circle cx="90" cy="90" r="10" fill="#feca57" />
              <g>
                <circle class="film-dot" cx="90" cy="50" r="6" fill="#48dbfb" />
                <circle class="film-dot" cx="130" cy="90" r="6" fill="#48dbfb" />
                <circle class="film-dot" cx="90" cy="130" r="6" fill="#48dbfb" />
                <circle class="film-dot" cx="50" cy="90" r="6" fill="#48dbfb" />
              </g>
            </g>
            <animateTransform attributeName="transform" attributeType="XML" type="rotate" from="0 90 90" to="360 90 90"
              dur="3s" repeatCount="indefinite" />
          </svg>
        </div>
      </div>
      <div class="login-right">
        <img src="/logo.png" alt="DEV CINEMA" class="login-logo" />
        <div class="login-tabs">
          <button class="login-tab" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">Đăng
            nhập</button>
          <button class="login-tab" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">Đăng
            ký</button>
        </div>
        <div class="login-title">{{ activeTab === 'login' ? 'Đăng nhập' : 'Đăng ký' }}</div>
        
        <!-- Debug info -->
        <div v-if="activeTab === 'register'" style="background: #f0f0f0; padding: 8px; margin-bottom: 10px; font-size: 12px; border-radius: 4px;">
          Debug: showOTP = {{ showOTP }}, isSubmitting = {{ isSubmitting }}
        </div>
        <form class="login-form" v-if="activeTab === 'login'" @submit.prevent="handleLogin">
          <input class="login-input" type="text" placeholder="Email hoặc tên đăng nhập" v-model="loginForm.username" />
          <div class="pw-group">
            <input class="login-input" :type="showLoginPassword ? 'text' : 'password'" placeholder="Mật khẩu"
              v-model="loginForm.password" />
            <span class="pw-toggle" @click="showLoginPassword = !showLoginPassword">
              <svg v-if="showLoginPassword" width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2"
                viewBox="0 0 24 24">
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
              <svg v-else width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2" viewBox="0 0 24 24">
                <path
                  d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33" />
                <path d="M1 1l22 22" />
              </svg>
            </span>
          </div>
          <button class="login-btn" type="submit">Đăng nhập</button>
        </form>
        <form class="login-form" v-else @submit.prevent="handleRegister">
          <div class="input-group">
            <label>Họ và tên</label>
            <input class="login-input" type="text" placeholder="Nhập họ và tên" v-model="registerForm.fullName"
              required />
          </div>
          <div class="input-group gender-group">
            <label>Giới tính</label>
            <select v-model="registerForm.gender" required>
              <option value="">Chọn giới tính</option>
              <option value="Nam">Nam</option>
              <option value="Nữ">Nữ</option>
              <option value="Khác">Khác</option>
            </select>
          </div>
          <div class="input-group">
            <label>Tên đăng nhập</label>
            <input class="login-input" type="text" placeholder="Tự đặt, có thể là chữ/số/email"
              v-model="registerForm.username" required />
          </div>
          <div class="input-group">
            <label>Email</label>
            <input class="login-input" type="email" placeholder="Nhập email" v-model="registerForm.email" required />
          </div>
          <div class="input-group">
            <label>Số điện thoại</label>
            <input class="login-input" type="tel" placeholder="Nhập số điện thoại" v-model="registerForm.phone"
              required />
          </div>
          <div class="input-group pw-group">
            <label>Mật khẩu</label>
            <input class="login-input" :type="showRegisterPassword ? 'text' : 'password'" placeholder="Nhập mật khẩu"
              v-model="registerForm.password" required />
            <span class="pw-toggle" @click="showRegisterPassword = !showRegisterPassword">
              <svg v-if="showRegisterPassword" width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2"
                viewBox="0 0 24 24">
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
              <svg v-else width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2" viewBox="0 0 24 24">
                <path
                  d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33" />
                <path d="M1 1l22 22" />
              </svg>
            </span>
          </div>
          <div class="input-group pw-group">
            <label>Xác nhận mật khẩu</label>
            <input class="login-input" :type="showConfirmPassword ? 'text' : 'password'" placeholder="Nhập lại mật khẩu"
              v-model="registerForm.confirmPassword" required />
            <span class="pw-toggle" @click="showConfirmPassword = !showConfirmPassword">
              <svg v-if="showConfirmPassword" width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2"
                viewBox="0 0 24 24">
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
              <svg v-else width="22" height="22" fill="none" stroke="#BFA2DB" stroke-width="2" viewBox="0 0 24 24">
                <path
                  d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33" />
                <path d="M1 1l22 22" />
              </svg>
            </span>
          </div>
          <div class="input-group">
            <label for="role-select">Vai trò</label>
            <select id="role-select" v-model="registerForm.role">
              <option value="customer">Khách hàng</option>
              <option value="employee">Nhân viên</option>
              <option value="manager">Quản lý</option>
            </select>
          </div>
          <div v-if="registerForm.role === 'employee'" class="input-group">
            <label>ID Nhân viên</label>
            <input v-model="registerForm.idNhanVien" type="number" placeholder="ID Nhân viên (hỏi quản lý để biết)"
              required />
          </div>
          <button class="login-btn" type="submit" :disabled="isSubmitting">
            <span v-if="isSubmitting">Đang gửi OTP...</span>
            <span v-else>Đăng ký</span>
          </button>
          <button type="button" @click="testShowOTP" class="test-btn">
            Test Hiện OTP
          </button>
          <button type="button" @click="testSendOTP" class="test-btn" style="background: #4ecdc4;">
            Test Gửi OTP API
          </button>
        </form>

        <!-- Màn hình OTP -->
        <div v-if="showOTP" class="otp-screen">
          <h3>Xác thực OTP</h3>
          <div class="otp-info">
            <p>Mã OTP đã được gửi đến email:</p>
            <p class="email-display">{{ registerForm.email }}</p>
          </div>
          <form @submit.prevent="verifyOTP" class="otp-form">
            <div class="otp-input-group">
              <input v-for="(digit, index) in 6" :key="index" v-model="otpDigits[index]" type="text" maxlength="1"
                class="otp-input" :data-index="index" @input="handleOTPInput($event, index)"
                @keydown="handleOTPKeydown($event, index)" />
            </div>
            <div class="otp-actions">
              <button type="submit" class="login-btn" :disabled="isVerifying">
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
        <a href="#" class="login-forgot" v-if="activeTab === 'login'">Quên mật khẩu?</a>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
const activeTab = ref('login')
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({
  fullName: '',
  gender: '',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  role: 'customer',
  idNhanVien: ''
})
const errorMessage = ref('')
const successMessage = ref('')

// OTP states
const showOTP = ref(false)
const isSubmitting = ref(false)
const isVerifying = ref(false)
const otpDigits = ref(['', '', '', '', '', ''])
const resendCountdown = ref(0)

const router = useRouter()

// OTP functions
function handleOTPInput(event, index) {
  const value = event.target.value
  if (value && /^\d$/.test(value)) {
    otpDigits.value[index] = value
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

function handleOTPKeydown(event, index) {
  if (event.key === 'Backspace' && !otpDigits.value[index] && index > 0) {
    otpDigits.value[index - 1] = ''
    nextTick(() => {
      const prevInput = document.querySelector(`input[data-index="${index - 1}"]`)
      if (prevInput) prevInput.focus()
    })
  }
}

function getOTPString() {
  return otpDigits.value.join('')
}

async function sendOTP() {
  try {
    const response = await api.post('/api/auth/send-otp', {
      phone: registerForm.value.phone,
      email: registerForm.value.email
    })
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

async function verifyOTPAPI(otp) {
  try {
    const response = await api.post('/api/auth/verify-otp', {
      phone: registerForm.value.phone,
      email: registerForm.value.email,
      otp: otp
    })
    return response.data
  } catch (error) {
    throw error
  }
}

function startResendCountdown() {
  resendCountdown.value = 60
  const timer = setInterval(() => {
    resendCountdown.value--
    if (resendCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function resendOTP() {
  try {
    await sendOTP()
    startResendCountdown()
    successMessage.value = 'Đã gửi lại mã OTP!'
  } catch (error) {
    errorMessage.value = 'Không thể gửi lại OTP. Vui lòng thử lại.'
  }
}

async function verifyOTP() {
  const otp = getOTPString()
  if (otp.length !== 6) {
    errorMessage.value = 'Vui lòng nhập đầy đủ 6 số OTP.'
    return
  }

  isVerifying.value = true
  errorMessage.value = ''

  try {
    const otpResult = await verifyOTPAPI(otp)
    
    if (otpResult.success) {
      // Nếu OTP đúng, đăng ký user
      await handleRegisterAfterOTP()
    } else {
      errorMessage.value = 'Mã OTP không đúng. Vui lòng thử lại.'
    }
  } catch (error) {
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = 'Xác thực OTP thất bại. Vui lòng thử lại.'
    }
  } finally {
    isVerifying.value = false
  }
}

async function handleRegisterAfterOTP() {
  try {
    let payload = {
      tenDangNhap: registerForm.value.username,
      matKhau: registerForm.value.password,
      email: registerForm.value.email,
      soDienThoai: registerForm.value.phone,
      tenKhachHang: registerForm.value.fullName,
      idVaiTro: registerForm.value.role === 'employee' ? 2 : registerForm.value.role === 'manager' ? 1 : 3
    }
    
    if (registerForm.value.role === 'customer') {
      payload.gioiTinh = registerForm.value.gender
    }
    if (registerForm.value.role === 'employee') {
      payload.idNhanVien = registerForm.value.idNhanVien
      await api.post('/api/auth/register-employee', payload)
    } else {
      await api.post('/api/auth/register/customer', payload)
    }
    
    successMessage.value = '✅ Đăng ký thành công! Vui lòng đăng nhập.'
    showOTP.value = false
    registerForm.value = {
      fullName: '',
      gender: '',
      username: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
      role: 'customer',
      idNhanVien: ''
    }
    setTimeout(() => {
      activeTab.value = 'login'
      successMessage.value = ''
    }, 1500)
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data.message || '❌ Đăng ký thất bại!'
    } else {
      errorMessage.value = '❌ Không thể kết nối đến server!'
    }
  }
}

function testShowOTP() {
  console.log('Test: Force hiển thị màn hình OTP')
  showOTP.value = true
  startResendCountdown()
}

async function testSendOTP() {
  console.log('Test: Gửi OTP API với email:', registerForm.value.email)
  try {
    await sendOTP()
    console.log('Test: Gửi OTP thành công!')
    showOTP.value = true
    startResendCountdown()
  } catch (error) {
    console.error('Test: Gửi OTP thất bại!')
  }
}

function backToRegister() {
  showOTP.value = false
  otpDigits.value = ['', '', '', '', '', '']
  errorMessage.value = ''
  successMessage.value = ''
}

async function handleLogin() {
  errorMessage.value = ''
  successMessage.value = ''
  if (!loginForm.value.username || !loginForm.value.password) {
    errorMessage.value = '❌ Vui lòng nhập đầy đủ thông tin!'
    return
  }
  try {
    // Gọi API đăng nhập qua proxy
    const response = await api.post('/api/auth/login', {
      tenDangNhap: loginForm.value.username,
      matKhau: loginForm.value.password
    })
    if (response.data) {
      const data = response.data
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify({
        username: loginForm.value.username,
        role: data.role || 'user',
        token: data.token || null
      }))
      alert('✅ Đăng nhập thành công!')
      const role = (data.role || 'user').toLowerCase()
      if (
        role === 'admin' ||
        role === 'administrator' ||
        role === 'quản trị viên' ||
        role === 'quản lý' ||
        role === 'quan ly'
      ) {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    }
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data.message || '❌ Đăng nhập thất bại!'
    } else {
      errorMessage.value = '❌ Không thể kết nối đến server!'
    }
  }
}
async function handleRegister() {
  errorMessage.value = ''
  successMessage.value = ''
  
  // Validate
  if (!registerForm.value.fullName || !registerForm.value.gender || !registerForm.value.username || !registerForm.value.email || !registerForm.value.phone || !registerForm.value.password || !registerForm.value.confirmPassword) {
    errorMessage.value = '❌ Vui lòng nhập đầy đủ thông tin!'
    return
  }
  if (registerForm.value.password.length < 6) {
    errorMessage.value = '❌ Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '❌ Mật khẩu xác nhận không khớp!'
    return
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.value.email)) {
    errorMessage.value = '❌ Email không hợp lệ!'
    return
  }
  const phoneRegex = /^0[0-9]{9}$/
  if (!phoneRegex.test(registerForm.value.phone)) {
    errorMessage.value = '❌ Số điện thoại không hợp lệ!'
    return
  }
  
  // Nếu là nhân viên thì phải có idNhanVien
  if (registerForm.value.role === 'employee' && !registerForm.value.idNhanVien) {
    errorMessage.value = '❌ Vui lòng chọn hoặc nhập ID nhân viên!'
    return
  }

  isSubmitting.value = true
  try {
    // Gửi OTP trước
    await sendOTP()
    console.log('Gửi OTP thành công, chuyển sang màn hình OTP')
    
    // Chuyển sang màn hình OTP
    showOTP.value = true
    startResendCountdown()
    
  } catch (error) {
    console.error('Lỗi khi gửi OTP:', error)
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = 'Không thể gửi OTP. Vui lòng thử lại.'
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  width: 100vw;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-bg-image {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  background: url('https://kenh14cdn.com/203336854389633024/2021/10/20/photo-1-16347021907881058551053.jpg') center center/cover no-repeat;
  z-index: 0;
  filter: brightness(0.55) blur(1.5px);
}

.login-bg-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #18191a99 0%, #232526cc 100%);
  z-index: 1;
}

.login-marquee {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  z-index: 2;
  background: rgba(24, 25, 26, 0.85);
  border-bottom: 1.5px solid #48dbfb44;
  height: 44px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.marquee-content {
  display: inline-block;
  white-space: nowrap;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 20px;
  font-weight: 900;
  letter-spacing: 1.5px;
  padding-left: 100vw;
  animation: marquee 18s linear infinite;
  background: linear-gradient(90deg, #feca57 0%, #F3D9CD 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 12px #B8C8FA, 0 1px 2px #000a;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }

  100% {
    transform: translateX(-100vw);
  }
}

.login-card {
  z-index: 3;
  background: #fff;
  border-radius: 28px;
  box-shadow: 0 8px 40px rgba(191, 162, 219, 0.13);
  display: flex;
  flex-direction: row;
  max-width: 900px;
  width: 100%;
  min-height: 520px;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #BFA2DB22 0%, #B7E4C722 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-right {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.login-logo {
  height: 56px;
  width: auto;
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 12px;
}

.login-title {
  font-size: 2rem;
  font-weight: 900;
  color: #BFA2DB;
  margin-bottom: 18px;
  text-align: center;
}

.login-form {
  width: 100%;
  max-width: 340px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.login-input {
  border-radius: 12px;
  border: 1.5px solid #BFA2DB;
  padding: 12px 16px;
  font-size: 16px;
  transition: border 0.2s;
}

.login-input:focus {
  border-color: #B7E4C7;
  outline: none;
}

.login-btn {
  background: linear-gradient(135deg, #BFA2DB 0%, #B7E4C7 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-weight: 800;
  font-size: 17px;
  padding: 14px 0;
  margin-top: 8px;
  box-shadow: 0 2px 8px rgba(191, 162, 219, 0.10);
  transition: background 0.2s, transform 0.2s;
}

.login-btn:hover {
  background: linear-gradient(135deg, #B7E4C7 0%, #BFA2DB 100%);
  transform: scale(1.04);
}

.login-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  justify-content: center;
}

.login-tab {
  background: #fff;
  color: #BFA2DB;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 16px;
  padding: 10px 28px;
  box-shadow: 0 2px 8px #BFA2DB22;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.login-tab.active {
  background: linear-gradient(135deg, #BFA2DB 0%, #B7E4C7 100%);
  color: #fff;
}

.login-forgot {
  color: #8F6BAE;
  text-align: right;
  margin-top: 8px;
  font-size: 15px;
  text-decoration: none;
  transition: color 0.2s;
}

.login-forgot:hover {
  color: #BFA2DB;
}

/* OTP Screen Styles */
.otp-screen {
  margin-top: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(191, 162, 219, 0.3);
}

.otp-screen h3 {
  color: #BFA2DB;
  text-align: center;
  margin-bottom: 16px;
  font-size: 18px;
}

.otp-info {
  text-align: center;
  margin-bottom: 20px;
  padding: 12px;
  background: rgba(191, 162, 219, 0.1);
  border-radius: 8px;
}

.otp-info p {
  margin: 4px 0;
  font-size: 14px;
  color: #8F6BAE;
}

.email-display {
  font-weight: bold;
  color: #BFA2DB !important;
  font-size: 16px !important;
  margin-top: 8px !important;
}

.otp-input-group {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  gap: 8px;
}

.otp-input {
  width: 45px;
  height: 45px;
  text-align: center;
  font-size: 20px;
  border: 2px solid #BFA2DB;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  color: #6C3483;
  font-weight: bold;
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
}

.otp-input:focus {
  border-color: #B7E4C7;
  box-shadow: 0 0 8px rgba(183, 228, 199, 0.3);
}

.otp-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.resend-btn {
  background: rgba(191, 162, 219, 0.1) !important;
  color: #BFA2DB !important;
  border: 2px solid #BFA2DB !important;
  font-size: 14px;
  padding: 8px 16px;
}

.resend-btn:hover:not(:disabled) {
  background: #BFA2DB !important;
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
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid #e0e0e0;
  width: 100%;
}

.back-btn:hover {
  background: #f5f5f5;
  border-color: #d0d0d0;
}

.test-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  margin-top: 10px;
  width: 100%;
  transition: background 0.2s;
}

.test-btn:hover {
  background: #ff5252;
}

.pw-group {
  position: relative;
  width: 100%;
}

.pw-toggle {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #BFA2DB;
  z-index: 2;
  display: flex;
  align-items: center;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 14px;
}

.input-group label {
  font-size: 15px;
  color: #BFA2DB;
  font-weight: 700;
  margin-bottom: 2px;
  letter-spacing: 0.2px;
}

.input-group select {
  border-radius: 10px;
  border: 1.5px solid #BFA2DB;
  padding: 10px 14px;
  font-size: 15px;
  background: #ede6f6;
  color: #8F6BAE;
  font-weight: 700;
  transition: border 0.2s, box-shadow 0.2s, background 0.2s;
}

.input-group select:focus {
  border-color: #B7E4C7;
  background: #f3eaff;
  box-shadow: 0 0 0 2px #B7E4C733;
  outline: none;
}

.input-group input {
  border-radius: 10px;
  border: 1.5px solid #BFA2DB;
  padding: 10px 14px;
  font-size: 15px;
  background: #f8f9fa;
  color: #6C3483;
  font-weight: 700;
  transition: border 0.2s, box-shadow 0.2s;
}

.input-group input:focus {
  border-color: #B7E4C7;
  box-shadow: 0 0 0 2px #B7E4C733;
  outline: none;
}

.gender-group label {
  font-size: 15px;
  color: #555;
  margin-bottom: 0;
}

.gender-group select {
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 8px 12px;
  font-size: 15px;
}

.input-icon {
  display: inline-flex;
  align-items: center;
  margin-right: 6px;
  color: #BFA2DB;
}

.error {
  color: #e74c3c;
  margin-top: 16px;
  font-size: 15px;
  text-align: center;
  background: rgba(231, 76, 60, 0.1);
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(231, 76, 60, 0.2);
}

.success {
  color: #27ae60;
  margin-top: 16px;
  font-size: 15px;
  text-align: center;
  background: rgba(39, 174, 96, 0.1);
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(39, 174, 96, 0.2);
}

.login-animated-svg {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 220px;
  min-width: 180px;
  background: none;
  animation: fadeIn 1.2s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(40px);
  }

  to {
    opacity: 1;
    transform: none;
  }
}

@media (max-width: 900px) {
  .login-card {
    flex-direction: column;
  }

  .login-left,
  .login-right {
    flex: unset;
    width: 100%;
  }

  .login-right {
    padding: 32px 12px;
  }
}
</style>
