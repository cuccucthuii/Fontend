package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.user.AvatarDto;
import org.example.cinema_reservation_system.entity.Avatar;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.repository.avatar.AvatarRepository;
import org.example.cinema_reservation_system.service.utility.AvatarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarServiceImpl implements AvatarService {
    
    private final AvatarRepository avatarRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<AvatarDto> getAllActiveAvatars() {
        List<Avatar> avatars = avatarRepository.findByIsActiveTrueOrderByIdAsc();
        return avatars.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AvatarDto> getAllAvatars() {
        List<Avatar> avatars = avatarRepository.findAllByOrderByIdAsc();
        return avatars.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AvatarDto getAvatarById(Long id) {
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Avatar không tồn tại"));
        return convertToDto(avatar);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isAvatarActive(Long id) {
        return avatarRepository.existsByIdAndIsActiveTrue(id);
    }
    
    private AvatarDto convertToDto(Avatar avatar) {
        return new AvatarDto(
                avatar.getId(),
                avatar.getAvatarName(),
                avatar.getAvatarUrl(),
                avatar.getDescription(),
                avatar.getIsActive(),
                avatar.getCreatedAt()
        );
    }
}

