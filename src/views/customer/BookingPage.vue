<template>
  <Header :is-logged-in="isLoggedIn" :user-info="userInfo" />
  
  <!-- Auth Modal -->
  <AuthModal 
    :show="showAuthModal" 
    @close="showAuthModal = false"
    @login-success="handleLoginSuccess"
  />
  
  <div class="booking-bg" :style="backgroundStyle">
    <div class="booking-page dark-mode">
      <h1 class="booking-title">🎟️ Đặt vé online</h1>
      <!-- Stepper -->
      <div class="stepper">
        <div v-if="!hasCompleteBookingInfo" :class="['step', step >= 1 ? 'active' : '']"><span class="step-icon">①</span> Chọn suất</div>
        <div :class="['step', step >= 2 ? 'active' : '']"><span class="step-icon">{{ hasCompleteBookingInfo ? '①' : '②' }}</span> Chọn ghế</div>
        <div :class="['step', step === 3 ? 'active' : '']"><span class="step-icon">{{ hasCompleteBookingInfo ? '②' : '③' }}</span> Thông tin</div>
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
          
          <!-- Loading state -->
          <div v-if="loadingSeats" class="loading-seats">
            <div class="loading-spinner"></div>
            <p>Đang tải sơ đồ ghế...</p>
          </div>
          
          <!-- Seat grid -->
          <div v-else class="seats">
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
          <button class="btn-back" type="button" @click="goBack">&larr; Quay lại</button>
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
import { ref, computed, watch, onUnmounted, onMounted } from 'vue'
import { fetchMovies } from '@/services/movieService'
import { fetchSchedules } from '@/services/scheduleService'
import { fetchSeatMap, fetchSeatAvailability, holdSeats, confirmSeats, releaseSeats } from '@/services/seatService'
import { useRoute } from 'vue-router'
import Header from '../../components/layout/Header.vue'
import HomeFooter from '../../components/layout/HomeFooter.vue'
import AuthModal from '../../components/modals/AuthModal.vue'
const route = useRoute()
const form = ref({ movie: '', branch: '', date: '', time: '' })
const scheduleId = ref('')

// Trạng thái đăng nhập
const isLoggedIn = ref(false)
const userInfo = ref({})
const showAuthModal = ref(false)

// Kiểm tra trạng thái đăng nhập
function checkLoginStatus() {
  try {
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('userInfo')
    
    if (token && user) {
      isLoggedIn.value = true
      userInfo.value = JSON.parse(user)
      console.log('✅ User logged in:', userInfo.value)
    } else {
      isLoggedIn.value = false
      userInfo.value = {}
      console.log('❌ User not logged in')
    }
  } catch (error) {
    console.error('❌ Error checking login status:', error)
    isLoggedIn.value = false
    userInfo.value = {}
  }
}

// Gọi khi component mount
onMounted(() => {
  checkLoginStatus()
})

// Lắng nghe thay đổi localStorage để cập nhật trạng thái đăng nhập
window.addEventListener('storage', (e) => {
  if (e.key === 'token' || e.key === 'userInfo') {
    checkLoginStatus()
  }
})

// Lắng nghe custom event từ AuthModal
window.addEventListener('login-success', () => {
  checkLoginStatus()
})

window.addEventListener('logout-success', () => {
  checkLoginStatus()
})

// Xử lý login success
function handleLoginSuccess() {
  checkLoginStatus()
  showAuthModal.value = false
}
// Lấy danh sách phim từ backend
const allMovies = ref([])
const movies = ref([])
async function loadMovies() {
  try {
    console.log('🔄 Loading movies...')
    const res = await fetchMovies()
    console.log('🎬 Movies response:', res)
    allMovies.value = res.data.data || res.data || []
    console.log('✅ Movies loaded:', allMovies.value)
    filterMoviesByBranch()
  } catch (e) {
    allMovies.value = []
    movies.value = []
    console.error('❌ Error loading movies:', e)
  }
}
loadMovies()

