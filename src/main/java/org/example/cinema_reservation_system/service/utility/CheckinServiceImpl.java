package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.checkindto.CheckinRequestDto;
import org.example.cinema_reservation_system.dto.checkindto.CheckinResponseDto;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.entity.Staff;
import org.example.cinema_reservation_system.entity.TicketCheckin;
import org.example.cinema_reservation_system.exception.BadRequestException;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.checkin.CheckinRepository;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.repository.staff.StaffRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiDatVe;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckinServiceImpl implements CheckinService {

    private final CheckinRepository checkinRepository;
    private final InvoiceRepository invoiceRepository;
    private final StaffRepository nhanVienRepository;

    @Override
    public CheckinResponseDto checkinVe(CheckinRequestDto request) {
        // 1. Kiểm tra hóa đơn tồn tại (thay cho booking)
        Invoice invoice = invoiceRepository.findById(request.getIdVePhim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn/đơn đặt vé"));

        // 2. Kiểm tra nhân viên tồn tại
        Staff nhanVien = nhanVienRepository.findById(request.getIdNhanVienCheckin())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        // 3. Kiểm tra vé đã được check-in chưa
        // Tạm thời không có cờ daCheckin trên Invoice: bỏ qua hoặc kiểm tra qua Ticket sau này
        if (false) {
            throw new BadRequestException("Vé này đã được check-in");
        }

        // 4. Kiểm tra vé đã được thanh toán chưa
        if (!"DA_THANH_TOAN".equalsIgnoreCase(String.valueOf(invoice.getTrangThai()))) {
            throw new BadRequestException("Chỉ có thể check-in vé đã thanh toán");
        }

        // 5. Kiểm tra thời gian check-in hợp lệ
        // Chưa có liên kết trực tiếp từ Invoice tới Suất chiếu/ghế, tạm thời bỏ validate thời gian

        // 6. Cập nhật trạng thái check-in
        // TODO: Khi có Ticket entity, cập nhật trạng thái check-in trên vé

        // 7. Tạo check-in record
        TicketCheckin checkin = new TicketCheckin();
        checkin.setThoiGianCheckin(LocalDateTime.now());
        checkin.setTrangThai("DA_CHECKIN");
        checkin.setNhanVienCheckin(nhanVien);
        checkin.setGhiChu(request.getGhiChu());
        checkin.setMaVe(invoice.getMaDatVe());

        TicketCheckin savedCheckin = checkinRepository.save(checkin);

        // 8. Convert to DTO
        return convertToDto(savedCheckin);
    }

    @Override
    public CheckinResponseDto checkinVeByMaVe(CheckinRequestDto request) {
        // 1. Tìm booking theo mã vé
        Invoice invoice = invoiceRepository.findByMaDatVe(request.getMaVe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn với mã: " + request.getMaVe()));

        // 2. Kiểm tra nhân viên tồn tại
        Staff nhanVien = nhanVienRepository.findById(request.getIdNhanVienCheckin())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        // 3. Kiểm tra vé đã được check-in chưa
        if (false) {
            throw new BadRequestException("Vé này đã được check-in");
        }

        // 4. Kiểm tra vé đã được thanh toán chưa
        if (!"DA_THANH_TOAN".equalsIgnoreCase(String.valueOf(invoice.getTrangThai()))) {
            throw new BadRequestException("Chỉ có thể check-in vé đã thanh toán");
        }

        // 5. Kiểm tra thời gian check-in hợp lệ
        // Bỏ qua validate thời gian tạm thời

        // 6. Cập nhật trạng thái check-in
        // TODO: cập nhật Ticket về trạng thái check-in sau khi có Ticket entity

        // 7. Tạo check-in record
        TicketCheckin checkin = new TicketCheckin();
        checkin.setThoiGianCheckin(LocalDateTime.now());
        checkin.setTrangThai("DA_CHECKIN");
        checkin.setNhanVienCheckin(nhanVien);
        checkin.setGhiChu(request.getGhiChu());
        checkin.setMaVe(invoice.getMaDatVe());

        TicketCheckin savedCheckin = checkinRepository.save(checkin);

        // 8. Convert to DTO
        return convertToDto(savedCheckin);
    }

    @Override
    public void huyCheckin(Integer idCheckin) {
        TicketCheckin checkin = checkinRepository.findById(idCheckin)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy check-in"));

        checkin.setTrangThai("DA_HUY");
        checkinRepository.save(checkin);
    }

    @Override
    public CheckinResponseDto getCheckinById(Integer idCheckin) {
        TicketCheckin checkin = checkinRepository.findById(idCheckin)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy check-in"));

        return convertToDto(checkin);
    }

    @Override
    public List<CheckinResponseDto> getCheckinByNgay(LocalDateTime ngay) {
        List<TicketCheckin> checkins = checkinRepository.findByNgayCheckin(ngay);
        return checkins.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinResponseDto> getCheckinByRapChieu(Integer idRapChieu) {
        // TODO: Implement logic lấy check-in theo rạp chiếu
        return List.of();
    }
    
    @Override
    public List<CheckinResponseDto> getCheckinBySuatChieu(Integer idSuatChieu) {
        // TODO: Implement logic lấy check-in theo suất chiếu
        return List.of();
    }
    
    @Override
    public boolean isVeDaCheckin(Integer idVePhim) {
        // TODO: Implement logic kiểm tra vé đã check-in
        return false;
    }

    @Override
    public Long countCheckinByNgay(LocalDateTime ngay) {
        return checkinRepository.countByNgayCheckin(ngay);
    }

    // TODO: validate thời gian check-in sẽ được thực hiện dựa trên Ticket/ShowTime liên kết với Invoice

    private CheckinResponseDto convertToDto(TicketCheckin checkin) {
        CheckinResponseDto dto = new CheckinResponseDto();
        dto.setIdCheckin(checkin.getIdCheckin());
        dto.setMaVe(checkin.getMaVe());
        dto.setThoiGianCheckin(checkin.getThoiGianCheckin());
        dto.setTrangThai(checkin.getTrangThai());
        dto.setGhiChu(checkin.getGhiChu());
        if (checkin.getNhanVienCheckin() != null) {
            dto.setTenNhanVienCheckin(checkin.getNhanVienCheckin().getTenNhanVien());
        }
        
        return dto;
    }
}

