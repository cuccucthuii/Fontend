<template>
  <div class="movie-page">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="icon">🎬</span>
          Quản lý Phim
        </h1>
        <p class="page-subtitle">
          Quản lý và theo dõi tất cả phim trong hệ thống
        </p>
      </div>
      <div class="header-actions">
        <router-link to="/admin/movies/add" class="btn btn-primary">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm Phim</span>
        </router-link>
        <router-link to="/admin/genres" class="btn btn-secondary">
          <span class="btn-icon">🏷️</span>
          <span class="btn-text">Thể loại</span>
        </router-link>
        <router-link to="/admin/movies/trash" class="btn btn-secondary">
          <span class="btn-icon">🗑️</span>
          <span class="btn-text">Thùng rác</span>
        </router-link>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🎬</div>
        <div class="stat-content">
          <div class="stat-number">{{ movies.length }}</div>
          <div class="stat-label">Tổng số phim</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎭</div>
        <div class="stat-content">
          <div class="stat-number">{{ activeMovies }}</div>
          <div class="stat-label">Đang chiếu</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏸️</div>
        <div class="stat-content">
          <div class="stat-number">{{ inactiveMovies }}</div>
          <div class="stat-label">Ngừng chiếu</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalPages }}</div>
          <div class="stat-label">Trang dữ liệu</div>
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-section">
      <div class="table-header">
        <h3 class="table-title">Danh sách phim</h3>
        <div class="table-actions">
          <!-- Enhanced Filters -->
          <div class="filters-section">
            <div class="filter-group">
              <label class="filter-label">
                <span class="filter-icon">🎬</span>
                Trạng thái
              </label>
              <div class="filter-dropdown">
                <select v-model="statusFilter" class="filter-select">
                  <option value="">Tất cả trạng thái</option>
                  <option value="DANG_CHIEU">🎭 Đang chiếu</option>
                  <option value="NGUNG_CHIEU">⏸️ Ngừng chiếu</option>
                </select>
                <span class="filter-arrow">▼</span>
              </div>
            </div>

            <div class="filter-group">
              <label class="filter-label">
                <span class="filter-icon">🎭</span>
                Thể loại
              </label>
              <div class="filter-dropdown">
                <select v-model="categoryFilter" class="filter-select">
                  <option value="">Tất cả thể loại</option>
                  <option
                    v-for="category in uniqueCategories"
                    :key="category"
                    :value="category"
                  >
                    🎬 {{ category }}
                  </option>
                </select>
                <span class="filter-arrow">▼</span>
              </div>
            </div>

            <div class="filter-group">
              <label class="filter-label">
                <span class="filter-icon">🎥</span>
                Định dạng
              </label>
              <div class="filter-dropdown">
                <select v-model="formatFilter" class="filter-select">
                  <option value="">Tất cả định dạng</option>
                  <option value="2D">🎬 2D</option>
                  <option value="3D">🥽 3D</option>
                  <option value="IMAX">🎪 IMAX</option>
                </select>
                <span class="filter-arrow">▼</span>
              </div>
            </div>

            <div class="filter-actions">
              <button
                class="btn btn-clear-filters"
                @click="clearFilters"
                v-if="hasActiveFilters"
              >
                <span class="btn-icon">🔄</span>
                <span class="btn-text">Xóa bộ lọc</span>
              </button>

              <button
                class="btn btn-export"
                @click="exportData"
                :disabled="isExporting"
              >
                <span v-if="isExporting" class="loading-spinner-btn"></span>
                <span v-else class="btn-icon">📊</span>
                <span class="btn-text">{{
                  isExporting ? "Đang xuất..." : "Xuất Excel"
                }}</span>
              </button>
            </div>
          </div>

          <div class="search-section">
            <div class="search-box">
              <input
                type="text"
                placeholder="Tìm kiếm phim..."
                v-model="searchQuery"
                class="search-input"
              />
              <span class="search-icon">🔍</span>
            </div>
            <div class="search-stats" v-if="searchQuery || hasActiveFilters">
              <span class="stats-text">
                Tìm thấy {{ filteredMovies.length }} phim
                <span v-if="searchQuery">cho "{{ searchQuery }}"</span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table v-if="filteredMovies.length" class="data-table">
          <thead>
            <tr>
              <th class="th-stt">STT</th>
              <th class="th-title">Tên Phim</th>
              <th class="th-description">Mô tả</th>
              <th class="th-duration">Thời lượng</th>
              <th class="th-release">Ngày phát hành</th>
              <th class="th-status">Trạng thái</th>
              <th class="th-format">Định dạng</th>
              <th class="th-created">Ngày tạo</th>
              <th class="th-director">Đạo diễn</th>
              <th class="th-actors">Diễn viên chính</th>
              <th class="th-category">Thể loại</th>
              <th class="th-poster">Poster</th>
              <th class="th-banner">Banner</th>
              <th class="th-trailer">Trailer</th>
              <th class="th-actions">Chức năng</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(movie, index) in paginatedMovies"
              :key="movie.idPhim"
              class="table-row"
            >
              <td class="td-stt">{{ calculateIndex(index) }}</td>

              <td class="td-title">
                <div class="movie-info">
                  <div class="movie-avatar">🎬</div>
                  <span class="movie-name">{{ movie.tenPhim }}</span>
                </div>
              </td>

              <td class="td-description">
                <div class="description-text" :title="movie.moTa">
                  {{ truncateText(movie.moTa, 50) }}
                </div>
              </td>

              <td class="td-duration">{{ movie.thoiLuong }} phút</td>
              <td class="td-release">{{ formatDate(movie.ngayPhatHanh) }}</td>

              <td class="td-status">
                <span
                  class="status-badge"
                  :class="getStatusClass(movie.trangThai)"
                >
                  {{ formatStatus(movie.trangThai) }}
                </span>
              </td>

              <td class="td-format">{{ movie.dinhDang }}</td>
              <td class="td-created">{{ formatDate(movie.ngayTao) }}</td>

              <td class="td-director">
                <div class="text-list">{{ joinNames(movie.daoDien) }}</div>
              </td>

              <td class="td-actors">
                <div class="text-list">{{ joinNames(movie.dienVien) }}</div>
              </td>

              <td class="td-category">
                <div class="text-list">{{ joinNames(movie.theLoai) }}</div>
              </td>

              <td class="td-poster">
                <div class="image-container">
                  <img
                    v-if="movie.posterUrl"
                    :src="fullImageUrl(movie.posterUrl)"
                    alt="Poster phim"
                    class="movie-poster"
                    loading="lazy"
                    @error="handleImageError($event, movie.posterUrl, 'poster')"
                  />
                  <div v-else class="no-image">📷</div>
                </div>
              </td>

              <td class="td-banner">
                <div class="image-container">
                  <img
                    v-if="movie.bannerUrl"
                    :src="fullImageUrl(movie.bannerUrl)"
                    alt="Banner phim"
                    class="movie-banner"
                    loading="lazy"
                    @error="handleImageError($event, movie.bannerUrl, 'banner')"
                  />
                  <div v-else class="no-image">🖼️</div>
                </div>
              </td>

              <td class="td-trailer">
                <button
                  v-if="movie.trailerUrl"
                  @click="openTrailer(movie.trailerUrl)"
                  class="trailer-btn"
                  title="Xem trailer"
                >
                  <span class="trailer-icon">▶️</span>
                  <span class="trailer-text">Xem</span>
                </button>
                <span v-else class="no-trailer">-</span>
              </td>

              <td class="td-actions">
                <div class="action-buttons">
                  <router-link
                    :to="`/admin/movies/edit/${movie.idPhim}`"
                    class="action-btn edit-btn"
                    title="Chỉnh sửa"
                  >
                    <span class="action-icon">✏️</span>
                  </router-link>
                  <router-link
                    :to="`/admin/movies/${movie.idPhim}`"
                    class="action-btn view-btn"
                    title="Xem chi tiết"
                  >
                    <span class="action-icon">👁️</span>
                  </router-link>
                  <button
                    class="action-btn delete-btn"
                    @click="onDelete(movie)"
                    title="Xóa"
                  >
                    <span class="action-icon">❌</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else-if="searchQuery" class="empty-state">
          <div class="empty-icon">🔍</div>
          <h3>Không tìm thấy kết quả</h3>
          <p>Không có phim nào phù hợp với từ khóa "{{ searchQuery }}"</p>
        </div>

        <div v-else class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu...</p>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages > 1">
      <div class="pagination-info">
        <span class="pagination-text">
          Hiển thị {{ (currentPage - 1) * pageSize + 1 }} -
          {{ Math.min(currentPage * pageSize, filteredMovies.length) }} trong
          tổng số {{ filteredMovies.length }} phim
        </span>
      </div>

      <div class="pagination-controls">
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          class="pagination-btn"
        >
          <span class="pagination-icon">⬅️</span>
          <span class="pagination-text">Trước</span>
        </button>

        <div class="page-numbers">
          <button
            v-for="page in visiblePages"
            :key="page"
            @click="goToPage(page)"
            :class="['page-btn', { active: page === currentPage }]"
          >
            {{ page }}
          </button>
        </div>

        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="pagination-btn"
        >
          <span class="pagination-text">Sau</span>
          <span class="pagination-icon">➡️</span>
        </button>
      </div>
    </div>

    <!-- Modal xem trailer -->
    <TrailerModal
      :show="showTrailerModal"
      :url="currentTrailerUrl"
      @close="showTrailerModal = false"
    />

    <!-- Modal thêm phim -->
    <AddMovieModal
      :show="showAddModal"
      @close="onCloseModal"
      @saved="onSaved"
    />

    <!-- Modal Chỉnh sửa Phim -->
    <transition name="modal-fade">
      <div
        v-if="showEditModal"
        class="modal-overlay"
        @click.self="closeEditModal"
      >
        <div class="modal-card modal-large">
          <div class="modal-header">
            <h2>Chỉnh sửa Phim</h2>
            <button class="modal-close" @click="closeEditModal">✕</button>
          </div>
          <form @submit.prevent="submitEditForm" class="edit-form">
            <div class="form-row">
              <div class="form-group">
                <label for="edit-title">Tên phim *</label>
                <input id="edit-title" v-model="editForm.tenPhim" required />
              </div>
              <div class="form-group">
                <label for="edit-duration">Thời lượng (phút) *</label>
                <input
                  id="edit-duration"
                  type="number"
                  v-model="editForm.thoiLuong"
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label for="edit-description">Mô tả</label>
              <textarea
                id="edit-description"
                v-model="editForm.moTa"
                rows="3"
              ></textarea>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="edit-release">Ngày phát hành</label>
                <input
                  id="edit-release"
                  type="date"
                  v-model="editForm.ngayPhatHanh"
                />
              </div>
              <div class="form-group">
                <label for="edit-format">Định dạng</label>
                <select id="edit-format" v-model="editForm.dinhDang">
                  <option value="2D">2D</option>
                  <option value="3D">3D</option>
                  <option value="IMAX">IMAX</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="edit-status">Trạng thái</label>
                <select id="edit-status" v-model="editForm.trangThai">
                  <option value="DANG_CHIEU">Đang chiếu</option>
                  <option value="NGUNG_CHIEU">Ngừng chiếu</option>
                </select>
              </div>
              <div class="form-group">
                <label for="edit-category">Thể loại</label>
                <input
                  id="edit-category"
                  v-model="editForm.theLoai"
                  placeholder="Phân cách bằng dấu phẩy"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="edit-director">Đạo diễn</label>
                <input
                  id="edit-director"
                  v-model="editForm.daoDien"
                  placeholder="Phân cách bằng dấu phẩy"
                />
              </div>
              <div class="form-group">
                <label for="edit-actors">Diễn viên</label>
                <input
                  id="edit-actors"
                  v-model="editForm.dienVien"
                  placeholder="Phân cách bằng dấu phẩy"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="edit-poster">URL Poster</label>
                <input
                  id="edit-poster"
                  v-model="editForm.posterUrl"
                  placeholder="Đường dẫn ảnh poster"
                />
              </div>
              <div class="form-group">
                <label for="edit-banner">URL Banner</label>
                <input
                  id="edit-banner"
                  v-model="editForm.bannerUrl"
                  placeholder="Đường dẫn ảnh banner"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="edit-trailer">URL Trailer</label>
              <input
                id="edit-trailer"
                v-model="editForm.trailerUrl"
                placeholder="Đường dẫn video trailer"
              />
            </div>

            <div class="modal-actions">
              <button
                type="button"
                class="btn btn-secondary"
                @click="closeEditModal"
                :disabled="isLoading"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="btn btn-primary"
                :disabled="isLoading"
              >
                <span v-if="isLoading" class="loading-spinner-btn"></span>
                Lưu thay đổi
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Modal Xác nhận Xóa -->
    <transition name="modal-fade">
      <div
        v-if="showDeleteModal"
        class="modal-overlay"
        @click.self="closeDeleteModal"
      >
        <div class="modal-card modal-danger">
          <div class="modal-header">
            <h2>Xác nhận xóa</h2>
            <button class="modal-close" @click="closeDeleteModal">✕</button>
          </div>
          <div class="delete-content">
            <div class="delete-icon">⚠️</div>
            <h3>Bạn có chắc chắn muốn xóa phim này?</h3>
            <div class="movie-preview" v-if="movieToDelete">
              <img
                v-if="movieToDelete.posterUrl"
                :src="fullImageUrl(movieToDelete.posterUrl)"
                :alt="movieToDelete.tenPhim"
                class="preview-poster"
              />
              <div v-else class="preview-placeholder">🎬</div>
              <div class="preview-info">
                <h4>{{ movieToDelete.tenPhim }}</h4>
                <p>
                  {{ movieToDelete.thoiLuong }} phút •
                  {{ movieToDelete.dinhDang }}
                </p>
                <p
                  class="preview-status"
                  :class="getStatusClass(movieToDelete.trangThai)"
                >
                  {{ formatStatus(movieToDelete.trangThai) }}
                </p>
              </div>
            </div>
            <p class="delete-warning">
              Hành động này không thể hoàn tác. Phim sẽ bị xóa vĩnh viễn khỏi hệ
              thống.
            </p>
          </div>
          <div class="modal-actions">
            <button
              class="btn btn-secondary"
              @click="closeDeleteModal"
              :disabled="isLoading"
            >
              Hủy
            </button>
            <button
              class="btn btn-danger"
              @click="confirmDeleteMovie"
              :disabled="isLoading"
            >
              <span v-if="isLoading" class="loading-spinner-btn"></span>
              Xóa vĩnh viễn
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Toast Notification -->
    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import {
  fetchMovies,
  deleteMovie,
  updateMovie,
  addMovie,
} from "../services/movieService";
import TrailerModal from "./TrailerModal.vue";
import AddMovieModal from "../components/AddMovieModal.vue";
import * as XLSX from "xlsx";

