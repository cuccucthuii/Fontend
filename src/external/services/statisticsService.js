import api from './api'

// Tổng doanh thu toàn hệ thống với params động
export function fetchTotalRevenue(params = {}) {
  return api.get('/api/admin/statistics', { params })
}

// Doanh thu theo tháng với params động
export function fetchMonthlyRevenue(params = {}) {
  return api.get('/api/admin/statistics/month', { params })
}

// Thống kê doanh thu theo rạp với params động
export function fetchTheaterStatistics(params = {}) {
  return api.get('/api/admin/statistics/theater', { params })
}

// Thống kê doanh thu theo phim với params động
export function fetchMovieStatistics(params = {}) {
  return api.get('/api/admin/statistics/movie', { params })
}

// Thống kê theo ngày
export function fetchDailyStatistics(date) {
  return api.get('/api/admin/statistics', {
    params: { type: 'day', date }
  })
}

// Thống kê theo quý
export function fetchQuarterlyStatistics(quarter, year) {
  return api.get('/api/admin/statistics', {
    params: { type: 'quarter', quarter, year }
  })
}

// Thống kê theo năm
export function fetchYearlyStatistics(year) {
  return api.get('/api/admin/statistics', {
    params: { type: 'year', year }
  })
}

// Thống kê tổng hợp với filter động
export function fetchStatisticsByType(type, params = {}) {
  const baseParams = { type, ...params }
  return api.get('/api/admin/statistics', { params: baseParams })
}

// === THỐNG KÊ THEO SUẤT CHIẾU ===

// Thống kê doanh thu theo suất chiếu
export function fetchShowtimeStatistics(params = {}) {
  return api.get('/api/admin/statistics/showtime', { params })
}

// Thống kê theo giờ chiếu
export function fetchHourlyStatistics(params = {}) {
  return api.get('/api/admin/statistics/hourly', { params })
}

// Thống kê theo phòng chiếu
export function fetchRoomStatistics(params = {}) {
  return api.get('/api/admin/statistics/room', { params })
}

// Thống kê tỷ lệ lấp đầy phòng
export function fetchOccupancyStatistics(params = {}) {
  return api.get('/api/admin/statistics/occupancy', { params })
}

// Thống kê suất chiếu hot nhất
export function fetchTopShowtimes(params = {}) {
  return api.get('/api/admin/statistics/top-showtimes', { params })
}

// Thống kê giờ vàng (peak hours)
export function fetchPeakHoursStatistics(params = {}) {
  return api.get('/api/admin/statistics/peak-hours', { params })
}

// Thống kê khách hàng
export function fetchCustomerStatistics(params = {}) {
  return api.get('/api/admin/statistics/customers', { params })
}




