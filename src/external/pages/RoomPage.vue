<template>
  <div class="room-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🏢</span>
          Quản lý Phòng Chiếu
        </h1>
        <p class="page-subtitle">Quản lý danh sách phòng chiếu, trạng thái và rạp chiếu</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openAddModal">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm phòng</span>
        </button>
        <button class="btn btn-danger" @click="openTrashModal">
          <span class="btn-icon">🗑️</span>
          <span class="btn-text">Thùng rác</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🏢</div>
        <div>
          <div class="stat-number">{{ rooms.length }}</div>
          <div class="stat-label">Tổng số phòng</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div>
          <div class="stat-number">{{ activeRooms }}</div>
          <div class="stat-label">Đang hoạt động</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⛔</div>
        <div>
          <div class="stat-number">{{ inactiveRooms }}</div>
          <div class="stat-label">Ngừng hoạt động</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎬</div>
        <div>
          <div class="stat-number">{{ uniqueCinemas.length }}</div>
          <div class="stat-label">Số rạp</div>
        </div>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm tên phòng hoặc rạp..." />
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
        <label class="filter-label"><span class="filter-icon">🏷️</span> Trạng thái phòng</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="HOAT_DONG">🟢 Hoạt động</option>
          <option value="BAO_TRI">🟡 Bảo trì</option>
          <option value="TAM_DUNG">🔴 Tạm dừng</option>
          <option value="DONG_CUA">⚫ Đóng cửa</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🎬</span> Trạng thái rạp</label>
        <select v-model="cinemaStatusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="HOAT_DONG">🟢 Hoạt động</option>
          <option value="BAO_TRI">🟡 Bảo trì</option>
          <option value="TAM_DUNG">🔴 Tạm dừng</option>
          <option value="DONG_CUA">⚫ Đóng cửa</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-actions">
        <button class="btn btn-clear-filters" @click="clearFilters" v-if="hasActiveFilters">
          <span class="btn-icon">🔄</span>
          <span class="btn-text">Xóa bộ lọc</span>
        </button>
        <button class="btn btn-export" @click="exportData" :disabled="isExporting">
          <span v-if="isExporting" class="loading-spinner-btn"></span>
          <span v-else class="btn-icon">📊</span>
          <span class="btn-text">{{ isExporting ? 'Đang xuất...' : 'Xuất Excel' }}</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <table v-if="paginatedRooms && paginatedRooms.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Phòng</th>
            <th>Diện Tích (m²)</th>
            <th>Trạng Thái Phòng</th>
            <th>Rạp Chiếu</th>
            <th>Địa Chỉ Rạp</th>
            <th>SĐT Rạp</th>
            <th>Trạng Thái Rạp</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(room, index) in paginatedRooms" :key="room.idPhongChieu">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ room.tenPhongChieu || '' }}</td>
            <td>{{ room.dienTichPhong || '' }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(room.trangThaiPhongChieu)]">
                {{ formatStatus(room.trangThaiPhongChieu) }}
              </span>
            </td>
            <td>{{ room.tenRapChieu || '' }}</td>
            <td class="wrap-text">{{ room.diaChi || '' }}</td>
            <td>{{ room.soDienThoai || '' }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(room.trangThaiRapChieu)]">
                {{ formatStatus(room.trangThaiRapChieu) }}
              </span>
            </td>
            <td class="actions-cell">
              <button class="edit action-btn" @click="openEditModal(room)">✏️</button>
              <button class="delete action-btn" @click="openDeleteModal(room)">❌</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải dữ liệu...</p>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages >= 1">
      <div class="pagination-info">
        Hiển thị {{ paginatedRooms.length }} phòng trên tổng số {{ filteredRooms.length }} phòng
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

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <RoomForm
          :isEdit="isEdit"
          :room="selectedRoom"
          @submit="handleSubmitRoom"
          @cancel="closeModal"
        />
      </div>
    </div>

    <!-- Toast Notification -->
    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>

  </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue'
