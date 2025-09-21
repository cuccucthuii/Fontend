package org.example.cinema_reservation_system.service.seat;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.SeatSelectionDTO;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.seat.SeatRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiDatVe;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatSelectionServiceImpl implements SeatSelectionService {

    private final InvoiceRepository invoiceRepository;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;
    private final CustomerRepository customerRepository;

    // In-memory holds: key = showTimeId, value = map(holdToken -> HoldInfo)
    private final Map<Long, Map<String, HoldInfo>> holdsByShowTime = new ConcurrentHashMap<>();

    @Override
    public SeatSelectionDTO.AvailabilityResponse getAvailability(Long showTimeId) {
        cleanupExpiredHolds(showTimeId);
        Set<Long> heldSeatIds = getHeldSeatIds(showTimeId);
        // Tạm thời: chưa có Ticket => không có danh sách ghế đã bán từ DB
        Set<Long> bookedSeatIds = java.util.Collections.emptySet();

        // Note: to biết available cần danh sách ghế của phòng, ở đây chỉ trả về held/booked
        return new SeatSelectionDTO.AvailabilityResponse(showTimeId, Collections.emptySet(), heldSeatIds, bookedSeatIds);
    }

    @Override
    public SeatSelectionDTO.HoldResponse holdSeats(SeatSelectionDTO.HoldRequest request) {
        cleanupExpiredHolds(request.getShowTimeId());
        // chặn giữ ghế trùng lặp của cùng khách hàng cho cùng suất chiếu
        if (hasActiveHold(request.getCustomerId(), request.getShowTimeId(), request.getSeatIds())) {
            throw new IllegalStateException("Khách hàng đã có phiên giữ các ghế này");
        }
        // kiểm tra chưa bị booked
        boolean ok = areSeatsAvailable(request.getShowTimeId(), request.getSeatIds());
        if (!ok) {
            throw new IllegalStateException("Ghế đã được đặt hoặc đang bị giữ");
        }
        String token = UUID.randomUUID().toString();
        int ttl = request.getHoldSeconds() > 0 ? request.getHoldSeconds() : 180; // default 3 phút
        LocalDateTime until = LocalDateTime.now().plusSeconds(ttl);
        holdsByShowTime.computeIfAbsent(request.getShowTimeId(), k -> new ConcurrentHashMap<>())
                .put(token, new HoldInfo(request.getCustomerId(), request.getShowTimeId(), new HashSet<>(request.getSeatIds()), until));
        return new SeatSelectionDTO.HoldResponse(request.getCustomerId(), request.getShowTimeId(), request.getSeatIds(), until, token);
    }

    @Override
    public void releaseSeats(SeatSelectionDTO.ReleaseRequest request) {
        Map<String, HoldInfo> map = holdsByShowTime.getOrDefault(request.getShowTimeId(), Collections.emptyMap());
        HoldInfo info = map.get(request.getHoldToken());
        if (info != null && Objects.equals(info.customerId, request.getCustomerId())) {
            map.remove(request.getHoldToken());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmSeats(SeatSelectionDTO.ConfirmRequest request) {
        // xác nhận chỉ hợp lệ nếu token còn sống và ghế chưa bị đặt
        cleanupExpiredHolds(request.getShowTimeId());
        Map<String, HoldInfo> map = holdsByShowTime.getOrDefault(request.getShowTimeId(), Collections.emptyMap());
        HoldInfo info = map.get(request.getHoldToken());
        if (info == null || !Objects.equals(info.customerId, request.getCustomerId())) {
            return false;
        }
        if (!areSeatsAvailable(request.getShowTimeId(), request.getSeatIds())) {
            return false;
        }
        // Tạo booking Beta theo luồng dự án hiện tại
        Optional<ShowTime> showTimeOpt = showTimeRepository.findById(request.getShowTimeId().intValue());
        Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId().intValue());
        if (showTimeOpt.isEmpty() || customerOpt.isEmpty()) {
            return false;
        }
        ShowTime showTime = showTimeOpt.get();
        Room room = showTime.getPhongChieu();
        Theater theater = room != null ? room.getRapChieu() : null;
        if (theater == null || theater.getTenRapChieu() == null || !theater.getTenRapChieu().toLowerCase().contains("beta")) {
            return false; // chỉ cho phép rạp Beta
        }
        List<Seat> seats = seatRepository.findAllById(request.getSeatIds().stream().map(Long::intValue).toList());
        if (seats.size() != request.getSeatIds().size()) {
            return false;
        }
        // TODO: Tạo Invoice + Ticket theo luồng mới. Tạm thời chỉ xác nhận hold.
        // xóa hold
        map.remove(request.getHoldToken());
        return true;
    }

    @Override
    public boolean areSeatsAvailable(Long showTimeId, Set<Long> seatIds) {
        cleanupExpiredHolds(showTimeId);
        // check holds
        Set<Long> heldSeatIds = getHeldSeatIds(showTimeId);
        for (Long sid : seatIds) {
            if (heldSeatIds.contains(sid)) return false;
        }
        // Tạm thời: chưa kiểm tra DB vì bỏ Booking; chỉ kiểm tra holds
        return true;
    }

    private void cleanupExpiredHolds(Long showTimeId) {
        Map<String, HoldInfo> map = holdsByShowTime.get(showTimeId);
        if (map == null) return;
        LocalDateTime now = LocalDateTime.now();
        map.entrySet().removeIf(e -> e.getValue().holdUntil.isBefore(now));
    }

    private Set<Long> getHeldSeatIds(Long showTimeId) {
        Map<String, HoldInfo> map = holdsByShowTime.get(showTimeId);
        if (map == null) return Collections.emptySet();
        return map.values().stream()
                .flatMap(h -> h.seatIds.stream())
                .collect(Collectors.toSet());
    }

    private record HoldInfo(Long customerId, Long showTimeId, Set<Long> seatIds, LocalDateTime holdUntil) {}

    private boolean hasActiveHold(Long customerId, Long showTimeId, Set<Long> seatIds) {
        Map<String, HoldInfo> map = holdsByShowTime.get(showTimeId);
        if (map == null || map.isEmpty()) return false;
        LocalDateTime now = LocalDateTime.now();
        for (HoldInfo info : map.values()) {
            if (Objects.equals(info.customerId, customerId) && info.holdUntil.isAfter(now)) {
                // nếu giao nhau ghế
                for (Long sid : seatIds) {
                    if (info.seatIds.contains(sid)) return true;
                }
            }
        }
        return false;
    }
}
