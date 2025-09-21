package org.example.cinema_reservation_system.controller.food;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.combo.ComboRequestDto;
import org.example.cinema_reservation_system.dto.combo.ComboResponseDto;
import org.example.cinema_reservation_system.entity.FoodCombo;
import org.example.cinema_reservation_system.service.food.ComboService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/combos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComboController {
    
    private final ComboService comboService;
    
    // ========== CRUD CƠ BẢN ==========
    
    /**
     * Lấy tất cả combo
     */
    @GetMapping
    public ResponseEntity<List<ComboResponseDto>> getAllCombos() {
        List<ComboResponseDto> combos = comboService.getAllCombos();
        return ResponseEntity.ok(combos);
    }
    
    /**
     * Lấy combo theo ID
     */
    @GetMapping("/{idCombo}")
    public ResponseEntity<ComboResponseDto> getComboById(@PathVariable Integer idCombo) {
        ComboResponseDto combo = comboService.getComboById(idCombo);
        return ResponseEntity.ok(combo);
    }
    
    /**
     * Tạo combo mới
     */
    @PostMapping
    public ResponseEntity<ComboResponseDto> createCombo(@Valid @RequestBody ComboRequestDto comboRequestDto) {
        ComboResponseDto createdCombo = comboService.createCombo(comboRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCombo);
    }
    
    /**
     * Cập nhật combo
     */
    @PutMapping("/{idCombo}")
    public ResponseEntity<ComboResponseDto> updateCombo(
            @PathVariable Integer idCombo,
            @Valid @RequestBody ComboRequestDto comboRequestDto) {
        ComboResponseDto updatedCombo = comboService.updateCombo(idCombo, comboRequestDto);
        return ResponseEntity.ok(updatedCombo);
    }
    
    /**
     * Xóa combo
     */
    @DeleteMapping("/{idCombo}")
    public ResponseEntity<Void> deleteCombo(@PathVariable Integer idCombo) {
        comboService.deleteCombo(idCombo);
        return ResponseEntity.noContent().build();
    }
    
    // ========== TÌM KIẾM VÀ LỌC ==========
    
    /**
     * Lấy combo theo trạng thái
     */
    @GetMapping("/status/{trangThai}")
    public ResponseEntity<List<ComboResponseDto>> getCombosByStatus(
            @PathVariable FoodCombo.TrangThaiCombo trangThai) {
        List<ComboResponseDto> combos = comboService.getCombosByStatus(trangThai);
        return ResponseEntity.ok(combos);
    }
    
    /**
     * Lấy combo đang hoạt động
     */
    @GetMapping("/active")
    public ResponseEntity<List<ComboResponseDto>> getActiveCombos() {
        List<ComboResponseDto> combos = comboService.getActiveCombos();
        return ResponseEntity.ok(combos);
    }
    
    /**
     * Tìm kiếm combo theo tên
     */
    @GetMapping("/search")
    public ResponseEntity<List<ComboResponseDto>> searchCombosByName(
            @RequestParam(required = false) String tenCombo) {
        List<ComboResponseDto> combos = comboService.searchCombosByName(tenCombo);
        return ResponseEntity.ok(combos);
    }
    
    /**
     * Lấy combo theo khoảng giá
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ComboResponseDto>> getCombosByPriceRange(
            @RequestParam BigDecimal giaMin,
            @RequestParam BigDecimal giaMax) {
        List<ComboResponseDto> combos = comboService.getCombosByPriceRange(giaMin, giaMax);
        return ResponseEntity.ok(combos);
    }
    
    // ========== THỐNG KÊ ==========
    
    /**
     * Đếm số combo theo trạng thái
     */
    @GetMapping("/count/{trangThai}")
    public ResponseEntity<Map<String, Object>> countCombosByStatus(
            @PathVariable FoodCombo.TrangThaiCombo trangThai) {
        long count = comboService.countCombosByStatus(trangThai);
        return ResponseEntity.ok(Map.of(
                "trangThai", trangThai,
                "count", count
        ));
    }
    
    /**
     * Lấy combo có giá cao nhất
     */
    @GetMapping("/most-expensive")
    public ResponseEntity<ComboResponseDto> getMostExpensiveCombo() {
        ComboResponseDto combo = comboService.getMostExpensiveCombo();
        return ResponseEntity.ok(combo);
    }
    
    /**
     * Lấy combo có giá thấp nhất
     */
    @GetMapping("/cheapest")
    public ResponseEntity<ComboResponseDto> getCheapestCombo() {
        ComboResponseDto combo = comboService.getCheapestCombo();
        return ResponseEntity.ok(combo);
    }
    
    // ========== VALIDATION ==========
    
    /**
     * Kiểm tra tên combo đã tồn tại chưa
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkComboNameExists(
            @RequestParam String tenCombo) {
        boolean exists = comboService.isComboNameExists(tenCombo);
        return ResponseEntity.ok(Map.of(
                "tenCombo", tenCombo,
                "exists", exists
        ));
    }
    
    /**
     * Kiểm tra tên combo đã tồn tại chưa (cho update)
     */
    @GetMapping("/check-name-update")
    public ResponseEntity<Map<String, Object>> checkComboNameExistsForUpdate(
            @RequestParam String tenCombo,
            @RequestParam Integer idCombo) {
        boolean exists = comboService.isComboNameExistsForUpdate(tenCombo, idCombo);
        return ResponseEntity.ok(Map.of(
                "tenCombo", tenCombo,
                "idCombo", idCombo,
                "exists", exists
        ));
    }
}

