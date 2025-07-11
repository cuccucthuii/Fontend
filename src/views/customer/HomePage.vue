<template>
  <div class="homepage dark-mode">
    <!-- Header lớn chuyên nghiệp -->
    <header class="main-header full-header dark">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/home" class="logo">🎬 DEV CINEMA</router-link>
        </div>
        <nav class="header-menu">
          <router-link to="/" class="menu-link" exact-active-class="active">Trang chủ</router-link>
          <router-link to="/showtimes" class="menu-link" active-class="active">Lịch chiếu</router-link>
          <router-link to="/news" class="menu-link" active-class="active">Tin tức</router-link>
          <router-link to="/promotions" class="menu-link" active-class="active">Khuyến mãi</router-link>
          <router-link to="/prices" class="menu-link" active-class="active">Giá vé</router-link>
          <router-link to="/about" class="menu-link" active-class="active">Giới thiệu</router-link>
          <router-link to="/contact" class="menu-link" active-class="active">Liên hệ</router-link>
        </nav>
        <div class="header-right">
          <span class="hotline">Hotline: <a href="tel:0123456789">0123 456 789</a></span>
          <div class="header-socials">
            <a href="https://facebook.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png" class="header-social-icon" /></a>
            <a href="https://instagram.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" class="header-social-icon" /></a>
            <a href="https://tiktok.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6f/Tiktok-logo.png" class="header-social-icon" /></a>
          </div>
          <!-- User Menu -->
          <div class="user-menu" v-if="isLoggedIn">
            <div class="user-info">
              <span class="user-name">{{ userInfo.username }}</span>
              <button class="logout-btn" @click="handleLogout">
                <span class="logout-icon">🚪</span>
                Đăng xuất
              </button>
            </div>
          </div>
          <div class="user-menu" v-else>
            <router-link to="/login" class="login-btn">
              <span class="login-icon">👤</span>
              Đăng nhập
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Banner lớn với slider phim hot -->
    <section class="hero-banner dark">
      <div class="banner-slider">
        <transition-group name="fade" tag="div">
          <div v-for="(movie, idx) in hotMovies" :key="movie.id" v-show="idx === bannerIndex" class="banner-slide active">
            <img :src="movie.poster" class="banner-img" />
            <div class="banner-overlay-gradient"></div>
            <div class="banner-content">
              <h1 class="banner-title">{{ movie.title }}</h1>
              <p class="banner-slogan">{{ movie.genre }}</p>
              <button class="btn-primary big" @click="goToBooking">Đặt vé ngay</button>
            </div>
          </div>
        </transition-group>
        <button class="banner-nav left" @click="prevBanner">‹</button>
        <button class="banner-nav right" @click="nextBanner">›</button>
      </div>
    </section>

    <!-- Tabs phim lớn -->
    <section class="movie-section dark">
      <div class="movie-tabs-large">
        <button
          v-for="t in movieTabs"
          :key="t.key"
          :class="['movie-tab-large', {active: tab === t.key}]"
          @click="tab = t.key"
        >
          {{ t.label }}
        </button>
      </div>
      <div v-if="loadingMovies" class="movie-loading">Đang tải phim...</div>
      <div v-else-if="errorMovies" class="movie-error">{{ errorMovies }}</div>
      <div v-else class="movie-grid-mockup">
        <div v-for="movie in moviesToShow" :key="movie.idPhim || movie.id" class="movie-card-mockup dark">
          <div class="movie-poster-wrap-mockup" @click="openMovieModal(movie)" style="cursor:pointer;">
            <img :src="getPosterUrl(movie.posterUrl)" :alt="movie.tenPhim" class="movie-poster-mockup" />
            <div class="badge-age-mockup" :style="{background: getAgeBadge(movie).color}">{{ getAgeBadge(movie).text }}</div>
            <div v-if="movie.isHot" class="badge-hot-mockup">HOT</div>
          </div>
          <div class="movie-info-mockup">
            <div class="movie-title-mockup">{{ movie.tenPhim }}</div>
            <div class="movie-meta-mockup">Thể loại: {{ movie.theLoai ? movie.theLoai.join(', ') : '-' }}</div>
            <div class="movie-meta-mockup">Ngày khởi chiếu: {{ movie.ngayPhatHanh || '-' }}</div>
            <div class="movie-meta-mockup movie-duration-mobile-hide">Thời lượng: {{ movie.thoiLuong ? movie.thoiLuong + ' phút' : '-' }}</div>
            <button class="btn-buy-mockup" @click="goToBooking">
              <span class="icon-ticket">🎟️</span> MUA VÉ
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer dark">
      <div class="footer-content">
        <div class="footer-col brand">
          <img src="/vite.svg" alt="DEV CINEMA" class="footer-logo" />
          <div class="footer-title">DEV CINEMA</div>
          <div class="footer-slogan">Trải nghiệm điện ảnh đỉnh cao, đặt vé siêu tốc!</div>
        </div>
        <div class="footer-col">
          <div class="footer-title">Liên kết nhanh</div>
          <a href="/">Trang chủ</a>
          <a href="/showtimes">Lịch chiếu</a>
          <a href="/news">Tin tức</a>
          <a href="/promotions">Khuyến mãi</a>
          <a href="/contact">Liên hệ</a>
        </div>
        <div class="footer-col">
          <div class="footer-title">Liên hệ</div>
          <div>Hotline: <a href="tel:0123456789">0123 456 789</a></div>
          <div>Email: <a href="mailto:devcinema@gmail.com">devcinema@gmail.com</a></div>
          <div class="footer-socials">
            <a href="https://facebook.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png" alt="Facebook" style="width:20px;height:20px;" /></a>
            <a href="https://instagram.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" alt="Instagram" style="width:20px;height:20px;" /></a>
            <a href="https://tiktok.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6f/Tiktok-logo.png" alt="TikTok" style="width:20px;height:20px;" /></a>
          </div>
        </div>
      </div>
      <div class="footer-copy">
        © 2024 DEV CINEMA. All rights reserved. | <router-link to="/terms">Điều khoản</router-link> | <router-link to="/privacy">Chính sách bảo mật</router-link>
      </div>
    </footer>
    <MovieDetailModal :movie="selectedMovie" :visible="showModal" @close="closeMovieModal" v-if="selectedMovie" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMovies } from '../../services/movieService'
