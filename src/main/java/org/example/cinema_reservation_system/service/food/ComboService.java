package org.example.cinema_reservation_system.service.food;

import org.example.cinema_reservation_system.dto.combo.ComboRequestDto;
import org.example.cinema_reservation_system.dto.combo.ComboResponseDto;
import org.example.cinema_reservation_system.entity.FoodCombo;

import java.math.BigDecimal;
import java.util.List;

public interface ComboService {
    
    // CRUD
    List<ComboResponseDto> getAllCombos();
    ComboResponseDto getComboById(Integer idCombo);
    ComboResponseDto createCombo(ComboRequestDto comboRequestDto);
    ComboResponseDto updateCombo(Integer idCombo, ComboRequestDto comboRequestDto);
    void deleteCombo(Integer idCombo);
    
    // Tìm kiếm và lọc
    List<ComboResponseDto> getCombosByStatus(FoodCombo.TrangThaiCombo trangThai);
    List<ComboResponseDto> getActiveCombos();
    List<ComboResponseDto> searchCombosByName(String tenCombo);
    List<ComboResponseDto> getCombosByPriceRange(BigDecimal giaMin, BigDecimal giaMax);
    
    // Thống kê
    long countCombosByStatus(FoodCombo.TrangThaiCombo trangThai);
    ComboResponseDto getMostExpensiveCombo();
    ComboResponseDto getCheapestCombo();
    
    // Validation
    boolean isComboNameExists(String tenCombo);
    boolean isComboNameExistsForUpdate(String tenCombo, Integer idCombo);
}

