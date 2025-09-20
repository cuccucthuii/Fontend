<template>
  <Header />
  <div class="booking-bg" :style="backgroundStyle">
    <div class="booking-page dark-mode">
      <h1 class="booking-title">🎟️ Đặt vé online</h1>
      <!-- Stepper -->
      <div class="stepper">
        <!-- Chỉ hiển thị bước "Chọn suất" nếu chưa có thông tin đầy đủ -->
        <div v-if="!hasCompleteBookingInfo" :class="['step', step >= 1 ? 'active' : '']">
          <span class="step-icon">①</span> Chọn suất
        </div>
        <div :class="['step', step >= 2 ? 'active' : '']">
          <span class="step-icon">{{ hasCompleteBookingInfo ? '①' : '②' }}</span> Chọn ghế
        </div>
        <div :class="['step', step === 3 ? 'active' : '']">
          <span class="step-icon">{{ hasCompleteBookingInfo ? '②' : '③' }}</span> Thông tin
        </div>
      </div>
      <!-- Step 1: Chọn phim, rạp, ngày, suất chiếu -->
      <section class="booking-step dark-box" v-show="step === 1 && !hasCompleteBookingInfo">
        <div class="step-header">
          <span class="step-number">1</span> Chọn phim, rạp, ngày, suất chiếu
        </div>
        <form class="booking-form" @submit.prevent>
          <div class="form-group">
            <label>Phim</label>
            <template v-if="isMovieLocked">
              <div class="locked-movie-name">
                {{ selectedMovie?.tenPhim || selectedMovie?.title || "..." }}
              </div>
            </template>
            <template v-else>
              <select v-model="form.movie" required>
                <option value="" disabled>Chọn phim</option>
                <option
                  v-for="movie in movies"
                  :key="movie.id || movie.idPhim"
                  :value="movie.id || movie.idPhim"
                >
                  {{ movie.title || movie.tenPhim }}
                </option>
              </select>
            </template>
          </div>
          <div class="form-group">
            <label>Rạp</label>
            <select v-model="form.branch" required>
              <option value="" disabled>Chọn rạp</option>
              <option
                v-for="branch in branches"
                :key="branch.id"
                :value="branch.id"
              >
                {{ branch.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Ngày</label>
            <input type="date" v-model="form.date" required />
          </div>
          <div class="form-group">
            <label>Suất chiếu</label>
            <div class="showtime-selection">
              <select v-model="form.time" required>
                <option value="" disabled>Chọn suất chiếu</option>
                <option v-for="time in availableTimes" :key="time">
                  {{ time }}
                </option>
              </select>
              <button
                type="button"
                class="btn-showtime-modal"
                @click="openShowtimeModal"
                :disabled="!form.movie"
                title="Xem lịch chiếu chi tiết"
              >
                📅 Lịch chiếu
              </button>
            </div>
          </div>
          <button
            class="btn-next"
            type="button"
            :disabled="!canSelectSeats"
            @click="goToStep(2)"
          >
            Tiếp tục &rarr;
          </button>
        </form>
      </section>

      <!-- Step 2: Sơ đồ ghế -->
      <section class="booking-step dark-box" v-show="step === 2">
        <div class="step-header">
          <span class="step-number">{{ hasCompleteBookingInfo ? '1' : '2' }}</span> Chọn ghế
          <div class="countdown" v-if="step === 2">
            <span class="timer-icon">⏰</span>
            <span :class="{ 'timer-warning': countdown <= 60 }">{{
              countdownDisplay
            }}</span>
          </div>
        </div>
        
        <!-- Hiển thị thông tin đã chọn từ modal -->
        <div v-if="hasCompleteBookingInfo" class="booking-summary">
          <h3>📋 Thông tin đã chọn:</h3>
          <div class="booking-info">
            <div class="booking-item">
              <strong>🎬 Phim:</strong> {{ getSelectedMovieName() }}
            </div>
            <div class="booking-item">
              <strong>🏢 Rạp:</strong> {{ route.query.cinema || 'Đang tải...' }}
            </div>
            <div class="booking-item">
              <strong>📅 Ngày:</strong> {{ route.query.date }}
            </div>
            <div class="booking-item">
              <strong>⏰ Suất chiếu:</strong> {{ route.query.time }}
            </div>
          </div>
        </div>
        
        <!-- Time Expired Overlay -->
        <div v-if="isTimeExpired" class="time-expired-overlay">
          <div class="time-expired-content">
            <div class="expired-icon">⏰</div>
            <h3>Hết thời gian chọn ghế!</h3>
            <p>Thời gian giữ ghế đã kết thúc. Vui lòng load lại trang để chọn ghế mới.</p>
            <button class="btn-reload" @click="reloadPage">
              🔄 Load lại trang
            </button>
          </div>
        </div>
        
        <div class="seat-map">
          
          <!-- Loading state -->
          <div v-if="loadingSeats" class="seats-loading">
            <p>Đang tải sơ đồ ghế...</p>
          </div>
          
          <!-- Screen indicator -->
          <div class="cinema-screen">
            <div class="curved-screen">
              <div class="screen-surface"></div>
            </div>
            <div class="screen-text">MÀN HÌNH</div>
          </div>
          
          <!-- Seats grid -->
          <div v-if="loadingSeats" class="seats-loading">
            <div class="loading-spinner-large"></div>
            <p>Đang tải sơ đồ ghế...</p>
          </div>
          
          <div v-else class="seats-container">
            <div class="seats-grid">
              <div v-for="row in seatRows" :key="row" class="seat-row">
                <div class="row-label">{{ row }}</div>
                <div class="seats-in-row">
                  <button
                    v-for="col in seatCols"
                    :key="`${row}${col}`"
                    :class="getSeatClass(row, col)"
                    @click="toggleSeat(row, col)"
                    :disabled="isSeatBooked(row, col)"
                    :title="getSeatTitle(row, col)"
                  >
                    <span class="seat-number">{{ row }}{{ col }}</span>
                  </button>
                </div>
                <div class="row-label">{{ row }}</div>
              </div>
            </div>
            
            <!-- Legend -->
            <div class="seat-legend">
              <div class="legend-item">
                <div class="seat seat-available-demo"></div>
                <span>Ghế trống</span>
              </div>
              <div class="legend-item">
                <div class="seat seat-selected-demo"></div>
                <span>Ghế đã chọn</span>
              </div>
              <div class="legend-item">
                <div class="seat seat-booked-demo"></div>
                <span>Ghế đã được đặt</span>
              </div>
            </div>
          </div>
        </div>
        <div class="step-actions">
          <button class="btn-back" type="button" @click="goToStep(1)">
            &larr; Quay lại
          </button>
          <button
            class="btn-next"
            type="button"
            :disabled="!selectedSeats.length"
            @click="goToStep(3)"
          >
            Tiếp tục &rarr;
          </button>
        </div>
      </section>

      <!-- Step 3: Thông tin vé & khách hàng -->
      <section class="booking-step dark-box" v-show="step === 3">
        <div class="step-header">
          <span class="step-number">3</span> Thông tin vé & khách hàng
        </div>
        <div class="ticket-info">
          <div>
            Phim: <b>{{ selectedMovie?.title || selectedMovie?.tenPhim || "Chưa chọn phim" }}</b>
          </div>
          <div>
            Rạp: <b>{{ selectedBranch?.name || selectedBranch?.tenRap || "Chưa chọn rạp" }}</b>
          </div>
          <div>
            Suất: <b>{{ form.date && form.time ? `${form.date} ${form.time}` : "Chưa chọn suất chiếu" }}</b>
          </div>
          <div>
            Ghế: <b>{{ selectedSeats.length > 0 ? selectedSeats.join(", ") : "Chưa chọn ghế" }}</b>
          </div>
          <div>
            Tổng tiền: <b>{{ formatCurrency(totalPrice) }}</b>
          </div>
        </div>

        <!-- Customer Form -->
        <form class="customer-form" @submit.prevent="submitBooking">
          <h3>Thông tin khách hàng</h3>
          <input
            v-model="customer.name"
            required
            placeholder="Họ tên khách hàng"
          />
          <input
            v-model="customer.phone"
            required
            placeholder="Số điện thoại"
          />
          <input v-model="customer.email" required placeholder="Email" />
        </form>

        <!-- Combo Selection -->
        <div class="combo-selection">
          <h3>Chọn combo bỏng nước</h3>
          <div v-if="loadingCombos" class="loading">Đang tải combo...</div>
          <div v-else-if="combos.length === 0" class="no-combos">Không có combo nào</div>
          <div v-else class="combo-grid">
            <div v-for="combo in combos" :key="combo.id" class="combo-card">
              <div class="combo-image">
                <img :src="combo.image || '/logo.png?v=2'" :alt="combo.name" />
              </div>
              <div class="combo-info">
                <h4>{{ combo.name }}</h4>
                <p class="combo-description">{{ combo.description }}</p>
                <div class="combo-price">{{ formatCurrency(combo.price) }}</div>
                <div class="combo-actions">
                  <button 
                    type="button" 
                    class="combo-btn remove" 
                    @click="removeCombo(combo.id)"
                    :disabled="getComboQuantity(combo.id) === 0"
                  >
                    -
                  </button>
                  <span class="combo-quantity">{{ getComboQuantity(combo.id) }}</span>
                  <button 
                    type="button" 
                    class="combo-btn add" 
                    @click="addCombo(combo)"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Selected Combos Summary -->
          <div v-if="selectedCombos.length > 0" class="selected-combos">
            <h4>Combo đã chọn:</h4>
            <div v-for="combo in selectedCombos" :key="combo.id" class="selected-combo-item">
              <span>{{ combo.name }} x{{ combo.quantity }}</span>
              <span>{{ formatCurrency(combo.price * combo.quantity) }}</span>
            </div>
            <div class="combo-total">
              <strong>Tổng combo: {{ formatCurrency(comboTotal) }}</strong>
            </div>
          </div>
        </div>

        <!-- Submit Actions -->
        <div class="step-actions">
          <button class="btn-back" type="button" @click="goToStep(2)">
            &larr; Quay lại
          </button>
          <button class="btn-primary" type="button" @click="submitBooking">
            Xác nhận đặt vé
          </button>
        </div>
      </section>

      <!-- Toast -->
      <transition name="toast-fade">
        <div v-if="toast.show" :class="['toast', toast.type]">
          {{ toast.message }}
        </div>
      </transition>
    </div>

    <!-- Showtime Modal -->
    <ShowtimeModal
      :visible="showShowtimeModal"
      :movie="selectedMovie"
      :cinema="selectedBranch"
      @close="showShowtimeModal = false"
      @selectShowtime="handleShowtimeSelection"
    />
  </div>
  <HomeFooter />
</template>

<script setup>
import { ref, computed, watch, onUnmounted, onMounted } from "vue";
import { fetchMovies } from "@/services/movieService";
import { fetchSchedules } from "@/services/scheduleService";
import { getAllCombos } from "@/services/comboService";
import { getSeatsBySchedule, mapSeatsData } from "@/services/seatService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import HomeFooter from "@/components/HomeFooter.vue";
import ShowtimeModal from "@/components/ShowtimeModal.vue";

const route = useRoute();
const router = useRouter();
const form = ref({ movie: "", branch: "", date: "", time: "" });

// Showtime Modal
const showShowtimeModal = ref(false);
// Lấy danh sách phim từ backend
const allMovies = ref([]);
const movies = ref([]);

// Combo variables
const combos = ref([]);
const selectedCombos = ref([]);
const loadingCombos = ref(false);

async function loadMovies() {
  try {
    const res = await fetchMovies();
    allMovies.value = res.data.data || res.data || [];
    filterMoviesByBranch();
  } catch (e) {
    allMovies.value = [];
    movies.value = [];
  }
}

// Load seats function
async function loadSeats() {
  // Debug: Log tất cả route query để kiểm tra
  console.log('🔍 Current route.query:', route.query);
  
  // Get schedule ID from route query
  const scheduleId = route.query.scheduleId;
  
  console.log('📅 Schedule ID found:', scheduleId);
  
  if (!scheduleId) {
    console.warn('⚠️ No scheduleId provided in query params:', Object.keys(route.query));
    // Fallback to hardcoded data
    seatRows.value = ["A", "B", "C", "D", "E"];
    seatCols.value = [1, 2, 3, 4, 5, 6, 7, 8];
    bookedSeats.value = ["A2", "A3", "B5", "C7", "D1"];
    return;
  }
  
  loadingSeats.value = true;
  try {
    console.log('🪑 Loading seats for schedule:', scheduleId);
    const response = await getSeatsBySchedule(scheduleId);
    console.log('📡 Raw API response:', response);
    
    if (response.success && response.data) {
      console.log('✅ Seats loaded successfully from API');
      const mappedData = response.data; // Đã được mapped trong service
      console.log('🗺️ Mapped data:', mappedData);
      
      seatRows.value = mappedData.seatRows;
      seatCols.value = mappedData.seatCols;
      bookedSeats.value = mappedData.bookedSeats;
      roomInfo.value = mappedData.roomInfo;
      
      console.log('🎭 Final seat state:', {
        rows: seatRows.value,
        cols: seatCols.value,
        booked: bookedSeats.value,
        total: mappedData.totalSeats
      });
    } else {
      console.error('❌ Failed to load seats:', response.message);
      showToast(`⚠️ ${response.message}`, 'error');
      // Fallback to hardcoded data
      seatRows.value = ["A", "B", "C", "D", "E", "F"];
      seatCols.value = [1, 2, 3, 4, 5, 6, 7, 8];
      bookedSeats.value = [];
    }
    
  } catch (error) {
    console.error('💥 Exception in loadSeats:', error);
    showToast('❌ Lỗi khi tải danh sách ghế', 'error');
    
    // Fallback data
    seatRows.value = ["A", "B", "C", "D", "E", "F"];
    seatCols.value = [1, 2, 3, 4, 5, 6, 7, 8];
    bookedSeats.value = [];
  } finally {
    loadingSeats.value = false;
  }
}

// Load combos function
async function loadCombos() {
  loadingCombos.value = true;
  try {
    const response = await getAllCombos();
    const rawCombos = response.data || response || [];
    
    // Map API response to expected format
    combos.value = rawCombos.map(combo => ({
      id: combo.idCombo,
      name: combo.tenCombo,
      description: combo.moTa,
      price: combo.giaCombo,
      status: combo.trangThai,
      image: combo.image || '/logo.png?v=2', // Default image if not provided
      createdAt: combo.ngayTao,
      updatedAt: combo.ngayCapNhat
    }));
    
    console.log("🍿 Loaded combos:", combos.value);
  } catch (error) {
    console.error("❌ Error loading combos:", error);
    // Fallback với mock data nếu API không có
    combos.value = [
      {
        id: 1,
        name: "Combo A: Bắp + Nước",
        description: "Bắp rang bơ + Nước ngọt 330ml",
        price: 50000,
        status: "HOAT_DONG",
        image: "/logo.png?v=2"
      },
      {
        id: 2,
        name: "Combo B: Bắp + Nước + Snack", 
        description: "Bắp rang bơ + Nước ngọt + Snack khoai tây",
        price: 80000,
        status: "HOAT_DONG",
        image: "/logo.png?v=2"
      },
      {
        id: 3,
        name: "Combo VIP: Bắp + Nước + Snack + Kem",
        description: "Bắp rang bơ + Nước ngọt + Snack + Kem ốc quế", 
        price: 120000,
        status: "HOAT_DONG",
        image: "/logo.png?v=2"
      }
    ];
  } finally {
    loadingCombos.value = false;
  }
}

function filterMoviesByBranch() {
  if (!form.value.branch) {
    movies.value = allMovies.value;
    return;
  }
  // Lọc các suất chiếu theo branchId
  const branchSchedules = schedules.value.filter(
    (sch) =>
      sch.branchId == form.value.branch || sch.rapChieuId == form.value.branch
  );
  // Lấy id phim từ các suất chiếu này
  const movieIds = [
    ...new Set(branchSchedules.map((sch) => sch.movieId || sch.phimId)),
  ];
  // Lọc phim từ allMovies
  movies.value = allMovies.value.filter(
    (m) => movieIds.includes(m.id) || movieIds.includes(m.idPhim)
  );
}

watch(
  () => form.value.branch,
  () => {
    filterMoviesByBranch();
    // Nếu phim đã chọn không còn trong danh sách, reset
    if (
      form.value.movie &&
      !movies.value.find(
        (m) => m.id === form.value.movie || m.idPhim === form.value.movie
      )
    ) {
      form.value.movie = "";
    }
  }
);
const schedules = ref([]);
async function loadSchedules() {
  try {
    const res = await fetchSchedules();
    schedules.value = res.data.data || res.data || [];
  } catch (e) {
    schedules.value = [];
  }
}
loadSchedules();
const branches = [
  { id: 1, name: "Cinema Center Hà Nội" },
  { id: 2, name: "Cinema Center Sài Gòn" },
];
const allTimes = ["09:00", "13:30", "16:00", "19:00", "21:30"];
const availableTimes = computed(() =>
  form.value.movie && form.value.branch && form.value.date ? allTimes : []
);

// Disable select phim nếu có movieId trên query
const isMovieLocked = computed(() => !!route.query.movieId);

// Stepper
const step = ref(1);

// Check xem có đầy đủ thông tin từ modal không
const hasCompleteBookingInfo = computed(() => {
  const { movieId, cinemaId, scheduleId, date, time } = route.query;
  return !!(movieId && cinemaId && scheduleId && date && time);
});

// Khởi tạo step dựa trên query params
// Watch route changes to reload seats
watch(() => route.query, (newQuery, oldQuery) => {
  console.log('📍 Route query changed:', { old: oldQuery, new: newQuery });
  
  const hasScheduleId = newQuery.scheduleId;
                    
  if (hasScheduleId && step.value === 2) {
    console.log('🔄 Reloading seats due to route change, scheduleId:', hasScheduleId);
    loadSeats();
  }
}, { deep: true });

onMounted(() => {
  console.log('🚀 BookingPage mounted, route.query:', route.query);
  loadMovies();
  loadCombos();
  loadSeats(); // Load seats from API
  
  if (hasCompleteBookingInfo.value) {
    console.log("🚀 Có đầy đủ thông tin từ modal, skip đến bước chọn ghế");
    
    // Pre-fill form với thông tin từ query
    const { movieId, cinemaId, date, time, cinema } = route.query;
    form.value.movie = movieId;
    form.value.branch = cinemaId;
    form.value.date = date;
    form.value.time = time;
    
    // Skip đến bước 2 (chọn ghế)
    step.value = 2;
    startCountdown();
  }
});

function goToStep(n) {
  if (n === 2 && !canSelectSeats.value) return;
  if (n === 3 && !selectedSeats.value.length) return;
  step.value = n;
  if (n === 2) startCountdown();
  else stopCountdown();
}

// Function để lấy tên phim từ movieId
function getSelectedMovieName() {
  const movieId = route.query.movieId;
  if (!movieId) return 'Đang tải...';
  
  const movie = allMovies.value.find(m => 
    (m.id && m.id.toString() === movieId.toString()) || 
    (m.idPhim && m.idPhim.toString() === movieId.toString())
  );
  
  return movie ? (movie.tenPhim || movie.title || 'Phim không xác định') : 'Đang tải...';
}

// Sơ đồ ghế (A1-A8, B1-B8, ...)
// Seat management - dynamic from API
const seatRows = ref([]);
const seatCols = ref([]);
const allSeatsData = ref([]);
const roomInfo = ref(null);
const loadingSeats = ref(false);
const bookedSeats = ref(["A2", "A3", "B5", "C7", "D1"]);
const selectedSeats = ref([]);
// Seat price - dynamic from API
const seatPrice = computed(() => {
  if (allSeatsData.value && allSeatsData.value.length > 0) {
    return allSeatsData.value[0].giaGhe || 50000;
  }
  return 50000; // Default price
});
const canSelectSeats = computed(
  () =>
    form.value.movie && form.value.branch && form.value.date && form.value.time
);

function isSeatBooked(row, col) {
  return bookedSeats.value.includes(row + col);
}

function getSeatClass(row, col) {
  const seatCode = row + col;
  return {
    'seat': true,
    'seat-available': !isSeatBooked(row, col) && !selectedSeats.value.includes(seatCode),
    'seat-selected': selectedSeats.value.includes(seatCode),
    'seat-booked': isSeatBooked(row, col)
  };
}

function getSeatTitle(row, col) {
  const seatCode = row + col;
  if (isSeatBooked(row, col)) {
    return `Ghế ${seatCode} đã được đặt`;
  } else if (selectedSeats.value.includes(seatCode)) {
    return `Ghế ${seatCode} đã chọn - Click để bỏ chọn`;
  } else {
    return `Ghế ${seatCode} - Click để chọn`;
  }
}

function toggleSeat(row, col) {
  const seat = row + col;
  if (isSeatBooked(row, col)) {
    return;
  }
  const idx = selectedSeats.value.indexOf(seat);
  if (idx === -1) {
    selectedSeats.value.push(seat);
  } else {
    selectedSeats.value.splice(idx, 1);
  }
}

// Countdown chọn ghế
const COUNTDOWN_SECONDS = 60;
const countdown = ref(COUNTDOWN_SECONDS);
const isTimeExpired = ref(false);
let countdownInterval = null;
const countdownDisplay = computed(() => {
  const min = Math.floor(countdown.value / 60)
    .toString()
    .padStart(2, "0");
  const sec = (countdown.value % 60).toString().padStart(2, "0");
  return `${min}:${sec}`;
});
function startCountdown() {
  stopCountdown();
  countdown.value = COUNTDOWN_SECONDS;
  isTimeExpired.value = false;
  countdownInterval = setInterval(() => {
    if (countdown.value > 0) countdown.value--;
    if (countdown.value === 0) {
      stopCountdown();
      showToast("⏰ Hết thời gian giữ ghế!", "error");
      selectedSeats.value = [];
      isTimeExpired.value = true;
    }
  }, 1000);
}
function stopCountdown() {
  if (countdownInterval) clearInterval(countdownInterval);
  countdownInterval = null;
}

function reloadPage() {
  window.location.reload();
}

onUnmounted(stopCountdown);

// Thông tin vé & khách hàng
// selectedMovie: lấy từ route.query hoặc form
const selectedMovie = computed(() => {
  const movieId = route.query.movieId || form.value.movie;
  const movieTitle = route.query.movieTitle;
  
  console.log('🔍 selectedMovie computed - movieId:', movieId, 'allMovies.length:', allMovies.value.length);
  
  if (!movieId) {
    console.log('❌ No movieId found');
    return null;
  }
  
  // Tìm movie đầy đủ từ allMovies
  const foundMovie = allMovies.value.find(
    (m) =>
      String(m.id) === String(movieId) || String(m.idPhim) === String(movieId)
  );
  
  if (foundMovie) {
    console.log('✅ Found movie in allMovies:', foundMovie.title || foundMovie.tenPhim);
    return foundMovie;
  }
  
  // Fallback: chỉ có thông tin basic từ query
  if (movieTitle && movieId) {
    console.log('⚠️ Using fallback movie data:', movieTitle);
    return { 
      id: movieId,
      idPhim: movieId,
      title: movieTitle,
      tenPhim: movieTitle,
      posterUrl: '/logo.png?v=2',
      bannerUrl: '/logo.png?v=2',
      duration: ''
    };
  }
  
  console.log('❌ No movie found at all');
  return null;
});

const selectedBranch = computed(() => {
  const cinemaName = route.query.cinema;
  const cinemaId = route.query.cinemaId || form.value.branch;
  
  if (cinemaName) {
    return { name: cinemaName, id: cinemaId };
  }
  
  return branches.find((b) => b.id === cinemaId);
});

// Tính tổng tiền bao gồm ghế và combo
const comboTotal = computed(() => {
  try {
    return selectedCombos.value.reduce((total, combo) => {
      return total + (combo.price * combo.quantity);
    }, 0);
  } catch (error) {
    console.error("Error calculating combo total:", error);
    return 0;
  }
});

const totalPrice = computed(() => {
  try {
    const seatTotal = selectedSeats.value.length * seatPrice.value;
    return seatTotal + comboTotal.value;
  } catch (error) {
    console.error("Error calculating total price:", error);
    return 0;
  }
});

const customer = ref({ name: "", phone: "", email: "" });

// Combo functions
function addCombo(combo) {
  const existingCombo = selectedCombos.value.find(c => c.id === combo.id);
  if (existingCombo) {
    existingCombo.quantity++;
  } else {
    selectedCombos.value.push({
      ...combo,
      quantity: 1
    });
  }
}

function removeCombo(comboId) {
  const comboIndex = selectedCombos.value.findIndex(c => c.id === comboId);
  if (comboIndex > -1) {
    if (selectedCombos.value[comboIndex].quantity > 1) {
      selectedCombos.value[comboIndex].quantity--;
    } else {
      selectedCombos.value.splice(comboIndex, 1);
    }
  }
}

function getComboQuantity(comboId) {
  const combo = selectedCombos.value.find(c => c.id === comboId);
  return combo ? combo.quantity : 0;
}

// Toast
const toast = ref({ show: false, message: "", type: "" });
let toastTimeout = null;
function showToast(message, type = "success") {
  toast.value = { show: true, message, type };
  if (toastTimeout) clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => (toast.value.show = false), 2200);
}

function formatCurrency(amount) {
  try {
    if (typeof amount !== 'number' || isNaN(amount)) {
      return "0 ₫";
    }
    return amount.toLocaleString("vi-VN") + " ₫";
  } catch (error) {
    console.error("Error formatting currency:", error, amount);
    return "0 ₫";
  }
}

function submitBooking() {
  if (!customer.value.name || !customer.value.phone || !customer.value.email) {
    showToast("⚠️ Vui lòng nhập đủ thông tin!", "error");
    return;
  }
  
  // Chuẩn bị dữ liệu để lưu vào localStorage
  const bookingData = {
    customerInfo: {
      name: customer.value.name,
      phone: customer.value.phone,
      email: customer.value.email
    },
    movieInfo: {
      title: selectedMovie.value?.title || selectedMovie.value?.tenPhim || 'Không xác định',
      poster: selectedMovie.value?.posterUrl || selectedMovie.value?.hinhAnh || selectedMovie.value?.bannerUrl || '/logo.png?v=2',
      duration: selectedMovie.value?.duration || selectedMovie.value?.thoiLuong || '',
      genre: selectedMovie.value?.genre || selectedMovie.value?.theLoai || '',
      director: selectedMovie.value?.director || selectedMovie.value?.daoDien || '',
      actors: selectedMovie.value?.actors || selectedMovie.value?.dienVien || '',
      description: selectedMovie.value?.description || selectedMovie.value?.moTa || ''
    },
    showInfo: {
      cinema: selectedBranch.value?.name || 'Rạp chiếu phim',
      room: roomInfo.value?.name || roomInfo.value?.roomName || 'Phòng chiếu',
      showtime: form.value.time || 'Chưa chọn',
      date: form.value.date || 'Chưa chọn'
    },
    selectedSeats: selectedSeats.value?.map(seatId => {
      // seatId là string như "A1", "B2", etc.
      return {
        id: seatId,
        name: seatId // Đã là tên ghế rồi (A1, B2, etc.)
      };
    }) || [],
    selectedCombos: selectedCombos.value || []
  };
  
  // Debug log để kiểm tra dữ liệu
  console.log('🎬 Selected Movie:', selectedMovie.value);
  console.log('📋 Booking data prepared:', bookingData);
  
  // Lưu dữ liệu vào localStorage
  try {
    localStorage.setItem('bookingData', JSON.stringify(bookingData));
    console.log('💾 Booking data saved to localStorage');
  } catch (error) {
    console.error('❌ Error saving to localStorage:', error);
    showToast("❌ Lỗi lưu dữ liệu!", "error");
    return;
  }
  
  // Hiển thị thông báo và chuyển trang
  showToast("📄 Đang tạo hóa đơn...", "success");
  
  // Chuyển đến trang hóa đơn
  setTimeout(() => {
    router.push({ name: 'InvoiceDetail' });
  }, 1000);
}

const backgroundStyle = computed(() => {
  if (
    isMovieLocked.value &&
    selectedMovie.value &&
    (selectedMovie.value.posterUrl || selectedMovie.value.bannerUrl)
  ) {
    const url = selectedMovie.value.bannerUrl || selectedMovie.value.posterUrl;
    const fullUrl = url?.startsWith("http") ? url : `http://localhost:${url}`;
    return {
      backgroundImage: `linear-gradient(rgba(24,25,26,0.92), rgba(35,37,38,0.96)), url('${fullUrl}')`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      backgroundRepeat: "no-repeat",
      minHeight: "100vh",
      width: "100%",
      position: "relative",
      zIndex: 0,
    };
  }
  return {
    background: "linear-gradient(135deg, #18191a 0%, #232526 100%)",
    minHeight: "100vh",
    width: "100%",
    position: "relative",
    zIndex: 0,
  };
});

// Khi có movieId trên query, luôn set form.movie nếu tìm thấy trong allMovies
watch(
  [() => allMovies.value, () => route.query.movieId],
  ([moviesArr, movieId]) => {
    if (movieId && moviesArr.length) {
      const found = moviesArr.find(
        (m) =>
          String(m.id) === String(movieId) ||
          String(m.idPhim) === String(movieId)
      );
      if (found) form.value.movie = String(found.id ?? found.idPhim);
    }
  },
  { immediate: true }
);

// Debug selectedMovie
watch(
  () => selectedMovie.value,
  (newMovie) => {
    console.log('🎬 selectedMovie changed:', newMovie);
  },
  { immediate: true }
);

// Showtime Modal Functions
function openShowtimeModal() {
  if (!form.value.movie) {
    showToast("⚠️ Vui lòng chọn phim trước!", "error");
    return;
  }
  showShowtimeModal.value = true;
}

function handleShowtimeSelection(showtimeData) {
  console.log("🎯 BookingPage: Handling showtime selection:", showtimeData);
  
  // Auto-fill form with selected data
  form.value.date = showtimeData.date;
  form.value.time = showtimeData.time;

  // Find and set branch if needed
  const branchName = showtimeData.cinema;
  const foundBranch = branches.value.find(
    (b) =>
      b.name.toLowerCase().includes(branchName.toLowerCase()) ||
      branchName.toLowerCase().includes(b.name.toLowerCase())
  );
  if (foundBranch) {
    form.value.branch = foundBranch.id;
  }

  // Cập nhật URL với scheduleId để trigger reload seats
  if (showtimeData.id) {
    router.replace({
      name: 'Booking',
      query: {
        ...route.query,
        scheduleId: showtimeData.id,
      }
    });
  }

  showToast(
    `✅ Đã chọn suất ${showtimeData.time} ngày ${showtimeData.date}`,
    "success"
  );
  
  showShowtimeModal.value = false;
}
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
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
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
  box-shadow: 0 4px 32px rgba(72, 219, 251, 0.1),
    0 1.5px 8px rgba(44, 62, 80, 0.18);
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

.showtime-selection {
  display: flex;
  gap: 8px;
  align-items: center;
}

.btn-showtime-modal {
  padding: 8px 12px;
  border-radius: 8px;
  border: 2px solid #bfa2db;
  background: linear-gradient(135deg, #bfa2db 0%, #b7e4c7 100%);
  color: #333;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: auto;
}

.btn-showtime-modal:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(191, 162, 219, 0.3);
  background: linear-gradient(135deg, #a78bfa 0%, #10b981 100%);
}

.btn-showtime-modal:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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
  justify-content: center;
  align-items: center;
}

/* Time Expired Overlay */
.time-expired-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(10px);
}

