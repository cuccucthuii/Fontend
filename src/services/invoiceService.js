import axios from 'axios'

export function fetchInvoices() {
  return axios.get('/api/pay')
}