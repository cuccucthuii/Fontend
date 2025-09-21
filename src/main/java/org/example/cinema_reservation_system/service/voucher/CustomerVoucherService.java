package org.example.cinema_reservation_system.service.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.entity.CustomerVoucher;
import java.util.List;

public interface CustomerVoucherService {
    
    /**
     * Gán voucher cho khách hàng
     */
    void assignVoucherToCustomer(Integer customerId, Integer voucherId);
    
    /**
     * Lấy danh sách voucher của khách hàng
     */
    List<Voucher> getCustomerVouchers(Integer customerId);
    
    /**
     * Lấy danh sách voucher chưa sử dụng của khách hàng
     */
    List<Voucher> getCustomerUnusedVouchers(Integer customerId);
    
    /**
     * Sử dụng voucher của khách hàng
     */
    void useCustomerVoucher(Integer customerId, Integer voucherId);
    
    /**
     * Kiểm tra khách hàng có voucher không
     */
    boolean hasVoucher(Integer customerId, Integer voucherId);
    
    /**
     * Lấy thông tin chi tiết voucher của khách hàng
     */
    CustomerVoucher getCustomerVoucherDetails(Integer customerId, Integer voucherId);
    
    /**
     * Lấy danh sách tất cả voucher đã gán cho khách hàng
     */
    List<CustomerVoucher> getAllCustomerVouchers(Integer customerId);
}
