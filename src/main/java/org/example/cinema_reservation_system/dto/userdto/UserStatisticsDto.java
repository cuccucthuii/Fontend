package org.example.cinema_reservation_system.dto.userdto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class UserStatisticsDto {
    
    private Long totalUsers;
    private Long totalCustomers;
    private Long totalStaff;
    private Long totalAdmins;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersThisMonth;
    private Long newUsersThisWeek;
    private Long newUsersToday;
    private Double averageAge;
    private String mostCommonGender;
    private String mostCommonRole;
    private LocalDateTime lastUpdated;
    
    // Constructor
    public UserStatisticsDto() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public UserStatisticsDto(Long totalUsers, Long activeUsers, Long inactiveUsers) {
        this();
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
    }
    
    // Inner classes for specific statistics
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegistrationDto {
        private Integer userId;
        private String username;
        private String email;
        private String role;
        private LocalDateTime createdAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUserDto {
        private Integer userId;
        private String username;
        private String email;
        private Long transactionCount;
        private Double totalAmount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRegistrationDto {
        private Integer month;
        private Long registrationCount;
    }
}
