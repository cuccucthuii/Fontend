<template>
  <div v-if="show" class="modal-backdrop" @click.self="close">
    <div
      class="modal-content fancy-modal"
      role="dialog"
      aria-modal="true"
      aria-label="Thêm phim"
    >
      <button class="btn-close" @click="close" aria-label="Đóng modal">
        &times;
      </button>
      <h2 class="modal-title">🎬 Thêm phim mới</h2>
      <form
        @submit.prevent="submitForm"
        enctype="multipart/form-data"
        novalidate
      >
        <div class="form-grid">
          <div class="form-group">
            <label for="tenPhim">Tên Phim <span>*</span></label>
            <input
              id="tenPhim"
              v-model="form.tenPhim"
              type="text"
              required
              placeholder="Nhập tên phim..."
            />
          </div>
          <div class="form-group">
            <label for="moTa">Mô tả <span>*</span></label>
            <textarea
              id="moTa"
              v-model="form.moTa"
              required
              placeholder="Nhập mô tả phim..."
            ></textarea>
          </div>
          <div class="form-group">
            <label for="thoiLuong">Thời lượng (phút) <span>*</span></label>
            <input
              id="thoiLuong"
              v-model.number="form.thoiLuong"
              type="number"
              min="1"
              required
              placeholder="VD: 120"
            />
            <div
              v-if="form.thoiLuong !== null && form.thoiLuong < 100"
              class="error-msg"
            >
              Thời lượng phải &gt;= 100 phút
            </div>
          </div>
          <div class="form-group">
            <label for="ngayPhatHanh">Ngày phát hành <span>*</span></label>
            <input
              id="ngayPhatHanh"
              v-model="form.ngayPhatHanh"
              type="date"
              required
            />
            <div v-if="releaseDateError" class="error-msg">
              {{ releaseDateError }}
            </div>
          </div>
          <div class="form-group">
            <label>Trạng thái</label>
            <input type="hidden" v-model="form.trangThai" />
            <span class="static-value">Sắp Chiếu</span>
          </div>
          <div class="form-group">
            <label for="dinhDang">Định dạng <span>*</span></label>
            <select id="dinhDang" v-model="form.dinhDang" required>
              <option value="">-- Chọn định dạng --</option>
              <option value="2D">2D</option>
              <option value="3D">3D</option>
              <option value="IMAX">IMAX</option>
            </select>
          </div>
          <div class="form-group">
            <label for="trailerUrl">Trailer URL (YouTube) <span>*</span></label>
            <input
              id="trailerUrl"
              v-model="form.trailerUrl"
              type="url"
              required
              placeholder="https://youtube.com/..."
            />
          </div>
          <div class="form-group">
            <label for="theLoaiMoi">Thể loại <span>*</span></label>

            <!-- Simple Test Dropdown -->
            <div style="position: relative; width: 100%; margin-bottom: 10px">
              <button
                type="button"
                @click="toggleGenreDropdown"
                style="
                  width: 100%;
                  padding: 12px;
                  border: 2px solid #e0e0e0;
                  border-radius: 8px;
                  background: white;
                  cursor: pointer;
                  text-align: left;
                  font-size: 14px;
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                "
              >
                <span v-if="form.theLoaiMoi.length === 0" style="color: #6c757d"
                  >-- Chọn thể loại --</span
                >
                <span v-else style="color: #263238"
                  >{{ form.theLoaiMoi.length }} thể loại đã chọn</span
                >
                <span style="color: #6c757d">▼</span>
              </button>

              <!-- Dropdown Options -->
              <div
                v-if="showGenreDropdown"
                style="
                  position: absolute;
                  top: 100%;
                  left: 0;
                  right: 0;
                  background: white;
                  border: 2px solid #e0e0e0;
                  border-top: none;
                  border-radius: 0 0 8px 8px;
                  max-height: 200px;
                  overflow-y: auto;
                  z-index: 99999;
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                "
              >
                <!-- Debug Info -->
                <div
                  style="
                    padding: 8px;
                    background: #f0f0f0;
                    font-size: 12px;
                    color: black;
                    border-bottom: 1px solid #ddd;
                  "
                >
                  Debug: {{ genres.length }} genres available
                </div>

                <!-- Genre Options -->
                <div
                  v-for="genre in genres"
                  :key="genre.idTheLoai"
                  @click="toggleGenre(genre.tenTheLoai)"
                  style="
                    padding: 10px 12px;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    background: white;
                    color: black;
                    border-bottom: 1px solid #eee;
                    transition: background-color 0.2s;
                  "
                  :style="{
                    backgroundColor: form.theLoaiMoi.includes(genre.tenTheLoai)
                      ? '#e3f2fd'
                      : 'white',
                  }"
                >
                  <span
                    style="
                      width: 16px;
                      height: 16px;
                      border: 2px solid #667eea;
                      border-radius: 3px;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      font-size: 12px;
                      color: white;
                      background-color: #667eea;
                    "
                  >
                    {{ form.theLoaiMoi.includes(genre.tenTheLoai) ? "✓" : "" }}
                  </span>
                  <span style="font-weight: 500; color: black">{{
                    genre.tenTheLoai
                  }}</span>
                </div>

                <!-- No Genres Message -->
                <div
                  v-if="genres.length === 0"
                  style="padding: 12px; text-align: center; color: #666"
                >
                  Không có thể loại nào
                </div>
              </div>
            </div>

            <!-- Selected Genres Tags -->
            <div
              v-if="form.theLoaiMoi && form.theLoaiMoi.length"
              style="margin-top: 8px; display: flex; flex-wrap: wrap; gap: 6px"
            >
              <span
                v-for="genre in form.theLoaiMoi"
                :key="genre"
                style="
                  display: inline-flex;
                  align-items: center;
                  gap: 4px;
                  padding: 4px 8px;
                  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                  color: white;
                  border-radius: 16px;
                  font-size: 12px;
                  font-weight: 500;
                "
              >
                {{ genre }}
                <button
                  type="button"
                  @click="removeGenre(genre)"
                  style="
                    background: none;
                    border: none;
                    color: white;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: bold;
                    padding: 0;
                    width: 16px;
                    height: 16px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 50%;
                  "
                >
                  ×
                </button>
              </span>
            </div>

            <!-- Debug Info -->
            <div style="margin-top: 8px; font-size: 11px; color: #666">
              Genres loaded: {{ genres.map((g) => g.tenTheLoai).join(", ") }}
            </div>
          </div>
          <div class="form-group">
            <label for="daoDienMoi">Đạo diễn <span>*</span></label>
            <input
              id="daoDienMoi"
              v-model="form.daoDienMoi"
              type="text"
              required
              placeholder="Tên đạo diễn..."
            />
          </div>
          <div class="form-group">
            <label for="dienVienMoi">Diễn viên chính<span>*</span></label>
            <input
              id="dienVienMoi"
              v-model="form.dienVienMoi"
              type="text"
              required
              placeholder="Tên diễn viên..."
            />
          </div>
          <div class="form-group">
            <label for="poster">Poster <span>*</span></label>
            <input
              id="poster"
              type="file"
              @change="onFileChange($event, 'poster')"
              accept="image/*"
              required
            />
            <div v-if="posterPreview" class="image-preview">
              <img :src="posterPreview" alt="Poster preview" />
            </div>
          </div>
          <div class="form-group">
            <label for="banner">Banner <span>*</span></label>
            <input
              id="banner"
              type="file"
              @change="onFileChange($event, 'banner')"
              accept="image/*"
              required
            />
            <div v-if="bannerPreview" class="image-preview">
              <img :src="bannerPreview" alt="Banner preview" />
            </div>
          </div>
        </div>
        <button type="submit" class="btn-submit">Lưu phim</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, ref, onMounted, watch } from "vue";
