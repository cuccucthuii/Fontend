<template>
  <div class="banner-carousel">
    <div class="carousel-container">
      <div class="carousel-wrapper" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
        <div 
          v-for="(movie, index) in movies" 
          :key="movie.idPhim" 
          class="carousel-slide"
          :class="{ active: index === currentIndex }"
        >
          <div class="banner-image" :style="{ backgroundImage: `url(${fullImageUrl(movie.bannerUrl)})` }">
            <div class="banner-overlay">
              <div class="banner-content">
                <div class="movie-info">
                  <h2 class="movie-title">{{ movie.tenPhim }}</h2>
                  <p class="movie-description">{{ movie.moTa }}</p>
                  <div class="movie-meta">
                    <span class="duration">{{ movie.thoiLuong }} phút</span>
                    <span class="format">{{ movie.dinhDang }}</span>
                    <span class="age-limit">{{ movie.tuoiGioiHan }}</span>
                  </div>
                  <div class="movie-actions">
                    <router-link :to="`/movie/${movie.idPhim}`" class="btn-primary">
                      Xem chi tiết
                    </router-link>
                    <button v-if="movie.trailerUrl" @click="playTrailer(movie)" class="btn-secondary">
                      Xem trailer
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Navigation arrows -->
      <button 
        v-if="movies.length > 1"
        class="nav-arrow nav-arrow-left" 
        @click="previousSlide"
        :disabled="currentIndex === 0"
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M15 6L9 12L15 18" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <button 
        v-if="movies.length > 1"
        class="nav-arrow nav-arrow-right" 
        @click="nextSlide"
        :disabled="currentIndex === movies.length - 1"
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 6L15 12L9 18" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      
      <!-- Dots indicator -->
      <div v-if="movies.length > 1" class="dots-indicator">
        <span 
          v-for="(movie, index) in movies" 
          :key="index"
          class="dot"
          :class="{ active: index === currentIndex }"
          @click="goToSlide(index)"
        ></span>
      </div>
    </div>
    
    <!-- Small banners below -->
    <div v-if="movies.length > 1" class="small-banners">
      <div 
        v-for="(movie, index) in movies.slice(0, 3)" 
        :key="movie.idPhim"
        class="small-banner"
        :class="{ active: index === currentIndex }"
        @click="goToSlide(index)"
      >
        <div class="small-banner-image" :style="{ backgroundImage: `url(${fullImageUrl(movie.posterUrl)})` }">
          <div class="small-banner-overlay">
            <h4>{{ movie.tenPhim }}</h4>
            <p>{{ movie.dinhDang }} • {{ movie.thoiLuong }} phút</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { fetchMovies } from '@/services/movieService'
import { API_BASE_URL } from '@/services/api'

