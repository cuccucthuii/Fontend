package org.example.cinema_reservation_system.controller.voucher;

import org.example.cinema_reservation_system.entity.Voucher;
import org.example.cinema_reservation_system.dto.voucher.VoucherDto;
import org.example.cinema_reservation_system.dto.voucher.VoucherValidationRequest;
import org.example.cinema_reservation_system.dto.voucher.VoucherApplicationRequest;
import org.example.cinema_reservation_system.service.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // =====================================================
    // CRUD OPERATIONS
    // =====================================================
    
    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable Integer id) {
        Voucher voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(voucher);
    }
    
    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody VoucherDto dto) {
        Voucher voucher = voucherService.createVoucher(dto);
        return ResponseEntity.ok(voucher);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Integer id, @Valid @RequestBody VoucherDto dto) {
        Voucher voucher = voucherService.updateVoucher(id, dto);
        return ResponseEntity.ok(voucher);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok().build();
    }
    
    // =====================================================
    // BUSINESS OPERATIONS
    // =====================================================
    
    @GetMapping("/available")
    public ResponseEntity<List<Voucher>> getAvailableVouchers() {
        List<Voucher> vouchers = voucherService.getAvailableVouchers();
        return ResponseEntity.ok(vouchers);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Voucher>> getCustomerVouchers(@PathVariable Integer customerId) {
        List<Voucher> vouchers = voucherService.getVouchersByCustomer(customerId);
        return ResponseEntity.ok(vouchers);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateVoucher(@Valid @RequestBody VoucherValidationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        boolean isValid = voucherService.validateVoucher(request.getVoucherCode(), request.getOrderAmount());
        response.put("valid", isValid);
        
        if (isValid) {
            Voucher voucher = voucherService.getVoucherByCode(request.getVoucherCode());
            BigDecimal discount = voucherService.calculateDiscount(voucher, request.getOrderAmount());
            response.put("discount", discount);
            response.put("voucher", voucher);
        } else {
            response.put("message", "Voucher không hợp lệ hoặc không thể áp dụng");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyVoucher(@Valid @RequestBody VoucherApplicationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            voucherService.applyVoucher(request.getCustomerId(), request.getVoucherId());
            response.put("success", true);
            response.put("message", "Voucher đã được áp dụng thành công");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignVoucherToCustomer(
            @RequestParam Integer customerId, 
            @RequestParam Integer voucherId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            voucherService.assignVoucherToCustomer(customerId, voucherId);
            response.put("success", true);
            response.put("message", "Voucher đã được gán cho khách hàng");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/use/{voucherId}")
    public ResponseEntity<Map<String, Object>> useVoucher(@PathVariable Integer voucherId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            voucherService.useVoucher(voucherId);
            response.put("success", true);
            response.put("message", "Voucher đã được sử dụng");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // =====================================================
    // ADVANCED OPERATIONS
    // =====================================================
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Voucher>> getVouchersByDateRange(
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        List<Voucher> vouchers = voucherService.getVouchersByDateRange(startDate, endDate);
        return ResponseEntity.ok(vouchers);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Voucher>> getVouchersByStatus(@PathVariable String status) {
        List<Voucher> vouchers = voucherService.getVouchersByStatus(status);
        return ResponseEntity.ok(vouchers);
    }
    
    @GetMapping("/code/{voucherCode}")
    public ResponseEntity<Voucher> getVoucherByCode(@PathVariable String voucherCode) {
        Voucher voucher = voucherService.getVoucherByCode(voucherCode);
        return ResponseEntity.ok(voucher);
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateDiscount(
            @RequestParam String voucherCode, 
            @RequestParam BigDecimal orderAmount) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Voucher voucher = voucherService.getVoucherByCode(voucherCode);
            BigDecimal discount = voucherService.calculateDiscount(voucher, orderAmount);
            
            response.put("voucher", voucher);
            response.put("orderAmount", orderAmount);
            response.put("discount", discount);
            response.put("finalAmount", orderAmount.subtract(discount));
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/{customerId}/{voucherId}")
    public ResponseEntity<Map<String, Object>> checkCustomerVoucher(
            @PathVariable Integer customerId, 
            @PathVariable Integer voucherId) {
        Map<String, Object> response = new HashMap<>();
        
        boolean hasVoucher = voucherService.hasVoucher(customerId, voucherId);
        response.put("hasVoucher", hasVoucher);
        
        return ResponseEntity.ok(response);
    }
}
