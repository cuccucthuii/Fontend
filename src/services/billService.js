import axios from 'axios'

export function fetchBills() {
  return axios.get('/api/bookings/history')
}
