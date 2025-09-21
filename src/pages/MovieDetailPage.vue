<template>
  <div class="movie-detail-page" v-if="movie">
    <!-- Header -->
    <Header />

    <section class="hero">
      <img v-if="movie.bannerUrl" :src="fullImageUrl(movie.bannerUrl)" class="hero-banner" alt="Banner"
           @error="handleImageError($event, 'banner')" />
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <img v-if="movie.posterUrl" :src="fullImageUrl(movie.posterUrl)" class="hero-poster" alt="Poster"
             @error="handleImageError($event, 'poster')" />
        <div class="hero-info">
          <h1 class="hero-title">{{ movie.tenPhim }}</h1>
          
          <!-- Rating System like MoMo -->
          <div class="rating-section">
            <div class="rating-badge" :class="getRatingBadgeClass(movie.rating)">
              <span class="rating-icon">⭐</span>
              <span class="rating-text">{{ getRatingText(movie.rating) }}</span>
            </div>
            <div class="rating-score">
              <span class="score">{{ movie.rating || '8.5' }}</span>
              <span class="score-max">/10</span>
            </div>
            <div class="rating-count">{{ movie.reviewCount || '1.2K' }} đánh giá</div>
          </div>
          
          <div class="hero-chips">
            <span class="chip" :class="getStatusClass(movie.trangThai)">{{ formatStatus(movie.trangThai) }}</span>
            <span class="chip">{{ movie.dinhDang }}</span>
            <span class="chip">{{ formatDate(movie.ngayPhatHanh) }}</span>
            <span v-for="genre in getGenreNames(movie.theLoai)" :key="genre" class="chip light">{{ genre }}</span>
          </div>
          <div class="hero-meta">
            <div><b>Đạo diễn:</b> {{ joinNames(movie.daoDien) || '-' }}</div>
            <div><b>Diễn viên:</b> {{ joinNames(movie.dienVien) || '-' }}</div>
          </div>
          <div class="hero-actions">
            <router-link to="/showtimes" class="btn primary booking-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 12l2 2 4-4"/>
                <path d="M21 12c-1 0-3-1-3-3s2-3 3-3 3 1 3 3-2 3-3 3"/>
                <path d="M3 12c1 0 3-1 3-3s-2-3-3-3-3 1-3 3 2 3 3 3"/>
                <path d="M12 3c0 1-1 3-3 3s-3-2-3-3 1-3 3-3 3 2 3 3"/>
                <path d="M12 21c0-1 1-3 3-3s3 2 3 3-1 3-3 3-3-2-3-3"/>
              </svg>
              Mua vé xem phim
            </router-link>
            <button v-if="movie.trailerUrl" @click="activeTab = 'trailer'" class="btn ghost">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="5,3 19,12 5,21"/>
              </svg>
              Xem trailer
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Sticky Tabs -->
    <div class="sticky-tabs" :class="{ 'sticky': isSticky }">
      <div class="tabs-container">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          @click="activeTab = tab.id"
          class="tab-btn"
          :class="{ 'active': activeTab === tab.id }"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- Tab Content -->
    <section class="tab-content">
      <!-- Tổng quan -->
      <div v-if="activeTab === 'overview'" class="content-grid">
      <div class="card description">
        <h2>Giới thiệu</h2>
        <p>{{ movie.moTa }}</p>
        <div class="quick-stats">
          <div class="qs-item"><span>Thời lượng</span><b>{{ movie.thoiLuong }} phút</b></div>
          <div class="qs-item"><span>Ngày tạo</span><b>{{ formatDate(movie.ngayTao) }}</b></div>
          <div class="qs-item"><span>Độ tuổi</span><b>{{ movie.tuoiGioiHan || '-' }}</b></div>
          <div class="qs-item"><span>Năm SX</span><b>{{ movie.namSanXuat || '-' }}</b></div>
        </div>
      </div>

      <div class="card trailer" v-if="movie.trailerUrl">
        <h2>Trailer</h2>
        <div v-if="isYouTube(movie.trailerUrl)" class="trailer-embed">
          <iframe :src="youtubeEmbedUrl(movie.trailerUrl)" frameborder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen></iframe>
        </div>
        <video v-else controls :src="fullImageUrl(movie.trailerUrl)" class="trailer-video"></video>
      </div>

      <div class="card credits">
        <h2>Diễn viên & Đoàn làm phim</h2>
        <div class="cast-grid">
          <div v-if="movie.daoDien && movie.daoDien.length" class="cast-section">
            <h3>Đạo diễn</h3>
            <div class="cast-list">
              <div v-for="director in movie.daoDien" :key="director" class="cast-item">
                <div class="cast-avatar">
                  <span>{{ getInitials(director) }}</span>
                </div>
                <div class="cast-info">
                  <span class="cast-name">{{ director }}</span>
                  <span class="cast-role">Đạo diễn</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="movie.dienVien && movie.dienVien.length" class="cast-section">
            <h3>Diễn viên</h3>
            <div class="cast-list">
              <div v-for="actor in movie.dienVien.slice(0, 8)" :key="actor" class="cast-item">
                <div class="cast-avatar">
                  <span>{{ getInitials(actor) }}</span>
                </div>
                <div class="cast-info">
                  <span class="cast-name">{{ actor }}</span>
                  <span class="cast-role">Diễn viên</span>
                </div>
              </div>
              <div v-if="movie.dienVien.length > 8" class="cast-item more">
                <div class="cast-avatar">
                  <span>+{{ movie.dienVien.length - 8 }}</span>
                </div>
                <div class="cast-info">
                  <span class="cast-name">Và {{ movie.dienVien.length - 8 }} người khác</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>

      <!-- Trailer -->
      <div v-if="activeTab === 'trailer'" class="trailer-tab">
        <div class="card trailer" v-if="movie.trailerUrl">
          <h2>Trailer chính thức</h2>
          <div v-if="isYouTube(movie.trailerUrl)" class="trailer-embed">
            <iframe :src="youtubeEmbedUrl(movie.trailerUrl)" frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen></iframe>
          </div>
          <video v-else controls :src="fullImageUrl(movie.trailerUrl)" class="trailer-video"></video>
        </div>
        <div v-else class="card">
          <p>Chưa có trailer cho phim này.</p>
        </div>
      </div>

      <!-- Lịch chiếu -->
      <div v-if="activeTab === 'showtimes'" class="showtimes-tab">
        <div class="card">
          <h2>Lịch chiếu</h2>
          <p>Thông tin lịch chiếu sẽ được cập nhật sớm.</p>
          <router-link to="/showtimes" class="btn primary">Xem tất cả lịch chiếu</router-link>
        </div>
      </div>

      <!-- Hình ảnh -->
      <div v-if="activeTab === 'gallery'" class="gallery-tab">
        <div class="card">
          <h2>Hình ảnh</h2>
          <div class="gallery-grid">
            <div v-if="movie.posterUrl" class="gallery-item">
              <img :src="fullImageUrl(movie.posterUrl)" alt="Poster" @error="handleImageError($event, 'poster')" />
              <span>Poster</span>
            </div>
            <div v-if="movie.bannerUrl" class="gallery-item">
              <img :src="fullImageUrl(movie.bannerUrl)" alt="Banner" @error="handleImageError($event, 'banner')" />
              <span>Banner</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Bình luận -->
      <div v-if="activeTab === 'comments'" class="comments-tab">
        <div class="card">
          <div class="comments-header">
            <h2>Bình luận từ người xem</h2>
            <div class="overall-rating">
              <div class="rating-display">
                <span class="rating-number">{{ movie.rating || '8.5' }}</span>
                <span class="rating-max">/10</span>
              </div>
              <div class="rating-stars">
                <span v-for="i in 5" :key="i" class="star" :class="{ 'filled': i <= Math.round((movie.rating || 8.5) / 2) }">⭐</span>
              </div>
              <div class="rating-count">{{ movie.reviewCount || '1.2K' }} đánh giá</div>
            </div>
          </div>
          
          <div class="comments-content">
            <div class="no-comments">
              <div class="no-comments-icon">💬</div>
              <h3>Hiện tại chưa có bình luận nào</h3>
              <p>Hãy là người đầu tiên bình luận về bộ phim này!</p>
              <button class="btn primary">Viết bình luận</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Related Movies -->
    <section class="related-movies" v-if="relatedMovies.length > 0">
      <div class="card">
        <div class="related-header">
          <h2>Phim liên quan</h2>
          <div class="related-navigation" v-if="relatedMovies.length > 4">
            <button 
              class="nav-btn prev-btn" 
              @click="scrollRelatedMovies('prev')"
              :disabled="relatedScrollIndex === 0"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button 
              class="nav-btn next-btn" 
              @click="scrollRelatedMovies('next')"
              :disabled="relatedScrollIndex >= maxRelatedScrollIndex"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="related-container">
          <div 
            class="related-grid"
            :style="{ transform: `translateX(-${relatedScrollIndex * 25}%)` }"
          >
            <div 
              v-for="relatedMovie in relatedMovies" 
              :key="relatedMovie.idPhim"
              class="related-item"
              @click="goToMovie(relatedMovie.idPhim)"
            >
              <img 
                :src="fullImageUrl(relatedMovie.posterUrl)" 
                :alt="relatedMovie.tenPhim"
                @error="handleImageError($event, 'poster')"
              />
              <div class="related-info">
                <h3>{{ relatedMovie.tenPhim }}</h3>
                <div class="related-meta">
                  <span class="chip status-active">Đang Chiếu</span>
                  <span class="chip">{{ relatedMovie.dinhDang }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <div class="page-actions">
      <router-link to="/showtimes" class="btn primary">Xem lịch chiếu</router-link>
      <router-link to="/" class="btn ghost">← Quay lại</router-link>
    </div>
  </div>
  <div v-else class="loading-state">
    <div class="loading-spinner"></div>
    <p>Đang tải dữ liệu phim...</p>
  </div>
</template>
<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchMovies, fetchGenres, getMovieById } from '../services/movieService'
import { API_BASE_URL } from '../services/api'
import Header from '../components/layout/Header.vue'

