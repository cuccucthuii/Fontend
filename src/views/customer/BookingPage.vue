<template>
  <div class="booking-page dark-mode">
    <h1 class="booking-title">🎟️ Đặt vé online</h1>
    <!-- Step 1: Chọn phim, rạp, ngày, suất chiếu -->
    <section class="booking-step dark-box">
      <div class="step-header"><span class="step-number">1</span> Chọn phim, rạp, ngày, suất chiếu</div>
      <form class="booking-form" @submit.prevent>
        <select v-model="form.movie" required>
          <option value="" disabled>Chọn phim</option>
          <option v-for="movie in movies" :key="movie.id" :value="movie.id">{{ movie.title }}</option>
        </select>
        <select v-model="form.branch" required>
          <option value="" disabled>Chọn rạp</option>
          <option v-for="branch in branches" :key="branch.id" :value="branch.id">{{ branch.name }}</option>
        </select>
        <input type="date" v-model="form.date" required />
        <select v-model="form.time" required>
          <option value="" disabled>Chọn suất chiếu</option>
          <option v-for="time in availableTimes" :key="time">{{ time }}</option>
        </select>
      </form>
    </section>

    <!-- Step 2: Sơ đồ ghế -->
    <section class="booking-step dark-box" v-if="canSelectSeats">
      <div class="step-header"><span class="step-number">2</span> Chọn ghế</div>
      <div class="seat-map">
        <div class="screen">Màn hình</div>
        <div class="seats">
          <div v-for="row in seatRows" :key="row" class="seat-row">
            <span class="row-label">{{ row }}</span>
            <button v-for="col in seatCols" :key="col" :class="seatClass(row, col)" @click="toggleSeat(row, col)" :disabled="isSeatBooked(row, col)">
              {{ row + col }}
            </button>
          </div>
        </div>
        <div class="seat-legend">
          <span class="seat seat-booked"></span> Đã đặt
          <span class="seat seat-selected"></span> Ghế bạn chọn
          <span class="seat seat-available"></span> Ghế thường
        </div>
      </div>
    </section>

    <!-- Step 3: Thông tin vé & khách hàng -->
    <section class="booking-step dark-box" v-if="selectedSeats.length">
      <div class="step-header"><span class="step-number">3</span> Thông tin vé & khách hàng</div>
      <div class="ticket-info">
        <div>Phim: <b>{{ selectedMovie?.title || '-' }}</b></div>
        <div>Rạp: <b>{{ selectedBranch?.name || '-' }}</b></div>
        <div>Suất: <b>{{ form.date }} {{ form.time }}</b></div>
        <div>Ghế: <b>{{ selectedSeats.join(', ') }}</b></div>
        <div>Tổng tiền: <b>{{ formatCurrency(totalPrice) }}</b></div>
      </div>
      <form class="customer-form" @submit.prevent="submitBooking">
        <input v-model="customer.name" required placeholder="Họ tên khách hàng" />
        <input v-model="customer.phone" required placeholder="Số điện thoại" />
        <input v-model="customer.email" required placeholder="Email" />
        <button class="btn-primary" type="submit">Xác nhận đặt vé</button>
      </form>
    </section>

    <!-- Toast -->
    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
// Mock data phim, rạp, suất chiếu
const movies = [
  { id: 1, title: 'Avengers: Endgame' },
  { id: 2, title: 'Dune: Part Two' },
  { id: 3, title: 'Godzilla x Kong' },
]
const branches = [
  { id: 1, name: 'Cinema Center Hà Nội' },
  { id: 2, name: 'Cinema Center Sài Gòn' },
]
const allTimes = ['09:00', '13:30', '16:00', '19:00', '21:30']
const form = ref({ movie: '', branch: '', date: '', time: '' })
const availableTimes = computed(() => form.value.movie && form.value.branch && form.value.date ? allTimes : [])

// Sơ đồ ghế (A1-A8, B1-B8, ...)
const seatRows = ['A', 'B', 'C', 'D', 'E']
const seatCols = [1,2,3,4,5,6,7,8]
const bookedSeats = ref(['A2', 'A3', 'B5', 'C7', 'D1'])
const selectedSeats = ref([])
const seatPrice = 75000
const canSelectSeats = computed(() => form.value.movie && form.value.branch && form.value.date && form.value.time)
function seatClass(row, col) {
  const seat = row + col
  if (bookedSeats.value.includes(seat)) return 'seat seat-booked'
  if (selectedSeats.value.includes(seat)) return 'seat seat-selected'
  return 'seat seat-available'
}
function isSeatBooked(row, col) {
  return bookedSeats.value.includes(row + col)
}
function toggleSeat(row, col) {
  const seat = row + col
  if (isSeatBooked(row, col)) return
  const idx = selectedSeats.value.indexOf(seat)
  if (idx === -1) selectedSeats.value.push(seat)
  else selectedSeats.value.splice(idx, 1)
}

// Thông tin vé & khách hàng
const selectedMovie = computed(() => movies.find(m => m.id === form.value.movie))
const selectedBranch = computed(() => branches.find(b => b.id === form.value.branch))
const totalPrice = computed(() => selectedSeats.value.length * seatPrice)
const customer = ref({ name: '', phone: '', email: '' })

// Toast
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null
function showToast(message, type = 'success') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => (toast.value.show = false), 2200)
}

function formatCurrency(amount) {
  return amount.toLocaleString('vi-VN') + ' ₫'
}

