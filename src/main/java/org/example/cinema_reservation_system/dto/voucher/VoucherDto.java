package org.example.cinema_reservation_system.dto.voucher;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VoucherDto {
    
    @NotBlank(message = "Tên voucher không được để trống")
    @Size(max = 100, message = "Tên voucher không được quá 100 ký tự")
    private String tenVoucher;
    
    @DecimalMin(value = "0.0", message = "Phần trăm giảm phải >= 0")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm phải <= 100")
    private BigDecimal phanTramGiam;
    
    @DecimalMin(value = "0.0", message = "Mức giảm tối đa phải >= 0")
    private BigDecimal mucGiamToiDa;
    
    @DecimalMin(value = "0.0", message = "Số tiền giảm phải >= 0")
    private BigDecimal soTienGiam;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;
    
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer soLuong;
    
    private String dieuKienGiam;
    
    @NotNull(message = "Ngày tạo không được để trống")
    private LocalDate ngayTao;
}
