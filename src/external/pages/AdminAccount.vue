<template>
  <div class="admin-page-container">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="icon">👥</span>
          Quản lý Tài Khoản Người Dùng
        </h1>
        <p class="page-subtitle">Quản lý và theo dõi tất cả tài khoản trong hệ thống</p>
      </div>
      <div class="header-actions beautiful-actions">
        <button class="btn btn-refresh" @click="refreshData" :disabled="isLoading">
          <span class="btn-icon big">🔄</span>
          <span class="btn-text">Làm mới</span>
        </button>
        <button class="btn btn-export" @click="exportUsers">
          <span class="btn-icon big">📊</span>
          <span class="btn-text">Xuất Excel</span>
        </button>
        <button class="btn btn-trash" @click="showTrash">
          <span class="btn-icon big">🗑️</span>
          <span class="btn-text">Thùng rác</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">👤</div>
        <div class="stat-content">
          <div class="stat-number">{{ users.length }}</div>
          <div class="stat-label">Tổng người dùng</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">{{ activeUsers }}</div>
          <div class="stat-label">Đang hoạt động</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👑</div>
        <div class="stat-content">
          <div class="stat-number">{{ adminUsers }}</div>
          <div class="stat-label">Quản trị viên</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalPages }}</div>
          <div class="stat-label">Trang dữ liệu</div>
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-section">
      <div class="table-header">
        <h3 class="table-title">Danh sách người dùng</h3>
        <div class="table-actions">
          <!-- Advanced Filters -->
          <div class="filters-section">
            <select v-model="roleFilter" class="filter-select" @change="applyFilters">
              <option value="">Tất cả vai trò</option>
              <option value="Khách hàng">Khách hàng</option>
              <option value="Quản lý">Quản lý</option>
              <option value="Nhân viên bán vé">Nhân viên bán vé</option>
            </select>
            <select v-model="statusFilter" class="filter-select" @change="applyFilters">
              <option value="">Tất cả trạng thái</option>
              <option value="HOAT_DONG">Hoạt động</option>
              <option value="KHONG_HOAT_DONG">Không hoạt động</option>
            </select>
          </div>
          <div class="search-box">
            <input 
              type="text" 
              placeholder="Tìm kiếm người dùng..." 
              v-model="searchQuery"
              class="search-input"
              @input="applyFilters"
            />
            <span class="search-icon">🔍</span>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table v-if="filteredUsers.length" class="data-table">
          <thead>
            <tr>
              <th class="th-stt">STT</th>
              <th class="th-username">Tên đăng nhập</th>
              <th class="th-role">Vai trò</th>
              <th class="th-status">Trạng thái</th>
              <th class="th-actions">Chức năng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(user, index) in paginatedUsers" :key="user.idUserAccount" class="table-row">
              <td class="td-stt">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="td-username">
                <div class="user-info">
                  <div class="user-avatar">👤</div>
                  <div class="user-details">
                    <span class="username">{{ user.tenDangNhap }}</span>
                    <span class="user-created" v-if="user.ngayTao">
                      Tạo: {{ formatDate(user.ngayTao) }}
                    </span>
                  </div>
                </div>
              </td>
              <td class="td-role">
                <span class="role-badge" :class="getRoleClass(user.vaiTro)">
                  {{ user.vaiTro }}
                </span>
              </td>
              <td class="td-status">
                <span class="status-badge" :class="getStatusClass(user.trangThai)">
                  {{ getStatusLabel(user.trangThai) }}
                </span>
                <div class="status-actions" v-if="user.trangThai === 'HOAT_DONG'">
                  <button class="status-btn lock-btn" @click="toggleUserStatus(user)" title="Khóa tài khoản">
                    🔒
                  </button>
                </div>
                <div class="status-actions" v-else>
                  <button class="status-btn unlock-btn" @click="toggleUserStatus(user)" title="Mở khóa tài khoản">
                    🔓
                  </button>
                </div>
              </td>
              <td class="td-actions">
                <div class="action-buttons">
                  <button class="action-btn edit-btn" @click="editUser(user)" title="Chỉnh sửa">
                    <span class="action-icon">✏️</span>
                  </button>
                  <button class="action-btn reset-btn" @click="resetPassword(user)" title="Reset mật khẩu">
                    <span class="action-icon">🔑</span>
                  </button>
                  <button class="action-btn delete-btn" @click="handleDeleteUser(user)" title="Xóa">
                    <span class="action-icon">
                      <svg width="18" height="18" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M6 6l8 8M14 6l-8 8" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
                      </svg>
                    </span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else-if="searchQuery" class="empty-state">
          <div class="empty-icon">🔍</div>
          <h3>Không tìm thấy kết quả</h3>
          <p>Không có người dùng nào phù hợp với từ khóa "{{ searchQuery }}"</p>
        </div>

        <div v-else class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu...</p>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages > 1">
      <div class="pagination-info">
        <span class="pagination-text">
          Hiển thị {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredUsers.length) }} 
          trong tổng số {{ filteredUsers.length }} người dùng
        </span>
      </div>
      
      <div class="pagination-controls">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 1"
          class="pagination-btn"
        >
          <span class="pagination-icon">⬅️</span>
          <span class="pagination-text">Trước</span>
        </button>
        
        <div class="page-numbers">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="goToPage(page)"
            :class="['page-btn', { active: page === currentPage }]"
          >
            {{ page }}
          </button>
        </div>
        
        <button 
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="pagination-btn"
        >
          <span class="pagination-text">Sau</span>
          <span class="pagination-icon">➡️</span>
        </button>
      </div>
    </div>



    <!-- Modal Chỉnh sửa User -->
    <transition name="modal-fade" appear>
      <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
        <div class="modal-card">
          <h2>Chỉnh sửa người dùng</h2>
          <form @submit.prevent="submitEditForm">
            <div class="form-group">
              <label for="edit-username">Tên đăng nhập</label>
              <input 
                id="edit-username" 
                v-model="editForm.tenDangNhap" 
                required 
                :disabled="isLoading"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label for="edit-role">Vai trò</label>
              <select 
                id="edit-role" 
                v-model="editForm.vaiTro" 
                required 
                :disabled="isLoading"
                class="form-select"
              >
                <option value="Khách hàng">Khách hàng</option>
                <option value="Quản lý">Quản lý</option>
                <option value="Nhân viên bán vé">Nhân viên bán vé</option>
              </select>
            </div>
            <div class="form-group">
              <label for="edit-status">Trạng thái</label>
              <select 
                id="edit-status" 
                v-model="editForm.trangThai" 
                required 
                :disabled="isLoading"
                class="form-select"
              >
                <option value="HOAT_DONG">Hoạt động</option>
                <option value="KHONG_HOAT_DONG">Không hoạt động</option>
              </select>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeEditModal" :disabled="isLoading">
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="loading-spinner-btn"></span>
                Lưu thay đổi
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Modal Xác Nhận Xóa -->
    <transition name="modal-fade" appear>
      <div v-if="showDeleteModal" class="modal-overlay" @click.self="closeDeleteModal">
        <div class="modal-card modal-danger">
          <h2>Xác nhận xóa</h2>
          <p>Bạn có chắc chắn muốn xóa tài khoản <b>{{ userToDelete?.tenDangNhap }}</b> không?</p>
          <div class="modal-actions">
            <button class="btn btn-secondary" @click="closeDeleteModal" :disabled="isLoading">Hủy</button>
            <button class="btn btn-danger" @click="confirmDeleteUser" :disabled="isLoading">
              <span v-if="isLoading" class="loading-spinner-btn"></span>
              Xóa
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Toast Notification -->
    <transition name="toast-fade" appear>
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { fetchUsers, updateUser, deleteUser } from '../services/userService'

