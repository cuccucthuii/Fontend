<template>
  <div 
    class="banner-carousel"
    @mouseenter="stopAutoPlay"
    @mouseleave="startAutoPlay"
  >
    <div class="carousel-container">
      <div class="carousel-wrapper" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
        <div 
          v-for="(movie, index) in movies" 
          :key="movie.idPhim" 
          class="carousel-slide"
          :class="{ active: index === currentIndex }"
        >
          <div 
            class="banner-container"
            @click="goToMovieDetail(movie)"
          >
            <!-- Video trailer hoặc ảnh fallback -->
            <div v-if="movie.trailerUrl && isYouTube(movie.trailerUrl)" class="banner-video">
              <iframe 
                :src="youtubeEmbedUrl(movie.trailerUrl)"
                frameborder="0"
                allowfullscreen
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                class="trailer-iframe"
              ></iframe>
            </div>
            <div v-else-if="movie.trailerUrl" class="banner-video">
              <video 
                :src="fullImageUrl(movie.trailerUrl)"
                autoplay
                muted
                loop
                playsinline
                class="trailer-video"
              ></video>
            </div>
            <div v-else class="banner-image" :style="{ backgroundImage: `url(${fullImageUrl(movie.bannerUrl)})` }">
            </div>
            
            <div class="banner-overlay">
              <div class="banner-content">
                <div class="movie-info">
                  <h2 class="movie-title">{{ movie.tenPhim }}</h2>
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
          <path d="M15 6L9 12L15 18" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <button 
        v-if="movies.length > 1"
        class="nav-arrow nav-arrow-right" 
        @click="nextSlide"
        :disabled="currentIndex === movies.length - 1"
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 6L15 12L9 18" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
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
        v-for="(movie, index) in movies.slice(0, 6)" 
        :key="movie.idPhim"
        class="small-banner"
        :class="{ active: index === currentIndex }"
        @click="goToSlide(index)"
      >
        <div class="small-banner-image" :style="{ backgroundImage: `url(${fullImageUrl(movie.posterUrl)})` }">
          <div class="small-banner-overlay">
            <h4>{{ movie.tenPhim }}</h4>
            <p>{{ movie.dinhDang }}</p>
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
      if (!url) return '/logo.png?v=2'
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
      // Chỉ hiển thị phim đang chiếu
      if (movie?.trangThai === 'NGUNG_CHIEU') return false
      const release = movie?.ngayPhatHanh
      if (!release) return false
      const releaseDate = new Date(release)
      if (isNaN(releaseDate.getTime())) return false
      const today = new Date(); today.setHours(0,0,0,0)
      releaseDate.setHours(0,0,0,0)
      
      // Chỉ hiển thị phim đã phát hành (đang chiếu)
      if (releaseDate.getTime() > today.getTime()) return false
      
      // Kiểm tra phim vẫn trong thời gian chiếu
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
        movies.value = showingMovies.slice(0, 10)
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


    const goToMovieDetail = (movie) => {
      window.location.href = `/movie/${movie.idPhim}`
    }

    const isYouTube = (url) => {
      return url.includes('youtube.com') || url.includes('youtu.be')
    }

    const youtubeEmbedUrl = (url) => {
      if (!url) return ''
      let videoId = ''
      if (url.includes('youtube.com/watch')) {
        videoId = url.split('v=')[1]?.split('&')[0]
      } else if (url.includes('youtu.be/')) {
        videoId = url.split('youtu.be/')[1]?.split('?')[0]
      }
      return videoId ? `https://www.youtube.com/embed/${videoId}?autoplay=1&mute=1&loop=1&playlist=${videoId}&controls=0&showinfo=0&rel=0&modestbranding=1` : url
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
      isYouTube,
      youtubeEmbedUrl,
      goToMovieDetail
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

.banner-container {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.banner-container:hover .movie-title {
  transform: scale(1.08);
  animation-duration: 1s;
}

.banner-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  transition: transform 0.3s ease;
}

.banner-video {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.trailer-iframe {
  width: 100%;
  height: 100%;
  border: none;
  object-fit: cover;
}

.trailer-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: none;
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
  position: relative;
}

.banner-content::before {
  content: '';
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.1) 0%, 
    rgba(255, 255, 255, 0.05) 25%, 
    transparent 50%, 
    rgba(255, 255, 255, 0.05) 75%, 
    rgba(255, 255, 255, 0.1) 100%);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  z-index: -1;
  opacity: 0.3;
}