.time-expired-content {
  background: linear-gradient(145deg, #232526, #414345);
  padding: 40px 50px;
  border-radius: 20px;
  text-align: center;
  color: #fff;
  max-width: 500px;
  width: 90%;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.5),
    0 0 40px rgba(231, 76, 60, 0.3);
  border: 2px solid rgba(231, 76, 60, 0.3);
  animation: expiredPulse 2s ease-in-out infinite alternate;
}

.expired-icon {
  font-size: 60px;
  margin-bottom: 20px;
  animation: iconShake 0.8s ease-in-out infinite;
}

.time-expired-content h3 {
  color: #e74c3c;
  font-size: 24px;
  margin-bottom: 15px;
  font-weight: 700;
}

.time-expired-content p {
  color: #bbb;
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 30px;
}

.btn-reload {
  padding: 15px 40px;
  background: linear-gradient(145deg, #e74c3c, #c0392b);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(231, 76, 60, 0.4);
}

.btn-reload:hover {
  background: linear-gradient(145deg, #c0392b, #a93226);
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(231, 76, 60, 0.6);
}

@keyframes expiredPulse {
  0% { box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5), 0 0 40px rgba(231, 76, 60, 0.3); }
  100% { box-shadow: 0 25px 70px rgba(0, 0, 0, 0.6), 0 0 50px rgba(231, 76, 60, 0.5); }
}

@keyframes iconShake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
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

/* Cinema Screen */
.cinema-screen {
  margin-bottom: 40px;
  text-align: center;
  perspective: 1000px;
}

/* Cinema Screen */
.cinema-screen {
  margin-bottom: 40px;
  text-align: center;
  perspective: 1000px;
}

.curved-screen {
  position: relative;
  width: 80%;
  height: 60px;
  margin: 0 auto 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  perspective: 1200px;
  perspective-origin: center 200px;
  transform-style: preserve-3d;
}

.screen-surface {
  width: 100%;
  height: 60px;
  background: linear-gradient(135deg, 
    rgba(20, 25, 40, 0.9) 0%, 
    rgba(36, 123, 160, 1) 15%, 
    rgba(72, 219, 251, 1) 35%, 
    rgba(72, 219, 251, 0.9) 50%, 
    rgba(72, 219, 251, 1) 65%, 
    rgba(36, 123, 160, 1) 85%, 
    rgba(20, 25, 40, 0.9) 100%
  );
  border: 2px solid rgba(72, 219, 251, 0.5);
  border-radius: 50px 50px 30px 30px;
  box-shadow: 
    0 15px 40px rgba(0, 0, 0, 0.4),
    0 8px 25px rgba(72, 219, 251, 0.5),
    inset 0 4px 12px rgba(255, 255, 255, 0.3),
    inset 0 -4px 12px rgba(0, 0, 0, 0.3),
    0 0 30px rgba(72, 219, 251, 0.6);
  position: relative;
  transform: perspective(600px) rotateX(-15deg) scaleY(1.2);
  transition: all 0.3s ease;
  clip-path: ellipse(50% 80% at 50% 100%);
  overflow: hidden;
}

/* Hiệu ứng glow cho màn hình cong nhẹ nhàng */
.curved-screen::before {
  content: '';
  position: absolute;
  top: -15px;
  left: -10%;
  right: -10%;
  height: 90px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(116, 185, 255, 0.15) 20%, 
    rgba(116, 185, 255, 0.25) 50%, 
    rgba(116, 185, 255, 0.15) 80%, 
    transparent 100%);
  border-radius: 50%;
  filter: blur(15px);
  animation: screenPulse 5s ease-in-out infinite alternate;
  z-index: -1;
}

