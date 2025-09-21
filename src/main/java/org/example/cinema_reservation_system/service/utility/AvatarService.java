package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.user.AvatarDto;

import java.util.List;

public interface AvatarService {
    
    /**
     * Lấy tất cả avatar đang hoạt động
     */
    List<AvatarDto> getAllActiveAvatars();
    
    /**
     * Lấy tất cả avatar (cả active và inactive)
     */
    List<AvatarDto> getAllAvatars();
    
    /**
     * Lấy avatar theo ID
     */
    AvatarDto getAvatarById(Long id);
    
    /**
     * Kiểm tra avatar có tồn tại và đang hoạt động không
     */
    boolean isAvatarActive(Long id);
}

