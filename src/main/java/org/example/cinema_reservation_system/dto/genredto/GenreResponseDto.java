package org.example.cinema_reservation_system.dto.genredto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponseDto {
    private Integer idTheLoai;
    private String tenTheLoai;
    private List<String> tenPhimList; // Danh sách tên phim thuộc thể loại này
} 