package org.example.cinema_reservation_system.repository.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    
    // Find available vouchers
    @Query("SELECT v FROM Voucher v WHERE v.trangThai = 'HOAT_DONG' " +
           "AND v.ngayBatDau <= CURRENT_DATE AND v.ngayKetThuc >= CURRENT_DATE " +
           "AND v.soLuong > 0")
    List<Voucher> findAvailableVouchers();
    
    // Find by voucher code
    Optional<Voucher> findByTenVoucher(String tenVoucher);
    
    // Find by date range
    List<Voucher> findByNgayBatDauBetween(LocalDate start, LocalDate end);
    
    // Find by status
    List<Voucher> findByTrangThai(TrangThai trangThai);
    
    // Find vouchers by customer (through CustomerVoucher)
    @Query("SELECT v FROM Voucher v JOIN CustomerVoucher cv ON v.idVoucher = cv.voucher.idVoucher " +
           "WHERE cv.khachHang.idKhachHang = :customerId")
    List<Voucher> findVouchersByCustomerId(@Param("customerId") Integer customerId);
    
    // Find active vouchers by customer
    @Query("SELECT v FROM Voucher v JOIN CustomerVoucher cv ON v.idVoucher = cv.voucher.idVoucher " +
           "WHERE cv.khachHang.idKhachHang = :customerId AND cv.trangThai = 'CHUA_SU_DUNG' " +
           "AND v.trangThai = 'HOAT_DONG' AND v.ngayBatDau <= CURRENT_DATE AND v.ngayKetThuc >= CURRENT_DATE")
    List<Voucher> findActiveVouchersByCustomerId(@Param("customerId") Integer customerId);
}