import api from '@/services/api';
import { fetchGenres } from "../services/movieService";

const props = defineProps({
  show: Boolean,
});
const emit = defineEmits(["close", "saved"]);

const form = reactive({
  tenPhim: "",
  moTa: "",
  thoiLuong: null,
  ngayPhatHanh: "",
  trangThai: "SAP_CHIEU",
  dinhDang: "",
  trailerUrl: "",
  theLoaiMoi: [],
  daoDienMoi: "",
  dienVienMoi: "",
  poster: null,
  banner: null,
});

const posterPreview = ref("");
const bannerPreview = ref("");
const showGenreDropdown = ref(false);
const genres = ref([]);

const releaseDateError = computed(() => {
  if (!form.ngayPhatHanh) return "";
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const releaseDate = new Date(form.ngayPhatHanh);
  if (releaseDate <= today) {
    return "Ngày phát hành phải lớn hơn ngày hiện tại!";
  }
  return "";
});

function close() {
  emit("close");
  resetForm();
}

function resetForm() {
  form.tenPhim = "";
  form.moTa = "";
  form.thoiLuong = null;
  form.ngayPhatHanh = "";
  form.trangThai = "SAP_CHIEU";
  form.dinhDang = "";
  form.trailerUrl = "";
  form.theLoaiMoi = [];
  form.daoDienMoi = "";
  form.dienVienMoi = "";
  form.poster = null;
  form.banner = null;
  posterPreview.value = "";
  bannerPreview.value = "";
}