export default {
  name: 'BannerCarousel',
  setup() {
    const movies = ref([])
    const currentIndex = ref(0)
    const autoPlayInterval = ref(null)

    const fullImageUrl = (url) => {
      if (!url) return '/logo.png'
      if (url.startsWith('http')) return url
      return `${API_BASE_URL}${url}`
    }

    const DEFAULT_SHOWING_DAYS = 30

    const isTodayOrFuture = (dateStr) => {
      if (!dateStr) return false
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return false
      const today = new Date()
      today.setHours(0,0,0,0)
      d.setHours(0,0,0,0)
      return d.getTime() >= today.getTime()
    }

    const isBannerVisible = (movie) => {
      if (movie?.trangThai === 'NGUNG_CHIEU') return false
      const release = movie?.ngayPhatHanh
      if (!release) return false
      const releaseDate = new Date(release)
      if (isNaN(releaseDate.getTime())) return false
      const today = new Date(); today.setHours(0,0,0,0)
      releaseDate.setHours(0,0,0,0)
      if (releaseDate.getTime() > today.getTime()) return true
      const endDate = new Date(releaseDate)
      endDate.setDate(endDate.getDate() + DEFAULT_SHOWING_DAYS)
      return today.getTime() <= endDate.getTime()
    }

    const loadMovies = async () => {
      try {
        const res = await fetchMovies()
        const list = res?.data?.content ?? res?.data ?? []
        const showingMovies = Array.isArray(list)
          ? list.filter((movie) => isBannerVisible(movie))
          : []
        movies.value = showingMovies.slice(0, 5)
      } catch (error) {
        console.error('Error loading movies for banner:', error)
        movies.value = []
      }
    }

    const nextSlide = () => {
      if (currentIndex.value < movies.value.length - 1) {
        currentIndex.value++
      } else {
        currentIndex.value = 0
      }
    }

    const previousSlide = () => {
      if (currentIndex.value > 0) {
        currentIndex.value--
      } else {
        currentIndex.value = movies.value.length - 1
      }
    }

    const goToSlide = (index) => {
      currentIndex.value = index
    }

    const startAutoPlay = () => {
      autoPlayInterval.value = setInterval(() => {
        nextSlide()
      }, 5000) // Change slide every 5 seconds
    }

    const stopAutoPlay = () => {
      if (autoPlayInterval.value) {
        clearInterval(autoPlayInterval.value)
        autoPlayInterval.value = null
      }
    }

    const playTrailer = (movie) => {
      if (movie.trailerUrl) {
        window.open(movie.trailerUrl, '_blank')
      }
    }

    onMounted(() => {
      loadMovies()
      if (movies.value.length > 1) {
        startAutoPlay()
      }
    })

    onUnmounted(() => {
      stopAutoPlay()
    })

    return {
      movies,
      currentIndex,
      fullImageUrl,
      nextSlide,
      previousSlide,
      goToSlide,
      playTrailer
    }
  }
}
</script>

<style scoped>
.banner-carousel {
  position: relative;
  width: 100%;
  margin-bottom: 40px;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 640px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.carousel-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
  position: relative;
}

.banner-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.7) 0%,
    rgba(0, 0, 0, 0.3) 50%,
    rgba(0, 0, 0, 0.8) 100%
  );
  display: flex;
  align-items: center;
  padding: 0 80px;
}

.banner-content {
  max-width: 760px;
  color: white;
}

.movie-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
  line-height: 1.2;
}

.movie-description {
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 24px;
  opacity: 0.9;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.movie-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 32px;
}

.movie-meta span {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.movie-actions {
  display: flex;
  gap: 16px;
}

.btn-primary, .btn-secondary {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background: #e50914;
  color: white;
}

.btn-primary:hover {
  background: #f40612;
  transform: translateY(-2px);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.nav-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: 2px solid white;
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.nav-arrow:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
}

.nav-arrow:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.nav-arrow-left {
  left: 20px;
}

.nav-arrow-right {
  right: 20px;
}

.dots-indicator {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.dot.active {
  background: #e50914;
  transform: scale(1.2);
}

.small-banners {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  justify-content: center;
}

.small-banner {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.7;
}

.small-banner:hover,
.small-banner.active {
  opacity: 1;
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.small-banner-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.small-banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 12px;
}

.small-banner-overlay h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 4px;
  line-height: 1.2;
}

.small-banner-overlay p {
  font-size: 0.8rem;
  opacity: 0.8;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .carousel-container {
    height: 360px;
  }
  
  .banner-overlay {
    padding: 0 20px;
  }
  
  .movie-title {
    font-size: 2rem;
  }
  
  .movie-description {
    font-size: 1rem;
  }
  
  .movie-meta {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .movie-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .nav-arrow {
    width: 50px;
    height: 50px;
    font-size: 16px;
  }
  
  .nav-arrow-left {
    left: 10px;
  }
  
  .nav-arrow-right {
    right: 10px;
  }
  
  .small-banners {
    flex-wrap: wrap;
    gap: 15px;
  }
  
  .small-banner {
    width: 150px;
    height: 90px;
  }
}
</style>

