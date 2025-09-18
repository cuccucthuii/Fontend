<template>
  <div class="invoice-page">
    <!-- Tabs -->
    <div class="tabs">
      <button :class="['tab-btn', { active: !showTrash }]" @click="showTrash = false">Tất cả hóa đơn</button>
      <button :class="['tab-btn', { active: showTrash }]" @click="showTrash = true">Thùng rác</button>
    </div>
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="header-icon">🧾</span>
          Quản lý Hóa Đơn
        </h1>
        <p class="page-subtitle">Quản lý danh sách hóa đơn, trạng thái và khách hàng</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddModal = true">
          <span class="btn-icon">➕</span>
          <span class="btn-text">Thêm hóa đơn</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">🧾</div>
        <div>
          <div class="stat-number">{{ invoices.length }}</div>
          <div class="stat-label">Tổng số hóa đơn</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div>
          <div class="stat-number">{{ shouldShowTotalAmount ? formatCurrency(totalAmount) : '-' }}</div>
          <div class="stat-label">Tổng tiền</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div>
          <div class="stat-number">{{ paidInvoices }}</div>
          <div class="stat-label">Đã thanh toán</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏳</div>
        <div>
          <div class="stat-number">{{ unpaidInvoices }}</div>
          <div class="stat-label">Chờ thanh toán</div>
        </div>
      </div>
    </div>

    <!-- Filters & Search -->
    <div class="filters-section">
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🔍</span> Tìm kiếm</label>
        <input v-model="searchQuery" class="filter-input" placeholder="Tìm mã hóa đơn, khách hàng, nhân viên..." />
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">🏷️</span> Trạng thái</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="DA_THANH_TOAN">Đã thanh toán</option>
          <option value="CHO_THANH_TOAN">Chờ thanh toán</option>
        </select>
        <span class="filter-arrow">▼</span>
      </div>
      <div class="filter-group">
        <label class="filter-label"><span class="filter-icon">📄</span> Loại hóa đơn</label>
        <select v-model="typeFilter" class="filter-select">
          <option value="">Tất cả loại</option>
          <option v-for="type in uniqueTypes" :key="type" :value="type">{{ type }}</option>
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
      <table v-if="paginatedInvoices.length">
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã Hóa Đơn</th>
            <th>Tổng Tiền</th>
            <th>Tiền Giảm</th>
            <th>Ngày Đặt</th>
            <th>Loại</th>
            <th>Trạng Thái</th>
            <th>Khách Hàng</th>
            <th>SĐT Khách</th>
            <th>Nhân Viên</th>
            <th>Voucher</th>
            <th>Số Tiền Giảm</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(invoice, index) in paginatedInvoices" :key="invoice.idHoaDon">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ invoice.tenHoaDon }}</td>
            <td>{{ formatCurrency(invoice.tongTien) }}</td>
            <td>{{ formatCurrency(invoice.tienGiam) }}</td>
            <td>{{ formatDate(invoice.ngayDat) }}</td>
            <td>{{ invoice.loaiHoaDon }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(invoice.trangThai)]">
                {{ invoice.trangThai }}
              </span>
            </td>
            <td>{{ invoice.tenKhachHang }}</td>
            <td>{{ invoice.soDienThoaiKhachHang }}</td>
            <td>{{ invoice.tenNhanVien || '-' }}</td>
            <td>{{ invoice.tenVoucher || '-' }}</td>
            <td>{{ invoice.soTienGiam != null ? formatCurrency(invoice.soTienGiam) : '-' }}</td>
            <td class="actions-cell">
              <button v-if="!showTrash" class="edit action-btn" @click="openEditModal(invoice)">✏️</button>
              <button v-if="!showTrash" class="delete action-btn" @click="softDeleteInvoice(invoice)">❌</button>
              <button v-if="showTrash" class="restore action-btn" @click="restoreInvoice(invoice)">♻️</button>
              <button v-if="showTrash" class="delete action-btn" @click="permanentlyDeleteInvoice(invoice)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="loading-state">Đang tải dữ liệu...</p>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages > 1">
      <div class="pagination-info">
        Hiển thị {{ paginatedInvoices.length }} hóa đơn trên tổng số {{ filteredInvoices.length }} hóa đơn
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

    <!-- Toast Notification -->
    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>

    <!-- Modal Thêm hóa đơn -->
    <transition name="modal-fade">
      <div v-if="showAddModal" class="modal-overlay" @click.self="closeAddModal">
        <div class="modal-card modal-large">
          <div class="modal-header">
            <h2>Thêm hóa đơn mới</h2>
            <button class="modal-close" @click="closeAddModal">✕</button>
          </div>
          <form class="add-form" @submit.prevent="submitAddForm">
            <div class="form-row">
              <div class="form-group">
                <label>Mã hóa đơn</label>
                <input v-model="addForm.tenHoaDon" required placeholder="Nhập mã hóa đơn" />
              </div>
              <div class="form-group">
                <label>Tổng tiền</label>
                <input v-model.number="addForm.tongTien" type="number" min="0" required placeholder="Tổng tiền" />
              </div>
              <div class="form-group">
                <label>Tiền giảm</label>
                <input v-model.number="addForm.tienGiam" type="number" min="0" placeholder="Tiền giảm" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Ngày đặt</label>
                <input v-model="addForm.ngayDat" type="date" required />
              </div>
              <div class="form-group">
                <label>Loại hóa đơn</label>
                <input v-model="addForm.loaiHoaDon" required placeholder="Loại hóa đơn" />
              </div>
              <div class="form-group">
                <label>Trạng thái</label>
                <select v-model="addForm.trangThai" required>
                  <option value="">Chọn trạng thái</option>
                  <option value="Đã thanh toán">Đã thanh toán</option>
                  <option value="Chờ thanh toán">Chờ thanh toán</option>
                </select>
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
                <label>Nhân viên</label>
                <input v-model="addForm.tenNhanVien" placeholder="Tên nhân viên" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Voucher</label>
                <input v-model="addForm.tenVoucher" placeholder="Tên voucher" />
              </div>
              <div class="form-group">
                <label>Số tiền giảm</label>
                <input v-model.number="addForm.soTienGiam" type="number" min="0" placeholder="Số tiền giảm" />
              </div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeAddModal" :disabled="isAdding">
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isAdding">
                <span v-if="isAdding" class="loading-spinner-btn"></span>
                Thêm hóa đơn
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { fetchInvoices } from '../services/invoiceService'
import * as XLSX from 'xlsx'

