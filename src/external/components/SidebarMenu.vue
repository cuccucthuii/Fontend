<template>
  <nav class="sidebar-menu">
    <div class="sidebar-header">
      <img src="/logo.png?v=2" alt="DEV CINEMA" class="sidebar-img-logo" />
      <p class="subtitle">Quản lý hệ thống</p>
    </div>

    <ul class="menu-list">
      <li v-if="isAdmin" class="menu-item">
        <router-link :to="{ name: 'Dashboard' }" exact-active-class="active" class="menu-link">
          <span class="icon">📊</span>
          <span class="text">Thống Kê Doanh Thu</span>
        </router-link>
      </li>
      <li v-if="isAdmin" class="menu-item">
        <router-link :to="{ name: 'Account' }" active-class="active" class="menu-link">
          <span class="icon">👤</span>
          <span class="text">Quản lý tài khoản</span>
        </router-link>
      </li>
      <li class="menu-item" :class="{ expanded: showMoviesMenu }">
        <div class="menu-link" @click="toggleMoviesMenu">
          <span class="icon">🎬</span>
          <span class="text">Quản lý phim</span>
          <span class="arrow" :class="{ open: showMoviesMenu }">▼</span>
        </div>
        <ul v-if="showMoviesMenu" class="submenu">
          <li>
            <router-link to="/admin/movies" active-class="active">Danh sách phim</router-link>
          </li>
          <li>
            <router-link to="/admin/genres" active-class="active">Thể loại</router-link>
          </li>
          <li>
            <router-link to="/admin/movies/trash" active-class="active">Thùng rác</router-link>
          </li>
        </ul>
      </li>
      <li v-if="isAdmin || isEmployee" class="menu-item">
        <router-link :to="{ name: 'Rooms' }" active-class="active" class="menu-link">
          <span class="icon">🏢</span>
          <span class="text">Quản lí phòng</span>
        </router-link>
      </li>
      <li v-if="isAdmin" class="menu-item">
        <router-link :to="{ name: 'Branches' }" active-class="active" class="menu-link">
          <span class="icon">📍</span>
          <span class="text">Quản lí chi nhánh</span>
        </router-link>
      </li>
      <li v-if="isAdmin || isEmployee" class="menu-item">
        <router-link :to="{ name: 'Seats' }" active-class="active" class="menu-link">
          <span class="icon">💺</span>
          <span class="text">Quản lí ghế ngồi</span>
        </router-link>
      </li>
      <li v-if="isAdmin || isEmployee" class="menu-item">
        <router-link :to="{ name: 'Schedule' }" active-class="active" class="menu-link">
          <span class="icon">🗓️</span>
          <span class="text">Quản lí lịch chiếu</span>
        </router-link>
      </li>
      <li v-if="isAdmin || isEmployee" class="menu-item">
        <router-link :to="{ name: 'Bills' }" active-class="active" class="menu-link">
          <span class="icon">🎟️</span>
          <span class="text">Quản lí vé phim</span>
        </router-link>
      </li>
      <li v-if="isAdmin || isEmployee" class="menu-item">
        <router-link :to="{ name: 'Invoices' }" active-class="active" class="menu-link">
          <span class="icon">🧾</span>
          <span class="text">Quản lí hóa đơn</span>
        </router-link>
      </li>
      <li v-if="isAdmin" class="menu-item">
        <router-link :to="{ name: 'AddEmployee' }" active-class="active" class="menu-link">
          <span class="icon">➕</span>
          <span class="text">Thêm nhân viên</span>
        </router-link>
      </li>
    </ul>

    <div class="logout-section">
      <div class="divider"></div>
      <button class="logout-btn" @click="showLogoutModal = true">
        <span class="icon">🚪</span>
        <span class="text">Đăng xuất</span>
      </button>
    </div>

    <transition name="modal-fade">
      <div v-if="showLogoutModal" class="modal-overlay" @click.self="showLogoutModal = false">
        <div class="modal-card">
          <h3>Xác nhận đăng xuất</h3>
          <p>Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?</p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="showLogoutModal = false">Hủy</button>
            <button class="btn-logout" @click="confirmLogout">Đăng xuất</button>
          </div>
        </div>
      </div>
    </transition>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
