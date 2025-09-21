package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.statistics.StatisticsByMovie;
import org.example.cinema_reservation_system.dto.statistics.StatisticsByTheater;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.service.utility.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.example.cinema_reservation_system.dto.statistics.RevenueByDateDTO;
import org.example.cinema_reservation_system.dto.statistics.RevenueByWeekDTO;
import org.example.cinema_reservation_system.dto.statistics.RevenueByMonthDTO;
import org.example.cinema_reservation_system.dto.statistics.RevenueByQuarterDTO;
import org.example.cinema_reservation_system.dto.statistics.RevenueByYearDTO;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final InvoiceRepository hoaDonRepo;

    public StatisticsServiceImpl(InvoiceRepository hoaDonRepo) {
        this.hoaDonRepo = hoaDonRepo;
    }

    @Override
    public Double getTongDoanhThu() {
        return hoaDonRepo.getTongDoanhThu() != null ? hoaDonRepo.getTongDoanhThu() : 0.0;
    }

    @Override
    public BigDecimal tongdoanhThuThangNam(int month, int year) {
        return hoaDonRepo.tongDoanhThuTheoThangNam(year, month);
    }

    @Override
    public List<StatisticsByTheater> getDoanhThuTheoRap() {
        List<Object[]> results = hoaDonRepo.getDoanhThuTheoRap();

        // Debug log
        for (Object[] row : results) {
            System.out.println(Arrays.toString(row));
        }

        // Map về DTO để trả ra JSON đẹp
        return results.stream()
                .map(r -> new StatisticsByTheater(
                        (String) r[0],
                        (BigDecimal) r[1],
                        ((Long) r[2]).longValue() // soVeBan
                ))
                .toList();
    }

    @Override
    public List<StatisticsByMovie> getDoanhThuTheoPhim() {
        List<Object[]> results = hoaDonRepo.getDoanhThuTheoPhim();

        for (Object[] row : results) {
            System.out.println(Arrays.toString(row));
        }

        return results.stream()
                .map(r -> new StatisticsByMovie(
                        (String) r[0],                // tenPhim
                        (BigDecimal) r[1],            // tongDoanhThu
                        ((Number) r[2]).longValue()   // soVeBan
                ))
                .toList();
    }

    @Override
    public List<RevenueByDateDTO> doanhThuTheoNgay(java.time.LocalDate from, java.time.LocalDate to) {
        return hoaDonRepo.doanhThuTheoNgay(java.sql.Date.valueOf(from), java.sql.Date.valueOf(to)).stream()
            .map(r -> new RevenueByDateDTO(
                ((java.sql.Date) r[0]).toLocalDate(),
                (java.math.BigDecimal) r[1]
            )).toList();
    }

    @Override
    public List<RevenueByWeekDTO> doanhThuTheoTuan(int year) {
        return hoaDonRepo.doanhThuTheoTuan(year).stream()
            .map(r -> new RevenueByWeekDTO(
                ((Number) r[0]).intValue(), // year
                ((Number) r[1]).intValue(), // week
                (java.math.BigDecimal) r[2]
            )).toList();
    }

    @Override
    public List<RevenueByMonthDTO> doanhThuTheoThang(int year) {
        return hoaDonRepo.doanhThuTheoThang(year).stream()
            .map(r -> new RevenueByMonthDTO(
                ((Number) r[0]).intValue(), // year
                ((Number) r[1]).intValue(), // month
                (java.math.BigDecimal) r[2]
            )).toList();
    }

    @Override
    public List<RevenueByQuarterDTO> doanhThuTheoQuy(int year) {
        return hoaDonRepo.doanhThuTheoQuy(year).stream()
            .map(r -> new RevenueByQuarterDTO(
                ((Number) r[0]).intValue(), // year
                ((Number) r[1]).intValue(), // quarter
                (java.math.BigDecimal) r[2]
            )).toList();
    }

    @Override
    public List<RevenueByYearDTO> doanhThuTheoNam() {
        return hoaDonRepo.doanhThuTheoNam().stream()
            .map(r -> new RevenueByYearDTO(
                ((Number) r[0]).intValue(), // year
                (java.math.BigDecimal) r[1]
            )).toList();
    }

}
