package org.example.cinema_reservation_system.service.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.dto.voucher.VoucherDto;
import org.example.cinema_reservation_system.dto.voucher.VoucherValidationRequest;
import org.example.cinema_reservation_system.dto.voucher.VoucherApplicationRequest;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    
    // CRUD operations
    List<Voucher> getAllVouchers();
    Voucher getVoucherById(Integer id);
    Voucher createVoucher(VoucherDto dto);
    Voucher updateVoucher(Integer id, VoucherDto dto);
    void deleteVoucher(Integer id);
    
    // Business logic
    List<Voucher> getAvailableVouchers();
    List<Voucher> getVouchersByCustomer(Integer customerId);
    boolean validateVoucher(String voucherCode, BigDecimal orderAmount);
    BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderAmount);
    void applyVoucher(Integer customerId, Integer voucherId);
    void useVoucher(Integer voucherId);
    
    // Advanced operations
    List<Voucher> getVouchersByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
    List<Voucher> getVouchersByStatus(String status);
    Voucher getVoucherByCode(String voucherCode);
    void assignVoucherToCustomer(Integer customerId, Integer voucherId);
    boolean hasVoucher(Integer customerId, Integer voucherId);
}
