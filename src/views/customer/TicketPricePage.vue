<template>
    <Header 
    :is-logged-in="isLoggedIn"
    :user-info="userInfo"
    @show-auth-modal="handleShowAuthModal"
    @logout-success="handleLogoutSuccess"
  />
  <div class="ticket-price-page">
    <!-- Hero Section -->
    <div class="price-hero">
      <img src="https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80" alt="Ticket Price Banner" class="price-hero-img" />
      <div class="price-hero-content">
        <h1>Bảng Giá Vé</h1>
        <p>Trải nghiệm điện ảnh đỉnh cao với giá cả hợp lý nhất!</p>
      </div>
    </div>

    <!-- Price Cards Section -->
    <div class="price-cards-section">
      <h2>Giá Vé Theo Loại</h2>
      <div class="price-cards">
        <div class="price-card">
          <div class="price-card-header">
            <div class="price-card-icon">🎬</div>
            <h3>Phim 2D</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Người lớn (T2-T6)</span>
              <span class="price-value">70.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">HSSV (T2-T6)</span>
              <span class="price-value">55.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">Người lớn (T7,CN,Lễ)</span>
              <span class="price-value">90.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">HSSV (T7,CN,Lễ)</span>
              <span class="price-value">70.000đ</span>
            </div>
          </div>
        </div>

        <div class="price-card featured">
          <div class="price-card-badge">PHỔ BIẾN</div>
          <div class="price-card-header">
            <div class="price-card-icon">🎭</div>
            <h3>Phim 3D</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Tất cả đối tượng</span>
              <span class="price-value">120.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">Áp dụng mọi ngày</span>
              <span class="price-value">-</span>
            </div>
          </div>
        </div>

        <div class="price-card">
          <div class="price-card-header">
            <div class="price-card-icon">🎪</div>
            <h3>Phim 4DX</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Người lớn</span>
              <span class="price-value">180.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">HSSV</span>
              <span class="price-value">150.000đ</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Seat Type Price Section -->
    <div class="price-cards-section">
      <h2>Giá Vé Theo Loại Ghế</h2>
      <div class="price-cards">
        <div class="price-card">
          <div class="price-card-header">
            <div class="price-card-icon">🪑</div>
            <h3>Ghế Thường</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Giá vé</span>
              <span class="price-value">50.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">Vị trí</span>
              <span class="price-value">Giữa phòng</span>
            </div>
            <div class="price-item">
              <span class="price-label">Đặc điểm</span>
              <span class="price-value">Ghế tiêu chuẩn</span>
            </div>
          </div>
        </div>

        <div class="price-card featured">
          <div class="price-card-badge">VIP</div>
          <div class="price-card-header">
            <div class="price-card-icon">👑</div>
            <h3>Ghế VIP</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Giá vé</span>
              <span class="price-value">80.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">Vị trí</span>
              <span class="price-value">2 hàng cuối</span>
            </div>
            <div class="price-item">
              <span class="price-label">Đặc điểm</span>
              <span class="price-value">Rộng rãi, thoải mái</span>
            </div>
          </div>
        </div>

        <div class="price-card">
          <div class="price-card-header">
            <div class="price-card-icon">💕</div>
            <h3>Ghế Đôi</h3>
          </div>
          <div class="price-card-content">
            <div class="price-item">
              <span class="price-label">Giá vé</span>
              <span class="price-value">120.000đ</span>
            </div>
            <div class="price-item">
              <span class="price-label">Vị trí</span>
              <span class="price-value">2 cột cuối</span>
            </div>
            <div class="price-item">
              <span class="price-label">Đặc điểm</span>
              <span class="price-value">Phù hợp cặp đôi</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Combo Section -->
    <div class="combo-section">
      <h2>Combo Ưu Đãi</h2>
      <div class="combo-cards">
        <div class="combo-card">
          <div class="combo-card-image">
            <img src="https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=400&q=80" alt="Combo 1" />
          </div>
          <div class="combo-card-content">
            <h3>Combo Tiết Kiệm</h3>
            <div class="combo-items">
              <span>2 vé phim</span>
              <span>1 bắp lớn</span>
              <span>2 nước ngọt</span>
            </div>
            <div class="combo-price">
              <span class="combo-price-old">280.000đ</span>
              <span class="combo-price-new">220.000đ</span>
            </div>
            <div class="combo-save">Tiết kiệm 60.000đ</div>
          </div>
        </div>

        <div class="combo-card">
          <div class="combo-card-image">
            <img src="https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=400&q=80" alt="Combo 2" />
          </div>
          <div class="combo-card-content">
            <h3>Combo Gia Đình</h3>
            <div class="combo-items">
              <span>4 vé phim</span>
              <span>2 bắp lớn</span>
              <span>4 nước ngọt</span>
            </div>
            <div class="combo-price">
              <span class="combo-price-old">520.000đ</span>
              <span class="combo-price-new">420.000đ</span>
            </div>
            <div class="combo-save">Tiết kiệm 100.000đ</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Detailed Price Table -->
    <div class="price-table-section">
      <h2>Bảng Giá Chi Tiết</h2>
      <div class="price-table-container">
        <table class="price-table">
          <thead>
            <tr>
              <th>Loại vé</th>
              <th>Đối tượng</th>
              <th>Thời gian</th>
              <th>Giá vé</th>
              <th>Ghi chú</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in priceList" :key="row.id">
              <td><span class="type-badge">{{ row.type }}</span></td>
              <td>{{ row.audience }}</td>
              <td>{{ row.time }}</td>
              <td class="price-value">{{ row.price }}</td>
              <td>{{ row.note }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Notes Section -->
    <div class="notes-section">
      <h2>Lưu Ý Quan Trọng</h2>
      <div class="notes-grid">
        <div class="note-item">
          <div class="note-icon">💡</div>
          <div class="note-content">
            <h4>Giá đã bao gồm VAT</h4>
            <p>Tất cả giá vé đã bao gồm thuế VAT theo quy định</p>
          </div>
        </div>
        <div class="note-item">
          <div class="note-icon">📅</div>
          <div class="note-content">
            <h4>Giá có thể thay đổi</h4>
            <p>Giá vé có thể thay đổi vào các dịp lễ, Tết hoặc theo từng rạp</p>
          </div>
        </div>
        <div class="note-item">
          <div class="note-icon">🎫</div>
          <div class="note-content">
            <h4>Xuất trình thẻ HSSV</h4>
            <p>Học sinh, sinh viên cần xuất trình thẻ HSSV hợp lệ</p>
          </div>
        </div>
        <div class="note-item">
          <div class="note-icon">☎️</div>
          <div class="note-content">
            <h4>Liên hệ hỗ trợ</h4>
            <p>Vui lòng liên hệ quầy vé hoặc hotline để biết thêm chi tiết</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <HomeFooter />

  <!-- Auth Modal -->
  <AuthModal 
    :show="showAuthModal"
    @close="showAuthModal = false"
    @login-success="handleLoginSuccess"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AuthModal from '../../components/modals/AuthModal.vue'
import HomeFooter from '../../components/layout/HomeFooter.vue'
import Header from '../../components/layout/Header.vue'

const priceList = ref([
  { id: 1, type: '2D', audience: 'Người lớn', time: 'Thứ 2 - Thứ 6', price: '70.000đ', note: '' },
  { id: 2, type: '2D', audience: 'Trẻ em, HSSV', time: 'Thứ 2 - Thứ 6', price: '55.000đ', note: 'Xuất trình thẻ HSSV' },
  { id: 3, type: '2D', audience: 'Người lớn', time: 'Thứ 7, CN, Lễ', price: '90.000đ', note: '' },
  { id: 4, type: '2D', audience: 'Trẻ em, HSSV', time: 'Thứ 7, CN, Lễ', price: '70.000đ', note: '' },
  { id: 5, type: '3D', audience: 'Tất cả', time: 'Tất cả các ngày', price: '120.000đ', note: '' },
  { id: 6, type: '4DX', audience: 'Người lớn', time: 'Tất cả các ngày', price: '180.000đ', note: '' },
  { id: 7, type: '4DX', audience: 'HSSV', time: 'Tất cả các ngày', price: '150.000đ', note: 'Xuất trình thẻ HSSV' },
  // Giá vé theo loại ghế
  { id: 8, type: 'Ghế Thường', audience: 'Tất cả', time: 'Tất cả các ngày', price: '50.000đ', note: 'Ghế ở giữa phòng' },
  { id: 9, type: 'Ghế VIP', audience: 'Tất cả', time: 'Tất cả các ngày', price: '80.000đ', note: '2 hàng cuối của phòng' },
  { id: 10, type: 'Ghế Đôi', audience: 'Tất cả', time: 'Tất cả các ngày', price: '120.000đ', note: '2 cột cuối của phòng' },
])

// Login state management
const isLoggedIn = ref(false)
const userInfo = ref({})
const showAuthModal = ref(false)

// Load login state from localStorage
function loadLoginState() {
  isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true'
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (error) {
      console.error('Error parsing userInfo:', error)
      userInfo.value = {}
    }
  }
}