const route = useRoute()
const router = useRouter()
const movie = ref(null)
const genres = ref([])
const allMovies = ref([])
const relatedMovies = ref([])
const isSticky = ref(false)
const activeTab = ref('overview')
const relatedScrollIndex = ref(0)
const maxRelatedScrollIndex = computed(() => Math.max(0, Math.ceil(relatedMovies.value.length / 4) - 1))
const BASE_URL = API_BASE_URL

// Tabs configuration
const tabs = [
  { id: 'overview', label: 'Tổng quan' },
  { id: 'trailer', label: 'Trailer' },
  { id: 'showtimes', label: 'Lịch chiếu' },
  { id: 'gallery', label: 'Hình ảnh' },
  { id: 'comments', label: 'Bình luận' }
]

function fullImageUrl(path) {
  if (!path) return ''
  
  // Nếu path đã là URL đầy đủ, trả về nguyên
  if (path.startsWith('http')) {
    return path
  }
  
  // Nếu path bắt đầu với /uploads, thêm BASE_URL
  if (path.startsWith('/uploads')) {
    return `${BASE_URL}${path}`
  }
  
  // Trường hợp khác, thêm BASE_URL và /
  return `${BASE_URL}${path.startsWith('/') ? '' : '/'}${path}`.replace(/([^:]\/)\/+/g, '$1')
}

