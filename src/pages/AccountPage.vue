<template>
  <div class="account-page">
    <div class="account-container">
      <!-- Sidebar -->
      <div class="account-sidebar">
        <div class="sidebar-header">
          <h2>Quản lý tài khoản</h2>
        </div>
        
        <nav class="sidebar-nav">
          <div class="nav-item" :class="{ active: activeTab === 'account' }" @click="setActiveTab('account')">
            <div class="nav-icon">👤</div>
            <span>Tài khoản</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'vouchers' }" @click="setActiveTab('vouchers')">
            <div class="nav-icon">🎫</div>
            <span>Voucher của tôi</span>
          </div>
        </nav>
        
        <div class="sidebar-footer">
          <div class="user-profile">
            <div class="profile-avatar">
              <img 
                :src="getAvatarUrl(userInfo)" 
                :alt="userInfo.username || userInfo.email"
                @error="handleAvatarError"
              />
            </div>
            <div class="profile-info">
              <div class="profile-name">{{ userInfo.username || userInfo.email }} ∞</div>
              <div class="profile-email">{{ userInfo.email }}</div>
            </div>
          </div>
          
          <button class="logout-btn" @click="handleLogout">
            <div class="logout-icon">↗️</div>
            <span>Thoát</span>
          </button>
        </div>
      </div>

      <!-- Main content -->
      <div class="account-main">
        <!-- Account Tab -->
        <div v-if="activeTab === 'account'" class="tab-content">
          <div class="content-header">
            <h1>Tài khoản</h1>
            <p>Cập nhật thông tin tài khoản</p>
          </div>

          <div class="account-form">
            <div class="form-group">
              <label>Email</label>
              <input 
                type="email" 
                :value="userInfo.email" 
                disabled 
                class="form-input disabled"
              />
                </div>
            
            <div class="form-group">
              <label>Tên hiển thị</label>
              <input 
                v-model="accountForm.displayName"
                type="text" 
                class="form-input"
                placeholder="Nhập tên hiển thị"
              />
        </div>

            <div class="form-group">
              <label>Giới tính</label>
              <div class="radio-group">
                <label class="radio-item">
                  <input 
                    v-model="accountForm.gender" 
                    type="radio" 
                    value="male" 
                  />
                  <span>Nam</span>
                </label>
                <label class="radio-item">
                  <input 
                    v-model="accountForm.gender" 
                    type="radio" 
                    value="female" 
                  />
                  <span>Nữ</span>
                </label>
                <label class="radio-item">
                  <input 
                    v-model="accountForm.gender" 
                    type="radio" 
                    value="other" 
                  />
                  <span>Không xác định</span>
                </label>
      </div>
    </div>

            <div class="form-actions">
              <button class="update-btn" @click="updateAccount">
                Cập nhật
          </button>
        </div>

            <div class="password-section">
              <a href="#" class="change-password-link" @click="showChangePassword = true">
                Đổi mật khẩu, nhấn vào <span class="highlight">đây</span>
              </a>
      </div>
          </div>

          <div class="profile-picture-section">
            <div class="profile-picture">
              <img 
                :src="getAvatarUrl(userInfo)" 
                :alt="userInfo.username || userInfo.email"
                @error="handleAvatarError"
              />
            </div>
            <div class="picture-info">
              <span>Ảnh có sẵn</span>
              <div class="picture-icon">⊞</div>
            </div>
            </div>
        </div>

        <!-- Vouchers Tab -->
        <div v-else class="tab-content">
          <div class="vouchers-section">
            <h1 class="section-title">VOUCHER CỦA TÔI</h1>
            <div class="vouchers-table">
              <div class="table-header">
                <div class="header-cell">MÃ VOUCHER</div>
                <div class="header-cell">NỘI DUNG VOUCHER</div>
                <div class="header-cell">LOẠI VOUCHER</div>
                <div class="header-cell">NGÀY HẾT HẠN</div>
                <div class="header-cell">THAO TÁC</div>
              </div>
              <div class="table-body">
                <div v-if="myVouchers.length === 0" class="empty-vouchers">
                  <div class="empty-icon">🎫</div>
                  <h3>Chưa có voucher nào</h3>
                  <p>Bạn chưa sở hữu voucher nào. Hãy tham gia các chương trình khuyến mãi để nhận voucher!</p>
                  <button class="explore-btn" @click="goToPromotions">Khám phá khuyến mãi</button>
                </div>
                <div v-else class="voucher-row" v-for="voucher in myVouchers" :key="voucher.id">
                  <div class="cell voucher-code">{{ voucher.code }}</div>
                  <div class="cell voucher-content">{{ voucher.content }}</div>
                  <div class="cell voucher-type">
                    <span class="type-badge" :class="voucher.type.toLowerCase()">{{ voucher.type }}</span>
                  </div>
                    <div class="cell expiry-date">{{ formatDate(voucher.expiryDate) }}</div>
                  <div class="cell actions">
                    <button class="use-btn" @click="useVoucher(voucher)" :disabled="isExpired(voucher.expiryDate)">
                      {{ isExpired(voucher.expiryDate) ? 'Hết hạn' : 'Sử dụng' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="vouchers-section">
            <h1 class="section-title">LỊCH SỬ VOUCHER</h1>
            <div class="vouchers-table">
              <div class="table-header">
                <div class="header-cell">THỜI GIAN</div>
                <div class="header-cell">MÃ VOUCHER</div>
                <div class="header-cell">NỘI DUNG VOUCHER</div>
                <div class="header-cell">TRẠNG THÁI</div>
              </div>
              <div class="table-body">
                <div v-if="voucherHistory.length === 0" class="empty-history">
                  <div class="empty-icon">📋</div>
                  <h3>Chưa có lịch sử</h3>
                  <p>Bạn chưa sử dụng voucher nào. Lịch sử sử dụng voucher sẽ hiển thị ở đây.</p>
                </div>
                <div v-else class="voucher-row" v-for="history in voucherHistory" :key="history.id">
                  <div class="cell usage-time">{{ formatDateTime(history.usedAt) }}</div>
                  <div class="cell voucher-code">{{ history.code }}</div>
                  <div class="cell voucher-content">{{ history.content }}</div>
                  <div class="cell status">
                    <span class="status-badge" :class="history.status.toLowerCase()">{{ getStatusText(history.status) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>

    <!-- Scroll to top button -->
    <button class="scroll-top" @click="scrollToTop">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M18 15L12 9L6 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
            </button>

    <!-- Avatar Modal -->
    <AvatarModal 
      :show="showAvatarModal"
      :current-avatar="getAvatarUrl(userInfo)"
      @close="showAvatarModal = false"
      @avatar-changed="handleAvatarChanged"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAvatarUrl } from '../services/avatarService'
import AvatarModal from '../components/AvatarModal.vue'

const router = useRouter()
const route = useRoute()
const showChangePassword = ref(false)
const showAvatarModal = ref(false)

const userInfo = ref({})
const accountForm = ref({
  displayName: '',
  gender: 'other'
})

// Vouchers state
const myVouchers = ref([])
const voucherHistory = ref([])

// Tabs state
const activeTab = ref('account')
function setActiveTab(tab) {
  activeTab.value = tab
  // keep URL in sync
  const query = { ...route.query, tab }
  router.replace({ path: '/account', query })
}

onMounted(() => {
  console.log('AccountPage mounted')
  // init tab from query
  const tabFromQuery = route.query.tab
  if (tabFromQuery === 'vouchers') {
    activeTab.value = 'vouchers'
  }
  // preload vouchers (replace with API later)
  loadVouchers()
  // Load user info from localStorage
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      userInfo.value = JSON.parse(userInfoStr)
      accountForm.value.displayName = userInfo.value.username || userInfo.value.displayName || ''
      accountForm.value.gender = userInfo.value.gender || 'other'
      console.log('User info loaded:', userInfo.value)
    } catch (e) {
      console.error('Error parsing user info:', e)
    }
  } else {
    console.log('No user info found in localStorage')
    // Set default values for testing
    userInfo.value = {
      username: 'Test User',
      email: 'test@example.com',
      avatar: '/logo.png'
    }
    accountForm.value.displayName = 'Test User'
  }
})

function handleAvatarError(event) {
  // Fallback to default avatar if image fails to load
  event.target.src = getAvatarUrl({})
}

// ----- Vouchers helpers -----
function loadVouchers() {
  // Placeholder data; connect to API when available
  myVouchers.value = []
  voucherHistory.value = []
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

function formatDateTime(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('vi-VN')
}

function isExpired(expiryDate) {
  if (!expiryDate) return false
  return new Date(expiryDate) < new Date()
}

function getStatusText(status) {
  const statusMap = {
    USED: 'Đã sử dụng',
    EXPIRED: 'Hết hạn',
    CANCELLED: 'Đã hủy'
  }
  return statusMap[status] || status
}

function useVoucher(voucher) {
  if (isExpired(voucher.expiryDate)) {
    alert('Voucher này đã hết hạn!')
    return
  }
  const historyItem = {
    id: Date.now(),
    code: voucher.code,
    content: voucher.content,
    usedAt: new Date().toISOString(),
    status: 'USED'
  }
  voucherHistory.value.unshift(historyItem)
  const index = myVouchers.value.findIndex(v => v.id === voucher.id)
  if (index > -1) myVouchers.value.splice(index, 1)
  alert(`Đã sử dụng voucher ${voucher.code}!`)
}

function goToPromotions() {
  router.push('/promotions')
}

function handleAvatarChanged(newAvatarUrl) {
  // Update user info with new avatar
  const updatedUserInfo = {
    ...userInfo.value,
    avatar: newAvatarUrl
  }
  
  localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
  userInfo.value = updatedUserInfo
  
  showAvatarModal.value = false
  alert('Cập nhật ảnh đại diện thành công!')
}


function updateAccount() {
  // Update user info
  const updatedUserInfo = {
    ...userInfo.value,
    username: accountForm.value.displayName,
    displayName: accountForm.value.displayName,
    gender: accountForm.value.gender
  }
  
  localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
  userInfo.value = updatedUserInfo
  
  alert('Cập nhật thông tin thành công!')
}

function handleLogout() {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  router.push('/')
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.account-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
  color: #333;
}

.account-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 40px);
}

