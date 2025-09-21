package org.example.cinema_reservation_system.dto.seatdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatLayoutDTO {
    
    private Long showTimeId;
    private Integer roomId;
    private String roomName;
    private String cinemaName;
    private Integer totalRows;
    private Integer totalColumns;
    private List<SeatInfo> seats;
    private Map<String, BigDecimal> priceBySeatType;
    private String movieTitle;
    private String showDate;
    private String showTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private Integer seatId;
        private String seatName; // A1, B2, etc.
        private Integer row;
        private Integer column;
        private String seatType; // THUONG, VIP, COUPLE
        private String status; // CON_TRONG, DA_DAT, DANG_SU_DUNG, HELD
        private BigDecimal price;
        private boolean available;
        private boolean selectable;
        private String holdToken; // null if not held
        private Long holdUntil; // timestamp
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatPriceInfo {
        private Integer seatId;
        private String seatName;
        private String seatType;
        private BigDecimal basePrice;
        private BigDecimal discountAmount;
        private BigDecimal finalPrice;
        private String discountReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalPriceRequest {
        private Long showTimeId;
        private List<Integer> seatIds;
        private List<String> appliedVouchers; // Optional
        private List<String> appliedPromotions; // Optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalPriceResponse {
        private BigDecimal subtotal;
        private BigDecimal totalDiscount;
        private BigDecimal totalPrice;
        private List<PriceBreakdown> breakdown;
        private List<String> appliedDiscounts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceBreakdown {
        private Integer seatId;
        private String seatName;
        private String seatType;
        private BigDecimal basePrice;
        private BigDecimal discountAmount;
        private BigDecimal finalPrice;
        private String discountReason;
    }
}
