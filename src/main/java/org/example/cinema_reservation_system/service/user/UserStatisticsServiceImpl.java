package org.example.cinema_reservation_system.service.user;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.userdto.UserStatisticsDto;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final UserAccountRepository userAccountRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public UserStatisticsDto getUserStatistics(Integer customerId) {
        // Lấy thống kê chi tiết của một người dùng cụ thể
        UserStatisticsDto dto = new UserStatisticsDto();
        
        // Thống kê đặt vé (refactor: dùng hóa đơn đã thanh toán)
        long totalBookings = invoiceRepository.countByUserId(customerId);
        long completedBookings = invoiceRepository.countByUserId(customerId); // coi như thanh toán thành công
        long cancelledBookings = 0L; // nếu cần, thêm repo hóa đơn hủy
        
        // Thống kê giao dịch
        long totalTransactions = invoiceRepository.countByUserId(customerId);
        double totalSpent = invoiceRepository.sumTotalAmountByUserId(customerId);
        
        // Thống kê phim đã xem
        long moviesWatched = 0L; // có thể derive từ ve_phim nếu cần
        
        dto.setTotalUsers(1L); // Chỉ 1 user
        dto.setTotalCustomers(1L);
        dto.setActiveUsers(1L);
        dto.setNewUsersThisMonth(0L);
        
        // Có thể thêm các field khác vào UserStatisticsDto nếu cần
        return dto;
    }

    @Override
    public UserStatisticsDto getBasicStatistics(Integer customerId) {
        // Lấy thống kê cơ bản của một người dùng cụ thể
        UserStatisticsDto dto = new UserStatisticsDto();
        
        // Thống kê cơ bản: số vé, phim, đánh giá, bình luận
        long totalBookings = invoiceRepository.countByUserId(customerId);
        long moviesWatched = 0L;
        
        // TODO: Thêm thống kê đánh giá và bình luận khi có repository
        // long totalReviews = reviewRepository.countByUserId(customerId);
        // long totalComments = commentRepository.countByUserId(customerId);
        
        dto.setTotalUsers(1L);
        dto.setTotalCustomers(1L);
        dto.setActiveUsers(1L);
        dto.setNewUsersThisMonth(0L);
        
        return dto;
    }

    @Override
    public UserStatisticsDto getOverallStatistics() {
        long totalUsers = userAccountRepository.count();
        long totalCustomers = userAccountRepository.countByRole("CUSTOMER");
        long totalStaff = userAccountRepository.countByRole("STAFF");
        long totalAdmins = userAccountRepository.countByRole("ADMIN");
        long activeUsers = userAccountRepository.countByTrangThai(org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount.HOAT_DONG);
        long newUsersThisMonth = userAccountRepository.countByNgayTaoAfter(
                YearMonth.now().atDay(1).atStartOfDay()
        );

        UserStatisticsDto dto = new UserStatisticsDto();
        dto.setTotalUsers(totalUsers);
        dto.setTotalCustomers(totalCustomers);
        dto.setTotalStaff(totalStaff);
        dto.setTotalAdmins(totalAdmins);
        dto.setActiveUsers(activeUsers);
        dto.setNewUsersThisMonth(newUsersThisMonth);
        
        return dto;
    }

    @Override
    public UserStatisticsDto getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        long totalUsers = userAccountRepository.countByNgayTaoBetween(
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
        );
        
        long activeUsers = userAccountRepository.countByTrangThaiAndNgayTaoBetween(
                org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount.HOAT_DONG,
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
        );

        UserStatisticsDto dto = new UserStatisticsDto();
        dto.setTotalUsers(totalUsers);
        dto.setActiveUsers(activeUsers);
        
        return dto;
    }

    @Override
    public Page<UserStatisticsDto.UserRegistrationDto> getNewUserRegistrations(Pageable pageable) {
        return userAccountRepository.findNewUserRegistrationsWithPagination(pageable)
                .map(this::convertToUserRegistrationDto);
    }

    @Override
    public Map<String, Long> getUserStatisticsByType() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("CUSTOMER", userAccountRepository.countByRole("CUSTOMER"));
        statistics.put("STAFF", userAccountRepository.countByRole("STAFF"));
        statistics.put("ADMIN", userAccountRepository.countByRole("ADMIN"));
        return statistics;
    }

    @Override
    public Map<String, Object> getUserActivityStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Thống kê đăng nhập
        long usersLoggedInToday = userAccountRepository.countByLastLoginAfter(
                LocalDate.now().atStartOfDay()
        );
        
        // Thống kê giao dịch
        long totalTransactions = invoiceRepository.count();
        long transactionsThisMonth = invoiceRepository.countByCreatedAtAfter(
                YearMonth.now().atDay(1).atStartOfDay()
        );
        
        // Thống kê "đặt vé" theo luồng mới: dựa trên hóa đơn đã tạo
        long totalBookings = invoiceRepository.count();
        long bookingsThisMonth = invoiceRepository.countByCreatedAtAfter(
                YearMonth.now().atDay(1).atStartOfDay()
        );
        
        statistics.put("usersLoggedInToday", usersLoggedInToday);
        statistics.put("totalTransactions", totalTransactions);
        statistics.put("transactionsThisMonth", transactionsThisMonth);
        statistics.put("totalBookings", totalBookings);
        statistics.put("bookingsThisMonth", bookingsThisMonth);
        
        return statistics;
    }

    @Override
    public List<UserStatisticsDto.TopUserDto> getTopUsersByTransactions(int limit) {
        return userAccountRepository.findTopUsersByTransactionCount(limit)
                .stream()
                .map(this::convertToTopUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStatisticsDto.MonthlyRegistrationDto> getMonthlyRegistrations(int year) {
        List<Object[]> monthlyData = userAccountRepository.getMonthlyRegistrations(year);
        
        return monthlyData.stream()
                .map(data -> {
                    UserStatisticsDto.MonthlyRegistrationDto dto = new UserStatisticsDto.MonthlyRegistrationDto();
                    dto.setMonth((Integer) data[0]);
                    dto.setRegistrationCount((Long) data[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private UserStatisticsDto.UserRegistrationDto convertUserToRegistrationDto(UserAccount userAccount) {
        UserStatisticsDto.UserRegistrationDto dto = new UserStatisticsDto.UserRegistrationDto();
        dto.setUserId(userAccount.getIdTaiKhoan());
        dto.setUsername(userAccount.getTenDangNhap());
        dto.setEmail(userAccount.getEmail());
        dto.setRole(userAccount.getVaiTro() != null ? userAccount.getVaiTro().getTenVaiTro() : null);
        dto.setCreatedAt(userAccount.getNgayTao());
        return dto;
    }
    
    private UserStatisticsDto.UserRegistrationDto convertToUserRegistrationDto(Object[] data) {
        UserStatisticsDto.UserRegistrationDto dto = new UserStatisticsDto.UserRegistrationDto();
        dto.setUserId((Integer) data[0]);
        dto.setUsername((String) data[1]);
        dto.setEmail((String) data[2]);
        dto.setRole((String) data[3]);
        dto.setCreatedAt(((LocalDateTime) data[4]));
        return dto;
    }

    private UserStatisticsDto.TopUserDto convertToTopUserDto(Object[] data) {
        UserStatisticsDto.TopUserDto dto = new UserStatisticsDto.TopUserDto();
        dto.setUserId((Integer) data[0]);
        dto.setUsername((String) data[1]);
        dto.setEmail((String) data[2]);
        dto.setTransactionCount((Long) data[3]);
        dto.setTotalAmount((Double) data[4]);
        return dto;
    }
}