const movies = ref([]);
const currentPage = ref(1);
const pageSize = 7;
const searchQuery = ref("");

import { BASE_URL } from "@/constants/api";

// Modal states
const showTrailerModal = ref(false);
const currentTrailerUrl = ref("");
const showAddModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);
const movieToDelete = ref(null);
const isLoading = ref(false);
const isExporting = ref(false);

// Toast state
const toast = ref({ show: false, message: "", type: "" });
let toastTimeout = null;

// Filter states
const statusFilter = ref("");
const categoryFilter = ref("");
const formatFilter = ref("");

// Form state
const editForm = ref({
  idPhim: null,
  tenPhim: "",
  thoiLuong: "",
  moTa: "",
  ngayPhatHanh: "",
  dinhDang: "",
  trangThai: "",
  theLoai: "",
  daoDien: "",
  dienVien: "",
  posterUrl: "",
  bannerUrl: "",
  trailerUrl: "",
});

// Computed properties
const filteredMovies = computed(() => {
  let filtered = movies.value;

  // Search filter
  if (searchQuery.value) {
    filtered = filtered.filter(
      (movie) =>
        movie.tenPhim.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        movie.moTa.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        movie.dinhDang.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // Status filter
  if (statusFilter.value) {
    filtered = filtered.filter(
      (movie) => movie.trangThai === statusFilter.value
    );
  }

  // Category filter
  if (categoryFilter.value) {
    filtered = filtered.filter(
      (movie) =>
        Array.isArray(movie.theLoai) &&
        movie.theLoai.includes(categoryFilter.value)
    );
  }

  // Format filter
  if (formatFilter.value) {
    filtered = filtered.filter(
      (movie) => movie.dinhDang === formatFilter.value
    );
  }

  return filtered;
});

const hasActiveFilters = computed(() => {
  return (
    statusFilter.value ||
    categoryFilter.value ||
    formatFilter.value ||
    searchQuery.value
  );
});

const uniqueCategories = computed(() => {
  const categories = new Set();
  movies.value.forEach((movie) => {
    if (Array.isArray(movie.theLoai)) {
      movie.theLoai.forEach((cat) => categories.add(cat));
    }
  });
  return Array.from(categories).sort();
});

const totalPages = computed(() =>
  Math.ceil(filteredMovies.value.length / pageSize)
);

const paginatedMovies = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredMovies.value.slice(start, start + pageSize);
});

