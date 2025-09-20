import api from '@/services/api'

export function fetchInvoices() {
  return api.get('/api/pay')
}