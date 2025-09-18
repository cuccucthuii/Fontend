<template>
  <div class="vouchers-page">
    <div class="vouchers-container">
      <!-- Sidebar -->
      <div class="vouchers-sidebar">
        <div class="sidebar-header">
          <h2>Quản lý tài khoản</h2>
        </div>
        
        <nav class="sidebar-nav">
          <div class="nav-item" :class="{ active: activeTab === 'favorites' }" @click="activeTab = 'favorites'">
            <div class="nav-icon">❤️</div>
            <span>Yêu thích</span>
          </div>
          
          <div class="nav-item" :class="{ active: activeTab === 'lists' }" @click="activeTab = 'lists'">
            <div class="nav-icon">➕</div>
            <span>Danh sách</span>
          </div>
          
          <div class="nav-item" :class="{ active: activeTab === 'continue' }" @click="activeTab = 'continue'">
            <div class="nav-icon">🕐</div>
            <span>Xem tiếp</span>
          </div>
          
          <div class="nav-item" :class="{ active: activeTab === 'notifications' }" @click="activeTab = 'notifications'">
            <div class="nav-icon">🔔</div>
            <span>Thông báo</span>
          </div>
          
          <div class="nav-item" :class="{ active: activeTab === 'vouchers' }" @click="activeTab = 'vouchers'">
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
      <div class="vouchers-main">
        <!-- Vouchers Tab -->
        <div v-if="activeTab === 'vouchers'" class="tab-content">
          <!-- My Vouchers Section -->
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
                  <button class="explore-btn" @click="goToPromotions">
                    Khám phá khuyến mãi
                  </button>
                </div>
                
                <div v-else class="voucher-row" v-for="voucher in myVouchers" :key="voucher.id">
                  <div class="cell voucher-code">{{ voucher.code }}</div>
                  <div class="cell voucher-content">{{ voucher.content }}</div>
                  <div class="cell voucher-type">
                    <span class="type-badge" :class="voucher.type.toLowerCase()">
                      {{ voucher.type }}
                    </span>
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

          <!-- Voucher History Section -->
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
                    <span class="status-badge" :class="history.status.toLowerCase()">
                      {{ getStatusText(history.status) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Other tabs content -->
        <div v-else class="tab-content">
          <div class="content-header">
            <h1>{{ getTabTitle(activeTab) }}</h1>
            <p>{{ getTabDescription(activeTab) }}</p>
          </div>
          
          <div class="empty-state">
            <div class="empty-icon">📋</div>
            <h3>Chưa có dữ liệu</h3>
            <p>Nội dung này sẽ được cập nhật trong phiên bản tiếp theo.</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAvatarUrl } from '../services/avatarService'

const router = useRouter()
const activeTab = ref('vouchers')

const userInfo = ref({})
const myVouchers = ref([])
const voucherHistory = ref([])

onMounted(() => {
  // Load user info from localStorage
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      userInfo.value = JSON.parse(userInfoStr)
    } catch (e) {
      console.error('Error parsing user info:', e)
    }
  }
  
  // Load sample data (replace with API calls)
  loadVouchers()
})

function handleAvatarError(event) {
  // Fallback to default avatar if image fails to load
  event.target.src = getAvatarUrl({})
}

function getTabTitle(tab) {
  const titles = {
    favorites: 'Yêu thích',
    lists: 'Danh sách',
    continue: 'Xem tiếp',
    notifications: 'Thông báo',
    vouchers: 'Voucher của tôi'
  }
  return titles[tab] || 'Trang'
}

function getTabDescription(tab) {
  const descriptions = {
    favorites: 'Danh sách phim yêu thích của bạn',
    lists: 'Danh sách phim tùy chỉnh',
    continue: 'Phim đang xem dở',
    notifications: 'Thông báo và cập nhật',
    vouchers: 'Quản lý voucher và lịch sử sử dụng'
  }
  return descriptions[tab] || 'Mô tả trang'
}

