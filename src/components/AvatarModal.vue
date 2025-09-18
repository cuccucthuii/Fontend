<template>
  <transition name="modal-fade">
    <div v-if="show" class="modal-overlay" @click.self="closeModal">
      <div class="modal-container">
        <!-- Close button -->
        <button class="close-btn" @click="closeModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <!-- Modal content -->
        <div class="modal-content">
          <div class="modal-header">
            <h2>Thay đổi ảnh đại diện</h2>
            <p>Chọn ảnh đại diện mới cho tài khoản của bạn</p>
          </div>

          <!-- Current avatar preview -->
          <div class="current-avatar-section">
            <h3>Ảnh hiện tại</h3>
            <div class="current-avatar">
              <img 
                :src="currentAvatar" 
                :alt="'Current avatar'"
                @error="handleAvatarError"
              />
            </div>
          </div>

          <!-- Avatar selection -->
          <div class="avatar-selection">
            <h3>Chọn ảnh đại diện</h3>
            
            <!-- Upload new avatar -->
            <div class="upload-section">
              <div class="upload-area" @click="triggerFileUpload" :class="{ 'dragover': isDragOver }">
                <input 
                  ref="fileInput"
                  type="file" 
                  accept="image/*" 
                  @change="handleFileUpload"
                  @dragover.prevent="isDragOver = true"
                  @dragleave.prevent="isDragOver = false"
                  @drop.prevent="handleFileDrop"
                  style="display: none"
                />
                <div class="upload-content">
                  <div class="upload-icon">📷</div>
                  <p>Kéo thả ảnh vào đây hoặc <span class="upload-link">chọn ảnh</span></p>
                  <small>Hỗ trợ: JPG, PNG, GIF (tối đa 5MB)</small>
                </div>
              </div>
            </div>

            <!-- Available avatars -->
            <div class="available-avatars">
              <h4>Ảnh có sẵn</h4>
              <div class="avatar-grid">
                <div 
                  v-for="avatar in availableAvatars" 
                  :key="avatar.id"
                  class="avatar-option"
                  :class="{ selected: selectedAvatar === avatar.id }"
                  @click="selectAvatar(avatar.id)"
                >
                  <img 
                    :src="avatar.url" 
                    :alt="avatar.name"
                    @error="handleAvatarError"
                  />
                  <div class="avatar-name">{{ avatar.name }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Preview new avatar -->
          <div v-if="previewAvatar" class="preview-section">
            <h3>Xem trước</h3>
            <div class="preview-avatar">
              <img 
                :src="previewAvatar" 
                :alt="'Preview avatar'"
                @error="handleAvatarError"
              />
            </div>
          </div>

          <!-- Actions -->
          <div class="modal-actions">
            <button class="btn-cancel" @click="closeModal">Hủy</button>
            <button 
              class="btn-save" 
              @click="saveAvatar"
              :disabled="!selectedAvatar && !previewAvatar"
            >
              {{ isLoading ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAvailableAvatars, uploadAvatar, setUserAvatar, DEFAULT_AVATAR_URL } from '../services/avatarService'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  currentAvatar: {
    type: String,
    default: DEFAULT_AVATAR_URL
  }
})

const emit = defineEmits(['close', 'avatar-changed'])

const fileInput = ref(null)
const isDragOver = ref(false)
const isLoading = ref(false)
const selectedAvatar = ref(null)
const previewAvatar = ref(null)
const availableAvatars = ref([])

onMounted(() => {
  loadAvailableAvatars()
})

function closeModal() {
  emit('close')
  resetModal()
}

function resetModal() {
  selectedAvatar.value = null
  previewAvatar.value = null
  isDragOver.value = false
}

function triggerFileUpload() {
  fileInput.value?.click()
}

function handleFileUpload(event) {
  const file = event.target.files[0]
  if (file) {
    processFile(file)
  }
}

function handleFileDrop(event) {
  isDragOver.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    processFile(file)
  }
}

function processFile(file) {
  // Validate file type
  if (!file.type.startsWith('image/')) {
    alert('Vui lòng chọn file ảnh hợp lệ!')
    return
  }

  // Validate file size (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('Kích thước file không được vượt quá 5MB!')
    return
  }

  // Create preview
  const reader = new FileReader()
  reader.onload = (e) => {
    previewAvatar.value = e.target.result
    selectedAvatar.value = null // Clear selected avatar when uploading new file
  }
  reader.readAsDataURL(file)
}

