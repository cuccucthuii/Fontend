<template>
  <div class="booking-page">
    <!-- Your existing template content -->
    <div class="booking-content">
      <!-- Date selection -->
      <div class="date-selection">
        <h3>Chọn ngày xem</h3>
        <div class="date-list">
          <div 
            v-for="date in availableDates" 
            :key="date.value"
            class="date-item"
            :class="{ active: selectedDate === date.value }"
            @click="selectDate(date.value)"
          >
            {{ date.label }}
          </div>
        </div>
      </div>
      
      <!-- Showtime selection -->
      <div class="showtime-selection">
        <h3>Chọn suất chiếu</h3>
        <div class="showtime-list">
          <div 
            v-for="showtime in filteredShowtimes" 
            :key="showtime.id"
            class="showtime-item"
            @click="selectShowtime(showtime)"
          >
            <div class="showtime-time">{{ showtime.time }}</div>
            <div class="showtime-room">{{ showtime.room }}</div>
            <div class="showtime-format">{{ showtime.format }}</div>
            <div class="showtime-seats">{{ showtime.availableSeats }}/{{ showtime.totalSeats }}</div>
            <button 
              class="book-btn"
              :disabled="!canBookShowtime(showtime)"
              @click.stop="bookShowtime(showtime)"
            >
              {{ getBookButtonText(showtime) }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { 
  safeFilter, 
  normalizeSchedules, 
  filterMoviesByBranch, 
  filterMoviesByDate,
  filterMoviesByStatus,
  debugData 
} from '@/utils/bookingHelpers'

// Props
const props = defineProps({
  movieId: {
    type: [String, Number],
    default: null
  },
  branchId: {
    type: [String, Number],
    default: null
  }
})

// Reactive data
const schedules = ref([])
const selectedDate = ref('')
const selectedBranch = ref(props.branchId)
const loading = ref(false)
const error = ref(null)

// Available dates (7 days from today)
const availableDates = computed(() => {
  const dates = []
  const today = new Date()
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    
    dates.push({
      value: date.toISOString().split('T')[0], // YYYY-MM-DD
      label: formatDateLabel(date)
    })
  }
  
  return dates
})

// Computed properties
const filteredShowtimes = computed(() => {
  try {
    // Debug dữ liệu
    debugData(schedules.value, 'Schedules')
    
    // Normalize dữ liệu
    const normalizedSchedules = normalizeSchedules(schedules.value)
    console.log('Normalized schedules:', normalizedSchedules)
    
    // Filter theo branch
    let filtered = normalizedSchedules
    if (selectedBranch.value) {
      filtered = filterMoviesByBranch(filtered, selectedBranch.value)
      console.log('Filtered by branch:', filtered)
    }
    
    // Filter theo date
    if (selectedDate.value) {
      filtered = filterMoviesByDate(filtered, selectedDate.value)
      console.log('Filtered by date:', filtered)
    }
    
    return filtered
  } catch (error) {
    console.error('Error in filteredShowtimes:', error)
    return []
  }
})

// Methods
const formatDateLabel = (date) => {
  const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
  const dayName = days[date.getDay()]
  const day = date.getDate().toString().padStart(2, '0')
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  
  return `${dayName} ${day} ${month}`
}

const selectDate = (date) => {
  selectedDate.value = date
  console.log('Selected date:', date)
}

const selectShowtime = (showtime) => {
  console.log('Selected showtime:', showtime)
  // Handle showtime selection
}

const canBookShowtime = (showtime) => {
  // Kiểm tra có thể đặt vé không
  if (!showtime) return false
  
  // Kiểm tra phim có phải SẮP_CHIẾU không
  const movieStatus = showtime.movie?.status || showtime.phim?.trang_thai
  if (movieStatus === 'SAP_CHIEU') {
    // Kiểm tra ngày phát hành
    const releaseDate = showtime.movie?.releaseDate || showtime.phim?.ngay_phat_hanh
    if (releaseDate && new Date(releaseDate) > new Date()) {
      return false // Chưa phát hành
    }
  }
  
  // Kiểm tra ghế còn trống
  const availableSeats = showtime.availableSeats || showtime.ghe_con_trong || 0
  return availableSeats > 0
}

const getBookButtonText = (showtime) => {
  if (!canBookShowtime(showtime)) {
    const movieStatus = showtime.movie?.status || showtime.phim?.trang_thai
    if (movieStatus === 'SAP_CHIEU') {
      return 'Chưa phát hành'
    }
    return 'Hết vé'
  }
  return 'Còn vé'
}

const bookShowtime = async (showtime) => {
  if (!canBookShowtime(showtime)) {
    alert('Không thể đặt vé cho suất chiếu này!')
    return
  }
  
  try {
    // TODO: Implement booking logic
    console.log('Booking showtime:', showtime)
    
    // Redirect to seat selection
    // router.push(`/seats/${showtime.id}`)
    
  } catch (error) {
    console.error('Booking error:', error)
    alert('Có lỗi xảy ra khi đặt vé!')
  }
}

const fetchSchedules = async () => {
  loading.value = true
  error.value = null
  
  try {
    // TODO: Replace with actual API call
    const response = await fetch(`/api/showtimes?movieId=${props.movieId}&branchId=${props.branchId}`)
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const data = await response.json()
    console.log('API response:', data)
    
    // Normalize dữ liệu từ API
    schedules.value = normalizeSchedules(data)
    
  } catch (err) {
    console.error('Error fetching schedules:', err)
    error.value = err.message
    schedules.value = []
  } finally {
    loading.value = false
  }
}

// Lifecycle
onMounted(() => {
  // Set default date to today
  if (availableDates.value.length > 0) {
    selectedDate.value = availableDates.value[0].value
  }
  
  // Fetch schedules
  fetchSchedules()
})

// Watchers
watch(() => props.movieId, () => {
  fetchSchedules()
})

watch(() => props.branchId, (newBranchId) => {
  selectedBranch.value = newBranchId
  fetchSchedules()
})
</script>

<style scoped>
.booking-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.booking-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.date-selection,
.showtime-selection {
  margin-bottom: 32px;
}

.date-selection h3,
.showtime-selection h3 {
  margin-bottom: 16px;
  color: #1f2937;
  font-size: 1.2rem;
  font-weight: 600;
}

.date-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.date-item {
  min-width: 80px;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.date-item:hover {
  border-color: #3b82f6;
  background: #f8fafc;
}

.date-item.active {
  border-color: #3b82f6;
  background: #3b82f6;
  color: white;
}

.showtime-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.showtime-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.showtime-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.showtime-time {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.showtime-room,
.showtime-format {
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 2px;
}

.showtime-seats {
  font-size: 0.9rem;
  color: #059669;
  margin-bottom: 12px;
}

.book-btn {
  width: 100%;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.book-btn:not(:disabled) {
  background: #059669;
  color: white;
}

.book-btn:not(:disabled):hover {
  background: #047857;
}

.book-btn:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
}

/* Loading state */
.loading {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

/* Error state */
.error {
  text-align: center;
  padding: 40px;
  color: #dc2626;
  background: #fef2f2;
  border-radius: 8px;
  margin: 20px 0;
}

/* Responsive */
@media (max-width: 768px) {
  .booking-page {
    padding: 16px;
  }
  
  .booking-content {
    padding: 16px;
  }
  
  .showtime-list {
    grid-template-columns: 1fr;
  }
  
  .date-list {
    gap: 8px;
  }
  
  .date-item {
    min-width: 70px;
    padding: 10px 12px;
  }
}
</style>
