<template>
  <div class="add-movie-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🎬</span>
          Thêm phim mới
        </h1>
        <p class="page-subtitle">Form thêm phim với đầy đủ validation</p>
      </div>
    </div>

    <div class="form-container">
      <form @submit.prevent="submitForm" enctype="multipart/form-data" novalidate class="movie-form">
        <div class="form-grid">
          <div class="form-group">
            <label for="tenPhim">Tên Phim <span class="required">*</span></label>
            <input id="tenPhim" v-model="form.tenPhim" type="text" required placeholder="Nhập tên phim..." />
          </div>
          
          <div class="form-group">
            <label for="moTa">Mô tả <span class="required">*</span></label>
            <textarea id="moTa" v-model="form.moTa" required placeholder="Nhập mô tả phim..."></textarea>
          </div>
          
          <div class="form-group">
            <label for="thoiLuong">Thời lượng (phút) <span class="required">*</span></label>
            <input id="thoiLuong" v-model.number="form.thoiLuong" type="number" min="1" required placeholder="VD: 120" />
            <div v-if="form.thoiLuong !== null && form.thoiLuong < 90" class="error-msg">Thời lượng phải >= 90 phút</div>
          </div>
          
          <div class="form-group">
            <label for="ngayPhatHanh">Ngày phát hành <span class="required">*</span></label>
            <input id="ngayPhatHanh" v-model="form.ngayPhatHanh" type="date" required />
            <div v-if="releaseDateError" class="error-msg">{{ releaseDateError }}</div>
          </div>
          
          <div class="form-group">
            <label>Trạng thái</label>
            <input type="hidden" v-model="form.trangThai" />
            <span class="static-value">Sắp Chiếu</span>
          </div>
          
          <div class="form-group">
            <label for="dinhDang">Định dạng <span class="required">*</span></label>
            <select id="dinhDang" v-model="form.dinhDang" required>
              <option value="">-- Chọn định dạng --</option>
              <option value="2D">2D</option>
              <option value="3D">3D</option>
              <option value="4D">4D</option>
              <option value="IMAX">IMAX</option>
            </select>
          </div>

          <div class="form-group">
            <label for="tuoiGioiHan">Tuổi giới hạn <span class="required">*</span></label>
            <select id="tuoiGioiHan" v-model="form.tuoiGioiHan" required>
              <option value="">-- Chọn giới hạn tuổi --</option>
              <option value="P">P (Mọi lứa tuổi)</option>
              <option value="T13">T13</option>
              <option value="T16">T16</option>
              <option value="T18">T18</option>
            </select>
          </div>

          <div class="form-group">
            <label for="namSanXuat">Năm sản xuất <span class="required">*</span></label>
            <input id="namSanXuat" v-model.number="form.namSanXuat" type="number" :min="2020" :max="currentYear" required placeholder="VD: 2025" />
            <div v-if="form.namSanXuat && (form.namSanXuat < 2020 || form.namSanXuat > currentYear)" class="error-msg">
              Năm sản xuất phải từ 2020 đến {{ currentYear }}
            </div>
          </div>

          

          
          
          <div class="form-group">
            <label for="trailerUrl">Trailer URL (YouTube) <span class="required">*</span></label>
            <input id="trailerUrl" v-model="form.trailerUrl" type="url" required placeholder="https://youtube.com/..." />
          </div>
          
          <div class="form-group">
            <label for="theLoaiMoi">Thể loại <span class="required">*</span></label>
            <select id="theLoaiMoi" v-model="form.theLoaiMoi" multiple required class="genre-select">
              <option value="" disabled>-- Chọn thể loại --</option>
              <option v-for="genre in genres" :key="genre.idTheLoai" :value="genre.tenTheLoai">
                {{ genre.tenTheLoai }}
              </option>
            </select>
            <div class="selected-genres" v-if="form.theLoaiMoi && form.theLoaiMoi.length">
              <span class="genre-tag" v-for="genre in form.theLoaiMoi" :key="genre">
                {{ genre }}
                <button type="button" @click="removeGenre(genre)" class="remove-genre">×</button>
              </span>
            </div>
            <div class="genre-info">
              <small>💡 Chọn nhiều thể loại bằng cách giữ Ctrl (hoặc Cmd trên Mac)</small>
            </div>
          </div>
          
          <div class="form-group">
            <label for="daoDienMoi">Đạo diễn <span class="required">*</span></label>
            <input id="daoDienMoi" v-model="form.daoDienMoi" type="text" required placeholder="Tên đạo diễn..." />
          </div>
          
          <div class="form-group">
            <label for="dienVienMoi">Diễn viên chính <span class="required">*</span></label>
            <input id="dienVienMoi" v-model="form.dienVienMoi" type="text" required placeholder="Tên diễn viên..." />
          </div>
          
          <div class="form-group">
            <label for="poster">Poster <span class="required">*</span></label>
            <input id="poster" type="file" @change="onFileChange($event, 'poster')" accept="image/*" required />
            <div v-if="posterPreview" class="image-preview">
              <img :src="posterPreview" alt="Poster preview" />
            </div>
          </div>
          
          <div class="form-group">
            <label for="banner">Banner <span class="required">*</span></label>
            <input id="banner" type="file" @change="onFileChange($event, 'banner')" accept="image/*" required />
            <div v-if="bannerPreview" class="image-preview">
              <img :src="bannerPreview" alt="Banner preview" />
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="goBack" class="btn btn-secondary">
            <span class="btn-icon">←</span>
            Quay lại
          </button>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            <span v-if="isSubmitting" class="loading-spinner"></span>
            <span v-else class="btn-icon">💾</span>
            <span v-if="isSubmitting">Đang thêm...</span>
            <span v-else>Lưu phim</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
