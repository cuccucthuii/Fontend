<template>
  <div class="invoice-container">
    <div class="invoice-header">
      <div class="cinema-logo">
        <h1>🎬 CINEMA BOOKING</h1>
        <p>Hóa đơn đặt vé</p>
      </div>
      <div class="invoice-info">
        <h2>HÓA ĐƠN #{{ invoiceNumber }}</h2>
        <p>Ngày: {{ currentDate }}</p>
        <p>Giờ: {{ currentTime }}</p>
      </div>
    </div>

    <div class="customer-info">
      <h3>📋 Thông tin khách hàng</h3>
      <div class="info-grid">
        <div class="info-item">
          <label>Họ tên:</label>
          <span>{{ customerInfo.name || 'Chưa cập nhật' }}</span>
        </div>
        <div class="info-item">
          <label>Số điện thoại:</label>
          <span>{{ customerInfo.phone || 'Chưa cập nhật' }}</span>
        </div>
        <div class="info-item">
          <label>Email:</label>
          <span>{{ customerInfo.email || 'Chưa cập nhật' }}</span>
        </div>
      </div>
    </div>

    <div class="movie-info">
      <h3>🎥 Thông tin phim</h3>
      <div class="movie-details">
        <div class="movie-poster">
          <img :src="movieInfo.poster || '/logo.png?v=2'" :alt="movieInfo.title" />
        </div>
        <div class="movie-text">
          <h4>{{ movieInfo.title || 'Đang tải...' }}</h4>
          <div class="movie-meta">
            <p v-if="movieInfo.duration"><strong>Thời lượng:</strong> {{ movieInfo.duration }}</p>
            <p v-if="movieInfo.genre"><strong>Thể loại:</strong> {{ movieInfo.genre }}</p>
            <p v-if="movieInfo.director"><strong>Đạo diễn:</strong> {{ movieInfo.director }}</p>
            <p><strong>Rạp:</strong> {{ showInfo.cinema || 'Chưa chọn' }}</p>
            <p><strong>Phòng:</strong> {{ showInfo.room || 'Chưa chọn' }}</p>
            <p><strong>Suất chiếu:</strong> {{ showInfo.showtime || 'Chưa chọn' }}</p>
            <p><strong>Ngày chiếu:</strong> {{ showInfo.date || 'Chưa chọn' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="seats-info">
      <h3>💺 Ghế đã chọn</h3>
      <div class="seats-list">
        <div v-if="selectedSeats.length > 0" class="seat-tags">
          <span v-for="seat in selectedSeats" :key="seat.id" class="seat-tag">
            {{ seat.name }}
          </span>
        </div>
        <p v-else class="no-seats">Chưa chọn ghế</p>
      </div>
    </div>

    <div class="combo-info" v-if="selectedCombos.length > 0">
      <h3>🍿 Combo đã chọn</h3>
      <div class="combo-list">
        <div v-for="combo in selectedCombos" :key="combo.id" class="combo-item">
          <div class="combo-details">
            <h4>{{ combo.name }}</h4>
            <p>{{ combo.description }}</p>
          </div>
          <div class="combo-quantity">
            <span>x{{ combo.quantity }}</span>
          </div>
          <div class="combo-price">
            <span>{{ formatCurrency(combo.price * combo.quantity) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="payment-summary">
      <h3>💰 Tóm tắt thanh toán</h3>
      <div class="summary-list">
        <div class="summary-item">
          <span>Tiền vé ({{ selectedSeats.length }} ghế):</span>
          <span>{{ formatCurrency(ticketTotal) }}</span>
        </div>
        <div class="summary-item" v-if="comboTotal > 0">
          <span>Combo:</span>
          <span>{{ formatCurrency(comboTotal) }}</span>
        </div>
        <div class="summary-item subtotal">
          <span>Tạm tính:</span>
          <span>{{ formatCurrency(subtotal) }}</span>
        </div>
        <div class="summary-item tax">
          <span>VAT (10%):</span>
          <span>{{ formatCurrency(taxAmount) }}</span>
        </div>
        <div class="summary-item total">
          <span>Tổng cộng:</span>
          <span>{{ formatCurrency(totalAmount) }}</span>
        </div>
      </div>
    </div>

    <div class="payment-actions">
      <button class="btn-back" @click="goBack">
        ← Quay lại
      </button>
      <button class="btn-payment" @click="processPayment">
        💳 Thanh toán VNPay
      </button>
    </div>

    <div class="invoice-footer">
      <p>🎬 Cảm ơn bạn đã chọn dịch vụ của chúng tôi!</p>
      <p>Vui lòng giữ lại hóa đơn này để làm thủ tục vào xem phim.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// Reactive data
const customerInfo = ref({
  name: '',
  phone: '',
  email: ''
})

const movieInfo = ref({
  title: '',
  poster: '',
  duration: ''
})

const showInfo = ref({
  cinema: '',
  room: '',
  showtime: '',
  date: ''
})

const selectedSeats = ref([])
const selectedCombos = ref([])
const ticketPrice = ref(75000) // Giá vé cơ bản

// Invoice info
const invoiceNumber = ref('')
const currentDate = ref('')
const currentTime = ref('')

// Computed properties
const ticketTotal = computed(() => selectedSeats.value.length * ticketPrice.value)

const comboTotal = computed(() => 
  selectedCombos.value.reduce((total, combo) => total + (combo.price * combo.quantity), 0)
)

const subtotal = computed(() => ticketTotal.value + comboTotal.value)

const taxAmount = computed(() => Math.round(subtotal.value * 0.1))

const totalAmount = computed(() => subtotal.value + taxAmount.value)

// Methods
function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

function generateInvoiceNumber() {
  const date = new Date()
  const timestamp = date.getTime().toString().slice(-6)
  return `INV${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}${timestamp}`
}

function goBack() {
  router.go(-1)
}

function processPayment() {
  // TODO: Tích hợp VNPay ở đây
  console.log('Processing payment with VNPay:', {
    amount: totalAmount.value,
    invoiceNumber: invoiceNumber.value,
    customerInfo: customerInfo.value,
    movieInfo: movieInfo.value,
    selectedSeats: selectedSeats.value,
    selectedCombos: selectedCombos.value
  })
  
  // Tạm thời chuyển đến trang thành công
  router.push({
    name: 'PaymentSuccess',
    query: {
      invoice: invoiceNumber.value,
      amount: totalAmount.value
    }
  })
}

function loadBookingData() {
  // Lấy dữ liệu từ localStorage hoặc route params
  const bookingData = JSON.parse(localStorage.getItem('bookingData') || '{}')
  
  if (bookingData.customerInfo) {
    customerInfo.value = bookingData.customerInfo
  }
  
  if (bookingData.movieInfo) {
    movieInfo.value = bookingData.movieInfo
  }
  
  if (bookingData.showInfo) {
    showInfo.value = bookingData.showInfo
  }
  
  if (bookingData.selectedSeats) {
    selectedSeats.value = bookingData.selectedSeats
  }
  
  if (bookingData.selectedCombos) {
    selectedCombos.value = bookingData.selectedCombos
  }
}

onMounted(() => {
  // Generate invoice info
  invoiceNumber.value = generateInvoiceNumber()
  const now = new Date()
  currentDate.value = now.toLocaleDateString('vi-VN')
  currentTime.value = now.toLocaleTimeString('vi-VN')
  
  // Load booking data
  loadBookingData()
})
</script>

<style scoped>
.invoice-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  background: #fff;
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.invoice-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e1e8ed;
}

.cinema-logo h1 {
  color: #1da1f2;
  margin: 0;
  font-size: 24px;
}

.cinema-logo p {
  color: #657786;
  margin: 5px 0 0 0;
  font-size: 14px;
}

.invoice-info {
  text-align: right;
}

.invoice-info h2 {
  color: #14171a;
  margin: 0;
  font-size: 20px;
}

.invoice-info p {
  color: #657786;
  margin: 5px 0;
  font-size: 14px;
}

.customer-info,
.movie-info,
.seats-info,
.combo-info,
.payment-summary {
  background: #f7f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.customer-info h3,
.movie-info h3,
.seats-info h3,
.combo-info h3,
.payment-summary h3 {
  color: #14171a;
  margin: 0 0 15px 0;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item label {
  font-weight: 600;
  color: #657786;
  font-size: 14px;
}

.info-item span {
  color: #14171a;
  font-size: 16px;
}

.movie-details {
  display: flex;
  gap: 20px;
  align-items: start;
}

.movie-poster {
  flex-shrink: 0;
}

.movie-poster img {
  width: 120px;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
}

.movie-text h4 {
  color: #14171a;
  margin: 0 0 15px 0;
  font-size: 20px;
}

.movie-meta p {
  margin: 8px 0;
  color: #657786;
  font-size: 14px;
}

.movie-meta strong {
  color: #14171a;
}

.seats-list {
  margin-top: 10px;
}

.seat-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.seat-tag {
  background: #1da1f2;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.no-seats {
  color: #657786;
  font-style: italic;
}

.combo-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.combo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e1e8ed;
}

.combo-details h4 {
  color: #14171a;
  margin: 0 0 5px 0;
  font-size: 16px;
}

.combo-details p {
  color: #657786;
  margin: 0;
  font-size: 14px;
}

.combo-quantity,
.combo-price {
  font-weight: 600;
  color: #14171a;
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.summary-item.subtotal {
  padding-top: 12px;
  border-top: 1px solid #e1e8ed;
  font-weight: 600;
}

.summary-item.tax {
  color: #657786;
  font-size: 14px;
}

.summary-item.total {
  padding-top: 12px;
  border-top: 2px solid #1da1f2;
  font-size: 20px;
  font-weight: 700;
  color: #1da1f2;
}

.payment-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin: 30px 0;
}

.btn-back,
.btn-payment {
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back {
  background: #e1e8ed;
  color: #657786;
}

.btn-back:hover {
  background: #d1d9e0;
}

.btn-payment {
  background: linear-gradient(135deg, #1da1f2, #1991db);
  color: white;
  box-shadow: 0 4px 15px rgba(29, 161, 242, 0.3);
}

.btn-payment:hover {
  background: linear-gradient(135deg, #1991db, #1976d2);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(29, 161, 242, 0.4);
}

.invoice-footer {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.invoice-footer p {
  color: #657786;
  margin: 8px 0;
  font-size: 14px;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .invoice-container {
    padding: 20px 15px;
  }
  
  .invoice-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .invoice-info {
    text-align: center;
  }
  
  .movie-details {
    flex-direction: column;
    text-align: center;
  }
  
  .movie-poster {
    align-self: center;
  }
  
  .combo-item {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .payment-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .btn-back,
  .btn-payment {
    width: 100%;
  }
}
</style>