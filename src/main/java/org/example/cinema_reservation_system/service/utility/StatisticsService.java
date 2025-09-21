package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.statistics.*;

import java.math.BigDecimal;
import java.util.List;

public interface StatisticsService {
    Double getTongDoanhThu();
    BigDecimal tongdoanhThuThangNam(int thang, int nam);
    List<StatisticsByTheater> getDoanhThuTheoRap();
    List<StatisticsByMovie> getDoanhThuTheoPhim();
    // Báo cáo doanh thu theo ngày, tuần, tháng, quý, năm
    List<RevenueByDateDTO> doanhThuTheoNgay(java.time.LocalDate from, java.time.LocalDate to);
    List<RevenueByWeekDTO> doanhThuTheoTuan(int year);
    List<RevenueByMonthDTO> doanhThuTheoThang(int year);
    List<RevenueByQuarterDTO> doanhThuTheoQuy(int year);
    List<RevenueByYearDTO> doanhThuTheoNam();
}
