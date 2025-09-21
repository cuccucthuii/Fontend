<template>
  <Header 
    :is-logged-in="isLoggedIn"
    :user-info="userInfo"
    @show-auth-modal="handleShowAuthModal"
    @logout-success="handleLogoutSuccess"
  />
  <div class="showtimes-page">
    <div class="showtimes-hero">
      <img src="https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=1200&q=80" alt="Showtimes Banner" class="showtimes-hero-img" />
      <div class="showtimes-hero-content">
        <h1>Lịch Chiếu Phim</h1>
        <p>Chọn rạp, ngày và phim để xem lịch chiếu chi tiết và đặt vé nhanh chóng!</p>
      </div>
    </div>
    <div class="showtimes-filter">
      <button class="btn-cinema-picker" @click="openCinemaModal" @mousedown="console.log('Button mousedown')" @mouseup="console.log('Button mouseup')">
        {{ selectedCinemaName || 'Tất cả rạp' }}
      </button>
      <input type="date" v-model="selectedDate" />
      <input type="text" v-model="search" placeholder="Tìm phim..." />
    </div>
    <div v-if="loadingMovies" class="showtimes-loading">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách phim...</p>
    </div>
    <div v-else-if="filteredMovies.length === 0" class="showtimes-empty">
      <p>Không có phim nào phù hợp với bộ lọc.</p>
    </div>
    <div v-else class="showtimes-list">
      <div v-for="movie in filteredMovies" :key="movie.idPhim" class="showtimes-movie-card">
        <img :src="getPosterUrl(movie.posterUrl)" :alt="movie.tenPhim" class="showtimes-movie-img" @click="goToMovieDetail(movie)" style="cursor: pointer;" />
        <div class="showtimes-movie-info">
          <h2 @click="goToMovieDetail(movie)" style="cursor: pointer;">{{ movie.tenPhim }}</h2>
          <div class="showtimes-movie-meta">
            {{ movie.theLoai ? movie.theLoai.join(', ') : 'Chưa phân loại' }} | 
            {{ movie.thoiLuong ? movie.thoiLuong + ' phút' : 'Chưa cập nhật' }}
          </div>
          <div class="showtimes-movie-status" :class="getStatusClass(movie.trangThai)">
            {{ formatStatus(movie.trangThai) }}
          </div>
          <button class="btn-book" @click="bookMovie(movie)">Đặt vé</button>
        </div>
      </div>
    </div>
  </div>
  <HomeFooter />
  <CinemaSelectModal
    :visible="showCinemaModal"
    mode="showtime"
    @close="showCinemaModal = false"
    @cinema-selected="onCinemaSelected"
    @showtime-flow="onCinemaSelected"
  />
  
  <ShowtimeModal
    :visible="showShowtimeModal"
    :movie="selectedMovie"
    :cinema="selectedCinemaObj"
    @close="showShowtimeModal = false"
    @selectShowtime="onShowtimeSelected"
  />

  <!-- Auth Modal -->
  <AuthModal 
    :show="showAuthModal"
    @close="showAuthModal = false"
    @login-success="handleLoginSuccess"
  />
</template>

<script setup>
import HomeFooter from '../../components/layout/HomeFooter.vue'
import Header from '../../components/layout/Header.vue'
import CinemaSelectModal from '../../components/modals/CinemaSelectModal.vue'
import ShowtimeModal from '../../components/modals/ShowtimeModal.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMovies } from '../../services/movieService'
import { fetchBranches } from '../../services/branchService'
import { API_BASE_URL } from '@/services/api'
import AuthModal from '../../components/modals/AuthModal.vue'

const branches = ref([])
const cinemas = ref([])
const selectedCinema = ref('')
const selectedCinemaObj = ref(null)
const showCinemaModal = ref(false)
const showShowtimeModal = ref(false)
const selectedMovie = ref(null)
const selectedCinemaName = computed(() => selectedCinemaObj.value?.tenRapChieu || selectedCinemaObj.value?.tenRap || selectedCinema.value || '')

