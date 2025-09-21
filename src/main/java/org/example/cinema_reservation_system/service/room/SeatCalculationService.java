package org.example.cinema_reservation_system.service.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatCalculationService {

    /**
     * Tính toán số ghế từ diện tích phòng với công thức linh hoạt
     * 
     * @param dienTichPhong Tổng diện tích phòng (m²)
     * @param dienTichTrungBinhMoiGhe Diện tích trung bình mỗi ghế (m²/ghế)
     * @return Map chứa thông tin tính toán
     */
    public Map<String, Object> calculateSeatsFromArea(Double dienTichPhong, Double dienTichTrungBinhMoiGhe) {
        log.info("Tính toán ghế từ diện tích: {}m², diện tích/ghế: {}m²", dienTichPhong, dienTichTrungBinhMoiGhe);
        
        // Công thức: Số ghế = Tổng diện tích phòng / Diện tích trung bình mỗi ghế
        double tongSoGheDouble = dienTichPhong / dienTichTrungBinhMoiGhe;
        int tongSoGhe = (int) Math.floor(tongSoGheDouble);
        
        // Tính chiều rộng và chiều dài tối ưu (tỷ lệ 16:9)
        double ratio = 16.0 / 9.0;
        int chieuRong = (int) Math.ceil(Math.sqrt(tongSoGhe * ratio));
        int chieuDai = (int) Math.ceil(tongSoGhe / (double) chieuRong);
        
        // Tính diện tích thực tế mỗi ghế
        double dienTichThucTeMoiGhe = dienTichPhong / tongSoGhe;
        
        // Tính toán phân loại ghế
        Map<String, Integer> phanLoaiGhe = calculateSeatDistribution(chieuDai, chieuRong);
        
        Map<String, Object> result = new HashMap<>();
        result.put("dienTichPhong", dienTichPhong);
        result.put("dienTichTrungBinhMoiGhe", dienTichTrungBinhMoiGhe);
        result.put("tongSoGhe", tongSoGhe);
        result.put("chieuRong", chieuRong);
        result.put("chieuDai", chieuDai);
        result.put("dienTichThucTeMoiGhe", roundTo2Decimals(dienTichThucTeMoiGhe));
        result.put("phanLoaiGhe", phanLoaiGhe);
        result.put("tyLeManHinh", "16:9");
        result.put("congThuc", "Số ghế = Diện tích phòng ÷ Diện tích/ghế");
        
        log.info("Kết quả tính toán: {} ghế, layout {}x{}", tongSoGhe, chieuRong, chieuDai);
        return result;
    }

    /**
     * Tính toán số ghế với diện tích mặc định (0.8m²/ghế)
     */
    public Map<String, Object> calculateSeatsFromArea(Double dienTichPhong) {
        return calculateSeatsFromArea(dienTichPhong, 0.8);
    }

    /**
     * Tính toán phân loại ghế theo vị trí
     */
    private Map<String, Integer> calculateSeatDistribution(int chieuDai, int chieuRong) {
        Map<String, Integer> phanLoai = new HashMap<>();
        
        int tongSoGhe = chieuDai * chieuRong;
        int soGheVIP = 0;
        int soGheCouple = 0;
        int soGheThuong = 0;
        
        for (int hang = 1; hang <= chieuDai; hang++) {
            for (int cot = 1; cot <= chieuRong; cot++) {
                String loaiGhe = determineSeatType(hang, chieuDai, cot, chieuRong);
                switch (loaiGhe) {
                    case "VIP":
                        soGheVIP++;
                        break;
                    case "COUPLE":
                        soGheCouple++;
                        break;
                    default:
                        soGheThuong++;
                        break;
                }
            }
        }
        
        phanLoai.put("THUONG", soGheThuong);
        phanLoai.put("VIP", soGheVIP);
        phanLoai.put("COUPLE", soGheCouple);
        phanLoai.put("TONG", tongSoGhe);
        
        return phanLoai;
    }

    /**
     * Xác định loại ghế theo vị trí
     */
    private String determineSeatType(int hang, int chieuDai, int cot, int chieuRong) {
        // Logic phân loại ghế:
        // - Hàng cuối (VIP): 2 hàng cuối
        // - Ghế couple: 2 cột cuối
        // - Còn lại: ghế thường
        
        if (hang > chieuDai - 2) {
            return "VIP";
        } else if (cot > chieuRong - 2) {
            return "COUPLE";
        } else {
            return "THUONG";
        }
    }

    /**
     * Tính giá vé theo loại ghế
     */
    public Map<String, BigDecimal> calculateSeatPrices(BigDecimal giaGheThuong, BigDecimal giaGheVIP, BigDecimal giaGheCouple) {
        Map<String, BigDecimal> giaVe = new HashMap<>();
        giaVe.put("THUONG", giaGheThuong);
        giaVe.put("VIP", giaGheVIP);
        giaVe.put("COUPLE", giaGheCouple);
        return giaVe;
    }

    /**
     * Tính tổng doanh thu dự kiến
     */
    public BigDecimal calculateExpectedRevenue(Map<String, Integer> phanLoaiGhe, Map<String, BigDecimal> giaVe) {
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        
        for (Map.Entry<String, Integer> entry : phanLoaiGhe.entrySet()) {
            String loaiGhe = entry.getKey();
            Integer soGhe = entry.getValue();
            BigDecimal gia = giaVe.getOrDefault(loaiGhe, BigDecimal.ZERO);
            
            if (!"TONG".equals(loaiGhe)) {
                BigDecimal doanhThu = gia.multiply(BigDecimal.valueOf(soGhe));
                tongDoanhThu = tongDoanhThu.add(doanhThu);
            }
        }
        
        return tongDoanhThu;
    }

    /**
     * Làm tròn số đến 2 chữ số thập phân
     */
    private double roundTo2Decimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Tính toán tối ưu layout với các tham số khác nhau
     */
    public Map<String, Object> optimizeLayout(Double dienTichPhong, Double minDienTichMoiGhe, Double maxDienTichMoiGhe) {
        Map<String, Object> result = new HashMap<>();
        
        // Thử các giá trị diện tích/ghế khác nhau
        double step = 0.1;
        Map<String, Object> bestLayout = null;
        double bestEfficiency = 0;
        
        for (double dienTichMoiGhe = minDienTichMoiGhe; dienTichMoiGhe <= maxDienTichMoiGhe; dienTichMoiGhe += step) {
            Map<String, Object> layout = calculateSeatsFromArea(dienTichPhong, dienTichMoiGhe);
            
            // Tính hiệu suất sử dụng không gian
            int tongSoGhe = (int) layout.get("tongSoGhe");
            double efficiency = tongSoGhe * dienTichMoiGhe / dienTichPhong;
            
            if (efficiency > bestEfficiency) {
                bestEfficiency = efficiency;
                bestLayout = layout;
            }
        }
        
        result.put("bestLayout", bestLayout);
        result.put("bestEfficiency", roundTo2Decimals(bestEfficiency));
        result.put("optimizationRange", minDienTichMoiGhe + " - " + maxDienTichMoiGhe + " m²/ghế");
        
        return result;
    }
}
