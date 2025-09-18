import api from './api'

// Booking related APIs (align with BE provided endpoints)
// - Seat hold: tạm giữ ghế theo suất chiếu, timeout phía BE
// - Confirm booking: xác nhận và phát hành mã vé/QR
// - Release: huỷ giữ ghế
// - Get ticket by code/id

export function holdSeats(payload) {
  // payload: { scheduleId, seatIds: number[], customerInfo?: {...} }
  return api.post('/api/booking/hold', payload)
}

export function confirmBooking(payload) {
  // payload: { holdId, paymentMethod: 'POS'|'VNPAY', promotionCode?: string }
  return api.post('/api/booking/confirm', payload)
}

export function releaseHold(holdId) {
  return api.post(`/api/booking/release/${holdId}`)
}

export function getTicketByCode(code) {
  return api.get(`/api/booking/ticket/${code}`)
}





