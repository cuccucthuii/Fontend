<template>
  <div class="payment-page">
    <div class="page-header">
      <h1>💰 Thanh Toán (Giả lập)</h1>
      <p>Xác nhận thông tin vé và thanh toán tại quầy.</p>
    </div>

    <div v-if="loaded" class="ticket-summary">
      <h2>🎬 Thông Tin Vé</h2>
      <p><strong>Phim:</strong> {{ schedule.tenPhim }}</p>
      <p><strong>Phòng:</strong> {{ schedule.tenPhongChieu }}</p>
      <p><strong>Ngày chiếu:</strong> {{ schedule.ngayChieu }}</p>
      <p><strong>Giờ:</strong> {{ schedule.gioChieu }}</p>
      <p><strong>Ghế:</strong> {{ seatNames }}</p>
      <p><strong>Tổng tiền:</strong> {{ totalPrice }} VND</p>
    </div>

    <p v-else>🔄 Đang tải thông tin...</p>

    <button
      class="btn-confirm"
      :disabled="!loaded || saving"
      @click="confirmPayment"
    >
      ✅ Xác nhận thanh toán
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchScheduleById } from '../../services/posService'

const route = useRoute()
const router = useRouter()

const scheduleId = route.query.scheduleId
const seatIds = route.query.seatIds?.split(',') || []

const schedule = ref(null)
const loaded = ref(false)
const saving = ref(false)

const SEAT_PRICE = 70000

const totalPrice = computed(() => seatIds.length * SEAT_PRICE)
const seatNames = computed(() => seatIds.map(id => `G${id}`).join(', '))

onMounted(async () => {
  try {
    const res = await fetchScheduleById(scheduleId)
    schedule.value = res.data
    loaded.value = true
  } catch (err) {
    console.error('❌ Error loading schedule:', err)
  }
})

async function confirmPayment() {
  if (!scheduleId || !seatIds.length) {
    alert('Thiếu thông tin vé!')
    return
  }

  saving.value = true
  console.log('✅ BẮT ĐẦU GIẢ LẬP THANH TOÁN...')

  // 🔥 Giả lập loading 2 giây
  await new Promise(resolve => setTimeout(resolve, 2000))

  alert('✅ Thanh toán thành công (giả lập)!')
  router.push({ name: 'POSHome' })
  saving.value = false
}
</script>

<style scoped>
.payment-page {
  padding: 32px;
  max-width: 600px;
  margin: 0 auto;
  background: #f8fafc;
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(0,0,0,0.05);
  font-family: Arial, sans-serif;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #059669;
  font-weight: 700;
}

.page-header p {
  color: #64748b;
  font-size: 16px;
  margin-top: 8px;
}

.ticket-summary {
  background: #ffffff;
  color: #2c3e50;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  font-size: 16px;
  line-height: 1.6;
}

.ticket-summary h2 {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #10b981;
}

.ticket-summary p {
  margin: 6px 0;
  font-size: 16px;
  color: #374151;
}

.btn-confirm {
  display: block;
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

<!-- hungall -->
