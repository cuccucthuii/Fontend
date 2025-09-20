import api from '@/services/api'

export function fetchRooms() {
  return api.get('/api/phong_chieu')
}

export function fetchTrashRooms() {
  return api.get('/api/phong_chieu/trash')
}

export function deleteRoom(id) {
  return api.delete(`/api/phong_chieu/${id}`)
}

export function softDeleteRoom(id) {
  return api.put(`/api/phong_chieu/${id}/soft-delete`)
}

export function restoreRoom(id) {
  return api.put(`/api/phong_chieu/${id}/restore`)
}

export function permanentlyDeleteRoom(id) {
  return api.delete(`/api/phong_chieu/${id}/permanent`)
}

export function createRoom(data) {
  console.log('🚀 Gọi API createRoom với data:', data)
  console.log('📡 URL:', '/api/phong_chieu')
  console.log('🔧 Headers:', axios.defaults.headers)
  return api.post('/api/phong_chieu', data)
}

export function updateRoom(id, data) {
  return api.put(`/api/phong_chieu/${id}`, data)
}