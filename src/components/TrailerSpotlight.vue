<template>
  <section class="trailer-spotlight">
    <div class="spotlight-container">
      <div class="spotlight-header">
        <h3 class="spotlight-title">Trailer nổi bật</h3>
        <p class="spotlight-subtitle">Xem trước những bộ phim sắp ra mắt</p>
      </div>
      
      <div class="trailer-grid">
        <div 
          v-for="movie in featuredMovies" 
          :key="movie.idPhim || movie.id" 
          class="trailer-card"
          @click="openTrailer(movie)"
        >
          <div class="trailer-thumbnail">
            <img 
              :src="getPosterUrl(movie.posterUrl)" 
              :alt="movie.tenPhim" 
              class="trailer-poster"
              @error="handleImageError($event, 'poster')"
            />
            <div class="play-overlay">
              <div class="play-button">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                  <path d="M8 5V19L19 12L8 5Z" fill="currentColor"/>
                </svg>
              </div>
            </div>
            <div class="trailer-badge">TRAILER</div>
          </div>
          <div class="trailer-info">
            <div class="trailer-title">{{ movie.tenPhim }}</div>
            <div class="trailer-meta">
              <span class="trailer-date">{{ formatDate(movie.ngayPhatHanh) }}</span>
              <span class="trailer-duration">{{ movie.thoiLuong }} phút</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Trailer Modal -->
    <div v-if="showModal" class="trailer-modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="closeModal">×</button>
        <div class="modal-header">
          <h4>{{ selectedMovie?.tenPhim }}</h4>
          <p>{{ formatDate(selectedMovie?.ngayPhatHanh) }} • {{ selectedMovie?.thoiLuong }} phút</p>
        </div>
        <div class="modal-video">
          <iframe 
            v-if="selectedMovie?.trailerUrl"
            :src="getEmbedUrl(selectedMovie.trailerUrl)"
            frameborder="0"
            allowfullscreen
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          ></iframe>
          <div v-else class="no-trailer">
            <p>Trailer chưa có sẵn</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import { fetchMovies } from '@/services/movieService'

const showModal = ref(false)
const selectedMovie = ref(null)
const movies = ref([])

// Load movies on mount
async function loadMovies() {
  try {
    const res = await fetchMovies()
    const list = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
    movies.value = list
  } catch (e) {
    console.error('Error loading movies for trailer spotlight:', e)
    movies.value = []
  }
}

// Get featured movies (coming soon with trailers)
const featuredMovies = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  return movies.value
    .filter(movie => {
      // Only movies with trailers
      if (!movie.trailerUrl) return false
      
      // Coming soon movies (release date > today)
      const releaseDate = new Date(movie.ngayPhatHanh)
      if (isNaN(releaseDate.getTime())) return false
      releaseDate.setHours(0, 0, 0, 0)
      
      return releaseDate.getTime() > today.getTime()
    })
    .slice(0, 6) // Show max 6 trailers
})

function openTrailer(movie) {
  selectedMovie.value = movie
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  selectedMovie.value = null
}

function getPosterUrl(posterUrl) {
  if (!posterUrl) return '/logo.png?v=2'
  if (posterUrl.startsWith('http')) return posterUrl
  if (posterUrl.startsWith('/')) return posterUrl // Public assets
  return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${posterUrl}`
}

function handleImageError(event, type) {
  event.target.src = '/logo.png?v=2'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('vi-VN')
  } catch {
    return dateStr
  }
}

function getEmbedUrl(trailerUrl) {
  if (!trailerUrl) return ''
  
  // Handle YouTube URLs
  if (trailerUrl.includes('youtube.com/watch')) {
    const videoId = trailerUrl.split('v=')[1]?.split('&')[0]
    return `https://www.youtube.com/embed/${videoId}?autoplay=1`
  }
  
  if (trailerUrl.includes('youtu.be/')) {
    const videoId = trailerUrl.split('youtu.be/')[1]?.split('?')[0]
    return `https://www.youtube.com/embed/${videoId}?autoplay=1`
  }
  
  // Return original URL if not YouTube
  return trailerUrl
}

// Load movies when component mounts
loadMovies()
</script>

<style scoped>
.trailer-spotlight {
  margin: 40px 0;
  padding: 0 16px;
}

.spotlight-container {
  max-width: 1200px;
  margin: 0 auto;
}

.spotlight-header {
  text-align: center;
  margin-bottom: 32px;
}

.spotlight-title {
  font-size: 28px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 8px 0;
}

.spotlight-subtitle {
  font-size: 16px;
  color: #b2bec3;
  margin: 0;
}

.trailer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.trailer-card {
  background: #232526;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.trailer-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.3);
}

.trailer-thumbnail {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.trailer-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.trailer-card:hover .trailer-poster {
  transform: scale(1.1);
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.trailer-card:hover .play-overlay {
  opacity: 1;
}

.play-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #e50914;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: scale(0.8);
  transition: transform 0.3s ease;
}

.trailer-card:hover .play-button {
  transform: scale(1);
}

.trailer-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #e50914;
  color: #fff;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
}

.trailer-info {
  padding: 16px;
}

.trailer-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.trailer-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #b2bec3;
}

.trailer-date {
  color: #ffd166;
  font-weight: 600;
}

/* Modal Styles */
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
  z-index: 5000;
  padding: 20px;
}

.modal-content {
  background: #1e1f22;
  border-radius: 16px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border: none;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-header {
  padding: 24px 24px 16px 24px;
  border-bottom: 1px solid #ffffff11;
}

.modal-header h4 {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
}

.modal-header p {
  font-size: 16px;
  color: #b2bec3;
  margin: 0;
}

.modal-video {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
}

.modal-video iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.no-trailer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b2d31;
  color: #b2bec3;
  font-size: 18px;
}

@media (max-width: 768px) {
  .trailer-spotlight {
    margin: 24px 0;
    padding: 0 12px;
  }
  
  .spotlight-title {
    font-size: 24px;
  }
  
  .trailer-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .trailer-thumbnail {
    height: 160px;
  }
  
  .modal-content {
    margin: 10px;
    max-height: 80vh;
  }
  
  .modal-header {
    padding: 16px;
  }
  
  .modal-header h4 {
    font-size: 20px;
  }
}
</style>




