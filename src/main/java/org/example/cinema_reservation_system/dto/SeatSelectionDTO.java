package org.example.cinema_reservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSelectionDTO {
    private Long showTimeId;
    private Long theaterId;
    private Long roomId;
    private Set<Long> seatIds;
    private List<String> seatNames;
    private LocalDateTime holdUntil;
    private boolean available;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityRequest {
        private Long showTimeId;
        private Set<Long> seatIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityResponse {
        private Long showTimeId;
        private Set<Long> availableSeatIds;
        private Set<Long> heldSeatIds;
        private Set<Long> bookedSeatIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HoldRequest {
        private Long customerId;
        private Long showTimeId;
        private Set<Long> seatIds;
        private int holdSeconds; // thời gian giữ ghế tạm thời
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HoldResponse {
        private Long customerId;
        private Long showTimeId;
        private Set<Long> seatIds;
        private LocalDateTime holdUntil;
        private String holdToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReleaseRequest {
        private Long customerId;
        private Long showTimeId;
        private Set<Long> seatIds;
        private String holdToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmRequest {
        private Long customerId;
        private Long showTimeId;
        private Set<Long> seatIds;
        private String holdToken;
    }
}












