function handleImageError(event, type = 'image') {
  console.error(`❌ [Image Load Error] Không tải được ${type} tại: ${event.target.src}`)
  event.target.src = '/logo.png?v=2' // Fallback image
  event.target.classList.add('image-error')
}

function joinNames(arr) {
  return Array.isArray(arr) ? arr.join(', ') : arr
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function formatStatus(status) {
  switch (status) {
    case 'DANG_CHIEU': return 'Đang Chiếu'
    case 'NGUNG_CHIEU': return 'Ngừng Chiếu'
    default: return status
  }
}

function getStatusClass(status) {
  return status === 'DANG_CHIEU' ? 'status-active' : status === 'NGUNG_CHIEU' ? 'status-inactive' : 'status-default'
}

function getGenreNames(ids) {
  if (!Array.isArray(ids)) return []
  return ids.map(id => {
    const found = genres.value.find(g => g.id === id || g.id == id)
    return found ? found.name : id
  })
}

function isYouTube(url) {
  return /youtu\.be|youtube\.com/.test(url || '')
}

function youtubeEmbedUrl(url) {
  if (!url) return ''
  const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/)
  const id = match ? match[1] : ''
  return id ? `https://www.youtube.com/embed/${id}` : url
}


// Navigate to movie
function goToMovie(movieId) {
  router.push(`/movie/${movieId}`)
}

