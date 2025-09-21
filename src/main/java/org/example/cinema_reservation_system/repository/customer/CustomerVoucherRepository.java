package org.example.cinema_reservation_system.repository.customer;

import org.example.cinema_reservation_system.entity.CustomerVoucher;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, CustomerVoucher.CustomerVoucherId> {
    
    // Find by customer ID
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.khachHang.idKhachHang = :customerId")
    List<CustomerVoucher> findByCustomerIdKhachHang(@Param("customerId") Integer customerId);
    
    // Find by customer ID and status
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.khachHang.idKhachHang = :customerId AND cv.trangThai = :status")
    List<CustomerVoucher> findByCustomerIdKhachHangAndTrangThai(@Param("customerId") Integer customerId, @Param("status") String status);
    
    // Find by customer and voucher IDs
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.khachHang.idKhachHang = :customerId AND cv.voucher.idVoucher = :voucherId")
    Optional<CustomerVoucher> findByCustomerIdKhachHangAndVoucherIdVoucher(@Param("customerId") Integer customerId, @Param("voucherId") Integer voucherId);
    
    // Check if customer has voucher
    @Query("SELECT COUNT(cv) > 0 FROM CustomerVoucher cv WHERE cv.khachHang.idKhachHang = :customerId AND cv.voucher.idVoucher = :voucherId")
    boolean existsByCustomerIdKhachHangAndVoucherIdVoucher(@Param("customerId") Integer customerId, @Param("voucherId") Integer voucherId);
    
    // Find by voucher ID
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.voucher.idVoucher = :voucherId")
    List<CustomerVoucher> findByVoucherIdVoucher(@Param("voucherId") Integer voucherId);
}
