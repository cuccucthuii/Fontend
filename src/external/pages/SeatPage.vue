<template>
  <div class="seat-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">💺</span>
          Quản lý Ghế Ngồi
        </h1>
        <p class="page-subtitle">Quản lý danh sách ghế, trạng thái, loại ghế và phòng chiếu</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddModal = true">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm Ghế</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">💺</div>
        <div>
          <div class="stat-number">{{ seats.length }}</div>
          <div class="stat-label">Tổng số ghế</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div>
          <div class="stat-number">{{ activeSeats }}</div>
          <div class="stat-label">Còn trống</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⛔</div>
        <div>
          <div class="stat-number">{{ reservedSeats }}</div>
          <div class="stat-label">Đã đặt</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🛠️</div>
        <div>
          <div class="stat-number">{{ usingSeats }}</div>
          <div class="stat-label">Đang sử dụng</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🏟️</div>
        <div>
          <div class="stat-number">{{ uniqueRooms.length }}</div>
          <div class="stat-label">Số phòng chiếu</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🪑</div>
        <div>
          <div class="stat-number">{{ uniqueTypes.length }}</div>
          <div class="stat-label">Số loại ghế</div>
        </div>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm vị trí, hàng, số ghế, loại ghế, phòng chiếu..." />
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏷️</span> Trạng thái</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="CON_TRONG">Còn trống</option>
          <option value="DA_DAT">Đã đặt</option>
          <option value="DANG_SU_DUNG">Đang sử dụng</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🪑</span> Loại ghế</label>
        <select v-model="typeFilter" class="filter-select">
          <option value="">Tất cả loại ghế</option>
          <option v-for="type in uniqueTypes" :key="type" :value="type">{{ type }}</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏟️</span> Phòng chiếu</label>
        <select v-model="roomFilter" class="filter-select">
          <option value="">Tất cả phòng</option>
          <option v-for="room in uniqueRooms" :key="room" :value="room">{{ room }}</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-actions">
        <button class="btn btn-clear-filters" @click="clearFilters" v-if="hasActiveFilters">
          <span class="btn-icon">🔄</span>
          <span class="btn-text">Xóa bộ lọc</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <table v-if="paginatedSeats.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Phòng</th>
            <th>Vị Trí Ghế</th>
            <th>Hàng</th>
            <th>Số Ghế</th>
            <th>Loại Ghế</th>
            <th>Trạng Thái</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(seat, index) in paginatedSeats" :key="seat.idGheNgoi">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ seat.tenPhongChieu }}</td>
            <td>{{ seat.viTriGhe }}</td>
            <td>{{ seat.hangGhe }}</td>
            <td>{{ seat.soGhe }}</td>
            <td>{{ seat.loaiGhe }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(seat.trangThai)]">
                {{ getStatusLabel(seat.trangThai) }}
              </span>
            </td>
            <td class="actions-cell">
              <button class="edit action-btn" @click="handleEditClick(seat)">✏️</button>
              <button class="delete action-btn">❌</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải dữ liệu...</p>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages > 1">
      <div class="pagination-info">
        Hiển thị {{ paginatedSeats.length }} ghế trên tổng số {{ filteredSeats.length }} ghế
      </div>
      <div class="pagination-controls">
        <button class="pagination-btn" @click="prevPage" :disabled="currentPage === 1">
          ⬅️ Trang trước
        </button>
        <span class="page-numbers">
          <button v-for="page in visiblePages" :key="page" class="page-btn" :class="{ active: page === currentPage }" @click="goToPage(page)">
            {{ page }}
          </button>
        </span>
        <button class="pagination-btn" @click="nextPage" :disabled="currentPage === totalPages">
          Trang sau ➡️
        </button>
      </div>
    </div>
     <!-- Form Modal -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-content">
        <SeatForm @submit="handleCreateSeat" @cancel="showAddModal = false" />
      </div>
    </div>
    <!-- Form Update Modal -->
<div v-if="showEditModal" class="modal-overlay">
  <div class="modal-content">
    <SeatForm
      :seat="selectedSeat"
      is-edit
      @submit="handleUpdateSeat"
      @cancel="() => { showEditModal = false; selectedSeat = null }"
    />
  </div>
</div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchSeats, createSeat, updateSeat } from '../services/seatService'
import SeatForm from '../components/SeatForm.vue'

const seats = ref([])
const currentPage = ref(1)
const pageSize = 10
const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const roomFilter = ref('')
const showAddModal = ref(false)
const selectedSeat = ref(null)

const showEditModal = ref(false)

const handleEditClick = (seat) => {
  selectedSeat.value = seat
  showEditModal.value = true
}

const handleUpdateSeat = async (updatedSeatData) => {
  try {
    await updateSeat(selectedSeat.value.idGheNgoi, updatedSeatData)
    const response = await fetchSeats()
    seats.value = response.data.content
    showEditModal.value = false
    selectedSeat.value = null
    alert('✔️ Cập nhật ghế thành công!')
  } catch (error) {
    console.error('❌ Lỗi khi cập nhật ghế:', error)
    alert('❌ Cập nhật ghế thất bại!')
  }
}