// Auth modal handlers
function handleShowAuthModal(type) {
  showAuthModal.value = true
}

function handleLoginSuccess(userData) {
  isLoggedIn.value = true
  userInfo.value = userData
  showAuthModal.value = false
}

function handleLogoutSuccess() {
  isLoggedIn.value = false
  userInfo.value = {}
}

// Load login state when component mounts
onMounted(() => {
  loadLoginState()
})
</script>

<style scoped>
.ticket-price-page {
  min-height: 100vh;
  background: #18191a;
  color: #fff;
}

/* Hero Section */
.price-hero {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
  margin-bottom: 60px;
}

.price-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.6);
}

.price-hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
  z-index: 2;
}

.price-hero-content h1 {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 16px;
  color: #48dbfb;
  text-shadow: 0 4px 20px rgba(0,0,0,0.8);
}

.price-hero-content p {
  font-size: 1.3rem;
  color: #feca57;
  max-width: 600px;
  margin: 0 auto;
}

/* Price Cards Section */
.price-cards-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.price-cards-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.price-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
}

.price-card {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  transition: all 0.3s ease;
  position: relative;
}

.price-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
  border-color: rgba(72,219,251,0.3);
}

.price-card.featured {
  border: 2px solid #feca57;
  box-shadow: 0 8px 32px rgba(254,202,87,0.2);
}

