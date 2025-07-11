<template>
  <div class="branch-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🏢</span>
          Quản lý Rạp Chiếu
        </h1>
        <p class="page-subtitle">Quản lý danh sách rạp chiếu, trạng thái và thông tin liên hệ</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddModal = true">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm Rạp</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🏢</div>
        <div>
          <div class="stat-number">{{ branches.length }}</div>
          <div class="stat-label">Tổng số rạp</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div>
          <div class="stat-number">{{ activeBranches }}</div>
          <div class="stat-label">Hoạt động</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⛔</div>
        <div>
          <div class="stat-number">{{ inactiveBranches }}</div>
          <div class="stat-label">Không hoạt động</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🛠️</div>
        <div>
          <div class="stat-number">{{ maintenanceBranches }}</div>
          <div class="stat-label">Bảo trì</div>
        </div>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm tên rạp, địa chỉ, số điện thoại..." />
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏷️</span> Trạng thái</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="HOAT_DONG">Hoạt động</option>
          <option value="KHONG_HOAT_DONG">Không hoạt động</option>
          <option value="BAO_TRI">Bảo trì</option>
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
      <table v-if="paginatedBranches.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Rạp</th>
            <th>Địa Chỉ</th>
            <th>Số Điện Thoại</th>
            <th>Trạng Thái</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(branch, index) in paginatedBranches" :key="branch.idRapChieu">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ branch.tenRapChieu }}</td>
            <td class="wrap-text">{{ branch.diaChi }}</td>
            <td>{{ branch.soDienThoai }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(branch.trangThaiRapChieu)]">
                {{ getStatusLabel(branch.trangThaiRapChieu) }}
              </span>
            </td>
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
        Hiển thị {{ paginatedBranches.length }} rạp trên tổng số {{ filteredBranches.length }} rạp
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchBranches } from '../services/branchService'

const branches = ref([])
const currentPage = ref(1)
const pageSize = 7
const searchQuery = ref('')
const statusFilter = ref('')

const filteredBranches = computed(() => {
  let filtered = branches.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    filtered = filtered.filter(branch =>
      (branch.tenRapChieu || '').toLowerCase().includes(q) ||
      (branch.diaChi || '').toLowerCase().includes(q) ||
      (branch.soDienThoai || '').toLowerCase().includes(q)
    )
  }
  if (statusFilter.value) {
    filtered = filtered.filter(branch => branch.trangThaiRapChieu === statusFilter.value)
  }
  return filtered
})

const paginatedBranches = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredBranches.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredBranches.value.length / pageSize))

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
  return searchQuery.value || statusFilter.value
})

function clearFilters() {
  searchQuery.value = ''
  statusFilter.value = ''
}

const activeBranches = computed(() => branches.value.filter(b => b.trangThaiRapChieu === 'HOAT_DONG').length)
const inactiveBranches = computed(() => branches.value.filter(b => b.trangThaiRapChieu === 'KHONG_HOAT_DONG').length)
const maintenanceBranches = computed(() => branches.value.filter(b => b.trangThaiRapChieu === 'BAO_TRI').length)

function getStatusClass(status) {
  if (!status) return 'status-default'
  if (status === 'HOAT_DONG') return 'status-active'
  if (status === 'KHONG_HOAT_DONG') return 'status-inactive'
  if (status === 'BAO_TRI') return 'status-maintenance'
  return 'status-default'
}

function getStatusLabel(status) {
  if (status === 'HOAT_DONG') return 'Hoạt động'
  if (status === 'KHONG_HOAT_DONG') return 'Không hoạt động'
  if (status === 'BAO_TRI') return 'Bảo trì'
  return status
}

onMounted(async () => {
  try {
    const response = await fetchBranches()
    branches.value = response.data
  } catch (error) {
    console.error('❌ Lỗi khi gọi API:', error)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
.branch-page {
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
.wrap-text {
  max-width: 300px;
  white-space: pre-wrap;
  word-wrap: break-word;
  overflow: auto;
}
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
.status-maintenance {
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
@keyframes slideInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
