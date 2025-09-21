<template>
  <div v-if="visible && movie && cinema" class="modal-backdrop" @click.self="$emit('close')">
    <div class="showtime-modal">
      <button class="modal-close" @click="$emit('close')">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>

      <!-- Header with Movie Info -->
      <div class="modal-header">
        <div class="movie-info">
          <div class="movie-poster">
            <img 
              :src="getPosterUrl(movie?.posterUrl || movie?.poster)" 
              :alt="movie?.tenPhim || movie?.title"
              @error="handleImageError"
            />
          </div>
          <div class="movie-details">
            <h2 class="movie-title">{{ movie?.tenPhim || movie?.title }}</h2>
            <div class="movie-meta">
              <span class="movie-format">{{ movie?.format || '2D PHỤ ĐỀ' }}</span>
              <span class="movie-duration">{{ movie?.duration || '120 phút' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Cinema Info -->
      <div class="cinema-section">
        <div class="cinema-info">
          <div class="cinema-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 7V5C3 3.89543 3.89543 3 5 3H19C20.1046 3 21 3.89543 21 5V7M3 7V19C3 20.1046 3.89543 21 5 21H19C20.1046 21 21 20.1046 21 19V7M3 7H21M7 11H17M7 15H13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="cinema-details">
            <h3 class="cinema-name">{{ props.cinema?.tenRapChieu || props.cinema?.name || "Rạp chiếu" }}</h3>
            <p class="cinema-address">{{ props.cinema?.address || "Địa chỉ rạp" }}</p>
          </div>
        </div>
      </div>

      <!-- Date Selection -->
      <div class="date-section">
        <div class="section-title">
          <h4>Chọn ngày xem</h4>
          <div class="date-indicator">
            <span class="selected-date-info">
              {{ formatSelectedDate(selectedDate) }}
            </span>
          </div>
        </div>
        <div class="date-tabs">
          <button
            v-for="date in availableDates"
            :key="date.value"
            :class="['date-tab', { active: selectedDate === date.value }]"
            @click="selectDate(date.value)"
          >
            <div class="date-content">
              <div class="date-weekday">{{ date.weekday }}</div>
            <div class="date-number">{{ date.day }}</div>
              <div class="date-month">{{ formatMonth(date.month) }}</div>
            </div>
            <div v-if="selectedDate === date.value" class="date-indicator-dot"></div>
          </button>
        </div>
      </div>

      <!-- Showtimes -->
      <div class="showtimes-section">
        <div class="section-title">
          <h4>Chọn suất chiếu</h4>
          <div class="showtime-count">
            <span v-if="filteredShowtimes.length > 0">
              {{ filteredShowtimes.length }} suất chiếu
            </span>
          </div>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải lịch chiếu...</p>
        </div>

        <div v-else-if="error" class="error-state">
          <div class="error-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2"/>
              <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <p>{{ error }}</p>
          <button @click="loadShowtimes" class="retry-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12A9 9 0 0 1 12 3A9 9 0 0 1 21 12A9 9 0 0 1 12 21A9 9 0 0 1 3 12Z" stroke="currentColor" stroke-width="2"/>
              <path d="M12 3V12L16 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Thử lại
          </button>
        </div>

        <div v-else>
          <!-- Special handling for upcoming movies with no showtimes -->
          <div v-if="movie?.trangThai === 'SAP_CHIEU' && filteredShowtimes.length === 0" class="upcoming-movie-notice">
            <div class="upcoming-icon">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 6V12L16 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h4>Phim sắp chiếu</h4>
            <p>Phim này sẽ sớm có lịch chiếu. Vui lòng quay lại sau!</p>
            <p class="release-info" v-if="movie?.ngayPhatHanh">
              Dự kiến phát hành: {{ formatReleaseDate(movie.ngayPhatHanh) }}
            </p>
          </div>
          
          <div v-else-if="filteredShowtimes.length === 0" class="no-showtimes">
            <div class="no-showtimes-icon">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M8 12H16M12 8V16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h4>Không có suất chiếu</h4>
            <p>Không có suất chiếu nào cho ngày {{ formatSelectedDate(selectedDate) }}</p>
            <p class="suggestion">Vui lòng chọn ngày khác hoặc thử rạp khác</p>
          </div>
          <div v-else class="showtimes-grid">
            <button
              v-for="showtime in filteredShowtimes"
              :key="showtime.id"
              :class="['showtime-btn', { 
                disabled: showtime.soldOut || movie?.trangThai === 'SAP_CHIEU',
                'low-seats': showtime.availableSeats <= 10 && !showtime.soldOut,
                'upcoming-movie': movie?.trangThai === 'SAP_CHIEU'
              }]"
              @click="selectShowtime(showtime)"
              :disabled="showtime.soldOut || movie?.trangThai === 'SAP_CHIEU'"
            >
              <div class="showtime-header">
              <div class="showtime-time">{{ showtime.time }}</div>
                <div class="showtime-end">{{ showtime.endTime }}</div>
              </div>
              
                      <div class="showtime-body">
                        <div class="showtime-format">{{ showtime.format || '2D PHỤ ĐỀ' }}</div>
                        
                        <!-- Room Information -->
                        <div class="showtime-room">
                          <div class="room-info">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <path d="M3 7V5C3 3.89543 3.89543 3 5 3H19C20.1046 3 21 3.89543 21 5V7M3 7V19C3 20.1046 3.89543 21 5 21H19C20.1046 21 21 20.1046 21 19V7M3 7H21M7 11H17M7 15H13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            <span>{{ showtime.tenPhong || showtime.roomName || showtime.phongChieu || 'Phòng A1' }}</span>
                          </div>
                        </div>
                        
                        <div class="showtime-seats">
                          <div class="seats-info">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <path d="M4 6H20C21.1046 6 22 6.89543 22 8V18C22 19.1046 21.1046 20 20 20H4C2.89543 20 2 19.1046 2 18V8C2 6.89543 2.89543 6 4 6Z" stroke="currentColor" stroke-width="2"/>
                              <path d="M6 6V4C6 2.89543 6.89543 2 8 2H16C17.1046 2 18 2.89543 18 4V6" stroke="currentColor" stroke-width="2"/>
                            </svg>
                            <span>{{ showtime.availableSeats }}/{{ showtime.totalSeats }}</span>
                          </div>
                          <div class="seats-status" :class="getSeatsStatusClass(showtime.availableSeats, showtime.totalSeats)">
                            {{ getSeatsStatusText(showtime.availableSeats, showtime.totalSeats) }}
                          </div>
                        </div>
                      </div>

              <div class="showtime-footer">
                <div v-if="movie?.trangThai === 'SAP_CHIEU'" class="upcoming-badge">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M12 6V12L16 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Sắp chiếu
                </div>
                <div v-else-if="showtime.soldOut" class="sold-out-badge">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Hết vé
                </div>
                <div v-else-if="showtime.availableSeats <= 10" class="low-seats-badge">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 9V13M12 17H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Sắp hết
                </div>
                <div v-else class="available-badge">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Còn vé
                </div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { getShowtimesByMovieAndCinema } from "../../services/showtimeService";