import { fetchGenres } from '../services/movieService'

const router = useRouter()
const isSubmitting = ref(false)

const form = reactive({
  tenPhim: '',
  moTa: '',
  thoiLuong: null,
  ngayPhatHanh: '',
  trangThai: 'SAP_CHIEU',
  dinhDang: '',
  tuoiGioiHan: '',
  namSanXuat: null,
  trailerUrl: '',
  theLoaiMoi: [],
  daoDienMoi: '',
  dienVienMoi: '',
  poster: null,
  banner: null,
})

const posterPreview = ref('')
const bannerPreview = ref('')
const genres = ref([])
const currentYear = new Date().getFullYear()

const releaseDateError = computed(() => {
  if (!form.ngayPhatHanh) return ''
  const today = new Date()
  today.setHours(0,0,0,0)
  const releaseDate = new Date(form.ngayPhatHanh)
  if (releaseDate <= today) {
    return 'Ngày phát hành phải lớn hơn ngày hiện tại!'
  }
  return ''
})

function goBack() {
  router.push('/admin/movies')
}

function resetForm() {
  form.tenPhim = ''
  form.moTa = ''
  form.thoiLuong = null
  form.ngayPhatHanh = ''
  form.trangThai = 'SAP_CHIEU'
  form.dinhDang = ''
  form.tuoiGioiHan = ''
  form.namSanXuat = null
  form.trailerUrl = ''
  form.theLoaiMoi = []
  form.daoDienMoi = ''
  form.dienVienMoi = ''
  form.poster = null
  form.banner = null
  posterPreview.value = ''
  bannerPreview.value = ''
}

async function loadGenres() {
  try {
    console.log('🔄 Đang tải danh sách thể loại từ API...')
    const response = await fetchGenres()
    console.log('📡 API Response:', response)
    
    if (response.data && response.data.length > 0) {
      genres.value = response.data
      console.log('✅ Thể loại đã tải từ API:', genres.value)
    } else {
      console.log('⚠️ API trả về mảng rỗng, giữ nguyên dữ liệu mẫu')
    }
  } catch (error) {
    console.error('❌ Lỗi khi tải danh sách thể loại:', error)
    console.log('🔄 Giữ nguyên dữ liệu mẫu do lỗi API')
  }
}

function removeGenre(genreToRemove) {
  form.theLoaiMoi = form.theLoaiMoi.filter(genre => genre !== genreToRemove)
}

function onFileChange(event, type) {
  const file = event.target.files[0]
  if (file) {
    if (type === 'poster') {
      form.poster = file
      posterPreview.value = URL.createObjectURL(file)
    } else if (type === 'banner') {
      form.banner = file
      bannerPreview.value = URL.createObjectURL(file)
    }
  }
}

// Load genres when component mounts
onMounted(() => {
  console.log('🎬 AddMoviePage mounted, genres:', genres.value)
  loadGenres()
})