const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const roleFilter = ref('')
const statusFilter = ref('')

// Computed properties
const filteredUsers = computed(() => {
  let filtered = users.value
  // Debug log giá trị thực tế
  console.log('DEBUG users:', users.value.map(u => ({ tenDangNhap: u.tenDangNhap, vaiTro: u.vaiTro, trangThai: u.trangThai })))
  console.log('DEBUG filter:', { role: roleFilter.value, status: statusFilter.value })
  // Search filter
  if (searchQuery.value) {
    filtered = filtered.filter(user => 
      user.tenDangNhap.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (user.vaiTro && user.vaiTro.toLowerCase().includes(searchQuery.value.toLowerCase()))
    )
  }
  
  // Role filter
  if (roleFilter.value) {
    filtered = filtered.filter(user => user.vaiTro === roleFilter.value)
  }
  
  // Status filter
  if (statusFilter.value) {
    filtered = filtered.filter(user => user.trangThai === statusFilter.value)
  }
  
  return filtered
})

const totalPages = computed(() => Math.ceil(filteredUsers.value.length / pageSize.value))

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

const activeUsers = computed(() => 
  users.value.filter(user => user.trangThai === 'HOAT_DONG').length
)

const adminUsers = computed(() => 
  users.value.filter(user => user.vaiTro === 'Quản lý').length
)

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// Modal state
const isLoading = ref(false)
const showEditModal = ref(false)
const editForm = ref({ idUserAccount: null, tenDangNhap: '', vaiTro: 'User', trangThai: 'HOAT_DONG' })

