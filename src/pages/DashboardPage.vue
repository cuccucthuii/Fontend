<template>
  <div class="dashboard-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">📈</span>
          Thống Kê Doanh Thu
        </h1>
        <p class="page-subtitle">Tổng quan doanh thu, vé bán, rạp và phim</p>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div>
          <div class="stat-number">{{ formatCurrency(totalRevenue) }}</div>
          <div class="stat-label">Tổng doanh thu</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div>
          <div class="stat-number">{{ formatCurrency(monthlyRevenue) }}</div>
          <div class="stat-label">Doanh thu tháng {{ month }}/{{ year }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🏢</div>
        <div>
          <div class="stat-number">{{ theaters.length }}</div>
          <div class="stat-label">Số rạp</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎬</div>
        <div>
          <div class="stat-number">{{ movies.length }}</div>
          <div class="stat-label">Số phim</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎟️</div>
        <div>
          <div class="stat-number">{{ totalTickets }}</div>
          <div class="stat-label">Tổng vé bán</div>
        </div>
      </div>
    </div>

    <!-- Biểu đồ doanh thu 12 tháng -->
    <div class="chart-section">
      <h2>📊 Doanh thu 12 tháng gần nhất</h2>
      <canvas ref="revenueLineChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ doanh thu theo rạp -->
    <div class="chart-section">
      <h2>🏢 Doanh thu theo Rạp Chiếu</h2>
      <canvas ref="theaterBarChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ doanh thu top 5 phim -->
    <div class="chart-section">
      <h2>🎬 Top 5 Phim Doanh Thu Cao Nhất</h2>
      <canvas ref="movieBarChart" height="80"></canvas>
    </div>

    <!-- Nút hiện/ẩn bảng + Xuất Excel -->
    <div class="toggle-table-btn">
      <button class="btn btn-toggle-table" @click="showTables = !showTables">
        {{ showTables ? 'Ẩn bảng chi tiết' : 'Hiện bảng chi tiết' }}
      </button>
      <button v-if="showTables" class="btn btn-export-excel" @click="exportExcel" :disabled="exportingExcel">
        <span v-if="exportingExcel" class="loading-spinner-btn"></span>
        <span v-else>📥 Xuất Excel</span>
      </button>
    </div>

    <!-- Filter by month -->
    <div class="filter-section">
      <div class="filter-form">
        <label>Tháng:
          <input type="number" v-model="month" min="1" max="12" />
        </label>
        <label>Năm:
          <input type="number" v-model="year" min="2000" max="2100" />
        </label>
        <button class="btn btn-primary" @click="fetchMonthlyRevenue" :disabled="loading.month">
          <span v-if="loading.month" class="loading-spinner-btn"></span>
          Tra cứu
        </button>
      </div>
      <div v-if="loading.month" class="loading-state">Đang tải...</div>
      <div v-else-if="monthlyRevenue !== null" class="revenue-amount">
        👉 Doanh thu tháng {{ month }}/{{ year }}: <span class="highlight">{{ formatCurrency(monthlyRevenue) }}</span>
      </div>
    </div>

    <!-- Doanh thu theo Rạp -->
    <div class="table-section" v-if="showTables">
      <h2>🔹 Doanh thu theo Rạp Chiếu</h2>
      <table v-if="theaters.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Rạp Chiếu</th>
            <th>Số Vé Bán</th>
            <th>Tổng Tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(theater, index) in theaters" :key="index">
            <td>{{ index + 1 }}</td>
            <td>{{ theater.tenRapChieu }}</td>
            <td><span class="badge badge-ticket">{{ theater.soVeBan }}</span></td>
            <td><span class="badge badge-money">{{ formatCurrency(theater.tongTien) }}</span></td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải hoặc không có dữ liệu.</p>
    </div>

    <!-- Doanh thu theo Phim -->
    <div class="table-section" v-if="showTables">
      <h2>🔹 Doanh thu theo Phim</h2>
      <table v-if="movies.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Phim</th>
            <th>Số Vé Bán</th>
            <th>Tổng Doanh Thu</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(movie, index) in movies" :key="index">
            <td>{{ index + 1 }}</td>
            <td>{{ movie.tenPhim }}</td>
            <td><span class="badge badge-ticket">{{ movie.soVeBan }}</span></td>
            <td><span class="badge badge-money">{{ formatCurrency(movie.tongDoanhThu) }}</span></td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải hoặc không có dữ liệu.</p>
    </div>

    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import {
  fetchTotalRevenue as fetchTotalRevenueApi,
  fetchMonthlyRevenue as fetchMonthlyRevenueApi,
  fetchTheaterStatistics as fetchTheaterStatisticsApi,
  fetchMovieStatistics as fetchMovieStatisticsApi
} from '../services/statisticsService'
import Chart from 'chart.js/auto'
import * as XLSX from 'xlsx'

// State
const totalRevenue = ref(0)
const monthlyRevenue = ref(null)
const month = ref(new Date().getMonth() + 1)
const year = ref(new Date().getFullYear())
const theaters = ref([])
const movies = ref([])
const loading = ref({
  total: true,
  month: false,
  theater: true,
  movie: true
})
const showTables = ref(false)
const exportingExcel = ref(false)
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null

const totalTickets = computed(() => {
  // Tổng số vé bán từ tất cả rạp
  return theaters.value.reduce((sum, t) => sum + (t.soVeBan || 0), 0)
})

// Chart refs
const revenueLineChart = ref(null)
const theaterBarChart = ref(null)
const movieBarChart = ref(null)
let revenueLineChartInstance = null
let theaterBarChartInstance = null
let movieBarChartInstance = null

// Dummy data for 12 months revenue (replace with real API if available)
const last12Months = computed(() => {
  const now = new Date()
  const arr = []
  for (let i = 11; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    arr.push({
      label: `${d.getMonth() + 1}/${d.getFullYear()}`,
      value: Math.floor(Math.random() * 1000000000) + 100000000 // Dummy
    })
  }
  return arr
})

// Methods
function formatCurrency(amount) {
  if (amount == null) return '-'
  return amount.toLocaleString('vi-VN') + ' ₫'
}

async function fetchTotalRevenue() {
  loading.value.total = true
  try {
    const res = await fetchTotalRevenueApi()
    totalRevenue.value = res.data.tongDoanhThu
  } catch (e) {
    console.error('❌ Lỗi tổng doanh thu:', e)
  } finally {
    loading.value.total = false
  }
}

async function fetchMonthlyRevenue() {
  loading.value.month = true
  try {
    const res = await fetchMonthlyRevenueApi(
      String(month.value).padStart(2, '0'),
      year.value
    )
    monthlyRevenue.value = res.data
  } catch (e) {
    console.error('❌ Lỗi doanh thu theo tháng:', e)
  } finally {
    loading.value.month = false
  }
}

async function fetchTheaterStatistics() {
  loading.value.theater = true
  try {
    const res = await fetchTheaterStatisticsApi()
    theaters.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi theo rạp:', e)
  } finally {
    loading.value.theater = false
  }
}

async function fetchMovieStatistics() {
  loading.value.movie = true
  try {
    const res = await fetchMovieStatisticsApi()
    movies.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi theo phim:', e)
  } finally {
    loading.value.movie = false
  }
}

function renderRevenueLineChart() {
  if (revenueLineChartInstance) revenueLineChartInstance.destroy()
  const ctx = revenueLineChart.value.getContext('2d')
  revenueLineChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: last12Months.value.map(m => m.label),
      datasets: [{
        label: 'Doanh thu',
        data: last12Months.value.map(m => m.value),
        borderColor: '#667eea',
        backgroundColor: 'rgba(102,126,234,0.1)',
        fill: true,
        tension: 0.4,
        pointRadius: 4,
        pointBackgroundColor: '#667eea',
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: { callbacks: { label: ctx => formatCurrency(ctx.parsed.y) } }
      },
      scales: {
        y: { ticks: { callback: v => v.toLocaleString('vi-VN') + ' ₫' } }
      }
    }
  })
}

