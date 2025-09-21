package org.example.cinema_reservation_system.dto.roomdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomLayoutRequestDto {
    
    @NotNull(message = "ID phòng không được để trống")
    private Integer idPhong;
    
    @NotNull(message = "Chiều rộng không được để trống")
    @Min(value = 1, message = "Chiều rộng phải lớn hơn 0")
    private Integer chieuRong; // Số cột
    
    @NotNull(message = "Chiều dài không được để trống")
    @Min(value = 1, message = "Chiều dài phải lớn hơn 0")
    private Integer chieuDai;  // Số hàng
    
    @NotNull(message = "Diện tích không được để trống")
    @Min(value = 1, message = "Diện tích phải lớn hơn 0")
    private Double dienTich;   // Diện tích phòng (m²)
    
    private BigDecimal giaGheThuong = BigDecimal.valueOf(50000.0);  // Giá ghế thường
    private BigDecimal giaGheVIP = BigDecimal.valueOf(80000.0);     // Giá ghế VIP
    private BigDecimal giaGheCouple = BigDecimal.valueOf(120000.0); // Giá ghế couple
    
    // Cấu hình loại ghế theo vị trí
    private String loaiGheMacDinh = "THUONG"; // THUONG, VIP, COUPLE
} 