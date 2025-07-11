<template>
  <HomeHeader />
  <div class="showtimes-page">
    <div class="showtimes-hero">
      <img src="https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=1200&q=80" alt="Showtimes Banner" class="showtimes-hero-img" />
      <div class="showtimes-hero-content">
        <h1>Lịch Chiếu Phim</h1>
        <p>Chọn rạp, ngày và phim để xem lịch chiếu chi tiết và đặt vé nhanh chóng!</p>
      </div>
    </div>
    <div class="showtimes-filter">
      <select v-model="selectedCinema">
        <option value="">Tất cả rạp</option>
        <option v-for="cinema in cinemas" :key="cinema" :value="cinema">{{ cinema }}</option>
      </select>
      <input type="date" v-model="selectedDate" />
      <input type="text" v-model="search" placeholder="Tìm phim..." />
    </div>
    <div class="showtimes-list">
      <div v-for="movie in filteredMovies" :key="movie.id" class="showtimes-movie-card">
        <img :src="movie.poster" :alt="movie.title" class="showtimes-movie-img" />
        <div class="showtimes-movie-info">
          <h2>{{ movie.title }}</h2>
          <div class="showtimes-movie-meta">{{ movie.genre }} | {{ movie.duration }} phút</div>
          <div class="showtimes-sessions">
            <span v-for="session in movie.sessions" :key="session.time" class="showtimes-session">
              {{ session.time }} - {{ session.cinema }}
            </span>
          </div>
          <button class="btn-book" @click="bookMovie(movie)">Đặt vé</button>
        </div>
      </div>
    </div>
  </div>
  <HomeFooter />
</template>

<script setup>
import HomeHeader from '@/components/HomeHeader.vue'
import HomeFooter from '@/components/HomeFooter.vue'
import { ref, computed } from 'vue'

const cinemas = ['DEV Cinema Hà Nội', 'DEV Cinema Sài Gòn', 'DEV Cinema Đà Nẵng']
const selectedCinema = ref('')
const selectedDate = ref('')
const search = ref('')

const movies = ref([
  {
    id: 1,
    title: 'Godzilla x Kong',
    genre: 'Hành động, Quái vật',
    duration: 120,
    poster: 'https://image.tmdb.org/t/p/w500/2vFuG6bWGyQUzYS9d69E5l85nIz.jpg',
    sessions: [
      { time: '09:00', cinema: 'DEV Cinema Hà Nội' },
      { time: '14:00', cinema: 'DEV Cinema Sài Gòn' }
    ]
  },
  {
    id: 2,
    title: 'Dune: Part Two',
    genre: 'Phiêu lưu, Khoa học viễn tưởng',
    duration: 150,
    poster: 'https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg',
    sessions: [
      { time: '11:00', cinema: 'DEV Cinema Hà Nội' },
      { time: '16:00', cinema: 'DEV Cinema Đà Nẵng' }
    ]
  }
])

const filteredMovies = computed(() => {
  return movies.value.filter(m =>
    (!selectedCinema.value || m.sessions.some(s => s.cinema === selectedCinema.value)) &&
    (!search.value || m.title.toLowerCase().includes(search.value.toLowerCase()))
  )
})

function bookMovie(movie) {
  alert(`Đặt vé cho phim: ${movie.title}`)
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
}
.showtimes-movie-info {
  flex: 1;
}
.showtimes-movie-info h2 {
  font-size: 1.3rem;
  font-weight: 800;
  margin-bottom: 6px;
  color: #48dbfb;
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
@media (max-width: 700px) {
  .showtimes-list { grid-template-columns: 1fr; }
  .showtimes-movie-card { flex-direction: column; align-items: center; }
  .showtimes-movie-img { width: 90px; height: 120px; }
}
</style> 