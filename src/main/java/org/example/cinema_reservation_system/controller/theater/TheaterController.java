package org.example.cinema_reservation_system.controller.theater;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.theaterdto.TheaterRequestDto;
import org.example.cinema_reservation_system.dto.theaterdto.TheaterResponseDto;
import org.example.cinema_reservation_system.service.utility.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rap_chieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TheaterController {

    private final CinemaService service;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TheaterRequestDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody TheaterRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xoá thành công rạp chiếu có ID: "+id);
    }

    // Tìm kiếm
 //   GET http://localhost:8080/api/rap_chieu/search?keyword=CGV&trangThai=HOAT_DONG&diaChi=Hà Nội
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String diaChi,
            Pageable pageable
    ) {
        // Backward-compatible: if pageable not provided, return non-paged search
        if (pageable == null) {
            List<TheaterResponseDto> result = service.search(keyword, trangThai, diaChi);
            return result.isEmpty()
                    ? ResponseEntity.status(204).body("Không tìm thấy rạp chiếu phù hợp.")
                    : ResponseEntity.ok(result);
        }
        Page<TheaterResponseDto> page = service.searchPaged(keyword, trangThai, null, null, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search-advanced")
    public ResponseEntity<Page<TheaterResponseDto>> searchAdvanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String thanhPho,
            @RequestParam(required = false) String khuVuc,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.searchPaged(keyword, trangThai, thanhPho, khuVuc, pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        List<TheaterResponseDto> all = service.findAll();
        List<TheaterResponseDto> active = all.stream()
                .filter(t -> t.getTrangThaiRapChieu() != null && t.getTrangThaiRapChieu().name().equalsIgnoreCase("HOAT_DONG"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(active);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        List<TheaterResponseDto> all = service.findAll();
        long total = all.size();
        long active = all.stream().filter(t -> t.getTrangThaiRapChieu() != null && t.getTrangThaiRapChieu().name().equalsIgnoreCase("HOAT_DONG")).count();
        long inactive = all.stream().filter(t -> t.getTrangThaiRapChieu() != null && !t.getTrangThaiRapChieu().name().equalsIgnoreCase("HOAT_DONG")).count();
        Map<String, Object> body = Map.of(
                "total", total,
                "active", active,
                "inactive", inactive
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/sync-counts")
    public ResponseEntity<TheaterResponseDto> syncCounts(@PathVariable Integer id) {
        return ResponseEntity.ok(service.syncCounts(id));
    }

    @PostMapping("/sync-counts-all")
    public ResponseEntity<List<TheaterResponseDto>> syncAllCounts() {
        return ResponseEntity.ok(service.syncAllCounts());
    }


    // ========== Nearby theaters ==========
    @GetMapping("/nearby")
    public ResponseEntity<List<TheaterResponseDto>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "20") double radiusKm,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<TheaterResponseDto> all = service.findAll();
        // Lọc rạp có lat/lng hợp lệ
        List<TheaterResponseDto> withGeo = all.stream()
                .filter(t -> t.getLatitude() != null && t.getLongitude() != null)
                .toList();

        // Tính khoảng cách Haversine
        List<TheaterResponseDto> result = withGeo.stream()
                .map(t -> {
                    double d = haversineKm(lat, lng, t.getLatitude(), t.getLongitude());
                    t.setDistanceKm(d);
                    return t;
                })
                .filter(t -> t.getDistanceKm() != null && t.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(t -> t.getDistanceKm()))
                .limit(Math.max(1, limit))
                .toList();

        return ResponseEntity.ok(result);
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // bán kính Trái Đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