import { fetchRooms, fetchTrashRooms, deleteRoom, softDeleteRoom, restoreRoom, permanentlyDeleteRoom, createRoom, updateRoom } from '../services/roomService'
import RoomForm from '../components/RoomForm.vue'
import * as XLSX from 'xlsx'

const rooms = ref([])
const currentPage = ref(1)
const pageSize = 7
const searchQuery = ref('')
const statusFilter = ref('')
const cinemaFilter = ref('')
const cinemaStatusFilter = ref('')
const isExporting = ref(false)
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null

const showModal = ref(false)
const isEdit = ref(false)
const selectedRoom = ref(null)


function openAddModal() {
  selectedRoom.value = null
  isEdit.value = false
  showModal.value = true
}

function openEditModal(room) {
  selectedRoom.value = { ...room }
  isEdit.value = true
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function openDeleteModal(room) {
  const confirmDelete = window.confirm(`Bạn có chắc chắn muốn xóa phòng "${room.tenPhongChieu}" không?`)
  if (!confirmDelete) return

  try {
    await deleteRoom(room.idPhongChieu)
    rooms.value = rooms.value.filter(r => r.idPhongChieu !== room.idPhongChieu)
    showToast('✅ Đã xóa phòng thành công!', 'success')
  } catch (error) {
    console.error('Lỗi khi xóa phòng:', error)
    showToast('❌ Xóa phòng thất bại!', 'error')
  }
}

async function handleSubmitRoom(roomData) {
  try {
    if (isEdit.value && selectedRoom.value?.idPhongChieu) {
      await updateRoom(selectedRoom.value.idPhongChieu, roomData)
      showToast('✅ Cập nhật thành công', 'success')
    } else {
      await createRoom(roomData)
      showToast('✅ Thêm mới thành công', 'success')
    }
    const response = await fetchRooms()
    rooms.value = response.data
    closeModal()
  } catch (error) {
    console.error('Lỗi khi gửi form:', error)
    showToast('❌ Lỗi xử lý dữ liệu', 'error')
  }
}

function openTrashModal() { showToast('🚧 Chức năng đang phát triển!', 'info') }

function showToast(message, type = 'info') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => (toast.value.show = false), 2500)
}

const filteredRooms = computed(() => {
  let filtered = rooms.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    filtered = filtered.filter(room =>
      (room.tenPhongChieu || '').toLowerCase().includes(q) ||
      (room.tenRapChieu || '').toLowerCase().includes(q)
    )
  }
  if (cinemaFilter.value) {
    filtered = filtered.filter(room => room.tenRapChieu === cinemaFilter.value)
  }
  if (statusFilter.value) {
    filtered = filtered.filter(room => room.trangThaiPhongChieu === statusFilter.value)
  }
  if (cinemaStatusFilter.value) {
    filtered = filtered.filter(room => room.trangThaiRapChieu === cinemaStatusFilter.value)
  }
  return filtered
})

const paginatedRooms = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredRooms.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredRooms.value.length / pageSize))

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
  return searchQuery.value || statusFilter.value || cinemaFilter.value || cinemaStatusFilter.value
})

function clearFilters() {
  searchQuery.value = ''
  statusFilter.value = ''
  cinemaFilter.value = ''
  cinemaStatusFilter.value = ''
  showToast('🔄 Đã xóa tất cả bộ lọc', 'info')
}

const uniqueCinemas = computed(() => {
  const set = new Set()
  rooms.value.forEach(r => r.tenRapChieu && set.add(r.tenRapChieu))
  return Array.from(set)
})

const activeRooms = computed(() => rooms.value.filter(r => r.trangThaiPhongChieu === 'HOAT_DONG').length)
const inactiveRooms = computed(() => rooms.value.filter(r => r.trangThaiPhongChieu !== 'HOAT_DONG').length)

function getStatusClass(status) {
  if (!status) return 'status-default'
  switch (status) {
    case 'HOAT_DONG':
      return 'status-active'
    case 'BAO_TRI':
      return 'status-warning'
    case 'TAM_DUNG':
      return 'status-inactive'
    case 'DONG_CUA':
      return 'status-danger'
    default:
      return 'status-default'
  }
}

