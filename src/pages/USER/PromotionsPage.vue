<template>
    <Header 
    :is-logged-in="isLoggedIn"
    :user-info="userInfo"
    @show-auth-modal="handleShowAuthModal"
    @logout-success="handleLogoutSuccess"
  />
  <div class="promotions-page">
    <!-- Hero Section -->
    <div class="promotions-hero">
      <img src="https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" alt="Promotions Banner" class="promotions-hero-img" />
      <div class="promotions-hero-content">
        <h1>Khuyến Mãi & Ưu Đãi</h1>
        <p>Săn ngay những ưu đãi hấp dẫn, voucher và quà tặng độc quyền!</p>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">50+</span>
            <span class="stat-label">Ưu đãi</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">10K+</span>
            <span class="stat-label">Khách hàng</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">24/7</span>
            <span class="stat-label">Hỗ trợ</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Featured Promotions -->
    <div class="featured-section">
      <h2>Khuyến Mãi Nổi Bật</h2>
      <div class="featured-promotions">
        <div class="featured-card main">
          <div class="featured-badge">HOT</div>
          <div class="featured-image">
            <img src="https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=600&q=80" alt="Featured Promotion" />
            <div class="featured-overlay">
              <div class="featured-content">
                <h3>Mua 1 Tặng 1 Vé 2D</h3>
                <p>Áp dụng từ thứ 2 đến thứ 6, số lượng có hạn!</p>
                <div class="featured-price">
                  <span class="price-old">140.000đ</span>
                  <span class="price-new">70.000đ</span>
                </div>
                <button class="btn-featured">Nhận Ngay</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- All Promotions -->
    <div class="promotions-section">
      <h2>Tất Cả Khuyến Mãi</h2>
      <div class="promotions-grid">
        <div v-for="promo in promotions" :key="promo.id" class="promotion-card">
          <div class="promotion-image">
            <img :src="promo.image" :alt="promo.title" />
            <div class="promotion-overlay">
              <div class="promotion-badge" :class="promo.type">{{ promo.badge }}</div>
              <button class="btn-quick-view">Xem Chi Tiết</button>
            </div>
          </div>
          <div class="promotion-content">
            <div class="promotion-header">
              <h3>{{ promo.title }}</h3>
              <div class="promotion-type" :class="promo.type">{{ promo.typeText }}</div>
            </div>
            <p class="promotion-desc">{{ promo.desc }}</p>
            <div class="promotion-details">
              <div class="detail-item">
                <span class="detail-icon">📅</span>
                <span>{{ promo.date }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-icon">🎯</span>
                <span>{{ promo.condition }}</span>
              </div>
            </div>
            <div class="promotion-footer">
              <div class="promotion-price">
                <span class="price-old">{{ promo.priceOld }}</span>
                <span class="price-new">{{ promo.priceNew }}</span>
              </div>
              <button class="btn-claim" @click="claimPromo(promo)">
                <span class="btn-text">Nhận Ưu Đãi</span>
                <span class="btn-icon">🎁</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Categories Section -->
    <div class="categories-section">
      <h2>Danh Mục Khuyến Mãi</h2>
      <div class="categories-grid">
        <div class="category-card">
          <div class="category-icon">🎬</div>
          <h3>Vé Phim</h3>
          <p>Ưu đãi vé phim các loại</p>
          <span class="category-count">12 khuyến mãi</span>
        </div>
        <div class="category-card">
          <div class="category-icon">🍿</div>
          <h3>Combo Ăn Uống</h3>
          <p>Combo bắp nước giá rẻ</p>
          <span class="category-count">8 khuyến mãi</span>
        </div>
        <div class="category-card">
          <div class="category-icon">🎁</div>
          <h3>Quà Tặng</h3>
          <p>Quà tặng độc quyền</p>
          <span class="category-count">5 khuyến mãi</span>
        </div>
        <div class="category-card">
          <div class="category-icon">💳</div>
          <h3>Thẻ Thành Viên</h3>
          <p>Ưu đãi cho thành viên</p>
          <span class="category-count">6 khuyến mãi</span>
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
import HomeFooter from '@/components/HomeFooter.vue'
import Header from '@/components/Header.vue'
import { ref, computed, onMounted } from 'vue'
import AuthModal from '@/components/AuthModal.vue'



const promotions = ref([
  {
    id: 1,
    title: 'Combo Bắp Nước Siêu Rẻ',
    desc: 'Mua vé online nhận ngay combo bắp nước chỉ 49K!',
    date: '01/07 - 15/07/2025',
    condition: 'Áp dụng cho vé online',
    priceOld: '89.000đ',
    priceNew: '49.000đ',
    image: 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=600&q=80',
    type: 'combo',
    typeText: 'Combo',
    badge: 'HOT'
  },
  {
    id: 2,
    title: 'Giảm 50% Vé 3D',
    desc: 'Giảm ngay 50% giá vé phim 3D cho học sinh, sinh viên!',
    date: '01/07 - 31/07/2025',
    condition: 'Xuất trình thẻ HSSV',
    priceOld: '120.000đ',
    priceNew: '60.000đ',
    image: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=600&q=80',
    type: 'ticket',
    typeText: 'Vé Phim',
    badge: '50% OFF'
  },
  {
    id: 3,
    title: 'Combo Gia Đình 4 Người',
    desc: 'Combo hoàn hảo cho gia đình: 4 vé + 2 bắp + 4 nước!',
    date: '01/07 - 20/07/2025',
    condition: 'Tối thiểu 4 vé',
    priceOld: '520.000đ',
    priceNew: '420.000đ',
    image: 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=600&q=80',
    type: 'combo',
    typeText: 'Combo',
    badge: 'SAVE 100K'
  },
  {
    id: 4,
    title: 'Tặng Voucher 100K',
    desc: 'Mua vé 4DX tặng ngay voucher 100K cho lần mua tiếp theo!',
    date: '01/07 - 25/07/2025',
    condition: 'Áp dụng cho vé 4DX',
    priceOld: '180.000đ',
    priceNew: '180.000đ + 100K',
    image: 'https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=600&q=80',
    type: 'gift',
    typeText: 'Quà Tặng',
    badge: 'FREE 100K'
  },
  {
    id: 5,
    title: 'Thẻ VIP Giảm 30%',
    desc: 'Thành viên VIP được giảm 30% tất cả dịch vụ!',
    date: '01/07 - 31/12/2025',
    condition: 'Thành viên VIP',
    priceOld: 'Giá gốc',
    priceNew: 'Giảm 30%',
    image: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=600&q=80',
    type: 'vip',
    typeText: 'VIP',
    badge: 'VIP ONLY'
  },
  {
    id: 6,
    title: 'Happy Hours - Giảm 25%',
    desc: 'Giảm 25% tất cả vé phim từ 14:00 - 17:00 các ngày trong tuần!',
    date: 'Thứ 2 - Thứ 6',
    condition: '14:00 - 17:00',
    priceOld: 'Giá gốc',
    priceNew: 'Giảm 25%',
    image: 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=600&q=80',
    type: 'ticket',
    typeText: 'Vé Phim',
    badge: 'HAPPY HOURS'
  }
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

function claimPromo(promo) {
  alert(`Bạn đã nhận ưu đãi: ${promo.title}`)
}

// Load login state when component mounts
onMounted(() => {
  loadLoginState()
})
</script>

<style scoped>
.promotions-page {
  min-height: 100vh;
  background: #18191a;
  color: #fff;
}

/* Hero Section */
.promotions-hero {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  margin-bottom: 60px;
}

.promotions-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.6);
}

.promotions-hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
  z-index: 2;
  width: 100%;
  max-width: 800px;
  padding: 0 20px;
}