/* Phản chiếu trên bề mặt màn hình */
.screen-surface::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 40%;
  background: linear-gradient(180deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    rgba(255, 255, 255, 0.1) 50%, 
    transparent 100%);
  border-radius: 50px 50px 0 0;
  pointer-events: none;
}

/* Hiệu ứng lóe sáng chạy qua màn hình */
.screen-surface::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -100%;
  width: 50%;
  height: 200%;
  background: linear-gradient(45deg, 
    transparent, 
    rgba(255, 255, 255, 0.2), 
    rgba(72, 219, 251, 0.4), 
    rgba(255, 255, 255, 0.6), 
    rgba(72, 219, 251, 0.4), 
    rgba(255, 255, 255, 0.2), 
    transparent
  );
  transform: rotate(25deg);
  animation: screenFlash 3s ease-in-out infinite;
  pointer-events: none;
  z-index: 1;
}

/* Hover effect nhẹ nhàng - chỉ tăng cường hiệu ứng */
.curved-screen:hover .screen-surface {
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.5),
    0 12px 35px rgba(72, 219, 251, 0.7),
    inset 0 5px 15px rgba(255, 255, 255, 0.4),
    inset 0 -5px 15px rgba(0, 0, 0, 0.4),
    0 0 40px rgba(72, 219, 251, 0.8);
  transform: perspective(600px) rotateX(-18deg) scaleY(1.3);
}

