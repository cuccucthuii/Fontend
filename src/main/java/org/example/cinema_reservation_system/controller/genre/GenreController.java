package org.example.cinema_reservation_system.controller.genre;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.genredto.GenreRequestDto;
import org.example.cinema_reservation_system.dto.genredto.GenreResponseDto;
import org.example.cinema_reservation_system.service.movie.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/the-loai")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAll() {
        return ResponseEntity.ok(genreService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(genreService.getById(id));
    }

    @PostMapping
    public ResponseEntity<GenreResponseDto> create(@Valid @RequestBody GenreRequestDto dto) {
        return ResponseEntity.ok(genreService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDto> update(@PathVariable Integer id, 
                                                   @Valid @RequestBody GenreRequestDto dto) {
        return ResponseEntity.ok(genreService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(genreService.delete(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GenreResponseDto>> searchByName(@RequestParam String tenTheLoai) {
        return ResponseEntity.ok(genreService.searchByName(tenTheLoai));
    }
} 