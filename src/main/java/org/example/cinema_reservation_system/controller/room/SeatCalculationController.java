package org.example.cinema_reservation_system.controller.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.service.room.SeatCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/seat-calculation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class SeatCalculationController {

    private final SeatCalculationService seatCalculationService;

    /**
     * Tính toán số ghế từ diện tích phòng với công thức linh hoạt
     * 
     * @param dienTichPhong Tổng diện tích phòng (m²)
     * @param dienTichTrungBinhMoiGhe Diện tích trung bình mỗi ghế (m²/ghế)
     * @return Thông tin tính toán layout ghế
     */
    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateSeats(
            @RequestParam Double dienTichPhong,
            @RequestParam(required = false, defaultValue = "0.8") Double dienTichTrungBinhMoiGhe) {
        
        try {
            log.info("Tính toán ghế: diện tích phòng = {}m², diện tích/ghế = {}m²", 
                    dienTichPhong, dienTichTrungBinhMoiGhe);
            
            Map<String, Object> result = seatCalculationService.calculateSeatsFromArea(dienTichPhong, dienTichTrungBinhMoiGhe);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Lỗi tính toán ghế", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tính toán với giá vé tùy chỉnh
     */
    @PostMapping("/calculate-with-prices")
    public ResponseEntity<Map<String, Object>> calculateSeatsWithPrices(
            @RequestParam Double dienTichPhong,
            @RequestParam(required = false, defaultValue = "0.8") Double dienTichTrungBinhMoiGhe,
            @RequestParam(required = false, defaultValue = "50000") BigDecimal giaGheThuong,
            @RequestParam(required = false, defaultValue = "80000") BigDecimal giaGheVIP,
            @RequestParam(required = false, defaultValue = "120000") BigDecimal giaGheCouple) {
        
        try {
            // Tính toán layout
            Map<String, Object> layout = seatCalculationService.calculateSeatsFromArea(dienTichPhong, dienTichTrungBinhMoiGhe);
            
            // Tính giá vé
            Map<String, BigDecimal> giaVe = seatCalculationService.calculateSeatPrices(giaGheThuong, giaGheVIP, giaGheCouple);
            layout.put("giaVe", giaVe);
            
            // Tính doanh thu dự kiến
            @SuppressWarnings("unchecked")
            Map<String, Integer> phanLoaiGhe = (Map<String, Integer>) layout.get("phanLoaiGhe");
            BigDecimal doanhThuDuKien = seatCalculationService.calculateExpectedRevenue(phanLoaiGhe, giaVe);
            layout.put("doanhThuDuKien", doanhThuDuKien);
            
            return ResponseEntity.ok(layout);
            
        } catch (Exception e) {
            log.error("Lỗi tính toán ghế với giá vé", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tối ưu hóa layout với các tham số khác nhau
     */
    @GetMapping("/optimize")
    public ResponseEntity<Map<String, Object>> optimizeLayout(
            @RequestParam Double dienTichPhong,
            @RequestParam(required = false, defaultValue = "0.6") Double minDienTichMoiGhe,
            @RequestParam(required = false, defaultValue = "1.2") Double maxDienTichMoiGhe) {
        
        try {
            log.info("Tối ưu hóa layout: diện tích phòng = {}m², khoảng diện tích/ghế = {}-{}m²", 
                    dienTichPhong, minDienTichMoiGhe, maxDienTichMoiGhe);
            
            Map<String, Object> result = seatCalculationService.optimizeLayout(
                    dienTichPhong, minDienTichMoiGhe, maxDienTichMoiGhe);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Lỗi tối ưu hóa layout", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * So sánh các phương án layout khác nhau
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareLayouts(
            @RequestParam Double dienTichPhong,
            @RequestParam Double[] dienTichMoiGheOptions) {
        
        try {
            Map<String, Object> result = new java.util.HashMap<>();
            java.util.List<Map<String, Object>> comparisons = new java.util.ArrayList<>();
            
            for (Double dienTichMoiGhe : dienTichMoiGheOptions) {
                Map<String, Object> layout = seatCalculationService.calculateSeatsFromArea(dienTichPhong, dienTichMoiGhe);
                layout.put("dienTichMoiGhe", dienTichMoiGhe);
                comparisons.add(layout);
            }
            
            result.put("dienTichPhong", dienTichPhong);
            result.put("comparisons", comparisons);
            result.put("totalOptions", dienTichMoiGheOptions.length);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Lỗi so sánh layout", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin công thức tính toán
     */
    @GetMapping("/formula-info")
    public ResponseEntity<Map<String, Object>> getFormulaInfo() {
        Map<String, Object> info = new java.util.HashMap<>();
        info.put("congThucChinh", "Số ghế = Diện tích phòng ÷ Diện tích trung bình mỗi ghế");
        info.put("congThucPhanLoai", "VIP: 2 hàng cuối, COUPLE: 2 cột cuối, THUONG: còn lại");
        info.put("tyLeManHinh", "16:9 (rộng hơn dài)");
        info.put("giaMacDinh", Map.of(
                "THUONG", "50,000 VNĐ",
                "VIP", "80,000 VNĐ", 
                "COUPLE", "120,000 VNĐ"
        ));
        info.put("dienTichMacDinh", "0.8 m²/ghế");
        info.put("viDu", Map.of(
                "dienTichPhong", "80 m²",
                "dienTichMoiGhe", "0.8 m²",
                "ketQua", "100 ghế (10×10)"
        ));
        
        return ResponseEntity.ok(info);
    }
}
