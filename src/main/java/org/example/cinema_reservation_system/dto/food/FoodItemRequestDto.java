package org.example.cinema_reservation_system.dto.food;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.entity.FoodItem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemRequestDto {
    
    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 100, message = "Tên món không được quá 100 ký tự")
    private String tenMon;
    
    @Size(max = 500, message = "Mô tả không được quá 500 ký tự")
    private String moTa;
    
    @NotNull(message = "Giá món không được để trống")
    @DecimalMin(value = "0.01", message = "Giá món phải lớn hơn 0")
    @DecimalMax(value = "9999999.99", message = "Giá món không được quá 9,999,999.99")
    private BigDecimal giaMon;
    
    @NotNull(message = "Loại món không được để trống")
    private FoodItem.LoaiMon loaiMon;
    
    private String hinhAnh;
    
    private FoodItem.TrangThaiMon trangThai = FoodItem.TrangThaiMon.HOAT_DONG;
}
