package org.example.cinema_reservation_system.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.time.LocalDate;

/**
 * DTO cho validation booking phim
 * Chứa thông tin về khả năng đặt vé và thông báo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieBookingValidationDto {
    
    /**
     * Có thể đặt vé hay không
     */
    private boolean canBook;
    
    /**
     * Trạng thái phim
     */
    private TrangThai movieStatus;
    
    /**
     * Tên phim
     */
    private String movieName;
    
    /**
     * Ngày phát hành
     */
    private LocalDate releaseDate;
    
    /**
     * Ngày chiếu
     */
    private LocalDate showDate;
    
    /**
     * Số ngày còn lại đến ngày phát hành
     */
    private long daysUntilRelease;
    
    /**
     * Thông báo lỗi (nếu có)
     */
    private String errorMessage;
    
    /**
     * Thông báo thông tin (nếu có)
     */
    private String infoMessage;
    
    /**
     * Có phải phim SẮP_CHIẾU không
     */
    private boolean isUpcomingMovie;
    
    /**
     * Có phải phim ĐANG_CHIẾU không
     */
    private boolean isCurrentlyShowing;
    
    /**
     * Constructor cho phim có thể đặt vé
     */
    public static MovieBookingValidationDto canBook(TrangThai movieStatus, String movieName, 
                                                   LocalDate releaseDate, LocalDate showDate) {
        MovieBookingValidationDto dto = new MovieBookingValidationDto();
        dto.setCanBook(true);
        dto.setMovieStatus(movieStatus);
        dto.setMovieName(movieName);
        dto.setReleaseDate(releaseDate);
        dto.setShowDate(showDate);
        dto.setDaysUntilRelease(0);
        dto.setUpcomingMovie(movieStatus == TrangThai.SAP_CHIEU);
        dto.setCurrentlyShowing(movieStatus == TrangThai.DANG_CHIEU);
        return dto;
    }
    
    /**
     * Constructor cho phim không thể đặt vé
     */
    public static MovieBookingValidationDto cannotBook(TrangThai movieStatus, String movieName, 
                                                      LocalDate releaseDate, LocalDate showDate, 
                                                      String errorMessage, long daysUntilRelease) {
        MovieBookingValidationDto dto = new MovieBookingValidationDto();
        dto.setCanBook(false);
        dto.setMovieStatus(movieStatus);
        dto.setMovieName(movieName);
        dto.setReleaseDate(releaseDate);
        dto.setShowDate(showDate);
        dto.setErrorMessage(errorMessage);
        dto.setDaysUntilRelease(daysUntilRelease);
        dto.setUpcomingMovie(movieStatus == TrangThai.SAP_CHIEU);
        dto.setCurrentlyShowing(movieStatus == TrangThai.DANG_CHIEU);
        
        if (movieStatus == TrangThai.SAP_CHIEU && daysUntilRelease > 0) {
            dto.setInfoMessage(String.format("Phim sẽ phát hành sau %d ngày", daysUntilRelease));
        }
        
        return dto;
    }
    
    /**
     * Kiểm tra có phải phim SẮP_CHIẾU chưa phát hành không
     */
    public boolean isUpcomingMovieNotReleased() {
        return isUpcomingMovie && daysUntilRelease > 0;
    }
    
    /**
     * Kiểm tra có phải phim SẮP_CHIẾU đã phát hành không
     */
    public boolean isUpcomingMovieReleased() {
        return isUpcomingMovie && daysUntilRelease == 0;
    }
}
