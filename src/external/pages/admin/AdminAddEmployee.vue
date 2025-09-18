<template>
  <div class="add-employee-page">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="icon">👥</span>
          Thêm Nhân Viên Mới
        </h1>
        <p class="page-subtitle">
          Thêm nhân viên mới vào hệ thống quản lý rạp chiếu phim
        </p>
      </div>
    </div>

    <!-- Form Section -->
    <div class="form-container">
      <div class="form-card">
        <div class="form-header">
          <h2 class="form-title">📝 Thông Tin Nhân Viên</h2>
          <p class="form-description">
            Vui lòng điền đầy đủ thông tin bên dưới
          </p>
        </div>

        <form class="employee-form" @submit.prevent="handleAddEmployee">
          <!-- Personal Information Section -->
          <div class="form-section">
            <h3 class="section-title">
              <span class="section-icon">👤</span>
              Thông Tin Cá Nhân
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label for="fullName" class="form-label">
                  Họ và tên <span class="required">*</span>
                </label>
                <input
                  id="fullName"
                  v-model="form.fullName"
                  type="text"
                  placeholder="Nhập họ và tên đầy đủ"
                  class="form-input"
                  @input="validateFullNameInput"
                  @focus="fullNameError = ''"
                  @blur="validateFullNameOnBlur"
                  required
                />
                <div v-if="fullNameError" class="field-error">
                  {{ fullNameError }}
                </div>
              </div>

              <div class="form-group">
                <label for="gender" class="form-label">
                  Giới tính <span class="required">*</span>
                </label>
                <select
                  id="gender"
                  v-model="form.gender"
                  class="form-select"
                  @change="validateGenderInput"
                  @focus="genderError = ''"
                  @blur="validateGenderOnBlur"
                  required
                >
                  <option value="">-- Chọn giới tính --</option>
                  <option value="Nam">Nam</option>
                  <option value="Nu">Nữ</option>
                  <option value="Khac">Khác</option>
                </select>
                <div v-if="genderError" class="field-error">
                  {{ genderError }}
                </div>
              </div>

              <div class="form-group">
                <label for="birthday" class="form-label">
                  Ngày sinh <span class="required">*</span>
                </label>
                <input
                  id="birthday"
                  v-model="form.birthday"
                  type="date"
                  class="form-input"
                  @input="validateBirthdayInput"
                  @focus="birthdayError = ''"
                  @blur="validateBirthdayOnBlur"
                  required
                />
                <div v-if="birthdayError" class="field-error">
                  {{ birthdayError }}
                </div>
              </div>

              <div class="form-group">
                <label for="cccd" class="form-label">
                  CCCD <span class="required">*</span>
                </label>
                <input
                  id="cccd"
                  v-model="form.cccd"
                  type="text"
                  placeholder="Nhập số CCCD"
                  class="form-input"
                  maxlength="12"
                  @input="validateCCCDInput"
                  @focus="cccdError = ''"
                  @blur="validateCCCDOnBlur"
                  required
                />
                <div v-if="cccdError" class="field-error">
                  {{ cccdError }}
                </div>
              </div>
            </div>
          </div>

          <!-- Contact Information Section -->
          <div class="form-section">
            <h3 class="section-title">
              <span class="section-icon">📞</span>
              Thông Tin Liên Hệ
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label for="phone" class="form-label">
                  Số điện thoại <span class="required">*</span>
                </label>
                <input
                  id="phone"
                  v-model="form.phone"
                  type="tel"
                  placeholder="VD: 0123456789 (10 số)"
                  class="form-input"
                  maxlength="10"
                  pattern="[0-9]{10}"
                  @input="validatePhoneInput"
                  @focus="phoneError = ''"
                  @blur="validatePhoneOnBlur"
                  required
                />
                <div v-if="phoneError" class="field-error">
                  {{ phoneError }}
                </div>
              </div>

              <div class="form-group">
                <label for="email" class="form-label">
                  Email <span class="required">*</span>
                </label>
                <input
                  id="email"
                  v-model="form.email"
                  type="email"
                  placeholder="VD: example@email.com"
                  class="form-input"
                  @input="validateEmailInput"
                  @focus="emailError = ''"
                  @blur="validateEmailOnBlur"
                  required
                />
                <div v-if="emailError" class="field-error">
                  {{ emailError }}
                </div>
              </div>
            </div>
          </div>

          <!-- Work Information Section -->
          <div class="form-section">
            <h3 class="section-title">
              <span class="section-icon">💼</span>
              Thông Tin Công Việc
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label for="startDate" class="form-label">
                  Ngày vào làm <span class="required">*</span>
                </label>
                <input
                  id="startDate"
                  v-model="form.startDate"
                  type="date"
                  class="form-input"
                  :min="getTodayDate()"
                  @input="validateStartDateInput"
                  @focus="startDateError = ''"
                  @blur="validateStartDateOnBlur"
                  required
                />
                <div v-if="startDateError" class="field-error">
                  {{ startDateError }}
                </div>
              </div>

              <div class="form-group">
                <label for="cinemaId" class="form-label">
                  Rạp chiếu <span class="required">*</span>
                </label>
                <select
                  id="cinemaId"
                  v-model="form.cinemaId"
                  class="form-select"
                  @change="validateCinemaIdInput"
                  @focus="cinemaIdError = ''"
                  @blur="validateCinemaIdOnBlur"
                  :disabled="isLoadingCinemas"
                  required
                >
                  <option value="">
                    {{
                      isLoadingCinemas
                        ? "🔄 Đang tải danh sách rạp..."
                        : "-- Chọn rạp chiếu --"
                    }}
                  </option>
                  <option
                    v-for="cinema in cinemas"
                    :key="cinema.idRapChieu"
                    :value="cinema.idRapChieu"
                  >
                    {{ cinema.tenRapChieu }} - {{ cinema.diaChi }}
                  </option>
                </select>
                <div v-if="cinemaIdError" class="field-error">
                  {{ cinemaIdError }}
                </div>
              </div>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="form-actions">
            <button type="submit" class="submit-btn" :disabled="isLoading">
              <span v-if="isLoading" class="loading-spinner"></span>
              <span v-else class="btn-text">
                <span class="btn-icon">➕</span>
                Thêm Nhân Viên
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Messages Section -->
    <div class="messages-section">
      <div v-if="errorMessage" class="message error-message">
        <span class="message-icon">❌</span>
        <span class="message-text">{{ errorMessage }}</span>
      </div>

      <div v-if="successMessage" class="message success-message">
        <span class="message-icon">✅</span>
        <span class="message-text">{{ successMessage }}</span>
      </div>

      <div v-if="newEmployeeId" class="message info-message">
        <span class="message-icon">🎉</span>
        <div class="message-content">
          <div class="message-title">Nhân viên đã được thêm thành công!</div>
          <div class="employee-id">
            ID nhân viên: <strong>{{ newEmployeeId }}</strong>
          </div>
          <div class="message-note">
            Hãy gửi ID này cho nhân viên để họ đăng ký tài khoản!
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from '@/services/api';
import { fetchBranches } from "../../services/branchService";
;