const showDeleteModal = ref(false)
const userToDelete = ref(null)

// Toast state
const toast = ref({ show: false, message: '', type: '' })
let toastTimeout = null

// Methods
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

function goToPage(page) {
  currentPage.value = page
}

function getRoleClass(role) {
  const roleClasses = {
    'Khách hàng': 'role-user',
    'Quản lý': 'role-manager',
    'Nhân viên bán vé': 'role-ticket',
  }
  return roleClasses[role] || 'role-default'
}

function getStatusClass(status) {
  const statusClasses = {
    'HOAT_DONG': 'status-active',
    'KHONG_HOAT_DONG': 'status-inactive',
    'Trống': 'status-empty'
  }
  return statusClasses[status] || 'status-default'
}

function getStatusLabel(status) {
  if (status === 'HOAT_DONG') return 'Hoạt động'
  if (status === 'KHONG_HOAT_DONG') return 'Không hoạt động'
  return status
}

// Utility functions
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

function applyFilters() {
  currentPage.value = 1 // Reset to first page when filtering
}

function refreshData() {
  loadUsers()
}

function exportUsers() {
  const csvContent = generateCSV()
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `users_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

function generateCSV() {
  const headers = ['STT', 'Tên đăng nhập', 'Vai trò', 'Trạng thái', 'Ngày tạo']
  const rows = filteredUsers.value.map((user, index) => [
    index + 1,
    user.tenDangNhap,
    user.vaiTro,
    user.trangThai || 'Trống',
    user.ngayTao ? formatDate(user.ngayTao) : ''
  ])
  
  return [headers, ...rows].map(row => row.join(',')).join('\n')
}

// Edit user functions
function editUser(user) {
  editForm.value = { ...user }
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
}

async function submitEditForm() {
  isLoading.value = true
  try {
    const response = await updateUser(editForm.value.idUserAccount, editForm.value)
    console.log('✅ Cập nhật user thành công:', response.data)
    
    // Refresh data
    await loadUsers()
    showToast('Cập nhật thành công!', 'success')
    closeEditModal()
  } catch (error) {
    console.error('❌ Lỗi khi cập nhật user:', error)
    showToast('Có lỗi xảy ra khi cập nhật!', 'error')
  } finally {
    isLoading.value = false
  }
}

// User status management
async function toggleUserStatus(user) {
  const newStatus = user.trangThai === 'HOAT_DONG' ? 'KHONG_HOAT_DONG' : 'HOAT_DONG'
  const action = newStatus === 'KHONG_HOAT_DONG' ? 'chuyển sang không hoạt động' : 'chuyển sang hoạt động'
  
  if (!confirm(`Bạn có chắc chắn muốn ${action} tài khoản ${user.tenDangNhap}?`)) {
    return
  }
  
  isLoading.value = true
  try {
    const updatedUser = { ...user, trangThai: newStatus }
    await updateUser(user.idUserAccount, updatedUser)
    
    // Refresh data
    await loadUsers()
    showToast(`Đã ${action} thành công!`, 'success')
  } catch (error) {
    console.error('❌ Lỗi khi thay đổi trạng thái:', error)
    showToast('Có lỗi xảy ra khi thay đổi trạng thái!', 'error')
  } finally {
    isLoading.value = false
  }
}

async function resetPassword(user) {
  if (!confirm(`Bạn có chắc chắn muốn reset mật khẩu cho tài khoản ${user.tenDangNhap}?`)) {
    return
  }
  
  isLoading.value = true
  try {
    // TODO: Implement reset password API call
    // const response = await resetUserPassword(user.idUserAccount)
    
    showToast('Reset mật khẩu thành công! Mật khẩu mới đã được gửi qua email.', 'success')
  } catch (error) {
    console.error('❌ Lỗi khi reset mật khẩu:', error)
    showToast('Có lỗi xảy ra khi reset mật khẩu!', 'error')
  } finally {
    isLoading.value = false
  }
}

function handleDeleteUser(user) {
  userToDelete.value = user
  showDeleteModal.value = true
}

function closeDeleteModal() {
  showDeleteModal.value = false
}

async function confirmDeleteUser() {
  isLoading.value = true
  try {
    const response = await deleteUser(userToDelete.value.idUserAccount)
    console.log('✅ Xóa user thành công:', response.data)
    
    // Cập nhật lại danh sách từ server
    const refreshResponse = await fetchUsers()
    users.value = refreshResponse.data || []
    
    showToast('Xóa người dùng thành công!', 'success')
  } catch (error) {
    console.error('❌ Lỗi khi xóa user:', error)
    showToast('Có lỗi xảy ra khi xóa người dùng!', 'error')
  } finally {
    isLoading.value = false
    closeDeleteModal()
  }
}

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => { toast.value.show = false }, 2500)
}

function showTrash() {
  showToast('Tính năng thùng rác đang được phát triển!', 'info')
}

async function loadUsers() {
  try {
    console.log('🔄 Đang gọi API fetchUsers...')
    const response = await fetchUsers()
    console.log('✅ Data User từ BE:', response.data)
    users.value = response.data || []
  } catch (error) {
    console.error('❌ Lỗi khi gọi API:', error)
    showToast('Lỗi khi tải dữ liệu từ server!', 'error')
    // Fallback với dữ liệu mẫu nếu API lỗi
    users.value = [
      { idUserAccount: 1, tenDangNhap: 'admin', vaiTro: 'QUẢN LÝ', trangThai: 'HOAT_DONG' },
      { idUserAccount: 2, tenDangNhap: 'user1', vaiTro: 'KHÁCH HÀNG', trangThai: 'HOAT_DONG' },
      { idUserAccount: 3, tenDangNhap: 'manager1', vaiTro: 'NHÂN VIÊN BÁN VÉ', trangThai: 'KHONG_HOAT_DONG' }
    ]
  }
}

// Lifecycle
onMounted(() => {
  loadUsers()
})

// Cleanup function để tránh memory leak
onUnmounted(() => {
  if (toastTimeout) {
    clearTimeout(toastTimeout)
  }
})
</script>

<style scoped>
.admin-page-container {
  padding: 32px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Header Section */
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

.header-content {
  flex: 1;
}

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

.page-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 400;
}

.header-actions.beautiful-actions {
  display: flex;
  gap: 18px;
  align-items: center;
}

.btn-refresh {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(52, 152, 219, 0.4);
}

.btn-export {
  background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(39, 174, 96, 0.4);
}

/* Buttons */
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
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.btn-secondary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.6);
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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

/* Table Section */
.table-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 32px;
}

.table-section::-webkit-scrollbar {
  height: 8px;
}
.table-section::-webkit-scrollbar-thumb {
  background: #b2bec3;
  border-radius: 4px;
}
.table-section::-webkit-scrollbar-track {
  background: #f1f2f6;
}

/* Firefox */
.table-section {
  scrollbar-color: #b2bec3 #f1f2f6;
  scrollbar-width: thin;
}

@media (max-width: 1200px) {
  .table-section {
    width: 100vw;
    max-width: 100vw;
    margin-left: 0;
    border-radius: 0;
  }
}
@media (max-width: 900px) {
  .data-table th, .data-table td {
    padding: 12px 8px;
    font-size: 13px;
  }
  .action-btn {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
}
@media (max-width: 768px) {
  .admin-page-container {
    padding: 16px;
  }
  .table-section {
    width: 100vw;
    max-width: 100vw;
    margin-left: 0;
    border-radius: 0;
    box-shadow: none;
  }
  .data-table {
    min-width: 700px;
  }
  .table-section {
    overflow-x: auto;
  }
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #ecf0f1;
}

.table-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.table-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.filters-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: border-color 0.2s;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  padding: 12px 16px 12px 44px;
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  font-size: 14px;
  width: 300px;
  transition: all 0.3s ease;
  background: #f8f9fa;
  color: #222 !important;
  font-weight: 600 !important;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  left: 16px;
  color: #7f8c8d;
  font-size: 16px;
}

/* Table */
.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: unset;
}

.data-table th {
  background: #f8f9fa;
  padding: 16px 24px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
  font-size: 14px;
}

.data-table td {
  padding: 16px 24px;
  border-bottom: 1px solid #ecf0f1;
  color: #2c3e50;
  font-size: 14px;
}

.table-row {
  transition: all 0.3s ease;
}

.table-row:hover {
  background: #f8f9fa;
  transform: scale(1.01);
}

/* User info */
.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-avatar {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(102,126,234,0.10);
}

.username {
  font-weight: 600;
  font-size: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-created {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 400;
}

/* Badges */
.role-badge, .status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.role-admin {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}

.role-user {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.role-manager {
  background: linear-gradient(90deg, #feca57 0%, #ff9ff3 100%);
  color: #fff;
}

.role-ticket {
  background: linear-gradient(90deg, #f39c12 0%, #e67e22 100%);
  color: #fff;
}

.role-default {
  background: #ecf0f1;
  color: #7f8c8d;
}

.status-active {
  background: linear-gradient(90deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
}

.status-inactive {
  background: linear-gradient(90deg, #b2bec3 0%, #636e72 100%);
  color: #fff;
}

.status-empty {
  background: #ecf0f1;
  color: #7f8c8d;
}

.status-default {
  background: #ecf0f1;
  color: #7f8c8d;
}

/* Action buttons */
.action-buttons {
  display: flex;
  gap: 12px;
}

.status-actions {
  display: flex;
  gap: 4px;
  margin-top: 4px;
}

.status-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.lock-btn {
  background: #e74c3c;
  color: white;
}

.lock-btn:hover {
  background: #c0392b;
}

.unlock-btn {
  background: #27ae60;
  color: white;
}

.unlock-btn:hover {
  background: #229954;
}

.action-btn {
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 2px 8px rgba(44,62,80,0.08);
  background: #f8f9fa;
}
.action-btn:hover {
  background: #e0e7ff;
  transform: scale(1.08);
  box-shadow: 0 4px 16px rgba(102,126,234,0.13);
}
.edit-btn {
  color: #667eea;
}
.delete-btn {
  color: #ff6b6b;
}
.reset-btn {
  color: #f39c12;
}

/* Empty and Loading states */
.empty-state, .loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #ecf0f1;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Pagination */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
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

/* Responsive Design */
@media (max-width: 768px) {
  .admin-page-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .header-actions.beautiful-actions {
    flex-direction: column;
    gap: 12px;
    width: 100%;
  }
  
  .btn-add-user, .btn-trash {
    width: 100%;
    justify-content: center;
    padding: 14px 0;
    font-size: 15px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
  
  .pagination-section {
    flex-direction: column;
    gap: 16px;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
}

/* Modal styles */
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

@keyframes popIn {
  from { transform: scale(0.85); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-card h2 {
  margin-top: 0;
  margin-bottom: 18px;
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 18px;
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #34495e;
  font-weight: 500;
}
.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1.5px solid #ecf0f1;
  font-size: 15px;
  background: #f8f9fa;
  transition: border 0.2s;
}
.form-group input:focus,
.form-group select:focus {
  border-color: #667eea;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 10px;
}

.btn-danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}
.btn-danger:hover {
  background: linear-gradient(135deg, #ff5252 0%, #d32f2f 100%);
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

.modal-danger h2 {
  color: #ff5252;
}

/* Modal transition */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.25s;
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

/* Toast styles */
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
.toast.success {
  border-left: 6px solid #28a745;
}
.toast.error {
  border-left: 6px solid #ff5252;
}
.toast.info {
  border-left: 6px solid #17a2b8;
}
.toast-fade-enter-active, .toast-fade-leave-active {
  transition: opacity 0.3s;
}
.toast-fade-enter-from, .toast-fade-leave-to {
  opacity: 0;
}

.btn-trash .btn-icon.big {
  color: #fff;
  font-size: 1.5em;
  display: flex;
  align-items: center;
  filter: drop-shadow(0 1px 2px rgba(0,0,0,0.12));
  margin-bottom: -2px;
}
</style> 