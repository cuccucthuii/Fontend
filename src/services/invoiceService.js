import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

export function fetchInvoices() {
  return axios.get(`${API_BASE_URL}/api/pay`)
}