const activeMovies = computed(
  () => movies.value.filter((movie) => movie.trangThai === "DANG_CHIEU").length
);

const inactiveMovies = computed(
  () => movies.value.filter((movie) => movie.trangThai === "NGUNG_CHIEU").length
);

const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages.value, start + maxVisible - 1);

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1);
  }

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

// Methods
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--;
}

function goToPage(page) {
  currentPage.value = page;
}

function openTrailer(url) {
  currentTrailerUrl.value = url;
  showTrailerModal.value = true;
}

function onAddPost() {
  showAddModal.value = true;
}

function onCloseModal() {
  showAddModal.value = false;
}

async function onSaved() {
  showAddModal.value = false;
  showToast("🎉 Lưu phim thành công!", "success");
  try {
    const response = await fetchMovies();
    movies.value = response.data;

    // Thông báo cho các trang khác biết có thay đổi dữ liệu
    localStorage.setItem("moviesUpdated", Date.now().toString());
    window.dispatchEvent(new CustomEvent("moviesUpdated"));
  } catch (error) {
    console.error("❌ [Fetch Error]:", error);
    showToast("❌ Lỗi khi tải dữ liệu!", "error");
  }
}

function onOpenTrash() {
  showToast("🗑️ Tính năng thùng rác đang phát triển", "info");
}