async function loadGenres() {
  try {
    console.log("🔄 Đang tải danh sách thể loại từ API...");
    console.log("📡 API URL: /api/the-loai");
    const response = await fetchGenres();
    console.log("📡 API Response:", response);
    console.log("📡 Response data:", response.data);
    console.log("📡 Response status:", response.status);

    if (response.data && response.data.length > 0) {
      genres.value = response.data;
      console.log("✅ Thể loại đã tải từ API:", genres.value);
    } else {
      console.log("⚠️ API trả về mảng rỗng, giữ nguyên dữ liệu mẫu");
      console.log("📋 Dữ liệu mẫu hiện tại:", genres.value);
    }
  } catch (error) {
    console.error("❌ Lỗi khi tải danh sách thể loại:", error);
    console.error("❌ Error details:", error.response?.data);
    console.error("❌ Error status:", error.response?.status);
    console.log("🔄 Giữ nguyên dữ liệu mẫu do lỗi API");
    console.log("📋 Dữ liệu mẫu hiện tại:", genres.value);
  }
}

function toggleGenreDropdown() {
  showGenreDropdown.value = !showGenreDropdown.value;
}

function toggleGenre(genreName) {
  const index = form.theLoaiMoi.indexOf(genreName);
  if (index > -1) {
    form.theLoaiMoi.splice(index, 1);
  } else {
    form.theLoaiMoi.push(genreName);
  }
}

function removeGenre(genreToRemove) {
  form.theLoaiMoi = form.theLoaiMoi.filter((genre) => genre !== genreToRemove);
}

function onFileChange(event, type) {
  const file = event.target.files[0];
  if (file) {
    if (type === "poster") {
      form.poster = file;
      posterPreview.value = URL.createObjectURL(file);
    } else if (type === "banner") {
      form.banner = file;
      bannerPreview.value = URL.createObjectURL(file);
    }
  }
}

// Load genres when component mounts
onMounted(() => {
  console.log("🎬 AddMovieModal mounted");
  console.log("📋 Genres initial:", genres.value);
  console.log("📋 Genres length:", genres.value.length);
  loadGenres();
});

// Watch for changes in genres
watch(
  genres,
  (newGenres) => {
    console.log("👀 Genres changed:", newGenres);
    console.log("👀 Genres length:", newGenres.length);
  },
  { deep: true }
);

