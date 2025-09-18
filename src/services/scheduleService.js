import axios from 'axios'

export function fetchSchedules() {
  return axios.get('/api/suat-chieu')
}

export function deleteSchedule(id) {
  return axios.delete(`/api/suat-chieu/${id}`)
}