function submitBooking() {
  if (!customer.value.name || !customer.value.phone || !customer.value.email) {
    showToast('⚠️ Vui lòng nhập đủ thông tin!', 'error')
    return
  }
  showToast('🎉 Đặt vé thành công!', 'success')
  // Reset form
  form.value = { movie: '', branch: '', date: '', time: '' }
  selectedSeats.value = []
  customer.value = { name: '', phone: '', email: '' }
}
</script>

<style scoped>
.booking-page.dark-mode {
  max-width: 950px;
  margin: 0 auto;
  padding: 32px 12px 60px 12px;
  background: linear-gradient(135deg, #18191a 0%, #232526 100%);
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #fff;
}
.booking-title {
  font-size: 34px;
  font-weight: 900;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 32px;
  letter-spacing: 1px;
  text-shadow: 0 2px 16px #232526;
}
.dark-box {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 18px;
  box-shadow: 0 4px 32px rgba(72,219,251,0.10), 0 1.5px 8px rgba(44,62,80,0.18);
  padding: 32px 28px 22px 28px;
  margin-bottom: 32px;
}
.step-header {
  font-size: 22px;
  font-weight: 800;
  color: #feca57;
  margin-bottom: 22px;
  display: flex;
  align-items: center;
  gap: 12px;
  letter-spacing: 1px;
}
.step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  font-size: 1.2em;
  font-weight: 900;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  margin-right: 8px;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
}
.booking-form {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  align-items: center;
}
.booking-form select,
.booking-form input[type="date"] {
  padding: 12px 16px;
  border-radius: 10px;
  border: 2px solid #232526;
  font-size: 15px;
  background: #18191a;
  color: #fff;
  min-width: 160px;
  transition: border 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(72,219,251,0.08);
}
.booking-form select:focus,
.booking-form input[type="date"]:focus {
  border-color: #48dbfb;
  outline: none;
  background: #232526;
}
.booking-form option {
  background: #232526;
  color: #fff;
}
/* Sơ đồ ghế */
.seat-map {
  margin-top: 12px;
  text-align: center;
}
.screen {
  background: linear-gradient(90deg, #feca57 0%, #ff7675 100%);
  color: #232526;
  border-radius: 12px;
  padding: 12px 0;
  margin-bottom: 22px;
  font-weight: 900;
  font-size: 18px;
  letter-spacing: 1px;
  box-shadow: 0 2px 16px #feca57aa;
}
.seats {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  margin-bottom: 18px;
}
.seat-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.row-label {
  font-weight: 700;
  color: #feca57;
  width: 28px;
  text-align: right;
  font-size: 1.1em;
}
.seat {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  border: none;
  font-size: 16px;
  font-weight: 800;
  margin: 0 3px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(44,62,80,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  outline: none;
  position: relative;
}
.seat-available {
  background: linear-gradient(135deg, #232526 0%, #636e72 100%);
  color: #fff;
}
.seat-selected {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
  box-shadow: 0 4px 16px #48dbfb55, 0 2px 8px #0abde355;
  border: 2.5px solid #feca57;
  transform: scale(1.08);
}
.seat-booked {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
  cursor: not-allowed;
  opacity: 0.7;
  border: 2.5px solid #232526;
  text-decoration: line-through;
}
.seat:hover:not(.seat-booked):not(.seat-selected) {
  box-shadow: 0 6px 24px #48dbfb55;
  transform: scale(1.08);
  background: linear-gradient(135deg, #636e72 0%, #48dbfb 100%);
}
.seat-legend {
  display: flex;
  gap: 22px;
  justify-content: center;
  align-items: center;
  margin-top: 14px;
  font-size: 16px;
  color: #b2bec3;
  background: #18191a;
  border-radius: 10px;
  padding: 10px 18px;
  box-shadow: 0 2px 8px rgba(72,219,251,0.08);
}
.seat-legend .seat {
  width: 28px;
  height: 28px;
  margin: 0 6px 0 0;
  font-size: 15px;
  border-radius: 8px;
  box-shadow: none;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
/* Thông tin vé & khách hàng */
.ticket-info {
  margin-bottom: 18px;
  font-size: 17px;
  color: #fff;
  display: flex;
  flex-wrap: wrap;
  gap: 22px;
  background: #232526;
  border-radius: 10px;
  padding: 18px 18px 10px 18px;
  box-shadow: 0 2px 12px rgba(72,219,251,0.10);
}
.customer-form {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-top: 8px;
}
.customer-form input {
  padding: 12px 16px;
  border-radius: 10px;
  border: 2px solid #232526;
  font-size: 15px;
  background: #18191a;
  color: #fff;
  min-width: 180px;
  transition: border 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(72,219,251,0.08);
}
.customer-form input:focus {
  border-color: #48dbfb;
  outline: none;
  background: #232526;
}
.btn-primary {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(72, 219, 251, 0.2);
  transition: all 0.3s;
}
.btn-primary:hover {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  transform: translateY(-2px) scale(1.04);
}
/* Toast */
.toast {
  position: fixed;
  top: 32px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 220px;
  background: #232526;
  color: #fff;
  padding: 16px 32px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(44,62,80,0.18);
  font-weight: 700;
  font-size: 15px;
  z-index: 2000;
  animation: popIn 0.3s;
  border-left: 6px solid #48dbfb;
}
.toast.success { border-left: 6px solid #27ae60; }
.toast.error { border-left: 6px solid #ff5252; }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity 0.3s; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; }
@keyframes popIn {
  from { transform: translateX(-50%) scale(0.85); opacity: 0; }
  to { transform: translateX(-50%) scale(1); opacity: 1; }
}
@media (max-width: 700px) {
  .booking-form, .customer-form, .ticket-info { flex-direction: column; gap: 10px; }
  .booking-step { padding: 16px 4px; }
}
</style> 