function filterMoviesByBranch() {
  console.log('🔍 filterMoviesByBranch called with branch:', form.value.branch)
  console.log('📊 schedules.value:', schedules.value)
  console.log('📊 allMovies.value:', allMovies.value)
  
  if (!form.value.branch) {
    movies.value = allMovies.value
    return
  }
  
  // Kiểm tra schedules.value có phải là array không
  if (!schedules.value || !Array.isArray(schedules.value)) {
    console.warn('⚠️ schedules.value is not an array:', schedules.value)
    movies.value = allMovies.value
    return
  }
  
  // Lọc các suất chiếu theo branchId
  const branchSchedules = schedules.value.filter(sch => sch.branchId == form.value.branch || sch.rapChieuId == form.value.branch)
  console.log('🎬 branchSchedules:', branchSchedules)
  
  // Lấy id phim từ các suất chiếu này
  const movieIds = [...new Set(branchSchedules.map(sch => sch.movieId || sch.phimId))]
  console.log('🎭 movieIds:', movieIds)
  
  // Lọc phim từ allMovies
  movies.value = allMovies.value.filter(m => movieIds.includes(m.id) || movieIds.includes(m.idPhim))
  console.log('✅ Filtered movies:', movies.value)
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
    console.log('🔄 Loading schedules...')
    const res = await fetchSchedules()
    console.log('📅 Schedules response:', res)
    schedules.value = res.data.data || res.data || []
    console.log('✅ Schedules loaded:', schedules.value)
  } catch (e) {
    schedules.value = []
    console.error('❌ Error loading schedules:', e)
  }
}
loadSchedules()

// Load ghế từ database
async function loadSeats() {
  if (!scheduleId.value) {
    console.log('❌ No scheduleId, cannot load seats')
    return
  }
  
  try {
    loadingSeats.value = true
    
    // Làm sạch scheduleId (loại bỏ ký tự không mong muốn)
    const cleanScheduleId = String(scheduleId.value).replace(/[^0-9]/g, '')
    console.log('🔄 Loading seats for showTime:', cleanScheduleId)
    console.log('🔍 Original scheduleId:', scheduleId.value)
    
    // Gọi API để lấy sơ đồ ghế
    const seatMapResponse = await fetchSeatMap(cleanScheduleId)
    console.log('🗺️ Seat map response:', seatMapResponse)
    
    // Gọi API để lấy trạng thái ghế
    const availabilityResponse = await fetchSeatAvailability(cleanScheduleId)
    console.log('📊 Seat availability response:', availabilityResponse)
    
    // Kết hợp dữ liệu sơ đồ và trạng thái
    const seatMap = seatMapResponse.data.data || seatMapResponse.data || []
    const availability = availabilityResponse.data.data || availabilityResponse.data || []
    
    // Merge dữ liệu
    seats.value = seatMap.map(seat => {
      const avail = availability.find(a => a.seatId === seat.id || a.hangGhe === seat.hangGhe && a.soGhe === seat.soGhe)
      return {
        ...seat,
        trangThai: avail ? avail.trangThai : 'CO_SAN',
        giaVe: avail ? avail.giaVe : seat.giaVe || seatPrice
      }
    })
    
    console.log('✅ Seats loaded:', seats.value)
    
    // Tạo danh sách hàng và cột từ dữ liệu ghế
    const rows = [...new Set(seats.value.map(seat => seat.hangGhe))].sort()
    const cols = [...new Set(seats.value.map(seat => seat.soGhe))].sort()
    
    seatRows.value = rows
    seatCols.value = cols
    
    console.log('📊 Seat rows:', seatRows.value)
    console.log('📊 Seat cols:', seatCols.value)
    
  } catch (error) {
    console.error('❌ Error loading seats:', error)
    
    // Fallback: sử dụng dữ liệu mẫu để test
    console.log('🔄 Using fallback seat data for testing')
    seats.value = generateFallbackSeats()
    
    // Tạo danh sách hàng và cột từ dữ liệu fallback
    const rows = [...new Set(seats.value.map(seat => seat.hangGhe))].sort()
    const cols = [...new Set(seats.value.map(seat => seat.soGhe))].sort()
    
    seatRows.value = rows
    seatCols.value = cols
    
    console.log('📊 Fallback seat rows:', seatRows.value)
    console.log('📊 Fallback seat cols:', seatCols.value)
  } finally {
    loadingSeats.value = false
  }
}

