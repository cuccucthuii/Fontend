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