function onEdit(movie) {
  editForm.value = { ...movie };
  showEditModal.value = true;
}

function onDelete(movie) {
  showDeleteModal.value = true;
  movieToDelete.value = movie;
}

function updateMovieStatus(movie) {
  showToast(`🎬 Cập nhật trạng thái phim "${movie.tenPhim}"`, "success");
}

function getStatusClass(status) {
  const statusClasses = {
    DANG_CHIEU: "status-active",
    NGUNG_CHIEU: "status-inactive",
  };
  return statusClasses[status] || "status-default";
}

function showToast(message, type = "success") {
  toast.value = { show: true, message, type };
  if (toastTimeout) clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => {
    toast.value.show = false;
  }, 3000);
}

function truncateText(text, maxLength) {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
}

// Helper functions
const formatDate = (dateStr) => {
  if (!dateStr) return "";
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
};

const formatStatus = (status) => {
  switch (status) {
    case "DANG_CHIEU":
      return "Đang Chiếu";
    case "NGUNG_CHIEU":
      return "Ngừng Chiếu";
    default:
      return status;
  }
};

const joinNames = (arr) => (Array.isArray(arr) ? arr.join(", ") : "");

const fullImageUrl = (path) => {
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
};

const logImageError = (path) => {
  console.error(`❌ [Image Load Error] Không tải được ảnh tại: ${path}`);
  // Có thể thêm logic để gửi lỗi về server hoặc analytics
};

