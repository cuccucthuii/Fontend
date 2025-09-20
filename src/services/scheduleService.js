import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

export function fetchSchedules() {
  return axios.get(`${API_BASE_URL}/api/suat-chieu`)
}

export function deleteSchedule(id) {
  return axios.delete(`${API_BASE_URL}/api/suat-chieu/${id}`)
}