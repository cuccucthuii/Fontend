import axios from 'axios'

// Tổng doanh thu toàn hệ thống
export function fetchTotalRevenue() {
  return axios.get('/api/admin/statistics')
}

// Doanh thu theo tháng
export function fetchMonthlyRevenue(month, year) {
  return axios.get('/api/admin/statistics/month', {
    params: { month, year }
  })
}

// Thống kê doanh thu theo rạp
export function fetchTheaterStatistics() {
  return axios.get('/api/admin/statistics/theater')
}

// Thống kê doanh thu theo phim
export function fetchMovieStatistics() {
  return axios.get('/api/admin/statistics/movie')
}