function loadVouchers() {
  // Sample data - replace with API calls
  myVouchers.value = [
    // {
    //   id: 1,
    //   code: 'WELCOME2024',
    //   content: 'Giảm 50% cho đơn hàng đầu tiên',
    //   type: 'DISCOUNT',
    //   expiryDate: '2024-12-31'
    // },
    // {
    //   id: 2,
    //   code: 'MOVIE100K',
    //   content: 'Giảm 100,000đ cho vé phim',
    //   type: 'CASHBACK',
    //   expiryDate: '2024-11-30'
    // }
  ]
  
  voucherHistory.value = [
    // {
    //   id: 1,
    //   code: 'SUMMER2024',
    //   content: 'Giảm 30% cho combo bắp nước',
    //   usedAt: '2024-10-15T14:30:00Z',
    //   status: 'USED'
    // }
  ]
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
    'USED': 'Đã sử dụng',
    'EXPIRED': 'Hết hạn',
    'CANCELLED': 'Đã hủy'
  }
  return statusMap[status] || status
}

function useVoucher(voucher) {
  if (isExpired(voucher.expiryDate)) {
    alert('Voucher này đã hết hạn!')
    return
  }
  
  // Move to history
  const historyItem = {
    id: Date.now(),
    code: voucher.code,
    content: voucher.content,
    usedAt: new Date().toISOString(),
    status: 'USED'
  }
  
  voucherHistory.value.unshift(historyItem)
  
  // Remove from active vouchers
  const index = myVouchers.value.findIndex(v => v.id === voucher.id)
  if (index > -1) {
    myVouchers.value.splice(index, 1)
  }
  
  alert(`Đã sử dụng voucher ${voucher.code}!`)
}

function goToPromotions() {
  router.push('/promotions')
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
.vouchers-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

.vouchers-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 40px);
}

.vouchers-sidebar {
  width: 280px;
  background: #232526;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
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
  color: #e74c3c;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 8px 0;
  transition: color 0.2s;
}

.logout-btn:hover {
  color: #c0392b;
}

.logout-icon {
  font-size: 16px;
}

.vouchers-main {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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

.header-cell:last-child {
  border-right: none;
}

.table-body {
  min-height: 200px;
}

.voucher-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr;
  border-bottom: 1px solid #e1e5e9;
  transition: background-color 0.2s;
}

.voucher-row:hover {
  background: #f8f9fa;
}

.voucher-row:last-child {
  border-bottom: none;
}

.cell {
  padding: 16px 12px;
  font-size: 14px;
  color: #333;
  border-right: 1px solid #e1e5e9;
  display: flex;
  align-items: center;
}

.cell:last-child {
  border-right: none;
}

.voucher-code {
  font-weight: 600;
  color: #667eea;
}

.type-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.type-badge.discount {
  background: #e8f5e8;
  color: #2e7d32;
}

.type-badge.cashback {
  background: #fff3e0;
  color: #f57c00;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.used {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-badge.expired {
  background: #ffebee;
  color: #c62828;
}

.status-badge.cancelled {
  background: #f3e5f5;
  color: #7b1fa2;
}

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

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-vouchers h3, .empty-history h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-vouchers p, .empty-history p {
  color: #666;
  font-size: 14px;
  margin: 0 0 20px 0;
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

.explore-btn:hover {
  transform: translateY(-2px);
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
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
  .vouchers-container {
    flex-direction: column;
    gap: 16px;
  }
  
  .vouchers-sidebar {
    width: 100%;
    position: static;
  }
  
  .vouchers-main {
    padding: 20px;
  }
  
  .table-header, .voucher-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .header-cell, .cell {
    border-right: none;
    border-bottom: 1px solid #e1e5e9;
  }
  
  .header-cell:last-child, .cell:last-child {
    border-bottom: none;
  }
}
</style>
