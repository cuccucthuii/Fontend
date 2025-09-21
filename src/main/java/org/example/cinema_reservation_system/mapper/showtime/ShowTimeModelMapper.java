package org.example.cinema_reservation_system.mapper.showtime;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.dto.showtimedto.*;

@Component
@RequiredArgsConstructor
public class ShowTimeModelMapper {
    private final ModelMapper mapper;

    public ShowTime toEntity(ShowTimeRequestDTO dto) {
        return mapper.map(dto, ShowTime.class);
    }

    public ShowTimeResponseDTO toResponse(ShowTime e) {
        ShowTimeResponseDTO r = mapper.map(e, ShowTimeResponseDTO.class);
        
        // Thông tin phim
        r.setTenPhim(e.getPhim().getTenPhim());
        r.setThoiLuongPhim(e.getPhim().getThoiLuong());
        
        // Thông tin phòng chiếu
        r.setTenPhongChieu(e.getPhongChieu().getTenPhongChieu());
        
        // Thông tin ghế từ phòng chiếu (sẽ được tính từ bảng ghe_ngoi)
        // r.setSoGhe(e.getPhongChieu().getSoGhe()); // Field không tồn tại trong Room entity
        
        // Thông tin ghế từ suất chiếu
        r.setSoGheConTrong(e.getSoGheConTrong());
        r.setTongSoGhe(e.getTongSoGhe());
        
        // Thông tin thời gian
        r.setNgayTao(e.getNgayTao());
        r.setNgayCapNhat(e.getNgayCapNhat());
        
        // Ghi chú
        r.setGhiChu(e.getGhiChu());
        
        return r;
    }

    public ShowTimeSummaryDTO toSummary(ShowTime e) {
        ShowTimeSummaryDTO s = mapper.map(e, ShowTimeSummaryDTO.class);
        s.setTenPhim(e.getPhim().getTenPhim());
        s.setTenPhongChieu(e.getPhongChieu().getTenPhongChieu());
        s.setTenRapChieu(e.getPhongChieu().getRapChieu().getTenRapChieu());
        s.setSoGheConTrong(e.getSoGheConTrong());
        s.setTongSoGhe(e.getTongSoGhe());
        s.setThoiGianBatDau(e.getThoiGianBatDau());
        s.setThoiGianKetThuc(e.getThoiGianKetThuc());
        return s;
    }
}