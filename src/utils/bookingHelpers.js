/**
 * Booking helpers để fix lỗi filter và xử lý dữ liệu
 */

/**
 * Safe filter function - đảm bảo schedules là array trước khi filter
 * @param {any} schedules - Dữ liệu schedules (có thể là array, object, null, undefined)
 * @param {Function} filterFn - Function để filter
 * @returns {Array} - Array đã được filter hoặc array rỗng
 */
export function safeFilter(schedules, filterFn) {
  // Kiểm tra schedules có phải array không
  if (!Array.isArray(schedules)) {
    console.warn('schedules is not an array:', schedules)
    return []
  }
  
  // Kiểm tra filterFn có phải function không
  if (typeof filterFn !== 'function') {
    console.warn('filterFn is not a function:', filterFn)
    return schedules
  }
  
  try {
    return schedules.filter(filterFn)
  } catch (error) {
    console.error('Error in safeFilter:', error)
    return []
  }
}

/**
 * Normalize schedules data - chuyển đổi dữ liệu về dạng array
 * @param {any} data - Dữ liệu gốc
 * @returns {Array} - Array đã được normalize
 */
export function normalizeSchedules(data) {
  // Nếu đã là array
  if (Array.isArray(data)) {
    return data
  }
  
  // Nếu là object có property schedules
  if (data && typeof data === 'object' && data.schedules) {
    return Array.isArray(data.schedules) ? data.schedules : []
  }
  
  // Nếu là object có property data
  if (data && typeof data === 'object' && data.data) {
    return Array.isArray(data.data) ? data.data : []
  }
  
  // Nếu là object có property showtimes
  if (data && typeof data === 'object' && data.showtimes) {
    return Array.isArray(data.showtimes) ? data.showtimes : []
  }
  
  // Nếu là object, chuyển thành array
  if (data && typeof data === 'object') {
    return Object.values(data)
  }
  
  // Mặc định trả về array rỗng
  return []
}

/**
 * Filter movies by branch - function an toàn
 * @param {any} schedules - Dữ liệu schedules
 * @param {string|number} branchId - ID của rạp
 * @returns {Array} - Array movies đã filter
 */
export function filterMoviesByBranch(schedules, branchId) {
  const normalizedSchedules = normalizeSchedules(schedules)
  
  return safeFilter(normalizedSchedules, (schedule) => {
    // Kiểm tra schedule có hợp lệ không
    if (!schedule || typeof schedule !== 'object') {
      return false
    }
    
    // Kiểm tra branchId
    if (branchId === null || branchId === undefined || branchId === '') {
      return true // Nếu không có branchId, trả về tất cả
    }
    
    // So sánh branchId
    const scheduleBranchId = schedule.branchId || schedule.id_rap_chieu || schedule.cinemaId
    return scheduleBranchId == branchId // Sử dụng == để so sánh string và number
  })
}

/**
 * Filter movies by date - function an toàn
 * @param {any} schedules - Dữ liệu schedules
 * @param {string} date - Ngày cần filter (YYYY-MM-DD)
 * @returns {Array} - Array movies đã filter
 */
export function filterMoviesByDate(schedules, date) {
  const normalizedSchedules = normalizeSchedules(schedules)
  
  return safeFilter(normalizedSchedules, (schedule) => {
    if (!schedule || typeof schedule !== 'object') {
      return false
    }
    
    if (!date) {
      return true
    }
    
    const scheduleDate = schedule.date || schedule.ngay_chieu || schedule.showDate
    return scheduleDate === date
  })
}

/**
 * Filter movies by status - function an toàn
 * @param {any} schedules - Dữ liệu schedules
 * @param {string} status - Trạng thái phim (DANG_CHIEU, SAP_CHIEU)
 * @returns {Array} - Array movies đã filter
 */
export function filterMoviesByStatus(schedules, status) {
  const normalizedSchedules = normalizeSchedules(schedules)
  
  return safeFilter(normalizedSchedules, (schedule) => {
    if (!schedule || typeof schedule !== 'object') {
      return false
    }
    
    if (!status) {
      return true
    }
    
    const movieStatus = schedule.movie?.status || 
                       schedule.phim?.trang_thai || 
                       schedule.status ||
                       schedule.movieStatus
    
    return movieStatus === status
  })
}

/**
 * Get unique movies from schedules
 * @param {any} schedules - Dữ liệu schedules
 * @returns {Array} - Array movies unique
 */
export function getUniqueMovies(schedules) {
  const normalizedSchedules = normalizeSchedules(schedules)
  const moviesMap = new Map()
  
  normalizedSchedules.forEach(schedule => {
    if (schedule && schedule.movie) {
      const movieId = schedule.movie.id || schedule.movie.id_phim
      if (movieId && !moviesMap.has(movieId)) {
        moviesMap.set(movieId, schedule.movie)
      }
    }
  })
  
  return Array.from(moviesMap.values())
}

/**
 * Validate schedule data structure
 * @param {any} schedule - Schedule object
 * @returns {boolean} - True nếu hợp lệ
 */
export function validateSchedule(schedule) {
  if (!schedule || typeof schedule !== 'object') {
    return false
  }
  
  // Kiểm tra các field cần thiết
  const requiredFields = ['movie', 'date', 'time']
  return requiredFields.every(field => {
    const value = schedule[field] || 
                  schedule[`${field}_id`] || 
                  schedule[`id_${field}`]
    return value !== null && value !== undefined
  })
}

/**
 * Debug function để log dữ liệu
 * @param {any} data - Dữ liệu cần debug
 * @param {string} label - Label cho log
 */
export function debugData(data, label = 'Data') {
  console.group(`🔍 Debug ${label}`)
  console.log('Type:', typeof data)
  console.log('Is Array:', Array.isArray(data))
  console.log('Value:', data)
  
  if (data && typeof data === 'object') {
    console.log('Keys:', Object.keys(data))
    console.log('Values:', Object.values(data))
  }
  
  console.groupEnd()
}

/**
 * Safe array operations
 */
export const safeArray = {
  /**
   * Safe map
   */
  map: (arr, fn) => {
    if (!Array.isArray(arr)) return []
    if (typeof fn !== 'function') return arr
    try {
      return arr.map(fn)
    } catch (error) {
      console.error('Error in safeArray.map:', error)
      return []
    }
  },
  
  /**
   * Safe find
   */
  find: (arr, fn) => {
    if (!Array.isArray(arr)) return undefined
    if (typeof fn !== 'function') return undefined
    try {
      return arr.find(fn)
    } catch (error) {
      console.error('Error in safeArray.find:', error)
      return undefined
    }
  },
  
  /**
   * Safe reduce
   */
  reduce: (arr, fn, initialValue) => {
    if (!Array.isArray(arr)) return initialValue
    if (typeof fn !== 'function') return initialValue
    try {
      return arr.reduce(fn, initialValue)
    } catch (error) {
      console.error('Error in safeArray.reduce:', error)
      return initialValue
    }
  }
}
