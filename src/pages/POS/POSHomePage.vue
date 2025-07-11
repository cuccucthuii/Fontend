<template>
  <div class="pos-home-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L3 7V12C3 16.4183 6.58172 20 11 20H13C17.4183 20 21 16.4183 21 12V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            <path d="M12 9L15 12L12 15L9 12L12 9Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="hero-title">Hệ Thống Bán Vé Tại Quầy</h1>
        <p class="hero-subtitle">POS System - DEV Cinema</p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">24/7</div>
            <div class="stat-label">Hoạt động</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">100%</div>
            <div class="stat-label">Bảo mật</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">⚡</div>
            <div class="stat-label">Nhanh chóng</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
      <h2 class="section-title">Thao Tác Nhanh</h2>
      <div class="actions-grid">
        <div class="action-card" @click="navigateToShowtimes">
          <div class="action-icon">🕒</div>
          <h3>Chọn Suất Chiếu</h3>
          <p>Tìm và chọn suất chiếu phù hợp cho khách hàng</p>
          <div class="action-arrow">→</div>
        </div>
        <div class="action-card" @click="navigateToSeats">
          <div class="action-icon">��️</div>
          <template v-if="!hasSelectedSeats">
            <h3>Chọn Ghế Ngồi</h3>
            <p>Xem sơ đồ ghế và chọn vị trí mong muốn</p>
            <div class="action-arrow">→</div>
          </template>
          <template v-else>
            <h3>Đã chọn ghế: {{ selectedSeatsText }}</h3>
            <p>Nhấn để thay đổi ghế</p>
            <div class="action-arrow">��</div>
          </template>
        </div>
        <div class="action-card" @click="navigateToPayment">
          <div class="action-icon">💳</div>
          <h3>Thanh Toán</h3>
          <p>Xác nhận thông tin và hoàn tất giao dịch</p>
          <div class="action-arrow">→</div>
        </div>
      </div>
    </div>

    <!-- System Status -->
    <div class="system-status">
      <h2 class="section-title">Trạng Thái Hệ Thống</h2>
      <div class="status-grid">
        <div class="status-card online">
          <div class="status-indicator"></div>
          <div class="status-content">
            <h3>Hệ thống chính</h3>
            <p>Hoạt động bình thường</p>
          </div>
        </div>
        <div class="status-card online">
          <div class="status-indicator"></div>
          <div class="status-content">
            <h3>Cơ sở dữ liệu</h3>
            <p>Kết nối ổn định</p>
          </div>
        </div>
        <div class="status-card online">
          <div class="status-indicator"></div>
          <div class="status-content">
            <h3>Máy in vé</h3>
            <p>Sẵn sàng in vé</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Welcome Message -->
    <div class="welcome-message">
      <div class="message-content">
        <h3>🎬 Chào mừng đến với DEV Cinema POS</h3>
        <p>Sử dụng menu bên trái để bắt đầu quy trình bán vé. Chúc bạn một ca làm việc hiệu quả và thành công!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Quản lý trạng thái ghế đã chọn bằng localStorage
const selectedSeats = ref([])

onMounted(() => {
  const saved = localStorage.getItem('pos_selected_seats')
  if (saved) {
    try {
      selectedSeats.value = JSON.parse(saved)
    } catch {}
  }
})

function saveSelectedSeats(seats) {
  selectedSeats.value = seats
  localStorage.setItem('pos_selected_seats', JSON.stringify(seats))
}

function navigateToShowtimes() {
  router.push({ name: 'POSShowtimes' })
}

function navigateToSeats() {
  // Truyền seatIds nếu đã chọn
  router.push({ 
    name: 'POSSeats', 
    query: selectedSeats.value.length ? { seatIds: selectedSeats.value.join(',') } : {} 
  })
}

function navigateToPayment() {
  router.push({ name: 'POSPayment' })
}

const hasSelectedSeats = computed(() => selectedSeats.value.length > 0)
const selectedSeatsText = computed(() => selectedSeats.value.join(', '))
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap');

.pos-home-page {
  font-family: 'Roboto', Arial, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  padding: 32px;
  color: #ffffff;
}

/* Hero Section */
.hero-section {
  text-align: center;
  margin-bottom: 48px;
  padding: 48px 0;
}

.hero-icon {
  margin-bottom: 24px;
  color: #ffffff;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #ffffff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.hero-subtitle {
  font-size: 1.3rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 300;
  margin-bottom: 32px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 32px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 900;
  color: #feca57;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Section Titles */
.section-title {
  font-size: 2rem;
  font-weight: 700;
  text-align: center;
  margin-bottom: 32px;
  color: #ffffff;
}

/* Quick Actions */
.quick-actions {
  margin-bottom: 48px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.action-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.action-card h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 12px;
  color: #ffffff;
}

.action-card p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 20px;
}

.action-arrow {
  font-size: 1.5rem;
  color: #feca57;
  font-weight: bold;
}

/* System Status */
.system-status {
  margin-bottom: 48px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.status-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2ed573;
  box-shadow: 0 0 10px rgba(46, 213, 115, 0.5);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.status-content h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 4px;
  color: #ffffff;
}

.status-content p {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

/* Welcome Message */
.welcome-message {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 32px;
  text-align: center;
}

.message-content h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: #ffffff;
}

.message-content p {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 24px;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
}
</style>
  