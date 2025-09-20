<template>
  <section class="movie-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🎬</span>
          Quản lý Bài Viết Phim
        </h1>
        <p class="page-subtitle">Danh sách phim và trailer</p>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🎬</div>
        <div>
          <div class="stat-number">{{ movies.length }}</div>
          <div class="stat-label">Tổng số phim</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎞️</div>
        <div>
          <div class="stat-number">{{ moviesWithTrailer }}</div>
          <div class="stat-label">Có trailer</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🚫</div>
        <div>
          <div class="stat-number">{{ moviesWithoutTrailer }}</div>
          <div class="stat-label">Chưa có trailer</div>
        </div>
      </div>
    </div>

    <!-- Filter & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"
          ><span class="filter-icon">🔍</span> Tìm kiếm</label
        >
        <input
          v-model="searchQuery"
          class="filter-input"
          placeholder="Tìm tên phim..."
        />
      </div>
      <div class="filter-actions">
        <button
          class="btn btn-clear-filters"
          @click="clearFilters"
          v-if="searchQuery"
        >
          <span class="btn-icon">🔄</span>
          <span class="btn-text">Xóa bộ lọc</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <table v-if="filteredMovies.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Phim</th>
            <th>Trailer</th>
            <th>Lịch chiếu</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(movie, index) in filteredMovies" :key="movie.idPhim">
            <td>{{ index + 1 }}</td>
            <td>{{ movie.tenPhim }}</td>
            <td>
              <button
                v-if="movie.trailerUrl"
                @click="openTrailer(movie.trailerUrl)"
                class="btn-trailer"
              >
                <span class="trailer-icon">▶️</span> Xem Trailer
              </button>
              <span v-else class="no-trailer">-</span>
            </td>
            <td>
              <button @click="openShowtimeModal(movie)" class="btn-showtime">
                <span class="showtime-icon">📅</span> Lịch chiếu
              </button>
            </td>
            <td>
              <span
                :class="[
                  'trailer-badge',
                  movie.trailerUrl ? 'has-trailer' : 'no-trailer',
                ]"
              >
                {{ movie.trailerUrl ? "Có trailer" : "Chưa có trailer" }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">
        Đang tải dữ liệu hoặc không có phim phù hợp.
      </p>
    </div>

    <!-- Gọi component modal -->
    <TrailerModal
      :show="showTrailerModal"
      :url="currentTrailerUrl"
      @close="showTrailerModal = false"
    />

    <!-- Showtime Modal -->
    <ShowtimeModal
      :visible="showShowtimeModal"
      :movie="selectedMovieForShowtime"
      @close="showShowtimeModal = false"
      @selectShowtime="handleShowtimeSelection"
    />
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import TrailerModal from "./TrailerModal.vue";
import ShowtimeModal from "../components/ShowtimeModal.vue";
import { fetchMovies } from "../services/movieService";

const router = useRouter();
const movies = ref([]);
const showTrailerModal = ref(false);
const currentTrailerUrl = ref("");
const searchQuery = ref("");

// Showtime Modal
const showShowtimeModal = ref(false);
const selectedMovieForShowtime = ref(null);

// Hàm load movies
async function loadMovies() {
  try {
    const res = await fetchMovies();
    movies.value = res.data;
  } catch (e) {
    console.error("Lỗi tải phim:", e);
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

function openTrailer(url) {
  currentTrailerUrl.value = url;
  showTrailerModal.value = true;
}

const filteredMovies = computed(() => {
  if (!searchQuery.value) return movies.value;
  return movies.value.filter((m) =>
    (m.tenPhim || "").toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const moviesWithTrailer = computed(
  () => movies.value.filter((m) => m.trailerUrl).length
);
const moviesWithoutTrailer = computed(
  () => movies.value.filter((m) => !m.trailerUrl).length
);

function clearFilters() {
  searchQuery.value = "";
}

// Showtime Modal Functions
function openShowtimeModal(movie) {
  selectedMovieForShowtime.value = movie;
  showShowtimeModal.value = true;
}

function handleShowtimeSelection(showtimeData) {
  // Redirect to booking page with pre-filled data
  router.push({
    name: "Booking",
    query: {
      movieId: selectedMovieForShowtime.value.idPhim,
      date: showtimeData.date,
      time: showtimeData.time,
      cinema: showtimeData.cinema,
    },
  });
}
</script>

<style scoped>
.movie-page {
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  padding: 32px;
}
.page-header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-bottom: 32px;
  background: white;
  padding: 24px 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}
.header-content {
  flex: 1;
}
.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.header-icon {
  font-size: 32px;
}
.page-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 400;
}
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}
.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}
.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}
.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}
.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-top: 4px;
}
.filters-section {
  display: flex;
  gap: 1.5rem;
  align-items: end;
  flex-wrap: wrap;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  margin-bottom: 1.5rem;
}
.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  min-width: 200px;
  flex: 1;
  animation: slideInUp 0.5s ease-out;
}
.filter-label {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1e293b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.25rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.filter-icon {
  font-size: 1rem;
  opacity: 0.8;
}
.filter-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  background: white;
  color: #374151;
  font-weight: 500;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.filter-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1),
    0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}
