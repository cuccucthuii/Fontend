<template>
  <header class="main-header full-header dark">
    <div class="header-container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <img src="/logo.png?v=2" alt="DEV CINEMA" class="logo-img" />
        </router-link>
      </div>
      <nav class="header-menu" :class="{ open: menuOpen }">
        <router-link to="/" class="menu-link" exact-active-class="active">Trang chủ</router-link>
        <router-link to="/showtimes" class="menu-link" active-class="active">Lịch chiếu</router-link>
        <router-link to="/news" class="menu-link" active-class="active">Tin tức</router-link>
        <router-link to="/promotions" class="menu-link" active-class="active">Khuyến mãi</router-link>
        <router-link to="/prices" class="menu-link" active-class="active">Giá vé</router-link>
        <router-link to="/about" class="menu-link" active-class="active">Giới thiệu</router-link>
        <router-link to="/contact" class="menu-link" active-class="active">Liên hệ</router-link>
      </nav>
      <div class="header-search-wrap">
        <input
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          class="header-search-input"
          type="text"
          placeholder="Tìm phim, tin tức..."
        />
        <button class="header-search-btn" @click="handleSearch">
          <svg width="20" height="20" fill="none" viewBox="0 0 24 24"><path d="M21 21l-4.35-4.35" stroke="#48dbfb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><circle cx="11" cy="11" r="7" stroke="#48dbfb" stroke-width="2"/></svg>
        </button>
      </div>
      <div class="header-right">
        <template v-if="!props.isLoggedIn">
          <button class="auth-btn register-btn" @click="showAuthModal('register')">
            Đăng ký
          </button>
          <button class="auth-btn login-btn" @click="showAuthModal('login')">
            Đăng nhập
          </button>
        </template>
        <UserMenu 
          v-else
          :user-info="props.userInfo"
          @logout="showLogoutModal = true"
        />
      </div>
      <button class="menu-toggle" @click="menuOpen = !menuOpen">
        <span :class="['bar', { open: menuOpen }]"></span>
        <span :class="['bar', { open: menuOpen }]"></span>
        <span :class="['bar', { open: menuOpen }]"></span>
      </button>
    </div>
    <transition name="modal-fade">
      <div v-if="showLogoutModal" class="modal-overlay" @click.self="showLogoutModal = false">
        <div class="modal-card">
          <h3>Xác nhận đăng xuất</h3>
          <p>Bạn có chắc chắn muốn đăng xuất khỏi tài khoản <strong>{{ props.userInfo.username || props.userInfo.email }}</strong>?</p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="showLogoutModal = false">Hủy</button>
            <button class="btn-logout" @click="confirmLogout">Đăng xuất</button>
          </div>
        </div>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import UserMenu from '../common/UserMenu.vue'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  isLoggedIn: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['show-auth-modal', 'logout-success'])

const menuOpen = ref(false)
const showLogoutModal = ref(false)
const router = useRouter()
const searchQuery = ref("")

// User info and login status are now passed as props from parent component

function confirmLogout() {
  showLogoutModal.value = false
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  // Emit event to parent to update login state
  emit('logout-success')
  router.push('/')
}

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push(`/search?q=${encodeURIComponent(searchQuery.value.trim())}`)
  }
}

function showAuthModal(type) {
  emit('show-auth-modal', type)
}
</script>

