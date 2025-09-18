<template>
  <div class="demo-page">
    <div class="demo-container">
      <h1>🎬 Demo Modal Lịch Chiếu</h1>
      <p>Trang demo để test modal lịch chiếu phim</p>
      
      <div class="demo-controls">
        <button @click="openModal" class="demo-btn">
          📅 Mở Modal Lịch Chiếu
        </button>
        
        <div class="demo-info" v-if="selectedShowtime">
          <h3>Thông tin đã chọn:</h3>
          <p><strong>Phim:</strong> {{ selectedShowtime.movie?.tenPhim }}</p>
          <p><strong>Rạp:</strong> {{ selectedShowtime.cinema }}</p>
          <p><strong>Ngày:</strong> {{ selectedShowtime.date }}</p>
          <p><strong>Giờ:</strong> {{ selectedShowtime.time }}</p>
          <p><strong>Ghế trống:</strong> {{ selectedShowtime.availableSeats }}/{{ selectedShowtime.totalSeats }}</p>
        </div>
      </div>
    </div>

    <!-- Showtime Modal -->
    <ShowtimeModal
      :visible="showModal"
      :movie="demoMovie"
      cinema="Rạp Beta Thanh Xuân"
      @close="showModal = false"
      @selectShowtime="handleShowtimeSelection"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ShowtimeModal from '../components/ShowtimeModal.vue'

const showModal = ref(false)
const selectedShowtime = ref(null)

// Demo movie data
const demoMovie = ref({
  idPhim: 1,
  tenPhim: 'Thanh Gươm Diệt Quỷ: Vô Hạn Thành',
  title: 'Thanh Gươm Diệt Quỷ: Vô Hạn Thành',
  posterUrl: 'https://example.com/poster.jpg'
})

function openModal() {
  showModal.value = true
}

function handleShowtimeSelection(showtimeData) {
  selectedShowtime.value = showtimeData
  console.log('Selected showtime:', showtimeData)
}
</script>

<style scoped>
.demo-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.demo-container {
  background: white;
  border-radius: 16px;
  padding: 40px;
  max-width: 600px;
  width: 100%;
  text-align: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.demo-container h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.demo-container p {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.demo-controls {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.demo-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 15px 30px;
  color: white;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.demo-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.demo-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  text-align: left;
  max-width: 400px;
  width: 100%;
}

.demo-info h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 18px;
}

.demo-info p {
  color: #555;
  margin-bottom: 8px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .demo-container {
    padding: 20px;
  }
  
  .demo-container h1 {
    font-size: 24px;
  }
  
  .demo-btn {
    padding: 12px 24px;
    font-size: 14px;
  }
}
</style>
