package org.example.cinema_reservation_system.repository.checkin;

import org.example.cinema_reservation_system.entity.TicketCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckinRepository extends JpaRepository<TicketCheckin, Integer> {
    
    // Kiểm tra vé đã được check-in chưa theo mã vé
    Optional<TicketCheckin> findByMaVe(String maVe);
    
    // Lấy danh sách check-in theo ngày
    @Query("SELECT c FROM TicketCheckin c WHERE DATE(c.thoiGianCheckin) = DATE(:ngay)")
    List<TicketCheckin> findByNgayCheckin(@Param("ngay") LocalDateTime ngay);
    
    // Thống kê check-in theo ngày
    @Query("SELECT COUNT(c) FROM TicketCheckin c WHERE DATE(c.thoiGianCheckin) = DATE(:ngay)")
    Long countByNgayCheckin(@Param("ngay") LocalDateTime ngay);
    
    // Kiểm tra vé đã được check-in chưa theo mã vé
    boolean existsByMaVe(String maVe);
}