async function submitForm() {
  try {
    // Validation tất cả các trường bắt buộc
    if (!form.tenPhim || form.tenPhim.trim() === "") {
      alert("Vui lòng nhập tên phim!");
      return;
    }

    if (!form.moTa || form.moTa.trim() === "") {
      alert("Vui lòng nhập mô tả phim!");
      return;
    }

    // Validation thời lượng
    if (!form.thoiLuong || form.thoiLuong < 100) {
      alert("Thời lượng phim phải lớn hơn hoặc bằng 100 phút!");
      return;
    }

    // Validation ngày phát hành
    if (!form.ngayPhatHanh) {
      alert("Vui lòng nhập ngày phát hành!");
      return;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const releaseDate = new Date(form.ngayPhatHanh);
    if (releaseDate <= today) {
      alert("Ngày phát hành phải lớn hơn ngày hiện tại!");
      return;
    }

    if (!form.dinhDang || form.dinhDang === "") {
      alert("Vui lòng chọn định dạng phim!");
      return;
    }

    if (!form.trailerUrl || form.trailerUrl.trim() === "") {
      alert("Vui lòng nhập trailer URL!");
      return;
    }

    if (!form.theLoaiMoi || form.theLoaiMoi.length === 0) {
      alert("Vui lòng chọn thể loại phim!");
      return;
    }

    if (!form.daoDienMoi || form.daoDienMoi.trim() === "") {
      alert("Vui lòng nhập đạo diễn phim!");
      return;
    }

    if (!form.dienVienMoi || form.dienVienMoi.trim() === "") {
      alert("Vui lòng nhập diễn viên phim!");
      return;
    }

    if (!form.poster) {
      alert("Vui lòng chọn poster phim!");
      return;
    }

    if (!form.banner) {
      alert("Vui lòng chọn banner phim!");
      return;
    }

    // Chuẩn bị formData để gửi lên backend
    const formData = new FormData();
    // Chuyển đổi các trường mới thành mảng
    formData.append(
      "phim",
      JSON.stringify({
        tenPhim: form.tenPhim,
        moTa: form.moTa,
        thoiLuong: form.thoiLuong,
        ngayPhatHanh: form.ngayPhatHanh,
        trangThai: form.trangThai,
        dinhDang: form.dinhDang,
        trailerUrl: form.trailerUrl,
        theLoaiMoi: form.theLoaiMoi || [],
        daoDienMoi: form.daoDienMoi
          ? form.daoDienMoi.split(",").map((s) => s.trim())
          : [],
        dienVienMoi: form.dienVienMoi
          ? form.dienVienMoi.split(",").map((s) => s.trim())
          : [],
      })
    );
    if (form.poster) formData.append("poster", form.poster);
    if (form.banner) formData.append("banner", form.banner);

    const response = await api.post(
      "/api/phim/add",
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );

    console.log("✅ Phim đã được thêm thành công:", response.data);
    console.log("📸 Poster URL:", response.data.posterUrl);
    console.log("🖼️ Banner URL:", response.data.bannerUrl);
    console.log("🔍 Full response data structure:", Object.keys(response.data));
    console.log("🔍 Response data values:", response.data);

    alert("✅ Thêm phim thành công!");

    // Thông báo cho các trang khác biết có thay đổi dữ liệu
    localStorage.setItem("moviesUpdated", Date.now().toString());
    window.dispatchEvent(new CustomEvent("moviesUpdated"));

    emit("saved");
    resetForm();
  } catch (error) {
    console.error("❌ Lỗi khi thêm phim:", error);
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.fancy-modal {
  background: #fff;
  border-radius: 18px;
  padding: 32px 32px 24px 32px;
  max-width: 700px;
  width: 98vw;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18), 0 1.5px 6px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  max-height: 95vh;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  position: relative;
  animation: modalIn 0.25s cubic-bezier(0.4, 2, 0.6, 1) both;
}
@keyframes modalIn {
  0% {
    transform: scale(0.95) translateY(30px);
    opacity: 0;
  }
  100% {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
}
.modal-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 18px;
  color: #1a237e;
  letter-spacing: 1px;
}
.btn-close {
  position: absolute;
  top: 12px;
  right: 18px;
  background: none;
  border: none;
  font-size: 32px;
  cursor: pointer;
  color: #888;
  transition: color 0.2s;
}
.btn-close:hover {
  color: #e53935;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px 32px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
label {
  font-weight: 600;
  color: #263238;
  margin-bottom: 2px;
  font-size: 1rem;
}
label span {
  color: #e53935;
  font-size: 1.1em;
}

.genre-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  background: white;
  transition: border-color 0.2s;
}

.genre-select:focus {
  outline: none;
  border-color: #667eea;
}

.genre-select option {
  padding: 8px;
  font-weight: 500;
}

.selected-genres {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.genre-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.remove-genre {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.remove-genre:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.genre-loading {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #667eea;
}

.loading-text {
  color: #6c757d;
  font-size: 12px;
  font-weight: 500;
}

.genre-info {
  margin-top: 8px;
}

.genre-info small {
  color: #6c757d;
  font-size: 11px;
  font-style: italic;
}

.custom-genre-select {
  position: relative;
  width: 100%;
}

.genre-dropdown {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  background: white;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: border-color 0.2s;
}

.genre-dropdown:hover {
  border-color: #667eea;
}

.placeholder {
  color: #6c757d;
}

.selected-count {
  color: #263238;
}

.dropdown-arrow {
  color: #6c757d;
  font-size: 12px;
  transition: transform 0.2s;
}

.genre-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.genre-options {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 2px solid #e0e0e0;
  border-top: none;
  border-radius: 0 0 8px 8px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 9999;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  /* Đảm bảo không có thanh xám che khuất */
  background-color: white !important;
}

.genre-option {
  padding: 10px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s;
  /* Đảm bảo không bị che khuất */
  position: relative;
  z-index: 1;
  background: white;
}

.genre-option:hover {
  background-color: #f8f9fa;
}

.genre-option.selected {
  background-color: #e3f2fd;
}

.checkbox {
  width: 16px;
  height: 16px;
  border: 2px solid #667eea;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
  background-color: #667eea;
}

.genre-name {
  font-weight: 500;
  color: #263238;
  /* Đảm bảo text hiển thị rõ ràng */
  text-shadow: none;
  background: transparent;
  z-index: 1;
  position: relative;
}
input[type="text"],
input[type="number"],
input[type="url"],
input[type="date"],
select,
textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1.5px solid #bdbdbd;
  font-size: 15px;
  box-sizing: border-box;
  background: #f8fafc;
  transition: border 0.2s, box-shadow 0.2s;
  color: #222;
}
input:focus,
select:focus,
textarea:focus {
  border: 1.5px solid #3949ab;
  outline: none;
  box-shadow: 0 0 0 2px #c5cae9;
  background: #fff;
  color: #222;
}
textarea {
  resize: vertical;
  min-height: 60px;
  max-height: 180px;
}
.btn-submit {
  margin: 32px auto 0 auto;
  display: block;
  background: linear-gradient(90deg, #3949ab, #1e88e5);
  color: #fff;
  padding: 14px 38px;
  border: none;
  border-radius: 10px;
  font-size: 1.15rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(30, 136, 229, 0.1);
  letter-spacing: 1px;
  transition: background 0.2s, box-shadow 0.2s;
}
.btn-submit:hover {
  background: linear-gradient(90deg, #1e88e5, #3949ab);
  box-shadow: 0 4px 16px rgba(30, 136, 229, 0.18);
}
.error-msg {
  color: #e53935;
  font-size: 13px;
  margin-top: 2px;
}
.static-value {
  display: inline-block;
  background: #e3eafc;
  color: #3949ab;
  font-weight: 600;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 1rem;
  margin-top: 2px;
}

.image-preview {
  margin-top: 8px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 8px;
  background: #f9f9f9;
}

.image-preview img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 6px;
  object-fit: cover;
}
@media (max-width: 900px) {
  .fancy-modal {
    padding: 18px 4vw 12px 4vw;
  }
  .form-grid {
    grid-template-columns: 1fr;
    gap: 14px 0;
  }
  .btn-submit {
    width: 100%;
  }
}
</style>
