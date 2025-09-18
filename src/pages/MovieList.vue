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
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm tên phim..." />
      </div>
      <div class="filter-actions">
        <button class="btn btn-clear-filters" @click="clearFilters" v-if="searchQuery">
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
            <th>Mô Tả</th>
            <th>Thời Lượng</th>
            <th>Ngày Phát Hành</th>
            <th>Trạng Thái</th>
            <th>Định Dạng</th>
            <th>Ngày Tạo</th>
            <th>Tuổi Giới Hạn</th>
            <th>Năm Sản Xuất</th>
            <th>Độ Phổ Biến</th>
            <th>Giá Vé Cơ Bản</th>
            <th>Đạo Diễn</th>
            <th>Diễn Viên Chính</th>
            <th>Thể Loại</th>
            <th>Poster</th>
            <th>Banner</th>
            <th>Trailer</th>
            <th>Chức Năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(movie, index) in filteredMovies" :key="movie.idPhim">
            <td>{{ index + 1 }}</td>
            <td class="movie-title">
              <div class="title-content">
                <span class="movie-icon">🎬</span>
                {{ movie.tenPhim }}
              </div>
            </td>
            <td class="movie-description">
              <div class="description-text" :title="movie.moTa">
                {{ movie.moTa || '-' }}
              </div>
            </td>
            <td class="duration">
              <span class="duration-badge">
                {{ movie.thoiLuong ? movie.thoiLuong + ' phút' : '-' }}
              </span>
            </td>
            <td class="release-date">
              {{ movie.ngayPhatHanh ? formatDate(movie.ngayPhatHanh) : '-' }}
            </td>
            <td class="status">
              <span :class="['status-badge', getStatusClass(movie.trangThai)]">
                {{ getStatusText(movie.trangThai) }}
              </span>
            </td>
            <td class="format">
              <span class="format-badge">
                {{ movie.dinhDang || '-' }}
              </span>
            </td>
            <td class="created-date">
              {{ movie.ngayTao ? formatDate(movie.ngayTao) : '-' }}
            </td>
            <td class="age-limit">
              <span class="age-badge">
                {{ movie.tuoiGioiHan ? movie.tuoiGioiHan + '+' : '-' }}
              </span>
            </td>
            <td class="production-year">
              {{ movie.namSanXuat || '-' }}
            </td>
            <td class="popularity">
              <div class="popularity-bar">
                <div class="popularity-fill" :style="{ width: (movie.doPhoBien || 0) + '%' }"></div>
                <span class="popularity-text">{{ movie.doPhoBien || 0 }}%</span>
              </div>
            </td>
            <td class="base-price">
              <span class="price-badge">
                {{ movie.giaVeCoBan ? formatPrice(movie.giaVeCoBan) : '-' }}
              </span>
            </td>
            <td class="director">
              {{ movie.daoDien || '-' }}
            </td>
            <td class="main-actor">
              {{ movie.dienVienChinh || '-' }}
            </td>
            <td class="genre">
              <span class="genre-badge">
                {{ movie.theLoai || '-' }}
              </span>
            </td>
            <td class="poster">
              <div v-if="movie.posterUrl" class="image-preview">
                <img :src="movie.posterUrl" :alt="movie.tenPhim" class="preview-image" />
              </div>
              <span v-else class="no-image">-</span>
            </td>
            <td class="banner">
              <div v-if="movie.bannerUrl" class="image-preview">
                <img :src="movie.bannerUrl" :alt="movie.tenPhim" class="preview-image" />
              </div>
              <span v-else class="no-image">-</span>
            </td>
            <td class="trailer">
              <button
                v-if="movie.trailerUrl"
                @click="openTrailer(movie.trailerUrl)"
                class="btn-trailer"
              >
                <span class="trailer-icon">▶️</span> Xem
              </button>
              <span v-else class="no-trailer">-</span>
            </td>
            <td class="actions">
              <div class="action-buttons">
                <button @click="viewMovie(movie)" class="btn-action btn-view" title="Xem chi tiết">
                  <span class="action-icon">👁️</span>
                </button>
                <button @click="editMovie(movie)" class="btn-action btn-edit" title="Sửa phim">
                  <span class="action-icon">✏️</span>
                </button>
                <button @click="deleteMovie(movie)" class="btn-action btn-delete" title="Xóa phim">
                  <span class="action-icon">🗑️</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải dữ liệu hoặc không có phim phù hợp.</p>
    </div>

    <!-- Gọi component modal -->
    <TrailerModal
      :show="showTrailerModal"
      :url="currentTrailerUrl"
      @close="showTrailerModal = false"
    />
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import TrailerModal from './TrailerModal.vue'
import { fetchMovies } from '../services/movieService'

const movies = ref([])
const showTrailerModal = ref(false)
const currentTrailerUrl = ref('')
const searchQuery = ref('')

// Hàm load movies
async function loadMovies() {
  try {
    const res = await fetchMovies()
    movies.value = res.data
  } catch (e) {
    console.error('Lỗi tải phim:', e)
  }
}

onMounted(async () => {
  await loadMovies()
  // Lắng nghe sự kiện khi có thay đổi phim từ admin
  window.addEventListener('moviesUpdated', async () => {
    console.log('🔄 Phát hiện thay đổi phim, đang refresh dữ liệu...')
    await loadMovies()
  })
})

function openTrailer(url) {
  currentTrailerUrl.value = url
  showTrailerModal.value = true
}

const filteredMovies = computed(() => {
  if (!searchQuery.value) return movies.value
  return movies.value.filter(m => (m.tenPhim || '').toLowerCase().includes(searchQuery.value.toLowerCase()))
})