const invoices = ref([])
const currentPage = ref(1)
const pageSize = 7
const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const isExporting = ref(false)
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null
const showAddModal = ref(false)
const isAdding = ref(false)
const showTrash = ref(false)
const addForm = ref({
  tenHoaDon: '',
  tongTien: '',
  tienGiam: '',
  ngayDat: '',
  loaiHoaDon: '',
  trangThai: '',
  tenKhachHang: '',
  soDienThoaiKhachHang: '',
  tenNhanVien: '',
  tenVoucher: '',
  soTienGiam: ''
})

watch(showTrash, (val) => {
  if (val) {
    console.log('Invoices khi vào tab Thùng rác:', JSON.parse(JSON.stringify(invoices.value)))
  }
})

function openAddModal() { showToast('🚧 Chức năng đang phát triển!', 'info') }
function openEditModal(invoice) { showToast('🚧 Chức năng đang phát triển!', 'info') }
function openDeleteModal(invoice) { showToast('🚧 Chức năng đang phát triển!', 'info') }

function showToast(message, type = 'info') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => (toast.value.show = false), 2500)
}

const filteredInvoices = computed(() => {
  let filtered = invoices.value
  if (showTrash.value) {
    filtered = filtered.filter(inv => Boolean(inv.daXoa) === true)
  } else {
    filtered = filtered.filter(inv => !inv.daXoa)
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    filtered = filtered.filter(inv =>
      (inv.tenHoaDon || '').toLowerCase().includes(q) ||
      (inv.tenKhachHang || '').toLowerCase().includes(q) ||
      (inv.tenNhanVien || '').toLowerCase().includes(q)
    )
  }
  if (statusFilter.value) {
    filtered = filtered.filter(inv => inv.trangThai === statusFilter.value)
  }
  if (typeFilter.value) {
    filtered = filtered.filter(inv => inv.loaiHoaDon === typeFilter.value)
  }
  return filtered
})

const paginatedInvoices = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredInvoices.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredInvoices.value.length / pageSize))

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
  return searchQuery.value || statusFilter.value || typeFilter.value
})

function clearFilters() {
  searchQuery.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  showToast('🔄 Đã xóa tất cả bộ lọc', 'info')
}

const uniqueTypes = computed(() => {
  const set = new Set()
  invoices.value.forEach(inv => inv.loaiHoaDon && set.add(inv.loaiHoaDon))
  return Array.from(set)
})

const paidInvoices = computed(() => invoices.value.filter(inv => inv.trangThai === 'Đã thanh toán' && !inv.daXoa).length)
const unpaidInvoices = computed(() => invoices.value.filter(inv => inv.trangThai === 'Chờ thanh toán' && !inv.daXoa).length)
const totalAmount = computed(() => invoices.value.filter(inv => !inv.daXoa).reduce((sum, inv) => sum + (inv.tongTien || 0), 0))
const shouldShowTotalAmount = computed(() => paidInvoices.value + unpaidInvoices.value > 0)

function getStatusClass(status) {
  if (!status) return 'status-default'
  if (status === 'Đã thanh toán') return 'status-active'
  if (status === 'Chờ thanh toán') return 'status-inactive'
  return 'status-default'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [year, month, day] = dateStr.split('-')
  return `${day}/${month}/${year}`
}

