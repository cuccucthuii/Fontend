import api from './api'

// VNPay sandbox: create payment URL and verify return

export function createVNPayPayment(payload) {
  // payload: { amount, orderInfo, returnUrl, holdId }
  return api.post('/api/payment/vnpay/create', payload)
}

export function verifyVNPayReturn(query) {
  // query: object parsed from return URL
  return api.get('/api/payment/vnpay/verify', { params: query })
}