function selectAvatar(avatarId) {
  selectedAvatar.value = avatarId
  previewAvatar.value = null // Clear preview when selecting available avatar
  
  // Find avatar URL
  const avatar = availableAvatars.value.find(a => a.id === avatarId)
  if (avatar) {
    previewAvatar.value = avatar.url
  }
}

async function loadAvailableAvatars() {
  try {
    const response = await getAvailableAvatars()
    availableAvatars.value = response.data || []
  } catch (error) {
    console.error('Error loading avatars:', error)
    // Fallback to default avatars
    availableAvatars.value = [
      { id: 'default', name: 'Mặc định', url: DEFAULT_AVATAR_URL },
      { id: 'doge', name: 'Doge', url: '/logo.png' }
    ]
  }
}

async function saveAvatar() {
  if (!selectedAvatar.value && !previewAvatar.value) {
    alert('Vui lòng chọn ảnh đại diện!')
    return
  }

  isLoading.value = true
  
  try {
    let avatarUrl = previewAvatar.value

    // If user uploaded a new file, upload it first
    if (selectedAvatar.value === null && previewAvatar.value && previewAvatar.value.startsWith('data:')) {
      // Convert data URL to file and upload
      const response = await fetch(previewAvatar.value)
      const blob = await response.blob()
      const file = new File([blob], 'avatar.jpg', { type: 'image/jpeg' })
      
      const uploadResponse = await uploadAvatar(file)
      avatarUrl = uploadResponse.data.url
    }

    // Set the avatar
    if (selectedAvatar.value) {
      await setUserAvatar(selectedAvatar.value)
    }

    emit('avatar-changed', avatarUrl)
    
  } catch (error) {
    console.error('Error saving avatar:', error)
    alert('Có lỗi xảy ra khi lưu ảnh đại diện. Vui lòng thử lại!')
  } finally {
    isLoading.value = false
  }
}

function handleAvatarError(event) {
  event.target.src = DEFAULT_AVATAR_URL
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.modal-container {
  position: relative;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.1);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  z-index: 10;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.2);
  color: #333;
}

.modal-content {
  padding: 32px;
}

.modal-header {
  text-align: center;
  margin-bottom: 32px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.modal-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.current-avatar-section {
  margin-bottom: 32px;
}

.current-avatar-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.current-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #e1e5e9;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.current-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-selection h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.upload-section {
  margin-bottom: 32px;
}

.upload-area {
  border: 2px dashed #e1e5e9;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #f8f9fa;
}

.upload-area:hover,
.upload-area.dragover {
  border-color: #667eea;
  background: #f0f4ff;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-content p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.upload-link {
  color: #667eea;
  font-weight: 600;
}

.upload-content small {
  color: #999;
  font-size: 12px;
}

.available-avatars h4 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 16px;
}

.avatar-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.avatar-option:hover {
  background: #f8f9fa;
}

.avatar-option.selected {
  border-color: #667eea;
  background: #f0f4ff;
}

.avatar-option img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e1e5e9;
}

.avatar-name {
  font-size: 12px;
  color: #666;
  text-align: center;
}

.preview-section {
  margin-bottom: 32px;
  text-align: center;
}

.preview-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.preview-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #667eea;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #e1e5e9;
}

.btn-cancel {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #e9ecef;
}

.btn-save {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Animation */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container,
.modal-fade-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from .modal-container {
  transform: scale(0.9) translateY(20px);
}

.modal-fade-leave-to .modal-container {
  transform: scale(0.9) translateY(20px);
}

/* Responsive */
@media (max-width: 768px) {
  .modal-container {
    width: 95%;
    margin: 20px;
  }
  
  .modal-content {
    padding: 20px;
  }
  
  .avatar-grid {
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    gap: 12px;
  }
  
  .avatar-option img {
    width: 40px;
    height: 40px;
  }
  
  .modal-actions {
    flex-direction: column;
  }
  
  .btn-cancel,
  .btn-save {
    width: 100%;
  }
}
</style>
