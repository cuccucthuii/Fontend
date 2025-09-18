<template>
  <div class="bill-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🎟️</span>
          Quản lý Vé Phim
        </h1>
        <p class="page-subtitle">Quản lý danh sách vé, trạng thái và khách hàng</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddModal = true">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm vé</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🎟️</div>
        <div>
          <div class="stat-number">{{ bills.length }}</div>
          <div class="stat-label">Tổng số vé</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div>
          <div class="stat-number">{{ formatCurrency(totalRevenue) }}</div>
          <div class="stat-label">Tổng doanh thu</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div>
          <div class="stat-number">{{ paidBills }}</div>
          <div class="stat-label">Đã thanh toán</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏳</div>
        <div>
          <div class="stat-number">{{ unpaidBills }}</div>
          <div class="stat-label">Chưa thanh toán</div>
        </div>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm tên phim, khách hàng, nhân viên..." />
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏷️</span> Trạng thái</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="DA_BAN">Đã bán</option>
          <option value="DA_DAT">Đã đặt</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏢</span> Rạp chiếu</label>
        <select v-model="cinemaFilter" class="filter-select">
          <option value="">Tất cả rạp</option>
          <option v-for="cinema in uniqueCinemas" :key="cinema" :value="cinema">{{ cinema }}</option>
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
      <table v-if="paginatedBills.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Phim</th>
            <th>Giá Vé</th>
            <th>Ngày Đặt</th>
            <th>Trạng Thái</th>
            <th>Nhân Viên</th>
            <th>Suất Chiếu</th>
            <th>Ngày Chiếu</th>
            <th>Giờ Chiếu</th>
            <th>Ghế</th>
            <th>Khách Hàng</th>
            <th>SĐT Khách</th>
            <th>Phòng Chiếu</th>
            <th>Rạp Chiếu</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(bill, index) in paginatedBills" :key="bill.idVePhim">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ bill.tenPhim }}</td>
            <td>{{ formatCurrency(bill.giaVe) }}</td>
            <td>{{ formatDate(bill.ngayDat) }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(bill.trangThai)]">
                {{ bill.trangThai }}
              </span>
            </td>
            <td>{{ bill.tenNhanVien }}</td>
            <td>{{ bill.tenSuatChieu }}</td>
            <td>{{ formatDate(bill.ngayChieu) }}</td>
            <td>{{ bill.gioChieu }}</td>
            <td>{{ bill.soGhe }}</td>
            <td>{{ bill.tenKhachHang }}</td>
            <td>{{ bill.soDienThoaiKhachHang }}</td>
            <td>{{ bill.tenPhongChieu }}</td>
            <td>{{ bill.tenRapChieu }}</td>
            <td class="actions-cell">
              <button class="edit action-btn">✏️</button>
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
        Hiển thị {{ paginatedBills.length }} vé trên tổng số {{ filteredBills.length }} vé
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

    <!-- Modal Thêm vé -->
    <transition name="modal-fade">
      <div v-if="showAddModal" class="modal-overlay" @click.self="closeAddModal">
        <div class="modal-card modal-large">
          <div class="modal-header">
            <h2>Thêm vé mới</h2>
            <button class="modal-close" @click="closeAddModal">✕</button>
          </div>
          <form class="add-form" @submit.prevent="submitAddForm">
            <div class="form-row">
              <div class="form-group">
                <label>Tên phim</label>
                <input v-model="addForm.tenPhim" required placeholder="Tên phim" />
              </div>
              <div class="form-group">
                <label>Giá vé</label>
                <input v-model.number="addForm.giaVe" type="number" min="0" required placeholder="Giá vé" />
              </div>
              <div class="form-group">
                <label>Ngày đặt</label>
                <input v-model="addForm.ngayDat" type="date" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Trạng thái</label>
                <select v-model="addForm.trangThai" required>
                  <option value="">Chọn trạng thái</option>
                  <option value="DA_BAN">Đã bán</option>
                  <option value="DA_DAT">Đã đặt</option>
                </select>
              </div>
              <div class="form-group">
                <label>Nhân viên</label>
                <input v-model="addForm.tenNhanVien" placeholder="Tên nhân viên" />
              </div>
              <div class="form-group">
                <label>Suất chiếu</label>
                <input v-model="addForm.tenSuatChieu" placeholder="Suất chiếu" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Ngày chiếu</label>
                <input v-model="addForm.ngayChieu" type="date" />
              </div>
              <div class="form-group">
                <label>Giờ chiếu</label>
                <input v-model="addForm.gioChieu" placeholder="Giờ chiếu" />
              </div>
              <div class="form-group">
                <label>Ghế</label>
                <input v-model="addForm.soGhe" placeholder="Ghế" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Khách hàng</label>
                <input v-model="addForm.tenKhachHang" required placeholder="Tên khách hàng" />
              </div>
              <div class="form-group">
                <label>SĐT khách</label>
                <input v-model="addForm.soDienThoaiKhachHang" required placeholder="Số điện thoại" />
              </div>
              <div class="form-group">
                <label>Phòng chiếu</label>
                <input v-model="addForm.tenPhongChieu" placeholder="Phòng chiếu" />
              </div>
              <div class="form-group">
                <label>Rạp chiếu</label>
                <input v-model="addForm.tenRapChieu" placeholder="Rạp chiếu" />
              </div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeAddModal" :disabled="isAdding">
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isAdding">
                <span v-if="isAdding" class="loading-spinner-btn"></span>
                Thêm vé
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const bills = ref([])
const currentPage = ref(1)
const pageSize = 7
const searchQuery = ref('')
const statusFilter = ref('')
const cinemaFilter = ref('')
const roomFilter = ref('')