const handleCreateSeat = async (seatData) => {
  try {
    await createSeat(seatData)
    const response = await fetchSeats()
    seats.value = response.data.content
    alert('🎉 Thêm ghế thành công!')
    showAddModal.value = false
  } catch (error) {
    console.error('❌ Lỗi khi tạo ghế:', error)
  }
}


const filteredSeats = computed(() => {
  let filtered = seats.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    filtered = filtered.filter(seat =>
      (seat.viTriGhe || '').toLowerCase().includes(q) ||
      (seat.hangGhe || '').toLowerCase().includes(q) ||
      (seat.soGhe || '').toString().toLowerCase().includes(q) ||
      (seat.loaiGhe || '').toLowerCase().includes(q) ||
      (seat.tenPhongChieu || '').toLowerCase().includes(q)
    )
  }
  if (statusFilter.value) {
    filtered = filtered.filter(seat => seat.trangThai === statusFilter.value)
  }
  if (typeFilter.value) {
    filtered = filtered.filter(seat => seat.loaiGhe === typeFilter.value)
  }
  if (roomFilter.value) {
    filtered = filtered.filter(seat => seat.tenPhongChieu === roomFilter.value)
  }
  return filtered
})

const paginatedSeats = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredSeats.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredSeats.value.length / pageSize))

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

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

const hasActiveFilters = computed(() => {
  return searchQuery.value || statusFilter.value || typeFilter.value || roomFilter.value
})

function clearFilters() {
  searchQuery.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  roomFilter.value = ''
}

const uniqueTypes = computed(() => {
  const set = new Set()
  seats.value.forEach(s => s.loaiGhe && set.add(s.loaiGhe))
  return Array.from(set)
})

const uniqueRooms = computed(() => {
  const set = new Set()
  seats.value.forEach(s => s.tenPhongChieu && set.add(s.tenPhongChieu))
  return Array.from(set)
})

const activeSeats = computed(() => seats.value.filter(s => s.trangThai === 'CON_TRONG').length)
const reservedSeats = computed(() => seats.value.filter(s => s.trangThai === 'DA_DAT').length)
const usingSeats = computed(() => seats.value.filter(s => s.trangThai === 'DANG_SU_DUNG').length)

function getStatusClass(status) {
  if (!status) return 'status-default'
  if (status === 'CON_TRONG') return 'status-active'
  if (status === 'DA_DAT') return 'status-inactive'
  if (status === 'DANG_SU_DUNG') return 'status-using'
  return 'status-default'
}

function getStatusLabel(status) {
  if (status === 'CON_TRONG') return 'Còn trống'
  if (status === 'DA_DAT') return 'Đã đặt'
  if (status === 'DANG_SU_DUNG') return 'Đang sử dụng'
  return status
}

onMounted(async () => {
  try {
    const response = await fetchSeats()
    seats.value = response.data.content
  } catch (error) {
    console.error('❌ Lỗi khi gọi API:', error)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
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
.header-actions {
  display: flex;
  gap: 12px;
}
.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}
.btn:hover::before { left: 100%; }
.btn-primary {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}
.btn-icon { font-size: 1rem; }
.btn-text { font-weight: 600; }
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
.filters-section {
  display: flex;
  gap: 1.5rem;
  align-items: end;
  flex-wrap: wrap;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  margin-bottom: 1.5rem;
}
.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  min-width: 200px;
  flex: 1;
  animation: slideInUp 0.5s ease-out;
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
.filter-icon { font-size: 1rem; opacity: 0.8; }
.filter-dropdown { position: relative; display: flex; align-items: center; }
.filter-select, .filter-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  background: white;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  appearance: none;
  font-weight: 500;
  padding-right: 2.5rem;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}
.filter-select:focus, .filter-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}
.filter-select:hover, .filter-input:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}
.filter-arrow {
  position: absolute;
  right: 1rem;
  color: #64748b;
  font-size: 0.75rem;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: bold;
}
.filter-dropdown:hover .filter-arrow { transform: rotate(180deg); color: #3b82f6; }
.filter-actions {
  display: flex;
  gap: 1rem;
  align-items: end;
  flex-wrap: wrap;
}
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}
.table-container {
  width: 100%;
  overflow-x: auto;
}
table {
  width: 100%;
  min-width: 1000px;
  border-collapse: collapse;
  background-color: #fff;
  table-layout: auto;
}
th, td {
  border: 1px solid #e2e8f0;
  padding: 10px 14px;
  text-align: left;
  color: #333;
  white-space: nowrap;
  font-size: 15px;
}
th {
  background-color: #f8f9fa;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}
tr:nth-child(even) { background-color: #f9f9f9; }
.actions-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.edit.action-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.edit.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
.delete.action-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}
.delete.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}
.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.status-active {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: white;
}
.status-inactive {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}
.status-using {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e42 100%);
  color: white;
}
.status-default {
  background: #ecf0f1;
  color: #7f8c8d;
}
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-top: 32px;
}
.pagination-info {
  color: #7f8c8d;
  font-size: 14px;
}
.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}
.pagination-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}
.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
}
.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-numbers {
  display: flex;
  gap: 8px;
}
.page-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}
.page-btn:hover {
  border-color: #667eea;
  color: #667eea;
}
.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease-in-out;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 1rem;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  animation: slideDown 0.3s ease-in-out;
}

@keyframes slideInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
