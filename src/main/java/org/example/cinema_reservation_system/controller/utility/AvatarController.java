package org.example.cinema_reservation_system.controller.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.user.AvatarDto;
import org.example.cinema_reservation_system.service.utility.AvatarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avatars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvatarController {
    
    private final AvatarService avatarService;
    
    /**
     * Lấy tất cả avatar đang hoạt động (cho người dùng chọn)
     */
    @GetMapping("/active")
    public ResponseEntity<List<AvatarDto>> getActiveAvatars() {
        List<AvatarDto> avatars = avatarService.getAllActiveAvatars();
        return ResponseEntity.ok(avatars);
    }
    
    /**
     * Lấy tất cả avatar (cho admin quản lý)
     */
    @GetMapping
    public ResponseEntity<List<AvatarDto>> getAllAvatars() {
        List<AvatarDto> avatars = avatarService.getAllAvatars();
        return ResponseEntity.ok(avatars);
    }
    
    /**
     * Lấy avatar theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AvatarDto> getAvatarById(@PathVariable Long id) {
        AvatarDto avatar = avatarService.getAvatarById(id);
        return ResponseEntity.ok(avatar);
    }
    
    /**
     * Kiểm tra avatar có hoạt động không
     */
    @GetMapping("/{id}/check")
    public ResponseEntity<Boolean> checkAvatarActive(@PathVariable Long id) {
        boolean isActive = avatarService.isAvatarActive(id);
        return ResponseEntity.ok(isActive);
    }
}

