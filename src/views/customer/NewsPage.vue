<template>
    <Header 
    :is-logged-in="isLoggedIn"
    :user-info="userInfo"
    @show-auth-modal="handleShowAuthModal"
    @logout-success="handleLogoutSuccess"
  />
  <div class="news-page dark-mode">
    <div class="news-content">
      <div class="news-header">
        <h1 class="news-title"><span class="icon-news">📰</span> Tin tức</h1>
        <div class="news-subtitle">Cập nhật thông tin mới nhất về phim, sự kiện tại rạp</div>
      </div>
      <div class="news-grid">
        <div v-for="item in newsList" :key="item.id" class="news-card" @click="viewNews(item)">
          <div class="news-img-wrap">
            <img :src="item.image" :alt="item.title" class="news-img" />
          </div>
          <div class="news-info">
            <div class="news-meta">
              <span class="news-date">🗓 {{ item.date }}</span>
              <span v-if="item.category" class="news-category">#{{ item.category }}</span>
            </div>
            <div class="news-card-title">{{ item.title }}</div>
            <div class="news-summary">{{ item.summary }}</div>
            <button class="btn-news-detail" @click.stop="viewNews(item)">Xem chi tiết</button>
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

const newsList = ref([
  {
    id: 1,
    title: 'Ưu đãi tháng 7: Mua 1 tặng 1',
    image: 'https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=600&q=80',
    summary: 'Áp dụng cho vé 2D từ thứ 2 đến thứ 6. Số lượng có hạn!',
    date: '04/07/2025',
    category: 'Khuyến mãi',
    content: 'Chi tiết chương trình ưu đãi...'
  },
  {
    id: 2,
    title: 'Ra mắt phim bom tấn Godzilla x Kong',
    image: 'https://image.tmdb.org/t/p/w500/2vFuG6bWGyQUzYS9d69E5l85nIz.jpg',
    summary: 'Đặt vé sớm nhận quà tặng hấp dẫn. Số lượng có hạn!',
    date: '04/07/2025',
    category: 'Sự kiện',
    content: 'Chi tiết sự kiện ra mắt phim...'
  },
  {
    id: 3,
    title: 'Thành viên VIP: Tích điểm đổi quà',
    image: 'https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=600&q=80',
    summary: 'Tham gia chương trình khách hàng thân thiết để nhận nhiều ưu đãi.',
    date: '01/07/2025',
    category: 'Khách hàng',
    content: 'Chi tiết chương trình VIP...'
  },
  {
    id: 4,
    title: 'Lịch chiếu phim mới tháng 7',
    image: 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=600&q=80',
    summary: 'Cập nhật lịch chiếu các phim hot nhất tháng 7.',
    date: '01/07/2025',
    category: 'Lịch chiếu',
    content: 'Chi tiết lịch chiếu...'
  },
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

function viewNews(item) {
  alert(`Xem chi tiết tin: ${item.title}`)
}

// Load login state when component mounts
onMounted(() => {
  loadLoginState()
})
</script>

<style scoped>
.news-page.dark-mode {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  color: #fff;
  min-height: 100vh;
  padding-bottom: 40px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.main-header.dark {
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 68px;
}
.logo {
  font-size: 26px;
  font-weight: 900;
  color: #48dbfb;
  letter-spacing: 2px;
  text-shadow: 0 2px 12px #48dbfb33;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.header-menu {
  display: flex;
  align-items: center;
  gap: 38px;
}
.menu-link {
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  text-decoration: none;
  padding: 8px 0;
  position: relative;
  transition: color 0.2s;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.menu-link.active, .menu-link:hover {
  color: #48dbfb;
}
.menu-link.active::after, .menu-link:hover::after {
  width: 100%;
}
.menu-link::after {
  content: '';
  display: block;
  height: 2.5px;
  width: 0;
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  border-radius: 2px;
  transition: width 0.25s;
  position: absolute;
  left: 0;
  bottom: -2px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.hotline {
  color: #feca57;
  font-size: 16px;
  font-weight: 700;
}
.header-socials {
  display: flex;
  gap: 10px;
}
.header-social-icon {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px #48dbfb22;
  transition: transform 0.2s;
}
.header-social-icon:hover {
  transform: scale(1.12) rotate(-8deg);
}
.footer.dark {
  background: #18191a;
  color: #fff;
  padding: 36px 0 12px 0;
  font-size: 16px;
  box-shadow: 0 -2px 16px #23252633;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 0 32px;
}
.footer-col {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.footer-title {
  font-size: 22px;
  font-weight: 900;
  color: #48dbfb;
}
.footer-socials {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
.footer-social-icon {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px #48dbfb22;
  margin-right: 6px;
  transition: transform 0.2s;
}
.footer-social-icon:hover {
  transform: scale(1.18) rotate(-8deg);
}
.footer-copy {
  text-align: center;
  color: #b2bec3;
  font-size: 14px;
  margin-top: 24px;
  letter-spacing: 1px;
}
@media (max-width: 900px) {
  .header-container, .footer-content { padding: 0 10px; }
  .header-menu { gap: 18px; }
}
@media (max-width: 700px) {
  .header-container, .footer-content { flex-direction: column; align-items: flex-start; gap: 18px; padding: 0 10px; }
  .header-menu { flex-wrap: wrap; gap: 10px; }
}
.news-header {
  max-width: 1200px;
  margin: 0 auto 32px auto;
  padding: 150px 0 0 0;
  text-align: center;
}
.news-title {
  font-size: 38px;
  font-weight: 900;
  color: #48dbfb;
  margin-bottom: 10px;
  letter-spacing: 1.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}
.icon-news { font-size: 38px; }
.news-subtitle {
  color: #feca57;
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 0;
}
.news-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px 32px;
  padding: 0 16px;
}
.news-card {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(72,219,251,0.10), 0 1.5px 8px rgba(44,62,80,0.18);
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.news-card:hover {
  transform: translateY(-10px) scale(1.03);
  box-shadow: 0 12px 40px rgba(72,219,251,0.18), 0 4px 24px rgba(44,62,80,0.28);
  z-index: 2;
}
.news-img-wrap {
  width: 100%;
  aspect-ratio: 16/9;
  background: #232526;
  overflow: hidden;
}
.news-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0 0 0 0;
  transition: transform 0.3s;
}
.news-card:hover .news-img {
  transform: scale(1.06);
}
.news-info {
  padding: 22px 18px 18px 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}
.news-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #b2bec3;
  margin-bottom: 2px;
}
.news-category {
  color: #48dbfb;
  font-weight: 700;
}
.news-date { color: #feca57; }
.news-card-title {
  font-size: 20px;
  font-weight: 800;
  color: #48dbfb;
  margin-bottom: 2px;
  text-align: left;
  transition: text-decoration 0.2s;
  cursor: pointer;
}
.news-card-title:hover {
  text-decoration: underline;
}
.news-summary {
  font-size: 15px;
  color: #b2bec3;
  margin-bottom: 0;
  flex: 1;
}
.btn-news-detail {
  width: fit-content;
  background: linear-gradient(135deg, #2196f3 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 8px 22px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: all 0.2s;
  margin-top: 10px;
}
.btn-news-detail:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 6px 18px rgba(72,219,251,0.18);
}
@media (max-width: 1100px) { .news-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 700px) { .news-grid { grid-template-columns: 1fr; gap: 18px; } .news-header { padding: 24px 0 0 0; } }
</style> 