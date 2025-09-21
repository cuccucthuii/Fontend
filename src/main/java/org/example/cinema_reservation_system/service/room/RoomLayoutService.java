package org.example.cinema_reservation_system.service.room;

import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutDto;
import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutRequestDto;

import java.util.List;
import java.util.Map;

public interface RoomLayoutService {
    
    /**
     * Lấy tất cả layout phòng chiếu
     */
    List<RoomLayoutDto> getAllRoomLayouts();
    
    /**
     * Lấy layout phòng chiếu theo ID phòng
     */
    RoomLayoutDto getRoomLayout(Integer idPhong);
    
    /**
     * Tạo layout phòng chiếu mới
     */
    RoomLayoutDto createRoomLayout(RoomLayoutRequestDto request);
    
    /**
     * Cập nhật layout phòng chiếu
     */
    RoomLayoutDto updateRoomLayout(Integer idPhong, RoomLayoutRequestDto request);
    
    /**
     * Xóa layout phòng chiếu (xóa tất cả ghế)
     */
    Map<String, Object> deleteRoomLayout(Integer idPhong);
    
    /**
     * Tính toán số ghế từ diện tích
     */
    Map<String, Object> calculateSeatsFromArea(Double dienTich);
    
    /**
     * Tạo danh sách ghế từ grid (Excel-like)
     */
    List<RoomLayoutDto.SeatPositionDto> generateSeatsFromGrid(Integer chieuRong, Integer chieuDai);
}


































