import axios from 'axios'
import { API_BASE_URL } from '../constants/api'

export function fetchSeats() {
  return axios.get(`${API_BASE_URL}/api/ghengoi`)
}

export function createSeat(data) {
  return axios.post(`${API_BASE_URL}/api/ghengoi`, data)
}

export function updateSeat(id, seatData) {
  return axios.put(`${API_BASE_URL}/api/ghengoi/${id}`, seatData)
}

export function fetchSeatsByRoom(roomId) {
  return axios.get(`${API_BASE_URL}/api/ghengoi/room/${roomId}`)
}

export function fetchSeatsBySchedule(scheduleId) {
  return axios.get(`${API_BASE_URL}/api/ghengoi/schedule/${scheduleId}`)
}

// API endpoints mới theo backend thực tế
export function fetchRoomLayout(roomId) {
  return axios.get(`${API_BASE_URL}/api/phong-layout/${roomId}`)
}

export function fetchSeatMap(showTimeId) {
  return axios.get(`${API_BASE_URL}/api/ghe-ngoi/sodo/${showTimeId}`)
}

export function fetchSeatAvailability(showTimeId) {
  return axios.get(`${API_BASE_URL}/api/seat-selection/availability/${showTimeId}`)
}

export function holdSeats(seatData) {
  return axios.post(`${API_BASE_URL}/api/seat-selection/hold`, seatData)
}

export function confirmSeats(seatData) {
  return axios.post(`${API_BASE_URL}/api/seat-selection/confirm`, seatData)
}

export function releaseSeats(seatData) {
  return axios.post(`${API_BASE_URL}/api/seat-selection/release`, seatData)
}