function openCinemaModal(){ 
  console.log('🎬 Opening cinema modal...')
  showCinemaModal.value = true 
  console.log('🎬 showCinemaModal.value:', showCinemaModal.value)
}
function onCinemaSelected(cinema){ 
  selectedCinemaObj.value = cinema; 
  showCinemaModal.value = false
  // Mở modal lịch chiếu sau khi chọn rạp
  if (selectedMovie.value) {
    showShowtimeModal.value = true
  }
}

function onShowtimeSelected(showtimeData) {
  console.log('Selected showtime:', showtimeData)
  // Chuyển đến trang booking với thông tin đã chọn
  router.push({ 
    path: '/booking', 
    query: { 
      movieId: String(showtimeData.movie.idPhim || showtimeData.movie.id),
      cinemaId: String(showtimeData.cinema.idRapChieu || showtimeData.cinema.id),
      date: showtimeData.date,
      time: showtimeData.time,
      scheduleId: String(showtimeData.scheduleId)
    }
  })
}
const selectedDate = ref('')
const search = ref('')

const router = useRouter()
const movies = ref([])
const loadingMovies = ref(true)
const loadingBranches = ref(true)

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

// Lắng nghe thay đổi localStorage để cập nhật trạng thái đăng nhập
window.addEventListener('storage', (e) => {
  if (e.key === 'token' || e.key === 'userInfo') {
    loadLoginState()
  }
})

// Lắng nghe custom event từ AuthModal
window.addEventListener('login-success', () => {
  loadLoginState()
})

window.addEventListener('logout-success', () => {
  loadLoginState()
})

// Hàm load movies
async function loadMovies() {
  loadingMovies.value = true
  try {
    const res = await fetchMovies()
    movies.value = res.data
  } catch (error) {
    console.error('Lỗi tải phim:', error)
  } finally {
    loadingMovies.value = false
  }
}

async function loadBranches() {
  loadingBranches.value = true
  try {
    const res = await fetchBranches()
    const list = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
    branches.value = list
    cinemas.value = list.map(b => b.tenRap || b.ten || b.name).filter(Boolean)
  } catch (error) {
    console.error('Lỗi tải rạp chiếu:', error)
    branches.value = []
    cinemas.value = []
  } finally {
    loadingBranches.value = false
  }
}

onMounted(async () => {
  loadLoginState() // Load login state first
  await Promise.all([loadMovies(), loadBranches()])
  
  // Lắng nghe sự kiện khi có thay đổi phim từ admin
  window.addEventListener('moviesUpdated', async () => {
    console.log('🔄 Phát hiện thay đổi phim, đang refresh dữ liệu...')
    await loadMovies()
  })
})

// Logic xác định phim đang chiếu giống HomePage
const DEFAULT_SHOWING_DAYS = 30

const isMovieAvailableForBooking = (movie) => {
  // Loại bỏ phim đã ngừng chiếu
  if (movie?.trangThai === 'NGUNG_CHIEU') return false
  
  // Hiển thị cả phim đang chiếu và sắp chiếu
  return movie?.trangThai === 'DANG_CHIEU' || movie?.trangThai === 'SAP_CHIEU'
}

const filteredMovies = computed(() => {
  const availableMovies = movies.value.filter(m => isMovieAvailableForBooking(m))
  const searchFiltered = availableMovies.filter(m =>
    (!search.value || m.tenPhim.toLowerCase().includes(search.value.toLowerCase()))
  )
  
  return searchFiltered
})

function getPosterUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/')) return url // Public assets
  return API_BASE_URL + url
}

function getStatusClass(status) {
  const statusClasses = {
    'DANG_CHIEU': 'status-active',
    'SAP_CHIEU': 'status-coming-soon',
    'NGUNG_CHIEU': 'status-inactive'
  }
  return statusClasses[status] || 'status-default'
}

function formatStatus(status) {
  const statusMap = {
    'DANG_CHIEU': 'Đang chiếu',
    'SAP_CHIEU': 'Sắp chiếu',
    'NGUNG_CHIEU': 'Ngừng chiếu'
  }
  return statusMap[status] || status
}

