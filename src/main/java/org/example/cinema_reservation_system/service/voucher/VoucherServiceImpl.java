package org.example.cinema_reservation_system.service.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.entity.CustomerVoucher;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.dto.voucher.VoucherDto;
import org.example.cinema_reservation_system.dto.voucher.VoucherValidationRequest;
import org.example.cinema_reservation_system.dto.voucher.VoucherApplicationRequest;
import org.example.cinema_reservation_system.repository.voucher.VoucherRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerVoucherRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CustomerVoucherRepository customerVoucherRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherById(Integer id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + id));
    }

    @Override
    public Voucher createVoucher(VoucherDto dto) {
        // Validate date range
        if (dto.getNgayBatDau().isAfter(dto.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        
        // Check if voucher name already exists
        if (voucherRepository.findByTenVoucher(dto.getTenVoucher()).isPresent()) {
            throw new RuntimeException("Tên voucher đã tồn tại");
        }
        
        Voucher voucher = modelMapper.map(dto, Voucher.class);
        voucher.setTrangThai(TrangThai.HOAT_DONG);
        
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher updateVoucher(Integer id, VoucherDto dto) {
        Voucher existingVoucher = getVoucherById(id);
        
        // Validate date range
        if (dto.getNgayBatDau().isAfter(dto.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        
        // Check if voucher name already exists (excluding current voucher)
        Optional<Voucher> existingByName = voucherRepository.findByTenVoucher(dto.getTenVoucher());
        if (existingByName.isPresent() && !existingByName.get().getIdVoucher().equals(id)) {
            throw new RuntimeException("Tên voucher đã tồn tại");
        }
        
        modelMapper.map(dto, existingVoucher);
        return voucherRepository.save(existingVoucher);
    }

    @Override
    public void deleteVoucher(Integer id) {
        Voucher voucher = getVoucherById(id);
        voucherRepository.delete(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getAvailableVouchers() {
        return voucherRepository.findAvailableVouchers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getVouchersByCustomer(Integer customerId) {
        return voucherRepository.findActiveVouchersByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateVoucher(String voucherCode, BigDecimal orderAmount) {
        Optional<Voucher> voucherOpt = voucherRepository.findByTenVoucher(voucherCode);
        if (!voucherOpt.isPresent()) {
            return false;
        }
        
        Voucher voucher = voucherOpt.get();
        
        // Check if voucher is active
        if (!voucher.getTrangThai().equals(TrangThai.HOAT_DONG)) {
            return false;
        }
        
        // Check date range
        LocalDate today = LocalDate.now();
        if (today.isBefore(voucher.getNgayBatDau()) || today.isAfter(voucher.getNgayKetThuc())) {
            return false;
        }
        
        // Check quantity
        if (voucher.getSoLuong() <= 0) {
            return false;
        }
        
        // Check minimum order amount if specified
        if (voucher.getDieuKienGiam() != null && voucher.getDieuKienGiam().contains("tối thiểu")) {
            // Extract minimum amount from condition (simplified)
            // In real implementation, you might want to parse this more carefully
            return orderAmount.compareTo(BigDecimal.valueOf(100000)) >= 0; // Example: 100k minimum
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderAmount) {
        if (!validateVoucher(voucher.getTenVoucher(), orderAmount)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        // Calculate percentage discount
        if (voucher.getPhanTramGiam() != null) {
            discount = orderAmount.multiply(voucher.getPhanTramGiam()).divide(BigDecimal.valueOf(100));
            
            // Apply maximum discount limit
            if (voucher.getMucGiamToiDa() != null && discount.compareTo(voucher.getMucGiamToiDa()) > 0) {
                discount = voucher.getMucGiamToiDa();
            }
        }
        
        // Calculate fixed amount discount
        if (voucher.getSoTienGiam() != null) {
            discount = discount.add(voucher.getSoTienGiam());
        }
        
        // Ensure discount doesn't exceed order amount
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        
        return discount;
    }

    @Override
    public void applyVoucher(Integer customerId, Integer voucherId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        
        Voucher voucher = getVoucherById(voucherId);
        
        // Check if customer already has this voucher
        if (hasVoucher(customerId, voucherId)) {
            throw new RuntimeException("Khách hàng đã có voucher này");
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
    public void useVoucher(Integer voucherId) {
        Voucher voucher = getVoucherById(voucherId);
        
        // Decrease quantity
        if (voucher.getSoLuong() > 0) {
            voucher.setSoLuong(voucher.getSoLuong() - 1);
            voucherRepository.save(voucher);
        }
        
        // If quantity reaches 0, mark as used
        if (voucher.getSoLuong() == 0) {
            voucher.setTrangThai(TrangThai.HET_HANG);
            voucherRepository.save(voucher);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getVouchersByDateRange(LocalDate startDate, LocalDate endDate) {
        return voucherRepository.findByNgayBatDauBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getVouchersByStatus(String status) {
        TrangThai trangThai = TrangThai.valueOf(status.toUpperCase());
        return voucherRepository.findByTrangThai(trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherByCode(String voucherCode) {
        return voucherRepository.findByTenVoucher(voucherCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với mã: " + voucherCode));
    }

    @Override
    public void assignVoucherToCustomer(Integer customerId, Integer voucherId) {
        applyVoucher(customerId, voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVoucher(Integer customerId, Integer voucherId) {
        return customerVoucherRepository.existsByCustomerIdKhachHangAndVoucherIdVoucher(customerId, voucherId);
    }
}