// Related movies navigation
function scrollRelatedMovies(direction) {
  if (direction === 'next' && relatedScrollIndex.value < maxRelatedScrollIndex.value) {
    relatedScrollIndex.value++
  } else if (direction === 'prev' && relatedScrollIndex.value > 0) {
    relatedScrollIndex.value--
  }
}

// Find related movies based on genres (only showing movies)
function findRelatedMovies() {
  if (!movie.value || !allMovies.value.length) return
  
  const currentGenres = movie.value.theLoai || []
  const currentId = movie.value.idPhim
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  relatedMovies.value = allMovies.value
    .filter(m => m.idPhim !== currentId)
    .filter(m => {
      // Chỉ hiển thị phim đang chiếu
      if (m.trangThai === 'NGUNG_CHIEU') return false
      
      const releaseDate = new Date(m.ngayPhatHanh)
      if (isNaN(releaseDate.getTime())) return false
      releaseDate.setHours(0, 0, 0, 0)
      
      // Phim đang chiếu: ngày phát hành <= hôm nay và chưa ngừng chiếu
      const isCurrentlyShowing = releaseDate.getTime() <= today.getTime() && m.trangThai !== 'NGUNG_CHIEU'
      
      if (!isCurrentlyShowing) return false
      
      // Kiểm tra thể loại tương tự
      const movieGenres = m.theLoai || []
      return movieGenres.some(genre => currentGenres.includes(genre))
    })
    .slice(0, 4) // Show max 4 related movies
}

// Handle scroll for sticky tabs
function handleScroll() {
  const hero = document.querySelector('.hero')
  if (hero) {
    const heroBottom = hero.offsetTop + hero.offsetHeight
    isSticky.value = window.scrollY > heroBottom - 100
  }
}

// Rating functions
function getRatingBadgeClass(rating) {
  const score = rating || 8.5
  if (score >= 9.0) return 'rating-excellent'
  if (score >= 8.0) return 'rating-very-good'
  if (score >= 7.0) return 'rating-good'
  return 'rating-average'
}

function getRatingText(rating) {
  const score = rating || 8.5
  if (score >= 9.0) return 'Siêu phẩm'
  if (score >= 8.0) return 'Xuất sắc'
  if (score >= 7.0) return 'Tốt'
  return 'Khá'
}

// Get initials for cast avatars
function getInitials(name) {
  if (!name) return '?'
  const words = name.trim().split(' ')
  if (words.length >= 2) {
    return (words[0][0] + words[words.length - 1][0]).toUpperCase()
  }
  return name.substring(0, 2).toUpperCase()
}

