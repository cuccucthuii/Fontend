package org.example.cinema_reservation_system.config;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.service.SeatHoldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SeatHoldCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(SeatHoldCleanupScheduler.class);
    private final SeatHoldService seatHoldService;

    // Chạy mỗi phút dọn các hold quá hạn
    @Scheduled(fixedDelay = 60_000)
    public void expireHoldsJob() {
        int expired = seatHoldService.expireHolds();
        if (expired > 0) {
            log.info("SeatHoldCleanup: expired {} holds", expired);
        }
    }
}




















