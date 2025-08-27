package org.example.cinema_reservation_system.service.payment;

import jakarta.transaction.Transactional;
import org.example.cinema_reservation_system.dto.payment.PaymentRequestDto;
import org.example.cinema_reservation_system.dto.payment.PaymentResponseDto;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.entity.Payment;
import org.example.cinema_reservation_system.entity.CinemaTicket;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.repository.payment.PaymentRepository;
import org.example.cinema_reservation_system.repository.cinematicket.CinemaTicketRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiThanhToan;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final InvoiceRepository hoaDonRepository;
    private final PaymentRepository thanhToanRepository;
    private final CinemaTicketRepository vePhimRepository;

    public PaymentServiceImpl(InvoiceRepository hoaDonRepository,
                              PaymentRepository thanhToanRepository,
                              CinemaTicketRepository vePhimRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.vePhimRepository = vePhimRepository;
    }

    @Override
    @Transactional
    public PaymentResponseDto thanhToanHoaDon(PaymentRequestDto request) {
        Invoice hoaDon = hoaDonRepository.findById(request.getIdHoaDon())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));

        List<CinemaTicket> veList = vePhimRepository.findAllByHoaDon_IdHoaDon(hoaDon.getIdHoaDon());
        if (veList.isEmpty()) throw new RuntimeException("Hóa đơn chưa có vé nào");

        BigDecimal tongTien = veList.stream()
                .map(CinemaTicket::getGiaVe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment thanhToan = new Payment();
        thanhToan.setHoaDon(hoaDon);
        thanhToan.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        thanhToan.setSoTien(tongTien);
        thanhToan.setNgayThanhToan(LocalDate.now());
        thanhToan.setTrangThai(TrangThaiThanhToan.HOAN_THANH);

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
