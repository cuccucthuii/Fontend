package org.example.cinema_reservation_system.controller.showtime;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.showtimedto.*;
import org.example.cinema_reservation_system.service.showtime.ShowTimeService;
import org.example.cinema_reservation_system.utils.enums.TrangThaiSuatChieu;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.entity.Seat;
import org.example.cinema_reservation_system.utils.enums.TrangThaiDatVe;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/suat-chieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ShowTimeController {
    private final ShowTimeService showTimeService;
    private final InvoiceRepository invoiceRepository;

    @GetMapping
    public Page<ShowTimeSummaryDTO> getAll(Pageable pageable) {
        return showTimeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ShowTimeResponseDTO getById(@PathVariable Integer id) {
        return showTimeService.findById(id);
    }

    @PostMapping
    public ShowTimeResponseDTO create(@Valid @RequestBody ShowTimeRequestDTO requestDTO) {
        return showTimeService.create(requestDTO);
    }

    @PostMapping("/bulk")
    public java.util.List<ShowTimeResponseDTO> createBulk(@Valid @RequestBody java.util.List<ShowTimeRequestDTO> requests) {
        return showTimeService.createBulk(requests);
    }

    @PutMapping("/{id}")
    public ShowTimeResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ShowTimeRequestDTO requestDTO) {
        return showTimeService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        showTimeService.delete(id);
    }

    @PutMapping("/status")
    public ShowTimeResponseDTO updateStatus(@Valid @RequestBody ShowTimeStatusUpdateDTO statusUpdateDTO) {
        return showTimeService.updateStatus(statusUpdateDTO);
    }

    @PostMapping("/{id}/activate")
    public void activate(@PathVariable Integer id) { 
        showTimeService.activateShowtime(id); 
    }
    
    @PostMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Integer id) { 
        showTimeService.deactivateShowtime(id); 
    }

    @GetMapping("/phim/{phimId}")
    public List<ShowTimeSummaryDTO> getByPhim(@PathVariable Integer phimId) {
        return showTimeService.findByPhimId(phimId);
    }

    @GetMapping("/phim/{phimId}/rap/{rapId}")
    public java.util.List<ShowTimeSummaryDTO> getByPhimAndRap(@PathVariable Integer phimId, @PathVariable Integer rapId) {
        return showTimeService.findByPhimAndRap(phimId, rapId);
    }

    @GetMapping("/phong/{phongId}")
    public List<ShowTimeSummaryDTO> getByPhong(@PathVariable Integer phongId) {
        return showTimeService.findByPhongChieuId(phongId);
    }

    @GetMapping("/ngay/{ngay}")
    public List<ShowTimeSummaryDTO> getByDate(@PathVariable LocalDate ngay) {
        return showTimeService.findByNgayChieu(ngay);
    }

    @GetMapping("/available")
    public List<ShowTimeSummaryDTO> getAvailable() {
        return showTimeService.findAvailableShowtimes();
    }

    @GetMapping("/search")
    public Page<ShowTimeSummaryDTO> search(
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) LocalDate fromDate,
            @RequestParam(required=false) LocalDate toDate,
            @RequestParam(required=false) TrangThaiSuatChieu status,
            Pageable pageable) {
        return showTimeService.searchShowtimes(keyword, fromDate, toDate, status, pageable);
    }

    // ====================== NEW ENDPOINTS ==========================

    @GetMapping("/today")
    public List<ShowTimeSummaryDTO> getTodayShowtimes() {
        return showTimeService.findTodayShowtimes();
    }

    @GetMapping("/upcoming/{days}")
    public List<ShowTimeSummaryDTO> getUpcomingShowtimes(@PathVariable int days) {
        return showTimeService.findUpcomingShowtimes(days);
    }

    @GetMapping("/phim/{phimId}/date-range")
    public List<ShowTimeSummaryDTO> getByPhimAndDateRange(
            @PathVariable Integer phimId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return showTimeService.findShowtimesByPhimAndDateRange(phimId, startDate, endDate);
    }

    @GetMapping("/phim/{phimId}/available")
    public List<ShowTimeSummaryDTO> getAvailableByPhimAndDate(
            @PathVariable Integer phimId,
            @RequestParam LocalDate ngayChieu) {
        return showTimeService.findAvailableShowtimesByPhimAndDate(phimId, ngayChieu);
    }

    @GetMapping("/phim/{phimId}/active")
    public List<ShowTimeSummaryDTO> getActiveByPhim(@PathVariable Integer phimId) {
        return showTimeService.findActiveShowtimesByPhimId(phimId);
    }

    @GetMapping("/conflict-check")
    public boolean checkTimeConflict(
            @RequestParam Integer phongChieuId,
            @RequestParam LocalDate ngayChieu,
            @RequestParam LocalTime thoiGianBatDau,
            @RequestParam LocalTime thoiGianKetThuc) {
        return !showTimeService.isTimeSlotAvailable(phongChieuId, ngayChieu, thoiGianBatDau, thoiGianKetThuc);
    }

    @GetMapping("/stats")
    public Object getShowtimeStats() {
        return Map.of(
            "totalShowtimes", showTimeService.countByTrangThai(null),
            "activeShowtimes", showTimeService.countByTrangThai(TrangThaiSuatChieu.DA_LEN_LICH),
            "cancelledShowtimes", showTimeService.countByTrangThai(TrangThaiSuatChieu.DA_HUY),
            "completedShowtimes", showTimeService.countByTrangThai(TrangThaiSuatChieu.HOAN_THANH)
        );
    }

    @PostMapping("/bulk-create")
    public List<ShowTimeResponseDTO> createBulkShowtimes(@Valid @RequestBody List<ShowTimeRequestDTO> requestDTOs) {
        return requestDTOs.stream()
                .map(showTimeService::create)
                .collect(Collectors.toList());
    }

    /**
     * Tạo nhiều suất chiếu tự động theo pattern lặp lại
     * Hỗ trợ: DAILY, WEEKLY, WEEKDAYS, WEEKENDS, CUSTOM, ALTERNATE_DAYS, ALTERNATE_WEEKS
     */

    @PostMapping("/{id}/duplicate")
    public ShowTimeResponseDTO duplicateShowtime(@PathVariable Integer id, @RequestParam LocalDate newDate) {
        ShowTimeResponseDTO original = showTimeService.findById(id);
        ShowTimeRequestDTO duplicateRequest = new ShowTimeRequestDTO();
        // Copy properties from original
        duplicateRequest.setTenSuatChieu(original.getTenSuatChieu() + " (Copy)");
        duplicateRequest.setNgayChieu(newDate);
        // duplicateRequest.setGioChieu(original.getGioChieu()); // Field gioChieu đã bị comment out trong entity
        duplicateRequest.setThoiGianBatDau(original.getThoiGianBatDau());
        duplicateRequest.setThoiGianKetThuc(original.getThoiGianKetThuc());
        duplicateRequest.setTrangThai(original.getTrangThai());
        duplicateRequest.setIdPhim(original.getIdPhim());
        duplicateRequest.setIdPhongChieu(original.getIdPhongChieu());
        duplicateRequest.setGiaVe(original.getGiaVe());
        duplicateRequest.setGhiChu("Được tạo từ suất chiếu ID: " + id);
        
        return showTimeService.create(duplicateRequest);
    }

    @GetMapping("/{showtimeId}/disabled-seats")
    public ResponseEntity<Set<Integer>> getDisabledSeats(@PathVariable Integer showtimeId) {
        // Tạm thời: chưa có Ticket entity, trả về rỗng để không khóa ghế
        return ResponseEntity.ok(java.util.Collections.emptySet());

    }

    @GetMapping("/by-movie-cinema")
    public ResponseEntity<ShowTimeResDTO> getShowtimes(
            @RequestParam("movieId") Long movieId,
            @RequestParam("cinemaId") Long cinemaId) {

        try {
            ShowTimeResDTO response = showTimeService.getShowtimes(movieId, cinemaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getShowtimes endpoint", e);
            ShowTimeResDTO errorResponse = new ShowTimeResDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Endpoint test để kiểm tra dữ liệu
    @GetMapping("/test-data")
    public ResponseEntity<Map<String, Object>> testData() {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // Đếm số lượng records
            long movieCount = showTimeService.countMovies();
            long showtimeCount = showTimeService.countShowtimes();
            long cinemaCount = showTimeService.countCinemas();
            
            result.put("success", true);
            result.put("movieCount", movieCount);
            result.put("showtimeCount", showtimeCount);
            result.put("cinemaCount", cinemaCount);
            result.put("message", "Dữ liệu test thành công");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in test-data endpoint", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint để tạo dữ liệu mẫu
    @PostMapping("/create-sample-data")
    public ResponseEntity<Map<String, Object>> createSampleData() {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            int createdCount = showTimeService.createSampleShowtimes();
            
            result.put("success", true);
            result.put("createdCount", createdCount);
            result.put("message", "Đã tạo " + createdCount + " suất chiếu mẫu");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating sample data", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint test query getShowtimeData
    @GetMapping("/test-query")
    public ResponseEntity<Map<String, Object>> testQuery(
            @RequestParam(defaultValue = "1") Long movieId,
            @RequestParam(defaultValue = "1") Long cinemaId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            log.info("Testing query with movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            // Test query trực tiếp
            Map<String, Object> queryResult = showTimeService.testGetShowtimeData(movieId, cinemaId);
            
            result.put("success", true);
            result.put("movieId", movieId);
            result.put("cinemaId", cinemaId);
            result.put("queryResult", queryResult);
            result.put("message", "Query test thành công");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error testing query", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint test fallback method
    @GetMapping("/test-fallback")
    public ResponseEntity<Map<String, Object>> testFallback(
            @RequestParam(defaultValue = "1") Long movieId,
            @RequestParam(defaultValue = "2") Long cinemaId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            log.info("Testing fallback with movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            List<ShowTimeSummaryDTO> summaries = showTimeService.findByPhimAndRap(movieId.intValue(), cinemaId.intValue());
            
            result.put("success", true);
            result.put("movieId", movieId);
            result.put("cinemaId", cinemaId);
            result.put("showtimeCount", summaries.size());
            result.put("showtimes", summaries);
            result.put("message", "Fallback test thành công");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error testing fallback", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint test count query
    @GetMapping("/test-count")
    public ResponseEntity<Map<String, Object>> testCount(
            @RequestParam(defaultValue = "1") Long movieId,
            @RequestParam(defaultValue = "2") Long cinemaId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            log.info("Testing count with movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            Long count = showTimeService.countShowtimesByMovieAndCinema(movieId, cinemaId);
            
            result.put("success", true);
            result.put("movieId", movieId);
            result.put("cinemaId", cinemaId);
            result.put("count", count);
            result.put("message", "Count test thành công");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error testing count", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint test JSON parsing
    @GetMapping("/test-json-parsing")
    public ResponseEntity<Map<String, Object>> testJsonParsing(
            @RequestParam(defaultValue = "1") Long movieId,
            @RequestParam(defaultValue = "2") Long cinemaId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            log.info("Testing JSON parsing with movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            // Test JSON parsing trực tiếp
            ShowTimeResDTO response = showTimeService.getShowtimes(movieId, cinemaId);
            
            result.put("success", true);
            result.put("movieId", movieId);
            result.put("cinemaId", cinemaId);
            result.put("response", response);
            result.put("message", "JSON parsing test thành công");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error testing JSON parsing", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("stackTrace", e.getStackTrace());
            return ResponseEntity.status(500).body(result);
        }
    }

    // Endpoint debug JSON parsing chi tiết
    @GetMapping("/debug-json-parsing")
    public ResponseEntity<Map<String, Object>> debugJsonParsing(
            @RequestParam(defaultValue = "1") Long movieId,
            @RequestParam(defaultValue = "2") Long cinemaId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            log.info("Debug JSON parsing with movieId: {}, cinemaId: {}", movieId, cinemaId);
            
            // Lấy raw data từ database
            Map<String, Object> rawResult = showTimeService.testGetShowtimeData(movieId, cinemaId);
            
            result.put("success", true);
            result.put("movieId", movieId);
            result.put("cinemaId", cinemaId);
            result.put("rawResult", rawResult);
            
            // Test JSON parsing từng bước
            String showtimesJson = (String) rawResult.get("showtimes");
            if (showtimesJson != null && !showtimesJson.equals("null")) {
                try {
                    // Test parse JSON
                    ObjectMapper mapper = new ObjectMapper();
                    List<Map<String, Object>> showtimesList = mapper.readValue(showtimesJson, 
                        new TypeReference<List<Map<String, Object>>>() {});
                    
                    result.put("jsonParsingSuccess", true);
                    result.put("showtimesCount", showtimesList.size());
                    result.put("firstShowtime", showtimesList.isEmpty() ? null : showtimesList.get(0));
                    
                } catch (Exception e) {
                    result.put("jsonParsingSuccess", false);
                    result.put("jsonParsingError", e.getMessage());
                }
            } else {
                result.put("jsonParsingSuccess", false);
                result.put("jsonParsingError", "showtimesJson is null or 'null'");
            }
            
            result.put("message", "Debug JSON parsing hoàn thành");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error debugging JSON parsing", e);
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(result);
        }
    }
}