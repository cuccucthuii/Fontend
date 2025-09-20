<template>
  <div class="seatmap-wrapper">
    <!-- Header thông tin suất chiếu -->
    <div class="seatmap-header">
      <div class="info-block">
        <div><strong>Phòng:</strong> {{ roomName }}</div>
        <div><strong>Giờ chiếu:</strong> {{ showTime }}</div>
      </div>
      <div class="info-block">
        <div><strong>Ngày:</strong> {{ showDate }}</div>
        <div><strong>Thời lượng:</strong> {{ duration }}</div>
      </div>
    </div>
    <!-- Legend màu ghế -->
    <div class="seatmap-legend">
      <div class="legend-item"><SeatIcon type="THUONG" status="available" /> <span>Ghế thường</span></div>
      <div class="legend-item"><SeatIcon type="VIP" status="available" /> <span>Ghế VIP</span></div>
      <div class="legend-item"><SeatIcon type="DOI" status="available" /> <span>Ghế đôi</span></div>
      <div class="legend-item"><SeatIcon type="THUONG" status="selected" /> <span>Đang chọn</span></div>
      <div class="legend-item"><SeatIcon type="THUONG" status="booked" /> <span>Đã đặt</span></div>
    </div>
    <!-- Grid ghế -->
    <div class="seatmap-grid">
      <div
        v-for="seat in seats"
        :key="seat.idGheNgoi"
        :class="['seatmap-item', seatClass(seat)]"
        @click="toggleSeat(seat)"
        :title="seatTooltip(seat)"
      >
        <SeatIcon :type="seat.loaiGhe" :status="seatStatus(seat)" />
        <span class="seatmap-label">{{ seat.hangGhe }}{{ seat.soGhe }}</span>
      </div>
    </div>
    <!-- Thông tin chọn ghế và tổng tiền -->
    <div class="seatmap-summary">
      <div>Ghế đã chọn: <span class="selected-list">{{ selectedSeatsNames }}</span></div>
      <div>Tổng tiền: <span class="total-price">{{ totalPrice | currency }}</span></div>
    </div>
    <!-- Nút thao tác -->
    <div class="seatmap-actions">
      <button class="btn-back" @click="$emit('back')">Quay lại</button>
      <button class="btn-confirm" :disabled="!selectedSeats.length" @click="confirmSeats">Thanh toán</button>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import SeatIcon from './SeatIcon.vue'
const props = defineProps({
  seats: Array,
  roomName: String,
  showTime: String,
  showDate: String,
  duration: String,
  priceMap: Object // { THUONG: 70000, VIP: 120000, DOI: 150000 }
})
const emit = defineEmits(['confirm', 'back'])
const selectedSeats = ref([])
function seatClass(seat) {
  if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') return 'seatmap-disabled'
  if (selectedSeats.value.includes(seat.idGheNgoi)) return 'seatmap-selected'
  return 'seatmap-available'
}
function seatStatus(seat) {
  if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') return 'booked'
  if (selectedSeats.value.includes(seat.idGheNgoi)) return 'selected'
  return 'available'
}
function seatTooltip(seat) {
  if (seat.trangThai === 'DA_DAT' || seat.trangThai === 'DANG_SU_DUNG') return `Ghế ${seat.hangGhe}${seat.soGhe} - Đã đặt`
  if (selectedSeats.value.includes(seat.idGheNgoi)) return `Ghế ${seat.hangGhe}${seat.soGhe} - Đang chọn`
  if (seat.loaiGhe === 'VIP') return `Ghế ${seat.hangGhe}${seat.soGhe} - VIP`
  if (seat.loaiGhe === 'DOI') return `Ghế ${seat.hangGhe}${seat.soGhe} - Ghế đôi`
  return `Ghế ${seat.hangGhe}${seat.soGhe} - Thường`
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
  emit('confirm', selectedSeats.value)
}
const selectedSeatsNames = computed(() =>
  props.seats
    .filter(seat => selectedSeats.value.includes(seat.idGheNgoi))
    .map(seat => `${seat.hangGhe}${seat.soGhe}`)
    .join(', ')
)
const totalPrice = computed(() =>
  props.seats
    .filter(seat => selectedSeats.value.includes(seat.idGheNgoi))
    .reduce((sum, seat) => sum + (props.priceMap?.[seat.loaiGhe] || 0), 0)
)
</script>
<script>
export default {
  filters: {
    currency(val) {
      if (!val) return '0đ'
      return val.toLocaleString('vi-VN') + 'đ'
    }
  }
}
</script>
<style scoped>
.seatmap-wrapper {
  background: #181a20;
  color: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  padding: 32px 24px 24px 24px;
  max-width: 700px;
  margin: 32px auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.seatmap-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #ffb347 0%, #ffcc33 100%);
  color: #222;
  border-radius: 12px;
  padding: 18px 28px;
  margin-bottom: 18px;
  font-size: 17px;
  font-weight: 600;
}
.info-block { display: flex; flex-direction: column; gap: 2px; }
.seatmap-legend {
  display: flex;
  gap: 28px;
  justify-content: center;
  align-items: center;
  margin: 18px 0 8px 0;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #eee;
  font-weight: 500;
}
.seatmap-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(48px, 1fr));
  gap: 14px;
  justify-items: center;
  margin: 24px 0 18px 0;
  background: #23242a;
  border-radius: 14px;
  padding: 32px 18px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.12);
}
.seatmap-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
  position: relative;
}
.seatmap-item:hover {
  transform: translateY(-4px) scale(1.08);
  z-index: 2;
}
.seatmap-label {
  margin-top: 4px;
  font-size: 13px;
  color: #bbb;
  font-weight: 500;
}
.seatmap-item.seatmap-disabled {
  pointer-events: none;
  opacity: 0.4;
}
.seatmap-item.seatmap-selected {
  filter: drop-shadow(0 0 8px #4f8cff);
}
.seatmap-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 18px 0 10px 0;
  font-size: 16px;
  font-weight: 500;
}
.selected-list { color: #4f8cff; }
.total-price { color: #ffd600; font-weight: 700; }
.seatmap-actions {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
  margin-top: 10px;
}
.btn-back, .btn-confirm {
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.btn-back {
  background: #333;
  color: #fff;
}
.btn-confirm {
  background: linear-gradient(90deg, #ffb347 0%, #ffcc33 100%);
  color: #222;
}
.btn-confirm:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 