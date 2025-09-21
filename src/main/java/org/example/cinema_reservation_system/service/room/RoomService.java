package org.example.cinema_reservation_system.service.room;

import org.example.cinema_reservation_system.dto.roomdto.RoomRequestDto;
import org.example.cinema_reservation_system.dto.roomdto.RoomResponseDto;
import org.example.cinema_reservation_system.entity.Room;
import org.example.cinema_reservation_system.utils.enums.TrangThaiPhongChieu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomService {
    @Transactional(readOnly = true)
    Room findById(Integer id);

    @Transactional
    boolean softDeleteById(Integer id);

    @Transactional
    boolean restoreById(Integer id);

    @Transactional
    boolean permanentDeleteById(Integer id);

    List<RoomResponseDto> findAllDto();
    RoomResponseDto findByIdDto(Integer id);
    RoomResponseDto savePhongChieu(RoomRequestDto dto);
    RoomResponseDto updatePhongChieu(Integer id, RoomRequestDto dto);
    void changeTrangThai(Integer id, TrangThaiPhongChieu trangThai);
    List<RoomResponseDto> filterPhongChieu(Integer idRap, String trangThaiRaw, String tenPhongChieu,
                                           Double dienTichMin, Double dienTichMax, String sortBy, String order);
    // ===== SOFT DELETE METHODS =====

    /**
     * Xóa mềm phòng chiếu (chuyển vào thùng rác)
     */
    boolean softDelete(Integer id);

    /**
     * Khôi phục phòng chiếu từ thùng rác
     */
    boolean restore(Integer id);

    /**
     * Xóa vĩnh viễn phòng chiếu khỏi database
     */
    boolean permanentDelete(Integer id);

    // ===== TRASH METHODS =====

    /**
     * Lấy tất cả phòng chiếu đã bị xóa (trong thùng rác)
     */
    List<RoomResponseDto> findAllDeletedDto();

    /**
     * Lấy phòng chiếu đã bị xóa theo ID
     */
    RoomResponseDto findDeletedByIdDto(Integer id);

    /**
     * Lấy entity phòng chiếu đã bị xóa theo ID
     */
    Room findDeletedById(Integer id);

    @Transactional(readOnly = true)
    boolean existsActiveById(Integer id);

    @Transactional(readOnly = true)
    boolean existsActiveByTenPhongChieu(String tenPhongChieu);

    @Transactional(readOnly = true)
    boolean existsDeletedById(Integer id);

    @Transactional(readOnly = true)
    long countActive();

    @Transactional(readOnly = true)
    long countDeleted();

    @Transactional(readOnly = true)
    long countByRapChieu(Integer idRap);

    @Transactional(readOnly = true)
    Room findDelete(Integer id);
}