const props = defineProps({
  movie: { 
    type: Object, 
    default: () => null 
  },
  visible: { 
    type: Boolean, 
    default: false 
  },
  cinema: { 
    type: Object, 
    default: () => null 
  },
});

const emit = defineEmits({
  close: null,
  selectShowtime: null
});

const selectedDate = ref("");
const selectedCinema = ref(props.cinema);
const showtimes = ref([]);
const availableDatesFromAPI = ref([]);
const loading = ref(false);
const error = ref("");

// Generate available dates (next 7 days) - fallback if API doesn't provide
const availableDates = computed(() => {
  if (availableDatesFromAPI.value.length > 0) {
    return availableDatesFromAPI.value;
  }

  const dates = [];
  const today = new Date();

  for (let i = 0; i < 7; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + i);

    const day = date.getDate();
    const month = `/${String(date.getMonth() + 1).padStart(2, "0")}`;
    const weekdays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    const weekday = weekdays[date.getDay()];

    dates.push({
      value: date.toISOString().split("T")[0],
      day: day,
      month: month,
      weekday: weekday,
      fullDate: date,
    });
  }

  return dates;
});

// Load showtimes from API
async function loadShowtimes() {
  if (!props.movie || !props.cinema) return;

  loading.value = true;
  error.value = "";

  try {
    const movieId = props.movie.idPhim || props.movie.id;
    const cinemaId = props.cinema.idRapChieu || props.cinema.id;

    console.log("🎬 Loading showtimes for:", {
      movieId,
      cinemaId,
      movieName: props.movie.tenPhim || props.movie.title,
      cinemaName: props.cinema.tenRapChieu || props.cinema.name
    });

    let response = await getShowtimesByMovieAndCinema(movieId, cinemaId);

    console.log("📅 Showtimes API response:", response);

    if (response.success) {
      showtimes.value = response.data.showtimes || [];
      availableDatesFromAPI.value = response.data.availableDates || [];

      console.log("📅 API returned dates:", availableDatesFromAPI.value);
      console.log("📅 API returned showtimes:", showtimes.value);

      // Set first available date as selected
      if (availableDatesFromAPI.value.length > 0) {
        selectedDate.value = availableDatesFromAPI.value[0].value;
        console.log("📅 Selected first API date:", selectedDate.value);
      } else if (showtimes.value.length > 0) {
        // Fallback: use first showtime date if no availableDates
        const firstDate = showtimes.value[0].date;
        selectedDate.value = firstDate;
        console.log("📅 Fallback to first showtime date:", selectedDate.value);
      }
    } else {
      console.log("🔄 Fallback: Trying alternative API...");
      // Fallback: Try using the general showtimes API with movieId filter
      const { getShowtimesByMovie } = await import("../../services/showtimeService");
      const fallbackResponse = await getShowtimesByMovie(movieId);
      
      console.log("📅 Fallback API response:", fallbackResponse);
      
      if (fallbackResponse.content && fallbackResponse.content.length > 0) {
        // Filter by cinema if possible
        const filteredShowtimes = fallbackResponse.content.filter(showtime => {
          // If cinema info is available in showtime data, filter by it
          return !cinemaId || showtime.idRapChieu === cinemaId || showtime.cinemaId === cinemaId;
        });
        
        showtimes.value = filteredShowtimes;
        
        // Generate available dates from showtimes
        const dates = [...new Set(filteredShowtimes.map(s => s.ngayChieu))].map(date => ({
          value: date,
          day: new Date(date).getDate(),
          month: new Date(date).getMonth() + 1,
          weekday: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'][new Date(date).getDay()]
        }));
        
        availableDatesFromAPI.value = dates;
        
        if (dates.length > 0) {
          selectedDate.value = dates[0].value;
      }
    } else {
      error.value = response.message || "Không thể tải lịch chiếu";
      }
    }
  } catch (err) {
    error.value = "Không thể tải lịch chiếu. Vui lòng thử lại.";
    console.error("❌ Error loading showtimes:", err);
  } finally {
    loading.value = false;
  }
}

