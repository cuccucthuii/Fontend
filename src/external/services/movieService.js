import api from './api'
import { MOVIE_ENDPOINTS } from '../constants/api'

export function fetchMovies() {
  return api.get(MOVIE_ENDPOINTS.GET_ALL)
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
