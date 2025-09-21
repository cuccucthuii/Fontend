package org.example.cinema_reservation_system.repository.avatar;

import org.example.cinema_reservation_system.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    
    /**
     * Lấy tất cả avatar đang hoạt động
     */
    List<Avatar> findByIsActiveTrueOrderByIdAsc();
    
    /**
     * Lấy tất cả avatar (cả active và inactive)
     */
    List<Avatar> findAllByOrderByIdAsc();
    
    /**
     * Kiểm tra avatar có tồn tại và đang hoạt động không
     */
    boolean existsByIdAndIsActiveTrue(Long id);
}

