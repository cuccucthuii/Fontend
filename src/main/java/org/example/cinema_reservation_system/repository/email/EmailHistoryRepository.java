package org.example.cinema_reservation_system.repository.email;

import org.example.cinema_reservation_system.entity.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Integer> {
    
    // Tìm email history theo hóa đơn
    @Query("SELECT eh FROM EmailHistory eh WHERE eh.hoaDon.idHoaDon = :hoaDonId ORDER BY eh.ngayGui DESC")
    List<EmailHistory> findByHoaDonIdOrderByNgayGuiDesc(@Param("hoaDonId") Integer hoaDonId);
    
    // Tìm email history theo trạng thái
    List<EmailHistory> findByTrangThai(String trangThai);
    
    // Tìm email history theo email người nhận
    List<EmailHistory> findByEmailNguoiNhanOrderByNgayGuiDesc(String emailNguoiNhan);
    
    // Tìm email history thất bại để retry
    List<EmailHistory> findByTrangThaiAndSoLanThuLaiLessThan(String trangThai, Integer maxRetries);
    
    // Thống kê email theo trạng thái
    @Query("SELECT COUNT(eh) FROM EmailHistory eh WHERE eh.trangThai = :trangThai")
    Long countByTrangThai(@Param("trangThai") String trangThai);
    
    // Thống kê email theo ngày
    @Query("SELECT COUNT(eh) FROM EmailHistory eh WHERE DATE(eh.ngayGui) = DATE(:ngay)")
    Long countByNgay(@Param("ngay") LocalDateTime ngay);
    
    // Tìm email history trong khoảng thời gian
    @Query("SELECT eh FROM EmailHistory eh WHERE eh.ngayGui BETWEEN :tuNgay AND :denNgay ORDER BY eh.ngayGui DESC")
    List<EmailHistory> findByNgayGuiBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
}