import { logoutUser } from '../../services/userService'
import MovieDetailModal from '../../components/MovieDetailModal.vue'

const hotMovies = [
  { id: 1, title: 'Avengers: Endgame', genre: 'Hành động, Viễn tưởng', poster: 'https://image.tmdb.org/t/p/w500/ulzhLuWrPK07P1YkdWQLZnQh1JL.jpg', isHot: true },
  { id: 2, title: 'Dune: Part Two', genre: 'Phiêu lưu, Khoa học viễn tưởng', poster: 'https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg', isHot: true },
  { id: 3, title: 'Godzilla x Kong', genre: 'Hành động, Quái vật', poster: 'https://image.tmdb.org/t/p/w500/2vFuG6bWGyQUzYS9d69E5l85nIz.jpg', isHot: true },
]
const hoveredMovie = ref(null)
const bannerIndex = ref(0)
let bannerTimer = null
function nextBanner() {
  bannerIndex.value = (bannerIndex.value + 1) % hotMovies.length
}
function prevBanner() {
  bannerIndex.value = (bannerIndex.value - 1 + hotMovies.length) % hotMovies.length
}
function startBannerAuto() {
  bannerTimer = setInterval(nextBanner, 4000)
}
function stopBannerAuto() {
  if (bannerTimer) clearInterval(bannerTimer)
}
onMounted(() => { startBannerAuto() })

const router = useRouter()

// User authentication state
const isLoggedIn = ref(false)
const userInfo = ref({})

// Check login status on mount
onMounted(() => {
  const loginStatus = localStorage.getItem('isLoggedIn')
  const userInfoStr = localStorage.getItem('userInfo')
  
  if (loginStatus === 'true' && userInfoStr) {
    try {
      isLoggedIn.value = true
      userInfo.value = JSON.parse(userInfoStr)
    } catch (e) {
      console.error('Error parsing userInfo:', e)
      isLoggedIn.value = false
    }
  }
})

