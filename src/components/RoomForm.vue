<template>
  <form @submit.prevent="handleSubmit" class="room-form">
    <div class="form-grid">
      <!-- Tên phòng -->
      <div class="form-group">
        <label for="tenPhongChieu">Tên phòng chiếu <span>*</span></label>
        <input id="tenPhongChieu" v-model="form.tenPhongChieu" type="text" required placeholder="Nhập tên phòng" />
      </div>
      <!-- Diện tích -->
      <div class="form-group">
        <label for="dienTichPhong">Diện tích (m²) <span>*</span></label>
        <input id="dienTichPhong" v-model.number="form.dienTichPhong" type="number" min="100" step="0.01" required placeholder="Nhập diện tích" />
        <div v-if="form.dienTichPhong !== null && form.dienTichPhong < 100" class="error-msg">Diện tích phải lớn hơn hoặc bằng 100 m²</div>
      </div>
             <!-- Trạng thái phòng -->
       <div class="form-group">
         <label>Trạng thái phòng</label>
         <span v-if="!isEdit" class="static-value">Hoạt động</span>
         <select v-else id="trangThaiPhongChieu" v-model="form.trangThaiPhongChieu" required>
           <option value="HOAT_DONG">Hoạt động</option>
           <option value="TAM_DUNG">Tạm dừng</option>
           <option value="DONG_CUA">Đóng cửa</option>
         </select>
       </div>
             <!-- ID Rạp Chiếu -->
       <div class="form-group">
         <label for="idRapChieu">Chọn Rạp Chiếu <span>*</span></label>
                   <select id="idRapChieu" v-model.number="form.idRapChieu" required @change="fetchRapInfo">
            <option value="">-- Chọn rạp chiếu --</option>
            <option v-for="rap in rapChieuList" :key="rap.idRapChieu" :value="rap.idRapChieu">
              {{ rap.idRapChieu }} - {{ rap.tenRapChieu }} ({{ rap.diaChi }})
            </option>
          </select>
         <div v-if="rapError" class="error-msg">{{ rapError }}</div>
       </div>
      <!-- Tên Rạp Chiếu -->
      <div class="form-group">
        <label for="tenRapChieu">Tên rạp chiếu</label>
        <input id="tenRapChieu" v-model="form.tenRapChieu" type="text" readonly />
      </div>
      <!-- Địa chỉ -->
      <div class="form-group">
        <label for="diaChi">Địa chỉ rạp</label>
        <input id="diaChi" v-model="form.diaChi" type="text" readonly />
      </div>
      <!-- Số điện thoại -->
      <div class="form-group">
        <label for="soDienThoai">SĐT rạp</label>
        <input id="soDienThoai" v-model="form.soDienThoai" type="tel" readonly />
      </div>
      <!-- Trạng thái rạp -->
      <div class="form-group">
        <label for="trangThaiRapChieu">Trạng thái rạp</label>
        <input id="trangThaiRapChieu" v-model="form.trangThaiRapChieu" type="text" readonly />
      </div>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">{{ isEdit ? 'Cập nhật' : 'Thêm mới' }}</button>
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">Hủy</button>
    </div>
  </form>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  isEdit: Boolean,
  room: {
    type: Object,
    default: () => ({
      tenPhongChieu: '',
      dienTichPhong: null,
      trangThaiPhongChieu: 'HOAT_DONG',
      idRapChieu: '',
      tenRapChieu: '',
      diaChi: '',
      soDienThoai: '',
      trangThaiRapChieu: ''
    })
  }
})
const emit = defineEmits(['submit', 'cancel'])
const form = reactive({ ...props.room })
const rapError = ref('')
const rapChieuList = ref([])

// Fetch danh sách rạp chiếu khi component mount
async function fetchRapChieuList() {
  try {
    const res = await axios.get('http://localhost:8080/api/rap_chieu')
    rapChieuList.value = res.data || []
  } catch (error) {
    console.error('Lỗi khi lấy danh sách rạp chiếu:', error)
    rapChieuList.value = []
  }
}

// Gọi fetchRapChieuList khi component mount
fetchRapChieuList()

