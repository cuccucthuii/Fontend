package org.example.cinema_reservation_system.service.movie;

import org.example.cinema_reservation_system.dto.moviedto.MovieRequestDto;
import org.example.cinema_reservation_system.dto.moviedto.MovieResponseDto;
import org.example.cinema_reservation_system.entity.MovieReview;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface MovieService {
    List<MovieResponseDto> getAll();
    MovieResponseDto getById(Integer id);
    MovieResponseDto create(MovieRequestDto dto, MultipartFile poster, MultipartFile banner);
    MovieResponseDto update(Integer id, MovieRequestDto dto, MultipartFile poster, MultipartFile banner);
    Map<String, Object> delete(Integer id);
    List<MovieResponseDto> filter(String theLoai, TrangThai trangThai);

    // New methods for enhanced movie management
    List<MovieResponseDto> searchMovies(String keyword);
    List<MovieResponseDto> getTrendingMovies();
    List<MovieResponseDto> getUpcomingMovies();
    List<MovieResponseDto> getSimilarMovies(Integer movieId);
    List<MovieReview> getMovieReviews(Integer movieId);
    List<ShowTime> getMovieShowtimes(Integer movieId);

    // New: search suggest with priority for DANG_CHIEU and limit
    List<MovieResponseDto> searchSuggest(String keyword, int limit);

    // ====================== SOFT DELETE METHODS ==========================

    // Lấy tất cả phim đã xóa
//    List<MovieResponseDto> getAllDeleted();

    // Xóa mềm phim
    Map<String, Object> softDelete(Integer id);

    // Khôi phục phim đã xóa
    Map<String, Object> restore(Integer id);

    // Xóa cứng phim (xóa vĩnh viễn)
    Map<String, Object> hardDelete(Integer id);

    // Lấy thống kê phim
    Map<String, Object> getStatistics();
}