.filter-input:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}
.btn.btn-clear-filters {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn.btn-clear-filters:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
}
.table-container {
  width: 100%;
  overflow-x: auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}
table {
  width: 100%;
  min-width: 700px;
  border-collapse: collapse;
  background-color: #fff;
  table-layout: auto;
}
th,
td {
  border: 1px solid #e2e8f0;
  padding: 12px 18px;
  text-align: left;
  color: #333;
  white-space: nowrap;
  font-size: 15px;
}
th {
  background-color: #f8f9fa;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}
tr:nth-child(even) {
  background-color: #f9f9f9;
}
.btn-trailer {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  border: none;
  padding: 8px 18px;
  color: white;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 700;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.08);
  transition: all 0.3s;
}
.btn-trailer:hover {
  background: linear-gradient(135deg, #0abde3 0%, #48dbfb 100%);
  transform: scale(1.06);
  box-shadow: 0 4px 16px rgba(72, 219, 251, 0.18);
}
.trailer-icon {
  font-size: 1.2em;
}

.btn-showtime {
  background: linear-gradient(135deg, #bfa2db 0%, #b7e4c7 100%);
  border: none;
  padding: 8px 18px;
  color: #333;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 700;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(191, 162, 219, 0.08);
  transition: all 0.3s;
}

.btn-showtime:hover {
  background: linear-gradient(135deg, #a78bfa 0%, #10b981 100%);
  transform: scale(1.06);
  box-shadow: 0 4px 16px rgba(191, 162, 219, 0.18);
}

.showtime-icon {
  font-size: 1.2em;
}
.trailer-badge {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  margin-right: 4px;
  display: inline-block;
}
.has-trailer {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}
.no-trailer {
  background: #ecf0f1;
  color: #7f8c8d;
}
.loading-state {
  text-align: center;
  padding: 40px 0;
  color: #7f8c8d;
  font-size: 16px;
}
.genre-management-link {
  margin: 24px 0 0 0;
  text-align: right;
}
.btn-genre {
  background: linear-gradient(135deg, #ffb347 0%, #ffcc33 100%);
  color: #222;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-left: 12px;
}
.btn-genre:hover {
  background: linear-gradient(135deg, #ffcc33 0%, #ffb347 100%);
  transform: translateY(-2px);
}
.btn-action {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-action:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: scale(1.06);
}
@media (max-width: 900px) {
  .page-header,
  .stats-section,
  .filters-section,
  .table-container {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }
  .stats-section {
    grid-template-columns: 1fr;
  }
  table {
    min-width: 400px;
  }
}
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
