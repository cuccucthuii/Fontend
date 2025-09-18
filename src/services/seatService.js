import axios from 'axios'

export function fetchSeats() {
  return axios.get('/api/ghengoi')
}

export function createSeat(data) {
  return axios.post('/api/ghengoi', data)
}

export function updateSeat(id, seatData) {
  return axios.put(`/api/ghengoi/${id}`, seatData)
}