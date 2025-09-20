import api from '@/services/api'

export function fetchSchedules() {
  return api.get('/api/suat-chieu')
}

export function deleteSchedule(id) {
  return api.delete(`/api/suat-chieu/${id}`)
}