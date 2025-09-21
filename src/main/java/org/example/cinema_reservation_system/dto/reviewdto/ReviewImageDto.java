package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageDto {
    private Integer idHinhAnhDanhGia;
    private String tenHinhAnh;
    private String urlHinhAnh;
    private String loaiHinhAnh;
    private String moTa;
    private LocalDateTime ngayTao;
}
































































