package org.example.cinema_reservation_system.dto.combo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.entity.FoodCombo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboResponseDto {
    
    private Integer idCombo;
    private String tenCombo;
    private String moTa;
    private BigDecimal giaCombo;
    private FoodCombo.TrangThaiCombo trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    
    // Constructor để convert từ Entity
    public ComboResponseDto(FoodCombo combo) {
        this.idCombo = combo.getIdCombo();
        this.tenCombo = combo.getTenCombo();
        this.moTa = combo.getMoTa();
        this.giaCombo = combo.getGiaCombo();
        this.trangThai = combo.getTrangThai();
        this.ngayTao = combo.getNgayTao();
        this.ngayCapNhat = combo.getNgayCapNhat();
    }
}

