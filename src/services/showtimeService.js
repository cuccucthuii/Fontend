import api from '@/services/api';
import { SUAT_CHIEU_ENDPOINTS } from '../constants/api';

export const getShowtimesByMovieAndCinema = async (movieId, cinemaId) => {
  try {
    console.log("📡 Calling showtimes API with params:", { movieId, cinemaId });
    console.log("📡 API Endpoint:", SUAT_CHIEU_ENDPOINTS.GET_BY_MOVIE_CINEMA);
    console.log("📡 Full URL will be:", `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${SUAT_CHIEU_ENDPOINTS.GET_BY_MOVIE_CINEMA}`);
    
    // Validate parameters
    if (!movieId || !cinemaId) {
      throw new Error(`Missing required parameters: movieId=${movieId}, cinemaId=${cinemaId}`);
    }
    
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
    console.error("❌ Error details:", {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      params: error.config?.params
    });
    
    // Return empty data structure instead of throwing error
    return {
      success: false,
      message: `Không thể tải lịch chiếu: ${error.response?.status === 500 ? 'Dữ liệu không tồn tại' : error.message}`,
      data: {
        movie: null,
        cinema: null,
        showtimes: [],
        availableDates: []
      }
    };
  }
};

// Lấy tất cả suất chiếu theo phim (không cần rạp cụ thể)
export const getShowtimesByMovie = async (movieId) => {
  try {
    console.log("📡 Calling showtimes by movie API with movieId:", movieId);
    console.log("📡 API Endpoint:", SUAT_CHIEU_ENDPOINTS.GET_ALL);
    
    // Validate parameters
    if (!movieId) {
      throw new Error(`Missing required parameter: movieId=${movieId}`);
    }
    
    const response = await api.get(SUAT_CHIEU_ENDPOINTS.GET_ALL, {
      params: {
        movieId,
      },
    });
    
    console.log("📡 Showtimes by movie API Response:", response);
    return response.data;
  } catch (error) {
    console.error("❌ Error fetching showtimes by movie:", error);
    console.error("❌ Error details:", {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      params: error.config?.params
    });
    
    // Return empty data structure instead of throwing error
    return {
      content: [],
      message: `Không thể tải lịch chiếu: ${error.response?.status === 500 ? 'Dữ liệu không tồn tại' : error.message}`
    };
  }
};

// Lấy tất cả suất chiếu theo rạp (không cần phim cụ thể)
export const getShowtimesByCinema = async (cinemaId) => {
  try {
    console.log("📡 Calling showtimes by cinema API with cinemaId:", cinemaId);
    console.log("📡 API Endpoint:", SUAT_CHIEU_ENDPOINTS.GET_ALL);
    
    // Validate parameters
    if (!cinemaId) {
      throw new Error(`Missing required parameter: cinemaId=${cinemaId}`);
    }
    
    const response = await api.get(SUAT_CHIEU_ENDPOINTS.GET_ALL, {
      params: {
        cinemaId,
      },
    });
    
    console.log("📡 Showtimes by cinema API Response:", response);
    return response.data;
  } catch (error) {
    console.error("❌ Error fetching showtimes by cinema:", error);
    console.error("❌ Error details:", {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      params: error.config?.params
    });
    
    // Return empty data structure instead of throwing error
    return {
      content: [],
      message: `Không thể tải lịch chiếu: ${error.response?.status === 500 ? 'Dữ liệu không tồn tại' : error.message}`
    };
  }
};

// Lấy tất cả suất chiếu (không filter)
export const getAllShowtimes = async () => {
  try {
    console.log("📡 Calling all showtimes API");
    console.log("📡 API Endpoint:", SUAT_CHIEU_ENDPOINTS.GET_ALL);
    
    const response = await api.get(SUAT_CHIEU_ENDPOINTS.GET_ALL);
    
    console.log("📡 All showtimes API Response:", response);
    return response.data;
  } catch (error) {
    console.error("❌ Error fetching all showtimes:", error);
    console.error("❌ Error details:", {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url
    });
    
    // Return empty data structure instead of throwing error
    return {
      content: [],
      message: `Không thể tải lịch chiếu: ${error.response?.status === 500 ? 'Dữ liệu không tồn tại' : error.message}`
    };
  }
};

// Alias cho tương thích với code cũ
export const fetchShowtimesByMovieCinema = getShowtimesByMovieAndCinema;
export const fetchShowtimesByMovie = getShowtimesByMovie;
export const fetchShowtimesByCinema = getShowtimesByCinema;
export const fetchAllShowtimes = getAllShowtimes;
