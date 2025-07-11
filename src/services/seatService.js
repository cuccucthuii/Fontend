import axios from 'axios'

export function fetchSeats() {
  return axios.get('/api/ghengoi')
}
