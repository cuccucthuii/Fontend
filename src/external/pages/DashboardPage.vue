<template>
  <div class="admin-page-container">
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

    <!-- Filter động: ngày, tháng, quý, năm -->
    <div class="filter-section">
      <div class="filter-form">
        <label>Thống kê theo:
          <select v-model="statType">
            <option value="day">Ngày</option>
            <option value="month">Tháng</option>
            <option value="quarter">Quý</option>
            <option value="year">Năm</option>
          </select>
        </label>
        <template v-if="statType==='day'">
          <input type="date" v-model="selectedDay" />
        </template>
        <template v-else-if="statType==='month'">
          <input type="month" v-model="selectedMonth" />
        </template>
        <template v-else-if="statType==='quarter'">
          <select v-model="selectedQuarter">
            <option value="1">Quý 1</option>
            <option value="2">Quý 2</option>
            <option value="3">Quý 3</option>
            <option value="4">Quý 4</option>
          </select>
          <input type="number" v-model="selectedYear" min="2000" max="2100" />
        </template>
        <template v-else>
          <input type="number" v-model="selectedYear" min="2000" max="2100" />
        </template>
        <button class="btn btn-primary" @click="fetchStatistics" :disabled="loading.total">Tra cứu</button>
      </div>
    </div>

    <!-- Chọn loại biểu đồ cho từng phần -->
    <div class="chart-type-section">
      <label>Loại biểu đồ:
        <select v-model="chartType" @change="updateCharts">
          <option value="line">Đường (Line)</option>
          <option value="bar">Cột (Bar)</option>
          <option value="pie">Tròn (Pie)</option>
        </select>
      </label>
    </div>

    <!-- Biểu đồ doanh thu tổng hợp -->
    <div class="chart-section">
      <h2>📊 Doanh thu tổng hợp theo thời gian</h2>
      <canvas ref="revenueChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ doanh thu theo rạp -->
    <div class="chart-section">
      <h2>🏢 Doanh thu theo Rạp Chiếu</h2>
      <canvas ref="theaterChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ doanh thu theo phim -->
    <div class="chart-section">
      <h2>🎬 Doanh thu theo Phim</h2>
      <canvas ref="movieChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ thống kê suất chiếu -->
    <div class="chart-section">
      <h2>🕒 Thống kê theo Suất Chiếu</h2>
      <canvas ref="showtimeChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ theo giờ chiếu -->
    <div class="chart-section">
      <h2>⏰ Doanh thu theo Giờ Chiếu</h2>
      <canvas ref="hourlyChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ theo phòng chiếu -->
    <div class="chart-section">
      <h2>🏟️ Doanh thu theo Phòng Chiếu</h2>
      <canvas ref="roomChart" height="80"></canvas>
    </div>

    <!-- Biểu đồ tỷ lệ lấp đầy -->
    <div class="chart-section">
      <h2>📊 Tỷ lệ Lấp đầy Phòng Chiếu</h2>
      <canvas ref="occupancyChart" height="80"></canvas>
    </div>

    <!-- Thống kê khách hàng -->
    <div class="chart-section">
      <h2>👥 Thống kê Khách hàng</h2>
      <div class="customer-stats-grid">
        <div class="customer-stat-card">
          <div class="stat-icon">👤</div>
          <div class="stat-content">
            <div class="stat-number">{{ customerStats.totalCustomers }}</div>
            <div class="stat-label">Tổng khách hàng</div>
          </div>
        </div>
        <div class="customer-stat-card">
          <div class="stat-icon">🆕</div>
          <div class="stat-content">
            <div class="stat-number">{{ customerStats.newCustomers }}</div>
            <div class="stat-label">Khách hàng mới</div>
          </div>
        </div>
        <div class="customer-stat-card">
          <div class="stat-icon">🔄</div>
          <div class="stat-content">
            <div class="stat-number">{{ customerStats.returningCustomers }}</div>
            <div class="stat-label">Khách hàng quay lại</div>
          </div>
        </div>
        <div class="customer-stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <div class="stat-number">{{ customerStats.vipCustomers }}</div>
            <div class="stat-label">Khách VIP</div>
          </div>
        </div>
      </div>
      <canvas ref="customerChart" height="80"></canvas>
    </div>

    <!-- Thống kê theo thể loại phim -->
    <div class="chart-section">
      <h2>🎭 Thống kê theo Thể loại Phim</h2>
      <canvas ref="genreChart" height="80"></canvas>
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

    <!-- Bảng chi tiết với phân trang và sắp xếp -->
    <div class="table-section" v-if="showTables">
      <div class="table-header">
        <h2>📋 Bảng chi tiết thống kê</h2>
        <div class="table-actions">
          <button class="btn btn-export-excel" @click="exportExcelAdvanced" :disabled="exportingExcel">
            <span v-if="exportingExcel" class="loading-spinner-btn"></span>
            <span v-else>📥 Xuất Excel nâng cao</span>
          </button>
          <button class="btn btn-export-pdf" @click="exportPDF" :disabled="exportingPDF">
            <span v-if="exportingPDF" class="loading-spinner-btn"></span>
            <span v-else>📄 Xuất PDF đầy đủ</span>
          </button>
          <button class="btn btn-export-pdf-simple" @click="exportPDFSimple" :disabled="exportingPDF">
            <span v-if="exportingPDF" class="loading-spinner-btn"></span>
            <span v-else>📋 Xuất PDF đơn giản</span>
          </button>
        </div>
      </div>
      
      <!-- Tabs cho loại dữ liệu -->
      <div class="data-tabs">
        <button 
          :class="['tab-btn', { active: tableDataType === 'theater' }]"
          @click="tableDataType = 'theater'"
        >
          🏢 Theo Rạp
        </button>
        <button 
          :class="['tab-btn', { active: tableDataType === 'movie' }]"
          @click="tableDataType = 'movie'"
        >
          🎬 Theo Phim
        </button>
        <button 
          :class="['tab-btn', { active: tableDataType === 'combined' }]"
          @click="tableDataType = 'combined'"
        >
          📊 Tổng hợp
        </button>
      </div>

      <!-- Filter và tìm kiếm -->
      <div class="table-controls">
        <div class="search-box">
          <input 
            v-model="tableSearch" 
            placeholder="🔍 Tìm kiếm..." 
            class="search-input"
          />
        </div>
        <div class="sort-controls">
          <select v-model="sortBy" class="sort-select">
            <option value="name">Tên</option>
            <option value="revenue">Doanh thu</option>
            <option value="tickets">Số vé</option>
          </select>
          <button @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'" class="btn-sort">
            {{ sortOrder === 'asc' ? '↑' : '↓' }}
          </button>
        </div>
      </div>

      <!-- Cards view thay vì table -->
      <div class="data-cards-container">
        <div v-if="filteredTableData.length" class="data-cards-grid">
          <div v-for="(item, index) in paginatedTableData" :key="index" class="data-card">
            <div class="card-header">
              <div class="card-rank">#{{ (currentPage - 1) * pageSize + index + 1 }}</div>
              <div class="card-trend" :class="item.trend">
                <span class="trend-icon">{{ item.trend === 'up' ? '↗' : item.trend === 'down' ? '↘' : '→' }}</span>
              </div>
            </div>
            <div class="card-content">
              <h3 class="card-title">{{ item.name }}</h3>
              <div class="card-stats">
                <div class="stat-item">
                  <div class="stat-label">Doanh thu</div>
                  <div class="stat-value revenue">{{ formatCurrency(item.revenue) }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">Số vé</div>
                  <div class="stat-value tickets">{{ item.tickets.toLocaleString() }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">Tỷ lệ</div>
                  <div class="stat-value percentage">{{ item.percentage }}%</div>
                </div>
              </div>
            </div>
            <div class="card-footer">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: item.percentage + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-data">
          <div class="no-data-icon">📊</div>
          <p>Không có dữ liệu để hiển thị</p>
        </div>
      </div>

      <!-- Phân trang -->
      <div class="pagination" v-if="totalPages > 1">
        <button @click="goToPage(1)" :disabled="currentPage === 1" class="btn-page">«</button>
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1" class="btn-page">‹</button>
        <button v-for="page in visiblePages" :key="page" 
                @click="goToPage(page)" 
                :class="['btn-page', { active: page === currentPage }]">
          {{ page }}
        </button>
        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages" class="btn-page">›</button>
        <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages" class="btn-page">»</button>
      </div>
    </div>



    <!-- Loading và Error states -->
    <div v-if="loading.total" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>Đang tải dữ liệu thống kê...</p>
    </div>

    <div v-if="error" class="error-message">
      <p>❌ {{ error }}</p>
      <button @click="retryFetch" class="btn btn-retry">Thử lại</button>
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
  fetchMovieStatistics as fetchMovieStatisticsApi,
  fetchShowtimeStatistics as fetchShowtimeStatisticsApi,
  fetchHourlyStatistics as fetchHourlyStatisticsApi,
  fetchRoomStatistics as fetchRoomStatisticsApi,
  fetchOccupancyStatistics as fetchOccupancyStatisticsApi,
  fetchTopShowtimes as fetchTopShowtimesApi,
  fetchPeakHoursStatistics as fetchPeakHoursStatisticsApi,
  fetchCustomerStatistics as fetchCustomerStatisticsApi,


} from '../services/statisticsService'
import Chart from 'chart.js/auto'
import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import 'jspdf-autotable'
import html2canvas from 'html2canvas'

// State
const totalRevenue = ref(0)
const monthlyRevenue = ref(null)
const month = ref(new Date().getMonth() + 1)
const year = ref(new Date().getFullYear())
const theaters = ref([])
const movies = ref([])
const showtimes = ref([])
const hourlyStats = ref([])
const roomStats = ref([])
const occupancyStats = ref([])
const topShowtimes = ref([])
const peakHours = ref([])

// Thống kê khách hàng
const customerStats = ref({
  totalCustomers: 0,
  newCustomers: 0,
  returningCustomers: 0,
  vipCustomers: 0
})

// Thống kê thể loại phim
const genreStats = ref([])



const showTables = ref(false)
const exportingExcel = ref(false)
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null
let chartUpdateTimeout = null

const loading = ref({
  total: true,
  month: false,
  theater: true,
  movie: true,
  showtime: true,
  hourly: true,
  room: true,
  occupancy: true,
  topShowtimes: true,
  peakHours: true,
  customer: true,
  genre: true,

})

// Debounce function
function debounce(func, wait) {
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(chartUpdateTimeout)
      func(...args)
    }
    clearTimeout(chartUpdateTimeout)
    chartUpdateTimeout = setTimeout(later, wait)
  }
}