const moviesWithTrailer = computed(() => movies.value.filter(m => m.trailerUrl).length)
const moviesWithoutTrailer = computed(() => movies.value.filter(m => !m.trailerUrl).length)

function clearFilters() {
  searchQuery.value = ''
}

// Helper functions for formatting data
function formatDate(dateString) {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN')
  } catch (e) {
    return dateString
  }
}

function formatPrice(price) {
  if (!price) return '-'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

function getStatusText(status) {
  const statusMap = {
    'DANG_CHIEU': 'Đang chiếu',
    'SAP_CHIEU': 'Sắp chiếu',
    'NGUNG_CHIEU': 'Ngừng chiếu',
    'HOAN_THANH': 'Hoàn thành'
  }
  return statusMap[status] || status || '-'
}

function getStatusClass(status) {
  const classMap = {
    'DANG_CHIEU': 'status-showing',
    'SAP_CHIEU': 'status-coming',
    'NGUNG_CHIEU': 'status-stopped',
    'HOAN_THANH': 'status-completed'
  }
  return classMap[status] || 'status-default'
}

// Action functions
function viewMovie(movie) {
  // Navigate to movie detail page
  console.log('Xem chi tiết phim:', movie.tenPhim)
  // You can add navigation logic here
}

function editMovie(movie) {
  // Navigate to edit movie page
  console.log('Sửa phim:', movie.tenPhim)
  // You can add navigation logic here
}

function deleteMovie(movie) {
  if (confirm(`Bạn có chắc chắn muốn xóa phim "${movie.tenPhim}"?`)) {
    console.log('Xóa phim:', movie.tenPhim)
    // You can add delete logic here
  }
}
</script>

<style scoped>
.movie-page {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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
.header-content { flex: 1; }
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
.header-icon { font-size: 32px; }
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
  content: '';
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
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
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
.filter-icon { font-size: 1rem; opacity: 0.8; }
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
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}
.filter-input:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
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
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
  margin-bottom: 32px;
}
table {
  width: 100%;
  min-width: 2000px;
  border-collapse: collapse;
  background-color: #fff;
  table-layout: auto;
}
th, td {
  border: 1px solid #e2e8f0;
  padding: 12px 8px;
  text-align: left;
  color: #333;
  font-size: 13px;
  vertical-align: top;
}
th {
  background-color: #f8f9fa;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
  position: sticky;
  top: 0;
  z-index: 10;
}
tr:nth-child(even) { background-color: #f9f9f9; }

/* Column-specific styling */
.movie-title {
  min-width: 150px;
  max-width: 200px;
}
.title-content {
  display: flex;
  align-items: center;
  gap: 8px;
}
.movie-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.movie-description {
  min-width: 200px;
  max-width: 250px;
}
.description-text {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  white-space: normal;
}

.duration, .age-limit, .production-year {
  text-align: center;
  min-width: 80px;
}
.duration-badge, .age-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.release-date, .created-date {
  min-width: 100px;
  text-align: center;
}

.status {
  min-width: 100px;
  text-align: center;
}
.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}
.status-showing {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}
.status-coming {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}
.status-stopped {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}
.status-completed {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
}
.status-default {
  background: #e5e7eb;
  color: #374151;
}

.format {
  text-align: center;
  min-width: 80px;
}
.format-badge {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.popularity {
  min-width: 120px;
}
.popularity-bar {
  position: relative;
  background: #e5e7eb;
  border-radius: 8px;
  height: 20px;
  overflow: hidden;
}
.popularity-fill {
  height: 100%;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  transition: width 0.3s ease;
}
.popularity-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 10px;
  font-weight: 600;
  color: #374151;
}

.base-price {
  text-align: center;
  min-width: 100px;
}
.price-badge {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}


.director, .main-actor {
  min-width: 120px;
  max-width: 150px;
}

.genre {
  text-align: center;
  min-width: 100px;
}
.genre-badge {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.poster, .banner {
  text-align: center;
  min-width: 80px;
}
.image-preview {
  width: 50px;
  height: 70px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.no-image {
  color: #9ca3af;
  font-size: 12px;
}

.trailer {
  text-align: center;
  min-width: 80px;
}

.actions {
  text-align: center;
  min-width: 120px;
}
.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
  align-items: center;
}
.btn-action {
  border: none;
  border-radius: 6px;
  padding: 6px 8px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-view {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}
.btn-view:hover {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%);
  transform: scale(1.1);
}
.btn-edit {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}
.btn-edit:hover {
  background: linear-gradient(135deg, #d97706 0%, #f59e0b 100%);
  transform: scale(1.1);
}
.btn-delete {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}
.btn-delete:hover {
  background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%);
  transform: scale(1.1);
}
.action-icon {
  font-size: 14px;
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
  box-shadow: 0 2px 8px rgba(72,219,251,0.08);
  transition: all 0.3s;
}
.btn-trailer:hover {
  background: linear-gradient(135deg, #0abde3 0%, #48dbfb 100%);
  transform: scale(1.06);
  box-shadow: 0 4px 16px rgba(72,219,251,0.18);
}
.trailer-icon {
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
@media (max-width: 1200px) {
  .table-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  table {
    min-width: 2000px;
  }
}

@media (max-width: 900px) {
  .page-header, .stats-section, .filters-section, .table-container {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }
  .stats-section {
    grid-template-columns: 1fr;
  }
  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }
  .filter-group {
    min-width: auto;
  }
  table {
    min-width: 2000px;
  }
  th, td {
    padding: 8px 4px;
    font-size: 11px;
  }
  .description-text {
    -webkit-line-clamp: 2;
  }
}
@keyframes slideInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
