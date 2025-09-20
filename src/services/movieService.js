import api from './api'
import { MOVIE_ENDPOINTS } from '../constants/api'

export async function fetchMovies() {
  // Thử nhiều endpoint fallback, trả về mảng rỗng nếu thất bại
  const candidates = [
    MOVIE_ENDPOINTS.GET_ALL,               // '/api/phim'
    `${MOVIE_ENDPOINTS.GET_ALL}/all`,      // '/api/phim/all'
    `${MOVIE_ENDPOINTS.GET_ALL}/list`,     // '/api/phim/list'
    '/api/phim/now-showing',
    '/api/phim/all'
  ]

  for (const url of candidates) {
    try {
      const res = await api.get(url)
      return res
    } catch (err) {
      // tiếp tục thử endpoint tiếp theo
      console.warn(`⚠️ Không lấy được phim từ ${url}`, err?.response?.status || err?.message)
    }
  }

  console.error('❌ Lỗi tải danh sách phim: tất cả endpoint đều thất bại. Kiểm tra BE tại cổng 8081.')
  return { data: [] }
}

export function getMovieById(id) {
  return api.get(`${MOVIE_ENDPOINTS.GET_BY_ID}/${id}`)
}

export function addMovie(data) {
  return api.post(MOVIE_ENDPOINTS.ADD, data)
}

export function updateMovie(id, data) {
  return api.put(`${MOVIE_ENDPOINTS.UPDATE}/${id}`, data)
}

export function deleteMovie(id) {
  return api.delete(`${MOVIE_ENDPOINTS.DELETE}/${id}`)
}

export function fetchGenres() {
  return api.get('/api/the-loai')
}

export function createGenre(data) {
  return api.post('/api/the-loai', data)
}

export function updateGenre(id, data) {
  return api.put(`/api/the-loai/${id}`, data)
}

export function deleteGenre(id) {
  return api.delete(`/api/the-loai/${id}`)
}
