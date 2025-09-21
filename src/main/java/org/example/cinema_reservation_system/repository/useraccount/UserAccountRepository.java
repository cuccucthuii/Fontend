package org.example.cinema_reservation_system.repository.useraccount;

import org.example.cinema_reservation_system.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    // Basic CRUD methods
    //thêm mới đoạn này
    @Query("""
         select u
         from UserAccount u
         left join fetch u.khachHang kh
         left join fetch u.vaiTro vr
         """)
    List<UserAccount> findAllWithCustomerAndRole();

    //thêm mơ tiếp
    @Query("""
         select u from UserAccount u
         left join fetch u.khachHang kh
         left join fetch u.vaiTro vr
         where u.daXoa = false
         """)
    List<UserAccount> findAllActiveWithJoins();
    @Query("""
         select u from UserAccount u
         left join fetch u.khachHang kh
         left join fetch u.vaiTro vr
         where u.daXoa = true
         """)
    List<UserAccount> findAllTrashWithJoins();


    Optional<UserAccount> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    void deleteByTenDangNhap(String tenDangNhap);

    // Email-based queries
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);

    // Employee-based queries
    boolean existsByNhanVien_IdNhanVien(Integer idNhanVien);

    // Role-based queries (thông qua vaiTro relationship)
    @Query("SELECT ua FROM UserAccount ua WHERE ua.vaiTro.tenVaiTro = :role")
    List<UserAccount> findByRole(@Param("role") String role);

    @Query("SELECT COUNT(ua) FROM UserAccount ua WHERE ua.vaiTro.tenVaiTro = :role")
    long countByRole(@Param("role") String role);

    // Status-based queries
    List<UserAccount> findByTrangThai(org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount trangThai);
    long countByTrangThai(org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount trangThai);

    // Date-based queries
    List<UserAccount> findByNgayTaoAfter(LocalDateTime date);
    long countByNgayTaoAfter(LocalDateTime date);
    List<UserAccount> findByNgayTaoBetween(LocalDateTime startDate, LocalDateTime endDate);
    long countByNgayTaoBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Combined queries
    @Query("SELECT COUNT(ua) FROM UserAccount ua WHERE ua.trangThai = :trangThai AND ua.ngayTao BETWEEN :startDate AND :endDate")
    long countByTrangThaiAndNgayTaoBetween(@Param("trangThai") org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount trangThai,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Login tracking
    List<UserAccount> findByLastLoginAfter(LocalDateTime date);
    long countByLastLoginAfter(LocalDateTime date);

    // Custom queries for statistics
    @Query("SELECT ua FROM UserAccount ua WHERE ua.vaiTro.tenVaiTro = :role ORDER BY ua.ngayTao DESC")
    List<UserAccount> findNewUserRegistrations(@Param("role") String role);

    @Query("SELECT ua.idTaiKhoan, ua.tenDangNhap, ua.email, ua.vaiTro.tenVaiTro, ua.ngayTao FROM UserAccount ua ORDER BY ua.ngayTao DESC")
    List<Object[]> findNewUserRegistrations();

    @Query("SELECT ua.idTaiKhoan, ua.tenDangNhap, ua.email, ua.vaiTro.tenVaiTro, ua.ngayTao FROM UserAccount ua ORDER BY ua.ngayTao DESC")
    Page<Object[]> findNewUserRegistrationsWithPagination(Pageable pageable);

    @Query("SELECT ua.idTaiKhoan, ua.tenDangNhap, ua.email, COUNT(i.idHoaDon) as transactionCount, COALESCE(SUM(i.tongTien), 0) as totalAmount " +
            "FROM UserAccount ua LEFT JOIN ua.khachHang k LEFT JOIN Invoice i ON i.khachHang.idKhachHang = k.idKhachHang " +
            "GROUP BY ua.idTaiKhoan, ua.tenDangNhap, ua.email " +
            "ORDER BY COUNT(i.idHoaDon) DESC")
    List<Object[]> findTopUsersByTransactionCount(@Param("limit") int limit);

    @Query(value = "SELECT EXTRACT(MONTH FROM ua.ngay_tao) as month, COUNT(ua.id_user_account) as count " +
            "FROM tai_khoan_nguoi_dung ua " +
            "WHERE EXTRACT(YEAR FROM ua.ngay_tao) = :year " +
            "GROUP BY EXTRACT(MONTH FROM ua.ngay_tao) " +
            "ORDER BY EXTRACT(MONTH FROM ua.ngay_tao)", nativeQuery = true)
    List<Object[]> getMonthlyRegistrations(@Param("year") int year);
}