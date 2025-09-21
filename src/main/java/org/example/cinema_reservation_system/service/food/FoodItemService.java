package org.example.cinema_reservation_system.service.food;

import org.example.cinema_reservation_system.dto.food.FoodItemRequestDto;
import org.example.cinema_reservation_system.dto.food.FoodItemResponseDto;
import org.example.cinema_reservation_system.entity.FoodItem;

import java.math.BigDecimal;
import java.util.List;

public interface FoodItemService {
    
    // CRUD cơ bản
    List<FoodItemResponseDto> getAllFoodItems();
    FoodItemResponseDto getFoodItemById(Integer idMon);
    FoodItemResponseDto createFoodItem(FoodItemRequestDto foodItemRequestDto);
    FoodItemResponseDto updateFoodItem(Integer idMon, FoodItemRequestDto foodItemRequestDto);
    void deleteFoodItem(Integer idMon);
    
    // Tìm kiếm và lọc
    List<FoodItemResponseDto> getFoodItemsByType(FoodItem.LoaiMon loaiMon);
    List<FoodItemResponseDto> getFoodItemsByStatus(FoodItem.TrangThaiMon trangThai);
    List<FoodItemResponseDto> getActiveFoodItems();
    List<FoodItemResponseDto> searchFoodItemsByName(String tenMon);
    List<FoodItemResponseDto> getFoodItemsByPriceRange(BigDecimal giaMin, BigDecimal giaMax);
    
    // Thống kê
    long countFoodItemsByStatus(FoodItem.TrangThaiMon trangThai);
    FoodItemResponseDto getMostExpensiveFoodItem();
    FoodItemResponseDto getCheapestFoodItem();
    
    // Validation
    boolean isFoodItemNameExists(String tenMon);
    boolean isFoodItemNameExistsForUpdate(String tenMon, Integer idMon);
    
    // Lấy món theo loại cụ thể
    List<FoodItemResponseDto> getBopRangBo();      // Bỏng rang bơ
    List<FoodItemResponseDto> getNuocNgot();       // Nước ngọt
    List<FoodItemResponseDto> getNuocEp();         // Nước ép
    List<FoodItemResponseDto> getSnack();          // Snack
    List<FoodItemResponseDto> getKem();            // Kem
    List<FoodItemResponseDto> getHat();            // Hạt
}