async function fetchRapInfo() {
  rapError.value = ''
  if (form.idRapChieu === '' || form.idRapChieu === null) {
    // Reset thông tin rạp khi không chọn
    form.tenRapChieu = ''
    form.diaChi = ''
    form.soDienThoai = ''
    form.trangThaiRapChieu = ''
    return
  }
  
  try {
    const res = await axios.get(`http://localhost:8080/api/rap_chieu/${form.idRapChieu}`)
    const data = res.data
    form.tenRapChieu = data.tenRapChieu || ''
    form.diaChi = data.diaChi || ''
    form.soDienThoai = data.soDienThoai || ''
    form.trangThaiRapChieu = data.trangThaiRapChieu || ''
    
    // Kiểm tra trạng thái rạp chiếu
    if (data.trangThaiRapChieu === 'BAO_TRI' || data.trangThaiRapChieu === 'KHONG_HOAT_DONG') {
      rapError.value = 'Không thể thêm phòng vào rạp đang bảo trì hoặc không hoạt động!'
      form.tenRapChieu = ''
      form.diaChi = ''
      form.soDienThoai = ''
      form.trangThaiRapChieu = ''
      return
    }
  } catch (e) {
    form.tenRapChieu = ''
    form.diaChi = ''
    form.soDienThoai = ''
    form.trangThaiRapChieu = ''
    rapError.value = 'Không tìm thấy rạp với ID này!'
  }
}

function handleSubmit() {
  console.log('Vào handleSubmit, form:', { ...form });
  if (form.dienTichPhong === null || form.dienTichPhong < 100) {
    console.log('Lỗi diện tích');
    rapError.value = ''
    alert('Diện tích phòng phải lớn hơn hoặc bằng 100 m²!')
    return
  }
  if (form.idRapChieu === '' || form.idRapChieu === null || form.idRapChieu <= 0) {
    console.log('Lỗi idRapChieu');
    rapError.value = 'Vui lòng chọn rạp chiếu!'
    return
  }
  // Khi thêm mới, trạng thái phòng luôn là HOAT_DONG
  if (!props.isEdit) {
    form.trangThaiPhongChieu = 'HOAT_DONG'
  } else if (!form.trangThaiPhongChieu || form.trangThaiPhongChieu === '') {
    console.log('Lỗi trangThaiPhongChieu');
    alert('Trạng thái phòng không được để trống!')
    return
  }
  if (rapError.value) {
    console.log('Có rapError:', rapError.value);
    return
  }
  if (form.trangThaiRapChieu === 'BAO_TRI' || form.trangThaiRapChieu === 'KHONG_HOAT_DONG') {
    console.log('Rạp không hoạt động');
    rapError.value = 'Không thể thêm phòng vào rạp đang bảo trì hoặc không hoạt động!'
    return
  }
  
  // Chỉ gửi 4 trường cần thiết lên backend
  const dataToSubmit = {
    tenPhongChieu: form.tenPhongChieu,
    dienTichPhong: form.dienTichPhong,
    trangThaiPhongChieu: form.trangThaiPhongChieu,
    idRapChieu: form.idRapChieu
  }
  
  // Log dữ liệu gửi lên để debug
  console.log('Dữ liệu gửi lên:', dataToSubmit)
  console.log('trangThaiPhongChieu:', form.trangThaiPhongChieu)
  emit('submit', dataToSubmit)
}
</script>

<style scoped>
.room-form {
  max-width: 700px;
  padding: 24px;
  border: 1.5px solid #e0e7ef;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 4px 24px rgba(30,136,229,0.08);
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
input, select, textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1.5px solid #bdbdbd;
  font-size: 15px;
  box-sizing: border-box;
  background: #f8fafc;
  color: #222;
  transition: border 0.2s, box-shadow 0.2s;
}
input:focus, select:focus, textarea:focus {
  border: 1.5px solid #3949ab;
  outline: none;
  box-shadow: 0 0 0 2px #c5cae9;
  background: #fff;
  color: #222;
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
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
  justify-content: flex-end;
}
.btn-primary {
  background: linear-gradient(90deg,#10b981,#06b6d4);
  color: white;
  font-weight: 700;
  border: none;
  border-radius: 8px;
  padding: 10px 28px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-primary:hover {
  background: linear-gradient(90deg,#06b6d4,#10b981);
}
.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1.5px solid #bdbdbd;
  border-radius: 8px;
  padding: 10px 28px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-secondary:hover {
  background: #e0e7ef;
}
@media (max-width: 900px) {
  .room-form { padding: 12px; }
  .form-grid { grid-template-columns: 1fr; gap: 14px 0; }
  .form-actions { flex-direction: column; gap: 10px; }
}
</style>
