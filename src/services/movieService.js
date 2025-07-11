import api from './api'
import { MOVIE_ENDPOINTS } from '../constants/api'

export function fetchMovies() {
  return api.get(MOVIE_ENDPOINTS.GET_ALL)
}

export function getMovieById(id) {
  return api.get(`${MOVIE_ENDPOINTS.GET_BY_ID}/${id}`)
}

export function addMovie(movie) {
  return api.post(MOVIE_ENDPOINTS.ADD, movie)
}

export function updateMovie(id, movie) {
  return api.put(`${MOVIE_ENDPOINTS.UPDATE}/${id}`, movie)
}

export function deleteMovie(id) {
  return api.delete(`${MOVIE_ENDPOINTS.DELETE}/${id}`)
}