onMounted(async () => {
  try {
    const id = route.params.id
    const [movieRes, genreRes, moviesRes] = await Promise.all([
      getMovieById(id),
      fetchGenres(),
      fetchMovies() // Load all movies for related movies
    ])
    
    // Một số API trả trực tiếp object, một số bọc trong data/content
    movie.value = movieRes?.data?.idPhim ? movieRes.data
      : movieRes?.data?.content?.idPhim ? movieRes.data.content
      : movieRes?.data || null
    genres.value = Array.isArray(genreRes?.data) ? genreRes.data : (genreRes?.data?.content || [])
    allMovies.value = Array.isArray(moviesRes.data) ? moviesRes.data : (moviesRes.data?.content || [])
    
    // Find related movies after loading data
    findRelatedMovies()
  } catch (e) {
    // Fallback: nếu API /:id không có, thử tải toàn bộ và lọc
    try {
      const res = await fetchMovies()
      const list = Array.isArray(res.data) ? res.data : (res.data?.content || [])
      const id = route.params.id
      movie.value = list.find(m => String(m.idPhim) === String(id)) || null
      allMovies.value = list
      findRelatedMovies()
    } catch {}
  }
  
  // Add scroll listener for sticky tabs
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>
<style scoped>
.movie-detail-page { padding: 24px; max-width: 1200px; margin: 0 auto; margin-top: 70px; }


/* Sticky Tabs */
.sticky-tabs { 
  position: relative; 
  z-index: 10; 
  margin: 20px 0;
}
.sticky-tabs.sticky { 
  position: fixed; 
  top: 80px; 
  left: 0; 
  right: 0; 
  background: rgba(255, 255, 255, 0.95); 
  backdrop-filter: blur(10px); 
  border-bottom: 1px solid #e2e8f0; 
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}
.tabs-container { 
  max-width: 1200px; 
  margin: 0 auto; 
  padding: 0 24px; 
  display: flex; 
  gap: 0;
}
.tab-btn { 
  padding: 12px 20px; 
  background: none; 
  border: none; 
  color: #64748b; 
  font-size: 14px; 
  font-weight: 600; 
  cursor: pointer; 
  border-bottom: 2px solid transparent; 
  transition: all 0.2s;
}
.tab-btn:hover { 
  color: #3b82f6; 
  background: #f8fafc;
}
.tab-btn.active { 
  color: #3b82f6; 
  border-bottom-color: #3b82f6; 
  background: #f8fafc;
}

/* Tab Content */
.tab-content { 
  margin-top: 20px;
}
.trailer-tab, .showtimes-tab, .gallery-tab, .comments-tab { 
  max-width: 800px; 
  margin: 0 auto;
}

/* Gallery */
.gallery-grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
  gap: 16px; 
  margin-top: 16px;
}
.gallery-item { 
  position: relative; 
  border-radius: 12px; 
  overflow: hidden; 
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.gallery-item img { 
  width: 100%; 
  height: 280px; 
  object-fit: cover; 
  transition: transform 0.3s;
}
.gallery-item:hover img { 
  transform: scale(1.05);
}
.gallery-item span { 
  position: absolute; 
  bottom: 0; 
  left: 0; 
  right: 0; 
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8)); 
  color: white; 
  padding: 12px; 
  font-weight: 600; 
  text-align: center;
}

/* Related Movies */
.related-movies { 
  margin-top: 40px;
}
.related-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.related-navigation {
  display: flex;
  gap: 8px;
}
.nav-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #64748b;
}
.nav-btn:hover:not(:disabled) {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
}
.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.related-container {
  overflow: hidden;
  position: relative;
}
.related-grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
  gap: 20px; 
  transition: transform 0.3s ease;
  width: 100%;
}
.related-item { 
  cursor: pointer; 
  border-radius: 12px; 
  overflow: hidden; 
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); 
  transition: all 0.3s;
}
.related-item:hover { 
  transform: translateY(-4px); 
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}
.related-item img { 
  width: 100%; 
  height: 280px; 
  object-fit: cover;
}
.related-info { 
  padding: 16px; 
  background: white;
}
.related-info h3 { 
  margin: 0 0 8px 0; 
  font-size: 16px; 
  font-weight: 700; 
  color: #1e293b; 
  line-height: 1.4;
}
.related-meta { 
  display: flex; 
  gap: 6px; 
  flex-wrap: wrap;
}

