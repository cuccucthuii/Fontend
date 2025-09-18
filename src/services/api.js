import axios from 'axios'

// Cấu hình axios với base URL rỗng, các endpoint dùng đường dẫn tuyệt đối '/api/...'
const api = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 30000 // 30 giây timeout để tránh timeout khi BE chậm
})

// Request interceptor - thêm token vào header nếu có
api.interceptors.request.use(
  (config) => {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const { token } = JSON.parse(userInfo)
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - xử lý response và lỗi
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('API Error:', error)
    // Xử lý lỗi 401 (Unauthorized) - token hết hạn
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api 
// API Base URL (server gốc) – dùng khi cần tuyệt đối
export const API_BASE_URL = import.meta?.env?.VITE_API_BASE_URL || 'http://localhost:8081'

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

// Phòng chiếu endpoints
export const PHONG_CHIEU_ENDPOINTS = {
  GET_ALL: '/api/phong_chieu',
  GET_BY_ID: '/api/phong_chieu',
  ADD: '/api/phong_chieu',
  UPDATE: '/api/phong_chieu',
  DELETE: '/api/phong_chieu'
}

// Rạp chiếu endpoints
export const RAP_CHIEU_ENDPOINTS = {
  GET_ALL: '/api/rap_chieu',
  GET_BY_ID: '/api/rap_chieu',
  ADD: '/api/rap_chieu',
  UPDATE: '/api/rap_chieu',
  DELETE: '/api/rap_chieu'
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