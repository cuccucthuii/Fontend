package org.example.cinema_reservation_system.controller.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.springframework.data.domain.PageRequest;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final InvoiceRepository invoiceRepo;
    private final ShowTimeRepository showTimeRepo;

    @GetMapping("/stats")
    public Map<String, Object> stats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDateTime start = from != null ? from.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = to != null ? to.atTime(23,59,59) : LocalDate.now().atTime(23,59,59);

        BigDecimal revenue = java.math.BigDecimal.valueOf(
                java.util.Optional.ofNullable(invoiceRepo.getTongDoanhThu()).orElse(0.0)
        );

        long showtimesScheduled = showTimeRepo.countByTrangThai(
                org.example.cinema_reservation_system.utils.enums.TrangThai.SAP_CHIEU
        );
        long showtimesCompleted = showTimeRepo.countByTrangThai(
                org.example.cinema_reservation_system.utils.enums.TrangThai.KET_THUC
        );
        long showtimesCancelled = showTimeRepo.countByTrangThai(
                org.example.cinema_reservation_system.utils.enums.TrangThai.DA_HUY
        );

        return Map.of(
                "revenue", revenue,
                "showtimesScheduled", showtimesScheduled,
                "showtimesCompleted", showtimesCompleted,
                "showtimesCancelled", showtimesCancelled
        );
    }

    @GetMapping("/top")
    public Map<String, Object> top(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDateTime start = from != null ? from.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = to != null ? to.atTime(23,59,59) : LocalDate.now().atTime(23,59,59);

        // Top movies by revenue
        var topMovies = invoiceRepo.getDoanhThuTheoPhim().stream()
                .map(arr -> java.util.Map.entry((String) arr[0], (java.math.BigDecimal) arr[1]))
                .limit(limit)
                .toList();

        // Top theaters by revenue
        var topTheaters = invoiceRepo.getDoanhThuTheoRap().stream()
                .map(arr -> java.util.Map.entry((String) arr[0], (java.math.BigDecimal) arr[1]))
                .limit(limit)
                .toList();

        return Map.of(
                "topMovies", topMovies,
                "topTheaters", topTheaters
        );
    }

    @GetMapping("/occupancy")
    public Map<String, Object> occupancy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDate startDate = from != null ? from : LocalDate.now().minusDays(7);
        LocalDate endDate = to != null ? to : LocalDate.now();

        var showtimes = showTimeRepo.findByNgayChieuBetween(startDate, endDate);
        var byDate = showtimes.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> s.getNgayChieu(),
                        java.util.stream.Collectors.toList()
                ));

        var series = byDate.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(e -> {
                    int tongSuat = e.getValue().size();
                    int tongGhe = e.getValue().stream().mapToInt(s -> s.getTongSoGhe() != null ? s.getTongSoGhe() : 0).sum();
                    int gheConTrong = e.getValue().stream().mapToInt(s -> s.getSoGheConTrong() != null ? s.getSoGheConTrong() : 0).sum();
                    int gheDaDat = Math.max(0, tongGhe - gheConTrong);
                    double occupancy = tongGhe > 0 ? (double) gheDaDat / (double) tongGhe : 0.0;
                    return Map.of(
                            "date", e.getKey().toString(),
                            "showtimes", tongSuat,
                            "seats", tongGhe,
                            "booked", gheDaDat,
                            "occupancy", occupancy
                    );
                })
                .toList();

        return Map.of("series", series);
    }
}