.promotions-hero-content h1 {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 16px;
  color: #48dbfb;
  text-shadow: 0 4px 20px rgba(0,0,0,0.8);
}

.promotions-hero-content p {
  font-size: 1.3rem;
  color: #feca57;
  margin-bottom: 30px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 900;
  color: #48dbfb;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #b2bec3;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Featured Section */
.featured-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.featured-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.featured-promotions {
  display: flex;
  justify-content: center;
}

.featured-card.main {
  position: relative;
  width: 100%;
  max-width: 800px;
  height: 400px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
}

.featured-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.9rem;
  z-index: 3;
}

.featured-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.featured-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(72,219,251,0.9) 0%, rgba(102,126,234,0.9) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.featured-content {
  text-align: center;
  color: #fff;
}

.featured-content h3 {
  font-size: 2.5rem;
  font-weight: 900;
  margin-bottom: 16px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

.featured-content p {
  font-size: 1.2rem;
  margin-bottom: 20px;
  opacity: 0.9;
}

.featured-price {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}

.price-old {
  color: rgba(255,255,255,0.7);
  text-decoration: line-through;
  font-size: 1.2rem;
}

.price-new {
  font-size: 2rem;
  font-weight: 900;
  color: #feca57;
}

.btn-featured {
  background: #fff;
  color: #48dbfb;
  border: none;
  padding: 15px 30px;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-featured:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

/* Promotions Section */
.promotions-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.promotions-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.promotions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.promotion-card {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  transition: all 0.3s ease;
}

.promotion-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
}

.promotion-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.promotion-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.promotion-card:hover .promotion-image img {
  transform: scale(1.1);
}

.promotion-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.promotion-card:hover .promotion-overlay {
  opacity: 1;
}

.promotion-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 700;
  color: #fff;
}

