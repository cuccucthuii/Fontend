package org.example.cinema_reservation_system.repository;

import org.example.cinema_reservation_system.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Integer> {

    @Query("select sh from SeatHold sh where sh.suatChieu.idSuatChieu = :suatChieuId and sh.gheNgoi.idGheNgoi = :gheId and sh.trangThai = 'DANG_GIU' and sh.thoiGianHetHan > :now")
    Optional<SeatHold> findActiveHold(@Param("suatChieuId") Integer suatChieuId,
                                      @Param("gheId") Integer gheId,
                                      @Param("now") LocalDateTime now);

    Optional<SeatHold> findByHoldToken(String holdToken);

    @Query("select sh from SeatHold sh where sh.suatChieu.idSuatChieu = :suatChieuId and sh.gheNgoi.idGheNgoi = :gheId order by sh.idGiuGhe desc")
    Optional<SeatHold> findLatestByShowAndSeat(@Param("suatChieuId") Integer suatChieuId,
                                               @Param("gheId") Integer gheId);
}


