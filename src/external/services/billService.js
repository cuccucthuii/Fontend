import api from './api'
import { BILL_ENDPOINTS } from '../constants/api'

export function fetchBills() {
  return api.get('/api/bookings/history')
}
