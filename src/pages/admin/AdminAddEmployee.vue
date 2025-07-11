<template>
  <div class="add-employee-container">
    <h2>Thêm nhân viên mới</h2>
    <form class="add-employee-form" @submit.prevent="handleAddEmployee">
      <div class="input-group">
        <label>Tên đăng nhập</label>
        <input v-model="form.username" placeholder="Tên đăng nhập" required />
      </div>
      <div class="input-group">
        <label>Mật khẩu</label>
        <input v-model="form.password" type="password" placeholder="Mật khẩu" required />
      </div>
      <div class="input-group">
        <label>Xác nhận mật khẩu</label>
        <input v-model="form.confirmPassword" type="password" placeholder="Xác nhận mật khẩu" required />
      </div>
      <button type="submit">Thêm nhân viên</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
})
const errorMessage = ref('')
const successMessage = ref('')

async function handleAddEmployee() {
  errorMessage.value = ''
  successMessage.value = ''
  if (!form.value.username || !form.value.password || !form.value.confirmPassword) {
    errorMessage.value = '❌ Vui lòng nhập đầy đủ thông tin!'
    return
  }
  if (form.value.password.length < 6) {
    errorMessage.value = '❌ Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    errorMessage.value = '❌ Mật khẩu xác nhận không khớp!'
    return
  }
  try {
    // Lấy token admin từ localStorage
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const token = userInfo.token
    const response = await axios.post('http://localhost:8080/api/auth/register-employee', {
      tenDangNhap: form.value.username,
      matKhau: form.value.password,
      idVaiTro: 2,
      idNhanVien: null
    }, {
      headers: {
        Authorization: token ? `Bearer ${token}` : undefined
      }
    })
    if (response.data) {
      successMessage.value = response.data.message || '✅ Thêm nhân viên thành công!'
      form.value = {
        username: '',
        password: '',
        confirmPassword: ''
      }
    } else {
      errorMessage.value = '❌ Thêm nhân viên thất bại! Vui lòng thử lại.'
    }
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data.message || '❌ Thêm nhân viên thất bại!'
    } else {
      errorMessage.value = '❌ Không thể kết nối đến server!'
    }
  }
}
</script>

<style scoped>
.add-employee-container {
  max-width: 400px;
  margin: 40px auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 32px 24px;
}
.add-employee-container h2 {
  text-align: center;
  margin-bottom: 24px;
}
.add-employee-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.input-group label {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 2px;
}
.input-group input {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-size: 15px;
}
button[type="submit"] {
  margin-top: 12px;
  padding: 10px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
button[type="submit"]:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
.error {
  color: #e74c3c;
  margin-top: 16px;
  text-align: center;
}
.success {
  color: #27ae60;
  margin-top: 16px;
  text-align: center;
}
</style> 