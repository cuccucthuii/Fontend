package org.example.cinema_reservation_system.repository.theater;

import org.example.cinema_reservation_system.entity.Theater;
import org.example.cinema_reservation_system.utils.enums.TrangThaiRapChieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Integer> {
    List<Theater> findByTenRapChieuContainingIgnoreCase(String keyword);
    List<Theater> findByTrangThai(TrangThaiRapChieu trangThai);
    List<Theater> findByThanhPhoContainingIgnoreCase(String thanhPho);
    List<Theater> findByKhuVucContainingIgnoreCase(String khuVuc);
    boolean existsByTenRapChieuIgnoreCase(String tenRapChieu);

    Optional<Theater> findTopByTenRapChieuOrderByIdRapChieuDesc(String ten); // ✅ Sửa ở đây

    @Modifying
    @Query(value = """
        INSERT INTO rap_chieu (ten_rap_chieu, dia_chi, so_dien_thoai, trang_thai, latitude, longitude)
        VALUES (:ten, :diaChi, :soDienThoai, :trangThai, :lat, :lng)
    """, nativeQuery = true)
    void insertRapChieu(
            @Param("ten") String ten,
            @Param("diaChi") String diaChi,
            @Param("soDienThoai") String soDienThoai,
            @Param("trangThai") String trangThai,
            @Param("lat") Double lat,
            @Param("lng") Double lng
    );

    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE rap_chieu
        SET ten_rap_chieu = :ten,
            dia_chi = :diaChi,
            so_dien_thoai = :soDienThoai,
            trang_thai = :trangThai,
            latitude = :lat,
            longitude = :lng
        WHERE id_rap_chieu = :id
    """, nativeQuery = true)
    void updateRapChieu(
            @Param("id") Integer id,
            @Param("ten") String ten,
            @Param("diaChi") String diaChi,
            @Param("soDienThoai") String soDienThoai,
            @Param("trangThai") String trangThai,
            @Param("lat") Double lat,
            @Param("lng") Double lng
    );

    @Query("""
        SELECT t FROM Theater t
        WHERE (:keyword IS NULL OR LOWER(t.tenRapChieu) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.diaChi) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:trangThai IS NULL OR t.trangThai = :trangThai)
          AND (:thanhPho IS NULL OR LOWER(t.thanhPho) LIKE LOWER(CONCAT('%', :thanhPho, '%')))
          AND (:khuVuc IS NULL OR LOWER(t.khuVuc) LIKE LOWER(CONCAT('%', :khuVuc, '%')))
    """)
    Page<Theater> searchPaged(@Param("keyword") String keyword,
                              @Param("trangThai") TrangThaiRapChieu trangThai,
                              @Param("thanhPho") String thanhPho,
                              @Param("khuVuc") String khuVuc,
                              Pageable pageable);
}


