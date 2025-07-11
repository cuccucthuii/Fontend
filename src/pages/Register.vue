<template>
  <div class="register-bg">
    <div class="register-glass">
      <div class="register-svg">
        <img src="/src/assets/register-illustration.svg" alt="Register Illustration" />
      </div>
      <div class="register-form">
        <div class="logo">
          <img src="/vite.svg" alt="Logo" />
        </div>
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
              <svg v-if="showPassword" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>
              <svg v-else width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"><path d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.81 21.81 0 014.22-5.94M9.53 9.53A3.5 3.5 0 0112 8.5c1.93 0 3.5 1.57 3.5 3.5 0 .47-.09.92-.26 1.33"/><path d="M1 1l22 22"/></svg>
            </span>
          </div>
          <input :type="showPassword ? 'text' : 'password'" v-model="confirmPassword" placeholder="Xác nhận mật khẩu" required />
          <button type="submit">Đăng ký</button>
        </form>
        <div class="register-links">
          <router-link to="/login">Đã có tài khoản? Đăng nhập</router-link>
        </div>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerUser } from '../services/userService'

const fullName = ref('')
const phone = ref('')
const email = ref('')
const role = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const showPassword = ref(false)
const router = useRouter()

async function handleRegister() {
  errorMessage.value = ''
  successMessage.value = ''
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp.'
    return
  }
  try {
    const res = await registerUser({
      fullName: fullName.value,
      phone: phone.value,
      email: email.value,
      role: role.value,
      password: password.value
    })
    if (res.success || res.status === 'success') {
      successMessage.value = 'Đăng ký thành công! Vui lòng đăng nhập.'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      errorMessage.value = res.message || 'Đăng ký thất bại!'
    }
  } catch (err) {
    errorMessage.value = 'Đăng ký thất bại!'
  }
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
  background: rgba(255,255,255,0.25);
  box-shadow: 0 8px 32px 0 rgba(31,38,135,0.18);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 40px;
  border: 1.5px solid rgba(255,255,255,0.18);
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
.register-svg img {
  width: 100%;
  max-width: 340px;
  height: auto;
}
.register-form {
  flex: 1;
  background: rgba(255,255,255,0.7);
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
input, select {
  width: 100%;
  margin-bottom: 16px;
  padding: 12px 14px;
  border: 1.5px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  background: rgba(255,255,255,0.8);
  transition: border 0.2s, box-shadow 0.2s;
  font-family: inherit;
  outline: none;
}
input:focus, select:focus {
  border: 1.5px solid #fda085;
  box-shadow: 0 2px 8px rgba(253,160,133,0.10);
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
  box-shadow: 0 2px 8px rgba(253,160,133,0.15);
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