const filteredBills = computed(() => {
  let filtered = bills.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    filtered = filtered.filter(bill =>
      (bill.tenPhim || '').toLowerCase().includes(q) ||
      (bill.tenKhachHang || '').toLowerCase().includes(q) ||
      (bill.tenNhanVien || '').toLowerCase().includes(q)
    )
  }
  if (statusFilter.value) {
    filtered = filtered.filter(bill => bill.trangThai === statusFilter.value)
  }
  if (cinemaFilter.value) {
    filtered = filtered.filter(bill => bill.tenRapChieu === cinemaFilter.value)
  }
  if (roomFilter.value) {
    filtered = filtered.filter(bill => bill.tenPhongChieu === roomFilter.value)
  }
  return filtered
})

const paginatedBills = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredBills.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredBills.value.length / pageSize))

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
  return searchQuery.value || statusFilter.value || cinemaFilter.value || roomFilter.value
})

function clearFilters() {
  searchQuery.value = ''
  statusFilter.value = ''
  cinemaFilter.value = ''
  roomFilter.value = ''
}

const uniqueCinemas = computed(() => {
  const set = new Set()
  bills.value.forEach(b => b.tenRapChieu && set.add(b.tenRapChieu))
  return Array.from(set)
})

const uniqueRooms = computed(() => {
  const set = new Set()
  bills.value.forEach(b => b.tenPhongChieu && set.add(b.tenPhongChieu))
  return Array.from(set)
})

const paidBills = computed(() => bills.value.filter(b => b.trangThai === 'Đã thanh toán').length)
const unpaidBills = computed(() => bills.value.filter(b => b.trangThai === 'Chưa thanh toán').length)
const totalRevenue = computed(() => bills.value.reduce((sum, b) => sum + (b.giaVe || 0), 0))

function getStatusClass(status) {
  if (!status) return 'status-default'
  if (status === 'Đã thanh toán') return 'status-active'
  if (status === 'Chưa thanh toán') return 'status-inactive'
  return 'status-default'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function formatCurrency(amount) {
  if (!amount) return ''
  return amount.toLocaleString('vi-VN') + ' ₫'
}

const showAddModal = ref(false)
const isAdding = ref(false)
const addForm = ref({
  tenPhim: '',
  giaVe: '',
  ngayDat: '',
  trangThai: '',
  tenNhanVien: '',
  tenSuatChieu: '',
  ngayChieu: '',
  gioChieu: '',
  soGhe: '',
  tenKhachHang: '',
  soDienThoaiKhachHang: '',
  tenPhongChieu: '',
  tenRapChieu: ''
})

function closeAddModal() {
  showAddModal.value = false
  resetAddForm()
}

function resetAddForm() {
  addForm.value = {
    tenPhim: '',
    giaVe: '',
    ngayDat: '',
    trangThai: '',
    tenNhanVien: '',
    tenSuatChieu: '',
    ngayChieu: '',
    gioChieu: '',
    soGhe: '',
    tenKhachHang: '',
    soDienThoaiKhachHang: '',
    tenPhongChieu: '',
    tenRapChieu: ''
  }
}

async function submitAddForm() {
  isAdding.value = true
  try {
    // Validate cơ bản
    if (!addForm.value.tenPhim || !addForm.value.giaVe || !addForm.value.ngayDat || !addForm.value.trangThai || !addForm.value.tenKhachHang || !addForm.value.soDienThoaiKhachHang) {
      alert('Vui lòng nhập đầy đủ thông tin bắt buộc!')
      isAdding.value = false
      return
    }
    bills.value.unshift({
      idVePhim: Date.now(),
      ...addForm.value
    })
    alert('Thêm vé thành công!')
    closeAddModal()
  } catch (e) {
    alert('Lỗi khi thêm vé!')
  } finally {
    isAdding.value = false
  }
}

onMounted(async () => {
  try {
    const response = await axios.get('/api/bookings/history')
    // Đảm bảo mỗi vé đều có trường daXoa nếu có soft delete
    bills.value = (response.data || []).map(bill => ({
      ...bill,
      daXoa: bill.daXoa === true ? true : false
    }))
  } catch (error) {
    console.error('❌ Lỗi khi gọi API:', error)
  }
})
</script>

<style scoped>
.bill-page {
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
  min-width: 1300px;
  border-collapse: collapse;
  background-color: #fff;
  table-layout: auto;
}
th, td {
  border: 1px solid #ddd;
  padding: 10px 14px;
  text-align: left;
  color: #333;
  white-space: nowrap;
}
th {
  background-color: #f5f5f5;
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
@keyframes slideInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(44, 62, 80, 0.45);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.modal-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 40px rgba(44,62,80,0.18);
  padding: 32px 28px 24px;
  min-width: 320px;
  max-width: 95vw;
  animation: popIn 0.3s cubic-bezier(.4,2,.6,1);
  position: relative;
}
.modal-large { min-width: 600px; max-width: 800px; }
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ecf0f1;
}
.modal-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}
.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #7f8c8d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
}
.modal-close:hover {
  background: #ecf0f1;
  color: #2c3e50;
}
.add-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-group label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}
.form-group input,
.form-group select {
  padding: 12px 16px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 14px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}
.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}
.btn-secondary {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(108, 117, 125, 0.4);
}
.btn-secondary:hover {
  background: linear-gradient(135deg, #495057 0%, #343a40 100%);
}
</style>
