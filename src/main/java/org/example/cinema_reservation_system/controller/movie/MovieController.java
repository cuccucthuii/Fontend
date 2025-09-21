package org.example.cinema_reservation_system.controller.movie;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.moviedto.MovieRequestDto;
import org.example.cinema_reservation_system.dto.moviedto.MovieResponseDto;
import org.example.cinema_reservation_system.entity.MovieReview;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.service.movie.MovieService;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phim")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {

    private final MovieService phimService;
    private final ObjectMapper objectMapper;


    @GetMapping
    public ResponseEntity<List<MovieResponseDto>> getAll() {
        return ResponseEntity.ok(phimService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(phimService.getById(id));
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDto> create(@RequestPart("phim") @Valid String phimJson,
                                                   @RequestPart(value = "poster", required = false) MultipartFile poster,
                                                   @RequestPart(value = "banner", required = false) MultipartFile banner) {
        try {
            MovieRequestDto dto = objectMapper.readValue(phimJson, MovieRequestDto.class);
            MovieResponseDto result = phimService.create(dto, poster, banner);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/add-from-url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieResponseDto> createFromUrl(@RequestBody @Valid MovieRequestDto dto) {
        try {
            MovieResponseDto result = phimService.create(dto, null, null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDto> update(@PathVariable Integer id,
                                                   @RequestPart("phim") @Valid String phimJson,
                                                   @RequestPart(value = "poster", required = false) MultipartFile poster,
                                                   @RequestPart(value = "banner", required = false) MultipartFile banner) {
        try {
            MovieRequestDto dto = objectMapper.readValue(phimJson, MovieRequestDto.class);
            return ResponseEntity.ok(phimService.update(id, dto, poster, banner));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(phimService.delete(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MovieResponseDto>> filter(@RequestParam(required = false) String theLoai,
                                                         @RequestParam(required = false) TrangThai trangThai) {
        return ResponseEntity.ok(phimService.filter(theLoai, trangThai));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDto>> searchMovies(@RequestParam String keyword) {
        return ResponseEntity.ok(phimService.searchMovies(keyword));
    }

    // New: search suggest with priority for DANG_CHIEU
    @GetMapping("/search-suggest")
    public ResponseEntity<List<MovieResponseDto>> searchSuggest(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(phimService.searchSuggest(keyword, limit));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<MovieResponseDto>> getTrendingMovies() {
        return ResponseEntity.ok(phimService.getTrendingMovies());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MovieResponseDto>> getUpcomingMovies() {
        return ResponseEntity.ok(phimService.getUpcomingMovies());
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<MovieResponseDto>> getSimilarMovies(@PathVariable Integer id) {
        return ResponseEntity.ok(phimService.getSimilarMovies(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<MovieReview>> getMovieReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(phimService.getMovieReviews(id));
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<List<ShowTime>> getMovieShowtimes(@PathVariable Integer id) {
        return ResponseEntity.ok(phimService.getMovieShowtimes(id));
    }

    //thêm mới
    // ========== DELETE OPERATIONS ==========

    // Xóa mềm phim (soft delete)
    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Map<String, Object>> softDelete(@PathVariable Integer id) {
        try {
            Map<String, Object> result = phimService.softDelete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi xóa mềm phim: " + e.getMessage()
            ));
        }
    }

    // Khôi phục phim đã xóa
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restore(@PathVariable Integer id) {
        try {
            Map<String, Object> result = phimService.restore(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi khôi phục phim: " + e.getMessage()
            ));
        }
    }

    // Xóa cứng phim (hard delete) - xóa vĩnh viễn
    @DeleteMapping("/{id}/hard-delete")
    public ResponseEntity<Map<String, Object>> hardDelete(@PathVariable Integer id) {
        try {
            Map<String, Object> result = phimService.hardDelete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi xóa vĩnh viễn phim: " + e.getMessage()
            ));
        }
    }



}
