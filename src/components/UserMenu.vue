<template>
  <div class="user-menu-container">
    <!-- User info display -->
    <div class="user-info" @click="toggleMenu">
      <div class="user-avatar">
        <img 
          :src="getAvatarUrl(userInfo)" 
          :alt="userInfo.username || userInfo.email"
          @error="handleAvatarError"
        />
      </div>
      <div class="user-details">
        <div class="user-name">{{ userInfo.username || userInfo.email }}</div>
        <div class="user-email">{{ userInfo.email }}</div>
      </div>
      <div class="dropdown-arrow" :class="{ open: showMenu }">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
          <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <!-- Dropdown menu -->
    <transition name="menu-fade">
      <div v-if="showMenu" class="dropdown-menu">
        <div class="menu-item" @click="goToAccount">
          <div class="menu-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Tài khoản của tôi</span>
        </div>

        <div class="menu-item" @click="goToVouchers">
          <div class="menu-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="3" width="20" height="14" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <line x1="8" y1="21" x2="16" y2="21" stroke="currentColor" stroke-width="2"/>
              <line x1="12" y1="17" x2="12" y2="21" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Voucher của tôi</span>
        </div>

        <div class="menu-divider"></div>

        <div class="menu-item logout" @click="handleLogout">
          <div class="menu-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="2"/>
              <polyline points="16,17 21,12 16,7" stroke="currentColor" stroke-width="2"/>
              <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Đăng xuất</span>
        </div>
      </div>
    </transition>

    <!-- Backdrop to close menu -->
    <div v-if="showMenu" class="menu-backdrop" @click="closeMenu"></div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAvatarUrl } from '../services/avatarService'

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['logout'])

const router = useRouter()
const showMenu = ref(false)

function toggleMenu() {
  showMenu.value = !showMenu.value
}

function closeMenu() {
  showMenu.value = false
}

function handleAvatarError(event) {
  // Fallback to default avatar if image fails to load
  event.target.src = getAvatarUrl({})
}

function goToAccount() {
  closeMenu()
  router.push('/account')
}

function goToVouchers() {
  closeMenu()
  router.push({ path: '/account', query: { tab: 'vouchers' } })
}

function handleLogout() {
  closeMenu()
  emit('logout')
}
</script>

<style scoped>
.user-menu-container {
  position: relative;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  color: white;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.2;
}

.user-email {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.2;
}

.dropdown-arrow {
  color: rgba(255, 255, 255, 0.7);
  transition: transform 0.2s;
}

.dropdown-arrow.open {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.1);
  min-width: 220px;
  z-index: 1000;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.logout {
  color: #e74c3c;
}

.menu-item.logout:hover {
  background: #fdf2f2;
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: #666;
}

.menu-item.logout .menu-icon {
  color: #e74c3c;
}

.menu-divider {
  height: 1px;
  background: #e9ecef;
  margin: 4px 0;
}

.menu-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

/* Animation */
.menu-fade-enter-active, .menu-fade-leave-active {
  transition: all 0.2s ease;
}

.menu-fade-enter-from {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

.menu-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* Responsive */
@media (max-width: 768px) {
  .user-info {
    padding: 6px 10px;
    gap: 8px;
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
  }
  
  .avatar-placeholder {
    font-size: 14px;
  }
  
  .user-name {
    font-size: 13px;
  }
  
  .user-email {
    font-size: 11px;
  }
  
  .dropdown-menu {
    min-width: 200px;
    right: -10px;
  }
}
</style>
