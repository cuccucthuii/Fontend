package org.example.cinema_reservation_system.repository.room;

import org.example.cinema_reservation_system.entity.Room;
import org.example.cinema_reservation_system.utils.enums.TrangThaiPhongChieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    // Tìm theo ID rạp chiếu
    List<Room> findByRapChieu_IdRapChieu(Integer idRapChieu);

    // Tìm theo trạng thái
    List<Room> findByTrangThai(TrangThaiPhongChieu trangThai);

    // Tắt trạng thái phòng chiếu (đổi về KHONG_HOAT_DONG)
    @Modifying
    @Query(value = """
        UPDATE phong_chieu 
        SET trang_thai = :trangThai 
        WHERE id_phong_chieu = :id
    """, nativeQuery = true)
    void changeTrangThai(@Param("id") Integer id, @Param("trangThai") String trangThai);

    // Thêm mới
    @Modifying
    @Query(value = """
        INSERT INTO phong_chieu (ten_phong_chieu, dien_tich_phong, trang_thai, id_rap_chieu)
        VALUES (:ten, :dienTich, :trangThai, :idRap)
    """, nativeQuery = true)
    void insertPhongChieu(
            @Param("ten") String ten,
            @Param("dienTich") BigDecimal dienTich,
            @Param("trangThai") String trangThai,
            @Param("idRap") Integer idRap
    );

    // Tìm bản ghi cuối cùng theo tên phòng và id rạp
    Optional<Room> findTopByTenPhongChieuAndRapChieu_IdRapChieuOrderByIdPhongChieuDesc(String tenPhongChieu, Integer idRapChieu);

    // Cập nhật
    @Modifying
    @Query(value = """
        UPDATE phong_chieu
        SET ten_phong_chieu = :ten,
            dien_tich_phong = :dienTich,
            trang_thai = :trangThai,
            id_rap_chieu = :idRap
        WHERE id_phong_chieu = :id
    """, nativeQuery = true)
    void updatePhongChieu(
            @Param("id") Integer id,
            @Param("ten") String ten,
            @Param("dienTich") BigDecimal dienTich,
            @Param("trangThai") String trangThai,
            @Param("idRap") Integer idRap
    );

    // ===== SOFT DELETE METHODS =====

    // Xóa mềm phòng theo ID
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.daXoa = true, r.ngayXoa = CURRENT_TIMESTAMP WHERE r.idPhongChieu = :id AND r.daXoa = false")
    int softDeleteById(@Param("id") Integer id);

    // Khôi phục phòng theo ID
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.daXoa = false, r.ngayXoa = null WHERE r.idPhongChieu = :id AND r.daXoa = true")
    int restoreById(@Param("id") Integer id);

    // Xóa vĩnh viễn phòng theo ID
    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.idPhongChieu = :id AND r.daXoa = true")
    int permanentDeleteById(@Param("id") Integer id);

    // ===== FIND METHODS (chỉ lấy phòng chưa bị xóa) =====

    // Lấy tất cả phòng chưa bị xóa
    @Query("SELECT r FROM Room r WHERE r.daXoa = false")
    List<Room> findAllActive();

    // Lấy phòng theo ID (chỉ phòng chưa bị xóa)
    @Query("SELECT r FROM Room r WHERE r.idPhongChieu = :id AND r.daXoa = false")
    Optional<Room> findActiveById(@Param("id") Integer id);

    // Lấy phòng theo tên (chỉ phòng chưa bị xóa)
    @Query("SELECT r FROM Room r WHERE r.tenPhongChieu = :tenPhongChieu AND r.daXoa = false")
    Optional<Room> findActiveByTenPhongChieu(@Param("tenPhongChieu") String tenPhongChieu);

    // Lấy phòng theo rạp chiếu (chỉ phòng chưa bị xóa)
    @Query("SELECT r FROM Room r WHERE r.rapChieu.idRapChieu = :idRap AND r.daXoa = false")
    List<Room> findActiveByRapChieu(@Param("idRap") Integer idRap);

    // ===== TRASH METHODS (chỉ lấy phòng đã bị xóa) =====

    // Lấy tất cả phòng đã bị xóa
    @Query("SELECT r FROM Room r WHERE r.daXoa = true")
    List<Room> findAllDeleted();

    // Lấy phòng đã bị xóa theo ID
    @Query("SELECT r FROM Room r WHERE r.idPhongChieu = :id AND r.daXoa = true")
    Optional<Room> findDeletedById(@Param("id") Integer id);

    // ===== COUNT METHODS =====

    // Đếm số phòng chưa bị xóa
    @Query("SELECT COUNT(r) FROM Room r WHERE r.daXoa = false")
    long countActive();

    // Đếm số phòng đã bị xóa
    @Query("SELECT COUNT(r) FROM Room r WHERE r.daXoa = true")
    long countDeleted();

    // Đếm số phòng theo rạp chiếu (chỉ phòng chưa bị xóa)
    @Query("SELECT COUNT(r) FROM Room r WHERE r.rapChieu.idRapChieu = :idRap AND r.daXoa = false")
    long countActiveByRapChieu(@Param("idRap") Integer idRap);

    // ===== EXISTS METHODS =====

    // Kiểm tra phòng có tồn tại và chưa bị xóa
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Room r WHERE r.idPhongChieu = :id AND r.daXoa = false")
    boolean existsActiveById(@Param("id") Integer id);

    // Kiểm tra tên phòng có tồn tại và chưa bị xóa
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Room r WHERE r.tenPhongChieu = :tenPhongChieu AND r.daXoa = false")
    boolean existsActiveByTenPhongChieu(@Param("tenPhongChieu") String tenPhongChieu);

    // Kiểm tra phòng có tồn tại trong thùng rác
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Room r WHERE r.idPhongChieu = :id AND r.daXoa = true")
    boolean existsDeletedById(@Param("id") Integer id);

    // ===== FILTER METHODS =====

    // Lọc phòng theo nhiều điều kiện (chỉ phòng chưa bị xóa)
    @Query("SELECT r FROM Room r WHERE r.daXoa = false " +
            "AND (:idRap IS NULL OR r.rapChieu.idRapChieu = :idRap) " +
            "AND (:trangThai IS NULL OR r.trangThai = :trangThai) " +
            "AND (:tenPhongChieu IS NULL OR LOWER(r.tenPhongChieu) LIKE LOWER(CONCAT('%', :tenPhongChieu, '%'))) " +
            "AND (:dienTichMin IS NULL OR r.dienTichPhong >= :dienTichMin) " +
            "AND (:dienTichMax IS NULL OR r.dienTichPhong <= :dienTichMax)")
    List<Room> filterActiveRooms(
            @Param("idRap") Integer idRap,
            @Param("trangThai") String trangThai,
            @Param("tenPhongChieu") String tenPhongChieu,
            @Param("dienTichMin") Double dienTichMin,
            @Param("dienTichMax") Double dienTichMax
    );

    // ===== DERIVED QUERY METHODS (Sử dụng tên property đúng) =====

    //  SỬA: Sử dụng idPhongChieu thay vì id
    Optional<Room> findByIdPhongChieuAndDaXoa(Integer idPhongChieu, boolean daXoa);

    //  SỬA: Sử dụng idPhongChieu thay vì id
    boolean existsByIdPhongChieuAndDaXoa(Integer idPhongChieu, boolean daXoa);

    //  SỬA: Sử dụng daXoa thay vì deleted
    List<Room> findAllByDaXoa(boolean daXoa);

    // SỬA: Sử dụng daXoa thay vì deleted
    List<Room> findAllByDaXoaTrue();

    //  SỬA: Sử dụng daXoa thay vì deleted
    List<Room> findAllByDaXoaFalse();


}
