import api from './api';
import { SUAT_CHIEU_ENDPOINTS, GHE_NGOI_ENDPOINTS, BILL_ENDPOINTS } from '../constants/api';

/**
 * Lấy chi tiết suất chiếu theo ID
 * @param {string|number} scheduleId
 */
export async function fetchScheduleById(scheduleId) {
  return api.get(`${SUAT_CHIEU_ENDPOINTS.GET_BY_ID}/${scheduleId}`);
}

/**
 * Lấy danh sách ghế theo phòng chiếu (từ suất chiếu đã lấy idPhòng)
 * @param {string|number} roomId
 */
export async function fetchSeatsByRoom(roomId) {
  return api.get(`${GHE_NGOI_ENDPOINTS.GET_BY_ROOM}/${roomId}`);
}

/**
 * Tạo hóa đơn / đặt vé
 * @param {Object} payload
 */
export async function createPOSBill(payload) {
  return api.post(BILL_ENDPOINTS.ADD, payload);
}