function renderTheaterBarChart() {
  if (theaterBarChartInstance) theaterBarChartInstance.destroy()
  const ctx = theaterBarChart.value.getContext('2d')
  theaterBarChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: theaters.value.map(t => t.tenRapChieu),
      datasets: [{
        label: 'Doanh thu',
        data: theaters.value.map(t => t.tongTien),
        backgroundColor: '#48dbfb',
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: { callbacks: { label: ctx => formatCurrency(ctx.parsed.y) } }
      },
      scales: {
        y: { ticks: { callback: v => v.toLocaleString('vi-VN') + ' ₫' } }
      }
    }
  })
}

function renderMovieBarChart() {
  if (movieBarChartInstance) movieBarChartInstance.destroy()
  const ctx = movieBarChart.value.getContext('2d')
  const top5 = [...movies.value].sort((a, b) => b.tongDoanhThu - a.tongDoanhThu).slice(0, 5)
  movieBarChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: top5.map(m => m.tenPhim),
      datasets: [{
        label: 'Doanh thu',
        data: top5.map(m => m.tongDoanhThu),
        backgroundColor: '#f59e42',
        borderRadius: 8
      }]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: { callbacks: { label: ctx => formatCurrency(ctx.parsed.x) } }
      },
      scales: {
        x: { ticks: { callback: v => v.toLocaleString('vi-VN') + ' ₫' } }
      }
    }
  })
}

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => { toast.value.show = false }, 2500)
}

