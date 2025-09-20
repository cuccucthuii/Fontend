import api from './api'
import { RAP_CHIEU_ENDPOINTS } from '../constants/api'

export function fetchBranches() {
  return api.get(RAP_CHIEU_ENDPOINTS.GET_ALL)
}

export function deleteBranch(id) {
  return api.delete(`${RAP_CHIEU_ENDPOINTS.DELETE}/${id}`)
}

export function updateBranch(id, updatedData) {
  return api.put(`${RAP_CHIEU_ENDPOINTS.UPDATE}/${id}`, updatedData)
}

export function createBranch(data) {
  return api.post(RAP_CHIEU_ENDPOINTS.ADD, data)
}
