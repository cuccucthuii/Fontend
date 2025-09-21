package org.example.cinema_reservation_system.controller.food;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.food.FoodItemRequestDto;
import org.example.cinema_reservation_system.dto.food.FoodItemResponseDto;
import org.example.cinema_reservation_system.entity.FoodItem;
import org.example.cinema_reservation_system.service.food.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodItemController {
    
    private final FoodItemService foodItemService;
    
    // ========== CRUD CƠ BẢN ==========
    
    /**
     * Lấy tất cả món ăn
     */
    @GetMapping
    public ResponseEntity<List<FoodItemResponseDto>> getAllFoodItems() {
        List<FoodItemResponseDto> foodItems = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy món ăn theo ID
     */
    @GetMapping("/{idMon}")
    public ResponseEntity<FoodItemResponseDto> getFoodItemById(@PathVariable Integer idMon) {
        FoodItemResponseDto foodItem = foodItemService.getFoodItemById(idMon);
        return ResponseEntity.ok(foodItem);
    }
    
    /**
     * Tạo món ăn mới
     */
    @PostMapping
    public ResponseEntity<FoodItemResponseDto> createFoodItem(@Valid @RequestBody FoodItemRequestDto foodItemRequestDto) {
        FoodItemResponseDto createdFoodItem = foodItemService.createFoodItem(foodItemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFoodItem);
    }
    
    /**
     * Cập nhật món ăn
     */
    @PutMapping("/{idMon}")
    public ResponseEntity<FoodItemResponseDto> updateFoodItem(
            @PathVariable Integer idMon,
            @Valid @RequestBody FoodItemRequestDto foodItemRequestDto) {
        FoodItemResponseDto updatedFoodItem = foodItemService.updateFoodItem(idMon, foodItemRequestDto);
        return ResponseEntity.ok(updatedFoodItem);
    }
    
    /**
     * Xóa món ăn
     */
    @DeleteMapping("/{idMon}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Integer idMon) {
        foodItemService.deleteFoodItem(idMon);
        return ResponseEntity.noContent().build();
    }
    
    // ========== TÌM KIẾM VÀ LỌC ==========
    
    /**
     * Lấy món ăn theo loại
     */
    @GetMapping("/type/{loaiMon}")
    public ResponseEntity<List<FoodItemResponseDto>> getFoodItemsByType(
            @PathVariable FoodItem.LoaiMon loaiMon) {
        List<FoodItemResponseDto> foodItems = foodItemService.getFoodItemsByType(loaiMon);
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy món ăn theo trạng thái
     */
    @GetMapping("/status/{trangThai}")
    public ResponseEntity<List<FoodItemResponseDto>> getFoodItemsByStatus(
            @PathVariable FoodItem.TrangThaiMon trangThai) {
        List<FoodItemResponseDto> foodItems = foodItemService.getFoodItemsByStatus(trangThai);
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy món ăn đang hoạt động
     */
    @GetMapping("/active")
    public ResponseEntity<List<FoodItemResponseDto>> getActiveFoodItems() {
        List<FoodItemResponseDto> foodItems = foodItemService.getActiveFoodItems();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Tìm kiếm món ăn theo tên
     */
    @GetMapping("/search")
    public ResponseEntity<List<FoodItemResponseDto>> searchFoodItemsByName(
            @RequestParam(required = false) String tenMon) {
        List<FoodItemResponseDto> foodItems = foodItemService.searchFoodItemsByName(tenMon);
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy món ăn theo khoảng giá
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<FoodItemResponseDto>> getFoodItemsByPriceRange(
            @RequestParam BigDecimal giaMin,
            @RequestParam BigDecimal giaMax) {
        List<FoodItemResponseDto> foodItems = foodItemService.getFoodItemsByPriceRange(giaMin, giaMax);
        return ResponseEntity.ok(foodItems);
    }
    
    // ========== MÓN ĂN THEO LOẠI CỤ THỂ ==========
    
    /**
     * Lấy tất cả bỏng rang bơ
     */
    @GetMapping("/bop-rang-bo")
    public ResponseEntity<List<FoodItemResponseDto>> getBopRangBo() {
        List<FoodItemResponseDto> foodItems = foodItemService.getBopRangBo();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy tất cả nước ngọt
     */
    @GetMapping("/nuoc-ngot")
    public ResponseEntity<List<FoodItemResponseDto>> getNuocNgot() {
        List<FoodItemResponseDto> foodItems = foodItemService.getNuocNgot();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy tất cả nước ép
     */
    @GetMapping("/nuoc-ep")
    public ResponseEntity<List<FoodItemResponseDto>> getNuocEp() {
        List<FoodItemResponseDto> foodItems = foodItemService.getNuocEp();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy tất cả snack
     */
    @GetMapping("/snack")
    public ResponseEntity<List<FoodItemResponseDto>> getSnack() {
        List<FoodItemResponseDto> foodItems = foodItemService.getSnack();
        return ResponseEntity.ok(foodItems);
    }
    
    /**
     * Lấy tất cả kem
     */
    @GetMapping("/kem")
    public ResponseEntity<List<FoodItemResponseDto>> getKem() {
        List<FoodItemResponseDto> foodItems = foodItemService.getKem();
        return ResponseEntity.ok(foodItems);
    }
    

    
    /**
     * Lấy tất cả hạt
     */
    @GetMapping("/hat")
    public ResponseEntity<List<FoodItemResponseDto>> getHat() {
        List<FoodItemResponseDto> foodItems = foodItemService.getHat();
        return ResponseEntity.ok(foodItems);
    }
    
    // ========== THỐNG KÊ ==========
    
    /**
     * Đếm số món ăn theo trạng thái
     */
    @GetMapping("/count/{trangThai}")
    public ResponseEntity<Map<String, Object>> countFoodItemsByStatus(
            @PathVariable FoodItem.TrangThaiMon trangThai) {
        long count = foodItemService.countFoodItemsByStatus(trangThai);
        return ResponseEntity.ok(Map.of(
                "trangThai", trangThai,
                "count", count
        ));
    }
    
    /**
     * Lấy món ăn có giá cao nhất
     */
    @GetMapping("/most-expensive")
    public ResponseEntity<FoodItemResponseDto> getMostExpensiveFoodItem() {
        FoodItemResponseDto foodItem = foodItemService.getMostExpensiveFoodItem();
        return ResponseEntity.ok(foodItem);
    }
    
    /**
     * Lấy món ăn có giá thấp nhất
     */
    @GetMapping("/cheapest")
    public ResponseEntity<FoodItemResponseDto> getCheapestFoodItem() {
        FoodItemResponseDto foodItem = foodItemService.getCheapestFoodItem();
        return ResponseEntity.ok(foodItem);
    }
    
    // ========== VALIDATION ==========
    
    /**
     * Kiểm tra tên món ăn đã tồn tại chưa
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkFoodItemNameExists(
            @RequestParam String tenMon) {
        boolean exists = foodItemService.isFoodItemNameExists(tenMon);
        return ResponseEntity.ok(Map.of(
                "tenMon", tenMon,
                "exists", exists
        ));
    }
    
    /**
     * Kiểm tra tên món ăn đã tồn tại chưa (cho update)
     */
    @GetMapping("/check-name-update")
    public ResponseEntity<Map<String, Object>> checkFoodItemNameExistsForUpdate(
            @RequestParam String tenMon,
            @RequestParam Integer idMon) {
        boolean exists = foodItemService.isFoodItemNameExistsForUpdate(tenMon, idMon);
        return ResponseEntity.ok(Map.of(
                "tenMon", tenMon,
                "idMon", idMon,
                "exists", exists
        ));
    }
}
