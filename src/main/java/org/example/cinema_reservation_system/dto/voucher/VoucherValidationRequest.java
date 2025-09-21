package org.example.cinema_reservation_system.dto.voucher;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class VoucherValidationRequest {
    
    @NotBlank(message = "Mã voucher không được để trống")
    private String voucherCode;
    
    @NotNull(message = "Số tiền đơn hàng không được để trống")
    @DecimalMin(value = "0.0", message = "Số tiền đơn hàng phải >= 0")
    private BigDecimal orderAmount;
    
    private Integer customerId;
}
