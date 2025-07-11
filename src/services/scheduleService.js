import axios from 'axios'

export function fetchSchedules() {
  return axios.get('/api/suat-chieu')
}