const form = ref({
  fullName: "",
  gender: "",
  phone: "",
  email: "",
  birthday: "",
  cccd: "",
  startDate: "",
  cinemaId: "",
});
const cinemas = ref([]);
const isLoadingCinemas = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const newEmployeeId = ref(null);
const isLoading = ref(false);
const phoneError = ref("");
const emailError = ref("");
const fullNameError = ref("");
const genderError = ref("");
const cccdError = ref("");
const birthdayError = ref("");
const startDateError = ref("");
const cinemaIdError = ref("");

function validatePhoneInput() {
  const phone = form.value.phone;

  // Clear error if empty
  if (!phone) {
    phoneError.value = "";
    return;
  }

  // Check if starts with 0
  if (!phone.startsWith("0")) {
    phoneError.value = "❌ Số điện thoại phải bắt đầu bằng số 0!";
    return;
  }

  // Check if contains only numbers
  if (!/^\d+$/.test(phone)) {
    phoneError.value = "❌ Số điện thoại chỉ được chứa số!";
    return;
  }

  // Check length
  if (phone.length !== 10) {
    phoneError.value = "❌ Số điện thoại phải có đúng 10 số!";
    return;
  }

  // All validations passed
  phoneError.value = "";
}

function validateEmailInput() {
  const email = form.value.email;

  // Clear error if empty
  if (!email) {
    emailError.value = "";
    return;
  }

  // Comprehensive email regex
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  if (!emailRegex.test(email)) {
    emailError.value =
      "❌ Email không đúng định dạng! (VD: example@domain.com)";
    return;
  }

  // Check for common invalid patterns
  if (email.startsWith(".") || email.endsWith(".") || email.includes("..")) {
    emailError.value =
      "❌ Email không được bắt đầu hoặc kết thúc bằng dấu chấm!";
    return;
  }

  if (email.includes("@.") || email.includes(".@")) {
    emailError.value = "❌ Email có ký tự không hợp lệ!";
    return;
  }

  // All validations passed
  emailError.value = "";
}