function formatStatus(status) {
  if (!status) return 'Không xác định'
  switch (status) {
    case 'HOAT_DONG':
      return 'Hoạt động'
    case 'BAO_TRI':
      return 'Bảo trì'
    case 'TAM_DUNG':
      return 'Tạm dừng'
    case 'DONG_CUA':
      return 'Đóng cửa'
    default:
      return status
  }
}

// Xuất Excel
function exportData() {
  isExporting.value = true
  try {
    const data = filteredRooms.value.map(room => ({
      'Tên phòng': room.tenPhongChieu || '',
      'Diện tích (m²)': room.dienTichPhong || '',
      'Trạng thái phòng': room.trangThaiPhongChieu || '',
      'Rạp chiếu': room.tenRapChieu || '',
      'Địa chỉ rạp': room.diaChi || '',
      'SĐT rạp': room.soDienThoai || '',
      'Trạng thái rạp': room.trangThaiRapChieu || ''
    }))
    if (data.length === 0) {
      showToast('⚠️ Không có dữ liệu để xuất!', 'error')
      return
    }
    const workbook = XLSX.utils.book_new()
    const worksheet = XLSX.utils.json_to_sheet(data)
    worksheet['!cols'] = [
      { wch: 25 }, { wch: 15 }, { wch: 18 }, { wch: 20 }, { wch: 40 }, { wch: 18 }, { wch: 18 }
    ]
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Danh sách phòng')
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array', bookSST: false })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `danh-sach-phong-${new Date().toISOString().split('T')[0]}.xlsx`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    showToast('📊 Xuất Excel thành công!', 'success')
  } catch (error) {
    showToast('❌ Lỗi khi xuất dữ liệu!', 'error')
  } finally {
    isExporting.value = false
  }
}

onMounted(async () => {
  try {
    const response = await fetchRooms()
    rooms.value = response.data
  } catch (error) {
    showToast('❌ Lỗi khi tải dữ liệu!', 'error')
  }
})
</script>

<style scoped>
.room-page {
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
.btn-danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}
.btn-clear-filters {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(108, 117, 125, 0.4);
  padding: 0.875rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.btn-export {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.2);
  padding: 0.875rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.btn:disabled, .btn-export:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
.table-container {
  width: 100%;
  overflow-x: auto;
}
table {
  width: 100%;
  min-width: 1100px;
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
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}
.status-warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}
.status-inactive {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}
.status-danger {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(107, 114, 128, 0.3);
}
.status-default {
  background: #ecf0f1;
  color: #7f8c8d;
}
.wrap-text {
  max-width: 300px;
  white-space: pre-wrap;
  word-wrap: break-word;
  overflow: auto;
}
.loading-state {
  text-align: center;
  padding: 60px 20px;
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

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(30, 41, 59, 0.5); /* màu nền mờ */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1500;
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.35s ease-out;
  position: relative;
}


.toast.success { border-left: 6px solid #28a745; }
.toast.error { border-left: 6px solid #ff5252; }
.toast.info { border-left: 6px solid #667eea; }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity 0.3s; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; }
@keyframes popIn {
  from { transform: translateX(-50%) scale(0.85); opacity: 0; }
  to { transform: translateX(-50%) scale(1); opacity: 1; }
}
@keyframes slideInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
@media (max-width: 900px) {
  .page-header, .pagination-section { flex-direction: column; gap: 20px; padding: 20px; }
  .header-actions { width: 100%; justify-content: stretch; }
  .btn { flex: 1; justify-content: center; }
  .stats-section { grid-template-columns: 1fr; }
  .filters-section { flex-direction: column; gap: 1rem; padding: 1rem; }
  .filter-group { min-width: 100%; }
  .filter-actions { width: 100%; justify-content: stretch; }
  .btn-clear-filters, .btn-export { flex: 1; justify-content: center; }
}

</style>
