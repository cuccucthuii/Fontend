package org.example.cinema_reservation_system.service.movie;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.genredto.GenreRequestDto;
import org.example.cinema_reservation_system.dto.genredto.GenreResponseDto;
import org.example.cinema_reservation_system.entity.Genre;
import org.example.cinema_reservation_system.exception.ResourceAlreadyExistsException;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.mapper.GenreMapper;
import org.example.cinema_reservation_system.repository.genre.GenreRepository;
import org.example.cinema_reservation_system.service.movie.GenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<GenreResponseDto> getAll() {
        List<Genre> genres = genreRepository.findAll();
        return genreMapper.toDtoList(genres);
    }

    @Override
    public GenreResponseDto getById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
        return genreMapper.toDto(genre);
    }

    @Override
    public GenreResponseDto create(GenreRequestDto dto) {
        // Kiểm tra thể loại đã tồn tại chưa
        if (genreRepository.findByTenTheLoaiIgnoreCase(dto.getTenTheLoai()).isPresent()) {
            throw new ResourceAlreadyExistsException("Thể loại '" + dto.getTenTheLoai() + "' đã tồn tại");
        }

        Genre genre = genreMapper.toEntity(dto);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDto(savedGenre);
    }

    @Override
    public GenreResponseDto update(Integer id, GenreRequestDto dto) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));

        // Kiểm tra tên thể loại mới có trùng với thể loại khác không
        genreRepository.findByTenTheLoaiIgnoreCase(dto.getTenTheLoai())
                .ifPresent(genre -> {
                    if (!genre.getIdTheLoai().equals(id)) {
                        throw new ResourceAlreadyExistsException("Thể loại '" + dto.getTenTheLoai() + "' đã tồn tại");
                    }
                });

        existingGenre.setTenTheLoai(dto.getTenTheLoai());
        Genre updatedGenre = genreRepository.save(existingGenre);
        return genreMapper.toDto(updatedGenre);
    }

    @Override
    public Map<String, Object> delete(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));

        // Kiểm tra xem thể loại có đang được sử dụng bởi phim nào không
        if (genre.getPhimList() != null && !genre.getPhimList().isEmpty()) {
            throw new RuntimeException("Không thể xóa thể loại '" + genre.getTenTheLoai() + "' vì đang được sử dụng bởi " + 
                    genre.getPhimList().size() + " phim");
        }

        genreRepository.delete(genre);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Xóa thể loại thành công");
        result.put("idTheLoai", genre.getIdTheLoai());
        result.put("tenTheLoai", genre.getTenTheLoai());
        return result;
    }

    @Override
    public List<GenreResponseDto> searchByName(String tenTheLoai) {
        List<Genre> genres = genreRepository.findAll().stream()
                .filter(genre -> genre.getTenTheLoai().toLowerCase().contains(tenTheLoai.toLowerCase()))
                .toList();
        return genreMapper.toDtoList(genres);
    }
} 