function exportExcel() {
  exportingExcel.value = true
  setTimeout(() => {
    try {
      // Sheet 1: Doanh thu theo rạp
      const theaterSheet = [
        ['STT', 'Tên Rạp Chiếu', 'Số Vé Bán', 'Tổng Tiền'],
        ...theaters.value.map((t, i) => [i + 1, t.tenRapChieu, t.soVeBan, t.tongTien])
      ]
      // Sheet 2: Doanh thu theo phim
      const movieSheet = [
        ['STT', 'Tên Phim', 'Số Vé Bán', 'Tổng Doanh Thu'],
        ...movies.value.map((m, i) => [i + 1, m.tenPhim, m.soVeBan, m.tongDoanhThu])
      ]
      const wb = XLSX.utils.book_new()
      const ws1 = XLSX.utils.aoa_to_sheet(theaterSheet)
      const ws2 = XLSX.utils.aoa_to_sheet(movieSheet)
      XLSX.utils.book_append_sheet(wb, ws1, 'Doanh thu theo rạp')
      XLSX.utils.book_append_sheet(wb, ws2, 'Doanh thu theo phim')
      XLSX.writeFile(wb, `ThongKeDoanhThu_${new Date().toISOString().slice(0,10)}.xlsx`)
      showToast('Xuất Excel thành công!', 'success')
    } catch (e) {
      showToast('Xuất Excel thất bại!', 'error')
    } finally {
      exportingExcel.value = false
    }
  }, 600)
}

// On mount
onMounted(() => {
  fetchTotalRevenue()
  fetchMonthlyRevenue()
  fetchTheaterStatistics()
  fetchMovieStatistics()
  setTimeout(() => {
    renderRevenueLineChart()
    renderTheaterBarChart()
    renderMovieBarChart()
  }, 800)
})

watch([theaters], () => {
  setTimeout(() => renderTheaterBarChart(), 200)
})
watch([movies], () => {
  setTimeout(() => renderMovieBarChart(), 200)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
.dashboard-page {
  font-family: 'Roboto', Arial, sans-serif;
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
  backdrop-filter: blur(10px);
}
.header-content { flex: 1; }
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
.header-icon { font-size: 32px; }
.page-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 400;
}
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}
.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}
.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}
.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}
.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-top: 4px;
}
.filter-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  padding: 24px 32px;
  margin-bottom: 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-start;
}
.filter-form {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 10px;
}
.filter-form input {
  width: 80px;
  padding: 8px 12px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  font-size: 15px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}
.filter-form input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}
.btn.btn-primary {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.btn.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.loading-spinner-btn {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 3px solid #ecf0f1;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 6px;
  vertical-align: middle;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.revenue-amount {
  font-size: 20px;
  font-weight: 600;
  color: #374151;
  margin-top: 8px;
}
.revenue-amount .highlight {
  color: #f59e42;
  font-size: 22px;
  font-weight: bold;
}
.table-section {
  margin-top: 30px;
  overflow-x: auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  padding: 24px 32px;
  margin-bottom: 32px;
}
table {
  width: 100%;
  border-collapse: collapse;
  background-color: transparent;
}
th, td {
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  text-align: left;
  font-size: 15px;
}
th {
  background-color: #f8f9fa;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}
tr:hover {
  background-color: #f1f5f9;
}
.badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  margin-right: 4px;
}
.badge-ticket {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: white;
}
.badge-money {
  background: linear-gradient(135deg, #f59e42 0%, #fbbf24 100%);
  color: #fff;
}
.loading-state {
  text-align: center;
  padding: 40px 0;
  color: #7f8c8d;
  font-size: 16px;
}
.chart-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  padding: 24px 32px;
  margin-bottom: 32px;
  margin-top: 24px;
}
.toggle-table-btn {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
.btn-toggle-table {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-toggle-table:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
}
.btn-export-excel {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-left: 12px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-export-excel:hover {
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  transform: translateY(-2px);
}
.toast {
  position: fixed;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 220px;
  background: #fff;
  color: #2c3e50;
  padding: 16px 32px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(44,62,80,0.18);
  font-weight: 600;
  font-size: 15px;
  z-index: 2000;
  animation: popIn 0.3s;
}
.toast.success { border-left: 6px solid #10b981; }
.toast.error { border-left: 6px solid #ff5252; }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity 0.3s; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; }
@media (max-width: 900px) {
  .page-header, .stats-section, .filter-section, .table-section {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }
  .stats-section {
    grid-template-columns: 1fr;
  }
}
</style>
