import api from '@/services/api';
import { SUAT_CHIEU_ENDPOINTS } from '../constants/api';

export const getShowtimesByMovieAndCinema = async (movieId, cinemaId) => {
  try {
    console.log("📡 Calling showtimes API with params:", { movieId, cinemaId });
    
    const response = await api.get(SUAT_CHIEU_ENDPOINTS.GET_BY_MOVIE_CINEMA, {
      params: {
        movieId,
        cinemaId,
      },
    });
    
    console.log("📡 Showtimes API Response:", response);
    return response.data;
  } catch (error) {
    console.error("❌ Error fetching showtimes:", error);
    throw error;
  }
};

// Alias cho tương thích với code cũ
export const fetchShowtimesByMovieCinema = getShowtimesByMovieAndCinema;
