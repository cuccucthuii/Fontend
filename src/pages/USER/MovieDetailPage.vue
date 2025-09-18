<template>
  <div class="movie-detail-page">
    <!-- Header -->
    <Header />
    
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải thông tin phim...</p>
    </div>
    
    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">❌</div>
      <h2>Không tìm thấy phim</h2>
      <p>{{ error }}</p>
      <button @click="$router.push('/home')" class="btn-back">Quay về trang chủ</button>
    </div>
    
    <!-- Movie Detail Content -->
    <div v-else-if="movie" class="movie-detail-content">
      <!-- Hero Section -->
      <div class="hero-section">
        <div class="hero-background" :style="{ backgroundImage: `url(${getPosterUrl(movie.bannerUrl || movie.posterUrl)})` }">
          <div class="hero-overlay"></div>
        </div>
        
        <div class="hero-content">
          <div class="container">
            <div class="movie-hero-grid">
              <!-- Poster -->
              <div class="movie-poster-section">
                <div class="movie-poster">
                  <img :src="getPosterUrl(movie.posterUrl)" :alt="movie.tenPhim" />
                  <div class="poster-overlay">
                    <button @click="playTrailer" class="play-trailer-btn" v-if="movie.trailerUrl">
                      <span class="play-icon">▶️</span>
                      <span>Xem Trailer</span>
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- Info -->
              <div class="movie-info-section">
                <div class="movie-header">
                  <h1 class="movie-title">{{ movie.tenPhim }}</h1>
                  <div class="movie-badges">
                    <span v-if="movie.dinhDang" class="badge-format">{{ movie.dinhDang }}</span>
                    <span class="badge-status" :class="getStatusClass(movie.trangThai)">
                      {{ formatStatus(movie.trangThai) }}
                    </span>
                  </div>
                </div>
                
                <div class="movie-meta">
                  <div class="meta-item">
                    <span class="meta-label">Thể loại:</span>
                    <span class="meta-value">{{ movie.theLoai ? movie.theLoai.join(', ') : 'Chưa phân loại' }}</span>
                  </div>
                  <div class="meta-item">
                    <span class="meta-label">Thời lượng:</span>
                    <span class="meta-value">{{ movie.thoiLuong ? movie.thoiLuong + ' phút' : 'Chưa cập nhật' }}</span>
                  </div>
                  <div class="meta-item">
                    <span class="meta-label">Khởi chiếu:</span>
                    <span class="meta-value">{{ formatDate(movie.ngayPhatHanh) || 'Chưa cập nhật' }}</span>
                  </div>
                  <div class="meta-item" v-if="movie.daoDien && movie.daoDien.length">
                    <span class="meta-label">Đạo diễn:</span>
                    <span class="meta-value">{{ movie.daoDien.join(', ') }}</span>
                  </div>
                  <div class="meta-item" v-if="movie.dienVien && movie.dienVien.length">
                    <span class="meta-label">Diễn viên:</span>
                    <span class="meta-value">{{ movie.dienVien.join(', ') }}</span>
                  </div>
                </div>
                
                <div class="movie-description">
                  <h3>Nội dung</h3>
                  <p>{{ movie.moTa || 'Chưa có mô tả cho phim này.' }}</p>
                </div>
                
                <div class="movie-actions">
                  <button @click="goToBooking" class="btn-book-ticket">
                    <span class="ticket-icon">🎟️</span>
                    <span>Đặt vé ngay</span>
                  </button>
                  <button @click="goToShowtimes" class="btn-showtimes">
                    <span class="calendar-icon">📅</span>
                    <span>Xem lịch chiếu</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Additional Info Section -->
      <div class="additional-info">
        <div class="container">
          <div class="info-grid">
            <!-- Cast & Crew -->
            <div class="info-section">
              <h3>Diễn viên & Đạo diễn</h3>
              <div class="cast-crew">
                <div v-if="movie.daoDien && movie.daoDien.length" class="crew-item">
                  <h4>Đạo diễn</h4>
                  <p>{{ movie.daoDien.join(', ') }}</p>
                </div>
                <div v-if="movie.dienVien && movie.dienVien.length" class="crew-item">
                  <h4>Diễn viên chính</h4>
                  <p>{{ movie.dienVien.join(', ') }}</p>
                </div>
              </div>
            </div>
            
            <!-- Technical Info -->
            <div class="info-section">
              <h3>Thông tin kỹ thuật</h3>
              <div class="tech-info">
                <div class="tech-item">
                  <span class="tech-label">Định dạng:</span>
                  <span class="tech-value">{{ movie.dinhDang || 'Chưa cập nhật' }}</span>
                </div>
                <div class="tech-item">
                  <span class="tech-label">Thời lượng:</span>
                  <span class="tech-value">{{ movie.thoiLuong ? movie.thoiLuong + ' phút' : 'Chưa cập nhật' }}</span>
                </div>
                <div class="tech-item">
                  <span class="tech-label">Ngày phát hành:</span>
                  <span class="tech-value">{{ formatDate(movie.ngayPhatHanh) || 'Chưa cập nhật' }}</span>
                </div>
                <div class="tech-item">
                  <span class="tech-label">Trạng thái:</span>
                  <span class="tech-value" :class="getStatusClass(movie.trangThai)">
                    {{ formatStatus(movie.trangThai) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Trailer Modal -->
    <div v-if="showTrailerModal" class="trailer-modal" @click.self="closeTrailerModal">
      <div class="trailer-modal-content">
        <button @click="closeTrailerModal" class="trailer-close-btn">×</button>
        <iframe
          v-if="trailerEmbedUrl"
          :src="trailerEmbedUrl"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
        <div v-else class="trailer-error">
          <p>Không thể tải trailer</p>
        </div>
      </div>
    </div>
    
    <!-- Footer -->
    <HomeFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMovieById } from '../../services/movieService'
import Header from '../../components/Header.vue'
import HomeFooter from '../../components/HomeFooter.vue'

const route = useRoute()
const router = useRouter()

const movie = ref(null)
const loading = ref(true)
const error = ref('')
const showTrailerModal = ref(false)

// Computed
const trailerEmbedUrl = computed(() => {
  if (!movie.value?.trailerUrl) return ''
  const match = movie.value.trailerUrl.match(/(?:v=|youtu\.be\/|embed\/)([\w-]{11})/)
  const videoId = match ? match[1] : ''
  return videoId ? `https://www.youtube.com/embed/${videoId}?autoplay=1` : ''
})

// Methods
async function loadMovie() {
  loading.value = true
  error.value = ''
  
  try {
    const movieId = route.params.id
    const response = await getMovieById(movieId)
    movie.value = response.data
  } catch (err) {
    console.error('Error loading movie:', err)
    error.value = 'Không thể tải thông tin phim. Vui lòng thử lại sau.'
  } finally {
    loading.value = false
  }
}

function getPosterUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function formatStatus(status) {
  const statusMap = {
    'DANG_CHIEU': 'Đang chiếu',
    'NGUNG_CHIEU': 'Ngừng chiếu'
  }
  return statusMap[status] || status
}

function getStatusClass(status) {
  const statusClasses = {
    'DANG_CHIEU': 'status-active',
    'NGUNG_CHIEU': 'status-inactive'
  }
  return statusClasses[status] || 'status-default'
}

function playTrailer() {
  if (movie.value?.trailerUrl) {
    showTrailerModal.value = true
  }
}

function closeTrailerModal() {
  showTrailerModal.value = false
}

function goToBooking() {
  router.push({ path: '/booking', query: { movieId: movie.value?.id || movie.value?.idPhim } })
}

function goToShowtimes() {
  router.push('/showtimes')
}

// Lifecycle
onMounted(() => {
  loadMovie()
})
</script>

<style scoped>
.movie-detail-page {
  min-height: 100vh;
  background: #18191a;
  color: #fff;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.loading-container,
.error-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #48dbfb33;
  border-top: 4px solid #48dbfb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.btn-back {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 20px;
  transition: transform 0.2s;
}

.btn-back:hover {
  transform: translateY(-2px);
}

/* Hero Section */
.hero-section {
  position: relative;
  min-height: 600px;
  display: flex;
  align-items: center;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: blur(8px);
  transform: scale(1.1);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(35, 37, 38, 0.9) 0%, rgba(28, 28, 28, 0.95) 100%);
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.movie-hero-grid {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 40px;
  align-items: start;
}

/* Poster Section */
.movie-poster-section {
  position: sticky;
  top: 20px;
}

.movie-poster {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(72, 219, 251, 0.2);
  transition: transform 0.3s;
}

.movie-poster:hover {
  transform: translateY(-8px);
}

.movie-poster img {
  width: 100%;
  height: 450px;
  object-fit: cover;
  display: block;
}

.poster-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-poster:hover .poster-overlay {
  opacity: 1;
}

.play-trailer-btn {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.2s;
}

.play-trailer-btn:hover {
  transform: scale(1.05);
}

/* Info Section */
.movie-info-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.movie-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.movie-title {
  font-size: 2.5rem;
  font-weight: 900;
  color: #48dbfb;
  margin: 0;
  line-height: 1.2;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.movie-badges {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.badge-format,
.badge-status {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
}

.badge-format {
  background: rgba(72, 219, 251, 0.2);
  color: #48dbfb;
  border: 1px solid #48dbfb;
}

.badge-status {
  color: #fff;
}

.status-active {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
}

.status-inactive {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}

.status-default {
  background: #636e72;
}

/* Meta Info */
.movie-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.meta-label {
  color: #b2bec3;
  font-weight: 600;
  min-width: 100px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.meta-value {
  color: #fff;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Description */
.movie-description h3 {
  color: #48dbfb;
  font-size: 1.3rem;
  margin-bottom: 12px;
}

.movie-description p {
  color: #feca57;
  line-height: 1.6;
  font-size: 1.1rem;
}

/* Actions */
.movie-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-book-ticket,
.btn-showtimes {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.btn-book-ticket {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
}

.btn-showtimes {
  background: transparent;
  color: #48dbfb;
  border: 2px solid #48dbfb;
}

.btn-book-ticket:hover,
.btn-showtimes:hover {
  transform: translateY(-2px);
}

/* Additional Info */
.additional-info {
  padding: 60px 0;
  background: #232526;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 40px;
}

.info-section h3 {
  color: #48dbfb;
  font-size: 1.5rem;
  margin-bottom: 20px;
  border-bottom: 2px solid #48dbfb;
  padding-bottom: 8px;
}

.cast-crew {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.crew-item h4 {
  color: #feca57;
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.crew-item p {
  color: #fff;
  line-height: 1.5;
}

.tech-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tech-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #404040;
}

.tech-label {
  color: #b2bec3;
  font-weight: 600;
}

.tech-value {
  color: #fff;
}

/* Trailer Modal */
.trailer-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

.trailer-modal-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  background: #232526;
  border-radius: 16px;
  overflow: hidden;
}

.trailer-close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
  transition: background 0.2s;
}

.trailer-close-btn:hover {
  background: rgba(0, 0, 0, 0.9);
}

.trailer-modal-content iframe {
  width: 100%;
  height: 70vh;
  min-height: 400px;
}

.trailer-error {
  padding: 60px 40px;
  text-align: center;
  color: #b2bec3;
}

/* Responsive */
@media (max-width: 768px) {
  .movie-hero-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .movie-poster-section {
    position: static;
  }
  
  .movie-poster img {
    height: 350px;
  }
  
  .movie-title {
    font-size: 2rem;
  }
  
  .movie-actions {
    flex-direction: column;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .tech-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style> 