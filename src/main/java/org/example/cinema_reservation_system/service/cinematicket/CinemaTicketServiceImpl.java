package org.example.cinema_reservation_system.service.cinematicket;
import jakarta.persistence.EntityNotFoundException;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketResponseDto;
import org.example.cinema_reservation_system.dto.cinematicketdto.CinemaTicketRequestDto;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.exception.BadRequestException;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.seat.SeatRepository;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.staff.StaffRepository;
import org.example.cinema_reservation_system.repository.movie.MovieRepository;
import org.example.cinema_reservation_system.repository.room.RoomRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.repository.payment.PaymentRepository;
import org.example.cinema_reservation_system.repository.cinematicket.CinemaTicketRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiVePhim;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.example.cinema_reservation_system.validation.MovieBookingValidator;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaTicketServiceImpl implements CinemaTicketService {
    private final CinemaTicketRepository vePhimRepo;
    private final SeatRepository gheNgoiRepository;
    private final MovieRepository phimRepository;
    private final ShowTimeRepository suatChieuRepository;
    private final CustomerRepository khachHangRepository;
    private final StaffRepository nhanVienRepository;
    private final RoomRepository phongChieuRepository;
    private final InvoiceRepository hoaDonRepository;
    private final PaymentRepository thanhToanRepository;
    private final CinemaTicketRepository cinemaTicketRepository;
    private ModelMapper modelMapper;
    
    @Autowired
    private MovieBookingValidator movieBookingValidator;
    public CinemaTicketServiceImpl(CinemaTicketRepository vePhimRepo, ModelMapper modelMapper, SeatRepository gheNgoiRepository, MovieRepository phimRepository, ShowTimeRepository suatChieuRepository, CustomerRepository khachHangRepository, StaffRepository nhanVienRepository, RoomRepository phongChieuRepository, InvoiceRepository hoaDonRepository, PaymentRepository thanhToanRepository, CinemaTicketRepository cinemaTicketRepository) {
        this.vePhimRepo = vePhimRepo;
        this.modelMapper = modelMapper;
        // Create TypeMap for VePhim to VePhimDto
        TypeMap<CinemaTicket, CinemaTicketResponseDto> propertyTypeMap = modelMapper.createTypeMap(CinemaTicket.class, CinemaTicketResponseDto.class);
        propertyTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getPhim().getTenPhim(), CinemaTicketResponseDto::setTenPhim);
            mapper.map(src -> src.getNhanVien().getTenNhanVien(), CinemaTicketResponseDto::setTenNhanVien);
            mapper.map(src -> src.getSuatChieu().getNgayChieu(), CinemaTicketResponseDto::setNgayChieu);
            mapper.map(src -> src.getSuatChieu().getThoiGianBatDau(), CinemaTicketResponseDto::setGioChieu);
            mapper.map(src -> src.getGheNgoi().getSoGhe(), CinemaTicketResponseDto::setSoGhe);
            mapper.map(src -> src.getSuatChieu().getTenSuatChieu(), CinemaTicketResponseDto::setTenSuatChieu);
            mapper.map(src -> src.getKhachHang().getTenKhachHang(), CinemaTicketResponseDto::setTenKhachHang);
            mapper.map(src -> src.getKhachHang().getSoDienThoaiKhachHang(), CinemaTicketResponseDto::setSoDienThoaiKhachHang);
            mapper.map(src -> src.getSuatChieu().getPhongChieu().getRapChieu().getTenRapChieu(), CinemaTicketResponseDto::setTenRapChieu);
        });
        this.gheNgoiRepository = gheNgoiRepository;
        this.phimRepository = phimRepository;
        this.suatChieuRepository = suatChieuRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.phongChieuRepository = phongChieuRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.cinemaTicketRepository = cinemaTicketRepository;
    }
    @Override
    public List<CinemaTicketResponseDto> getAllVePhim() {
        return vePhimRepo.findAll().stream()
                .map(vePhim -> modelMapper.map(vePhim, CinemaTicketResponseDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public CinemaTicketResponseDto createVePhim(CinemaTicketRequestDto dto) {
        // 1. Validate & truy vấn các entity liên quan
        Seat gheNgoi = gheNgoiRepository.findById(dto.getIdGheNgoi())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghế ngồi"));
        Movie phim = phimRepository.findById(dto.getIdPhim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
        ShowTime suatChieu = suatChieuRepository.findById(dto.getIdSuatChieu())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu"));
        Customer khachHang = khachHangRepository.findById(dto.getIdKhachHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
        Staff nhanVien = nhanVienRepository.findById(dto.getIdNhanVien())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        Room phongChieu = phongChieuRepository.findById(dto.getIdPhongChieu())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu"));
        Invoice hoaDon = hoaDonRepository.findById(dto.getIdHoaDon())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));
        
        // ========== VALIDATION CHO PHIM SẮP_CHIẾU ==========
        
        // Sử dụng MovieBookingValidator để validate
        movieBookingValidator.validateSapChieuBooking(phim, suatChieu);
        
        // ========== END VALIDATION ==========
        
        // 2. Kiểm tra ghế đã được đặt trong suất chiếu chưa
        if (vePhimRepo.existsByGheNgoiAndSuatChieu(gheNgoi, suatChieu)) {
            throw new BadRequestException("Ghế này đã được đặt trong suất chiếu này");
        }
        // 3. Parse enum trạng thái
        TrangThaiVePhim trangThai;
        try {
            trangThai = TrangThaiVePhim.valueOf(dto.getTrangThai().toUpperCase()); // để phòng client gửi lowercase
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái vé không hợp lệ");
        }
        // 4. Gọi native insert
        vePhimRepo.insertVePhimNative(
                gheNgoi.getIdGheNgoi(),
                dto.getGiaVe(),
                hoaDon.getIdHoaDon(),
                khachHang.getIdKhachHang(),
                java.sql.Date.valueOf(dto.getNgayDat()),
                nhanVien.getIdNhanVien(),
                phim.getIdPhim(),
                suatChieu.getIdSuatChieu(),
                phongChieu.getIdPhongChieu(),
                trangThai.name()
        );
        // 5. Trả lại entity (chưa có ID vì insert native, có thể mapping lại nếu cần)
        CinemaTicket vePhim = new CinemaTicket();
        vePhim.setGiaVe(dto.getGiaVe());
        vePhim.setNgayDat(dto.getNgayDat());
        vePhim.setGheNgoi(gheNgoi);
        vePhim.setPhim(phim);
        vePhim.setSuatChieu(suatChieu);
        vePhim.setNhanVien(nhanVien);
        vePhim.setHoaDon(hoaDon);
        vePhim.setKhachHang(khachHang);
        vePhim.setPhongChieu(phongChieu);
        vePhim.setTrangThai(trangThai);
        // Convert Entity ➜ Response DTO
        return modelMapper.map(vePhim, CinemaTicketResponseDto.class);
    }
    @Override
    public CinemaTicketResponseDto getVePhimDtoById(Integer id) {
        CinemaTicket vePhim = vePhimRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vé phim với id: " + id));
        return modelMapper.map(vePhim, CinemaTicketResponseDto.class);
    }

    // ========== SOFT DELETE METHODS ==========

    @Override
    @Transactional
    public CinemaTicketResponseDto softDeleteVePhim(Integer id) {
        CinemaTicket ticket = cinemaTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé phim với ID: " + id));

        // Kiểm tra vé đã bị xóa chưa
        if (Boolean.TRUE.equals(ticket.getDaXoa())) {
            throw new RuntimeException("Vé phim đã bị xóa trước đó");
        }

        // Soft delete
        ticket.setDaXoa(true);
        ticket.setNgayCapNhat(LocalDateTime.now());

        CinemaTicket updatedTicket = cinemaTicketRepository.save(ticket);
        return convertToResponseDto(updatedTicket);
    }

    @Override
    @Transactional
    public CinemaTicketResponseDto restoreVePhim(Integer id) {
        CinemaTicket ticket = cinemaTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé phim với ID: " + id));

        // Kiểm tra vé có bị xóa không
        if (!Boolean.TRUE.equals(ticket.getDaXoa())) {
            throw new RuntimeException("Vé phim chưa bị xóa, không thể khôi phục");
        }

        // Restore
        ticket.setDaXoa(false);
        ticket.setNgayCapNhat(LocalDateTime.now());

        CinemaTicket updatedTicket = cinemaTicketRepository.save(ticket);
        return convertToResponseDto(updatedTicket);
    }

    @Override
    public List<CinemaTicketResponseDto> getDeletedVePhim() {
        // Chỉ lấy vé đã bị xóa (daXoa = true)
        List<CinemaTicket> deletedTickets = cinemaTicketRepository.findByDaXoaTrue();
        return deletedTickets.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CinemaTicketResponseDto updatePaymentStatus(Integer id, String trangThaiThanhToan) {
        CinemaTicket ticket = cinemaTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé phim với ID: " + id));

        // Kiểm tra vé đã bị xóa chưa
        if (Boolean.TRUE.equals(ticket.getDaXoa())) {
            throw new RuntimeException("Không thể cập nhật trạng thái vé đã bị xóa");
        }

        // Validate trạng thái thanh toán
        if (!isValidPaymentStatus(trangThaiThanhToan)) {
            throw new RuntimeException("Trạng thái thanh toán không hợp lệ: " + trangThaiThanhToan);
        }

        // Cập nhật trạng thái thanh toán
        ticket.setTrangThaiThanhToan(trangThaiThanhToan);
        ticket.setNgayCapNhat(LocalDateTime.now());

        CinemaTicket updatedTicket = cinemaTicketRepository.save(ticket);
        return convertToResponseDto(updatedTicket);
    }

    // ========== HELPER METHODS ==========

    private boolean isValidPaymentStatus(String status) {
        return "CHO_THANH_TOAN".equals(status) ||
                "DA_THANH_TOAN".equals(status) ||
                "DA_HOAN_TIEN".equals(status);
    }

    private CinemaTicketResponseDto convertToResponseDto(CinemaTicket ticket) {
        CinemaTicketResponseDto dto = new CinemaTicketResponseDto();
        dto.setIdVePhim(ticket.getIdVePhim());
        dto.setGiaVe(ticket.getGiaVe());
        dto.setNgayDat(ticket.getNgayDat());
        dto.setTrangThai(ticket.getTrangThai());
//        dto.setGiaVe(ticket.getGiaVe());
//        dto.setNgayTao(ticket.getNgayTao());
//        dto.setNgayCapNhat(ticket.getNgayCapNhat());
//        dto.setGhiChu(ticket.getGhiChu());
//        dto.setTrangThaiThanhToan(ticket.getTrangThaiThanhToan());
//        dto.setDaXoa(ticket.getDaXoa());

        // Map các entity liên quan
        if (ticket.getSuatChieu() != null) {
            dto.setTenSuatChieu(ticket.getSuatChieu().getTenSuatChieu());
            dto.setNgayChieu(ticket.getSuatChieu().getNgayChieu());
            dto.setGioChieu(ticket.getSuatChieu().getThoiGianBatDau());
        }

        if (ticket.getKhachHang() != null) {
            dto.setTenKhachHang(ticket.getKhachHang().getTenKhachHang());
            dto.setSoDienThoaiKhachHang(ticket.getKhachHang().getSoDienThoaiKhachHang());
        }

        if (ticket.getNhanVien() != null) {
            dto.setTenNhanVien(ticket.getNhanVien().getTenNhanVien());
        }

        if (ticket.getGheNgoi() != null) {
            dto.setSoGhe(ticket.getGheNgoi().getSoGhe());
        }

        if (ticket.getPhongChieu() != null) {
            dto.setTenPhongChieu(ticket.getPhongChieu().getTenPhongChieu());
        }

        if (ticket.getPhim() != null) {
            dto.setTenPhim(ticket.getPhim().getTenPhim());
        }

//        if (ticket.getRapChieu() != null) {
//            dto.setTenRapChieu(ticket.getRapChieu().getTenRapChieu());
//        }

        return dto;
    }

    private CinemaTicket convertToEntity(CinemaTicketRequestDto dto) {
        // Implementation tùy thuộc vào cấu trúc DTO
        // Đây là placeholder - cần implement theo cấu trúc thực tế
        CinemaTicket ticket = new CinemaTicket();
        // Map các field từ DTO sang Entity
        return ticket;
    }

}
