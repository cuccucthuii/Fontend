<template>
  <div class="login-bg">
    <div class="login-glass">
      <div class="login-svg">
        <img src="https://undraw.co/api/illustrations/2e1e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e" alt="Login Illustration" />
      </div>
      <div class="login-form">
        <div class="logo">
          <img src="/vite.svg" alt="Logo" />
        </div>
        <!-- Tab Navigation -->
        <div class="tab-container">
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
        <!-- Login Form -->
        <div v-if="activeTab === 'login'" class="form-container">
          <h2>Đăng nhập</h2>
          <form @submit.prevent="handleLogin">
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M15.75 7.5a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0z"/><path d="M4.5 19.25a7.25 7.25 0 1115 0v.25a.75.75 0 01-.75.75h-13.5a.75.75 0 01-.75-.75v-.25z"/></svg>
              </span>
              <input v-model="loginForm.username" placeholder="Tên đăng nhập hoặc SĐT" required />
            </div>
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><rect width="14" height="10" x="5" y="11" rx="2"/><path d="M12 16v-2"/><path d="M8 11V7a4 4 0 118 0v4"/></svg>
              </span>
              <input :type="showLoginPassword ? 'text' : 'password'" v-model="loginForm.password" placeholder="Mật khẩu" required />
              <span class="toggle-pw" @click="showLoginPassword = !showLoginPassword">
                <svg v-if="showLoginPassword" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33"/><path d="M1 1l22 22"/></svg>
              </span>
            </div>
            <button type="submit">Đăng nhập</button>
          </form>
          <div class="login-links">
            <a href="#">Quên mật khẩu?</a>
          </div>
        </div>
        <!-- Register Form -->
        <div v-if="activeTab === 'register'" class="form-container">
          <h2>Đăng ký</h2>
          <form @submit.prevent="handleRegister">
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M15.75 7.5a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0z"/><path d="M4.5 19.25a7.25 7.25 0 1115 0v.25a.75.75 0 01-.75.75h-13.5a.75.75 0 01-.75-.75v-.25z"/></svg>
              </span>
              <input v-model="registerForm.fullName" placeholder="Họ và tên" required />
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
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M15 12h.01"/><path d="M12 12h.01"/><path d="M9 12h.01"/><path d="M6 12h.01"/><path d="M3 12h.01"/></svg>
              </span>
              <input v-model="registerForm.username" placeholder="Tên đăng nhập (tự đặt, có thể là chữ/số/email)" required />
            </div>
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
              </span>
              <input v-model="registerForm.email" type="email" placeholder="Email" required />
            </div>
            <div class="input-group">
              <span class="input-icon">
                <!-- Icon điện thoại -->
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M22 16.92v3a2 2 0 01-2.18 2A19.72 19.72 0 013 5.18 2 2 0 015 3h3a2 2 0 012 1.72c.13 1.05.37 2.07.72 3.06a2 2 0 01-.45 2.11l-1.27 1.27a16 16 0 006.29 6.29l1.27-1.27a2 2 0 012.11-.45c.99.35 2.01.59 3.06.72A2 2 0 0121 16.92z"></path></svg>
              </span>
              <input v-model="registerForm.phone" type="tel" placeholder="Số điện thoại" required />
            </div>
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><rect width="14" height="10" x="5" y="11" rx="2"/><path d="M12 16v-2"/><path d="M8 11V7a4 4 0 118 0v4"/></svg>
              </span>
              <input :type="showRegisterPassword ? 'text' : 'password'" v-model="registerForm.password" placeholder="Mật khẩu" required />
              <span class="toggle-pw" @click="showRegisterPassword = !showRegisterPassword">
                <svg v-if="showRegisterPassword" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33"/><path d="M1 1l22 22"/></svg>
              </span>
            </div>
            <div class="input-group">
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><rect width="14" height="10" x="5" y="11" rx="2"/><path d="M12 16v-2"/><path d="M8 11V7a4 4 0 118 0v4"/></svg>
              </span>
              <input :type="showConfirmPassword ? 'text' : 'password'" v-model="registerForm.confirmPassword" placeholder="Xác nhận mật khẩu" required />
              <span class="toggle-pw" @click="showConfirmPassword = !showConfirmPassword">
                <svg v-if="showConfirmPassword" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33"/><path d="M1 1l22 22"/></svg>
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
              <span class="input-icon">
                <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
              </span>
              <!-- Nếu có biến employees (danh sách nhân viên), hiển thị dropdown, nếu không thì input số -->
              <template v-if="employees && employees.length">
                <select v-model="registerForm.idNhanVien" required>
                  <option value="" disabled>Chọn nhân viên</option>
                  <option v-for="emp in employees" :key="emp.id" :value="emp.id">{{ emp.name }}</option>
                </select>
              </template>
              <template v-else>
                <input v-model="registerForm.idNhanVien" type="number" placeholder="ID Nhân viên (hỏi quản lý để biết)" required />
              </template>
            </div>
            <button type="submit">Đăng ký</button>
          </form>
        </div>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// Tab state
