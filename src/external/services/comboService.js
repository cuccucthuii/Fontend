import api from './api';
import { COMBO_ENDPOINTS } from '../constants/api';

export const getAllCombos = async () => {
  try {
    const response = await api.get(COMBO_ENDPOINTS.GET_ALL);
    return response.data;
  } catch (error) {
    console.error("Error fetching combos:", error);
    throw error;
  }
};

export const getComboById = async (id) => {
  try {
    const response = await api.get(`${COMBO_ENDPOINTS.GET_BY_ID}/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching combo:", error);
    throw error;
  }
};

export const createCombo = async (comboData) => {
  try {
    const response = await api.post(COMBO_ENDPOINTS.ADD, comboData);
    return response.data;
  } catch (error) {
    console.error("Error creating combo:", error);
    throw error;
  }
};

export const updateCombo = async (id, comboData) => {
  try {
    const response = await api.put(`${COMBO_ENDPOINTS.UPDATE}/${id}`, comboData);
    return response.data;
  } catch (error) {
    console.error("Error updating combo:", error);
    throw error;
  }
};

export const deleteCombo = async (id) => {
  try {
    const response = await api.delete(`${COMBO_ENDPOINTS.DELETE}/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error deleting combo:", error);
    throw error;
  }
};
