package org.example.cinema_reservation_system.validation;

import org.example.cinema_reservation_system.entity.Movie;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.exception.BadRequestException;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Validator cho việc đặt vé phim
 * Xử lý các business rules liên quan đến booking
 */
@Component
public class MovieBookingValidator {

    /**
     * Validate booking cho phim SẮP_CHIẾU
     * @param phim Phim cần validate
     * @param suatChieu Suất chiếu cần validate
     * @throws BadRequestException nếu không thể đặt vé
     */
    public void validateSapChieuBooking(Movie phim, ShowTime suatChieu) {
        // Kiểm tra phim có phải SẮP_CHIẾU không
        if (phim.getTrangThai() == TrangThai.SAP_CHIEU) {
            LocalDate ngayPhatHanh = phim.getNgayPhatHanh();
            LocalDate ngayHienTai = LocalDate.now();
            
            if (ngayPhatHanh.isAfter(ngayHienTai)) {
                throw new BadRequestException(
                    "Không thể đặt vé cho phim chưa phát hành. " +
                    "Phim '" + phim.getTenPhim() + "' sẽ phát hành vào ngày " + ngayPhatHanh + ". " +
                    "Vui lòng quay lại sau ngày phát hành để đặt vé."
                );
            }
        }
        
        // Kiểm tra suất chiếu có phải của phim SẮP_CHIẾU không
        if (suatChieu.getPhim().getTrangThai() == TrangThai.SAP_CHIEU) {
            LocalDate ngayPhatHanh = suatChieu.getPhim().getNgayPhatHanh();
            LocalDate ngayChieu = suatChieu.getNgayChieu();
            
            if (ngayPhatHanh.isAfter(ngayChieu)) {
                throw new BadRequestException(
                    "Không thể đặt vé cho suất chiếu trước ngày phát hành. " +
                    "Suất chiếu ngày " + ngayChieu + " của phim '" + suatChieu.getPhim().getTenPhim() + 
                    "' chưa được phép đặt vé vì phim chưa phát hành (ngày phát hành: " + ngayPhatHanh + ")."
                );
            }
        }
    }

    /**
     * Kiểm tra xem có thể đặt vé cho phim không
     * @param phim Phim cần kiểm tra
     * @param ngayChieu Ngày chiếu
     * @return true nếu có thể đặt vé, false nếu không
     */
    public boolean canBookMovie(Movie phim, LocalDate ngayChieu) {
        // Phim ĐANG_CHIẾU luôn có thể đặt vé
        if (phim.getTrangThai() == TrangThai.DANG_CHIEU) {
            return true;
        }
        
        // Phim SẮP_CHIẾU chỉ có thể đặt vé từ ngày phát hành
        if (phim.getTrangThai() == TrangThai.SAP_CHIEU) {
            LocalDate ngayPhatHanh = phim.getNgayPhatHanh();
            return !ngayPhatHanh.isAfter(ngayChieu);
        }
        
        // Phim NGỪNG_CHIẾU và HOÀN_THÀNH không thể đặt vé
        return false;
    }

    /**
     * Lấy thông báo lỗi chi tiết cho phim SẮP_CHIẾU
     * @param phim Phim SẮP_CHIẾU
     * @param ngayChieu Ngày chiếu
     * @return Thông báo lỗi
     */
    public String getSapChieuErrorMessage(Movie phim, LocalDate ngayChieu) {
        LocalDate ngayPhatHanh = phim.getNgayPhatHanh();
        
        if (ngayPhatHanh.isAfter(ngayChieu)) {
            return String.format(
                "Không thể đặt vé cho phim chưa phát hành. " +
                "Phim '%s' sẽ phát hành vào ngày %s. " +
                "Vui lòng quay lại sau ngày phát hành để đặt vé.",
                phim.getTenPhim(),
                ngayPhatHanh
            );
        }
        
        return "Không thể đặt vé cho phim này.";
    }

    /**
     * Kiểm tra phim có phải SẮP_CHIẾU không
     * @param phim Phim cần kiểm tra
     * @return true nếu là phim SẮP_CHIẾU
     */
    public boolean isSapChieuMovie(Movie phim) {
        return phim.getTrangThai() == TrangThai.SAP_CHIEU;
    }

    /**
     * Kiểm tra phim có phải ĐANG_CHIẾU không
     * @param phim Phim cần kiểm tra
     * @return true nếu là phim ĐANG_CHIẾU
     */
    public boolean isDangChieuMovie(Movie phim) {
        return phim.getTrangThai() == TrangThai.DANG_CHIEU;
    }

    /**
     * Lấy số ngày còn lại đến ngày phát hành
     * @param phim Phim SẮP_CHIẾU
     * @return Số ngày còn lại (0 nếu đã phát hành)
     */
    public long getDaysUntilRelease(Movie phim) {
        if (!isSapChieuMovie(phim)) {
            return 0;
        }
        
        LocalDate ngayPhatHanh = phim.getNgayPhatHanh();
        LocalDate ngayHienTai = LocalDate.now();
        
        if (ngayPhatHanh.isAfter(ngayHienTai)) {
            return java.time.temporal.ChronoUnit.DAYS.between(ngayHienTai, ngayPhatHanh);
        }
        
        return 0;
    }
}
