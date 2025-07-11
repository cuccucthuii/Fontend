<template>
  <div class="account-page">
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
        <button class="btn btn-add-user" @click="addUser" v-if="!showTrashMode">
          <span class="btn-icon big">➕</span>
          <span class="btn-text">Thêm Người Dùng</span>
        </button>
        <button class="btn btn-trash" @click="toggleTrashMode">
          <span class="btn-icon big">🗑️</span>
          <span class="btn-text">{{ showTrashMode ? 'Quay lại' : 'Thùng rác' }}</span>
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
        <h3 class="table-title">{{ showTrashMode ? 'Thùng rác tài khoản đã xóa' : 'Danh sách người dùng' }}</h3>
        <div class="table-actions" v-if="!showTrashMode">
          <div class="search-box">
            <input 
              type="text" 
              placeholder="Tìm kiếm người dùng..." 
              v-model="searchQuery"
              class="search-input"
            />
            <span class="search-icon">🔍</span>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table v-if="(showTrashMode ? trashUsers.length : filteredUsers.length)" class="data-table">
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
            <tr v-for="(user, index) in (showTrashMode ? paginatedTrashUsers : paginatedUsers)" :key="user.idUserAccount" class="table-row">
              <td class="td-stt">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="td-username">
                <div class="user-info">
                  <div class="user-avatar">👤</div>
                  <span class="username">{{ user.tenDangNhap }}</span>
                </div>
              </td>
              <td class="td-role">
                <span class="role-badge" :class="getRoleClass(user.vaiTro)">
                  {{ user.vaiTro }}
                </span>
              </td>
              <td class="td-status">
                <span class="status-badge" :class="getStatusClass(user.trangThai)">
                  {{ user.trangThai ?? 'Trống' }}
                </span>
              </td>
              <td class="td-actions">
                <div class="action-buttons">
                  <button v-if="!showTrashMode" class="action-btn edit-btn" @click="editUser(user)" title="Chỉnh sửa">
                    <span class="action-icon">✏️</span>
                  </button>
                  <button v-if="!showTrashMode" class="action-btn delete-btn" @click="deleteUser(user)" title="Xóa">
                    <span class="action-icon">
                      <svg width="18" height="18" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M6 6l8 8M14 6l-8 8" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
                      </svg>
                    </span>
                  </button>
                  <button v-if="showTrashMode" class="action-btn edit-btn" @click="restoreUser(user)" title="Khôi phục">
                    <span class="action-icon">♻️</span>
                  </button>
                  <button v-if="showTrashMode" class="action-btn delete-btn" @click="deleteUserPermanently(user)" title="Xóa vĩnh viễn">
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

        <div v-else-if="!showTrashMode && searchQuery" class="empty-state">
          <div class="empty-icon">🔍</div>
          <h3>Không tìm thấy kết quả</h3>
          <p>Không có người dùng nào phù hợp với từ khóa "{{ searchQuery }}"</p>
        </div>

        <div v-else class="loading-state">
          <div class="loading-spinner"></div>
          <p>{{ showTrashMode ? 'Không có tài khoản nào trong thùng rác.' : 'Đang tải dữ liệu...' }}</p>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="totalPages > 1">
      <div class="pagination-info">
        <span class="pagination-text">
          Hiển thị {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, (showTrashMode ? trashUsers.length : filteredUsers.length)) }} 
          trong tổng số {{ showTrashMode ? trashUsers.length : filteredUsers.length }} người dùng
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

    <!-- Modal Thêm/Sửa User -->
    <transition name="modal-fade">
      <div v-if="showUserModal" class="modal-overlay" @click.self="closeUserModal">
        <div class="modal-card">
          <h2>{{ isEditMode ? 'Chỉnh sửa người dùng' : 'Thêm người dùng' }}</h2>
          <form @submit.prevent="submitUserForm">
            <div class="form-group">
              <label for="username">Tên đăng nhập</label>
              <input id="username" v-model="userForm.tenDangNhap" required :disabled="isLoading" />
            </div>
            <div class="form-group">
              <label for="role">Vai trò</label>
              <select id="role" v-model="userForm.vaiTro" required :disabled="isLoading">
                <option value="Admin">Admin</option>
                <option value="User">User</option>
                <option value="Manager">Manager</option>
              </select>
            </div>
            <div class="form-group">
              <label for="status">Trạng thái</label>
              <select id="status" v-model="userForm.trangThai" required :disabled="isLoading">
                <option value="Hoạt động">Hoạt động</option>
                <option value="Khóa">Khóa</option>
              </select>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeUserModal" :disabled="isLoading">Hủy</button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="loading-spinner-btn"></span>
                {{ isEditMode ? 'Lưu thay đổi' : 'Thêm mới' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Modal Xác Nhận Xóa -->
    <transition name="modal-fade">
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
    <transition name="toast-fade">
      <div v-if="toast.show" :class="['toast', toast.type]">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchUsers } from '../services/userService'

const users = ref([])
const currentPage = ref(1)
const pageSize = 10
const searchQuery = ref('')
const showTrashMode = ref(false)

// Computed properties
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value.filter(u => !u.daXoa)
  return users.value.filter(user =>
    !user.daXoa && (
      user.tenDangNhap.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.vaiTro.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  )
})
const trashUsers = computed(() => users.value.filter(u => u.daXoa))

