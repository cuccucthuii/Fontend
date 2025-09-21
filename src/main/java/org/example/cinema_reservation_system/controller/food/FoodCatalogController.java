package org.example.cinema_reservation_system.controller.food;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.combo.ComboResponseDto;
import org.example.cinema_reservation_system.dto.food.FoodItemResponseDto;
import org.example.cinema_reservation_system.service.food.ComboService;
import org.example.cinema_reservation_system.service.food.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodCatalogController {

    private final FoodItemService foodItemService;
    private final ComboService comboService;

    /**
     * Trả về danh sách món lẻ đang hoạt động và combo đang hoạt động trong một response
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFoodAndCombos(
            @org.springframework.web.bind.annotation.RequestParam(name = "includeInactive", required = false, defaultValue = "false") boolean includeInactive,
            @org.springframework.web.bind.annotation.RequestParam(name = "foodName", required = false) String foodName,
            @org.springframework.web.bind.annotation.RequestParam(name = "foodPriceMin", required = false) BigDecimal foodPriceMin,
            @org.springframework.web.bind.annotation.RequestParam(name = "foodPriceMax", required = false) BigDecimal foodPriceMax,
            @org.springframework.web.bind.annotation.RequestParam(name = "foodType", required = false) String foodType,
            @org.springframework.web.bind.annotation.RequestParam(name = "comboName", required = false) String comboName,
            @org.springframework.web.bind.annotation.RequestParam(name = "comboPriceMin", required = false) BigDecimal comboPriceMin,
            @org.springframework.web.bind.annotation.RequestParam(name = "comboPriceMax", required = false) BigDecimal comboPriceMax
    ) {
        List<FoodItemResponseDto> foodItems = includeInactive
                ? foodItemService.getAllFoodItems()
                : foodItemService.getActiveFoodItems();

        List<ComboResponseDto> combos = includeInactive
                ? comboService.getAllCombos()
                : comboService.getActiveCombos();

        // Filter food items by optional params
        if (foodName != null && !foodName.isBlank()) {
            String keyword = foodName.trim().toLowerCase();
            foodItems = foodItems.stream()
                    .filter(i -> {
                        String name = null;
                        try {
                            name = (String) i.getClass().getMethod("getTenMon").invoke(i);
                        } catch (Exception ignored) {}
                        return name != null && name.toLowerCase().contains(keyword);
                    })
                    .toList();
        }
        if (foodPriceMin != null) {
            foodItems = foodItems.stream()
                    .filter(i -> {
                        try {
                            BigDecimal price = (BigDecimal) i.getClass().getMethod("getGiaMon").invoke(i);
                            return price != null && price.compareTo(foodPriceMin) >= 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .toList();
        }
        if (foodPriceMax != null) {
            foodItems = foodItems.stream()
                    .filter(i -> {
                        try {
                            BigDecimal price = (BigDecimal) i.getClass().getMethod("getGiaMon").invoke(i);
                            return price != null && price.compareTo(foodPriceMax) <= 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .toList();
        }

        // Filter by food type (loaiMon), supports comma-separated values
        if (foodType != null && !foodType.isBlank()) {
            final List<String> requestedTypes = java.util.Arrays.stream(foodType.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .toList();
            if (!requestedTypes.isEmpty()) {
                foodItems = foodItems.stream()
                        .filter(i -> {
                            try {
                                Object typeObj = i.getClass().getMethod("getLoaiMon").invoke(i);
                                String typeName = typeObj != null ? typeObj.toString().toUpperCase() : null;
                                return typeName != null && requestedTypes.contains(typeName);
                            } catch (Exception ignored) {
                                return false;
                            }
                        })
                        .toList();
            }
        }

        // Filter combos by optional params
        if (comboName != null && !comboName.isBlank()) {
            String keyword = comboName.trim().toLowerCase();
            combos = combos.stream()
                    .filter(c -> {
                        String name = null;
                        try {
                            name = (String) c.getClass().getMethod("getTenCombo").invoke(c);
                        } catch (Exception ignored) {}
                        return name != null && name.toLowerCase().contains(keyword);
                    })
                    .toList();
        }
        if (comboPriceMin != null) {
            combos = combos.stream()
                    .filter(c -> {
                        try {
                            BigDecimal price = (BigDecimal) c.getClass().getMethod("getGiaCombo").invoke(c);
                            return price != null && price.compareTo(comboPriceMin) >= 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .toList();
        }
        if (comboPriceMax != null) {
            combos = combos.stream()
                    .filter(c -> {
                        try {
                            BigDecimal price = (BigDecimal) c.getClass().getMethod("getGiaCombo").invoke(c);
                            return price != null && price.compareTo(comboPriceMax) <= 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .toList();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("foodItems", foodItems);
        response.put("combos", combos);

        return ResponseEntity.ok(response);
    }
}


