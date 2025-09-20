import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

const BASE_URL = `${API_BASE_URL}/api`

/**
 * Lấy chi tiết suất chiếu theo ID
 * @param {string|number} scheduleId 
 */
export async function fetchScheduleById(scheduleId) {
  return axios.get(`${BASE_URL}/suat-chieu/${scheduleId}`)
}

/**
 * Lấy danh sách ghế theo phòng chiếu (từ suất chiếu đã lấy idPhòng)
 * @param {string|number} roomId 
 */
export async function fetchSeatsByRoom(roomId) {
  return axios.get(`${BASE_URL}/ghengoi/phongchieu/${roomId}`)
}

/**
 * Tạo hóa đơn / đặt vé
 * @param {Object} payload
 */
export async function createPOSBill(payload) {
  return axios.post(`${BASE_URL}/bill`, payload);
}
