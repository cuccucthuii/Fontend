package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.checkindto.CheckinRequestDto;
import org.example.cinema_reservation_system.dto.checkindto.CheckinResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface CheckinService {
    
    // Check-in vé
    CheckinResponseDto checkinVe(CheckinRequestDto request);
    
    // Check-in vé bằng mã vé
    CheckinResponseDto checkinVeByMaVe(CheckinRequestDto request);
    
    // Hủy check-in
    void huyCheckin(Integer idCheckin);
    
    // Lấy thông tin check-in theo ID
    CheckinResponseDto getCheckinById(Integer idCheckin);
    
    // Lấy danh sách check-in theo ngày
    List<CheckinResponseDto> getCheckinByNgay(LocalDateTime ngay);
    
    // Lấy danh sách check-in theo rạp chiếu
    List<CheckinResponseDto> getCheckinByRapChieu(Integer idRapChieu);
    
    // Lấy danh sách check-in theo suất chiếu
    List<CheckinResponseDto> getCheckinBySuatChieu(Integer idSuatChieu);
    
    // Kiểm tra vé đã được check-in chưa
    boolean isVeDaCheckin(Integer idVePhim);
    
    // Thống kê check-in theo ngày
    Long countCheckinByNgay(LocalDateTime ngay);
}

