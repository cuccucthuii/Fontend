// Delegate to root project API base URL
export { API_BASE_URL as BASE_URL } from "../../services/api";

// Auth endpoints
export const AUTH_ENDPOINTS = {
  LOGIN: "/api/auth/login",
  REGISTER: "/api/auth/register",
  LOGOUT: "/api/auth/logout",
  REFRESH_TOKEN: "/api/auth/refresh",
  SEND_OTP: "/api/auth/send-otp",
  VERIFY_OTP: "/api/auth/verify-otp",
};

// User endpoints
export const USER_ENDPOINTS = {
  GET_ALL: "/api/user/all",
  GET_BY_ID: "/api/user",
  ADD: "/api/user/add",
  UPDATE: "/api/user",
  DELETE: "/api/user",
};

// Movie endpoints
export const MOVIE_ENDPOINTS = {
  GET_ALL: "/api/phim",
  GET_BY_ID: "/api/phim",
  ADD: "/api/phim/add",
  UPDATE: "/api/phim",
  DELETE: "/api/phim",
};

// Phòng chiếu endpoints
export const PHONG_CHIEU_ENDPOINTS = {
  GET_ALL: "/api/phong_chieu",
  GET_BY_ID: "/api/phong_chieu",
  ADD: "/api/phong_chieu",
  UPDATE: "/api/phong_chieu",
  DELETE: "/api/phong_chieu",
};

// Rạp chiếu endpoints
export const RAP_CHIEU_ENDPOINTS = {
  GET_ALL: "/api/rap_chieu",
  GET_BY_ID: "/api/rap_chieu",
  ADD: "/api/rap_chieu",
  UPDATE: "/api/rap_chieu",
  DELETE: "/api/rap_chieu",
};

// Cinema endpoints (alias for RAP_CHIEU_ENDPOINTS)
export const CINEMA_ENDPOINTS = {
  GET_ALL: "/api/rap_chieu",
  GET_BY_ID: "/api/rap_chieu",
  ADD: "/api/rap_chieu",
  UPDATE: "/api/rap_chieu",
  DELETE: "/api/rap_chieu",
};

// Suất chiếu endpoints
export const SUAT_CHIEU_ENDPOINTS = {
  GET_ALL: "/api/suat-chieu",
  GET_BY_ID: "/api/suat-chieu",
  GET_BY_MOVIE_CINEMA: "/api/suat-chieu/by-movie-cinema",
  ADD: "/api/suat-chieu",
  UPDATE: "/api/suat-chieu",
  DELETE: "/api/suat-chieu",
};

// Ghế ngồi endpoints
export const GHE_NGOI_ENDPOINTS = {
  GET_ALL: "/api/ghengoi",
  GET_BY_ID: "/api/ghengoi",
  GET_BY_ROOM: "/api/ghengoi/phongchieu",
  ADD: "/api/ghengoi",
  UPDATE: "/api/ghengoi",
  DELETE: "/api/ghengoi",
};

// Hóa đơn endpoints
export const BILL_ENDPOINTS = {
  GET_ALL: "/api/bill",
  GET_BY_ID: "/api/bill",
  ADD: "/api/bill",
  UPDATE: "/api/bill",
  DELETE: "/api/bill",
};

// Invoice endpoints (cho booking system)
export const INVOICE_ENDPOINTS = {
  CREATE: "/api/invoice/create",
  GET_BY_ID: "/api/invoice",
  GET_BY_USER: "/api/invoice/user",
  UPDATE_STATUS: "/api/invoice/status",
};

// VNPay endpoints (cho tương lai)
export const VNPAY_ENDPOINTS = {
  CREATE_PAYMENT: "/api/vnpay/create-payment",
  CALLBACK: "/api/vnpay/callback",
  QUERY: "/api/vnpay/query",
};

// Combo endpoints
export const COMBO_ENDPOINTS = {
  GET_ALL: "/api/combos",
  GET_BY_ID: "/api/combos",
  ADD: "/api/combos",
  UPDATE: "/api/combos",
  DELETE: "/api/combos",
};

// Thống kê endpoints
export const STATISTICS_ENDPOINTS = {
  REVENUE: "/api/statistics/revenue",
  MOVIES: "/api/statistics/movies",
  USERS: "/api/statistics/users",
};

// Seat endpoints
export const SEAT_ENDPOINTS = {
  GET_BY_SCHEDULE: "/api/ghengoi/showtime", // GET /api/ghengoi/schedule/:scheduleId
};

// HTTP Status Codes
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
};

// User Roles
export const USER_ROLES = {
  ADMIN: "admin",
  USER: "user",
  STAFF: "staff",
};