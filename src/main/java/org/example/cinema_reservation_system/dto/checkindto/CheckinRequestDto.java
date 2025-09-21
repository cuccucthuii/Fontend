package org.example.cinema_reservation_system.dto.checkindto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckinRequestDto {
    
    @NotNull(message = "ID vé phim không được để trống")
    private Integer idVePhim;
    
    private String maVe;
    
    @NotNull(message = "ID nhân viên check-in không được để trống")
    private Integer idNhanVienCheckin;
    
    private String ghiChu;
}

