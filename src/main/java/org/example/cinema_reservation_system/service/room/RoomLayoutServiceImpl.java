package org.example.cinema_reservation_system.service.room;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutDto;
import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutRequestDto;
import org.example.cinema_reservation_system.entity.Room;
import org.example.cinema_reservation_system.entity.Seat;
import org.example.cinema_reservation_system.repository.room.RoomRepository;
import org.example.cinema_reservation_system.repository.seat.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomLayoutServiceImpl implements RoomLayoutService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomLayoutDto> getAllRoomLayouts() {
        List<Room> roomList = roomRepository.findAll();
        return roomList.stream()
                .map(this::convertToRoomLayoutDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomLayoutDto getRoomLayout(Integer idPhong) {
        Room room = roomRepository.findById(idPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + idPhong));
        
        return convertToRoomLayoutDto(room);
    }

    @Override
    public RoomLayoutDto createRoomLayout(RoomLayoutRequestDto request) {
        Room room = roomRepository.findById(request.getIdPhong())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + request.getIdPhong()));

        // Xóa tất cả ghế cũ nếu có
        seatRepository.deleteByPhongChieuIdPhongChieu(request.getIdPhong());

        // Tạo ghế mới theo grid
        List<Seat> seatList = generateSeatsFromGrid(
                request.getChieuRong(),
                request.getChieuDai(),
                request.getIdPhong(),
                request.getGiaGheThuong(),
                request.getGiaGheVIP(),
                request.getGiaGheCouple(),
                request.getLoaiGheMacDinh()
        );

        seatRepository.saveAll(seatList);

        // Cập nhật thông tin phòng
        room.setDienTichPhong(BigDecimal.valueOf(request.getDienTich()));
        roomRepository.save(room);

        return convertToRoomLayoutDto(room);
    }

    @Override
    public RoomLayoutDto updateRoomLayout(Integer idPhong, RoomLayoutRequestDto request) {
        return createRoomLayout(request); // Logic tương tự như create
    }

    @Override
    public Map<String, Object> deleteRoomLayout(Integer idPhong) {
        // Xóa tất cả ghế
        seatRepository.deleteByPhongChieuIdPhongChieu(idPhong);
        
        // Reset thông tin phòng
        Room room = roomRepository.findById(idPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + idPhong));

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Đã xóa tất cả ghế trong phòng " + room.getTenPhongChieu());
        result.put("idPhong", idPhong);
        result.put("tongSoGheDaXoa", 0);
        
        return result;
    }

    @Override
    public Map<String, Object> calculateSeatsFromArea(Double dienTich) {
        // Công thức tính toán: 0.8m²/ghế (tiêu chuẩn)
        double dienTichTrungBinhMoiGhe = 0.8;
        int tongSoGhe = (int) Math.floor(dienTich / dienTichTrungBinhMoiGhe);
        
        // Tính chiều rộng và chiều dài tối ưu (tỷ lệ 16:9)
        double ratio = 16.0 / 9.0;
        int chieuRong = (int) Math.ceil(Math.sqrt(tongSoGhe * ratio));
        int chieuDai = (int) Math.ceil(tongSoGhe / (double) chieuRong);

        Map<String, Object> result = new HashMap<>();
        result.put("dienTich", dienTich);
        result.put("tongSoGhe", tongSoGhe);
        result.put("chieuRong", chieuRong);
        result.put("chieuDai", chieuDai);
        result.put("dienTichTrungBinhMoiGhe", dienTichTrungBinhMoiGhe);
        
        return result;
    }

    @Override
    public List<RoomLayoutDto.SeatPositionDto> generateSeatsFromGrid(Integer chieuRong, Integer chieuDai) {
        List<RoomLayoutDto.SeatPositionDto> seats = new ArrayList<>();
        
        for (int hang = 1; hang <= chieuDai; hang++) {
            for (int cot = 1; cot <= chieuRong; cot++) {
                String tenGhe = generateSeatName(hang, cot);
                String loaiGhe = determineSeatType(hang, chieuDai, cot, chieuRong);
                BigDecimal giaGhe = calculateSeatPrice(loaiGhe);
                
                RoomLayoutDto.SeatPositionDto seat = new RoomLayoutDto.SeatPositionDto();
                seat.setTenGhe(tenGhe);
                seat.setHang(hang);
                seat.setCot(cot);
                seat.setTrangThai("TRONG");
                seat.setLoaiGhe(loaiGhe);
                seat.setGiaGhe(giaGhe);
                
                seats.add(seat);
            }
        }
        
        return seats;
    }

    private RoomLayoutDto convertToRoomLayoutDto(Room room) {
        List<Seat> seatList = seatRepository.findByPhongChieuIdPhongChieu(room.getIdPhongChieu());
        
        List<RoomLayoutDto.SeatPositionDto> seatDtos = seatList.stream()
                .map(this::convertToSeatPositionDto)
                .collect(Collectors.toList());

        RoomLayoutDto dto = new RoomLayoutDto();
        dto.setIdPhong(room.getIdPhongChieu());
        dto.setTenPhong(room.getTenPhongChieu());
        dto.setChieuRong(0); // Room entity không có field này
        dto.setChieuDai(0);  // Room entity không có field này
        dto.setDienTich(room.getDienTichPhong().doubleValue());
        dto.setTongSoGhe(seatList.size());
        dto.setDanhSachGhe(seatDtos);
        
        return dto;
    }

    private RoomLayoutDto.SeatPositionDto convertToSeatPositionDto(Seat seat) {
        RoomLayoutDto.SeatPositionDto dto = new RoomLayoutDto.SeatPositionDto();
        dto.setIdGhe(seat.getIdGheNgoi());
        dto.setTenGhe(seat.getHangGhe() + seat.getSoGhe());
        dto.setHang(convertHangToNumber(seat.getHangGhe()));
        dto.setCot(Integer.parseInt(seat.getSoGhe()));
        dto.setTrangThai(seat.getTrangThai().name());
        dto.setLoaiGhe(seat.getLoaiGhe());
        dto.setGiaGhe(seat.getGiaGhe());
        return dto;
    }

    private List<Seat> generateSeatsFromGrid(Integer chieuRong, Integer chieuDai, Integer idPhong,
                                          BigDecimal giaGheThuong, BigDecimal giaGheVIP, 
                                          BigDecimal giaGheCouple, String loaiGheMacDinh) {
        List<Seat> seatList = new ArrayList<>();
        
        for (int hang = 1; hang <= chieuDai; hang++) {
            for (int cot = 1; cot <= chieuRong; cot++) {
                String hangGhe = generateHangGhe(hang);
                String soGhe = String.valueOf(cot);
                String loaiGhe = determineSeatType(hang, chieuDai, cot, chieuRong);
                BigDecimal giaGhe = calculateSeatPrice(loaiGhe, giaGheThuong, giaGheVIP, giaGheCouple);
                
                Seat seat = new Seat();
                seat.setHangGhe(hangGhe);
                seat.setSoGhe(soGhe);
                seat.setTrangThai(org.example.cinema_reservation_system.utils.enums.TrangThai.CON_TRONG);
                seat.setLoaiGhe(loaiGhe);
                seat.setGiaGhe(giaGhe);
                
                // Tạo Room object để set relationship
                Room room = new Room();
                room.setIdPhongChieu(idPhong);
                seat.setPhongChieu(room);
                
                seatList.add(seat);
            }
        }
        
        return seatList;
    }

    private String generateHangGhe(int hang) {
        // Chuyển đổi số hàng thành chữ cái (1=A, 2=B, ...)
        char hangChar = (char) ('A' + hang - 1);
        return String.valueOf(hangChar);
    }
    
    private int convertHangToNumber(String hangGhe) {
        if (hangGhe == null || hangGhe.isEmpty()) {
            return 0;
        }
        return hangGhe.charAt(0) - 'A' + 1;
    }

    private String generateSeatName(int hang, int cot) {
        // Tạo tên ghế theo format: A1, A2, B1, B2, ...
        char hangChar = (char) ('A' + hang - 1);
        return String.format("%c%d", hangChar, cot);
    }

    private String determineSeatType(int hang, int chieuDai, int cot, int chieuRong) {
        // Logic phân loại ghế:
        // - Hàng cuối (VIP): 2 hàng cuối
        // - Ghế couple: 2 cột cuối
        // - Còn lại: ghế thường
        
        if (hang > chieuDai - 2) {
            return "VIP";
        } else if (cot > chieuRong - 2) {
            return "COUPLE";
        } else {
            return "THUONG";
        }
    }

    private BigDecimal calculateSeatPrice(String loaiGhe) {
        return calculateSeatPrice(loaiGhe, 
                BigDecimal.valueOf(50000.0), 
                BigDecimal.valueOf(80000.0), 
                BigDecimal.valueOf(120000.0));
    }

    private BigDecimal calculateSeatPrice(String loaiGhe, BigDecimal giaGheThuong, 
                                        BigDecimal giaGheVIP, BigDecimal giaGheCouple) {
        switch (loaiGhe) {
            case "VIP":
                return giaGheVIP;
            case "COUPLE":
                return giaGheCouple;
            default:
                return giaGheThuong;
        }
    }
}
