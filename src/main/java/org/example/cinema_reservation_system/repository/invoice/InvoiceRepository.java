package org.example.cinema_reservation_system.repository.invoice;

import org.example.cinema_reservation_system.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Query("SELECT SUM(h.tongTien) FROM Invoice h WHERE h.trangThai = 'DA_THANH_TOAN'")
    Double getTongDoanhThu();


    @Query(value = "SELECT COALESCE(SUM(tong_tien), 0) FROM hoa_don " +
            "WHERE trang_thai = 'DA_THANH_TOAN' " +
            "AND EXTRACT(YEAR FROM ngay_dat) = :nam " +
            "AND EXTRACT(MONTH FROM ngay_dat) = :thang", nativeQuery = true)
    BigDecimal tongDoanhThuTheoThangNam(
            @Param("nam") int nam,
            @Param("thang") int thang
    );

    @Query(value = """
    SELECT r.ten_rap_chieu,
           COALESCE(SUM(h.tong_tien), 0) AS tong_doanh_thu,
           COUNT(v.id_ve_phim) AS so_ve_ban
    FROM hoa_don h
    JOIN ve_phim v ON h.id_hoa_don = v.id_hoa_don
    JOIN phong_chieu p ON v.id_phong_chieu = p.id_phong_chieu
    JOIN rap_chieu r ON p.id_rap_chieu = r.id_rap_chieu
    WHERE h.trang_thai = 'DA_THANH_TOAN'
    GROUP BY r.ten_rap_chieu
    ORDER BY tong_doanh_thu DESC
    """, nativeQuery = true)
    List<Object[]> getDoanhThuTheoRap();

    @Query(value = """
    SELECT
        p.ten_phim,
        COALESCE(SUM(h.tong_tien), 0) AS tong_doanh_thu,
        COUNT(v.id_ve_phim) AS so_ve_ban
    FROM
        hoa_don h
        JOIN ve_phim v ON h.id_hoa_don = v.id_hoa_don
        JOIN phim p ON v.id_phim = p.id_phim
    WHERE
        h.trang_thai = 'DA_THANH_TOAN'
    GROUP BY
        p.ten_phim
    ORDER BY
        tong_doanh_thu DESC
    """, nativeQuery = true)
    List<Object[]> getDoanhThuTheoPhim();

    // Doanh thu theo ngày trong khoảng
    @Query(value = """
        SELECT DATE(ngay_dat) AS ngay, COALESCE(SUM(tong_tien), 0) AS doanh_thu
        FROM hoa_don
        WHERE trang_thai = 'DA_THANH_TOAN'
          AND ngay_dat BETWEEN :from AND :to
        GROUP BY DATE(ngay_dat)
        ORDER BY ngay
    """, nativeQuery = true)
    List<Object[]> doanhThuTheoNgay(@Param("from") java.sql.Date from, @Param("to") java.sql.Date to);

    // Doanh thu theo tuần trong năm
    @Query(value = """
        SELECT EXTRACT(YEAR FROM ngay_dat) AS nam, EXTRACT(WEEK FROM ngay_dat) AS tuan, COALESCE(SUM(tong_tien), 0) AS doanh_thu
        FROM hoa_don
        WHERE trang_thai = 'DA_THANH_TOAN'
          AND EXTRACT(YEAR FROM ngay_dat) = :year
        GROUP BY nam, tuan
        ORDER BY nam, tuan
    """, nativeQuery = true)
    List<Object[]> doanhThuTheoTuan(@Param("year") int year);

    // Doanh thu theo tháng trong năm
    @Query(value = """
        SELECT EXTRACT(YEAR FROM ngay_dat) AS nam, EXTRACT(MONTH FROM ngay_dat) AS thang, COALESCE(SUM(tong_tien), 0) AS doanh_thu
        FROM hoa_don
        WHERE trang_thai = 'DA_THANH_TOAN'
          AND EXTRACT(YEAR FROM ngay_dat) = :year
        GROUP BY nam, thang
        ORDER BY nam, thang
    """, nativeQuery = true)
    List<Object[]> doanhThuTheoThang(@Param("year") int year);

    // Doanh thu theo quý trong năm
    @Query(value = """
        SELECT EXTRACT(YEAR FROM ngay_dat) AS nam, EXTRACT(QUARTER FROM ngay_dat) AS quy, COALESCE(SUM(tong_tien), 0) AS doanh_thu
        FROM hoa_don
        WHERE trang_thai = 'DA_THANH_TOAN'
          AND EXTRACT(YEAR FROM ngay_dat) = :year
        GROUP BY nam, quy
        ORDER BY nam, quy
    """, nativeQuery = true)
    List<Object[]> doanhThuTheoQuy(@Param("year") int year);

    // Doanh thu theo năm
    @Query(value = """
        SELECT EXTRACT(YEAR FROM ngay_dat) AS nam, COALESCE(SUM(tong_tien), 0) AS doanh_thu
        FROM hoa_don
        WHERE trang_thai = 'DA_THANH_TOAN'
        GROUP BY nam
        ORDER BY nam
    """, nativeQuery = true)
    List<Object[]> doanhThuTheoNam();

    // ========== TRA CỨU ĐƠN HÀNG ==========
    
    // Tìm hóa đơn theo mã giao dịch
    Optional<Invoice> findByMaGiaoDich(String maGiaoDich);
    
    // Tìm hóa đơn theo mã đặt vé
    Optional<Invoice> findByMaDatVe(String maDatVe);
    
    // Tìm hóa đơn theo mã giao dịch hoặc mã đặt vé
    @Query("SELECT h FROM Invoice h WHERE h.maGiaoDich = :ma OR h.maDatVe = :ma")
    Optional<Invoice> findByMaGiaoDichOrMaDatVe(@Param("ma") String ma);
    
    // Tìm hóa đơn theo khách hàng và mã
    @Query("SELECT h FROM Invoice h WHERE h.khachHang.idKhachHang = :customerId AND (h.maGiaoDich = :ma OR h.maDatVe = :ma)")
    Optional<Invoice> findByCustomerAndMa(@Param("customerId") Integer customerId, @Param("ma") String ma);
    
    // Tìm hóa đơn theo mã giao dịch VNPay - đã hợp nhất sang ma_giao_dich chung
    // Optional<Invoice> findByMaGiaoDichVNPay(String maGiaoDichVNPay);
    
    // ========== STATISTICS METHODS ==========
    
    // Đếm số hóa đơn theo khách hàng
    @Query("SELECT COUNT(h) FROM Invoice h WHERE h.khachHang.idKhachHang = :userId")
    long countByUserId(@Param("userId") Integer userId);
    
    // Tính tổng số tiền theo khách hàng
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM Invoice h WHERE h.khachHang.idKhachHang = :userId AND h.trangThai = 'DA_THANH_TOAN'")
    Double sumTotalAmountByUserId(@Param("userId") Integer userId);
    
    // Đếm số hóa đơn theo thời gian tạo
    @Query("SELECT COUNT(h) FROM Invoice h WHERE h.ngayDat >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);

}
