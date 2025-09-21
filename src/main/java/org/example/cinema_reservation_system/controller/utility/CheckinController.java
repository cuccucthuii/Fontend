package org.example.cinema_reservation_system.controller.utility;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.checkindto.CheckinRequestDto;
import org.example.cinema_reservation_system.dto.checkindto.CheckinResponseDto;
import org.example.cinema_reservation_system.service.utility.CheckinService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CheckinController {

    private final CheckinService checkinService;

    // Check-in vé
    @PostMapping("/ve")
    public ResponseEntity<CheckinResponseDto> checkinVe(@Valid @RequestBody CheckinRequestDto request) {
        CheckinResponseDto response = checkinService.checkinVe(request);
        return ResponseEntity.ok(response);
    }

    // Hủy check-in
    @DeleteMapping("/{idCheckin}")
    public ResponseEntity<String> huyCheckin(@PathVariable Integer idCheckin) {
        checkinService.huyCheckin(idCheckin);
        return ResponseEntity.ok("Đã hủy check-in thành công");
    }

    // Lấy thông tin check-in theo ID
    @GetMapping("/{idCheckin}")
    public ResponseEntity<CheckinResponseDto> getCheckinById(@PathVariable Integer idCheckin) {
        CheckinResponseDto response = checkinService.getCheckinById(idCheckin);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách check-in theo ngày
    @GetMapping("/ngay")
    public ResponseEntity<List<CheckinResponseDto>> getCheckinByNgay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngay) {
        List<CheckinResponseDto> response = checkinService.getCheckinByNgay(ngay);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách check-in theo rạp chiếu
    @GetMapping("/rap-chieu/{idRapChieu}")
    public ResponseEntity<List<CheckinResponseDto>> getCheckinByRapChieu(@PathVariable Integer idRapChieu) {
        List<CheckinResponseDto> response = checkinService.getCheckinByRapChieu(idRapChieu);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách check-in theo suất chiếu
    @GetMapping("/suat-chieu/{idSuatChieu}")
    public ResponseEntity<List<CheckinResponseDto>> getCheckinBySuatChieu(@PathVariable Integer idSuatChieu) {
        List<CheckinResponseDto> response = checkinService.getCheckinBySuatChieu(idSuatChieu);
        return ResponseEntity.ok(response);
    }

    // Kiểm tra vé đã được check-in chưa
    @GetMapping("/kiem-tra/{idVePhim}")
    public ResponseEntity<Boolean> isVeDaCheckin(@PathVariable Integer idVePhim) {
        boolean result = checkinService.isVeDaCheckin(idVePhim);
        return ResponseEntity.ok(result);
    }

    // Thống kê check-in theo ngày
    @GetMapping("/thong-ke/ngay")
    public ResponseEntity<Long> countCheckinByNgay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngay) {
        Long count = checkinService.countCheckinByNgay(ngay);
        return ResponseEntity.ok(count);
    }

    // Check-in bằng mã vé (QR code)
    @PostMapping("/ma-ve/{maVe}")
    public ResponseEntity<CheckinResponseDto> checkinByMaVe(
            @PathVariable String maVe,
            @RequestParam Integer idNhanVienCheckin,
            @RequestParam(required = false) String ghiChu) {
        
        // Tìm vé theo mã vé
        CheckinRequestDto request = new CheckinRequestDto();
        request.setMaVe(maVe);
        request.setIdNhanVienCheckin(idNhanVienCheckin);
        request.setGhiChu(ghiChu);
        
        CheckinResponseDto response = checkinService.checkinVeByMaVe(request);
        return ResponseEntity.ok(response);
    }
}