.movie-title {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 20px;
  line-height: 1.1;
  color: #ffffff;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-family: 'Montserrat', 'Arial Black', sans-serif;
  
  /* Gradient text effect */
  background: linear-gradient(135deg, #feca57 0%, #ffd166 30%, #feca57 60%, #ffd166 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  
  /* Multiple text shadows for depth */
  text-shadow: 
    0 0 10px rgba(254, 202, 87, 0.4),
    0 0 20px rgba(254, 202, 87, 0.3),
    0 0 30px rgba(254, 202, 87, 0.2),
    2px 2px 4px rgba(0, 0, 0, 0.8),
    4px 4px 8px rgba(0, 0, 0, 0.6),
    6px 6px 12px rgba(0, 0, 0, 0.4);
  
  /* Glow effect */
  filter: drop-shadow(0 0 15px rgba(254, 202, 87, 0.3));
  
  /* Animation */
  animation: titleGlow 3s ease-in-out infinite alternate;
  
  /* Hover effect */
  transition: all 0.3s ease;
}

.movie-title:hover {
  transform: scale(1.05);
  animation-duration: 1.5s;
}

@keyframes titleGlow {
  0% {
    text-shadow: 
      0 0 10px rgba(254, 202, 87, 0.4),
      0 0 20px rgba(254, 202, 87, 0.3),
      0 0 30px rgba(254, 202, 87, 0.2),
      2px 2px 4px rgba(0, 0, 0, 0.8),
      4px 4px 8px rgba(0, 0, 0, 0.6),
      6px 6px 12px rgba(0, 0, 0, 0.4);
  }
  100% {
    text-shadow: 
      0 0 15px rgba(254, 202, 87, 0.5),
      0 0 25px rgba(254, 202, 87, 0.4),
      0 0 35px rgba(254, 202, 87, 0.3),
      2px 2px 4px rgba(0, 0, 0, 0.8),
      4px 4px 8px rgba(0, 0, 0, 0.6),
      6px 6px 12px rgba(0, 0, 0, 0.4);
  }
}



.nav-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.4);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  font-size: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.nav-arrow::before {
  content: '';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-style: solid;
}

.nav-arrow-left::before {
  left: 8px;
  border-width: 8px 12px 8px 0;
  border-color: transparent rgba(255, 255, 255, 0.8) transparent transparent;
}

.nav-arrow-right::before {
  right: 8px;
  border-width: 8px 0 8px 12px;
  border-color: transparent transparent transparent rgba(255, 255, 255, 0.8);
}

.nav-arrow:hover {
  background: rgba(0, 0, 0, 0.7);
  color: rgba(255, 255, 255, 1);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
}

.nav-arrow:hover::before {
  border-color: transparent rgba(255, 255, 255, 1) transparent transparent;
}

.nav-arrow-right:hover::before {
  border-color: transparent transparent transparent rgba(255, 255, 255, 1);
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
  gap: 15px;
  margin-top: 20px;
  justify-content: center;
  flex-wrap: wrap;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
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
    font-size: 2.2rem;
    letter-spacing: 1px;
    margin-bottom: 15px;
  }
  
  
  
  .nav-arrow {
    width: 45px;
    height: 45px;
    font-size: 18px;
  }
  
  .nav-arrow-left::before {
    left: 6px;
    border-width: 6px 10px 6px 0;
  }
  
  .nav-arrow-right::before {
    right: 6px;
    border-width: 6px 0 6px 10px;
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