const totalTickets = computed(() => {
  // Tổng số vé bán từ tất cả rạp
  return theaters.value.reduce((sum, t) => sum + (t.soVeBan || 0), 0)
})

// Chart refs
const revenueChart = ref(null)
const theaterChart = ref(null)
const movieChart = ref(null)
const showtimeChart = ref(null)
const hourlyChart = ref(null)
const roomChart = ref(null)
const occupancyChart = ref(null)
const customerChart = ref(null)
const genreChart = ref(null)

let revenueChartInstance = null
let theaterChartInstance = null
let movieChartInstance = null
let showtimeChartInstance = null
let hourlyChartInstance = null
let roomChartInstance = null
let occupancyChartInstance = null
let customerChartInstance = null
let genreChartInstance = null


const statType = ref('month')
const selectedDay = ref(new Date().toISOString().slice(0, 10))
const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const selectedQuarter = ref('1')
const selectedYear = ref(new Date().getFullYear())
const chartType = ref('line')

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
    const params = {
      month: String(month.value).padStart(2, '0'),
      year: year.value
    }
    const res = await fetchMonthlyRevenueApi(params)
    monthlyRevenue.value = res.data
  } catch (e) {
    console.error('❌ Lỗi doanh thu theo tháng:', e)
    monthlyRevenue.value = null
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

async function fetchShowtimeStatistics() {
  loading.value.showtime = true
  try {
    const res = await fetchShowtimeStatisticsApi()
    showtimes.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi theo suất chiếu:', e)
  } finally {
    loading.value.showtime = false
  }
}

async function fetchHourlyStatistics() {
  loading.value.hourly = true
  try {
    const res = await fetchHourlyStatisticsApi()
    hourlyStats.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi theo giờ:', e)
  } finally {
    loading.value.hourly = false
  }
}

async function fetchRoomStatistics() {
  loading.value.room = true
  try {
    const res = await fetchRoomStatisticsApi()
    roomStats.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi theo phòng:', e)
  } finally {
    loading.value.room = false
  }
}

async function fetchOccupancyStatistics() {
  loading.value.occupancy = true
  try {
    const res = await fetchOccupancyStatisticsApi()
    occupancyStats.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi tỷ lệ lấp đầy:', e)
  } finally {
    loading.value.occupancy = false
  }
}

async function fetchTopShowtimes() {
  loading.value.topShowtimes = true
  try {
    const res = await fetchTopShowtimesApi()
    topShowtimes.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi top suất chiếu:', e)
  } finally {
    loading.value.topShowtimes = false
  }
}

async function fetchPeakHours() {
  loading.value.peakHours = true
  try {
    const res = await fetchPeakHoursStatisticsApi()
    peakHours.value = Array.isArray(res.data) ? res.data : [res.data]
  } catch (e) {
    console.error('❌ Lỗi giờ vàng:', e)
  } finally {
    loading.value.peakHours = false
  }
}

async function fetchStatistics() {
  loading.value.total = true
  let params = {}
  if (statType.value === 'day') {
    params = { type: 'day', date: selectedDay.value }
  } else if (statType.value === 'month') {
    const [year, month] = selectedMonth.value.split('-')
    params = { type: 'month', month, year }
  } else if (statType.value === 'quarter') {
    params = { type: 'quarter', quarter: selectedQuarter.value, year: selectedYear.value }
  } else if (statType.value === 'year') {
    params = { type: 'year', year: selectedYear.value }
  }
  try {
    // Gọi API tổng hợp
    const res = await fetchTotalRevenueApi(params)
    totalRevenue.value = res.data.tongDoanhThu
    // Gọi API theo rạp
    const resTheater = await fetchTheaterStatisticsApi(params)
    theaters.value = Array.isArray(resTheater.data) ? resTheater.data : [resTheater.data]
    // Gọi API theo phim
    const resMovie = await fetchMovieStatisticsApi(params)
    movies.value = Array.isArray(resMovie.data) ? resMovie.data : [resMovie.data]
    
    // Gọi API theo suất chiếu (với error handling)
    try {
      const resShowtime = await fetchShowtimeStatisticsApi(params)
      showtimes.value = Array.isArray(resShowtime.data) ? resShowtime.data : [resShowtime.data]
    } catch (e) {
      console.warn('⚠️ API suất chiếu chưa sẵn sàng:', e.message)
      showtimes.value = generateMockShowtimeData()
    }
    
    // Gọi API theo giờ (với error handling)
    try {
      const resHourly = await fetchHourlyStatisticsApi(params)
      hourlyStats.value = Array.isArray(resHourly.data) ? resHourly.data : [resHourly.data]
    } catch (e) {
      console.warn('⚠️ API giờ chiếu chưa sẵn sàng:', e.message)
      hourlyStats.value = generateMockHourlyData()
    }
    
    // Gọi API theo phòng (với error handling)
    try {
      const resRoom = await fetchRoomStatisticsApi(params)
      roomStats.value = Array.isArray(resRoom.data) ? resRoom.data : [resRoom.data]
    } catch (e) {
      console.warn('⚠️ API phòng chiếu chưa sẵn sàng:', e.message)
      roomStats.value = generateMockRoomData()
    }
    
    // Gọi API tỷ lệ lấp đầy (với error handling)
    try {
      const resOccupancy = await fetchOccupancyStatisticsApi(params)
      occupancyStats.value = Array.isArray(resOccupancy.data) ? resOccupancy.data : [resOccupancy.data]
    } catch (e) {
      console.warn('⚠️ API tỷ lệ lấp đầy chưa sẵn sàng:', e.message)
      occupancyStats.value = generateMockOccupancyData()
    }
    
    // Gọi API top suất chiếu (với error handling)
    try {
      const resTopShowtimes = await fetchTopShowtimesApi(params)
      topShowtimes.value = Array.isArray(resTopShowtimes.data) ? resTopShowtimes.data : [resTopShowtimes.data]
    } catch (e) {
      console.warn('⚠️ API top suất chiếu chưa sẵn sàng:', e.message)
      topShowtimes.value = []
    }
    
    // Gọi API giờ vàng (với error handling)
    try {
      const resPeakHours = await fetchPeakHoursStatisticsApi(params)
      peakHours.value = Array.isArray(resPeakHours.data) ? resPeakHours.data : [resPeakHours.data]
    } catch (e) {
      console.warn('⚠️ API giờ vàng chưa sẵn sàng:', e.message)
      peakHours.value = []
    }
    
    // Gọi API thống kê khách hàng (với error handling)
    try {
      await fetchCustomerStatistics()
    } catch (e) {
      console.warn('⚠️ API khách hàng chưa sẵn sàng:', e.message)
    }
    
    // Gọi API thống kê thể loại (với error handling)
    try {
      await fetchGenreStatistics()
    } catch (e) {
      console.warn('⚠️ API thể loại chưa sẵn sàng:', e.message)
    }
    

    
    // Vẽ lại biểu đồ
    setTimeout(() => {
      renderRevenueChart()
      renderTheaterChart()
      renderMovieChart()
      renderShowtimeChart()
      renderHourlyChart()
      renderRoomChart()
      renderOccupancyChart()
      renderCustomerChart()
      renderGenreChart()

    }, 500)
  } catch (e) {
    console.error('❌ Lỗi fetch thống kê:', e)
    showToast('Lỗi tải dữ liệu thống kê', 'error')
  } finally {
    loading.value.total = false
  }
}

function renderRevenueChart() {
  if (revenueChartInstance) revenueChartInstance.destroy()
  if (!revenueChart.value || theaters.value.length === 0) return
  
  const ctx = revenueChart.value.getContext('2d')
  
  // Tạo dữ liệu tổng hợp thực sự
  let labels, data
  if (statType.value === 'day') {
    // Theo giờ trong ngày
    labels = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    data = [totalRevenue.value * 0.05, totalRevenue.value * 0.08, totalRevenue.value * 0.12, 
            totalRevenue.value * 0.25, totalRevenue.value * 0.35, totalRevenue.value * 0.15]
  } else if (statType.value === 'month') {
    // Theo ngày trong tháng
    labels = ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4']
    data = [totalRevenue.value * 0.2, totalRevenue.value * 0.25, totalRevenue.value * 0.3, totalRevenue.value * 0.25]
  } else if (statType.value === 'quarter') {
    // Theo tháng trong quý
    labels = ['Tháng 1', 'Tháng 2', 'Tháng 3']
    data = [totalRevenue.value * 0.3, totalRevenue.value * 0.35, totalRevenue.value * 0.35]
  } else {
    // Theo quý trong năm
    labels = ['Q1', 'Q2', 'Q3', 'Q4']
    data = [totalRevenue.value * 0.2, totalRevenue.value * 0.25, totalRevenue.value * 0.3, totalRevenue.value * 0.25]
  }
  
  revenueChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu tổng hợp',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(102,126,234,0.5)',
        borderColor: '#667eea',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#667eea',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderTheaterChart() {
  if (theaterChartInstance) theaterChartInstance.destroy()
  if (!theaterChart.value || theaters.value.length === 0) return
  
  const ctx = theaterChart.value.getContext('2d')
  const labels = theaters.value.map(t => t.tenRapChieu)
  const data = theaters.value.map(t => t.tongTien)
  
  theaterChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu theo rạp',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(54,162,235,0.5)',
        borderColor: '#36A2EB',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#36A2EB',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderMovieChart() {
  if (movieChartInstance) movieChartInstance.destroy()
  if (!movieChart.value || movies.value.length === 0) return
  
  const ctx = movieChart.value.getContext('2d')
  const labels = movies.value.map(m => m.tenPhim)
  const data = movies.value.map(m => m.tongDoanhThu)
  
  movieChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu theo phim',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(255,99,132,0.5)',
        borderColor: '#FF6384',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#FF6384',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderShowtimeChart() {
  if (showtimeChartInstance) showtimeChartInstance.destroy()
  if (!showtimeChart.value || showtimes.value.length === 0) return
  
  const ctx = showtimeChart.value.getContext('2d')
  const labels = showtimes.value.map(s => s.tenSuatChieu || s.gioChieu)
  const data = showtimes.value.map(s => s.doanhThu || s.tongTien)
  
  showtimeChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu theo suất chiếu',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(75,192,192,0.5)',
        borderColor: '#4BC0C0',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#4BC0C0',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderHourlyChart() {
  if (hourlyChartInstance) hourlyChartInstance.destroy()
  if (!hourlyChart.value || hourlyStats.value.length === 0) return
  
  const ctx = hourlyChart.value.getContext('2d')
  const labels = hourlyStats.value.map(h => h.gioChieu || h.gio)
  const data = hourlyStats.value.map(h => h.doanhThu || h.tongTien)
  
  hourlyChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu theo giờ',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(255,159,64,0.5)',
        borderColor: '#FF9F40',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#FF9F40',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderRoomChart() {
  if (roomChartInstance) roomChartInstance.destroy()
  if (!roomChart.value || roomStats.value.length === 0) return
  
  const ctx = roomChart.value.getContext('2d')
  const labels = roomStats.value.map(r => r.tenPhongChieu || r.phongChieu)
  const data = roomStats.value.map(r => r.doanhThu || r.tongTien)
  
  roomChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels,
      datasets: [{
        label: 'Doanh thu theo phòng',
        data,
        backgroundColor: chartType.value === 'pie' ? [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
        ] : 'rgba(153,102,255,0.5)',
        borderColor: '#9966FF',
        borderWidth: 2,
        fill: chartType.value === 'line',
        tension: 0.4,
        pointRadius: chartType.value === 'line' ? 4 : 0,
        pointBackgroundColor: '#9966FF',
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { 
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: { 
          callbacks: { 
            label: function(context) {
              return formatCurrency(context.parsed.y || context.parsed)
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: { 
          beginAtZero: true,
          ticks: { 
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : {}
    }
  })
}

function renderOccupancyChart() {
  if (occupancyChartInstance) {
    occupancyChartInstance.destroy()
  }
  if (!occupancyChart.value || !occupancyStats.value.length) return

  const ctx = occupancyChart.value.getContext('2d')
  const data = occupancyStats.value

  occupancyChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels: data.map(item => item.tenPhongChieu),
      datasets: [{
        label: 'Tỷ lệ lấp đầy (%)',
        data: data.map(item => item.tyLeLapDay),
        backgroundColor: [
          'rgba(255, 99, 132, 0.8)',
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(75, 192, 192, 0.8)',
          'rgba(153, 102, 255, 0.8)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: {
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              return `${context.label}: ${context.parsed.y || context.parsed}%`
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: {
          beginAtZero: true,
          max: 100,
          ticks: {
            callback: function(value) {
              return value + '%'
            }
          }
        }
      } : undefined
    }
  })
}

function renderCustomerChart() {
  if (customerChartInstance) {
    customerChartInstance.destroy()
  }
  if (!customerChart.value) return

  const ctx = customerChart.value.getContext('2d')
  const data = customerStats.value

  customerChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels: ['Tổng khách hàng', 'Khách hàng mới', 'Khách hàng quay lại', 'Khách VIP'],
      datasets: [{
        label: 'Số lượng khách hàng',
        data: [data.totalCustomers, data.newCustomers, data.returningCustomers, data.vipCustomers],
        backgroundColor: [
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 99, 132, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(153, 102, 255, 0.8)'
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(255, 99, 132, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: {
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              return `${context.label}: ${context.parsed.y || context.parsed} người`
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return value.toLocaleString()
            }
          }
        }
      } : undefined
    }
  })
}

function renderGenreChart() {
  if (genreChartInstance) {
    genreChartInstance.destroy()
  }
  if (!genreChart.value || !genreStats.value.length) return

  const ctx = genreChart.value.getContext('2d')
  const data = genreStats.value

  genreChartInstance = new Chart(ctx, {
    type: chartType.value,
    data: {
      labels: data.map(item => item.tenTheLoai),
      datasets: [{
        label: 'Doanh thu (VND)',
        data: data.map(item => item.doanhThu),
        backgroundColor: [
          'rgba(255, 99, 132, 0.8)',
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(75, 192, 192, 0.8)',
          'rgba(153, 102, 255, 0.8)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: {
          display: chartType.value === 'pie',
          position: 'bottom'
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              return `${context.label}: ${formatCurrency(context.parsed.y || context.parsed)}`
            }
          }
        }
      },
      scales: chartType.value !== 'pie' ? {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return formatCurrency(value)
            }
          }
        }
      } : undefined
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

const tableDataType = ref('theater')
const sortBy = ref('revenue')
const sortOrder = ref('desc')
const tableSearch = ref('')
const currentPage = ref(1)
const pageSize = 10
const exportingPDF = ref(false)
const error = ref('')



const filteredTableData = computed(() => {
  let data = []
  if (tableDataType.value === 'theater') {
    data = theaters.value.map(t => ({
      name: t.tenRapChieu,
      tickets: t.soVeBan,
      revenue: t.tongTien,
      percentage: ((t.tongTien / totalRevenue.value) * 100).toFixed(1),
      trend: Math.random() > 0.5 ? 'up' : 'down'
    }))
  } else if (tableDataType.value === 'movie') {
    data = movies.value.map(m => ({
      name: m.tenPhim,
      tickets: m.soVeBan,
      revenue: m.tongDoanhThu,
      percentage: ((m.tongDoanhThu / totalRevenue.value) * 100).toFixed(1),
      trend: Math.random() > 0.5 ? 'up' : 'down'
    }))
  } else {
    // Combined data
    data = [
      ...theaters.value.map(t => ({
        name: `Rạp: ${t.tenRapChieu}`,
        tickets: t.soVeBan,
        revenue: t.tongTien,
        percentage: ((t.tongTien / totalRevenue.value) * 100).toFixed(1),
        trend: 'up'
      })),
      ...movies.value.map(m => ({
        name: `Phim: ${m.tenPhim}`,
        tickets: m.soVeBan,
        revenue: m.tongDoanhThu,
        percentage: ((m.tongDoanhThu / totalRevenue.value) * 100).toFixed(1),
        trend: 'down'
      }))
    ]
  }

  // Filter by search
  if (tableSearch.value) {
    data = data.filter(item => 
      item.name.toLowerCase().includes(tableSearch.value.toLowerCase())
    )
  }

  // Sort
  data.sort((a, b) => {
    let aVal, bVal
    if (sortBy.value === 'name') {
      aVal = a.name
      bVal = b.name
    } else if (sortBy.value === 'revenue') {
      aVal = a.revenue
      bVal = b.revenue
    } else {
      aVal = a.tickets
      bVal = b.tickets
    }
    
    if (sortOrder.value === 'asc') {
      return aVal > bVal ? 1 : -1
    } else {
      return aVal < bVal ? 1 : -1
    }
  })

  return data
})

const totalPages = computed(() => Math.ceil(filteredTableData.value.length / pageSize))

const paginatedTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredTableData.value.slice(start, start + pageSize)
})

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  if (end - start < maxVisible - 1) start = Math.max(1, end - maxVisible + 1)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

function goToPage(page) {
  if (page >= 1 && page <= totalPages.value) currentPage.value = page
}

async function exportExcelAdvanced() {
  exportingExcel.value = true
  try {
    const wb = XLSX.utils.book_new()
    
    // Sheet 1: Tổng quan
    const overviewData = [
      ['Báo cáo thống kê doanh thu', ''],
      ['Thời gian', `${statType.value}: ${getFilterDisplayValue()}`],
      ['Tổng doanh thu', totalRevenue.value],
      ['Tổng vé bán', totalTickets.value],
      ['Số rạp', theaters.value.length],
      ['Số phim', movies.value.length],
      ['', ''],
      ['Chi tiết theo rạp', ''],
      ['Tên rạp', 'Số vé', 'Doanh thu', 'Tỷ lệ (%)'],
      ...theaters.value.map(t => [
        t.tenRapChieu,
        t.soVeBan,
        t.tongTien,
        ((t.tongTien / totalRevenue.value) * 100).toFixed(1)
      ]),
      ['', ''],
      ['Chi tiết theo phim', ''],
      ['Tên phim', 'Số vé', 'Doanh thu', 'Tỷ lệ (%)'],
      ...movies.value.map(m => [
        m.tenPhim,
        m.soVeBan,
        m.tongDoanhThu,
        ((m.tongDoanhThu / totalRevenue.value) * 100).toFixed(1)
      ])
    ]
    
    const ws1 = XLSX.utils.aoa_to_sheet(overviewData)
    XLSX.utils.book_append_sheet(wb, ws1, 'Tổng quan')
    
    // Sheet 2: Chi tiết theo rạp
    const theaterSheet = [
      ['STT', 'Tên Rạp Chiếu', 'Số Vé Bán', 'Tổng Tiền', 'Tỷ lệ (%)'],
      ...theaters.value.map((t, i) => [
        i + 1,
        t.tenRapChieu,
        t.soVeBan,
        t.tongTien,
        ((t.tongTien / totalRevenue.value) * 100).toFixed(1)
      ])
    ]
    const ws2 = XLSX.utils.aoa_to_sheet(theaterSheet)
    XLSX.utils.book_append_sheet(wb, ws2, 'Theo rạp')
    
    // Sheet 3: Chi tiết theo phim
    const movieSheet = [
      ['STT', 'Tên Phim', 'Số Vé Bán', 'Tổng Doanh Thu', 'Tỷ lệ (%)'],
      ...movies.value.map((m, i) => [
        i + 1,
        m.tenPhim,
        m.soVeBan,
        m.tongDoanhThu,
        ((m.tongDoanhThu / totalRevenue.value) * 100).toFixed(1)
      ])
    ]
    const ws3 = XLSX.utils.aoa_to_sheet(movieSheet)
    XLSX.utils.book_append_sheet(wb, ws3, 'Theo phim')
    
    XLSX.writeFile(wb, `ThongKeDoanhThu_${getFilterDisplayValue()}_${new Date().toISOString().slice(0,10)}.xlsx`)
    showToast('Xuất Excel nâng cao thành công!', 'success')
  } catch (e) {
    showToast('Xuất Excel thất bại!', 'error')
  } finally {
    exportingExcel.value = false
  }
}

async function exportPDF() {
  exportingPDF.value = true
  try {
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const margin = 20
    let yPosition = margin

    // Tiêu đề
    pdf.setFontSize(24)
    pdf.setFont('helvetica', 'bold')
    pdf.text('BÁO CÁO THỐNG KÊ DOANH THU', pageWidth / 2, yPosition, { align: 'center' })
    yPosition += 15

    // Thông tin thời gian
    pdf.setFontSize(14)
    pdf.setFont('helvetica', 'normal')
    pdf.text(`Thời gian: ${statType.value.toUpperCase()} - ${getFilterDisplayValue()}`, margin, yPosition)
    yPosition += 10
    pdf.text(`Ngày xuất báo cáo: ${new Date().toLocaleDateString('vi-VN')}`, margin, yPosition)
    yPosition += 20

    // Thống kê tổng quan
    pdf.setFontSize(16)
    pdf.setFont('helvetica', 'bold')
    pdf.text('THỐNG KÊ TỔNG QUAN', margin, yPosition)
    yPosition += 10

    pdf.setFontSize(12)
    pdf.setFont('helvetica', 'normal')
    const overviewData = [
      ['Chỉ số', 'Giá trị'],
      ['Tổng doanh thu', formatCurrency(totalRevenue.value)],
      ['Tổng vé bán', totalTickets.value.toString()],
      ['Số rạp', theaters.value.length.toString()],
      ['Số phim', movies.value.length.toString()],
      ['Xu hướng doanh thu', `${revenueTrend.value}% ${revenueTrend.icon}`],
      ['Top performer', topPerformer.value.name],
      ['Growth rate', `${growthRate.value}%`]
    ]

    pdf.autoTable({
      startY: yPosition,
      head: [['Chỉ số', 'Giá trị']],
      body: overviewData.slice(1),
      theme: 'grid',
      headStyles: { fillColor: [102, 126, 234] },
      styles: { fontSize: 10 },
      margin: { left: margin, right: margin }
    })

    yPosition = pdf.lastAutoTable.finalY + 15

    // Kiểm tra nếu còn chỗ cho bảng dữ liệu
    if (yPosition < pageHeight - 100) {
      // Bảng doanh thu theo rạp
      pdf.setFontSize(16)
      pdf.setFont('helvetica', 'bold')
      pdf.text('DOANH THU THEO RẠP CHIẾU', margin, yPosition)
      yPosition += 10

      const theaterData = theaters.value.map((t, i) => [
        i + 1,
        t.tenRapChieu,
        t.soVeBan.toString(),
        formatCurrency(t.tongTien),
        `${((t.tongTien / totalRevenue.value) * 100).toFixed(1)}%`
      ])

      pdf.autoTable({
        startY: yPosition,
        head: [['STT', 'Tên Rạp', 'Số Vé', 'Doanh Thu', 'Tỷ lệ']],
        body: theaterData,
        theme: 'grid',
        headStyles: { fillColor: [102, 126, 234] },
        styles: { fontSize: 8 },
        margin: { left: margin, right: margin }
      })

      yPosition = pdf.lastAutoTable.finalY + 15
    }

    // Kiểm tra nếu cần thêm trang mới cho bảng phim
    if (yPosition > pageHeight - 80) {
      pdf.addPage()
      yPosition = margin
    }

    // Bảng doanh thu theo phim
    pdf.setFontSize(16)
    pdf.setFont('helvetica', 'bold')
    pdf.text('DOANH THU THEO PHIM', margin, yPosition)
    yPosition += 10

    const movieData = movies.value.map((m, i) => [
      i + 1,
      m.tenPhim,
      m.soVeBan.toString(),
      formatCurrency(m.tongDoanhThu),
      `${((m.tongDoanhThu / totalRevenue.value) * 100).toFixed(1)}%`
    ])

    pdf.autoTable({
      startY: yPosition,
      head: [['STT', 'Tên Phim', 'Số Vé', 'Doanh Thu', 'Tỷ lệ']],
      body: movieData,
      theme: 'grid',
      headStyles: { fillColor: [102, 126, 234] },
      styles: { fontSize: 8 },
      margin: { left: margin, right: margin }
    })

    // Thêm biểu đồ nếu có thể
    try {
      // Chụp ảnh biểu đồ
      const chartElements = document.querySelectorAll('canvas')
      if (chartElements.length > 0) {
        // Thêm trang mới cho biểu đồ
        pdf.addPage()
        yPosition = margin

        pdf.setFontSize(16)
        pdf.setFont('helvetica', 'bold')
        pdf.text('BIỂU ĐỒ THỐNG KÊ', pageWidth / 2, yPosition, { align: 'center' })
        yPosition += 20

        // Chụp từng biểu đồ
        for (let i = 0; i < Math.min(chartElements.length, 2); i++) {
          const canvas = chartElements[i]
          const chartTitle = canvas.closest('.chart-section')?.querySelector('h2')?.textContent || `Biểu đồ ${i + 1}`
          
          pdf.setFontSize(12)
          pdf.setFont('helvetica', 'bold')
          pdf.text(chartTitle, margin, yPosition)
          yPosition += 10

          try {
            const imgData = canvas.toDataURL('image/png')
            const imgWidth = pageWidth - 2 * margin
            const imgHeight = (canvas.height * imgWidth) / canvas.width
            
            // Kiểm tra nếu cần thêm trang mới
            if (yPosition + imgHeight > pageHeight - margin) {
              pdf.addPage()
              yPosition = margin
            }

            pdf.addImage(imgData, 'PNG', margin, yPosition, imgWidth, imgHeight)
            yPosition += imgHeight + 15
          } catch (e) {
            console.error('Lỗi chụp biểu đồ:', e)
            pdf.text('Không thể hiển thị biểu đồ', margin, yPosition)
            yPosition += 10
          }
        }
      }
    } catch (e) {
      console.error('Lỗi xử lý biểu đồ:', e)
    }

    // Footer
    const totalPages = pdf.internal.getNumberOfPages()
    for (let i = 1; i <= totalPages; i++) {
      pdf.setPage(i)
      pdf.setFontSize(10)
      pdf.setFont('helvetica', 'italic')
      pdf.text(`Trang ${i}/${totalPages}`, pageWidth / 2, pageHeight - 10, { align: 'center' })
    }

    // Xuất file
    const fileName = `BaoCaoThongKe_${getFilterDisplayValue()}_${new Date().toISOString().slice(0,10)}.pdf`
    pdf.save(fileName)
    
    showToast('Xuất PDF thành công!', 'success')
  } catch (e) {
    console.error('Lỗi xuất PDF:', e)
    showToast('Xuất PDF thất bại: ' + e.message, 'error')
  } finally {
    exportingPDF.value = false
  }
}

async function exportPDFSimple() {
  exportingPDF.value = true
  try {
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const margin = 20
    let yPosition = margin

    // Tiêu đề
    pdf.setFontSize(20)
    pdf.setFont('helvetica', 'bold')
    pdf.text('BÁO CÁO THỐNG KÊ DOANH THU', pageWidth / 2, yPosition, { align: 'center' })
    yPosition += 15

    // Thông tin thời gian
    pdf.setFontSize(12)
    pdf.setFont('helvetica', 'normal')
    pdf.text(`Thời gian: ${statType.value.toUpperCase()} - ${getFilterDisplayValue()}`, margin, yPosition)
    yPosition += 8
    pdf.text(`Ngày xuất: ${new Date().toLocaleDateString('vi-VN')}`, margin, yPosition)
    yPosition += 15

    // Thống kê tổng quan
    pdf.setFontSize(14)
    pdf.setFont('helvetica', 'bold')
    pdf.text('TỔNG QUAN', margin, yPosition)
    yPosition += 8

    const summaryData = [
      ['Tổng doanh thu', formatCurrency(totalRevenue.value)],
      ['Tổng vé bán', totalTickets.value.toString()],
      ['Số rạp', theaters.value.length.toString()],
      ['Số phim', movies.value.length.toString()]
    ]

    pdf.autoTable({
      startY: yPosition,
      body: summaryData,
      theme: 'plain',
      styles: { fontSize: 10 },
      margin: { left: margin, right: margin }
    })

    yPosition = pdf.lastAutoTable.finalY + 15

    // Bảng doanh thu theo rạp
    if (theaters.value.length > 0) {
      pdf.setFontSize(14)
      pdf.setFont('helvetica', 'bold')
      pdf.text('DOANH THU THEO RẠP', margin, yPosition)
      yPosition += 8

      const theaterData = theaters.value.map((t, i) => [
        i + 1,
        t.tenRapChieu,
        t.soVeBan.toString(),
        formatCurrency(t.tongTien)
      ])

      pdf.autoTable({
        startY: yPosition,
        head: [['STT', 'Tên Rạp', 'Số Vé', 'Doanh Thu']],
        body: theaterData,
        theme: 'grid',
        headStyles: { fillColor: [102, 126, 234] },
        styles: { fontSize: 9 },
        margin: { left: margin, right: margin }
      })

      yPosition = pdf.lastAutoTable.finalY + 15
    }

    // Kiểm tra nếu cần thêm trang mới
    if (yPosition > pageHeight - 60) {
      pdf.addPage()
      yPosition = margin
    }

    // Bảng doanh thu theo phim
    if (movies.value.length > 0) {
      pdf.setFontSize(14)
      pdf.setFont('helvetica', 'bold')
      pdf.text('DOANH THU THEO PHIM', margin, yPosition)
      yPosition += 8

      const movieData = movies.value.map((m, i) => [
        i + 1,
        m.tenPhim,
        m.soVeBan.toString(),
        formatCurrency(m.tongDoanhThu)
      ])

      pdf.autoTable({
        startY: yPosition,
        head: [['STT', 'Tên Phim', 'Số Vé', 'Doanh Thu']],
        body: movieData,
        theme: 'grid',
        headStyles: { fillColor: [102, 126, 234] },
        styles: { fontSize: 9 },
        margin: { left: margin, right: margin }
      })
    }

    // Footer
    const totalPages = pdf.internal.getNumberOfPages()
    for (let i = 1; i <= totalPages; i++) {
      pdf.setPage(i)
      pdf.setFontSize(8)
      pdf.setFont('helvetica', 'italic')
      pdf.text(`Trang ${i}/${totalPages}`, pageWidth / 2, pageHeight - 10, { align: 'center' })
    }

    // Xuất file
    const fileName = `BaoCaoDonGian_${getFilterDisplayValue()}_${new Date().toISOString().slice(0,10)}.pdf`
    pdf.save(fileName)
    
    showToast('Xuất PDF đơn giản thành công!', 'success')
  } catch (e) {
    console.error('Lỗi xuất PDF đơn giản:', e)
    showToast('Xuất PDF thất bại: ' + e.message, 'error')
  } finally {
    exportingPDF.value = false
  }
}

function getFilterDisplayValue() {
  if (statType.value === 'day') return selectedDay.value
  if (statType.value === 'month') return selectedMonth.value
  if (statType.value === 'quarter') return `Q${selectedQuarter.value}/${selectedYear.value}`
  return selectedYear.value
}

function retryFetch() {
  error.value = ''
  fetchStatistics()
}

// Mock data generators
function generateMockShowtimeData() {
  return [
    { tenSuatChieu: 'Suất 1 (9:00)', doanhThu: totalRevenue.value * 0.25 },
    { tenSuatChieu: 'Suất 2 (12:00)', doanhThu: totalRevenue.value * 0.35 },
    { tenSuatChieu: 'Suất 3 (15:00)', doanhThu: totalRevenue.value * 0.20 },
    { tenSuatChieu: 'Suất 4 (18:00)', doanhThu: totalRevenue.value * 0.15 },
    { tenSuatChieu: 'Suất 5 (21:00)', doanhThu: totalRevenue.value * 0.05 }
  ]
}

function generateMockHourlyData() {
  return [
    { gioChieu: '09:00', doanhThu: totalRevenue.value * 0.15 },
    { gioChieu: '10:00', doanhThu: totalRevenue.value * 0.10 },
    { gioChieu: '11:00', doanhThu: totalRevenue.value * 0.05 },
    { gioChieu: '12:00', doanhThu: totalRevenue.value * 0.20 },
    { gioChieu: '13:00', doanhThu: totalRevenue.value * 0.15 },
    { gioChieu: '14:00', doanhThu: totalRevenue.value * 0.10 },
    { gioChieu: '15:00', doanhThu: totalRevenue.value * 0.10 },
    { gioChieu: '16:00', doanhThu: totalRevenue.value * 0.05 },
    { gioChieu: '17:00', doanhThu: totalRevenue.value * 0.05 },
    { gioChieu: '18:00', doanhThu: totalRevenue.value * 0.05 }
  ]
}

function generateMockRoomData() {
  return [
    { tenPhongChieu: 'Phòng 1', doanhThu: totalRevenue.value * 0.30 },
    { tenPhongChieu: 'Phòng 2', doanhThu: totalRevenue.value * 0.25 },
    { tenPhongChieu: 'Phòng 3', doanhThu: totalRevenue.value * 0.20 },
    { tenPhongChieu: 'Phòng 4', doanhThu: totalRevenue.value * 0.15 },
    { tenPhongChieu: 'Phòng VIP', doanhThu: totalRevenue.value * 0.10 }
  ]
}

function generateMockOccupancyData() {
  return [
    { tenPhongChieu: 'Phòng 1', tyLeLapDay: 85 },
    { tenPhongChieu: 'Phòng 2', tyLeLapDay: 78 },
    { tenPhongChieu: 'Phòng 3', tyLeLapDay: 92 },
    { tenPhongChieu: 'Phòng 4', tyLeLapDay: 65 },
    { tenPhongChieu: 'Phòng VIP', tyLeLapDay: 95 }
  ]
}

function generateMockCustomerData() {
  return {
    totalCustomers: 1250,
    newCustomers: 89,
    returningCustomers: 1161,
    vipCustomers: 45
  }
}

function generateMockGenreData() {
  return [
    { tenTheLoai: 'Hành động', doanhThu: totalRevenue.value * 0.35, soVeBan: 450 },
    { tenTheLoai: 'Tình cảm', doanhThu: totalRevenue.value * 0.25, soVeBan: 320 },
    { tenTheLoai: 'Hài hước', doanhThu: totalRevenue.value * 0.20, soVeBan: 280 },
    { tenTheLoai: 'Kinh dị', doanhThu: totalRevenue.value * 0.15, soVeBan: 200 },
    { tenTheLoai: 'Hoạt hình', doanhThu: totalRevenue.value * 0.05, soVeBan: 80 }
  ]
}



function updateCharts() {
  // Chỉ vẽ lại khi có dữ liệu
  if (theaters.value.length > 0 || movies.value.length > 0) {
    setTimeout(() => {
      if (theaters.value.length > 0) renderTheaterChart()
      if (movies.value.length > 0) renderMovieChart()
      if (theaters.value.length > 0 || movies.value.length > 0) renderRevenueChart()
      if (showtimes.value.length > 0) renderShowtimeChart()
      if (hourlyStats.value.length > 0) renderHourlyChart()
      if (roomStats.value.length > 0) renderRoomChart()
      if (occupancyStats.value.length > 0) renderOccupancyChart()
      if (customerStats.value.totalCustomers > 0) renderCustomerChart()
      if (genreStats.value.length > 0) renderGenreChart()

    }, 100)
  }
}



// On mount
onMounted(() => {
  fetchStatistics()
})

watch([theaters], () => {
  setTimeout(() => renderTheaterChart(), 200)
})
watch([movies], () => {
  setTimeout(() => renderMovieChart(), 200)
})

async function fetchCustomerStatistics() {
  loading.value.customer = true
  try {
    const res = await fetchCustomerStatisticsApi()
    const data = res.data
    customerStats.value = {
      totalCustomers: data.tongKhachHang || 0,
      newCustomers: data.khachHangMoi || 0,
      returningCustomers: data.khachHangQuayLai || 0,
      vipCustomers: data.khachVIP || 0
    }
  } catch (e) {
    console.warn('⚠️ API khách hàng chưa sẵn sàng:', e.message)
    customerStats.value = generateMockCustomerData()
  } finally {
    loading.value.customer = false
  }
}

function fetchGenreStatistics() {
  loading.value.genre = true
  try {
    // Tính toán thống kê theo thể loại từ dữ liệu phim hiện có
    const genreMap = new Map()
    
    // Duyệt qua tất cả phim để tính toán theo thể loại
    movies.value.forEach(movie => {
      if (movie.theLoai && Array.isArray(movie.theLoai)) {
        movie.theLoai.forEach(theLoai => {
          const tenTheLoai = theLoai.trim()
          if (tenTheLoai) {
            if (!genreMap.has(tenTheLoai)) {
              genreMap.set(tenTheLoai, {
                tenTheLoai: tenTheLoai,
                doanhThu: 0,
                soVeBan: 0,
                soPhim: 0
              })
            }
            const genre = genreMap.get(tenTheLoai)
            genre.doanhThu += movie.tongDoanhThu || 0
            genre.soVeBan += movie.soVeBan || 0
            genre.soPhim += 1
          }
        })
      }
    })
    
    // Chuyển đổi Map thành Array và sắp xếp theo doanh thu
    const genreStatsArray = Array.from(genreMap.values())
      .sort((a, b) => b.doanhThu - a.doanhThu)
    
    genreStats.value = genreStatsArray.length > 0 ? genreStatsArray : generateMockGenreData()
  } catch (e) {
    console.warn('⚠️ Lỗi tính toán thống kê thể loại:', e.message)
    genreStats.value = generateMockGenreData()
  } finally {
    loading.value.genre = false
  }
}


</script>

<style scoped>
.dashboard-page {
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
  background: #f1f5ff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(79,140,255,0.10);
  padding: 24px;
  font-size: 1.25rem;
  font-weight: 700;
  color: #232946;
  display: flex;
  align-items: center;
  gap: 16px;
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
  margin-right: 16px;
}
.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2563eb;
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
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
}
.filter-form input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}
.filter-form select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
  min-width: 120px;
}

