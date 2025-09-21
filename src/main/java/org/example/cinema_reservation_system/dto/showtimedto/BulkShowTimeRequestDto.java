package org.example.cinema_reservation_system.dto.showtimedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThaiSuatChieu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkShowTimeRequestDto {
    
    @NotNull(message = "ID phim không được để trống")
    private Integer idPhim;
    
    @NotNull(message = "ID phòng chiếu không được để trống")
    private Integer idPhongChieu;
    
    @NotBlank(message = "Tên suất chiếu không được để trống")
    @Size(max = 100, message = "Tên suất chiếu không được vượt quá 100 ký tự")
    private String tenSuatChieu;
    
    @NotNull(message = "Giờ chiếu không được để trống")
    private LocalTime gioChieu;
    
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalTime thoiGianBatDau;
    
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalTime thoiGianKetThuc;
    
    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé phải lớn hơn 0")
    private BigDecimal giaVe;
    
    @NotNull(message = "Trạng thái không được để trống")
    private TrangThaiSuatChieu trangThai;
    
    private String ghiChu;
    
    // Scheduling pattern
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;
    
    @NotNull(message = "Mẫu lặp lại không được để trống")
    private RepeatPattern repeatPattern;
    
    @Valid
    private List<LocalDate> ngayCuThe;
    
    // Advanced scheduling options
    @Builder.Default
    private Boolean skipWeekends = false;
    @Builder.Default
    private Boolean skipHolidays = false;
    private List<LocalDate> ngayNghi;
    private List<LocalTime> gioChieuBoSung;
    
    public enum RepeatPattern {
        DAILY,           // Hàng ngày
        WEEKLY,          // Hàng tuần
        WEEKDAYS,        // Chỉ ngày trong tuần (T2-T6)
        WEEKENDS,        // Chỉ cuối tuần (T7, CN)
        CUSTOM,          // Tùy chỉnh theo danh sách ngày
        ALTERNATE_DAYS,  // Cách ngày
        ALTERNATE_WEEKS  // Cách tuần
    }
    
    // Validation methods
    public boolean isValidSchedule() {
        if (ngayBatDau == null || ngayKetThuc == null) {
            return false;
        }
        
        if (ngayBatDau.isAfter(ngayKetThuc)) {
            return false;
        }
        
        if (thoiGianBatDau == null || thoiGianKetThuc == null) {
            return false;
        }
        
        if (thoiGianBatDau.isAfter(thoiGianKetThuc)) {
            return false;
        }
        
        if (repeatPattern == RepeatPattern.CUSTOM && (ngayCuThe == null || ngayCuThe.isEmpty())) {
            return false;
        }
        
        // Validate that start date is not in the past
        if (ngayBatDau.isBefore(LocalDate.now())) {
            return false;
        }
        
        return true;
    }
    
    public List<LocalDate> generateScheduleDates() {
        List<LocalDate> dates = new java.util.ArrayList<>();
        
        switch (repeatPattern) {
            case DAILY:
                LocalDate current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (!skipWeekends || !isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusDays(1);
                }
                break;
                
            case WEEKLY:
                current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (!skipWeekends || !isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusWeeks(1);
                }
                break;
                
            case WEEKDAYS:
                current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (!isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusDays(1);
                }
                break;
                
            case WEEKENDS:
                current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusDays(1);
                }
                break;
                
            case ALTERNATE_DAYS:
                current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (!skipWeekends || !isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusDays(2);
                }
                break;
                
            case ALTERNATE_WEEKS:
                current = ngayBatDau;
                while (!current.isAfter(ngayKetThuc)) {
                    if (!skipWeekends || !isWeekend(current)) {
                        if (!skipHolidays || !isHoliday(current)) {
                            dates.add(current);
                        }
                    }
                    current = current.plusWeeks(2);
                }
                break;
                
            case CUSTOM:
                dates.addAll(ngayCuThe);
                break;
        }
        
        return dates;
    }
    
    private boolean isWeekend(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }
    
    private boolean isHoliday(LocalDate date) {
        return ngayNghi != null && ngayNghi.contains(date);
    }
}