let userRole = 'user'
const showLogoutModal = ref(false)
try {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  userRole = userInfo?.role?.toLowerCase?.() || 'user'
} catch (e) { }
function removeVietnameseTones(str) {
  return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/Đ/g, 'D');
}
const normalizedRole = removeVietnameseTones(userRole)
const isAdmin = computed(() =>
  normalizedRole.includes('admin') ||
  normalizedRole.includes('quan tri') ||
  normalizedRole.includes('quan ly')
)
const isEmployee = computed(() =>
  normalizedRole.includes('nhan vien') ||
  normalizedRole.includes('employee') ||
  normalizedRole.includes('staff')
)
const showMoviesMenu = ref(false)
function toggleMoviesMenu() {
  showMoviesMenu.value = !showMoviesMenu.value
}

async function signOut() {
  // Nếu không có API logout, chỉ cần xóa localStorage
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

function confirmLogout() {
  showLogoutModal.value = false
  signOut()
}
</script>

<style scoped>
.sidebar-menu {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  height: 100vh;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: #ecf0f1;
  box-shadow: 4px 0 20px rgba(44, 62, 80, 0.15);
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
  border-bottom: 1px solid rgba(236, 240, 241, 0.1);
  position: relative;
  z-index: 1;
}

.sidebar-img-logo {
  height: 28px;
  width: auto;
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 8px;
}

.logo {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #ecf0f1;
  background: none;
  -webkit-background-clip: unset;
  -webkit-text-fill-color: unset;
  background-clip: unset;
  text-shadow: 0 2px 4px rgba(44, 62, 80, 0.3);
}

.subtitle {
  font-weight: 800;
  color: #bdc3c7;
  font-size: 16px;
  margin: 0;
  opacity: 0.8;
  text-shadow: 0 1px 2px rgba(44, 62, 80, 0.3);
}

/* Menu List */
.menu-list {
  list-style: none;
  padding: 20px 15px;
  margin: 0;
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  z-index: 1;
  overflow-y: auto;
}

.menu-item {
  position: relative;
  overflow: hidden;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 14px 18px;
  color: #ecf0f1;
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: transparent;
  border: 1.5px solid transparent;
  font-weight: 700;
  font-size: 16px;
  box-shadow: none;
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
  background: #3498db;
  color: #fff;
  border: 1.5px solid #3498db;
  transform: translateX(5px) scale(1.03);
  box-shadow: 0 4px 16px rgba(52, 152, 219, 0.3);
}

.menu-link.active {
  background: #e74c3c;
  color: #fff;
  font-weight: 700;
  border: 1.5px solid #e74c3c;
  box-shadow: 0 8px 25px rgba(231, 76, 60, 0.3);
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
  filter: drop-shadow(0 2px 4px rgba(191, 162, 219, 0.10));
}

.text {
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

/* Logout Section */
.logout-section {
  padding: 20px 15px;
  position: relative;
  z-index: 1;
  margin-top: 0;
}

.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #BFA2DB55, transparent);
  margin-bottom: 20px;
}

.logout-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 14px 18px;
  background: linear-gradient(135deg, #ffb6b6, #BFA2DB);
  border: none;
  border-radius: 12px;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(191, 162, 219, 0.10);
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
  background: linear-gradient(135deg, #BFA2DB, #ffb6b6);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(191, 162, 219, 0.18);
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

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(44, 62, 80, 0.35);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.modal-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 40px rgba(44, 62, 80, 0.18);
  padding: 32px 28px 24px;
  min-width: 320px;
  max-width: 95vw;
  animation: popIn 0.2s cubic-bezier(.4, 2, .6, 1);
  position: relative;
  text-align: center;
}

@keyframes popIn {
  from {
    transform: scale(0.92);
    opacity: 0;
  }

  to {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-card h3 {
  margin-top: 0;
  margin-bottom: 14px;
  font-size: 22px;
  font-weight: 800;
  color: #BFA2DB;
}

.modal-card p {
  color: #7f8c8d;
  font-size: 16px;
  margin-bottom: 22px;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 18px;
}

.btn-cancel {
  background: #f1f1f1;
  color: #8F6BAE;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-cancel:hover {
  background: #e0e7ff;
}

.btn-logout {
  background: linear-gradient(135deg, #BFA2DB 0%, #B7E4C7 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(191, 162, 219, 0.10);
  transition: background 0.2s, transform 0.2s;
}

.btn-logout:hover {
  background: linear-gradient(135deg, #B7E4C7 0%, #BFA2DB 100%);
  transform: scale(1.04);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.2s;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.menu-item { position: relative; }
.menu-link { display: flex; align-items: center; cursor: pointer; user-select: none; }
.arrow { margin-left: auto; transition: transform 0.2s; }
.arrow.open { transform: rotate(180deg); }
.submenu { padding-left: 24px; }
.expanded > .menu-link { background: #f3e8ff; color: #7c3aed; }
</style>
