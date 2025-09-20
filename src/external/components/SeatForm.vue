<template>
  <form @submit.prevent="handleSubmit" class="seat-form">
    <!-- Hàng Ghế -->
    <div class="form-group">
      <label for="hangGhe">Hàng ghế</label>
      <input
        id="hangGhe"
        v-model="form.hangGhe"
        type="text"
        required
        placeholder="Nhập hàng ghế (VD: A)"
      />
    </div>

    <!-- Số Ghế -->
    <div class="form-group">
      <label for="soGhe">Số ghế</label>
      <input
        id="soGhe"
        v-model="form.soGhe"
        type="text"
        required
        placeholder="Nhập số ghế (VD: 01, 12)"
      />
    </div>

    <!-- ID Phòng Chiếu -->
    <div class="form-group">
      <label for="idPhongChieu">ID phòng chiếu</label>
      <input
        id="idPhongChieu"
        v-model.number="form.idPhongChieu"
        type="number"
        required
        placeholder="Nhập ID phòng chiếu"
      />
    </div>

    <!-- Tên Phòng Chiếu -->
    <div class="form-group">
      <label for="tenPhongChieu">Tên phòng chiếu</label>
      <input
        id="tenPhongChieu"
        v-model="form.tenPhongChieu"
        type="text"
        required
        placeholder="Nhập tên phòng chiếu"
      />
    </div>

    <!-- Vị Trí Ghế -->
    <div class="form-group">
      <label for="viTriGhe">Vị trí ghế</label>
      <input
        id="viTriGhe"
        v-model="form.viTriGhe"
        type="text"
        required
        placeholder="Nhập vị trí ghế (VD: A01)"
      />
    </div>

    <!-- Trạng Thái -->
    <div class="form-group">
      <label for="trangThai">Trạng thái</label>
      <select id="trangThai" v-model="form.trangThai" required>
        <option value="CON_TRONG">Còn trống</option>
        <option value="DA_DAT">Đã đặt</option>
        <option value="DANG_SU_DUNG">Đang sử dụng</option>
      </select>
    </div>

    <!-- Loại Ghế -->
    <div class="form-group">
      <label for="loaiGhe">Loại ghế</label>
      <select id="loaiGhe" v-model="form.loaiGhe" required>
        <option value="Thuong">Thường</option>
        <option value="Vip">VIP</option>
      </select>
    </div>

    <!-- Hành Động -->
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">
        {{ isEdit ? 'Cập nhật ghế' : 'Thêm ghế' }}
      </button>
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">Hủy</button>
    </div>
  </form>
</template>

<script>
export default {
  name: 'SeatForm',
  props: {
    seat: {
      type: Object,
      default: null
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit', 'cancel'],
  data() {
    return {
      form: {
        hangGhe: '',
        soGhe: '',
        idPhongChieu: null,
        tenPhongChieu: '',
        viTriGhe: '',
        trangThai: 'CON_TRONG',
        loaiGhe: 'Thuong'
      }
    }
  },
  watch: {
    seat: {
      handler(newSeat) {
        if (newSeat) {
          this.form = { ...newSeat }
        }
      },
      immediate: true
    }
  },
  methods: {
    handleSubmit() {
      this.$emit('submit', { ...this.form })
    }
  }
}
</script>

<style scoped>
.seat-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 400px;
  margin: 0 auto;
}

.form-group label {
  font-weight: bold;
  margin-bottom: 0.5rem;
  display: block;
}

input,
select {
  width: 100%;
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}
</style>
