<template>
  <div class="success-container">
    <div class="success-content">
      <div class="success-icon">
        <div class="checkmark">✓</div>
      </div>
      
      <h1>Thanh toán thành công!</h1>
      <p class="success-message">
        Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. 
        Vé điện tử đã được gửi về email của bạn.
      </p>
      
      <div class="transaction-info">
        <div class="info-item">
          <span class="label">Mã giao dịch:</span>
          <span class="value">{{ transactionId }}</span>
        </div>
        <div class="info-item">
          <span class="label">Số hóa đơn:</span>
          <span class="value">{{ invoiceNumber }}</span>
        </div>
        <div class="info-item">
          <span class="label">Số tiền:</span>
          <span class="value">{{ formatCurrency(amount) }}</span>
        </div>
        <div class="info-item">
          <span class="label">Thời gian:</span>
          <span class="value">{{ currentDateTime }}</span>
        </div>
      </div>
      
      <div class="success-actions">
        <button class="btn-home" @click="goHome">
          🏠 Về trang chủ
        </button>
        <button class="btn-booking" @click="bookAnother">
          🎬 Đặt vé khác
        </button>
      </div>
      
      <div class="success-note">
        <p>
          💌 Vé điện tử và thông tin chi tiết đã được gửi về email của bạn.
        </p>
        <p>
          🎟️ Vui lòng mang theo giấy tờ tùy thân khi đến rạp.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const transactionId = ref('')
const invoiceNumber = ref('')
const amount = ref(0)
const currentDateTime = ref('')

function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

function generateTransactionId() {
  const timestamp = Date.now().toString()
  const random = Math.random().toString(36).substr(2, 5).toUpperCase()
  return `TXN${timestamp.slice(-6)}${random}`
}

function goHome() {
  // Clear booking data
  localStorage.removeItem('bookingData')
  router.push({ name: 'Home' })
}

function bookAnother() {
  // Clear booking data
  localStorage.removeItem('bookingData')
  router.push({ name: 'Booking' })
}

onMounted(() => {
  // Get data from query params
  invoiceNumber.value = route.query.invoice || 'N/A'
  amount.value = parseInt(route.query.amount) || 0
  
  // Generate transaction ID
  transactionId.value = generateTransactionId()
  
  // Current date time
  const now = new Date()
  currentDateTime.value = now.toLocaleString('vi-VN')
})
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.success-content {
  background: white;
  padding: 50px;
  border-radius: 20px;
  text-align: center;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.success-icon {
  margin-bottom: 30px;
}

.checkmark {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
  font-size: 40px;
  font-weight: bold;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  animation: checkmarkPulse 2s ease-in-out infinite;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.3);
}

@keyframes checkmarkPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

h1 {
  color: #2c3e50;
  font-size: 32px;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.success-message {
  color: #7f8c8d;
  font-size: 18px;
  line-height: 1.6;
  margin-bottom: 40px;
}

.transaction-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}

.info-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.label {
  color: #6c757d;
  font-weight: 500;
}

.value {
  color: #2c3e50;
  font-weight: 700;
}

.success-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 40px;
}

.btn-home,
.btn-booking {
  padding: 15px 30px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 150px;
}

.btn-home {
  background: linear-gradient(135deg, #95a5a6, #7f8c8d);
  color: white;
}

.btn-home:hover {
  background: linear-gradient(135deg, #7f8c8d, #6c757d);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(127, 140, 141, 0.3);
}

.btn-booking {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.btn-booking:hover {
  background: linear-gradient(135deg, #5a6fd8, #6a4190);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.success-note {
  background: #e8f4f8;
  border: 2px solid #b8daff;
  border-radius: 10px;
  padding: 20px;
}

.success-note p {
  color: #004085;
  margin: 8px 0;
  font-size: 14px;
  line-height: 1.5;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .success-container {
    padding: 15px;
  }
  
  .success-content {
    padding: 30px 20px;
  }
  
  .checkmark {
    width: 60px;
    height: 60px;
    font-size: 30px;
  }
  
  h1 {
    font-size: 24px;
  }
  
  .success-message {
    font-size: 16px;
  }
  
  .success-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .btn-home,
  .btn-booking {
    width: 100%;
  }
  
  .info-item {
    flex-direction: column;
    text-align: center;
    gap: 5px;
  }
}
</style>