const handleImageError = (event, path, type) => {
  console.error(`❌ [Image Load Error] Không tải được ${type} tại: ${path}`);

  // Thay thế bằng ảnh placeholder
  const target = event.target;
  if (type === "poster") {
    target.src = "/logo.png"; // Sử dụng ảnh có sẵn trong public
    target.alt = "Poster không khả dụng";
  } else if (type === "banner") {
    target.src = "/logo.png"; // Sử dụng ảnh có sẵn trong public
    target.alt = "Banner không khả dụng";
  }

  // Thêm class để style khác
  target.classList.add("image-error");
};

const calculateIndex = (index) =>
  (currentPage.value - 1) * pageSize + index + 1;

// Lifecycle
onMounted(async () => {
  await loadMovies();

  // Listen for movie updates
  window.addEventListener("moviesUpdated", loadMovies);
});

// Cleanup
onUnmounted(() => {
  window.removeEventListener("moviesUpdated", loadMovies);
});

async function loadMovies() {
  try {
    console.log("🔄 Loading movies...");
    const response = await fetchMovies();
    movies.value = response.data;
    console.log("✅ Movies loaded:", movies.value.length);

    // Debug: Kiểm tra cấu trúc dữ liệu của phim đầu tiên
    if (movies.value && movies.value.length > 0) {
      const firstMovie = movies.value[0];
      console.log("🔍 First movie structure:", firstMovie);
      console.log("🔍 Available fields:", Object.keys(firstMovie));
      console.log("🔍 Poster URL field:", firstMovie.posterUrl);
      console.log("🔍 Banner URL field:", firstMovie.bannerUrl);
    }
  } catch (error) {
    console.error("❌ [Fetch Error]:", error);
    showToast("❌ Lỗi khi tải dữ liệu!", "error");
  }
}