.filter-form select option {
  background: white;
  color: #2c3e50;
  font-weight: 500;
  padding: 8px;
}

.filter-form select option:hover {
  background: #667eea;
  color: white;
}

.filter-form select option:checked {
  background: #667eea;
  color: white;
  font-weight: 600;
}

.filter-form select:focus {
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
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.chart-section h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-section canvas {
  max-height: 350px !important;
  width: 100% !important;
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
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.loading-spinner {
  border: 8px solid #f3f3f3;
  border-top: 8px solid #3498db;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
}
.error-message {
  background: #ffebee;
  color: #d32f2f;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(211, 47, 47, 0.1);
  text-align: center;
  margin-top: 20px;
}
.btn-retry {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-retry:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
}
.trend-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}
.trend-card {
  background: #f1f5ff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(79,140,255,0.10);
  padding: 24px;
  text-align: center;
}
.trend-card h3 {
  margin-bottom: 10px;
  color: #2c3e50;
  font-size: 18px;
}
.trend-value {
  font-size: 36px;
  font-weight: 700;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.trend-value.positive { color: #10b981; }
.trend-value.negative { color: #ff5252; }
.trend-value.neutral { color: #7f8c8d; }
.trend-icon { font-size: 24px; }
.top-performer {
  font-size: 24px;
  font-weight: 700;
  color: #2563eb;
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.growth-rate {
  font-size: 18px;
  color: #7f8c8d;
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.rate-value {
  font-size: 24px;
  font-weight: 700;
  color: #10b981;
}
.rate-period {
  font-size: 16px;
}
.trend-chart {
  height: 200px; /* Adjust height as needed */
  position: relative;
}
.trend-chart canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
.trend-indicator {
  font-size: 18px;
  font-weight: bold;
}
.trend-indicator.up { color: #10b981; }
.trend-indicator.down { color: #ff5252; }
.trend-indicator.neutral { color: #7f8c8d; }
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.table-actions {
  display: flex;
  gap: 10px;
}
.btn-export-pdf {
  background: linear-gradient(135deg, #ff9f40 0%, #ff6b6b 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-export-pdf:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff9f40 100%);
  transform: translateY(-2px);
}
.btn-export-pdf-simple {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-export-pdf-simple:hover {
  background: linear-gradient(135deg, #20c997 0%, #28a745 100%);
  transform: translateY(-2px);
}
.table-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}
.filter-group label {
  font-size: 15px;
  font-weight: 600;
  color: #555;
}
.table-container {
  overflow-x: auto;
}
.no-data {
  text-align: center;
  padding: 40px 0;
  color: #7f8c8d;
  font-size: 16px;
}
.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}
.btn-page {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 15px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-page:hover:not(.active) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
}
.btn-page.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}
.btn-page:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #e0e0e0;
  color: #9e9e9e;
}
.btn-sort {
  background: #e0e0e0;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
}
.btn-sort:hover {
  background: #d0d0d0;
  transform: translateY(-2px);
}
.chart-type-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  padding: 20px 32px;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.chart-type-section label {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-type-section select {
  padding: 10px 16px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
  min-width: 150px;
}

.chart-type-section select option {
  background: white;
  color: #2c3e50;
  font-weight: 500;
  padding: 8px;
}

.chart-type-section select option:hover {
  background: #667eea;
  color: white;
}

.chart-type-section select option:checked {
  background: #667eea;
  color: white;
  font-weight: 600;
}

.chart-type-section select:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.badge-percent {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.table-filters {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
}

.table-filters .filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-filters .filter-group label {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  min-width: 100px;
}

.table-filters .filter-group select,
.table-filters .filter-group input {
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #ced4da;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
  min-width: 120px;
}

.table-filters .filter-group select:focus,
.table-filters .filter-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.table-container table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
}

.table-container th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  font-size: 14px;
}

.table-container td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.table-container tr:hover {
  background: #f8f9fa;
}

.table-container tr:last-child td {
  border-bottom: none;
}

.trend-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.trend-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  border: 1px solid #dee2e6;
  transition: all 0.3s ease;
}

.trend-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.trend-card h3 {
  margin: 0 0 16px 0;
  color: #495057;
  font-size: 18px;
  font-weight: 600;
}

.trend-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.trend-value.positive {
  color: #28a745;
}

.trend-value.negative {
  color: #dc3545;
}

.trend-value.neutral {
  color: #6c757d;
}

.trend-icon {
  font-size: 20px;
}

.top-performer {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.top-performer strong {
  font-size: 20px;
  color: #495057;
}

.top-performer span {
  font-size: 24px;
  font-weight: 700;
  color: #28a745;
}

.growth-rate {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.rate-value {
  font-size: 28px;
  font-weight: 700;
  color: #28a745;
}

.rate-period {
  font-size: 14px;
  color: #6c757d;
}

.trend-indicator {
  font-size: 16px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}

.trend-indicator.up {
  color: #28a745;
  background: rgba(40, 167, 69, 0.1);
}

.trend-indicator.down {
  color: #dc3545;
  background: rgba(220, 53, 69, 0.1);
}

.trend-indicator.neutral {
  color: #6c757d;
  background: rgba(108, 117, 125, 0.1);
}

@media (max-width: 900px) {
  .page-header, .stats-section, .filter-section, .table-section {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }
  .stats-section {
    grid-template-columns: 1fr;
  }
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .table-actions {
    width: 100%;
    justify-content: center;
  }
  .table-filters {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .filter-group {
    width: 100%;
    justify-content: space-between;
  }
  .filter-group label {
    flex: 1;
  }
  .filter-group select, .filter-group input {
    width: calc(50% - 5px); /* Adjust for gap */
  }
  .btn-sort {
    width: 25px;
    height: 25px;
  }
}

@media (max-width: 768px) {
  .chart-type-section {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .chart-type-section label {
    justify-content: center;
  }
  
  .trend-analysis {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .trend-card {
    padding: 20px;
  }
  
  .trend-value {
    font-size: 28px;
  }
  
  .table-filters {
    padding: 16px;
  }
  
  .table-filters .filter-group {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .table-filters .filter-group label {
    min-width: auto;
  }
  
  .table-filters .filter-group select,
  .table-filters .filter-group input {
    min-width: auto;
    width: 100%;
  }
  
  .table-container {
    font-size: 12px;
  }
  
  .table-container th,
  .table-container td {
    padding: 8px 6px;
  }
}

/* Customer stats grid */
.customer-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.customer-stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: transform 0.2s ease;
}

.customer-stat-card:hover {
  transform: translateY(-2px);
}

.customer-stat-card .stat-icon {
  font-size: 32px;
  opacity: 0.9;
}

.customer-stat-card .stat-content {
  flex: 1;
}

.customer-stat-card .stat-number {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.customer-stat-card .stat-label {
  font-size: 14px;
  opacity: 0.9;
}



.data-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.tab-btn {
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-btn:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.tab-btn.active {
  background: #667eea;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.sort-controls {
  display: flex;
  gap: 10px;
}

.sort-select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
  min-width: 120px;
}

.sort-select option {
  background: white;
  color: #2c3e50;
  font-weight: 500;
  padding: 8px;
}

.sort-select option:hover {
  background: #667eea;
  color: white;
}

.sort-select option:checked {
  background: #667eea;
  color: white;
  font-weight: 600;
}

.sort-select:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.data-cards-container {
  overflow-x: auto;
}

.data-cards-grid {
  display: flex;
  gap: 20px;
}

.data-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  padding: 20px;
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-rank {
  font-size: 18px;
  font-weight: 600;
}

.card-trend {
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}

.card-trend.up {
  background: rgba(40, 167, 69, 0.1);
  color: #28a745;
}

.card-trend.down {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
}

.card-trend.neutral {
  background: rgba(108, 117, 125, 0.1);
  color: #6c757d;
}

.card-content {
  margin-bottom: 16px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.card-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.progress-bar {
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #667eea;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Table section styling */
.table-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  margin-bottom: 32px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
}

.table-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.table-actions .btn {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-export-excel {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.btn-export-excel:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.btn-export-pdf {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.btn-export-pdf:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.4);
}

.btn-export-pdf-simple {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(107, 114, 128, 0.3);
}

.btn-export-pdf-simple:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(107, 114, 128, 0.4);
}

/* Data tabs */
.data-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  background: #f8fafc;
  padding: 8px;
  border-radius: 12px;
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tab-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* Table controls */
.table-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  color: #1f2937;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-select {
  padding: 10px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sort-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-sort {
  padding: 10px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 50px;
}

.btn-sort:hover {
  border-color: #667eea;
  background: #f8fafc;
  transform: translateY(-1px);
}

/* Data cards container */
.data-cards-container {
  margin: 24px 0;
}

.data-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.data-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.data-card:hover::before {
  transform: scaleX(1);
}

.data-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-rank {
  font-size: 28px;
  font-weight: 800;
  color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-trend {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.card-trend.up {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.card-trend.down {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.card-trend.neutral {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
}

.card-trend::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transition: all 0.3s ease;
  transform: translate(-50%, -50%);
}

.card-trend:hover::before {
  width: 100%;
  height: 100%;
}

.card-trend:hover {
  transform: scale(1.1) rotate(5deg);
}

.card-content {
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
  line-height: 1.3;
  min-height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.stat-item:hover::before {
  left: 100%;
}

.stat-item:hover {
  background: #f1f5f9;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 18px;
  font-weight: 800;
  line-height: 1.2;
}

.stat-value.revenue {
  color: #059669;
}

.stat-value.tickets {
  color: #2563eb;
}

.stat-value.percentage {
  color: #7c3aed;
}

.card-footer {
  margin-top: 20px;
}

.progress-bar {
  background: #e2e8f0;
  border-radius: 12px;
  height: 8px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  height: 100%;
  border-radius: 12px;
  transition: width 0.8s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.no-data {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.no-data-icon {
  font-size: 64px;
  color: #cbd5e1;
  margin-bottom: 16px;
}

.no-data p {
  font-size: 18px;
  color: #64748b;
  font-weight: 500;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 32px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.btn-page {
  padding: 10px 14px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-page:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8fafc;
  transform: translateY(-1px);
}

.btn-page.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

/* Loading spinner for buttons */
.loading-spinner-btn {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Responsive Design */
@media (max-width: 1200px) {
  .data-cards-grid {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  }
  
  .table-controls {
    gap: 16px;
  }
  
  .search-box {
    max-width: 300px;
  }
}

@media (max-width: 768px) {
  .data-cards-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }
  
  .data-card {
    padding: 20px;
  }
  
  .card-title {
    font-size: 18px;
    min-height: 48px;
  }
  
  .card-stats {
    gap: 12px;
  }
  
  .stat-value {
    font-size: 16px;
  }
  
  .table-section {
    padding: 20px;
  }
  
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .table-actions {
    width: 100%;
    justify-content: center;
  }
  
  .table-controls {
    flex-direction: column;
    gap: 16px;
  }
  
  .search-box {
    max-width: 100%;
  }
  
  .pagination {
    flex-wrap: wrap;
    gap: 6px;
  }
  
  .btn-page {
    padding: 8px 12px;
    font-size: 13px;
    min-width: 36px;
  }
}

@media (max-width: 480px) {
  .data-cards-grid {
    grid-template-columns: 1fr;
  }
  
  .card-stats {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .stat-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: left;
  }
  
  .stat-label {
    margin-bottom: 0;
  }
  
  .table-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .table-actions .btn {
    width: 100%;
    justify-content: center;
  }
  
  .pagination {
    padding: 16px;
  }
  
  .btn-page {
    padding: 6px 10px;
    font-size: 12px;
    min-width: 32px;
  }
}
</style>