<style scoped>
.header,
.main-header,
.main-header.full-header.dark,
.header-container {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  margin: 0 !important;
  padding: 0 !important;
  position: absolute !important;
  top: 0; left: 0; right: 0;
  z-index: 10;
  color: #fff;
}
.main-header.full-header.dark {
  width: 100%;
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
  position: sticky;
  top: 0;
  z-index: 100;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.header-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
  box-sizing: border-box;
  background: rgba(24,25,26,0.95);
  backdrop-filter: blur(10px);
  border-radius: 0 0 20px 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.header-left, .header-menu, .header-search-wrap, .header-right {
  margin: 0;
}
.header-left .logo {
  font-size: 28px;
  font-weight: 800;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  text-transform: uppercase;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(72, 219, 251, 0.05);
  border: 1px solid rgba(72, 219, 251, 0.1);
}

.header-left .logo:hover {
  color: #feca57;
  transform: scale(1.05);
  background: rgba(254, 202, 87, 0.1);
  border-color: rgba(254, 202, 87, 0.2);
}
.logo-img {
  height: 60px;
  width: auto;
  display: inline-block;
  vertical-align: middle;
  margin-right: 16px;
  filter: drop-shadow(0 2px 8px rgba(72, 219, 251, 0.3));
  transition: all 0.3s ease;
}

.logo:hover .logo-img {
  filter: drop-shadow(0 4px 12px rgba(254, 202, 87, 0.4));
}
.header-menu {
  display: flex;
  gap: 28px;
  align-items: center;
  transition: all 0.3s ease;
  flex: 1;
  justify-content: center;
  margin: 0 20px;
}
.menu-link {
  color: #fff;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 12px;
  transition: all 0.3s ease;
  background: none;
  border: none;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  letter-spacing: 0.3px;
  text-transform: none;
  position: relative;
  border-radius: 8px;
}
.menu-link.active, .menu-link:hover {
  color: #48dbfb;
  background: rgba(72, 219, 251, 0.1);
  transform: translateY(-1px);
}
.menu-link::after {
  content: '';
  display: block;
  height: 2px;
  width: 0;
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  border-radius: 1px;
  transition: width 0.3s ease;
  position: absolute;
  left: 50%;
  bottom: 4px;
  transform: translateX(-50%);
}
.menu-link.active::after, .menu-link:hover::after {
  width: 80%;
}
.header-search-wrap {
  display: flex;
  align-items: center;
  background: rgba(35, 37, 38, 0.8);
  border: 1px solid rgba(72, 219, 251, 0.2);
  border-radius: 12px;
  padding: 4px 12px;
  box-shadow: 0 2px 12px rgba(72, 219, 251, 0.1);
  height: 42px;
  transition: all 0.3s ease;
  min-width: 200px;
}

.header-search-wrap:focus-within {
  border-color: rgba(72, 219, 251, 0.5);
  box-shadow: 0 4px 16px rgba(72, 219, 251, 0.2);
}
.header-search-input {
  border: none;
  outline: none;
  background: transparent;
  color: #fff;
  font-size: 14px;
  padding: 4px 8px;
  flex: 1;
  font-family: inherit;
  min-width: 0;
}
.header-search-input::placeholder {
  color: #b2bec3;
  opacity: 1;
}
.header-search-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0 4px;
  display: flex;
  align-items: center;
  transition: filter 0.2s;
}
.header-search-btn:hover svg {
  filter: brightness(1.5);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.auth-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  text-decoration: none;
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  white-space: nowrap;
}
.register-btn {
  background: transparent;
  border: 2px solid #48dbfb;
  color: #48dbfb;
}
.register-btn:hover {
  background: #48dbfb;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.4);
}
.login-btn {
  background: linear-gradient(135deg, #2196f3, #48dbfb);
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}
.login-btn:hover {
  background: linear-gradient(135deg, #1976d2, #2196f3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}
.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 12px;
  border-radius: 8px;
  backdrop-filter: blur(10px);
}
.logout-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}
.logout-btn:hover {
  background: linear-gradient(135deg, #ff5252, #d32f2f);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}
.logout-icon {
  font-size: 14px;
}
.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  z-index: 200;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.menu-toggle:hover {
  background: rgba(72, 219, 251, 0.1);
}

.bar {
  width: 24px;
  height: 3px;
  background: #48dbfb;
  border-radius: 2px;
  transition: all 0.3s ease;
  transform-origin: center;
}

.bar.open:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.bar.open:nth-child(2) {
  opacity: 0;
  transform: scale(0);
}

.bar.open:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}
@media (max-width: 1200px) {
  .header-menu { gap: 20px; }
  .header-container { padding: 0 20px; }
}

@media (max-width: 1024px) {
  .header-menu { gap: 16px; }
  .header-search-wrap { min-width: 160px; }
  .menu-link { font-size: 14px; padding: 6px 10px; }
}

@media (max-width: 900px) {
  .header-container { 
    padding: 0 16px; 
    height: 64px;
  }
  .header-menu { 
    gap: 12px; 
    margin: 0 10px;
  }
  .menu-link { 
    font-size: 13px; 
    padding: 6px 8px; 
  }
  .header-search-wrap { 
    min-width: 140px; 
  }
  .logo-img { height: 50px; }
  .header-left .logo { font-size: 24px; padding: 6px 12px; }
}
@media (max-width: 768px) {
  .header-container { 
    padding: 0 12px; 
    height: 60px;
  }
  .header-menu {
    position: fixed;
    top: 60px;
    right: -100vw;
    flex-direction: column;
    background: rgba(24,25,26,0.98);
    backdrop-filter: blur(20px);
    width: 280px;
    height: calc(100vh - 60px);
    gap: 0;
    padding: 20px 0;
    box-shadow: -4px 0 20px rgba(0,0,0,0.3);
    transition: right 0.3s ease;
    z-index: 1000;
    border-radius: 0 0 0 20px;
  }
  .header-menu.open { right: 0; }
  .menu-link { 
    padding: 16px 24px; 
    font-size: 16px; 
    margin: 4px 16px;
    border-radius: 12px;
    text-align: left;
  }
  .header-search-wrap {
    display: none;
  }
  .menu-toggle { 
    display: flex; 
    margin-left: 12px;
  }
  .logo-img { height: 45px; }
  .header-left .logo { font-size: 20px; padding: 4px 8px; }
  .auth-btn {
    padding: 8px 14px;
    font-size: 13px;
  }
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(44, 62, 80, 0.35);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.2s;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.modal-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 40px rgba(44,62,80,0.18);
  padding: 32px 28px 24px;
  min-width: 320px;
  max-width: 95vw;
  animation: popIn 0.2s cubic-bezier(.4,2,.6,1);
  position: relative;
  text-align: center;
}
@keyframes popIn {
  from { transform: scale(0.92); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
.modal-card h3 {
  margin-top: 0;
  margin-bottom: 14px;
  font-size: 22px;
  font-weight: 800;
  color: #48dbfb;
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
  color: #2196f3;
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
  background: linear-gradient(135deg, #2196f3 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(33,150,243,0.10);
  transition: background 0.2s, transform 0.2s;
}
.btn-logout:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
  transform: scale(1.04);
}
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.2s;
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}
</style> 