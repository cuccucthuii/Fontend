package org.example.cinema_reservation_system.repository.food;

import org.example.cinema_reservation_system.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {
    
    // Tìm món theo loại
    List<FoodItem> findByLoaiMon(FoodItem.LoaiMon loaiMon);
    
    // Tìm món theo trạng thái
    List<FoodItem> findByTrangThai(FoodItem.TrangThaiMon trangThai);
    
    // Tìm món đang hoạt động
    List<FoodItem> findByTrangThaiOrderByGiaMonAsc(FoodItem.TrangThaiMon trangThai);
    
    // Tìm món theo tên (tìm kiếm mờ)
    @Query("SELECT m FROM FoodItem m WHERE LOWER(m.tenMon) LIKE LOWER(CONCAT('%', :tenMon, '%'))")
    List<FoodItem> findByTenMonContainingIgnoreCase(@Param("tenMon") String tenMon);
    
    // Tìm món theo khoảng giá
    @Query("SELECT m FROM FoodItem m WHERE m.giaMon BETWEEN :giaMin AND :giaMax AND m.trangThai = :trangThai")
    List<FoodItem> findByGiaMonBetweenAndTrangThai(
            @Param("giaMin") java.math.BigDecimal giaMin,
            @Param("giaMax") java.math.BigDecimal giaMax,
            @Param("trangThai") FoodItem.TrangThaiMon trangThai
    );
    
    // Kiểm tra tên món đã tồn tại chưa
    boolean existsByTenMon(String tenMon);
    
    // Kiểm tra tên món đã tồn tại chưa (trừ món hiện tại khi update)
    @Query("SELECT COUNT(m) > 0 FROM FoodItem m WHERE m.tenMon = :tenMon AND m.idMon != :idMon")
    boolean existsByTenMonAndIdMonNot(@Param("tenMon") String tenMon, @Param("idMon") Integer idMon);
    
    // Đếm số món theo trạng thái
    long countByTrangThai(FoodItem.TrangThaiMon trangThai);
    
    // Tìm món có giá cao nhất
    @Query("SELECT m FROM FoodItem m WHERE m.giaMon = (SELECT MAX(m2.giaMon) FROM FoodItem m2 WHERE m2.trangThai = :trangThai) AND m.trangThai = :trangThai")
    Optional<FoodItem> findTopByGiaMonMaxAndTrangThai(@Param("trangThai") FoodItem.TrangThaiMon trangThai);
    
    // Tìm món có giá thấp nhất
    @Query("SELECT m FROM FoodItem m WHERE m.giaMon = (SELECT MIN(m2.giaMon) FROM FoodItem m2 WHERE m2.trangThai = :trangThai) AND m.trangThai = :trangThai")
    Optional<FoodItem> findTopByGiaMonMinAndTrangThai(@Param("trangThai") FoodItem.TrangThaiMon trangThai);
    
    // Tìm món theo loại và trạng thái
    List<FoodItem> findByLoaiMonAndTrangThai(FoodItem.LoaiMon loaiMon, FoodItem.TrangThaiMon trangThai);
}
