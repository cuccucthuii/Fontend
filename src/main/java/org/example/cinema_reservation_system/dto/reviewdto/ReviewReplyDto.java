package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyDto {
    private Integer idPhanHoi;
    private Integer idNguoiPhanHoi;
    private String tenNguoiPhanHoi;
    private String anhDaiDienNguoiPhanHoi;
    private String vaiTroNguoiPhanHoi; // ADMIN, CUSTOMER, STAFF
    private String noiDungPhanHoi;
    private LocalDateTime ngayPhanHoi;
    private String loaiPhanHoi;
    private String trangThai;
    private Integer soLuotThich;
    private Boolean daThich; // Người dùng hiện tại đã thích chưa
}
































































