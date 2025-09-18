<template>
  <div class="movie-detail-admin-container">
    <button class="btn-back" @click="goBack">← Quay lại danh sách phim</button>
    <div class="movie-detail-card">
      <div class="movie-poster">
        <img :src="movie.poster" :alt="movie.title" />
      </div>
      <div class="movie-info">
        <h1 class="movie-title">{{ movie.title }}</h1>
        <div class="movie-meta">
          <span class="badge genre">{{ movie.genre }}</span>
          <span class="badge status" :class="movie.status === 'Đang chiếu' ? 'active' : 'inactive'">{{ movie.status }}</span>
          <span class="badge duration">{{ movie.duration }} phút</span>
        </div>
        <div class="movie-desc">
          <h3>Mô tả</h3>
          <p>{{ movie.description }}</p>
        </div>
        <div class="movie-details">
          <div><b>Ngày khởi chiếu:</b> {{ movie.releaseDate }}</div>
          <div><b>Đạo diễn:</b> {{ movie.director }}</div>
          <div><b>Diễn viên:</b> {{ movie.actors }}</div>
          <div><b>Quốc gia:</b> {{ movie.country }}</div>
        </div>
        <div class="movie-trailer">
          <h3>Trailer</h3>
          <iframe v-if="movie.trailer" :src="movie.trailer" frameborder="0" allowfullscreen></iframe>
          <div v-else>Chưa có trailer</div>
        </div>
        <div class="movie-showtimes">
          <h3>Lịch chiếu liên quan</h3>
          <ul>
            <li v-for="show in movie.showtimes" :key="show.id">
              {{ show.date }} - {{ show.time }} - Phòng: {{ show.room }} - Vé còn: {{ show.ticketsLeft }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function goBack() {
  router.back()
}
// Mock data
const movie = {
  title: 'Avengers: Endgame',
  poster: 'https://upload.wikimedia.org/wikipedia/vi/f/f9/Avengers_Endgame_poster.jpg',
  genre: 'Hành động, Khoa học viễn tưởng',
  status: 'Đang chiếu',
  duration: 181,
  description: 'Sau khi Thanos xóa sổ một nửa sự sống trong vũ trụ, các Avengers còn lại phải hợp lực để đảo ngược hành động của hắn.',
  releaseDate: '26/04/2019',
  director: 'Anthony Russo, Joe Russo',
  actors: 'Robert Downey Jr., Chris Evans, Mark Ruffalo, Chris Hemsworth',
  country: 'Mỹ',
  trailer: 'https://www.youtube.com/embed/TcMBFSGVi1c',
  showtimes: [
    { id: 1, date: '2024-06-10', time: '19:00', room: '1', ticketsLeft: 20 },
    { id: 2, date: '2024-06-11', time: '21:00', room: '2', ticketsLeft: 5 },
    { id: 3, date: '2024-06-12', time: '18:00', room: '3', ticketsLeft: 0 },
  ]
}
</script>

<style scoped>
.movie-detail-admin-container {
  max-width: 1100px;
  margin: 32px auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(44,62,80,0.13);
  padding: 32px 24px 40px 24px;
}
.btn-back {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 24px;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-back:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
.movie-detail-card {
  display: flex;
  gap: 36px;
  align-items: flex-start;
}
.movie-poster img {
  width: 260px;
  border-radius: 12px;
  box-shadow: 0 4px 18px rgba(102,126,234,0.13);
}
.movie-info {
  flex: 1;
}
.movie-title {
  font-size: 2.1em;
  font-weight: 800;
  margin-bottom: 10px;
  color: #2c3e50;
}
.movie-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;
}
.badge {
  padding: 7px 16px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  background: #e0e7ff;
  color: #333;
}
.badge.status.active {
  background: linear-gradient(90deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
}
.badge.status.inactive {
  background: linear-gradient(90deg, #b2bec3 0%, #636e72 100%);
  color: #fff;
}
.badge.genre {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
.badge.duration {
  background: #feca57;
  color: #fff;
}
.movie-desc {
  margin-bottom: 18px;
}
.movie-desc h3 {
  margin: 0 0 6px 0;
  font-size: 1.1em;
  color: #764ba2;
}
.movie-details {
  margin-bottom: 18px;
  color: #444;
  font-size: 15px;
}
.movie-trailer {
  margin-bottom: 18px;
}
.movie-trailer h3 {
  color: #764ba2;
  font-size: 1.1em;
  margin-bottom: 8px;
}
.movie-trailer iframe {
  width: 380px;
  height: 215px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(44,62,80,0.13);
}
.movie-showtimes h3 {
  color: #764ba2;
  font-size: 1.1em;
  margin-bottom: 8px;
}
.movie-showtimes ul {
  padding-left: 18px;
  color: #333;
  font-size: 15px;
}
@media (max-width: 900px) {
  .movie-detail-card {
    flex-direction: column;
    align-items: stretch;
  }
  .movie-poster img {
    width: 100%;
    max-width: 320px;
    margin: 0 auto 18px auto;
    display: block;
  }
  .movie-trailer iframe {
    width: 100%;
    max-width: 100vw;
    height: 200px;
  }
}
</style> 