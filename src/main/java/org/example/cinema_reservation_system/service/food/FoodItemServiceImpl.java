package org.example.cinema_reservation_system.service.food;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.food.FoodItemRequestDto;
import org.example.cinema_reservation_system.dto.food.FoodItemResponseDto;
import org.example.cinema_reservation_system.entity.FoodItem;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.repository.food.FoodItemRepository;
import org.example.cinema_reservation_system.service.food.FoodItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodItemServiceImpl implements FoodItemService {
    
    private final FoodItemRepository foodItemRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDto getFoodItemById(Integer idMon) {
        FoodItem foodItem = foodItemRepository.findById(idMon)
                .orElseThrow(() -> new BusinessException("Không tìm thấy món ăn với ID: " + idMon));
        return new FoodItemResponseDto(foodItem);
    }
    
    @Override
    public FoodItemResponseDto createFoodItem(FoodItemRequestDto foodItemRequestDto) {
        // Kiểm tra tên món đã tồn tại chưa
        if (isFoodItemNameExists(foodItemRequestDto.getTenMon())) {
            throw new BusinessException("Tên món đã tồn tại: " + foodItemRequestDto.getTenMon());
        }
        
        FoodItem foodItem = new FoodItem();
        foodItem.setTenMon(foodItemRequestDto.getTenMon());
        foodItem.setMoTa(foodItemRequestDto.getMoTa());
        foodItem.setGiaMon(foodItemRequestDto.getGiaMon());
        foodItem.setLoaiMon(foodItemRequestDto.getLoaiMon());
        foodItem.setHinhAnh(foodItemRequestDto.getHinhAnh());
        foodItem.setTrangThai(foodItemRequestDto.getTrangThai());
        
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        return new FoodItemResponseDto(savedFoodItem);
    }
    
    @Override
    public FoodItemResponseDto updateFoodItem(Integer idMon, FoodItemRequestDto foodItemRequestDto) {
        FoodItem foodItem = foodItemRepository.findById(idMon)
                .orElseThrow(() -> new BusinessException("Không tìm thấy món ăn với ID: " + idMon));
        
        // Kiểm tra tên món đã tồn tại chưa (trừ món hiện tại)
        if (isFoodItemNameExistsForUpdate(foodItemRequestDto.getTenMon(), idMon)) {
            throw new BusinessException("Tên món đã tồn tại: " + foodItemRequestDto.getTenMon());
        }
        
        foodItem.setTenMon(foodItemRequestDto.getTenMon());
        foodItem.setMoTa(foodItemRequestDto.getMoTa());
        foodItem.setGiaMon(foodItemRequestDto.getGiaMon());
        foodItem.setLoaiMon(foodItemRequestDto.getLoaiMon());
        foodItem.setHinhAnh(foodItemRequestDto.getHinhAnh());
        foodItem.setTrangThai(foodItemRequestDto.getTrangThai());
        
        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return new FoodItemResponseDto(updatedFoodItem);
    }
    
    @Override
    public void deleteFoodItem(Integer idMon) {
        if (!foodItemRepository.existsById(idMon)) {
            throw new BusinessException("Không tìm thấy món ăn với ID: " + idMon);
        }
        foodItemRepository.deleteById(idMon);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getFoodItemsByType(FoodItem.LoaiMon loaiMon) {
        return foodItemRepository.findByLoaiMon(loaiMon).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getFoodItemsByStatus(FoodItem.TrangThaiMon trangThai) {
        return foodItemRepository.findByTrangThai(trangThai).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getActiveFoodItems() {
        return foodItemRepository.findByTrangThaiOrderByGiaMonAsc(FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> searchFoodItemsByName(String tenMon) {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            return getAllFoodItems();
        }
        return foodItemRepository.findByTenMonContainingIgnoreCase(tenMon.trim()).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getFoodItemsByPriceRange(BigDecimal giaMin, BigDecimal giaMax) {
        if (giaMin == null || giaMax == null) {
            throw new BusinessException("Giá min và max không được để trống");
        }
        if (giaMin.compareTo(giaMax) > 0) {
            throw new BusinessException("Giá min không được lớn hơn giá max");
        }
        
        return foodItemRepository.findByGiaMonBetweenAndTrangThai(giaMin, giaMax, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countFoodItemsByStatus(FoodItem.TrangThaiMon trangThai) {
        return foodItemRepository.countByTrangThai(trangThai);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDto getMostExpensiveFoodItem() {
        return foodItemRepository.findTopByGiaMonMaxAndTrangThai(FoodItem.TrangThaiMon.HOAT_DONG)
                .map(FoodItemResponseDto::new)
                .orElseThrow(() -> new BusinessException("Không có món ăn nào đang hoạt động"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDto getCheapestFoodItem() {
        return foodItemRepository.findTopByGiaMonMinAndTrangThai(FoodItem.TrangThaiMon.HOAT_DONG)
                .map(FoodItemResponseDto::new)
                .orElseThrow(() -> new BusinessException("Không có món ăn nào đang hoạt động"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isFoodItemNameExists(String tenMon) {
        return foodItemRepository.existsByTenMon(tenMon);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isFoodItemNameExistsForUpdate(String tenMon, Integer idMon) {
        return foodItemRepository.existsByTenMonAndIdMonNot(tenMon, idMon);
    }
    
    // ========== LẤY MÓN THEO LOẠI CỤ THỂ ==========
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getBopRangBo() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.BOP_RANG_BO, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getNuocNgot() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.NUOC_NGOT, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getNuocEp() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.NUOC_EP, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getSnack() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.SNACK, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getKem() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.KEM, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDto> getHat() {
        return foodItemRepository.findByLoaiMonAndTrangThai(FoodItem.LoaiMon.HAT, FoodItem.TrangThaiMon.HOAT_DONG).stream()
                .map(FoodItemResponseDto::new)
                .collect(Collectors.toList());
    }
}
