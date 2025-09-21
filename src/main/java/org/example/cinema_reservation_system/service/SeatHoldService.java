package org.example.cinema_reservation_system.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.cinema_reservation_system.entity.SeatHold;
import org.example.cinema_reservation_system.repository.SeatHoldRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;
    private static final Logger log = LoggerFactory.getLogger(SeatHoldService.class);

    public SeatHold holdSeat(Integer suatChieuId, Integer gheNgoiId, Integer userAccountId, String token, int minutes) {
        // Rule: Không cho giữ nếu đang có hold còn hiệu lực
        seatHoldRepository.findActiveHold(suatChieuId, gheNgoiId, LocalDateTime.now())
                .ifPresent(x -> { throw new IllegalStateException("Ghế đang được giữ"); });

        SeatHold sh = new SeatHold();
        // set only foreign key ids via references to avoid extra loads
        sh.setSuatChieu(new org.example.cinema_reservation_system.entity.ShowTime());
        sh.getSuatChieu().setIdSuatChieu(suatChieuId);
        sh.setGheNgoi(new org.example.cinema_reservation_system.entity.Seat());
        sh.getGheNgoi().setIdGheNgoi(gheNgoiId);
        if (userAccountId != null) {
            org.example.cinema_reservation_system.entity.UserAccount ua = new org.example.cinema_reservation_system.entity.UserAccount();
            ua.setIdTaiKhoan(userAccountId);
            sh.setUserAccount(ua);
        }
        sh.setHoldToken(token);
        sh.setThoiGianBatDau(LocalDateTime.now());
        sh.setThoiGianHetHan(LocalDateTime.now().plusMinutes(minutes));
        sh.setTrangThai("DANG_GIU");
        return seatHoldRepository.save(sh);
    }

    public List<SeatHold> listActiveHolds() {
        LocalDateTime now = LocalDateTime.now();
        return seatHoldRepository.findAll().stream()
                .filter(s -> "DANG_GIU".equals(s.getTrangThai()) && s.getThoiGianHetHan() != null && s.getThoiGianHetHan().isAfter(now))
                .toList();
    }

    // Dọn các hold quá hạn: chuyển trạng thái sang HET_HAN
    public int expireHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatHold> all = seatHoldRepository.findAll();
        int[] count = {0};
        all.forEach(sh -> {
            if ("DANG_GIU".equals(sh.getTrangThai()) && sh.getThoiGianHetHan() != null && !sh.getThoiGianHetHan().isAfter(now)) {
                sh.setTrangThai("HET_HAN");
                seatHoldRepository.save(sh);
                count[0]++;
            }
        });
        return count[0];
    }

    public boolean releaseByToken(String token) {
        return seatHoldRepository.findByHoldToken(token)
                .map(sh -> {
                    if (!"DANG_GIU".equals(sh.getTrangThai())) {
                        log.info("Release by token blocked: status={} token={}", sh.getTrangThai(), token);
                        return false; // Không release nếu đã HET_HAN/DA_MUA/HUY
                    }
                    sh.setTrangThai("HUY");
                    seatHoldRepository.save(sh);
                    log.info("Released seat hold by token: token={}", token);
                    return true;
                })
                .orElse(false);
    }

    public boolean releaseByShowAndSeat(Integer suatChieuId, Integer gheNgoiId) {
        return seatHoldRepository.findLatestByShowAndSeat(suatChieuId, gheNgoiId)
                .map(sh -> {
                    if (!"DANG_GIU".equals(sh.getTrangThai())) {
                        log.info("Release by seat blocked: status={} suatChieuId={} gheNgoiId={}", sh.getTrangThai(), suatChieuId, gheNgoiId);
                        return false;
                    }
                    sh.setTrangThai("HUY");
                    seatHoldRepository.save(sh);
                    log.info("Released seat hold by seat: suatChieuId={} gheNgoiId={}", suatChieuId, gheNgoiId);
                    return true;
                })
                .orElse(false);
    }
}