// Logout function
async function handleLogout() {
  try {
    await logoutUser()
  } catch (error) {
    console.log('Logout error:', error)
  } finally {
    // Clear localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
    
    // Update state
    isLoggedIn.value = false
    userInfo.value = {}
    
    // Redirect to login
    router.push('/login')
  }
}

function goToBooking() {
  router.push('/booking')
}

const movies = ref([])
const loadingMovies = ref(true)
const errorMovies = ref('')
onMounted(async () => {
  loadingMovies.value = true
  try {
    const res = await fetchMovies()
    movies.value = res.data
    errorMovies.value = ''
  } catch (e) {
    errorMovies.value = 'Không thể tải danh sách phim.'
  } finally {
    loadingMovies.value = false
  }
})
const nowShowing = computed(() => movies.value.filter(m => {
  if (!m.status) return true;
  const s = m.status.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
  return s.includes('dang chieu');
}))
const comingSoon = computed(() => movies.value.filter(m => {
  if (!m.status) return true;
  const s = m.status.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
  return s.includes('sap chieu');
}))
const specialShow = computed(() => movies.value.filter(m => {
  if (!m.status) return false;
  const s = m.status.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
  return s.includes('suat chieu');
}))
function getAgeBadge(movie) {
  if (movie.age === 'P') return { text: 'P', color: '#27ae60' }
  if (movie.age === 'T13') return { text: 'T13', color: '#3498db' }
  if (movie.age === 'T16') return { text: 'T16', color: '#e17055' }
  if (movie.age === 'T18') return { text: 'T18', color: '#d63031' }
  return { text: '', color: '#636e72' }
}

const movieTabs = [
  { key: 'nowShowing', label: 'PHIM ĐANG CHIẾU' },
  { key: 'comingSoon', label: 'PHIM SẮP CHIẾU' },
  { key: 'specialShow', label: 'SUẤT ĐẶC BIỆT' },
]
const tab = ref('nowShowing')
const moviesToShow = computed(() => {
  if (tab.value === 'nowShowing') return nowShowing.value
  if (tab.value === 'comingSoon') return comingSoon.value
  if (tab.value === 'specialShow') return specialShow.value
  return []
})

function getPosterUrl(url) {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return 'http://localhost:8080' + url;
}

const selectedMovie = ref(null)
const showModal = ref(false)
function openMovieModal(movie) {
  selectedMovie.value = movie
  showModal.value = true
}
function closeMovieModal() {
  showModal.value = false
}
</script>

<style scoped>
.homepage.dark-mode {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  color: #fff;
  width: 100%;
  box-sizing: border-box;
  padding-left: 16px;
  padding-right: 16px;
}

@media (min-width: 900px) {
  .homepage.dark-mode {
    padding-left: 40px;
    padding-right: 40px;
  }
}

