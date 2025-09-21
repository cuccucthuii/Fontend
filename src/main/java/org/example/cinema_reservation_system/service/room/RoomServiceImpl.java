package org.example.cinema_reservation_system.service.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.roomdto.RoomRequestDto;
import org.example.cinema_reservation_system.dto.roomdto.RoomResponseDto;
import org.example.cinema_reservation_system.entity.Room;
import org.example.cinema_reservation_system.repository.room.RoomRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.example.cinema_reservation_system.utils.enums.TrangThaiPhongChieu;
import org.example.cinema_reservation_system.utils.enums.TrangThaiRapChieu;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

//    @Override
//    @Transactional(readOnly = true)
//    public List<RoomResponseDto> findAllDto() {
//        List<Room> roomList = roomRepository.findAll();
//        return roomList.stream()
//                .map(this::convertToRoomResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public RoomResponseDto findByIdDto(Integer id) {
//        Room room = roomRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + id));
//
//        return convertToRoomResponseDto(room);
//    }

    @Override
    public RoomResponseDto savePhongChieu(RoomRequestDto dto) {
        Room room = new Room();
        room.setTenPhongChieu(dto.getTenPhongChieu());
        room.setDienTichPhong(BigDecimal.valueOf(dto.getDienTichPhong()));
        room.setTrangThai(TrangThai.HOAT_DONG);

        // Tạo Theater object để set relationship
        org.example.cinema_reservation_system.entity.Theater theater = new org.example.cinema_reservation_system.entity.Theater();
        theater.setIdRapChieu(dto.getIdRapChieu());
        room.setRapChieu(theater);

        Room savedRoom = roomRepository.save(room);
        return convertToRoomResponseDto(savedRoom);
    }

    @Override
    public RoomResponseDto updatePhongChieu(Integer id, RoomRequestDto dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + id));

        room.setTenPhongChieu(dto.getTenPhongChieu());
        room.setDienTichPhong(BigDecimal.valueOf(dto.getDienTichPhong()));

        // Cập nhật Theater relationship nếu cần
        if (dto.getIdRapChieu() != null) {
            org.example.cinema_reservation_system.entity.Theater theater = new org.example.cinema_reservation_system.entity.Theater();
            theater.setIdRapChieu(dto.getIdRapChieu());
            room.setRapChieu(theater);
        }

        Room updatedRoom = roomRepository.save(room);
        return convertToRoomResponseDto(updatedRoom);
    }

    @Override
    public void changeTrangThai(Integer id, TrangThaiPhongChieu trangThai) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + id));

        // Convert TrangThaiPhongChieu to TrangThai
        TrangThai trangThaiEnum = TrangThai.valueOf(trangThai.name());
        room.setTrangThai(trangThaiEnum);
        roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> filterPhongChieu(Integer idRap, String trangThaiRaw, String tenPhongChieu,
                                                  Double dienTichMin, Double dienTichMax, String sortBy, String order) {
        // Tạo Sort object
        Sort sort = Sort.by(Sort.Direction.fromString(order != null ? order : "asc"),
                sortBy != null ? sortBy : "idPhongChieu");

        // Sử dụng repository method đơn giản hơn
        List<Room> roomList = roomRepository.findAll(sort);

        // Filter manually (có thể cải thiện bằng cách tạo custom query)
        return roomList.stream()
                .filter(room -> idRap == null || (room.getRapChieu() != null && room.getRapChieu().getIdRapChieu().equals(idRap)))
                .filter(room -> trangThaiRaw == null || room.getTrangThai().name().equals(trangThaiRaw))
                .filter(room -> tenPhongChieu == null || room.getTenPhongChieu().contains(tenPhongChieu))
                .filter(room -> dienTichMin == null || room.getDienTichPhong().doubleValue() >= dienTichMin)
                .filter(room -> dienTichMax == null || room.getDienTichPhong().doubleValue() <= dienTichMax)
                .map(this::convertToRoomResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean softDelete(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            int deletedCount = roomRepository.softDeleteById(id);
            if (deletedCount > 0) {
                log.info("Đã xóa mềm phòng chiếu với ID: {}", id);
                return true;   //  Return boolean
            } else {
                log.warn("Không tìm thấy phòng chiếu để xóa mềm với ID: {}", id);
                return false;  //  Return boolean
            }

        } catch (Exception e) {
            log.error("Lỗi khi xóa mềm phòng chiếu với ID {}: {}", id, e.getMessage());
            return false;  // Return boolean
        }
    }

    @Override
    public boolean restore(Integer id) {
        return false;
    }

    @Override
    public boolean permanentDelete(Integer id) {
        return false;
    }

    private RoomResponseDto convertToRoomResponseDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setIdPhongChieu(room.getIdPhongChieu());
        dto.setTenPhongChieu(room.getTenPhongChieu());
        dto.setDienTichPhong(room.getDienTichPhong());
        // Convert TrangThai to TrangThaiPhongChieu
        TrangThaiPhongChieu trangThaiPhongChieu = TrangThaiPhongChieu.valueOf(room.getTrangThai().name());
        dto.setTrangThaiPhongChieu(trangThaiPhongChieu);

        // Thông tin từ Rạp Chiếu
        if (room.getRapChieu() != null) {
            dto.setIdRapChieu(room.getRapChieu().getIdRapChieu());
            dto.setTenRapChieu(room.getRapChieu().getTenRapChieu());
            dto.setDiaChi(room.getRapChieu().getDiaChi());
            dto.setSoDienThoai(room.getRapChieu().getSoDienThoai());
            // Convert TrangThai to TrangThaiRapChieu
            TrangThaiRapChieu trangThaiRapChieu = TrangThaiRapChieu.valueOf(room.getRapChieu().getTrangThai().name());
            dto.setTrangThaiRapChieu(trangThaiRapChieu);
        }

        return dto;
    }

    //thêm mới
    // ===== SOFT DELETE METHODS =====

    @Transactional
    @Override
    public boolean softDeleteById(Integer id) {  //  Sửa tên + return type
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            int deletedCount = roomRepository.softDeleteById(id);
            if (deletedCount > 0) {
                log.info("Đã xóa mềm phòng chiếu với ID: {}", id);
                return true;   //  Return boolean
            } else {
                log.warn("Không tìm thấy phòng chiếu để xóa mềm với ID: {}", id);
                return false;  //  Return boolean
            }

        } catch (Exception e) {
            log.error("Lỗi khi xóa mềm phòng chiếu với ID {}: {}", id, e.getMessage());
            return false;  // Return boolean
        }
    }

    @Transactional
    @Override
    public boolean restoreById(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            int restoredCount = roomRepository.restoreById(id);
            if (restoredCount > 0) {
                log.info("Đã khôi phục phòng chiếu với ID: {}", id);
            } else {
                log.warn("Không tìm thấy phòng chiếu để khôi phục với ID: {}", id);
            }

        } catch (Exception e) {
            log.error("Lỗi khi khôi phục phòng chiếu với ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Không thể khôi phục phòng chiếu: " + e.getMessage());
        }
        return false;
    }

    @Transactional
    @Override
    public boolean permanentDeleteById(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            int deletedCount = roomRepository.permanentDeleteById(id);
            if (deletedCount > 0) {
                log.info("Đã xóa vĩnh viễn phòng chiếu với ID: {}", id);
            } else {
                log.warn("Không tìm thấy phòng chiếu để xóa vĩnh viễn với ID: {}", id);
            }

        } catch (Exception e) {
            log.error("Lỗi khi xóa vĩnh viễn phòng chiếu với ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Không thể xóa vĩnh viễn phòng chiếu: " + e.getMessage());
        }
        return false;
    }

    // ===== FIND METHODS =====

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> findAllDto() {
        try {
            List<Room> rooms = roomRepository.findAllByDaXoaFalse();

            if (rooms.isEmpty()) {
                return new ArrayList<>();
            }

            return rooms.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách phòng chiếu: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto findByIdDto(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            Optional<Room> room = roomRepository.findByIdPhongChieuAndDaXoa(id, false);

            if (room.isEmpty()) {
                throw new RuntimeException("Không tìm thấy phòng chiếu với ID: " + id);
            }

            return convertToDto(room.get());

        } catch (Exception e) {
            log.error("Lỗi khi tìm phòng chiếu với ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Không thể tìm phòng chiếu: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Room findById(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            Optional<Room> room = roomRepository.findByIdPhongChieuAndDaXoa(id, false);

            if (room.isEmpty()) {
                throw new RuntimeException("Không tìm thấy phòng chiếu với ID: " + id);
            }

            return room.get();

        } catch (Exception e) {
            log.error("Lỗi khi tìm phòng chiếu với ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Không thể tìm phòng chiếu: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> findAllDeletedDto() {
        try {
            List<Room> deletedRooms = roomRepository.findAllByDaXoaTrue();

            if (deletedRooms.isEmpty()) {
                return new ArrayList<>();
            }

            return deletedRooms.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách phòng đã xóa: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Room findDeletedById(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            Optional<Room> deletedRoom = roomRepository.findByIdPhongChieuAndDaXoa(id, true);

            if (deletedRoom.isEmpty()) {
                log.warn("Không tìm thấy phòng chiếu đã xóa với ID: {}", id);
                return null;
            }

            return deletedRoom.get();

        } catch (Exception e) {
            log.error("Lỗi khi tìm phòng đã xóa với ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    // ===== EXISTS METHODS =====

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveById(Integer id) {
        try {
            return roomRepository.existsByIdPhongChieuAndDaXoa(id, false);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra phòng chiếu active với ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveByTenPhongChieu(String tenPhongChieu) {
        try {
            return roomRepository.existsActiveByTenPhongChieu(tenPhongChieu);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra tên phòng chiếu {}: {}", tenPhongChieu, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsDeletedById(Integer id) {
        try {
            return roomRepository.existsByIdPhongChieuAndDaXoa(id, true);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra phòng chiếu deleted với ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    // ===== COUNT METHODS =====

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        try {
            return roomRepository.countActive();
        } catch (Exception e) {
            log.error("Lỗi khi đếm phòng chiếu active: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countDeleted() {
        try {
            return roomRepository.countDeleted();
        } catch (Exception e) {
            log.error("Lỗi khi đếm phòng chiếu deleted: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRapChieu(Integer idRap) {
        try {
            return roomRepository.countActiveByRapChieu(idRap);
        } catch (Exception e) {
            log.error("Lỗi khi đếm phòng chiếu theo rạp {}: {}", idRap, e.getMessage());
            return 0;
        }
    }

    // ===== CONVERSION METHODS =====

    // ===== CONVERSION METHODS =====

    private RoomResponseDto convertToDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setIdPhongChieu(room.getIdPhongChieu());
        dto.setTenPhongChieu(room.getTenPhongChieu());
        dto.setDienTichPhong(room.getDienTichPhong());
        dto.setTrangThaiPhongChieu(TrangThaiPhongChieu.valueOf(room.getTrangThai().toString()));

        if (room.getRapChieu() != null) {
            dto.setIdRapChieu(room.getRapChieu().getIdRapChieu());
            dto.setTenRapChieu(room.getRapChieu().getTenRapChieu());
            dto.setDiaChi(room.getRapChieu().getDiaChi());
            dto.setSoDienThoai(room.getRapChieu().getSoDienThoai());
            dto.setTrangThaiRapChieu(TrangThaiRapChieu.valueOf(room.getRapChieu().getTrangThai().toString()));
        }

        // Soft delete fields
        dto.setDaXoa(room.getDaXoa());
        dto.setNgayXoa(room.getNgayXoa());
        dto.setNguoiXoa(room.getNguoiXoa());

        // Audit fields
//        dto.setNgayTao(room.getNgayTao());
//        dto.setNgayCapNhat(room.getNgayCapNhat());
//        dto.setNguoiTao(room.getNguoiTao());
//        dto.setNguoiCapNhat(room.getNguoiCapNhat());

        return dto;
    }

    private Room convertToEntity(RoomRequestDto dto) {
        Room room = new Room();
        room.setTenPhongChieu(dto.getTenPhongChieu());
        room.setDienTichPhong(BigDecimal.valueOf(dto.getDienTichPhong()));
        room.setTrangThai(convertTrangThai(String.valueOf(dto.getTrangThaiPhongChieu())));
        return room;
    }

    private TrangThai convertTrangThai(String trangThai) {
        if (trangThai == null) return TrangThai.HOAT_DONG;

        try {
            return TrangThai.valueOf(trangThai.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrangThai.HOAT_DONG;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Room findDelete(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            // Tìm phòng đã bị xóa mềm
            Optional<Room> deletedRoom = roomRepository.findByIdPhongChieuAndDaXoa(id, true);

            if (deletedRoom.isEmpty()) {
                log.warn("Không tìm thấy phòng chiếu đã xóa với ID: {}", id);
                return null;
            }

            return deletedRoom.get();

        } catch (Exception e) {
            log.error("Lỗi khi tìm phòng đã xóa với ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto findDeletedByIdDto(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID phòng chiếu không hợp lệ: " + id);
            }

            Optional<Room> deletedRoom = roomRepository.findByIdPhongChieuAndDaXoa(id, true);

            if (deletedRoom.isEmpty()) {
                log.warn("Không tìm thấy phòng chiếu đã xóa với ID: {}", id);
                return null;
            }

            return convertToDto(deletedRoom.get());

        } catch (Exception e) {
            log.error("Lỗi khi tìm phòng đã xóa với ID {}: {}", id, e.getMessage());
            return null;
        }
    }


}
