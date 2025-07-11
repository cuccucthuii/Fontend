// API Base URL
export const API_BASE_URL = 'http://localhost:8080'

// Auth endpoints
export const AUTH_ENDPOINTS = {
  LOGIN: '/api/auth/login',
  REGISTER: '/api/auth/register',
  LOGOUT: '/api/auth/logout',
  REFRESH_TOKEN: '/api/auth/refresh'
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
  GET_ALL: '/api/movie/all',
  GET_BY_ID: '/api/movie',
  ADD: '/api/movie/add',
  UPDATE: '/api/movie',
  DELETE: '/api/movie'
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
  GET_ALL: '/api/schedule/all',
  GET_BY_ID: '/api/schedule',
  ADD: '/api/schedule/add',
  UPDATE: '/api/schedule',
  DELETE: '/api/schedule'
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