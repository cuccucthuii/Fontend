// API Base URL
export const API_BASE_URL = 'http://localhost:8081'

// Auth endpoints
export const AUTH_ENDPOINTS = {
  LOGIN: '/api/auth/login',
  REGISTER: '/api/auth/register',
  LOGOUT: '/api/auth/logout',
  REFRESH_TOKEN: '/api/auth/refresh',
  SEND_OTP: '/api/auth/send-otp',
  VERIFY_OTP: '/api/auth/verify-otp'
}

// User endpoints
export const USER_ENDPOINTS = {
  GET_ALL: '/api/user/all',
  GET_BY_ID: '/api/user',
  ADD: '/api/user/add',
  UPDATE: '/api/user',
  DELETE: '/api/user'
}

// Movie endpoints
export const MOVIE_ENDPOINTS = {
  GET_ALL: '/api/phim',
  GET_BY_ID: '/api/phim',
  ADD: '/api/phim/add',
  UPDATE: '/api/phim',
  DELETE: '/api/phim'
}

// Room endpoints
export const ROOM_ENDPOINTS = {
  GET_ALL: '/api/room/all',
  GET_BY_ID: '/api/room',
  ADD: '/api/room/add',
  UPDATE: '/api/room',
  DELETE: '/api/room'
}

// Branch endpoints
export const BRANCH_ENDPOINTS = {
  GET_ALL: '/api/branch/all',
  GET_BY_ID: '/api/branch',
  ADD: '/api/branch/add',
  UPDATE: '/api/branch',
  DELETE: '/api/branch'
}

// Cinema endpoints (alias for branch)
export const CINEMA_ENDPOINTS = {
  GET_ALL: '/api/rap_chieu',
  GET_BY_ID: '/api/rap_chieu',
  ADD: '/api/rap_chieu',
  UPDATE: '/api/rap_chieu',
  DELETE: '/api/rap_chieu'
}

// Seat endpoints
export const SEAT_ENDPOINTS = {
  GET_ALL: '/api/seat/all',
  GET_BY_ID: '/api/seat',
  ADD: '/api/seat/add',
  UPDATE: '/api/seat',
  DELETE: '/api/seat'
}

// Schedule endpoints
export const SCHEDULE_ENDPOINTS = {
  GET_ALL: '/api/suat-chieu',
  GET_BY_ID: '/api/suat-chieu',
  ADD: '/api/suat-chieu',
  UPDATE: '/api/suat-chieu',
  DELETE: '/api/suat-chieu'
}

// Suất chiếu endpoints (alias for schedule)
export const SUAT_CHIEU_ENDPOINTS = {
  GET_ALL: '/api/suat-chieu',
  GET_BY_ID: '/api/suat-chieu',
  GET_BY_MOVIE_CINEMA: '/api/suat-chieu/by-movie-cinema',
  ADD: '/api/suat-chieu',
  UPDATE: '/api/suat-chieu',
  DELETE: '/api/suat-chieu'
}

// Bill endpoints
export const BILL_ENDPOINTS = {
  GET_ALL: '/api/bill/all',
  GET_BY_ID: '/api/bill',
  ADD: '/api/bill/add',
  UPDATE: '/api/bill',
  DELETE: '/api/bill'
}

// Invoice endpoints
export const INVOICE_ENDPOINTS = {
  GET_ALL: '/api/invoice/all',
  GET_BY_ID: '/api/invoice',
  ADD: '/api/invoice/add',
  UPDATE: '/api/invoice',
  DELETE: '/api/invoice'
}

// HTTP Status Codes
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

// User Roles
export const USER_ROLES = {
  ADMIN: 'admin',
  USER: 'user',
  STAFF: 'staff'
} 