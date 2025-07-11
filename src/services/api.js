import axios from 'axios'

// Cấu hình axios với base URL
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10 giây timeout
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