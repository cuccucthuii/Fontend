import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

export function fetchRooms() {
  return axios.get(`${API_BASE_URL}/api/phong_chieu`)
}

export function fetchTrashRooms() {
  return axios.get(`${API_BASE_URL}/api/phong_chieu/trash`)
}

export function deleteRoom(id) {
  return axios.delete(`${API_BASE_URL}/api/phong_chieu/${id}`)
}

export function softDeleteRoom(id) {
  return axios.put(`${API_BASE_URL}/api/phong_chieu/${id}/soft-delete`)
}

export function restoreRoom(id) {
  return axios.put(`${API_BASE_URL}/api/phong_chieu/${id}/restore`)
}

export function permanentlyDeleteRoom(id) {
  return axios.delete(`${API_BASE_URL}/api/phong_chieu/${id}/permanent`)
}

export function createRoom(data) {
  console.log('🚀 Gọi API createRoom với data:', data)
  console.log('📡 URL:', `${API_BASE_URL}/api/phong_chieu`)
  console.log('🔧 Headers:', axios.defaults.headers)
  return axios.post(`${API_BASE_URL}/api/phong_chieu`, data)
}

export function updateRoom(id, data) {
  return axios.put(`${API_BASE_URL}/api/phong_chieu/${id}`, data)
}