// Filter showtimes by selected date
const filteredShowtimes = computed(() => {
  if (!selectedDate.value) return [];
  
  console.log("🔍 Filtering showtimes:", {
    selectedDate: selectedDate.value,
    allShowtimes: showtimes.value,
    showtimeDates: showtimes.value.map(s => s.date)
  });
  
  const filtered = showtimes.value.filter((showtime) => {
    const match = showtime.date === selectedDate.value;
    console.log(`🔍 Showtime ${showtime.id}: ${showtime.date} === ${selectedDate.value} = ${match}`);
    return match;
  });
  
  console.log("🔍 Filtered result:", filtered);
  return filtered;
});

// Watch for modal visibility and load data
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      loadShowtimes();
    }
  }
);

function selectDate(date) {
  selectedDate.value = date;
}

function selectShowtime(showtime) {
  if (showtime.soldOut) return;
  
  // Prevent booking for upcoming movies
  if (props.movie?.trangThai === 'SAP_CHIEU') {
    console.log("🚫 Cannot book upcoming movie:", props.movie.tenPhim);
    return;
  }

  console.log("🎯 Selected showtime data:", showtime);

  emit("selectShowtime", {
    id: showtime.id,
    movie: props.movie,
    cinema: props.cinema,
    date: selectedDate.value,
    time: showtime.time,
    endTime: showtime.endTime,
    availableSeats: showtime.availableSeats,
    totalSeats: showtime.totalSeats,
    scheduleId: showtime.scheduleId || showtime.idLichChieu || showtime.id,
  });

  emit("close");
}

