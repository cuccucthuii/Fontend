package org.example.cinema_reservation_system.dto.combo;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.entity.FoodCombo;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboRequestDto {
    
    @NotBlank(message = "Tên combo không được để trống")
    @Size(max = 100, message = "Tên combo không được quá 100 ký tự")
    private String tenCombo;
    
    @Size(max = 500, message = "Mô tả không được quá 500 ký tự")
    private String moTa;
    
    @NotNull(message = "Giá combo không được để trống")
    @DecimalMin(value = "0.01", message = "Giá combo phải lớn hơn 0")
    @DecimalMax(value = "9999999.99", message = "Giá combo không được quá 9,999,999.99")
    private BigDecimal giaCombo;
    
    private FoodCombo.TrangThaiCombo trangThai = FoodCombo.TrangThaiCombo.HOAT_DONG;
}

