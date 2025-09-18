<template>
  <Header />
  <div class="showtimes-page">
    <div class="showtimes-hero">
      <img
        src="https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=1200&q=80"
        alt="Showtimes Banner"
        class="showtimes-hero-img"
      />
      <div class="showtimes-hero-content">
        <h1>Lịch Chiếu Phim</h1>
        <p>
          Chọn rạp, ngày và phim để xem lịch chiếu chi tiết và đặt vé nhanh
          chóng!
        </p>
      </div>
    </div>
    <div class="showtimes-filter">
      <select v-model="selectedCinemaFilter">
        <option value="">Tất cả rạp</option>
        <option v-for="cinema in cinemas" :key="cinema" :value="cinema">
          {{ cinema }}
        </option>
      </select>
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
      <div
        v-for="movie in filteredMovies"
        :key="movie.idPhim"
        class="showtimes-movie-card"
      >
        <img
          :src="getPosterUrl(movie.posterUrl)"
          :alt="movie.tenPhim"
          class="showtimes-movie-img"
          @click="goToMovieDetail(movie)"
          style="cursor: pointer"
        />
        <div class="showtimes-movie-info">
          <h2 @click="goToMovieDetail(movie)" style="cursor: pointer">
            {{ movie.tenPhim }}
          </h2>
          <div class="showtimes-movie-meta">
            {{ movie.theLoai ? movie.theLoai.join(", ") : "Chưa phân loại" }} |
            {{ movie.thoiLuong ? movie.thoiLuong + " phút" : "Chưa cập nhật" }}
          </div>
          <div
            class="showtimes-movie-status"
            :class="getStatusClass(movie.trangThai)"
          >
            {{ formatStatus(movie.trangThai) }}
          </div>
          <button class="btn-book" @click="openCinemaModal(movie)">
            Đặt vé
          </button>
        </div>
      </div>
    </div>

    <!-- Cinema Selection Modal -->
    <CinemaSelectModal
      :visible="showCinemaModal"
      :mode="cinemaModalMode"
      @close="showCinemaModal = false"
      @showtime-flow="handleCinemaSelection"
    />

    <!-- Showtime Modal -->
    <ShowtimeModal
      :visible="showShowtimeModal"
      :movie="selectedMovieForShowtime"
      :cinema="selectedCinemaForShowtime"
      @close="showShowtimeModal = false"
      @selectShowtime="handleShowtimeSelection"
    />
  </div>
  <HomeFooter />
</template>

<script setup>
import HomeFooter from "@/components/HomeFooter.vue";
import ShowtimeModal from "@/components/ShowtimeModal.vue";
import CinemaSelectModal from "@/components/CinemaSelectModal.vue";
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { fetchMovies } from "../../services/movieService";
import { BASE_URL } from "@/constants/api";

const cinemas = [
  "DEV Cinema Hà Nội",
  "DEV Cinema Sài Gòn",
  "DEV Cinema Đà Nẵng",
];
const selectedCinemaFilter = ref("");
const selectedDate = ref("");
const search = ref("");

const router = useRouter();
const movies = ref([]);
const loadingMovies = ref(true);

// Cinema Modal
const showCinemaModal = ref(false);
const cinemaModalMode = ref('showtime');

// Showtime Modal  
const showShowtimeModal = ref(false);
const selectedMovieForShowtime = ref(null);
const selectedCinemaForShowtime = ref(null);

// Hàm load movies
async function loadMovies() {
  loadingMovies.value = true;
  try {
    const res = await fetchMovies();
    movies.value = res.data;
  } catch (error) {
    console.error("Lỗi tải phim:", error);
  } finally {
    loadingMovies.value = false;
  }
}

onMounted(async () => {
  await loadMovies();

  // Lắng nghe sự kiện khi có thay đổi phim từ admin
  window.addEventListener("moviesUpdated", async () => {
    console.log("🔄 Phát hiện thay đổi phim, đang refresh dữ liệu...");
    await loadMovies();
  });
});

const filteredMovies = computed(() => {
  return movies.value.filter(
    (m) =>
      (!search.value ||
        m.tenPhim.toLowerCase().includes(search.value.toLowerCase())) &&
      m.trangThai === "DANG_CHIEU" // Chỉ hiển thị phim đang chiếu
  );
});

function getPosterUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  return (
    BASE_URL + url
  );
}

function getStatusClass(status) {
  const statusClasses = {
    DANG_CHIEU: "status-active",
    NGUNG_CHIEU: "status-inactive",
  };
  return statusClasses[status] || "status-default";
}

function formatStatus(status) {
  const statusMap = {
    DANG_CHIEU: "Đang chiếu",
    NGUNG_CHIEU: "Ngừng chiếu",
  };
  return statusMap[status] || status;
}

function openCinemaModal(movie) {
  console.log("🎬 ShowtimesPage: Opening cinema modal for movie:", movie);
  console.log("🎬 ShowtimesPage: Current route:", window.location.pathname);
  selectedMovieForShowtime.value = movie;
  showCinemaModal.value = true;
}

function handleCinemaSelection(cinema) {
  console.log("🎬 ShowtimesPage: Cinema selected for showtime flow:", cinema);
  selectedCinemaForShowtime.value = cinema;
  showCinemaModal.value = false;
  showShowtimeModal.value = true;
  console.log("🎬 Opening showtime modal with:", {
    movie: selectedMovieForShowtime.value,
    cinema: selectedCinemaForShowtime.value
  });
}

function openShowtimeModal(movie) {
  selectedMovieForShowtime.value = movie;
  showShowtimeModal.value = true;
}

function handleShowtimeSelection(showtimeData) {
  console.log("🎯 ShowtimesPage: Handling showtime selection:", showtimeData);
  
  // Redirect to booking page with pre-filled data
  router.push({
    name: "Booking",
    query: {
      movieId: selectedMovieForShowtime.value.idPhim,
      cinemaId: selectedCinemaForShowtime.value.idRapChieu,
      scheduleId: showtimeData.id,
      date: showtimeData.date,
      time: showtimeData.time,
      cinema: selectedCinemaForShowtime.value.tenRapChieu,
    },
  });
}

function goToMovieDetail(movie) {
  router.push(`/movie/${movie.idPhim}`);
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
.showtimes-filter input[type="date"],
.showtimes-filter input[type="text"] {
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
  box-shadow: 0 4px 24px rgba(72, 219, 251, 0.1);
  display: flex;
  gap: 18px;
  align-items: flex-start;
  padding: 18px;
  transition: box-shadow 0.2s, transform 0.2s;
}
.showtimes-movie-card:hover {
  box-shadow: 0 8px 32px rgba(72, 219, 251, 0.18);
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
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
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

.status-inactive {
  background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
  color: #fff;
}

.status-default {
  background: #636e72;
  color: #fff;
}

@media (max-width: 700px) {
  .showtimes-list {
    grid-template-columns: 1fr;
  }
  .showtimes-movie-card {
    flex-direction: column;
    align-items: center;
  }
  .showtimes-movie-img {
    width: 90px;
    height: 120px;
  }
}
</style>
