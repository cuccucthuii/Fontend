<template>
    <div class="seat-page">
      <!-- Header -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="header-icon">🎟️</span>
            Chọn Ghế Cho Khách
          </h1>
          <p class="page-subtitle">
            Chọn ghế ngồi từ sơ đồ bên dưới để bán vé tại quầy.
          </p>
        </div>
      </div>
  
  
      <!-- Thông tin suất chiếu -->
      <div class="schedule-card" v-if="schedule">
        <p><strong>🎬 Phim:</strong> {{ schedule.tenPhim }}</p>
        <p><strong>🏟️ Phòng:</strong> {{ schedule.tenPhongChieu }}</p>
        <p><strong>🕒 Thời gian:</strong> {{ schedule.gioBatDau }} - {{ schedule.gioKetThuc }}</p>
      </div>
  
      <!-- Loading -->
      <p v-if="loading" class="loading-state">Đang tải dữ liệu ghế...</p>
  
      <!-- Grid ghế -->
      <div class="seat-grid" v-else>
        <div
          v-for="seat in seats"
          :key="seat.idGheNgoi"
          :class="['seat-item', seatClass(seat)]"
          @click="toggleSeat(seat)"
        >
          {{ seat.hangGhe }}{{ seat.soGhe }}
        </div>
      </div>
  
      <!-- Tóm tắt -->
      <div class="selected-summary" v-if="selectedSeats.length">
        ✅ Đã chọn {{ selectedSeats.length }} ghế: {{ selectedSeatsNames }}
      </div>
  
      <button
        class="btn-confirm"
        :disabled="!selectedSeats.length"
        @click="confirmSeats"
      >
        ➡️ Xác nhận ghế
      </button>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import DatePicker from 'vue-datepicker-next'
  import 'vue-datepicker-next/index.css'
  
  import { fetchSeatsByRoom, fetchScheduleById } from '../../services/posService'
  
  const route = useRoute()
  const router = useRouter()
  
  const scheduleId = route.params.scheduleId
  console.log('✅ scheduleId:', scheduleId)
  const schedule = ref(null)
  const seats = ref([])
  const selectedSeats = ref([])
  const loading = ref(true)
  const selectedDate = ref('')
  
  onMounted(async () => {
  console.log('🟡 Route param scheduleId:', scheduleId);
  if (!scheduleId) {
    console.error('❌ scheduleId is missing!');
    return;
  }

  try {
    loading.value = true;

    console.log('🟡 Fetching schedule with id:', scheduleId);
    const resSchedule = await fetchScheduleById(scheduleId);
    console.log('🟡 Schedule response:', resSchedule);

    schedule.value = resSchedule.data;
    selectedDate.value = schedule.value?.ngayChieu || '';

    const roomId = schedule.value.idPhongChieu; //hung
    console.log('🟡 Room id for seats API:', roomId);

    const resSeats = await fetchSeatsByRoom(roomId);
    console.log('🟢 Seats response:', resSeats);

    seats.value = resSeats.data;
  } catch (err) {
    console.error('❌ Error:', err);
  } finally {
    loading.value = false;
  }
})

  
  function seatClass(seat) {
    if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') return 'seat-disabled'
    if (selectedSeats.value.includes(seat.idGheNgoi)) return 'seat-selected'
    return 'seat-available'
  }
  
  function toggleSeat(seat) {
    if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') return
    if (selectedSeats.value.includes(seat.idGheNgoi)) {
      selectedSeats.value = selectedSeats.value.filter(id => id !== seat.idGheNgoi)
    } else {
      selectedSeats.value.push(seat.idGheNgoi)
    }
  }
  
  function confirmSeats() {
    router.push({
      name: 'POSPayment',
      query: {
        scheduleId,
        seatIds: selectedSeats.value.join(',')
      }
    })
  }
  
  const selectedSeatsNames = computed(() =>
    seats.value
      .filter(seat => selectedSeats.value.includes(seat.idGheNgoi))
      .map(seat => `${seat.hangGhe}${seat.soGhe}`)
      .join(', ')
  )
  </script>
  
  <style scoped>
  .seat-page {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
    padding: 32px;
  }
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 32px;
    background: white;
    padding: 24px 32px;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .header-content {
    flex: 1;
  }
  
  .page-title {
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .header-icon {
    font-size: 32px;
  }
  
  .page-subtitle {
    margin: 0;
    color: #7f8c8d;
    font-size: 16px;
    font-weight: 400;
  }
  
  /* DatePicker wrapper with custom icon */
  .datepicker-wrapper {
    position: relative;
    width: 100%;
  }
  
  .filter-input-with-icon {
    width: 100%;
    padding: 0.875rem 1rem 0.875rem 2.5rem;
    border: 2px solid #e2e8f0;
    border-radius: 0.75rem;
    font-size: 0.875rem;
    background: white;
    color: #374151;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .filter-input-with-icon:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    transform: translateY(-1px);
  }
  
  .datepicker-custom-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    font-size: 1rem;
    color: #64748b;
    pointer-events: none;
  }
  
  .filters-section {
    max-width: 400px;
    margin: 0 auto 24px auto;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    padding: 1.5rem;
    border-radius: 1rem;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }
  
  .filter-group {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    flex: 1;
  }
  
  .filter-label {
    font-size: 0.875rem;
    font-weight: 700;
    color: #1e293b;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.25rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  .schedule-card {
    background: white;
    padding: 20px 28px;
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.05);
    margin-bottom: 24px;
    font-size: 16px;
    color: #2c3e50;
    line-height: 1.6;
    max-width: 500px;
    margin-left: auto;
    margin-right: auto;
  }
  
  .loading-state {
    text-align: center;
    padding: 40px 20px;
    font-size: 16px;
    color: #7f8c8d;
  }
  
  .seat-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(48px, 1fr));
    gap: 10px;
    max-width: 400px;
    margin: 0 auto 24px auto;
    background: white;
    padding: 20px;
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.05);
  }
  
  .seat-item {
    padding: 10px 0;
    text-align: center;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    user-select: none;
    transition: 0.2s;
    border: 2px solid #e2e8f0;
    font-size: 14px;
  }
  
  .seat-available {
    background: #ecf0f1;
    color: #34495e;
  }
  
  .seat-selected {
    background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
    color: #fff;
    border-color: #0abde3;
  }
  
  .seat-disabled {
    background: #bdc3c7;
    color: #7f8c8d;
    cursor: not-allowed;
  }
  
  .seat-item:hover:not(.seat-disabled) {
    transform: scale(1.05);
  }
  
  .selected-summary {
    text-align: center;
    margin: 20px 0;
    font-weight: 600;
    color: #2c3e50;
    font-size: 16px;
  }
  
  .btn-confirm {
    display: block;
    margin: 0 auto;
    padding: 12px 24px;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    cursor: pointer;
    transition: 0.3s;
    box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
  }
  
  .btn-confirm:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  .btn-confirm:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(16, 185, 129, 0.4);
  }
  </style>
  