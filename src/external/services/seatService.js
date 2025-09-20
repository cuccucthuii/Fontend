import api from '@/services/api'
import { SEAT_ENDPOINTS } from '@/constants/api.js';

export function fetchSeats() {
  return api.get('/api/ghengoi')
}

export function createSeat(data) {
  return api.post('/api/ghengoi', data)
}

export function updateSeat(id, seatData) {
  return api.put(`/api/ghengoi/${id}`, seatData)
}

/**
 * Get seats by schedule ID
 * @param {number} scheduleId - Schedule ID
 * @returns {Promise} API response with seat data
 */
export const getSeatsBySchedule = async (scheduleId) => {
  try {
    console.log("🪑 Calling seats API for schedule:", scheduleId);
    
    const response = await api.get(`${SEAT_ENDPOINTS.GET_BY_SCHEDULE}/${scheduleId}`);
    
    console.log("🪑 Seats API Raw Response:", response);
    console.log("🪑 Seats Response Data:", response.data);
    
    if (response.data) {
      return {
        success: true,
        data: mapSeatsData(response.data.data || response.data)
      };
    }
    
    return {
      success: false,
      message: "Không có dữ liệu ghế"
    };
  } catch (error) {
    console.error("❌ Error fetching seats by schedule:", error);
    return {
      success: false,
      message: error.response?.data?.message || "Lỗi khi tải danh sách ghế",
      error: error
    };
  }
};

/**
 * Get seats by showtime/room ID (DEPRECATED - use getSeatsBySchedule instead)
 * @param {number} idPhongChieu - Room ID (idphongchieu)
 * @returns {Promise} API response with seat data
 */
export const getSeatsByShowtime = (idPhongChieu) => {
  console.warn("⚠️ getSeatsByShowtime is deprecated, use getSeatsBySchedule instead");
  return api.get(`${SEAT_ENDPOINTS.GET_BY_SCHEDULE}/${idPhongChieu}`);
};

/**
 * Map API response to frontend format
 * @param {Array} apiSeats - Raw seats data from API
 * @returns {Object} Formatted seats data
 */
export const mapSeatsData = (apiSeats) => {
  console.log("🗺️ Mapping seats data:", apiSeats);
  
  try {
    if (!apiSeats || !Array.isArray(apiSeats)) {
      console.warn("⚠️ Invalid seats data provided to mapSeatsData:", apiSeats);
      return {
        seatsByRow: {},
        seatRows: [],
        seatCols: [],
        bookedSeats: [],
        availableSeats: [],
        totalSeats: 0,
        roomInfo: null
      };
    }

    // Group seats by row
    const seatsByRow = {};
    const bookedSeats = [];
    const availableSeats = [];
    
    apiSeats.forEach(seat => {
      const row = seat.hangGhe;
      const col = seat.soGhe;
      const seatCode = row + col;
      
      if (!seatsByRow[row]) {
        seatsByRow[row] = [];
      }
      
      seatsByRow[row].push({
        id: seat.idGheNgoi,
        row: row,
        col: col,
        code: seatCode,
        status: seat.trangThai,
        type: seat.loaiGhe,
        price: seat.giaGhe,
        isValid: seat.viTriGheHopLe
      });
      
      // Categorize seats by status
      if (seat.trangThai === 'CON_TRONG') {
        availableSeats.push(seatCode);
      } else {
        bookedSeats.push(seatCode);
      }
    });
    
    // Get unique rows and columns for rendering
    const seatRows = Object.keys(seatsByRow).sort();
    const maxCols = Math.max(...Object.values(seatsByRow).map(rowSeats => 
      Math.max(...rowSeats.map(seat => parseInt(seat.col)))
    ));
    const seatCols = Array.from({length: maxCols}, (_, i) => i + 1);
    
    const result = {
      seatsByRow,
      seatRows,
      seatCols,
      bookedSeats,
      availableSeats,
      totalSeats: apiSeats.length,
      roomInfo: apiSeats[0]?.phongChieu || null
    };
    
    console.log("✅ Mapped seats data:", result);
    return result;
  } catch (error) {
    console.error('❌ Error mapping seats data:', error);
    return {
      seatsByRow: {},
      seatRows: [],
      seatCols: [],
      bookedSeats: [],
      availableSeats: [],
      totalSeats: 0,
      roomInfo: null
    };
  }
};