.price-card-badge {
  position: absolute;
  top: -10px;
  right: 20px;
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
  color: #18191a;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
}

.price-card-header {
  text-align: center;
  margin-bottom: 25px;
}

.price-card-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.price-card-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #48dbfb;
  margin: 0;
}

.price-card-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(72,219,251,0.1);
}

.price-item:last-child {
  border-bottom: none;
}

.price-label {
  color: #b2bec3;
  font-size: 0.95rem;
}

.price-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #feca57;
}

/* Combo Section */
.combo-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.combo-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.combo-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.combo-card {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  transition: all 0.3s ease;
}

.combo-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
}

.combo-card-image {
  height: 200px;
  overflow: hidden;
}

.combo-card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.combo-card:hover .combo-card-image img {
  transform: scale(1.1);
}

.combo-card-content {
  padding: 25px;
}

.combo-card-content h3 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 15px;
}

.combo-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.combo-items span {
  color: #b2bec3;
  font-size: 0.95rem;
  position: relative;
  padding-left: 20px;
}

.combo-items span::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #48dbfb;
  font-weight: 700;
}

.combo-price {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.combo-price-old {
  color: #b2bec3;
  text-decoration: line-through;
  font-size: 1rem;
}

.combo-price-new {
  font-size: 1.5rem;
  font-weight: 700;
  color: #feca57;
}

.combo-save {
  color: #27ae60;
  font-size: 0.9rem;
  font-weight: 600;
}

/* Price Table Section */
.price-table-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.price-table-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.price-table-container {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  overflow: hidden;
}

.price-table {
  width: 100%;
  border-collapse: collapse;
  color: #fff;
}

.price-table th {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  padding: 18px 20px;
  text-align: left;
  font-weight: 700;
  font-size: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.price-table td {
  padding: 18px 20px;
  border-bottom: 1px solid rgba(72,219,251,0.1);
}

.price-table tr:hover {
  background: rgba(72,219,251,0.05);
}

.type-badge {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 0.9rem;
}

.price-value {
  font-weight: 700;
  color: #feca57;
  font-size: 1.1rem;
}

/* Notes Section */
.notes-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.notes-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
}

.note-item {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  display: flex;
  gap: 15px;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.note-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(72,219,251,0.2);
}

.note-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.note-content h4 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 8px;
}

.note-content p {
  color: #b2bec3;
  font-size: 0.95rem;
  line-height: 1.5;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .price-hero-content h1 {
    font-size: 2.5rem;
  }
  
  .price-hero-content p {
    font-size: 1.1rem;
  }
  
  .price-cards,
  .combo-cards,
  .notes-grid {
    grid-template-columns: 1fr;
  }
  
  .price-table-container {
    padding: 20px;
    overflow-x: auto;
  }
  
  .price-table th,
  .price-table td {
    padding: 12px 15px;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .price-hero {
    height: 300px;
  }
  
  .price-hero-content h1 {
    font-size: 2rem;
  }
  
  .price-card,
  .combo-card,
  .note-item {
    padding: 20px;
  }
}
</style> 