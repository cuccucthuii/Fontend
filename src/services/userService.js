import api from './api'
import { AUTH_ENDPOINTS, USER_ENDPOINTS } from '../constants/api'

export function fetchUsers() {
  return api.get(USER_ENDPOINTS.GET_ALL)
}

export function addUser(user) {
  return api.post(USER_ENDPOINTS.ADD, user)
}

export function loginUser(data) {
  return api.post(AUTH_ENDPOINTS.LOGIN, data)
}

export function registerUser(user) {
  return api.post(AUTH_ENDPOINTS.REGISTER, user)
}

// Thêm các API khác nếu cần
export function updateUser(id, user) {
  return api.put(`${USER_ENDPOINTS.UPDATE}/${id}`, user)
}

export function deleteUser(id) {
  return api.delete(`${USER_ENDPOINTS.DELETE}/${id}`)
}

export function getUserById(id) {
  return api.get(`${USER_ENDPOINTS.GET_BY_ID}/${id}`)
}

// API để logout
export function logoutUser() {
  return api.post(AUTH_ENDPOINTS.LOGOUT)
}