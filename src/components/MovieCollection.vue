<template>
  <section class="movie-collection">
    <div class="collection-header">
      <h3 class="collection-title">{{ title }}</h3>
      <button class="view-all-btn" @click="goToMovies">Xem tất cả →</button>
    </div>
    
    <div class="collection-container">
      <button 
        class="nav-arrow nav-prev" 
        @click="scrollLeft"
        :disabled="scrollPosition <= 0"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      
      <div class="movies-scroll" ref="scrollContainer">
        <div class="movies-list" :style="{ transform: `translateX(-${scrollPosition}px)` }">
          <div 
            v-for="movie in movies" 
            :key="movie.idPhim || movie.id" 
            class="movie-item"
            @click="goToMovieDetail(movie)"
          >
            <div class="movie-poster-wrap">
              <img 
                :src="getPosterUrl(movie.posterUrl)" 
                :alt="movie.tenPhim" 
                class="movie-poster" 
                @error="handleImageError($event, 'poster')"
              />
              <div class="badge-age" :style="{background: getAgeBadge(movie).color}">
                {{ getAgeBadge(movie).text }}
              </div>
              <div v-if="movie.isHot" class="badge-hot">HOT</div>
            </div>
            <div class="movie-info">
              <div class="movie-title">{{ movie.tenPhim }}</div>
              <div class="movie-meta">{{ movie.theLoai ? movie.theLoai.join(', ') : '-' }}</div>
              <div class="movie-date">{{ formatDate(movie.ngayPhatHanh) }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <button 
        class="nav-arrow nav-next" 
        @click="scrollRight"
        :disabled="scrollPosition >= maxScroll"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  movies: {
    type: Array,
    default: () => []
  },
  viewAllRoute: {
    type: String,
    default: '/movies'
  }
})

const router = useRouter()
const scrollContainer = ref(null)
const scrollPosition = ref(0)
const itemWidth = 200 // Width of each movie item + gap
const visibleItems = 5 // Number of items visible at once

const maxScroll = computed(() => {
  return Math.max(0, (props.movies.length - visibleItems) * itemWidth)
})

function scrollLeft() {
  scrollPosition.value = Math.max(0, scrollPosition.value - itemWidth * 2)
}

function scrollRight() {
  scrollPosition.value = Math.min(maxScroll.value, scrollPosition.value + itemWidth * 2)
}

function goToMovieDetail(movie) {
  const movieId = movie.idPhim || movie.id
  if (movieId) {
    router.push(`/movie-detail/${movieId}`)
  }
}

function goToMovies() {
  router.push(props.viewAllRoute)
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

function getAgeBadge(movie) {
  const age = movie.tuoiGioiHan || movie.ageRating || 'T13'
  const badges = {
    'T13': { text: 'T13', color: '#ffd166' },
    'T16': { text: 'T16', color: '#ff6b6b' },
    'T18': { text: 'T18', color: '#e74c3c' },
    'P': { text: 'P', color: '#2ecc71' }
  }
  return badges[age] || badges['T13']
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

// Auto-scroll on mount
onMounted(() => {
  if (props.movies.length > visibleItems) {
    const interval = setInterval(() => {
      if (scrollPosition.value >= maxScroll.value) {
        scrollPosition.value = 0
      } else {
        scrollPosition.value += itemWidth
      }
    }, 4000)
    
    onUnmounted(() => clearInterval(interval))
  }
})
</script>

<style scoped>
.movie-collection {
  margin: 40px 0;
  padding: 0 16px;
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.collection-title {
  font-size: 24px;
  font-weight: 900;
  color: #fff;
  margin: 0;
}

.view-all-btn {
  background: transparent;
  color: #ffd166;
  border: 1px solid #ffd16666;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

.view-all-btn:hover {
  background: #ffd16622;
  transform: translateX(2px);
}

.collection-container {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-arrow {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.nav-arrow:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.nav-arrow:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.movies-scroll {
  flex: 1;
  overflow: hidden;
  border-radius: 12px;
}

.movies-list {
  display: flex;
  gap: 16px;
  transition: transform 0.3s ease;
  padding: 8px 0;
}

.movie-item {
  flex-shrink: 0;
  width: 180px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.movie-item:hover {
  transform: translateY(-4px);
}

.movie-poster-wrap {
  position: relative;
  width: 180px;
  height: 240px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.movie-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.movie-item:hover .movie-poster {
  transform: scale(1.05);
}

.badge-age {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  color: #000;
}

.badge-hot {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #e50914;
  color: #fff;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
}

.movie-info {
  padding: 12px 0;
}

.movie-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-meta {
  font-size: 13px;
  color: #b2bec3;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-date {
  font-size: 12px;
  color: #ffd166;
  font-weight: 600;
}

@media (max-width: 768px) {
  .movie-collection {
    margin: 24px 0;
    padding: 0 12px;
  }
  
  .collection-title {
    font-size: 20px;
  }
  
  .nav-arrow {
    width: 32px;
    height: 32px;
  }
  
  .movie-item {
    width: 140px;
  }
  
  .movie-poster-wrap {
    width: 140px;
    height: 190px;
  }
  
  .visible-items {
    --visible-items: 3;
  }
}
</style>