// Tạo dữ liệu ghế mẫu để test
function generateFallbackSeats() {
  const rows = ['A', 'B', 'C', 'D', 'E']
  const cols = [1, 2, 3, 4, 5, 6, 7, 8]
  const seats = []
  
  rows.forEach(row => {
    cols.forEach(col => {
      // Xác định loại ghế dựa trên vị trí
      let loaiGhe = 'THUONG'
      let giaVe = SEAT_PRICES.THUONG
      
      // 2 hàng cuối (D, E) = VIP
      if (row === 'D' || row === 'E') {
        loaiGhe = 'VIP'
        giaVe = SEAT_PRICES.VIP
      }
      
      // 2 cột cuối (7, 8) = Ghế đôi
      if (col === 7 || col === 8) {
        loaiGhe = 'COUPLE'
        giaVe = SEAT_PRICES.COUPLE
      }
      
      seats.push({
        id: `${row}${col}`,
        hangGhe: row,
        soGhe: col,
        loaiGhe: loaiGhe,
        trangThai: 'CO_SAN', // Tất cả ghế đều có sẵn
        giaVe: giaVe
      })
    })
  })
  
  console.log('🎭 Generated fallback seats:', seats.length)
  console.log('💰 Seat prices:', SEAT_PRICES)
  return seats
}

const branches = [
  { id: 1, name: 'Cinema Center Hà Nội' },
  { id: 2, name: 'Cinema Center Sài Gòn' },
]
const allTimes = ['09:00', '13:30', '16:00', '19:00', '21:30']
const availableTimes = computed(() => form.value.movie && form.value.branch && form.value.date ? allTimes : [])

// Disable select phim nếu có movieId trên query
const isMovieLocked = computed(() => !!route.query.movieId)

// Kiểm tra xem có thể bỏ qua bước chọn suất chiếu không
const canSkipShowtimeStep = computed(() => {
  const cinemaId = route.query.cinemaId || route.query.cinemald
  const canSkip = route.query.scheduleId && route.query.movieId && cinemaId && route.query.date && route.query.time
  console.log('🔍 canSkipShowtimeStep check:', {
    scheduleId: route.query.scheduleId,
    movieId: route.query.movieId,
    cinemaId: cinemaId,
    date: route.query.date,
    time: route.query.time,
    canSkip
  })
  return canSkip
})

// Kiểm tra có đủ thông tin booking không
const hasCompleteBookingInfo = computed(() => {
  return canSkipShowtimeStep.value
})

// Stepper - bắt đầu từ step 2 nếu có đủ thông tin
const step = ref(hasCompleteBookingInfo.value ? 2 : 1)
async function goToStep(n) {
  if (n === 2 && !canSelectSeats.value) return
  if (n === 3 && !selectedSeats.value.length) return
  
  // Nếu chuyển từ bước 2 sang bước 3, xác nhận ghế
  if (step.value === 2 && n === 3) {
    try {
      await confirmSelectedSeats()
    } catch (error) {
      console.error('❌ Error confirming seats:', error)
      return // Không chuyển bước nếu xác nhận thất bại
    }
  }
  
  step.value = n
  if (n === 2) {
    startCountdown()
    // Load ghế khi chuyển sang bước chọn ghế
    loadSeats()
  } else {
    stopCountdown()
  }
}

// Xử lý nút quay lại
function goBack() {
  if (hasCompleteBookingInfo.value) {
    // Nếu đã skip step 1, quay về trang showtimes
    window.history.back()
  } else {
    // Nếu chưa skip, quay về step 1
    goToStep(1)
  }
}

// Xác nhận ghế đã chọn
async function confirmSelectedSeats() {
  if (!selectedSeats.value.length) return
  
  const cleanScheduleId = String(scheduleId.value).replace(/[^0-9]/g, '')
  const confirmData = {
    showTimeId: cleanScheduleId,
    seats: selectedSeats.value.map(seatId => {
      const seat = seats.value.find(s => s.hangGhe + s.soGhe === seatId)
      return {
        seatId: seat.id,
        hangGhe: seat.hangGhe,
        soGhe: seat.soGhe,
        giaVe: seat.giaVe
      }
    })
  }
  
  console.log('✅ Confirming seats:', confirmData)
  const response = await confirmSeats(confirmData)
  console.log('🎫 Seats confirmed:', response)
  return response
}

