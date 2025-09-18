<template>
  <div v-if="visible" class="modal-backdrop" @click.self="$emit('close')">
    <div class="showtime-modal">
      <button class="modal-close" @click="$emit('close')">×</button>

      <!-- Header -->
      <div class="modal-header">
        <h2 class="modal-title">
          LỊCH CHIẾU - {{ movie?.tenPhim || movie?.title }}
        </h2>
      </div>

      <!-- Cinema Selection -->
      <div class="cinema-section">
        <h3 class="cinema-name">
          {{ props.cinema?.tenRapChieu || props.cinema?.name || "Rạp chiếu" }}
        </h3>
      </div>

      <!-- Date Selection -->
      <div class="date-section">
        <div class="date-tabs">
          <button
            v-for="date in availableDates"
            :key="date.value"
            :class="['date-tab', { active: selectedDate === date.value }]"
            @click="selectDate(date.value)"
          >
            <div class="date-number">{{ date.day }}</div>
            <div class="date-month">{{ date.month }}</div>
            <div class="date-weekday">{{ date.weekday }}</div>
          </button>
        </div>
      </div>

      <!-- Showtimes -->
      <div class="showtimes-section">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải lịch chiếu...</p>
        </div>

        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="loadShowtimes" class="retry-btn">Thử lại</button>
        </div>

        <div v-else>
          <div class="format-label">2D PHỤ ĐỀ</div>
          <div v-if="filteredShowtimes.length === 0" class="no-showtimes">
            <p>Không có suất chiếu nào cho ngày này</p>
          </div>
          <div v-else class="showtimes-grid">
            <button
              v-for="showtime in filteredShowtimes"
              :key="showtime.id"
              :class="['showtime-btn', { disabled: showtime.soldOut }]"
              @click="selectShowtime(showtime)"
              :disabled="showtime.soldOut"
            >
              <div class="showtime-time">{{ showtime.time }}</div>
              <div class="showtime-seats">
                {{ showtime.availableSeats }} ghế trống
              </div>
              <div class="showtime-endtime">{{ showtime.endTime }}</div>
              <div class="showtime-total">
                {{ showtime.totalSeats }} ghế tổng
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
import { getShowtimesByMovieAndCinema } from "../services/showtimeService";

const props = defineProps({
  movie: { type: Object, required: true },
  visible: { type: Boolean, default: false },
  cinema: { type: Object, required: true },
});

const emit = defineEmits(["close", "selectShowtime"]);

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

    const response = await getShowtimesByMovieAndCinema(movieId, cinemaId);

    console.log("📅 Showtimes API response:", response);

    if (response.success) {
      showtimes.value = response.data.showtimes || [];
      availableDatesFromAPI.value = response.data.availableDates || [];

      // Set first available date as selected
      if (availableDatesFromAPI.value.length > 0) {
        selectedDate.value = availableDatesFromAPI.value[0].value;
      }
    } else {
      error.value = response.message || "Không thể tải lịch chiếu";
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
  return showtimes.value.filter((showtime) => {
    return showtime.date === selectedDate.value;
  });
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
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.showtime-modal {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 900px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  padding: 0;
}

.modal-close {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  z-index: 10;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  color: #000;
}

.modal-header {
  padding: 20px 60px 20px 20px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  text-transform: uppercase;
}

.cinema-section {
  padding: 20px;
  text-align: center;
}

.cinema-name {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.date-section {
  padding: 0 20px 20px;
  border-bottom: 1px solid #eee;
}

.date-tabs {
  display: flex;
  gap: 0;
  overflow-x: auto;
  padding-bottom: 10px;
}

.date-tab {
  background: none;
  border: none;
  padding: 15px 20px;
  cursor: pointer;
  text-align: center;
  border-bottom: 3px solid transparent;
  min-width: 80px;
  transition: all 0.3s ease;
}

.date-tab:hover {
  background: #f5f5f5;
}

.date-tab.active {
  border-bottom-color: #007bff;
  background: #f8f9fa;
}

.date-number {
  font-size: 24px;
  font-weight: 700;
  color: #007bff;
  line-height: 1;
}

.date-tab.active .date-number {
  color: #007bff;
}

.date-month {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.date-weekday {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.showtimes-section {
  padding: 20px;
}

.format-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  text-transform: uppercase;
}

.showtimes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
}

.showtime-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 12px 8px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.showtime-btn:hover:not(.disabled) {
  background: #e9ecef;
  border-color: #007bff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
}

.showtime-btn.disabled {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
}

.showtime-time {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.showtime-seats {
  font-size: 11px;
  color: #28a745;
  margin-bottom: 2px;
}

.showtime-endtime {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.showtime-total {
  font-size: 10px;
  color: #999;
}

.showtime-btn.disabled .showtime-time {
  color: #6c757d;
}

.showtime-btn.disabled .showtime-seats {
  color: #6c757d;
}

/* Responsive */
@media (max-width: 768px) {
  .showtime-modal {
    margin: 10px;
    max-height: 95vh;
  }

  .modal-title {
    font-size: 14px;
    padding-right: 40px;
  }

  .cinema-name {
    font-size: 20px;
  }

  .date-tabs {
    gap: 5px;
  }

  .date-tab {
    padding: 10px 15px;
    min-width: 70px;
  }

  .date-number {
    font-size: 20px;
  }

  .showtimes-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 8px;
  }

  .showtime-btn {
    padding: 10px 6px;
    min-height: 70px;
  }
}

/* Loading and Error States */
.loading-state,
.error-state {
  text-align: center;
  padding: 40px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.retry-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 12px;
}

.retry-btn:hover {
  background: #0056b3;
}

.no-showtimes {
  text-align: center;
  padding: 20px;
  color: #666;
}

.error-state p {
  color: #dc3545;
  margin-bottom: 12px;
}
</style>