/* Comments Section */
.comments-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 24px; 
  padding-bottom: 16px; 
  border-bottom: 1px solid #e5e7eb;
}
.overall-rating { 
  display: flex; 
  align-items: center; 
  gap: 16px;
}
.rating-display { 
  display: flex; 
  align-items: baseline; 
  gap: 2px;
}
.rating-number { 
  font-size: 32px; 
  font-weight: 900; 
  color: #1f2937; 
}
.rating-max { 
  font-size: 20px; 
  color: #6b7280; 
}
.rating-stars { 
  display: flex; 
  gap: 2px;
}
.star { 
  font-size: 18px; 
  opacity: 0.3; 
  transition: opacity 0.2s;
}
.star.filled { 
  opacity: 1; 
}
.comments-content { 
  min-height: 200px; 
  display: flex; 
  align-items: center; 
  justify-content: center;
}
.no-comments { 
  text-align: center; 
  padding: 40px 20px;
}
.no-comments-icon { 
  font-size: 48px; 
  margin-bottom: 16px; 
  opacity: 0.5;
}
.no-comments h3 { 
  margin: 0 0 8px 0; 
  font-size: 18px; 
  color: #374151; 
}
.no-comments p { 
  margin: 0 0 20px 0; 
  color: #6b7280; 
}
.hero { position: relative; border-radius: 24px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,.18); }
.hero-banner { width: 100%; height: 360px; object-fit: cover; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(180deg, rgba(0,0,0,.35) 0%, rgba(0,0,0,.6) 70%); }
.hero-content { position: absolute; left: 24px; right: 24px; bottom: 24px; display: flex; gap: 24px; align-items: flex-end; }
.hero-poster { width: 200px; height: 280px; object-fit: cover; border-radius: 16px; box-shadow: 0 12px 40px rgba(0,0,0,.5); }
.hero-info { flex: 1; background: rgba(17,24,39,.75); backdrop-filter: blur(6px); border: 1px solid rgba(255,255,255,.08); padding: 16px 18px; border-radius: 16px; color: #e5e7eb; }
.hero-title { font-size: 32px; font-weight: 900; margin: 0 0 16px 0; color: #ffffff; letter-spacing: .3px; }

/* Rating System */
.rating-section { 
  display: flex; 
  align-items: center; 
  gap: 16px; 
  margin-bottom: 16px; 
  flex-wrap: wrap;
}
.rating-badge { 
  display: flex; 
  align-items: center; 
  gap: 6px; 
  padding: 6px 12px; 
  border-radius: 20px; 
  font-size: 12px; 
  font-weight: 700; 
  letter-spacing: 0.5px;
}
.rating-excellent { 
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%); 
  color: #92400e; 
}
.rating-very-good { 
  background: linear-gradient(135deg, #10b981 0%, #059669 100%); 
  color: #064e3b; 
}
.rating-good { 
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); 
  color: #1e3a8a; 
}
.rating-average { 
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%); 
  color: #f9fafb; 
}
.rating-icon { 
  font-size: 14px; 
}
.rating-score { 
  display: flex; 
  align-items: baseline; 
  gap: 2px;
}
.score { 
  font-size: 24px; 
  font-weight: 900; 
  color: #ffffff; 
}
.score-max { 
  font-size: 16px; 
  color: #cbd5e1; 
}
.rating-count { 
  font-size: 14px; 
  color: #cbd5e1; 
}
.hero-chips { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 10px; }
.chip { background: #111827; color: #fff; padding: 6px 12px; border-radius: 999px; font-size: 12px; font-weight: 800; letter-spacing: .2px; }
.chip.light { background: rgba(255,255,255,.12); color: #e5e7eb; }
.status-active { background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important; }
.status-inactive { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%) !important; }
.hero-meta { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 8px 16px; margin-top: 6px; font-size: 14px; color: #cbd5e1; }
.hero-actions { margin-top: 12px; display: flex; gap: 10px; }
.btn { display: inline-block; padding: 10px 18px; border-radius: 12px; font-weight: 800; text-decoration: none; letter-spacing: .2px; }
.btn.primary { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: #fff; }
.btn.ghost { background: rgba(255,255,255,.12); color: #e5e7eb; border: 1px solid rgba(255,255,255,.18); }
.booking-btn { 
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%) !important; 
  font-weight: 700; 
  display: flex; 
  align-items: center; 
  gap: 8px;
}

.content-grid { display: grid; grid-template-columns: 2fr 1.2fr; gap: 20px; margin-top: 24px; }
.card { background: #ffffff; border-radius: 20px; padding: 20px; box-shadow: 0 10px 36px rgba(0,0,0,.08); }
.card h2 { margin: 0 0 12px 0; font-size: 20px; font-weight: 900; color: #111827; }
.card.description p { color: #334155; line-height: 1.6; }
.quick-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-top: 12px; }
.qs-item { background: #f8fafc; border: 1px solid #e5e7eb; border-radius: 12px; padding: 10px 12px; color: #475569; font-size: 13px; }
.qs-item b { display: block; color: #111827; margin-top: 4px; }

.trailer-embed { position: relative; padding-top: 56.25%; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.18); }
.trailer-embed iframe { position: absolute; top:0; left:0; width:100%; height:100%; border:0; }
.trailer-video { width: 100%; border-radius: 16px; box-shadow: 0 8px 24px rgba(0,0,0,0.18); }

/* Cast & Crew Grid */
.cast-grid { 
  display: grid; 
  grid-template-columns: 1fr; 
  gap: 24px; 
  margin-top: 16px;
}
.cast-section h3 { 
  margin: 0 0 12px 0; 
  font-size: 16px; 
  font-weight: 700; 
  color: #374151; 
}
.cast-list { 
  display: grid; 
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); 
  gap: 12px;
}
.cast-item { 
  display: flex; 
  align-items: center; 
  gap: 12px; 
  padding: 12px; 
  background: #f8fafc; 
  border-radius: 12px; 
  transition: all 0.2s;
}
.cast-item:hover { 
  background: #e2e8f0; 
  transform: translateY(-2px);
}
.cast-avatar { 
  width: 40px; 
  height: 40px; 
  border-radius: 50%; 
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: white; 
  font-weight: 700; 
  font-size: 14px;
}
.cast-item.more .cast-avatar { 
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%); 
}
.cast-info { 
  flex: 1; 
  min-width: 0;
}
.cast-name { 
  display: block; 
  font-weight: 600; 
  color: #1f2937; 
  font-size: 14px; 
  line-height: 1.4;
}
.cast-role { 
  display: block; 
  color: #6b7280; 
  font-size: 12px; 
  margin-top: 2px;
}

.page-actions { display: flex; gap: 10px; margin: 18px 0 8px; }

.loading-state { text-align: center; padding: 60px 0; color: #7f8c8d; }
.loading-spinner { width: 40px; height: 40px; border: 4px solid #ecf0f1; border-top: 4px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 16px; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

.image-error { opacity: .7; filter: grayscale(40%); }

@media (max-width: 900px) {
  .movie-detail-page { padding: 16px; }
  
  
  .hero-content { flex-direction: column; align-items: flex-start; bottom: 16px; }
  .hero-poster { width: 140px; height: 200px; }
  .content-grid { grid-template-columns: 1fr; }
  .quick-stats { grid-template-columns: repeat(2, 1fr); }
  
  .tabs-container { 
    padding: 0 16px; 
    overflow-x: auto; 
    scrollbar-width: none;
  }
  .tabs-container::-webkit-scrollbar { 
    display: none;
  }
  .tab-btn { 
    white-space: nowrap; 
    flex-shrink: 0;
  }
  
  .related-grid { 
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); 
    gap: 16px;
  }
  .gallery-grid { 
    grid-template-columns: 1fr; 
  }
  
  .rating-section { 
    flex-direction: column; 
    align-items: flex-start; 
    gap: 8px;
  }
  
  .cast-list { 
    grid-template-columns: 1fr; 
  }
  
  .comments-header { 
    flex-direction: column; 
    align-items: flex-start; 
    gap: 16px;
  }
  .overall-rating { 
    flex-direction: column; 
    align-items: flex-start; 
    gap: 8px;
  }
}
</style> 