package org.example.cinema_reservation_system.service.user;

import org.example.cinema_reservation_system.dto.userdto.UserStatisticsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserStatisticsService {
    
    /**
     * Lấy thống kê của một người dùng cụ thể
     */
    UserStatisticsDto getUserStatistics(Integer customerId);
    
    /**
     * Lấy thống kê cơ bản của một người dùng cụ thể
     */
    UserStatisticsDto getBasicStatistics(Integer customerId);
    
    /**
     * Lấy thống kê tổng quan người dùng
     */
    UserStatisticsDto getOverallStatistics();
    
    /**
     * Lấy thống kê người dùng theo khoảng thời gian
     */
    UserStatisticsDto getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy danh sách người dùng mới đăng ký
     */
    Page<UserStatisticsDto.UserRegistrationDto> getNewUserRegistrations(Pageable pageable);
    
    /**
     * Lấy thống kê người dùng theo loại
     */
    Map<String, Long> getUserStatisticsByType();
    
    /**
     * Lấy thống kê hoạt động người dùng
     */
    Map<String, Object> getUserActivityStatistics();
    
    /**
     * Lấy top người dùng có nhiều giao dịch nhất
     */
    List<UserStatisticsDto.TopUserDto> getTopUsersByTransactions(int limit);
    
    /**
     * Lấy thống kê đăng ký theo tháng
     */
    List<UserStatisticsDto.MonthlyRegistrationDto> getMonthlyRegistrations(int year);
}
