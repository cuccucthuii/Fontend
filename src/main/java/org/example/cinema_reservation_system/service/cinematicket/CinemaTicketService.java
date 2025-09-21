package org.example.cinema_reservation_system.service.cinematicket;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketResponseDto;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CinemaTicketService {
    List<CinemaTicketResponseDto> getAllVePhim();
    CinemaTicketResponseDto createVePhim(CinemaTicketRequestDto vePhimRequestDto);
    CinemaTicketResponseDto getVePhimDtoById(Integer id);

    //thêm
    // ========== SOFT DELETE METHODS ==========

    /**
     * Soft delete vé phim - chuyển vào thùng rác
     * @param id ID của vé phim
     * @return Vé phim đã được soft delete
     */
    CinemaTicketResponseDto softDeleteVePhim(Integer id);

    /**
     * Khôi phục vé phim từ thùng rác
     * @param id ID của vé phim
     * @return Vé phim đã được khôi phục
     */
    CinemaTicketResponseDto restoreVePhim(Integer id);

    /**
     * Lấy danh sách vé phim đã bị xóa (thùng rác)
     * @return Danh sách vé phim đã bị soft delete
     */
    List<CinemaTicketResponseDto> getDeletedVePhim();

    CinemaTicketResponseDto updatePaymentStatus(Integer id, String trangThaiThanhToan);
}
