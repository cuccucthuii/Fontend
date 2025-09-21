package org.example.cinema_reservation_system.dto.food;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.entity.FoodItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemResponseDto {
    
    private Integer idMon;
    private String tenMon;
    private String moTa;
    private BigDecimal giaMon;
    private FoodItem.LoaiMon loaiMon;
    private String hinhAnh;
    private FoodItem.TrangThaiMon trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    
    // Constructor để convert từ Entity
    public FoodItemResponseDto(FoodItem monAn) {
        this.idMon = monAn.getIdMon();
        this.tenMon = monAn.getTenMon();
        this.moTa = monAn.getMoTa();
        this.giaMon = monAn.getGiaMon();
        this.loaiMon = monAn.getLoaiMon();
        this.hinhAnh = monAn.getHinhAnh();
        this.trangThai = monAn.getTrangThai();
        this.ngayTao = monAn.getNgayTao();
        this.ngayCapNhat = monAn.getNgayCapNhat();
    }
}