// Modal actions
function closeEditModal() {
  showEditModal.value = false;
  editForm.value = {
    idPhim: null,
    tenPhim: "",
    thoiLuong: "",
    moTa: "",
    ngayPhatHanh: "",
    dinhDang: "",
    trangThai: "",
    theLoai: "",
    daoDien: "",
    dienVien: "",
    posterUrl: "",
    bannerUrl: "",
    trailerUrl: "",
  };
}

async function submitEditForm() {
  isLoading.value = true;
  try {
    await updateMovie(editForm.value.idPhim, editForm.value);

    // Cập nhật local state
    const idx = movies.value.findIndex(
      (m) => m.idPhim === editForm.value.idPhim
    );
    if (idx !== -1) {
      movies.value[idx] = { ...editForm.value };
    }

    showToast("🎉 Cập nhật phim thành công!", "success");

    // Thông báo cho các trang khác biết có thay đổi dữ liệu
    localStorage.setItem("moviesUpdated", Date.now().toString());
    window.dispatchEvent(new CustomEvent("moviesUpdated"));
  } catch (error) {
    console.error("❌ [Update Error]:", error);
    showToast("❌ Lỗi khi cập nhật phim!", "error");
  } finally {
    isLoading.value = false;
    closeEditModal();
  }
}

function closeDeleteModal() {
  showDeleteModal.value = false;
  movieToDelete.value = null;
}

async function confirmDeleteMovie() {
  isLoading.value = true;
  try {
    await deleteMovie(movieToDelete.value.idPhim);
    // Xóa khỏi local state sau khi API thành công
    movies.value = movies.value.filter(
      (m) => m.idPhim !== movieToDelete.value.idPhim
    );
    showToast("🗑️ Xóa phim thành công!", "success");

    // Thông báo cho các trang khác biết có thay đổi dữ liệu
    localStorage.setItem("moviesUpdated", Date.now().toString());
    window.dispatchEvent(new CustomEvent("moviesUpdated"));
  } catch (error) {
    console.error("❌ [Delete Error]:", error);
    showToast("❌ Lỗi khi xóa phim!", "error");
  } finally {
    isLoading.value = false;
    closeDeleteModal();
  }
}

// Filter functionality
function clearFilters() {
  statusFilter.value = "";
  categoryFilter.value = "";
  formatFilter.value = "";
  searchQuery.value = "";
  showToast("🔄 Đã xóa tất cả bộ lọc", "info");
}

