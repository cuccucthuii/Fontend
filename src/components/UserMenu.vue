<template>
  <div class="user-menu-container">
    <!-- Avatar only display -->
    <div class="user-avatar-wrapper" @click="toggleMenu">
      <div class="user-avatar">
        <img 
          v-if="getAvatarUrl(userInfo) && getAvatarUrl(userInfo) !== '/logo.png'"
          :src="getAvatarUrl(userInfo)" 
          :alt="userInfo.username || userInfo.email"
          @error="handleAvatarError"
        />
        <div v-else class="avatar-placeholder">
          {{ getInitials(userInfo) }}
        </div>
      </div>
      <div class="avatar-indicator" :class="{ active: showMenu }">
        <svg width="8" height="8" viewBox="0 0 24 24" fill="none">
          <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>

    <!-- Dropdown menu -->
    <transition name="menu-fade">
      <div v-if="showMenu" class="dropdown-menu">
        <!-- User info header -->
        <div class="user-info-header">
          <div class="user-info-avatar">
            <img 
              v-if="getAvatarUrl(userInfo) && getAvatarUrl(userInfo) !== '/logo.png'"
              :src="getAvatarUrl(userInfo)" 
              :alt="userInfo.username || userInfo.email"
              @error="handleAvatarError"
            />
            <div v-else class="avatar-placeholder">
              {{ getInitials(userInfo) }}
            </div>
          </div>
          <div class="user-info-details">
            <div class="user-info-name">{{ userInfo.username || userInfo.fullName || 'Người dùng' }}</div>
            <div class="user-info-email">{{ userInfo.email }}</div>
          </div>
        </div>

        <div class="menu-divider"></div>

        <!-- Menu items -->
        <div class="menu-item" @click="goToAccount">
          <div class="menu-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Tài khoản của tôi</span>
        </div>

        <div class="menu-item" @click="goToVouchers">
          <div class="menu-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="3" width="20" height="14" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <line x1="8" y1="21" x2="16" y2="21" stroke="currentColor" stroke-width="2"/>
              <line x1="12" y1="17" x2="12" y2="21" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Voucher của tôi</span>
        </div>

        <div class="menu-item" @click="showChangePassword">
          <div class="menu-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="16" r="1" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <span>Đổi mật khẩu</span>
        </div>

        <div class="menu-divider"></div>

        <div class="menu-item logout" @click="handleLogout">
          <div class="menu-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
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

    <!-- Change Password Modal -->
    <ChangePasswordModal 
      :show="showChangePasswordModal"
      @close="showChangePasswordModal = false"
      @success="handlePasswordChangeSuccess"
      @error="handlePasswordChangeError"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAvatarUrl } from '../services/avatarService'
import ChangePasswordModal from './ChangePasswordModal.vue'

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['logout'])

const router = useRouter()
const showMenu = ref(false)
const showChangePasswordModal = ref(false)

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

function getInitials(userInfo) {
  const name = userInfo.username || userInfo.fullName || userInfo.email || 'U'
  const words = name.split(' ')
  if (words.length >= 2) {
    return (words[0][0] + words[1][0]).toUpperCase()
  }
  return name[0].toUpperCase()
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

function showChangePassword() {
  closeMenu()
  showChangePasswordModal.value = true
}

function handlePasswordChangeSuccess(message) {
  showChangePasswordModal.value = false
  // You can add a toast notification here
  console.log('Success:', message)
  alert(message) // Temporary - replace with proper toast notification
}

function handlePasswordChangeError(errorMessage) {
  // You can add a toast notification here
  console.error('Error:', errorMessage)
  alert(errorMessage) // Temporary - replace with proper toast notification
}
</script>

<style scoped>
.user-menu-container {
  position: relative;
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar-wrapper:hover {
  transform: scale(1.05);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #48dbfb 0%, #feca57 100%);
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  position: relative;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  filter: brightness(1.1) contrast(1.1);
}

.user-avatar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(72, 219, 251, 0.1) 0%, rgba(254, 202, 87, 0.1) 100%);
  border-radius: 50%;
  z-index: 1;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #48dbfb 0%, #feca57 100%);
  color: white;
  font-weight: 700;
  font-size: 16px;
  border-radius: 50%;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.avatar-indicator {
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 14px;
  height: 14px;
  background: #48dbfb;
  border: 2px solid #18191a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 8px;
  transition: all 0.3s ease;
  transform: rotate(0deg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.avatar-indicator.active {
  background: #feca57;
  transform: rotate(180deg);
  box-shadow: 0 2px 6px rgba(254, 202, 87, 0.4);
}

.avatar-indicator svg {
  width: 5px;
  height: 5px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.1);
  min-width: 260px;
  z-index: 1000;
  overflow: hidden;
  padding: 12px 0;
}

/* User info header */
.user-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  margin-bottom: 8px;
}

.user-info-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #48dbfb 0%, #feca57 100%);
  border: 2px solid rgba(72, 219, 251, 0.3);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.user-info-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  filter: brightness(1.05) contrast(1.05);
}

.user-info-details {
  flex: 1;
}

.user-info-name {
  color: #333;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.3;
  margin-bottom: 2px;
}

.user-info-email {
  color: #666;
  font-size: 12px;
  line-height: 1.3;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  margin: 1px 8px;
  border-radius: 8px;
}

.menu-item:hover {
  background: #f8f9fa;
  color: #48dbfb;
}

.menu-item.logout {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.05);
  border: 1px solid rgba(231, 76, 60, 0.1);
  font-weight: 600;
}

.menu-item.logout:hover {
  background: rgba(231, 76, 60, 0.1);
  color: #c0392b;
  border-color: rgba(231, 76, 60, 0.2);
  transform: translateX(2px);
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  color: #666;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.menu-item:hover .menu-icon {
  color: #48dbfb;
  transform: scale(1.1);
}

.menu-item.logout .menu-icon {
  color: #e74c3c;
}

.menu-item.logout:hover .menu-icon {
  color: #c0392b;
  transform: scale(1.1);
}

.menu-divider {
  height: 1px;
  background: #e9ecef;
  margin: 6px 16px;
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
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-fade-enter-from {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

.menu-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

/* Responsive */
@media (max-width: 768px) {
  .user-avatar {
    width: 38px;
    height: 38px;
  }
  
  .avatar-indicator {
    width: 12px;
    height: 12px;
  }
  
  .dropdown-menu {
    min-width: 240px;
    right: -8px;
  }
  
  .user-info-header {
    padding: 0 12px 6px;
  }
  
  .user-info-avatar {
    width: 36px;
    height: 36px;
  }
  
  .user-info-name {
    font-size: 14px;
  }
  
  .user-info-email {
    font-size: 11px;
  }
  
  .menu-item {
    padding: 8px 12px;
    font-size: 13px;
    margin: 1px 4px;
  }
  
  .menu-icon {
    width: 16px;
    height: 16px;
  }
  
  .avatar-placeholder {
    font-size: 14px;
  }
}
</style>
