<template>
  <Header />
  <div class="booking-bg" :style="backgroundStyle">
    <div class="booking-page dark-mode">
      <h1 class="booking-title">🎟️ Đặt vé online</h1>
      <!-- Stepper -->
      <div class="stepper">
        <div :class="['step', step >= 1 ? 'active' : '']"><span class="step-icon">①</span> Chọn suất</div>
        <div :class="['step', step >= 2 ? 'active' : '']"><span class="step-icon">②</span> Chọn ghế</div>
        <div :class="['step', step === 3 ? 'active' : '']"><span class="step-icon">③</span> Thông tin</div>
      </div>
      <!-- Step 1: Chọn phim, rạp, ngày, suất chiếu -->
      <section class="booking-step dark-box" v-show="step === 1">
        <div class="step-header"><span class="step-number">1</span> Chọn phim, rạp, ngày, suất chiếu</div>
        <form class="booking-form" @submit.prevent>
          <div class="form-group">
            <label>Phim</label>
            <template v-if="isMovieLocked">
              <div class="locked-movie-name">{{ selectedMovie?.tenPhim || selectedMovie?.title || '...' }}</div>
            </template>
            <template v-else>
              <select v-model="form.movie" required>
                <option value="" disabled>Chọn phim</option>
                <option v-for="movie in movies" :key="movie.id || movie.idPhim" :value="movie.id || movie.idPhim">{{
                  movie.title || movie.tenPhim }}</option>
              </select>
            </template>
          </div>
          <div class="form-group">
            <label>Rạp</label>
            <select v-model="form.branch" required>
              <option value="" disabled>Chọn rạp</option>
              <option v-for="branch in branches" :key="branch.id" :value="branch.id">{{ branch.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Ngày</label>
            <input type="date" v-model="form.date" required />
          </div>
          <div class="form-group">
            <label>Suất chiếu</label>
            <select v-model="form.time" required>
              <option value="" disabled>Chọn suất chiếu</option>
              <option v-for="time in availableTimes" :key="time">{{ time }}</option>
            </select>
          </div>
          <button class="btn-next" type="button" :disabled="!canSelectSeats" @click="goToStep(2)">Tiếp tục
            &rarr;</button>
        </form>
      </section>

      <!-- Step 2: Sơ đồ ghế -->
      <section class="booking-step dark-box" v-show="step === 2">
        <div class="step-header">
          <span class="step-number">2</span> Chọn ghế
          <div class="countdown" v-if="step === 2">
            <span class="timer-icon">⏰</span>
            <span :class="{ 'timer-warning': countdown <= 60 }">{{ countdownDisplay }}</span>
          </div>
        </div>
        <div class="seat-map">
          <div class="screen">Màn hình</div>
          <div class="seats">
            <div v-for="row in seatRows" :key="row" class="seat-row">
              <span class="row-label">{{ row }}</span>
              <button v-for="col in seatCols" :key="col" :class="seatClass(row, col)" @click="toggleSeat(row, col)"
                :disabled="isSeatBooked(row, col)">
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
        <div class="step-actions">
          <button class="btn-back" type="button" @click="goToStep(1)">&larr; Quay lại</button>
          <button class="btn-next" type="button" :disabled="!selectedSeats.length" @click="goToStep(3)">Tiếp tục
            &rarr;</button>
        </div>
      </section>

      <!-- Step 3: Thông tin vé & khách hàng -->
      <section class="booking-step dark-box" v-show="step === 3">
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
          <div class="step-actions">
            <button class="btn-back" type="button" @click="goToStep(2)">&larr; Quay lại</button>
            <button class="btn-primary" type="submit">Xác nhận đặt vé</button>
          </div>
        </form>
      </section>

      <!-- Toast -->
      <transition name="toast-fade">
        <div v-if="toast.show" :class="['toast', toast.type]">
          {{ toast.message }}
        </div>
      </transition>
    </div>
  </div>
  <HomeFooter />
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { fetchMovies } from '@/services/movieService'
import { fetchSchedules } from '@/services/scheduleService'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import HomeFooter from '@/components/HomeFooter.vue'
const route = useRoute()
const form = ref({ movie: '', branch: '', date: '', time: '' })
// Lấy danh sách phim từ backend
const allMovies = ref([])
const movies = ref([])
async function loadMovies() {
  try {
    const res = await fetchMovies()
    allMovies.value = res.data.data || res.data || []
    filterMoviesByBranch()
  } catch (e) {
    allMovies.value = []
    movies.value = []
  }
}
loadMovies()

function filterMoviesByBranch() {
  if (!form.value.branch) {
    movies.value = allMovies.value
    return
  }
  // Lọc các suất chiếu theo branchId
  const branchSchedules = schedules.value.filter(sch => sch.branchId == form.value.branch || sch.rapChieuId == form.value.branch)
  // Lấy id phim từ các suất chiếu này
  const movieIds = [...new Set(branchSchedules.map(sch => sch.movieId || sch.phimId))]
  // Lọc phim từ allMovies
  movies.value = allMovies.value.filter(m => movieIds.includes(m.id) || movieIds.includes(m.idPhim))
}

watch(() => form.value.branch, () => {
  filterMoviesByBranch()
  // Nếu phim đã chọn không còn trong danh sách, reset
  if (form.value.movie && !movies.value.find(m => m.id === form.value.movie || m.idPhim === form.value.movie)) {
    form.value.movie = ''
  }
})
const schedules = ref([])
async function loadSchedules() {
  try {
    const res = await fetchSchedules()
    schedules.value = res.data.data || res.data || []
  } catch (e) {
    schedules.value = []
  }
}
loadSchedules()
const branches = [
  { id: 1, name: 'Cinema Center Hà Nội' },
  { id: 2, name: 'Cinema Center Sài Gòn' },
]
const allTimes = ['09:00', '13:30', '16:00', '19:00', '21:30']
const availableTimes = computed(() => form.value.movie && form.value.branch && form.value.date ? allTimes : [])

// Disable select phim nếu có movieId trên query
const isMovieLocked = computed(() => !!route.query.movieId)

// Stepper
const step = ref(1)
function goToStep(n) {
  if (n === 2 && !canSelectSeats.value) return
  if (n === 3 && !selectedSeats.value.length) return
  step.value = n
  if (n === 2) startCountdown()
  else stopCountdown()
}

// Sơ đồ ghế (A1-A8, B1-B8, ...)
const seatRows = ['A', 'B', 'C', 'D', 'E']
const seatCols = [1, 2, 3, 4, 5, 6, 7, 8]
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

// Countdown chọn ghế
const COUNTDOWN_SECONDS = 60
const countdown = ref(COUNTDOWN_SECONDS)
let countdownInterval = null
const countdownDisplay = computed(() => {
  const min = Math.floor(countdown.value / 60).toString().padStart(2, '0')
  const sec = (countdown.value % 60).toString().padStart(2, '0')
  return `${min}:${sec}`
})
function startCountdown() {
  stopCountdown()
  countdown.value = COUNTDOWN_SECONDS
  countdownInterval = setInterval(() => {
    if (countdown.value > 0) countdown.value--
    if (countdown.value === 0) {
      stopCountdown()
      showToast('⏰ Hết thời gian giữ ghế, vui lòng chọn lại!', 'error')
      selectedSeats.value = []
      goToStep(1)
    }
  }, 1000)
}
function stopCountdown() {
  if (countdownInterval) clearInterval(countdownInterval)
  countdownInterval = null
}
onUnmounted(stopCountdown)

// Thông tin vé & khách hàng
// selectedMovie: luôn lấy từ allMovies theo movieId
const selectedMovie = computed(() => {
  const movieId = form.value.movie
  return allMovies.value.find(m => String(m.id) === String(movieId) || String(m.idPhim) === String(movieId))
})
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
  goToStep(1)
}

const backgroundStyle = computed(() => {
  if (isMovieLocked.value && selectedMovie.value && (selectedMovie.value.posterUrl || selectedMovie.value.bannerUrl)) {
    const url = selectedMovie.value.bannerUrl || selectedMovie.value.posterUrl
    const fullUrl = url?.startsWith('http') ? url : `http://localhost:${url}`
    return {
      backgroundImage: `linear-gradient(rgba(24,25,26,0.92), rgba(35,37,38,0.96)), url('${fullUrl}')`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      minHeight: '100vh',
      width: '100%',
      position: 'relative',
      zIndex: 0
    }
  }
  return {
    background: 'linear-gradient(135deg, #18191a 0%, #232526 100%)',
    minHeight: '100vh',
    width: '100%',
    position: 'relative',
    zIndex: 0
  }
})

// Khi có movieId trên query, luôn set form.movie nếu tìm thấy trong allMovies
watch([
  () => allMovies.value,
  () => route.query.movieId
], ([moviesArr, movieId]) => {
  if (movieId && moviesArr.length) {
    const found = moviesArr.find(m => String(m.id) === String(movieId) || String(m.idPhim) === String(movieId))
    if (found) form.value.movie = String(found.id ?? found.idPhim)
  }
}, { immediate: true })
</script>

<style scoped>
.booking-bg {
  min-height: 100vh;
  width: 100vw;
  position: relative;
  overflow-x: hidden;
}

.booking-page.dark-mode {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 16px 80px 16px;
  background: rgba(24, 25, 26, 0.92);
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #fff;
  border-radius: 32px;
  box-shadow: 0 8px 48px 0 #48dbfb33, 0 1.5px 8px #23252655;
  position: relative;
  z-index: 1;
}

.booking-title {
  font-size: 40px;
  font-weight: 900;
  color: #48dbfb;
  text-align: center;
  margin-bottom: 36px;
  letter-spacing: 2px;
  text-shadow: 0 2px 16px #232526;
}

.stepper {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}

.step {
  font-size: 20px;
  font-weight: 700;
  color: #b2bec3;
  padding: 10px 28px;
  border-radius: 20px;
  background: #232526cc;
  box-shadow: 0 2px 8px #48dbfb22;
  transition: background 0.2s, color 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.step.active {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  box-shadow: 0 4px 16px #48dbfb55;
}

.step-icon {
  font-size: 22px;
  margin-right: 6px;
}

.dark-box {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 18px;
  box-shadow: 0 4px 32px rgba(72, 219, 251, 0.10), 0 1.5px 8px rgba(44, 62, 80, 0.18);
  padding: 36px 36px 28px 36px;
  margin-bottom: 36px;
  position: relative;
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
  justify-content: space-between;
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
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
}

.countdown {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #48dbfb;
  background: #18191a;
  border-radius: 10px;
  padding: 6px 18px;
  box-shadow: 0 2px 8px #48dbfb22;
}

.timer-icon {
  font-size: 22px;
}

.timer-warning {
  color: #ff7675;
}

.booking-form {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  align-items: flex-end;
  margin-bottom: 8px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 180px;
}

.form-group label {
  font-size: 15px;
  color: #b2bec3;
  font-weight: 600;
  margin-bottom: 2px;
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
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.08);
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

.btn-next,
.btn-back {
  padding: 12px 32px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  border: none;
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  margin-top: 18px;
  margin-right: 12px;
  box-shadow: 0 2px 8px #48dbfb22;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, transform 0.2s;
}

.btn-back {
  background: #232526;
  color: #48dbfb;
  border: 2px solid #48dbfb;
  margin-right: 0;
}

.btn-next:disabled {
  background: #b2bec3;
  color: #fff;
  cursor: not-allowed;
}

.step-actions {
  display: flex;
  gap: 18px;
  margin-top: 18px;
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
  color: #48dbfb;
  margin-right: 8px;
}

.seat {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  border: none;
  font-size: 15px;
  font-weight: 700;
  margin: 0 2px;
  background: #232526;
  color: #fff;
  box-shadow: 0 2px 8px #48dbfb22;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, transform 0.2s;
}

.seat-available:hover {
  background: #48dbfb;
  color: #232526;
  transform: scale(1.08);
}

.seat-booked {
  background: #b2bec3;
  color: #fff;
  cursor: not-allowed;
}

.seat-selected {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  box-shadow: 0 2px 12px #48dbfb55;
}

.seat-legend {
  display: flex;
  gap: 18px;
  justify-content: center;
  align-items: center;
  margin-top: 18px;
  font-size: 15px;
  color: #b2bec3;
}

.seat-legend .seat {
  width: 28px;
  height: 28px;
  margin: 0 4px 0 0;
  vertical-align: middle;
}

.ticket-info {
  margin-bottom: 18px;
  font-size: 17px;
  color: #fff;
  background: #232526;
  border-radius: 12px;
  padding: 18px 24px;
  box-shadow: 0 2px 8px #48dbfb22;
}

.customer-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 8px;
}

.customer-form input {
  padding: 12px 16px;
  border-radius: 10px;
  border: 2px solid #232526;
  font-size: 15px;
  background: #18191a;
  color: #fff;
  transition: border 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.08);
}

