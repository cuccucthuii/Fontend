package org.example.cinema_reservation_system.repository.combo;

import org.example.cinema_reservation_system.entity.FoodCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComboRepository extends JpaRepository<FoodCombo, Integer> {
    
    // Tìm combo theo trạng thái
    List<FoodCombo> findByTrangThai(FoodCombo.TrangThaiCombo trangThai);
    
    // Tìm combo đang hoạt động
    List<FoodCombo> findByTrangThaiOrderByGiaComboAsc(FoodCombo.TrangThaiCombo trangThai);
    
    // Tìm combo theo tên (tìm kiếm mờ)
    @Query("SELECT c FROM FoodCombo c WHERE LOWER(c.tenCombo) LIKE LOWER(CONCAT('%', :tenCombo, '%'))")
    List<FoodCombo> findByTenComboContainingIgnoreCase(@Param("tenCombo") String tenCombo);
    
    // Tìm combo theo khoảng giá
    @Query("SELECT c FROM FoodCombo c WHERE c.giaCombo BETWEEN :giaMin AND :giaMax AND c.trangThai = :trangThai")
    List<FoodCombo> findByGiaComboBetweenAndTrangThai(
            @Param("giaMin") java.math.BigDecimal giaMin,
            @Param("giaMax") java.math.BigDecimal giaMax,
            @Param("trangThai") FoodCombo.TrangThaiCombo trangThai
    );
    
    // Kiểm tra tên combo đã tồn tại chưa
    boolean existsByTenCombo(String tenCombo);
    
    // Kiểm tra tên combo đã tồn tại chưa (trừ combo hiện tại khi update)
    @Query("SELECT COUNT(c) > 0 FROM FoodCombo c WHERE c.tenCombo = :tenCombo AND c.idCombo != :idCombo")
    boolean existsByTenComboAndIdComboNot(@Param("tenCombo") String tenCombo, @Param("idCombo") Integer idCombo);
    
    // Đếm số combo theo trạng thái
    long countByTrangThai(FoodCombo.TrangThaiCombo trangThai);
    
    // Tìm combo có giá cao nhất
    @Query("SELECT c FROM FoodCombo c WHERE c.giaCombo = (SELECT MAX(c2.giaCombo) FROM FoodCombo c2 WHERE c2.trangThai = :trangThai) AND c.trangThai = :trangThai")
    Optional<FoodCombo> findTopByGiaComboMaxAndTrangThai(@Param("trangThai") FoodCombo.TrangThaiCombo trangThai);
    
    // Tìm combo có giá thấp nhất
    @Query("SELECT c FROM FoodCombo c WHERE c.giaCombo = (SELECT MIN(c2.giaCombo) FROM FoodCombo c2 WHERE c2.trangThai = :trangThai) AND c.trangThai = :trangThai")
    Optional<FoodCombo> findTopByGiaComboMinAndTrangThai(@Param("trangThai") FoodCombo.TrangThaiCombo trangThai);
}

