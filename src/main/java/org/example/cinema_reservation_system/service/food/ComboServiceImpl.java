package org.example.cinema_reservation_system.service.food;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.combo.ComboRequestDto;
import org.example.cinema_reservation_system.dto.combo.ComboResponseDto;
import org.example.cinema_reservation_system.entity.FoodCombo;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.repository.combo.ComboRepository;
import org.example.cinema_reservation_system.service.food.ComboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ComboServiceImpl implements ComboService {
    
    private final ComboRepository comboRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ComboResponseDto> getAllCombos() {
        return comboRepository.findAll().stream()
                .map(ComboResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ComboResponseDto getComboById(Integer idCombo) {
        FoodCombo combo = comboRepository.findById(idCombo)
                .orElseThrow(() -> new BusinessException("Không tìm thấy combo với ID: " + idCombo));
        return new ComboResponseDto(combo);
    }
    
    @Override
    public ComboResponseDto createCombo(ComboRequestDto comboRequestDto) {
        // Kiểm tra tên combo đã tồn tại chưa
        if (isComboNameExists(comboRequestDto.getTenCombo())) {
            throw new BusinessException("Tên combo đã tồn tại: " + comboRequestDto.getTenCombo());
        }
        
        FoodCombo combo = new FoodCombo();
        combo.setTenCombo(comboRequestDto.getTenCombo());
        combo.setMoTa(comboRequestDto.getMoTa());
        combo.setGiaCombo(comboRequestDto.getGiaCombo());
        combo.setTrangThai(comboRequestDto.getTrangThai());
        
        FoodCombo savedCombo = comboRepository.save(combo);
        return new ComboResponseDto(savedCombo);
    }
    
    @Override
    public ComboResponseDto updateCombo(Integer idCombo, ComboRequestDto comboRequestDto) {
        FoodCombo combo = comboRepository.findById(idCombo)
                .orElseThrow(() -> new BusinessException("Không tìm thấy combo với ID: " + idCombo));
        
        // Ktra tên combo đã tồn tại ch (trừ combo hiện tại)
        if (isComboNameExistsForUpdate(comboRequestDto.getTenCombo(), idCombo)) {
            throw new BusinessException("Tên combo đã tồn tại: " + comboRequestDto.getTenCombo());
        }
        
        combo.setTenCombo(comboRequestDto.getTenCombo());
        combo.setMoTa(comboRequestDto.getMoTa());
        combo.setGiaCombo(comboRequestDto.getGiaCombo());
        combo.setTrangThai(comboRequestDto.getTrangThai());
        
        FoodCombo updatedCombo = comboRepository.save(combo);
        return new ComboResponseDto(updatedCombo);
    }
    
    @Override
    public void deleteCombo(Integer idCombo) {
        if (!comboRepository.existsById(idCombo)) {
            throw new BusinessException("Không tìm thấy combo với ID: " + idCombo);
        }
        comboRepository.deleteById(idCombo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ComboResponseDto> getCombosByStatus(FoodCombo.TrangThaiCombo trangThai) {
        return comboRepository.findByTrangThai(trangThai).stream()
                .map(ComboResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ComboResponseDto> getActiveCombos() {
        return comboRepository.findByTrangThaiOrderByGiaComboAsc(FoodCombo.TrangThaiCombo.HOAT_DONG).stream()
                .map(ComboResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ComboResponseDto> searchCombosByName(String tenCombo) {
        if (tenCombo == null || tenCombo.trim().isEmpty()) {
            return getAllCombos();
        }
        return comboRepository.findByTenComboContainingIgnoreCase(tenCombo.trim()).stream()
                .map(ComboResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ComboResponseDto> getCombosByPriceRange(BigDecimal giaMin, BigDecimal giaMax) {
        if (giaMin == null || giaMax == null) {
            throw new BusinessException("Giá min và max không được để trống");
        }
        if (giaMin.compareTo(giaMax) > 0) {
            throw new BusinessException("Giá min không được lớn hơn giá max");
        }
        
        return comboRepository.findByGiaComboBetweenAndTrangThai(giaMin, giaMax, FoodCombo.TrangThaiCombo.HOAT_DONG).stream()
                .map(ComboResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countCombosByStatus(FoodCombo.TrangThaiCombo trangThai) {
        return comboRepository.countByTrangThai(trangThai);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ComboResponseDto getMostExpensiveCombo() {
        return comboRepository.findTopByGiaComboMaxAndTrangThai(FoodCombo.TrangThaiCombo.HOAT_DONG)
                .map(ComboResponseDto::new)
                .orElseThrow(() -> new BusinessException("Không có combo nào đang hoạt động"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public ComboResponseDto getCheapestCombo() {
        return comboRepository.findTopByGiaComboMinAndTrangThai(FoodCombo.TrangThaiCombo.HOAT_DONG)
                .map(ComboResponseDto::new)
                .orElseThrow(() -> new BusinessException("Không có combo nào đang hoạt động"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isComboNameExists(String tenCombo) {
        return comboRepository.existsByTenCombo(tenCombo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isComboNameExistsForUpdate(String tenCombo, Integer idCombo) {
        return comboRepository.existsByTenComboAndIdComboNot(tenCombo, idCombo);
    }
}

