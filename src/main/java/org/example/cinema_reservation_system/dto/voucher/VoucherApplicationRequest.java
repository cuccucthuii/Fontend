package org.example.cinema_reservation_system.dto.voucher;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class VoucherApplicationRequest {
    
    @NotNull(message = "ID khách hàng không được để trống")
    private Integer customerId;
    
    @NotNull(message = "ID voucher không được để trống")
    private Integer voucherId;
    
    private Integer orderId;
}
