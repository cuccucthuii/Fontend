package org.example.cinema_reservation_system.controller.validation;

import org.example.cinema_reservation_system.dto.validation.MovieBookingValidationDto;
import org.example.cinema_reservation_system.service.validation.MovieBookingValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý validation booking phim
 * Cung cấp API để kiểm tra khả năng đặt vé
 */
@RestController
@RequestMapping("/api/validation")
@CrossOrigin(origins = "http://localhost:5173")
public class MovieBookingValidationController {

    @Autowired
    private MovieBookingValidationService validationService;

    /**
     * Validate booking cho phim và suất chiếu
     * @param movieId ID phim
     * @param showtimeId ID suất chiếu
     * @return MovieBookingValidationDto chứa thông tin validation
     */
    @GetMapping("/booking/{movieId}/{showtimeId}")
    public ResponseEntity<MovieBookingValidationDto> validateBooking(
            @PathVariable Integer movieId,
            @PathVariable Integer showtimeId) {
        try {
            MovieBookingValidationDto validation = validationService.validateBooking(movieId, showtimeId);
            return ResponseEntity.ok(validation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra phim có phải SẮP_CHIẾU không
     * @param movieId ID phim
     * @return true nếu là phim SẮP_CHIẾU
     */
    @GetMapping("/movie/{movieId}/is-upcoming")
    public ResponseEntity<Boolean> isSapChieuMovie(@PathVariable Integer movieId) {
        try {
            boolean isUpcoming = validationService.isSapChieuMovie(movieId);
            return ResponseEntity.ok(isUpcoming);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra phim có phải ĐANG_CHIẾU không
     * @param movieId ID phim
     * @return true nếu là phim ĐANG_CHIẾU
     */
    @GetMapping("/movie/{movieId}/is-currently-showing")
    public ResponseEntity<Boolean> isDangChieuMovie(@PathVariable Integer movieId) {
        try {
            boolean isCurrentlyShowing = validationService.isDangChieuMovie(movieId);
            return ResponseEntity.ok(isCurrentlyShowing);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy số ngày còn lại đến ngày phát hành
     * @param movieId ID phim
     * @return Số ngày còn lại
     */
    @GetMapping("/movie/{movieId}/days-until-release")
    public ResponseEntity<Long> getDaysUntilRelease(@PathVariable Integer movieId) {
        try {
            long days = validationService.getDaysUntilRelease(movieId);
            return ResponseEntity.ok(days);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin validation cho phim
     * @param movieId ID phim
     * @return MovieBookingValidationDto chứa thông tin phim
     */
    @GetMapping("/movie/{movieId}/info")
    public ResponseEntity<MovieBookingValidationDto> getMovieValidationInfo(@PathVariable Integer movieId) {
        try {
            MovieBookingValidationDto info = validationService.getMovieValidationInfo(movieId);
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Validate booking với query parameters
     * @param movieId ID phim
     * @param showtimeId ID suất chiếu
     * @return MovieBookingValidationDto chứa thông tin validation
     */
    @GetMapping("/booking")
    public ResponseEntity<MovieBookingValidationDto> validateBookingWithParams(
            @RequestParam Integer movieId,
            @RequestParam Integer showtimeId) {
        try {
            MovieBookingValidationDto validation = validationService.validateBooking(movieId, showtimeId);
            return ResponseEntity.ok(validation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
