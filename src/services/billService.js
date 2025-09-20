import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

export function fetchBills() {
  return axios.get(`${API_BASE_URL}/api/bookings/history`)
}