async function submitForm() {
  try {
    // Validation tất cả các trường bắt buộc
    if (!form.tenPhim || form.tenPhim.trim() === '') {
      alert('Vui lòng nhập tên phim!')
      return
    }
    
    if (!form.moTa || form.moTa.trim() === '') {
      alert('Vui lòng nhập mô tả phim!')
      return
    }
    
    // Validation thời lượng
    if (!form.thoiLuong || form.thoiLuong < 90) {
      alert('Thời lượng phim phải lớn hơn hoặc bằng 90 phút!')
      return
    }
    
    // Validation ngày phát hành
    if (!form.ngayPhatHanh) {
      alert('Vui lòng nhập ngày phát hành!')
      return
    }
    const today = new Date()
    today.setHours(0,0,0,0)
    const releaseDate = new Date(form.ngayPhatHanh)
    if (releaseDate <= today) {
      alert('Ngày phát hành phải lớn hơn ngày hiện tại!')
      return
    }
    
    if (!form.dinhDang || form.dinhDang === '') {
      alert('Vui lòng chọn định dạng phim!')
      return
    }
    
    if (!form.trailerUrl || form.trailerUrl.trim() === '') {
      alert('Vui lòng nhập trailer URL!')
      return
    }
    
    // Validation năm sản xuất
    if (!form.namSanXuat || form.namSanXuat < 2020 || form.namSanXuat > currentYear) {
      alert(`Năm sản xuất phải từ 2020 đến ${currentYear}!`)
      return
    }

    if (!form.theLoaiMoi || form.theLoaiMoi.length === 0) {
      alert('Vui lòng chọn thể loại phim!')
      return
    }
    
    if (!form.daoDienMoi || form.daoDienMoi.trim() === '') {
      alert('Vui lòng nhập đạo diễn phim!')
      return
    }
    
    if (!form.dienVienMoi || form.dienVienMoi.trim() === '') {
      alert('Vui lòng nhập diễn viên phim!')
      return
    }
    
    if (!form.poster) {
      alert('Vui lòng chọn poster phim!')
      return
    }
    
    if (!form.banner) {
      alert('Vui lòng chọn banner phim!')
      return
    }

    isSubmitting.value = true

    // Chuẩn bị formData để gửi lên backend
    const formData = new FormData()
    // Chuyển đổi các trường mới thành mảng
    formData.append('phim', JSON.stringify({
      tenPhim: form.tenPhim,
      moTa: form.moTa,
      thoiLuong: form.thoiLuong,
      ngayPhatHanh: form.ngayPhatHanh,
      trangThai: form.trangThai,
      dinhDang: form.dinhDang,
      tuoiGioiHan: form.tuoiGioiHan,
      namSanXuat: form.namSanXuat,
      trailerUrl: form.trailerUrl,
      theLoaiMoi: form.theLoaiMoi || [],
      daoDienMoi: form.daoDienMoi ? form.daoDienMoi.split(',').map(s => s.trim()) : [],
      dienVienMoi: form.dienVienMoi ? form.dienVienMoi.split(',').map(s => s.trim()) : []
    }))
    if (form.poster) formData.append('poster', form.poster)
    if (form.banner) formData.append('banner', form.banner)

    const response = await api.post('/api/phim/add', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    console.log('✅ Phim đã được thêm thành công:', response.data)
    console.log('📸 Poster URL:', response.data.posterUrl)
    console.log('🖼️ Banner URL:', response.data.bannerUrl)

    alert('✅ Thêm phim thành công!')
    
    // Thông báo cho các trang khác biết có thay đổi dữ liệu
    localStorage.setItem('moviesUpdated', Date.now().toString())
    window.dispatchEvent(new CustomEvent('moviesUpdated'))
    
    resetForm()
    router.push('/admin/movies')
  } catch (error) {
    console.error('❌ Lỗi khi thêm phim:', error)
    alert('❌ Lỗi khi thêm phim: ' + (error.response?.data?.message || error.message))
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.add-movie-page {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: white;
  padding: 20px 24px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.header-content { flex: 1; }

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 6px 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-icon { font-size: 28px; }

.page-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 400;
}

.form-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.movie-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group:nth-child(1),
.form-group:nth-child(2) {
  grid-column: 1 / -1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.required {
  color: #e74c3c;
  font-weight: bold;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  transition: all 0.3s ease;
  background: white;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.genre-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  background: white;
  transition: all 0.3s ease;
}

.genre-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
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

.static-value {
  display: inline-block;
  padding: 12px 16px;
  background: #f8f9fa;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  color: #6c757d;
  font-weight: 600;
}

.error-msg {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 4px;
  font-weight: 500;
}

.image-preview {
  margin-top: 8px;
  max-width: 200px;
}

.image-preview img {
  width: 100%;
  height: auto;
  border-radius: 8px;
  border: 2px solid #e1e8ed;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background: #f8f9fa;
  color: #6c757d;
  border: 2px solid #e1e8ed;
}

.btn-secondary:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .add-movie-page {
    padding: 16px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    justify-content: center;
  }
}
</style> 