const totalPages = computed(() => Math.ceil((showTrashMode.value ? trashUsers.value.length : filteredUsers.value.length) / pageSize))

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})
const paginatedTrashUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return trashUsers.value.slice(start, start + pageSize)
})

const activeUsers = computed(() =>
  users.value.filter(user => user.trangThai === 'Hoạt động' && !user.daXoa).length
)
const adminUsers = computed(() =>
  users.value.filter(user => user.vaiTro === 'Admin' && !user.daXoa).length
)

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let total = totalPages.value
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(total, start + maxVisible - 1)
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// Modal state
const showUserModal = ref(false)
const isEditMode = ref(false)
const isLoading = ref(false)
const userForm = ref({ idUserAccount: null, tenDangNhap: '', vaiTro: 'User', trangThai: 'Hoạt động' })

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

function toggleTrashMode() {
  showTrashMode.value = !showTrashMode.value
  currentPage.value = 1
}

function getRoleClass(role) {
  const roleClasses = {
    'Admin': 'role-admin',
    'User': 'role-user',
    'Manager': 'role-manager'
  }
  return roleClasses[role] || 'role-default'
}

function getStatusClass(status) {
  const statusClasses = {
    'Hoạt động': 'status-active',
    'Khóa': 'status-inactive',
    'Trống': 'status-empty'
  }
  return statusClasses[status] || 'status-default'
}

function openAddUserModal() {
  isEditMode.value = false
  userForm.value = { idUserAccount: null, tenDangNhap: '', vaiTro: 'User', trangThai: 'Hoạt động' }
  showUserModal.value = true
}

function openEditUserModal(user) {
  isEditMode.value = true
  userForm.value = { ...user }
  showUserModal.value = true
}

function closeUserModal() {
  showUserModal.value = false
}

async function submitUserForm() {
  isLoading.value = true
  setTimeout(() => { // Giả lập API
    if (isEditMode.value) {
      // Sửa user
      const idx = users.value.findIndex(u => u.idUserAccount === userForm.value.idUserAccount)
      if (idx !== -1) users.value[idx] = { ...userForm.value }
      showToast('Cập nhật thành công!', 'success')
    } else {
      // Thêm user
      users.value.unshift({ ...userForm.value, idUserAccount: Date.now() })
      showToast('Thêm người dùng thành công!', 'success')
    }
    isLoading.value = false
    closeUserModal()
  }, 1200)
}

function addUser() {
  openAddUserModal()
}

function editUser(user) {
  openEditUserModal(user)
}

function deleteUser(user) {
  // Soft delete: set daXoa = true
  const idx = users.value.findIndex(u => u.idUserAccount === user.idUserAccount)
  if (idx !== -1) users.value[idx].daXoa = true
  showToast('Đã chuyển vào thùng rác!', 'success')
}

function restoreUser(user) {
  const idx = users.value.findIndex(u => u.idUserAccount === user.idUserAccount)
  if (idx !== -1) users.value[idx].daXoa = false
  showToast('Khôi phục thành công!', 'success')
}

function deleteUserPermanently(user) {
  users.value = users.value.filter(u => u.idUserAccount !== user.idUserAccount)
  showToast('Đã xóa vĩnh viễn!', 'success')
}

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type }
  if (toastTimeout) clearTimeout(toastTimeout)
  toastTimeout = setTimeout(() => { toast.value.show = false }, 2500)
}

// Lifecycle
onMounted(async () => {
  try {
    const response = await fetchUsers()
    users.value = (response.data || []).map(u => ({ ...u, daXoa: u.daXoa ?? false }))
  } catch (error) {
    console.error('❌ Lỗi khi gọi API:', error)
  }
})
</script>

<style scoped>
.account-page {
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
  gap: 12px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.username {
  font-weight: 500;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.role-manager {
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
  color: white;
}

.role-default {
  background: #ecf0f1;
  color: #7f8c8d;
}

.status-active {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: white;
}

.status-inactive {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
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
}

.edit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.edit-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
}

.delete-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
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
  .account-page {
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