.main-header.dark {
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 64px;
  box-sizing: border-box;
}
.logo {
  font-size: 22px;
  font-weight: 800;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
}
.nav-menu {
  display: flex;
  gap: 24px;
  align-items: center;
}
.nav-link {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  padding: 8px 0;
  transition: color 0.2s, border-bottom 0.2s;
  border-bottom: 2px solid transparent;
}
.nav-link.active, .nav-link:hover {
  color: #48dbfb;
  border-bottom: 2px solid #48dbfb;
}
.btn-login {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: all 0.3s;
}
.btn-login:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  transform: translateY(-2px) scale(1.04);
}
.hero-banner.dark {
  width: 100%;
  margin: 0 auto 32px auto;
  position: relative;
  min-height: 520px;
}
.banner-slider {
  position: relative;
  width: 100%;
  height: 600px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.banner-slide {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.7s;
  pointer-events: none;
}
.banner-slide.active {
  opacity: 1;
  pointer-events: auto;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-overlay-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(0,0,0,0.38) 60%, rgba(44,62,80,0.18) 100%);
  z-index: 2;
}
.banner-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 3;
  text-align: center;
  color: #fff;
  width: 100%;
  max-width: 600px;
  padding: 0 24px;
}
.banner-title {
  font-size: 52px;
  font-weight: 900;
  margin-bottom: 16px;
  letter-spacing: 2.5px;
  background: linear-gradient(90deg, #48dbfb 10%, #fff 60%, #667eea 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 6px 32px rgba(0,0,0,0.45), 0 1px 0 #fff, 0 0 8px #48dbfb;
  filter: drop-shadow(0 2px 12px #232526);
}
.banner-slogan {
  font-size: 26px;
  margin-bottom: 32px;
  font-weight: 600;
  color: #feca57;
  text-shadow: 0 2px 12px rgba(0,0,0,0.35), 0 1px 0 #fff;
}
.btn-primary.big {
  font-size: 20px;
  padding: 16px 38px;
  border-radius: 16px;
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: all 0.3s;
}
.btn-primary.big:hover {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  transform: scale(1.04);
}
.banner-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(44,62,80,0.45);
  color: #fff;
  border: none;
  font-size: 2.2em;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  cursor: pointer;
  z-index: 4;
  transition: background 0.2s;
  box-shadow: 0 2px 8px rgba(44,62,80,0.18);
}
.banner-nav.left { left: 32px; }
.banner-nav.right { right: 32px; }
.banner-nav:hover { background: rgba(44,62,80,0.7); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.7s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.movie-section.dark {
  background: transparent;
  margin: 0 auto 32px auto;
  max-width: 1200px;
  padding: 0 16px;
}
.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #48dbfb;
  margin: 40px 0 24px 0;
  text-align: left;
  letter-spacing: 1px;
}
.movie-carousel {
  overflow-x: auto;
  padding-bottom: 8px;
}
.carousel-track {
  display: flex;
  gap: 32px;
  min-width: 100%;
}
.movie-card-pro.dark {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(72,219,251,0.10), 0 1.5px 8px rgba(44,62,80,0.18);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  min-width: 220px;
  max-width: 240px;
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.movie-card-pro.dark:hover {
  transform: scale(1.07);
  box-shadow: 0 8px 32px rgba(72,219,251,0.18), 0 2.5px 16px rgba(44,62,80,0.28);
  z-index: 2;
}
.movie-hover-effect {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(72,219,251,0.12) 0%, rgba(102,126,234,0.10) 100%);
  pointer-events: none;
  border-radius: 18px;
  z-index: 3;
  animation: hoverGlow 0.5s;
}
@keyframes hoverGlow {
  from { opacity: 0; }
  to { opacity: 1; }
}
.movie-poster-wrap-pro {
  position: relative;
  width: 100%;
  aspect-ratio: 2/3;
  background: #232526;
  display: flex;
  align-items: center;
  justify-content: center;
}
.movie-poster-pro {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.3s;
}
.movie-card-pro.dark:hover .movie-poster-pro {
  transform: scale(1.06);
}
.badge-age {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(44,62,80,0.10);
}
.badge-hot {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff7675 0%, #fd79a8 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 10px;
  z-index: 2;
}
.badge-special {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 10px;
  z-index: 2;
}
.movie-info-pro {
  width: 100%;
  padding: 16px 12px 18px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.movie-title-pro {
  font-size: 17px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 6px;
  text-align: center;
}
.movie-meta-pro {
  font-size: 14px;
  color: #b2bec3;
  margin-bottom: 14px;
  text-align: center;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
}
.btn-buy-pro {
  width: 100%;
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: all 0.3s;
  margin-top: 4px;
}
.btn-buy-pro:hover {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  transform: translateY(-2px) scale(1.04);
}
.footer.dark {
  background: linear-gradient(135deg, #18191a 60%, #232526 100%);
  color: #fff;
  padding: 48px 0 18px 0;
  width: 100%;
  margin-top: auto;
  box-shadow: 0 -2px 24px rgba(44,62,80,0.18);
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 32px;
  padding: 0 24px;
}
.footer-col {
  flex: 1 1 220px;
  min-width: 180px;
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.footer-col.brand {
  align-items: flex-start;
}
.footer-logo {
  width: 48px;
  height: 48px;
  margin-bottom: 8px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
}
.footer-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #48dbfb;
  letter-spacing: 1px;
}
.footer-slogan {
  font-size: 15px;
  color: #feca57;
  margin-bottom: 8px;
}
.footer-col a {
  color: #b2bec3;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.footer-col a:hover {
  color: #48dbfb;
  text-decoration: underline;
}
.footer-socials {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.footer-social-icon {
  width: 36px;
  height: 36px;
  background: #232526;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  transition: background 0.2s, color 0.2s;
  box-shadow: 0 2px 8px rgba(72,219,251,0.10);
}
.footer-social-icon:hover {
  background: #48dbfb;
  color: #232526;
}
.footer-copy {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #b2bec3;
  margin-top: 18px;
}
.footer-copy a {
  color: #48dbfb;
  text-decoration: none;
  margin: 0 6px;
}
.footer-copy a:hover {
  text-decoration: underline;
}
@media (max-width: 1200px) { .carousel-track { gap: 18px; } .footer-content { gap: 18px; } }
@media (max-width: 900px) { .carousel-track { gap: 12px; } .footer-content { flex-direction: column; align-items: center; gap: 12px; } .banner-slider { height: 260px; } .banner-title { font-size: 28px; } .banner-slogan { font-size: 17px; } }
@media (max-width: 600px) { .carousel-track { gap: 8px; } .footer-col { min-width: 100px; } .banner-slider { height: 140px; } .banner-title { font-size: 16px; } .banner-slogan { font-size: 12px; } }
.full-header {
  width: 100%;
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 72px;
  box-sizing: border-box;
}
.header-left .logo {
  font-size: 26px;
  font-weight: 900;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
}
.header-menu {
  display: flex;
  gap: 32px;
  align-items: center;
}
.menu-link {
  color: #fff;
  text-decoration: none;
  font-size: 17px;
  font-weight: 700;
  padding: 8px 0;
  transition: color 0.2s, border-bottom 0.2s;
  border-bottom: 2px solid transparent;
  letter-spacing: 0.5px;
}
.menu-link.active, .menu-link:hover {
  color: #48dbfb;
  border-bottom: 2px solid #48dbfb;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.hotline {
  color: #feca57;
  font-weight: 700;
  font-size: 15px;
}
.hotline a { color: #feca57; text-decoration: none; }
.header-socials {
  display: flex;
  gap: 10px;
}
.header-social-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #fff;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: filter 0.2s;
}
.header-social-icon:hover { filter: brightness(0.8); }
@media (max-width: 1100px) {
  .header-menu { gap: 16px; }
  .header-container { padding: 0 8px; }
}
@media (max-width: 900px) {
  .header-container { flex-direction: column; height: auto; gap: 8px; padding: 0 2px; }
  .header-menu { flex-wrap: wrap; gap: 8px; justify-content: center; }
  .header-left .logo { font-size: 18px; }
}
.movie-loading, .movie-error {
  color: #feca57;
  text-align: center;
  font-size: 18px;
  margin: 32px 0;
}
@media (max-width: 900px) { .movie-grid { gap: 16px; } }
@media (max-width: 600px) { .movie-grid { gap: 8px; grid-template-columns: 1fr; } }
.movie-grid-mockup {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 72px 56px;
  padding: 0 40px;
}

.movie-card-mockup.dark {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 22px;
  box-shadow: 0 8px 32px rgba(72,219,251,0.10), 0 2px 12px rgba(44,62,80,0.18);
  overflow: hidden;
  transition: box-shadow 0.25s, transform 0.25s;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  min-width: 0;
  max-width: 320px;
  margin: 0 auto;
  background-clip: padding-box;
}
.movie-card-mockup.dark:hover {
  transform: translateY(-12px) scale(1.05);
  box-shadow: 0 20px 48px rgba(72,219,251,0.18), 0 8px 32px rgba(44,62,80,0.28);
  z-index: 3;
}
.movie-poster-wrap-mockup {
  position: relative;
  width: 100%;
  aspect-ratio: 3/4;
  min-height: 320px;
  max-height: 420px;
  background: #232526;
  display: flex;
  align-items: center;
  justify-content: center;
}
.movie-poster-mockup {
  width: 100%;
  height: 100%;
  min-height: 320px;
  max-height: 420px;
  object-fit: cover;
  border-radius: 18px;
  transition: transform 0.3s;
  box-shadow: 0 2px 12px rgba(44,62,80,0.12);
}
.movie-card-mockup.dark:hover .movie-poster-mockup {
  transform: scale(1.06);
  box-shadow: 0 8px 32px rgba(72,219,251,0.18);
}
.badge-age-mockup {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(44,62,80,0.10);
  border: 2px solid #fff2;
}
.badge-hot-mockup {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff7675 0%, #fd79a8 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 12px;
  z-index: 2;
  border: 2px solid #fff2;
}
.movie-info-mockup {
  width: 100%;
  padding: 18px 14px 18px 14px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}
.movie-title-mockup {
  font-size: 18px;
  font-weight: 900;
  color: #2196f3;
  margin-bottom: 2px;
  text-align: left;
  transition: text-decoration 0.2s;
  cursor: pointer;
}
.movie-title-mockup:hover {
  text-decoration: underline;
}
.movie-meta-mockup {
  font-size: 14px;
  color: #b2bec3;
  margin-bottom: 0;
}
.btn-buy-mockup {
  width: 100%;
  background: linear-gradient(135deg, #2196f3 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: all 0.2s;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-buy-mockup:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 6px 18px rgba(72,219,251,0.18);
}
.icon-ticket {
  font-size: 18px;
  margin-right: 2px;
}
.movie-loading, .movie-error {
  color: #feca57;
  text-align: center;
  font-size: 18px;
  margin: 32px 0;
}
@media (max-width: 1200px) { .movie-grid-mockup { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) { .movie-grid-mockup { gap: 24px; grid-template-columns: 1fr; padding: 0 8px; } }
@media (max-width: 600px) { .movie-grid-mockup { gap: 8px; grid-template-columns: 1fr; padding: 0 4px; } }

.movie-tabs-large {
  display: flex;
  gap: 32px;
  justify-content: center;
  margin-bottom: 32px;
  border-bottom: 2px solid #e0e0e0;
  background: transparent;
}
.movie-tab-large {
  background: none;
  border: none;
  color: #fff;
  font-size: 2.1rem;
  font-weight: 800;
  letter-spacing: 2px;
  padding: 18px 32px 12px 32px;
  text-transform: uppercase;
  cursor: pointer;
  transition: color 0.2s;
  position: relative;
  outline: none;
}
.movie-tab-large.active {
  color: #48dbfb;
}
.movie-tab-large.active::after {
  content: '';
  display: block;
  height: 4px;
  width: 100%;
  background: linear-gradient(90deg, #1976d2 0%, #48dbfb 100%);
  border-radius: 2px;
  position: absolute;
  left: 0;
  bottom: -2px;
  transition: width 0.3s;
}
</style>

<style>
html {
  overflow-y: scroll !important;
  height: 100%;
}

body {
  overflow-y: scroll !important;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  height: auto;
}

#app {
  min-height: 100vh;
  height: auto;
}

/* User Menu Styles */
.user-menu {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.user-info {
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

.login-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #2196f3, #48dbfb);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}

.login-btn:hover {
  background: linear-gradient(135deg, #1976d2, #2196f3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}

.login-icon {
  font-size: 14px;
}

/* Responsive for user menu */
@media (max-width: 768px) {
  .user-menu {
    margin-left: 10px;
  }
  
  .user-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .user-name {
    font-size: 12px;
    padding: 6px 8px;
  }
  
  .logout-btn, .login-btn {
    font-size: 12px;
    padding: 6px 8px;
  }
}

@media (max-width: 600px) {
  .movie-duration-mobile-hide {
    display: none;
  }
}
</style>
