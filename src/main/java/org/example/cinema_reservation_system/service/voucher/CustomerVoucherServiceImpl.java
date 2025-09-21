package org.example.cinema_reservation_system.service.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.entity.CustomerVoucher;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerVoucherRepository;
import org.example.cinema_reservation_system.repository.voucher.VoucherRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

    @Autowired
    private CustomerVoucherRepository customerVoucherRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public void assignVoucherToCustomer(Integer customerId, Integer voucherId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId));
        
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + voucherId));
        
        // Check if customer already has this voucher
        if (hasVoucher(customerId, voucherId)) {
            throw new RuntimeException("Khách hàng đã có voucher này");
        }
        
        // Check if voucher is available
        if (voucher.getSoLuong() <= 0) {
            throw new RuntimeException("Voucher đã hết số lượng");
        }
        
        // Create customer-voucher relationship
        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setKhachHang(customer);
        customerVoucher.setVoucher(voucher);
        customerVoucher.setNgayNhan(LocalDate.now());
        customerVoucher.setTrangThai(TrangThai.CHUA_SU_DUNG.toString());
        
        customerVoucherRepository.save(customerVoucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getCustomerVouchers(Integer customerId) {
        return customerVoucherRepository.findByCustomerIdKhachHang(customerId)
                .stream()
                .map(CustomerVoucher::getVoucher)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getCustomerUnusedVouchers(Integer customerId) {
        return customerVoucherRepository.findByCustomerIdKhachHangAndTrangThai(customerId, TrangThai.CHUA_SU_DUNG.toString())
                .stream()
                .map(CustomerVoucher::getVoucher)
                .filter(voucher -> voucher.getTrangThai().toString().equals("HOAT_DONG"))
                .filter(voucher -> LocalDate.now().isAfter(voucher.getNgayBatDau().minusDays(1)) && 
                                 LocalDate.now().isBefore(voucher.getNgayKetThuc().plusDays(1)))
                .collect(Collectors.toList());
    }

    @Override
    public void useCustomerVoucher(Integer customerId, Integer voucherId) {
        CustomerVoucher customerVoucher = customerVoucherRepository
                .findByCustomerIdKhachHangAndVoucherIdVoucher(customerId, voucherId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không có voucher này"));
        
        if (TrangThai.DA_SU_DUNG.toString().equals(customerVoucher.getTrangThai())) {
            throw new RuntimeException("Voucher đã được sử dụng");
        }
        
        // Mark voucher as used
        customerVoucher.setTrangThai(TrangThai.DA_SU_DUNG.toString());
        customerVoucherRepository.save(customerVoucher);
        
        // Decrease voucher quantity
        Voucher voucher = customerVoucher.getVoucher();
        if (voucher.getSoLuong() > 0) {
            voucher.setSoLuong(voucher.getSoLuong() - 1);
            voucherRepository.save(voucher);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVoucher(Integer customerId, Integer voucherId) {
        return customerVoucherRepository.existsByCustomerIdKhachHangAndVoucherIdVoucher(customerId, voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerVoucher getCustomerVoucherDetails(Integer customerId, Integer voucherId) {
        return customerVoucherRepository
                .findByCustomerIdKhachHangAndVoucherIdVoucher(customerId, voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher của khách hàng"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerVoucher> getAllCustomerVouchers(Integer customerId) {
        return customerVoucherRepository.findByCustomerIdKhachHang(customerId);
    }
}
