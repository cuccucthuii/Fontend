import api from '@/services/api';
import { CINEMA_ENDPOINTS } from "../constants/api";

export const getAllCinemas = async () => {
  try {
    const response = await api.get(CINEMA_ENDPOINTS.GET_ALL);
    return response.data;
  } catch (error) {
    console.error("Error fetching cinemas:", error);
    throw error;
  }
};
