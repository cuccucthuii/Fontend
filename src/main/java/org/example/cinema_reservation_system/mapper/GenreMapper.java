package org.example.cinema_reservation_system.mapper;

import org.example.cinema_reservation_system.dto.genredto.GenreRequestDto;
import org.example.cinema_reservation_system.dto.genredto.GenreResponseDto;
import org.example.cinema_reservation_system.entity.Genre;
import org.example.cinema_reservation_system.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreMapper {

    public Genre toEntity(GenreRequestDto dto) {
        Genre genre = new Genre();
        genre.setTenTheLoai(dto.getTenTheLoai());
        return genre;
    }

    public GenreResponseDto toDto(Genre genre) {
        GenreResponseDto dto = new GenreResponseDto();
        dto.setIdTheLoai(genre.getIdTheLoai());
        dto.setTenTheLoai(genre.getTenTheLoai());
        
        // Lấy danh sách tên phim
        if (genre.getPhimList() != null) {
            List<String> tenPhimList = genre.getPhimList().stream()
                    .map(Movie::getTenPhim)
                    .collect(Collectors.toList());
            dto.setTenPhimList(tenPhimList);
        }
        
        return dto;
    }

    public List<GenreResponseDto> toDtoList(List<Genre> genres) {
        return genres.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 