package org.example.cinema_reservation_system.repository.cinematicket;

import org.example.cinema_reservation_system.entity.Seat;
import org.example.cinema_reservation_system.entity.ShowTime;
import org.example.cinema_reservation_system.entity.CinemaTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaTicketRepository extends JpaRepository<CinemaTicket, Integer>, CinemaTicketRepositoryCustom {


    boolean existsByGheNgoiAndSuatChieu(Seat gheNgoi, ShowTime suatChieu);
    List<CinemaTicket> findAllByHoaDon_IdHoaDon(Integer idHoaDon);

    //thêm mới
    // ========== SOFT DELETE QUERIES ==========

    /**
     * Tìm vé phim chưa bị xóa (daXoa = false hoặc null)
     */
    @Query("SELECT t FROM CinemaTicket t WHERE (t.daXoa = false OR t.daXoa IS NULL)")
    List<CinemaTicket> findByDaXoaFalseOrDaXoaIsNull();

    /**
     * Tìm vé phim đã bị xóa (daXoa = true)
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.daXoa = true")
    List<CinemaTicket> findByDaXoaTrue();

    /**
     * Tìm vé phim theo ID và chưa bị xóa
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.idVePhim = :id AND (t.daXoa = false OR t.daXoa IS NULL)")
    Optional<CinemaTicket> findByIdAndNotDeleted(@Param("id") Integer id);

    /**
     * Tìm vé phim theo ID và đã bị xóa
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.idVePhim = :id AND t.daXoa = true")
    Optional<CinemaTicket> findByIdAndDeleted(@Param("id") Integer id);

    /**
     * Đếm số vé phim chưa bị xóa
     */
    @Query("SELECT COUNT(t) FROM CinemaTicket t WHERE (t.daXoa = false OR t.daXoa IS NULL)")
    long countNotDeleted();

    /**
     * Đếm số vé phim đã bị xóa
     */
    @Query("SELECT COUNT(t) FROM CinemaTicket t WHERE t.daXoa = true")
    long countDeleted();

    // ========== BUSINESS QUERIES ==========

    /**
     * Tìm vé phim theo suất chiếu và chưa bị xóa
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.suatChieu.idSuatChieu = :suatChieuId AND (t.daXoa = false OR t.daXoa IS NULL)")
    List<CinemaTicket> findBySuatChieuAndNotDeleted(@Param("suatChieuId") Integer suatChieuId);

    /**
     * Tìm vé phim theo khách hàng và chưa bị xóa
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.khachHang.idKhachHang = :khachHangId AND (t.daXoa = false OR t.daXoa IS NULL)")
    List<CinemaTicket> findByKhachHangAndNotDeleted(@Param("khachHangId") Integer khachHangId);

    /**
     * Tìm vé phim theo trạng thái thanh toán và chưa bị xóa
     */
    @Query("SELECT t FROM CinemaTicket t WHERE t.trangThaiThanhToan = :trangThai AND (t.daXoa = false OR t.daXoa IS NULL)")
    List<CinemaTicket> findByTrangThaiThanhToanAndNotDeleted(@Param("trangThai") String trangThai);

}

