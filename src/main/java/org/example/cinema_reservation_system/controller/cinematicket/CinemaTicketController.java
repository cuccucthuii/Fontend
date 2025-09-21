package org.example.cinema_reservation_system.controller.cinematicket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketRequestDto;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketResponseDto;
import org.example.cinema_reservation_system.service.cinematicket.CinemaTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@CrossOrigin
        (origins = "http://localhost:5173")
public class CinemaTicketController {
    private final CinemaTicketService cinemaTicketService;
    @GetMapping("/history")
    public ResponseEntity<List<CinemaTicketResponseDto>> findAll() {
        return ResponseEntity.ok(cinemaTicketService.getAllVePhim());
    }
    @PostMapping("/new")
    public ResponseEntity<CinemaTicketResponseDto> createVePhim(@Valid @RequestBody CinemaTicketRequestDto dto) {
        CinemaTicketResponseDto response = cinemaTicketService.createVePhim(dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CinemaTicketResponseDto> getVePhimById(@Valid @PathVariable Integer id) {
        CinemaTicketResponseDto vePhimDto = cinemaTicketService.getVePhimDtoById(id);
        return ResponseEntity.ok(vePhimDto);
    }

    //thêm mới
    // ========== SOFT DELETE ENDPOINTS ==========

    /**
     * Xóa mềm vé phim - chuyển vào thùng rác
     * @param id ID của vé phim cần xóa
     * @return Vé phim đã được xóa mềm
     */
    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<CinemaTicketResponseDto> softDeleteVePhim(@PathVariable Integer id) {
        try {
            CinemaTicketResponseDto response = cinemaTicketService.softDeleteVePhim(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Khôi phục vé phim từ thùng rác
     * @param id ID của vé phim cần khôi phục
     * @return Vé phim đã được khôi phục
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<CinemaTicketResponseDto> restoreVePhim(@PathVariable Integer id) {
        try {
            CinemaTicketResponseDto response = cinemaTicketService.restoreVePhim(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy danh sách vé phim đã bị xóa (thùng rác)
     * @return Danh sách vé phim đã bị soft delete
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<CinemaTicketResponseDto>> getDeletedVePhim() {
        try {
            List<CinemaTicketResponseDto> deletedTickets = cinemaTicketService.getDeletedVePhim();
            return ResponseEntity.ok(deletedTickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cập nhật trạng thái thanh toán của vé phim
     * @param id ID của vé phim
     * @param trangThaiThanhToan Trạng thái mới (CHO_THANH_TOAN, DA_THANH_TOAN, DA_HOAN_TIEN)
     * @return Vé phim đã được cập nhật
     */
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<CinemaTicketResponseDto> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String trangThaiThanhToan) {
        try {
            CinemaTicketResponseDto response = cinemaTicketService.updatePaymentStatus(id, trangThaiThanhToan);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