.account-sidebar {
  width: 280px;
  background: #1a1a1a;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.sidebar-header h2 {
  color: white;
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 24px 0;
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #b2bec3;
  font-size: 14px;
  font-weight: 500;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: #ffd700;
  color: #333;
}

.nav-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.profile-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e1e5e9;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: white;
  font-weight: 700;
  font-size: 14px;
}

.avatar-placeholder.large {
  font-size: 24px;
}

.profile-info {
  flex: 1;
}

.profile-name {
  color: white;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 2px;
}

.profile-email {
  color: #b2bec3;
  font-size: 12px;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #b2bec3;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 8px 0;
  transition: color 0.2s;
}

.logout-btn:hover {
  color: #e74c3c;
}

.logout-icon {
  font-size: 16px;
}

.account-main {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.content-header {
  margin-bottom: 32px;
}

.content-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.content-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.account-form {
  max-width: 500px;
}

.form-group {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
  outline: none;
}

.form-input:focus {
  border-color: #667eea;
}

.form-input.disabled {
  background: #f8f9fa;
  color: #666;
  cursor: not-allowed;
  border-color: #e1e5e9;
}

.radio-group {
  display: flex;
  gap: 24px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.radio-item input[type="radio"] {
  margin: 0;
}

.form-actions {
  margin-bottom: 24px;
}

.update-btn {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #333;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.3);
}

.update-btn:hover {
  background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.password-section {
  margin-top: 24px;
}

.change-password-link {
  color: #333;
  text-decoration: none;
  font-size: 14px;
}

.change-password-link:hover {
  text-decoration: underline;
}

.highlight {
  color: #667eea;
  font-weight: 600;
  text-decoration: underline;
  cursor: pointer;
}

.profile-picture-section {
  position: absolute;
  right: 32px;
  top: 32px;
  text-align: center;
}

.profile-picture {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  border: 2px solid #e1e5e9;
}

.profile-picture img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.picture-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.picture-icon {
  font-size: 14px;
  color: #666;
}

.vouchers-section {
  margin-bottom: 48px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
  margin: 0 0 24px 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.vouchers-table {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr;
  background: #f8f9fa;
  border-bottom: 1px solid #e1e5e9;
}

.header-cell {
  padding: 16px 12px;
  font-size: 12px;
  font-weight: 700;
  color: #333;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-right: 1px solid #e1e5e9;
}

.header-cell:last-child { border-right: none; }

.table-body { min-height: 200px; }

.voucher-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr;
  border-bottom: 1px solid #e1e5e9;
  transition: background-color 0.2s;
}

.voucher-row:hover { background: #f8f9fa; }
.voucher-row:last-child { border-bottom: none; }

.cell {
  padding: 16px 12px;
  font-size: 14px;
  color: #333;
  border-right: 1px solid #e1e5e9;
  display: flex;
  align-items: center;
}

.cell:last-child { border-right: none; }

.voucher-code { font-weight: 600; color: #667eea; }

.type-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.type-badge.discount { background: #e8f5e8; color: #2e7d32; }
.type-badge.cashback { background: #fff3e0; color: #f57c00; }

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.used { background: #e8f5e8; color: #2e7d32; }
.status-badge.expired { background: #ffebee; color: #c62828; }
.status-badge.cancelled { background: #f3e5f5; color: #7b1fa2; }

.use-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.use-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.use-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.empty-vouchers, .empty-history {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
}

.explore-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.explore-btn:hover { transform: translateY(-2px); }

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.scroll-top {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  color: #666;
}

.scroll-top:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

/* Responsive */
@media (max-width: 768px) {
  .account-container {
    flex-direction: column;
    gap: 16px;
  }
  
  .account-sidebar {
    width: 100%;
    position: static;
  }
  
  .account-main {
    padding: 20px;
  }
  
  .profile-picture-section {
    position: static;
    margin-top: 24px;
  }
  
  .radio-group {
    flex-direction: column;
    gap: 12px;
  }
}
</style>