function validateFullNameInput() {
  const fullName = form.value.fullName.trim();

  if (!fullName) {
    fullNameError.value = "";
    return;
  }

  if (fullName.length < 2) {
    fullNameError.value = "❌ Họ và tên phải có ít nhất 2 ký tự!";
    return;
  }

  if (fullName.length > 50) {
    fullNameError.value = "❌ Họ và tên không được quá 50 ký tự!";
    return;
  }

  // Check for valid characters (Vietnamese characters, spaces, hyphens)
  const nameRegex = /^[a-zA-ZÀ-ỹ\s\-]+$/;
  if (!nameRegex.test(fullName)) {
    fullNameError.value =
      "❌ Họ và tên chỉ được chứa chữ cái, dấu cách và gạch ngang!";
    return;
  }

  fullNameError.value = "";
}

function validateCCCDInput() {
  const cccd = form.value.cccd;

  if (!cccd) {
    cccdError.value = "";
    return;
  }

  if (!/^\d+$/.test(cccd)) {
    cccdError.value = "❌ CCCD chỉ được chứa số!";
    return;
  }

  if (cccd.length !== 12) {
    cccdError.value = "❌ CCCD phải có đúng 12 số!";
    return;
  }

  cccdError.value = "";
}

function validateBirthdayInput() {
  const birthday = form.value.birthday;

  if (!birthday) {
    birthdayError.value = "";
    return;
  }

  const birthDate = new Date(birthday);
  const today = new Date();
  const age = today.getFullYear() - birthDate.getFullYear();

  if (age < 18) {
    birthdayError.value = "❌ Nhân viên phải từ 18 tuổi trở lên!";
    return;
  }

  if (age > 65) {
    birthdayError.value = "❌ Nhân viên không được quá 65 tuổi!";
    return;
  }

  if (birthDate > today) {
    birthdayError.value = "❌ Ngày sinh không thể là tương lai!";
    return;
  }

  birthdayError.value = "";
}

function validateStartDateInput() {
  const startDate = form.value.startDate;

  if (!startDate) {
    startDateError.value = "";
    return;
  }

  const start = new Date(startDate);
  const today = new Date();

  // Reset time to start of day for accurate comparison
  start.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

  if (start < today) {
    startDateError.value = "❌ Ngày vào làm phải từ hôm nay trở đi!";
    return;
  }

  startDateError.value = "";
}

function validateCinemaIdInput() {
  const cinemaId = form.value.cinemaId;

  if (!cinemaId) {
    cinemaIdError.value = "❌ Vui lòng chọn rạp chiếu!";
    return;
  }

  cinemaIdError.value = "";
}

// Blur validation functions for immediate feedback
function validateFullNameOnBlur() {
  if (!form.value.fullName.trim()) {
    fullNameError.value = "❌ Vui lòng nhập họ và tên!";
  }
}

function validatePhoneOnBlur() {
  if (!form.value.phone) {
    phoneError.value = "❌ Vui lòng nhập số điện thoại!";
  }
}

function validateEmailOnBlur() {
  if (!form.value.email) {
    emailError.value = "❌ Vui lòng nhập email!";
  }
}

function validateCCCDOnBlur() {
  if (!form.value.cccd) {
    cccdError.value = "❌ Vui lòng nhập CCCD!";
  }
}

function validateBirthdayOnBlur() {
  if (!form.value.birthday) {
    birthdayError.value = "❌ Vui lòng chọn ngày sinh!";
  }
}

function validateStartDateOnBlur() {
  if (!form.value.startDate) {
    startDateError.value = "❌ Vui lòng chọn ngày vào làm!";
  } else {
    // Also validate the date when blur
    validateStartDateInput();
  }
}

function validateCinemaIdOnBlur() {
  if (!form.value.cinemaId) {
    cinemaIdError.value = "❌ Vui lòng chọn rạp chiếu!";
  }
}

function validateGenderInput() {
  genderError.value = "";
}

function validateGenderOnBlur() {
  if (!form.value.gender) {
    genderError.value = "❌ Vui lòng chọn giới tính!";
  }
}

function getTodayDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

async function fetchCinemas() {
  isLoadingCinemas.value = true;
  try {
    const response = await fetchBranches();

    if (response.data) {
      cinemas.value = response.data;
      console.log("✅ Danh sách rạp:", cinemas.value);
    }
  } catch (error) {
    console.error("❌ Lỗi khi lấy danh sách rạp:", error);
    errorMessage.value = "❌ Không thể tải danh sách rạp chiếu!";

    // Fallback data nếu API lỗi
    cinemas.value = [
      {
        idRapChieu: 1,
        tenRapChieu: "Rạp CGV Aeon Mall",
        diaChi: "123 Đường ABC, Quận 1, TP.HCM",
      },
      {
        idRapChieu: 2,
        tenRapChieu: "Rạp CGV Crescent Mall",
        diaChi: "456 Đường XYZ, Quận 7, TP.HCM",
      },
      {
        idRapChieu: 3,
        tenRapChieu: "Rạp CGV Landmark 81",
        diaChi: "789 Đường DEF, Quận Bình Thạnh, TP.HCM",
      },
    ];
  } finally {
    isLoadingCinemas.value = false;
  }
}

onMounted(() => {
  fetchCinemas();
});

async function handleAddEmployee() {
  if (isLoading.value) return; // Prevent multiple requests

  errorMessage.value = "";
  successMessage.value = "";
  newEmployeeId.value = null;
  isLoading.value = true;

  // Validate all required fields first
  let hasEmptyFields = false;

  if (!form.value.fullName.trim()) {
    fullNameError.value = "❌ Vui lòng nhập họ và tên!";
    hasEmptyFields = true;
  }

  if (!form.value.gender) {
    genderError.value = "❌ Vui lòng chọn giới tính!";
    hasEmptyFields = true;
  }

  if (!form.value.phone) {
    phoneError.value = "❌ Vui lòng nhập số điện thoại!";
    hasEmptyFields = true;
  }

  if (!form.value.email) {
    emailError.value = "❌ Vui lòng nhập email!";
    hasEmptyFields = true;
  }

  if (!form.value.birthday) {
    birthdayError.value = "❌ Vui lòng chọn ngày sinh!";
    hasEmptyFields = true;
  }

  if (!form.value.cccd) {
    cccdError.value = "❌ Vui lòng nhập CCCD!";
    hasEmptyFields = true;
  }

  if (!form.value.startDate) {
    startDateError.value = "❌ Vui lòng chọn ngày vào làm!";
    hasEmptyFields = true;
  } else {
    // Validate date logic
    const start = new Date(form.value.startDate);
    const today = new Date();

    // Reset time to start of day for accurate comparison
    start.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    if (start < today) {
      startDateError.value = "❌ Ngày vào làm phải từ hôm nay trở đi!";
      hasEmptyFields = true;
    }
  }

  if (!form.value.cinemaId) {
    cinemaIdError.value = "❌ Vui lòng chọn rạp chiếu!";
    hasEmptyFields = true;
  }

  if (hasEmptyFields) {
    errorMessage.value = "❌ Vui lòng điền đầy đủ tất cả thông tin bắt buộc!";
    isLoading.value = false;
    return;
  }
  // Check phone validation
  if (phoneError.value) {
    errorMessage.value = "❌ Vui lòng sửa lỗi số điện thoại!";
    isLoading.value = false;
    return;
  }

  // Check email validation
  if (emailError.value) {
    errorMessage.value = "❌ Vui lòng sửa lỗi email!";
    isLoading.value = false;
    return;
  }

  // Check other validations
  if (
    fullNameError.value ||
    genderError.value ||
    cccdError.value ||
    birthdayError.value ||
    startDateError.value ||
    cinemaIdError.value
  ) {
    errorMessage.value = "❌ Vui lòng sửa các lỗi trong form!";
    isLoading.value = false;
    return;
  }

  try {
    // Lấy token admin từ localStorage
    const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
    const token = userInfo.token;

    // Kiểm tra email đã tồn tại chưa
    const emailCheckResponse = await api.get(
      `/api/employees/check-email?email=${form.value.email}`,
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
        },
      }
    );

    if (emailCheckResponse.data) {
      errorMessage.value = "❌ Email đã tồn tại!";
      isLoading.value = false;
      return;
    }

    // Kiểm tra CCCD đã tồn tại chưa
    const cccdCheckResponse = await api.get(
      `/api/employees/check-cccd?cccd=${form.value.cccd}`,
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
        },
      }
    );

    if (cccdCheckResponse.data) {
      errorMessage.value = "❌ CCCD đã tồn tại!";
      isLoading.value = false;
      return;
    }

    // Kiểm tra số điện thoại đã tồn tại chưa
    const phoneCheckResponse = await api.get(
      `/api/employees/check-sdt?soDienThoai=${form.value.phone}`,
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
        },
      }
    );

    if (phoneCheckResponse.data) {
      errorMessage.value = "❌ Số điện thoại đã tồn tại!";
      isLoading.value = false;
      return;
    }

    // Nếu tất cả đều OK thì thêm nhân viên
    const response = await api.post(
      `/api/employees/addEmployee`,
      {
        tenNhanVien: form.value.fullName,
        email: form.value.email,
        soDienThoai: form.value.phone,
        ngaySinh: form.value.birthday, // yyyy-MM-dd
        gioiTinh: form.value.gender, // Nam/Nu/Khac
        cccd: form.value.cccd,
        ngayVaoLam: form.value.startDate, // yyyy-MM-dd
        idRapChieu: Number(form.value.cinemaId),
      },
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
        },
      }
    );

    if (response.data) {
      successMessage.value =
        response.data.message || "✅ Thêm nhân viên thành công!";
      newEmployeeId.value = response.data.idNhanVien;
      form.value = {
        fullName: "",
        gender: "",
        phone: "",
        email: "",
        birthday: "",
        cccd: "",
        startDate: "",
        cinemaId: "",
      };
    } else {
      errorMessage.value = "❌ Thêm nhân viên thất bại! Vui lòng thử lại.";
    }
  } catch (error) {
    if (error.response) {
      errorMessage.value =
        error.response.data.message || "❌ Thêm nhân viên thất bại!";
    } else {
      errorMessage.value = "❌ Không thể kết nối đến server!";
    }
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
.add-employee-page {
  padding: 32px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* Header Section */
.page-header {
  background: white;
  padding: 32px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin: 0 0 12px 0;
  font-size: 32px;
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
  font-size: 18px;
  font-weight: 400;
}

/* Form Container */
.form-container {
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.form-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  text-align: center;
}

.form-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
}

.form-description {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

/* Form Sections */
.employee-form {
  padding: 32px;
}

.form-section {
  margin-bottom: 40px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 24px 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  padding-bottom: 12px;
  border-bottom: 2px solid #ecf0f1;
}

.section-icon {
  font-size: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

/* Form Groups */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-weight: 600;
  color: #34495e;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #e74c3c;
  font-weight: 700;
}

.form-input,
.form-select {
  padding: 14px 16px;
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  font-size: 15px;
  background: #f8f9fa;
  transition: all 0.3s ease;
  color: #2c3e50;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input::placeholder {
  color: #95a5a6;
}

.field-error {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 4px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Submit Button */
.form-actions {
  margin-top: 40px;
  text-align: center;
}

.submit-btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-text {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-icon {
  font-size: 18px;
}

/* Loading Spinner */
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Messages Section */
.messages-section {
  max-width: 800px;
  margin: 32px auto 0 auto;
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  font-size: 15px;
  line-height: 1.5;
}

.message-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.error-message {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.success-message {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.info-message {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.message-content {
  flex: 1;
}

.message-title {
  font-weight: 700;
  margin-bottom: 8px;
}

.employee-id {
  background: white;
  padding: 12px 16px;
  border-radius: 8px;
  margin: 12px 0;
  font-family: "Courier New", monospace;
  font-size: 16px;
  border: 2px dashed #667eea;
}

.message-note {
  font-size: 14px;
  opacity: 0.8;
  margin-top: 8px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .add-employee-page {
    padding: 16px;
  }

  .page-header {
    padding: 24px 20px;
  }

  .page-title {
    font-size: 24px;
    flex-direction: column;
    gap: 8px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .form-header {
    padding: 24px 20px;
  }

  .form-title {
    font-size: 20px;
  }

  .employee-form {
    padding: 24px 20px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .section-title {
    font-size: 18px;
  }

  .submit-btn {
    width: 100%;
    justify-content: center;
  }

  .message {
    padding: 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 20px 16px;
  }

  .employee-form {
    padding: 20px 16px;
  }

  .form-input,
  .form-select {
    padding: 12px 14px;
    font-size: 14px;
  }
}
</style>