// Export functionality
function exportData() {
  isExporting.value = true;
  try {
    const data = filteredMovies.value.map((movie) => ({
      "Tên phim": movie.tenPhim || "",
      "Mô tả": movie.moTa || "",
      "Thời lượng": movie.thoiLuong ? `${movie.thoiLuong} phút` : "",
      "Ngày phát hành": formatDate(movie.ngayPhatHanh) || "",
      "Trạng thái": formatStatus(movie.trangThai) || "",
      "Định dạng": movie.dinhDang || "",
      "Ngày tạo": formatDate(movie.ngayTao) || "",
      "Đạo diễn": joinNames(movie.daoDien) || "",
      "Diễn viên": joinNames(movie.dienVien) || "",
      "Thể loại": joinNames(movie.theLoai) || "",
    }));

    if (data.length === 0) {
      showToast("⚠️ Không có dữ liệu để xuất!", "error");
      return;
    }

    // Tạo workbook và worksheet
    const workbook = XLSX.utils.book_new();
    const worksheet = XLSX.utils.json_to_sheet(data);

    // Thiết lập column widths
    const columnWidths = [
      { wch: 30 }, // Tên phim
      { wch: 50 }, // Mô tả
      { wch: 15 }, // Thời lượng
      { wch: 15 }, // Ngày phát hành
      { wch: 15 }, // Trạng thái
      { wch: 12 }, // Định dạng
      { wch: 15 }, // Ngày tạo
      { wch: 25 }, // Đạo diễn
      { wch: 30 }, // Diễn viên
      { wch: 20 }, // Thể loại
    ];
    worksheet["!cols"] = columnWidths;

    // Thiết lập header style (optional - sẽ được áp dụng khi mở trong Excel)
    const headerRange = XLSX.utils.decode_range(worksheet["!ref"]);
    for (let col = headerRange.s.c; col <= headerRange.e.c; col++) {
      const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
      if (!worksheet[cellAddress]) continue;

      worksheet[cellAddress].s = {
        font: { bold: true, color: { rgb: "FFFFFF" } },
        fill: { fgColor: { rgb: "4472C4" } },
        alignment: { horizontal: "center" },
      };
    }

    // Thêm worksheet vào workbook
    XLSX.utils.book_append_sheet(workbook, worksheet, "Danh sách phim");

    // Tạo file Excel
    const excelBuffer = XLSX.write(workbook, {
      bookType: "xlsx",
      type: "array",
      bookSST: false,
    });

    // Tạo blob và download
    const blob = new Blob([excelBuffer], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });

    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute(
      "download",
      `danh-sach-phim-${new Date().toISOString().split("T")[0]}.xlsx`
    );
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url); // Clean up

    showToast("📊 Xuất Excel thành công!", "success");
  } catch (error) {
    console.error("❌ [Export Error]:", error);
    showToast("❌ Lỗi khi xuất dữ liệu!", "error");
  } finally {
    isExporting.value = false;
  }
}
</script>

<style scoped>
.movie-page {
  padding: 32px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* Header Section */
.page-header {
  display: flex;
  justify-content: space-between;
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

.page-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* Buttons */
.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.btn-secondary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.6);
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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

/* Table Section */
.table-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 32px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #ecf0f1;
}

.table-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  padding: 12px 16px 12px 44px;
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  font-size: 14px;
  width: 300px;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  left: 16px;
  color: #7f8c8d;
  font-size: 16px;
}

/* Table */
.table-container {
  overflow-x: auto;
  max-height: 600px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1400px;
}

.data-table th {
  background: #f8f9fa;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.data-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #ecf0f1;
  color: #2c3e50;
  font-size: 14px;
  vertical-align: middle;
}

.table-row {
  transition: all 0.3s ease;
}

.table-row:hover {
  background: #f8f9fa;
  transform: scale(1.01);
}

/* Movie info */
.movie-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.movie-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.movie-name {
  font-weight: 600;
  color: #2c3e50;
}

/* Description */
.description-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #7f8c8d;
  font-size: 13px;
}

/* Text lists */
.text-list {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #34495e;
  font-size: 13px;
}

/* Status badges */
.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-active {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: white;
}

.status-inactive {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}

.status-default {
  background: #ecf0f1;
  color: #7f8c8d;
}