.screen-text {
  color: #48dbfb;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
  text-shadow: 0 0 15px rgba(72, 219, 251, 0.6);
}

/* Seats Loading */
.seats-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  color: #b2bec3;
}

.loading-spinner-large {
  width: 50px;
  height: 50px;
  border: 4px solid #414345;
  border-left: 4px solid #48dbfb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

/* Seats Container */
.seats-container {
  text-align: center;
}

.seats-grid {
  display: inline-block;
  background: #1a1a1a;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid #333;
  margin-bottom: 30px;
}

.seat-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  gap: 15px;
}

.row-label {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #48dbfb;
  font-weight: 700;
  font-size: 16px;
  background: #232526;
  border-radius: 8px;
  border: 1px solid #48dbfb33;
}

.seats-in-row {
  display: flex;
  gap: 6px;
  align-items: center;
}

.seat {
  width: 48px;
  height: 42px;
  border-radius: 12px;
  border: 2px solid transparent;
  font-size: 11px;
  font-weight: 700;
  background: #2d3436;
  color: #ddd;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.seat::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 60%;
  background: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, transparent 100%);
  border-radius: 10px 10px 50% 50%;
}

.seat-number {
  position: relative;
  z-index: 1;
}

.seat-available {
  background: linear-gradient(145deg, #636e72, #2d3436);
  color: #fff;
  border-color: #48dbfb33;
}

.seat-available:hover {
  background: linear-gradient(145deg, #48dbfb, #0984e3);
  color: #fff;
  transform: translateY(-2px) scale(1.05);
  border-color: #48dbfb;
  box-shadow: 0 8px 25px rgba(72, 219, 251, 0.3);
}

.seat-selected {
  background: linear-gradient(145deg, #48dbfb, #0984e3);
  color: #fff;
  border-color: #48dbfb;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(72, 219, 251, 0.4);
}

.seat-selected:hover {
  background: linear-gradient(145deg, #00b894, #48dbfb);
  transform: translateY(-2px) scale(1.05);
}

.seat-booked {
  background: linear-gradient(145deg, #e74c3c, #c0392b);
  color: #fff;
  cursor: not-allowed;
  opacity: 0.9;
  border-color: #e74c3c;
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.4);
}

.seat-booked::after {
  content: '✗';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 18px;
  color: #fff;
  z-index: 2;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

/* Legend */
.seat-legend {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  margin-top: 30px;
  padding: 20px;
  background: #232526;
  border-radius: 15px;
  border: 1px solid #48dbfb22;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #ddd;
  font-size: 14px;
  font-weight: 500;
}

.seat-available-demo {
  background: linear-gradient(145deg, #636e72, #2d3436);
  border-color: #48dbfb33;
  width: 32px;
  height: 32px;
  color: #fff;
}

.seat-selected-demo {
  background: linear-gradient(145deg, #48dbfb, #0984e3);
  border-color: #48dbfb;
  width: 32px;
  height: 32px;
  color: #fff;
  box-shadow: 0 4px 15px rgba(72, 219, 251, 0.3);
}

.seat-booked-demo {
  background: linear-gradient(145deg, #e74c3c, #c0392b);
  border-color: #e74c3c;
  width: 32px;
  height: 32px;
  color: #fff;
  opacity: 0.9;
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.4);
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
  margin: 20px 0;
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #48dbfb33;
}

.customer-form h3 {
  color: #48dbfb;
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 700;
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

/* Booking Summary Styles */
.booking-summary {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #48dbfb33;
}

.booking-summary h3 {
  color: #48dbfb;
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 700;
}

.booking-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
}

.booking-item {
  background: #18191a;
  padding: 12px 16px;
  border-radius: 8px;
  color: #fff;
  font-size: 15px;
  border-left: 3px solid #48dbfb;
}

.booking-item strong {
  color: #feca57;
}

/* Combo Selection Styles */
.combo-selection {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 12px;
  padding: 20px;
  margin: 20px 0;
  border: 1px solid #48dbfb33;
}

.combo-selection h3 {
  color: #48dbfb;
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 700;
}

.combo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.combo-card {
  background: #18191a;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #333;
  transition: all 0.3s ease;
}

.combo-card:hover {
  border-color: #48dbfb;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.1);
}

.combo-image {
  width: 100%;
  height: 120px;
  margin-bottom: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.combo-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Animations */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 6px 20px rgba(72, 219, 251, 0.4);
  }
  50% {
    box-shadow: 0 6px 30px rgba(72, 219, 251, 0.6);
  }
}

@keyframes screenPulse {
  0% {
    opacity: 0.6;
    transform: scale(1);
  }
  100% {
    opacity: 0.9;
    transform: scale(1.02);
  }
}

@keyframes screenFlash {
  0% {
    left: -100%;
    opacity: 0;
  }
  30% {
    opacity: 1;
  }
  50% {
    left: 100%;
    opacity: 0.8;
  }
  100% {
    left: 150%;
    opacity: 0;
  }
}

.combo-info h4 {
  color: #fff;
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.combo-description {
  color: #aaa;
  font-size: 13px;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.combo-price {
  color: #feca57;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 12px;
}

.combo-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.combo-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.combo-btn.add {
  background: #48dbfb;
  color: #000;
}

.combo-btn.add:hover {
  background: #3ac7e8;
  transform: scale(1.1);
}

.combo-btn.remove {
  background: #ff6b6b;
  color: #fff;
}

.combo-btn.remove:hover:not(:disabled) {
  background: #ff5252;
  transform: scale(1.1);
}

.combo-btn:disabled {
  background: #444;
  color: #666;
  cursor: not-allowed;
  transform: none;
}

.combo-quantity {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
}

.selected-combos {
  background: #1a1b1c;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #333;
}

.selected-combos h4 {
  color: #48dbfb;
  margin: 0 0 12px 0;
  font-size: 16px;
}

.selected-combo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  color: #fff;
  border-bottom: 1px solid #333;
}

.selected-combo-item:last-child {
  border-bottom: none;
}

.combo-total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #48dbfb;
  text-align: right;
  color: #feca57;
  font-size: 16px;
}

.loading, .no-combos, .seats-loading {
  text-align: center;
  color: #aaa;
  padding: 40px 20px;
  font-style: italic;
}

.seats-loading {
  background: #18191a;
  border-radius: 12px;
  margin: 20px 0;
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

  .showtime-selection {
    flex-direction: column;
    gap: 6px;
    align-items: stretch;
  }

  .btn-showtime-modal {
    font-size: 12px;
    padding: 6px 10px;
  }

  /* Mobile Seat Styles */
  .seats-grid {
    padding: 20px 15px;
    border-radius: 15px;
  }

  .seat {
    width: 36px;
    height: 32px;
    font-size: 9px;
  }

  .seats-in-row {
    gap: 4px;
  }

  .seat-row {
    gap: 10px;
    margin-bottom: 8px;
  }

  .row-label {
    width: 25px;
    height: 25px;
    font-size: 14px;
  }

  .cinema-screen {
    margin-bottom: 25px;
    perspective: 1000px;
  }

  .curved-screen {
    width: 85%;
    height: 45px;
    perspective: 1200px;
    perspective-origin: center 180px;
  }

  .screen-surface {
    height: 45px;
    transform: perspective(500px) rotateX(-12deg) scaleY(1.15);
    border-radius: 40px 40px 25px 25px;
  }

  .curved-screen::before {
    height: 70px;
    top: -12px;
    filter: blur(12px);
  }

  .screen-text {
    font-size: 12px;
    letter-spacing: 1.5px;
  }

  .seat-legend {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }

  .legend-item {
    justify-content: center;
    gap: 8px;
  }

  .seat-available-demo,
  .seat-selected-demo,
  .seat-booked-demo {
    width: 28px;
    height: 28px;
  }

  .screen {
    font-size: 15px;
  }

  /* Time Expired Mobile */
  .time-expired-content {
    padding: 30px 25px;
    margin: 0 15px;
  }

  .expired-icon {
    font-size: 50px;
    margin-bottom: 15px;
  }

  .time-expired-content h3 {
    font-size: 20px;
    margin-bottom: 12px;
  }

  .time-expired-content p {
    font-size: 14px;
    margin-bottom: 25px;
  }

  .btn-reload {
    padding: 12px 30px;
    font-size: 16px;
  }

  .step-header {
    font-size: 17px;
  }
}
</style>
