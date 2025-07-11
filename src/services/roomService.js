import axios from 'axios'

export function fetchRooms() {
  return axios.get('/api/phong_chieu')
}