function bookMovie(movie) {
  selectedMovie.value = movie
  showCinemaModal.value = true
}

function goToMovieDetail(movie) {
  router.push(`/movie/${movie.idPhim}`)
}
</script>

<style scoped>
.showtimes-page {
  min-height: 100vh;
  background: #18191a;
  color: #fff;
  padding-bottom: 40px;
}
.showtimes-hero {
  position: relative;
  width: 100%;
  height: 320px;
  overflow: hidden;
  margin-bottom: 32px;
}
.showtimes-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.7);
}
.showtimes-hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
}
.showtimes-hero-content h1 {
  font-size: 2.5rem;
  font-weight: 900;
  margin-bottom: 10px;
  color: #48dbfb;
}
.showtimes-hero-content p {
  font-size: 1.2rem;
  color: #feca57;
}
.showtimes-filter {
  display: flex;
  gap: 18px;
  justify-content: center;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}
.showtimes-filter select,
.showtimes-filter input[type='date'],
.showtimes-filter input[type='text'] {
  padding: 10px 16px;
  border-radius: 8px;
  border: 1.5px solid #48dbfb;
  background: #232526;
  color: #fff;
  font-size: 1rem;
  outline: none;
}
.btn-cinema-picker { 
  padding: 10px 16px; 
  border-radius: 8px; 
  border: 1.5px solid #48dbfb; 
  background: #232526; 
  color: #fff; 
  font-size: 1rem; 
  cursor: pointer; 
  transition: all 0.3s ease;
  position: relative;
  z-index: 999;
  pointer-events: auto;
}

.btn-cinema-picker:hover {
  background: #2c3e50;
  border-color: #feca57;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.3);
}
.showtimes-list {
  max-width: 1100px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 32px;
}
.showtimes-movie-card {
  background: #232526;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(72,219,251,0.10);
  display: flex;
  gap: 18px;
  align-items: flex-start;
  padding: 18px;
  transition: box-shadow 0.2s, transform 0.2s;
}
.showtimes-movie-card:hover {
  box-shadow: 0 8px 32px rgba(72,219,251,0.18);
  transform: translateY(-6px) scale(1.03);
}
.showtimes-movie-img {
  width: 110px;
  height: 160px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 2px 8px #48dbfb33;
  transition: transform 0.2s, box-shadow 0.2s;
}

.showtimes-movie-img:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px #48dbfb66;
}
.showtimes-movie-info {
  flex: 1;
}
.showtimes-movie-info h2 {
  font-size: 1.3rem;
  font-weight: 800;
  margin-bottom: 6px;
  color: #48dbfb;
  transition: color 0.2s;
}

.showtimes-movie-info h2:hover {
  color: #feca57;
}
.showtimes-movie-meta {
  font-size: 1rem;
  color: #b2bec3;
  margin-bottom: 10px;
}
.showtimes-sessions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}
.showtimes-session {
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  color: #18191a;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 12px;
  font-size: 0.98rem;
}
.btn-book {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 24px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px #48dbfb33;
  transition: background 0.2s, transform 0.2s;
}
.btn-book:hover {
  background: linear-gradient(90deg, #667eea 0%, #48dbfb 100%);
  transform: scale(1.05);
}
.showtimes-loading {
  text-align: center;
  padding: 60px 20px;
  color: #48dbfb;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #48dbfb33;
  border-top: 4px solid #48dbfb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.showtimes-empty {
  text-align: center;
  padding: 60px 20px;
  color: #b2bec3;
}

.showtimes-movie-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.status-active {
  background: linear-gradient(90deg, #27ae60 0%, #2ecc71 100%);
  color: #fff;
}

.status-coming-soon {
  background: linear-gradient(90deg, #f39c12 0%, #e67e22 100%);
  color: #fff;
}

.status-inactive {
  background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
  color: #fff;
}

.status-default {
  background: #636e72;
  color: #fff;
}

@media (max-width: 700px) {
  .showtimes-list { grid-template-columns: 1fr; }
  .showtimes-movie-card { flex-direction: column; align-items: center; }
  .showtimes-movie-img { width: 90px; height: 120px; }
}
</style> 