package org.example.cinema_reservation_system.service.seat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.seatdto.SeatLayoutDTO;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.repository.seat.SeatRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.repository.room.RoomRepository;
import org.example.cinema_reservation_system.service.seat.SeatLayoutService;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLayoutServiceImpl implements SeatLayoutService {

    private final SeatRepository seatRepository;
    private final ShowTimeRepository showTimeRepository;
    private final RoomRepository roomRepository;

    @Override
    public SeatLayoutDTO getSeatLayoutForShowtime(Long showTimeId) {
        try {
            // Lấy thông tin suất chiếu
            ShowTime showTime = showTimeRepository.findById(showTimeId.intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + showTimeId));

            // Lấy thông tin phòng chiếu
            Room room = showTime.getPhongChieu();
            
            // Lấy tất cả ghế trong phòng
            List<Seat> allSeats = seatRepository.findByPhongChieuIdPhongChieu(room.getIdPhongChieu());
            
            // Lấy ghế đã được đặt cho suất chiếu này
            Set<Integer> bookedSeatIds = getBookedSeatIds(showTimeId);
            
            // Lấy ghế đang được giữ
            Set<Integer> heldSeatIds = getHeldSeatIds(showTimeId);
            
            // Chuyển đổi sang DTO
            List<SeatLayoutDTO.SeatInfo> seatInfos = allSeats.stream()
                    .map(seat -> convertToSeatInfo(seat, bookedSeatIds, heldSeatIds))
                    .collect(Collectors.toList());
            
            // Tính toán layout
            int maxRow = allSeats.stream()
                    .mapToInt(seat -> seat.getHangGhe().charAt(0) - 'A' + 1)
                    .max().orElse(0);
            
            int maxColumn = allSeats.stream()
                    .mapToInt(seat -> Integer.parseInt(seat.getSoGhe()))
                    .max().orElse(0);
            
            // Tạo map giá theo loại ghế
            Map<String, BigDecimal> priceBySeatType = allSeats.stream()
                    .collect(Collectors.groupingBy(
                            Seat::getLoaiGhe,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    seats -> seats.get(0).getGiaGhe()
                            )
                    ));
            
            SeatLayoutDTO layout = new SeatLayoutDTO();
            layout.setShowTimeId(showTimeId);
            layout.setRoomId(room.getIdPhongChieu());
            layout.setRoomName(room.getTenPhongChieu());
            layout.setCinemaName(room.getRapChieu().getTenRapChieu());
            layout.setTotalRows(maxRow);
            layout.setTotalColumns(maxColumn);
            layout.setSeats(seatInfos);
            layout.setPriceBySeatType(priceBySeatType);
            layout.setMovieTitle(showTime.getPhim().getTenPhim());
            layout.setShowDate(showTime.getNgayChieu().toString());
            layout.setShowTime(showTime.getThoiGianBatDau().toString());
            
            return layout;
            
        } catch (Exception e) {
            log.error("Error getting seat layout for showtime: {}", showTimeId, e);
            throw new RuntimeException("Không thể lấy layout ghế cho suất chiếu: " + e.getMessage());
        }
    }

    @Override
    public SeatLayoutDTO getSeatLayoutForRoom(Integer roomId) {
        try {
            // Lấy thông tin phòng chiếu
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + roomId));

            // Lấy tất cả ghế trong phòng
            List<Seat> allSeats = seatRepository.findByPhongChieuIdPhongChieu(roomId);
            
            // Chuyển đổi sang DTO (không có thông tin booking)
            List<SeatLayoutDTO.SeatInfo> seatInfos = allSeats.stream()
                    .map(this::convertToSeatInfoBasic)
                    .collect(Collectors.toList());
            
            // Tính toán layout
            int maxRow = allSeats.stream()
                    .mapToInt(seat -> seat.getHangGhe().charAt(0) - 'A' + 1)
                    .max().orElse(0);
            
            int maxColumn = allSeats.stream()
                    .mapToInt(seat -> Integer.parseInt(seat.getSoGhe()))
                    .max().orElse(0);
            
            // Tạo map giá theo loại ghế
            Map<String, BigDecimal> priceBySeatType = allSeats.stream()
                    .collect(Collectors.groupingBy(
                            Seat::getLoaiGhe,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    seats -> seats.get(0).getGiaGhe()
                            )
                    ));
            
            SeatLayoutDTO layout = new SeatLayoutDTO();
            layout.setRoomId(roomId);
            layout.setRoomName(room.getTenPhongChieu());
            layout.setCinemaName(room.getRapChieu().getTenRapChieu());
            layout.setTotalRows(maxRow);
            layout.setTotalColumns(maxColumn);
            layout.setSeats(seatInfos);
            layout.setPriceBySeatType(priceBySeatType);
            
            return layout;
            
        } catch (Exception e) {
            log.error("Error getting seat layout for room: {}", roomId, e);
            throw new RuntimeException("Không thể lấy layout ghế cho phòng: " + e.getMessage());
        }
    }

    @Override
    public SeatLayoutDTO.SeatPriceInfo getSeatPrice(Integer seatId) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId));
            
            SeatLayoutDTO.SeatPriceInfo priceInfo = new SeatLayoutDTO.SeatPriceInfo();
            priceInfo.setSeatId(seatId);
            priceInfo.setSeatName(seat.getHangGhe() + seat.getSoGhe());
            priceInfo.setSeatType(seat.getLoaiGhe());
            priceInfo.setBasePrice(seat.getGiaGhe());
            priceInfo.setDiscountAmount(BigDecimal.ZERO); // TODO: Implement discount logic
            priceInfo.setFinalPrice(seat.getGiaGhe());
            priceInfo.setDiscountReason(null);
            
            return priceInfo;
            
        } catch (Exception e) {
            log.error("Error getting seat price for seat: {}", seatId, e);
            throw new RuntimeException("Không thể lấy giá ghế: " + e.getMessage());
        }
    }

    @Override
    public SeatLayoutDTO.TotalPriceResponse calculateTotalPrice(SeatLayoutDTO.TotalPriceRequest request) {
        try {
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            List<SeatLayoutDTO.PriceBreakdown> breakdown = new ArrayList<>();
            List<String> appliedDiscounts = new ArrayList<>();
            
            for (Integer seatId : request.getSeatIds()) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId));
                
                BigDecimal basePrice = seat.getGiaGhe();
                BigDecimal discountAmount = BigDecimal.ZERO; // TODO: Implement discount logic
                BigDecimal finalPrice = basePrice.subtract(discountAmount);
                
                subtotal = subtotal.add(basePrice);
                totalDiscount = totalDiscount.add(discountAmount);
                
                SeatLayoutDTO.PriceBreakdown priceBreakdown = new SeatLayoutDTO.PriceBreakdown();
                priceBreakdown.setSeatId(seatId);
                priceBreakdown.setSeatName(seat.getHangGhe() + seat.getSoGhe());
                priceBreakdown.setSeatType(seat.getLoaiGhe());
                priceBreakdown.setBasePrice(basePrice);
                priceBreakdown.setDiscountAmount(discountAmount);
                priceBreakdown.setFinalPrice(finalPrice);
                priceBreakdown.setDiscountReason(null);
                
                breakdown.add(priceBreakdown);
            }
            
            SeatLayoutDTO.TotalPriceResponse response = new SeatLayoutDTO.TotalPriceResponse();
            response.setSubtotal(subtotal);
            response.setTotalDiscount(totalDiscount);
            response.setTotalPrice(subtotal.subtract(totalDiscount));
            response.setBreakdown(breakdown);
            response.setAppliedDiscounts(appliedDiscounts);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error calculating total price", e);
            throw new RuntimeException("Không thể tính tổng giá vé: " + e.getMessage());
        }
    }

    @Override
    public List<SeatLayoutDTO.SeatInfo> getSeatsByStatus(Long showTimeId, String status) {
        try {
            ShowTime showTime = showTimeRepository.findById(showTimeId.intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + showTimeId));

            Room room = showTime.getPhongChieu();
            List<Seat> allSeats = seatRepository.findByPhongChieuIdPhongChieu(room.getIdPhongChieu());
            
            Set<Integer> bookedSeatIds = getBookedSeatIds(showTimeId);
            Set<Integer> heldSeatIds = getHeldSeatIds(showTimeId);
            
            return allSeats.stream()
                    .map(seat -> convertToSeatInfo(seat, bookedSeatIds, heldSeatIds))
                    .filter(seatInfo -> status.equals(seatInfo.getStatus()))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("Error getting seats by status: {} for showtime: {}", status, showTimeId, e);
            throw new RuntimeException("Không thể lấy ghế theo trạng thái: " + e.getMessage());
        }
    }

    // Helper methods
    private SeatLayoutDTO.SeatInfo convertToSeatInfo(Seat seat, Set<Integer> bookedSeatIds, Set<Integer> heldSeatIds) {
        SeatLayoutDTO.SeatInfo seatInfo = new SeatLayoutDTO.SeatInfo();
        seatInfo.setSeatId(seat.getIdGheNgoi());
        seatInfo.setSeatName(seat.getHangGhe() + seat.getSoGhe());
        seatInfo.setRow(seat.getHangGhe().charAt(0) - 'A' + 1);
        seatInfo.setColumn(Integer.parseInt(seat.getSoGhe()));
        seatInfo.setSeatType(seat.getLoaiGhe());
        seatInfo.setPrice(seat.getGiaGhe());
        
        // Xác định trạng thái
        if (bookedSeatIds.contains(seat.getIdGheNgoi())) {
            seatInfo.setStatus("DA_DAT");
            seatInfo.setAvailable(false);
            seatInfo.setSelectable(false);
        } else if (heldSeatIds.contains(seat.getIdGheNgoi())) {
            seatInfo.setStatus("HELD");
            seatInfo.setAvailable(false);
            seatInfo.setSelectable(false);
        } else if (seat.getTrangThai() == TrangThai.CON_TRONG) {
            seatInfo.setStatus("CON_TRONG");
            seatInfo.setAvailable(true);
            seatInfo.setSelectable(true);
        } else {
            seatInfo.setStatus(seat.getTrangThai().name());
            seatInfo.setAvailable(false);
            seatInfo.setSelectable(false);
        }
        
        return seatInfo;
    }

    private SeatLayoutDTO.SeatInfo convertToSeatInfoBasic(Seat seat) {
        SeatLayoutDTO.SeatInfo seatInfo = new SeatLayoutDTO.SeatInfo();
        seatInfo.setSeatId(seat.getIdGheNgoi());
        seatInfo.setSeatName(seat.getHangGhe() + seat.getSoGhe());
        seatInfo.setRow(seat.getHangGhe().charAt(0) - 'A' + 1);
        seatInfo.setColumn(Integer.parseInt(seat.getSoGhe()));
        seatInfo.setSeatType(seat.getLoaiGhe());
        seatInfo.setPrice(seat.getGiaGhe());
        seatInfo.setStatus(seat.getTrangThai().name());
        seatInfo.setAvailable(seat.getTrangThai() == TrangThai.CON_TRONG);
        seatInfo.setSelectable(seat.getTrangThai() == TrangThai.CON_TRONG);
        return seatInfo;
    }

    private Set<Integer> getBookedSeatIds(Long showTimeId) {
        // TODO: Implement logic to get booked seat IDs from ve_phim table
        return Collections.emptySet();
    }

    private Set<Integer> getHeldSeatIds(Long showTimeId) {
        // TODO: Implement logic to get held seat IDs from seat hold system
        return Collections.emptySet();
    }
}