function formatCurrency(amount) {
  if (amount == null) return '-'
  return amount.toLocaleString('vi-VN') + ' ₫'
}

// Xuất Excel
function exportData() {
  isExporting.value = true
  try {
    const data = filteredInvoices.value.map(inv => ({
      'Mã hóa đơn': inv.tenHoaDon,
      'Tổng tiền': inv.tongTien,
      'Tiền giảm': inv.tienGiam,
      'Ngày đặt': formatDate(inv.ngayDat),
      'Loại': inv.loaiHoaDon,
      'Trạng thái': inv.trangThai,
      'Khách hàng': inv.tenKhachHang,
      'SĐT khách': inv.soDienThoaiKhachHang,
      'Nhân viên': inv.tenNhanVien || '-',
      'Voucher': inv.tenVoucher || '-',
      'Số tiền giảm': inv.soTienGiam != null ? inv.soTienGiam : '-'
    }))
    if (data.length === 0) {
      showToast('⚠️ Không có dữ liệu để xuất!', 'error')
      return
    }
    const workbook = XLSX.utils.book_new()
    const worksheet = XLSX.utils.json_to_sheet(data)
    worksheet['!cols'] = [
      { wch: 20 }, { wch: 15 }, { wch: 15 }, { wch: 15 }, { wch: 15 }, { wch: 18 }, { wch: 20 }, { wch: 15 }, { wch: 18 }, { wch: 18 }, { wch: 15 }
    ]
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Danh sách hóa đơn')
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array', bookSST: false })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `danh-sach-hoa-don-${new Date().toISOString().split('T')[0]}.xlsx`)
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

function closeAddModal() {
  showAddModal.value = false
  resetAddForm()
}

function resetAddForm() {
  addForm.value = {
    tenHoaDon: '',
    tongTien: '',
    tienGiam: '',
    ngayDat: '',
    loaiHoaDon: '',
    trangThai: '',
    tenKhachHang: '',
    soDienThoaiKhachHang: '',
    tenNhanVien: '',
    tenVoucher: '',
    soTienGiam: ''
  }
}

async function submitAddForm() {
  isAdding.value = true
  try {
    if (!addForm.value.tenHoaDon || !addForm.value.tongTien || !addForm.value.ngayDat || !addForm.value.loaiHoaDon || !addForm.value.trangThai || !addForm.value.tenKhachHang || !addForm.value.soDienThoaiKhachHang) {
      showToast('⚠️ Vui lòng nhập đầy đủ thông tin bắt buộc!', 'error')
      isAdding.value = false
      return
    }
    invoices.value.unshift({
      idHoaDon: Date.now(),
      ...addForm.value,
      daXoa: false
    })
    showToast('🎉 Thêm hóa đơn thành công!', 'success')
    closeAddModal()
  } catch (e) {
    showToast('❌ Lỗi khi thêm hóa đơn!', 'error')
  } finally {
    isAdding.value = false
  }
}

// Soft delete (thùng rác)
function softDeleteInvoice(invoice) {
  const idx = invoices.value.findIndex(i => i.idHoaDon === invoice.idHoaDon)
  if (idx !== -1) {
    invoices.value[idx] = { ...invoices.value[idx], daXoa: true }
    console.log('Đã xóa:', invoices.value[idx])
    showToast('🗑️ Đã chuyển vào thùng rác!', 'info')
  }
}
function restoreInvoice(invoice) {
  const idx = invoices.value.findIndex(i => i.idHoaDon === invoice.idHoaDon)
  if (idx !== -1) {
    invoices.value[idx] = { ...invoices.value[idx], daXoa: false }
    showToast('♻️ Đã khôi phục hóa đơn!', 'success')
  }
}
function permanentlyDeleteInvoice(invoice) {
  const idx = invoices.value.findIndex(i => i.idHoaDon === invoice.idHoaDon)
  if (idx !== -1) {
    invoices.value.splice(idx, 1)
    showToast('🗑️ Đã xóa vĩnh viễn hóa đơn!', 'success')
  }
}

onMounted(async () => {
  try {
    const response = await fetchInvoices()
    // Đảm bảo mỗi hóa đơn đều có trường daXoa
    invoices.value = (response.data || []).map(inv => ({
      ...inv,
      daXoa: inv.daXoa === true ? true : false
    }))
  } catch (error) {
    showToast('❌ Lỗi khi tải dữ liệu!', 'error')
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
.invoice-page {
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
  min-width: 1200px;
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
  grid-template-columns: 1fr 1fr 1fr;
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
.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}
.tab-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px 8px 0 0;
  background: #e5e7eb;
  color: #374151;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}
.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}
.restore.action-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}
.restore.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}
</style>