// Sơ đồ ghế - lấy từ database
const seats = ref([]) // Danh sách ghế từ database
const seatRows = ref([]) // Hàng ghế (A, B, C, D, E...)
const seatCols = ref([]) // Cột ghế (1, 2, 3, 4, 5, 6, 7, 8...)
const selectedSeats = ref([]) // Ghế đang được chọn
// Giá vé mặc định theo loại ghế
const SEAT_PRICES = {
  THUONG: 50000,    // Ghế thường
  VIP: 80000,       // Ghế VIP  
  COUPLE: 120000    // Ghế đôi
}
const seatPrice = SEAT_PRICES.THUONG // Giá mặc định
const loadingSeats = ref(false)
const canSelectSeats = computed(() => form.value.movie && form.value.branch && form.value.date && form.value.time)
function seatClass(row, col) {
  const seatId = row + col
  const seat = seats.value.find(s => s.hangGhe === row && s.soGhe === col)
  
  if (!seat) {
    console.log(`❌ Seat ${seatId} not found`)
    return 'seat seat-disabled'
  }
  
  if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') {
    console.log(`🔴 Seat ${seatId} is booked`)
    return 'seat seat-booked'
  }
  
  if (selectedSeats.value.includes(seatId)) {
    console.log(`🔵 Seat ${seatId} is selected`)
    return 'seat seat-selected'
  }
  
  console.log(`⚪ Seat ${seatId} is available`)
  return 'seat seat-available'
}
function isSeatBooked(row, col) {
  const seat = seats.value.find(s => s.hangGhe === row && s.soGhe === col)
  return seat && (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG')
}

async function toggleSeat(row, col) {
  const seatId = row + col
  if (isSeatBooked(row, col)) return
  
  const seat = seats.value.find(s => s.hangGhe === row && s.soGhe === col)
  if (!seat) return
  
  const idx = selectedSeats.value.indexOf(seatId)
  
  try {
    if (idx === -1) {
      // Chọn ghế - gọi API giữ ghế
      const cleanScheduleId = String(scheduleId.value).replace(/[^0-9]/g, '')
      const holdData = {
        showTimeId: cleanScheduleId,
        seatId: seat.id,
        hangGhe: row,
        soGhe: col
      }
      
      console.log('🔒 Holding seat:', holdData)
      await holdSeats(holdData)
      
      selectedSeats.value.push(seatId)
      console.log(`✅ Seat ${seatId} selected and held`)
    } else {
      // Bỏ chọn ghế - gọi API hủy giữ
      const cleanScheduleId = String(scheduleId.value).replace(/[^0-9]/g, '')
      const releaseData = {
        showTimeId: cleanScheduleId,
        seatId: seat.id,
        hangGhe: row,
        soGhe: col
      }
      
      console.log('🔓 Releasing seat:', releaseData)
      await releaseSeats(releaseData)
      
      selectedSeats.value.splice(idx, 1)
      console.log(`❌ Seat ${seatId} deselected and released`)
    }
  } catch (error) {
    console.error('❌ Error toggling seat:', error)
    // Có thể hiển thị thông báo lỗi cho user
  }
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
onUnmounted(() => {
  stopCountdown()
  // Hủy giữ tất cả ghế đã chọn khi thoát trang
  releaseAllSeats()
})

// Hủy giữ tất cả ghế đã chọn
async function releaseAllSeats() {
  if (!selectedSeats.value.length) return
  
  try {
    const cleanScheduleId = String(scheduleId.value).replace(/[^0-9]/g, '')
    const releaseData = {
      showTimeId: cleanScheduleId,
      seats: selectedSeats.value.map(seatId => {
        const seat = seats.value.find(s => s.hangGhe + s.soGhe === seatId)
        return {
          seatId: seat.id,
          hangGhe: seat.hangGhe,
          soGhe: seat.soGhe
        }
      })
    }
    
    console.log('🔓 Releasing all seats:', releaseData)
    await releaseSeats(releaseData)
    console.log('✅ All seats released')
  } catch (error) {
    console.error('❌ Error releasing seats:', error)
  }
}

// Thông tin vé & khách hàng
// selectedMovie: luôn lấy từ allMovies theo movieId
const selectedMovie = computed(() => {
  const movieId = form.value.movie
  return allMovies.value.find(m => String(m.id) === String(movieId) || String(m.idPhim) === String(movieId))
})
const selectedBranch = computed(() => branches.find(b => b.id === form.value.branch))
const totalPrice = computed(() => {
  return selectedSeats.value.reduce((total, seatId) => {
    const seat = seats.value.find(s => s.hangGhe + s.soGhe === seatId)
    if (seat) {
      return total + (seat.giaVe || SEAT_PRICES[seat.loaiGhe] || SEAT_PRICES.THUONG)
    }
    return total + SEAT_PRICES.THUONG
  }, 0)
})
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

// Tự động điền form từ URL parameters
function autoFillFormFromQuery() {
  console.log('🔄 autoFillFormFromQuery called')
  const query = route.query
  console.log('📋 Query params:', query)
  console.log('🔗 Current URL:', window.location.href)
  
  // Điền movieId
  if (query.movieId && allMovies.value.length) {
    const found = allMovies.value.find(m => String(m.id) === String(query.movieId) || String(m.idPhim) === String(query.movieId))
    if (found) {
      form.value.movie = String(found.id ?? found.idPhim)
      console.log('🎬 Movie set:', form.value.movie)
    }
  }
  
  // Điền cinemaId (xử lý cả cinemaId và cinemald)
  const cinemaId = query.cinemaId || query.cinemald
  if (cinemaId) {
    form.value.branch = String(cinemaId)
    console.log('🏢 Branch set:', form.value.branch)
  }
  
  // Điền date
  if (query.date) {
    form.value.date = String(query.date)
    console.log('📅 Date set:', form.value.date)
  }
  
  // Điền time
  if (query.time) {
    form.value.time = String(query.time)
    console.log('⏰ Time set:', form.value.time)
  }
  
  // Điền scheduleId
  if (query.scheduleId) {
    scheduleId.value = String(query.scheduleId)
    console.log('🎫 ScheduleId set:', scheduleId.value)
  }
  
  // Nếu có đủ thông tin, load ghế ngay lập tức
  console.log('🔍 Checking canSkipShowtimeStep:', canSkipShowtimeStep.value)
  if (canSkipShowtimeStep.value) {
    console.log('🚀 Auto-loading seats for pre-selected showtime')
    startCountdown()
    // Load ghế khi có đủ thông tin
    loadSeats()
  } else {
    console.log('❌ Cannot skip showtime step, staying at step 1')
  }
}

// Khi có movieId trên query, luôn set form.movie nếu tìm thấy trong allMovies
watch([
  () => allMovies.value,
  () => route.query
], ([moviesArr, query]) => {
  console.log('BookingPage - query params:', query)
  console.log('BookingPage - allMovies length:', moviesArr.length)
  
  if (moviesArr.length) {
    autoFillFormFromQuery()
  }
}, { immediate: true })

// Theo dõi thay đổi route để tự động điền form
watch(() => route.query, () => {
  if (allMovies.value.length) {
    autoFillFormFromQuery()
  }
}, { deep: true })
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
  background: rgba(24, 25, 26, 0.95);
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #fff;
  border-radius: 32px;
  box-shadow: 0 8px 48px 0 #48dbfb33, 0 1.5px 8px #23252655;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(72, 219, 251, 0.1);
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
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  align-items: end;
  margin-bottom: 8px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  border: 1px solid rgba(72, 219, 251, 0.1);
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
  padding: 14px 18px;
  border-radius: 12px;
  border: 2px solid #232526;
  font-size: 15px;
  background: #18191a;
  color: #fff;
  min-width: 160px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.08);
  position: relative;
}

