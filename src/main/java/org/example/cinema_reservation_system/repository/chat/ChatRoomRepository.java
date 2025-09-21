package org.example.cinema_reservation_system.repository.chat;

import org.example.cinema_reservation_system.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    
    // Tìm phòng chat theo room ID
    Optional<ChatRoom> findByRoomId(String roomId);
    
    // Tìm phòng chat theo customer ID
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.customer.idKhachHang = :customerId AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByCustomerId(@Param("customerId") Integer customerId);
    
    // Tìm phòng chat theo staff ID
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.staff.idNhanVien = :staffId AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByStaffId(@Param("staffId") Integer staffId);
    
    // Tìm phòng chat đang mở theo customer ID
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.customer.idKhachHang = :customerId AND cr.status = 'OPEN' AND cr.isActive = true")
    Optional<ChatRoom> findOpenRoomByCustomerId(@Param("customerId") Integer customerId);
    
    // Lấy danh sách phòng chat đang mở
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.status = 'OPEN' AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    Page<ChatRoom> findOpenRooms(Pageable pageable);
    
    // Lấy danh sách phòng chat theo priority
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.status = 'OPEN' AND cr.isActive = true ORDER BY " +
           "CASE cr.priority " +
           "WHEN 'URGENT' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'NORMAL' THEN 3 " +
           "WHEN 'LOW' THEN 4 " +
           "END, cr.lastMessageAt DESC")
    Page<ChatRoom> findOpenRoomsByPriority(Pageable pageable);
    
    // Lấy danh sách phòng chat chưa có staff
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.status = 'OPEN' AND cr.isActive = true AND cr.staff IS NULL ORDER BY cr.createdAt ASC")
    List<ChatRoom> findUnassignedRooms();
    
    // Đếm số phòng chat đang mở
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.status = 'OPEN' AND cr.isActive = true")
    Long countOpenRooms();
    
    // Đếm số phòng chat chưa có staff
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.status = 'OPEN' AND cr.isActive = true AND cr.staff IS NULL")
    Long countUnassignedRooms();
    
    // Cập nhật thời gian tin nhắn cuối
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.lastMessageAt = :lastMessageAt WHERE cr.roomId = :roomId")
    void updateLastMessageAt(@Param("roomId") String roomId, @Param("lastMessageAt") LocalDateTime lastMessageAt);
    
    // Đóng phòng chat
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.status = 'CLOSED', cr.closedAt = :closedAt, cr.isActive = false WHERE cr.roomId = :roomId")
    void closeRoom(@Param("roomId") String roomId, @Param("closedAt") LocalDateTime closedAt);
    
    // Gán staff cho phòng chat
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.staff.idNhanVien = :staffId WHERE cr.roomId = :roomId")
    void assignStaffToRoom(@Param("roomId") String roomId, @Param("staffId") Integer staffId);
    
    // Tìm phòng chat theo tags
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.tags LIKE %:tag% AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByTags(@Param("tag") String tag);
    
    // Lấy phòng chat theo thời gian tạo
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.createdAt >= :since AND cr.isActive = true ORDER BY cr.createdAt DESC")
    List<ChatRoom> findRoomsCreatedSince(@Param("since") LocalDateTime since);
}

