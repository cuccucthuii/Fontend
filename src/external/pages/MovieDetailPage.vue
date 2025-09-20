<template>
  <div class="movie-detail-page" v-if="movie">
    <div class="detail-header">
      <img
        v-if="movie.bannerUrl"
        :src="fullImageUrl(movie.bannerUrl)"
        class="banner"
        alt="Banner phim"
        @error="handleImageError($event, 'banner')"
      />
      <div class="header-content">
        <img
          v-if="movie.posterUrl"
          :src="fullImageUrl(movie.posterUrl)"
          class="poster"
          alt="Poster phim"
          @error="handleImageError($event, 'poster')"
        />
        <div class="info">
          <h1 class="title">{{ movie.tenPhim }}</h1>
          <div class="meta">
            <span class="badge" :class="getStatusClass(movie.trangThai)">{{
              formatStatus(movie.trangThai)
            }}</span>
            <span class="badge">{{ movie.dinhDang }}</span>
            <span class="badge">{{ formatDate(movie.ngayPhatHanh) }}</span>
          </div>
          <div class="genres">
            <span
              v-for="genre in getGenreNames(movie.theLoai)"
              :key="genre"
              class="genre"
              >{{ genre }}</span
            >
          </div>
          <div class="directors">
            <b>Đạo diễn:</b> {{ joinNames(movie.daoDien) }}
          </div>
          <div class="actors">
            <b>Diễn viên:</b> {{ joinNames(movie.dienVien) }}
          </div>
        </div>
      </div>
    </div>
    <div class="detail-body">
      <h2>Mô tả</h2>
      <p class="description">{{ movie.moTa }}</p>
      <div class="row">
        <div><b>Thời lượng:</b> {{ movie.thoiLuong }} phút</div>
        <div><b>Ngày tạo:</b> {{ formatDate(movie.ngayTao) }}</div>
      </div>
      <div v-if="movie.trailerUrl" class="trailer-block">
        <h2>Trailer</h2>
        <video
          controls
          :src="fullImageUrl(movie.trailerUrl)"
          class="trailer-video"
        ></video>
      </div>
    </div>
    <router-link to="/admin/movies" class="btn-back"
      >← Quay lại danh sách phim</router-link
    >
  </div>
  <div v-else class="loading-state">
    <div class="loading-spinner"></div>
    <p>Đang tải dữ liệu phim...</p>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { fetchMovies, fetchGenres } from "../services/movieService";

const route = useRoute();
const movie = ref(null);
const genres = ref([]);
import { BASE_URL } from "@/constants/api";

function fullImageUrl(path) {
  if (!path) return "";

  // Nếu path đã là URL đầy đủ, trả về nguyên
  if (path.startsWith("http")) {
    return path;
  }

  // Nếu path bắt đầu với /uploads, thêm BASE_URL
  if (path.startsWith("/uploads")) {
    return `${BASE_URL}${path}`;
  }

  // Trường hợp khác, thêm BASE_URL và /
  return `${BASE_URL}${path.startsWith("/") ? "" : "/"}${path}`.replace(
    /([^:]\/)\/+/g,
    "$1"
  );
}

function handleImageError(event, type = "image") {
  console.error(
    `❌ [Image Load Error] Không tải được ${type} tại: ${event.target.src}`
  );
  event.target.src = "/logo.png?v=2"; // Fallback image
  event.target.classList.add("image-error");
}

function joinNames(arr) {
  return Array.isArray(arr) ? arr.join(", ") : arr;
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
}

function formatStatus(status) {
  switch (status) {
    case "DANG_CHIEU":
      return "Đang Chiếu";
    case "NGUNG_CHIEU":
      return "Ngừng Chiếu";
    default:
      return status;
  }
}

function getStatusClass(status) {
  return status === "DANG_CHIEU"
    ? "status-active"
    : status === "NGUNG_CHIEU"
    ? "status-inactive"
    : "status-default";
}

function getGenreNames(ids) {
  if (!Array.isArray(ids)) return [];
  return ids.map((id) => {
    const found = genres.value.find((g) => g.id === id || g.id == id);
    return found ? found.name : id;
  });
}

onMounted(async () => {
  try {
    const [res, genreRes] = await Promise.all([fetchMovies(), fetchGenres()]);
    const id = route.params.id;
    movie.value = res.data.find((m) => String(m.idPhim) === String(id));
    genres.value = genreRes.data;
  } catch (e) {
    movie.value = null;
    genres.value = [];
  }
});
</script>
<style scoped>
.movie-detail-page {
  padding: 32px;
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 40px rgba(44, 62, 80, 0.08);
}
.detail-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.banner {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 12px;
}
.header-content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  margin-top: -60px;
}
.poster {
  width: 120px;
  height: 170px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(44, 62, 80, 0.18);
  background: #f8f9fa;
}
.info {
  flex: 1;
}
.title {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #2c3e50;
}
.meta {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}
.badge {
  background: #f3e8ff;
  color: #7c3aed;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}
.status-active {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
}
.status-inactive {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
}
.status-default {
  background: #ecf0f1;
  color: #7f8c8d;
}
.genres {
  margin: 8px 0;
}
.genre {
  background: #e0e7ff;
  color: #3730a3;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  margin-right: 6px;
}
.directors,
.actors {
  font-size: 15px;
  color: #374151;
  margin: 4px 0;
}
.detail-body {
  margin-top: 32px;
}
.description {
  font-size: 16px;
  color: #374151;
  background: #f8fafc;
  padding: 18px;
  border-radius: 10px;
  margin-bottom: 18px;
}
.row {
  display: flex;
  gap: 32px;
  margin-bottom: 18px;
  color: #555;
}
.trailer-block {
  margin-top: 24px;
}
.trailer-video {
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(44, 62, 80, 0.12);
}
.btn-back {
  display: inline-block;
  margin-top: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 12px 28px;
  border-radius: 10px;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.2s;
}
.btn-back:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #7f8c8d;
}
.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #ecf0f1;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.image-error {
  opacity: 0.6;
  filter: grayscale(50%);
  border: 2px dashed #e74c3c;
}
</style>
