package org.example.cinema_reservation_system.service.validation;

import org.example.cinema_reservation_system.dto.validation.MovieBookingValidationDto;
import org.example.cinema_reservation_system.entity.Movie;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.repository.movie.MovieRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.validation.MovieBookingValidator;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service xử lý validation booking phim
 * Cung cấp các method để kiểm tra khả năng đặt vé
 */
@Service
public class MovieBookingValidationService {

    @Autowired
    private MovieBookingValidator movieBookingValidator;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ShowTimeRepository showTimeRepository;

    /**
     * Validate booking cho phim và suất chiếu
     * @param movieId ID phim
     * @param showtimeId ID suất chiếu
     * @return MovieBookingValidationDto chứa thông tin validation
     */
    public MovieBookingValidationDto validateBooking(Integer movieId, Integer showtimeId) {
        // Lấy thông tin phim và suất chiếu
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
        
        ShowTime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu với ID: " + showtimeId));
        
        return validateBooking(movie, showtime);
    }

    /**
     * Validate booking cho phim và suất chiếu
     * @param movie Phim
     * @param showtime Suất chiếu
     * @return MovieBookingValidationDto chứa thông tin validation
     */
    public MovieBookingValidationDto validateBooking(Movie movie, ShowTime showtime) {
        // Kiểm tra có thể đặt vé không
        boolean canBook = movieBookingValidator.canBookMovie(movie, showtime.getNgayChieu());
        
        if (canBook) {
            return MovieBookingValidationDto.canBook(
                movie.getTrangThai(),
                movie.getTenPhim(),
                movie.getNgayPhatHanh(),
                showtime.getNgayChieu()
            );
        } else {
            // Tạo thông báo lỗi
            String errorMessage = movieBookingValidator.getSapChieuErrorMessage(movie, showtime.getNgayChieu());
            long daysUntilRelease = movieBookingValidator.getDaysUntilRelease(movie);
            
            return MovieBookingValidationDto.cannotBook(
                movie.getTrangThai(),
                movie.getTenPhim(),
                movie.getNgayPhatHanh(),
                showtime.getNgayChieu(),
                errorMessage,
                daysUntilRelease
            );
        }
    }

    /**
     * Kiểm tra phim có phải SẮP_CHIẾU không
     * @param movieId ID phim
     * @return true nếu là phim SẮP_CHIẾU
     */
    public boolean isSapChieuMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
        
        return movieBookingValidator.isSapChieuMovie(movie);
    }

    /**
     * Kiểm tra phim có phải ĐANG_CHIẾU không
     * @param movieId ID phim
     * @return true nếu là phim ĐANG_CHIẾU
     */
    public boolean isDangChieuMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
        
        return movieBookingValidator.isDangChieuMovie(movie);
    }

    /**
     * Lấy số ngày còn lại đến ngày phát hành
     * @param movieId ID phim
     * @return Số ngày còn lại
     */
    public long getDaysUntilRelease(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
        
        return movieBookingValidator.getDaysUntilRelease(movie);
    }

    /**
     * Lấy thông tin validation cho phim
     * @param movieId ID phim
     * @return MovieBookingValidationDto chứa thông tin phim
     */
    public MovieBookingValidationDto getMovieValidationInfo(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
        
        // Tạo validation info cho phim (không có suất chiếu cụ thể)
        MovieBookingValidationDto dto = new MovieBookingValidationDto();
        dto.setMovieStatus(movie.getTrangThai());
        dto.setMovieName(movie.getTenPhim());
        dto.setReleaseDate(movie.getNgayPhatHanh());
        dto.setDaysUntilRelease(movieBookingValidator.getDaysUntilRelease(movie));
        dto.setUpcomingMovie(movieBookingValidator.isSapChieuMovie(movie));
        dto.setCurrentlyShowing(movieBookingValidator.isDangChieuMovie(movie));
        
        // Xác định có thể đặt vé không (dựa trên ngày hiện tại)
        LocalDate today = LocalDate.now();
        dto.setCanBook(movieBookingValidator.canBookMovie(movie, today));
        
        if (!dto.isCanBook() && dto.isUpcomingMovie()) {
            dto.setErrorMessage(movieBookingValidator.getSapChieuErrorMessage(movie, today));
            dto.setInfoMessage(String.format("Phim sẽ phát hành sau %d ngày", dto.getDaysUntilRelease()));
        }
        
        return dto;
    }
}
