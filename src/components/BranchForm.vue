<template>
  <form @submit.prevent="handleSubmit" class="branch-form">
    <div class="form-header">
      <div class="form-icon">🏢</div>
      <h3 class="form-title">{{ isEdit ? 'Chỉnh Sửa Rạp Chiếu' : 'Thêm Rạp Chiếu Mới' }}</h3>
      <p class="form-subtitle">Nhập thông tin chi tiết về rạp chiếu</p>
    </div>

    <div class="form-content">
      <div class="form-group">
        <label for="tenRapChieu" class="form-label">
          <span class="label-icon">🎬</span>
          Tên Rạp Chiếu
        </label>
        <div class="input-wrapper">
          <input
            id="tenRapChieu"
            type="text"
            v-model="form.tenRapChieu"
            required
            placeholder="Nhập tên rạp chiếu"
            class="form-input"
          />
          <div class="input-focus-border"></div>
        </div>
      </div>

      <div class="form-group">
        <label for="diaChi" class="form-label">
          <span class="label-icon">📍</span>
          Địa Chỉ
        </label>
        <div class="input-wrapper">
          <input
            id="diaChi"
            type="text"
            v-model="form.diaChi"
            required
            placeholder="Nhập địa chỉ chi tiết"
            class="form-input"
          />
          <div class="input-focus-border"></div>
        </div>
      </div>

      <div class="form-group">
        <label for="trangThaiRapChieu" class="form-label">
          <span class="label-icon">🔄</span>
          Trạng Thái
        </label>
        <div class="input-wrapper">
          <select id="trangThaiRapChieu" v-model="form.trangThaiRapChieu" required class="form-select">
            <option value="HOAT_DONG">🟢 Hoạt động</option>
            <option value="TAM_DUNG">🟡 Tạm dừng</option>
            <option value="DONG_CUA">🔴 Đóng cửa</option>
          </select>
          <div class="input-focus-border"></div>
        </div>
      </div>

      <div class="form-group">
        <label for="soDienThoai" class="form-label">
          <span class="label-icon">📞</span>
          Số điện thoại
        </label>
        <div class="input-wrapper">
          <input
            id="soDienThoai"
            type="tel"
            v-model="form.soDienThoai"
            required
            placeholder="0123456789"
            class="form-input"
            pattern="[0-9]{10,11}"
          />
          <div class="input-focus-border"></div>
        </div>
      </div>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-primary">
        <span class="btn-icon">{{ isEdit ? '✏️' : '➕' }}</span>
        {{ isEdit ? 'Cập nhật' : 'Thêm mới' }}
      </button>
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">
        <span class="btn-icon">❌</span>
        Hủy
      </button>
    </div>
  </form>
</template>

<script>
export default {
  name: 'BranchForm',
  props: {
    branch: {
      type: Object,
      default: () => ({
        tenRapChieu: '',
        diaChi: '',
        trangThaiRapChieu: 'HOAT_DONG',
        soDienThoai: ''
      })
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit', 'cancel'],
  data() {
    return {
      form: { ...this.branch } // Clone để tránh sửa trực tiếp prop
    };
  },
  methods: {
    handleSubmit() {
      this.$emit('submit', { ...this.form });
    }
  }
};
</script>

<style scoped>
.branch-form {
  max-width: 500px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.form-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px 25px 25px;
  text-align: center;
  color: white;
  position: relative;
}

.form-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

.form-icon {
  font-size: 48px;
  margin-bottom: 15px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.form-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
  font-weight: 400;
}

.form-content {
  padding: 30px 25px;
}

.form-group {
  margin-bottom: 25px;
  position: relative;
}

.form-label {
  display: flex;
  align-items: center;
  font-weight: 600;
  margin-bottom: 10px;
  color: #374151;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.label-icon {
  margin-right: 8px;
  font-size: 16px;
}

.input-wrapper {
  position: relative;
}

.form-input,
.form-select {
  width: 100%;
  padding: 15px 18px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 16px;
  background: #ffffff;
  color: #374151;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-sizing: border-box;
  position: relative;
  z-index: 1;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.form-input::placeholder {
  color: #9ca3af;
  font-style: italic;
}

.input-focus-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 0;
}

.form-input:focus + .input-focus-border,
.form-select:focus + .input-focus-border {
  opacity: 0.1;
}

.form-actions {
  display: flex;
  gap: 15px;
  padding: 25px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.btn {
  flex: 1;
  padding: 15px 20px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(107, 114, 128, 0.3);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 114, 128, 0.4);
}

.btn-icon {
  font-size: 18px;
}

/* Responsive */
@media (max-width: 600px) {
  .branch-form {
    max-width: 100%;
    margin: 10px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-header {
    padding: 20px 15px;
  }
  
  .form-content {
    padding: 20px 15px;
  }
}

/* Animation */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.branch-form {
  animation: fadeInUp 0.6s ease-out;
}
</style>
