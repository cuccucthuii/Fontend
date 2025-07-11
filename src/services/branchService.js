import axios from 'axios'

export function fetchBranches() {
  return axios.get('/api/rap_chieu')
}