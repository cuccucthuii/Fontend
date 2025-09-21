package org.example.cinema_reservation_system.service.showtime.impl;

// Service triển khai các chức năng xử lý logic cho Suất Chiếu
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.showtimedto.*;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.mapper.showtime.ShowTimeModelMapper;
import org.example.cinema_reservation_system.repository.movie.MovieRepository;
import org.example.cinema_reservation_system.repository.room.RoomRepository;
import org.example.cinema_reservation_system.repository.showtime.ShowTimeRepository;
import org.example.cinema_reservation_system.service.showtime.ShowTimeService;
import org.example.cinema_reservation_system.utils.enums.TrangThaiSuatChieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShowTimeServiceImpl implements ShowTimeService {

    private final ShowTimeRepository repo;
    private final MovieRepository phimRepo;
    private final RoomRepository phongRepo;
    private final org.example.cinema_reservation_system.repository.seat.SeatRepository seatRepository;
    private final ShowTimeModelMapper mapper;

    // Tạo mới một suất chiếu
    @Override
    public ShowTimeResponseDTO create(ShowTimeRequestDTO req) {
        validateShowtimeSchedule(req); // kiểm tra giờ bắt đầu và ngày chiếu hợp lệ
        validateTimeConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getThoiGianBatDau(), req.getThoiGianKetThuc(), null); // kiểm tra xung đột lịch
        ShowTime e = mapper.toEntity(req);
        e.setPhim(phimRepo.findById(req.getIdPhim()).orElseThrow(() -> new BusinessException("error.not_found")));
        e.setPhongChieu(phongRepo.findById(req.getIdPhongChieu()).orElseThrow(() -> new BusinessException("error.not_found")));
        // map trạng thái từ TrangThaiSuatChieu (DTO) sang TrangThai (entity)
        if (req.getTrangThai() != null) {
            e.setTrangThai(mapTrangThai(req.getTrangThai()));
        }
        return mapper.toResponse(repo.save(e));
    }

    // Tạo nhiều suất chiếu cùng lúc và tự động set số ghế theo layout
    @Override
    public java.util.List<ShowTimeResponseDTO> createBulk(java.util.List<ShowTimeRequestDTO> requests) {
        if (requests == null || requests.isEmpty()) return java.util.List.of();
        java.util.List<ShowTime> toSave = new java.util.ArrayList<>();
        for (ShowTimeRequestDTO req : requests) {
            validateShowtimeSchedule(req);
            validateTimeConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getThoiGianBatDau(), req.getThoiGianKetThuc(), null);
            ShowTime e = mapper.toEntity(req);
            e.setPhim(phimRepo.findById(req.getIdPhim()).orElseThrow(() -> new BusinessException("error.not_found")));
            e.setPhongChieu(phongRepo.findById(req.getIdPhongChieu()).orElseThrow(() -> new BusinessException("error.not_found")));
            if (req.getTrangThai() != null) {
                e.setTrangThai(mapTrangThai(req.getTrangThai()));
            }
            // set ghế theo layout phòng
            long tong = seatRepository.countByPhongChieuIdPhongChieu(req.getIdPhongChieu());
            long trong = seatRepository.countByPhongChieuIdPhongChieuAndTrangThai(req.getIdPhongChieu(), org.example.cinema_reservation_system.utils.enums.TrangThai.CON_TRONG);
            e.setTongSoGhe((int) tong);
            e.setSoGheConTrong((int) trong);
            toSave.add(e);
        }
        return repo.saveAll(toSave).stream().map(mapper::toResponse).collect(java.util.stream.Collectors.toList());
    }

    // Cập nhật suất chiếu nếu chưa quá thời gian
    @Override
    public ShowTimeResponseDTO update(Integer id, ShowTimeRequestDTO req) {
        ShowTime exist = repo.findById(id).orElseThrow(() -> new BusinessException("error.showtime.not_found"));
        if (!canUpdateShowtime(id)) throw new BusinessException("error.conflict");
        validateShowtimeSchedule(req);
        validateTimeConflict(req.getIdPhongChieu(), req.getNgayChieu(), req.getThoiGianBatDau(), req.getThoiGianKetThuc(), id);
        exist.setTenSuatChieu(req.getTenSuatChieu());
        exist.setNgayChieu(req.getNgayChieu());
        exist.setThoiGianBatDau(req.getThoiGianBatDau());
        exist.setThoiGianKetThuc(req.getThoiGianKetThuc());
        if (req.getTrangThai() != null) {
            exist.setTrangThai(mapTrangThai(req.getTrangThai()));
        }
        return mapper.toResponse(repo.save(exist));
    }

    // Xóa suất chiếu nếu chưa diễn ra
    @Override
    public void delete(Integer id) {
        if (!canDeleteShowtime(id)) throw new BusinessException("error.theater.cannot_delete");
        repo.deleteById(id);
    }

    // Tìm theo ID suất chiếu
    @Override
    public ShowTimeResponseDTO findById(Integer id) {
        return mapper.toResponse(repo.findById(id).orElseThrow(() -> new BusinessException("error.showtime.not_found")));
    }

    // Lấy tất cả suất chiếu phân trang
    @Override
    public Page<ShowTimeSummaryDTO> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toSummary);
    }

    // Cập nhật trạng thái suất chiếu (ví dụ: huỷ, đã lên lịch)
    @Override
    public ShowTimeResponseDTO updateStatus(ShowTimeStatusUpdateDTO dto) {
        ShowTime e = repo.findById(dto.getIdSuatChieu()).orElseThrow(() -> new BusinessException("error.showtime.not_found"));
        e.setTrangThai(mapTrangThai(dto.getTrangThai()));
        return mapper.toResponse(repo.save(e));
    }

    // Kích hoạt suất chiếu
    @Override
    public void activateShowtime(Integer id) {
        updateStatus(new ShowTimeStatusUpdateDTO(id, TrangThaiSuatChieu.DA_LEN_LICH));
    }

    // Hủy suất chiếu
    @Override
    public void deactivateShowtime(Integer id) {
        updateStatus(new ShowTimeStatusUpdateDTO(id, TrangThaiSuatChieu.DA_HUY));
    }

    // Lấy suất chiếu theo phim
    @Override
    public List<ShowTimeSummaryDTO> findByPhimId(Integer phimId) {
        return repo.findByPhim_IdPhim(phimId).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public Page<ShowTimeSummaryDTO> findByPhimId(Integer phimId, Pageable pageable) {
        return repo.findByPhim_IdPhim(phimId, pageable).map(mapper::toSummary);
    }

    // Lấy suất chiếu theo phòng
    @Override
    public List<ShowTimeSummaryDTO> findByPhongChieuId(Integer phongId) {
        return repo.findByPhongChieu_IdPhongChieu(phongId).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public Page<ShowTimeSummaryDTO> findByPhongChieuId(Integer phongId, Pageable pageable) {
        return repo.findByPhongChieu_IdPhongChieu(phongId, pageable).map(mapper::toSummary);
    }

    // Lấy suất chiếu theo ngày chiếu
    @Override
    public List<ShowTimeSummaryDTO> findByNgayChieu(LocalDate ngayChieu) {
        return repo.findByNgayChieu(ngayChieu).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public Page<ShowTimeSummaryDTO> findByNgayChieu(LocalDate ngayChieu, Pageable pageable) {
        return repo.findByNgayChieu(ngayChieu, pageable).map(mapper::toSummary);
    }

    @Override
    public List<ShowTimeSummaryDTO> findByNgayChieuBetween(LocalDate startDate, LocalDate endDate) {
        return repo.findByNgayChieuBetween(startDate, endDate).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Lấy theo trạng thái chiếu
    @Override
    public List<ShowTimeSummaryDTO> findByTrangThai(TrangThaiSuatChieu trangThai) {
        return repo.findByTrangThai(mapTrangThai(trangThai)).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public Page<ShowTimeSummaryDTO> findByTrangThai(TrangThaiSuatChieu trangThai, Pageable pageable) {
        return repo.findByTrangThai(mapTrangThai(trangThai), pageable).map(mapper::toSummary);
    }

    // Suất chiếu có thể đặt (đã lên lịch, từ hôm nay trở đi)
    @Override
    public List<ShowTimeSummaryDTO> findAvailableShowtimes() {
        return repo.findAvailableShowtimes(LocalDate.now()).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Suất chiếu khả dụng theo phim + ngày
    @Override
    public List<ShowTimeSummaryDTO> findAvailableShowtimesByPhimAndDate(Integer phimId, LocalDate ngayChieu) {
        return repo.findAvailableByPhimAndDate(phimId, ngayChieu).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Lấy lịch chiếu phim trong khoảng ngày
    @Override
    public List<ShowTimeSummaryDTO> findShowtimesByPhimAndDateRange(Integer phimId, LocalDate startDate, LocalDate endDate) {
        return repo.findByPhimAndNgayChieuBetween(phimId, startDate, endDate).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Suất chiếu đang hoạt động theo phim
    @Override
    public List<ShowTimeSummaryDTO> findActiveShowtimesByPhimId(Integer phimId) {
        return repo.findActiveShowtimesByPhimId(phimId).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public List<ShowTimeSummaryDTO> findByPhimAndRap(Integer phimId, Integer rapId) {
        return repo.findByPhimIdAndRapId(phimId, rapId).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Kiểm tra xem suất chiếu có thể xóa hay không
    @Override
    public boolean canDeleteShowtime(Integer id) {
        ShowTime e = repo.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy"));
        return !e.getNgayChieu().isBefore(LocalDate.now());
    }

    // Kiểm tra xem suất chiếu có thể cập nhật hay không
    @Override
    public boolean canUpdateShowtime(Integer id) {
        ShowTime e = repo.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy"));
        return !e.getNgayChieu().isBefore(LocalDate.now());
    }

    // Kiểm tra logic thời gian suất chiếu
    @Override
    public void validateShowtimeSchedule(ShowTimeRequestDTO req) {
        if (req.getThoiGianBatDau().isAfter(req.getThoiGianKetThuc()))
            throw new BusinessException("Start time must be before end time");
        if (req.getNgayChieu().isBefore(LocalDate.now()))
            throw new BusinessException("Ngày chiếu không được trong quá khứ");
    }

    // Kiểm tra có trùng khung giờ trong cùng phòng không
    @Override
    public void validateTimeConflict(Integer phongChieuId, LocalDate ngay, LocalTime start, LocalTime end, Integer excludeId) {
        List<ShowTime> conflicts = repo.findTimeConflicts(phongChieuId, ngay, start, end);
        if (excludeId != null) {
            conflicts.removeIf(sc -> sc.getIdSuatChieu().equals(excludeId));
        }
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Xung đột lịch chiếu");
        }
    }

    // Thống kê
    @Override
    public long countByPhimId(Integer phimId) {
        return repo.countByPhim_IdPhim(phimId);
    }

    @Override
    public long countByPhongChieuId(Integer phongChieuId) {
        return repo.countByPhongChieu_IdPhongChieu(phongChieuId);
    }

    @Override
    public long countByNgayChieu(LocalDate ngayChieu) {
        return repo.countByNgayChieu(ngayChieu);
    }

    @Override
    public long countByTrangThai(TrangThaiSuatChieu trangThai) {
        return repo.countByTrangThai(mapTrangThai(trangThai));
    }

    // Lấy các suất chiếu sắp tới trong N ngày
    @Override
    public List<ShowTimeSummaryDTO> findUpcomingShowtimes(int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);
        return repo.findByNgayChieuBetween(today, end).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Lấy suất chiếu hôm nay
    @Override
    public List<ShowTimeSummaryDTO> findTodayShowtimes() {
        return repo.findByNgayChieu(LocalDate.now()).stream().map(mapper::toSummary).collect(Collectors.toList());
    }

    // Hủy các suất chiếu đã qua ngày chiếu
    @Override
    public void expireOldShowtimes() {
        List<ShowTime> expired = repo.findByNgayChieuBefore(LocalDate.now());
        expired.forEach(sc -> sc.setTrangThai(TrangThai.KET_THUC));
        repo.saveAll(expired);
    }

    // Kiểm tra khung giờ khả dụng khi tạo mới
    @Override
    public boolean isTimeSlotAvailable(Integer roomId, LocalDate ngay, LocalTime start, LocalTime end) {
        return isTimeSlotAvailableForUpdate(null, roomId, ngay, start, end);
    }

    // Kiểm tra khung giờ khả dụng khi cập nhật (bỏ qua chính nó)
    @Override
    public boolean isTimeSlotAvailableForUpdate(Integer id, Integer roomId, LocalDate ngay, LocalTime start, LocalTime end) {
        List<ShowTime> c = repo.findTimeConflicts(roomId, ngay, start, end);
        if (id != null) c.removeIf(sc -> sc.getIdSuatChieu().equals(id));
        return c.isEmpty();
    }

    // Tìm kiếm nâng cao theo từ khóa, ngày và trạng thái
    @Override
    public Page<ShowTimeSummaryDTO> searchShowtimes(String keyword, LocalDate from, LocalDate to, TrangThaiSuatChieu t, Pageable pageable) {
        return repo.searchShowtimes(keyword, from, to, mapTrangThai(t), pageable).map(mapper::toSummary);
    }

    // helper chuyển đổi trạng thái
    private TrangThai mapTrangThai(TrangThaiSuatChieu suatChieu) {
        return switch (suatChieu) {
            case DA_LEN_LICH -> TrangThai.SAP_CHIEU;
            case DANG_CHIEU -> TrangThai.DANG_CHIEU;
            case HOAN_THANH -> TrangThai.KET_THUC;
            case DA_HUY -> TrangThai.DA_HUY;
//            case TAM_HOAN -> TrangThai.TAM_DUNG;
//            case HUY -> TrangThai.DA_HUY;
//            case CHUAN_BI -> TrangThai.SAP_CHIEU;
//            case DA_KET_THUC -> TrangThai.KET_THUC;
        };
    }

    @Override
    public ShowTimeResDTO getShowtimes(Long movieId, Long cinemaId) {
        try {
            log.info("Getting showtimes for movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            ShowTimeResDTO response = new ShowTimeResDTO();
            response.setSuccess(true);
            response.setMessage("Lấy dữ liệu suất chiếu thành công");
            
            // Tạo DataContent
            ShowTimeResDTO.DataContent data = new ShowTimeResDTO.DataContent();
            
            // Sử dụng endpoint cũ để lấy dữ liệu (đã hoạt động)
            List<ShowTime> showTimeEntities = repo.findByPhimIdAndRapId(movieId.intValue(), cinemaId.intValue());
            log.info("Found {} showtime entities", showTimeEntities.size());
            
            if (showTimeEntities.isEmpty()) {
                log.warn("No showtime data found for movieId: {}, cinemaId: {}", movieId, cinemaId);
                response.setSuccess(false);
                response.setMessage("Không tìm thấy suất chiếu");
                return response;
            }
            
            // Tạo danh sách ngày có suất chiếu
            Set<String> uniqueDates = new HashSet<>();
            List<ShowTimeResDTO.ShowtimeInfo> showtimes = new ArrayList<>();
            
            for (ShowTime entity : showTimeEntities) {
                // Thêm ngày vào set
                uniqueDates.add(entity.getNgayChieu().toString());
                
                // Tạo showtime info
                ShowTimeResDTO.ShowtimeInfo showtime = new ShowTimeResDTO.ShowtimeInfo();
                showtime.setIdSuatChieu(entity.getIdSuatChieu().longValue());
                showtime.setNgayChieu(entity.getNgayChieu().toString());
                showtime.setStartTime(entity.getThoiGianBatDau() != null ? 
                    entity.getThoiGianBatDau().toString() : "09:00");
                showtime.setEndTime(entity.getThoiGianKetThuc() != null ? 
                    entity.getThoiGianKetThuc().toString() : "11:00");
                showtime.setAvailableSeats(entity.getSoGheConTrong() != null ? 
                    entity.getSoGheConTrong() : 45);
                showtime.setTotalSeats(entity.getTongSoGhe() != null ? 
                    entity.getTongSoGhe() : 50);
                showtime.setSoldOut(entity.getSoGheConTrong() != null && 
                    entity.getSoGheConTrong() == 0);
                showtime.setFormat("2D PHỤ ĐỀ");
                showtime.setMovieId(movieId);
                showtime.setTenPhim(entity.getPhim().getTenPhim());
                showtime.setCinemaId(cinemaId);
                   showtime.setCinemaName(entity.getPhongChieu().getRapChieu().getTenRapChieu());
                   
                   // Thông tin phòng chiếu
                   showtime.setRoomId(entity.getPhongChieu().getIdPhongChieu().longValue());
                   showtime.setRoomName(entity.getPhongChieu().getTenPhongChieu());

                   // Lấy poster URL từ phim
                String posterUrl = null;
                try {
                    // Tạm thời set poster URL mặc định dựa trên tên phim
                    if ("Nhà Bà Nữ".equals(entity.getPhim().getTenPhim())) {
                        posterUrl = "http://localhost:8081/uploads/posters/poster_nha_ba_nu.jpg";
                    } else {
                        // Có thể mở rộng cho các phim khác
                        posterUrl = "http://localhost:8081/uploads/posters/default_poster.jpg";
                    }
                } catch (Exception e) {
                    log.warn("Could not get poster URL for movie: {}", entity.getPhim().getTenPhim());
                }
                showtime.setPosterUrl(posterUrl);
                showtimes.add(showtime);
            }
            
            // Tạo danh sách available dates và sắp xếp theo thứ tự thời gian
            List<ShowTimeResDTO.AvailableDate> availableDates = new ArrayList<>();
            
            // Chuyển Set thành List và sắp xếp theo thứ tự thời gian
            List<String> sortedDates = new ArrayList<>(uniqueDates);
            sortedDates.sort((date1, date2) -> {
                LocalDate d1 = LocalDate.parse(date1);
                LocalDate d2 = LocalDate.parse(date2);
                return d1.compareTo(d2);
            });
            
            for (String dateStr : sortedDates) {
                LocalDate date = LocalDate.parse(dateStr);
                ShowTimeResDTO.AvailableDate availableDate = new ShowTimeResDTO.AvailableDate();
                availableDate.setValue(dateStr);
                availableDate.setDay(date.getDayOfMonth());
                availableDate.setMonth("/" + String.format("%02d", date.getMonthValue()));
                availableDate.setWeekday(date.getDayOfWeek().toString().substring(0, 3));
                availableDates.add(availableDate);
            }
            
            // Sắp xếp suất chiếu theo ngày và giờ
            showtimes.sort((s1, s2) -> {
                // Sắp xếp theo ngày trước
                int dateCompare = s1.getDate().compareTo(s2.getDate());
                if (dateCompare != 0) {
                    return dateCompare;
                }
                // Nếu cùng ngày, sắp xếp theo giờ bắt đầu
                return s1.getTime().compareTo(s2.getTime());
            });
            
            data.setAvailableDates(availableDates);
            data.setShowtimes(showtimes);
            response.setData(data);
            
            log.info("Successfully retrieved showtimes: {} dates, {} showtimes", 
                    availableDates.size(), showtimes.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error in getShowtimes method", e);
            ShowTimeResDTO errorResponse = new ShowTimeResDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public long countMovies() {
        return 0;
    }

    @Override
    public long countShowtimes() {
        return 0;
    }

    @Override
    public long countCinemas() {
        return 0;
    }

    @Override
    public int createSampleShowtimes() {
        return 0;
    }

    @Override
    public Map<String, Object> testGetShowtimeData(Long movieId, Long cinemaId) {
        return repo.getShowtimeData(movieId, cinemaId);
    }

    @Override
    public Long countShowtimesByMovieAndCinema(Long movieId, Long cinemaId) {
        return repo.countShowtimesByMovieAndCinema(movieId, cinemaId);
    }
}