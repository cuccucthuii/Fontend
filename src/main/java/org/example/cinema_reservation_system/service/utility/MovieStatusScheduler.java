package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.entity.Movie;
import org.example.cinema_reservation_system.repository.movie.MovieRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieStatusScheduler {

    private final MovieRepository movieRepository;
    private final ShowTimeRepository showTimeRepository;

    // Chạy lúc 00:05 hàng ngày
    @Scheduled(cron = "0 5 0 * * *")
    public void updateMovieStatusesDaily() {
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieRepository.findAll();
        int updated = 0;
        for (Movie m : movies) {
            TrangThai desired = m.getTrangThai();
            try {
                if (m.getNgayPhatHanh() != null && m.getNgayPhatHanh().isAfter(today)) {
                    desired = TrangThai.SAP_CHIEU;
                } else {
                    // Có suất chiếu từ hôm nay trở đi → đang chiếu
                    boolean hasFutureShowtimes = !showTimeRepository
                            .findByPhimAndNgayChieuBetween(m.getIdPhim(), today, today.plusYears(1))
                            .isEmpty();
                    desired = hasFutureShowtimes ? TrangThai.DANG_CHIEU : TrangThai.NGUNG_CHIEU;
                }
                if (desired != m.getTrangThai()) {
                    m.setTrangThai(desired);
                    movieRepository.save(m);
                    updated++;
                }
            } catch (Exception ex) {
                log.warn("Không thể cập nhật trạng thái phim {}: {}", m.getIdPhim(), ex.getMessage());
            }
        }
        if (updated > 0) {
            log.info("Đã cập nhật trạng thái cho {} phim vào {}", updated, today);
        }
    }
}

