// Helper functions for UI
function getPosterUrl(url) {
  if (!url) return '/logo.png?v=2';
  if (url.startsWith('http')) return url;
  if (url.startsWith('/')) return url;
  return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${url}`;
}

function handleImageError(event) {
  event.target.src = '/logo.png?v=2';
}

function formatSelectedDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  const options = { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  };
  return date.toLocaleDateString('vi-VN', options);
}

function formatMonth(monthString) {
  if (!monthString) return '';
  return monthString.replace('/', '');
}

function formatReleaseDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  };
  return date.toLocaleDateString('vi-VN', options);
}

function getSeatsStatusClass(available, total) {
  const percentage = (available / total) * 100;
  if (percentage === 0) return 'sold-out';
  if (percentage <= 20) return 'low-seats';
  if (percentage <= 50) return 'medium-seats';
  return 'high-seats';
}

function getSeatsStatusText(available, total) {
  const percentage = (available / total) * 100;
  if (percentage === 0) return 'Hết vé';
  if (percentage <= 20) return 'Sắp hết';
  if (percentage <= 50) return 'Còn ít';
  return 'Còn nhiều';
}

</script>

<style scoped>
/* Modal Backdrop */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Main Modal */
.showtime-modal {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 1000px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Close Button */
.modal-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  color: #666;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.modal-close:hover {
  background: rgba(255, 255, 255, 1);
  color: #000;
  transform: scale(1.1);
}

/* Header Section */
.modal-header {
  padding: 30px 30px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px 20px 0 0;
}

.movie-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.movie-poster {
  width: 80px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-details {
  flex: 1;
}

.movie-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.movie-meta {
  display: flex;
  gap: 16px;
  align-items: center;
}

.movie-format,
.movie-duration {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

/* Cinema Section */
.cinema-section {
  padding: 25px 30px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.cinema-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cinema-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.cinema-details {
  flex: 1;
}

.cinema-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
}

.cinema-address {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* Date Section */
.date-section {
  padding: 25px 30px;
  border-bottom: 1px solid #e9ecef;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title h4 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.date-indicator {
  background: #e3f2fd;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #bbdefb;
}

.selected-date-info {
  font-size: 14px;
  font-weight: 600;
  color: #1976d2;
}

.date-tabs {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.date-tab {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 16px 20px;
  cursor: pointer;
  text-align: center;
  min-width: 90px;
  transition: all 0.3s ease;
  position: relative;
  flex-shrink: 0;
}

.date-tab:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.date-tab.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.date-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.date-weekday {
  font-size: 12px;
  font-weight: 600;
  opacity: 0.8;
}

.date-number {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.date-month {
  font-size: 12px;
  font-weight: 600;
  opacity: 0.8;
}

.date-indicator-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: #4caf50;
  border-radius: 50%;
  border: 2px solid white;
}

/* Showtimes Section */
.showtimes-section {
  padding: 25px 30px;
}

.showtime-count {
  background: #e8f5e8;
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid #c8e6c9;
}

.showtime-count span {
  font-size: 12px;
  font-weight: 600;
  color: #2e7d32;
}

.showtimes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.showtime-btn {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 20px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.showtime-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.showtime-btn:hover:not(.disabled) {
  border-color: #667eea;
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.2);
}

.showtime-btn:hover:not(.disabled)::before {
  transform: scaleX(1);
}

.showtime-btn.disabled {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
  border-color: #dee2e6;
}

.showtime-btn.upcoming-movie {
  background: linear-gradient(135deg, rgba(243, 156, 18, 0.2) 0%, rgba(230, 126, 34, 0.2) 100%);
  border: 2px solid rgba(243, 156, 18, 0.3);
  cursor: not-allowed;
}

.showtime-btn.upcoming-movie:hover {
  transform: none;
  box-shadow: none;
}

.showtime-btn.low-seats {
  border-color: #ff9800;
}

.showtime-btn.low-seats:hover {
  border-color: #f57c00;
  box-shadow: 0 12px 30px rgba(255, 152, 0, 0.2);
}

.showtime-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.showtime-time {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.showtime-end {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.showtime-body {
  margin-bottom: 12px;
}

.showtime-format {
  font-size: 12px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.showtime-room {
  margin-bottom: 8px;
}

.room-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #555;
  font-weight: 500;
}

.room-info svg {
  color: #667eea;
}


.showtime-seats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seats-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.seats-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 12px;
}

.seats-status.high-seats {
  background: #e8f5e8;
  color: #2e7d32;
}

.seats-status.medium-seats {
  background: #fff3e0;
  color: #f57c00;
}

.seats-status.low-seats {
  background: #ffebee;
  color: #d32f2f;
}

.seats-status.sold-out {
  background: #f5f5f5;
  color: #757575;
}

.showtime-footer {
  display: flex;
  justify-content: center;
}

.sold-out-badge,
.low-seats-badge,
.available-badge,
.upcoming-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 16px;
}

.upcoming-badge {
  background: #fff3e0;
  color: #f39c12;
}

.sold-out-badge {
  background: #ffebee;
  color: #d32f2f;
}

.low-seats-badge {
  background: #fff3e0;
  color: #f57c00;
}

.available-badge {
  background: #e8f5e8;
  color: #2e7d32;
}

/* Loading and Error States */
.loading-state,
.error-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  color: #dc3545;
  margin-bottom: 20px;
}

.error-state p {
  color: #dc3545;
  margin-bottom: 20px;
  font-size: 16px;
}

.retry-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.upcoming-movie-notice {
  text-align: center;
  padding: 40px 20px;
  color: #b2bec3;
  background: linear-gradient(135deg, rgba(243, 156, 18, 0.1) 0%, rgba(230, 126, 34, 0.1) 100%);
  border-radius: 12px;
  border: 1px solid rgba(243, 156, 18, 0.2);
  margin: 20px 0;
}

.upcoming-icon {
  margin-bottom: 16px;
  color: #f39c12;
}

.upcoming-movie-notice h4 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #f39c12;
}

.upcoming-movie-notice p {
  margin-bottom: 8px;
  line-height: 1.5;
}

.release-info {
  font-size: 0.9rem;
  color: #e67e22;
  font-weight: 500;
  background: rgba(243, 156, 18, 0.1);
  padding: 8px 16px;
  border-radius: 8px;
  display: inline-block;
}

.no-showtimes {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-showtimes-icon {
  color: #ccc;
  margin-bottom: 20px;
}

.no-showtimes h4 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
}

.no-showtimes p {
  font-size: 16px;
  margin: 0 0 8px 0;
}

.suggestion {
  font-size: 14px;
  color: #999;
  font-style: italic;
}

/* Responsive Design */
@media (max-width: 768px) {
  .modal-backdrop {
    padding: 10px;
  }

  .showtime-modal {
    max-height: 95vh;
    border-radius: 16px;
  }

  .modal-header {
    padding: 20px;
    border-radius: 16px 16px 0 0;
  }

  .movie-info {
    gap: 15px;
  }

  .movie-poster {
    width: 60px;
    height: 90px;
  }

  .movie-title {
    font-size: 18px;
  }

  .movie-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .cinema-section,
  .date-section,
  .showtimes-section {
    padding: 20px;
  }

  .cinema-info {
    gap: 12px;
  }

  .cinema-icon {
    width: 40px;
    height: 40px;
  }

  .cinema-name {
    font-size: 18px;
  }

  .date-tabs {
    gap: 8px;
  }

  .date-tab {
    padding: 12px 16px;
    min-width: 80px;
  }

  .date-number {
    font-size: 20px;
  }

  .showtimes-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .showtime-btn {
    padding: 16px;
  }

  .showtime-time {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .modal-close {
    top: 15px;
    right: 15px;
    width: 35px;
    height: 35px;
  }

  .movie-info {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .movie-poster {
    width: 80px;
    height: 120px;
    margin: 0 auto;
  }

  .section-title {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .date-tabs {
    flex-direction: column;
    gap: 8px;
  }

  .date-tab {
    width: 100%;
    min-width: auto;
  }
}
</style>