/* Images */
.image-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.movie-poster {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.movie-poster:hover {
  transform: scale(1.1);
}

.movie-banner {
  width: 120px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.movie-banner:hover {
  transform: scale(1.1);
}

.image-error {
  opacity: 0.6;
  filter: grayscale(50%);
  border: 2px dashed #e74c3c;
}

.no-image {
  width: 60px;
  height: 80px;
  background: #ecf0f1;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #7f8c8d;
}

/* Trailer button */
.trailer-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.trailer-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.no-trailer {
  color: #7f8c8d;
  font-size: 12px;
}

/* Action buttons */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.edit-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}

.delete-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

/* Status toggle */
.status-toggle {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.status-toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

/* Empty and Loading states */
.empty-state,
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
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

/* Pagination */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.pagination-info {
  color: #7f8c8d;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.page-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(44, 62, 80, 0.45);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 40px rgba(44, 62, 80, 0.18);
  padding: 32px 28px 24px;
  min-width: 320px;
  max-width: 95vw;
  animation: popIn 0.3s cubic-bezier(0.4, 2, 0.6, 1);
  position: relative;
}

.modal-large {
  min-width: 600px;
  max-width: 800px;
}

.modal-danger {
  min-width: 500px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ecf0f1;
}

.modal-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #7f8c8d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #ecf0f1;
  color: #2c3e50;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 14px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

.btn-danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.btn-danger:hover {
  background: linear-gradient(135deg, #ff5252 0%, #d32f2f 100%);
}

.loading-spinner-btn {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 3px solid #ecf0f1;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 6px;
  vertical-align: middle;
}

/* Delete modal content */
.delete-content {
  text-align: center;
  margin-bottom: 24px;
}

.delete-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.delete-content h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 20px;
}

.movie-preview {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 12px;
  margin: 20px 0;
}

.preview-poster {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.preview-placeholder {
  width: 60px;
  height: 80px;
  background: #ecf0f1;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #7f8c8d;
}

.preview-info {
  flex: 1;
  text-align: left;
}

.preview-info h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.preview-info p {
  margin: 4px 0;
  color: #7f8c8d;
  font-size: 14px;
}

.preview-status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.delete-warning {
  color: #e74c3c;
  font-weight: 600;
  font-size: 14px;
  margin: 0;
}

/* Modal transition */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.25s;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* Toast */
.toast {
  position: fixed;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 220px;
  background: #fff;
  color: #2c3e50;
  padding: 16px 32px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(44, 62, 80, 0.18);
  font-weight: 600;
  font-size: 15px;
  z-index: 2000;
  animation: popIn 0.3s;
}

.toast.success {
  border-left: 6px solid #28a745;
}

.toast.error {
  border-left: 6px solid #ff5252;
}

.toast.info {
  border-left: 6px solid #667eea;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: opacity 0.3s;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
}

@keyframes popIn {
  from {
    transform: translateX(-50%) scale(0.85);
    opacity: 0;
  }
  to {
    transform: translateX(-50%) scale(1);
    opacity: 1;
  }
}

/* Enhanced Filters */
.table-actions {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  flex-wrap: wrap;
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

.filter-dropdown {
  position: relative;
  display: flex;
  align-items: center;
}

.filter-select {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  background: white;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  font-weight: 500;
  padding-right: 2.5rem;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1),
    0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.filter-select:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.filter-arrow {
  position: absolute;
  right: 1rem;
  color: #64748b;
  font-size: 0.75rem;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: bold;
}

.filter-dropdown:hover .filter-arrow {
  transform: rotate(180deg);
  color: #3b82f6;
}

.filter-actions {
  display: flex;
  gap: 1rem;
  align-items: end;
  flex-wrap: wrap;
}

.btn-clear-filters {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(108, 117, 125, 0.4);
  padding: 0.875rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-clear-filters:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(108, 117, 125, 0.6);
}

.btn-export {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
  padding: 0.875rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-export:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.6);
}

.btn-icon {
  font-size: 1rem;
}

.btn-text {
  font-weight: 600;
}

.search-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 300px;
}

/* Responsive design for filters */
@media (max-width: 768px) {
  .filters-section {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .filter-group {
    min-width: 100%;
  }

  .filter-actions {
    width: 100%;
    justify-content: stretch;
  }

  .btn-clear-filters,
  .btn-export {
    flex: 1;
    justify-content: center;
  }

  .search-section {
    min-width: 100%;
  }
}

/* Filter animations */
.filter-group {
  animation: slideInUp 0.5s ease-out;
}

.filter-group:nth-child(1) {
  animation-delay: 0.1s;
}
.filter-group:nth-child(2) {
  animation-delay: 0.2s;
}
.filter-group:nth-child(3) {
  animation-delay: 0.3s;
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

/* Filter hover effects */
.filter-group:hover .filter-label {
  color: #3b82f6;
}

.filter-group:hover .filter-icon {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}

/* Enhanced filter select styling */
.filter-select option {
  padding: 0.5rem;
  font-weight: 500;
}

.filter-select option:hover {
  background: #f1f5f9;
}

.search-stats {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 0.75rem;
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.2);
  animation: slideInUp 0.3s ease-out;
}

.stats-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .movie-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .header-actions {
    width: 100%;
    justify-content: stretch;
  }

  .btn {
    flex: 1;
    justify-content: center;
  }

  .stats-section {
    grid-template-columns: 1fr;
  }

  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    min-width: auto;
  }

  .search-section {
    min-width: auto;
  }

  .search-input {
    width: 100%;
  }

  .pagination-section {
    flex-direction: column;
    gap: 16px;
  }

  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