.booking-form select:hover,
.booking-form input[type="date"]:hover {
  border-color: rgba(72, 219, 251, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.15);
}

.booking-form select:focus,
.booking-form input[type="date"]:focus {
  border-color: #48dbfb;
  outline: none;
  background: #232526;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(72, 219, 251, 0.25);
}

.booking-form option {
  background: #232526;
  color: #fff;
}

.btn-next,
.btn-back {
  padding: 14px 36px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  border: none;
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  margin-top: 18px;
  margin-right: 12px;
  box-shadow: 0 4px 16px rgba(72, 219, 251, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.btn-next:hover,
.btn-back:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(72, 219, 251, 0.4);
}

.btn-next:active,
.btn-back:active {
  transform: translateY(0);
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
  width: 42px;
  height: 42px;
  border-radius: 10px;
  border: none;
  font-size: 15px;
  font-weight: 700;
  margin: 0 3px;
  background: #232526;
  color: #fff;
  box-shadow: 0 3px 12px rgba(72, 219, 251, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.seat-available:hover {
  background: #48dbfb;
  color: #232526;
  transform: scale(1.1) translateY(-2px);
  box-shadow: 0 6px 20px rgba(72, 219, 251, 0.4);
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

/* Loading state cho ghế */
.loading-seats {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #b2bec3;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #232526;
  border-top: 4px solid #48dbfb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-seats p {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
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
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .info-card {
    padding: 16px;
  }
  
  .info-card h3 {
    font-size: 18px;
  }
}
</style>