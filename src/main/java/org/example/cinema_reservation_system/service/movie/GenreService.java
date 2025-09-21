package org.example.cinema_reservation_system.service.movie;

import org.example.cinema_reservation_system.dto.genredto.GenreRequestDto;
import org.example.cinema_reservation_system.dto.genredto.GenreResponseDto;

import java.util.List;
import java.util.Map;

public interface GenreService {
    List<GenreResponseDto> getAll();
    GenreResponseDto getById(Integer id);
    GenreResponseDto create(GenreRequestDto dto);
    GenreResponseDto update(Integer id, GenreRequestDto dto);
    Map<String, Object> delete(Integer id);
    List<GenreResponseDto> searchByName(String tenTheLoai);
} 