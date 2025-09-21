package org.example.cinema_reservation_system.service.payment;

import jakarta.transaction.Transactional;
import org.example.cinema_reservation_system.dto.payment.PaymentRequestDto;
import org.example.cinema_reservation_system.dto.payment.PaymentResponseDto;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.entity.Payment;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.repository.payment.PaymentRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiThanhToan;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final InvoiceRepository hoaDonRepository;
    private final PaymentRepository thanhToanRepository;
    

    public PaymentServiceImpl(InvoiceRepository hoaDonRepository,
                              PaymentRepository thanhToanRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    @Transactional
    public PaymentResponseDto thanhToanHoaDon(PaymentRequestDto request) {
        Invoice hoaDon = hoaDonRepository.findById(request.getIdHoaDon())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));

        // Lấy tổng tiền từ hóa đơn trực tiếp
        Double tongTienDouble = hoaDon.getTongTien();
        if (tongTienDouble == null || tongTienDouble <= 0) {
            throw new RuntimeException("Hóa đơn chưa có số tiền hợp lệ");
        }
        BigDecimal tongTien = BigDecimal.valueOf(tongTienDouble);

        Payment thanhToan = new Payment();
        thanhToan.setHoaDon(hoaDon);
        thanhToan.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        thanhToan.setSoTien(tongTien);
        thanhToan.setNgayThanhToan(LocalDate.now());
        thanhToan.setTrangThai(TrangThaiThanhToan.DA_THANH_TOAN);

        thanhToanRepository.save(thanhToan);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setIdThanhToan(thanhToan.getIdThanhToan());
        response.setSoTien(tongTien);
        response.setPhuongThucThanhToan(thanhToan.getPhuongThucThanhToan());
        response.setTrangThai(thanhToan.getTrangThai());
        response.setNgayThanhToan(thanhToan.getNgayThanhToan());

        return response;
    }
}