.customer-form input:focus {
  border-color: #48dbfb;
  outline: none;
  background: #232526;
}

.btn-primary {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  padding: 14px 0;
  font-size: 17px;
  margin-top: 8px;
  box-shadow: 0 2px 8px #48dbfb22;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, transform 0.2s;
}

.btn-primary:hover {
  background: linear-gradient(90deg, #667eea 0%, #48dbfb 100%);
  color: #fff;
  transform: translateY(-2px);
}

.toast {
  position: fixed;
  top: 24px;
  right: 32px;
  background: #232526;
  color: #fff;
  padding: 16px 32px;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 700;
  box-shadow: 0 2px 16px #48dbfb33;
  z-index: 9999;
  opacity: 0.98;
}

.toast.success {
  background: #27ae60;
  color: #fff;
}

.toast.error {
  background: #e74c3c;
  color: #fff;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: opacity 0.4s;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
}

.locked-movie-name {
  font-size: 18px;
  font-weight: 800;
  color: #48dbfb;
  background: #232526;
  border-radius: 10px;
  padding: 12px 18px;
  margin-top: 2px;
  margin-bottom: 2px;
  box-shadow: 0 2px 8px #48dbfb22;
  letter-spacing: 1px;
}

@media (max-width: 900px) {
  .booking-page.dark-mode {
    max-width: 100vw;
    padding: 16px 2px 60px 2px;
  }

  .dark-box {
    padding: 18px 6px 12px 6px;
  }

  .stepper {
    gap: 10px;
  }

  .booking-form {
    gap: 8px;
  }

  .form-group {
    min-width: 120px;
  }

  .seat {
    width: 28px;
    height: 28px;
    font-size: 13px;
  }

  .screen {
    font-size: 15px;
  }

  .step-header {
    font-size: 17px;
  }
}
</style>