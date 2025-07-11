<template>
  <nav class="sidebar-menu">
    <div class="sidebar-header">
      <h2 class="logo">🎬 Cinema Admin</h2>
      <p class="subtitle">Quản lý hệ thống</p>
    </div>

    <ul class="menu-list">
      <li class="menu-item">
        <router-link :to="{ name: 'Dashboard' }" exact-active-class="active" class="menu-link">
          <span class="icon">📊</span>
          <span class="text">Thống Kê Doanh Thu</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Account' }" active-class="active" class="menu-link">
          <span class="icon">👤</span>
          <span class="text">Quản lý tài khoản</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Movies' }" active-class="active" class="menu-link">
          <span class="icon">🎬</span>
          <span class="text">Quản lí Phim</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Rooms' }" active-class="active" class="menu-link">
          <span class="icon">🏢</span>
          <span class="text">Quản lí phòng</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Branches' }" active-class="active" class="menu-link">
          <span class="icon">📍</span>
          <span class="text">Quản lí chi nhánh</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Seats' }" active-class="active" class="menu-link">
          <span class="icon">💺</span>
          <span class="text">Quản lí ghế ngồi</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Schedule' }" active-class="active" class="menu-link">
          <span class="icon">🗓️</span>
          <span class="text">Quản lí lịch chiếu</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Bills' }" active-class="active" class="menu-link">
          <span class="icon">🎟️</span>
          <span class="text">Quản lí vé phim</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'Invoices' }" active-class="active" class="menu-link">
          <span class="icon">🧾</span>
          <span class="text">Quản lí hóa đơn</span>
        </router-link>
      </li>
      <li class="menu-item">
        <router-link :to="{ name: 'AddEmployee' }" active-class="active" class="menu-link">
          <span class="icon">➕</span>
          <span class="text">Thêm nhân viên</span>
        </router-link>
      </li>
    </ul>

    <div class="logout-section">
      <div class="divider"></div>
      <button class="logout-btn" @click="signOut">
        <span class="icon">🚪</span>
        <span class="text">Đăng xuất</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { logoutUser } from '../services/userService'

const router = useRouter()

async function signOut() {
  try {
    // Gọi API logout nếu backend có endpoint này
    await logoutUser()
  } catch (error) {
    console.log('Logout error:', error)
  } finally {
    // Xóa thông tin user khỏi localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
    
    // Chuyển về trang login
    router.push('/login')
  }
}
</script>

<style scoped>
.sidebar-menu {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.sidebar-menu::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

/* Header */
.sidebar-header {
  padding: 30px 25px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1;
}

.logo {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(45deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
  font-weight: 300;
}

/* Menu List */
.menu-list {
  list-style: none;
  padding: 20px 15px;
  margin: 0;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  z-index: 1;
}

.menu-item {
  position: relative;
  overflow: hidden;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.menu-link:hover::before {
  left: 100%;
}

.menu-link:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.menu-link.active {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.1));
  color: white;
  font-weight: 600;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.menu-link.active::after {
  content: '';
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.icon {
  font-size: 20px;
  margin-right: 15px;
  width: 24px;
  text-align: center;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.text {
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

/* Logout Section */
.logout-section {
  padding: 20px 15px;
  position: relative;
  z-index: 1;
}

.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  margin-bottom: 20px;
}

.logout-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 16px 20px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.logout-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.logout-btn:hover::before {
  left: 100%;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.4);
  background: linear-gradient(135deg, #ff5252, #d32f2f);
}

.logout-btn:active {
  transform: translateY(0);
}

/* Responsive Design */
@media (max-width: 768px) {
  .sidebar-menu {
    width: 100%;
    height: auto;
  }

  .menu-list {
    flex-direction: row;
    overflow-x: auto;
    padding: 15px;
    gap: 10px;
  }

  .menu-item {
    min-width: 200px;
  }

  .logout-section {
    padding: 15px;
  }
}

/* Scrollbar Styling */
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* Animation for menu items */
.menu-item {
  animation: slideInLeft 0.5s ease-out;
  animation-fill-mode: both;
}

.menu-item:nth-child(1) {
  animation-delay: 0.1s;
}

.menu-item:nth-child(2) {
  animation-delay: 0.2s;
}

.menu-item:nth-child(3) {
  animation-delay: 0.3s;
}

.menu-item:nth-child(4) {
  animation-delay: 0.4s;
}

.menu-item:nth-child(5) {
  animation-delay: 0.5s;
}

.menu-item:nth-child(6) {
  animation-delay: 0.6s;
}

.menu-item:nth-child(7) {
  animation-delay: 0.7s;
}

.menu-item:nth-child(8) {
  animation-delay: 0.8s;
}

.menu-item:nth-child(9) {
  animation-delay: 0.9s;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
