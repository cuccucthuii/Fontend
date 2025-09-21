<template>
  <div class="genre-admin-container">
    <h1>Quản lý thể loại phim</h1>
    <div class="genre-actions">
      <input v-model="newGenre" placeholder="Nhập tên thể loại mới..." class="genre-input" @keyup.enter="addGenre" />
      <button class="btn-add" @click="addGenre" :disabled="isLoading">Thêm thể loại</button>
    </div>
    <div v-if="isLoading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Đang tải dữ liệu...</p>
    </div>
    <table v-else class="genre-table">
      <thead>
        <tr>
          <th>STT</th>
          <th>Tên thể loại</th>
          <th>Chức năng</th>
        </tr>
      </thead>
      <tbody>
                 <tr v-for="(genre, idx) in genres" :key="genre.idTheLoai">
           <td>{{ idx + 1 }}</td>
           <td v-if="editId !== genre.idTheLoai">{{ genre.tenTheLoai }}</td>
          <td v-else>
            <input v-model="editName" class="genre-input" />
          </td>
          <td>
                         <button v-if="editId !== genre.idTheLoai" class="btn-edit" @click="startEdit(genre)">Sửa</button>
             <button v-else class="btn-save" @click="saveEdit(genre)" :disabled="isLoading">Lưu</button>
             <button v-if="editId === genre.idTheLoai" class="btn-cancel" @click="cancelEdit">Hủy</button>
                         <button class="btn-delete" @click="handleDeleteGenre(genre)" :disabled="isLoading">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchGenres, createGenre, updateGenre, deleteGenre } from '../../services/movieService'

const genres = ref([])
const newGenre = ref('')
const editId = ref(null)
const editName = ref('')
const isLoading = ref(false)
const message = ref('')
const messageType = ref('')

async function loadGenres() {
  isLoading.value = true
  try {
    console.log('🔄 Đang tải thể loại từ API...')
    const res = await fetchGenres()
    console.log('📡 API Response:', res)
    console.log('📡 Response data:', res.data)
    console.log('📡 Response status:', res.status)
    
    genres.value = res.data
    console.log('📋 Genres loaded:', genres.value)
    
    // Debug: Kiểm tra cấu trúc dữ liệu
    if (genres.value && genres.value.length > 0) {
      console.log('🔍 First genre structure:', genres.value[0])
      console.log('🔍 Available fields:', Object.keys(genres.value[0]))
    }
  } catch (error) {
    console.error('❌ Lỗi tải thể loại:', error)
    console.error('❌ Error details:', error.response?.data)
    console.error('❌ Error status:', error.response?.status)
    showMessage('Lỗi tải dữ liệu thể loại', 'error')
  } finally {
    isLoading.value = false
  }
}

async function addGenre() {
  const name = newGenre.value.trim()
  if (!name) return
     if (genres.value.some(g => g.tenTheLoai.toLowerCase() === name.toLowerCase())) {
    showMessage('Thể loại này đã tồn tại', 'error')
    return
  }
  
  isLoading.value = true
  try {
         await createGenre({ tenTheLoai: name })
    await loadGenres()
    newGenre.value = ''
    showMessage('Thêm thể loại thành công', 'success')
  } catch (error) {
    console.error('Lỗi thêm thể loại:', error)
    showMessage('Lỗi thêm thể loại', 'error')
  } finally {
    isLoading.value = false
  }
}

function startEdit(genre) {
  editId.value = genre.idTheLoai
  editName.value = genre.tenTheLoai
}

async function saveEdit(genre) {
  const name = editName.value.trim()
  if (!name) return
     if (genres.value.some(g => g.tenTheLoai.toLowerCase() === name.toLowerCase() && g.idTheLoai !== genre.idTheLoai)) {
    showMessage('Thể loại này đã tồn tại', 'error')
    return
  }
  
  isLoading.value = true
  try {
         await updateGenre(genre.idTheLoai, { tenTheLoai: name })
    await loadGenres()
    editId.value = null
    editName.value = ''
    showMessage('Cập nhật thể loại thành công', 'success')
  } catch (error) {
    console.error('Lỗi cập nhật thể loại:', error)
    showMessage('Lỗi cập nhật thể loại', 'error')
  } finally {
    isLoading.value = false
  }
}

function cancelEdit() {
  editId.value = null
  editName.value = ''
}

async function handleDeleteGenre(genre) {
     if (!confirm(`Bạn có chắc chắn muốn xóa thể loại "${genre.tenTheLoai}"?`)) return
  
  isLoading.value = true
  try {
         await deleteGenre(genre.idTheLoai)
    await loadGenres()
    showMessage('Xóa thể loại thành công', 'success')
  } catch (error) {
    console.error('Lỗi xóa thể loại:', error)
    showMessage('Lỗi xóa thể loại', 'error')
  } finally {
    isLoading.value = false
  }
}

function showMessage(msg, type) {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

onMounted(() => {
  loadGenres()
})
</script>

<style scoped>
.genre-admin-container {
  max-width: 600px;
  margin: 40px auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(44,62,80,0.10);
  padding: 32px 24px 40px 24px;
}
h1 {
  text-align: center;
  color: #764ba2;
  margin-bottom: 28px;
}
.genre-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  justify-content: center;
}
.genre-input {
  padding: 10px 14px;
  border-radius: 8px;
  border: 1.5px solid #e0e0e0;
  font-size: 15px;
  min-width: 180px;
  transition: border 0.2s;
}
.genre-input:focus {
  border-color: #667eea;
  outline: none;
}
.btn-add {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-add:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
.btn-add:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.genre-table {
  width: 100%;
  border-collapse: collapse;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
}
.genre-table th, .genre-table td {
  padding: 14px 12px;
  text-align: left;
  font-size: 15px;
}
.genre-table th {
  background: #e0e7ff;
  color: #2c3e50;
  font-weight: 700;
}
.genre-table tr {
  background: #fff;
  transition: background 0.2s;
}
.genre-table tr:hover {
  background: #f3f6fd;
}
.btn-edit, .btn-save, .btn-cancel, .btn-delete {
  border: none;
  border-radius: 6px;
  padding: 7px 14px;
  font-size: 14px;
  font-weight: 600;
  margin-right: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-edit {
  background: #e0e7ff;
  color: #764ba2;
}
.btn-edit:hover {
  background: #d1d5fa;
}
.btn-save {
  background: #27ae60;
  color: #fff;
}
.btn-save:hover:not(:disabled) {
  background: #219150;
}
.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-cancel {
  background: #b2bec3;
  color: #fff;
}
.btn-cancel:hover {
  background: #636e72;
}
.btn-delete {
  background: #ff6b6b;
  color: #fff;
}
.btn-delete:hover:not(:disabled) {
  background: #ee5a52;
}
.btn-delete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.loading-state {
  text-align: center;
  padding: 40px 0;
  color: #7f8c8d;
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
.message {
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  font-weight: 600;
  text-align: center;
}
.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}
.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
@media (max-width: 700px) {
  .genre-admin-container {
    padding: 16px 4px 24px 4px;
    max-width: 98vw;
  }
  .genre-table th, .genre-table td {
    padding: 10px 4px;
    font-size: 13px;
  }
}
</style> 