.promotion-badge.combo { background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%); }
.promotion-badge.ticket { background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%); }
.promotion-badge.gift { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); }
.promotion-badge.vip { background: linear-gradient(135deg, #a55eea 0%, #fd79a8 100%); }

.btn-quick-view {
  background: #48dbfb;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-quick-view:hover {
  background: #667eea;
  transform: scale(1.05);
}

.promotion-content {
  padding: 25px;
}

.promotion-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.promotion-header h3 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #48dbfb;
  margin: 0;
  flex: 1;
}

.promotion-type {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 700;
  color: #fff;
  text-transform: uppercase;
}

.promotion-type.combo { background: rgba(72,219,251,0.2); }
.promotion-type.ticket { background: rgba(254,202,87,0.2); }
.promotion-type.gift { background: rgba(255,107,107,0.2); }
.promotion-type.vip { background: rgba(165,94,234,0.2); }

.promotion-desc {
  color: #b2bec3;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 15px;
}

.promotion-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #b2bec3;
}

.detail-icon {
  font-size: 1rem;
}

.promotion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.promotion-price {
  display: flex;
  flex-direction: column;
}

.price-old {
  color: #b2bec3;
  text-decoration: line-through;
  font-size: 0.9rem;
}

.price-new {
  font-size: 1.2rem;
  font-weight: 700;
  color: #feca57;
}

.btn-claim {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 15px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-claim:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(72,219,251,0.3);
}

.btn-icon {
  font-size: 1.1rem;
}

/* Categories Section */
.categories-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.categories-section h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 40px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.category-card {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 16px;
  padding: 30px 25px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  transition: all 0.3s ease;
}

.category-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
}

.category-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.category-card h3 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 10px;
}

.category-card p {
  color: #b2bec3;
  font-size: 0.95rem;
  margin-bottom: 15px;
}

.category-count {
  color: #feca57;
  font-size: 0.9rem;
  font-weight: 600;
}



/* Responsive */
@media (max-width: 768px) {
  .promotions-hero-content h1 {
    font-size: 2.5rem;
  }
  
  .promotions-hero-content p {
    font-size: 1.1rem;
  }
  
  .hero-stats {
    gap: 20px;
  }
  
  .featured-card.main {
    height: 300px;
  }
  
  .featured-content h3 {
    font-size: 2rem;
  }
  
  .promotions-grid {
    grid-template-columns: 1fr;
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  

}

@media (max-width: 480px) {
  .promotions-hero {
    height: 400px;
  }
  
  .promotions-hero-content h1 {
    font-size: 2rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
}
</style> 