const activeTab = ref('login')

// Login form
const loginForm = ref({
  username: '',
  password: ''
})

// Register form
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

// Danh sách nhân viên chưa có tài khoản (nếu có API)
const employees = ref([])
// Nếu có API, gọi lấy danh sách nhân viên ở đây
// onMounted(async () => {
//   const res = await axios.get('API_LAY_DANH_SACH_NHAN_VIEN_CHUA_CO_TAI_KHOAN')
//   employees.value = res.data
// })

// UI states
const errorMessage = ref('')
const successMessage = ref('')
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)

const router = useRouter()

async function handleLogin() {
    errorMessage.value = ''
    successMessage.value = ''
    if (!loginForm.value.username || !loginForm.value.password) {
        errorMessage.value = '❌ Vui lòng nhập đầy đủ thông tin!'
        return
    }
    try {
        // Gọi API đăng nhập như cũ
        const response = await axios.post('http://localhost:8080/api/auth/login', {
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
  // Xác định idVaiTro
  let idVaiTro = 3
  if (registerForm.value.role === 'employee') idVaiTro = 2
  if (registerForm.value.role === 'manager') idVaiTro = 1
  // Nếu là nhân viên thì phải có idNhanVien
  if (registerForm.value.role === 'employee' && !registerForm.value.idNhanVien) {
    errorMessage.value = '❌ Vui lòng chọn hoặc nhập ID nhân viên!'
    return
  }
  try {
    let payload = {
      tenDangNhap: registerForm.value.username,
      matKhau: registerForm.value.password,
      email: registerForm.value.email,
      soDienThoai: registerForm.value.phone,
      tenKhachHang: registerForm.value.fullName,
      idVaiTro: idVaiTro
    }
    if (registerForm.value.role === 'customer') {
      payload.gioiTinh = registerForm.value.gender
    }
    if (registerForm.value.role === 'employee') {
      payload.idNhanVien = registerForm.value.idNhanVien
      await axios.post('http://localhost:8080/api/auth/register/employee', payload)
    } else {
      await axios.post('http://localhost:8080/api/auth/register/customer', payload)
    }
    successMessage.value = '✅ Đăng ký thành công! Vui lòng đăng nhập.'
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
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');

.login-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #f6d365 0%, #fda085 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', sans-serif;
}

.login-glass {
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

.login-svg {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  min-width: 320px;
  padding: 32px 0 32px 32px;
}

.login-svg img {
  width: 100%;
  max-width: 340px;
  height: auto;
}

.login-form {
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

.input-group {
  position: relative;
  width: 100%;
  margin-bottom: 18px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  z-index: 2;
}

input {
  width: 100%;
  padding: 12px 14px 12px 42px;
  border: 1.5px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.8);
  transition: border 0.2s, box-shadow 0.2s;
  font-family: inherit;
  outline: none;
}

input:focus {
  border: 1.5px solid #fda085;
  box-shadow: 0 2px 8px rgba(253, 160, 133, 0.10);
  background: #fff;
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

.login-links {
  width: 100%;
  text-align: right;
  margin-top: 8px;
}

.login-links a {
  color: #fda085;
  text-decoration: none;
  font-size: 14px;
  transition: text-decoration 0.2s;
}

.login-links a:hover {
  text-decoration: underline;
}

/* Tab Navigation */
.tab-container {
  display: flex;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 4px;
  gap: 4px;
}

.tab-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn.active {
  background: rgba(255, 255, 255, 0.9);
  color: #fda085;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-btn:hover:not(.active) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

/* Form Container */
.form-container {
  width: 100%;
}

/* Success Message */
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

.gender-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.gender-group label {
  margin-bottom: 0;
  min-width: 70px;
  font-size: 15px;
  color: #555;
}
.gender-group select {
  flex: 1;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 15px;
}

@media (max-width: 900px) {
  .login-glass {
    flex-direction: column;
    max-width: 98vw;
    border-radius: 32px;
  }

  .login-svg {
    padding: 32px 32px 0 32px;
    min-width: unset;
  }

  .login-form {
    padding: 40px 16px;
    min-width: unset;
  }
}

@media (max-width: 600px) {
  .login-bg {
    padding: 0;
  }

  .login-glass {
    max-width: 100vw;
    border-radius: 0;
    box-shadow: none;
  }

  .login-svg {
    display: none;
  }

  .login-form {
    padding: 32px 8px;
  }
}
</style>
