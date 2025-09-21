package org.example.cinema_reservation_system.service.seat;

import org.example.cinema_reservation_system.dto.seatdto.SeatLayoutDTO;

import java.util.List;

public interface SeatLayoutService {
    
    /**
     * Lấy layout ghế cho suất chiếu cụ thể
     */
    SeatLayoutDTO getSeatLayoutForShowtime(Long showTimeId);
    
    /**
     * Lấy layout ghế cho phòng chiếu
     */
    SeatLayoutDTO getSeatLayoutForRoom(Integer roomId);
    
    /**
     * Lấy thông tin giá vé cho ghế cụ thể
     */
    SeatLayoutDTO.SeatPriceInfo getSeatPrice(Integer seatId);
    
    /**
     * Tính tổng giá vé cho danh sách ghế
     */
    SeatLayoutDTO.TotalPriceResponse calculateTotalPrice(SeatLayoutDTO.TotalPriceRequest request);
    
    /**
     * Lấy danh sách ghế theo trạng thái
     */
    List<SeatLayoutDTO.SeatInfo> getSeatsByStatus(Long showTimeId, String status);
}
