<template>
  <div v-if="show" class="modal-backdrop" @click.self="close">
    <div class="modal-content" role="dialog" aria-modal="true" aria-label="Thêm phim">
      <button class="btn-close" @click="close" aria-label="Đóng modal">&times;</button>
      <h2>Thêm phim mới</h2>
      <form @submit.prevent="submitForm" enctype="multipart/form-data" novalidate>
        <div>
          <label for="tenPhim">Tên Phim:</label>
          <input id="tenPhim" v-model="form.tenPhim" type="text" required />
        </div>
        <div>
          <label for="moTa">Mô tả:</label>
          <textarea id="moTa" v-model="form.moTa" required></textarea>
        </div>
        <div>
          <label for="thoiLuong">Thời lượng (phút):</label>
          <input id="thoiLuong" v-model.number="form.thoiLuong" type="number" min="1" required />
        </div>
        <div>
          <label for="ngayPhatHanh">Ngày phát hành:</label>
          <input id="ngayPhatHanh" v-model="form.ngayPhatHanh" type="date" required />
        </div>
        <div>
          <label for="trangThai">Trạng thái:</label>
          <select id="trangThai" v-model="form.trangThai" required>
            <option value="SAP_CHIEU">Sắp Chiếu</option>
            <option value="DANG_CHIEU">Đang Chiếu</option>
            <option value="NGUNG_CHIEU">Ngừng Chiếu</option>
          </select>
        </div>
        <div>
          <label for="dinhDang">Định dạng:</label>
          <input id="dinhDang" v-model="form.dinhDang" type="text" required />
        </div>
        <div>
          <label for="trailerUrl">Trailer URL (YouTube):</label>
          <input id="trailerUrl" v-model="form.trailerUrl" type="url" required />
        </div>
        <div>
          <label for="theLoaiMoi">Thể loại mới (cách nhau dấu phẩy):</label>
          <input id="theLoaiMoi" v-model="form.theLoaiMoi" type="text" />
        </div>
        <div>
          <label for="daoDienMoi">Đạo diễn mới (cách nhau dấu phẩy):</label>
          <input id="daoDienMoi" v-model="form.daoDienMoi" type="text" />
        </div>
        <div>
          <label for="dienVienMoi">Diễn viên mới (cách nhau dấu phẩy):</label>
          <input id="dienVienMoi" v-model="form.dienVienMoi" type="text" />
        </div>
        <div>
          <label for="poster">Poster:</label>
          <input id="poster" type="file" @change="onFileChange($event, 'poster')" accept="image/*" />
        </div>
        <div>
          <label for="banner">Banner:</label>
          <input id="banner" type="file" @change="onFileChange($event, 'banner')" accept="image/*" />
        </div>
        <button type="submit">Lưu</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch, toRefs } from 'vue'
import axios from 'axios'

const props = defineProps({
  show: Boolean
})
const emit = defineEmits(['close', 'saved'])

const form = reactive({
  tenPhim: '',
  moTa: '',
  thoiLuong: null,
  ngayPhatHanh: '',
  trangThai: 'SAP_CHIEU',
  dinhDang: '',
  trailerUrl: '',
  theLoaiMoi: '',
  daoDienMoi: '',
  dienVienMoi: '',
  poster: null,
  banner: null,
})

function close() {
  emit('close')
  resetForm()
}

function resetForm() {
  form.tenPhim = ''
  form.moTa = ''
  form.thoiLuong = null
  form.ngayPhatHanh = ''
  form.trangThai = 'SAP_CHIEU'
  form.dinhDang = ''
  form.trailerUrl = ''
  form.theLoaiMoi = ''
  form.daoDienMoi = ''
  form.dienVienMoi = ''
  form.poster = null
  form.banner = null
}

function onFileChange(event, type) {
  const file = event.target.files[0]
  if (file) {
    if (type === 'poster') form.poster = file
    else if (type === 'banner') form.banner = file
  }
}

async function submitForm() {
  try {
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
      trailerUrl: form.trailerUrl,
      theLoaiMoi: form.theLoaiMoi ? form.theLoaiMoi.split(',').map(s => s.trim()) : [],
      daoDienMoi: form.daoDienMoi ? form.daoDienMoi.split(',').map(s => s.trim()) : [],
      dienVienMoi: form.dienVienMoi ? form.dienVienMoi.split(',').map(s => s.trim()) : []
    }))
    if (form.poster) formData.append('poster', form.poster)
    if (form.banner) formData.append('banner', form.banner)

    await axios.post('http://localhost:8080/api/phim/add', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    emit('saved')
    resetForm()
  } catch (error) {
    console.error('❌ Lỗi khi thêm phim:', error)
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.modal-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 0 15px rgba(0,0,0,0.3);
  overflow-y: auto;
  max-height: 90vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.btn-close {
  position: absolute;
  top: 6px;
  right: 10px;
  background: none;
  border: none;
  font-size: 30px;
  cursor: pointer;
  color: #444;
}
.btn-close:hover {
  color: #000;
}
form > div {
  margin-bottom: 12px;
}
label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}
input[type="text"],
input[type="number"],
input[type="url"],
input[type="date"],
textarea,
select {
  width: 100%;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 14px;
  box-sizing: border-box;
}
textarea {
  resize: vertical;
  min-height: 60px;
}
button[type="submit"] {
  background-color: #28a745;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
button[type="submit"]:hover {
  background-color: #218838;
}
</style>
